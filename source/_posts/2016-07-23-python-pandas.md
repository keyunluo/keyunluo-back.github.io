---
title: Python Pandas数据处理入门
comments: true
toc: true
date: 2016-07-23 17:50:02
categories: Programming
tags : Python
keywords: Python, Pandas
---

>**本节内容：**Pandas包含高级的数据结构和精巧的工具，使得在Python中处理数据非常快速和简单。Pandas建造在NumPy之上，它使得以NumPy为中心的应用很容易使用。

<!-- more -->

##  Series

### 概述

Series是一个一维的类似数组的对象，包含一个数组的数据（任何NumPy的数据类型）和一个与数组关联的数据标签，被叫做 索引 。最简单的Series是由一个数组的数据构成：

``` python
>>> import pandas as pd
>>> x=pd.Series(index = range(5),data=[1,3,9,11,12])
>>> x
0 1
1 3
2 9
3 11
4 12
```
Pandas的数据结构主要用来处理时序数据，因此，`index`项必须是可排序的。

### 数据访问

``` python
>>> x=pd.Series(index = ['a','b','d','z','z'],data=[1,3,9,11,12])
>>> x
a     1
b     3
d     9
z    11
z    12
dtype: int64

```

 - 点标选择

 ``` python
 >>> x.a
 1
 >>> x.z
 z 11
 z 12
 ```

 - 下标选择,使用iloc

 ``` python
 >>> x.iloc[:3]
 a 1
 b 3
 d 9
 >>> x.loc['a':'d']
 a    1
 b    3
 d    9
 dtype: int64
 >>> x['a':'d']
 a    1
 b    3
 d    9
 dtype: int64
 ```
使用分片时，注意包括终点。

### 聚集和统计

``` python
>>> x = pd.Series(range(5),[1,2,11,9,10])
>>> x
1     0
2     1
11    2
9     3
10    4
dtype: int64
>>> grp=x.groupby(lambda i:i%2)
>>> grp.get_group(0)
2     1
10    4
dtype: int64
>>> grp.get_group(1)
1     0
11    2
9     3
dtype: int64
>>> grp.max()
0    4
1    3
dtype: int64

```

 ##  DataFrame

 一个DataFrame表示一个表格，类似电子表格的数据结构，包含一个经过排序的列表集，它们每一个都可以有不同的类型值（数字，字符串，布尔等等）。DataFrame有行和列的索引；它可以被看作是一个Series的字典（每个Series共享一个索引）。与其它你以前使用过的（如 R 的 data.frame )类似DataFrame的结构相比，在DataFrame里的面向行和面向列的操作大致是对称的。在底层，数据是作为一个或多个二维数组存储的，而不是列表，字典，或其它一维的数组集合。

### 创建 ： 使用字典

索引会自动分配，并且对列进行了排序：

``` python
>>> df = pd.DataFrame({'col1':[1,3,11,2],'col2':[9,23,0,2]})
>>> df
   col1  col2
0     1     9
1     3    23
2    11     0
3     2     2

```

### 数据访问

``` python
>>> df.iloc[:2,:2]
   col1  col2
0     1     9
1     3    23
>>> df['col1']
0     1
1     3
2    11
3     2
Name: col1, dtype: int64
>>> df.col1
0     1
1     3
2    11
3     2
Name: col1, dtype: int64

```

### 聚集和统计

``` python
>>> df = pd.DataFrame({'col1':[1,1,0,0],'col2':[1,2,3,4]})
>>> df
   col1  col2
0     1     1
1     1     2
2     0     3
3     0     4
>>> grp = df.groupby('col1')
>>> grp.get_group(0)
   col1  col2
2     0     3
3     0     4
>>> grp.get_group(1)
   col1  col2
0     1     1
1     1     2
>>> grp.sum()
      col2
col1
0        7
1        3
```
### 产生新列

给一个不存在的列赋值，将会创建一个新的列。

``` python
>>> df['sum_col']=df.eval('col1+col2')
>>> df
   col1  col2  sum_col
0     1     1        2
1     1     2        3
2     0     3        3
3     0     4        4

>>> grp = df.groupby(['sum_col','col1'])
>>> res = grp.sum()
>>> res
              col2
sum_col col1
2       1        1
3       0        3
        1        2
4       0        4
>>> res.unstack()
        col2
col1       0    1
sum_col
2        NaN  1.0
3        3.0  2.0
4        4.0  NaN

```