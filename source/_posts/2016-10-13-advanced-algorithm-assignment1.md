---
title: 高级算法--作业1
comments: true
toc: true
date: 2016-10-13 20:41:02
categories: AdvancedAlgorithms
tags : 算法
keywords: 作业
mathjax : true
---

>**本节内容：**高级算法：第一次作业。

<!-- more -->

## Problem 1

For any $\alpha\ge 1$, a cut $C$ in an undirected (multi)graph $G(V,E) $ is called an **α-min-cut** if $|C|\le\alpha|C^*| $ where $ C^*$ is a min-cut in $G$.

1. Give a lower bound to the probability that Karger's Random Contraction algorithm returns an **α-min-cut** in a graph $G(V,E) $ of n vertices.

2. Use the above bound to estimate the number of distinct **α-min** cuts in $G$.

## Problem 2

Let $G(V,E) $ be an undirected graph with positive edge weights $w:E\to\mathbb{Z}^+$. Given a partition of $V$ into $k$ disjoint subsets $S\_1,S\_2,\ldots,S\_k$, we define
$w(S\_1,S\_2,\ldots,S\_k)=\sum\_{uv\in E\atop \exists i\neq j: u\in S\_i,v\in S\_j}w(uv)$
as the cost of the k-cut $\{S\_1,S\_2,\ldots,S\_k\}$. Our goal is to find a **k-cut** with maximum cost.

1. Give a poly-time greedy algorithm for finding the weighted max $k-cut$. Prove that the approximation ratio is $(1 − 1 / k)$.

2. Consider the following local search algorithm for the weighted max cut **(max 2-cut)**.

    Fill in the blank parenthesis. Give an analysis of the running time of the algorithm. And prove that the approximation ratio is *0.5*.


>start with an arbitrary bipartition of $V$ into disjoint $S\_0,S\_1$;
>
>  while (true) do
>
>   &emsp;&emsp;if $\exists i\in\{0,1\}$ and $v\in S_i$ such that (______________)
>
>   &emsp;&emsp;&emsp;then $v$ leaves $S\_i$ and joins $S\_{1 − i}$;
>
>   &emsp;&emsp;&emsp;continue;
>
>   &emsp;&emsp;end if
>
>   &emsp;&emsp;break;
>
>end


## Problem 3

Given $m$ subsets $S\_1,S\_2,\ldots, S\_m\subseteq U $of a universe $U$ of size $n$, we want to find a $C\subseteq\{1,2,\ldots, n\}$ of fixed size $k = | C |$ with the *maximum coverage* $\left|\bigcup\_{i\in C}S\_i\right|$.

- Give a poly-time greedy algorithm for the problem. Prove that the approximation ratio is $1 − (1 − 1 / k)k > 1 − 1 / e$.

## Problem 4

We consider minimum makespan scheduling on parallel identical machines when jobs are subject to **precedence constraints**.

We still want to schedule $n$ jobs $j=1,2,\ldots, n$ on $m$ identical machines, where job $j$ has processing time $p_j$. But now a partial order $\preceq$ is defined on jobs, so that if $j\prec k $ then job $j$ must be completely finished before job $k$ begins. The following is a variant of the *List algorithm* for this problem: we still assume that the input is a list of $n$ jobs with processing times $p_1,p_2,\ldots, p_n$.

>whenever a machine becomes idle
>
>  &emsp;&emsp;assign the next *available job* on the list to the machine;


Here a job $k$ is available if all jobs $j\prec k$ have already been completely processed.

- Prove that the approximation ratio is 2.

## Problem 5

For a **hypergraph** $H(V,E)$ with vertex set $V$, every *hyperedge* $e\in E$ is a subset $e\subset V$ of vertices, not necessarily of size 2. A hypergraph $H(V,E)$ is **k-uniform** if every hyperedge $e\in V$ is of size $k = | e | $.

A **hypergraph** $H(V,E)$ is said to have **property B** (named after Bernstein) if $H$ is 2-coloable; that is, if there is a proper 2-coloring $f:V \to \\{ {\color{red}{R},\color{blue}{B}} \\}$ which assigns each vertex one of the two colors $\color{red}{Red}$ or $\color{blue}{Blue}$, such that none of the hyperedge is monochromatic.

1. Let $H(V,E)$ be a **k-uniform hypergraph** in which every hyperedge $e\in E$ shares vertices with at most d other hyperedges.
    - Prove that if $2\mathrm{e}\cdot (d+1)\le 2^{k}$, then $H$ has **property B**.
    
    - Describe how to use **Moser's recursive Fix** algorithm to find a proper **2-coloring** of $H$. Give the pseudocode. Prove the condition in in terms of $d$ and $k$ under which the algorithm can find a 2-coloring of $H$ with high probability.
    
    - Describe how to use Moser-Tardos random solver to find a proper 2-coloring of H. Give the pseudocode. Prove the condition in in terms of d and k under which the algorithm can find a 2-coloring of $H$ within bounded expected time. Give an upper bound on the expected running time.
    
2. Let $H(V,E)$ be a hypergraph (not necessarily uniform) with at least $n\ge 2 $ vertices satisfying that
$\forall v\in V, \sum\_{e\ni v}(1-1/n)^{-|e|}2^{-|e|+1}\le \frac{1}{n}$.

- Prove that $H$ has **property B**.

- Describe how to use Moser-Tardos random solver to find a proper 2-coloring of $H$. Give an upper bound on the expected running time.
