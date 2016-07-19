---
title: "Markdown中编写LaTex数学公式"
date: 2016-07-19 15:31:46 +0800
comments: true
toc: true
categories: Tools
tags : [Markdown, Latex]
keywords: Markdown, Latex
mathjax: true
---


>**本节内容：**目前hexo中写博客使用Markdown语言，而写博客经常要用到一些数学公式，在Markdown中写LaTeX数学公式则显得非常重要，本节将一些常用的数学公式的写法记录下来，以备日后查询。

<!-- more -->

******

## $\LaTeX$基本语法

- **使用LaTeX**

`$`符号开始

``` tex
$ y_k=\varphi(u_k+v_k)$
$J\alpha(x) = \sum{m=0}^\infty \frac{(-1)^m}{m! \Gamma (m + \alpha + 1)} {\left({ \frac{x}{2} }\right)}^{2m + \alpha}$
#注意下面的写法：(右对齐)
$$ y_k=\varphi(u_k+v_k)$$
```
则依次显示为：

$ y_k=\varphi(u_k+v_k)$

$J\alpha(x) = \sum{m=0}^\infty \frac{(-1)^m}{m! \Gamma (m + \alpha + 1)} {\left({ \frac{x}{2} }\right)}^{2m + \alpha}$

$$ y_k=\varphi(u_k+v_k)$$

- **输入上下标**

`^`表示上标, `_`表示下标。如果上下标的内容多于一个字符，要用`{}`把这些内容括起来当成一个整体。上下标是可以嵌套的，也可以同时使用。例如：
``` tex
$x^{y^z}=(1+{\rm e}^x)^{-2xy^w}$
$f(x)=x_2^3+1$
#如果要在左右两边都有上下标，可以用\sideset命令:
$\sideset{^12}{^34}\bigotimes$
```
则显示如下：

$x^{y^z}=(1+{\rm e}^x)^{-2xy^w}$

$f(x)=x_2^3+1$

$\sideset{^12}{^34}\bigotimes$

- **输入括号和分隔符**

``` tex
 ()、[]和|表示自己，{}表示{}。当要显示大号的括号或分隔符时，要用\left和\right命令：
 $f(x,y,z) = 3y^2z \left( 3+\frac{7x+5}{1+y^2} \right)$
 有时候要用\left.或\right.进行匹配而不显示本身：
 $\frac{du}{dt}  \| _{x=0}$
```
显示为：

$f(x,y,z) = 3y^2z \left( 3+\frac{7x+5}{1+y^2} \right)$

$\frac{du}{dt}  \| _{x=0}$

- **微分方程**

``` tex
$\frac{du}{dt} and \frac{d^2 u}{dx^2}$
```

$\frac{du}{dt} and \frac{d^2 u}{dx^2}$

- **输入偏微分方程**

``` tex
$$
\begin{eqnarray}
\frac{\partial u}{\partial t} \\
= h^2 \left( \frac{\partial^2 u}{\partial x^2} \\
\+ \frac{\partial^2 u}{\partial y^2} \\
\+ \frac{\partial^2 u}{\partial z^2}\right)
\end{eqnarray}
$$
```
$$
\begin{eqnarray}
\frac{\partial u}{\partial t} \\
= h^2 \left( \frac{\partial^2 u}{\partial x^2} \\
\+ \frac{\partial^2 u}{\partial y^2} \\
\+ \frac{\partial^2 u}{\partial z^2}\right)
\end{eqnarray}
$$



- **输入分数**

``` tex
$\frac{1}{3}$
$P(v)=\frac{1}{1+exp(-v/T)}$
```
  依次显示为：
  $\frac{1}{3}$

  $P(v)=\frac{1}{1+exp(-v/T)}$

- **输入开方**
 \sqrt

``` tex
  $\sqrt{2}$
  $\sqrt[n]{3}$
```
  $\sqrt{2}$

  $\sqrt[n]{3}$

- **输入省略号**
数学公式中常见的省略号有两种，**\ldots**表示与文本底线对齐的省略号，**\cdots**表示与文本中线对齐的省略号。

