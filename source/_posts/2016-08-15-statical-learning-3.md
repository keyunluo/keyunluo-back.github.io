---
title: 统计学习方法——K近邻算法
comments: true
toc: true
date: 2016-08-15 15:25:02
categories: MachineLearning
tags : 机器学习
keywords: 机器学习, 统计学习
mathjax: true
---

>**本节内容：**k近邻算法(k-nearest neighbor,k-NN)是一种基本的分类与回归算法，不具有显式的学习过程。k值选择、距离度量以及分类决策规则是k近邻算法的三个基本要素。本节对应于统计学习方法第三章的内容。


<!-- more -->

## K近邻算法

### 算法描述

给定一个训练数据集，对新的输入实例，在训练数据集中找到跟它最近的k个实例，根据这k个实例的类判断它自己的类（一般采用多数表决的方法）。

### 算法流程

输入： 训练数据集 $T=\\{(x_1,y_1),(x_2,y_2),\cdots ,(x_N,y_N)\\}, 其中，x_i \in \mathcal{X} \subseteq R^n 为实例的特征向量，y_i \in \mathcal{Y} = \\{c_1,c_2,\cdots ,c_K \\}为实例的类别，i = 1,2,\cdots ,N;实例特征向量x;$

输出：实例x所属的类别y

(1) 根据给定的距离度量，在训练集T中找出与x最邻近的k个点，涵盖这k个点的x的领域记为$N_k(x)$;

(2) 在$N_k(x)$中根据分类决策规则(如多数表决)决定x的类别y:

$$y = arg max\_{c\_j} \sum\_{x\_i \in N\_k(x)} I(y\_i = c\_j),i=1,2, \cdots ,N;j = 1,2, \cdots ,K$$

式中I为指示函数，即当$y_i=c_i$时I为1，否则I为0.

k近邻算法的特殊情况是k=1的情形，称为最近邻算法。对于输入的实例点x，最近邻法将训练数据集中与x最邻近的类作为x的类。

## K近邻模型

模型有3个要素——距离度量方法、k值的选择和分类决策规则。

### 模型

当3要素确定的时候，对任何实例（训练或输入），它所属的类都是确定的，相当于将特征空间分为一些子空间。

![k近邻模型](/resource/blog/2016-08/k近邻模型.jpg)

### 距离度量

对n维实数向量空间$R^n$，经常用$L_p$距离或曼哈顿$Minkowski$距离。

设特征空间$\mathcal{X}$是n维实数向量空间$R^n,x_i,x_j \in \mathcal{X}, x_i = (x_i^{(1)},x_i^{(2)},\cdots , x_i^{(n)})^T,x_j = (x_j^{(1)},x_j^{(2)},\cdots , x_j^{(n)})^T,x_i,x_j的L_p距离定义为：$

$$L\_p(x\_i,x\_j)=\left( \sum\_{l=1}^{n} \vert x\_i^{(l)}-x\_j^{(l)} \vert ^p \right)^{\frac{1}{p}}$$

这里$p \geq 1$,当p=2时，称为欧氏距离(Euclidean distance)，即：

$$L\_2(x\_i,x\_j)=\left( \sum\_{l=1}^{n} \vert x\_i^{(l)}-x\_j^{(l)} \vert ^2 \right ) ^{\frac{1}{2}}$$

当p=1时，称为曼哈顿距离(Manhattan distance),即：

$$L\_1(x\_i,x\_j)=\sum\_{l=1}^{n} \vert x\_i^{(l)}-x\_j^{(l)} \vert $$

当$p=\infty$时，它是各个坐标距离的最大值,即：

$$L\_{\infty}(x\_i,x\_j)=\max \vert x\_i^{(l)}-x\_j^{(l)} \vert $$

### k值的选择

k较小，容易被噪声影响，发生过拟合。

k较大，较远的训练实例也会对预测起作用，容易发生错误。

在应用中，k值一般取一个比较小的数值，通常采用交叉验证法来选取最优的k值。

### 分类决策规则

使用0-1损失函数衡量，那么误分类率是：

$$\frac{1}{k} \sum\_{x\_i \in N\_k(x)} I(y\_i \not= c\_j) = 1- \frac{1}{k}\sum\_{x\_i \in N\_k(x)} I(y\_i = c\_j)$$

$N\_k$是近邻集合，要使左边最小，右边$\sum\_{x\_i \in N\_k(x)} I(y\_i = c\_j)$必须最大，所以多数表决等价于经验风险最小化。

## kd树

算法核心在于怎么快速搜索k个近邻出来，朴素做法是线性扫描，不可取，这里介绍的方法是kd树。

### 构造kd树

对数据集T中的子集S初始化S=T，取当前节点node=root取维数的序数i=0，对S递归执行：

