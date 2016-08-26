---
title: Java中的注解(1)
comments: true
toc: true
date: 2016-08-01 11:25:02
categories: Programming
tags : Java
keywords: Java，注解
---

>**本节内容：**从JDK1.5开始，Java增加了对元数据(MetaData)的支持，也就是Annotation。Annotation是代码里的特殊标记，这些标记可以在编译，类加载，运行时被读取，并进行相应的处理。通过使用注解，程序开发人员可以在不改变原有逻辑的情况下，在源文件中嵌入一些补充信息。


<!-- more -->

## 注解

Annotation提供了一种为程序元素设置元数据的方法，从某些方面来看，Annotation就像修饰符一样，可用于修饰包，类，构造器，方法，成员变量，参数，局部变量的声明，这些信息被存储在Annotation的"name=value"中。Annotation不影响程序代码的执行，无论增加或删除Annotation，代码都将始终如一的执行。若希望让程序中的Annotation在运行时起一定的作用，只有通过某种配套的工具对Annotation中的信息进行访问和处理，这种工具统称为APT(Annotation Processing Tool)。

Java中有5个基本Annotation，它们都定义在`java.lang`包下。使用Annotation时需要在其前面增加@符号，并把该Annotation当做一个修饰符使用。

- @Override
- @Deprecated
- @SupressWarnings
- @SafeVarargs (Since Java7)
- @FunctionalInterface (Since Java8)


## 限定重写父类方法：@Override

@Override用来指定方法重载的，它可以强制一个子类必须覆盖父类的方法。@Override的作用是告诉编译器来检查这个方法，保证父类中的某个方法必须被重写，这样可以在编译阶段发现错误。

例子：

``` java
pulic class Fruit{

public void info(){
    System.out.println("水果的info方法...")；
}
}

class Apple extends Fruit{
    //使用@Override指定下面方法必须重写父类方法
    @Override
    public void info(){
    System.out.println("苹果重写水果的Info方法...");
}
}
```

## 标记已过时：@Deprecated

@Deprecated用来表示某个程序元素(类，方法等)已过时，当其他程序使用已过时的类，方法时，编译器将给出警告。

``` java
pulic class Apple{

//定义info方法已过时
@Deprecated
public void info(){
    System.out.println("苹果的info方法...")；
}
}

class AppleTest{
    public static void main(String[] args){
    //编译器将给出警告
    new Apple().info();
}

}
```

@Deprecated的作用与文档注释中的@deprecated标记的作用基本相同，但它们的用法不同，前者是Java5才支持的注解，无需放到文档的注释部分，而是直接放到要修饰的方法，类，接口等。

## 抑制编译器警告：@SuppressWarnings

@SuppressWarnings指示被该Annotation修饰的程序元素(以及该程序元素中的所有子元素)取消显示指定的编译器警告。在通常情况下，如果程序中没有限定泛型限制的集合将会引起编译器警告，为了避免这种编译器警告，可以使用@SuppressWarning修饰。例如，下面的程序取消了没有使用泛型的编译器警告：

``` java
// 关闭整个类中的编译器警告
@SuppressWarnings(value="unchecked")
public class SuppressWarningTest{
    public static void main(String[] args){
    List<String> mylist = new ArrayList();
}

}
```

## Java7中的”堆污染“警告与@SafeVarargs

在泛型擦除时，如下代码可能导致运行时异常：

``` java
List list = new ArrayList<Integer>();
list.add(20);  //添加元素时引发unchecked异常
// 下面的代码引起”未经检查的转换“的警告，编译，运行完全正常
List<String> ls = list;
// 但只要访问ls里的元素，如下面的代码就会引起运行时的异常
System.out.println(ls.get(0));
```

Java把引发这种错误的原因称为"堆污染"(heap pollution),当把一个不带泛型的对象赋值给一个带泛型的变量时，往往就会发生这种堆污染现象。

对于形参个数可变的方法，该形参的类型又是泛型，将更容易导致”堆污染“，例如如下的工具类：

``` java
public class ErrorUtils{
    public static void faultyMethod(List<String>... listStrArray){
    //Java语言不允许创建泛型数组，因此listArray只能被当成List[]处理
    //此时相当于把List<String>赋给了List，已经发生了”堆污染“

    List[] listArray = listStrArray;
    List<Integer> myList = new ArrayList<Integer>();
    myList.add(new Random().next(100));
    //把listArray的第一个元素赋为myArray
    listArray[0] = myList;
    String s = listArray[0].get[0];
}
}
```
在Java6以及更早的版本中，Java编译器认为faultyMethod()方法没有任何问题，等到使用该方法时：

``` java
public class ErrorUtilsTest{
    publc static void main(String[] args){
    ErrorUtils.faultyMethod(Arrays.asList("Hello"),Arrays)
}
}
```
会引发一个unchecked警告，在运行时会引发ClassCastException异常。

从Java7开始，Java编译器进行更加严格的检查，在编译ErrorUtils时就会发出一个如下的警告：

``` java
ErrorUtils.java:15:警告[unchecked] 参数化 varargs类型List<String>的堆可能已经受到污染
```

但有些时候，开发者不希望看到这个警告，则可以使用如下三种方式来抑制这个警告：

- 使用@SafeVarargs修饰引发该警告的方法或构造器。
- 使用@SuppressWarnings("unchecked")修饰。
- 编译时使用-Xlint:varargs选项。

通常使用@SafeVarargs修饰，编译上述两个程序时都不会发出任何警告。

## Java8的函数式接口与@FunctionalInterface

Java8规定：如果接口中只有一个抽象方法（可以包含多个默认方法或多个static方法），该接口就是函数式接口。@FunctionalInterface就是用来指定某个接口必须是函数式接口。

``` java
@FunctionalInterface
public interface FunctionalInterface{
    static void foo(){
    System.out.println("foo类方法");
}
default void bar(){
    System.out.println("bar默认方法");
}
void test();  //只定义一个抽象方法
}
```

@FunctionalInterface告诉编译器检查这个接口，保证该接口只能包含一个抽象方法，否则就编译出错。如果再增加一个抽象方法abc()，则会编译出错：意外的@FunctionalInterface注释。




