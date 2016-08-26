---
title: 统计学习方法——决策树
comments: true
toc: true
date: 2016-08-18 09:25:02
categories: MachineLearning
tags : 机器学习
keywords: 机器学习, 统计学习
mathjax: true
---

>**本节内容：**决策树(decision tree)是一种基本的分类与回归方法。决策树模型呈树形结构，其主要优点是模型具有可读性，分类速度快，学习时，利用训练数据根据损失函数最小化的原则建立决策树模型，分类速度快。决策树的学习通常包括3个步骤：特征选择、决策树的生成和决策树的修建。本节对应于统计学习方法第五章的内容，主要学习ID3、C4.5和CART算法。


<!-- more -->

# 决策树模型与学习

## 决策树模型

分类决策树模型是一种描述对实例进行分类的树形结构。决策树由结点和有向边组成。结点有两种类型：内部节点和叶节点，内部节点表示一个特征或属性，叶节点表示一个类。

分类的时候，从根节点开始，当前节点设为根节点，当前节点必定是一种特征，根据实例的该特征的取值，向下移动，直到到达叶节点，将实例分到叶节点对应的类中。

## 决策树与if-then规则

决策树的属性结构其实对应着一个规则集合：由决策树的根节点到叶节点的每条路径构成的规则组成；路径上的内部特征对应着if条件，叶节点对应着then结论。决策树和规则集合是等效的，都具有一个重要的性质：互斥且完备。也就是说任何实例都被且仅被一条路径或规则覆盖。

## 决策树与条件概率分布

决策树还是给定特征条件下类的条件概率分布的一种退化表示（非等效，个人理解）。该条件分布定义在特征空间的划分上，特征空间被花费为互不相交的单元，每个单元定义一个类的概率分布就构成了一个条件概率分布。决策树的每条路径对应于划分中的一个单元。给定实例的特征X，一定落入某个划分，决策树选取该划分里最大概率的类作为结果输出。如图：

![决策树与条件概率密度](/resource/blog/2016-08/决策树与条件概率密度.jpg)

关于b图，我是这么理解的，将a图的基础上增加一个条件概率的维度P，代表在当前特征X的情况下，分类为+的后验概率。图中的方块有些地方完全没有，比如x2轴上[a2,1]这个区间，说明只要X落在这里，Y就一定是-的，同理对于[0,a1]和[0,a2]围起来的一定是+的。有些地方只有一半，比如x1轴上[a1,1]这个区间，说明决策树认为X落在这里，Y只有一半概率是+的，根据选择条件概率大的类别的原则，就认为Y是-的（因为不满足P(+)>0.5)。

## 决策树学习

决策树学习算法包含特征选择、决策树的生成与剪枝过程。决策树的学习算法一般是递归地选择最优特征，并用最优特征对数据集进行分割。开始时，构建根节点，选择最优特征，该特征有几种值就分割为几个子集，每个子集分别递归调用此方法，返回节点，返回的节点就是上一层的子节点。直到数据集为空，或者数据集只有一维特征为止。

基本骨架的Python实现：

``` python
def majorityCnt(classList):
    """
返回出现次数最多的分类名称
    :param classList: 类列表
    :return: 出现次数最多的类名称
    """
    classCount = {}  # 这是一个字典
    for vote in classList:
        if vote not in classCount.keys(): classCount[vote] = 0
        classCount[vote] += 1
    sortedClassCount = sorted(classCount.iteritems(), key=operator.itemgetter(1), reverse=True)
    return sortedClassCount[0][0]


def createTree(dataSet, labels, chooseBestFeatureToSplitFunc=chooseBestFeatureToSplitByID3):
    """
创建决策树
    :param dataSet:数据集
    :param labels:数据集每一维的名称
    :return:决策树
    """
    classList = [example[-1] for example in dataSet]  # 类别列表
    if classList.count(classList[0]) == len(classList):
        return classList[0]  # 当类别完全相同则停止继续划分
    if len(dataSet[0]) == 1:  # 当只有一个特征的时候，遍历完所有实例返回出现次数最多的类别
        return majorityCnt(classList)
    bestFeat = chooseBestFeatureToSplitFunc(dataSet)
    bestFeatLabel = labels[bestFeat]
    myTree = {bestFeatLabel: {}}
    del (labels[bestFeat])
    featValues = [example[bestFeat] for example in dataSet]
    uniqueVals = set(featValues)
    for value in uniqueVals:
        subLabels = labels[:]  # 复制操作
        myTree[bestFeatLabel][value] = createTree(splitDataSet(dataSet, bestFeat, value), subLabels)
    return myTree


```

