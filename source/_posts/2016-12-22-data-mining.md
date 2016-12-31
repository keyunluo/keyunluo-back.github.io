---
title: 数据挖掘——课程总结
comments: true
toc: true
date: 2016-12-22 14:25:02
categories: MachineLearning
tags : 机器学习
keywords: 数据挖掘, 南京大学
mathjax: true
---

>**本节内容：**2016年秋南京大学计算机系数据挖掘课程学期总结。


<!-- more -->

## 降维

### Distance Measure

- **$L\_p-Norm$**(Minkowski distance)

  形式：给定$\bar{X}=(x\_1,x\_2,\ldots, x\_d), \bar{Y}=(y\_1,y\_2,\ldots, y\_d)$, 他们之间的Lp范数距离为：
  $$Dist(\bar{X}, \bar{Y}) = \left( \sum\_{i=1}^d \vert x\_i - y\_i |^p \right )^{1/p}$$

  特例：p=1时为曼哈顿距离：各分量绝对值之和；p=2时为欧式距离：根号下和的平方；$p=\infty$时为无穷范数，又称为切比雪夫距离 ：绝对值最大的数；p=0时为0范数：非零元素个数，非凸；0<p<1时为分数范数，非凸。

- Standardized Euclidean distance

    标准化欧式距离，两个分量间减去均值除以标准差进行标准化：

    $$Dist(\bar{X}, \bar{Y}) = \left( \sum\_{i=1}^d \vert \frac{x\_i - y\_i}{s_i} |^2 \right )^{1/2}$$

- **Mahalanobis Distance**

    马氏距离用来消除不同维度之间的相关性和和尺度不同的性质。样本矩阵$X$的协方差矩阵为$\Sigma$,则它的各个向量间的马氏距离为：

    $$Dist(X_i, X_j) = \sqrt{(X_i - X_j)^T\Sigma^{-1}(X_i-X_j)}$$

    若对协方差矩阵进行正交投影分解，即：$\Sigma = U \Lambda U^T = \sum\_{i=1}^{d} \sigma\_i u\_iu\_i^T$, 那么，$\Sigma^{-1} = U \Lambda U^T= \sum\_{i=1}^{d} \sigma\_i^{-1} u\_iu\_i^T$, 马氏距离可表示成如下：

    $$Dist(X\_i, X\_j) = \sqrt{(X\_i - X\_j)(\sum\_{i=1}^d \sigma\_i^{-1}u\_iu\_i^T)(X\_i - X\_j)^T} = \sqrt{\sum\_{i=1}^d \frac{((X\_i - X\_j)u\_i)^2}{\sigma\_i}}$$

- Cosine

    夹角余弦越大表示两个向量的夹角越小,越相似，夹角余弦越小表示两向量的夹角越大，越不相似。当两个向量的方向重合时夹角余弦取最大值1，当两个向量的方向完全相反夹角余弦取最小值-1。

    $$\cos(\theta) = \frac{\sum\_{i=1}^d x\_iy\_i}{\sqrt{\sum\_{i=1}^d x\_i^2} \sqrt{\sum\_{i=1}^d y\_i^2}}$$

- Hamming distance

    两个等长字符串s1与s2之间的汉明距离定义为将其中一个变为另外一个所需要作的最小替换次数。

- Jaccard similarity coefficient

    - 杰卡德相似系数：两个集合A和B的交集元素在A，B的并集中所占的比例: $J(A,B) = \frac{A \bigcap B}{A \bigcup B}$

    - 杰卡德距离:两个集合中不同元素占所有元素的比例， $J\_\delta(A,B) = 1 - \frac{A \bigcap B}{A \bigcup B}$

- Correlation coefficient And Correlation distance

    - 相关系数：相关系数是衡量随机变量X与Y相关程度的一种方法，相关系数的取值范围是[-1,1]。相关系数的绝对值越大，则表明X与Y相关度越高。当X与Y线性相关时，相关系数取值为1（正线性相关）或-1（负线性相关）。$\rho\_{XY} = \frac{Cov(X,Y)}{\sqrt{D(X)} \sqrt{D(Y)}} = \frac{E((X-E(X))(Y-E(Y)))}{\sqrt{D(X)} \sqrt{D(Y)}}$

    - 相关距离：$D\_{xy} = 1 - \rho\_{XY}$

