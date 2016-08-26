---
title: Java中的序列化
comments: true
toc: true
date: 2016-08-07 18:25:02
categories: Programming
tags : Java
keywords: Java，序列化
---

>**本节内容：**对象序列化的目标是将对象保存在磁盘中，或允许网络直接传输对象。对象序列化的机制允许把内存中的java对象转换成平台无关的二进制流，其他程序一旦获取了这种二进制流，则就可以将其恢复成原来的Java对象。

<!-- more -->

## 序列化的含义和意义

序列化机制允许将实现序列化的Java对象转换成字节序列，这些字节序列可以保存在磁盘上，或者通过网络传输。

对象的序列化(Serialize)指将一个Java对象写入IO流，于此对应的是，对象的反序列化(Deserialize)则是指从IO流中恢复该Java对象。

为了让某个对象支持序列化机制，该类必须实现如下两个接口之一：

- Serializable
- Externalizable

Java的很多类已经实现了Serializable接口，该接口是一个标记接口，实现该接口无须实现任何方法，它只是表明该类的实例是可序列化的。

所有可能在网络上传输的对象的类、所有需要保存到磁盘里的对象的类都必须可序列化。

## 使用对象流实现序列化

一旦某个类实现了Serializable接口，该类的对象就是可序列化的：

- 创建一个ObjectOutputStream，这个输出流是一个处理流，所以必须建立在其他节点流的基础之上：`ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("object.txt"))`;
- 调用ObjectOutputStream对象的writeObject()方法输出可序列化对象：`oos.writeObject(per)`;

如果希望从二进制流中恢复Java对象，则需要使用反序列化：

- 创建一个ObjectInputStream输入流，必须建立在其他节点流的基础之上：`ObjectInputStream ois = new ObjectInputStream(new FileInputStream("object.txt"));`
- 调用ObjectInputStream对象的readObject()方法读取流中的对象，该方法返回一个Object类型的java对象，如果知道该对象的类型，则可以将该对象强制转换成其真实的类型：`Person p = (Person)ois.readObject();`

反序列化读取的仅仅是Java对象的数据，而不是Java类，因此采用反序列化恢复Java对象时，必须提供该Java对象所属类的class文件，否则引发ClassNotFoundException异常。

## 对象引用的序列化

如果某个类的成员变量的类型不是基本类型或String类型，而是另一个引用类型，那么这个引用类型必须是可序列化的，否则拥有该类型的成员变量的类也是不可序列化的。

为了防止引用对象不一致的情况，Java序列化机制采用了一种特殊的序列化算法：

- 所有保存到磁盘中的对象都有一个序列化编号。
- 当程序试图序列化一个对象时，程序将先检查该对象是否已经被序列化过，只有该对象从未(在本次虚拟机中)被序列化过，系统才会将该对象转换成字节序列并输出。
- 如果某个对象已经序列化过，程序将只是直接输出一个序列化编号，而不是再次重新序列化该对象。

当然，这种机制也有一种潜在的问题：当程序序列化一个可变对象时，只有第一次使用writeObject()方法输出时才会将该对象转换成字节序列输出，当后面该对象的实例变量值改变后，当程序再次调用writeObject()方法时，也只是输出前面的序列化编号。

## 自定义序列化1

在一些特殊的场景下，如果一个类里面包含的某些实例变量是敏感信息，这时不希望将该实例变量进行序列化，或者某个实力变量的类型是不可序列化的，因此不希望对该变量进行递归序列化，以免引发java.io.NotSerializableException异常。

通过在实例变量前面使用transient关键字修饰，可以指定Java序列化时无须理会该实例变量，反序列化是该变量的值为0。方法和静态变量也不会被序列化。

使用transient关键字修饰实例变量虽然简单、方便，但该变量将被完全隔离在序列化机制之外，这样导致在反序列化恢复Java对象时无法取得该实例变量值。Java还提供了一种自定义序列化机制，可以让程序控制如何序列化各实例变量，甚至完全不序列化某些实例变量(与使用transient关键字的效果相同)。

在序列化和反序列化过程中需要特殊处理的类应该提供如下特殊签名的方法，这些特殊的方法可以实现自定义序列化：

- private void writeObject(java.io.ObjectOutputStream out) throws IOException
- private void readObject(java.io.ObjectInputStream in) throws IOException,ClassNotFoundException;
- private void readObjectNoData() throws ObjectStreamException;

当序列化流不完整时，readObjectNoData()方法可以用来正确地初始化反序列化的对象。

- 例子

Person类提供了writeObject()和readObject()两个方法，其中writeObject()方法在保存Person对象时将其name实例变量包装成StringBuffer，并将其字符序列反转后写入；在readObject()方法中处理name的策略与此对应——先将读取的数据强制转换成StringBuffer，再将其反转后赋给name实例变量。

还有一种更加彻底的自定义机制，它甚至可以在序列化对象时将该对象替换成其它对象，使用特殊方法：`writeReplace`

