---
title: Java中的注解(2)
comments: true
toc: true
date: 2016-08-02 08:25:02
categories: Programming
tags : Java
keywords: Java，注解
---

>**本节内容：**JDK除了在java.lang下提供了5个基本的Annotation之外，还在java.lang.annotation包下提供了6个Meta Annotation，其中有五个元Annotation都用于修饰其他Annotation定义。


<!-- more -->

## 使用@Retention

@Retention只能用于修饰Annotation定义，用于指定被修饰的Annotation可以保留多长时间，@Retention包含一个RetentionPolicy类型的value成员变量，所以使用@Retention时必须为该value成员变量指定值。

value成员变量的值只能是如下三个：

- RetentionPolicy.CLASS ：编译器将把Annotation记录在class文件中。当运行Java程序时，JVM不可获取Annotation信息，这是默认值。
- RetentionPolicy.RUNTIME :编译器将把Annotation记录在class文件中，当运行Java程序时，JVM也可以获取Annotation信息，程序通过反射获取该Annotation信息。
- RetentionPolicy.SOURCE ： Annotation只保留在源代码中，编译器将直接丢弃这种Annotation。

``` java
// 定义下面的Testable Annotation保留到运行时
@Retention(
value=RetentionPolicy.RUNTIME)
public @interface Testable{}
```

如果使用注解时只需要为value成员变量指定值，则使用该注解时可以直接在该注释后面的括号里指定value成员变量的值，无需使用“value=变量值”的形式。因此，上面的例子中可省略`value=`。

## 使用@Target

@Target也只能修饰一个Annotation定义，它用于指定被修饰的Annotation能用于修饰哪些程序单元。@Target元Annotation也包含一个value的成员变量，该成员变量的值只能是如下几个：

- ElementType.ANNOTATION_TYPE:指定该策略的Annotation只能修饰Annotation。
- ElementType.CONSTRUCTOR:指定该策略的Annotation只能修饰构造器。
- ElementType.FIELD: 指定该策略的Annotation只能修饰成员变量。
- ElementType.LOCAL_VARIABLE:指定该策略的Annotation只能修饰局部变量。
- ElementType.METHOD:指定该策略的Annotation只能修饰方法定义。
- ElementType.PACKAGE:指定该策略的Annotation只能修饰包定义。
- ElementType.PARAMETER:指定该策略的Annotation可以修饰参数。
- ElementType.TYPE:指定该策略的Annotation可以修饰类，接口(包括注解类型)或枚举定义。

与使用@Retention类似，使用@Target也可以直接在括号里指定value的值，而无需使用name=value的形式。

``` java
@Target(ElementType.FIELD)
public @interface ActionListenerFor{}
```

## 使用@Documented

@Documented用于指定被该元Annotation修饰的Annotation类将被javadoc等工具提取成文档，如果定义Annotation类时使用了@Documented修饰，则所有使用该Annotation修饰的程序元素的API文档中将会包含该Annotation说明。

下面的代码定义了一个Testable Annotation，程序使用@Documented来修饰@Testable Annotation定义，所以该Annotation将被javadoc工具所提取：

``` java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
// 定义Testable Annotation将被javadoc工具提取
@Documented
public @interface Testable{

}
```

上面代码中指定了javadoc工具生成的API文档将提取@Testable的使用信息。下面的代码定义了一个MyTest类，该类中的info()方法使用了@Testable修饰。

``` java
public class MyTest{
    // 使用@testable修饰info()方法
    @Testable
    public void info(){
    System.out.println("info方法...");
}
}
```

## 使用@Inherited

@Inherited元Annotation指定被它修饰的Annotation将具有继承性——如果某个类使用了@Xxx注解(定义该Annotation时使用了@Inherited修饰)修饰，则其子类将自动被@Xxx修饰。

下面使用@Inherited元Annotation修饰@Inherited定义，则该Annotation将具有继承性：

``` java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Inheritable{

}
```

上面的程序代码表明@Inheritable具有继承性，如果某个类使用了@Inheritable修饰，则该类的子类将自动使用@Inheritable修饰。

下面程序中定义了一个Base基类，该基类使用了@Inheritable修饰，则Base类的子类将会默认使用@Inheritable修饰：

``` java
// 使用@Inheritable修饰的Base类
@Inheritable
class Base{

}

// InheritableTest类只是继承了Base类
// 并未直接使用@Inheritable Annotation修饰
public class InheritableTest extends Base {
    public static void main(String[] args){
    // 打印InheritableTest类是否具有@Inheritable修饰
    System.out.prinln(InheritableTest.class.isAnnotationPresent(Inheritable.class));
}
}
```

上面的程序会输出：true。

## 自定义Annotation

定义新的Annotation类型使用@interface关键字，定义一个新的Annotation类型与定义一个接口非常像：

``` java
// 定义一个简单的Annotation类型
public @interface Test{

}
```