- Information Entropy

    信息熵是衡量分布的混乱程度或分散程度的一种度量。$Entropy(X) = - \sum\_{i=1}^{n} p\_i \log\_2 p\_i$

- **Nonlinear Distributions: ISOMAP**

    - 对每个点计算其k个近邻
    - 构造一个带权图G，结点代表数据点，边的权值代表k个近邻间的欧式距离。
    - 计算任意两点间的距离$Dist(\bar{X}, \bar{Y})$为图中点$\bar{X}, \bar{Y}$之间的最短距离。
    - 计算 MDS(multidimensional scaling)：得到图的向量表示，即映设矩阵。

### PCA

1. 轴旋转： $x = [x^1, x^2, \ldots, x^d]^T \in \mathcal{R}^d \Leftrightarrow x = x^1e\_1 + x^2e\_2 + \ldots + x^de\_d$，W:正交矩阵，$W = [w\_1, w\_2, \ldots, w\_d]$, 则$x = WW^Tx = (\sum\_{i=1}^dw\_iw\_i^T)x = \sum\_{i=1}^dw\_i(w\_i^Tx) = (w\_1^Tx)w\_1 + \ldots + (w\_d^Tx)w\_d$, 因而，在坐标W的投影下，新坐标是$y = [w\_1^Tx, w\_2^Tx, \ldots, w\_d^Tx]^T \in \mathcal{R}^d$. $w\_i^T = <w\_i, x>$表示新的坐标，含义是将$x$沿着$w\_i$的方向投影。

2. 降维目标：给定数据点集合${x\_1,x\_2, \ldots, x\_n}, x\_i \in \mathcal{R}^d$，寻找一个投影方向${w\_1,w\_2,\ldots,w\_k}$，使得方差$y\_1 = [w\_1^Tx\_1, w\_2^Tx\_1,\ldots, w\_k^Tx\_1]^T, y\_2 = [w\_1^Tx\_2, w\_2^Tx\_2,\ldots, w\_k^Tx\_2]^T, \ldots, y\_n = [w\_1^Tx\_n, w\_2^Tx\_n,\ldots, w\_k^Tx\_n]^T$ 最大，即最大化类间方差。

   考虑一维的情况，形式化为：新坐标：$w\_1^Tx\_1,w\_1^Tx\_2, \ldots, w\_1^Tx\_n$,方差：$\frac{1}{n} \sum\_{i=1}^n(w\_1^Tx\_i - \mu )^2, \mu = \frac{1}{n} \sum\_{i=1}^nw\_1^Tx\_i$.

3. 问题推导过程

    - 令$\bar{x} = \frac{1}{n} \sum\_{i=1}^nx\_i$为均值向量
    - 那么，$\mu = w\_1^T\bar{x}$,

        $$\begin{array} {lcl}
        \frac{1}{n} \sum\_{i=1}^n(w\_1^Tx\_i - \mu)^2 & = & \frac{1}{n} \sum\_{i=1}^n(w\_1^Tx\_i - w\_1^T\bar{x})^2 \\\\
        & = & \frac{1}{n} \sum\_{i=1}^n(w\_1^T(x\_i - \bar{x}))^2 \\\\
        & = & \frac{1}{n} \sum\_{i=1}^nw\_1^T(x\_i - \bar{x})(x\_i - \bar{x})^Tw\_1 \\\\
        & = & w\_1^T(\frac{1}{n} \sum\_{i=1}^n(x\_i - \bar{x})(x\_i - \bar{x})^T)w\_1
        \end{array}$$

    - 于是， PCA转变成一个优化问题：

    > $\max\_{w \in \mathcal{R}^d}\ w^TCw$
    >
    > $s.t. \Vert w \Vert\_2^2 = 1$

    这里， $C = \frac{1}{n}\sum\_{i=1}^n(x\_i - \bar{x})(x\_i - \bar{x})^T)$为协方差矩阵。该协方差矩阵具有如下特征：对称性，半正定，矩阵的秩最大为n-1。

4. 解决方法

    - 拉格朗日法：$-w^TCw + \lambda(\Vert w \Vert\_2^2 -1)$

    - 对$w$求导，令导数为0：$-2Cw + 2\lambda w  = 0$, 得出$Cw = \lambda w$

    - $(w, \lambda)$ 是协方差矩阵C的特征向量和特征值

    - 目标函数变成：$w^TCw = \lambda w^T w = \lambda$

    - 因此， 我们选择C中最大的特征值和特征向量

