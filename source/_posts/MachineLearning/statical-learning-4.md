---
title: 统计学习方法——朴素贝叶斯法
comments: true
toc: true
date: 2016-08-16 09:25:02
categories: MachineLearning
tags : 机器学习
keywords: 机器学习, 统计学习
mathjax: true
---

>**本节内容：**朴素贝叶斯(native Bates)是基于贝叶斯定理与特征条件独立假设的分类法。对于给定的训练数据集，首先基于特征条件独立假设学习输入/输出的联合概率密度；然后基于此模型，对给定的输入x，利用贝叶斯定理求出后验概率的最大的输出y。朴素贝叶斯实现简单，学习和预测的效率都很高，是一种常用的方法。本节对应于统计学习方法第四章的内容。


<!-- more -->

## 朴素贝叶斯法的学习与分类

### 基本方法

设输入空间$\mathcal{X} \subseteq R^n$为n维向量的集合，输出空间为类标记集合$\mathcal{Y} = \\{c\_1,c\_2, \cdots ,c\_K \\}$.输入为特征向量$x \in \mathcal{X}$,输出为类标记$y \in \mathcal{Y},X$是定义在输入空间$\mathcal{X}$上的随机变量，$\mathcal{Y}$是定义在输出空间Y上的随机变量.P(X,Y)是X和Y的联合概率分布。训练数据集：

$$T = \\{(x\_1,y\_1),(x\_2,y\_2), \cdots , (x\_N,y\_N) \\}$$

由P(X,Y)独立同分布产生。朴素贝叶斯法通过训练数据集学习联合概率分布P(X,Y),具体地，学习以下先验概率密度及条件概率密度。先验概率密度：

$$P(Y=c\_k) , k= 1,2, \cdots ,K$$

条件概率密度：

$$P(X=x|Y=c\_k) = P(X^{(1)}=x^{(1)},\cdots , X^{(n)}=x^{(n)} |Y=c\_k), k= 1,2, \cdots ,K$$

于是学习到联合概率分布P(X,Y).

朴素贝叶斯法对条件概率分布作了条件独立性的假设：

$$P(X=x|Y=c\_k) = P(X^{(1)}=x^{(1)},\cdots , X^{(n)}=x^{(n)} |Y=c\_k)=\prod_{j=1}^n P(X^{(j)}=x^{(j)} | Y = c\_k)$$

朴素贝叶斯法实际上学习到的是生成数据的机制，所以属于生成模型。条件独立性假设是说用于分类的特征在类确定的情况都是条件独立的，这一假设会使得朴素贝叶斯法变得简单，但有时会牺牲一定的分类准确率。

朴素贝叶斯法时，对给定的输入x，通过学习到的模型计算后验概率分布$P(Y=c\_k|X=x)$，将后验概率最大的类作为x的类输出，后验概率计算根据贝叶斯定理进行：

$$P(Y=c\_k|X=x)= \frac{P(X=x|Y=c\_k)P(Y=c\_k)}{\sum\_k P(X=x|Y=c\_k)P(Y=c\_k)}$$

将条件独立性假设公式带入上式，得：

$$P(Y=c\_k|X=x)= \frac{P(Y=c\_k)\prod\_jP(X^{(j)}=x^{(j)}|Y=c\_k)}{\sum\_k P(Y=c\_k)\prod\_jP(X^{(j)}=x^{(j)}s|Y=c\_k)} , k=1,2, \cdots ,K$$

考虑到分母对所有的$c\_k$都相同，朴素贝叶斯分类器可表示为：

$$y =  arg \max\_{c\_k}P(Y=c\_k)\prod\_jP(X^{(j)}=x^{(j)}|Y=c\_k)$$

### 后验概率最大化的含义

朴素贝叶斯法将实例分到后验概率最大的类中，这等价于期望风险最小化，假设选择0-1损失函数：

$$
L(Y,f(X))=
\\begin{cases}
0& Y=f(x)\\\\
1& Y \neq f(X)
\\end{cases}
$$

式中f(X)是分类决策函数，这时期望风险函数为：

$$R\_{exp}(f)=E[L(Y,f(X))]$$

期望是对联合分布P(X,Y)取的，由此取期望：

$$R\_{exp}(f)=E\_X\sum_{k=1}^{K}[L(c\_k,f(X))]P(c\_k|X)$$

为了使期望风险最小化，只需对X=x逐个最小化，由此得到：

$$f(x)= \arg \min\_{y \in \mathcal{Y}} P(y=c\_k|X=x)$$

## 朴素贝叶斯法的参数估计

### 极大似然估计