找出S的第i维的中位数对应的点，通过该点，且垂直于第i维坐标轴做一个超平面。该点加入node的子节点。该超平面将空间分为两个部分，对这两个部分分别重复此操作（S=S'，++i，node=current），直到不可再分。

#### 算法流程

输入：k维空间数据集 $T=\\{x_1,x_2,...,x_N\\}$,其中$x_i=(x_i^{(1)},x_i^{(2)},\cdots ,x_i^{(k)})^T,i=1,2,\cdots ,N$

输出： kd树

(1) 开始：构造根节点，根节点对应于包含T的k维空间的超矩形区域：选择$x^{(1)}$为坐标轴，以T中所有实例的$x^{(1)}$坐标的中位数为切分点，将根节点对应的超矩形区域切分为两个子区域。切分由通过切分点并与坐标轴$x^{(1)}$垂直的超平面实现。

由根节点生成深度为1的左右子节点：左子节点对应坐标$x^{(1)}$小于切分点的子区域，右子节点对应坐标$x^{(1)}$大于切分点的子区域。

将落在切分超平面的实例点保存在根节点。

(2) 重复：对深度为j的节点，选择$x^{(l)}$为切分的坐标轴，$l=j(mod k) +1$,以该节点的区域中所有实例的$x^{(l)}$坐标的中位数为切分点，将该节点对应的超矩形区域切分为两个子区域，切分由通过切分点并与坐标轴$x^{(l)}$垂直的超平面实现。

由该节点生成深度为j+1的左右子节点：左子节点对应坐标$x^{(l)}$小于切分点的子区域，右子节点对应坐标$x^{(l)}$大于切分点的子区域。

将落在切分超平面的实例点保存在根节点。

(3) 直到两个子区域没有实例存在时停止，从而形成kd树的区域划分。


#### 例子

给定一个二维空间的数据集：$T=\\{(2,3)^T,(5,4)^T,(9,6)^T,(4,7)^T,(8,1)^T,(7,2)^T\\}$,
构造一棵平衡kd树。

#### python实现

``` python
T = [[2, 3], [5, 4], [9, 6], [4, 7], [8, 1], [7, 2]]

class node:
    def __init__(self, point):
        self.left = None
        self.right = None
        self.point = point
        pass

def median(lst):
    m = int(len(lst) / 2)
    return lst[m], m

def build_kdtree(data, d):
    data = sorted(data, key=lambda x: x[d])
    p, m = median(data)
    tree = node(p)

    del data[m]
    print(data, p)

    if m > 0: tree.left = build_kdtree(data[:m], not d)
    if len(data) > 1: tree.right = build_kdtree(data[m:], not d)
    return tree

kd_tree = build_kdtree(T, 0)
print(kd_tree)
```

#### 可视化

``` python
# -*- coding:utf-8 -*-
# Filename: kdtree.py

import copy
import itertools
from matplotlib import pyplot as plt
from matplotlib.patches import Rectangle
from matplotlib import animation

T = [[2, 3], [5, 4], [9, 6], [4, 7], [8, 1], [7, 2]]


def draw_point(data):
    X, Y = [], []
    for p in data:
        X.append(p[0])
        Y.append(p[1])
    plt.plot(X, Y, 'bo')


def draw_line(xy_list):
    for xy in xy_list:
        x, y = xy
        plt.plot(x, y, 'g', lw=2)


def draw_square(square_list):
    currentAxis = plt.gca()
    colors = itertools.cycle(["r", "b", "g", "c", "m", "y", '#EB70AA', '#0099FF'])
    for square in square_list:
        currentAxis.add_patch(
            Rectangle((square[0][0], square[0][1]), square[1][0] - square[0][0], square[1][1] - square[0][1],
                      color=next(colors)))


def median(lst):
    m = int(len(lst) / 2)
    return lst[m], m


history_quare = []


def build_kdtree(data, d, square):
    history_quare.append(square)
    data = sorted(data, key=lambda x: x[d])
    p, m = median(data)

    del data[m]
    print(data, p)

    if m >= 0:
        sub_square = copy.deepcopy(square)
        if d == 0:
            sub_square[1][0] = p[0]
        else:
            sub_square[1][1] = p[1]
        history_quare.append(sub_square)
        if m > 0: build_kdtree(data[:m], not d, sub_square)
    if len(data) > 1:
        sub_square = copy.deepcopy(square)
        if d == 0:
            sub_square[0][0] = p[0]
        else:
            sub_square[0][1] = p[1]
        build_kdtree(data[m:], not d, sub_square)


build_kdtree(T, 0, [[0, 0], [10, 10]])
print(history_quare)


# draw an animation to show how it works, the data comes from history
# first set up the figure, the axis, and the plot element we want to animate
fig = plt.figure()
ax = plt.axes(xlim=(0, 2), ylim=(-2, 2))
line, = ax.plot([], [], 'g', lw=2)
label = ax.text([], [], '')

# initialization function: plot the background of each frame
def init():
    plt.axis([0, 10, 0, 10])
    plt.grid(True)
    plt.xlabel('x_1')
    plt.ylabel('x_2')
    plt.title('构造KD树')
    draw_point(T)


currentAxis = plt.gca()
colors = itertools.cycle(["#FF6633", "g", "#3366FF", "c", "m", "y", '#EB70AA', '#0099FF', '#66FFFF'])

# animation function.  this is called sequentially
def animate(i):
    square = history_quare[i]
    currentAxis.add_patch(
        Rectangle((square[0][0], square[0][1]), square[1][0] - square[0][0], square[1][1] - square[0][1],
                  color=next(colors)))
    return

# call the animator.  blit=true means only re-draw the parts that have changed.
anim = animation.FuncAnimation(fig, animate, init_func=init, frames=len(history_quare), interval=1000, repeat=False,
                               blit=False)
plt.show()
anim.save('kdtree_build.gif', fps=2, writer='imagemagick')
```
![构建kd树](/resource/blog/2016-08/kdtree_build.gif)

