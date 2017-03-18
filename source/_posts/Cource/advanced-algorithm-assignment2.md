---
title: 高级算法--作业2
comments: true
toc: false
date: 2016-11-03 15:41:02
categories: Cource
tags : 高级算法
keywords: 作业
mathjax : true
---

>**本节内容：**高级算法：第二次作业。

<!-- more -->


## Problem 1

Consider the following optimization problem.

>*Instance*: **n** positive integers $x\_1<x\_2<\cdots <x\_n$.
Find two *disjoint* nonempty subsets $A,B\subset\{1,2,\ldots,n\}$ with $\sum\_{i\in A}x\_i\ge \sum\_{i\in B}x\_i$, such that the ratio $\frac{\sum\_{i\in A}x\_i}{\sum\_{i\in B}x\_i}$ is minimized.

Give a pseudo-polynomial time algorithm for the problem, and then give an FPTAS for the problem based on the pseudo-polynomial time algorithm.


---
### My Solution

1>. **PTAS Algorithm**

 Let $X =\sum\_{i=1}^{n}x\_i$ , and table $T = [1, \ldots,X][ 1, \ldots,X]$, each value in $T\_i[a][b]$ is a boolean value which indicates if there are disjoint subset **A** and **B** that the sum of element in $X\_A$ is **a** and the sum of element in $X\_B$ is **b** . For any $x\_i$ can be in $X\_A$ or $X\_B$ or neither of them.

 The table can be computed as follows:

$$T\_i[a][b]=
\begin{cases}
1 & \text{if} \  T\_{i-1}[a][b]=1  \\\\
1 & \text{if} \  T\_{i-1}[a-x\_i][b]=1  \\\\
1 & \text{if} \  T\_{i-1}[a][b-x\_i]=1  \\\\
0 & otherwise
\end{cases}$$

  After filling the table,  it takes overall $O(nX^2)$ time, the optimal result can be found by choosing the minimum ratio $a/b$ with the constraint that $T[a][b]=1\  \text{and} \  a \ge b$.  This can be done in $O(X^2)$ time, after get the optimal value, we can re-construct the set $A,B$ by the following algorithm:

> Algorithm: **Re-Construct-Set**
>
> **Init**: A,B = $\emptyset$
>
> **Input**: a, b, i
>
> **Output**: set A, B
>
> **Steps:**
>
> &emsp;&emsp; if i == 1:
>
> &emsp;&emsp;&emsp;&emsp; if a == $x\_1$:
>
> &emsp;&emsp;&emsp;&emsp; &emsp;&emsp;  return ({a}, $\emptyset$)
>
> &emsp;&emsp;&emsp;&emsp; if b == $x\_1$:
>
> &emsp;&emsp;&emsp;&emsp; &emsp;&emsp;  return ($\emptyset$,{b})
>
> &emsp;&emsp; if $T\_{i-1}[a][b]$ == 1:
>
>  &emsp;&emsp;&emsp;&emsp; return Re-Construct-Set(a,b,i-1):
>
>  &emsp;&emsp; if $T\_{i-1}[a-x\_i][b]$ == 1:
>
>  &emsp;&emsp;&emsp;&emsp; (A,B)$\Leftarrow$ Re-Construct-Set(a,b,i-1):
>
>  &emsp;&emsp;&emsp;&emsp; return ($A \bigcup \{x\_i\}$, B) :
>
>  &emsp;&emsp; if $T\_{i-1}[a][b-x\_i]$ == 1:
>
>  &emsp;&emsp;&emsp;&emsp; (A,B)$\Leftarrow$ Re-Construct-Set(a,b,i-1):
>
>  &emsp;&emsp;&emsp;&emsp; return (A, $B \bigcup \{x\_i\}$) :

This recursive algorithm takes $O(n)$ time,  so all in all, the running time is $O(nX^2)$, it is a  pseudo-polynomial time algorithm.

2>. **FPTAS Algorithm** (unsolved)
 According to the algorithm above, we know that the  input  of $x\_1, x\_2, \ldots, x\_n$ is  ascending.

