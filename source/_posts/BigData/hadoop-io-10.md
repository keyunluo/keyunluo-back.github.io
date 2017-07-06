---
title: Hadoop源码学习(14)——压缩(2)
comments: true
toc: true
date: 2016-08-15 08:25:02
categories: BigData
tags : Hadoop
keywords: Hadoop,IO,Compress
---

>**本节内容：**数据压缩往往是计算密集型操作，考虑到性能，建议使用本地库(Native Library)来压缩和解压缩。Snappy在Google的生产环境中经过了PB级数据压缩的考验，具有优良的性能，以Snappy为例，学习在Hadoop提供的压缩框架下集成新的压缩算法。


<!-- more -->

## Java本地化方法

Hadoop的DEFLATE、gzip和Snappy都支持算法的本地化实现，默认情况下，Hadoop会在它运行的平台上查找本地库。

|压缩类型|Java实现|原生实现|
| :--- | :--- | :---|
| DEFLATE | Yes | Yes |
| gzip | Yes|  Yes|
| bzip2 | Yes Yes|
| LZO | No|  Yes|
| LZ4 | No | Yes|
| Snappy | No | Yes|

Java提供了一些钩子函数，使得调用本地方法成为可能。Java语言中的关键字native用于表示某个方法为本地方法，显然，本地方法是类的成员方法。例如，下面的例子中在org.apache.hadoop.io.compress.snappy包中，类SnappyCompressor的静态方法initIDs()和方法compressByteDirect()用关键字native修饰，表明这是一个本地化方法。

实际上，如果什么都不做也可以编译这个类，但是当使用这个类的时候，Java虚拟机会告诉你无法找到上述两个方法。要想实现这两个本地方法，一般需要三个步骤：

- 为方法生成一个Java调用和实际C函数转换的C存根
- 建立一个共享库并导出该存根
- 使用System.loadLibrary()方法通知Java运行环境加载共享库

Java为C存根的生成提供了实用程序javah，以上面的SnappyCompressor为例，可以在build/classes目录下执行命令：`javah org.apache.hadoop.io.compress.snappy.SnappyCompressor`,系统会生成一个头文件：`org_apache_hadoop_io_compress_snappy_SnappyCompressor.h`,该头文件包含上述两个本地方法相应的声明：

- Java_org_apache_hadoop_io_compress_snappy_SnappyCompressor_initIDs
- Java_org_apache_hadoop_io_compress_snappy_SnappyCompressor_compressBytesDirect

这两个声明遵从了Java本地方法的命名规则，以Java起首，然后是类的名字、方法名。声明中的JNIEXPORT和JNICALL命令表明了这两个方法会被JNI调用。上述第一个声明对应的方法是Java...initIDs，由于是一个静态方法，它的参数类型为JNIEnv的指针，用于和JVM通信。JNIEnv提供了大量的函数，可以执行类和对象的相关方法，也可以访问对象的成员变量或类的静态变量，参数jclass提供了引用静态方法对应类的机制，而Java...compressBytesDirect中的jobject相当于this引用，这两个参数大量应用于JNI提供的C API中。

JNIEnv提供了C代码和Java虚拟机通信的环境，GetObjectField()函数可用于获得对象的一个域，GetIntField()可用于得到Java对象的整型成员变量，SetField()设置Java对象的整型成员变量的值。最后使用System.loadLibrary()方法调用在java.library.path指定的路径下，寻找并加载附加驱动的动态库，如Snappy的libsnappy.so库。

## CodecPool

如果使用的是原生代码库并且需要在应用中执行大量压缩和解压缩操作，可以考虑使用 CodecPool。它支持反复使用压缩和解压缩，以分摊创建这些对象的开销。

下面的代码显示了API函数：

