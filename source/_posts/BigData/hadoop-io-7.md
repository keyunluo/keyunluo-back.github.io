---
title: Hadoop源码学习(11)——SequenceFile
comments: true
toc: true
date: 2016-08-13 08:25:02
categories: BigData
tags : Hadoop
keywords: Hadoop,IO,SequenceFile
---

>**本节内容：**SequenceFile为二进制键值对提供了一个持久数据结构，也可以作为小文件的容器，HDFS和MapReduce是针对大文件优化的，所以通过SequenceFile类型将小文件包装起来，可以获得更高效率的存储和处理。


<!-- more -->

## SequenceFile类

SequenceFile类位于org.apache.hadoop.io包内，类结构如下：

![SequenceFile类](/resource/blog/2016-08/SequenceFile.png)

## SequenceFile的格式

SequenceFile文件由文件头和随后的一条或多条记录组成。顺序文件的前三个字节是SEQ，紧随其后的一个字节表示顺序文件的版本号。文件头还包括其他字段，例如键和值类的名称、数据压缩细节、用户定义的元数据以及同步标识。同步标识用于在读取文件时能够从任意位置开始识别边界记录。

记录的内部结构取决于是否启用压缩。如果已经启用，则取决于记录压缩还是数据块压缩。

如果没有启用压缩(默认情况)，那么每条记录则由记录长度(字节数)、键长度、键和值组成。长度字段为4字节长的整数，遵循java.io.DataOutput类中的writeInt()方法的约定，写入顺序文件的类定义Serialization类来实现键和值的序列化。

记录压缩格式与无压缩情况基本相同，只不过值是用文件头定义的codec压缩的，但是键并没有被压缩。

![SequenceFile-无压缩和记录压缩](/resource/blog/2016-08/sequencefilewithnocompression.jpg)

块压缩是指一次性压缩多条记录，因为它可以利用记录间的相似性进行压缩，所以相较于单条记录压缩方法，该方法的压缩效率更高，如下图所示。可以不断向数据块中压缩记录，直到块的字节数不小于io.seqfile.compress.blocksize属性中设置的字节数：默认为1MB。每一个新块的开始处都需要插入同步标识。数据块的格式如下：首先是一个指示数据块中字节数的字段；紧接着是4个压缩字段(键长度、键、值长度和值)

![SequenceFile-块压缩](/resource/blog/2016-08/sequencefilewithblockcompression.jpg)

## SequenceFile写操作

通过createWriter()静态方法可以创建SequenceFile对象，并返回SequenceFile.Writer实例。该静态方法有多个重载版本，但都需要指定待写入的数据流(FSDataOutputStream或FileSystem对象和Path对象)，Configuration对象，以及键和值的类型，写入的参数信息等。另外，可选参数包括压缩类型以及相应的codec,Progressable回调函数用于通知写入的进度，以及在SequenceFile头文件中存储的Metadata实例。

存储在SequenceFile中的键和值并不一定需要是Writable类型，只要能被Serialization序列化和反序列化，任何类型都可以。

一旦拥有SequenceFile.Writer实例，就可以通过append()方法在文件末尾附加键值对。写完后，可以调用close()方法(SequenceFile.Writer实现了java.io.Closable接口)。

``` java
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.IOUtils;
import java.io.IOException;
import java.net.URI;

public class SequenceFileWriteDemo{
    private static final String[] DATA = {
    "One, two, buckle my shoe",
    "Three, four, shut the door",
    "Five, six, pick up sticks",
    "Seven, eight, lay them straight",
    "Nine, ten, a big fat hen"
};

public static void main(String[] args) throws IOException{
    String url = args[0];
    Configuration conf = new Configuration();
    FileSystem fs = FileSystem.get(URI.create(url),conf);
    Path path = new Path(url);

    IntWritable key = new IntWritable();
    Text value = new Text();
    SequenceFile.Writer writer = null;
    try{
        writer = SequenceFile.createWriter(fs,conf,path,key.getClass(),value.getClass());
        for (int i=0;i<1024;i++){
            key.set(1024-i);
            value.set(DATA[i % DATA.length]);
            writer.append(key,value);
        }
    } finally{
        IOUtils.closeStream(writer);
    }
}

}
```

