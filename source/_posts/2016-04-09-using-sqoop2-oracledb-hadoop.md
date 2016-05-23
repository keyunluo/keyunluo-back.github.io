---
title: "使用Sqoop从Oracle中导入数据到hadoop"
date: 2016-04-09 13:28:43 +0800
comments: true
categories: BigData
toc: true
mathjax: true
tags : [Oracle,DataBase,sqoop,hadoop]
keywords: Oracle12c,数据库,Sqoop
---


>**本节内容：**本篇博文记录从Oracle12c导入数据到Hadoop中的流程，使用Sqoop2工具,目前Sqoop2只支持将oracle/mysql数据库中的数据与HDFS中数据相互导入导出，目前并不支持导入到HBase/Hive中，因此在实际业务中并不好用，后面介绍使用Sqoop1导入数据到Hbase的流程。

<!-- more -->

******

## Sqoop2安装过程

- *1*: 下载最新版本的sqoop2:sqoop1.99.6

`wget http://mirrors.ustc.edu.cn/apache/sqoop/1.99.6/sqoop-1.99.6-bin-hadoop200.tar.gz`

本人将其安装在服务器`slave01`中的`/opt/bigdata/sqoop`目录下。

- *2*:配置环境变量:在`/etc/profile`中配置环境变量

``` python
export SQOOP2_HOME=/opt/bigdata/sqoop
export PATH=$PATH:$SQOOP2_HOME/bin
export CATALINA_BASE=$SQOOP2_HOME/server
export LOGDIR=$SQOOP2_HOME/logs
```
在Bash中运行`source /etc/profile`使环境变量立即生效。

- *3*: Sqoop服务端配置

编辑` $SQOOP2_HOME/server/conf/catalina.properties`文件，修改`common.loader `配置，将Hadoop路径下的jar包全部包括进去。

``` text
common.loader=${catalina.base}/lib/*.jar,${catalina.base}/lib/*.jar,${catalina.home}/lib/*.jar,${catalina.home}/lib/*.jar,${catalina.home}/../lib/*.jar,/opt/bigdata/sqoop/server/lib/*.jar,/opt/hadoop-2.7.1/etc/hadoop,/opt/hadoop-2.7.1/share/hadoop/common/lib/*.jar,/opt/hadoop-2.7.1/share/hadoop/common/*.jar,/opt/hadoop-2.7.1/share/hadoop/hdfs/*.jar,/opt/hadoop-2.7.1/share/hadoop/hdfs/lib/*.jar,/opt/hadoop-2.7.1/share/hadoop/hdfs/*.jar,/opt/hadoop-2.7.1/share/hadoop/yarn/lib/*.jar,/opt/hadoop-2.7.1/share/hadoop/yarn/*.jar,/opt/hadoop-2.7.1/share/hadoop/mapreduce/lib/*.jar,/opt/hadoop-2.7.1/share/hadoop/mapreduce/*.jar,/opt/bigdata/hbase/lib/*.jar
```

编辑`$SQOOP2_HOME/server/conf/sqoop.properties`文件，修改Hadoop安装的配置目录:

``` text
org.apache.sqoop.submission.engine.mapreduce.configuration.directory=/opt/hadoop-2.7.1/etc/hadoop/
```
找到这行`org.apache.sqoop.repository.jdbc.url=jdbc:derby:@BASEDIR@/repository/db;create=true`,修改`@BASEDIR@`为`sqoop2`安装目录：

```text
org.apache.sqoop.repository.jdbc.url=jdbc:derby:/opt/bigdata/sqoop/repository/db;create=true
```
- *4*:下载数据库驱动

必备驱动：Mysql数据取驱动是必须使用的，这里使用的是`mysql-connector-java-5.1.37-bin.jar`

Oracle驱动：因为要连接Oracle数据库，所以还要使用oracle的驱动：`ojdbc7.jar`

将上述两个驱动复制到`$SQOOP2_HOME/server/lib`目录下，至此，配置完成。

## 验证sqoop2安装

使用`sqoop2-tool verify`验证Sqoo2是否安装成功：

