---
title: Python Numpy中的数组
comments: true
toc: true
date: 2016-07-18 16:50:05
categories: Programming
tags : Python
keywords: Python, Numpy
mathjax: true
---

>**本节内容：**NumPy 是 Numerical Python 的简称，是高性能计算和数据分析的基础包。本篇博文记录Numpy的数组一般用法，主要学习多维数组等用法。

<!-- more -->

## 概述

Numpy是Python的一个科学计算的库，提供了矩阵运算的功能，其一般与Scipy、matplotlib一起使用。

NumPy的主要对象是同类型的多维数组。这种数组里面包含了一个表格的元素（通常是数字），所有的元素为同一个类型。由一个正整数组成的元组来索引。在NumPy中维度被称为`axes`(轴)，轴的数目被称为`rank`(秩)。

Numpy的主要作用如下：
- ndarray，快速和节省空间的多维数组，提供数组化的算术运算和高级的 广播 功能。
- 使用标准数学函数对整个数组的数据进行快速运算，而不需要编写循环。
- 读取/写入磁盘上的阵列数据和操作存储器映像文件的工具。
- 线性代数，随机数生成，和傅里叶变换的能力。
- 集成C，C++，Fortran代码的工具。

从生态系统的角度看，最后一点是最为重要的。因为NumPy 提供了易用的`C API`，它可以很容易的将数据传递到使用低级语言编写的外部库，也可以使外部库返回NumPy数组数据到Python。 这一特性使得Python成为包装传统的C/C++/Fortran代码库，并给它们一个动态的、易于使用的接口的首选语言。

## 多维数组对象

在`NumPy`中表示数组的类为`ndarray`，别名就是array，注意，这里的`numpy.array`不同于Python标准库中的类`array.array`（这个数组只维护了一维的数组，仅仅提供了很少的函数方法）。下面是`ndarray`的重要属性：

1. `ndarray.ndim`
在ndarray数组中轴（维度）的数量。在Python的世界里，维度的数量又叫做rank。

2. `ndarray.shape`
在ndarray中数组的形状，这是一个由正整数组成的元组来代表数组中每一个维度的大小。对于一个n*m的矩阵，shape就是(n,m)，因此，shape元组的长度代表的就是维度也就是ndim。

3. `ndarray.size`
在ndarray数组中所有元素的数量，这个等于shape元组中相乘的数量。

4. `ndarray.dtype`
描述数组中的元素的类型的对象。人们可以创建类型或者使用python标准库中的类型来指定dtype。另外，NumPy也提供了它自己的类型，比如numpy.int32,numpy.int16,numpy.float64等。

5. `ndarray.itemsize`
描述数组中元素所占内存bytes的大小。举个例子，元素的类型为float64，那么itemsize就是8。这个值相当于ndarray.dtype.itemsize。

6. `ndarray.data`
含有数组中实际元素的缓冲器（the buffer containing the actual elements of the array）。通常情况下，我们并不需要使用这个属性，因为我们将使用索引访问数组中的元素。

例如如下操作：

``` python
In [1]: import numpy as np

In [2]: a = np.arange(15).reshape(3,5)
   ...:

In [3]: a
Out[3]:
array([[ 0,  1,  2,  3,  4],
       [ 5,  6,  7,  8,  9],
       [10, 11, 12, 13, 14]])

In [4]: a.shape
Out[4]: (3, 5)

In [5]: a.ndim
Out[5]: 2

In [6]: a.dtype.name
Out[6]: 'int64'

In [7]: a.itemsize
Out[7]: 8

In [8]: a.size
Out[8]: 15

In [9]: type(a)
Out[9]: numpy.ndarray

In [10]: b = np.array([6,7,8])

In [11]: b
Out[11]: array([6, 7, 8])

In [12]: type(b)
Out[12]: numpy.ndarray

```

## 创建数组

- 使用`numpy.array`方法，以`list`或`tuple`变量为参数:
``` python
In [13]: c = np.array([1,2,3,5])

In [14]: d = np.array((1.2,3,4,5))

In [15]: e = np.array([[1,2],[3,4],[5,6]])

In [16]: print(c,d,e,sep='\n')
[1 2 3 5]
[ 1.2  3.   4.   5. ]
[[1 2]
 [3 4]
 [5 6]]
```
 元素的类型也可以在创建数组的时候明确的指定：
 ``` python
 In [17]: print(np.array([[2,4,6],[8,10,12]],dtype=complex))
 [[  2.+0.j   4.+0.j   6.+0.j]
  [  8.+0.j  10.+0.j  12.+0.j]]

 ```
