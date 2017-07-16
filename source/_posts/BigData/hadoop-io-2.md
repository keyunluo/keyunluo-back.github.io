---
title: Hadoop源码学习(6)——Writable(1)
comments: true
toc: true
date: 2016-08-09 09:25:02
categories: BigData
tags : Hadoop
keywords: Hadoop,IO,Writable
---

>**本节内容：**Writable是Hadoop的核心，Hadoop通过它定义了Hadoop中基本的数据类型及其操作，它是实现序列化的基础，本节学习其中的Writable接口和Java基础类型包装。

<!-- more -->

## Writable接口

Writable接口作为所有可序列化对象必须实现的接口，定义了如下接口：

``` java
public interface Writable {
  /**
   * 输出序列化对象到流中.
   *
   * @param out DataOuput 流，序列化的结果保存在流中.
   * @throws IOException
   */
  void write(DataOutput out) throws IOException;

  /**
   * 从流中读取反序列化对象
   * 为了效率，请尽可能复用现有对象
   *
   * @param in DataInput流，从该流中读取数据.
   * @throws IOException
   */
  void readFields(DataInput in) throws IOException;
}

```

Writable.write()方法用于将对象状态写入二进制的DataOutput中，反序列化的过程由readFields()从DataInput流中读取状态完成。

## WritableComparable和RawComparator

### 相关类

- WritableComparable : 它提供了类型比较的能力
- WritableComparator : WritableComparator是WritableComparable的比较器，它是RawComparator针对WritableComparator类的一个通用实现。首先，提供了一个RawComparator的compare()默认实现，该实现从数据流中反序列化要比较的对象，然后调用对象的compare()方法进行比较；其次，它充当了RawComparator实例的一个工厂方法，例如，可以通过如下代码获得IntWritable的RawComparator：`RawComparator<IntWritable> comparator = WritableComparator.get(IntWritable.class)`
- RawComparator ： 允许执行者比较流中读取的未被序列化为对象的记录，从而省去了创建对象的开销

### 类图

![Comparator](/resource/blog/2016-08/Comparator.png)


Hadoop将很多Writable类归入org.apache.hadoop.io包中，在这些类中比较重要的有Java基本类型类、Text、Writable集合、ObjectWritable等。

## Java基本类型的Writable封装

### Java基本类型封装对照

| Java基本类型 | Writable | 序列化后长度 |
| :---- | :---- | :---- |
| 布尔型(boolean) | BooleanWritable | 1 |
| 字节型(byte)| ByteWritable | 1 |
| 整型(int)|IntWritable/VIntWritable  | 4/1~5|
| 浮点型(float)| FloatWritable | 4 |
| 长整型long()| LongWritable/VLongWritable | 8/1~9 |
| 双精度浮点型(double)| DoubleWritable | 8 |

对整型(int、long)进行编码的时候,有固定长度格式(IntWritable、LongWritable)和可变长度格式(VIntWritable、VLongWritable)两种选择。固定长度格式的整型序列化后的数据是定长的，而可变长度格式对于数值比较小的整型往往比较节省空间。同时，由于VIntWritable和VLongWritable的编码规则一致，所以VIntWritable的输出可以用VLongWritable读入。

### Writable的基本类封装

下面以VIntWritable为例，了解其封装。

``` java
public class VIntWritable implements WritableComparable<VIntWritable> {
  private int value;

  public VIntWritable() {}

  public VIntWritable(int value) { set(value); }

  /** 设置VIntWritable的值 */
  public void set(int value) { this.value = value; }

  /** 获取VIntWritable的值*/
  public int get() { return value; }

  // 实现writable里面的接口readFields和write方法
  @Override
  public void readFields(DataInput in) throws IOException {
    value = WritableUtils.readVInt(in);
  }

  @Override
  public void write(DataOutput out) throws IOException {
    WritableUtils.writeVInt(out, value);
  }

  /** 判断VIntWritable的值是否相等*/
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof VIntWritable))
      return false;
    VIntWritable other = (VIntWritable)o;
    return this.value == other.value;
  }

  @Override
  public int hashCode() {
    return value;
  }

  /** 比较两个VIntWritables. */
  @Override
  public int compareTo(VIntWritable o) {
    int thisValue = this.value;
    int thatValue = o.value;
    return (thisValue < thatValue ? -1 : (thisValue == thatValue ? 0 : 1));
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }

}
```

