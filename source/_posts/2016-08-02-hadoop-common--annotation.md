---
title: Hadoop源码学习(2)——Hadoop-Common-Annotations
comments: true
toc: true
date: 2016-08-02 17:25:02
categories: SourceCodeLearning
tags : Java
keywords: Java，注解
---

>**本节内容：**本节学习Hadoop-Common-Project中的hadoop-annotation模块，该模块主要实现了hadoop项目中的注解。


<!-- more -->

## hadoop-annotations 源代码结构

``` txt
src
└── main
    ├── java
    │   └── org
    │       └── apache
    │           └── hadoop
    │               └── classification
    │                   ├── InterfaceAudience.java
    │                   ├── InterfaceStability.java
    │                   └── tools
    │                       ├── ExcludePrivateAnnotationsJDiffDoclet.java
    │                       ├── ExcludePrivateAnnotationsStandardDoclet.java
    │                       ├── IncludePublicAnnotationsStandardDoclet.java
    │                       ├── package-info.java
    │                       ├── RootDocProcessor.java
    │                       └── StabilityOptions.java
    └── main34.iml

7 directories, 9 files

```

## org.apache.hadoop.classification

该包内有两个类`InterfaceAudience`和`InterfaceStability`，分别是观众接口和稳定性接口。

- 类`InterfaceAudience`：

本注解告诉用户一个包，类，方法的认定的受众

``` java
// 被任何项目或应用使用的，public
public @interface Public {};

// 仅被注解指定的项目使用，例如”Common“，”HDFS“，”Zookeeper"等
public @interface LimitedPrivate {
    String[] value();
  };

// 仅被hadoop自身使用
public @interface Private {};
```

- 类`InterfaceStability`：

本注解告诉用户随着时间改变如何依赖一个特定的包，类或方法。

``` java
// 对于一个小版本发布能够发展并保持兼容性，仅在大版本(m.0)发布时会破坏兼容性
 public @interface Stable {};

// 能够发展，但在小版本(m.x)发布时能破坏兼容性
 public @interface Evolving {};

// 对任何级别的发布粒度都不能保证可靠性和稳定性
  public @interface Unstable {};
```

## org.apache.hadoop.classification.tools

该包定义了一些Javadoc设置能被指定项目使用：
``` java
@InterfaceAudience.LimitedPrivate({"Common", "Avro", "Chukwa", "HBase", "HDFS",
  "Hive", "MapReduce", "Pig", "ZooKeeper"})
```

- ExcludePrivateAnnotationsStandardDoclet：排除被注解为Private或LimitedPrivate的元素，它委托至标准的Doclet(指定Javadoc工具的输出内容和格式),并采用相同的选项。

- ExcludePrivateAnnotationsJDiffDoclet：排除被注解为Private或LimitedPrivate的元素，它委托至JDiff的Doclet(指定Javadoc工具的输出内容和格式),并采用相同的选项。

- IncludePublicAnnotationsStandardDoclet：仅包括被Public注解修饰的类级别的元素，类级别的没有使用注解的被排除在外，此外，所有被标记为Private，LimitedPrivate的元素都被排除在外，它委托至标准的Doclet(指定Javadoc工具的输出内容和格式),并采用相同的选项。

- RootDocProcessor：通过嵌套的排除Private和LimitedPrivate注解的元素的代理对象来处理替换Rootdoc。基于 http://www.sixlegs.com/blog/java/exclude-javadoc-tag.html 上的代码。

- StabilityOptions：稳定性选项:"-stable","-evolving","-unstable"。



