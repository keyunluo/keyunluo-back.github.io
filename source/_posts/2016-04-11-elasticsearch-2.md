---
title: "Elasticsearch入门学习(2)——服务URL"
date: 2016-04-11 19:42:53 +0800
comments: true
toc: true
categories: BigData
tags : [elasticsearch , 大数据]
keywords: elasticsearch服务URL
---


>**ElasticSearch** 将各种功能、配置、服务都以API的形式暴露，这也是elasticsearch区别于其他搜索引擎框架的一个特征，本文将ES中常用的URL整理以供查询。

<!-- more -->

##  与Elasticsearch交互

如何与Elasticsearch交互取决于你是否使用Java，可以分为`Java API交互`和`基于HTTP协议，以JSON为数据交互格式的RESTful API`两种，本文主要讲解使用RESTful API，通过9200端口的与Elasticsearch进行通信。使用java则端口为9300.

向Elasticsearch发出的请求的组成部分与其它普通的HTTP请求是一样的：

``` cpp
curl -X<VERB> '<PROTOCOL>://<HOST>:<PORT>/<PATH>?<QUERY_STRING>' -d '<BODY>'
```

- VERB HTTP方法：GET, POST, PUT, HEAD, DELETE
- PROTOCOL http或者https协议（只有在Elasticsearch前面有https代理的时候可用）
- HOST Elasticsearch集群中的任何一个节点的主机名，如果是在本地的节点，那么就叫localhost
- PORT Elasticsearch HTTP服务所在的端口，默认为9200
- PATH API路径（例如_count将返回集群中文档的数量），PATH可以包含多个组件，例如_cluster/stats或者_nodes/stats/jvm
- QUERY_STRING 一些可选的查询请求参数，例如?pretty参数将使请求返回更加美观易读的JSON数据
- BODY 一个JSON格式的请求主体（如果请求需要的话）

例如，为了计算集群中的文档数量，我们可以这样做：

``` python
[hadoop@slave01 ~]$ curl -XGET 'http://localhost:9200/_count?pretty' -d '
{
    "query": {
        "match_all": {}
    }
}
'
{
  "count" : 3498781,
  "_shards" : {
    "total" : 37,
    "successful" : 37,
    "failed" : 0
  }
}

```
Elasticsearch返回一个类似200 OK的HTTP状态码和JSON格式的响应主体（除了HEAD请求）。

##  Elasticsearch常见服务URL

- HTTP方法: POST新增，PUT更新，GET获取，DELETE删除，HEAD判断是否存在

- 集群健康查看:`curl  'http://server:9200/_cat/health?v'`

``` cpp
epoch      timestamp cluster       status node.total node.data shards pri relo init unassign pending_tasks max_task_wait_time active_shards_percent
1460378717 20:45:17  Elasticsearch green           3         3     74  37    0    0        0             0                  -                100.0%

```


- 节点健康查看:`curl  'http://server:9200/_cat/nodes?v'`

``` cpp
host            ip              heap.percent ram.percent load node.role master name
192.168.100.107 192.168.100.107           30          22 0.00 d         -      ES-Hadoop-Spark-Node1
192.168.100.110 192.168.100.110           11          23 0.01 d         -      ES-Hadoop-Spark-Node2
192.168.100.101 192.168.100.101           19          27 1.80 d         *      ES-Hadoop-Spark-Master

```

- 列出集群索引:`curl  'http://server:9200/_cat/indices?v'`

``` cpp
health status index                 pri rep docs.count docs.deleted store.size pri.store.size
green  open   demo                    5   1          1            0      7.9kb          3.9kb
green  open   my_index                5   1         11            0     53.4kb         26.7kb
green  open   .marvel-es-2016.03.29   1   1     162773           54     94.6mb         47.3mb


```

-  创建customer索引,pretty表示打印json响应:`curl -XPUT 'http://server:9200/customer?pretty'`

``` cpp
{
  "acknowledged" : true
}

```

- 索引数据：`curl -XPUT 'http://server:9200/customer/external/1?pretty' '-d { "name":"JOhn Doe"}' `

``` cpp
{
  "_index" : "customer",
  "_type" : "external",
  "_id" : "1",
  "_version" : 1,
  "_shards" : {
    "total" : 2,
    "successful" : 2,
    "failed" : 0
  },
  "created" : true
}
```

- 查询数据：`curl -XGET 'http://server:9200/customer/external/1?pretty'`

``` cpp
{
  "_index" : "customer",
  "_type" : "external",
  "_id" : "1",
  "_version" : 1,
  "found" : true,
  "_source" : {
    "name" : "JOhn Doe"
  }
}

```

