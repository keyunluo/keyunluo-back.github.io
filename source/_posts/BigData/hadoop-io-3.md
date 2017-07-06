---
title: Hadoop源码学习(7)——Writable(2)
comments: true
toc: true
date: 2016-08-10 10:25:02
categories: BigData
tags : Hadoop
keywords: Hadoop,IO,Writable
---

>**本节内容：**Writable是Hadoop的核心，Hadoop通过它定义了Hadoop中基本的数据类型及其操作，它是实现序列化的基础，本节学习其中的几个重要的工具类的实现。


<!-- more -->

## WritableUtils

|方法|参数|返回值|备注|
| :---- | :---- | :---- | :---- |
|readCompressedByteArray| DataInput in | byte [] |读取压缩的GZIP字节数组|
|skipCompressedByteArray| DataInput in | void |跳过压缩的字节数组|
|writeCompressedByteArray| DataOutput out , byte[] bytes | int|写入压缩的字节数组|
|readCompressedString| DataInput in| String |读取压缩的字符串|
|writeCompressedString|DataOutput out , String s| int |写入压缩的字符串|
|writeString|DataOutput out ,String s|void |写字符串 |
|readString|DataInput in| String |读字符串|
|writeStringArray|DataOutput out ,String[] s|void |写字符串数组 |
|writeCompressedStringArray|DataOutput out ,String[] s|void |写压缩字符串数组 |
|readStringArray|DataInput in| String |读字符串数组|
|readCompressedStringArray|DataInput in| String []|读压缩字符串数组|
|displayByteArray|byte[] record|void|显示ByteArray|
|clone| T orig,Configuration conf| < T extends Writable\> T|使用序列化拷贝一个writable object到buffer中|
|cloneInto|Writable dst , Writable src | void|已经弃用的方法，功能同上面拷贝一样|
|writeVInt|DataOutput stream, int i|void |写VInt型数据|
|writeVLong|DataOutput stream, long i|void |写VLong型数据|
|readLong|DataInput stream|void |读Long型数据|
|readVInt|DataInput stream|int|读VInt型数据|
|readVIntInRange|DataInput stream,int lower,int upper|int|读VInt型数据,验证其处于[lower,upper]之间|
|isNegativeVInt|byte value|boolean|判断byte第一位是否为负|
|decodeVIntSize|byte value|int|通过第一位返回bytes大小|
|getVIntSize| long i| int | 获得变长存储的整数编码长度|
|readEnum|DataInput in,Class<T> enumType| < T extends Enum < T >> T |从DataInput中读取枚举值|
|writeEnum|DataOutput out,Enum < ?> enumVal|void|把字符串类型的枚举值写入DataOutput|
|skipFully|DataInput in, int len|void |在输入流中跳过len长度的字节|
|toByteArray|Wriatble... writables|byte[]|把Writable转换成byte array|
|readStringSafely|DataInput in,int maxLength|String|读取字符串，但是检查正确性|

## Text

- encode：将String使用UTF-8编码成字节bytes,如果replace参数为true，那么难看的输入将会被替换的字母U+FFFD代替，否则抛出一个MalformedInputException

``` java
public static ByteBuffer encode(String string, boolean replace)
    throws CharacterCodingException {
    CharsetEncoder encoder = ENCODER_FACTORY.get();
    if (replace) {
      encoder.onMalformedInput(CodingErrorAction.REPLACE);
      encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
    }
    ByteBuffer bytes =
      encoder.encode(CharBuffer.wrap(string.toCharArray()));
    if (replace) {
      encoder.onMalformedInput(CodingErrorAction.REPORT);
      encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
    }
    return bytes;
  }
```

- decode：将byte array使用UTF-8编码成字符串String,如果replace参数为true，那么难看的输入将会被替换的字母U+FFFD代替，否则抛出一个MalformedInputException

