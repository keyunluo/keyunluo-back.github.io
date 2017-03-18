---
title: 统计学习方法——感知机
comments: true
toc: true
date: 2016-08-05 08:25:02
categories: MachineLearning
tags : 机器学习
keywords: 机器学习, 统计学习
mathjax: true
---

>**本节内容：**感知机(perceptron)是二类分类的线性分类模型，其输入为实例的特征向量，输出为实例的类别取+1和-1二值，感知机对应于输入空间(特征空间)中将实例划分为正负两类的分离超平面，本节对应于统计学习方法第二章的内容。


<!-- more -->

## 感知机模型

1. 定义

    假设输入空间(特征空间)是 $ \mathcal{X} \subseteq \mathcal{R}^n $ ,输出空间是$\mathcal{y} = \\{+1,-1\\}$. 输入$x \in \mathcal{X}$表示实例的特征向量，对应于输入空间(特征空间)的点；输出$y \in \mathcal{Y}$表示实例的类别。由输入空间到输出空间的如下函数：

    $$f(x)=sign(w \cdot x+b)$$

    被称为感知机，其中，$w$和$b$称为感知机模型参数，$w \in \mathcal{R}^n $称为权值或权值向量，$b \in \mathcal{R} $叫做偏置，$w \cdot x $ 表示$w$和$x$的内积，$sign$是符号函数，即：

    $$
    f(x)=
    \begin{cases}
    +1& \text{x >= 0} \\\\
    -1& \text{x < 0}
    \end{cases}
    $$

    感知机是一种线性分类模型，属于判别模型，感知机模型的架设空间是定义在特征空间中的所有线性分类模型l(linear classification model)或线性分类器(linear classifier)，即函数集合$\\{f \mid f(x)=w \cdot x+b \\}$

2. 感知机的几何解释

    线性方程 $ w \cdot x +b =0 $ 所代表的超平面将特征空间划分为正负两个部分。

    ![感知机模型](/resource/blog/2016-08/perceptron.jpg)

    这个平面（2维时退化为直线）称为分离超平面。

## 感知机学习策略

1. 数据集的线性可分性

    给定一个数据集： $T = \\{(x_1,y_1),(x_2,y_2),\cdots ,(x_N,y_N)\\}$,其中，$x_i \in \mathcal{X} = \mathcal{R}^n , y_i \in \mathcal{Y} = \\{+1,-1\\}, i = 1,2,\cdots ,N$,如果存在某个超平面$S$ : $w \cdot x + b =0$ 能够将数据集的正实例点和负实例点完全正确的划分到超平面的两侧，则称数据集$T$为线性可分数据集,否则称数据集$T$线性不可分。

