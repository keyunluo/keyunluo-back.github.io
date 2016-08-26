---
title: Java中的NIO2
comments: true
toc: true
date: 2016-08-08 09:25:02
categories: Programming
tags : Java
keywords: Java，NIO2
---

>**本节内容：**Java7对原有的NIO进行了重大改进，提供了全面的文件IO和文件系统的访问支持，基于异步Channel的IO。

<!-- more -->

## Path、Paths和Files核心API

早起的JAVA只提供了一个File类来访问文件系统，功能有限并且性能低下。NIO.2引入了一个Path接口，Path接口代表一个平台无关的平台路径，此外，NIO.2还提供了Files和Paths两个工具类，其中Files包含了大量的静态的工具方法来操作文件，Paths则包含了两个返回Path的静态工厂方法。

### Path接口的功能和用法

``` java
import java.nio.file.Path;
import java.nio.file.Paths;

public class NioPath{
    public static void main(String[] args){
        // 以当前路径来创建Path对象
        Path path = Paths.get("/home/streamer/tmp/NioPath.java");
        System.out.println("path里包含的路径数量:"+path.getNameCount());
        System.out.println("path的根路径:"+path.getRoot());
        // 获取path对应的绝对路径
        Path absolutePath = path.toAbsolutePath();
        System.out.println("path的绝对路径:"+absolutePath);
        // 获取绝对路径的根路径
        System.out.println("绝对路径的根路径:"+absolutePath.getRoot());
        // 获取绝对路径所包含的路径数量
        System.out.println("绝对路径包含的路径数量:"+absolutePath.getNameCount());
        System.out.println("绝对路径第三层路径:"+absolutePath.getName(3));
        // 以多个String来构建Path对象
        Path path2= Paths.get("/usr","local","anaconda3");
        System.out.println("构建的PATH:"+path2);
    }
}
```

显示的如下结果：
``` python
path里包含的路径数量:4
path的根路径:/
path的绝对路径:/home/streamer/tmp/NioPath.java
绝对路径的根路径:/
绝对路径包含的路径数量:4
绝对路径第三层路径:NioPath.java
构建的PATH:/usr/local/anaconda3

```

### Files类的用法

Files是一个操作文件的工具类，它提供了大量便捷的工具方法

``` java
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.FileStore;
import java.nio.charset.Charset;
import java.io.FileOutputStream;
import java.util.List;
import java.util.ArrayList;

public class NioFile{
    public static void main(String[] args) throws Exception{
        // 复制文件
        Files.copy(Paths.get("NioFile.java"),new FileOutputStream("NioFile.txt"));
        // 判断NioFile.java是否为隐藏文件
        System.out.println("NioFile.java是否为隐藏文件:"+Files.isHidden(Paths.get("NioFile.java")));
        // 一次性读取NioFile.java文件的所有行
        List<String> lines = Files.readAllLines(Paths.get("NioFile.java"),Charset.forName("UTF-8"));
        System.out.println(lines);
        // 判断指定文件的大小
        System.out.println("NioFile.java文件的大小："+Files.size(Paths.get("NioFile.java")));
        // 直接将多个字符串写入到指定文件中
        List<String> poem = new ArrayList<>();
        poem.add("水晶潭底银鱼跃");
        poem.add("清徐风中碧竿横");
        Files.write(Paths.get("poem.txt"),poem,Charset.forName("gbk"));

        // 使用Java8新增的Stream API列出当前目录下所有文件和子目录
        Files.list(Paths.get(".")).forEach(path -> System.out.println(path));
        // 使用Java8新增的Stream API读取文件内容
        Files.lines(Paths.get("poem.txt"),Charset.forName("gbk")).forEach(line -> System.out.println(line));

        // 判断/data的总空间、可用空间
        FileStore cStore = Files.getFileStore(Paths.get("/data/"));
        System.out.println("/data:共有空间："+cStore.getTotalSpace());
        System.out.println("/data:可用空间："+cStore.getUsableSpace());
    }
}
```

## 使用FileVisitor遍历文件和目录

Files类提供了如下两个方法来遍历文件和子目录：

- walkFileTree(Path start,FileVisitor<? super Path> visitor):遍历start目录下的所有文件和子目录
- walkFileTree(Path start,Set<FileVisitOption> options,int maxDepth,FileVisitor<? super Path> visitor):与上一个方法类似，该方法最多遍历maxDepth深度的文件

