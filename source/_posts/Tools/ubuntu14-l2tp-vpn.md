---
title: "Ubuntu14.04配置L2TP-VPN"
date: 2016-03-20 12:54:12 +0800
comments: true
categories: Tools
tags : VPN
keywords: Ubuntu, L2TP, VPN
---

>**本节内容：**本篇博文记录在Ubuntu14.04上安装L2TP插件。

<!-- more -->

## 添加PPA

`sudo apt-add-repository ppa:seriy-pr/network-manager-l2tp`

## 刷新软件包缓存

`sudo apt-get update`

## 安装network-manager-l2tp

`sudo apt-get install network-manager-l2tp-gnome`

## 由于墙问题无法添加PPA
 若无法进行上述操作，则可以在[网站](https://launchpad.net/~seriy-pr/+archive/ubuntu/network-manager-l2tp/+packages)下载软件包`network-manager-l2tp`，`network-manager-l2tp-gnome `，根据系统是32位还是64位下载相应版本的`deb`包后先后安装(注意：需要联网下载一些依赖包，需要联网,注意顺序)。

## 安装完之后需要运行下面命令

```
sudo service xl2tpd stop
sudo update-rc.d xl2tpd disable
```

## 重启机器，新建VPN
在`网络`-`编辑连接`-`添加`-`选择连接类型`-`VPN`-`L2TP`,然后配置网关，用户名密码，若需要共享密钥，则点击`ipses settings`,在`pre-shared key`内填入公司分配的共享密钥，否则就可以直接连接VPN了。

图：选择VPN类型

![L2TP-VPN](/resource/blog/2016-03/L2TP-VPN.png)