5. **PCA算法(1维)**

    - 计算均值向量：$\bar{x} = \frac{1}{n} \sum\_{i=1}^n x\_i$
    - 计算协方差矩阵：$C = \frac{1}{n} \sum\_{i=1}^n(x\_i - \bar{x})(x\_i - \bar{x})^T)$
    - 计算C中最大的特征值对应的特征向量

6. **PCA算法(k维)**

    - **优化目标**:
    > $\max\_{w \in \mathcal{R}^{d*k}}\ trace(W^TCW)$
    >
    > $s.t. W^TW = I$

    其中， $W=[w\_1, \ldots, w\_k]$ 是C的k个大的特征向量。

    - **算法流程**

        - 计算均值向量：$\bar{x} = \frac{1}{n} \sum\_{i=1}^n x\_i$
        - 计算协方差矩阵：$C = \frac{1}{n} \sum\_{i=1}^n(x\_i - \bar{x})(x\_i - \bar{x})^T)$
        - 计算C中最大的k个特征值对应的特征向量

    - **特征值含义**
        - $\lambda\_i$是第i个特征坐标间的方差
        - 度量PCA质量的好坏：所取的前k个特征值之和与所有的特征值和之比。

    - **另一个思考视角**：最小化投影误差

    - PCA算法是线性的，无监督的

### SVD

1. 奇异值分解: $X=U\Sigma V^T = \sum\_{i=1}^d \sigma\_iu\_iv\_i^T$:

    - $U = [u\_1, u\_2,\ldots, u\_d] \in \mathcal{R}^{d*d}, U^TU=UU^T = I$
    - $V = [v\_1,v\_2, \ldots,v\_d] \in \mathcal{R}^{n*d}, V^TV=I$
    - $\Sigma = diag(\sigma\_1, \sigma\_2,\ldots, \sigma\_d),\in \mathcal{R}^{d*d}, \sigma\_1 \ge \sigma\_2 \ge \ldots \ge \sigma\_d \ge 0$

2. 紧凑SVD：$X=U\_r\Sigma\_r V\_r^T = \sum\_{i=1}^r \sigma\_iu\_iv\_i^T$， **rank(r) < min(d,n)**:

    - $U\_r = [u\_1, u\_2,\ldots, u\_r] \in \mathcal{R}^{d*r}, U\_r^TU\_r=I$
    - $V\_r = [v\_1,v\_2, \ldots,v\_r] \in \mathcal{R}^{n*r}, V\_r^TV\_r=I$
    - $\Sigma\_r = diag(\sigma\_1, \sigma\_2,\ldots, \sigma\_r),\in \mathcal{R}^{r*r}, \sigma\_1 \ge \sigma\_2 \ge \ldots \ge \sigma\_r \ge 0$

3. 使用SVD降维

    - 计算X的k个最大的左奇异向量$u\_1,u\_2,\ldots,u\_k$
    - x的新坐标：$U\_k^Tx = [u\_1^Tx, u\_2^Tx, \ldots, u\_k^Tx] \in \mathcal{R}^k, U\_k = [u\_1, u\_2,\ldots,u\_k] \in \mathcal{R}^{d*k}$
    - X的新坐标：$U\_k^TX=U\_k^TU\_r\Sigma\_rV\_r^T = \Sigma\_kV\_k^T$

4. SVD的优化问题：
    - 一维：
        > $\max\_{w \in \mathcal{R}^d}\ w^T(XX^T)w$
        >
        > $s.t. \Vert w \Vert\_2^2 = 1$
    - k维：
        >$\max\_{w \in \mathcal{R}^{d*k}}\ trace(W^T(XX^T)W)$
        >
        > $s.t. W^TW = I$

5. PCA by SVD

    - 计算均值向量$\bar{x}$:$ \frac{1}{n} \sum\_{i=1}^n x\_i$
    - 计算$\bar{X}=[x\_1 - \bar{x},\ldots, x\_n - \bar{x}]$最大的k个左奇异值

### MDS

1. 输入

    - 图G：n个结点
    - 距离:$\delta\_{ij} = \delta\_{ji}$

2. 输出

    - 适配于距离的坐标集

