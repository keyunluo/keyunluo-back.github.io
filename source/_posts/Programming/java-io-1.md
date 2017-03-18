---
title: Java中的IO流(1)
comments: true
toc: true
date: 2016-08-06 11:25:02
categories: Programming
tags : Java
keywords: Java，IO流
---

>**本节内容：**Java中的IO流是实现输入输出的基础，在Java中把不同的输入/输出源(键盘、文件、网络连接等)抽象表述为“流(stream)”,通过流的方式Java可以使用相同的方式来访问不同的输入/输出源。stream是从起源(source)到接受(sink)的有序数据。

<!-- more -->

## 流的分类

1. 输入流和输出流 (按流的流向来分)

    输入流：InputStream和Reader作为基类

    输出流：OutputStream和Writer作为基类

2. 字节流和字符流 (操作的数据大小)

    字节流：8位字节，由InputStream和OutputStream作为基类

    字符流：16位字符，主要由Reader和Writer作为基类

3. 节点流和处理流 (流的角色)

    节点流：可以从/向一个特定的IO设备(磁盘、网络)读写数据的流，也被称为低级流

    处理流：对一个已经存在的流进行连接或封装，通过封装后的流来实现数据的读写功能，也被称为高级流

4. 处理流的功能

    - 性能的提高：以增加缓冲的方式来提高输入/输出的效率
    - 操作的便捷：一次性输入/输出大批量的内容
    - 屏蔽了设备无关性：代码书写更加简洁

## InputStream和Reader

InputStream和Reader是所有输入流的抽象基类,本身不能创建实例，但它们分别有一个用于读取文件的输入流:FileInputStream和FileReader，它们都是节点流——会和指定文件关联。

- InputStream
主要包含下面三个方法：

    - **int read()**:从输入流中读取单个字节，返回所读取的字节数据(字节数据可以直接转换成int类型)
    - **int read(byte[] b)**:从输入流中最多读取b.length个字节的数据，并将其存储在字节数组b中，返回实际读取的字节数
    - **int read(byte[] b,int off ,int len)**:从输入流中最多读取len个字节的数据，并将其存储在数组b中，放入数组b中时，并不是从数组起点开始，而是从off位置开始，返回实际读取的字节数

- Reader
主要包含下面三个方法：

    - **int read()**:从输入流中读取单个字符，返回所读取的字符数据(字符数据可以直接转换成int类型)
    - **int read(char[] cbuf)**:从输入流中最多读取cbuf.length个字符的数据，并将其存储在字符数组cbuf中，返回实际读取的字符数
    - **int read(char[] cbuf,int off ,int len)**:从输入流中最多读取len个字符的数据，并将其存储在字符数组cbuf中，放入数组cbuf中时，并不是从数组起点开始，而是从off位置开始，返回实际读取的字符数

- 一个简单的例子-FileInputStream：读取源文件本身

``` java
import java.io.*;

public class FileInput{
    public static void main(String[] args) throws IOException{
        // 创建字节输入流
        FileInputStream fin = new FileInputStream("FileInput.java");
        // 创建一个长度为1024的缓冲
        byte[] buf = new byte[1024];
        // 用于保存实际读取的字节数
        int hasRead = 0;
        // 使用循环来重复读取
        while ((hasRead = fin.read(buf)) > 0){
            // 读取字节，将字节数组转换成字符串输入
            System.out.print(new String(buf,0,hasRead));
        }
        // 关闭文件输入流，放在finally块里面更加安全
        fin.close();
    }
}
```
上面程序最后使用了fin.close()来关闭该文件输入流，与JDBC编程一样，程序打开的文件IO资源不属于内存里的资源，垃圾回收机制无法回收该资源。Java7改写了所有的IO资源类，它们都实现了AutoCloseable接口，因此都可以通过自动关闭资源的try语句来关闭这些IO流。

- 一个简单的例子-FileReader：读取源文件本身

