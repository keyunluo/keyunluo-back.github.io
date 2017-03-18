---
title: Hadoop源码学习(8)——序列化
comments: true
toc: true
date: 2016-08-10 14:25:02
categories: BigData
tags : Hadoop
keywords: Hadoop,IO,序列化
---

>**本节内容：**序列化是指将结构化对象转换成字节流以便在网络上传输或写到磁盘进行永久存储的过程。序列化在分布式数据处理的两大领域经常出现：进程间通信和永久存储。本节学习Hadoop序列化的实现机制。


<!-- more -->

## Java内建序列化机制

序列化用途：

- 作为一种持久化格式：一个对象被序列化后，它的编码可以被存储在磁盘上，供以后反序列化使用。
- 作为一种数据通信格式：序列化结果可以从一个正在运行的虚拟机，通过网络传输到另一个虚拟机上。
- 作为一种拷贝机制：将对象序列化到内存的缓冲区中，然后通过反序列化，可以得到一个对已存在对象进行深拷贝的新对象。

Java序列化机制将对象转换成连续的byte数据，使用时只需要在类声明中加入implements Serializable即可。序列化的结果中包含了大量与类相关的信息，父类的信息也会递归的保存下来，导致序列化结果膨胀，对于需要保存和处理大规模数据的Hadoop来说，需要一个新的序列化机制。

## Hadoop序列化机制

和Java序列化机制不同(在对象流ObjectOutputStream对象上调用writeObject()方法)，Hadoop的序列化机制通过调用对象的write()方法(它带有一个类型为DataOutput的参数)，将对象序列化到流中，反序列化则通过readFields()方法从流中读取数据，并且用户可以复用同一个对象得到多个反序列化的结果。

## Hadoop序列化机制的特征

- 紧凑：充分利用数据中心的带宽
- 快速：在进程间通信时会大量使用到序列化机制，因此必须减少序列化和反序列化的开销
- 可扩展：序列化机制要支持系统间通信协议的升级后和类的定义变化
- 互操作：可以支持不同开发语言间的通信，如C++和Java，这样的通信可以通过文件或IPC机制实现

Java的序列化机制虽然强大，却不符合上面这些要求，Java Serialization将每个对象的类名写入输出流中，导致要占用比原来对象更多的空间，同时，为了减少数据量，同一个类的对象的序列化结果只输出一份元数据，并通过某种形式的引用来共享元数据。引用导致对序列化后的流进行处理时需要保持一些状态。在一些场景中，有一个上百G的文件中，反序列化某个对象，需要访问文件中前面的某一个元数据，这将导致这个文件不能被切割，并通过MapReduce处理。

## Hadoop序列化框架

### 相关项目

大部分的MapReduce程序都使用Writable键-值对作为输入输出，但这并不是Hadoop API指定的，其他的序列化机制也能和Hadoop配合，并应用于MapReduce中。如：Hadoop Avro,Apache Thrift和Google Protocol Buffer。

- Avro:数据序列化系统，用于支持大批量数据交换的应用，它的主要特点是：支持二进制序列化方式，可以便捷、快速的处理大批量数据，动态语言支持良好，Avro提供的机制时动态语言可以很方便地处理Avro数据。
- Thrift:是一个可伸缩的、跨语言的服务开发框架，由FaceBook贡献给开源社区。基于Thrift的跨平台能力封装的Hadoop文件系统Thrift API提供了不同开发语言开发的系统访问HDFS的能力。
- Protocol Buffer：是Google内部的混合语言数据标准，提供了一种轻便高效的结构化数据存储方式，目前提供了C++/Java/Python三种语言的API，广泛应用于Google内部的通信协议、数据存储等领域。

### Serialization序列化框架

Hadoop提供了一个简单的序列化框架API，用于集成各种序列化实现，该框架由Serialization实现(在org.apache.hadoop.io.serializer包中)。

Serialization是一个接口，使用抽象工厂的设计模式，提供了一系列和序列化相关并相互依赖对象的接口。通过Serialization应用可以获得类型的Serializer实例，即将一个对象转换为一个字节流的实现实例，Deserializer实例和Serializer实例相反，它用于将一个字节流转换成一个对象。很明显，Serializer和Deserializer相互依赖，所以必须通过抽象工厂Serialization，才能获得对应的实现：

``` java
public interface Serialization<T> {

  // 客户端用于判断序列化实现是否支持该类对象
  boolean accept(Class<?> c);

  // 获得用于序列化对象的Serialization实现
  Serializer<T> getSerializer(Class<T> c);

  // 获得用于反序列化对象的Deserializer实现
  Deserializer<T> getDeserializer(Class<T> c);
}
```

如果需要使用Serializer来执行序列化，一般需要通过open()方法打开Serializer，open()方法传入一个底层的流对象，然后就可以使用serialize()方法序列化对象到底层的流中。最后序列化结束时，通过close()方法关闭Serializer，Serializer接口的相关代码如下：

``` java
public interface Serializer<T> {
  // 为输出(序列化)对象做准备
  void open(OutputStream out) throws IOException;

  // 将对象序列化到底层的流中
  void serialize(T t) throws IOException;

  // 序列化结束，清理
  void close() throws IOException;
}
```

Hadoop目前支持两个Serialization实现：支持Writable的WritableSerialization和支持Java序列化的JavaSerialization。通过JavaSerialization可以在MapReduce程序中方便地使用标准的Java类型，如int，String等，但Java的ObjectSerialization不如Hadoop序列化机制有效，不推荐使用。

### 使用序列化框架

将io.serializations属性设置为一个由逗号分隔的类名列表，即可以注册Serialization实现。它的默认值包括org.apache.hadoop.io.serializer.WritableSerialization和Avro指定序列化和反序列化类，这意味着只有Writable对象和Avro对象才可以在外部序列化和反序列化。