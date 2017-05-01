---
title: 数据挖掘——试卷分析
comments: true
toc: true
date: 2016-12-27 14:25:02
categories: Course
tags : 数据挖掘
keywords: 数据挖掘, 南京大学
mathjax: true
---

>**本节内容：**2015年秋南京大学计算机系数据挖掘期末试卷分析。


<!-- more -->

## PCA(3 points)

> Given n data points $x\_1,\ldots,x\_n$, where $x\_i \in \mathcal{R}^d$. Describe how to find the top **k** principle components by **SVD**.

解答：SVD奇异值分解
- 对X进行奇异值分解：$X = U\Sigma V^T = \sum\_{i=1}^d \sigma\_iu\_iv\_i^T$
- 取X的k个最大的左奇异向量：$u\_1,u\_2,\ldots,u\_k$
- x的新坐标：$U\_k^Tx = [u\_1^Tx, u\_2^Tx, \ldots, u\_k^Tx] \in \mathcal{R}^k, U\_k = [u\_1, u\_2,\ldots,u\_k] \in \mathcal{R}^{d*k}$
- X的新坐标：$U\_k^TX=U\_k^TU\_r\Sigma\_rV\_r^T = \Sigma\_kV\_k^T$

## Association Pattern Mining(2 points)

> The **downward closure property (i.e.,every subset of frequent itemset is also frequcent)** is leveraged to design efficient algorithms for association pattern mining. Why does this property hold?

解答：由于支持单调性，一个项集I包含在一个交易中，那么它的所有子集也包含在这个交易中，即子集的支持度不小于父集的支持度。

## NMF(3 points)

> Suppose we want to find a rank-k approximation of matrix $X \in \mathcal{R}^{d*n}$ by nonnegative matrix factorization. What is the optimization problem? Is it convex?

解答：
- 优化问题：$\min\_{U \in \mathcal{R}^{d\*k}, V \in \mathcal{R}^{v*k}} \ \Vert X - UV^T \Vert\_F^2 $， $s.t. \ U \ge 0, V \ge 0$
- 非负矩阵分解是非凸的

## SVM(6 points)

> Given a set of training data $(x\_1,y\_1),\ldots,(x\_n,y\_n)$, where $x\_i \in \mathcal{R}^d$ and $y\_i \in \{\pm 1\}$. The primal problem of SVM without intercept is given by: $$\min\_{w \in \mathcal{R}^d} \sum\_{i=1}^m \max(0,1-y\_iw^Tx\_i) + \frac{\lambda}{2} \Vert w \Vert\_2^2$$. Show the derivation of the dual problem of SVM.

>Hints: Let $\mathcal{l}(x) = max(0,1-x)$ be the hinge loss. Then, its conjugate function id given by
$$ \mathcal{l}^\*(y) = sup\_x(yx-\mathcal{l}(x)) = \begin{cases} y& -1 \le y \le 0 \\\\
 \infty& \ otherwise
 \end{cases}$$

 解：SVM对偶问题的推导，无截距$b\_0$

 - 由上述定义的hinge loss及其共轭函数知，原优化问题简化为：$\min\_{w \in R^d} \sum\_{i=1}^n \mathcal{l}(y\_iw^Tx\_i) + \frac{\lambda}{2}\Vert w \Vert\_2^2$,等价于：
$$\sum\_{i=1}^n \mathcal{l}(u\_i) + \frac{\lambda}{2} \Vert w \Vert\_2^2, s.t. \ u\_i = y\_iw^Tx\_i, i = 1, \ldots, n$$

- 由拉格朗日乘法，得：$L(w,u,v) = \sum\_{i=1}^n \mathcal{l}(u\_i) + \frac{\lambda}{2} \Vert w \Vert\_2^2 + \sum\_{i=1}^n v\_i(u\_i - y\_iw^Tx\_i)$,其对偶问题：


$$\begin{array} {lcl}
    g(v) &=& \inf\_{w,u} L(w,u,v) \\\\
         &=& \inf\_{w,u} \sum\_{i=1}^n \mathcal{l}(u\_i) + \frac{\lambda}{2} \Vert w \Vert\_2^2 + \sum\_{i=1}^n v\_i(u\_i - y\_iw^Tx\_i) \\\\
         &=& \inf\_{w,u} \sum\_{i=1}^n(\mathcal{l}(u\_i) + v\_iu\_i) + (\frac{\lambda}{2} \Vert w \Vert\_2^2 - w^T\sum\_{i=1}^nv\_iy\_ix\_i)
\end{array}$$

    依次最小化w,u:

- $\inf\_{u\_i}(\mathcal{l}(u\_i) + v\_iu\_i) = -\sup\_{u\_i}(-v\_iu\_i - l(u\_i)) = - \mathcal{l}^\* (-v\_i) = v\_i $, if $0 \le v\_i \le 1$
- $\nabla\_w L(w,u,v) = \lambda w - \sum\_{i=1}^nv\_iy\_ix\_i)$, 得：$w = \frac{1}{\lambda}  \sum\_{i=1}^nv\_iy\_ix\_i$
- 最后， $g(v) = \sum\_{i=1}^n v\_i - \frac{1}{2 \lambda}\sum\_{i=1}^n\sum\_{j=1}^n v\_iv\_jy\_iy\_jx\_i^Tx\_j$，得到对偶问题：
$$\max\_{v \in R^n} \sum\_{i=1}^n v\_i - \frac{1}{2 \lambda}\sum\_{i=1}^n\sum\_{j=1}^n v\_iv\_jy\_iy\_jx\_i^Tx\_j, \ s.t.\ 0 \le v\_i \le 1, i=1, \ldots,n$$


 ## Ensemble(2 points)

 > Ensemble analysis is used to reduce the bias or variance of the classification process. Which of them is reduced by bagging/boosting?

 解答：

 a) Bagging aims to reduce the: **variance**(代表性算法：随机森林，随机化的决策树模型，每次随机选择一定大小的特征)

 b) Boosting aims to reduce the:**bias**(代表性算法：AdaBoost，每个训练实例都有个权重，分类错误的实例会赋予一个更大的权重)

 ## Ridge Regression(5 points)

 > Given a set of training data $(x\_1,y\_1),\ldots,(x\_n,y\_n)$, where $x\_i \in \mathcal{R}^d$ and $y\_i \in \mathcal{R}$. Our goal is to learn a linear model $f(x) = x^Tw + b$ to predict the label $y \in \mathcal{R}$ of an instance $x \in \mathcal{R}^d$

a) Show the optimization problem of the ridge regression for learning $w \in \mathcal{R}^d$ and $b \in \mathcal{R}$.
Notations:$X = [x\_1,\ldots,x\_n] \in \mathcal{R}^{d\*n}, y = [y\_1, \ldots, y\_n]$

b) Derive the optimal solution $w\_\*$, and $b\_\*$ of the above problem.
Notations: I is the identity matrix, and $H = I - \frac{1}{n}1\_n1\_n^T$ is the centering matrix. Hints:$\frac{\partial \Vert u - A^Tw \Vert\_2^2}{\partial w} = 2A(A^Tw-u)$

解答：

1. 优化问题：$\min\_{b \in R,w \in R^d} \Vert y - Xw - 1\_Nb \Vert\_2^2 + \lambda \Vert w \Vert\_2^2$, 其中：$1\_N = [1,\ldots,1]^T \in R^d$

2. - 对上式$b$进行求导，并令倒数为0：$-2*1\_N^T(y-Xw -1\_Nb)=0, b  = \frac{1}{N}1\_N^T(y-Xw)$
   - 对上式$w$进行求导，并令倒数为0：$2*X^T(Xw -y + 1\_Nb) + 2 \lambda w=0, (X^T(I-\frac{1}{N}1\_N1\_N^T)X + \lambda I)w=X^T(I-\frac{1}{N}1\_N1\_N^T)y$
   - 令$H = I - \frac{1}{N}1\_N1\_N^T$为中心矩阵，则得到：$w\_\* = (X^THX + \lambda I)^{-1}X^THy,b\_\* = \frac{1}{N}1\_N^T(y-Xw\_\*)$

## Advanced Classification(4 points)

> Give a brief introduction of **semi-suppervised learning** and **active learning**

解答：半监督学习和主动学习

- **semi-suppervised learning**：
    - 标记数据的代价昂贵并且难于获得；无标签数据通常是大量可得到的；无标签数据是有用的：无标签数据可以用来评估数据的低维流型结构和特征的联合概率分布。
    - 相关算法：
        - 元算法：使用任何现有的算法作为子程序，这类算法主要有Self-training和Co-training。
        - 具体算法：半监督贝叶斯分类器，转导支持向量机，基于图的半监督学习算法。
    - 数据要求：数据的类别特征应该近似和它的聚类特征匹配；在实际应用中，当有标签的数据非常少时效率很高。
- **active learning**：
    - 主动学习是半监督机器学习的一个特例，在主动学习中，一个学习算法可以主动地提出一些标注请求，将一些经过筛选的数据提交给专家进行标注。
    - 两种查询系统：选择性取样和基于池的采样。
    - 种类：基于异构的模型、基于性能的模型、基于代表的模型。

## Collaborative Filtering(5 points)

> A merchant has an n*d ratings matrix D representing the preferences of n customers across d items. It is assumed that the matrix is sparse, and therefore each customer may have bought only a few items. Please provide one approach that the utilizes the rating matrix D to make recommendations to customers.

解答：总的方法如下：
- 基于邻居的方法：基于用户的评级相似性、基于商品的评级相似性
- 基于图的方法：
- 聚类方法：自适应k-means聚类，自适应协同聚类
- 潜伏因子模型：奇异值分解、矩阵分解、矩阵填充

本题使用Matrix Completion方法：$\min\_{X \in R^{n\*d}} \Vert X \Vert\_\*, s.t. \ X\_{ij} = D\_{ij} \in \Omega$。对矩阵D进行低秩分解，用$U\*V^T$来逼近D，用于填充，其中$D \in R^{n\*d}, U \in R^{n\*r}, V \in R^{d\*r}$, 即$U*V^T$得到近似矩阵M来填充D上的缺失值。