### 搜索kd树

#### 算法流程

搜索跟二叉树一样，是一个递归的过程。先找到目标点的插入位置，然后往上走，逐步用自己到目标点的距离画个超球体，用超球体圈住的点来更新最近邻（或k最近邻）。

输入：已构造的kd树，目标点x

输出：x的最近邻

(1) 在kd树中找到包含目标点x的叶节点：从根节点出发，递归的向下访问kd树。若目标点x当前维的坐标小于切分点的坐标，则移动到左子节点，否则移动到右子节点。直到子节点为叶节点为止。

(2) 以此叶节点为“当前最近点”

(3) 递归的向上回退，在每个节点进行如下操作：

- 如果该节点保存的实例点比当前最近点距离目标点更近，则以该实例点为“当前最近点”
- 当前最近点一定存在于该节点一个子节点对应的区域，检查该子节点的父节点的另一子节点对应的区域是否有更近的点。具体的，检查另一子节点对应的区域是否以目标点为球心、以目标点与“当前最近点”间的距离为半径的超球体相交。如果相交，可能在另一个子节点对应的区域内存在距离目标点更近的点，移动到另一个子节点，接着，递归地进行最近邻搜索。如果不相交，向上回退

(4) 当回退到根节点时，搜索结束。最后的“当前最近点”即为x的最近邻点。

#### python实现

以最近邻为例，实现如下（本实现由于测试数据简单，没有做超球体与超立体相交的逻辑）：

``` python
# -*- coding:utf-8 -*-
# Filename: search_kdtree.py

T = [[2, 3], [5, 4], [9, 6], [4, 7], [8, 1], [7, 2]]


class node:
    def __init__(self, point):
        self.left = None
        self.right = None
        self.point = point
        self.parent = None
        pass

    def set_left(self, left):
        if left == None: pass
        left.parent = self
        self.left = left

    def set_right(self, right):
        if right == None: pass
        right.parent = self
        self.right = right


def median(lst):
    m = int(len(lst) / 2)
    return lst[m], m


def build_kdtree(data, d):
    data = sorted(data, key=lambda x: x[d])
    p, m = median(data)
    tree = node(p)

    del data[m]

    if m > 0: tree.set_left(build_kdtree(data[:m], not d))
    if len(data) > 1: tree.set_right(build_kdtree(data[m:], not d))
    return tree


def distance(a, b):
    print(a, b)
    return ((a[0] - b[0]) ** 2 + (a[1] - b[1]) ** 2) ** 0.5


def search_kdtree(tree, d, target):
    if target[d] < tree.point[d]:
        if tree.left != None:
            return search_kdtree(tree.left, not d, target)
    else:
        if tree.right != None:
            return search_kdtree(tree.right, not d, target)

    def update_best(t, best):
        if t == None: return
        t = t.point
        d = distance(t, target)
        if d < best[1]:
            best[1] = d
            best[0] = t


    best = [tree.point, 100000.0]
    while (tree.parent != None):
        update_best(tree.parent.left, best)
        update_best(tree.parent.right, best)
        tree = tree.parent
    return best[0]


kd_tree = build_kdtree(T, 0)
print(search_kdtree(kd_tree, 0, [9, 4]))
```

输出
``` python
[8, 1] [9, 4]
[5, 4] [9, 4]
[9, 6] [9, 4]
[9, 6]
```