由于决策树表示条件概率分布，所以高度不同的决策树对应不同复杂度的概率模型。最优决策树的生成是个NP问题，能实现的生成算法都是局部最优的，剪枝则是既定决策树下的全局最优。

# 特征选择

## 特征选择

样本通常有很多维特征，希望选择具有分类能力的特征。比如下表：

![贷款申请](/resource/blog/2016-08/贷款申请.jpg)

可以用Python建立数据集：

``` python
def createDataSet():
    """
创建数据集

    :return:
    """
    dataSet = [[u'青年', u'否', u'否', u'一般', u'拒绝'],
               [u'青年', u'否', u'否', u'好', u'拒绝'],
               [u'青年', u'是', u'否', u'好', u'同意'],
               [u'青年', u'是', u'是', u'一般', u'同意'],
               [u'青年', u'否', u'否', u'一般', u'拒绝'],
               [u'中年', u'否', u'否', u'一般', u'拒绝'],
               [u'中年', u'否', u'否', u'好', u'拒绝'],
               [u'中年', u'是', u'是', u'好', u'同意'],
               [u'中年', u'否', u'是', u'非常好', u'同意'],
               [u'中年', u'否', u'是', u'非常好', u'同意'],
               [u'老年', u'否', u'是', u'非常好', u'同意'],
               [u'老年', u'否', u'是', u'好', u'同意'],
               [u'老年', u'是', u'否', u'好', u'同意'],
               [u'老年', u'是', u'否', u'非常好', u'同意'],
               [u'老年', u'否', u'否', u'一般', u'拒绝'],
               ]
    labels = [u'年龄', u'有工作', u'有房子', u'信贷情况']
    # 返回数据集和每个维度的名称
    return dataSet, labels
```

也可以根据特征分割数据集：

``` python
def splitDataSet(dataSet, axis, value):
    """
按照给定特征划分数据集
    :param dataSet: 待划分的数据集
    :param axis: 划分数据集的特征的维度
    :param value: 特征的值
    :return: 符合该特征的所有实例（并且自动移除掉这维特征）
    """
    retDataSet = []
    for featVec in dataSet:
        if featVec[axis] == value:
            reducedFeatVec = featVec[:axis]  # 删掉这一维特征
            reducedFeatVec.extend(featVec[axis + 1:])
            retDataSet.append(reducedFeatVec)
    return retDataSet
```

## 信息增益

对于一个可能有n种取值的随机变量：$P(X=x\_i)=p\_i$,其熵为：$H(X)=-\sum\_{i=1}^np\_i\log p\_i$ ,另外，0log0=0,当对数的底为2时，熵的单位是bit，为自然对数时，单位是nat。

用Python实现信息熵（香农熵）：

``` python
def calcShannonEnt(dataSet):
    """
计算训练数据集中的Y随机变量的香农熵
    :param dataSet:
    :return:
    """
    numEntries = len(dataSet)  # 实例的个数
    labelCounts = {}
    for featVec in dataSet:  # 遍历每个实例，统计标签的频次
        currentLabel = featVec[-1]
        if currentLabel not in labelCounts.keys(): labelCounts[currentLabel] = 0
        labelCounts[currentLabel] += 1
    shannonEnt = 0.0
    for key in labelCounts:
        prob = float(labelCounts[key]) / numEntries
        shannonEnt -= prob * log(prob, 2)  # log base 2
    return shannonEnt

```