- 使用numpy.arange方法：产生连续的序列
``` python
In [18]: print(np.arange(2,20))
[ 2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19]

In [19]: print(np.arange(2,20).reshape(3,6))
[[ 2  3  4  5  6  7]
 [ 8  9 10 11 12 13]
 [14 15 16 17 18 19]]

```
- 使用numpy.linspace方法
：指定起点(包括)，终点(不包括),数据个数。
``` python
In [20]: print(np.linspace(1,5,10))
[ 1.          1.44444444  1.88888889  2.33333333  2.77777778  3.22222222
  3.66666667  4.11111111  4.55555556  5.        ]

```
- 使用numpy.zeros，numpy.ones，numpy.eye等方法构造特定的矩阵
``` python
In [21]: print(np.zeros((3,4)))
[[ 0.  0.  0.  0.]
 [ 0.  0.  0.  0.]
 [ 0.  0.  0.  0.]]

In [22]: print(np.ones((3,4)))
[[ 1.  1.  1.  1.]
 [ 1.  1.  1.  1.]
 [ 1.  1.  1.  1.]]

In [23]: print(np.eye(3))
[[ 1.  0.  0.]
 [ 0.  1.  0.]
 [ 0.  0.  1.]]

```

用于构建数组的标准函数的清单

|方法|描述|
| ------ | ------ |
| array | 转换输入数据（列表，数组或其它序列类型）到一个ndarray，可以推断一个dtype或明确的设置一个dtype。默认拷贝输入数据。|
|asarray|转换输入为一个ndarray，当输入已经是一个ndarray时就不拷贝。|
|arange|同内建的range函数，但不返回列表而是一个ndarray|
|ones, ones_like|根据提供的shape和dtype产生一个全1的数组。ones_like使用另一个数组为输入参数，产生一个shape和dtype都相同的数组。|
|zeros, zeros_like|同ones和ones_like，但是生成全0的数组|
|empty, enpty_like|通过分配新内存来构造新的数组，但不同与ones 和 zeros，不初始任何值。|
|eye, identity|生成一个NxN的单位方阵（对角线上为1，其它地方为0）|


## 数组和纯量之间的操作

相同大小的数组间的算术运算，其操作作用在对应的元素上

``` python
In [24]: e
Out[24]:
array([[1, 2],
       [3, 4],
       [5, 6]])

In [25]: e*e
Out[25]:
array([[ 1,  4],
       [ 9, 16],
       [25, 36]])

In [26]: 1.5*e
Out[26]:
array([[ 1.5,  3. ],
       [ 4.5,  6. ],
       [ 7.5,  9. ]])

In [27]: e**0.5
Out[27]:
array([[ 1.        ,  1.41421356],
       [ 1.73205081,  2.        ],
       [ 2.23606798,  2.44948974]])
In [28]: 2*e-1
Out[28]:
array([[ 1,  3],
       [ 5,  7],
       [ 9, 11]])
```
跟其他矩阵操作语言不同的是，NumPy的数组中，操作符*的操作结果是矩阵中元素对应相乘，矩阵相乘可以使用dot函数和方法:
``` python
In [31]: e
Out[31]:
array([[1, 2],
       [3, 4],
       [5, 6]])

In [32]: f =np.array([2,3])

In [33]: e.dot(f)
Out[33]: array([ 8, 18, 28])

In [34]: np.dot(e,f)
Out[34]: array([ 8, 18, 28])

```

## 数组的索引和切片

切片同list一样，非常方便。
``` python
In [39]: arr = np.arange(10)

In [40]: arr[4]
Out[40]: 4

In [41]: arr[5:8]
Out[41]: array([5, 6, 7])

In [42]: arr[-1]
Out[42]: 9

In [43]: arr2d = np.array([[1, 2, 3], [4, 5, 6], [7, 8, 9]])

In [44]: arr2d[1]
Out[44]: array([4, 5, 6])

In [45]: arr2d[:2]
Out[45]:
array([[1, 2, 3],
       [4, 5, 6]])

In [46]: arr2d[:2,1:]
Out[46]:
array([[2, 3],
       [5, 6]])

In [47]: arr2d[:, :1]
Out[47]:
array([[1],
       [4],
       [7]])

```

