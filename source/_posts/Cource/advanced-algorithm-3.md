---
title: 高级算法(3)--Min-Cut-Max-Flow(2)-近似算法
comments: true
toc: true
date: 2016-10-06 14:41:02
categories: Cource
tags : 高级算法
keywords: Karger's Contraction algorithm
mathjax : true
---

>**本节内容：**本节重点讨论Karger's Contraction algorithm解决最小割问题及其运行复杂度分析。

<!-- more -->

## 收缩操作

在上一博客的基础上，讨论收缩操作：

> The contraction operator Contract(G, e)

>  say e = uv:
- replace $\\{u,v\\}$ by a new vertex x;
- for every edge (no matter parallel or not) in the form of $uw$ or $vw$ that connects one of $\\{u,v\\}$ to a vertex $w\in V\setminus\\{u,v\\}$ in the graph other than $u,v$, replace it by a new edge $xw$;
- the reset of the graph does not change.

换句话说，$Contract(G, uv)$就是将和$u,v$合并成一个新的点$x$,其他与新点$x$相邻的点所形成的边都将保存下来，因此，即使原图没有并行边，收缩操作也会产生并行边，所以我们把条件放宽至multi-graph。

## Karger's algorithm

### 简单的idea

- 在当前的multi-graph中每一步随机选择一条边来收缩直到最终仅剩下两个点为止

- 这两个点之间的并行边一定是这个原始图的一个Cut

- 我们返回这个Cut并且希望有很大几率这个Cut是minimum-Cut

### 伪代码

> RandomContract (Karger 1993)

> Input: multi-graph G(V,E);

> while | V | > 2 do

> &emsp;&emsp;choose an edge ${uv}\in E$ uniformly at random;

> &emsp;&emsp;G = $Contract(G,uv)$;

> return C = E (the parallel edges between the only two vertices in V);

从另一个方面说，顶点集$V=\\{v\_1,v\_2,\ldots,v\_n\\}$,我们从n个顶点类$ S\_1,S\_2,\ldots, S\_n ，其中每一个S\_i = {v\_i} $仅包含一个顶点开始，通过调用收缩操作$Contract(G,uv),  u \in S\_i and v\in S\_j , i \neq j$, 我们使用$S\_i \bigcup S\_j$操作。

### 简单分析

在上述RandomContract算法中，一共有$n-2$个收缩操作，每一个收缩操作可以在$O(n)$时间内完成，因此，时间上限是：$O(n^2)$

## 精度分析

### 分析

**约定**：$ e\_1,e\_2,\ldots,e\_{n-2} $ 为运行*RandomContract Algorithm*随机选择的边，$G\_1 = G$为原始的输入multi-graph,$G\_{i + 1} = Contract(G\_i,e\_i) ,i = 1,2, \ldots, n-2$, $C$是multi-graph $G$ 中的最小割， $p\_C=Pr\[C\ is\  returned \  by \  RandomContract\]$。