<p>
<p>
<p>
<p>
<p>
<p>
<p>
<p>


---

## Problem 2

In the *maximum directed cut* (MAX-DICUT) problem, we are given as input a directed graph *G(V,E)*. The goal is to partition *V* into disjoint *S* and *T* so that the number of edges in $E(S,T)=\{(u,v)\in E\mid u\in S, v\in T\}$ is maximized. The following is the integer program for MAX-DICUT:

\begin{align}
\text{maximize} &&& \sum\_{(u,v)\in E}y\_{u,v}\\\\
\text{subject to} && y\_{u,v} &\le x\_u, & \forall (u,v)&\in E,\\\\
&& y\_{u,v} &\le 1-x\_v, & \forall (u,v)&\in E,\\\\
&& x\_v &\in\{0,1\}, & \forall v&\in V,\\\\
&&  y\_{u,v} &\in\{0,1\}, & \forall (u,v)&\in E.
\end{align}

Let $x\_v^\*,y\_{u,v}^\*$ denote the optimal solution to the **LP-relaxation** of the above integer program.

- Apply the randomized rounding such that for every $v\in V, \hat{x}\_v=1$ independently with probability $x\_v^*$. Analyze the approximation ratio (between the expected size of the random cut and OPT).

- Apply another randomized rounding such that for every $v\in V, \hat{x}\_v=1$ independently with probability $1/4+x\_v^*/2$. Analyze the approximation ratio for this algorithm.

---
### My Solution

1. let **OPT** be the maximum weight of *MAX-DICUT*, and it equals the value of given *ILP* algorithm,  and the result of the optimal  LP-relaxation is $OPT\_{LP}$, the probability of points(u,v) in cut is:

$$\begin{array} {lcl}
\Pr((u,v)\ in\ cut) & = & \Pr(u \in S \ and \ v \in T) \\\\
        & = & \Pr(u \in S) \cdot \Pr(v \in T)\\\\
        & = & x\_u(1 -x\_v) \\\\
        & = &\frac{1}{2}x\_u+x\_u(\frac{1}{2}-x\_v)\\\\
        & \ge & \frac{1}{2}y\_{u,v} + x\_u(\frac{1}{2}-x\_v)
\end{array}$$

since that $\sum\_{(u,v \in E)}x\_u(\frac{1}{2}-x\_v) \ge 0 $, so the total number of cuts W is as follows:

  $$E(W)= \sum\_{(u,v \in E)} \Pr((u,v)\ in\ cut) \ge \sum\_{(u,v \in E)}\frac{y\_{u,v}}{2} \ge \frac{OPT\_{LP}}{2} \ge \frac{OPT}{2}$$

   The approximation ratio for this algorithm is 0.5.

2. let **OPT** be the maximum weight of *MAX-DICUT*, and it equals the value of given *ILP* algorithm,  and the result of the optimal  LP-relaxation is $OPT\_{LP}$, the probability of points(u,v) in cut is:

$$\begin{array} {lcl}
\Pr((u,v)\ in\ cut) & = & \Pr(u \in S \ and \ v \in T) \\\\
        & = & \Pr(u \in S) \cdot \Pr(v \in T)\\\\
        & = & (\frac{1}{4}+\frac{x\_u}{2})(1 - (\frac{1}{4}+\frac{x\_v}{2})) \\\\
        & = & (\frac{1}{4}+\frac{x\_u}{2})(\frac{1}{4}+\frac{1-x\_v}{2})\\\\
        & \ge & (\frac{1}{4}+\frac{y\_{u,v}}{2})(\frac{1}{4}+\frac{y\_{u,v}}{2})  \\\\
        & = & (\frac{1}{4}-\frac{y\_{u,v}}{2})^2 + \frac{y\_{u,v}}{2}  \\\\
        & \ge & \frac{y\_{u,v}}{2}
\end{array}$$

So, the total number of cuts W is as follows:

$$E(W)= \sum\_{(u,v \in E)} \Pr((u,v)\ in\ cut) \ge \sum\_{(u,v \in E)}\frac{y\_{u,v}}{2} \ge \frac{OPT\_{LP}}{2} \ge \frac{OPT}{2}$$

 The approximation ratio for this algorithm is 0.5.

---

## Problem 3

Recall the MAX-SAT problem and its integer program:

\begin{align}
\text{maximize} &&& \sum\_{j=1}^my\_j\\\\
\text{subject to} &&& \sum\_{i\in S\_j^+}x\_i+\sum\_{i\in S\_j^-}(1-x\_i)\ge y\_j, && 1\le j\le m,\\\\
&&& x\_i\in\{0,1\}, && 1\le i\le n,\\\\
&&& y\_j\in\{0,1\}, && 1\le j\le m.
\end{align}

Recall that $S\_j^+,S\_j^-\subseteq\{1,2,\ldots,n\}$ are the respective sets of variables appearing positively and negatively in clause $j$.

Let $x\_i^\*,y\_j^\*$ denote the optimal solution to the **LP-relaxation** of the above integer program. In our class we learnt that if $\hat{x}\_i$ is round to 1 independently with probability $x\_i^*$, we have approximation ratio $1 − 1 / e$.

We consider a generalized rounding scheme such that every $\hat{x}\_i$ is round to 1 independently with probability $f(x\_i^*)$ for some function $f:[0,1]\to[0,1]$ to be specified.

