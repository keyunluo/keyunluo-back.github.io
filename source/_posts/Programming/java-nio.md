---
title: Java中的NIO块
comments: true
toc: true
date: 2016-08-08 08:25:02
categories: Programming
tags : Java
keywords: Java，NIO
---

>**本节内容：**Java中的IO的输入流和输出流都是阻塞式的，如果没有读到有效数据则程序会在此处阻塞该线程的执行，并且，传统的输入流、输出流都是通过字节的移动来处理的，通常效率不高，新的IO(NIO)从JDK1.4版本开始，新增了许多用于处理输入/输出的类。

<!-- more -->

## 概述

新IO采用内存映射文件的方式来处理输入/输出，新IO将文件或文件的一段区域映射到内存中，这样就可以像访问内存一样访问文件，速度要快的多。

Java中与新IO相关的包如下：

- java.nio包：包含了各种与Buffer相关的类
- java.nio.channels包：主要包含了与Channel和Selector相关的类
- java.nio.charset包：主要包含与字符集相关的类
- java.nio.channels.spi包：主要包含与Channel相关的服务提供者编程接口
- java.nio.charset.spi包：主要包含与字符集相关的服务提供者编程接口

Channel(通道)和Buffer(缓冲)是新IO中的两个核心对象，Channel是对传统的输入输出系统的模拟，在新IO中，所有的数据都要通过通道传输，与传统的输入输出流相比，主要在于它提供了一个map()方法，通过该map()方法可以将"一块数据"映射到内存中。

Buffer可以被理解为一个容器，它的本质是一个数组，发送到Channel中的所有对象都必须首先放在Buffer中，Buffer可以到Channel中取数据，也允许使用Channel直接将文件的某块数据映射成Buffer。

除了Channel和Buffer之外，新IO还提供了用于将Unicode字符串映射成字节序列以及逆映射操作的Charset类，也提供了用于支持费非阻塞式输入输出的Selector类。

## 使用Buffer

Buffer可以保存多个类型相同的数据，它是一个抽象类，最常用的子类是ByteBuffer，其他基本类型Boolean除外，都有相应的Buffer类：CharBuffer、ShortBuffer、IntBuffer、FloatBuffer、DoubleBuffer。

比较常用的是ByteBuffer和CharBuffer，通过`static XxxBuffer allocate(int capacity)`创建一个容量为capacity的XxxBuffer对象。其中ByteBuffer类还有一个子类：MappedByteBuffer，它哟关于表示Channel将磁盘文件的部分或全部内容映射到内存中后得到的结果，通常MappedByteBuffer对象由Channel的map()方法返回。

Buffer中有三个重要的概念：容量(capacity)、界限(limit)和位置(position):

- 容量：该Buffer的最大数据容量，创建后不能更改
- 界限：第一个不能被读写的缓冲区位置索引
- 位置：下一个可以被读出或者写入的缓冲区的位置索引

除此之外，Buffer里还支持一个可选的标记mark，Buffer允许直接将position定位到该mark处，这些值满足如下大小关系：`0=<mark=<position=<limit=<capacity`.

Buffer的主要作用就是装入数据(put)，然后输出数据(get)，几个重要的方法：

- flip():将limit设置为position所在位置，并将position设为0，为输出数据做准备
- clear():将position置为0，将limit置为capacity，为装入数据做准备


## 使用Channel

Channel类似于传统的流对象，但有两个显著的差别：

- Channel可以直接将指定文件的部分或全部直接映射成Buffer
- 程序不能直接访问Channel中数据，Channel只能与Buffer进行交互

Java为Channel接口提供了DatagramChannel(UDP网络通信)、FileChannel、Pipe.SinkChannel(线程间通信管道)、Pipe.SourceChannel、SelectableChannel、ServerSocketChannel(TCP网络通信)、SocketChannel等实现类。

所有的Channel都不应该通过构造器直接创建，而是通过传统的节点InputStream、outputStream的getChannel()方法来返回对应的Channel。

Channel中最常见的三类方法是map()、read()、write()。map()方法的方法签名为：`MappedByteBuffer map(FileChannel.MapMode mode,long position,long size)`,第一个参数执行映射的模式：只读、读写，而第二个、第三个参数用于控制将Channel的哪些数据映射成ByteBuffer。

### 例子1:Channel读写

直接将FileChannel的全部数据映射成ByteBuffer

``` java
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class FileChannelTest{
    public static void main(String[] args){
        File file = new File("FileChannelTest.java");
        try(FileChannel inChannel = new FileInputStream(file).getChannel();
            FileChannel outChannel = new FileOutputStream("out.txt").getChannel()){
            // 将FileChannel里的全部数据映射成ByteBuffer
            MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY,0,file.length());
            buffer.clear();
            // 直接将buffer里面的数据全部取出
            outChannel.write(buffer);
            // 再次调用buffer的clear()方法，复原limit、position的位置
            buffer.clear();
            // 使用UTF-8的字符集来创建解码器
            Charset charset = Charset.forName("UTF-8");
            // 创建解码器(CharsetDecoder)对象
            CharsetDecoder decoder = charset.newDecoder();
            // 使用解码器将ByteBuffer转换成CharBuffer
            CharBuffer charBuffer = decoder.decode(buffer);
            // CharBuffer的toString方法可以获取对应的字符串
            System.out.println(charBuffer.toString());
            }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
```

