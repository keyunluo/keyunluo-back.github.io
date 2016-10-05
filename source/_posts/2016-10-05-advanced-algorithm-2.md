---
title: 高级算法(2)--Min-Cut-Max-Flow(1)-确定性算法
comments: true
toc: true
date: 2016-10-05 13:41:02
categories: AdvancedAlgorithms
tags : 算法
keywords: Min-Cut , 最小割 , Max-Flow , 最大流
mathjax : true
---

>**本节内容：**对于一个图中的两个节点来说，如果把图中的一些边去掉，刚好让他们之间无法连通的话，这些被去掉的边组成的集合就叫做割了，最小割就是指所有割中权重之和最小的一个割。

<!-- more -->

## 图的割

### 定义

对于无向图 $G(V,E)$ 以及边的子集 $C \subseteq E$, 如果把C中所有的边都删除后图G不再连通则称边的子集C是图G的一个割。更加严格地，我们考虑割集断开了图的剩余部分的顶点的一个子集S。

一对不相交集合： $S,T \subseteq V$ 如果S和T不为空，并且$S \bigcup T = V$成立，那么S,T称作V的二划分。

给定一个顶点V的二划分 $\\{S,T\\}$，一个割集C定义如下：

$$C = E(S,T), 其中 E(S,T) = \\{ uv \in E \vert u \in S , v \in T \\}$$

对于一个图G，割可能有很多个，我们对找出最小的割集和最大的割集感兴趣。

### 最小割

> 输入：无向图 $G(V,E)$ , 输出：一个G中边数最小的割C。

也就是说，将顶点集合$V$二划分成两个不相交的非空集合$\\{S，T\\}$，使得 $E|(S,T)|$ 最小。

更一般地，允许点$(u,v)$之间存在不止一条边，即并行边，这样的图称为**multi-graphs**,此时割C包括并行边。

## 流网络

### 定义

流网络（Flow Networks）指的是一个有向图 $G = (V, E)$，其中每条边 $(u, v) ∈ E$ 均有一非负容量 $c(u, v) ≥ 0$。如果 $(u, v) ∉ E$ 则可以规定 $c(u, v) = 0$。流网络中有两个特殊的顶点：源点 s （source）和汇点 t（sink）。为方便起见，假定每个顶点均处于从源点到汇点的某条路径上，就是说，对每个顶点 $v ∈ E$，存在一条路径 s --> v --> t。因此，图 G 为连通图，且 $|E| ≥ |V| - 1$。

### 流(Flow)的基本性质

设$C\_{uv}$代表边u到v最大允许流量(Capacity), $f\_{uv}$代表u到v的当前流量, 那么有一下两个性质:

- $(u, v)$为有向图边, $0 \leq f\_{uv} \leq C\_{uv}$, 即对于所有的边, 当前流量不允许超过其Capacity

- 除了$s, t$之外, 对所有节点有 $\sum\limits\_{(v, u)}f\_{wu} = \sum\limits\_{(u, v)}f\_{uv}$, 即对于任何一点, 流入该点的流量等于留出该点的流量, 流量守恒原则(类似与能量守恒的概念)。

### 相关概念

- 残存网络 ：给定网络G和流量f, 残存网络$G_f$由那些仍有空间对流量进行调整的边构成.**残存网络 = 容量网络capacity - 流量网络flow**。

- 增广路径：增广路径p是残存网络中一条从源节点s到汇点t的简单路径,在一条增广路径p上能够为每条边增加的流量的最大值为路径p的残存容量$c\_f(p) = min \{c\_f(u,v):(u,v) \in p \}$。 在一条增广路径p上, 要增加整条增广路径的水流量, 则必须看最小能承受水流量的管道, 不然水管会爆掉, 这最小承受水流量就是**残存容量**。


### 最大流

流的值定义为：$|f| =Σ\_{v∈V}f(s, v)$，即从源点 s 出发的总流。

最大流问题（Maximum-flow problem）中，给出源点 s 和汇点 t 的流网络 G，希望找出从 s 到 t 的最大值流。

## 最小割最大流定理

### 定理

如果 f 是具有源点 s 和汇点 t 的流网络 $G = (V, E)$ 中的一个流，则下列条件是等价的：

- f 是 G 的一个最大流。

- 残留网络 $G\_f$ 不包含增广路径。

- 对 G 的某个割 (S, T)，有 |f| = c(S, T)。

### 算法

- min=MAXINT,确定一个源点

- 枚举汇点

- 计算最大流，并确定当前源汇的最小割集，若比min小更新min

- 转到2直到枚举完毕

- min即为所求，输出min

不难看出复杂度很高：枚举汇点要$O(n)$，最短增广路最大流算法求最大流是$O((n^2)m)$复杂度，在复杂网络中$O(m)=O(n^2)$，算法总复杂度就是$O(n^5)$；哪怕采用最高标号预进流算法求最大流$O((n^2)(m^{0.5}))$，算法总复杂度也要$O(n^4)$

## 最小割的确定性算法

目前已知最好的求最小割的确定性算法是** Stoer–Wagner algorithm** $(O(mn + n^2\log n), m=|E|)$,包含并行边

### 算法思路：

1.min=MAXINT，固定一个顶点P

2.从点P用“类似”prim的s算法扩展出“最大生成树”，记录最后扩展的顶点和最后扩展的边

