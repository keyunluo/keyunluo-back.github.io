---
title: 计算模型导引——递归函数论
comments: true
toc: true
date: 2017-05-01 14:25:02
categories: Course
tags : 计算模型导引
keywords: 递归函数论, 南京大学
mathjax: true
---

>**本节内容：**2017年研究生课程——计算模型导引第一章课后习题(习题课整理)。


<!-- more -->

## 知识点概要

### 数论函数
> 以下三类数论函数称为本原函数(**Initial Function, IF**)
> - 零函数Z:$\mathbb{N} \rightarrow \mathbb{N}, \forall x \in \mathbb{N}.Z(x)=0$
> - 后继函数S:$\mathbb{N} \rightarrow \mathbb{N}, \forall x \in \mathbb{N}.S(x)=x+1$
> - 投影函数$P\_i^n:\mathbb{N}^n \rightarrow \mathbb{N}, \forall x\_1, x\_2, \ldots, x\_n \in \mathbb{N}. P\_i^n(x\_1, x\_2, \ldots, x\_n) = x\_i$

**基本函数(Basic Function, BF)**:
- $\mathcal{IF} \subseteq \mathcal{BF}$
- $\mathcal{BF}$对复合封闭，即对任意的$m,n \in \mathbb{N}^+, f:\mathbb{N}^m \rightarrow \mathbb{N}, g\_1, g\_2, \ldots, g\_m:\mathbb{N}^n \rightarrow \mathbb{N}, \ 若 \ f, g\_1, \ldots, g\_m \in \mathcal{BF},\ 则 \ Comp\_m^n[f,g\_1, \ldots, g\_m] \in \mathcal{BF} $

### 配对函数
> 设$pg(x,y):\mathbb{N}^2 \rightarrow \mathbb{N}, K(x): \mathbb{N} \rightarrow \mathbb{N}, L(x):\mathbb{N} \rightarrow \mathbb{N}$为数论函数， 若它们对任意的$x,y \in \mathbb{N}$, 满足：$K(pg(x,y)) = x, L(pg(x,y)) = y$, 则称pg为配对函数， K和L分别称为左函数、右函数，{pg, K, L}称为配对组。

### 初等函数
> 初等函数(Elementary Function)类$\mathcal{EF}$是满足以下条件的最小集：
> - $\mathcal{IF} \subseteq \mathcal{EF}$
> - $x+y, x\ddot{-}y, x × y， \left \lfloor x/y \right \rfloor \in \mathcal{EF}$
> - $\mathcal{EF}$对于复合、有界迭加算子$\Sigma[ \cdot ]$和有界迭乘算子$\prod[\cdot ]$封闭

### 原始递归函数
> (1)设$n \in \mathbb{N}^+, f:\mathbb{N}^n \rightarrow \mathbb{N}, g:\mathbb{N}^{n+2} \rightarrow \mathbb{N}$, 定义函数$h: \mathbb{N}^{n+1} \rightarrow \mathbb{N}$如下：$h(\vec{x}, 0) = f(\vec{x}), h(\vec{x}, y+1) = g(\vec{x}, y, h(\vec{x}, y))$称h由f和g经带参原始递归算子$Prim^n[\cdot, \cdot]$而得，记作$h=Prim^n[f,g]$.
> (2)设$a\in \mathbb{N}, g:\mathbb{N}^2 \rightarrow \mathbb{N},$ 定义函数$h:\mathbb{N} \rightarrow \mathbb{N}$如下：$h(0) = a, h(y+1) = g(y, h(y))$, 这时则称h由g经无参原始递归算子$Prim^0[\cdot, \cdot]$而得，记作$h=Prim^0[a,g]$

**原始递归函数(Primitive Recursive Functions, PRF)**:
- $\mathcal{IF} \subseteq \mathcal{BF}$
- $\mathcal{PRF}$对于复合、带参原始递归算子和无参原始递归算子封闭。


### 递归函数
>一般递归函数**(General Recursive Functions,GRF)**为满足如下条件的最小集合：
>- $\mathcal{IF} \subseteq \mathcal{GRF}$
>- $\mathcal{GRF}$对于复合和原始递归算子封闭
>- $\mathcal{GRF}$对于正则$\mu-$算子封闭

## 习题

### 1.1

>证明： 对于固定的$k$, 一元数论函数 $x+k \in \mathcal{BF}$

解答：
$x + 0 = P\_1^1 \in \mathcal{BF}, x + k = \underbrace{S ◦ S ◦ \ldots ◦ S}\_{k-1 \ 次}  = S^{k-1}(x) \in \mathcal{BF}, \forall k > 1$, 故结论成立。

### 1.2

>证明：对任意 $k \in \mathbb{N}^{+}$$，$$f: \mathbb{N}^k \rightarrow \mathbb{N}$，若 $f \in \mathcal{BF}$，则存在 $h$，使得 $f(\vec{x}) < \|\vec{x}\|+h$
其中$\|\vec{x}\| = \max\{x\_i; 1\leq i \leq k\}.$

解答：设$f \in \mathcal{BF}$