由定义知，X的熵与X的值无关，只与分布有关，所以也可以将X的熵记作H(p),即：

$$H(p)=-\sum\__{i=1}^np\_i\log p\_i$$

熵其实就是X的不确定性，从定义可以验证$0 \leq H(p) \leq \log n$

设随机变量(X,Y)，其联合分布为：

$$P(X=x\_i,Y=y\_i)=p\_{ij},i=1,2,\cdots,n;j=1,2,\cdots,m$$

条件熵H(Y|X)表示在已知随机变量X的条件下随机变量Y的不确定性，定义为在X给定的条件下，Y的概率分布对X的数学期望：

$$H(Y|X)=\sum\_{i=1}^np\_iH(Y|X=x\_i),p\_i=P(X=x\_i),i=1,2,\cdots,n$$

当上述定义式中的概率由数据估计（比如上一章提到的极大似然估计）得到时，所对应的熵和条件熵分别称为经验熵和经验条件熵。

Python实现条件熵的计算：

``` python
def calcConditionalEntropy(dataSet, i, featList, uniqueVals):
    '''
    计算X_i给定的条件下，Y的条件熵
    :param dataSet:数据集
    :param i:维度i
    :param featList: 数据集特征列表
    :param uniqueVals: 数据集特征集合
    :return:条件熵
    '''
    ce = 0.0
    for value in uniqueVals:
        subDataSet = splitDataSet(dataSet, i, value)
        prob = len(subDataSet) / float(len(dataSet))  # 极大似然估计概率
        ce += prob * calcShannonEnt(subDataSet)  # ∑pH(Y|X=xi) 条件熵的计算
    return ce
```

有了上述知识，就可以一句话说明什么叫信息增益了：信息增益表示得知特征X的信息而使类Y的信息的熵减少的程度。形式化的定义如下：

>特征A对训练数据集D的信息增益g(D,A),定义为集合D的经验熵H(D)与特征A给定条件下D的经验条件熵H(D|A)之差，即g(D|A)=H(D)-H(D|A),这个差又称为互信息，决策树学习中的信息增益等价于训练数据集中类与特征的互信息。

用Python计算信息增益：

``` python
def calcInformationGain(dataSet, baseEntropy, i):
    """
    计算信息增益
    :param dataSet:数据集
    :param baseEntropy:数据集中Y的信息熵
    :param i: 特征维度i
    :return: 特征i对数据集的信息增益g(dataSet|X_i)
    """
    featList = [example[i] for example in dataSet]  # 第i维特征列表
    uniqueVals = set(featList)  # 转换成集合
    newEntropy = calcConditionalEntropy(dataSet, i, featList, uniqueVals)
    infoGain = baseEntropy - newEntropy  # 信息增益，就是熵的减少，也就是不确定性的减少
    return infoGain
```

回到最初的问题，如何判断一个特征的分类能力呢？信息增益大的特征具有更强的分类能力。只要计算出各个特征的信息增益，找出最大的那一个就行。

## 信息增益的算法

输入：训练数据集D和特征A；

输出：特征A对训练数据集D的信息增益g(D,A);

(1) 计算数据集D的经验熵H(D)

$$H(D)=-\sum\_{k=1}^K\frac{\vert C\_k \vert}{\vert D \vert}\log\_2\frac{\vert C\_k \vert}{\vert D \vert}$$

(2) 计算特征A对数据集D的经验条件熵H(D|A)