``` tex
  $f(x_1,x_2,\ldots,x_n) = x_1^2 + x_2^2 + \cdots + x_n^2$
```
   $f(x_1,x_2,\ldots,x_n) = x_1^2 + x_2^2 + \cdots + x_n^2$

- **输入向量**

``` tex
  $\vec{a} \cdot \vec{b}=0$
```
  $\vec{a} \cdot \vec{b}=0$

- **输入积分**

``` tex
  $\int_0^1 x^2 {\rm d}x$
```
  $\int_0^1 x^2 {\rm d}x$

- **输入极限运算**

```  tex
  $\lim_{n \rightarrow +\infty} \frac{1}{n(n+1)}$
  $\frac{1}{\lim_{u \rightarrow \infty}}, \frac{1}{\lim\limits_{u \rightarrow \infty}}$
```
$\lim_{n \rightarrow +\infty} \frac{1}{n(n+1)}$

$\frac{1}{\lim_{u \rightarrow \infty}}$

$\frac{1}{\lim\limits_{u \rightarrow \infty}}$

- **输入累加、累乘运算**

```  tex
  $\sum_{i=0}^n \frac{1}{i^2}$
  $\prod_{i=0}^n \frac{1}{i^2}$
```
 $\sum_{i=0}^n \frac{1}{i^2}$

 $\prod_{i=0}^n \frac{1}{i^2}$



- **等效**

```  tex
$$
\begin{equation}
${\frac{3}{5} [3 + 2*( a+ b)]}$
\end{equation}
$$

$$
${\frac{3}{5} [3 + 2*( a+ b)]}$
$$
```

${\frac{3}{5} [3 + 2*( a+ b)]}$



## 希腊字母

|希腊字母(小写)|  输入 |希腊字母(大写)|  输入  |
|:----:| :----|:----: | :----|
|  α  | \alpha|  Α   |   A    |
|  β  | \beta |  Β   |   B    |
|  γ  | \gamma|  Γ   | \Gamma |
|  δ  | \delta|  Δ   | \Delta |
|  ε或$\epsilon$  | \epsilon或\varepsilon|  Ε |     E  |
|  ζ  | \zeta|  Ζ   |   Z   |
|  η  | \eta|  Η   |    H   |
|  θ或$\vartheta$| \theta或\vartheta|  Θ    | \Theta      |
|  ι  | \iota　|  Ι   |   I    |
|  κ  | \kappa|  Κ   |   K    |
|  λ  | \lambda|  Λ   |   \Lambda    |
|  μ  | \mu|  Μ   |  M    |
|  ν  | \nu|  Ν   |  N     |
|  ξ  | \xi|  Ξ   |  \Xi     |
|  ο  | o|  Ο   |  O     |
|  π或$\varpi$| \pi或\varpi|  Π   |  \Pi     |
|  ρ或$\varrho$| \rho或\varrho|  Ρ   |  P     |
|  σ或$\varsigma$| \sigma或\varsigma|  Σ   |  \Sigma     |
|  τ  | \tau|  Τ   |  T     |
|  υ  | \upsilon|  Υ   |  \Upsilon     |
|  φ或$\varphi$| \phi或\varphi|  Φ   |  \Phi　     |
|  χ  | \chi|  Χ   |  X     |
|  ψ  | \psi|  Ψ   |  \Psi     |
|  ω  | \omega|  Ω   |  \Omega     |

## 三角函数与逻辑数学字符

