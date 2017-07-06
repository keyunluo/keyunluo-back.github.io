---
title: RedHat7.0更换CentOS源
comments: true
toc: true
date: 2016-07-27 13:25:02
categories: Tools
tags : CentOS
keywords: RedHat7.0更换CentOS源
---

>**本节内容：**服务器默认安装的是RedHat Linux，Redhat 的更新包只对注册的用户生效，所以我们自己手动更改成CentOS 的更新包，CentOS几乎和redhat是一样的，所以无需担心软件包是否可安装，安装之后是否有问题，本文记录配置过程。


<!-- more -->

## 查看系统本身所安装的yum 软件包

``` python
[root@slave10 hadoop]# rpm -qa | grep yum
yum-langpacks-0.4.2-3.el7.noarch
yum-metadata-parser-1.1.4-10.el7.x86_64
yum-3.4.3-118.el7.noarch
yum-utils-1.1.31-24.el7.noarch
yum-rhn-plugin-2.0.1-4.el7.noarch
PackageKit-yum-0.8.9-11.el7.x86_64
```

## 删除这些软件包

``` python
rpm -e yum-3.4.3-118.el7.noarch --nodeps
rpm -e yum-utils-1.1.31-24.el7.noarch --nodeps
rpm -e yum-rhn-plugin-2.0.1-4.el7.noarch --nodeps
rpm -e yum-metadata-parser-1.1.4-10.el7.x86_64 --nodeps
rpm -e yum-langpacks-0.4.2-3.el7.noarch --nodeps
rpm -e PackageKit-yum-0.8.9-11.el7.x86_64 --nodeps
rpm -e subscription-manager-gui --nodeps
rpm -e subscription-manager-firstboot --nodeps
rpm -e --nodeps redhat-release-server
rpm -e --nodeps redhat-logos
```

## 设置代理

代理服务器使用的是`tinyproxy`,安装在`192.168.100.102`中，可以访问外网。

注意：不使用此方法——系统级代理：写入：/etc/profile
```
http_proxy="http://192.168.100.2:8899/"
export http_proxy
```
这样整个系统都使用了代理，可能对Hadoop集群通信造成困扰，因此不予采纳。

### 为YUM设置代理

编辑 `sudo vim /etc/yum.conf` ，添加一行`proxy=http://192.168.100.2:8899/`,更改如下：

``` python
[main]
cachedir=/var/cache/yum/$basearch/$releasever
keepcache=0
debuglevel=2
logfile=/var/log/yum.log
exactarch=1
obsoletes=1
gpgcheck=1
plugins=1
installonly_limit=5
bugtracker_url=http://bugs.centos.org/set_project.php?project_id=23&ref=http://bugs.centos.org/bug_report_page.php?category=yum
distroverpkg=centos-release
proxy=http://192.168.100.2:8899/
```

### 为wget设置代理

编辑/etc/wgetrc，在最后加入
``` python
http_proxy=http://192.168.100.2:8899/
https_proxy = http://192.168.100.2:8899/
```
刷新：`source /etc/wgetrc`

## 导入GPG-KEY

``` python
wget https://mirrors.tuna.tsinghua.edu.cn/centos/7/os/x86_64/RPM-GPG-KEY-CentOS-7
sudo mv RPM-GPG-KEY-CentOS-7 /etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7
```

## 找到自己所需要的版本下载

``` python
wget https://mirrors.tuna.tsinghua.edu.cn/centos/7/os/x86_64/Packages/yum-3.4.3-132.el7.centos.0.1.noarch.rpm
wget https://mirrors.tuna.tsinghua.edu.cn/centos/7/os/x86_64/Packages/yum-utils-1.1.31-34.el7.noarch.rpm
wget https://mirrors.tuna.tsinghua.edu.cn/centos/7/os/x86_64/Packages/yum-rhn-plugin-2.0.1-5.el7.noarch.rpm
wget https://mirrors.tuna.tsinghua.edu.cn/centos/7/os/x86_64/Packages/yum-metadata-parser-1.1.4-10.el7.x86_64.rpm
wget https://mirrors.tuna.tsinghua.edu.cn/centos/7/os/x86_64/Packages/yum-langpacks-0.4.2-4.el7.noarch.rpm
wget https://mirrors.tuna.tsinghua.edu.cn/centos/7/os/x86_64/Packages/PackageKit-yum-1.0.7-5.el7.centos.x86_64.rpm
wget https://mirrors.tuna.tsinghua.edu.cn/centos/7/os/x86_64/Packages/yum-plugin-fastestmirror-1.1.31-34.el7.noarch.rpm
wget https://mirrors.tuna.tsinghua.edu.cn/centos/7/os/x86_64/Packages/PackageKit-1.0.7-5.el7.centos.x86_64.rpm
```

## 安装软件包

``` python
rpm -ivh yum-metadata-parser-1.1.4-10.el7.x86_64.rpm
rpm -ivh yum-plugin-fastestmirror-1.1.31-34.el7.noarch.rpm yum-3.4.3-132.el7.centos.0.1.noarch.rpm
rpm -ivh yum-rhn-plugin-2.0.1-5.el7.noarch.rpm yum-utils-1.1.31-34.el7.noarch.rpm yum-langpacks-0.4.2-4.el7.noarch.rpm
```

## 配置Repo

编辑 `vim /etc/yum.repos.d/CentOS-Base.repo` :

``` python
[base]
name=CentOS-$releasever - Base
baseurl=http://mirrors.tuna.tsinghua.edu.cn/centos/7/os/$basearch/
gpgcheck=1
gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7

#released updates
[updates]
name=CentOS-$releasever - Updates
baseurl=http://mirrors.tuna.tsinghua.edu.cn/centos/7/updates/$basearch/
gpgcheck=1
gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7

#additional packages that may be useful
[extras]
name=CentOS-$releasever - Extras
baseurl=http://mirrors.tuna.tsinghua.edu.cn/centos/7/extras/$basearch/
gpgcheck=1
gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7

#additional packages that extend functionality of existing packages
[centosplus]
name=CentOS-$releasever - Plus
baseurl=http://mirrors.tuna.tsinghua.edu.cn/centos/7/centosplus/$basearch/
gpgcheck=1
enabled=0
gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7
```
## 清除旧版本信息,删除RedHat信息

``` python
yum clean all

yum install subscription-manager-gui
yum install libdevmapper* -y
yum install PackageKit

yum remove rhnlib redhat-support-tool redhat-support-lib-python

yum clean all
yum upgrade
```

## 查看更新后的结果：

``` python
[hadoop@slave10 ~]$ cat /etc/redhat-release
CentOS Linux release 7.2.1511 (Core)
```