3. 优化问题

    $\min\_{x\_1,x\_2,\ldots, x\_n \in \mathcal{R}^k} \sum\_{i,j:i<j}(\Vert x\_i - x\_j \Vert\_2 - \delta\_{ij})^2$

4. MDS算法

 - 计算点积：$S= -\frac{1}{2}(I - \frac{np.ones^T}{n})G^2(I - \frac{np.ones^T}{n})$
 - 对S进行奇异值分解：$S=U \Lambda U^T = \sum\_{i=1}^n\lambda\_iu\_iu\_i^T$
 - 新坐标：$U\_k\Lambda\_k^{1/2} \in \mathcal{R}^{n*k}$


## 关联规则挖掘

### The Frequent Pattern Mining Model

1. U : d个item集合
2. T ：n个交易的集合,$T\_1,T\_2, \ldots, T\_n, T\_i \in U$
3. $T\_i$二进制表示
4. Itemset,k-itemset: item集合， k个items的集合
5. Support(支持度)：包含itemset I的数据集T的一个子集，用sup(I)表示，表示某个item集合在数据表中出现的比例。
6. minsup(最小支持度)：预先定义的阈值，只有支持度大于minsup的子集才被视为一个频繁项。
7. frequent itemset(频繁项集)：项集X的支持度超过最小门限值minsup时，称X为频繁项集

### Property

- 支持单调性
(Support Monotonicity Property)： $\sup(J) \ge \sup(I), \forall J \in I$, 这就意味着一个itemset I 包含在一个交易中，那么它的所有子集也包含在这个交易中。

- 向下闭包属性(Downward Closure Property)：每一个频繁项的子集也是一个频繁项。

### Association Rule

1. 置信度：$conf(X \Rightarrow Y) = \frac{sup(X \bigcup Y)}{sup(X)}$

2. 关联规则：同时满足最小支持度阈值和最小置信度阈值的规则称为关联规则。这分别保证了有效数量的交易是相关的和在条件概率方面有足够的强度。

3. 一般框架：
    - 找出所有频繁项集，即候选规则
    - 对所有候选规则计算置信度，找出其中的关联规则

4. 一个直观的实现：
    - 给定一个频繁集I
    - 产生所有可能的划分X,Y:Y=I-X
    - 检测置信度：$X \Rightarrow Y$

5. 置信度单调性：$X\_1 \subset X\_2 \subset I, conf(X\_2 \Rightarrow I - X\_2) \ge conf(X\_1 \Rightarrow I - X\_1)$

### [Apriori Algorithm](http://blog.csdn.net/golden1314521/article/details/41457019)

- 基本思想：为了减少频繁项集的生成时间，我们应该尽早的消除一些完全不可能是频繁项集的集合，Apriori的两条定律如下：
    - 如果一个集合是频繁项集，则它的所有子集都是频繁项集。举例：假设一个集合{A,B}是频繁项集，即A、B同时出现在一条记录的次数大于等于最小支持度minsup，则它的子集{A},{B}出现次数必定大于等于minsup，即它的子集都是频繁项集。
    - 如果一个集合不是频繁项集，则它的所有超集都不是频繁项集。举例：假设集合{A}不是频繁项集，即A出现的次数小于minsup，则它的任何超集如{A,B}出现的次数必定小于minsup，因此其超集必定也不是频繁项集。

- 算法
    - 使用k-itemsets频繁项产生(k+1)-itemsets候选集
    - 在计数前对候选集剪枝
    - 对余下的(k+1)-candidates计算支持度
    - 当(k+1)-candidates中没有频繁项时停止，否则循环
![Apriori算法](/resource/blog/2016-12/apriori.png)

- 优化1： Candidates Generation
    - 思路：U中的Item使用字典序排列，Itemsets按字符串排序
    - 方法：
        - 对k-itemsets频繁项排序
        - 如果两个itemset的前k-1 items相同则合并
- 优化2：Level-wise Pruning Trick
    - 令$F\_k$为k-itemsets频繁项，$C\_{k+1}$为(k+1)-candidates 集
    - 对于一个集合I:$I \in C\_{k+1}$为频繁集当且仅当I中的所有的k-subsets都是频繁项
    - 剪枝：
        - 产生I的所有的k-subsets
        - 如果它们当中的一个不属于$F\_k$，那么移除I