$$H(D|A)=\sum\_{i=1}^n\frac{\vert D\_i \vert}{\vert D \vert}H(D\_i)=-\sum\_{i=1]^n\frac{\vert D\_i \vert}{\vert D \vert}\sum\_{k=1}^K\frac{\vert D\_{ik} \vert}{\vert D\_i \vert}\log\_2\frac{\vert D\_{ik} \vert}{\vert D\_i \vert}$$

(3) 计算信息增益

$$g(D,A)=H(D)-H(D|A)$$

## 信息增益比

信息增益算法有个缺点，信息增益的值是相对于训练数据集而言的，当H(D)大的时候，信息增益值往往会偏大，这样对H(D)小的特征不公平。改进的方法是信息增益比：

>特征增益比：特征A对训练数据集D的信息增益比$g\_R(D,A)$定义为其信息增益g(D,A)与训练数据集D的经验熵H(D)之比：$$g\_R(D,A)=\frac{g(D,A)}{H(D)}$$

Python代码：

``` python
def calcInformationGainRate(dataSet, baseEntropy, i):
    """
    计算信息增益比
    :param dataSet:数据集
    :param baseEntropy:数据集中Y的信息熵
    :param i: 特征维度i
    :return: 特征i对数据集的信息增益g(dataSet|X_i)
    """
    return calcInformationGain(dataSet, baseEntropy, i) / baseEntropy
```
# 决策树的生成

## ID3算法

### 算法描述

从根节点开始，计算所有可能的特征的信息增益，选择信息增益最大的特征作为当前节点的特征，由特征的不同取值建立空白子节点，对空白子节点递归调用此方法，直到所有特征的信息增益小于阀值或者没有特征可选为止。

### Python实现

ID3特征选择算法的Python实现：

``` python
def chooseBestFeatureToSplitByID3(dataSet):
    """
    选择最好的数据集划分方式
    :param dataSet:
    :return:
    """
    numFeatures = len(dataSet[0]) - 1  # 最后一列是分类
    baseEntropy = calcShannonEnt(dataSet)
    bestInfoGain = 0.0
    bestFeature = -1
    for i in range(numFeatures):  # 遍历所有维度特征
        infoGain = calcInformationGain(dataSet, baseEntropy, i)
        if (infoGain > bestInfoGain):  # 选择最大的信息增益
            bestInfoGain = infoGain
            bestFeature = i
    return bestFeature  # 返回最佳特征对应的维度
```

完整调用：

``` python
# -*- coding:utf-8 -*-
# Filename: testTree.py
# Author：hankcs
# Date: 2014-04-19 下午9:19

###########中文支持################
import sys
from tree import *

reload(sys)
sys.setdefaultencoding('utf-8')
from pylab import *

mpl.rcParams['font.sans-serif'] = ['SimHei']  # 指定默认字体
mpl.rcParams['axes.unicode_minus'] = False  # 解决保存图像时负号'-'显示为方块的问题
##################################

# 测试决策树的构建
myDat, labels = createDataSet()
myTree = createTree(myDat, labels)
# 绘制决策树
import treePlotter
treePlotter.createPlot(myTree)
```

### 可视化

