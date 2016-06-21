---
title: "Elasticsearch入门学习(1)——安装"
date: 2016-04-11 18:42:53 +0800
comments: true
toc: true
categories: Elasticsearch
tags : [elasticsearch , 大数据]
keywords: elasticsearch2.2安装
---


>**ElasticSearch**是一个基于Lucene的接近实时的搜索平台。它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口，是当前流行的企业级搜索引擎。本文先从安装Elasticsearch开始，一步一步探索。

<!-- more -->

##  ES下载和安装

- es(官方下载地址)[https://www.elastic.co/downloads/elasticsearch]，最新版本为2.3.1。不过github上有一个更好的选择(ElasticSearch-RTF)[https://github.com/medcl/elasticsearch-rtf.git],这个版本中已经集成了中文分词插件等，并且已经将插件配置好了，最新版本为2.2.1 ，非常适合初学者。安装时注意了，不要使用root权限用户安装es，否则es启动会报错，应该创建一个普通的用户安装es。

- 运行环境
  - Linux和windows均可
  - jdk7+
  - 32位/64位

- 配置文件

本文将Elasticsearch安装在`/opt/bigdata/elasticsearch`目录下，版本为`2.2.0`，集群为`Redhat7`，配置文件为`/opt/bigdata/elasticsearch/config/elasticsearch.yml`，下面贴上配置文件：

``` cpp
# ======================== Elasticsearch Configuration =========================
#
# NOTE: Elasticsearch comes with reasonable defaults for most settings.
#       Before you set out to tweak and tune the configuration, make sure you
#       understand what are you trying to accomplish and the consequences.
#
# The primary way of configuring a node is via this file. This template lists
# the most important settings you may want to configure for a production cluster.
#
# Please see the documentation for further information on configuration options:
# <http://www.elastic.co/guide/en/elasticsearch/reference/current/setup-configuration.html>
#
# ---------------------------------- Cluster -----------------------------------
#
# Use a descriptive name for your cluster:
# 集群名称
 cluster.name: Elasticsearch
#
# ------------------------------------ Node ------------------------------------
#
# Use a descriptive name for the node:
# 节点信息
 node.name: ES-Hadoop-Spark-Master
 node.master: true
 node.data: true
 index.number_of_replicas: 1
#
# Add custom attributes to the node:
#
# node.rack: r1
#
# ----------------------------------- Paths ------------------------------------
#
# Path to directory where to store the data (separate multiple locations by comma):
# 数据路径
 path.data: /opt/elasticsearch/data

#
# Path to log files:
#
# path.logs: /path/to/logs
 path.logs: /opt/elasticsearch/logs
 path.work: /opt/elasticsearch/work
# ----------------------------------- Memory -----------------------------------
#
# Lock the memory on startup:
#
# bootstrap.mlockall: true
#
# Make sure that the `ES_HEAP_SIZE` environment variable is set to about half the memory
# available on the system and that the owner of the process is allowed to use this limit.
#
# Elasticsearch performs poorly when the system is swapping the memory.
#
# ---------------------------------- Network -----------------------------------
#
# Set the bind address to a specific IP (IPv4 or IPv6):
# 网络，设置为外网可以访问
 network.host: 0.0.0.0
#
# Set a custom port for HTTP:
# 端口，默认9200
 http.port: 9200
#
# For more information, see the documentation at:
# <http://www.elastic.co/guide/en/elasticsearch/reference/current/modules-network.html>
#
# --------------------------------- Discovery ----------------------------------
#
# Pass an initial list of hosts to perform discovery when new node is started:
# The default list of hosts is ["127.0.0.1", "[::1]"]
#
# discovery.zen.ping.unicast.hosts: ["host1", "host2"]
#
# Prevent the "split brain" by configuring the majority of nodes (total number of nodes / 2 + 1):
#
# discovery.zen.minimum_master_nodes: 3
#
# For more information, see the documentation at:
# <http://www.elastic.co/guide/en/elasticsearch/reference/current/modules-discovery.html>
# 设置节点自动发现
 discovery.zen.minimum_master_nodes: 1
 discovery.zen.ping.timeout: 3s    ##节点间自动发现的响应时间
 discovery.zen.ping.unicast.hosts: ["192.168.100.101"]
# ---------------------------------- Gateway -----------------------------------
#
# Block initial recovery after a full cluster restart until N nodes are started:
#
# gateway.recover_after_nodes: 3
#
# For more information, see the documentation at:
# <http://www.elastic.co/guide/en/elasticsearch/reference/current/modules-gateway.html>
#
# ---------------------------------- Various -----------------------------------
#
# Disable starting multiple nodes on a single system:
#
# node.max_local_storage_nodes: 1
#
# Require explicit names when deleting indices:
#
# action.destructive_requires_name: true

```

在上述配置中，已经设置主节点为`192.168.100.101`，还有两个从节点`192.168.100.107`以及`192.168.100.110`，它们的配置不同点在于`Node`的设置，从节点中设置如下：`node.name: ES-Hadoop-Spark-Node1`，`node.master: false`,因为设置了节点的自动发现机制，所以在启动Elasticsearch后，集群会自动根据`cluster.name: Elasticsearch`来添加数据节点。

- 启动集群
 进入`/opt/bigdata/elasticsearch`，运行`bin/elasticsearch`,便可以启动`elasticsearch`，如果想当shell关闭时任然要在后台运行elasticsearch服务，可以运行命令`nohub bin/elasticsearch &`,这样当shell关闭时，仍可访问服务。分别进入到两个数据节点，运行同样的命令，这样，集群便搭建好了。
 当然我们也可以在启动的时候修改集群的名称和节点的名称。例如：
`bin/elasticsearch --cluster.name my_cluster_name --node.name my_node_name`来指定集群名字和主节点。


## ES插件安装

`head`插件可以很方便的查询数据和监控集群健康状况，下面以`head`插件安装为例，介绍下ES2.x下插件安装的一般步骤。

进入`/opt/bigdata/elasticsearch`目录，运行`bin/plugin install mobz/elasticsearch-head`, 下载完成后，访问网址`http://server:9200/_plugin/head/`，便可以看到如下界面，说明安装成功。
![head](/resource/blog/2016-04/elasticsearch-head.png)


插件可以分为核心插件和非核心插件，核心插件是官方提供的，可以直接安装，例如`bin/plugin install analysis-icu`，这样就会下载合适的版本安装到Elasticsearch中。非核心插件可以是官方提供的，也可以是社区提供的，可以从官方，Maven或者GitHub中下载安装：`bin/plugin install [org]/[user|component]/[version]`。

例如，安装github上的插件，`plugin install lmenezes/elasticsearch-kopf`，插件会尝试先到官方去下载，如果没有找到会到maven.com中去下载，如果在没有找到回到github中去下载。脚本还是非常智能的。当我们从直接maven中央库中安装时可以直接使用下面的方式，最后的版本号是必须要写的。
`plugin install org.elasticsearch.plugin/mapper-attachments/3.0.0 `
从自定义网址或者本地安装：`plugin install [url] `，例如，在本地文件系统中安装一个插件，可以运行：`plugin install file:///path/to/plugin.zip`。