``` java
import java.io.*;
import java.util.*;

class Person implements Serializable{
    private String name;
    private int age;
    // 注意：此处没有提供无参数的构造方法
    public Person(String name , int age){
        System.out.println("有参数的构造器");
        this.name = name;
        this.age  = age ;
    }

    public String getName(){ return name;}

    public int getAge() {return age;}

    public void setName(String name){ this.name = name;}

    public void setAge(int age) {this.age  = age ;}

    private void writeObject(ObjectOutputStream out) throws IOException{
        out.writeObject(new StringBuffer(name).reverse());
        out.writeInt(age);
    }

    private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException{
        this.name = ((StringBuffer)in.readObject()).reverse().toString();
        this.age = in.readInt();
    }
}

class PersonReplace implements Serializable{
    private String name;
    private int age;
    // 注意：此处没有提供无参数的构造方法
    public PersonReplace(String name , int age){
        System.out.println("有参数的构造器");
        this.name = name;
        this.age  = age ;
    }

    public String getName(){ return name;}

    public int getAge() {return age;}

    public void setName(String name){ this.name = name;}

    public void setAge(int age) {this.age  = age ;}

    private Object writeReplace() throws ObjectStreamException{
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(name);
        list.add(age);
        return list;
    }

}

public class SerializePerson{
    public static void main(String[] args){
        System.out.println("==测试自定义序列化==");
        try(
            // 创建一个ObjectOutputStream输出值
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("serialize.txt"));
            // 创建一个ObjectInputStream输入流
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("serialize.txt"))){
            Person per = new Person("孙悟空",500);
            oos.writeObject(per);
            Person p = (Person)ois.readObject();
            System.out.println("姓名:"+p.getName()+",年龄:"+p.getAge());
            }
            catch (Exception ex){ex.printStackTrace();}

        System.out.println("============");

        System.out.println("==测试writeReplace序列化==");
        try(
            // 创建一个ObjectOutputStream输出值
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("serialize-replace.txt"));
            // 创建一个ObjectInputStream输入流
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("serialize-replace.txt"))){
            PersonReplace per = new PersonReplace("孙悟空",500);
            oos.writeObject(per);
            // 反序列化读取ArrayList
            ArrayList list = (ArrayList)ois.readObject();
            System.out.println("姓名:"+list.get(0)+",年龄:"+list.get(1));
            }
            catch (Exception ex){ex.printStackTrace();}
    }
}
```

与writeReplace()方法相对，序列化机制里还有一个特殊的方法，它可以实现保护性复制整个对象：`readResolve()`,这个方法会紧接着readObject()之后被调用，该方法的返回值将替代原来的反序列化的对象，而原来的readObject()反序列化的对象将会被立即丢弃。


## 自定义序列化2

Java还提供了另一种序列化机制：Externalizable，这种序列化机制完全由程序员决定存储和恢复对象数据。该接口定义了如下两个方法：

- void readExternal(ObjectInput in):需要序列化的类实现readExternal()方法来实现反序列化。该方法调用DataInput(它是ObjectInput的父接口)的方法来恢复基本类型的实例变量值，调用ObjectInput的readObject()方法来恢复引用类型的实例变量值。
- void writeExternal(ObjectOutput out):需要序列化的类实现writeExternal()方法来实现序列化。该方法调用DataOutput(它是ObjectOutput的父接口)的方法来保存基本类型的实例变量值，调用ObjectOutput的writeObject()方法来保存引用类型的实例变量值。

Externalizable的接口强制自定义序列化：

``` java
public class Person implements Externalizable{
    private String name;
    private int age;

    public Person(String name,int age){
        System.out.println("有参数的构造器");
        this.name = name;
        this.age  = age ;
    }
    ...
    public void writeExternal(ObjectOutput out) throws IOException{
        // 将name实例变量值反转后写入二进制流
        out.writeObject(new StringBuffer(name).reverse());
        out.writeInt(age);
    }

    public void readExternal(ObjectInput in) throws IOException,ClassNotFoundException{
        // 将读取的字符串反转后赋给name实例变量
        this.name = ((StringBuffer)in.readObject()).reverse().toString();
        this.age = in.readInt();
    }
}
```

如果程序需要序列化实现Externalizable接口的对象，一样调用ObjectOutputStream的writeObject()方法输出该对象即可；反序列化该对象，则调用ObjectInputStream的readObject()方法。

## 版本兼容性

Java序列化机制允许为序列化的类提供一个private static final的serialVersionUID值，该类变量的值用于标识该Java类的序列化版本，如果一个类升级后，它的serialVersionUID类变量值保持不变，序列化机制也会把它们当成同一个序列化版本。

分配serialVersionUID:

``` java
public class Test{
    //为该类指定一个serialVersionUID类变量值
    private static final long serialVersionUID = 512L;
    ...
}
```

这样，即使在某个对象被序列化后，它所对应的类被修改了，该对象也依然可以被正确的反序列化。