VIntWritable通过调用VWritable工具类中提供的readVInt()和writeVInt()来读写数据，方法readVInt()和writeVInt()只是简单调用了readVLong()和writeVLong()方法。

``` java
public static void writeVLong(DataOutput stream, long i) throws IOException {
    处于[-112,127]的整数，直接写入
    if (i >= -112 && i <= 127) {
      stream.writeByte((byte)i);
      return;
    }

    // 计算情况2的第一个字节
    int len = -112;
    if (i < 0) {
      i ^= -1L; // take one's complement'
      len = -120;
    }

    long tmp = i;
    while (tmp != 0) {
      tmp = tmp >> 8;
      len--;
    }

    stream.writeByte((byte)len);

    len = (len < -120) ? -(len + 120) : -(len + 112);

    // 输出后续字节
    for (int idx = len; idx != 0; idx--) {
      int shiftbits = (idx - 1) * 8;
      long mask = 0xFFL << shiftbits;
      stream.writeByte((byte)((i & mask) >> shiftbits));
    }
  }
  ```

writeVLong()方法实现了对整型数值的变长编码，它的编码规则如下：

如果输入的整数大于会等于-112并且小于或等于127，那么编码需要1字节，否则，序列化结果的第一个字节，保存了输入整数的符号和后续编码的字节数。符号和后续字节数依据下面的编码规则：

- 如果是正数，则编码值范围落在-113和-120之间(闭区间)，后续字节数可以通过-(v+112)计算
- 如果是负数，则编码值范围落在-121和-128之间(闭区间)，后续字节数可以通过-(v+120)计算

后续编码将高位在前，写入输入的整数(除去前面的全0字节)


## Text类型

Text类型是针对UTF-8序列的Writable类，一般可认为它等价于java.lang.String的Writable。Text替代了UTF8类,但这并不是一个很好的替代，一者因为不支持对字节数超过32767的字符串进行编码，二者因为它使用的是Java的UTF-8修订版。

Text类使用整型(通过边长编码的方式)来存储字符串编码中所需要的字节数，因此该最大值为2GB。另外Text使用标准的UTF-8编码，这使得能够更加简便的与其他理解UTF-8编码的工具进行交互操作。

Text类型与String类型的主要差别如下：

- String的长度定义为String包含的字符个数；Text的长度定义为UTF-8编码的字节数
- String类的indexOf()方法返回的是char类型字符的索引，比如字符串"1234",字符3的位置是2；而Text的find()方法返回的是字节偏移量。
- String的CharAt()方法返回的是指定位置的char字符，而Text的charAT()方法需要指定偏移量

另外，在Text类中定义了一个方法toString(),它用于将Text类型转换成String类型。

## BytesWritable

它是一个二进制数据数组的封装，其序列化格式为一个指定锁包含数据字节的整数域(4字节)，后跟数据内容本身。例如，长度为2的字节数组包含数值3和5，序列化形成为一个4字节的整数(00000002)和该数组中的两个字节(03和05)：

``` java
BytesWritable b = new BytesWritable( new byte[] {3,5});
byte[] bytes = serialize(b);
assertThat(StringUtils.byteToHexString(bytes),is("000000020305"));
```
BytesWritable是可变的，其值可以通过set()方法进行修改。和Text相似，BytesWritable类的getBytes()方法返回的字节数组长度——容量可能无法体现BytesWritable所存储数据的实际大小。可以通过getLength()方法来确定BytesWritable的大小。

## NullWritable