- case 1：如果 $f$ 为零函数 $Z$，后继函数 $S$，或投影函数$P\_i^n$ 之一，
  $Z(x) < x+1, \quad S(x)<x+2, \quad P\_i^n(\vec{x})<\|\vec{x}\|+1$
  显然存在这样的 $h$；
- case 2：设 $f(\vec{x}) = g(g\_1(\vec{x}), g\_2(\vec{x}), \cdots, g\_m(\vec{x}))$
  设 $g(\vec{y}) < \|\vec{y}\| + h\_0，g\_i(\vec{x\_i}) < \|\vec{x}\|+h\_i\ (i = 1,2,3, \cdots, m)$
  从而$f(\vec{x}) < \max\_{1\leq i \leq m} g\_i(\vec{x}) + h\_0<\max\_{1\leq i \leq m}(\|\vec{x}\|+ h\_i) + h\_0 < \|\vec{x}\|+ h\_0+h\_1+ \cdots + h\_m$
  取 $h = h\_0+h\_1+ \cdots + h\_m$ 即可。

### 1.3
> 证明：二元数论函数 $x+y \notin \mathcal{BF}$

解答： 假设$ x+y \in \mathcal{BF}$, 根据上述习题1.2， 从而有h使得$x + y < \max (x,y) + h$, 令$x=y=h+1$, 得到$2h+2 < 2h+1$矛盾， 故假设不成立。

### 1.4
>证明：二元数论函数 $x \dot{-}y \notin \mathcal{BF}$.

解答： 假设$x \dot{-} y \in \mathcal{BF}$, 从而$x\dot{-}1 \in \mathcal{BF}$， 然而$f(x) \in \mathcal{BF}\ 时，f(x)\geq 0 \ 或 \ =0$，得出矛盾！

### 1.5
>设 $pg(x, y) = 2^x(2y+1)\dot{-}1$，证明：存在初等函数 $K(z)$ 和 $ L(z)$ 使得
$$
K(pg(x,y))=x, L(pg(x,y))=y, pg(K(x), L(y)) = z.
$$

解答：$z = 2^x(2y+1) \dot{-}1,\ iff\  z+1 = 2^x(2y+1), \ iff\ x = ep\_0(z+1), 且 \ 2y +1 = \frac{z+1}{2^x}$, 带人x, 得$2y+1 = \left[\frac{z+1}{2^{\text{ep}\_0(z+1)}}\right], \ iff \ x = K(z), 且 \ y=L(z)$, 这里$K(z) = ep\_0(z+1), L(z) = \left[\frac{\left[\frac{z+1}{2^{\text{ep}\_0(z+1)}}\right]\dot{-}1}{2}\right]$

### 1.6
>设 $f: \mathbb{N} \rightarrow \mathbb{N}$，证明：$f$ 可以作为配对函数的左函数当且仅当对任何 $i \in \mathbb{N}$，
$|\ \{\ x\in \mathbb{N}: f(x)=i\ \}\ | = \aleph\_0.$

解答：
- "$\Rightarrow$"，设 $f$ 为配对函数 $pg(x,y)$ 的左函数
  $\because f(pg(i, j)) = i,  \forall j$
  从而对任何 $i \in \mathbb{N}$，$\therefore \{\ x\ |\ f(x) = i\ \} \supseteq \{\ pg(i,j) \ |\  j \in \mathbb{N}\ \} \sim \{\ j \ | \ j \in \mathbb{N}\ \} \sim \mathbb{N}$
  因此，$\{\ x\ |\ f(x) = i\ \}$ 无穷。


- “$\Leftarrow$”，设任何 $i \in \mathbb{N}$，$f^{-1}[\{i\}]$ 无穷，
  $\because \mathbb{N}$ 良序，
  $\therefore$ 可设 $f^{-1}[\{i\}] = \{\ a\_{ij}\  |\ j \in \mathbb{N}\ \}$
  $g: \mathbb{N} \rightarrow \mathbb{N}$ 定义如下：

  $$
  g(x) =
  \begin{cases}
  j, & \text{if } x = a\_{ij} \\\\
  0, & \text{else}
  \end{cases}
  $$
  从而对任何 $i,j \in \mathbb{N}$，$f(a\_{ij}) = i$，$g(a\_{ij}) = j$，

  令 $pg(i, j) = a\_{ij}$，$f$ 即为左函数。

### 1.7
>证明：从本原函数出发，经复合和算子 $\prod\limits\_{i=n}^{m}[\cdot]$ 可以生成所有的初等函数，这里

$$
\prod\limits\_{i=n}^{m}[f(\vec{i})] =
\begin{cases}
f(n) \cdot f(n-1) \cdot\ \cdots \ \cdot f(m), & \text{if } m\geq n\\\\
1, & \text{if } m < n
\end{cases}
$$

解答：

只需证 $\sum\limits\_{i=0}^n$ ， $\prod\limits\_{i=0}^n$ 和函数 $\ddot{-}$ 可表示出。

1.  $\prod\limits\_{i=0}^n$ 为 $\prod\limits\_{i=n}^{m}$ 的特例，取 $n=0$ 即可；
2. $x^y = \prod\limits\_{i=1}^{y}P\_1^1(x)$，从而 $2^y = \prod\limits\_{i=1}^{y}SSZ(i)$；
3. $N(x) = \prod\limits\_{i=1}^{x}Z(i)$ ；
4. $\text{leq}(x,y) = \prod\limits\_{i=x}^{y}Z(i) = \begin{cases}0, & \text{if } x \leq y \\\\
 1, & \text{if } x > y\end{cases}$