- 删除索引:`curl -XDELETE 'http://server:9200/customer?pretty` `

- 通过id更新索引数据:`curl -XPUT 'http://server:9200/customer/external/1?pretty' '-d { "name":"JOhn Doe"}' `

``` cpp
{
  "_index" : "customer",
  "_type" : "external",
  "_id" : "1",
  "_version" : 2,
  "_shards" : {
    "total" : 2,
    "successful" : 2,
    "failed" : 0
  },
  "created" : false
}

```

- 添加索引数据随机id:`curl -XPOST 'http://server:9200/customer/external?pretty' '-d { "name":"JOhn Doe"}' `

``` cpp
{
  "_index" : "customer",
  "_type" : "external",
  "_id" : "AVQFbXxylcDfhGhMoLpJ",
  "_version" : 1,
  "_shards" : {
    "total" : 2,
    "successful" : 2,
    "failed" : 0
  },
  "created" : true
}

```

- 通过id删除：`curl -XDELETE 'http://server:9200/customer/external/2?pretty' `

- 通过查询删除:

``` cpp
curl -XDELETE 'http://server:9200/customer/external/_query?pretty' -d '
{
  "query": { "match": { "name": "John" } }
}'
```

- 批量新增

``` cpp
curl -XPOST 'http://server:9200/customer/external/_bulk?pretty' -d '
{"index":{"_id":"1"}}
{"name": "John Doe" }
{"index":{"_id":"2"}}
{"name": "Jane Doe" }
'
```

- 批量更新/删除

``` cpp
curl -XPOST 'http://server:9200/customer/external/_bulk?pretty' -d '
{"update":{"_id":"1"}}
{"doc": { "name": "John Doe becomes Jane Doe" } }
{"delete":{"_id":"2"}}
```

- 读文件批量索引

```cpp
curl -XPOST 'http://server:9200/bank/account/_bulk?pretty' --data-binary @accounts.json
```

- 批量索引操作

``` cpp
curl -XPOST 'http://server:9200/bank/_search?pretty' -d '
{
  "query": {
    "bool": {
      "must": [
        { "match": { "address": "mill" } },
        { "match": { "address": "lane" } }
      ]
    }
  }
}'
```

- 查看进程信息，包括打开文件数，是否锁定内存等:`curl 'http://server:9200/_nodes/process?pretty'`

## 索引相关

|    URL   | 说明  |
| :--------| :---- |
|/index/_search|不解释|
|/_aliases|获取或操作索引的别名|
|/index/| |
|/index/type/|创建或操作类型|
|/index/_mapping|创建或操作mapping|
|/index/_settings|创建或操作设置(number_of_shards是不可更改的)|
|/index/_open|打开被关闭的索引|
|/index/_close|关闭索引|
|/index/_refresh|刷新索引（使新加内容对搜索可见）|
|/index/_flush|刷新索引，将变动提交到lucene索引文件中，并清空elasticsearch的transaction log|
|/index/_optimize|优化segement，个人认为主要是对segement进行合并|
|/index/_status|获得索引的状态信息|
|/index/_segments|获得索引的segments的状态信息|
|/index/_explain|不执行实际搜索，而返回解释信息|
|/index/_analyze|不执行实际搜索，根据输入的参数进行文本分析|
|/index/type/id|操作指定文档，不解释|
|/index/type/id/_create|创建一个文档，如果该文件已经存在，则返回失败|
|/index/type/id/_update|更新一个文件，如果改文件不存在，则返回失败|

## 集群相关

|    URL   | 说明  |
| :--------| :---- |
|/_cluster/nodes|获得集群中的节点列表和信息|
|/_cluster/health|获得集群信息|
|/_cluster/state|获得集群里的所有信息（集群信息、节点信息、mapping信息等）|

## 节点相关

|    URL   | 说明  |
| :--------| :---- |
|/_nodes/process|主要看file descriptor 这个信息|
|/_nodes/process/stats|统计信息（内存、CPU等）|
|/_nodes/jvm|获得各节点的虚拟机统计和配置信息|
|/_nodes/jvm/stats|更详细的虚拟机信息|
|/_nodes/http|获得各个节点的http信息（如ip地址）|
|/_nodes/http/stats|获得各个节点处理http请求的统计情况|
|/_nodes/thread_pool|获得各种类型的线程池，（elasticsearch分别对不同的操作提供不同的线程池）的配置信息|
|/_nodes/thread_pool/stats|获得各种类型的线程池的统计信息|

以上这些操作可以通过如：

`/_nodes/${nodeId}/jvm/stats`，`/_nodes/${nodeip}/jvm/stats`，`/_nodes/${nodeattribute}/jvm/stats`的形式针对指定节点的操作。