``` java
import java.io.*;

public class FileReaderInput{
    public static void main(String[] args) throws IOException{
        try{
        // 创建字符输入流
        FileReader fre = new FileReader("FileReaderInput.java");
        // 创建一个长度为32的缓冲
        char[] buf = new char[32];
        // 用于保存实际读取的字符数
        int hasRead = 0;
        // 使用循环来重复读取
        while ((hasRead = fre.read(buf)) > 0){
            // 读取字符，将字符数组转换成字符串输入
            System.out.print(new String(buf,0,hasRead));
        }
        }
        catch (IOException ex){
        ex.printStackTrace();
    }
    }
}
```

- 输入流移动记录指针

    - void mark(int readAheadLimit):在记录指针当前位置记录一个标记(mark)
    - boolean markSupported():判断此输入流是否支持mark()操作
    - void reset():将此流的记录指针重新定位到上一次记录标记(mark)的位置
    - long skip(long n):记录指针向前移动n个字节/字符

## OutputStream和Writer

### 主要方法

OutputStream和Writer也非常类似，两个流都提供了如下三个方法：

- void write(int c):将指定的字节/字符输出到输出流中，其中c既可以表示字节，也可以表示字符
- void write(byte[]/char[] buf):将字节数组/字符数组中的数据输出到指定输出流中
- void write(byte[]/char[] buf,int off,int len):将字节数组/字符数组从off位置开始，长度为len的字节/字符输出到输出流中

因为字符流直接以字符作为操作单位，所以Writer可以用字符串来代替字符数组，即以String对象作为参数，Writer里还包含如下两个方法：

- void write(String str):将str字符串里包含的字符输出到指定输出流中
- void write(String str,int off,int len):将str字符串里从off位置开始，长度为len的字符输出到指定输出流中

### 例子

从FileInputStream输入，使用FileOutputStream输出：

``` java
import java.io.*;

public class FileInputOutput{
    public static void main(String[] args) throws IOException{
        try{
        // 创建字节输入流
        FileInputStream fin = new FileInputStream("FileInputOutput.java");
        // 创建字符输出流
        FileOutputStream fout = new FileOutputStream("FileInputOutput-new.txt");
        // 创建一个长度为32的缓冲
        byte[] buf = new byte[32];
        // 用于保存实际读取的字符数
        int hasRead = 0;
        // 使用循环来重复读取
        while ((hasRead = fin.read(buf)) > 0){
            // 读取字符，将字符数组转换成字符串输入
            fout.write(buf,0,hasRead);
        }
        }
        catch (IOException ex){
        ex.printStackTrace();
    }
    }
}
```

## 处理流

使用处理流的典型思路是，使用处理流来包装节点流，程序通过处理流来执行输入/输出功能，让节点流与底层的IO设备交互。

例如，使用PrintStream处理流来包装OutputStream，使用处理流后在输出时更加方便：

``` java
import java.io.*;

public class PrintStreamTest{
    public static void main(String[] args){
        try(
            FileOutputStream fout = new FileOutputStream("test.txt");
            PrintStream ps = new PrintStream(fout)){
            // 使用PrintStream执行输出
            ps.println("普通字符串");
            // 直接使用PrintStream输出对象
            ps.println(new PrintStreamTest());
            }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
```

## 输入/输出流体系

Java的输入/输出流体系提供了将近40个类，这些类的常用分类如下：


|分类|字节输入流|字节输出流|字符输入流|字符输出流|
|:----| :----|:---- | :----| :---- |
| 节点流抽象基类| InputStream| OutputStream|Reader|Writer|
| 访问文件| FileInputStream | FileOutputStream | FileReader | FileWriter|
| 访问数组| ByteArrayInputStream | ByteArrayOutputStream | CharArrayReader |CharArrayWriter |
| 访问管道| PipedInputStream | PipedOutputStream | PipedReader | PipedWriter|
| 访问字符串| | | StringReader | StringWriter |
| 缓冲流|BufferedInputStream|BufferedOutputStream|BufferedReader|BufferedWriter|
| 转换流| | | InputStreamReader | OutputStreamWriter|
| 对象流|ObjectInputStream | ObjectOutputStream| | |
| 处理流抽象基类|FilterInputStream|FilterOutputStream|FilterReader|FilterWriter|
| 打印流| | PrintStream | | PrintWriter |
| 推回输入流|PushbackInputStream | | PushbackReader | |
| 特殊流 | DataInputStream | DataOutputStream | | |