5. $\text{geq}(x, y) = \prod\limits\_{i=y}^{x} Z(i)= \begin{cases}0, & \text{if } x \geq y \\\\
1, & \text{if } x < y
\end{cases}$
6. $\text{eq}(x,y) = \text{leq}(x,y)^{N\text{geq}(x,y)}=\begin{cases}0, & \text{if } x = y \\\\
1, & \text{if } x \neq y
\end{cases}$

7. $\log(x) = \prod\limits\_{i=0}^x i^{N \text{ eq}(2^i,x)}$
   注意：$\log(2^y) = \prod\limits\_{i=0}^{2^y}i^{N\text{ eq}(2^i, 2^y)}$
8. $\sum\limits\_{i=n}^{m} f(i, \vec{x}) = \log(2^{\sum\_{i=n}^mf(i, \vec{x})}) = \log(\prod\limits\_{i=n}^{m}2^{f(i, \vec{x})})$
9. $x \cdot y = \sum\limits\_{i=1}^{x}P\_1^1(y)$
10. $x+y = \log(2^x \cdot 2^y)$
11. $x \ddot{-} y = \left(\sum\limits\_{i=y+1}^{x}SZ(i)\right) + \left(\sum\limits\_{i=x+1}^{y}SZ(i)\right)$

### 1.8
>设
$$
M(x) =
\begin{cases}
M(M(x+11)), & \text{if } x \leq 100, \\\\
x-10, & \text{if } x>100,
\end{cases}
$$
证明：
$$
M(x) =
\begin{cases}
91, & \text{if } x \leq 100, \\\\
x-10, & \text{else.}
\end{cases}
$$

解答：
只需证，当 $0\leq x \leq 100$$ 时，$$M(x) = 91$
- $M(90) = M(M(101))=M(91)=M(92) = \cdots = M(100) = M(M(111))=M(101)=91$；
- 当 $0\leq x \leq 100$ 时，存在 $k$ 使得 $90 \leq x+11k \leq 100$，从而 $M(x) = M^2(x+1*11)= M^{k+1}(x+11k) = M^kM(x+11k)=M^k(91) = 91$。

### 1.9
>证明：
$$
\min x \leq n. [f(x, \vec{y})] = n \dot{-}\max x \leq n. [f(n \dot{-}x, \vec{y})], \\\\
\max x \leq n. [f(x, \vec{y})] = n \dot{-}\min x \leq n. [f(n \dot{-}x, \vec{y})].
$$

解答：
- case 1: $f(x)\ 在 \ [0, n]$ 中有零点。
  设 $\min{x\leq n.}[f(x)] = k$，故 $f(k) = 0$，从而 $f(n \dot{-}(n\dot{-}k)) = 0$，$n-k$ 为 $g(x) = f(n\dot{-}x)$ 的零点。
  当 $k$ 为 $f(x)$ 的最小零点时，$n \dot{-} k$ 为 $g(x)$ 的最大零点，从而$n \dot{-} k = \max{x \leq n}. [f(n\dot{-}x)]$
  因此，$k = n \dot{-} \max{x\leq n}. [f(n \dot{-}x)]
- case 2: $f(x)$ 在 $[0, n]$ 中无零点，等式左边$=n$，右边$=n\dot{-}0=n$，相等。
同理，可证 $\max x \leq n. [f(x, \vec{y})] = n \dot{-}\min x \leq n. [f(n \dot{-}x, \vec{y})].$

### 1.10
> 证明：$\mathcal{EF}$ 对有界 $\max$-算子封闭.

解答：
$\because \max x \leq n. f(x, \vec{y}) = n \dot{-} \min x\leq n. f(n \dot{-}x, \vec{y})$ (习题 1.9)
$\therefore \mathcal{EF}$ 对有界 $\max$-算子封闭.

### 1.11
> Euler 函数 $\varphi : \mathbb{N} \rightarrow \mathbb{N}$ 定义为
$\varphi(n) = |\{\ x: 0<x\leq n \wedge \gcd(x,n) = 1\ \}|,$
即 $\varphi(n)$ 表示小于等于$n$ 且与 $n$ 互素的正整数个数，例如 $\varphi(1)=1$，因为 1 与其本身互素；$\varphi(9) = 6$，因为 9 与 1, 2, 4, 5, 7, 8 互素。证明：$\varphi \in \mathcal{EF}$.

