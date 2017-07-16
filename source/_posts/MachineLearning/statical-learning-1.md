---
title: 统计学习方法——概论
comments: true
toc: true
date: 2016-07-26 19:25:02
categories: MachineLearning
tags : 机器学习
keywords: 机器学习, 统计学习
mathjax: true
---

>**本节内容：**统计学习(statistical learning)做的事情是：基于数据建立概率统计模型，然后用模型对数据进行预测和分析。


<!-- more -->

## 统计学习三要素
1. 模型

    * **模型** 就是所要学习的条件概率分布或决策函数。    $Y=f(X)$ 或 $P(Y|X)$

    * **模型的假设空间** 包括所有可能的条件概率分布或决策函数。

    $\mathcal{F} = \\{f | Y=f_\theta(X), \theta\in R^n \\}$

    或 $ \mathcal{F} = \\{P | P_\theta(Y|X), \theta\in R_n \\}$。 其中 $ \theta$的取值空间$R^n$称为**参数空间**。
2. 策略
    **策略**也即学习的准则。一般来说监督学习的策略即指经验风险或结构风险函数最优化。
    * 经验风险

        * 损失函数 ：$ L(Y, f(X))$
            1. 0-1损失函数
            2. 平方损失函数
            3. 绝对损失函数
            4. 对数损失函数
        * 风险函数
            **风险函数(risk function)又叫期望损失(expected loss)**，是理论模型$f(X)$关于联合分布$P(X,Y)$的平均意义下的损失。损失函数的期望是：

        $$ R\_{exp}(f) = E\_p[L(Y,f(X))]=\\int\_{\mathcal{X}\times \mathcal{Y}}L(y,f(x))P(x,y)dxdy $$

        * 经验风险
            风险函数和联合分布$P(X, Y)$，用作为模型的后者求作为策略的前者，显然是病态的。故取训练数据集上的平均损失称为**经验风险(empirical risk)**。

        $$ R\_{emp}(f) = \frac1N\sum_{i=1}^N L(y_i, f(x_i)) $$

           当训练样本数量$N$趋于无穷时，$R\_{emp}$趋于$R\_{exp}$。

    * 结构风险

        **结构风险(structural risk)**在经验风险的基础上添加正则化项(regularization，也叫罚项(penalty term))。

        $$ R\_{srm}(f)=\frac1N\sum\_{i=1}^N L(y\_i,f(x\_i))+ \lambda J(f), \lambda \geq 0 $$

    * 经验风险最小化

        在$\mathcal{F}$找到一个$f$使得$R_{emp}$最小。
        p.s. 当模型是条件概率分布，损失函数是对数损失函数时，经验风险最小化等价于**极大似然估计**。

    * 结构风险最小化

        为防止经验风险最小化有可能带来的过拟合，添加**代表模型复杂度**的罚项$J(f)$。
3. 算法
    最优化算法

----------

## 模型评估与模型选择

### 误差

1. 训练误差

    **训练误差(training error)**是学习到的模型$Y=\hat{f}(X)$关于训练数据集的平均损失。

    $$R\_{emp}(\hat{f}) = \frac1N\sum\_{i=1}^N L(y\_i, \hat{f}(x\_i))$$
2. 测试误差

    **测试误差(test error)**是学习到的模型$Y=\hat{f}(X)$关于测试数据集的平均损失。

    $$e\_{test}(\hat{f}) = \frac1{N'}\sum\_{i=1}^{N'} L(y\_i, \hat{f}(x\_i))$$
当损失函数是0-1损失时，测试误差即为测试数据集上的误差率。

### 正则化

正则化方法就是在经验风险函数上添加正则化项。**正则化项(regularizer)**一般是模型复杂度的单调递增函数。如可以是模型参数向量的范数。

正则化的一般形式：

$$ min\_{f \in \mathcal{F} } \frac1N\sum\_{i=1}^N L(y\_i,f(x\_i))+ \lambda J(f)$$

其中第一项是经验风险，第二项是正则化项，正则化项可以取不同的形式，例如，在回归问题中，损失函数是平方损失，正则化项可以是参数向量的$L_2$范数：

$$L(w)=\frac1N\sum\_{i=1}^N L(y\_i,f(x\_i))+ \frac{\lambda}{2} {\Vert w \Vert}^2$$

* 奥卡姆剃刀(Occam's razor)原理：在所有可能选择的模型中，能够很好解释已知数据并且十分简单才是最好的模型。

### 交叉验证

将数据集随机分为训练集、验证集(validation set)和测试集，分别用于模型的训练、选择和评估。

1. 简单交叉验证
    分两部分：训练集和测试集
2. $S$折交叉验证
    等分$S$部分：$S-1$份做训练集，1份做测试集。重复进行。
3. 留一交叉验证
    $S$折交叉验证的特例$S=N$。

### 泛化能力

1. 泛化误差

    **泛化误差(generalization error)**学到的模型$\hat{f}$对未知数据预测的误差。

    $$R\_{exp}(\hat{f}) = E\_p[L(Y,\hat{f}(X))]=\int \_{\mathcal{X}\times \mathcal{Y}}L(y,\hat{f}(x))P(x,y)dxdy$$

    泛化误差就是学习到的模型的期望风险。

2. 泛化误差上界
    * 样本容量增加，泛化误差上界趋近于0
    * 假设空间容量增加，泛化误差上界增大

----------

## 监督学习分类

### 按学习方法分类
1. 生成方法->生成模型
    * 由数据学习联合概率分布$P(X,Y)$后，求出概率分布$P(Y|X) = P(X,Y)/P(X)$。
    * 包括：朴素贝叶斯法、隐马尔科夫模型
2. 判别方法->判别模型
    * 由数据直接学习决策函数$f(X)$或者条件概率分布$P(Y|X)$。
    * 包括：k近邻法、感知机、逻辑斯蒂回归模型、最大熵模型、支持向量机、提升方法、条件随机场。
3. 区别：
    * 生成方法：
        * 可还原出$P(X,Y)$
        * 学习收敛速度快，当N增大时，更快收敛于真实模型
        * 当存在隐变量时，仍可以使用
    * 判别方法
        * 直接学习$f(X)$或$P(Y|X)$，往往学习的准确率更高
        * 可对数据进行抽象、特征定义以简化学习问题

### 研究议题

输入变量和输出变量均为连续变量的预测问题称为回归问题，如函数拟合；输出变量为有限个离散变量的预测问题称为分类问题，学习出的分类模型或分类决策函数称为分类器（classifier）；输入变量与输出变量均为变量序列的预测问题称为标注问题，如词性标注，输入词序列，输出是（词，词性）的标记序列。


1. 分类问题
    * 评价指标
        * 精确率(precision)
            $$P = \frac{TP}{TP+FP}$$
        * 召回率(recall)
            $$R = \frac{TP}{TP+FN}$$
        * $F_1$
            $$F_1 = \frac{2}{\frac1P + \frac1R} = \frac{2TP}{2TP+FP+FN}$$
2. 标注问题
3. 回归问题