2. 感知机的学习策略

    假定数据集线性可分，我们希望找到一个合理的损失函数。

    一个朴素的想法是采用误分类点的总数，但是这样的损失函数不是参数$w，b$的连续可导函数，不可导自然不能把握函数的变化，也就不易优化（不知道什么时候该终止训练，或终止的时机不是最优的）。

    另一个想法是选择所有误分类点到超平面S的总距离。为此，先定义点$x_0$到平面$S$的距离：

    $$\frac{1}{\Vert w \Vert} \vert w \cdot x_0 + b \vert $$

    其中，$\Vert w \Vert$是$w$的$L_2$范数。

    考虑到误分类时-y>0,去掉绝对值符号，得到误分类点到超平面S的距离公式：

    $$-\frac{1}{\Vert w \Vert} y_i ( w \cdot x_i + b)$$

    假设所有误分类点构成集合M，那么所有误分类点到超平面S的总距离为:

    $$-\frac{1}{\Vert w \Vert} \sum_{x_i \in M}y_i ( w \cdot x_i + b)$$

    不考虑系数，得到感知机$sign(w \cdot x +b$学习的损失函数：

    $$L(w,b)= - \sum_{x_i \in M} y_i(w \cdot x_i +b)$$

    其中$M$为误分类点的集合，这个损失函数就是感知机学习的经验风险函数。


## 感知机学习算法的原始形式

1. 概述

    感知机学习算法是对以下最优化问题的算法,求参数$w,b$，使损失函数极小化：

    $$min\_{w,b} L(w,b)= - \sum\_{x\_i \in M} y\_i(w \cdot x\_i +b)$$

    感知机学习算法是误分类驱动的，先随机选取一个超平面，然后用随机梯度下降法(Stochastic Gradient Decent)不断极小化上述损失函数。损失函数的梯度由：

    $$ \nabla\_w L(w,b) = - \sum\_{x\_i \in M} y\_ix\_i $$

    $$ \nabla\_b L(w,b) = - \sum\_{x\_i \in M} y\_i $$

    给出，所谓梯度，是一个向量，指向的是标量场增长最快的方向，长度是最大变化率。所谓标量场，指的是空间中任意一个点的属性都可以用一个标量表示的场。

    随机选择一个误分类点$(x_i,y_i)$，对$w,b$进行更新：

    $$
    w \leftarrow w + \eta y_ix_i \\\\
    b \leftarrow b+ \eta y_i
    $$

    $\eta(0<\eta \leq 1)$是步长，在统计学习中称为学习率。损失函数的参数加上梯度上升的反方向，于是就梯度下降了。所以，上述迭代可以使损失函数不断减小，直到为0。

2. 感知器学习算法

    输入：训练数据集$T=\\{(x_1,y_1),(x_2,y_2), \cdots , (x_N,y_N)\\},其中x_i \in \mathcal{X} = \mathcal{R}^n,y_i \in \mathcal{Y} = \\{+1,-1 \\},i=1,2,\cdots ,N;学习率\eta(0<\eta \leq 1);$

    输出：$w,b; 感知机模型 f(x) = sign(w \cdot x +b)$

    (1) 选取初值 $w_0,b_0$

    (2) 在训练集中选取数据$(x_i,y_i)$

    (3) 如果$y_i(w \cdot x_i +b) \leq 0 $

    $$
    w \leftarrow w + \eta y_ix_i  \\\\
    b \leftarrow b+ \eta y_i
    $$

    (4) 转至(2)，直到训练集中没有误分类点

3. 一个例子

    如下图所示的训练数据集，其正实例点是$x_1=(3,3)^T,x_2=(4,3)^T$,负实例点是$x_3=(1,1)^T$，试用感知机学习算法的原始形式求解感知机模型$f(x)=sign(w \cdot x +b),其中w = (w^{(1)},w^{(2)})^T,x = (x^{(1)},x^{(2)})^T$

    ![感知机实例](/resource/blog/2016-08/perceptronexample.jpg)

    解： 构建最优化问题,按照上述算法求解$w,b,\eta = 1$

    (1) 取初值$w_0=0,b_0 =0$

    (2) 对$x_1=(3,3)^T,y_1(w_0 \cdot x_1 +b_0) = 0$,未能被正确分类，更新$w,b$:

    $$
    w_1 = w_0 +y_1x_1=(3,3)^T  \\\\
    b_1 = b_0 +y_1 = 1
    $$

    得到线性模型

    $$w_1 \cdot x + b_1 = 3x^{(1)}+3x^{(2)}+1$$

    (3) 对$x_1,x_2，显然有y_i(w_1 \cdot x_i +b_1)>0，被正确分类，不修改w,b;对于x_3=(1,1)^T,y_3(w_1 \cdot x_3 +b_1)<0，被误分类，更新w,b:$

    $$
    w_2 = w_1 +y_3x_3=(2,2)^T \\\\
    b_2 = b_1 + y_3 = 0
    $$

    得到线性模型： $w_2 \cdot x + b_2 = 2x^{(1)}+2x^{(2)}$

    如此继续下去，直到：

    $$
    w_7 =(1,1)^T ,b_7=-3
    $$

    对于所有的点都没有误分类点，损失函数达到极小。

4. Python代码实现

``` python
# -*- coding:utf-8 -*-
import copy
from matplotlib import pyplot as plt
from matplotlib import animation

training_set = [[(3, 3), 1], [(4, 3), 1], [(1, 1), -1]]
w = [0, 0]
b = 0
eta = 1
history = []

# 更新变量:w,b
def update(item):
    global w, b, eta , history
    w[0] += eta * item[1] * item[0][0]
    w[1] += eta * item[1] * item[0][1]
    b += eta * item[1]
    print(w, b)
    history.append([copy.copy(w), b])

# 计算y(w*xi+b)
def cal(item):
    res = 0
    for i in range(len(item[0])):
        res += item[0][i] * w[i]
    res += b
    res *= item[1]
    return res

# 判断超平面是否正确
def check():
    flag = False
    for item in training_set:
        if cal(item) <= 0:
            flag = True
            update(item)

    if not flag:
        print("RESULT: w: " + str(w) + " b: " + str(b))
    return flag


if __name__ == "__main__":
    for i in range(1000):
        if not check(): break

    # first set up the figure, the axis, and the plot element we want to animate
    fig = plt.figure()
    ax = plt.axes(xlim=(0, 4), ylim=(-2, 2))
    line, = ax.plot([], [], 'g', lw=2)
    label = ax.text([], [], '')

    # initialization function: plot the background of each frame
    def init():
        line.set_data([], [])
        x, y, x_, y_ = [], [], [], []
        for p in training_set:
            # 正实例
            if p[1] > 0:
                x.append(p[0][0])
                y.append(p[0][1])
            # 负实例
            else:
                x_.append(p[0][0])
                y_.append(p[0][1])

        plt.plot(x,y,'bo')
        plt.plot(x_,y_,'rx')
        plt.legend(['超平面','正实例', '负实例'], loc='upper left')
        plt.axis([-1, 5, -1, 5])
        plt.grid(True)
        plt.xlabel('x')
        plt.ylabel('y')

        plt.title('感知机算法实验——原始形式')
        return line, label

    # animation function.  this is called sequentially
    def animate(i):
        global history, ax, line, label

        w = history[i][0]
        b = history[i][1]

        hyperplane = "{0}*x1+{1}*x2+{2}=0".format(w[0],w[1],b)

        if w[1] == 0:
            line.set_data([0, 0], [0, 0])
            label.set_text("第%d次运算:"%(i+1)+"超平面:"+hyperplane)
            label.set_position([0, 0])
            return line, label
        x1 = -6
        y1 = -(b + w[0] * x1) / w[1]
        x2 = 6
        y2 = -(b + w[0] * x2) / w[1]
        line.set_data([x1, x2], [y1, y2])
        x1 = 0
        y1 = -(b + w[0] * x1) / w[1]
        label.set_text("第%d次运算:"%(i+1)+"超平面:"+hyperplane)
        label.set_position([x1, y1])
        return line, label

    print(history)
    anim = animation.FuncAnimation(fig, animate, init_func=init, frames=len(history), interval=1000, repeat=True,blit=True)
    plt.show()
    anim.save('perceptron-animate.gif', fps=2, writer='imagemagick')
```
动画显示如下：
![perceptron-animate](/resource/blog/2016-08/perceptron-animate.gif)


## 感知机学习算法的对偶形式

1. 概述
    对偶指的是，将$w和b$表示为测试数据$i$的线性组合形式，通过求解系数得到$w和b$。具体说来，如果对误分类点i逐步修改$wb$修改了$n$次，则$w，b关于i$的增量分别为增量${\alpha}_iy_ix_i和{\alpha}_iy_i，这里{\alpha}_i=n_i\eta$，则最终求解到的参数分别表示为：

    $$
    w \leftarrow w + \eta y_ix_i \\\\
    b \leftarrow b + \eta y_i
    $$

2. 算法

    输入：线性可分的数据集$T=\\{(x_1,y_1),(x_2,y_2), \cdots , (x_N,y_N)\\},其中x_i \in \mathcal{X} = \mathcal{R}^n,y_i \in \mathcal{Y} = \\{+1,-1 \\},i=1,2,\cdots ,N;学习率\eta(0<\eta \leq 1);$

    输出：$\alpha,b; 感知机模型 f(x) = sign(\sum_{j=1}^N \alpha_jy_jx_j \cdot x +b),其中\alpha = (\alpha_1,\alpha_2,\cdots ,\alpha_N)^T$

    (1) 选取初值 $\alpha \leftarrow 0,b \leftarrow 0$

    (2) 在训练集中选取数据$(x_i,y_i)$

    (3) 如果$y\_i(\sum\_{j=1}^N \alpha\_jy\_jx\_j \cdot x +b) \leq 0 $

    $$
    \alpha_i \leftarrow \alpha_i + \eta  \\\\
    b \leftarrow b+ \eta y_i
    $$

    (4) 转至(2)，直到训练集中没有误分类点

    对偶形式中的训练实例仅以内积的形式出现，为了方便，可以预先将训练集中实例间的内积计算出来并以矩阵的形式存储，这个矩阵被称为Gram矩阵：

    $$ G = [x\_i \cdot x\_j]\_{N \times N} $$

3. 代码

``` python
# -*- coding:utf-8 -*-
import numpy as np
from matplotlib import pyplot as plt
from matplotlib import animation

training_set = [[(3, 3), 1], [(4, 3), 1], [(1, 1), -1]]
# training_set = np.array([[[3, 3], 1], [[4, 3], 1], [[1, 1], -1], [[5, 2], -1]])
a = np.zeros(len(training_set), np.float)
b = 0.0
Gram = None
eta = 1
# y = np.array(training_set[:, 1])
y = np.empty((len(training_set)), np.float)
x = np.empty((len(training_set), 2), np.float)
for i in range(len(training_set)):
    x[i] = training_set[i][0]
    y[i] = training_set[i][1]
history = []

# 计算Gram矩阵
def cal_gram():
    g = np.empty((len(training_set), len(training_set)), np.int)
    for i in range(len(training_set)):
        for j in range(len(training_set)):
            g[i][j] = np.dot(training_set[i][0], training_set[j][0])
    return g

# 更新变量:a,b
def update(i):
    global a, b, eta
    a[i] += eta
    b = b + y[i]
    history.append([np.dot(a * y, x), b])

# 计算判断条件
def cal(i):
    global a, b, x, y
    res = np.dot(a * y, Gram[i])
    res = (res + b) * y[i]
    return res

# 判断超平面是否正确
def check():
    global a, b, x, y
    flag = False
    for i in range(len(training_set)):
        if cal(i) <= 0:
            flag = True
            update(i)
    if not flag:
        w = np.dot(a * y, x)
        print("RESULT: w: " + str(w) + " b: " + str(b))
        return False
    return True


if __name__ == "__main__":
    Gram = cal_gram()
    for i in range(1000):
        if not check(): break

    # first set up the figure, the axis, and the plot element we want to animate
    fig = plt.figure()
    ax = plt.axes(xlim=(0, 4), ylim=(-2, 2))
    line, = ax.plot([], [], 'g', lw=2)
    label = ax.text([], [], '')

    # initialization function: plot the background of each frame
    def init():
        line.set_data([], [])
        x, y, x_, y_ = [], [], [], []
        for p in training_set:
            # 正实例
            if p[1] > 0:
                x.append(p[0][0])
                y.append(p[0][1])
            # 负实例
            else:
                x_.append(p[0][0])
                y_.append(p[0][1])

        plt.plot(x,y,'bo')
        plt.plot(x_,y_,'rx')
        plt.legend(['超平面','正实例', '负实例'], loc='upper left')
        plt.axis([-1, 5, -1, 5])
        plt.grid(True)
        plt.xlabel('x')
        plt.ylabel('y')

        plt.title('感知机算法实验——对偶形式')
        return line, label

    # animation function.  this is called sequentially
    def animate(i):
        global history, ax, line, label

        w = history[i][0]
        b = history[i][1]

        hyperplane = "{0}*x1+{1}*x2+{2}=0".format(w[0],w[1],b)

        if w[1] == 0:
            line.set_data([0, 0], [0, 0])
            label.set_text("第%d次运算:"%(i+1)+"超平面:"+hyperplane)
            label.set_position([0, 0])
            return line, label
        x1 = -6
        y1 = -(b + w[0] * x1) / w[1]
        x2 = 6
        y2 = -(b + w[0] * x2) / w[1]
        line.set_data([x1, x2], [y1, y2])
        x1 = 0
        y1 = -(b + w[0] * x1) / w[1]
        label.set_text("第%d次运算:"%(i+1)+"超平面:"+hyperplane)
        label.set_position([x1, y1])
        return line, label

    print(history)
    anim = animation.FuncAnimation(fig, animate, init_func=init, frames=len(history), interval=1000, repeat=True,blit=True)
    plt.show()
    anim.save('perceptron-animate2.gif', fps=2, writer='imagemagick')
```


## 感知机学习算法的收敛性

设训练数据集$T=\\{(x_1,y_1),(x_2,y_2), \cdots , (x_N,y_N)\\}是线性可分的,其中x_i \in \mathcal{X} = \mathcal{R}^n,y_i \in \mathcal{Y} = \\{+1,-1 \\},i=1,2,\cdots ,N;学习率\eta(0<\eta \leq 1);$,则：

(1) 存在满足条件$\Vert \hat{w}\_{opt} \Vert = 1的超平面 \hat{w}\_{opt} \cdot x + b\_{opt} =0 将训练数据集完全正确分开；且存在\gamma >0,对所有的 i=1,2,\cdots , N $ :

$$y\_i(\hat{w}]\_{opt} \cdot \hat{x}]\_i) = y\_i(w\_{opt} \cdot x\_i + b\_{opt}) \geq \gamma $$

(2) 令 $R = {max}\_{1 \leq i \leq N} \Vert \hat{x}\_i \Vert$,则感知机算法在训练集上的错误分类次数k满足不等式：

$$k \leq {\left( \frac{R}{\gamma} \right)}^2$$