解答：
我们有 $\varphi(n) = n \prod\limits\_{p\ \vert \ n}(1- \frac{1}{p})$
$\because \prod\limits\_{p\ \vert \ n} p = \prod\limits\_{i=0}^n\ \text{ IF } \  P\_i|n \ \text{ THEN }\ P\_i \ \text{ ELSE }\ 1$
$=\prod\limits\_{i=0}^n\ \text{ IF } \  \text{ep}\_i \geq 1 \ \text{ THEN }\ P\_i \ \text{ ELSE }\ 1$
$=\prod\limits\_{i=0}^n(P\_iN(1\dot{-} \text{ep}\_i(n))+N^2(1\dot{-}\text{ep}\_i(n))) \in \mathcal{EF}$
同理，$\prod\limits\_{p\ \vert \ n} (p-1) =\prod\limits\_{i=0}^n((P\_i-1)N(1\dot{-} \text{ep}\_i(n))+N^2(1\dot{-}\text{ep}\_i(n))) \in \mathcal{EF} $
$\therefore \varphi(n) = N(n\dot{-}1)+N^2(n \dot{-}1)\left[\dfrac{\prod\limits\_{p\ \vert \ n} (p-1)}{\prod\limits\_{p\ \vert \ n} p}\right] \in \mathcal{EF}$.

### 1.12
>设 $h(x)$ 为 $x$ 的最大素因子的下标，约定 $h(0)=0, h(1) = 0$。例如 $h(88) = 4$，因为 $88=2^3 \times 11$ 的最大素因子 11 是第 4 个素数 $p\_4$，其下标为 4。证明：$h \in \mathcal{EF}$。

解答：$h(0)=h(1) = 0$，当 $x \geq 2$ 时：

$\because h(x) = \max y\leq x. [\text{ep}\_y{x} \geq 1]$
$h(x) = \max y \leq x. [1 \dot{-}\text{ep}\_y(x)]$
$\therefore h \in \mathcal{EF}$

### 1.13
> 设 $f:\mathbb{N} \rightarrow \mathbb{N}$ 满足$f(0) = 1, f(1) = 1,f(x+2) =f(x)+f(x+1)$
证明：
(1) $f \in \mathcal{PRF}$；
(2) $f \in \mathcal{EF}$。

解答：
(1) 令$[a\_0, a\_1, \cdots, a\_n] \prod\limits\_{i=0}^{n} p\_i^{a\_i}$
这里 $p\_i$ 为第 $i$ 个素数，比如$p\_0=2，p\_1=3，p\_2=5$，那么 $[a\_0, a\_1, a\_2] = 2^{a\_0} \cdot 3^{a\_1} \cdot 5^{a\_2}$，为 Gödel 编码形式$ep\_i(a) = a$ 的素因子分解式中第 $i$ 个素数的指数。易见
$$
ep\_i[a\_0, a\_1, \cdots, a\_n] =
\begin{cases}
 a\_i, & i\leq n \\\\
0, & i>n
\end{cases}
$$
从而令 $F(n) = [f(n), f(n+1)]，F(0) = [f(0), f(1)] = 2^1 \cdot 3^1 = 6$
$$
\begin{array}{rl} F(n+1) & = [f(n+1), f(n+2)]\\\\
& = [f(n+1), f(n+1)+f(n)] \\\\
& = [ep\_1F(n), ep\_1F(n)+ep\_0F(n)] \\\\
& = H(F(n))\end{array}
$$
这里，$$H(x) = [ep\_1x, ep\_1x+ep\_0x] = 2^{ep\_1x} \cdot 3^{ep\_1x} \cdot 3^{ep\_0x}$$

$\therefore H(x)$ 是初等的。
又 $\because F(0)=6, F(n+1) = H(F(n))$ ，以及 $H$ 为原始递归函数
$\therefore F(n)$ 为原始递归函数
又 $\because f(n)=ep\_0F(x)$
$\therefore f(n)$ 为原始递归函数。

(2) 现在证明 $f(n)$ 是初等的。

<u>**证法一：**</u>

首先有 $f(n) \leq 2^n$ ，归纳证明如下：
当 $n=0,1$ 时，$f(0)=1 \leq 2^0 ，f(1) = 1 \leq 2^1$
归纳假设：$\forall k \leq n, f(k) \leq 2^k$
归纳步骤：当 $n<2$ 时，$f(n) \leq 2^n$ 为真，当 $ n \geq 2$ 时，
$f(n) = f(n-1)+f(n-2) \leq 2^{n-1} +2^{n-2} \leq 2^{n-2} \cdot 3 \leq 2^{n-2} \cdot 4 \leq 2^n$

其次，还有 $F(n) \leq G(n)$ ，这里 $G(n) = 2^{2^n}\cdot 3^{2^{n+1}}$，且 $G(n)$ 是初等的。

$F(n) = [f(n), f(n+1)] = 2^{f(n)} \cdot 3^{f(n+1)} \leq  2^{2^n}\cdot 3^{2^{n+1}} = G(n)$，易见 $G(n)$ 的初等性。

令$\alpha(n) = [F(0), \cdots, F(n)] \leq [G(0), \cdots, G(n)]$，有
$\alpha(n) \leq \prod\limits\_{i=0}^np\_i^{G(i)} \leq \prod\limits\_{i=0}^np\_n^{(2^{2^n}\cdot 3^{2^{n+1}} \cdot (n+1))} = \beta(n)
$
易见，$\beta(n)$ 为初等函数。
因为
$$
\begin{array}{rl}\alpha(n)  & = \mu x \leq \beta(n) \cdot (ep0x = F(0)) \wedge \forall i<n, ep{i+1}x = H(ep\_ix)  \\
 & = \mu x \leq \beta(n) \cdot[ep\_0x=6 \wedge \forall i <n , (ep\_{i+1}x \ddot{-}H(ep\_ix)=0)] \\
  &= \mu x \leq \beta(n) \cdot\left[ep\_0x=6 + \sum\_{i \rightarrow n \ddot{-}1}(ep\_{i+1}x\ddot{-}H(ep\_ix))N^2n\right] \\
   & = \mu x \leq \beta(n) \cdot \gamma(n)