``` python
# -*- coding:utf-8 -*-
# Filename: treePlotter.py
# Author：hankcs
# Date: 2015/2/9 21:24
import matplotlib.pyplot as plt

# 定义文本框和箭头格式
decisionNode = dict(boxstyle="round4", color='#3366FF')  #定义判断结点形态
leafNode = dict(boxstyle="circle", color='#FF6633')  #定义叶结点形态
arrow_args = dict(arrowstyle="<-", color='g')  #定义箭头

#绘制带箭头的注释
def plotNode(nodeTxt, centerPt, parentPt, nodeType):
    createPlot.ax1.annotate(nodeTxt, xy=parentPt, xycoords='axes fraction',
                            xytext=centerPt, textcoords='axes fraction',
                            va="center", ha="center", bbox=nodeType, arrowprops=arrow_args)


#计算叶结点数
def getNumLeafs(myTree):
    numLeafs = 0
    firstStr = myTree.keys()[0]
    secondDict = myTree[firstStr]
    for key in secondDict.keys():
        if type(secondDict[key]).__name__ == 'dict':
            numLeafs += getNumLeafs(secondDict[key])
        else:
            numLeafs += 1
    return numLeafs


#计算树的层数
def getTreeDepth(myTree):
    maxDepth = 0
    firstStr = myTree.keys()[0]
    secondDict = myTree[firstStr]
    for key in secondDict.keys():
        if type(secondDict[key]).__name__ == 'dict':
            thisDepth = 1 + getTreeDepth(secondDict[key])
        else:
            thisDepth = 1
        if thisDepth > maxDepth:
            maxDepth = thisDepth
    return maxDepth


#在父子结点间填充文本信息
def plotMidText(cntrPt, parentPt, txtString):
    xMid = (parentPt[0] - cntrPt[0]) / 2.0 + cntrPt[0]
    yMid = (parentPt[1] - cntrPt[1]) / 2.0 + cntrPt[1]
    createPlot.ax1.text(xMid, yMid, txtString, va="center", ha="center", rotation=30)


def plotTree(myTree, parentPt, nodeTxt):
    numLeafs = getNumLeafs(myTree)
    depth = getTreeDepth(myTree)
    firstStr = myTree.keys()[0]
    cntrPt = (plotTree.xOff + (1.0 + float(numLeafs)) / 2.0 / plotTree.totalW, plotTree.yOff)
    plotMidText(cntrPt, parentPt, nodeTxt)  #在父子结点间填充文本信息
    plotNode(firstStr, cntrPt, parentPt, decisionNode)  #绘制带箭头的注释
    secondDict = myTree[firstStr]
    plotTree.yOff = plotTree.yOff - 1.0 / plotTree.totalD
    for key in secondDict.keys():
        if type(secondDict[key]).__name__ == 'dict':
            plotTree(secondDict[key], cntrPt, str(key))
        else:
            plotTree.xOff = plotTree.xOff + 1.0 / plotTree.totalW
            plotNode(secondDict[key], (plotTree.xOff, plotTree.yOff), cntrPt, leafNode)
            plotMidText((plotTree.xOff, plotTree.yOff), cntrPt, str(key))
    plotTree.yOff = plotTree.yOff + 1.0 / plotTree.totalD


def createPlot(inTree):
    fig = plt.figure(1, facecolor='white')
    fig.clf()
    axprops = dict(xticks=[], yticks=[])
    createPlot.ax1 = plt.subplot(111, frameon=False, **axprops)
    plotTree.totalW = float(getNumLeafs(inTree))
    plotTree.totalD = float(getTreeDepth(inTree))
    plotTree.xOff = -0.5 / plotTree.totalW;
    plotTree.yOff = 1.0;
    plotTree(inTree, (0.5, 1.0), '')
    plt.show()
```

## C4.5生成算法

### 算法描述

### python实现

``` python
def chooseBestFeatureToSplitByC45(dataSet):
    """
选择最好的数据集划分方式
    :param dataSet:
    :return:
    """
    numFeatures = len(dataSet[0]) - 1  # 最后一列是分类
    baseEntropy = calcShannonEnt(dataSet)
    bestInfoGainRate = 0.0
    bestFeature = -1
    for i in range(numFeatures):  # 遍历所有维度特征
        infoGainRate = calcInformationGainRate(dataSet, baseEntropy, i)
        if (infoGainRate > bestInfoGainRate):  # 选择最大的信息增益
            bestInfoGainRate = infoGainRate
            bestFeature = i
    return bestFeature  # 返回最佳特征对应的维度
```

调用方法只需加个参数：

``` python
myTree = createTree(myDat, labels, chooseBestFeatureToSplitByC45)
```

# 决策树的剪枝

