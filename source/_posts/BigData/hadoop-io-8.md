---
title: Hadoop源码学习(12)——MapFile
comments: true
toc: true
date: 2016-08-13 08:25:02
categories: BigData
tags : Hadoop
keywords: Hadoop,IO,MapFile
---

>**本节内容：**MapFile是已经排过序的SequenceFile，它有索引，所以可以按键查找。可以将MapFile视为java.util.Map的持久化形式(尽管它并没有实现该接口)，它的大小可以超过保存在内存中一个map的大小。


<!-- more -->

## MapFile相关类

与MapFile相关的类共有3个，它们都位于org.apache.hadoop.io包下：

- ArrayFile：一种稠密的基于文件的从整数到值的映射。整型表示数组元素的索引，值是Writable类型。
- BloomMapFile：使用动态Bloom过滤器来提供快速的键之间的测试，并且提供了一个快速的Reader.get(WritableComparable, Writable)操作，尤其在稀疏密度的MapFile中。该实现使用一个动态的布隆过滤器来检测某个给定的键是否在map文件中，这个操作在内存中实现，故而速度比较快，但可能误判。经过布隆过滤器过滤后，如果存在相应的结果，则调用get()方法。
- SetFile：一个基于文件的keys集合。用于存储Writable键的集合，键必须按照排好的顺序添加。

![MapFile类](/resource/blog/2016-08/MapFile.png)

## MapFile写操作

MapFile的写操作类似于SequenceFile的写操作。新建一个MapFile.Writer实例，然后调用append()方法顺序写入文件内容。如果不按顺序写入，就会抛出一个IOException异常。键必须是WritableComparable类型的实例，值必须是Writable类型的实例，这与SequenceFile正好相反，后者可以为其条目使用任意序列化框架。

``` java
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.IOUtils;
import java.io.IOException;
import java.net.URI;

public class MapFileWriteDemo{
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

    IntWritable key = new IntWritable();
    Text value = new Text();
    MapFile.Writer writer = null;
    try{
        writer = new MapFile.Writer(conf,fs,url,key.getClass(),value.getClass());
        for (int i=0;i<1024;i++){
            key.set(i+1);
            value.set(DATA[i % DATA.length]);
            writer.append(key,value);
        }
    } finally{
        IOUtils.closeStream(writer);
    }
}

}
```

编译运行：
``` python
➜  project export CLASSPATH=$HADOOP_CLASSPATH:$CLASSPATH
➜  project javac MapFileWriteDemo.java
注: MapFileWriteDemo.java使用或覆盖了已过时的 API。
注: 有关详细信息, 请使用 -Xlint:deprecation 重新编译。
➜  project java MapFileWriteDemo  mapfile.map
16/08/13 15:07:57 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
16/08/13 15:07:58 INFO compress.CodecPool: Got brand-new compressor [.deflate]
16/08/13 15:07:59 INFO compress.CodecPool: Got brand-new compressor [.deflate]
➜  project hdfs dfs -ls
Found 2 items
drwxr-xr-x   - streamer supergroup          0 2016-05-25 22:37 COPROCESSOR
drwxr-xr-x   - streamer supergroup          0 2016-08-13 15:07 mapfile.map
➜  project hdfs dfs -text mapfile.map/data | head
16/08/13 15:10:20 INFO zlib.ZlibFactory: Successfully loaded & initialized native-zlib library
16/08/13 15:10:20 INFO compress.CodecPool: Got brand-new decompressor [.deflate]
1   One, two, buckle my shoe
2   Three, four, shut the door
3   Five, six, pick up sticks
4   Seven, eight, lay them straight
5   Nine, ten, a big fat hen
6   One, two, buckle my shoe
7   Three, four, shut the door
8   Five, six, pick up sticks
9   Seven, eight, lay them straight
10  Nine, ten, a big fat hen

➜  project hdfs dfs -text mapfile.map/index
16/08/13 15:10:41 INFO zlib.ZlibFactory: Successfully loaded & initialized native-zlib library
16/08/13 15:10:41 INFO compress.CodecPool: Got brand-new decompressor [.deflate]
16/08/13 15:10:41 INFO compress.CodecPool: Got brand-new decompressor [.deflate]
16/08/13 15:10:41 INFO compress.CodecPool: Got brand-new decompressor [.deflate]
16/08/13 15:10:41 INFO compress.CodecPool: Got brand-new decompressor [.deflate]
1   128
129 6079
257 12054
385 18030
513 24002
641 29976
769 35947
897 41922

```