``` java
[hadoop@slave01 sqoop]$ sqoop2-tool verify
Sqoop home directory: /opt/bigdata/sqoop
Setting SQOOP_HTTP_PORT:     12000
Setting SQOOP_ADMIN_PORT:     12001
Using   CATALINA_OPTS:
Adding to CATALINA_OPTS:    -Dsqoop.http.port=12000 -Dsqoop.admin.port=12001
Apr 09, 2016 4:13:03 PM org.apache.catalina.startup.ClassLoaderFactory validateFile
WARNING: Problem with directory [/opt/bigdata/sqoop/lib], exists: [false], isDirectory: [false], canRead: [false]
Sqoop tool executor:
	Version: 1.99.6
	Revision: 07244c3915975f26f03d9e1edf09ab7d06619bb8
	Compiled on Wed Apr 29 10:40:43 CST 2015 by root
Running tool: class org.apache.sqoop.tools.tool.VerifyTool
16/04/09 16:13:03 INFO core.SqoopServer: Booting up Sqoop server
16/04/09 16:13:03 INFO core.PropertiesConfigurationProvider: Starting config file poller thread
log4j: Parsing for [root] with value=[WARN, file].
log4j: Level token is [WARN].
log4j: Category root set to WARN
log4j: Parsing appender named "file".
log4j: Parsing layout options for "file".
log4j: Setting property [conversionPattern] to [%d{ISO8601} %-5p %c{2} [%l] %m%n].
log4j: End of parsing for "file".
log4j: Setting property [file] to [@LOGDIR@/sqoop.log].
log4j: Setting property [maxBackupIndex] to [5].
log4j: Setting property [maxFileSize] to [25MB].
log4j: setFile called: @LOGDIR@/sqoop.log, true
log4j: setFile ended
log4j: Parsed "file" options.
log4j: Parsing for [org.apache.sqoop] with value=[DEBUG].
log4j: Level token is [DEBUG].
log4j: Category org.apache.sqoop set to DEBUG
log4j: Handling log4j.additivity.org.apache.sqoop=[null]
log4j: Parsing for [org.apache.derby] with value=[INFO].
log4j: Level token is [INFO].
log4j: Category org.apache.derby set to INFO
log4j: Handling log4j.additivity.org.apache.derby=[null]
log4j: Finished configuring.
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/opt/hadoop-2.7.1/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/opt/bigdata/hbase/lib/slf4j-log4j12-1.7.10.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.slf4j.impl.Log4jLoggerFactory]
log4j: Could not find root logger information. Is this OK?
log4j: Parsing for [default] with value=[INFO,defaultAppender].
log4j: Level token is [INFO].
log4j: Category default set to INFO
log4j: Parsing appender named "defaultAppender".
log4j: Parsing layout options for "defaultAppender".
log4j: Setting property [conversionPattern] to [%d %-5p %c: %m%n].
log4j: End of parsing for "defaultAppender".
log4j: Setting property [file] to [@LOGDIR@/default.audit].
log4j: setFile called: @LOGDIR@/default.audit, true
log4j: setFile ended
log4j: Parsed "defaultAppender" options.
log4j: Handling log4j.additivity.default=[null]
log4j: Finished configuring.
log4j: Finalizing appender named [EventCounter].
Exception in thread "PurgeThread" org.apache.sqoop.common.SqoopException: JDBCREPO_0009:Failed to finalize transaction
	at org.apache.sqoop.repository.JdbcRepositoryTransaction.close(JdbcRepositoryTransaction.java:115)
	at org.apache.sqoop.repository.JdbcRepository.doWithConnection(JdbcRepository.java:111)
	at org.apache.sqoop.repository.JdbcRepository.doWithConnection(JdbcRepository.java:63)
	at org.apache.sqoop.repository.JdbcRepository.purgeSubmissions(JdbcRepository.java:591)
	at org.apache.sqoop.driver.JobManager$PurgeThread.run(JobManager.java:660)
Caused by: java.sql.SQLNonTransientConnectionException: No current connection.
	at org.apache.derby.impl.jdbc.SQLExceptionFactory40.getSQLException(Unknown Source)
	at org.apache.derby.impl.jdbc.Util.newEmbedSQLException(Unknown Source)
	at org.apache.derby.impl.jdbc.Util.newEmbedSQLException(Unknown Source)
	at org.apache.derby.impl.jdbc.Util.noCurrentConnection(Unknown Source)
	at org.apache.derby.impl.jdbc.EmbedConnection.checkIfClosed(Unknown Source)
	at org.apache.derby.impl.jdbc.EmbedConnection.setupContextStack(Unknown Source)
	at org.apache.derby.impl.jdbc.EmbedConnection.commit(Unknown Source)
	at org.apache.commons.dbcp.DelegatingConnection.commit(DelegatingConnection.java:334)
	at org.apache.commons.dbcp.DelegatingConnection.commit(DelegatingConnection.java:334)
	at org.apache.commons.dbcp.PoolingDataSource$PoolGuardConnectionWrapper.commit(PoolingDataSource.java:211)
	at org.apache.sqoop.repository.JdbcRepositoryTransaction.close(JdbcRepositoryTransaction.java:112)
	... 4 more
Caused by: java.sql.SQLException: No current connection.
	at org.apache.derby.impl.jdbc.SQLExceptionFactory.getSQLException(Unknown Source)
	at org.apache.derby.impl.jdbc.SQLExceptionFactory40.wrapArgsForTransportAcrossDRDA(Unknown Source)
	... 15 more
Verification was successful.
Tool class org.apache.sqoop.tools.tool.VerifyTool has finished correctly.
[hadoop@slave01 sqoop]$

```

