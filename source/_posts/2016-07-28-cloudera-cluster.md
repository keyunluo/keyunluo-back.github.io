---
title: 搭建Cloudera集群
comments: true
toc: true
date: 2016-07-28 14:25:02
categories: Tools
tags : Cloudera
keywords: Cloudera，Hadoop集群
---

>**本节内容：**使用Cloudera Manager 5 利用镜像搭建Hadoop/Spark集群。


<!-- more -->

## SSH,JDK

- 配置ssh,使slave01能免密钥访问`slave02~slave10`,即将`slave01~slave10`的公钥`id_rsa.pub`添加到slave01的`~/.ssh/authorized_keys`中。

- `pip`设置代理

编辑`~/.pip/pip.conf`

``` python
[global]
index-url = http://pypi.tuna.tsinghua.edu.cn/simple
proxy  = http://server:8899

[install]
trusted-host=pypi.tuna.tsinghua.edu.cn

```

在root用户下拷贝pip设置：

``` python
mkdir ~/.pip
cp /home/hadoop/.pip/pip.conf ~/.pip/
```

- slave01安装pssh，pscp

root用户下安装pssh： `pip install -U pssh`

安装pscp:

``` python
export http_proxy="http://server:8899/"
export https_proxy="http://server:8899/"
git clone https://github.com/pssh/parallel-ssh
cd parallel-ssh
python setup.py install
```

- 新建slaves文件

新建一个`slaves`文件：

``` txt
slave02
slave03
slave04
slave05
slave06
slave07
slave08
slave09
slave10
```
- 批量拷贝安装pscp

``` python
pscp -h /home/hadoop/slaves -r parallel-ssh /home/hadoop/
sudo pssh -h ~/slaves "cd /home/hadoop/parallel-ssh && python setup.py install"
```

- 批量安装JDK,并设置环境变量

下载Oracle-JDK：

```  python
 wget http://download.oracle.com/otn-pub/java/jdk/8u102-b14/jdk-8u102-linux-x64.rpm?AuthParam=1469704553_712c2889a98a77ece53c26087d3ae902
 scp jdk-8u102-linux-x64.rpm\?AuthParam\=1469704553_712c2889a98a77ece53c26087d3ae902 hadoop@slave01:~/cloudera/jdk-8u102-linux-x64.rpm
```

安装JDK

``` python
rpm -ivh jdk-8u102-linux-x64.rpm
pscp -h /home/hadoop/slaves jdk-8u102-linux-x64.rpm /home/hadoop/jdk-8u102-linux-x64.rpm
pssh -h /home/hadoop/slaves "rpm -ivh /home/hadoop/jdk-8u102-linux-x64.rpm && rm -f /home/hadoop/jdk-8u102-linux-x64.rpm"
```

安装Scala:

root下：
``` python
wget http://downloads.lightbend.com.sixxs.org/scala/2.11.8/scala-2.11.8.rpm
rpm -ivh scala-2.11.8.rpm
pscp -h /home/hadoop/slaves scala-2.11.8.rpm /home/hadoop/
pssh -h /home/hadoop/slaves "rpm -ivh /home/hadoop/scala-2.11.8.rpm"
pssh -h /home/hadoop/slaves "rm -f /home/hadoop/scala-2.11.8.rpm"
```

设置环境变量

`sudo vim /etc/profile.d/cloudera.sh`:
```
export JAVA_HOME=/usr/java/default
export SCALA_HOME=/usr/share/scala
export M2_HOME=/usr/local/maven
export CLASSPATH=.:$JAVA_HOME/lib:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$SCALA_HOME/lib
export PATH=$JAVA_HOME/bin:$SCALA_HOME/bin:$M2_HOME/bin:$PATH
```

复制到其他节点

``` python
pscp -h /home/hadoop/slaves /etc/profile.d/cloudera.sh /etc/profile.d/cloudera.sh
pssh -h /home/hadoop/slaves "source /etc/profile"
```

## 安装Cloudera

- 配置Cloudera源

`vim /etc/yum.repos.d/Cloudera.repo`：

``` python
[cloudera-manager]
# Packages for Cloudera Manager, Version 5, on RedHat or CentOS 7 x86_64
name=Cloudera Manager
baseurl=http://192.168.80.80/cm5/redhat/7/x86_64/cm/5/
gpgkey =http://192.168.80.80/cm5/redhat/7/x86_64/cm/RPM-GPG-KEY-cloudera
gpgcheck = 0
```

复制到集群的其他节点上：`pscp -h /home/hadoop/slaves /etc/yum.repos.d/Cloudera.repo /etc/yum.repos.d/`,更新缓存：`pssh -h /home/hadoop/slaves "yum clean all;yum makecache"`.

导入key:`rpm --import http://archive.cloudera.com/cm5/redhat/7/x86_64/cm/RPM-GPG-KEY-cloudera`

在一个合适的目录下执行：

``` python
wget http://192.168.80.80/cm5/installer/latest/cloudera-manager-installer.bin
sudo chmod +x cloudera-manager-installer.bin
sudo ./cloudera-manager-installer.bin
```

**保证sudo无密码访问**：

``` python
# chmod u+w /etc/sudoers
grid ALL=(root)NOPASSWD:ALL
cloudera-scm  ALL=(ALL)  ALL
# chmod u-w /etc/sudoers
```


一路点确定后就安装好了，点击`http://slave01:7180/cmf/login`就可以登录，默认用户名和密码都是`admin`.

选择Express版本：
![版本](/resource/blog/2016-07/Cloudera.png)
选择机器：
![机器](/resource/blog/2016-07/Cloudera-Host.png)
选择Parcel：
![Parcel](/resource/blog/2016-07/Cloudera-Parcel.png)
选择集群CDH：
![CDH](/resource/blog/2016-07/Cloudera-Cluster.png)
不选择单用户模式：
安装界面：
![Install](/resource/blog/2016-07/Cloudera-Install.png)
检查，提示警告：`echo never > /sys/kernel/mm/transparent_hugepage/defrag`
安装所有服务：
![服务](/resource/blog/2016-07/Cloudera-Service.png)
集群分配：
![Configure](/resource/blog/2016-07/Cloudera-Configure.png)
接下来是数据库配置，**自己手动记录用户名和密码**。
接着是集群安装，最后安装成功如下：
![安装成功](/resource/blog/2016-07/Cloudera-Final.png)