\end{array}
$$
易见，$\gamma(n)$ 是初等的，所以 $\alpha(n)$ 是初等的。
因为 $f(n) = ep\_0(F(n)) = ep\_0(ep\_n(\alpha(n)))$，所以 $f(n)$ 是初等的。

**<u>证法二：</u>**
$f(n) = \frac{1}{\sqrt{5}}\left(\frac{1+\sqrt{5}}{2}\right)^{n+1} - \frac{1}{\sqrt{5}}\left(\frac{1-\sqrt{5}}{2}\right)^{n+1}
$
（该公式的证明参见 Kolman B, Busby R C. Ross S. Discrete mathematical structures. Prentice-Hall, Inc. 1996 (3rd) ）

从而
$$
\begin{array}{rl}f(n) & = \frac{1}{2^{n+1}\sqrt5}\left[(1+\sqrt 5)^{n+1} - (1-\sqrt 5)^{n+1}\right] \\\\
& = \frac{1}{2^{n+1}\sqrt5}\left[\sum\_{i=0}^{n+1}C\_{n+1}^i (\sqrt 5)^i - \sum\_{i=0}^{n+1}C\_{n+1}^i (-\sqrt 5)^i \right] \\\\
& = \frac{1}{2^{n+1}\sqrt5}\left[\sum\_{i=0}^{n+1}C\_{n+1}^i 2(\sqrt 5)^i N^2rs(i,2) \right] \\\\
& = \frac{1}{2^{n}}\left[\sum\_{i=0}^{n+1}C\_{n+1}^i 5^{\lfloor\frac{i}{2}\rfloor} N^2rs(i,2) \right] \\\\
\end{array}
$$

### 1.14
>设数论谓词 $Q(x,y,z,v)$ 定义为
$Q(x, y, z, v) \equiv p(\langle x,y,z\rangle) ~|~ v,$其中，$p(n)$ 表示第 $n$ 个素数，$\langle x,y,z\rangle$ 是 $x,y,z$ 的 Godel 编码。证明：$Q(x,y,z,v)$ 是初等的。

解答：$\because \langle x,y,z\rangle= 2^x \cdot 3^y \cdot 5^z \in \mathcal{EF} $
又 $Q(x,y,z,v)$ 的特征函数为 $N^2rs(v, p(\langle x,y,z\rangle))$
$\therefore Q \in \mathcal{EF}$

### 1.15

>设 $f:\mathbb{N} \rightarrow \mathbb{N}$ 满足
$f(0) = 1,f(1) = 4,f(2)=6, f(x+3) = f(x)+(f(x+1))^2+(f(x+2))^3$
证明：$f(x) \in \mathcal{PRF}$。

解答：
令 $g(x) = \langle f(x), f(x+1), f(x+2) \rangle, f(x) = (g(x))\_1$
$g(0) = \langle 1, 4, 6 \rangle$，
$g(x+1) = \langle (g(x))\_2, (g(x))\_3, (g(x))\_1+(g(x))\_2^2+(g(x))\_3^3 \rangle = B(g(x)) $
这里，$B(z) = \langle (z)\_2, (z)\_3, (z)\_1+((z)\_2)^2+((z)\_3)^3 \rangle \in \mathcal{PRF}$
故 $g \in \mathcal{PRF}$，从而 $f \in \mathcal{PRF}$。

### 1.16
>设 $f: \mathbb{N}\rightarrow \mathbb{N}$ 满足
$f(0)=0,f(1) = 1,f(2) = 2^2,f(3) = 3^{3^3} \cdots \cdots,
f(n) = n^{.^{.^{.^n}}} (\text{the number of } n \text{ is } n) $
证明：$f \in \mathcal{PRF} - \mathcal{EF}$

解答：

令 $g(m,n) = n^{.^{.^{.^n}}}$，具有 $m$ 个 $n$ 的形式，
$$
\begin{cases}
 g(0, n) = N^2n \\\\
 g(m+1, n) = n^{g(m,n)}
 \end{cases}
 $$，从而 $f(n) = g(n, n)$
$\because g \in \mathcal{PRF}$
$\therefore f \in \mathcal{PRF}$
以下证 $ f \notin \mathcal{EF}$
从而 $\exists k ~\forall n, f(n) < 2^{.^{.^{.^{2^n}}}} \} k$ 个 2，取 $n = k+2$，从而
$(k+2)^{.^{.^{.^{(k+2)}}}} \}k+2$ 个 $< 2^{.^{.^{.^{2^{(k+2)}}}}} \}k$ 个 2，矛盾。

### 1.17
设 $g: \mathbb{N}\rightarrow \mathbb{N} \in \mathcal{PRF}, f: \mathbb{N}^2 \rightarrow \mathbb{N}$ ，满足
$
f(x, 0)=g(x)， f(x,y+1) = f(f(\cdots f(f(x,y), y-1), \cdots),0),$
证明：$f \in \mathcal{PRF}$