忽略`JDBCREPO_0009`异常，出现`Verification was successful`则说明安装是没有问题的。

## 连接Oracle数据库

- 1 启动sqoop

``` python
[hadoop@slave01 sqoop]$ sqoop.sh server start
Sqoop home directory: /opt/bigdata/sqoop
Setting SQOOP_HTTP_PORT:     12000
Setting SQOOP_ADMIN_PORT:     12001
Using   CATALINA_OPTS:
Adding to CATALINA_OPTS:    -Dsqoop.http.port=12000 -Dsqoop.admin.port=12001
Using CATALINA_BASE:   /opt/bigdata/sqoop/server
Using CATALINA_HOME:   /opt/bigdata/sqoop/server
Using CATALINA_TMPDIR: /opt/bigdata/sqoop/server/temp
Using JRE_HOME:        /opt/oracle_jdk_1.7
Using CLASSPATH:       /opt/bigdata/sqoop/server/bin/bootstrap.jar

```

- 2 使用`sqoop2-shell`操作

``` python
[hadoop@slave01 sqoop]$ sqoop2-shell
Sqoop home directory: /opt/bigdata/sqoop
Sqoop Shell: Type 'help' or '\h' for help.

sqoop:000> show version --all
client version:
  Sqoop 1.99.6 source revision 07244c3915975f26f03d9e1edf09ab7d06619bb8
  Compiled by root on Wed Apr 29 10:40:43 CST 2015
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:/opt/hadoop-2.7.1/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:/opt/bigdata/sqoop/shell/lib/slf4j-log4j12-1.6.1.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.slf4j.impl.Log4jLoggerFactory]
16/04/09 16:19:50 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
server version:
  Sqoop 1.99.6 source revision 07244c3915975f26f03d9e1edf09ab7d06619bb8
  Compiled by root on Wed Apr 29 10:40:43 CST 2015
API versions:
  [v1]
sqoop:000>

```

- 3 查看可用的connectors

``` python
sqoop:000> show connector
+----+------------------------+---------+------------------------------------------------------+----------------------+
| Id |          Name          | Version |                        Class                         | Supported Directions |
+----+------------------------+---------+------------------------------------------------------+----------------------+
| 1  | kite-connector         | 1.99.6  | org.apache.sqoop.connector.kite.KiteConnector        | FROM/TO              |
| 2  | kafka-connector        | 1.99.6  | org.apache.sqoop.connector.kafka.KafkaConnector      | TO                   |
| 3  | hdfs-connector         | 1.99.6  | org.apache.sqoop.connector.hdfs.HdfsConnector        | FROM/TO              |
| 4  | generic-jdbc-connector | 1.99.6  | org.apache.sqoop.connector.jdbc.GenericJdbcConnector | FROM/TO              |
+----+------------------------+---------+------------------------------------------------------+----------------------+
sqoop:000>
```
可以看到，我们将要使用的是hdfs和jdbc连接。

- 4 创建Oracle连接

``` python
sqoop:000> create link -c 4
Creating link for connector with id 4
Please fill following values to create new link object
Name: Oracle DB

Link configuration

JDBC Driver Class: oracle.jdbc.OracleDriver
JDBC Connection String: jdbc:oracle:thin:@//192.168.100.108/orcl
Username: c##hadoop
Password: ******
JDBC Connection Properties:
There are currently 0 values in the map:
entry#
New link was successfully created with validation status OK and persistent id 6

```

- 5 创建HDFS连接

``` python
sqoop:000> create link -c 3
Creating link for connector with id 3
Please fill following values to create new link object
Name: HDFS Link

Link configuration

HDFS URI: hdfs://slave01:9000/
Hadoop conf directory: /opt/hadoop-2.7.1/etc/hadoop
New link was successfully created with validation status OK and persistent id 7

```

- 6 验证连接

``` python
sqoop:000> show link
+----+------------+--------------+------------------------+---------+
| Id |    Name    | Connector Id |     Connector Name     | Enabled |
+----+------------+--------------+------------------------+---------+
| 6  | Oracle DB  | 4            | generic-jdbc-connector | true    |
| 7  | HDFS Link  | 3            | hdfs-connector         | true    |
+----+------------+--------------+------------------------+---------+

```

- 7 创建`Sqoop Job`

