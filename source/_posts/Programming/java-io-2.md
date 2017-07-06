---
title: Java中的IO流(2)
comments: true
toc: true
date: 2016-08-07 09:25:02
categories: Programming
tags : Java
keywords: Java，IO流
---

>**本节内容：**本节学习输入输出重定向，Java虚拟机读取进程数据以及随机访问文件等IO内容。

<!-- more -->

## 重定向标准输入/输出

Java的标准输入输出分别通过System.in和System.out代表，在默认情况下它们分表代表键盘和显示器，在System类里提供了如下三个重定向标准输入输出的方法：

- static void setErr(PrintStream err): 重定向"标准"错误输出流
- static void setIn(InputStream in):重定向"标准"输入流
- static void setOut(PrintStream out):重定向"标准"输出流

简单的输出输入重定向：

``` java
// 输出重定向
PrintStream ps = new PrintStream(new FileOutputStream("out.txt"));
System.setOut(ps)

// 输入重定向
FileInputStream fin = new FileInputStream("RedirectIn.java");
System.setIn(fin)
```

## Java虚拟机读取其他进程数据

Java使用Runtime对象的exec()方法可以运行平台上的其他程序，该方法产生一个Process对象，Process对象代表由该Java程序启动的子进程，Process类提供了如下三个方法，用于让程序和其子进程进行通信：

- InputStream getErrorStream（）：获取子进程的错误流
- InputStream getInputStream():获取子进程的输入流
- OutputStream getOutputStream():获取子进程的输出流

此处的输入流和输出流是相对于Java程序而言的，如果是试图让子进程读取程序中的数据，则是输出流。

### 输入流例子：

``` java
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
public class ReadFromProcess{
    public static void main(String[] args) throws IOException{
        //运行javac命令，返回运行该命令的子进程
        Process p = Runtime.getRuntime().exec("javac");
        try(// 以p进程的错误流创建BufferedReader对象
            // 这个错误流对本程序是输入流，对p进程则是输出流
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()))){
            String buff = null;
            // 采用循环方式来读取p进程的错误输出
            while ((buff = br.readLine()) != null) System.out.println(buff);
            }
    }
}
```

结果将输出javac命令的提示。

### 输出流例子

``` java
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.util.Scanner;
import java.io.IOException;

public class WriteToProcess{
    public static void main(String[] args) throws IOException{
        // 运行java ReadStandard命令，返回运行该命令的子进程
        Process p = Runtime.getRuntime().exec("java ReadStandard");
        try(
            // 以进程p的输出流创建PrintStream对象
            // 这个输出流对本程序是输出流，对p进程则是输入流
            PrintStream ps = new PrintStream(p.getOutputStream()))
            {
                // 向ReadStandard程序写入内容，这些内容将被ReadStandard读取
                ps.println("普通字符串");
                ps.println(new WriteToProcess());
            }
    }
}

// 定义一个ReadStandard类，该类可以接收标准输入
// 并将标准输入写入out.txt
class ReadStandard{
    public static void main(String[] args){
        try(
            // 使用System.in创建Scanner对象，用于获取标准输入
            Scanner sc = new Scanner(System.in);
            PrintStream ps = new PrintStream(
            new FileOutputStream("out.txt"))){
            // 增加下面一行只把回车作为分隔符
             sc.useDelimiter("\n");
            // 判断是否还有下一个输入项
            while(sc.hasNext()) ps.println("键盘的输入内容是："+sc.next());
            }
            catch (IOException ex){
                ex.printStackTrace();
        }
    }
}

```

由WriteToProcess类运行ReadStandard类。

## RandomAccessFile

RandomAccessFile提供了众多的方法来访问文件内容，它既可以读取文件内容，也可以向文件中输出内容，并且支持“随机访问”的方式，程序可以直接跳转到任意位置读写数据。如果程序需要向已存在的文件后面追加内容，则应该使用RandomAccessFile。

RandomAccessFile包含了如下两个方法来操作文件记录指针：

- long getFilePointer():返回文件记录指针的当前位置
- void seek(long pos):将文件记录指针定位到pos位置

RandomAccessFile的构造器第一个参数是文件名，第二个参数是指定RandomAccessFile的访问模式：

- "r":以只读的方式打开指定文件，如果尝试写入，则抛出IOException异常。
- "rw":以读、写方式打开指定文件，如果文件尚不存在，则尝试创建该文件。
- "rws":以读、写方式打开指定文件，相对于"rw"模式，还要求对文件的内容或元数据的每个更新都同步写入到底层存储设备。
- "rwd":以读、写方式打开指定文件，相对于"rw"模式，还要求对文件内容的每个更新都同步写入到底层存储设备。

### 例子：插入内容

``` java
import java.io.*;

public class InsertContent{
    public static void insert(String fileName, long pos , String insertContent) throws IOException{
        File tmp = File.createTempFile("tmp",null);
        tmp.deleteOnExit();
        try(
            RandomAccessFile raf = new RandomAccessFile(fileName,"rw");
            // 使用临时文件来保存插入点后的数据
            FileOutputStream tmpOut = new FileOutputStream(tmp);
            FileInputStream tmpIn = new FileInputStream(tmp)){
            raf.seek(pos);

            // 下面的代码将插入点后的内容读入临时文件中保存
            byte[] bbuf = new byte[64];
            // 用于保存实际读取的字节数
            int hasRead =0;
            while((hasRead = raf.read(bbuf)) > 0) tmpOut.write(bbuf,0,hasRead);

            // 下面代码用于插入内容
            //把文件记录指针重新定位到pos位置
            raf.seek(pos);
            // 追加需要插入的内容
            raf.write(insertContent.getBytes());
            //追加临时文件中的内容
            while ((hasRead = tmpIn.read(bbuf)) > 0) raf.write(bbuf,0,hasRead);
        }
    }

    public static void main(String[] args) throws IOException{
        insert("InsertContent.java",50,"\n/*新插入的内容!*/\n");
    }
}
```
上面程序中使用File的CreateTempFile(String prefix,String suffix)方法创建一个临时文件，用以保存被插入文件的插入点后面的内容。
