---
title: Hadoop源码学习(13)——压缩(1)
comments: true
toc: true
date: 2016-08-14 08:25:02
categories: BigData
tags : Hadoop
keywords: Hadoop,IO,Compress
---

>**本节内容：**压缩广泛应用于海量数据处理中，对数据文件进行压缩，可以有效减少存储文件所需的空间，并加快数据在网络上或者磁盘上的传输速度。在Hadoop中，压缩应用于文件存储、Map阶段到Reduce阶段的数据交换等情景。


<!-- more -->

## Hadoop压缩简介

Hadoop作为一个通用的海量数据处理平台，在使用压缩方式上，主要考虑压缩速度和压缩文件额可分割性。

用于Hadoop的常见压缩格式以及特性：

|压缩格式|UNIX工具|算法|文件扩展名|支持多文件|可分割|编码解码器|
| :---- | :---- | :---- | :---- | :---- | :---- | :---- |
|DEFLATE|无|DEFLATE|.deflate|否|否|org.apache.hadoop.io.compress.DefaultCodec|
|gzip|gzip|DEFLATE|.gz|否|否|org.apache.hadoop.io.compress.GzipCodec|
|zip|zip|DEFLATE|.zip|是|是||
|bzip|bzip2|bzip2|.bz2|否|是|org.apache.hadoop.io.compress.BZip2Codec|
|LZO|lzop|LZO|.lzo|否|否|com.hadoop.compression.lzo.LzopCodec|
|LZ4|无|LZ4|.lz4|否|否|org.apache.hadoop.io.compress.Lz4Codec|
|Snappy|无|Snappy|.snappy|否|否|org.apache.hadoop.io.compress.SnappyCodec|


## Hadoop压缩API应用实例

本节学习使用编码/解码器的典型实例。其中compress()方法接受一个字符串参数，用于指定编码/解码器，并应用对应的压缩算法对文本文件CodecDemo.java进行压缩。字符串参数使用Java的反射机制创建对应的编码/解码器对象，通过CompressionCodec对象，进一步使用它的createOutputStream()方法构造一个CompressionOutputStream流，未压缩的数据通过IOUtils.copyBytes()方法，从输入流中复制写入CompressionOutputStream流，最终以压缩格式写入底层的输出流中。底层的输出流是文件输出流FileOutputStream，它关联文件的文件名，是在原有文件名的基础上添加压缩算法相应的扩展名生成。该扩展名可以通过CompressionCodec对象的getDefaultExtension()方法获得。

``` java
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.fs.Path;

public class CodecDemo{
    public static void compress(String method) throws Exception {
        File fileIn = new File("CodecDemo.java");
        // 输入流
        InputStream in = new FileInputStream(fileIn);
        Class<?> codecClass = Class.forName(method);

        Configuration conf = new Configuration();

        //通过名称找对应的编码、解码器
        CompressionCodec codec = (CompressionCodec) ReflectionUtils.newInstance(codecClass,conf);

        File fileOut = new File("CodecDemo.java"+codec.getDefaultExtension());
        fileOut.delete();
        // 文件输出流
        OutputStream out = new FileOutputStream(fileOut);

        // 通过编码/解码器创建对应的输出流
        CompressionOutputStream cout = codec.createOutputStream(out);

        // 压缩
        IOUtils.copyBytes(in,cout,4096,false);

        in.close();
        cout.close();
    }

    public static void decompress(File file) throws IOException{
        Configuration conf = new Configuration();
        CompressionCodecFactory factory = new CompressionCodecFactory(conf);

        // 通过文件扩展名获取相应的编码/解码器
        CompressionCodec codec = factory.getCodec(new Path(file.getName()));

        if (codec == null) {
            System.out.println("无法找到"+file+"的codec解码器");
            return ;
        }

        File fileOut = new File(file.getName()+".txt");

        // 通过编码/解码器创建对应的输入流

        InputStream in = codec.createInputStream( new FileInputStream(file));
        OutputStream out = new FileOutputStream(fileOut);
        IOUtils.copyBytes(in,out,4096,false);

        in.close();
        out.close();
    }

    public static void main(String[] args){
        try{
        System.out.println("创建压缩文件...");
        compress("org.apache.hadoop.io.compress.BZip2Codec");
        System.out.println("解压缩文件...");
        File file = new File("CodecDemo.java.bz2");
        decompress(file);
        } catch (Exception e){
        e.printStackTrace();
        }

    }
}
```

## Hadoop压缩框架

Hadoop通过以编码/解码器为基础的抽象工厂方法，提供了一个可扩展的框架，支持多种压缩方法。

### 编码/解码器

CompressionCodec接口实现了编码/解码器，使用的是抽象工厂的设计模式。CompressionCodec提供了一系列的方法，用于创建特定压缩算法的相关设施，其类图如下：

![CompressionCodec](/resource/blog/2016-08/CompressionCodec.png)

CompressionCodec中的方法很对称，一个压缩功能对应一个解压缩的功能，其中，压缩有关的方法包括：