定义了该Annotation后，就可以在程序的任何地方使用该Annotation，使用Annotation的语法非常类似于public，final这样的修饰符，通常可以用来修饰程序中的类，方法，变量，接口等定义。

``` java
// 使用Test修饰类的定义
@Test
public class MyClass{

}
```

在默认情况下，Annotation可用于修饰任何程序元素，包括类，接口，方法等，如下程序使用@Test来修饰方法：

``` java
public class MyClass{
    // 使用@Test Annotation修饰方法
    @Test
    public void info(){
    ...
}
}
```

Annotation还可以带成员变量：

``` java
public @interface MyTag{
    // 定义带两个成员变量的Annotation
    // Annotation中的成员变量以方法的形式定义
    String name()；
    int age();
}
```

一旦在Annotation中定义了成员变量后，使用该Annotation时就应该为该Annotation的成员变量指定值，如下面的代码所示：

``` java
public class Test{
    // 使用带成员变量的Annotation时，需要为成员变量赋值
    @MyTag(name="xx",age=6)
    public void info(){
    ...
}
}
```

也可以在定义Annotation的成员变量时为其指定初始值（默认值），可以使用default关键字：

``` java
public @interface MyTag{
    // 定义带两个成员变量的Annotation
    // Annotation中的成员变量以方法的形式定义
    // 使用default为两个成员变量指定初始值
    String name() default "Mike"；
    int age() default 24;
}
```

如果为Annotation的成员变量指定了默认值，使用Annotation时则可以不为这些变量指定值，而是直接使用默认值。

通常把没有定义成员变量的Annotation类型称为标记，把包含成员变量的Annotation称为元数据Annotation。

## 提取Annotation信息

使用了Annotation修饰了类，方法，成员变量后，这些Annotation不会自己生效，必须由开发者提供相应的工具来提取并处理Annotation信息。

java.lang.reflect包下主要包含了一些实现反射功能的工具类，从JDK1.5开始，java.lang.reflect包所提供的反射API增加了读取运行时的Annotation能力，只有当定义Annotation时使用了@Retention(RetentionPolicy.RUNTIME)修饰，该Annotation才会在运行时可见，JVM才会在装载class文件是读取保存在class文件中Annotation。

Java 5 在java.lang.reflect包下新增了AnnotatedElement接口，该接口代表程序中可以接受注解的程序元素，主要包括Class，Constructor，Field，Method，Package等。AnnotatedElement接口是所有程序元素的父接口，所以程序通过反射获取了某几个类的AnnotatedElement对象后，程序就可以调用该对象的如下几个方法来访问Annotation信息：

- <A extends Annotation> A getAnnotation(Class<A> annotationClass):返回该程序元素上存在的，指定类型的注解，若不存在，返回null。
- <A extends Annotation> A getDeclaredAnnotation(Class<A> annotationClass):这是Java8新增的方法，该方法尝试获取直接修饰该程序元素，指定类型的Annotation，若不存在，返回null。
- Annotation[] getAnnotations():返回该程序元素上存在的所有注解。
- Annotation[] getDeclaredAnnotations():返回直接修饰该程序元素的所有Annotation。
- boolean isAnnotationPresent(Class<? extends Annotation> annotationClass):判断该程序元素上是否存在指定类型的注解。
- <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationClass):获取修饰该程序元素，指定类型的多个Annotation。
- <A extends Annotation> A[] getDeclaredAnnotationsByType(Class<A> annotationClass):获取直接修饰该程序元素，指定类型的多个Annotation。

Java8中可以在如下位置上使用Type Annotation“

- 创建对象(使用new关键字)
- 类型转换
- 使用implements实现接口
- 使用throws声明抛出异常

``` java
//获取Test类的info方法的所有注解
Annotation[] aArray =Class.forName("Test").getMethod("info").getAnnotations();
for ( Annotation an : aArray){
    System.out.println(an);
}
```

## Java8新增的Type Annotation

Java8为ElementType枚举增加了TYPE_PARAMETER,TYPE_USE两个枚举值，这样就允许定义枚举时使用@Target(ElementType.TYPE_USE)修饰，这种注解被称为Type Annotation(类型注解),Type Annotation可用在任何用到类型的地方。

``` java
// 定义一个简单的Type Annotation,不带任何成员变量
@Target(ElementType.TYPE_USE)
@interface NotNull{}
//定义类时使用Type Annotation
@NotNull
public class TypeAnnotationTest implements @NotNull /* implements时使用Type Annotation */ Serializable{
    //方法形参中使用Type Annotation
    public static void main(@NotNull String[] args)
    // throws时使用Type Annotation
    throws @NotNull FileNotFoundException{
    Object obj ="lovejava.org";
    //强制类型转换时使用Type Annotation
    String str = (@NotNull String)obj;
    //创建对象时使用Type Annotation
    Object win = new @NotNull JFrame("疯狂软件");
}
// 泛型中使用Type Annotation
public  void foo(List<@NotNull String > info) {}
}
```


