---
title: Hadoop源码学习(5)——Hadoop-Common-IO
comments: true
toc: true
date: 2016-08-06 09:25:02
categories: BigData
tags : Hadoop
keywords: Hadoop，IO
---

>**本节内容：**本节学习Hadoop-Common-Project中的hadoop-common-io模块，该模块由传统的I/O系统发展而来，但又有些不同，Hadoop需要处理P、T级别的数据，所以在org.apache.hadoop.io包中包含了一些面向海量数据处理的基本输入输出工具，本节主要学习IOUtils类。


<!-- more -->

## Hadoop-IO 目录结构

``` txt
.
├── AbstractMapWritable.java
├── ArrayFile.java
├── ArrayPrimitiveWritable.java
├── ArrayWritable.java
├── BinaryComparable.java
├── BloomMapFile.java
├── BooleanWritable.java
├── BoundedByteArrayOutputStream.java
├── ByteBufferPool.java
├── BytesWritable.java
├── ByteWritable.java
├── Closeable.java
├── compress
├── CompressedWritable.java
├── DataInputBuffer.java
├── DataInputByteBuffer.java
├── DataOutputBuffer.java
├── DataOutputByteBuffer.java
├── DataOutputOutputStream.java
├── DefaultStringifier.java
├── DoubleWritable.java
├── ElasticByteBufferPool.java
├── EnumSetWritable.java
├── FastByteComparisons.java
├── file
├── FloatWritable.java
├── GenericWritable.java
├── InputBuffer.java
├── IntWritable.java
├── IOUtils.java
├── LongWritable.java
├── MapFile.java
├── MapWritable.java
├── MD5Hash.java
├── MultipleIOException.java
├── nativeio
├── NullWritable.java
├── ObjectWritable.java
├── OutputBuffer.java
├── package.html
├── RawComparator.java
├── ReadaheadPool.java
├── retry
├── SecureIOUtils.java
├── SequenceFile.java
├── serializer
├── SetFile.java
├── ShortWritable.java
├── SortedMapWritable.java
├── Stringifier.java
├── Text.java
├── TwoDArrayWritable.java
├── UTF8.java
├── VersionedWritable.java
├── VersionMismatchException.java
├── VIntWritable.java
├── VLongWritable.java
├── WritableComparable.java
├── WritableComparator.java
├── WritableFactories.java
├── WritableFactory.java
├── Writable.java
├── WritableName.java
└── WritableUtils.java

5 directories, 59 files
```

Hadoop 提供了一些如下一些与IO相关的类：

- org.apache.hadoop.io (基础类型读写)
- org.apache.hadoop.io.compress (数据压缩，减少文件所需的存储空间，加快数据传输速度)
- org.apache.hadoop.io.file.tfile (TFile文件类型)
- org.apache.hadoop.io.nativeio (JNI包装的本地IO接口)
- org.apache.hadoop.io.retry (RPC重试)
- org.apache.hadoop.io.serializer (序列化)
- org.apache.hadoop.io.serializer.avro  (为Avro提供数据序列化操作)

## org.apache.hadoop.io

该包下的类提供了一些基础类型的读写，包括Writable机制、Comparable机制、数据类型(Int、Byte、Bytes、Long、Double、Float、Boolean、Text、Map)、基于文件的数据结构(SequenceFile、MapFile)、Buffer机制(InputBuffer、OutputBuffer)、线程池(Pool)、IO操作工具等。

## IOUtils类详细学习

### 类图
![IOUtils](/resource/blog/2016-08/hadoop-common-io-ioutils.png)

从类图中可以看出，主要由字节流拷贝、字节流读取出错包装、读取指定位置的全部字节、跳过指定位置的全部字节、关闭可关闭对象/流/Socket、空输出流写、全部输出流写等。

### 字节流拷贝

该方法有五种形式，核心方法是：

``` java
  public static void copyBytes(InputStream in, OutputStream out, int buffSize)
    throws IOException {
    // 判断是否是处理流：PrintStream
    PrintStream ps = out instanceof PrintStream ? (PrintStream)out : null;
    byte buf[] = new byte[buffSize];
    int bytesRead = in.read(buf);
    // 循环写
    while (bytesRead >= 0) {
      out.write(buf, 0, bytesRead);
      if ((ps != null) && ps.checkError()) {
        throw new IOException("Unable to write to output stream.");
      }
      bytesRead = in.read(buf);
    }
  }
```

以及copy指定大小的数据：

``` java
  public static void copyBytes(InputStream in, OutputStream out, long count,
      boolean close) throws IOException {
    // 缓冲区大小
    byte buf[] = new byte[4096];
    // 需要拷贝的数据大小
    long bytesRemaining = count;
    int bytesRead;

    try {
      while (bytesRemaining > 0) {
        int bytesToRead = (int)
          (bytesRemaining < buf.length ? bytesRemaining : buf.length);

        bytesRead = in.read(buf, 0, bytesToRead);
        if (bytesRead == -1)
          break;

        out.write(buf, 0, bytesRead);
        bytesRemaining -= bytesRead;
      }
      // 关闭读写指针
      if (close) {
        out.close();
        out = null;
        in.close();
        in = null;
      }
    } finally {
      // 关闭流
      if (close) {
        closeStream(out);
        closeStream(in);
      }
    }
  }
```

### 读取全部字节

从输入流中读取

``` java
  public static void readFully(InputStream in, byte buf[],
      int off, int len) throws IOException {
    int toRead = len;
    while (toRead > 0) {
      int ret = in.read(buf, off, toRead);
      if (ret < 0) {
        throw new IOException( "Premature EOF from inputStream");
      }
      toRead -= ret;
      off += ret;
    }
  }
```