编译与运行：

``` python
➜  project export CLASSPATH=$HADOOP_CLASSPATH:$CLASSPATH
➜  project javac SequenceFileWriteDemo.java
➜  project java SequenceFileWriteDemo numbers.seq

```

## SequenceFile读操作

从头到尾读取顺序文件不外乎创建SequenceFile.Reader实例后反复调用next()方法迭代读取记录。读取的是哪条记录与你使用的序列化框架有关。如果使用Writable类型，那么通过键和值作为参数的next()方法可以将数据流中的下一条键值对读入变量中。`public boolean next(Writable key,Writable val)`。对于其他非Writable类型的序列化框架，例如Apache Thrift，则应该使用下面两个方法：

``` java
public Object next(Object key) throws IOException
public Object getCurrentValue(Object val) throws IOException
```

在这种情况下，需要确保io.serializations属性已经设置了你想要的序列化框架。如果next()方法返回的是非null对象，则可以从数据流中读取键、值对，并且可以通过getCurrentValue()方法读取该值。否则，如果next()返回null值，表示已经读到文件末尾。

``` java
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.ReflectionUtils;
import java.io.IOException;
import java.net.URI;

public class SequenceFileReadDemo{

public static void main(String[] args) throws IOException{
    String url = args[0];
    Configuration conf = new Configuration();
    FileSystem fs = FileSystem.get(URI.create(url),conf);
    Path path = new Path(url);

    SequenceFile.Reader reader = null;
    try{
        reader = new SequenceFile.Reader(fs,path,conf);
        Writable key = (Writable) ReflectionUtils.newInstance(reader.getKeyClass(),conf);
        Writable value = (Writable) ReflectionUtils.newInstance(reader.getValueClass(),conf);
        long position = reader.getPosition();
        while (reader.next(key,value)){
            String syncSeen = reader.syncSeen() ? "*" : " ";
            System.out.printf("[%s%s]\t%s\t%s\n",position,syncSeen,key,value);
            position = reader.getPosition();
        }
    } finally{
        IOUtils.closeStream(reader);
    }
}

}
```

## 通过命令行接口显示SequenceFile

hdfs dfs命令有一个-text选项可以以文本形式显示顺序文件，该选项可以查看文件的代码，由此检测出文件的类型并将其转换成相应的文本。该选项可以识别gzip压缩文件和顺序文件；否则，假设输入为纯文本文件。

对于顺序文件，如果键和值是由具体含义的字符串表示，那么这个命令就非常有用(通过toString()方法定义)，同样，如果有自己定义的键或值的类，则应该确保它们在Hadoop类路径目录下。

``` python
➜  project hdfs dfs -text numbers.seq | head
16/08/13 16:39:23 INFO zlib.ZlibFactory: Successfully loaded & initialized native-zlib library
16/08/13 16:39:23 INFO compress.CodecPool: Got brand-new decompressor [.deflate]
1024    One, two, buckle my shoe
1023    Three, four, shut the door
1022    Five, six, pick up sticks
1021    Seven, eight, lay them straight
1020    Nine, ten, a big fat hen
1019    One, two, buckle my shoe
1018    Three, four, shut the door
1017    Five, six, pick up sticks
1016    Seven, eight, lay them straight
1015    Nine, ten, a big fat hen

```

## SequenceFile的排序和合并

MapReduce是对多个顺序文件进行排序或合并的最有效的方法。MapReduce本身是并行的，并且可由用户指定要使用多少个reducer，该数决定这输出分区数。例如，通过指定一个reducer，可以得到一个输出文件。

除了MapReduce实现排序/归并，还有一种方法是使用SequenceFile.Sorter类中的sort()方法和merge()方法。它们比MapReduce更早出现，比MapReduce更底层，为了实现并行，需要手动对数据进行分区，因此不建议使用。