上面两个方法都需要FileVisitor参数，FileVisitor代表一个文件访问器，walkFileTree()方法会自动遍历start路径下的所有文件和子目录，遍历文件和子目录都会触发FileVisitor中的相应方法。FileVisitor中定义了如下四种方法：

- FileVisitResult postVisitDirectory(T dir,IOException exc):访问子目录之后触发该方法
- FileVisitResult preVisitDirectory(T dir,BasicFileAttributes attrs):访问子目录之前触发该方法
- FileVisitResult visitFile(T dir,BasicFileAttributes attrs):访问file文件时触发该方法
- FileVisitResult visitFileFailed(T dir,IOException exc):访问file文件失败时触发该方法

上面4个方法都返回一个FileVisitResult对象，它是一个枚举类，代表访问之后的后续行为，FileVisitResult定义了如下几种后续行为：

- CONTINUE:代表"继续访问"的后续行为
- SKIP_SIBLINGS:代表"继续访问"的后续行为,但不访问该文件或目录的兄弟文件或目录
- SKIP_SUBTREE:代表"继续访问"的后续行为,但不访问该文件或目录的子目录树
- TERMINATE:代表"终止访问"的后续行为

实际编程时没必要为FileVisitor的四个方法都提供实现，可以通过继承SimpleFileVisitor(FileVisitor的实现类)来实现自己的"文件访问器"。

``` java
import java.nio.file.Files;
import java.nio.file.FileVisitor;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;

public class NioFileVisitor{
    public static void main(String[] args) throws Exception{
        // 遍历/usr/local/anaconda3/include 目录下的所有文件和子目录
        Files.walkFileTree(Paths.get("/usr","local","anaconda3","include"), new SimpleFileVisitor<Path>(){
            // 访问文件时触发该方法
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException{
                System.out.println("正在访问："+file+"文件");
                // 找到了Python.h文件
                if (file.endsWith("Python.h")){
                    System.out.println("--已经找到目标文件--");
                    return FileVisitResult.TERMINATE;
                }
                return FileVisitResult.CONTINUE;
            }

            // 开始访问目录时触发该方法
            @Override
            public FileVisitResult preVisitDirectory(Path dir,BasicFileAttributes attrs) throws IOException{
                System.out.println("正在访问:"+dir+"路径");
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
```

## 使用WatchService监控文件变化

NIO.2的Path类提供了如下方法来监听文件系统的变化：

- register(WatchService watcher,WatchEvent.Kind<?> ... events):用watcher监听该path代表的目录下的文件变化，events参数指定要监听哪些类型的事件。

一旦使用register()方法完成注册后，接下来就可以调用WatchService的如下三个方法来获取被监听目录的文件变化事件。

- WatchKey poll():获取下一个WatchKey，如果没有WatchKey发生就立即返回null
- WatchKey poll(long timeout,TimeUnit unit):尝试等待timeout时间去获取下一个WatchKey
- WatchKey take():获取下一个WatchKey，如果没有WatchKey发生就一直等待

如果程序需要一直监控，则应该选择使用take()方法；如果程序只需要监控指定时间，择可以考虑使用poll方法，下面的程序使用WatchService来监控/data根目录下文件的变化：

``` java
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.FileSystems;

public class NioWatchService{
    public static void main(String[] args) throws Exception{
        // 获取文件系统的WatchService对象
        WatchService watchService = FileSystems.getDefault().newWatchService();
        // 为/data根路径注册监听
        Paths.get("/data/").register(watchService
                , StandardWatchEventKinds.ENTRY_CREATE
                , StandardWatchEventKinds.ENTRY_MODIFY
                , StandardWatchEventKinds.ENTRY_DELETE);
        while(true){
            // 获取下一个文件变化事件
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()){
                System.out.println(event.context()+ " 文件发生了 "+ event.kind() +"事件!");
            }
            // 重设WatchKey
            boolean valid = key.reset();
            // 如果重设失败，退出监听
            if (!valid) break;
        }
    }
}

```
事件:
``` python
➜  data touch test.txt
➜  data echo "test" >> test.txt
➜  data rm test.txt
```
结果：
``` python
test.txt 文件发生了 ENTRY_CREATE事件!
test.txt 文件发生了 ENTRY_MODIFY事件!
test.txt 文件发生了 ENTRY_MODIFY事件!
test.txt 文件发生了 ENTRY_DELETE事件!
```
## 访问文件属性

Java7的NIO.2在java.nio.file.attribute包下提供了大量的工具类，通过这些工具类，开发者可以非常简单地读取、修改文件属性，这些工具类主要分为如下两类：