- Suppose $f(x)$ is an arbitrary function satisfying that $1-4^{-x}\le f(x)\le 4^{x-1}$ for any $x\in[0,1]$. Show that with this rounding scheme, the approximation ratio (between the expected number of satisfied clauses and OPT is at least $3 / 4$.
- Is it possible that for some more clever $f$ we can do better than this? Try to justify your argument.

---
### My Solution

1. let $g(x) = 1 -4^{-x}, x \in[0,1], g(x) \le f(x) \le 1 - g(1-x), g\prime\prime(x)<0$, then *g(x)* is <u>monotonically increasing and concavity</u>, $0 \le g(x) \le \frac{3}{4}, g(x) \ge  \frac{3}{4}x$, let $X= \sum\_{i=1}^k x\_i\prime, x\_i\prime= x\_i\ for\ i \in [0,l],\  x\_i\prime= 1 -  x\_i\ for\ i \in (l,k]$, then $g(X) \ge \frac{3}{4}X, X \in [0,1], g(X) \ge \frac{3}{4}, X \in [1,\infty]$

$$\begin{array} {lcl}
\Pr(S\_j\ is \ satisfied) & = & 1 - \prod\_{i \in S\_j^+}(1 - f(x\_i))\prod\_{i \in S\_j^-}f(x\_i)\\\\
        & \ge &1 - \prod\_{i =1}^l(1 - g(x\_i))\prod\_{i =l+1}^k(1 - g(1 - x\_i))\\\\
        & = & 1 - \prod\_{i=1}^k(1- g(x\_i^\prime)) = 1 - \prod\_{i=1}^k(4^{-x\_i\prime}) =  1 - \prod\_{i=1}^k(4^{-X}) \\\\
        & = & g(X) \\\\
        & \ge & \frac{3}{4} min(1, \sum\_{i=1}^k x\_i^\prime) =  \frac{3}{4} min(1, \sum\_{i=1}^l x\_i + \sum\_{i = l+1}^k(1-x\_i))\\\\
        & \ge & \frac{3}{4} min(1, y\_i) \ge \frac{3}{4} y\_i
\end{array}$$

   So,  $OPT \ge OPT\_{LP} = \sum\_{j=1}^my\_j^*$, the approximation ratio  = $\frac{3}{4}$

2. It is impossible that for some more clever f we can do better than this.

  Considering the symmetric property of $1- f(x\_i)$ and $f(x\_i)$, the best result is that they are equal, and the expectation of $S\_j$ is not satisfied is $\frac{1}{4^{y\_j^\ast} }$, so $\Pr(S\_j\ is \ satisfied) \ge 1 - \frac{1}{4^{y\_j^\ast} } \ge \frac{3}{4}{y\_j^\ast}$

---

## Problem 4

Recall that the instance of **set cover** problem is a collection of *m* subsets $S\_1,S\_2,\ldots,S\_m\subseteq U$, where $U$ is a universe of size $n = | U | $. The goal is to find the smallest $C\subseteq\{1,2,\ldots,m\}$ such that $U=\bigcup\_{i\in C}S\_i$. The frequency *f* is defined to be $\max\_{x\in U}|\{i\mid x\in S\_i\}|$.

- Give the primal integer program for set cover, its LP-relaxation and the dual LP.
- Describe the complementary slackness conditions for the problem.
- Give a primal-dual algorithm for the problem. Present the algorithm in the language of primal-dual scheme (alternatively raising variables for the LPs). Analyze the approximation ratio in terms of the frequency *f*.

---
### My Solution

1>.

(1)**Primal Integer Program**

Minimize:  $\sum\_{j=1}^m c\_j x\_j$

Subject to:
- $\sum\_{j:e\_i \in S\_j}x\_j \ge 1, i = 1,2,\ldots, n$, *or* :$\sum\_{j=1}^ma\_{i,j}x\_j \ge 1, i = 1,2,\ldots, n$
- $x\_j=\{0,1\}, j = 1,2,\ldots, m$

    (2)**LP-relaxation**
    Minimize:  $\sum\_{j =1}^mc\_jx\_j$
    Subject to:
- $\sum\_{j:e\_i \in S\_j}x\_j \ge 1, i = 1,2,\ldots, n$, *or* :$\sum\_{j=1}^ma\_{i,j}x\_j \ge 1, i = 1,2,\ldots, n$
- $x\_j \ge 0, j = 1,2,\ldots, m$

    (3) **Dual LP**
    Maximize: $\sum\_{i=1}^n y\_i$
    Subject to:
    - $\sum\_{j:e\_i \in S\_j}y\_j \le c\_j, j=1, 2,\ldots, m$, *or* :$\sum\_{i=1}^na\_{i,j}y\_j \le c\_j, j = 1,2,\ldots, m$

    - $y\_i \ge 0, i =1,2,\ldots,n$


2>.Complementary Slackness Conditions

>$\forall$ feasible primal solution x and dual solution y, x and y are both optimal if:
>
>- For each $1 \le i \le n\ ,either\ \sum\_{j=1}^m a\_{i,j}x\_j =1\ or\ y\_i\ =\ 0\ $
>
>- For each $1 \le j \le m\ ,either \ \sum\_{i=1}^na\_{i,j}y\_j=c\_j\ or\ x\_j=\ 0 $
>
>
>$\forall$ feasible primal solution x and dual solution y, for $\alpha,\beta \ge 1$:
>
>- For each $1 \le i \le n\ ,either\ 1 \le \sum\_{j=1}^m a\_{i,j}x\_j \le \beta \ or\ y\_i\ =\ 0\ $
>
>- For each $1 \le j \le m\ ,either \ c\_j \ge  \sum\_{i=1}^na\_{i,j}y\_j \ge \frac{c\_j}{\alpha}\ or\ x\_j=\ 0 $

3>.

> 1. Initially $x=0,y=0$;
>
> 2. While $E \neq \emptyset:$
>
>3. &emsp;&emsp;Pick an $e$ uncovered and raise $y\_e$ until some set goes tight
>
> 4. &emsp;&emsp;Pick all tight sets in the cover and update $x$
>
>5.  &emsp;&emsp;Delete these sets from $E$
>
> 6. Output the set cover $x$

here, $cx \le \alpha\beta y \le \alpha\beta OPT$, in primal conditions, the variables will be incremented integrally, $x\_j \neq 0 \Rightarrow \sum\_{j:e\_i \in S\_j}y\_j  = c\_j, \alpha =1$, in the dual conditions, each element having a nonzero dual value can be covered at most f times, $y\_i \neq 0 \Rightarrow \sum\_{j:e\_i \in S\_j}x\_j  \le f, \beta =f$.
So, the approximation ratio is $f$.






---