在朴素贝叶斯法中，学习意味着估计$P(Y=c\_k)和P(X^{(j)}=x^{(j)}| Y=c\_k)$,可以应用极大似然估计法估计相应的概率。先验概率$P(Y=c\_k)$的极大似然估计是：

$$P(Y=c\_k)=\frac{\sum_{i=1}^NI(y\_i=c\_k)}{N},k=1,2,\cdots ,K$$

设第j个特征$x^{(j)}$可能取值的集合为$\\{a\_{j1},a\_{j2},\cdots ,a\_{jS\_j}\\}$,条件概率$P(X^{(j)}=a\_{jl}|Y=c\_k)$的极大似然估计是：

$$P(X^{(j)}=a\_{jl}|Y=c\_k)=\frac{\sum_{i=1}^NI(x\_i^{(j)}=a\_{jl},y\_i=c\_k)}{\sum\_{i=1}^NI(y\_i=c\_k)}, j=1,2,\cdots ,n;l=1,2,\cdots ,S\_j;k=1,2,\cdots ,K$$

式中，$x\_i^{(j)}$是第i个样本的第j个特征；$a\_{jl}$是第j个特征可能取的第l个值；I为指示函数。

### 学习与分类方法

**朴素贝叶斯算法**

输入：训练数据集$T=\\{(x\_1,y\_1),(x\_2,y\_2),\cdots ,(x\_N,y\_N)\\}$,其中$x\_i=(x\_i^{(1)},\cdots , x\_i^{(n)})^T,x\_i^{(j)}$是第i个样本的第j个特征，$x\_i^{(j)} \in \\{a\_{j1},a\_{j2}, \cdots ,a\_{jS\_j}\\},a\_{jl}$是第j个特征可能取的第l个值，$j=1,2,\cdots ,n;l=1,2, \cdots ,S\_j;y\_i \in \\{c\_1,c\_2,\cdots ,c\_K\\}$;实例x;
输出：实例x的分类

(1) 计算先验概率及条件概率

$$P(Y=c\_k)=\frac{\sum\_{i=1}^NI(y\_i=c\_k)}{N},k=1,2,\cdots ,K$$

$$P(X^{(j)}=a\_{jl} | Y= c\_k)=\frac{\sum_{i=1}^NY(x\_i^{(j)}=a\_{jl},y\_i=c\_k)}{\sum\_{i=1}^NI(y\_i=c\_k)},j=1,2,\cdots,n;l=1,2,\cdots,S\_j;k=1,2,\cdots,K$$

(2) 对于给定的实例$x=(x^{(1)},x^{(2)},\cdots ,x^{(n)})^T$,计算：

$$P(Y=c\_k)\prod\_jP(X^{(j)}=x^{(j)}s|Y=c\_k) , k=1,2, \cdots ,K$$

(3) 确定实例x的分类

$$y =  arg \max\_{c\_k}P(Y=c\_k)\prod\_jP(X^{(j)}=x^{(j)}|Y=c\_k)$$

### 例子

试由下表的训练数据学习一个朴素贝叶斯分类器并确定$x=(2,S)^T$的类标记y，表中$X^{(1)},X^{(2)}$为特征，取值的集合分别为$A\_1=\\{1,2,3\\},A\_2=\\{S,M,L\\}$,Y为类标记，$Y \in C =\\{1,-1\\}$

| |1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|
|--- |--- |--- |--- |--- |--- |--- |--- |--- |--- |--- |--- |--- |--- |--- |--- |
|$X^{(1)}$|1|1|1|1|1|2|2|2|2|2|3|3|3|3|3|
|$X^{(2)}$|S|M|M|S|S|S|M|M|L|L|L|M|M|L|L|
|$Y$|-1|-1|1|1|-1|-1|-1|1|1|1|1|1|1|1|-1|

根据朴素贝叶斯分类器，容易计算下列概率：

$P(Y=1)=9/15,p(Y=-1)=6/15$

$P(X^{(1)}=1|Y=1)=2/9,P(X^{(1)}=2|Y=1)=3/9,P(X^{(1)}=3|Y=1)=4/9$

$P(X^{(2)}=S|Y=1)=1/9,P(X^{(2)}=M|Y=1)=4/9,P(X^{(2)}=L|Y=1)=4/9$

$P(X^{(1)}=1|Y=-1)=3/6,P(X^{(1)}=2|Y=-1)=2/6,P(X^{(1)}=3|Y=-1)=1/6$

$P(X^{(2)}=S|Y=-1)=3/6,P(X^{(2)}=M|Y=-1)=2/6,P(X^{(2)}=L|Y=-1)=1/6$

于是，对于给定的$x=(2,S)^T$,计算：