- XxxAttributeView：代表某种文件属性的视图
- XxxAttributes:代表某种文件属性的集合，程序一般通过XxxAttributeView对象来获取XxxAttributes

在这些工具类中，FileAttributeView是其他XxxAttributeView的父接口

|View|功能|
|:----| :----|
|AclFileAttributeView|开发者可以为特定文件设置ACL(Access Control List) 及文件所有者属性，它的getAcl()方法返回List<AclEntry>对象，该返回值代表了该文件的权限集，通过setAcl(List)方法可以修改该文件的ACL|
|BasicFileAttributeView|它可以获取或修改文件的基本属性，包括文件的最后修改时间、最后访问时间、创建时间、大小、是否为目录、是否为符号链接等，它的readAttribute方法返回一个BasicFileAttribute对象，对文件夹基本属性的修改是通过BasicFileAttribute对象完成的。|
|DosFileAttributeView|主要用于获取或修改文件DOS相关属性，如文件是否只读、隐藏，是否为系统文件、存档文件等|
|FileOwnerAttributeView|获取或修改文件的所有者|
|PosixFileAttributeView|用于获取或修改POSIX属性，它的readAttribute()方法返回一个PosixFileAttribute对象，该对象可用于获取或修改文件的所有者、组所有者和访问权限信息，仅在*nx系统上有用|
|UserDefinedFileAttributeView|让开发者为文件设置一些自定义属性|

下面程序示范了如何读取、修改文件的属性

``` java
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.FileSystems;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Date;

public class NioAttributeView{
    public static void main(String[] args) throws Exception{
        // 获取将要操作的文件
        Path path = Paths.get("NioAttributeView.java");

        // 获取访问基本属性的BasicFileAttributeView
        BasicFileAttributeView basicView = Files.getFileAttributeView(path,BasicFileAttributeView.class);
        // 获取访问基本属性的BasicFileAttributes
        BasicFileAttributes basicAttr = basicView.readAttributes();
        // 访问文件的基本属性
        System.out.println("创建时间："+ new Date(basicAttr.creationTime().toMillis()));
        System.out.println("最后访问时间："+ new Date(basicAttr.lastAccessTime().toMillis()));
        System.out.println("最后修改时间："+ new Date(basicAttr.lastModifiedTime().toMillis()));
        System.out.println("文件大小："+ basicAttr.size());

        // 获取访问文件属主属性的FileOwnerAttributeView
        FileOwnerAttributeView ownerView = Files.getFileAttributeView(path,FileOwnerAttributeView.class);
        // 获取该文件所属用户
        System.out.println("所有者："+ ownerView.getOwner());
        // 获取系统中streamer对应的用户
        UserPrincipal user = FileSystems.getDefault().getUserPrincipalLookupService().lookupPrincipalByName("streamer");
        // 修改用户
        ownerView.setOwner(user);

        // 获取访问自定义属性的FileOwnerAttributeView
        UserDefinedFileAttributeView userView = Files.getFileAttributeView(path,UserDefinedFileAttributeView.class);
        // 添加一个自定义属性
        userView.write("发行版",Charset.defaultCharset().encode("streamer"));
        userView.write("编辑器",Charset.defaultCharset().encode("vim/gedit"));
        List<String> attrNames = userView.list();
        // 遍历所有的自定义属性
        for (String name : attrNames){
        ByteBuffer buf = ByteBuffer.allocate(userView.size(name));
        userView.read(name,buf);
        buf.flip();
        String value = Charset.defaultCharset().decode(buf).toString();
        System.out.println(name + "----->" + value);
        }

        // 获取访问Posix属性的PosixFileAttributeView
        PosixFileAttributeView posixView = Files.getFileAttributeView(path,PosixFileAttributeView.class);
        // 获取访问基本属性的PosixFileAttributes
        PosixFileAttributes posixAttr = posixView.readAttributes();
        // 访问文件的基本属性
        System.out.println("创建者："+ posixAttr.owner().toString());
    }
}

```

结果如下：
``` python
➜  tmp sudo chown root:root NioAttributeView.java
➜  tmp sudo chmod 777 NioAttributeView.java
➜  tmp javac NioAttributeView.java
➜  tmp sudo java NioAttributeView
创建时间：Mon Aug 08 18:14:40 UTC 2016
最后访问时间：Mon Aug 08 18:15:04 UTC 2016
最后修改时间：Mon Aug 08 18:14:40 UTC 2016
文件大小：3211
所有者：root
发行版----->streamer
编辑器----->vim/gedit
创建者：streamer

```