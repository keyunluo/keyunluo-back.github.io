---
title: PlantUML时序图绘制
comments: true
toc: true
date: 2016-07-25 16:50:02
categories: Tools
tags : PlantUML
keywords: PlantUML绘图教程——时序图
---

>**本节内容：**PlantUML是一个开源项目，并支持快速绘制常见的UML图像,本节主要学习其时序图的用法,时序图通过描述对象之间发送消息的时间顺序显示多个对象之间的动态协作。

<!-- more -->


## 简单示例

你可以使用->来绘制参与者之间的消息传递， 而不必显式的声明参与者。
你也可以使用 "-->" 绘制一个虚线箭头表示异步消息。

另外，你也可以使用 "<-" 和 "<--". 这虽然不影响图形绘制，但可以增加可读性。 注意：仅适用于时序图，其它图形的规则是不同的。

``` python
{% plantuml %}
Alice -> Bob: Authentication Request
Bob --> Alice: Authentication Response

Alice -> Bob: Another authentication Request
Alice <-- Bob: another authentication Response
{% endplantuml %}
```

{% plantuml %}
Alice -> Bob: Authentication Request
Bob --> Alice: Authentication Response

Alice -> Bob: Another authentication Request
Alice <-- Bob: another authentication Response
{% endplantuml %}



## 注释

所有以单引号开头的行 '都是注释。

你也可以使用多行注释，多行注释以 /' 开头 '/ 结尾。

## 声明参与者

可以使用 participant 关键字来改变参与者的先后顺序。
你也可以使用其它关键字来声明参与者:

- actor
- boundary
- control
- entity
- database

``` python
{% plantuml %}
actor Foo1
boundary Foo2
control Foo3
entity Foo4
database Foo5
Foo1 -> Foo2 : To boundary
Foo1 -> Foo3 : To control
Foo1 -> Foo4 : To entity
Foo1 -> Foo5 : To database
{% endplantuml %}
```

{% plantuml %}
actor Foo1
boundary Foo2
control Foo3
entity Foo4
database Foo5
Foo1 -> Foo2 : To boundary
Foo1 -> Foo3 : To control
Foo1 -> Foo4 : To entity
Foo1 -> Foo5 : To database
{% endplantuml %}

使用 as 关键字重命名参与者，可以使用RGB值或者颜色名修改 actor 或参与者的背景色。

``` python
{% plantuml %}
actor Bob #red
' The only difference between actor
'and participant is the drawing
participant Alice
participant "I have a really\nlong name" as L #99FF99
/' You can also declare:
   participant L as "I have a really\nlong name"  #99FF99
  '/

Alice->Bob: Authentication Request
Bob->Alice: Authentication Response
Bob->L: Log transaction
{% endplantuml %}
```

{% plantuml %}
actor Bob #red
' The only difference between actor
'and participant is the drawing
participant Alice
participant "I have a really\nlong name" as L #99FF99
/' You can also declare:
   participant L as "I have a really\nlong name"  #99FF99
  '/

Alice->Bob: Authentication Request
Bob->Alice: Authentication Response
Bob->L: Log transaction
{% endplantuml %}

## 更改箭头的样式