决策树很容易发生过拟合，过拟合的原因在于学习的时候过多地考虑如何提高对训练数据的正确分类，从而构建出过于复杂的决策树。解决这个问题的办法就是简化已生成的决策树，也就是剪枝。

决策树的剪枝往往通过极小化决策树整体的损失函数或代价函数来实现。

设决策树T的叶节点有|T|个，t是某个叶节点，t有Nt个样本点，其中归入k类的样本点有Ntk个，Ht(T)为叶节点t上的经验熵，α≥0为参数，则损失函数可以定义为：

$$$$

其中经验熵Ht(T)为：

$$$$

表示叶节点t所代表的类别的不确定性。损失函数对它求和表示所有被导向该叶节点的样本点所带来的不确定的和的和。我没有多打一个“的和”，第二个是针对叶节点t说的。

在损失函数中，将右边第一项记作：

$$$$

则损失函数可以简单记作：

$$$$

C(T)表示模型对训练数据的预测误差，即模型与训练数据的拟合程度，|T|表示模型复杂度，参数α≥0控制两者之间的影响，α越大，模型越简单，α=0表示不考虑复杂度。

剪枝，就是当α确定时，选择损失函数最小的模型。子树越大C(T)越小，但是α|T|越大，损失函数反映的是两者的平衡。

决策树的生成过程只考虑了信息增益或信息增益比，只考虑更好地拟合训练数据，而剪枝过程则考虑了减小复杂度。前者是局部学习，后者是整体学习。

## 树的剪枝算法

从每个叶节点往上走，走了后如果损失函数减小了，则减掉叶节点，将父节点作为叶节点。如图：

![树的剪枝算法](/resource/blog/2016-08/决策树剪枝.jpg)

说是这么说，实际上如果叶节点有多个，那么父节点变成叶节点后，新叶节点到底应该选择原来的叶节点中的哪一种类别呢？大概又是多数表决吧，原著并没有深入展开。


# CART算法

分类与回归树（CART）模型同样由特征选取、树的生成和剪枝组成，既可以用于分类也可以用于回归。CART假设决策树是二叉树，内部节点特征的取值为是和否，对应一个实例的特征是否是这样的。决策树递归地二分每个特征，将输入空间划分为有限个单元。

## CART生成

决策树的生成就是递归地构建二叉决策树的过程。对回归树用平方误差最小化准则，对分类树用基尼系数最小化准则，进行特征选择，生成二叉树。

### 回归树

回归树与分类树在数据集上的不同就是数据集的输出部分不是类别，而是连续变量。

假设输入空间已经被分为M个单元输入空间单元$R\_1,R\_2,\cdots,R\_M$，分别对应输出值$c\_m$，于是回归树模型可以表示为：

$$f(x)=\sum\_{m=1}^Mc\_mI(x \in R\_m)$$

回归树的预测误差：

$$\sum\_{x\_x \in R\_m}(y\_i - f(x\_i))^2$$

那么输出值就是使上面误差最小的值，也就是均值：

$$\hat c\_m = ave(y\_i \vert x\_i \in R\_m)$$

难点在于怎么划分，一种启发式的方法（其实就是暴力搜索吧）：

遍历所有输入变量，选择第j个变量和它的值s作为切分变量和切分点，将空间分为两个区域：

$$R\_1(j,s)=\\{x \vert x^{(j)} \leq s\\} 和R\_2(j,s)=\\{x \vert x^{(j)} > s\\}$$

然后计算两个区域的平方误差，求和，极小化这个和，具体的，就是：

$$\min\_{j,s} \left [ \min\_{c\_1} \sum\_{x\_i \in R\_1(j,s)}(y\_i-c\_1)^2 + \min\_{c\_2} \sum\_{x\_i \in R\_2(j,s)}(y\_i-c\_2)^2 \right ]$$

当j最优化的时候，就可以将切分点最优化：

$$\hat c\_1 = ave()y\_i | x\_i \in R\_1(j,s)) 和 \hat c\_2 = ave()y\_i | x\_i \in R\_2(j,s))$$

