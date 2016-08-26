---
title: Hadoop源码学习(15)——文件系统(1)
comments: true
toc: true
date: 2016-08-17 16:25:02
categories: SourceCodeLearning
tags : Hadoop
keywords: Hadoop,FileSystem
---

>**本节内容：**为了提供对不同数据访问的一致接口，Hadoop借鉴了Linux虚拟文件系统的概念，引入了Hadoop抽象文件系统，并在Hadoop抽象文件系统上，提供了大量的具体文件系统的实现，满足构建于Hadoop应用之上的数据访问需求。本节学习Hadoop文件系统API。


<!-- more -->

## 类图

Hadoop提供了一个抽象的文件系统，该系统可以作为分布式系统实现也可是本地磁盘。HDFS是这个抽象文件系统的具体实现。抽象系统类org.apache.hadoop.fs.FileSystem的类图如下：

![FileSystem](/resource/blog/2016-08/FileSystem.png)



## API对应关系

和Linux与Java文件API类似，Hadoop抽象文件系统的方法可以分为两个部分：一部分用于处理文件和目录的相关事务，另一部分用于读写数据。下表总结了Hadoop抽象文件系统的文件操作与Java、Linux的对应关系

- Hadoop抽象文件系统文件操作(部分)

|HadoopFileSystem类|Java操作|Linux操作|描述|
| ---- | ---- | ---- | ---- |
|URL.openStream,FileSystem.open,FileSystem.create.FileSystem.append|URL.openStream|open|打开一个文件|
|FSDataInputStream.read|InputStream.read|read|读取文件中包含的数据|