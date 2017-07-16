---
title: Hexo博客中的绘图
comments: true
toc: true
date: 2016-07-25 15:50:02
categories: Tools
tags : UML
keywords: Hexo博客绘图
---

>**本节内容：**UML是一种开放的方法，用于说明、可视化、构建和编写一个正在开发的、面向对象的、软件密集系统的制品的开放方法。本文介绍在Hexo中的两种绘图组件。

<!-- more -->

##  hexo-diagram

特点：
 - 时序图
 - 鲁棒图
 - 流程图

### 安装
```
npm install hexo-diagram --save
```

### 使用

diagram_type: sequence,robustness,flow,默认是时序图。

实例：

``` python
``` sequence
@found "You", ->
  @message "Think", ->
    @message "Write your idea", "JUMLY", ->
      @create "Diagram"
jumly

@found "Browser", ->
  @message "http request", "HTTP Server", ->
    @create "HTTP Session", ->
      @message "init"
      @message "aquire lock", "Database"
    @message "do something"
    @message "release lock", "Database"
    @reply "", "Browser"

```

![sequence](/resource/diagrams/2016-07-25-hexo-uml-diagram-0.png)

![sequence](/resource/diagrams/2016-07-25-hexo-uml-diagram-1.png)

流程图的例子：
``` python
//纵向流程图
st=>start: Start:>http://www.google.com[blank]
e=>end:>http://www.google.com
op1=>operation: My Operation
sub1=>subroutine: My Subroutine
cond=>condition: Yes
or No?:>http://www.google.com
io=>inputoutput: catch something...

st->op1->cond
cond(yes)->io->e
cond(no)->sub1(right)->op1

//横向流程图
st=>start: Start|past:>http://www.google.com[blank]
e=>end: End|future:>http://www.google.com
op1=>operation: My Operation|past
op2=>operation: Stuff|current
sub1=>subroutine: My Subroutine|invalid
cond=>condition: Yes
or No?|approved:>http://www.google.com
c2=>condition: Good idea|rejected
io=>inputoutput: catch something...|future

st->op1(right)->cond
cond(yes, right)->c2
cond(no)->sub1(left)->op1
c2(yes)->io->e
c2(no)->op2->e

```

![flow](/resource/diagrams/2016-07-25-hexo-uml-diagram-2.png)

![flow](/resource/diagrams/2016-07-25-hexo-uml-diagram-3.png)

![flow](/resource/diagrams/2016-07-25-hexo-uml-diagram-4.png)

## hexo-tag-plantuml

PlantUML是一个快速创建UML图形的组件，通过简单和直观的语言来定义图形，PlantUML支持的图形有：
- 时序图(sequence diagram)
- 用例图(use case diagram)
- 类图(class diagram)
- 活动图(activity diagram)
- 组件图(component diagram)
- 状态图(state diagram)
- 部署图(deployment diagram)
- 对象图(object diagram)
- 图形接口(wireframe graphical interface)

它的详细的官方文档为:[PlantUML Language Reference Guide](http://plantuml.com/PlantUML_Language_Reference_Guide.pdf)

### 安装

在Hexo博客的根目录下执行下面命令：

```
npm install hexo-tag-plantuml --save
```

本质上是使用在线生成的方式产生图片的。

### 简单使用

一个简单例子如下：

``` python
{% plantuml %}
Alice -> Bob: Authentication Request
Bob --> Alice: Authentication Response
Alice -> Bob: Another authentication Request
Alice <-- Bob: another authentication Response
{% endplantuml %}

{% plantuml %}
package org.nju.dislab.uml{
    class TestA {
        -String name
        +int id
    }

    class TestB extends TestA{
        -String desc
        +String getDesc()
        +void setDesc(String desc)
    }
}
{% endplantuml %}

```

{% plantuml %}
Alice -> Bob: Authentication Request
Bob --> Alice: Authentication Response
Alice -> Bob: Another authentication Request
Alice <-- Bob: another authentication Response
{% endplantuml %}

{% plantuml %}
package org.nju.dislab.uml{
    class TestA {
        -String name
        +int id
    }

    class TestB extends TestA{
        -String desc
        +String getDesc()
        +void setDesc(String desc)
    }
}
{% endplantuml %}