不仅InputStream、OutputStream包含了getChannel()方法，在RandomAccessFile中也包含了该方法，RandomAccessFile返回的FileChannel()是只读的还是读写的，取决于RandomAccessFile打开文件的模式。

``` java
File file = new File("a.txt");
FileChannel randomChannel = new RandomAccessFile(file,"rw").getChannel();
ByteBuffer buffer = randomChannel.map(FileChannel.MapMode.READ_ONLY,0,file.length());
randomChannel.position(file.length());
randomChannel.write(buffer);
```

### 例子2:多次map

如果channel对应的文件过大，一次性将所有的文件内容映射到内存引起性能下降，可以多次映射

``` java
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class ReadFile{
    public static void main(String[] args) throws IOException{
    try(
    // 创建文件输入流
    FileInputStream fin = new FileInputStream("ReadFile.java");
    // 创建一个FileChannel
    FileChannel fch = fin.getChannel()
    ){
    // 定义一个ByteBuffer对象
    ByteBuffer bbuf = ByteBuffer.allocate(256);
    // 将FileChannel中的数据放入ByteBuffer中
    while (fch.read(bbuf) != -1){
    // 锁定Buffer的空白区
    bbuf.flip();
    // 创建Charset对象
    Charset charset = Charset.forName("UTF-8");
    // 创建解码器对象
    CharsetDecoder decoder = charset.newDecoder();
    // 将ByteBuffer的内容转码
    CharBuffer cbuf = decoder.decode(bbuf);
    System.out.println(cbuf);
    // 将Buffer初始化，为下一次读取数据作准备
    bbuf.clear();
    }
  }
 }
}
```

## 字符集和Charset

编码：将明文的字符序列转换成计算机中的二进制序列

解码：将二进制序列转换成字符序列

比较常用的字符编码：

- GBK: 简体中文字符集
- BIG5: 繁体中文字符集
- ISO-8859-1/ISO-LATIN-1: 拉丁字母表
- UTF-8: 8位UCS转换格式
- UTF-16BE: 16位UCS转换格式，大端字节(最低位地址存放高位字节)字母顺序
- UTF-16LE: 16位UCS转换格式，小端字节(最低位地址存放低位字节)字母顺序
- UTF-16: 16位UCS转换格式，字节顺序由可选的字节顺序标记来标识

一旦知道了字符集的别名后，程序就可以调用Charset的forName()方法来创建对应的Charset对象，然后通过该对象的newDecoder/newEncoder这两个方法分别返回CharsetDecoder和CharsetEncoder对象，调用CharsetDecoder的decode方法就可以将ByteBuffer转换成CharBuffer，调用CharsetEncoder的encode方法就可以将CharBuffer或String转换成ByteBuffer。

``` java
Charset cn = Charset.forName("GBK");
CharsetEncoder cnEncoder = cn.newEncoder();
CharsetDecoder cnDecoder = cn.newDecoder();

CharBuffer cbuf = CharBuffer.allocate(8);
cbuf.put('孙');
cbuf.put('悟');
cbuf.put('空');
cbuf.flip();
ByteBuffer bbuf = cnEncoder.encode(cbuf);
System.out.println(cnDecoder.decode(bbuf));
```

Charset类提供了如下三个方法：

- CharBuffer decode(ByteBuffer bb): 将ByteBuffer中的字节序列转换成字符序列的便捷方法
- ByteBuffer encode(CharBuffer cb):将CharBuffer中的字符序列转换成字节序列的便捷方法
- ByteBuffer encode(String str): 将String中的字符序列转换成字节序列的便捷方法

在获取了Charset对象后，如果仅仅需要进行简单的编码、解码操作，无需创键CharsetEncoder和CharsetDecoder对象，直接调用Charset的encode()和decode()方法进行编码、解码即可。

String类里面也提供了一个getBytes(String charset)方法，该方法返回byte[],也可以使用指定的字符集将字符串转换成字节序列。

## 文件锁

在NIO中，Java提供了FileLock来支持文件锁定功能，在FileChannel中提供的lock()/tryLock()方法可以获得文件锁FileLock对象，从而锁定文件(排它锁)。lock()尝试锁定文件时，如果无法获得文件锁，程序将抑制阻塞，而tryLock则返回null。

如果FileChannel只想锁定部分内容，择可以使用如下的方法：

- lock(long position,long size,boolean shared):对文件从position开始，长度为size的内容加锁，该方法是阻塞式的。
- tryLock(long position,long size,boolean shared):对文件从position开始，长度为size的内容加锁，该方法是非阻塞式的。

当参数shared是true时，表明该锁是一个共享锁，它将允许多个进程来读取该文件，但阻止其他进程获得对该文件的排它锁。当shared为false时，表明该锁是一个排它锁，它将锁住对该文件的读写。程序通过调用FileLock的isShared来判断它获得的锁是否为共享锁。