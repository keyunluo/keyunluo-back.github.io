---
title: Python Scipy学习(1)
comments: true
toc: true
date: 2016-07-19 14:41:02
categories: Programming
tags : Python
keywords: Python, Scipy
mathjax: true
---

>**本节内容：**SciPy是另一种使用NumPy来做高等数学、信号处理、优化、统计和许多其它科学任务的语言扩展,SciPy在NumPy的基础上增加了众多的数学、科学以及工程计算中常用的模块。例如线性代数、常微分方程数值求解、信号处理、图像处理、稀疏矩阵等等,本节主要学习其统计和优化模块。

<!-- more -->

## 常用模块

由于SciPy以NumPy为基础，那么import scipy的同时便import 了numpy库。

- 统计子库-scipy.stats
- 线性代数-scipy.linalg
- 拟合与优化-scipy.optimize
- 插值-scipy.interpolate
- 数值积分-scipy.integate
- 稀疏矩阵-scipy.sparse
- 图像操作scipy.misc
- 图像处理-scipy.ndimage

## 统计

scipy.stats的主要功能有以下几个方面：
1. 数值随机变量对象（包括密度分布函数，累积分布函数，样本函数等）
2. 一些估计方法
3. 一些测试方法

- 随机变量与分布:考虑$Beta$函数

numpy 中提供了获取随机变量的样本的方法：

``` python
In [1]: import numpy as np

In [2]: np.random.beta(5, 5, size=3)
Out[2]: array([ 0.6167565 ,  0.67994589,  0.32346476])
```
但是np.random.beta(a,b)是根据下面的函数得到的：

$f(x;a,b)=\frac{x^{(a-1)}(1-x)^{(b-1)} }{\int_0^1 u^{(a-1)}u^{(b-1)} {\rm d}u} , (0 \le x \le 1)$

为了获取更多beta分布的特性，我们经常使用scipy.stats:
``` python
import numpy as np
from scipy.stats import beta
from matplotlib.pyplot import hist, plot, show
q = beta(5, 5)      # Beta(a, b), with a = b = 5 q是一个对象
obs = q.rvs(2000)   # 2000 observations 获得2000个样本
hist(obs, bins=40, normed=True)
grid = np.linspace(0.01, 0.99, 100)
plot(grid, q.pdf(grid), 'k-', linewidth=2)
show()
```

结果如图：
![beta分布特性](/resource/blog/2016-07/beta.png)

``` python
>>> q.cdf(0.4)      # Cumulative distribution function  累积密度函数
0.26656768000000003
>>> q.pdf(0.4)      # Density function   密度函数
2.0901888000000013
>>> q.ppf(0.8)      # Quantile (inverse cdf) function
0.63391348346427079
>>> q.mean()
0.5

```

- 线性回归

``` python
In [19]: from scipy.stats import linregress

In [20]: x = np.random.randn(200)

In [21]: y = 2 * x + 0.1 * np.random.randn(200)

In [22]: gradient, intercept, r_value, p_value, std_err = linregress(x, y)

In [23]: gradient, intercept
Out[23]: (1.9962554379482236, 0.008172822032671799)
```

## 线性代数

NumPy和SciPy都提供了线性代数函数库linalg，SciPy的线性代数库比NumPy更加全面。

- 解线性方程组

`numpy.linalg.solve(A, b)`和`scipy.linalg.solve(A, b)`可以用来解线性方程组$Ax = b $，也就是计算$ x = A^{-1}b$ 。这里$A$为(M, M)的方形矩阵，$x$和$b$为长为M的向量。有时候$A$是固定的，需要对多组$b$进行求解，因此第二个参数也可以是(M, N)的矩阵$B$。这样计算出来的$X$也为(M, N)的矩阵。它相当于计算$A^{-1}B$。

``` python
>>> import numpy as np
>>> from scipy import linalg
>>>
>>> M, N = 500, 50
>>> A = np.random.rand(M, M)
>>> B = np.random.rand(M, N)
>>> X1 = linalg.solve(A, B)
>>> X2 = np.dot(linalg.inv(A), B)
>>> np.allclose(X1, X2)
True

```

lu_factor(A)对矩阵$A$进行LU分解，得到一个元组：(LU矩阵，排序数组)，将这个元组传递给lu_solve(),即可对不同的$B$进行求解。

``` python
>>> luf = linalg.lu_factor(A)
>>> X3 = linalg.lu_solve(luf, B)
>>> np.allclose(X1, X3)
True

```

- 最小二乘解

lstsq()比solve()更一般化，它不要求矩阵$A$是正方形的，也就是说方程的个数可以少于、等于或者多于未知数的个数。它找到一组解$x$，使得$\Vert b - A \cdot x \Vert$最小。我们称所得到的结果为最小二乘解，即它使得所有等式的误差的平方和最小。

- 奇异值分解-SVD

奇异值分解是线性代数中一种重要的矩阵分解，在信号处理、统计学等领域有重要应用。假设$M$是一个m×n阶矩阵，存在一个分解使得：$ M = U \sum V^{\*} $。其中$U$是m×m阶矩阵；$ \sum $是半正定m×n阶对角矩阵；而$V^*$，即$V$的共轭转置，是n×n阶矩阵。这样的分解就称作$M$的奇异值分解。$ \sum $对角线上的元素为$M$的奇异值。通常奇异值按照从大到小的顺序排列。

## 拟合与优化

- 求根与稳定点

稳定点：已知连续函数$f(x)$ ,则函数$f(x)$的稳定点为$x_0$，使得条件$f(x_0) = x_0$成立。

例如：$f(x)=sin(4(x-\frac{1}{4})) + x + x^{20} -1$

``` python
from scipy.optimize import bisect
f =lambda x : np.sin(4 * (x -0.25)) + x + x**20 -1
bisect(f,0,1)
0.4082935042797544
```

- 非线性方程组求解

fsolve()可以对非线性方程组进行求解。它的基本调用形式为fsolve(func, x0)。其中func是计算方程组误差的函数，它的参数x是一个数组，其值为方程组的一组可能的解。func返回将x代入方程组之后得到的每个方程的误差，x0为未知数的一组初始值。假设要对下面的方程组进行求解:
$$ f1(u1,u2,u3) = 0 ,f2(u1,u2,u3) = 0 , f3(u1,u2,u3) = 0 $$

那么func函数可以如下定义：
``` python
def func(x):
    u1,u2,u3 = x
    return [f1(u1,u2,u3), f2(u1,u2,u3), f3(u1,u2,u3)]
```

例如：对下列方程组求解：
$$ 5x_1 +3 =0;4x_0^2 - 2sin(x_1x_2)=0;x_1x_2-1.5=0$$

``` python
from math import sin, cos
from scipy import optimize

def f(x):
    x0, x1, x2 = x.tolist()
    return [
        5*x1+3,
        4*x0*x0 - 2*sin(x1*x2),
        x1*x2 - 1.5
    ]

# f计算方程组的误差，[1,1,1]是未知数的初始值
result = optimize.fsolve(f, [1,1,1])
print(result)
print(f(result))
```
f()是计算方程组的误差的函数，x参数是一组可能的解。fsolve()在调用f()时，传递给f()的参数是一个数组。

调用fsolve()时，传递计算误差的函数f()，以及未知数的初始值。






