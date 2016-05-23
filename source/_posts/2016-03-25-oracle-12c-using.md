---
title: "Oracle 12c 使用入门(1) ——SQL*Plus"
date: 2016-03-25 13:25:56 +0800
comments: true
categories: DataBase
toc: true
mathjax: true
tags : [Oracle,DataBase]
keywords: Oracle12c,数据库
---


>**本节内容：**本篇博文记录开始使用Oracle12c的一些步骤以及注意事项。

<!-- more -->

******

## 启动和关闭Oracle数据库

刚开始安装完Oracle数据库时，如果数据库未设置成自动启动，则启动服务后还需要启动数据库。启动数据库需要以`sys`用户在`SQL * PLUS`中执行`startup`命令。下面命令以操作系统验证的方式连接数据库，不需要输入账号和密码，连接数据库后，以sys用户执行各个操作：

```SQL
[hadoop@slave08 ~]$ sqlplus / as sysdba

SQL*Plus: Release 12.1.0.2.0 Production on Sat Mar 26 15:43:16 2016

Copyright (c) 1982, 2014, Oracle.  All rights reserved.


Connected to:
Oracle Database 12c Enterprise Edition Release 12.1.0.2.0 - 64bit Production
With the Partitioning, OLAP, Advanced Analytics and Real Application Testing options

SQL> startup
ORA-01081: cannot start already-running ORACLE - shut it down first
SQL> shutdown immediate
Database closed.
Database dismounted.
ORACLE instance shut down.
SQL> startup
ORACLE instance started.

Total System Global Area 4.0534E+10 bytes
Fixed Size		    7653432 bytes
Variable Size		 6308235208 bytes
Database Buffers	 3.4091E+10 bytes
Redo Buffers		  126562304 bytes
Database mounted.
Database opened.
SQL>

```

关闭数据库使用shutdown命令，可以附加normal,transactional,immediate以及abort选项，默认选项为normal，会等待所有用户退出连接后再关闭数据库，一般使用`immediate`。


## 常用SQL*PLus操作

Oracle数据库的客户端工具主要有SQL * Plus(字符界面数据库工具，面向应用开发人员以及数据库管理人员，用于执行SQL命令，编写存储过程，以及各种数据库管理任务) ,SQL Developer(Java编写的一个图形界面工具，面向对象主要是数据库应用开发人员) ,Enterprise Manager(OEM，使用浏览器管理数据，面向对象为数据库管理员)。

#### 使用SQL*Plus连接到本地数据库
要使用数据库，必须使用合法的用户及密码， 有以下三种方式:

 - 启动SQL*Plus的同时，输入用户名及密码。
 - 使用 nolog选项，单独启动SQL*Plus，然后再输入用户名和密码。
 - 单独启动SQL*Plus，根据提示输入用户名和密码。

#### 切换连接用户
SQL*Plus使用`connect`命令切换用户，`connect`关键字可以简写为`conn`,使用`show user`命令可以查看当前的数据库的用户名称。

#### 切换数据库
oracle的一个数据库对应一个服务，切换数据库也就是切换数据库服务。本地服务器如果创建了多个数据库，可以通过设置ORACLE_SID环境变量切换SQL*Plus默认连接的数据库。例如：`set oracle_sid=law12`便可以改变默认连接的数据库。

#### 执行SQL脚本文件
可以在文本文件中编辑任意SQL命令，然后保存为sql脚本文件，便可以在SQL*Plus中执行该脚本，使用`Start`或`@` 该脚本。

## 设置SQL*Plus环境
SQL*Plus在启动时，会自动执行`%ORACLE_HOME%\sqlplus\admin`目录下的`glogin.sql`文件，可以把经常使用的环境命令设置添加到次文件中。

```SQL
define_editor=vim  #设置ED命令默认使用的编辑器
set pagesize 100    #每页大小
set linesize 300    #设置每行容纳的字符数量
set sqlprompt `&_user&_connect_identifier>`  #把SQL提示符修改为当前用户名称以及连接的字符串
```

在SQL*Plus中，可以使用`column column_name format an`来设置字符串列的显示宽度，以避免显示结果因为换行而显得凌乱，其中的column以及format可以分别简写为col及for,a后面的数字用于指定字符的数量。

```SQL
col table_name for a8
col file_name  for a60
col segment_name for a8
col tablespace_name for a8
```