``` python
sqoop:000> create job -f 6 -t 7
Creating job for links with from id 6 and to id 7
Please fill following values to create new job object
Name: Oracle HDFS

From database configuration

Schema name: C##hadoop
Table name: T_DTXX_GPSXX20160317
Table SQL statement:
Table column names: ID,ZDBH,GPSSJ,JLSJ,CCSJ,JD,WD,WXSL,BZ
Partition column name: ID
Null value allowed for the partition column:
Boundary query:

Incremental read

Check column:
Last value:

To HDFS configuration

Override null value:
Null value:
Output format:
  0 : TEXT_FILE
  1 : SEQUENCE_FILE
Choose: 0
Compression format:
  0 : NONE
  1 : DEFAULT
  2 : DEFLATE
  3 : GZIP
  4 : BZIP2
  5 : LZO
  6 : LZ4
  7 : SNAPPY
  8 : CUSTOM
Choose: 0
Custom compression format:
Output directory: /user/hadoop/oracledata
Append mode:

Throttling resources

Extractors:
Loaders:
New job was successfully created with validation status OK  and persistent id 3
sqoop:000>

```

- 8 查看Job

``` python
sqoop:000> show job
+----+--------------+----------------+--------------+---------+
| Id |     Name     | From Connector | To Connector | Enabled |
+----+--------------+----------------+--------------+---------+
| 1  | OH           | 4              | 3            | true    |
| 2  | 1519         | 4              | 3            | true    |
| 3  | Oracle HDFS  | 4              | 3            | true    |
+----+--------------+----------------+--------------+---------+

```

其中`3`就是我们刚刚创建的Job。

- 9 启动Job

``` python
sqoop:000> start job -j 3
Submission details
Job ID: 3
Server URL: http://localhost:12000/sqoop/
Created by: hadoop
Creation date: 2016-04-09 16:46:57 CST
Lastly updated by: hadoop
External ID: job_1459172324738_0017
	http://slave01:8088/proxy/application_1459172324738_0017/
Source Connector schema: Schema{name=C##hadoop.T_DTXX_GPSXX20160317,columns=[
	Text{name=ID,nullable=true,type=TEXT,charSize=null},
	Text{name=ZDBH,nullable=true,type=TEXT,charSize=null},
	Date{name=GPSSJ,nullable=true,type=DATE_TIME,hasFraction=true,hasTimezone=false},
	Date{name=JLSJ,nullable=true,type=DATE_TIME,hasFraction=true,hasTimezone=false},
	Date{name=CCSJ,nullable=true,type=DATE_TIME,hasFraction=true,hasTimezone=false},
	Text{name=JD,nullable=true,type=TEXT,charSize=null},
	Text{name=WD,nullable=true,type=TEXT,charSize=null},
	Decimal{name=WXSL,nullable=true,type=DECIMAL,precision=2,scale=0},
	Text{name=BZ,nullable=true,type=TEXT,charSize=null}]}
2016-04-09 16:46:57 CST: BOOTING  - Progress is not available

```
没有抛出异常后，sqoop就已经在后台开始运行了，这时候可以查看Hadoop JobServer,可以看到任务已经在运行了。

- 10 查看HDFS上数据

``` python
[hadoop@slave01 sqoop]$ hdfs dfs -ls oracledata
Found 8 items
-rw-r--r--   2 hadoop supergroup   46362076 2016-04-09 16:47 oracledata/034fd092-bd7a-462a-a566-05cd123d11ed.txt
-rw-r--r--   2 hadoop supergroup   92831643 2016-04-09 16:47 oracledata/0b537981-1711-411a-bb09-d304c83839ea.txt
-rw-r--r--   2 hadoop supergroup   46547213 2016-04-09 16:47 oracledata/1526e2d2-b385-4a45-9841-f8001fc74a7e.txt
-rw-r--r--   2 hadoop supergroup  185898348 2016-04-09 16:48 oracledata/851645db-b5a3-4265-8ead-916c8680dc11.txt
-rw-r--r--   2 hadoop supergroup   92913401 2016-04-09 16:47 oracledata/da722cf7-38d9-41c0-b17f-dcd5a19a69e6.txt
-rw-r--r--   2 hadoop supergroup  139531748 2016-04-09 16:48 oracledata/dacf1fc9-ca48-4058-837e-9380aefc2dfe.txt
-rw-r--r--   2 hadoop supergroup  139583915 2016-04-09 16:48 oracledata/e393d0f5-f05a-4790-bde0-9d8d8d4a7b06.txt

```

至此，数据已经导入了

## 常用命令总结

``` python
set option --name verbose --value true 设置异常显示，便于查错
sqoop.sh server start    启动
sqoop.sh server stop     停止
sqoop.sh client          进入客户端
set server --host hadoopMaster --port 12000 --webapp sqoop 设置服务器，注意hadoopMaster为hdfs主机名
show connector --all    查看连接类型
create link --cid 1    创建连接，cid为连接类型id
show link 查看连接
update link -l 1 修改id为1的连接
delete link -l 1 删除id为1的连接
create job -f 1 -t 2 创建从连接1到连接2的job
show job 查看job
update job -jid 1    修改job
delete job -jid 1    删除job
status job -jid 1    看看job状态

```