| 数学字符        | 输入     | 数学字符  | 输入         |
| :------: |:-------------:| :-----:|:------:|
| $\pm$     | \pm | $\times$ |\times|
| $\div$    |  \div |$\mid$    | \mid    |
| $\nmid$    | \nmid  |$\cdot$    | \cdot    |
| $\circ$    | \circ  |$\ast$    |  \ast   |
| $\bigodot$  | \bigodot  |$\bigotimes$ |  \bigotimes   |
| $\bigoplus$    | \bigoplus  |$\leq$    |  \leq   |
| $\geq$    |  \geq |$\neq$    |  \neq   |
| $\approx$    | \approx  |$\equiv$    |   \equiv  |
| $\sum$    | \sum  |$\prod$    |  \prod   |
| $\coprod$    | \coprod  |$\emptyset$    |  \emptyset   |
| $\in$    | \in  |$\notin$    |  \notin   |
| $\subset$    |\subset   |$\supset$    |   \supset  |
| $\subseteq$    | \subseteq  |$\supseteq$ | \supseteq    |
| $\bigcap$    | \bigcap  |$\bigcup$    | \bigcup    |
| $\bigvee$    |\bigvee   |$\bigwedge$    |  \bigwedge   |
| $\biguplus$  | \biguplus  |$\bigsqcup$  |  \bigsqcup   |
| $\log$    |  \log |$\lg$    |  \lg   |
| $\ln$    | \ln  |$\bot$    |   \bot  |
| $\angle$    | \angle  |$30^\circ$    | 30^\circ    |
| $\sin$    |  \sin |$\cos$    | \cos    |
| $\tan$    | \tan  |$\cot$    |  \cot   |
| $\sec$    | \sec  |$\csc$    |  \csc   |
| $\prime$    | \prime  |$\int$    |  \int   |
| $\iint$    |  \iint |$\iiint$    |  \iiint   |
| $\iiiint$    | \iiiint  |$\oint$    | \oint    |
| $\lim$    | \lim  |$\infty$    |  \infty   |
| $\nabla$    |  \nabla |$\because$    | \because    |
| $\therefore$|\therefore |$\forall$    |  \forall   |
| $\exists$    |\exists   |$\not=$    | \not=    |
| $\not>$    | \not>  |$\not\subset$    |   \not\subset  |
| $\hat{y}$    | \hat{y}  |$\check{y}$    |  \check{y}   |
| $\breve{y}$    | \breve{y}  |$\overline{a+b+c+d}$    |  \overline{a+b+c+d}   |
| $\underline{a+b+c+d}$ | \underline{a+b+c+d}  |$\overbrace{a+\underbrace{b+c}{1.0}+d}^{2.0}$ |  \overbrace{a+\underbrace{b+c}{1.0}+d}^{2.0}   |
| $\uparrow$ | \uparrow  |$\downarrow$ | \downarrow    |
| $\Uparrow$ | \Uparrow  |$\Downarrow$ |  \Downarrow   |
| $\rightarrow$|\rightarrow  |$\leftarrow$ |\leftarrow   |
| $\Rightarrow$ |\Rightarrow|$\Leftarrow$ |\Leftarrow|
| $\longrightarrow$ |\longrightarrow|$\longleftarrow$ |   \longleftarrow  |
| $\Longrightarrow$ | \Longrightarrow  |$\Longleftarrow$ | \Longleftarrow    |
| $\ $ | \空格  |\# |  \\#   |

## 字体转换

要对公式的某一部分字符进行字体转换，可以用{\rm 需转换的部分字符}命令，其中\rm可以参照下表选择合适的字体。一般情况下，公式默认为意大利体。

`\rm`　　罗马体

`\it`　　意大利体

`\bf`　　黑体　

`\sf`　　等线体

`\mit` 　数学斜体　

`\tt`　打字机字体

`\sc`　　小体大写字母

## 表格

``` tex
$$\\begin{array}{c|lcr}
n & \text{Left} & \text{Center} & \text{Right} \\\\
\hline
1 & 0.24 & 1 & 125 \\\\
2 & -1 & 189 & -8 \\\\
3 & -20 & 2000 & 1+10i \\\\
\\end{array} $$
```

$$\\begin{array}{c|lcr}
n & \text{Left} & \text{Center} & \text{Right} \\\\
\hline
1 & 0.24 & 1 & 125 \\\\
2 & -1 & 189 & -8 \\\\
3 & -20 & 2000 & 1+10i \\\\
\\end{array} $$

## 花括号用法

$$\\begin{array}{cc}
  a & b \\\\
  c & c
