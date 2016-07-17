---
title: Ubuntu16.04配置L2TP-VPN
comments: true
toc: true
date: 2016-07-17 13:41:02
categories: Tools
tags : VPN
keywords: Ubuntu, L2TP, VPN
---

>**本节内容：**本篇博文记录在Ubuntu16.04上安装L2TP插件,之前记录了在[Ubuntu14.04](/2016/03/20/2016-03-20-ubuntu14-l2tp-vpn.html)上安装L2TP插件，但ppa上还没有更新这个软件包，故只有手动从源代码编译安装。

<!-- more -->

## 安装依赖软件

从源代码编译时需要很多第三方软件，因此首先需要在系统中安装这些库，具体如下:

``` shell
sudo apt  install git libtool automake libglib2.0-dev  intltool ppp-dev libgtk+-3-dev libnma-dev libsecret-1-dev libnm-gtk-dev libnm-glib-dev  libnm-glib-vpn-dev xl2tpd
```

安装好这些库后便可以从`github`上`clone`最新的代码:

``` shell
git clone https://github.com/nm-l2tp/network-manager-l2tp
```

## 编译

使用终端进入源代码目录`network-manager-l2tp`，运行如下的`configure`命令：

``` python
./configure \
  --prefix=/usr --localstatedir=/var --sysconfdir=/etc \
  --libexecdir=/usr/lib/NetworkManager \
  --with-pppd-plugin-dir=/usr/lib/pppd/2.4.7 \
  --enable-absolute-paths
```

没有报错后，接着便`make`和`make install` :

``` python
make
sudo make install
```

## 使用

安装完成后，还不能直接使用，需要关闭`xl2tp`，然后机器重新启动:

``` python
sudo service xl2tpd stop
sudo update-rc.d xl2tpd disable
sudo reboot
```
此时，通过`网络-编辑连接-增加-第二层隧道协议`便可以配置L2TP-VPN了。


![L2TP-VPN](/resource/blog/2016-07/L2TP.png)

配置好用户名，密码，地址后，基本上就OK了。不过由于笔者实验室配置的L2TP貌似有问题，导致部分网页打不开，比如微博。。。然而却能Ping的通，经过一番摸索后，发现是VPN的`PPP`设置中的`MTU`值过大，于是将其改到了一个较小的数值，便可以愉快的玩耍了。


![PPP设置](/resource/blog/2016-07/PPP设置.png)