3.计算最后扩展到的顶点的切割值（即与此顶点相连的所有边权和），若比min小更新min

4.合并最后扩展的那条边的两个端点为一个顶点（当然他们的边也要合并，这个好理解吧？）

5.转到2，合并N-1次后结束

6.min即为所求，输出min

prim本身复杂度是$O(n^2)$，合并n-1次，算法复杂度即为$O(n^3)$

如果在prim中加堆优化，复杂度会降为$O((n^2)logn)$

其核心思想是迭代缩小规模, 算法基于这样一个事实:

> 对于图中任意两点s和t, 它们要么属于最小割的两个不同集中, 要么属于同一个集.

如果是后者, 那么合并s和t后并不影响最小割. 基于这么个思想, 如果每次能求出图中某两点之间的最小割, 然后更新答案后合并它们再继续求最小割, 就得到最终答案了. 算法步骤如下:

1. 设最小割cut=INF, 任选一个点s到集合A中, 定义W(A, p)为A中的所有点到A外一点p的权总和.

2. 对刚才选定的s, 更新W(A,p)(该值递增).

3. 选出A外一点p, 且W(A,p)最大的作为新的s, 若A!=G(V), 则继续2.

4. 把最后进入A的两点记为s和t, 用W(A,t)更新cut.

5. 新建顶点u, 边权w(u, v)=w(s, v)+w(t, v), 删除顶点s和t, 以及与它们相连的边.

6. 若|V|!=1则继续1.

一个很详细的博客[朝花夕拾：无向图全局最小割](http://blog.coolstack.cc/2016/01/08/%E6%9C%9D%E8%8A%B1%E5%A4%95%E6%8B%BE%EF%BC%9A%E6%97%A0%E5%90%91%E5%9B%BE%E5%85%A8%E5%B1%80%E6%9C%80%E5%B0%8F%E5%89%B2/)


### 基本步骤：

用wage数组记录点的连通度，vis数组标记点是否在集合里面，In数组表示点被其它点合并。

- 一：找到S - T的最小割Mincut，其中S 和 T为最后并入集合的两个点。

    - 1，初始化数组vis 和 wage；

    - 2，遍历所有不在集合且没有被合并的点，找到最大wage值的点Next，并记录Mincut、S和T；

    - 3，Next并入集合，叠加与Next相连的所有点（不在集合 且 没有被合并），更新这些点的wage值；

    - 4，重复操作2和3一共N次 或者 找不到新的Next值时 跳出，返回Mincut；


- 二、找全局最小割ans，需要重复第一步N-1次，因为每次合并一个点，最多合并N-1个点；

    - 1，每次对返回的Mincut，更新ans = min(ans, Mincut)，当然ans为0时，说明图不连通；

    - 2，把点T合并到S点，操作有：对 所有没有被合并的点j，Map[S][j] += Map[T][j]。


### 例子

来自[CSDN博客](http://blog.csdn.net/i_love_home/article/details/9698791)

步骤: ![寻找 s, t 两点，然后合并于 s 点](/resource/blog/2016-10/mincut.jpg)

``` cpp
#include <cstdio>
#include <cstring>
#include <algorithm>
#define MAXN 500+10
#define INF 0x3f3f3f3f
using namespace std;
int Map[MAXN][MAXN];
bool vis[MAXN];//是否已并入集合
int wage[MAXN];//记录每个点的连通度
bool In[MAXN];//该点是否已经合并到其它点
int N, M;
void getMap()
{
    memset(Map, 0, sizeof(Map));
    int a, b, c;
    for(int i = 0; i < M; i++)
    {
        scanf("%d%d%d", &a, &b, &c);
        a++, b++;
        Map[a][b] += c;
        Map[b][a] += c;
    }
}
int S, T;//记录每次找s-t割  所遍历的最后两个点
int work()
{
    int Mincut;//每一步找到的s-t割
    memset(wage, 0, sizeof(wage));
    memset(vis, false, sizeof(vis));
    int Next;
    for(int i = 1; i <= N; i++)
    {
        int Max = -INF;
        for(int j = 1; j <= N; j++)
        {
            if(!In[j] && !vis[j] && Max < wage[j])//找最大的wage值
            {
                Next = j;
                Max = wage[j];
            }
        }
        if(Next == T) break;//找不到点 图本身不连通
        vis[Next] = true;//标记 已经并入集合
        Mincut = Max;//每次更新
        S = T, T = Next;// 记录前、后点
        for(int j = 1; j <= N; j++)//继续找不在集合 且 没有被合并过的点
        {
            if(In[j] || vis[j]) continue;
            wage[j] += Map[Next][j];//累加 连通度
        }
    }
    return Mincut;
}
int Stoer_wagner()
{
    memset(In, false, sizeof(In));
    int ans = INF;
    for(int i = 0; i < N-1; i++)
    {
        ans = min(ans, work());
        if(ans == 0) return 0;//本身不连通
        In[T] = true;
        for(int j = 1; j <= N; j++)//把T点合并到S点
        {
            if(In[j]) continue;//已经合并
            Map[S][j] += Map[T][j];
            Map[j][S] += Map[j][T];
        }
    }
    return ans;
}
int main()
{
    while(scanf("%d%d", &N, &M) != EOF)
    {
        getMap();
        printf("%d\n", Stoer_wagner());
    }
    return 0;
}
```