``` java
    private static String decode(ByteBuffer utf8, boolean replace)
    throws CharacterCodingException {
    CharsetDecoder decoder = DECODER_FACTORY.get();
    if (replace) {
      decoder.onMalformedInput(
          java.nio.charset.CodingErrorAction.REPLACE);
      decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
    }
    String str = decoder.decode(utf8).toString();
    // set decoder back to its default value: REPORT
    if (replace) {
      decoder.onMalformedInput(CodingErrorAction.REPORT);
      decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
    }
    return str;
  }
```

- readString：从输入流中读取UTF8 编码的String，附加最大尺寸

``` java
  public static String readString(DataInput in) throws IOException {
    return readString(in, Integer.MAX_VALUE);
  }

  public static String readString(DataInput in, int maxLength)
      throws IOException {
    int length = WritableUtils.readVIntInRange(in, 0, maxLength);
    byte [] bytes = new byte[length];
    in.readFully(bytes, 0, length);
    return decode(bytes);
  }
```

## ObjectWritable

针对Java基本类型、字符串、枚举、Writable、空值、Writable的其他子类，ObjectWritable提供了一个封装，适用于字段需要使用多种类型。ObjectWritable可用于Hadoop的远程调用中参数的序列化和反序列化。ObjectWritable的另一个典型应用是在需要序列化不同类型的对象到某一个字段，如在一个SequenceFile的值保存不同类型的对象时，可以将该值声明为ObjectWritable。

ObjectWritable包含三个成员变量：被封装的对象实例instance、该对象运行时类的class对象declaredClass、Configuration对象conf。

ObjectWritable的write方法调用的是静态方法ObjectWritable.writeObject(),该方法可以向DataOutput接口中写入各种Java对象：

``` java
  @Override
  public void readFields(DataInput in) throws IOException {
    readObject(in, this, this.conf);
  }

  @Override
  public void write(DataOutput out) throws IOException {
    writeObject(out, instance, declaredClass, conf);
  }

  public static void writeObject(DataOutput out, Object instance,
                                 Class declaredClass,
                                 Configuration conf) throws IOException {
    writeObject(out, instance, declaredClass, conf, false);
  }

    public static void writeObject(DataOutput out, Object instance,
        Class declaredClass, Configuration conf, boolean allowCompactArrays)
    throws IOException {

    if (instance == null) {                       // null
      instance = new NullInstance(declaredClass, conf);
      declaredClass = Writable.class;
    }

    // Special case: must come before writing out the declaredClass.
    // If this is an eligible array of primitives,
    // wrap it in an ArrayPrimitiveWritable$Internal wrapper class.
    if (allowCompactArrays && declaredClass.isArray()
        && instance.getClass().getName().equals(declaredClass.getName())
        && instance.getClass().getComponentType().isPrimitive()) {
      instance = new ArrayPrimitiveWritable.Internal(instance);
      declaredClass = ArrayPrimitiveWritable.Internal.class;
    }

    // 写出declaredClass的规范名
    UTF8.writeString(out, declaredClass.getName()); // always write declared

    if (declaredClass.isArray()) {     // non-primitive or non-compact array
      int length = Array.getLength(instance);
      out.writeInt(length);
      for (int i = 0; i < length; i++) {
        writeObject(out, Array.get(instance, i),
            declaredClass.getComponentType(), conf, allowCompactArrays);
      }

    } else if (declaredClass == ArrayPrimitiveWritable.Internal.class) {
      ((ArrayPrimitiveWritable.Internal) instance).write(out);

    } else if (declaredClass == String.class) {   // String
      UTF8.writeString(out, (String)instance);

    } else if (declaredClass.isPrimitive()) {     // primitive type

      if (declaredClass == Boolean.TYPE) {        // boolean
        out.writeBoolean(((Boolean)instance).booleanValue());
      } else if (declaredClass == Character.TYPE) { // char
        out.writeChar(((Character)instance).charValue());
      } else if (declaredClass == Byte.TYPE) {    // byte
        out.writeByte(((Byte)instance).byteValue());
      } else if (declaredClass == Short.TYPE) {   // short
        out.writeShort(((Short)instance).shortValue());
      } else if (declaredClass == Integer.TYPE) { // int
        out.writeInt(((Integer)instance).intValue());
      } else if (declaredClass == Long.TYPE) {    // long
        out.writeLong(((Long)instance).longValue());
      } else if (declaredClass == Float.TYPE) {   // float
        out.writeFloat(((Float)instance).floatValue());
      } else if (declaredClass == Double.TYPE) {  // double
        out.writeDouble(((Double)instance).doubleValue());
      } else if (declaredClass == Void.TYPE) {    // void
      } else {
        throw new IllegalArgumentException("Not a primitive: "+declaredClass);
      }
    } else if (declaredClass.isEnum()) {         // enum
      UTF8.writeString(out, ((Enum)instance).name());
    } else if (Writable.class.isAssignableFrom(declaredClass)) { // Writable
      UTF8.writeString(out, instance.getClass().getName());
      ((Writable)instance).write(out);

    } else if (Message.class.isAssignableFrom(declaredClass)) {
      ((Message)instance).writeDelimitedTo(
          DataOutputOutputStream.constructOutputStream(out));
    } else {
      throw new IOException("Can't write: "+instance+" as "+declaredClass);
    }
  }

  public static Object readObject(DataInput in, Configuration conf)
    throws IOException {
    return readObject(in, null, conf);
  }

  public static Object readObject(DataInput in, ObjectWritable objectWritable, Configuration conf)
    throws IOException {
    String className = UTF8.readString(in);
    Class<?> declaredClass = PRIMITIVE_NAMES.get(className);
    if (declaredClass == null) {
      declaredClass = loadClass(conf, className);
    }

    Object instance;

    Writable writable = WritableFactories.newInstance(instanceClass, conf);
    writable.readFields(in);
    instance = writable;
    ......
}

```

