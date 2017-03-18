---
title: Python Matplotlib绘图基础
comments: true
toc: true
date: 2016-07-18 20:10:34
categories: Programming
tags : Python
keywords: Python, Matplotlib
---

>**本节内容：**Matplotlib 是python最著名的绘图库，它提供了一整套和matlab相似的命令API，十分适合交互式地进行制图。而且也可以方便地将它作为绘图控件，嵌入GUI应用程序中。。

<!-- more -->

## 简单绘图

``` python
# 创建多项式 1*x^2+0*x+0
>>> func = np.poly1d(np.array([1,0,0]).astype(float))
# 一阶导数
>>> func1 = func.deriv(1)
# 在-10,10之间产生100个均匀分布的数值
>>> x = np.linspace(-10,10,100)
>>> y = func(x)
>>> y1 = func1(x)
# 绘制两条曲线 分别为红色r和蓝色b
>>> pyplot.plot(x,y,'r',x,y1,'b')
[<matplotlib.lines.Line2D object at 0xb2eeefec>, <matplotlib.lines.Line2D object at 0xb2ef46ac>]
# 横轴标签
>>> pyplot.xlabel('x')
<matplotlib.text.Text object at 0xb2f37e4c>
>>> pyplot.ylabel('y')
<matplotlib.text.Text object at 0xb3083c8c>
>>> pyplot.show()
```
生成如下图像:
![简单绘图](/resource/blog/2016-07/简单绘图.png)

## 子图

``` python
from matplotlib import pyplot
import numpy as np

func = np.poly1d(np.array([1,2,3,4]).astype(float))
x = np.linspace(-10,10,100)
y = func(x)
func1 = func.deriv(1)
y1 = func1(x)
func2 = func.deriv(2)
y2 = func2(x)
# subplot()创建子图，第一个参数为子图的行数，第二个参数是子图的列数，第三个参数是序号
pyplot.subplot(3,1,1)
pyplot.plot(x,y,'r')
# 子图标题
pyplot.title('Polynomail')

pyplot.subplot(3,1,2)
pyplot.plot(x,y1,'b')
pyplot.title('Firse Derivative')

pyplot.subplot(3,1,3)
pyplot.plot(x,y2,'y')
pyplot.title('Second Derivative')

pyplot.xlabel('x')
pyplot.ylabel('y')

pyplot.show()
```

生成如下图像：
![绘制子图](/resource/blog/2016-07/子图.png)

## 绘制3维图像

``` python
from mpl_toolkits.mplot3d import Axes3D
import matplotlib.pyplot as plt
from matplotlib import cm
from numpy import *
fig = plt.figure()
ax = fig.add_subplot(1,1,1,projection='3d')
u = linspace(-1,1,100)
# 创建2维坐标网络
x,y = meshgrid(u,u)
z = x**2+y**2
# 指定行和列的步长，并指定颜色
ax.plot_surface(x,y,z,rstride=4,cstride=4,cmap=cm.binary_r)
plt.show()
```

生成如下图像:
![三维图像](/resource/blog/2016-07/三维图像.png)

## 绘制等高线图

``` python
import matplotlib.pyplot as plt
from matplotlib import cm
from numpy import *
fig = plt.figure()
# 这里不需要指定三维参数 projection='3d'
ax = fig.add_subplot(1,1,1)
u = linspace(-1,1,100)
x,y = meshgrid(u,u)
z = x**2+y**2
ax.contourf(x,y,z)
plt.show()
```

生成如下图像:
![等高线图](/resource/blog/2016-07/等高线图.png)

## 绘制动画

首先新建了图片、坐标和一条空白的线作为全局变量。然后init方法是一个初始化的方法，什么都不干。animate方法中的参数i表示当前帧数，通过正弦函数接受i生成了坐标集合，并且更新到线条中去。接下来新建了anim对象，几个参数的名称都很好懂，最后一个blit方法是告诉matplotlib记得在每帧之前擦除init方法返回的那些图元。

``` python
import numpy as np
from matplotlib import pyplot as plt
from matplotlib import animation

# first set up the figure, the axis, and the plot element we want to animate
fig = plt.figure()
ax = plt.axes(xlim=(0, 2), ylim=(-2, 2))
line, = ax.plot([], [], lw=2)

# initialization function: plot the background of each frame
def init():
    line.set_data([], [])
    return line,

# animation function.  this is called sequentially
def animate(i):
    x = np.linspace(0, 2, 1000)
    y = np.sin(2 * np.pi * (x - 0.01 * i))
    line.set_data(x, y)
    return line,

# call the animator.  blit=true means only re-draw the parts that have changed.
anim = animation.FuncAnimation(fig, animate, init_func=init,
                               frames=200, interval=20, blit=True)
plt.show()
```
生成如下图像:
![动画](/resource/blog/2016-07/animation.gif)

## 导出GIF

- 安装ImageMagick
- 配置matplotlib

  先看看自己的配置文件放在了哪里：

  ``` python
  import matplotlib
  matplotlib.matplotlib_fname()
  '/usr/local/anaconda3/lib/python3.5/site-packages/matplotlib/mpl-data/matplotlibrc'
  ```
  取消“animation.convert_path”前面的注释，这样应该就配置好了，接下来用一句话就可以导出gif：

  ``` python
  anim.save('perceptron.gif', fps=2, writer='imagemagick')
  ```

  但是，出现了错误：

  ``` python
  UserWarning: imagemagick MovieWriter unavailable
  warnings.warn ("% s MovieWriter unavailable" writer%)
  ```

  此时，需要打开matplotlib的安装目录`/usr/local/anaconda3/lib/python3.5/site-packages/matplotlib/__init__.py`，在1131行`rcParams = rc_params()`下面添加一句：`rcParams['animation.convert_path'] = '/usr/bin/convert'`，这样就好了。

