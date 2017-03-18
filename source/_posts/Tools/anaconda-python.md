---
title: 使用Anaconda Python科学计算包
comments: true
toc: true
date: 2016-07-17 15:41:02
categories: Tools
tags : Python
keywords: Python, Anaconda
---

>**本节内容：**本篇博文记录在Ubuntu16.04上配置Python的科学计算环境包，使用anaconda发行版和清华大学的镜像服务。

<!-- more -->

## Anaconda下载与安装

由于使用教育网，便可以很方便的使用IPV6资源，其中就包括镜像服务。Anaconda 是一个用于科学计算的 Python 发行版，支持 Linux, Mac, Windows, 包含了众多流行的科学计算、数据分析的 Python 包。

`Anaconda` 安装包可以到 [清华大学镜像站](https://mirrors.tuna.tsinghua.edu.cn/anaconda/archive/) 下载，本文使用了`Anaconda3-4.1.1-Linux-x86_64.sh `。

安装完成后，默认是已经添加了环境变量的，这里在Ubuntu16.04上安装的，会与系统的Python3版本冲突，因此需要修改环境，为了不影响系统的Python3，将Anaconda的Python路径放至PATH的最后，即在`/etc/profile`的最后一行添加`export PATH=$PATH:/usr/local/anaconda3/bin`。同时为了抛弃系统的Python3，一个解决方案是使用软链将Anaconda的`python`指向`/usr/local/bin/python3`,具体入下：

``` python
ln -s /usr/local/anaconda3/bin/python /usr/local/bin/python3
```

## 镜像源与PyPi配置

通常Anaconda的官方源速度无法忍受，因此需要更改为国内的源，这里可以清华大学的镜像源：Anaconda Python 免费仓库。

Linux下在终端中输入：

``` python
conda config --add channels 'https://mirrors.tuna.tsinghua.edu.cn/anaconda/pkgs/free/'
conda config --set show_channel_urls yes
```

Windows下在CMD中输入：

``` python
conda config --add channels https://mirrors.tuna.tsinghua.edu.cn/anaconda/pkgs/free/
conda config --set show_channel_urls yes
```

一般我们使用PyPi的安装大部分的Python组件，同理，官方速度也很够呛，因此在这里配置国内的源。
编辑`~/.pip/pip.conf`(没有就创建一个)，里面改为清华源：

``` python
[global]
index-url = https://pypi.tuna.tsinghua.edu.cn/simple
```

或中科大源，速度都很给力

``` python
[global]
index-url = https://pypi.mirrors.ustc.edu.cn/simple
```

## 使用

由于安装了Anaconda Python3，所以安装软件时可以用`conda install XXX`安装或`conda update XXX`来更新，当然也可以用`pip3`。

当要为系统的Python2版本安装Numpy时，先安装一些编译依赖项,如果直接用pip安装最新的numpy时会编译错误，原因是缺少必要的库，因此需要先安装这些库，一个简单的做法是使用Ubuntu的build-dep命令：

``` python
sudo apt build-dep python-numpy python-scipy python-matplotlib
```

然后就可以直接安装了：`sudo -H pip install -U numpy`.

