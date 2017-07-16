---
title: Hadoop源码学习(10)——Avro(2)
comments: true
toc: true
date: 2016-08-12 08:25:02
categories: BigData
tags : Hadoop
keywords: Hadoop,IO,Avro
---

>**本节内容：**Avro是一个独立于编程语言的数据序列化系统，该项目由Hadoop之父Doug Cutting创建，旨在解决Hadoop中Writable类型的不足：缺乏语言的可移植性。拥有一个可被多种语言(C/C++/C#/Java/PHP/Python/Ruby)处理的数据格式与绑定到单一语言的数据格式相比，前者更容易与公众共享数据。本节学习Avro数据文件、互操作性、模式解析和排列顺序。


<!-- more -->

## Avro数据文件

Avro的对象容器文件格式主要用于存储Avro对象序列，这与Hadoop顺序文件的设计非常类似，主要区别在于Avro数据文件主要是面向跨语言使用而设计的，所以可以用P圆通写入文件，用C语言来读取文件。

数据文件的头部分包含元数据，包括一个Avro模式和一个sync marker同步标志，紧接着是一系列包含序列化Avro对象的数据块(压缩可选)，数据块由sync marker来分隔，它对该文件而言，是唯一的，并且允许在文件中搜索到任意位置之后通过块边界快速地重新进行同步。因此，Avro数据文件是可切分的，适合MapReduce快速处理。

将Avro对象写入数据文件和写到数据流类似，我们使用一个DatumWriter，但没有用Encoder，而是用DatumWriter来创建一个DatumFileWriter实例，然后便可以新建一个数据文件，该文件一般使用.avro扩展名并向它附加新写入的对象：

``` java
File file = new File("data.avro");
DatumWriter<GenericRecord> writer =
new GenericDatumWriter<GenericRecord>(schema);
DataFileWriter<GenericRecord> dataFileWriter =
new DataFileWriter<GenericRecord>(writer);
dataFileWriter.create(schema, file);
dataFileWriter.append(datum);
dataFileWriter.close();
```

从数据文件中读取对象与前面例子中在内存数据流中读取数据非常类似，只有一个重要区别：我们不需要指定模式，因为可以从文件元数据中读取它。事实上，还可以对DataFileReader实例调用getSchema()方法来获取该模式，并验证该模式是否和原始写入对象的模式相同：

``` java
DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>();
DataFileReader<GenericRecord> dataFileReader =
new DataFileReader<GenericRecord>(file, reader);
assertThat("Schema is the same", schema, is(dataFileReader.getSchema()));
```
DataFileReader是一个人常规的Java迭代器，由此我们可以调用其hasNext()和next()方法来迭代其数据对象。下面的代码检查是否只有一条记录以及该记录是否有期望的字段值：

``` java
assertThat(dataFileReader.hasNext(),is(True));
GenericRecord result = dataFileReader.next();
assertThat(result.get("left").toString(),is("L"));
assertThat(result.get("right").toString(),is("R"));
assertThat(dataFileReader.hasNext(),is(false));
```
但是更合适的做法是使用重载并将返回对象实例作为输入参数(该例中为GenericRecord对象)，而非直接使用next()方法，因为这样可以减少对象分配和垃圾回收所产生的开销，特别是文件中包含有很多对象时。

``` java
GenericRecord record = null;
while (dataFileReader.hasNext()) {
record = dataFileReader.next(record);
// process record
}
```

如果对象重用不是那么重要，则可以使用如下更简短的形式：

``` java
for (GenericRecord record : dataFileReader) {
// process record
}
```

如果是一般的从hadoop文件系统中读取文件，可以使用Avro的FsInput对象来指定使用Hadoop Path 对象作为输入对象。事实上，DataFileReader对象提供了对Avro数据文件的随机访问(通过seek()和sync()方法)。但在大多数情况下，如果顺序访问数据流就足够了，则使用DataFileStream对象，DataFileStream对象可以从任意Java InputStream对象读取数据。

## 互操作性

为了说明互操作性，我们使用Python语言来写入数据，使用另一种语言来读取这个数据。

### 关于Python API

下面例子中的程序从标准输入中读取由逗号分隔的字符串，并将它们以StringPair记录的方式写入Avro数据文件。与写数据文件的Java代码类似，我们新建一个DatumWriter对象和一个DataFileWriter对象，注意，我们在代码中嵌入了Avro模式，尽管没有该模式，我们仍然可以从文件中正确读取数据。

Python以目录的形式表示Avro记录，从标准输入中读取的每一行都转换成Dict对象并被附加到DataFileWriter对象末尾。

``` python
# -*- coding:utf-8 -*-

import os
import string
import sys

from avro import schema
from avro import io
from avro import datafile

if __name__ == '__main__':
  if len(sys.argv) !=2:
    sys.exit("使用方法：%s <data_file>" % sys.argv[0])
  avro_file = sys.argv[1]
  writer = open(avro_file,'wb')
  datum_writer = io.DatumWriter()
  schema_object = schema.parse(' \
    { "type":"record", \
    "name":"Pair", \
    "doc":"StringPair", \
    "fields":[ \
      {"name":"left","type":"string"}, \
      {"name":"right","type":"string"} \
    ] \
  }')
  dfw = datafile.DataFileWriter(writer,datum_writer,schema_object)

  print("请输入以逗号分隔的数据，Ctrl-d结束")
  for line in sys.stdin.readlines():
    (left,right) = string.split(line.strip(),',')
    dfw.append({'left':left,'right':right});
  dfw.close()
```

为了运行该程序，我们首先安装Avro：`pip install avro`,指定文件名pairs.avro,输出结果会被写到这个文件。通过标准的输入发送成对记录，结束文件输入时键入Ctrl-D:

``` python
python avro/write_pairs.py pairs.avro
a,1
b,2
c,3
d,4
^D
```

### 关于C API

下面转向C API，写程序显示pairs.avro文件的内容，如下示例：

``` c
#include <avro.h>
#include <stdio.h>
#include <stdlib.h>

int main(int argc, char *argv[]){
  if (argc !=2) {
    fprintf(stderr,"使用：./dump_pairs <data_file>\n");
    exit(EXIT_FAILURE);
  }

  const char *avrofile = argv[1];
  avro_schema_error_t error;
  avro_file_reader_t filereader;
  avro_datum_t pair;
  avro_datum_t left;
  avro_datum_t right;
  int rval;
  char *p;

  avro_file_reader(avrofile,&filereader);
  while (1){
  ravl = avro_file_reader_read(filereader,NULL, &pair);
  if (rval) break;
  if ( avro_record_get(pair,"left",&left) == 0){
    avro_string_get(left,&p);
    fprintf(stdout,"%s,",p);
  }
  if (avro_record_get(pair,"right",&right) == 0){
    avro_string_get(right,&p);
    fprintf(stdout,"%s,",p);
  }
  }
  avro_file_reader_close(filereader);
  return 0;
}
```

### 使用Avro Tools

下面使用Avro Tools(使用Java写的)来显示pairs.avro文件的内容，假定所有的Avro Jar包已经被下载下来，存放在$AVRO_HOME。tojson命令将Avro 数据文件转换成JSON然后在终端中显示：

``` python
java -jar $AVRO_HOME/avro-tools-*.jar tojson pairs.avro
{"left":"a","right":"1"}
{"left":"b","right":"2"}
{"left":"c","right":"3"}
{"left":"d","right":"4"}
```

## 模式的解析

在选择的时候，读取数据的模式可以不同于我们写入数据的模式，这意味着模式演化。例如，为了便于说明，我们考虑新增一个description字段，形成一个新的模式：

``` json
{
"type": "record",
"name": "StringPair",
"doc": "A pair of strings with an added field.",
"fields": [
{"name": "left", "type": "string"},
{"name": "right", "type": "string"},
{"name": "description", "type": "string", "default": ""}
]
}
```

我们可以使用该模式读取前面序列化的数据，因为我们已经为description字段指定了一个默认值(空字符串)共Avro在读取没有字段的记录时使用。如果忽略default属性，在读取旧数据时会报错。

读模式不同于写模式，我们调用GenericDatumReader的构造函数，它取两个模式对象，即读取对象和写入对象，并按照以下顺序：

``` java
DatumReader<GenericRecord> reader =
new GenericDatumReader<GenericRecord>(schema, newSchema);
Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(),
null);
GenericRecord result = reader.read(null, decoder);
assertThat(result.get("left").toString(), is("L"));
assertThat(result.get("right").toString(), is("R"));
assertThat(result.get("description").toString(), is(""));
```

对于元数据中存储有写入模式的数据文件，我们只需要显示指定写入模式，具体做法是向写入模式传递null：

``` java
DatumReader<GenericRecord> reader =
new GenericDatumReader<GenericRecord>(null, newSchema);
```

另一个不同的读取模式的应用是去掉记录中的某些字段，该操作可以被称为投影(projection)。记录中有大量字段但如果只需要读取其中一部分，这种做法非常有用。例如，可以使用这一模式只读取StringPair对象的right字段：

``` json
{
"type": "record",
"name": "StringPair",
"doc": "The right field of a pair of strings.",
"fields": [
{"name": "right", "type": "string"}
]
}
```

模式解析规则可以直接解决模式由一个版本演化到另一个版本时可能产生的问题，Avro规范中对所有Avro类型均有详细说明。下表从类型读写(客户端和服务器端)的角度总结了记录演化规则。

|新模式|写入|读取|操作|
|:---- | :---- | :---- | :----|
|增加的字段|旧|新|通过默认值读取新字段，因为写入时没有该字段|
|增加的字段|新|旧|读取时不知道新写入的字段，所以忽略该字段(投影)|
|删除的字段|旧|新|读取时忽略已删除的字段(投影)|
|删除的字段|新|旧|写入时不写入已删除的字段。如果旧模式对该字段有默认值，那么读取时可以使用该默认值，否则报错。在这种情况下，最好同时更新读取模式或在更新写入模式之前更新读取模式|

Avro模式演化的另一个有用的技术是使用别名。别名允许你在读取Avro数据的模式时写入Avro数据的模式中使用不同的名称。例如，下面读取模式可以使用新的字段名称(即first和second)来读取StringPair数据，而非写入数据时使用的字段名称(即left和right)。

``` json
{
"type": "record",
"name": "StringPair",
"doc": "A pair of strings with aliased field names.",
"fields": [
{"name": "first", "type": "string", "aliases": ["left"]},
{"name": "second", "type": "string", "aliases": ["right"]}
]
}
```

注意，别名的主要作用是将写入模式转换(在读取的时候)为读取模式，但是别名对读取模式程序不是可见的。在上述程序中，读取程序无法再使用字段名left和right，因为他们已经被转换成first和second了。

## 排列顺序

Avro定义了对象的排列顺序。Avro大多数类型的排列顺序与用户的期望符合，例如，数值型按照数值的升序进行排列，其他的就没有这么巧妙了，例如枚举通过符号的定义而非符号字符串的值来排序。

除了record之外，所有的类型均按照Avro规范中预先定义的规则来排序，这些规则不能被用户改写。但对于记录，可以指定order属性来控制排列顺序。它有三个值：ascending(默认值)、descending(降序)或ignore(为了比较目的，可以忽略该字段)。

例如，通过将right字段设置为descending,下述模式(SortedStringPair.avsc)定义StringPair记录按降序顺序。为了排序的目的，忽略left字段，但依旧保留在投影中：

``` json
{
"type": "record",
"name": "StringPair",
"doc": "A pair of strings, sorted by right field descending.",
"fields": [
{"name": "left", "type": "string", "order": "ignore"},
{"name": "right", "type": "string", "order": "descending"}
]
}
```

按照读取模式中的文档顺序，记录中的字段两两进行比较。这样，通过指定一个恰当的读取模式，便可以对数据记录使用任意顺序。该模式(SwitchedStringPair.avsc)首先定义了一组right字段的顺序，然后是left字段的顺序：

``` json
{
"type": "record",
"name": "StringPair",
"doc": "A pair of strings, sorted by right then left.",
"fields": [
{"name": "right", "type": "string"},
{"name": "left", "type": "string"}
]
}
```

Avro实现了高效的二进制比较，也就是说Avro不需要将二进制对象反序列化成对象就可以实现比较，因为它可以直接对字节流进行操作。在使用StringPair模式的情况下(没有order属性)，Avro按以下方式实现了二进制比较：第一个字段(即left字段)，使用UTF-8编码，由此Avro可以根据字母表顺序进行比较，如果它们的顺序是不确定的，那么Avro可以在该处停止比较。否则，如果这两个字节顺序是相同的，那么它们比较第二个字段(right)，同样在字节尺度上使用字母表排序，因为该字段同样也使用UTF-8编码。

## 代码

- [AvroTest.java](/resource/codes/2016-08-12/AvroTest.java)
- [write_pairs.py](/resource/codes/2016-08-12/write_pairs.py)
- [StringPair.avsc](/resource/codes/2016-08-12/StringPair.avsc)
- [StringPairNew.avsc](/resource/codes/2016-08-12/StringPairNew.avsc)

运行：
``` java
javac -cp avro-1.7.7.jar:jackson-core-asl-1.9.13.jar:avro-maven-plugin-1.7.7.jar AvroTest.java
java -classpath jackson-mapper-asl-1.9.13.jar:avro-1.7.7.jar:jackson-core-asl-1.9.13.jar:$CLASSPATH AvroTest
从字节缓冲区中读取对象
读取结果：left:L
读取结果：right:R
从数据文件中读取对象
读取结果：left:L
读取结果：right:R
新模式读取对象
读取结果：left:L
读取结果：right:R
读取结果：description:

```