``` java
public class PooledStreamCompressor {
  public static void main(String[] args) throws Exception {
  String codecClassname = args[0];
  Class<?> codecClass = Class.forName(codecClassname);
  Configuration conf = new Configuration();
  CompressionCodec codec = (CompressionCodec)
  ReflectionUtils.newInstance(codecClass, conf);
  Compressor compressor = null;
  try {
    compressor = CodecPool.getCompressor(codec);
    CompressionOutputStream out = codec.createOutputStream(System.out, compressor);
    IOUtils.copyBytes(System.in, out, 4096, false);
    out.finish();
  } finally {
  CodecPool.returnCompressor(compressor);
    }
  }
}
```

在codec的重载方法createOutputStream()中，对于指定的CompressionCodec，我们从池中获取一个Compressor实例。通过使用finally数据块，我们在不同的数据流之家来回复制数据，即使出现IOException异常，也可以确保compressor可以返回池中。

## 支持Snappy压缩

org.apache.hadoop.io.compress.snappy包括支持Snappy的压缩器SnappyCompressor和解压器SnappyDecompressor。LoadSnappy类用于判断Snappy本地库是否可用，如果可用，则通过System.loadLibrary()加载本地库。

SnappyCompressor实现了Compressor接口。压缩器的一般方法是循环调用setInput()、finish()和compress()三个方法对数据进行压缩。

SnappyCompressor的主要成员变量如下：

``` java
private static final int DEFAULT_DIRECT_BUFFER_SIZE = 64 * 1024;

  // HACK - Use this as a global lock in the JNI layer
  @SuppressWarnings({"unchecked", "unused"})
  private static Class clazz = SnappyCompressor.class;

  private int directBufferSize;
  private Buffer compressedDirectBuf = null;  // 输出(压缩)数据缓冲区
  private int uncompressedDirectBufLen;
  private Buffer uncompressedDirectBuf = null;  // 输入数据缓冲区
  // userBuf,userBufOff,userBufLen用于保存通过setInput()设置的，但超过压缩器工作空间uncompressedDirectBuf剩余可用空间的数据
  private byte[] userBuf = null;
  private int userBufOff = 0, userBufLen = 0;
  private boolean finish, finished;

  private long bytesRead = 0L; //计数器，供getBytesRead()使用
  private long bytesWritten = 0L; //计数器，供getBytesWritten()使用

  private static boolean nativeSnappyLoaded = false;
```
在分析压缩器/解压器和压缩流/解压流中，一直强调Compressor的setInput、needsInput、finish、finished和compress五个方法间的配合。下面分析这些方法的实现：

### setInput

setInput方法为压缩器提供数据，在做了一番数据合法性检查后，先将finished标志为false，并尝试将数据复制到内部缓冲区中。如果内部缓存器剩余空间不够大，那么压缩器将借用输入数据对应的缓冲区，即利用userBuff/userBufOff/userBufLen记录输入的数据。否则，setInput()复制数据到uncompressedDirectBuf中。

需要注意的是，当借用发生时，使用的是引用，即数据并没有实际的复制，用户不能随便修改传入的数据。同时，缓冲区只能借用一次，用户如果再次调用setInput，将会替换原来保存的信息，造成数据错误：

``` java
public void setInput(byte[] b, int off, int len) {
    if (b == null) {
      throw new NullPointerException();
    }
    if (off < 0 || len < 0 || off > b.length - len) {
      throw new ArrayIndexOutOfBoundsException();
    }
    finished = false;

    if (len > uncompressedDirectBuf.remaining()) {
      // 借用外部缓冲区，这个时候needsInput为false
      this.userBuf = b;
      this.userBufOff = off;
      this.userBufLen = len;
    } else {
      ((ByteBuffer) uncompressedDirectBuf).put(b, off, len);
      uncompressedDirectBufLen = uncompressedDirectBuf.position();
    }

    bytesRead += len;
  }
```

setInput()借用外部缓冲区后就不能再接收数据，这时用户调用needsInput()将返回false，就可以获知这个消息。

### needsInput

