---
title: Hadoop源码学习(1)——Hadoop-Common-Project
comments: true
toc: true
date: 2016-08-01 16:17:02
categories: SourceCodeLearning
tags : Hadoop
keywords: hadoop，结构
---

>**本节内容：**Hadoop-Common-Project模块包括了一系列支撑其他Hadoop模块的公共工具，本系列文章从源代码的角度分析其实现。本文主要从总体上了解Hadoop-Common-Project模块。


<!-- more -->

## Hadoop主要模块

- hadoop                       (主要的Hadoop模块)

    - hadoop-project           (所有Hadoop Maven 模块的Parent POM，由以下部分组成：        )
        - hadoop-project-dist      (Parent POM for modules that generate distributions.)
        - hadoop-annotations       (Generates the Hadoop doclet used to generated the Javadocs)
        - hadoop-assemblies        (Maven assemblies used by the different modules)
        - hadoop-common-project    (Hadoop Common)
        - hadoop-hdfs-project      (Hadoop HDFS)
        - hadoop-mapreduce-project (Hadoop MapReduce)
        - hadoop-tools             (Hadoop tools like Streaming, Distcp, etc.)
        - hadoop-dist              (Hadoop distribution assembler)

- hadoop-2.6.4-src结构

``` txt
.
├── BUILDING.txt
├── dev-support
├── hadoop-assemblies
├── hadoop-client
├── hadoop-common-project
├── hadoop-dist
├── hadoop-hdfs-project
├── hadoop-main.iml
├── hadoop-mapreduce-project
├── hadoop-maven-plugins
├── hadoop-minicluster
├── hadoop-project
├── hadoop-project-dist
├── hadoop-tools
├── hadoop-yarn-project
├── LICENSE.txt
├── NOTICE.txt
├── pom.xml
└── README.txt

```

## Hadoop-Common-Project

- 结构

``` txt
├── dev-support：补丁等开发支持
├── hadoop-annotations：Hadoop注解
├── hadoop-auth：Hadoop授权
├── hadoop-auth-examples：Hadoop授权例子
├── hadoop-common：Hadoop Common模块
├── hadoop-common-project.iml
├── hadoop-kms：基于KeyProvider API的密钥管理服务器
├── hadoop-minikdc：基于Apache Directory服务器迷你型密钥分发中心(KDC)，能够被嵌入到测试用例或作为一个独立的KDC从命令行使用
├── hadoop-nfs：Hadoop NFS模块
├── pom.xml
└── target
```

Hadoop-Common奠定了整个Hadoop项目的基石，其中hadoop-common是核心，IO、FS、HA、IPC、NET等构成了分布式系统的基础。