## 转换流

转换流实现将字节流转换为字符流，其中InputStreamReader将字节输入流转换成字符输入流，OutputStreamWriter将字节输出流转换成字符输出流。

例子：Java使用System.in代表标准输入，即键盘输入，但这个标准输入流是InputStream类的实例，考虑到键盘输入内容都是文本内容，所以可以使用InputStreamReader将其转换为字符输入流，将普通的Reader包装成BufferedReader，利用BufferedReader的readLine()方法可以一次读取一行内容。

``` java
import java.io.*;

public class KeyIn{
    public static void main(String[] args){
        try(
            // 将System.in对象转换为Reader对象
            InputStreamReader reader = new InputStreamReader(System.in);
            // 将普通的Reader包装成BufferedReader
            BufferedReader br = new BufferedReader(reader)){
            String line = null;
            // 采用循环的方式逐行读取
            while ((line = br.readLine()) !=null){
                // 如果读取的是“exit”，则程序退出
                if (line.equals("exit")){
                    System.exit(1);
                }
                // 打印读取的内容
                System.out.println("输入内容为："+line);
            }
            }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }
}

```

## 推回输入流

在输入/输出流体系中，有两个流与众不同，就是PushbackInputStream和PushbackReader，它们都提供了如下三个方法：

- void unread(byte[]/char[] buf)：将一个字节/字符数组内容推回到推回缓冲区里面，从而允许重复读取刚刚读取的内容。
- void unread(byte[]/char[] buf , int off ,int len):将一个字节/字符数组里从off开始，长度为len字节/字符的内容推回到推回缓冲区里，从而允许重复读取刚刚读取的内容。
- void unread(int b):将一个字节/字符内容推回到推回缓冲区里面，从而允许重复读取刚刚读取的内容。

当程序差创建一个推回输入流时，需要指定推回缓冲区的大小，默认的推回缓冲区大小是1，如果程序中的退回到退回缓冲区的内容超过了退回缓冲区的大小，将会引发Pushback buffer overflow的IOException异常。

例子：程序试图找出程序中的"new PushbackReader"字符串，当找到该字符串时，程序只打印目标字符串之前的内容：

``` java
import java.io.*;

public class PushBackStream{
    public static void main(String[] args){
        try(
            // 创键一个PushbackReader对象，指定推回缓冲区的大小为64
            PushbackReader pr = new PushbackReader(new FileReader("PushBackStream.java"),64)){
            char[] buf = new char[32];
            // 用以保存上次读取的字符串内容
            String lastContent = "";
            int hasRead = 0;
            while ((hasRead = pr.read(buf)) >0){
                String content = new String(buf,0,hasRead);
                int targetIndex = 0;
                // 将上次读取的字符串和本次读取的字符串拼接起来
                // 查看是否包含目标字符串，如果包含目标字符串
                if ((targetIndex = (lastContent + content).indexOf("new PushbackReader")) > 0){
                    // 将本次内容和上次内容一起推回缓冲区
                    pr.unread((lastContent+content).toCharArray());
                    // 重新定义一个长度为targetIndex的char数组
                    if (targetIndex > 32) buf = new char[targetIndex];

                    //再次读取指定长度的内容(就是目标字符串之前的内容)
                    pr.read(buf, 0 ,targetIndex);
                    //打印读取的内容
                    System.out.println(new String(buf , 0 ,targetIndex));
                    System.exit(0);
                }
                else{
                    // 打印上次读取的内容
                    System.out.println(lastContent);
                    // 将本次内容设置为上次内容
                    lastContent = content;
                }
            }
            }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
```