\\end{array} $$

``` tex
$$f(x)=
\begin{cases}
0& \text{x=0}\\
1& \text{x!=0}
\end{cases}$$
```
$$
f(x)=
\\begin{cases}
0& \text{x=0}\\\\
1& \text{x!=0}
\\end{cases}
$$

## 多行数学式对齐

**利用&符号来对齐，在每个等号前添加&符号即可**

``` tex
$$\begin{array} {lcl}
\cos 2\theta & = & \cos^2 \theta - \sin^2 \theta \\\\
             & = & 2 \cos^2 \theta - 1.
\end{array}$$

$$\begin{array} {lcl}
f(x) & = & (a+b)^2 \\\\
     & = & a^2+2ab+b^2
\end{array}$$
```
$$\begin{array} {lcl}
\cos 2\theta & = & \cos^2 \theta - \sin^2 \theta \\\\
             & = & 2 \cos^2 \theta - 1.
\end{array}$$

$$\begin{array} {lcl}
f(x) & = & (a+b)^2 \\\\
     & = & a^2+2ab+b^2
\end{array}$$


## 矩阵与行列式

``` tex
$$
matrix
[ ( \begin{array}{ccc}
a & b & c \\\\
d & e & f \\\\
g & h & i \end{array} )]$$

$$
[ \chi(\lambda) = \left| \begin{array}{ccc}
\lambda - a & -b & -c \\\\
-d & \lambda - e & -f \\\\
-g & -h & \lambda - i \end{array} \right|.]
$$

$$ \left[
      \begin{array}{cc|c}
        1&2&3 \\\\
        4&5&6
      \end{array}
    \right]
$$
```

$$
matrix
[ ( \begin{array}{ccc}
a & b & c \\\\
d & e & f \\\\
g & h & i \end{array} )]$$

$$
[ \chi(\lambda) = \left| \begin{array}{ccc}
\lambda - a & -b & -c \\\\
-d & \lambda - e & -f \\\\
-g & -h & \lambda - i \end{array} \right|.]
$$

$$ \left[
      \begin{array}{cc|c}
        1&2&3 \\\\
        4&5&6
      \end{array}
    \right]
$$

## 括号的其他用法


| 功能      |    语法 | 显示  |
| :-------- | :--------| :--: |
|圆括号，小括号|\left( \frac{a}{b} \right) |$\left( \frac{a}{b} \right)$|
|方括号，中括号|\left[ \frac{a}{b} \right] | $$\left[ \frac{a}{b} \right]$$|
|花括号，大括号 |\left\{ \frac{a}{b}\right\}    | $$\left\\{ \frac{a}{b} \right\\}$$|
|角括号 |\left \langle \frac{a}{b} \right \rangle |$\left \langle \frac{a}{b} \right \rangle$ |
|单竖线，绝对值 |\left\mid \frac{a}{b} \right\mid ,或\vert|$\mid \frac{a}{b} \mid$ |
| 双竖线，范式|\Vert \frac{a}{b} \Vert |$$\Vert \frac{a}{b} \Vert$$ |
|取整函数 |\left \lfloor \frac{a}{b} \right \rfloor |$\left \lfloor \frac{a}{b} \right \rfloor$|
| 取顶函数|\left \lceil \frac{c}{d} \right \rceil    |$\left \lceil \frac{c}{d} \right \rceil   $ |
|斜线与反斜线|\left / \frac{a}{b} \right \backslash|$\left / \frac{a}{b} \right \backslash$|
|上下箭头|\left \uparrow \frac{a}{b} \right \downarrow|$\left \uparrow \frac{a}{b} \right \downarrow$|
|混合括号1|\left [ 0,1 \right )|$\left [ 0,1 \right )$|
|混合括号2|\langle \psi  \mid  |$\langle \psi  \mid $|
|单左括号|\left \{ \frac{a}{b} \right .|$$\left \\{ \frac{a}{b} \right .$$|
|单右括号|\left . \frac{a}{b} \right \}|$$\left . \frac{a}{b} \right \\}$$|
