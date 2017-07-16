---
title: "大数据二次排序"
date: 2016-03-07 20:33:53 +0800
comments: true
categories: BigData
tags : hadoop
toc: true
mathjax: true
keywords: 二次排序, Hadoop, MapReduce
---

##  问题概述
> 在hadoop中每个reduce的输入的key都是有序的，而value则是无序的。而且同一个job运行多次，由于map完成顺序不同，reduce收到的value顺序是不固定的,为了使reduce收到有序的value,便产生了二次排序的概念。

<!-- more -->

**二次排序实例**
考虑到在科学实验中的温度数据，它们可能的格式如下，每一列分别为 `year`,`month`,`day`,`temperature`:
``` python
2012,01,01,5
2012,01,02,6
2012,01,03,2
2012,01,04,1
2012,01,05,10
2012,01,06,4
...
2001,11,01,18
2001,11,02,19
2001,11,03,15
2001,11,04,12
...
```
我们想按照`year-month`对温度数据进行升序排序并输出，并且我们希望对`reducer`中的迭代值是已经排序的。因此，期望的输出为：
``` python
2012-01: 1,2,4,5,6,10...
2001-11: 12,15,18,19...
```

## 解决方案
为了使用MapReduce编程框架,当数据量很大时在reduce阶段在内存中对同一个key里面的值进行排序是不太可能的，因此`通过向原生key增加一部分或全部值(value)作为复合key来实现排序操作`。

**方法概要**:

  - *1*:使用`Value-to-Key`设计模式：设计复合Key-Value,`(K，V1,V2)=>(K1,V2)`，其中K是原生Key，V1是要排序的值，V2是其他值，K1是复合Key，显然K1=(K,V1)。
  - *2*:使用MapReduce执行框架进行排序(注意：不是直接在内存中排序，而是利用集群节点进行排序)。
  - *3*:在多个Key-Value键值对中保存状态来进行处理，可以通过原生键对mapper输出的中间结果进行partition操作。