- **上述优化后的算法**：
    - 将数据库中的事务的数据排序，首先将每条事务记录中多个元素排序，然后将事务整体排序。
    - 令k=1，扫描数据库，得到候选的1-项集并统计其出现次数，由此得到各个1-项集的支持度，然后根据最小支持度来提出掉非频繁的1-项集进而得到频繁的1-项集。
    - 令k=k+1.通过频繁（k-1）-项集产生k-项集候选集的方法（也称“连接步”）：如果两个（k-1）-项集，如果只有最后一个元素不同，其他都相同，那么这两个(k-1)-项集项集可以“连接”为一个k-项集。不能连接的就不用考虑了，不会频繁的。
    - 从候选集中剔除非频繁项集的方法（也称“剪枝步”）：对任一候选集，看其所有子项集(其实只需要对k-2个子项集进行判别即可)是否存在于频繁的(k-1)-项集中，如果不在，直接剔除；扫描数据库，计数，最终确认得到的频繁的k-项集。
    - 如果得到的频繁的k-项集的数目<=1，则搜索频繁项集的过程结束；否则转到第3步。

- 优化3：Support Counting
    - 朴素的方法：对于每一个候选集:$I\_i \in C\_{k+1}$,对于每一个交易$T\_j$,检查$T\_i$是否在$T\_j$出现。
    - 优化方法：将$C\_{k+1}$中的候选集模式组织成一个Hash tree，使用Hash tree加速计数:先将所有的k-阶候选集存储在哈希树的结构的叶节点上，然后对每个transaction记录找到其包含的所有k-阶候选集，所以这个过程只需要浏览一遍数据库。

- Hash Tree
    - 1.用k表示插入进行到第几层，初始值为1.
    - 2.对输入项集的第k项进行hash，得到n
    - 3.判断当前层的根节点的第n个子节点是否为叶节点，若非则跳到1继续
    - 4.将项集插入到该叶子节点是否
    - 5.判断该叶子节点是否已满，若是则进行分裂，否则插入结束，分裂过程是将该节点原有的项集和新项集按第level项进行hash，然后分别插入到下一层的新叶节点中，而原叶节点变为非叶节点

- 基于Hash Tree的计数
    - 对于每一个$T\_j$，识别那些在Hash Tree中可能包含子集项的叶子节点
    - 过程：
        - 根节点：对$T\_j$中的所有项进行Hash
        - 如果在叶子节点上，则寻找$T\_j$中所有的子集项
        - 如果在内部节点，则在给定位置之后Hash每一个项

## 聚类

### K-Means

- 思想
    - 基于代表的算法，错误平方和,即类间平方和最小：$\min\_{\bar{Y\_1},\ldots, \bar{Y\_k}} O = \sum\_{i=1}^n[\min\_j \ Dist(\bar{X\_i},\bar{Y\_j})]$
    - 距离度量：$Dist(\bar{X\_i}, \bar{Y\_j}) = \Vert \bar{X\_i} - \bar{Y\_j} \Vert\_2^2$
    - 指定聚类：$C\_1,\ldots, C\_k$
    - 优化步骤：$\bar{Y\_j} = argmin\_{\bar{Y}} \sum\_{\bar{X\_i} \in C\_j} \Vert \bar{X\_i} - \bar{Y} \Vert\_2^2 = \frac{1}{C\_j}\sum\_{\bar{X\_i} \in C\_j} \bar{X\_i}$

- 算法流程
    - 为每个聚类确定一个初始聚类中心，这样就有K 个初始聚类中心。
    - 将样本集中的样本按照最小距离原则分配到最邻近聚类。
    - 使用每个聚类中的样本均值作为新的聚类中心。
    - 重复步骤2.3直到聚类中心不再变化。
    - 结束，得到K个聚类。

- 本地马氏距离度量：$Dist(\bar{X\_i}, \bar{Y\_j}) = (\bar{X\_i} - \bar{Y\_j} ) \Sigma\_r^{-1}(\bar{X\_i} - \bar{Y\_j})^T$

- 基于核的方法

