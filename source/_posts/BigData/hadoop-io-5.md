---
title: Hadoop源码学习(9)——Avro(1)
comments: true
toc: true
date: 2016-08-11 14:25:02
categories: BigData
tags : Hadoop
keywords: Hadoop,IO,Avro
---

>**本节内容：**Avro是一个独立于编程语言的数据序列化系统，该项目由Hadoop之父Doug Cutting创建，旨在解决Hadoop中Writable类型的不足：缺乏语言的可移植性。拥有一个可被多种语言(C/C++/C#/Java/PHP/Python/Ruby)处理的数据格式与绑定到单一语言的数据格式相比，前者更容易与公众共享数据。本节学习Avro数据序列化系统。


<!-- more -->

## Avro特点

- Avro数据使用语言无关的模式定义的，代码生成是可选的
- Avro模式通常用JSON来写，数据通常采用二进制格式来编码
- Avro规范精确定义所有实现都必须支持的二进制格式
- Avro有丰富的模式解析能力，支持模式演化，即新模式能够读取旧模式定义的数据，旧模式也能读取新模式的数据同时忽略新模式中新加入的字段
- Avro为一系列对象指定了一个对象容器格式——类似于Hadoop的顺序文件，Avro数据文件包含元数据项，使此文件可以自我声明，并且Avro数据支持压缩和可切分
- Avro还可以用于RPC

## Avro数据类型和模式

Avro定义了少量数据基本类型，它们可用于以写模式的方式来构建应用特定的数据结构，考虑到互操作性，其实现必须支持所有的Avro类型。

Avro的基本类型如下：

``` json
{ "type":"null"}
```

|类型|描述|模式示例|
|:---- | :---- | :----|
|null| 空值 |"null"|
|boolean|布尔类型 |"boolean"|
|int |32位带符号整数 |"int"|
|long| 64位带符号整数| "long"|
|float |单精度(32-bit) IEEE 754 浮点数 |"float"|
|double| 双精度 (64-bit) IEEE 754 浮点数 |"double"|
|bytes |8位无符号字节序列| "bytes"|
|string |Unicode字符序列|"string"|

Avro复杂类型如下：

|类型|描述|模式示例|
|:---- | :---- | :----|
|array | 一个排过序的对象集合。特定数组中的所有对象必须模式相同|{ "type": "array","items": "long"}|
|map |未排过序的键值对。键必须是字符串，值可以是任何类型，但一个特定map中所有值必须模式相同|{"type": "map","values":"string"}|
|record|一个任意类型的命名字段集合|{"type": "record","name":"WeatherRecord","doc": "A weather reading.","fields": [{"name":"year", "type":"int"},{"name":"temperature","type": "int"},{"name":"stationId","type": "string"}]}|
|enum|一个命名的值集合|{"type": "enum","name":"Cutlery","doc": "An eating utensil.","symbols":["KNIFE", "FORK","SPOON"]}|
|fixed|一组固定数量的8位无符号字节|{"type": "fixed","name":"Md5Hash","size": 16}|
|union|模式的并集，并集可用JSON数组表示，其中每个元素为一个模式。并集表示的数据必须与其中一个模式相匹配|["null","string",{"type": "map","values": "string"}]|

一种语言可能有多种表示或映射。所有的语言都支持动态映射，即使运行前并不知道具体模式，也可以使用动态映射。对此，Java称为通用映射。

另外，Java和C++实现可以自动生成代码来表示符合某种Avro模式的数据。代码生成(code generation，Java中称为“特殊映射”)能优化数据处理，如果读写数据之前就有一个模式备份。那么，为用户代码生成的类和通用代码生成的类相比，前者提供的API更贴近问题域。

Java拥有第三类映射，即自反映射(reflect mapping,将Avro类型映射到已有的Java类型)，它的速度比通用映射和特殊映射都慢，所以不推荐在新应用中使用。

特殊映射与通用映射仅在record，enum，fixed是那个类型方面有区别，其他所有类型均有自动生成的类(类名由name属性和可选的namespace属性决定)。

|Avro类型|通用Java映射|特殊Java映射|Java自反映射|
| :---- | :---- | :---- | :---- |
|null |null type| | |
|boolean | boolean | | |
|int |int | | byte, short, int|
|long | long | | |
|float | float | | |
|double | double | | |
|bytes |java.nio.ByteBuffer | | 字节数组|
|string | org.apache.avro.util.Utf8(高效，优先使用) or java.lang.String | | java.lang.String|
|array | org.apache.avro.generic.GenericArray | |数组或 java.util.Collection|
|map | java.util.Map | | |
|record | org.apache.avro.generic.GenericRecord | 生成实现org.apache.avro.specific.SpecificRecord 类的实现|具有零参数构造函数的任意用户类，继承了所有不传递的实例字段 |
|enum | java.lang.String | 生成Java enum类型 | 任意Java enum类型 |
|fixed | org.apache.avro. generic.GenericFixed | 生成实现org.apache.avro.specific.SpecificFixed |org.apache.avro.generic.genericFixed|
|union |java.lang.Object | | |

## 内存中的序列化和反序列化

Avro为序列化和反序列化提供了API，如果想把Avro集成到现有系统(比如已定义帧格式的消息系统)，这些API函数就很有用。其他情况，请考虑使用Avro的数据文件格式。

### 通用API

让我们写一个Java程序从数据流读写Avro数据。首先以一个简单的Avro模式为例，它用于表示以记录形式出现的一对字符串：

``` json
{
"type": "record",
"name": "StringPair",
"doc": "A pair of strings.",
"fields": [
{"name": "left", "type": "string"},
{"name": "right", "type": "string"}
]
}
```

如果这一模式存储在类路径下一个名为StringPair.avsc的文件中吗，我们可以通过下面两行代码进行加载：

``` java
Schema.Parser parser = new Schema.Parser();
Schema schema = parser.parse(getClass().getResourceAsStream("StringPair.avsc"));
```

我们可以使用以下通用API新建一个Avro记录的实例：
``` java
GenericRecord datum = new GenericData.Record(schema);
datum.put("left",new Utf8("L"));
datum.put("right",new Utf8("R"));
```

接下来，将记录序列化到输出流中：
``` java
ByteArrayOutputStream out = new ByteArrayOutputStream();
DatumWriter<GenericRecord> writer =
new GenericDatumWriter<GenericRecord>(schema);
Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
writer.write(datum, encoder);
encoder.flush();
out.close();
```

这里有两个重要的对象：DatumWriter和Encoder。DatumWriter对象将数据对象翻译成Encoder对象可以理解的模型，然后由后者写入到输出流。这里，我们使用GenericDatumWriter对象，它将GenericRecord字段的值传递给Encoder对象。我们将null传给encoder工厂，因为我们这里没有重用先前构建的encoder。

我们可以使用反向的处理过程从字节缓冲区中读回对象：
``` java
DatumReader<GenericRecord> reader =
new GenericDatumReader<GenericRecord>(schema);
Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(),
null);
GenericRecord result = reader.read(null, decoder);
assertThat(result.get("left").toString(), is("L"));
assertThat(result.get("right").toString(), is("R"));
```

我们需要传递空值(null)并调用binaryDecoder()和read()方法，因为这里没有重用对象(分别是decoder或记录)。

由result.get("left")和result.get("right")的输出对象是Utf8类型，因此我们需要通过调用toString()方法将他们转型为Java String类型。

### 特定API

通过使用Avro的Maven插件编译模式，我们可以根据模式文件生成StringPair类，以下是与Maven Project Object Model(POM)相关的部分：

``` xml
<project>
...
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.avro</groupId>
        <artifactId>avro-maven-plugin</artifactId>
        <version>${avro.version}</version>
        <executions>
          <execution>
            <id>schemas</id>
            <phase>generate-sources</phase>
            <goals>
              <goal>schema</goal>
            </goals>
            <configuration>
              <includes>
                <include>StringPair.avsc</include>
              </includes>
              <stringType>String</stringType>
              <sourceDirectory>src/main/resources</sourceDirectory>
              <outputDirectory>${project.build.directory}/generated-sources/java
              </outputDirectory>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
...
</project>
```

在序列化和反序列化的代码中，我们构建一个StringPair实例来替代GenericRecord对象(使用SpecificDatumWriter类将该对象写入数据流),并使用SpecificDatumRecorder类读回数据：

``` java
StringPair datum = new StringPair();
datum.setLeft("L");
datum.setRight("R");
ByteArrayOutputStream out = new ByteArrayOutputStream();
DatumWriter<StringPair> writer =
new SpecificDatumWriter<StringPair>(StringPair.class);
Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
writer.write(datum, encoder);
encoder.flush();
out.close();
DatumReader<StringPair> reader =
new SpecificDatumReader<StringPair>(StringPair.class);
Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(),
null);
StringPair result = reader.read(null, decoder);
assertThat(result.getLeft(), is("L"));
assertThat(result.getRight(), is("R"));
```

从Avro1.6.0版本开始，生成的Java代码中包含有get()和set()方法，因此不用再写datum.setLeft("L")和result.getLeft()方法。