这是一个占位符，它的序列化长度为零，没有数值从流中读出或者是写入流中。这个类型不可以与其他类型比较，在MapReduce中，如果不需要使用键或值的序列化地址，就可以将键或值声明为NullWritable，结果是高效的存储常量空值。如果希望存储一系列数值，与键/值对相对，NullWritable也可以用作在SequenceFile中的键，它是一个不可变的单实例类型，通过调用NullWritable.get()方法可以获取这个实例。

## ObjectWritable和GenericWritable

ObjectWritable是对Java基本类型(String,enum,Writable,null或这些类型组成的数组)的一个通用封装，它在Hadoop RPC中用于对方法的参数和返回类型进行封装和解封装。

当一个字段中包含多个类型时，ObjectWritable非常有用：例如，如果SequenceFile中的值包含多个类型，就可以将值类型声明为ObjectWritable，并将每个类型封装在一个ObjectWritable中。作为一个通用的机制，每次序列化都写封装类型的名称将非常浪费空间。如果封装的类型数量比较少并且能够提前知道，那么可以通过使用静态类型的数组，并使用对序列化后的类型的引用加入位置索引来提高性能。GenericWritable类采取的就是这种方式，所以你得在继承的子类中指定支持什么类型。

## Writable集合类

org.apache.hadoop.io软件包中一共有6隔Writable集合类：ArrayWritable、ArrayPrimitiveWritable、TwoDArrayWritable、MapWritable、SortedMapWritable和EnumMapWritable。

ArrayWritable和TwoDArrayWritable是对Writable的数组和二维数组的实现。ArrayWritable或TwoDArrayWritable中的所有元素必须是同一类的实例(在构造函数中指定)：`ArrayWritable writable = new ArrayWritable(Text.class)`
如果Writable根据类型来定义，例如SequenceFile的键或值，或更多时候作为MapReduce的输入，则需要继承ArrayWritable(或相应的TwoDArrayWritable类)并设置静态类型：

``` java
public class TextArrayWritable extends ArrayWritable{
    public TextArrayWritable(){
    super(Text.class);
  }
}
```

ArrayWritable和TwoDArrayWritable都有get()、set()和toArray()方法。toArray()方法用于新建该数组(或二维数组)的一个“浅拷贝”。

ArrayPrimitiveWritable是对Java基本数组类型的一个封装。调用set()方法时，可以识别相应组件类型，因此无需通过继承该类来设置类型。

MapWritable和SortedMapWritable分别实现了java.util.Map<Writable,Writable>和java.util.SortedMap<WritableComparator,Writable>。每个键值对字段使用的类型是相应字段序列化形式的一部分。类型存储为单个字节(充当类型数组的索引)。在org.apache.hadoop.io包中，数组经常与标准类型结合使用，而定制的Writable类型也通常结合使用，但对于非标准类型，则需要在包头指明所使用的数组类型。根据实现，MapWritable类和SortedMapWritable类通过正byte值来指示定制的类型，所以在MapWritable和SortedMapWritable实例中最多可使用127隔不同的非标准Writable类。下面显示使用了不同键和值类型的MapWritable实例：

``` java
MapWritable src = new MapWritable();
src.put(new IntWritable(1),new Text("cat"));
src.put(new VIntWritable(2),new LongWritable(163));

MapWritable dest = new  MapWritable();
WritableUtils.cloneInto(dest , src);
assertThat((Text) dest.get(new IntWritable(1)),is(new Text("cat")));
assertThat((LongWritable) dest.get(new VIntWritable(2)),is(new LongWritable(163)));
```

显然，可以通过Writable集合类来实现集合和列表。可以使用MapWritable类型(或针对排序集合，使用SortedMapWritable)来枚举集合中的元素，用NullWritable类型枚举值。对集合的枚举类型可采用EnumSetWritable。对于单类型的Writable列表，使用ArrayWritable就足够了，但如果需要把不同的Writable类型存储在单个列表中，可以用GenericWritable将元素封装在一个ArrayWritable中。另一个可选方案是借鉴MapWritable的思路写一个通用的ListWritable。