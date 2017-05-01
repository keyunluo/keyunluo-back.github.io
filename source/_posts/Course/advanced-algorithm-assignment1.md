---
title: 高级算法--作业1
comments: true
toc: false
date: 2016-10-13 20:41:02
categories: Course
tags : 高级算法
keywords: 作业
mathjax : true
---

>**本节内容：**高级算法：第一次作业。

<!-- more -->

## Problem 1

For any $\alpha\ge 1$, a cut $C$ in an undirected (multi)graph $G(V,E)$ is called an **α-min-cut** if $|C|\le\alpha|C^\*|$ where $C^\*$ is a min-cut in $G$.

1. Give a lower bound to the probability that Karger's Random Contraction algorithm returns an **α-min-cut** in a graph $G(V,E) $ of n vertices.

2. Use the above bound to estimate the number of distinct **α-min** cuts in $G$.


------
### My Solution

1.From Karger's algorithm we know that the maximum size of α-min-cut **C** is **α|C\*|**, the degree of multi-graph $G\_i:degree(G\_i) = 2|E\_i| \geq |V\_i| |C^*| \ge |V\_i|\cdot|C|/ \alpha $ ,so
the probability $p\_i =\Pr[e\_i \notin C \vert \forall j < i,e\_i \notin C]$ can be computed as:

$$\begin{array} {lcl}
p\_i  & = & 1 - \frac{\vert C \vert}{\vert E\_i \vert} \\\\
& \geq & 1 - \frac{2\alpha}{|V\_i|} \\\\
& = & 1- \frac{2\alpha}{n-i+1}
\end{array}$$

and further, apply the Karger's algorithm until **2α** vertices remain:

$$\begin{array} {lcl}
p\_{\text{survives until 2α vertices remain}} & \geq & \Pr[\,C\_{2α}\mbox{ is returned by }{RandomContract}\,]\\\\
& = & \Pr[\,e\_i\not\in C\mbox{ for all }i=1,2,\ldots,n-2α\,]\\\\
& = & \prod\_{i=1}^{n-2α}\Pr[e\_i\not\in C\mid \forall j<i, e\_j\not\in C]\\\\
& \geq & \prod\_{i=1}^{n-2α}\left(1-\frac{2α}{n-i+1}\right)\\\\
& = & \frac{(n-2α)!}{n!/(2α)!} \\\\
& = & \frac{(n-2α)!(2α)!}{n!} \\\\
& = & \frac{1}{n \choose 2α}
\end{array}$$

then, choose a random cut in the remaining multi-graph:G

$$\begin{array} {lcl}
p\_{\text{correct}} & = & \Pr[\text{C survives until 2α vertices remain}] \times \Pr[\text{C survives random cut}]\\\\
& \geq & \frac{1}{n \choose 2α} \times \frac{2}{2^{2α}}\\\\
& = & \frac{(2α)!}{2^{2α-1}} \times \frac{(n-2α)!}{n!}\\\\
& \geq & \frac{(2α)!}{2^{2α-1}} \times \frac{1}{n^{2α}}\\\\
& \geq & \frac{1}{n^{2α}}
\end{array}$$

So, a lower bound to the probability that Karger's Random Contraction algorithm returns an α-min-cut in a graph G(V,E) of n vertices can be $n^{-2α}$.


2.By the analysis of Karger's algorithm, we know $p\_C\ge\frac{1}{n^{2α}}$. And since $p\_{correct}$ is a well defined probability, due to the unitarity of probability, it must hold that $p\_{\text{correct}}\le 1$. Therefore:

$$1 \ge p\_{correct} = \sum\_{C \in \sf{C}}p\_C \ge |\sf{C}| \frac{1}{n^{2α}}$$

and this means that $ |\sf{C}| \le n^{2α}$, So  the up bound of the number of distinct α-min cuts in G is $ n^{2α}$.


------

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


------

### My Solution

1.(1) **algorithm**

>$S\_1={v\_1},S\_2={v\_2},\ldots,S\_k={v\_k}$,$V =V - \{v\_1,v\_2,\ldots,v\_k\} $  ;

>while ( V is not empty) do:

> &emsp;&emsp; Randomly select  a vertic $v$ in V;

> &emsp;&emsp; $V = V- v$