$P(Y=1)P(X^{(1)}=2|Y=1)P(X^{(2)}=S|Y=1)=9/15\cdot 3/9 \cdot 1/9=1/45$

$P(Y=-1)P(X^{(1)}=2|Y=-1)P(X^{(2)}=S|Y=-1)=6/15\cdot 2/6 \cdot 3/6=1/15$

故$y=-1$


### 贝叶斯估计

用极大似然估计可能会出现所要估计的概率的值为0的情况，这时会影响到后验概率的计算结果，使分类出现偏差，解决这一问题的方法是采用贝叶斯估计。具体地，条件概率的贝叶斯估计是

$$P\_{\lambda}(X^{(j)}=a\_{jl}|Y=c\_k)=\frac{\sum\_{i=1}^NI(x\_i^{(j)},y\_i=c\_k)+\lambda}{\sum\_{i=1}^NI(y\_i=c\_k)+S\_j\lambda}$$

式中$\lambda \geq 0$，等价于在随机变量各个取值的频数上赋予一个正数λ>0,当λ=0时就是极大似然估计。常取λ=1，这时称为拉普拉斯平滑(Laplace smoothing)。显然，对任何$l=1,2,\cdots ,S\_j,k=1,2, \cdots ,K$，有

$$P\_{\lambda}(X^{(j)}=a\_{jl}|Y=c\_k)>0$$

$$\sum\_{l=1}^{S\_j}P(X^{(j)}=a\_{jl}|Y=c\_k)=1$$

表明上式确为一种概率分布，同样先验概率的贝叶斯估计是

$$P\_{\lambda}(Y=c\_k)=\frac{\sum\_{i=1}^NI(y\_i=c\_k)+\lambda}{N+K\lambda}$$

### 综合：python实现简单情感极性分析器

``` python
# -*- coding:utf-8 -*-
# Filename: Bayes.py
from math import log, exp


class LaplaceEstimate(object):
    """
    拉普拉斯平滑处理的贝叶斯估计
    """

    def __init__(self):
        self.d = {}  # [词-词频]的map
        self.total = 0.0  # 全部词的词频
        self.none = 1  # 当一个词不存在的时候，它的词频（等于0+1）

    def exists(self, key):
        return key in self.d

    def getsum(self):
        return self.total

    def get(self, key):
        if not self.exists(key):
            return False, self.none
        return True, self.d[key]

    def getprob(self, key):
        """
        估计先验概率
        :param key: 词
        :return: 概率
        """
        return float(self.get(key)[1]) / self.total

    def samples(self):
        """
        获取全部样本
        :return:
        """
        return self.d.keys()

    def add(self, key, value):
        self.total += value
        if not self.exists(key):
            self.d[key] = 1
            self.total += 1
        self.d[key] += value


class Bayes(object):
    def __init__(self):
        self.d = {}  # [标签, 概率] map
        self.total = 0  # 全部词频


    def train(self, data):
        for d in data:  # d是[[词链表], 标签]
            c = d[1]  # c是分类
            if c not in self.d:
                self.d[c] = LaplaceEstimate()  # d[c]是概率统计工具
            for word in d[0]:
                self.d[c].add(word, 1)  # 统计词频
        self.total = sum(map(lambda x: self.d[x].getsum(), self.d.keys()))

    def classify(self, x):
        tmp = {}
        for c in self.d:  # 分类
            tmp[c] = log(self.d[c].getsum()) - log(self.total)  # P(Y=ck)
            for word in x:
                tmp[c] += log(self.d[c].getprob(word))          # P(Xj=xj | Y=ck)
        ret, prob = 0, 0
        for c in self.d:
            now = 0
            try:
                for otherc in self.d:
                    now += exp(tmp[otherc] - tmp[c])            # 将对数还原为1/p
                now = 1 / now
            except OverflowError:
                now = 0
            if now > prob:
                ret, prob = c, now
        return (ret, prob)


class Sentiment(object):
    def __init__(self):
        self.classifier = Bayes()

    def segment(self, sent):
        words = sent.split(' ')
        return words

    def train(self, neg_docs, pos_docs):
        data = []
        for sent in neg_docs:
            data.append([self.segment(sent), '消极'])
        for sent in pos_docs:
            data.append([self.segment(sent), '积极'])
        self.classifier.train(data)

    def classify(self, sent):

        return self.classifier.classify(self.segment(sent))

s = Sentiment()
s.train(['糟糕', '好 差劲','坏','好 坏'], ['优秀', '很 好','棒','好 人']) # 空格分词

print(s.classify("好 棒"))
```

输出结果:

``` python
('积极', 0.6451612903225805)
```