writeObject()方法首先输出对象的类名(通过对象对应的Class对象的getName()方法获得)，然后根据传入对象的模型，分情况序列化对象到输出流中。在ObjectWritable.writeObject()的逻辑中，需要分别处理null、Java数组、字符串、Java基本类型、枚举和Writable的子类6种情况，由于类的继承，处理Writable时，序列化的结果包含对象类名，对象实际类名和对象序列化结果三部分。

需要对象实际类名：根据Java的单根继承规则，ObjectWritable中传入的declaredClass，可以是传入instance对象对应的类的对象，也可以是instance对象的父类的类对象。但是在序列化和反序列化时，往往不能使用父类的序列化方法(如write方法)来序列化子类对象，所以在序列化结果重必须记住对象的实际类名。

和输出对应，ObjectWritable的readFields()方法调用的是静态方法ObjectWritable.readObject(),该方法的实现和writeObject()类似，但其依赖于WritableFactories类。WritableFactories类允许非公有的Writable子类定义一个对象工厂，有该工厂创建Writable对象，如在上面的readObject()代码中，通过WritableFactories的静态方法newInstance()，可以创建类型为instanceClass的Writable子对象。

``` java
// 保存了类型和WritableFactory工厂的对应关系
private static final Map<Class, WritableFactory> CLASS_TO_FACTORY =
    new ConcurrentHashMap<Class, WritableFactory>();

public static void setFactory(Class c, WritableFactory factory) {
    CLASS_TO_FACTORY.put(c, factory);
  }

public static WritableFactory getFactory(Class c) {
    return CLASS_TO_FACTORY.get(c);
  }

public static Writable newInstance(Class<? extends Writable> c, Configuration conf) {
    // 根据输入类型查找对应的工厂对象
    WritableFactory factory = WritableFactories.getFactory(c);
    if (factory != null) {
      Writable result = factory.newInstance();
      // 如果该对象是可配置的，newInstance会通过对象的setConf()方法配置对象
      if (result instanceof Configurable) {
        ((Configurable) result).setConf(conf);
      }
      return result;
    } else {
      // 采用传统的反射工具ReflectionUtils创建对象
      return ReflectionUtils.newInstance(c, conf);
    }
  }

// 使用一个定义的工厂创建一个类的新实例
public static Writable newInstance(Class<? extends Writable> c) {
    return newInstance(c, null);
  }

```