> &emsp;&emsp; put $v$ to $S\_j$ for the biggest increased value

(2) **proof**:

$$\begin{array} {lcl}
E(w) & = & \sum\_{(i,j) \in E} w\_{i,j} \Pr[i \in V\_m,j\in V\_n, \forall m \neq n] \\\\
        & = & \sum\_{(i,j) \in E} w\_{i,j}  (1 - \Pr[i \in V\_m,j\in V\_m, \forall m ]) \\\\
        & = & \sum\_{(i,j) \in E} w\_{i,j}  (1 - \sum\_{m=1}^k\Pr[i \in V\_m,j\in V\_m]) \\\\
        & = & \sum\_{(i,j) \in E} w\_{i,j}  (1 - \sum\_{m=1}^k\frac{1}{k^2}) \\\\
        & = & (1-\frac{1}{k})\sum\_{(i,j) \in E} w\_{i,j}  \\\\
        & \ge & (1-\frac{1}{k})OPT
\end{array}$$


2.(1) the **blank**: $\underline{v \in S\_{1-i} \ has\ bigger\ cut \ result}$

(2) **analysis**: calculate the cut  between $S\_0,S\_1$ is $O(n^2)$, and in each loop the value is increaed at least 1, thus the maximum possible cut value is $\sum\_{e \in E}w\_e$,then the loop size is $\sum\_{e \in E}w\_e$ at most. finally the total running time is $O(n^2 \cdot \sum\_{e \in E}w\_e)$.

(3) **proof**:
- the *OPT* can't be larger than the sum of all egde weights, then : $\sum\_{e \in E} w\_e\geq OPT $