> **命题1**：如果：$e \notin C$，那么$C$仍然是multi-graph $G^{'} = contract(G,e)$的一个最小割

基于上述命题，事件$ \mbox{“}C\mbox{ is returned by }RandomContract\mbox{”}\,$等价于事件$“ e\_i \notin C , for\ all\ i=1,2,\ldots,n-2 ”$，因此，根据条件概率中的链式规则，可以得到如下等式：

$$\begin{array} {lcl}
p\_C  & = & Pr[ C\ is\ returned\ by\ RandomContract] \\\\
      & = & Pr[e\_i \notin C for\ all\ i = 1,2,\ldots, n-2] \\\\
      & = & \prod\_{i=1}^{n-2}Pr[ e\_i \notin C \vert \forall j < i, e\_j \notin C]
\end{array}$$

即前i-1个边都不在C中，第i个边在上述条件发生的情况下也不在C中，下面考虑上式的上界：

> **命题2**:如果 C 是multi-graph $G(V,E)$中的一个最小割，那么其满足如下约束条件：$degree(G)=2|E|/|V| \geq |C|$，即C的度数小于等于平均每个点的度数。

已知，$|V\_i| = n -i +1， p\_i = Pr[e\_i \notin C \vert \forall j < i, e\_j \notin C]$,根据命题2，可得：

$$\begin{array} {lcl}
p\_i  & = & 1 - \frac{\vert C \vert}{\vert E\_i \vert} \\\\
      & \geq & 1 - \frac{2}{|V\_i|} \\\\
      & = & 1- \frac{2}{n-i+1}
\end{array}$$

因此，得到最终结果：

$$\begin{array} {lcl}
p\_{\text{correct}} & = & \Pr[\,\text{a minimum cut is returned by }RandomContract\,]\\\\
                    & \geq & \Pr[\,C\mbox{ is returned by }{RandomContract}\,]\\\\
                    & = & \Pr[\,e\_i\not\in C\mbox{ for all }i=1,2,\ldots,n-2\,]\\\\
                    & = & \prod\_{i=1}^{n-2}\Pr[e\_i\not\in C\mid \forall j<i, e\_j\not\in C]\\\\
                    & \geq & \prod\_{i=1}^{n-2}\left(1-\frac{2}{n-i+1}\right)\\\\
                    & = & \prod\_{k=3}^{n}\frac{k-2}{k}\\\\
                    & = & \frac{2}{n(n-1)}.
\end{array}$$

### 定理

> **定理**：对于任意具有n个顶点的多图，随机收缩算法返回一个最小割的概率至少是$\frac{2}{n(n-1)}$,当我们独立地运行$t=\frac{n(n-1)\ln n}{2}$次时，一个最小割被找到的概率为：
$$\begin{array} {lcl}
&\quad & 1-\Pr[\,\mbox{all }t\mbox{ independent runnings of } RandomContract\mbox{ fails to find a min-cut}\,] \\\\
& = & 1-\Pr[\,\mbox{a single running of }{RandomContract}\mbox{ fails}\,]^{t} \\\\
& \geq & 1- \left(1-\frac{2}{n(n-1)}\right)^{\frac{n(n-1)\ln n}{2}} \\\\
& \geq & 1-\frac{1}{n}.
\end{array}$$

随机收缩算法时间复杂度为$O(n^2)$,所以总的时间复杂度为：$O(n^4\log n)$

### 推论

根据上述定理，一个随机收缩算法找到一个正确的最小割的概率至少是$\frac{2}{n(n-1)}$，则最小割至多有$\frac{n(n-1)}{2}$个。

## Fast Min-Cut

在随机收缩算法中，$p\_C \ge\prod\_{i=1}^{n-2}\left(1-\frac{2}{n-i+1}\right).$,随着i增加$\left(1-\frac{2}{n-i+1}\right)$值减小，这就意味着成功的概率会随着收缩程度的增加而不断减小，此时剩余的点变少。

因此，我们考虑如下的分段算法：首先使用随机收缩算法减少顶点的个数到一个相当小的程度，然后在每一个小的实例里寻找最小割，这样的算法称为FastCut。

### FastCut算法

### 算法

- 预处理算法：RandomContract

>RandomContract(G,t)

>Input: multi-graph $G(V,E)$, and integer $t \ge 2$;
while $| V | > t$ do
  - choose an edge ${uv}\in E$ uniformly at random;
  - $G = Contract(G,uv)$;
return $G$;

- FastCut算法

>FastCut(G)

>Input: multi-graph $G(V,E)$;

>if $|V|\le 6$ then return a mincut by brute force;

>else let $t=\left\lceil1+|V|/\sqrt{2}\right\rceil$;
- $G\_1$ = $RandomContract(G,t)$;
- $G\_2$ = $RandomContract(G,t)$;
- return the smaller one of $FastCut(G\_1)$ and $FastCut(G\_2)$;

### 分析

**随机收缩过程**：

$$\begin{array} {lcl}
&\quad & \Pr[C\text{ survives all contractions in }RandomContract(G,t)]\\\\
& = & \prod\_{i=1}^{n-t}\Pr[C\text{ survives the }i\text{-th contraction}\mid C\text{ survives the first }(i-1)\text{-th contractions}]\\\\
& \ge & \prod\_{i=1}^{n-t}\left(1-\frac{2}{n-i+1}\right)\\\\
& = & \prod\_{k=t+1}^{n}\frac{k-2}{k}\\\\
& = & \frac{t(t-1)}{n(n-1)}.
\end{array}$$

当$t=\left\lceil1+n/\sqrt{2}\right\rceil$, 上式概率至少是$ 1 / 2$

**FastCut过程**:

p(n)为C被FastCut(G)返回的概率：

$$\begin{array} {lcl}
p(n) & = & \Pr[C\text{ is returned by }\textit{FastCut}(G)]\\\\
     & = & 1-\left(1-\Pr[C\text{ survives in }G\_1\wedge C=\textit{FastCut}(G\_1)]\right)^2\\\\
     & = & 1-\left(1-\Pr[C\text{ survives in }G\_1]\Pr[C=\textit{FastCut}(G\_1)\mid C\text{ survives in }G\_1]\right)^2\\\\
     & \ge & 1-\left(1-\frac{1}{2}p\left(\left\lceil1+n/\sqrt{2}\right\rceil\right)\right)^2,
\end{array}$$

基本案例是$ p(n) = 1\ for\ n\le 6.$

$$p(n)=\Omega\left(\frac{1}{\log n}\right).$$

时间复杂度为：

$$T(n)=2T\left(\left\lceil1+n/\sqrt{2}\right\rceil\right)+O(n^2),$$

$T(n) = O(1)\ for\ n\le 6$, 因此$ T(n) = O(n^2logn)$.