多维数组可以在每一个轴上有一个索引，这些索引可以在元组里面用逗号分隔：
``` python
In [48]: def f(x,y):
    ...:     return 2*x-y
    ...:

In [49]: g = np.fromfunction(f,(5,4),dtype=int)

In [50]: g
Out[50]:
array([[ 0, -1, -2, -3],
       [ 2,  1,  0, -1],
       [ 4,  3,  2,  1],
       [ 6,  5,  4,  3],
       [ 8,  7,  6,  5]])
In [51]: g[2,3]
Out[51]: 1

In [52]: g[0:5,1]
Out[52]: array([-1,  1,  3,  5,  7])

In [53]: g[:,1]
Out[53]: array([-1,  1,  3,  5,  7])


```


## 通用函数

NumPy提供了相似的数学函数，例如`sin`,`cos`,`exp`等。在NumPy中，这些被叫做`universal function`，在NumPy里这些函数作用按数组的元素运算，然后产生一个新的数组作为输出。

``` python
>>> B = np.arange(3)
>>> B
array([0, 1, 2])
>>> np.exp(B)
array([ 1.        ,  2.71828183,  7.3890561 ])
>>> np.sqrt(B)
array([ 0.        ,  1.        ,  1.41421356])
>>> C = np.array([2., -1., 4.])
>>> np.add(B, C)
array([ 2.,  0.,  6.])
```

- 单目运算

|方法|描述|
| ------ | ------ |
|abs, fabs  | 计算基于元素的整形，浮点或复数的绝对值。fabs对于没有复数数据的快速版本|
|sqrt   | 计算每个元素的平方根。等价于 arr ** 0.5|
|square | 计算每个元素的平方。等价于 arr ** 2|
|exp |计算每个元素的指数。|
|log, log10, log2, log1p |自然对数（基于e），基于10的对数，基于2的对数和 log(1 + x)|
|sign  |  计算每个元素的符号：1(positive)，0(zero)， -1(negative)|
|ceil  |  计算每个元素的天花板，即大于或等于每个元素的最小值|
|floor  | 计算每个元素的地板，即小于或等于每个元素的最大值|
|rint   | 圆整每个元素到最近的整数，保留dtype|
|modf    |分别返回分数和整数部分的数组|
|isnan   |返回布尔数组标识哪些元素是 NaN （不是一个数）|
|isfinite, isinf |分别返回布尔数组标识哪些元素是有限的（non-inf, non-NaN）或无限的|
|cos, cosh, sin sinh, tan, tanh  regular 和 hyperbolic| 三角函数|
|arccos, arccosh, arcsin, arcsinh, arctan, arctanh   |反三角函数|
|logical_not |计算基于元素的非x的真值。等价于 -arr|

- 双目运算

|方法|描述|
| ------ | ------ |
|add |在数组中添加相应的元素|
|substract  | 在第一个数组中减去第二个数组|
|multiply   | 对数组元素相乘|
|divide, floor_divide   | 除和地板除（去掉余数）|
|power  | 使用第二个数组作为指数提升第一个数组中的元素|
|maximum, fmax  | 基于元素的最大值。 fmax 忽略 NaN|
|minimum, fmin |  基于元素的最小值。 fmin 忽略 NaN|
|mod |基于元素的模（取余）|
|copysign   | 拷贝第二个参数的符号到第一个参数 |
|greater, greater_equal, less, less_equal, not_equal| 基于元素的比较，产生布尔数组。等价于中缀操作符 >, >=, <, <=, ==, !=
|logical_and, logical_or, logical_xor |   计算各个元素逻辑操作的真值。等价于中缀操作符 &, ^ |

## 转置数组和交换坐标轴

