---
title: Hadoop源码学习(15)——文件系统(1)
comments: true
toc: true
date: 2016-09-22 16:25:02
categories: BigData
tags : Hadoop
keywords: Hadoop,FileSystem
---

>**本节内容：**为了提供对不同数据访问的一致接口，Hadoop借鉴了Linux虚拟文件系统的概念，引入了Hadoop抽象文件系统，并在Hadoop抽象文件系统上，提供了大量的具体文件系统的实现，满足构建于Hadoop应用之上的数据访问需求。本节学习Hadoop文件系统API。


<!-- more -->

## 分布式文件系统的特性

- 访问透明性：用户不需要了解文件的分布性

- 位置透明性：客户程序可以使用单一的文件空间

- 移动透明性：文件被移动时，客户程序不需要改变

- 性能透明性，伸缩透明性，复制透明性，故障透明性，并发透明性

- 数据完整性、安全性和系统异构

## Hadoop文件系统类图

Hadoop提供了一个抽象的文件系统，该系统可以作为分布式系统实现也可是本地磁盘。HDFS是这个抽象文件系统的具体实现。抽象系统类org.apache.hadoop.fs.FileSystem的类图如下：

![FileSystem](/resource/blog/2016-08/FileSystem.png)



## API对应关系

和Linux与Java文件API类似，Hadoop抽象文件系统的方法可以分为两个部分：一部分用于处理文件和目录的相关事务，另一部分用于读写数据。下表总结了Hadoop抽象文件系统的文件操作与Java、Linux的对应关系

- Hadoop抽象文件系统文件操作(部分)

|HadoopFileSystem类|Java操作|Linux操作|描述|
| ---- | ---- | ---- | ---- |
|URL.openStream, FileSystem.open, FileSystem.create, FileSystem.append|URL.openStream|open|打开一个文件|
|FSDataInputStream.read|InputStream.read|read|读取文件中包含的数据|
|FSDataOutputStream.write|OutputStream.write|write|向文件中写数据|
|FSDataOutputStream.close, FSDataInputStream.close|InputStream.close,OutputStream.close|close|关闭一个文件|
|FSDataInputStream.seek|RandomAccessFile.seek|lseek|改变文件的读写位置|
|FileSystem.getFileStatus, FileSystem.get* |File.get* |stat|获取文件/目录的属性|
|FileSystem.set*|File.set*|chmod等|修改文件属性|
|FileSystem.createNewFile|File.createNewFile|create|创建一个文件|
|FileSystem.delete|File.delete|remove/rmdir|从文件系统中删除一个文件/文件夹|
|FileSystem.rename|File.renameTo|rename|更改文件/目录名|
|FileSystem.mkdirs|FileSystem.mkdir|mkdir|在给定目录下创建一个子目录|
|FileSystem.listStatus|File.list|readdir|读取一个目录下的项目|
|FileSystem.setWorkingDirectory| |getcwd/getwd|返回当前工作目录|
|FileSystem.setWorkingDirectory| |chdir|更改当前工作目录|