- k-Medians 算法：
    - 使用曼哈顿距离：$Dist(\bar{X\_i}, \bar{Y\_j}) = \Vert \bar{X\_i} - \bar{Y\_j} \Vert\_1 = \sum\_{p=1}^d \vert X\_i^p - Y\_j^p \vert$
    - 优化步骤：$Y\_j^p = argmin\_Y \sum\_{\bar{X\_i} \in C\_j} |X\_i^p -Y| = median{X\_i^p | \bar{X\_i} \in C\_j}$
    - 缺点：$\bar{Y} = [Y\_j^1,\ldots,Y\_j^d]$可能不在数据集合中。

### K-Medoids

- 核心：代表点是从数据中选出来的：$\min\_{\bar{Y\_1},\ldots, \bar{Y\_k} \in D} O = \sum\_{i=1}^n[\min\_j \ Dist(\bar{X\_i},\bar{Y\_j})]$
- 基于爬山法的优化：代表点S初始化为D中的k个点，S通过不断迭代地与D中的点交换来改善。
- 交换方法：所有点|S||D|交换；随机选择r对$(\bar{X\_i},\bar{Y\_j}$点然后选择最好的。

### Spectral Clustering

- 思想：把数据映射到一个新空间,该空间里具有约化的维度,使得相似性更加显而易见,然后对新空间的数据进行聚类。
- 优化问题：(k=1)
    - $\min\_{y \in \mathcal{R}^n} \ y^TLy$
    - s.t. $y^TDy = 1$
    - 解决方案：$Ly = \lambda Dy$, 最小值为$y^1=1$,使用第二最小值$y^2$
- 拉普拉斯特征值映设(k>1):
    - 向量形式：$O = \sum\_{i=1}^n\sum\_{j=1}^nw\_{ij} \Vert y\_i - y\_j \Vert\_2^2 = 2 trace(Y^TLY)$
    - $Y = [y\_1,\ldots,y\_n]^T \in \mathcal{R}^{n*k}$
    - $L = D -W \in \mathcal{R}^{n*n}$:图的拉普拉斯表示
    - $W=[w\_{ij}] \in \mathcal{R}^{n*n}$:相似度矩阵
    - $D \in \mathcal{R}^{n*n}$是一个对角矩阵，$D\_{ii} = \sum\_{j=1}^nw\_{ij}$
- 优化问题：(k>1)
    - $\min\_{y \in \mathcal{R}^{n*k}} \ trace(Y^TLY)$
    - s.t. $Y^TDY = I$
    - 解决方案：$Ly = \lambda Dy$, 最小值为$y^1=1$,$Y = [y^2,\ldots,y^{k+1}] \in \mathcal{R}^{n*k}$
- 步骤：
    - 构建带权图相似度矩阵 W:使用 k 近邻算法,对每个点选取前 k 个邻居为 1,其余为 0,并对称化该矩阵;
    - 构建拉普拉斯图矩阵 L=D-W,并归一化:构建 W 的对角元素矩阵 D($d\_i = \sum\_iW\_{ij}$), $L = D^{-0.5}LD^{-0.5} = I - D^{-0.5}WD^{-0.5}$；
    - 特征值分解,得到新的数据:eig_values, eig_vectors = np.linalg.eigh(L),将特征值排序,选取特征值最小的 k 个对应的特征向量列组成显得数据;
    - 调用 KMedoids 算法

### Non-negative Matrix Factorization

- 优化问题：
    - $\min\_{U \in \mathcal{R}^{d\*k}, V \in \mathcal{R}^{v*k}} \ \Vert X - UV^T \Vert\_F^2 $
    - $s.t. \ U \ge 0, V \ge 0$
    - 非负矩阵分解是非凸的
- 解释
    - 矩阵近似：$X  \approx UV^T， x\_i \approx Uv\_i = \sum\_{j=1}^ku\_jv\_{ij}$
    - 向量近似：$x\_i \approx Uv\_i = \sum\_{j=1}^ku\_jv\_{ij}, u\_1,\ldots,u\_k \in \mathcal{R}^d$可视为基向量。$v\_i = [v\_{i1},\ldots,v\_{ik}]^T$可视为原始$x\_i$的新的k维表示。

## 分类

### LDA

- 两分类问题

### Naive Bayes

### SVM

### Logistic Regression

## 凸优化

### The Dual Problem

## 高级分类方法

### Semisupervised Learning

### Active Learning

### Ensemble Methods

## 线性回归

### Least Square

### Ridge Regression

## Web挖掘

### Page Ranking

### Collaborative Filtering