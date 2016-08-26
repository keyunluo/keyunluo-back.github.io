---
title: 搭建Cloudera离线parcel源
comments: true
toc: true
date: 2016-07-28 13:25:02
categories: Tools
tags : Cloudera
keywords: Cloudera，镜像
---

>**本节内容：**Cloudera外网访问速度比较慢，并且服务器集群内部访问外网比较麻烦，因此在外网服务器中搭建Cloudera源，供安装使用。


<!-- more -->

## 配置网卡静态地址

外网服务器有五块网卡，eth0连接外网，eth2连接内网，这里配置eth1,对外界访问隔离：设置地址为`192.168.80.80`,编辑`/etc/network/interfaces`:

``` python
auto lo
iface lo inet loopback

iface eth1 inet static
address 192.168.80.80
netmask 255.255.255.0
gateway 192.168.80.2
```

## 集群配置网络

- 关闭SELINUX

编辑 `sudo vim /etc/selinux/config`，修改为 `
SELINUX=disabled`,然后重启。

- 修改host

编辑 `/etc/hosts`,添加一行`192.168.80.80   archive.cloudera.com`

## 配置Cloudera镜像

参考(cloudera_mirror)[https://github.com/sskaje/cloudera_mirror],搭建Cloudera镜像。

- 建立目录

``` python
/data/repo/cloudera_mirror
```

- 安装PHP

``` python
sudo apt-get install php5 php5-gd php5-cli
```

- 新建`fixlink.php`，固定链接

``` php
<?php
$domain = 'archive.cloudera.com.sixxs.org';

if (!isset($argv[1])) {
    echo "{$argv[0]} cm4|cm5 \n";
    exit;
}

if ($argv[1] == 'cm4') {
    $path = '/cm4/redhat/7/x86_64/cm/';
    $compare_with = '4';
} else if ($argv[1] == 'cm5') {
    $path = '/cm5/redhat/7/x86_64/cm/';
    $compare_with = '5';
}

$web_contents = file_get_contents('http://' . $domain . $path);
preg_match_all('#<a href="([\d\.a-z\-]+)/">([\d\.a-z\-]+)/</a>#', $web_contents, $m);

@chdir(__DIR__ . '/' . $domain . $path);
foreach ($m[2] as $k=>$v) {
    if ($v === $compare_with || strpos($v, $compare_with) === 0) {
        continue;
    }
    $name = $v;
    @unlink($name);
    symlink($compare_with, $name);
}

```

- 新建`clear_outdated.php`,获得更新

``` php
<?php
if (!isset($argv[1]) || (!preg_match('#^http://archive.cloudera.com.sixxs.org/.+/parcels/latest/$#', $argv[1]) && !preg_match('#^http://archive.cloudera.com.sixxs.org/cm#', $argv[1]))) {
        echo <<<USAGE
{$argv[0]} URL
        URL should be like:
                http://archive.cloudera.com.sixxs.org/PRODUCT/parcels/latest/
                http://archive.cloudera.com/cm4/redhat/7/x86_64/cm/4/
                http://archive.cloudera.com/cm5/redhat/7/x86_64/cm/5/

USAGE;
        exit;
}

$path = __DIR__ . '/' . substr($argv[1], strlen('http://'));
if (strpos($argv[1], 'parcels')) {
        $jsonfile = $path . 'manifest.json';

        if (!is_file($jsonfile)) {
                echo 'manifest.json not found';
                exit;
        }
        $json = json_decode(file_get_contents($jsonfile), true);
        $parcels = array();
        foreach ($json['parcels'] as $p) {
                $parcels[] = $p['parcelName'];
                $parcels[] = $p['parcelName'].'.sha1';
        }

        chdir($path);
        $files = glob('*');

        foreach ($files as $f) {
                if ($f == 'manifest.json') continue;
                if (!in_array($f, $parcels)) {
                        echo "Deleting outdated file {$f}...\n";
                        unlink($f);
                }
        }
} else {
        chdir($path);
        $files = glob('*');

        $tree = array();
        foreach ($files as $f) {
                preg_match('#\d#', $f, $m, PREG_OFFSET_CAPTURE);
                if (isset($m[0][1]) && $m[0][1]) {
                        $tree[substr($f, 0, $m[0][1])][] = $f;
                }
        }

        foreach ($tree as $f) {
                $keep = max($f);
                foreach ($f as $_f) {
                        if ($_f != $keep) {
                                echo "Removing file ", $_f, "\n";
                                unlink($_f);
                        }
                }

        }

}

```

- 批量下载，新建`wget.sh`,使用`sixxs.org`加速

``` shell
#!/bin/bash

WGET="/usr/bin/wget -mc -e robots=off"
PHP=/usr/bin/php
FIND=/usr/bin/find
CURRENT_DIR=`dirname "$0"`

PARCELS=(
    "http://archive.cloudera.com.sixxs.org/cdh5/parcels/latest/"
    "http://archive.cloudera.com.sixxs.org/cdh5/parcels/5/"
    "http://archive.cloudera.com.sixxs.org/gplextras5/parcels/latest/"
    "http://archive.cloudera.com.sixxs.org/cm5/installer/latest/"
    "http://archive.cloudera.com.sixxs.org/sqoop-connectors/parcels/latest/"
    "http://archive.cloudera.com.sixxs.org/beta/impala-kudu/parcels/latest/"
    "http://archive.cloudera.com.sixxs.org/beta/kudu/parcels/latest/"
    "http://archive.cloudera.com.sixxs.org/cdh4/parcels/latest/"
    "http://archive.cloudera.com.sixxs.org/search/parcels/latest/"
    "http://archive.cloudera.com.sixxs.org/impala/parcels/latest/"
    "http://archive.cloudera.com.sixxs.org/sentry/parcels/latest/"
    "http://archive.cloudera.com.sixxs.org/gplextras/parcels/latest/"
    "http://archive.cloudera.com.sixxs.org/spark/parcels/latest/"
    "http://archive.cloudera.com.sixxs.org/accumulo/parcels/latest/"
    "http://archive.cloudera.com.sixxs.org/cm4/installer/latest/"

    "http://archive.cloudera.com.sixxs.org/accumulo-c5/parcels/latest/"
)

function download_parcel()
{
    $WGET  $1 --accept-regex='.*el7.*'
    $WGET  $1/manifest.json
    $PHP clear_outdated.php $1
}

cd $CURRENT_DIR

$WGET  http://archive.cloudera.com.sixxs.org/cm4/redhat/7/x86_64/cm/4/ --accept-regex='\/4\/' #--reject-regex='index\.html'
$WGET  http://archive.cloudera.com.sixxs.org/cm4/installer/latest/cloudera-manager-installer.bin
$WGET  http://archive.cloudera.com.sixxs.org/cm4/redhat/7/x86_64/cm/RPM-GPG-KEY-cloudera
$WGET  http://archive.cloudera.com.sixxs.org/cm4/redhat/7/x86_64/cm/cloudera-manager.repo
$PHP clear_outdated.php http://archive.cloudera.com.sixxs.org/cm4/redhat/7/x86_64/cm/4/RPMS/x86_64/

$WGET  http://archive.cloudera.com.sixxs.org/cm5/redhat/7/x86_64/cm/5/ --accept-regex='\/5\/' #--reject-regex='index\.html'
$WGET  http://archive.cloudera.com.sixxs.org/cm5/installer/latest/cloudera-manager-installer.bin
$WGET  http://archive.cloudera.com.sixxs.org/cm5/redhat/7/x86_64/cm/RPM-GPG-KEY-cloudera
$WGET  http://archive.cloudera.com.sixxs.org/cm5/redhat/7/x86_64/cm/cloudera-manager.repo
$PHP clear_outdated.php http://archive.cloudera.com.sixxs.org/cm5/redhat/7/x86_64/cm/5/RPMS/x86_64/

# fix cm yum repo link
$PHP fixlink.php cm4
$PHP fixlink.php cm5

# Download All parcels
for i in ${PARCELS[@]}; do
    download_parcel $i;
done



```

## 下载镜像

``` python
chmod +x wget.sh
./wget.sh
```

## 配置`nginx`

编辑`/etc/nginx/sites-available/cloudera.conf `

``` python
server {
        listen        192.168.80.80:80;
        server_name   archive.cloudera.com.dislab;
        access_log    logs/archive.cloudera.log;
        root   /data/repo/cloudera_mirror/archive.cloudera.com.sixxs.org/ ;
    autoindex on;
        autoindex_exact_size    off;
        autoindex_localtime     on;


```

软链到`sites-enabled`:

``` python
sudo ln -s /etc/nginx/sites-available/cloudera.conf /etc/nginx/sites-enabled/
sudo service nginx reload
```

这样，镜像就搭好了。

![镜像](/resource/blog/2016-07/Cloudera-Parcel-Repo.png)