- if we move $v$ from $S\_i \  to \ S\_{1-i}$, then $\sum\_{v \in S\_{1-i}, (u,v) \in E}w(u,v) \geq \sum\_{v \in S\_{i}, (u,v) \in E}w(u,v) $,
according to the mean value theorem, we have $\sum\_{v \in S\_{1-i}, (u,v) \in E}w(u,v) \geq \frac{1}{2}\sum\_{v :(u,v) \in E}w(u,v)$, and  for any $u' \in S\_{1-i}$ , we can get $\sum\_{v \in S\_i, (u',v) \in E}w(u',v) \geq \frac{1}{2}\sum\_{v :(u',v) \in E}w(u',v)$ similarly.

Finally, for all vertices in V, the max-cut is :

$$\begin{array} {lcl}
V(S) &= & \sum\_{u \in S\_i,v \in S\_{1-i},(u,v) \in E } \\\\
        & = & \sum\_{v \in S\_i, (u,v) \in E}w(u,v) + \sum\_{v \in S\_{1-i}, (u,v) \in E}w(u,v)  \\\\
        & \ge & \frac{1}{2}\sum\_{v :(u',v) \in E}w(u,v) + \frac{1}{2}\sum\_{v :(u,v) \in E}w(u,v) \\\\
        & = & \frac{1}{2} \sum\_{e \in E}w\_e \\\\
        & \ge &\frac{1}{2}OPT
 \end{array}$$

------


## Problem 3

Given $m$ subsets $S\_1,S\_2,\ldots, S\_m\subseteq U $of a universe $U$ of size $n$, we want to find a $C\subseteq\{1,2,\ldots, n\}$ of fixed size $k = | C |$ with the *maximum coverage* $\left|\bigcup\_{i\in C}S\_i\right|$.

- Give a poly-time greedy algorithm for the problem. Prove that the approximation ratio is $1 − (1 − 1 / k)k > 1 − 1 / e$.


------

### My Solution

**algorithm**:

>**GreedyCover**

>Input: sets $S\_1,S\_2,\ldots,S\_m$;

>initially, $U=\bigcup\_{i=1}^mS\_i$, and $C=\emptyset$, count = 0;

>while $U\neq\emptyset$ and count < k do

>&emsp;&emsp;find $i\in\{1,2,\ldots, m\}$ with the largest $|S\_i\cap U|$;

>&emsp;&emsp;let $C=C\cup\{i\}$and $U=U-  S\_i$;

>&emsp;&emsp;count = count + 1

>return C;

**proof**:
let $c\_i$ denote the elements of selected $S\_j$ in the i-th round , and $C\_i=\sum\_{i=1}^ic\_i$, the optimum solution is $OPT$, the remaining elements in $OPT$ is $R\_i$, we have: $R\_i = OPT - C\_i,R\_0 = OPT, C\_0 =0$.

At each round , the Greedy algorithm selects the subset $S\_j$ with maximum size of uncoverd elements yet,  there exists one set that cover at least $\frac{1}{k}$ fraction of the remaning uncovered elements $R\_i$, so we have $c\_{i+1} \ge \frac{R\_i}{k}$.

On the other hand, consider  the  conclusion that we want to prove:$C\_k  = OPT - R\_k \ge (1- (1-\frac{1}{k})^k)\cdot OPT$, that equals to: $R\_k \leq (1- \frac{1}{k})^k \cdot OPT$,it is hard to prove it directly, we use  the technique of mathematical induction: $R\_i \leq (1- \frac{1}{k})^i \cdot OPT$

- for i =0, $R\_0 \le OPT$, it is true
- suppose  i, it is true: $R\_i \le (1 - \frac{1}{k})^i\cdot OPT$
- when it comes to i+1: $R\_{i+1} \leq R\_{i} - c\_{i+1} \leq R\_i - \frac{R\_i}{k} = R\_i(1- \frac{1}{k}) \le (1- \frac{1}{k})^{i+1}\cdot OPT$
- we have proved it!

------


## Problem 4

We consider minimum makespan scheduling on parallel identical machines when jobs are subject to **precedence constraints**.

We still want to schedule $n$ jobs $j=1,2,\ldots, n$ on $m$ identical machines, where job $j$ has processing time $p_j$. But now a partial order $\preceq$ is defined on jobs, so that if $j\prec k $ then job $j$ must be completely finished before job $k$ begins. The following is a variant of the *List algorithm* for this problem: we still assume that the input is a list of $n$ jobs with processing times $p_1,p_2,\ldots, p_n$.

>whenever a machine becomes idle
>
>  &emsp;&emsp;assign the next *available job* on the list to the machine;


Here a job $k$ is available if all jobs $j\prec k$ have already been completely processed.

- Prove that the approximation ratio is 2.


------
### My Solution

**proof**:

- with **precedence constraints**,it is known that:
   $$OPT \ge \max\_{\mathcal{A}:i \preceq j}p\_{\mathcal{A}}, OPT\ge\frac{1}{m}\sum\_{j=1}^np\_j$$

- with **greedy stratege**,it is known that the max  completion time is less than the sum of the max processing time $p\_{max}$ in  precedence constraints and the average processing time $p\_{mean}$ among m identical machines, which is:
 $$C\_{max} \le \max\_{\mathcal{A}:i \preceq j}p(\mathcal{A}) + \frac{1}{m}\sum\_{j=1}^np\_j = 2\cdot OPT$$


------


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

- Describe how to use Moser-Tardos random solver to find a proper 2-coloring of $H$. Give an <u> upper bound </u> on the expected running time.


------
### My Solution

1.**(1)proof**:

- if $2\mathrm{e}\cdot (d+1)\le 2^{k}$, it equal to $\mathrm{e}\cdot \frac{1}{2^{k-1}} (d+1)\le 1 \qquad(1)$, on the other hand,  the size of each edge $|e| = k$,  let the probability of one edge cannot be  2-coloable in edge $e\_i$ is $Pr[e\_i]= 2 / 2^k = 1/2^{k-1} \qquad (2)$, it is a bad thing. From the two formulas above, we know that it  meets `Lovász Local Lemma `  condition . So the probability that not all bad things happen is greater than 0. In our problem, it means H has property B.

**(2)algorithm**:
> - $\phi$: k-CNF of max degree d with m clauses on n variables, $|e| = m$, for clause $C\_i$, bad event $A\_i: C\_i$ is not satisfied, which is the vertices in edge is colored the same color.
>
> - $n$ variables: $x\_1,x\_2,\ldots,x\_n, \ n$ is the total number of vertices, $x\_i=\\{0,1\\}$
>
> - For each clause C in φ, we denote by $\mathsf{vbl}(C)\subseteq\mathcal{X}$ the set of variables on which C is defined.
>
> - We also abuse the notation and denote by $\Gamma(C)=\\{\text{clause }D\text{ in }\phi\mid D\neq C, \mathsf{vbl}(C)\cap\mathsf{vbl}(D)\neq\phi\\} $ the neighborhood of **C**, i.e. the set of other clauses in φ that shares variables with C, and $\Gamma^+(C)=\Gamma(C)\cup\\{C\\}$ the inclusive neighborhood of **C**,
>
>**Solve($\phi$)**
>
>1. Pick values of $x\_1,x\_2\ldots,x\_n$ uniformly and independently at random;
>
>2.  while ∃ unsatisfied clause C  in φ :
>
>&emsp;&emsp;Fix ($C$);
>
> **Fix(C):**
>
> 1. Replace the values of variables in $\mathsf{vbl}(C)$ with uniform and independent random values;
>
> 2. while ∃ unsatisfied clause D overlapping with C:
>
> &emsp;&emsp;Fix(D);

- **proof**:
    - first, for the edges number m, we have $m \le 2^{k-1}$, that is $k \ge \log\_2^m +1$, the proof is as follows:
    the probability of one edge cannot be  2-coloable in edge $e\_i$ is $Pr[e\_i]= 2 / 2^k = 1/2^{k-1} $, therefore the probability of the bad 2-coloring is:$Pr(\bigwedge\_{i=1}^m e\_i)\le \sum\_{i=1}^mPr(e\_i)=\frac{m}{2^{k-1}} \le 1$, so $k \ge \log\_2^m +1 \qquad(1)$.
    - second,  using Moser's recursive Fix algorithm, there are **m** recursion trees, **t** total nodes, total of random bits is: $n+tk$, the sequence of random bits can be recoverd from <u>final assignment+ reciursion trees, for each recursion tree, the root uses $\left \lceil \log\_2m\right \rceil$ bits, each internal node uses at most : $\log\_2d + c $ bits</u>,  which is $m\left \lceil \log\_2m\right \rceil + t(\log\_2d + c)$, with the **Incompressibility Theorem**(N uniform random bits cannot be encoded to substantially less than N bits.), we have $n+tk \le m\left \lceil \log\_2m\right \rceil + t(\log\_2d + c)$,  and it is equal to $t(k-c-\log\_2d \le m\left \lceil \log\_2m\right \rceil + \log\_2n)$, and $k-c-\log\_2d >0$, we get : $d\le 2^{k-c} \qquad(2)$,  with `Lovász Local Lemma `, we know that $ep(d+1) \leq 1$, and  here is $p = 1/2^{k-1}$, so $d \le \frac{2^{k-1}}{e} - 1\qquad(3)$  ,and the probability the algorithm can find a 2-coloring of H is $n+tk=O(n+km\log\_2m)$ .
    - using (1) ,(2) and (3), we can get the conditions of d and k: $k \ge \log\_2^m +1$,$d \le \frac{2^{k-1}}{e} - 1$


**(3)algorithm**:

> - $\mathcal{X}$ is a set of mutually independent random variables.
>
>**RandomSolver:**
>
>1. sample all $X \in \mathcal{X}$;
>
>2. while there is non-violated bad event $A \in \mathcal{A}$:
>
>&emsp;&emsp;resample all $X \in vbl(A)$;


- **proof**:
    - from above problem, we know that  $k \ge \log\_2^m +1 \qquad(1)$.
    - with `Lovász Local Lemma `, we know that $d \le \frac{2^{k-1}}{e} - 1\qquad(2)$

2. **(1) proof(unsolved)**:

- let $k= |e|$, **m** edges, **n** vertices, $n \ge 2$, max degree of dependency graph: **d**, then we have $2(1-1/n) \ge 1$

- $\forall v\in V, \frac{1}{n} \geq \sum\_{e \ni v}(1-\frac{1}{n})^{-|e|}2^{-|e|+1} = \sum\_{e \ni v}2(2(1-1/n))^{-|e|}= 2 \sum\_{i=1}^n \frac{k\_i}{2(2-1/n)^{k\_i}}$

- $\forall i, p = \Pr[A\_i] \le \frac{2}{2^{\min(k)}} \le \frac{1}{2}, d \le \max(k\_i)$


**(2) algorithm(unsolved)**