解答：
证明 $f(x,y)$ 呈形 $g^{a(y)}(x)$
Basis: $ y = 0, f(x,y) = f(x,0) = g(x), a(0) = 1$
假设 $f(x,y) = g^{a(y)}(x)$，那么，
$$
\begin{array}{rl}
f(x, y+1) &=f(f(\cdots f(f(x,y), y-1), \cdots), 0) \\\\
& = g^{a(0)}(f(\cdots f(f(x,y), y-1), \cdots), 1) \\\\
& = g^{a(0)+a(1)}(f(\cdots f(f(x,y), y-1), \cdots), 2) \\\\
& = g^{a(0)+a(1)+ \cdots a(n)}(x)
\end{array}
$$
从而，$a(0)=1, a(y+1) = a(0)+a(1)+\cdots +a(y)$
易见 $a(y) \in \mathcal{PRF}$，故 $f(x,y) = g^{a(y)} (x)$
令 $h(x,y) = g^y(x)$，因为
$$
\begin{cases}
h(x, 0) = x \\\\
h(x, y+1) = g(h(x,y))
\end{cases}
$$

$\therefore h \in \mathcal{PRF}$
$\because f(x,y)= h(x, a(y))$
$\therefore f \in \mathcal{PRF}$

### 1.18
>设 $k \in \mathbb{N}^+$，函数 $f: \mathbb{N}^ k\rightarrow \mathbb{N}$ 和 $g: \mathbb{N}^k \rightarrow \mathbb{N}$尽在有穷个点取不同值，证明：$f $ 为递归函数当且仅当 $g$ 为递归函数。

解答：
设 $f$ 与 $g$ 在有穷个点取不同值，从而有 $k \in \mathbb{N}$，使当 $x > k$ 时，$f(x) =g(x)$ ，从而，

$$
f(x) =
\begin{cases}
f(x), & \text{if } x \leq k\\\\
g(x), & \text{if } x >k
\end{cases}
$$
令
$$
f^\prime(x) =
\begin{cases}
f(x), & \text{if }x \leq k\\\\
0, &\text{if }x > k
\end{cases}
$$
易见，$f^\prime \in \mathcal{PRF}$
$f(x) = f^\prime(x)N(x\dot{-}k) + g(x)N^2(x\dot{-}k)$
易见，$g \in \mathcal{PRF} \Rightarrow f \in \mathcal{PRF}$
同理，$f \in \mathcal{PRF} \Rightarrow g\in \mathcal{PRF}$

### 1.19
> 证明：$f(n) = \Bigl\lfloor\left(\frac{\sqrt{5}+1}{2}\right)n\Bigr\rfloor$
为初等函数。

解答：
$f(n) = \max x \leq 2n. x \leq \frac{\sqrt{5}+1}{2}n$
$f(n) = \max x \leq 2n. 2x \leq \sqrt{5} n+n$
$f(n) = \max x \leq 2n. 2x \ddot{-}n \leq \sqrt{5}n$
$f(n) = \max x \leq 2n. (2x \ddot{-}n)^2 \leq 5n^2$
$f(n) = \max x \leq 2n. 4x^2  \leq 4n^2 + 4xn$
$f(n) = \max x \leq 2n. x^2 \dot{-} (n^2 + xn) \in \mathcal{EF}$

### 1.20
>证明：$Ack(4,n) \in \mathcal{PRF} - \mathcal{EF}$，其中 $Ack(x,y)$ 是 Ackermann 函数。

解答：
令 $f(n) = Ack(4,n)$，
$f(0) = Ack(4,0) = Ack(3,1) = f\_3(1)=2^{1+3}-3=13$
$f(n+1) = Ack(4, n+1) = Ack(3, A(4,n)) = Ack(3, f(n)) = 2^{f(n)+3}\dot{-}3$
令 $g(n) = f(n)+3$，从而$g(0) = 16 = 2^4$，$g(n+1) = g(n)$

因此，$g(n) = \left . 2^{\cdot^{\cdot^{\cdot^2}}}  \right \\} n+3 $ 个2.

从而，$Ack(4,n) = 2^{\cdot^{\cdot^{\cdot^2}}} \dot{-}3 \in \mathcal{PRF}$

又因为：
$\lim \limits\_{n \rightarrow \infty} \frac{\left. 2^{\cdot^{\cdot^{\cdot^2}}}  \right\\} k \text{ 个 }2}{g(n)} = 0$

$\therefore g \notin \mathcal{EF}$，$\therefore f \notin \mathcal{EF}$ 。

### 1.21
> 设 $f: \mathbb{N} \rightarrow \mathbb{N}$，$f$ 为单射（1-1）且满射（onto），证明：$f\in \mathcal{GRF} \Leftrightarrow f^{-1} \in \mathcal{GRF}$

解答：
设 $f$ 为单射且满射，令 $g(x) =f^{-1}(x)$，从而
$y=g(x) \text{ iff } f(y) = x \text{ iff } y = \mu z. f(z) = x$
从而，$g(x) = \mu z. (f(z) \ddot{-}z)$
因此，$g \in \mathcal{GRF} \Leftarrow f \in \mathcal{GRF}$
同理，$f\in \mathcal{GRF} \Leftarrow g \in \mathcal{GRF}$