可以发现MapFile包含data和index两个文件，这两个文件都是SequenceFile，data文件包含所有记录，index文件包含一部分键和data文件键到其偏移量的映射。

从输出中可以看见，默认情况下只有每隔128隔键才有一个包含在index文件中，当然也可以调整，调用MapFile.Writer实例的setIndexInterval()方法来设置io.map.index.interval属性即可。增加索引间隔大小可以有效减少MapFile存储索引所需要的内存，但降低了随机访问效率。

因为索引只保留了一部分键，所以整个MapFile无法枚举所有键甚至计算它自己有多少键，唯一的办法是读取整个文件。

## MapFile读操作

在MapFile中依次遍历文件中所有条目的过程类似于SequenceFile中的过程：首先新建一个MapFile.Reader实例，然后调用next()方法，直到返回值为false，表示没有条目返回，因为已经读到文件末尾，通过调用get()方法可以随机访问文件中的数据，返回值用于确定是否在MapFile中找到相应的条目，如果是null，说明指定key没有相应的条目。

getClosest()方法与get()方法类似，只不过它返回的是与指定键匹配的最接近的键，而不是在不匹配时返回null，更准确地说，如果MapFile包含指定的键，则返回对应的条目，否则，返回MapFile中第一个大于(或小于，由相应的boolean参数指定)指定键的键。

大型MapFile的索引会占据大量内存，不要在修改索引间隔之后重建索引，要在读取索引时设置io.mao.index.skip属性来加载一定比例的索引键。该属性通常设置为0，意味着不跳过索引键，如果设置为1，则表示每次跳过索引键中的一个。设置的越大，越节省内存，但会增加搜索时间，因为平均而言，扫描的键更多。

## 将SequenceFile转换成MapFile

在MapFile中搜索相当于在加有索引和排过序的SequenceFile中搜索，下面例子显示了对MapFile调用fix()静态方法，将SequenceFile重建索引成MapFile。

``` java
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.SequenceFile;
import java.net.URI;

public class MapFileFixer{

public static void main(String[] args) throws Exception{
    String url = args[0];
    Configuration conf = new Configuration();
    FileSystem fs = FileSystem.get(URI.create(url),conf);

    Path map = new Path(url);
    Path mapData = new Path(map,MapFile.DATA_FILE_NAME);

    // 从sequence 数据文件获得key和value的类型
    SequenceFile.Reader reader = new SequenceFile.Reader(fs,mapData,conf);
    Class keyClass = reader.getKeyClass();
    Class valueClass = reader.getValueClass();
    reader.close();

    // 创建map索引文件
    long entries = MapFile.fix(fs,map,keyClass,valueClass,false,conf);
    System.out.printf("创建MapFile %s ,入口是 %d",map,entries);
}

}
```

Fix()方法通常用于重建已损坏的索引，但它能够从头开始建立新的索引，所以此处我们可以使用此方法满足需求，具体用法如下：

(1) 将名为 numbers.seq的顺序文件排序后，保存到名为number.map的文件夹下，该文件夹就是最终的MapFile(如果顺序文件已经排过序，则可以跳过这一步。只需要把这个文件复制到number.map/data文件，然后直接跳至第三步)：

``` shell
hadoop jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.6.4.jar sort -r 1 \
-inFormat org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat \
-outFormat org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat \
-outKey org.apache.hadoop.io.IntWritable \
-outValue org.apache.hadoop.io.Text \
numbers.seq numbers.map
```
(2) 将MapReduce的输出重命名为data文件：

``` shell
hdfs dfs -mv numbers.map/part-r-00000 numbers.map/data
```

(3) 建立index文件

``` shell
java MapFileFixer numbers.map
```

``` python
➜  project java MapFileFixer numbers.map
16/08/13 17:45:39 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
16/08/13 17:45:40 INFO compress.CodecPool: Got brand-new compressor [.deflate]
创建MapFile numbers.map ,入口是 1024%
➜  project hdfs dfs -ls numbers.map
Found 2 items
-rw-r--r--   1 streamer supergroup      40403 2016-08-13 17:43 numbers.map/data
-rw-r--r--   1 streamer supergroup        336 2016-08-13 17:45 numbers.map/index
```
大功告成。