修改箭头样式的方式有以下几种:
- 末尾加 x 表示一条丢失的消息
- 使用 \ 或 / 替代 < 或 > 来表示 have only the bottom or top part of the arrow
- 使用两个箭头标记 (如 >> 或 //) 表示空心箭头。
- 使用 -- 替代 - 以表示虚线箭头。
- 在箭头末尾加 “o”
- 双向箭头。

``` python
Bob ->x Alice
Bob -> Alice
Bob ->> Alice
Bob -\ Alice
Bob \\- Alice
Bob //-- Alice

Bob ->o Alice
Bob o\\-- Alice

Bob <-> Alice
Bob <->o Alice
```

{% plantuml %}
Bob ->x Alice
Bob -> Alice
Bob ->> Alice
Bob -\ Alice
Bob \\- Alice
Bob //-- Alice

Bob ->o Alice
Bob o\\-- Alice

Bob <-> Alice
Bob <->o Alice
{% endplantuml %}


## 修改箭头颜色

``` python
Bob -[#red]> Alice : hello
Alice -[#0000FF]->Bob : ok
```

{% plantuml %}
Bob -[#red]> Alice : hello
Alice -[#0000FF]->Bob : ok
{% endplantuml %}

## 消息编号

``` python
{% plantuml %}
autonumber
Bob -> Alice : Authentication Request
Bob <- Alice : Authentication Response
{% endplantuml %}
```

{% plantuml %}
autonumber
Bob -> Alice : Authentication Request
Bob <- Alice : Authentication Response
{% endplantuml %}

你可以在双引号内指定编号的格式。
格式是由 Java 的DecimalFormat类实现的 ('0' 表示数字, '#' 表示数字且默认为 0 )。

你还可以使用 HTML 标签来制定格式。

``` python
{% plantuml %}

title Simple communication example

autonumber "<b>[000]"
Bob -> Alice : Authentication Request
Bob <- Alice : Authentication Response

autonumber 15 "<b>(<u>##</u>)"
Bob -> Alice : Another authentication Request
Bob <- Alice : Another authentication Response

autonumber 40 10 "<font color=red><b>Message 0  "
Bob -> Alice : Yet another authentication Request
Bob <- Alice : Yet another authentication Response
{% endplantuml %}
```

{% plantuml %}
title Simple communication example
autonumber "<b>[000]"
Bob -> Alice : Authentication Request
Bob <- Alice : Authentication Response

autonumber 15 "<b>(<u>##</u>)"
Bob -> Alice : Another authentication Request
Bob <- Alice : Another authentication Response

autonumber 40 10 "<font color=red><b>Message 0  "
Bob -> Alice : Yet another authentication Request
Bob <- Alice : Yet another authentication Response
{% endplantuml %}

## 给图表(diagram)添加备注

关键字 legend 和 end legend 用于添加备注。
可选项 left， right 和 center 可用于设置标注的对齐方式。

``` python
{% plantuml %}
Alice -> Bob : Hello
legend right
  Short
  legend
endlegend
{% endplantuml %}
```
{% plantuml %}
Alice -> Bob : Hello
legend right
  Short
  legend
endlegend
{% endplantuml %}

## 给消息添加注释

我们可以通过在消息后面添加 note left 或者 note right 关键词来给消息添加注释。
你也可以通过使用 end note 来添加多行注释。


``` python
{% plantuml %}
Alice->Bob : hello
note left: this is a first note

Bob->Alice : ok
note right: this is another note

Bob->Bob : I am thinking
note left
    a note
    can also be defined
    on several lines
end note
{% endplantuml %}
```

{% plantuml %}
Alice->Bob : hello
note left: this is a first note

Bob->Alice : ok
note right: this is another note

Bob->Bob : I am thinking
note left
    a note
    can also be defined
    on several lines
end note
{% endplantuml %}

## 改变备注框的形状

你可以使用 hnote 和 rnote 这两个关键字来修改备注框的形状。

``` python
{% plantuml %}
caller -> server : conReq
hnote over caller : idle
caller <- server : conConf
rnote over server
 "r" as rectangle
 "h" as hexagon
endrnote
{% endplantuml %}
```

{% plantuml %}
caller -> server : conReq
hnote over caller : idle
caller <- server : conConf
rnote over server
 "r" as rectangle
 "h" as hexagon
endrnote
{% endplantuml %}

## 空间

你可以使用|||来增加空间。
还可以使用数字指定增加的像素的数量。

``` python
{% plantuml %}
Alice -> Bob: message 1
Bob --> Alice: ok
|||
Alice -> Bob: message 2
Bob --> Alice: ok
||45||
Alice -> Bob: message 3
Bob --> Alice: ok
{% endplantuml %}
```

{% plantuml %}
Alice -> Bob: message 1
Bob --> Alice: ok
|||
Alice -> Bob: message 2
Bob --> Alice: ok
||45||
Alice -> Bob: message 3
Bob --> Alice: ok
{% endplantuml %}

## 生命线的激活与撤销

关键字activate和deactivate用来表示参与者的生命活动。
一旦参与者被激活，它的生命线就会显示出来。

activate和deactivate适用于以上情形。

destroy表示一个参与者的生命线的终结。

``` python
{% plantuml %}
participant User

User -> A: DoWork
activate A

A -> B: << createRequest >>
activate B

B -> C: DoWork
activate C
C --> B: WorkDone
destroy C

B --> A: RequestCreated
deactivate B

A -> User: Done
deactivate A
{% endplantuml %}
```

{% plantuml %}
participant User

User -> A: DoWork
activate A

A -> B: << createRequest >>
activate B

B -> C: DoWork
activate C
C --> B: WorkDone
destroy C

B --> A: RequestCreated
deactivate B

A -> User: Done
deactivate A
{% endplantuml %}

## 进入和发出消息

如果只想关注部分图示，你可以使用进入和发出箭头。
使用方括号[和]表示图示的左、右两侧。


``` python
{% plantuml %}
[-> A: DoWork

activate A

A -> A: Internal call
activate A

A ->] : << createRequest >>

A<--] : RequestCreated
deactivate A
[<- A: Done
deactivate A
{% endplantuml %}
```

{% plantuml %}
[-> A: DoWork

activate A

A -> A: Internal call
activate A

A ->] : << createRequest >>

A<--] : RequestCreated
deactivate A
[<- A: Done
deactivate A
{% endplantuml %}

## 包裹参与者

可以使用box和end box画一个盒子将参与者包裹起来。
还可以在box关键字之后添加标题或者背景颜色。

``` python
{% plantuml %}
box "Internal Service" #LightBlue
    participant Bob
    participant Alice
end box
participant Other

Bob -> Alice : hello
Alice -> Other : hello
{% endplantuml %}
```

{% plantuml %}
box "Internal Service" #LightBlue
    participant Bob
    participant Alice
end box
participant Other

Bob -> Alice : hello
Alice -> Other : hello
{% endplantuml %}

## 外观参数(skinparam)

使用skinparam命令改变颜色和字体。
如下场景可以使用这一命令：

- 在图示定义中，
- 在一个包含文件中,
- 在由命令行或者ANT任务提供的配置文件中。

``` python
{% plantuml %}
skinparam sequenceArrowThickness 2
skinparam roundcorner 20
skinparam maxmessagesize 60
skinparam sequenceParticipant underline

actor User
participant "First Class" as A
participant "Second Class" as B
participant "Last Class" as C

User -> A: DoWork
activate A

A -> B: Create Request
activate B

B -> C: DoWork
activate C
C --> B: WorkDone
destroy C

B --> A: Request Created
deactivate B

A --> User: Done
deactivate A
{% endplantuml %}
```

{% plantuml %}
skinparam sequenceArrowThickness 2
skinparam roundcorner 20
skinparam maxmessagesize 60
skinparam sequenceParticipant underline

actor User
participant "First Class" as A
participant "Second Class" as B
participant "Last Class" as C

User -> A: DoWork
activate A

A -> B: Create Request
activate B

B -> C: DoWork
activate C
C --> B: WorkDone
destroy C

B --> A: Request Created
deactivate B

A --> User: Done
deactivate A
{% endplantuml %}

``` python
{% plantuml %}
skinparam backgroundColor #EEEBDC
skinparam handwritten true

skinparam sequence {
    ArrowColor DeepSkyBlue
    ActorBorderColor DeepSkyBlue
    LifeLineBorderColor blue
    LifeLineBackgroundColor #A9DCDF

    ParticipantBorderColor DeepSkyBlue
    ParticipantBackgroundColor DodgerBlue
    ParticipantFontName Impact
    ParticipantFontSize 17
    ParticipantFontColor #A9DCDF

    ActorBackgroundColor aqua
    ActorFontColor DeepSkyBlue
    ActorFontSize 17
    ActorFontName Aapex
}

actor User
participant "First Class" as A
participant "Second Class" as B
participant "Last Class" as C

User -> A: DoWork
activate A

A -> B: Create Request
activate B

B -> C: DoWork
activate C
C --> B: WorkDone
destroy C

B --> A: Request Created
deactivate B

A --> User: Done
deactivate A
{% endplantuml %}
```

{% plantuml %}
skinparam backgroundColor #EEEBDC
skinparam handwritten true

skinparam sequence {
    ArrowColor DeepSkyBlue
    ActorBorderColor DeepSkyBlue
    LifeLineBorderColor blue
    LifeLineBackgroundColor #A9DCDF

    ParticipantBorderColor DeepSkyBlue
    ParticipantBackgroundColor DodgerBlue
    ParticipantFontName Impact
    ParticipantFontSize 17
    ParticipantFontColor #A9DCDF

    ActorBackgroundColor aqua
    ActorFontColor DeepSkyBlue
    ActorFontSize 17
    ActorFontName Aapex
}

actor User
participant "First Class" as A
participant "Second Class" as B
participant "Last Class" as C

User -> A: DoWork
activate A

A -> B: Create Request
activate B

B -> C: DoWork
activate C
C --> B: WorkDone
destroy C

B --> A: Request Created
deactivate B

A --> User: Done
deactivate A
{% endplantuml %}