---
title: Python 基本数据结构
comments: true
toc: true
date: 2016-07-18 12:41:02
categories: Programming
tags : Python
keywords: Python, 数据结构
---

>**本节内容：**本篇博文学习并总结Python的基本数据结构常用用法，包括基本数据类型和四个比较重要的基本数据结构：列表，元组，字典，集合。
<!-- more -->

## 基本数据类型

 - 数字：数字数据类型用于存储数值。他们是不可改变的数据类型，这意味着改变数字数据类型会分配一个新的对象。
   Python支持四种不同的数字类型：
    - int（有符号整型）
    - long（长整型[也可以代表八进制和十六进制]）
    - float（浮点型）
    - complex（复数）
 - 布尔型：True和False
 - 字符串：字符串或串(String)是由数字、字母、下划线组成的一串字符。从左到右索引默认0开始的，最大范围是字符串长度少1，从右到左索引默认-1开始的，最大范围是字符串开头。



## list

- 基本方法

|方法|描述|
| ------ | ------ |
| list.append(x) |  把一个元素添加到列表的结尾，相当于 a[len(a):] = [x]。|
| list.extend(L) |  通过添加指定列表的所有元素来扩充列表，相当于 a[len(a):] = L。|
| list.insert(i, x) |   在指定位置插入一个元素。第一个参数是准备插入到其前面的那个元素的索引，例如 a.insert(0, x) 会插入到整个列表之前，而 a.insert(len(a), x) 相当于 a.append(x) 。|
| list.remove(x) |  删除列表中值为 x 的第一个元素。如果没有这样的元素，就会返回一个错误。|
| list.pop([i]) |   从列表的指定位置删除元素，并将其返回。如果没有指定索引，a.pop()返回最后一个元素。元素随即从列表中被删除。（方法中 i 两边的方括号表示这个参数是可选的，而不是要求你输入一对方括号，你会经常在 Python 库参考手册中遇到这样的标记。）|
| list.clear()  |   移除列表中的所有项，等于del a[:]。|
| list.index(x) |   返回列表中第一个值为 x 的元素的索引。如果没有匹配的元素就会返回一个错误。|
| list.count(x) |   返回 x 在列表中出现的次数。|
| list.sort() | 对列表中的元素进行排序。|
| list.reverse() |  倒排列表中的元素。|
| list.copy() | 返回列表的浅复制，等于a[:]。|

``` python
>>> a = [66.25, 333, 333, 1, 1234.5]
>>> print(a.count(333), a.count(66.25), a.count('x'))
2 1 0
>>> a.insert(2, -1)
>>> a.append(333)
>>> a
[66.25, 333, -1, 333, 1, 1234.5, 333]
>>> a.index(333)
1
>>> a.remove(333)
>>> a
[66.25, -1, 333, 1, 1234.5, 333]
>>> a.reverse()
>>> a
[333, 1234.5, 1, 333, -1, 66.25]
>>> a.sort()
>>> a
[-1, 1, 66.25, 333, 333, 1234.5]
```
- 列表推导式

列表推导式提供了从序列创建列表的简单途径。通常应用程序将一些操作应用于某个序列的每个元素，用其获得的结果作为生成新列表的元素，或者根据确定的判定条件创建子序列。
每个列表推导式都在 for 之后跟一个表达式，然后有零到多个 for 或 if 子句。返回结果是一个根据表达从其后的 for 和 if 上下文环境中生成出来的列表。如果希望表达式推导出一个元组，就必须使用括号。

``` python
>>> squares = [x**2 for x in range(10)]
>>> squares
[0, 1, 4, 9, 16, 25, 36, 49, 64, 81]
>>> list(map(lambda x: x**2, range(10)))
[0, 1, 4, 9, 16, 25, 36, 49, 64, 81]
>>> [(x, y) for x in [1,2,3] for y in [3,1,4] if x != y]
[(1, 3), (1, 4), (2, 3), (2, 1), (2, 4), (3, 1), (3, 4)]

```

## tuple

元组由若干逗号分隔的值组成，里面的值是不可改变的。

``` python
>>> t = 12345, 54321, 'hello!'
>>> t[0]
12345
>>> t
(12345, 54321, 'hello!')
>>> # Tuples may be nested:
... u = t, (1, 2, 3, 4, 5)
>>> u
((12345, 54321, 'hello!'), (1, 2, 3, 4, 5))
>>> # Tuples are immutable:
... t[0] = 88888
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
TypeError: 'tuple' object does not support item assignment
>>> # but they can contain mutable objects:
... v = ([1, 2, 3], [3, 2, 1])
>>> v
([1, 2, 3], [3, 2, 1])
```

## dict

另一个非常有用的 Python 内建数据类型是字典。列表是以连续的整数为索引，与此不同的是，字典以关键字为索引，关键字可以是任意不可变类型，通常用字符串或数值。
理解字典的最佳方式是把它看做无序的键=>值对集合。在同一个字典之内，关键字必须是互不相同。一对大括号创建一个空的字典：{}。

字典的主要操作是依据键来存储和析取值。也可以用 del来删除键：值对（key:value）。如果你用一个已经存在的关键字存储值，以前为该关键字分配的值就会被遗忘。试图从一个不存在的键中取值会导致错误。

对一个字典执行 list(d.keys()) 将返回一个字典中所有关键字组成的无序列表（如果你想要排序，只需使用 sorted(d.keys()) ）。使用 in 关键字（指Python语法）可以检查字典中是否存在某个关键字（指字典）。