WritableFactories提供注册机制，使得这些Writable的子类可以将该工厂登记到WritableFactories的静态成员变量CLASS_TO_FACTORY中。


## GenericWritable

ObjectWritable作为一种通用机制，相当浪费资源，它需要为每一个输出写入封装类型的名字，如果类型的数量不是很多，而且可以事先知道，则可以使用一个静态类型数组来提高效率，并使用数组索引作为类型的序列化引用。为此，引入了GenericWritable。

``` java
public abstract class GenericWritable implements Writable, Configurable {

  private static final byte NOT_SET = -1;

  private byte type = NOT_SET;

  private Writable instance;

  private Configuration conf = null;

  /**
   * Set the instance that is wrapped.
   *
   * @param obj
   */
  public void set(Writable obj) {
    instance = obj;
    Class<? extends Writable> instanceClazz = instance.getClass();
    Class<? extends Writable>[] clazzes = getTypes();
    for (int i = 0; i < clazzes.length; i++) {
      Class<? extends Writable> clazz = clazzes[i];
      if (clazz.equals(instanceClazz)) {
        type = (byte) i;
        return;
      }
    }
    throw new RuntimeException("The type of instance is: "
                               + instance.getClass() + ", which is NOT registered.");
  }

  /**
   * Return the wrapped instance.
   */
  public Writable get() {
    return instance;
  }

  @Override
  public String toString() {
    return "GW[" + (instance != null ? ("class=" + instance.getClass().getName() +
        ",value=" + instance.toString()) : "(null)") + "]";
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    type = in.readByte();
    Class<? extends Writable> clazz = getTypes()[type & 0xff];
    try {
      instance = ReflectionUtils.newInstance(clazz, conf);
    } catch (Exception e) {
      e.printStackTrace();
      throw new IOException("Cannot initialize the class: " + clazz);
    }
    instance.readFields(in);
  }

  @Override
  public void write(DataOutput out) throws IOException {
    if (type == NOT_SET || instance == null)
      throw new IOException("The GenericWritable has NOT been set correctly. type="
                            + type + ", instance=" + instance);
    out.writeByte(type);
    instance.write(out);
  }

  /**
   * Return all classes that may be wrapped.  Subclasses should implement this
   * to return a constant array of classes.
   */
  abstract protected Class<? extends Writable>[] getTypes();

  @Override
  public Configuration getConf() {
    return conf;
  }

  @Override
  public void setConf(Configuration conf) {
    this.conf = conf;
  }

}
```

当两个具有相同类型的key但是值类型不同的sequence file在map阶段结束到reduce阶段时，多种类型的值类型是不被允许的。在这种情况下，这个类能够帮助你包装不同类型的实例。

和ObjectWritable相比，这个类效率更加高，因为ObjectWritable会在每一个Key-Value键值对中追加类声明到输出文件中。

- 使用GenericWritable类的一般方法

 - 写自定义的class，例如GenericObject，它继承自GenericWritable
 - 实现抽象方法 getTypes(),定义在应用中在GenericObject里将要包装的类。注意：在getTypes()方法里定义的类必须实现Writable接口。

一个简单示例：

``` java
public class GenericObject extends GenericWritable {

   private static Class[] CLASSES = {
               ClassType1.class,
               ClassType2.class,
               ClassType3.class,
               };

   protected Class[] getTypes() {
       return CLASSES;
   }
}
 ```