### 1.22
>设 $p(x)$ 为整系数多项式，令 $f: \mathbb{N} \rightarrow \mathbb{N}$ 定义为 $f(a) = p(x)-a$ 对于 $x$ 的非负整数根，证明：$f \in \mathcal{RF}$。

解答：
$f(n) = \mu x. (p(x) = n), \because p(x)$ 为整系数多项式, $\therefore$ 存在正整系数多项式 $s(x)$ 与 $t(x)$ 使 $p(x) = s(x)-t(x)$,从而$f(n) = \mu x. (s(x) \ddot{-} (n+t(x)))$. 易见，$s(x), t(x) \in \mathcal{PRF}$，故 $f \in \mathcal{RF}$。

### 1.23
> 设
$$
f(x) =
\begin{cases}
x / y, & \text{if } y \neq 0 \text{ and } y~|~x,\\\\
\uparrow, & \text{else.}
\end{cases}
$$
证明：$f \in \mathcal{RF}$。

解答：$f(x) = \mu z. (zy =x) \text{ and } (y \neq 0) = \mu z.(x \ddot{-}zy)\cdot Ny$

### 1.24
> 设 $g: \mathbb{N} \rightarrow \mathbb{N}$ 满足
$g(0)=0,g(1) = 1,g(n+2) = rs((2002g(n+1)+2003g(n)), 2005)$
(1) 试求 $g(2006)$，
(2) 证明：$g \in \mathcal{EF}$。

解答：

先证明(2)，令 $h: \mathbb{N} \rightarrow \mathbb{Z}$ 如下：
$h(0) = 0,h(1) = 1,h(n+2) = -3h(n+1)-2h(n)$
易见 $g(n) = rs(h(n), 2005)$
(注：为何这样构造？$g(n+2) = rs((2005-3)g(n+1)+(2005-2)g(n), 2005) $)

这是因为 $h(n)$ 的特征方程 $\lambda^2= -3\lambda-2$，其根为 $-1, -2$，从而 $h(n)$ 呈形 $a(-1)^n+b(-2)^n$，由 $h(0)=0, h(1) = 1$，故得 $a = 1, b = -1$，因此 $h(n) = (-1)^n-(-2)^n = (-1)^{n+1}(2^n \dot{-}1)$

从而，

$$g(n) =
\begin{cases}
2005 \dot{-} rs(2^n \dot{-}1, 2005), & \text{if } n \text{ is even,}\\\\
rs(2^n \dot{-}1, 2005), & \text{if } n \text{ is odd.}
\end{cases}$$


$g(n) = (2005 \dot{-}rs(2^n\dot{-}1, 2005) Nrs(n,2)) + rs(2^n\dot{-}1, 2005)N^2rs(n,2)$
故 $g \in \mathcal{EF}$。
再计算(1)，$2005 = 5 \times 401$，$5, 401$ 均为素数

由Fermat's little theorem（费马小定理：假如 $p$ 是素数，且 $(a,p) =1$，那么 $a^{p-1} \equiv 1 (\text{mod } p)$)知，
$2^4 \equiv 1 \mod 5, 2^{400} \equiv 1 \mod 401$
$\because (5, 401) = 1, \therefore 2^{400} \equiv 1(\text{mod }5 \times 401)$$，即 $$rs(2^{400}, 2005) = 1$
从而，$g(n+400) = g(n)$，$g$ 的周期为 $400$
故 $g(2006) = g(6) = rs(h(6), 2005)) = 2005 \dot{-} rs(2^6-1, 2005) = 1942$。

### 1.25
> 设 $f: \mathbb{N} \rightarrow \mathbb{N}$ 定义为
$f(n)=\pi \text{ 的十进制展开式中第 }n\text{ 位数字}$
例如 $f(0) = 3, f(1) = 1, f(2) = 4$。证明：$f \in \mathcal{GRF}$

解答：
首先，我们有$\frac{1}{1+x^2} = \sum\limits\_{i=0}^{n}(-1)^ix^{2i} + \frac{(-1)^{n+1}x^{2n+2}}{1+x^2}$
$\therefore \arctan x = \int\_0^x\frac{\text{d}t}{1+t^2} =\sum\limits\_{i=0}^{n}(-1)^i\int\_0^xt^{2i}\text{d}t + \int\_0^x\frac{(-1)^{n+1}}{1+t^2}t^{2n+2}\text{d}t$
$
=\sum\limits\_{i=0}^{n}(-1)^i\frac{x^{2i+1}}{2i+1} + \int\_0^x\frac{(-1)^{n+1}}{1+t^2}t^{2n+2}\text{d}t \quad \cdots (*)
$