needsInput()返回false有三种情况：输出缓冲区（即保持压缩结果的缓冲区）有未读取的数据、输入缓冲区没有空间、压缩器已经借用外部缓冲区。这时，用户需要通过compress()方法取走已经压缩的数据，直至needsInput()返回true，才可以再次通过setInput()方法添加待压缩数据，相关代码如下：

``` java
public boolean needsInput() {
    return !(compressedDirectBuf.remaining() > 0
        || uncompressedDirectBuf.remaining() == 0 || userBufLen > 0);
  }
```

### compress

compress方法用于获取压缩后的数据，它需要处理needsInput()返回false的几种情况。

- 如果压缩数据缓冲区有数据，即compressedDirectBuf中还有数据，则读取这部分数据，并返回。
- 如果该缓冲区为空，则需要压缩数据。首先清理compressedDirectBuf，这个清理（即调用clear()和limit()）是一个典型的Buffer操作。待压缩的数据有两个来源：输入缓冲区uncompressedDirectBuf或者“借用”的数据缓冲区。
- 如果输入缓冲区没有数据，那待压缩数据可能(可以在没有任何待压缩数据的情况下调用compress()方法)在“借用”的数据缓冲区里，这时使用setInputFromSavedData()方法复制“借用”数据缓冲区中的数据到uncompressedDirectBuf中。setInputFromSavedData()方法调用结束后，待压缩数据缓冲区中里还没有数据，则设置finished标志位，并返回0，表明压缩数据已经读完。

uncompressedDirectBuffer中的数据，利用前面介绍的native方法compressBytesDirect()进行压缩，压缩后的数据保存在compressedDirectBuf中。由于待压缩数据缓冲区和压缩数据缓冲区的大小是一样的，所以uncompressedDirectBuf中的数据是一次被处理完的。compressedByteDirect()调用结束后，需要再次设置缓冲区的标记，并根据情况复制数据到compress()的参数b提供的缓冲区中。相关代码如下：

``` java
public int compress(byte[] b, int off, int len)
      throws IOException {
    if (b == null) {
      throw new NullPointerException();
    }
    if (off < 0 || len < 0 || off > b.length - len) {
      throw new ArrayIndexOutOfBoundsException();
    }

    // 是否还有未取走的已经压缩的数据
    int n = compressedDirectBuf.remaining();
    if (n > 0) {
      n = Math.min(n, len);
      ((ByteBuffer) compressedDirectBuf).get(b, off, n);
      bytesWritten += n;
      return n;
    }

    // 清理压缩数据缓冲区
    compressedDirectBuf.clear();
    compressedDirectBuf.limit(0);
    if (0 == uncompressedDirectBuf.position()) {
      // 输入数据缓冲区没有数据
      setInputFromSavedData();
      if (0 == uncompressedDirectBuf.position()) {
        // Called without data; write nothing
        finished = true;
        return 0;
      }
    }

    // 压缩数据
    n = compressBytesDirect();
    compressedDirectBuf.limit(n);
    uncompressedDirectBuf.clear(); // snappy consumes all buffer input

    // snappy已经处理完所有的数据，设置finished为true
    if (0 == userBufLen) {
      finished = true;
    }

    // Get atmost 'len' bytes
    n = Math.min(n, len);
    bytesWritten += n;
    ((ByteBuffer) compressedDirectBuf).get(b, off, n);

    return n;
  }
```

### finished()和finish

finished()返回true，表明压缩过程已经结束。压缩过程结束包含多个条件，包括finish标志位和finished标志位都必须为true，以及compressedDirectBuf中没有未取走的数据。其中，finish为true，表明用户已经确认完成数据的输入过程，finished表明压缩器中没有待压缩数据，这三个条件缺一不可。

``` java
public boolean finished() {
    // Check if all uncompressed data has been consumed
    return (finish && finished && compressedDirectBuf.remaining() == 0);
  }
```