转置是一种特殊形式的变形，类似的它会返回基础数据的一个视窗，而不会拷贝任何东西。数组有`transpose`方法和专门的`T`属性：
``` python
In [55]: arr = np.arange(15).reshape((3, 5))

In [56]: arr
Out[56]:
array([[ 0,  1,  2,  3,  4],
       [ 5,  6,  7,  8,  9],
       [10, 11, 12, 13, 14]])

In [57]: arr.T
Out[57]:
array([[ 0,  5, 10],
       [ 1,  6, 11],
       [ 2,  7, 12],
       [ 3,  8, 13],
       [ 4,  9, 14]])
```
当进行矩阵运算时，，使用 np.dot 计算内部矩阵来产生$X^TX$ ：
``` python
In [58]: np.dot(arr.T, arr)
Out[58]:
array([[125, 140, 155, 170, 185],
       [140, 158, 176, 194, 212],
       [155, 176, 197, 218, 239],
       [170, 194, 218, 242, 266],
       [185, 212, 239, 266, 293]])
```

对于更高维的数组，`transpose`接受用于转置的坐标轴的号码的一个元组（for extra mind bending）：
``` python
In [59]: arr = np.arange(16).reshape((2, 2, 4))

In [60]: arr
Out[60]:
array([[[ 0,  1,  2,  3],
        [ 4,  5,  6,  7]],

       [[ 8,  9, 10, 11],
        [12, 13, 14, 15]]])

In [61]: arr.transpose((1, 0, 2))
Out[61]:
array([[[ 0,  1,  2,  3],
        [ 8,  9, 10, 11]],

       [[ 4,  5,  6,  7],
        [12, 13, 14, 15]]])
In [62]: arr.swapaxes(1, 2)
Out[62]:
array([[[ 0,  4],
        [ 1,  5],
        [ 2,  6],
        [ 3,  7]],

       [[ 8, 12],
        [ 9, 13],
        [10, 14],
        [11, 15]]])

```

## 数组合并

``` python
#水平组合
>>> a = arange(9).reshape(3,3)
>>> b = a * 2
>>> hstack((a,b))
array([[ 0,  1,  2,  0,  2,  4],
       [ 3,  4,  5,  6,  8, 10],
       [ 6,  7,  8, 12, 14, 16]])

#垂直组合
>>> vstack((a,b))
array([[ 0,  1,  2],
       [ 3,  4,  5],
       [ 6,  7,  8],
       [ 0,  2,  4],
       [ 6,  8, 10],
       [12, 14, 16]])

# 也可以通过concatenate函数来进行组合
# NumPy中维度(dimensions)叫做轴(axis)，轴的个数叫做秩(rank)
>>> concatenate((a,b),axis=0)
array([[ 0,  1,  2],
       [ 3,  4,  5],
       [ 6,  7,  8],
       [ 0,  2,  4],
       [ 6,  8, 10],
       [12, 14, 16]])

# 深度组合
>>> dstack((a,b))
array([[[ 0,  0],
        [ 1,  2],
        [ 2,  4]],

       [[ 3,  6],
        [ 4,  8],
        [ 5, 10]],

       [[ 6, 12],
        [ 7, 14],
        [ 8, 16]]])
```

## 数组分割

``` python
>>> a = arange(9).reshape(3,3)
>>> print a
[[0 1 2]
 [3 4 5]
 [6 7 8]]

# 水平分割
>>> hsplit(a,3)
[array([[0],
       [3],
       [6]]), array([[1],
       [4],
       [7]]), array([[2],
       [5],
       [8]])]

# 垂直分割
>>> vsplit(a,3)
[array([[0, 1, 2]]), array([[3, 4, 5]]), array([[6, 7, 8]])]

# 通过split 指定轴进行分割
>>> split(a,3,axis=1)
[array([[0],
       [3],
       [6]]), array([[1],
       [4],
       [7]]), array([[2],
       [5],
       [8]])]

# 深度分割
# 必须三个维度以上的数组，
>>> a = arange(24).reshape(2,3,4)
>>> dsplit(a,2)
[array([[[ 0,  1],
        [ 4,  5],
        [ 8,  9]],

       [[12, 13],
        [16, 17],
        [20, 21]]]), array([[[ 2,  3],
        [ 6,  7],
        [10, 11]],

       [[14, 15],
        [18, 19],
        [22, 23]]])]
```