``` python
>>> tel = {'jack': 4098, 'sape': 4139}
>>> tel['guido'] = 4127
>>> tel
{'sape': 4139, 'guido': 4127, 'jack': 4098}
>>> tel['jack']
4098
>>> del tel['sape']
>>> tel['irv'] = 4127
>>> tel
{'guido': 4127, 'irv': 4127, 'jack': 4098}
>>> list(tel.keys())
['irv', 'guido', 'jack']
>>> sorted(tel.keys())
['guido', 'irv', 'jack']
>>> 'guido' in tel
True
>>> 'jack' not in tel
False
```

dict() 构造函数可以直接从 key-value 对中创建字典:
``` python
>>> dict([('sape', 4139), ('guido', 4127), ('jack', 4098)])
{'sape': 4139, 'jack': 4098, 'guido': 4127}
```

此外，字典推导式可以从任意的键值表达式中创建字典:
``` python
>>> {x: x**2 for x in (2, 4, 6)}
{2: 4, 4: 16, 6: 36}
```

如果关键字都是简单的字符串，有时通过关键字参数指定 key-value 对更为方便:
``` python
>>> dict(sape=4139, guido=4127, jack=4098)
{'sape': 4139, 'jack': 4098, 'guido': 4127}
```


## set

Python 还包含了一个数据类型 —— set （集合）。集合是一个无序不重复元素的集。基本功能包括关系测试和消除重复元素。集合对象还支持 union（联合），intersection（交），difference（差）和 sysmmetric difference（对称差集）等数学运算。

大括号或 set() 函数可以用来创建集合。注意：想要创建空集合，你必须使用 set() 而不是 {},后者用于创建空字典。

``` python
>>> basket = {'apple', 'orange', 'apple', 'pear', 'orange', 'banana'}
>>> print(basket)                      # show that duplicates have been removed
{'orange', 'banana', 'pear', 'apple'}
>>> 'orange' in basket                 # fast membership testing
True
>>> 'crabgrass' in basket
False

>>> # Demonstrate set operations on unique letters from two words
...
>>> a = set('abracadabra')
>>> b = set('alacazam')
>>> a                                  # unique letters in a
{'a', 'r', 'b', 'c', 'd'}
>>> a - b                              # letters in a but not in b
{'r', 'd', 'b'}
>>> a | b                              # letters in either a or b
{'a', 'c', 'r', 'd', 'b', 'm', 'z', 'l'}
>>> a & b                              # letters in both a and b
{'a', 'c'}
>>> a ^ b                              # letters in a or b but not both
{'r', 'd', 'b', 'm', 'z', 'l'}
>>> a = {x for x in 'abracadabra' if x not in 'abc'}
>>> a
{'r', 'd'}

```

## 遍历技巧

- 在字典中遍历时，关键字和对应的值可以使用 items() 方法同时解读出来：

``` python
>>> knights = {'gallahad': 'the pure', 'robin': 'the brave'}
>>> for k, v in knights.items():
...     print(k, v)
...
gallahad the pure
robin the brave
```

- 在序列中遍历时，索引位置和对应值可以使用 enumerate() 函数同时得到：

``` python
>>> for i, v in enumerate(['tic', 'tac', 'toe']):
...     print(i, v)
...
0 tic
1 tac
2 toe
```
- 同时循环两个或更多的序列，可以使用 zip() 整体打包:

``` python
>>> questions = ['name', 'quest', 'favorite color']
>>> answers = ['lancelot', 'the holy grail', 'blue']
>>> for q, a in zip(questions, answers):
...     print('What is your {0}?  It is {1}.'.format(q, a))
...
What is your name?  It is lancelot.
What is your quest?  It is the holy grail.
What is your favorite color?  It is blue.
```

- 需要逆向循环序列的话，先正向定位序列，然后调用 reversed() 函数:

``` python
>>> for i in reversed(range(1, 10, 2)):
...     print(i)
...
9
7
5
3
1
```

- 要按排序后的顺序循环序列的话，使用 sorted() 函数，它不改动原序列，而是生成一个新的已排序的序列:

``` python
>>> basket = ['apple', 'orange', 'apple', 'pear', 'orange', 'banana']
>>> for f in sorted(set(basket)):
...     print(f)
...
apple
banana
orange
pear
```

- 若要在循环内部修改正在遍历的序列（例如复制某些元素），建议您首先制作副本。在序列上循环不会隐式地创建副本。切片表示法使这尤其方便:

``` python
>>> words = ['cat', 'window', 'defenestrate']
>>> for w in words[:]:  # Loop over a slice copy of the entire list.
...     if len(w) > 6:
...         words.insert(0, w)
...
>>> words
['defenestrate', 'cat', 'window', 'defenestrate']
```

## Python数据类型转换

有时候，我们需要对数据内置的类型进行转换，数据类型的转换，你只需要将数据类型作为函数名即可。
以下几个内置的函数可以执行数据类型之间的转换。这些函数返回一个新的对象，表示转换的值。

|方法|描述|
| ------ | ------ |
|int(x [,base])|将x转换为一个整数。|
|long(x [,base] )|将x转换为一个长整数。|
|float(x)|将x转换到一个浮点数。|
|complex(real [,imag])|创建一个复数。|
|str(x)|将对象 x 转换为字符串。|
|repr(x)|将对象 x 转换为表达式字符串。|
|eval(str)|用来计算在字符串中的有效Python表达式,并返回一个对象。|
|tuple(s)|将序列 s 转换为一个元组。|
|list(s)|将序列 s 转换为一个列表。|
|set(s)|转换为可变集合。|
|dict(d)|创建一个字典。d 必须是一个序列 (key,value)元组。|
|frozenset(s)|转换为不可变集合。|
|chr(x)|将一个整数转换为一个字符。|
|unichr(x)|将一个整数转换为Unicode字符。|
|ord(x)|将一个字符转换为它的整数值。|
|hex(x)|将一个整数转换为一个十六进制字符串。|
|oct(x)|将一个整数转换为一个八进制字符串。|