由 Hutton's Formula 知，
$
\frac{\pi}{4} = 2\arctan\frac{1}{3}+\arctan\frac{1}{7} \Rightarrow \pi = 8\arctan\frac{1}{3}+4\arctan\frac{1}{7}
$
在 (*) 式中取 $$n = 2k+1$$，这是使余项为正且估计更精确
$
\pi = 8 \sum\limits\_{i=0}^{2k+1}(-1)^i\frac{1}{(2i+1)3^{2i+1}} + 8 \int\_0^{\frac{1}{3}}\frac{t^{4k+4}}{1+t^2}\text{d}t + 4 \sum\limits\_{i=0}^{2k+1}(-1)^i\frac{1}{(2i+1)7^{2i+1}}+ 4 \int\_0^{\frac{1}{7}}\frac{t^{4k+4}}{1+t^2}\text{d}t
$
$
t\_k =8 \sum\limits\_{i=0}^{2k+1}(-1)^i\frac{1}{(2i+1)}\left(\frac{1}{3^{2i+1}} + \frac{1}{2 \cdot 7^{2i+1}}\right)
$
$
r\_k =8 \int\_0^{\frac{1}{3}}\frac{t^{4k+4}}{1+t^2}\text{d}t + 4\int\_0^{\frac{1}{7}}\frac{t^{4k+4}}{1+t^2}\text{d}t
$
$
\leq 8 \int\_0^{\frac{1}{3}}t^{4k+4}\text{d}t + 4\int\_0^{\frac{1}{7}}t^{4k+4}\text{d}t
$
$
\leq 8 \cdot \frac{1}{3}\cdot \frac{1}{3^{4k+4}} + 4\cdot \frac{1}{7} \cdot \frac{1}{7^{4k+4}} \leq \frac{1}{3^{4k}} \leq \frac{1}{80^k}
$

因此，$\pi = t\_k + r\_k$，且 $0 <r\_k < \frac{1}{80^k}$，$t$ 为有理数，设 $t\_k$ 的十进制展开式为
$t\_k = a\_{k0}a\_{k1} a\_{k2} \cdots a\_{kn} \cdots$
对于 $n \in \mathbb{N}$，存在 $l \geq n+1$ 使在 $t\_l$ 中并非$a\_{l,n+1}, a\_{l, n+2}, \cdots, a\_{l,l}$ 皆为 $9$，若不然，对任何 $l \geq n+1$，$a\_l, a\_{l,n+1}, a\_{l, n+2}, \cdots, a\_{l,l}$ 皆为 $9$。

$\because 10^{l}\pi = 10^lt\_l + 10^lr\_l$，$\therefore 10^lt\_l < 10^l \pi < 10^lt\_l+\frac{1}{8^l}$
这样在 $\pi$ 的展开式中，从某位起皆为 $9$，从而 $\pi$ 为有理数
令$l=l(n) = \mu l. (l\geq n+1$ 且在 $a\_{l, n+1}, \cdots, a\_{l,l}$ 中并非皆为 $9)$
$\because a(l,i) = a\_{l, i} = t\_l$ 展开式的第 $i$ 个数字 $\in \mathcal{EF}$
$\therefore l(n) = \mu l .((l \geq n+1) \wedge \prod\limits\_{i = n+1}^l(a\_{l,i}\dot{-}9 \neq 0)) \in\mathcal{GRF}$
（注：若知道 $\pi$ 展开式中连续 $9$ 的个数有上限，则 $l(n) \in \mathcal{EF}$）
我们有 $t\_k < \pi < t\_l+\frac{1}{80^l}$，
对于 $n \in \mathbb{N}$，取 $l = l(n)$
$\because a\_{l, n+1}, \cdots, a\_{l,l}$ 并非皆为 $9$，

$\therefore$ 设 $a\_{l,m} < 9$，这里 $n+1 \leq m \leq l$$，设 $$\pi = \pi\_0\pi\_1\pi\_2\cdots$

从而 $t\_l < \pi < t\_l + \frac{1}{80^m}$，$10^mt\_l < 10^m\pi<10^mt\_l + \frac{1}{8^m}$

从而
$a\_{l0}a\_{l1}a\_{l2}\cdots a\_{ln}a\_{l\overline{n+1}} \cdots a\_{lm}a\_{l\overline{m+1}} \cdots$
$<\pi\_0\pi\_1\pi\_2 \cdots \pi\_n\pi\_{n+1}\cdots \pi\_m \pi\_{m+1} \cdots$
$< a\_{l0}a\_{l1}a\_{l2}\cdots a\_{ln}a\_{l\overline{n+1}} \cdots a\_{lm}a\_{l\overline{m+1}} \cdots + \frac{1}{8^m}(m \geq 1)$
$< a\_{l0}a\_{l1}a\_{l2}\cdots a\_{ln}a\_{l\overline{n+1}} \cdots (a\_{lm}+1)a\_{l\overline{m+1}} \cdots$

因此，
$$
\begin{array}{rl}
a\_{l0}a\_{l1}\cdots a\_{ln}a\_{l\overline{n+1}} \cdots a\_{lm} & \leq \pi\_0\pi\_1\pi\_2 \cdots \pi\_n\pi\_{n+1}\cdots \pi\_m \\
& \leq a\_{l0}a\_{l1}a\_{l2}\cdots a\_{ln}a\_{l\overline{n+1}} \cdots (a\_{lm}+1)
\end{array}
$$
$\because m \geq n+1$， $\therefore \pi\_n = a\_{ln}$

因此，$f(n) = \pi\_n = a(l(n), n) \in \mathcal{GRF}$。