- createOutputStream()用于通过底层输出流创建对应压缩算法的压缩流，重载的createOutputStream()方法可使用压缩器创建压缩流；
- createCompressor()用于创建压缩算法对应的压缩器，后面会继续介绍压缩流CompressionoutputStream和压缩器Compressor,解压缩也有对应的方法和类。

CompressionCodec中还提供了获取对应文件扩展名的方法：getDefaultExtension()，如对于org.apache.hadoop.io.compression.BZip2Codec,该方法返回字符串".bz2"。

CompressionCodecFactory是Hadoop压缩框架中的另一个类，它应用了工厂方法，使用者可以通过它提供的方法获得CompressionCodec。通过CompressionCodecFactory的getCodec()方法，可以创建GzipCodec对象或BZip2Codec对象。为了分析该方法，需要了解CompressionCodec类中保存文件扩展名和CompressionCodec映射关系的成员变量codecs。

codecs是一个有序映射表，即它本身是一个Map，同时它对Map的键排序，下面是codecs中保存的一个可能的映射关系：

``` json
2zb.: org.apache.hadoop.io.compress.BZip2Codec,
etalfed.: org.apache.hadoop.io.compress.DeflatCodec,
yppans.: org.apache.hadoop.io.compress.SnappyCodec,
zg.: org.apache.hadoop.io.compress.GzipCodec,
```

getCodec()方法的输入是Path对象，保存着文件路径，如上例中的"CodecDemo.java.bz2"。首先通过获取Path对象对应的文件名并逆转该字符串得到"2zb.avaj.omeDcedoC",然后通过有序映射SortedMap的heapMap()方法，查找最接近上述字符串的有序映射的部分视图，如输入"2zb.avaj.omeDcedoC"的查找结果subMap，只包含"2zb."对应的那个键值对，如果输入的是"zg.avaj.omeDcedoC",则subMap会包含成员变量codecs中的保存的所有的键值对。然后简单地获取subMap的最后一个元素的键，如果该键是逆转文件名的前缀，那么就找到了文件对应的编码/解码器，否则返回空。实现的代码如下：

``` java
public class CompressionCodecFactory {

    // 该有序映射保存了逆转文件后缀(包括后缀前的‘.’)到CompressionCodec的映射
    // 通过逆转文件后缀，我们可以找到最长匹配后缀
    private SortedMap<String, CompressionCodec> codecs = null;
    ......
     public CompressionCodec getCodec(Path file) {
    CompressionCodec result = null;
    if (codecs != null) {
      String filename = file.getName();
      String reversedFilename = new StringBuilder(filename).reverse().toString();
      SortedMap<String, CompressionCodec> subMap =
        codecs.headMap(reversedFilename);
      if (!subMap.isEmpty()) {
        String potentialSuffix = subMap.lastKey();
        if (reversedFilename.startsWith(potentialSuffix)) {
          result = codecs.get(potentialSuffix);
        }
      }
    }
    return result;
  }
}
```

### 压缩器和解压器

压缩器(Compressor)和解压缩器(Decompressor)是Hadoop压缩框架中的一对重要概念。Compressor可以插入压缩输出流的实现中，提供具体的压缩功能；相反，Decompressor提供具体的解压功能并插入CompressionInputStream中。Compressor和Decompressor的这种设计，最初是在Java的zlib库中引入的，对应的实现分别是java.util.zip.Deflater和java.util.zip.Inflater。下面以Compressor为例介绍这对组件。

Compressor通过setInput()方法接收数据到内部缓冲区，通过needsInput()的返回值，如果是false，表明缓冲区已满，这时必须通过compress方法获取压缩后的数据，释放缓冲区的空间。为了提高压缩效率，并不是每次用户调用setInput()方法压缩器就会立即工作，所以，为了通知压缩器所有数据已经写入，必须使用finish()方法。finish方法调用结束后，压缩器缓冲区中保持的已经压缩过的数据，可以继续通过compress()方法获得。至于要判断压缩器中是否还有未读取的压缩数据，则需要利用finished()方法来判断。

下面是使用Compressor的一个典型实例：

``` java
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.io.compress.Compressor;
import org.apache.hadoop.io.compress.zlib.BuiltInZlibDeflater;


public class CompressorDemo{
    public static void compressor() throws ClassNotFoundException,IOException{
        final int compressorOutputBufferSize = 64;
        // 读入被压缩的内容
        File fin = new File("CompressorDemo.java");
        InputStream in = new FileInputStream(fin);
        int datalength = in.available();
        byte[] inbuf = new byte[datalength];
        in.read(inbuf,0,datalength);
        in.close();

        // 长度受限制的输出缓冲区，用于说明finished()方法
        byte[] outbuf = new byte[compressorOutputBufferSize];

        Compressor compressor = new BuiltInZlibDeflater(); // 构造压缩器

        int step = 100; // 一些计数器
        int inputPos = 0;
        int putcount = 0;
        int getcount = 0;
        int compressedlen = 0;

        while ( inputPos < datalength){
            // 进行多次setInput()
            int len = (datalength-inputPos >= step) ? step:datalength-inputPos;
            compressor.setInput(inbuf,inputPos,len);
            putcount++;

            while (!compressor.needsInput()){
                compressedlen = compressor.compress(outbuf, 0, compressorOutputBufferSize);
                if (compressedlen > 0){
                    getcount++;  // 能读到数据
                }
            }

            inputPos += step;
        }

        compressor.finish();

        while (!compressor.finished()) { // 压缩器中有数据
            getcount++;
            compressor.compress(outbuf, 0, compressorOutputBufferSize);
        }

        System.out.println("Compress "+compressor.getBytesRead()+" byte into "+compressor.getBytesWritten());

        System.out.println("put "+putcount+" times and get "+getcount+" times");

        compressor.end(); // 停止
    }

    public static void main(String[] args) throws Exception{
        compressor();
    }
}
```

