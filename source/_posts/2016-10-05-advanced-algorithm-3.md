---
title: 高级算法(3)--Min-Cut-Max-Flow(2)-近似算法
comments: true
toc: true
date: 2016-10-05 20:41:02
categories: AdvancedAlgorithms
tags : 算法
keywords: Min-Cut , 最小割 , Max-Flow , 最大流
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