递归调用此过程，这种回归树通常称为最小二乘回归树。

### 最小二乘回归树生成算法

输入：训练数据集D

输出：回归树f(x)

在训练数据集所在的输入空间中，递归地将每个区域划分为两个子区域并决定每个子区域上的输出值，构建二叉决策树：

(1) 选择最优切分变量j与切分点s，求解：

$$\min\_{j,s} \left [ \min\_{c\_1} \sum\_{x\_i \in R\_1(j,s)}(y\_i-c\_1)^2 + \min\_{c\_2} \sum\_{x\_i \in R\_2(j,s)}(y\_i-c\_2)^2 \right ]$$

遍历变量j，对固定的切分变量j扫描切分点s,选择使上式达到最小值的对(j,s)

(2) 用选定的对(j,s)划分区域并决定相应的输出值：

$$R\_1(j,s)=\\{x \vert x^{(j)} \leq s\\} 和R\_2(j,s)=\\{x \vert x^{(j)} > s\\}$$

$$\hat c\_m = \frac{1}{N\_m}\sum\_{x\_i \in R\_m(j,s)} y\_i, x \in R\_m,m=1,2 $$

(3) 继续对两个子区域调用步骤(1)和(2)，直到满足停止条件

(4) 将输入空间划分为M个区域R\_1,R\_2,\cdots,R\_M,生成决策树：

$$f(x)=\sum\_{m=1}^M \hat c\_m I(x \in R\_m)$$


### 分类树

与回归树算法流程类似，只不过选择的是最优切分特征和最优切分点，并采用基尼指数衡量。基尼指数定义：

$$$$

对于给定数据集D，其基尼指数是：

$$$$

Ck是属于第k类的样本子集，K是类的个数。Gini(D)反应的是D的不确定性（与熵类似），分区的目标就是降低不确定性。

D根据特征A是否取某一个可能值a而分为D1和D2两部分：

$$$$

则在特征A的条件下，D的基尼指数是：

$$Gini(D,A)=$$

有了上述知识储备，可以给出CART生成算法的伪码：

设节点的当前数据集为D，对D中每一个特征A，对齐每个值a根据D中样本点是否满足A==a分为两部分，计算基尼指数。对所有基尼指数选择最小的，对应的特征和切分点作为最优特征和最优切分点，生成两个子节点，将对应的两个分区分配过去，然后对两个子节点递归。

## CART剪枝

在上面介绍的损失函数中，当α固定时，一定存在使得损失函数最小的子树，记为复杂度=Tα，α偏大Tα就偏小。设对α递增的序列，对应的最优子树序列为Tn，子树序列第一棵包含第二棵，依次类推。

从T0开始剪枝，对它内部的任意节点t，只有t这一个节点的子树的损失函数是：

$$C\_{\alpha}=C(t)+\alpha$$

以t为根节点的子树的损失函数是：

$$C\_{\alpha}(T\_t)=C(T\_t)+\alpha \vert T \vert$$

当α充分小，肯定有:

$$C\_{\alpha}(T\_t)<C\_{\alpha}(t)$$

这个不等式的意思是复杂模型在复杂度影响力小的情况下损失函数更小。

当α增大到某一点，这个不等式的符号会反过来。

只要$\alpha = \frac{C(t)-C(T\_t)}{\vert T\_t \vert -1}$,损失函数值就相同，但是t更小啊，所以t更可取，于是把Tt剪枝掉。

为此，对每一个t，计算

$$g(t)=$\frac{C(t)-C(T\_t)}{\vert T\_t \vert -1}$$

表示损失函数的减少程度，从T中剪枝掉g(t)最小的Tt，取新的α=g(t)，直到根节点。这样就得到了一个子树序列，对此序列，应用独立的验证数据集交叉验证，选取最优子树，剪枝完毕。