在压缩的过程中，Compressor可以通过getByteRead()和getBytesWritten()方法获得Compressor输入未压缩字节的总数和输出压缩字节的总数。

Compressor和Decompressor的类图如下：

![Compressor-Decompressor](/resource/blog/2016-08/Compressor-Decompressor.png)

Compressor.end()方法用于关闭解压缩器并放弃所有未处理的输入；reset()方法用于重置压缩器，以处理新的输入数据集合；reinit()方法更进一步允许使用Hadoop的配置系统，重置并重新配置压缩器。

### 压缩流和解压缩流

Java最初版本的输入/输出系统是基于流的，流抽象了任何有能力产出数据的数据源，或者有能力接收数据的接收端。一般来说，通过设计模式的修饰，可以为流添加一些额外的功能，如前面题解的序列化流ObjectInputStream和ObjectOutputStream。

压缩流(CompressionOutputStream)和解压缩流(CompressionInputStream)是hadoop压缩框架中的另一对重要的概念，它提供了基于流的压缩和解压缩能力。这里只分析和压缩相关的代码，即CompressionOutputStream及其子类。

CompressionOutputStream继承自OutputStream，也是个抽象类。CompressionOutputStream实现了OutputStream的close()方法和flush()方法，但用于输出数据的write()方法、用于结束压缩过程并将输入写到底层的finish方法和重置压缩状态的resetState()方法还是抽象方法，需要CompressionOutputStream的子类实现。CompressionOutputStream规定了压缩流的对外接口，可以提供一个通用的、使用压缩器的压缩流实现。CompressorStream使用了压缩器实现了一个通用的压缩流，主要代码如下：

``` java
public class CompressorStream extends CompressionOutputStream {
  protected Compressor compressor;
  protected byte[] buffer;
  protected boolean closed = false;

  // 构造函数：底层输出流、压缩器、缓冲区大小
  public CompressorStream(OutputStream out, Compressor compressor, int bufferSize) {
    super(out);
    // 参数检查
    if (out == null || compressor == null) {
      throw new NullPointerException();
    } else if (bufferSize <= 0) {
      throw new IllegalArgumentException("Illegal bufferSize");
    }

    this.compressor = compressor;
    buffer = new byte[bufferSize];
  }

  public CompressorStream(OutputStream out, Compressor compressor) {
    this(out, compressor, 512);
  }

  protected CompressorStream(OutputStream out) {
    super(out);
  }

  // 将待压缩的数据写入流中
  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    // Sanity checks
    if (compressor.finished()) {
      throw new IOException("write beyond end of stream");
    }
    if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
      throw new IndexOutOfBoundsException();
    } else if (len == 0) {
      return;
    }
    // 调用压缩器的setInput方法进入压缩器
    compressor.setInput(b, off, len);
    // 判断是否需要调用compress方法
    while (!compressor.needsInput()) {
      compress();
    }
  }

  // 提取数据
  protected void compress() throws IOException {
    int len = compressor.compress(buffer, 0, buffer.length);
    if (len > 0) {
      out.write(buffer, 0, len);
    }
  }

  // 结束输入
  @Override
  public void finish() throws IOException {
    if (!compressor.finished()) {
      compressor.finish();
      while (!compressor.finished()) {
        compress();
      }
    }
  }

  @Override
  public void resetState() throws IOException {
    compressor.reset();
  }

  // 关闭流
  @Override
  public void close() throws IOException {
    if (!closed) {
      try {
        finish(); // 结束压缩
      }
      finally {
        out.close();  // 关闭底层流
        closed = true;
      }
    }
  }

  private byte[] oneByte = new byte[1];
  @Override
  public void write(int b) throws IOException {
    oneByte[0] = (byte)(b & 0xff);
    write(oneByte, 0, oneByte.length);
  }

}

```

CompressorStream提供了几个不同的构造函数，用于初始化相关的成员变量。上述代码片段中保留了参数最多的构造函数，其中，CompressorStream需要底层输出流out和压缩时使用的压缩器，都可以作为参数传入构造函数。另一个参数是CompressorStream工作时使用的缓冲区buffer的大小，构造时会利用这个参数分配该缓冲区。

压缩器方法的典型调用顺序：

![调用顺序](/resource/blog/2016-08/compress.png)