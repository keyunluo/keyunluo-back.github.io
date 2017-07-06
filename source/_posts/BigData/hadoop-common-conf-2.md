---
title: Hadoop源码学习(4)——Hadoop-Common-Conf(2)
comments: true
toc: true
date: 2016-08-04 15:25:02
categories: BigData
tags : Hadoop
keywords: Hadoop，配置
---

>**本节内容：**本节学习Hadoop-Common-Project中的hadoop-common-conf模块，该模块主要实现了hadoop项目中的配置信息处理，学习设置配置等内容。

<!-- more -->

## get*

get用于在Configuration对象中获取相应的配置信息，这些配置信息可以是boolean、int、long等基本类型，也可以是其他一些Hadoop常用类型，如类的信息(getClassByName、getClasses、getClass)、String数组(getStringCollection、getStrings)、URL(getResource)等，这些方法里最重要的是get()方法，它根据配置项的键获取相应的值，如果键不存在，则返回默认值defaultValue，其他的方法都会依赖Configuration.get()，并在get()的基础上做进一步的处理，get方法如下：

``` java
  public String get(String name, String defaultValue) {
    String[] names = handleDeprecation(deprecationContext.get(), name);
    String result = null;
    for(String n : names) {
      result = substituteVars(getProps().getProperty(n, defaultValue));
    }
    return result;
  }
```

Configuration.get()会调用私有方法substituteVars(),该方法会完成配置的属性扩展。属性扩展是将配置项的值包含${key}这种格式的变量替换成相应的值。

## 配置属性扩展高效算法

代码中提到了一种快速的属性替换算法，比正则表达式快15倍以上。substituteVars的工作依赖于正则表达式：

``` python
varPat : \$\{[^\}\$ ]+ \}
```
即匹配${key},key不包含`}`,`$`和空格的一系列字符串，key至少出现一次。


该算法的详细代码如下：
``` java
  // 属性扩展只能进行一定的次数，最多20次，避免死循环
  private static final int MAX_SUBST = 20;

  // 开始处的下标
  private static final int SUB_START_IDX = 0;
  // 结束处的下标
  private static final int SUB_END_IDX = SUB_START_IDX + 1;

  /**
   * This is a manual implementation of the following regex
   * "\\$\\{[^\\}\\$\u0020]+\\}". It can be 15x more efficient than
   * a regex matcher as demonstrated by HADOOP-11506. This is noticeable with
   * Hadoop apps building on the assumption Configuration#get is an O(1)
   * hash table lookup, especially when the eval is a long string.
   *
   * @param eval a string that may contain variables requiring expansion.
   * @return a 2-element int array res such that
   * eval.substring(res[0], res[1]) is "var" for the left-most occurrence of
   * ${var} in eval. If no variable is found -1, -1 is returned.
   */
  private static int[] findSubVariable(String eval) {
    int[] result = {-1, -1};

    int matchStart;  // 匹配开始处，不断移动
    int leftBrace;   // 从开始处匹配的第一个`{`

    // scanning for a brace first because it's less frequent than $
    // that can occur in nested class names
    //
    match_loop:
    // 从括号左边扫描
    for (matchStart = 1, leftBrace = eval.indexOf('{', matchStart);
         // minimum left brace position (follows '$')
         leftBrace > 0
         // right brace of a smallest valid expression "${c}"
         && leftBrace + "{c".length() < eval.length();
         leftBrace = eval.indexOf('{', matchStart)) {
      int matchedLen = 0;
      if (eval.charAt(leftBrace - 1) == '$') {
        int subStart = leftBrace + 1; // after '{'
        for (int i = subStart; i < eval.length(); i++) {
          switch (eval.charAt(i)) {
            case '}':  //匹配到结果，返回结果
              if (matchedLen > 0) { // match
                result[SUB_START_IDX] = subStart;
                result[SUB_END_IDX] = subStart + matchedLen;
                break match_loop;
              }
              // fall through to skip 1 char
            case ' ':
            case '$':
              matchStart = i + 1;
              continue match_loop;
            default:
              matchedLen++;
          }
        }
        // scanned from "${"  to the end of eval, and no reset via ' ', '$':
        //    no match!
        break match_loop;
      } else {
        // not a start of a variable
        // 不是一个变量的开始，从括号后一个字母继续
        matchStart = leftBrace + 1;
      }
    }
    return result;
  }

  /**
   * Attempts to repeatedly expand the value {@code expr} by replacing the
   * left-most substring of the form "${var}" in the following precedence order
   * <ol>
   *   <li>by the value of the Java system property "var" if defined</li>
   *   <li>by the value of the configuration key "var" if defined</li>
   * </ol>
   *
   * If var is unbounded the current state of expansion "prefix${var}suffix" is
   * returned.
   *
   * @param expr the literal value of a config key
   * @return null if expr is null, otherwise the value resulting from expanding
   * expr using the algorithm above.
   * @throws IllegalArgumentException when more than
   * {@link Configuration#MAX_SUBST} replacements are required
   */
  // 重复替换表达式最左边的属性扩展
  private String substituteVars(String expr) {
    if (expr == null) {
      return null;
    }
    String eval = expr;
    // 循环，最多做20次属性扩展
    for (int s = 0; s < MAX_SUBST; s++) {
      // 老版本： private static pattern varPat = Pattern.compile("\\$\\{[^\\}\\$\u0020]+\\}");
      // 替换： Matcher match = varPat.matcher("");
      final int[] varBounds = findSubVariable(eval);

      // 什么都没有找到，返回表达式
      if (varBounds[SUB_START_IDX] == -1) {
        return eval;
      }

      // 提取子串
      final String var = eval.substring(varBounds[SUB_START_IDX],
          varBounds[SUB_END_IDX]);
      String val = null;
      // 查看系统属性里有没有var对应的val
      // 这一步保证有限使用系统属性做属性扩展
      try {
        val = System.getProperty(var);
      } catch(SecurityException se) {
        LOG.warn("Unexpected SecurityException in Configuration", se);
      }
      // 查看Configuration保存的键-值对里有没有var对应的val
      if (val == null) {
        val = getRaw(var);
      }
      //属性扩展中的var没有绑定，不做扩展，返回
      if (val == null) {
        return eval; // return literal ${var}: var is unbound
      }
      final int dollar = varBounds[SUB_START_IDX] - "${".length();
      final int afterRightBrace = varBounds[SUB_END_IDX] + "}".length();
      // 替换，完成属性扩展
      eval = eval.substring(0, dollar)
             + val
             + eval.substring(afterRightBrace);
    }
    //次数过多，抛出异常
    throw new IllegalStateException("Variable substitution depth too large: "
                                    + MAX_SUBST + " " + expr);
  }
```

## Configurable 接口

Configurable的含义是可配置的，如果一个类实现了该接口，意味着该类是可以配置的，可以通过为这个类的对象传入一个Configuration实例。

- 类图
![Configurable](/resource/blog/2016-08/configurable.png)

Hadoop中有大量的类实现了Configurable接口，如org.apache.hadoop.mapreduce.lib.input.RegexFilter,RegexFilter对象工作时，需要提供一个正则表达式，用于过滤读取的记录。由于在RegexFilter的父类Filter中实现了Configurable接口，RegexFilter可以在它的setConf()方法中使用Configuration.get()方法获取以字符串传入的正则表达式，并初始化成员变量：

``` java
    /** configure the Filter by checking the configuration
     */
    public void setConf(Configuration conf) {
      String regex = conf.get(FILTER_REGEX);
      if (regex == null)
        throw new RuntimeException(FILTER_REGEX + "not set");
      this.p = Pattern.compile(regex);
      this.conf = conf;
    }
```

一般来说，对象创建以后，就应该使用setConf()方法，为对象提供进一步的初始化工作。为了简化对象创建和调用setConf()方法这两个连续的步骤，org.apache.hadoop.util.ReflectionUtils提供了静态方法newInstance(),代码如下：

``` java
  @SuppressWarnings("unchecked")
  public static <T> T newInstance(Class<T> theClass, Configuration conf) {
    T result;
    try {
      Constructor<T> meth = (Constructor<T>) CONSTRUCTOR_CACHE.get(theClass);
      if (meth == null) {
        meth = theClass.getDeclaredConstructor(EMPTY_ARRAY);
        meth.setAccessible(true);
        CONSTRUCTOR_CACHE.put(theClass, meth);
      }
      result = meth.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    setConf(result, conf);
    return result;
  }
```

方法newInstance()利用Java反射机制，根据对象类型信息(参数 theClass)，创建一个新的相应类型的对象，然后调用ReflectionUtils中的另一个静态方法setConf()配置对象，代码如下：

``` java
  public static void setConf(Object theObject, Configuration conf) {
    if (conf != null) {
      if (theObject instanceof Configurable) {
        ((Configurable) theObject).setConf(conf);
      }
      setJobConf(theObject, conf);
    }
  }
```

在setConf()中，如果对象实现了Configurable接口，那么对象的setConf()方法会被调用，并根据COnfiguration类的实例conf进一步初始化对象。

## Configured类

Configured类实现了Configurable接口，默认在构造函数中调用了setConf()方法，可简化使用

- 类图
![Configured](/resource/blog/2016-08/configured.png)

## ConfServlet类

输出正在运行的configuration数据的一个Servlet.

![ConfServlet](/resource/blog/2016-08/ConfServlet.png)

## Reconfigurable

在运行时改变配置文件，与Reconfigurable相关的类共有6个：

- Reconfigurable ：接口，属性是否可改变等方法
- ReconfigurableBase ：实现Reconfigurable接口的基类
- ReconfigurationException ：属性不可改变，抛出异常
- ReconfigurationServlet ：改变一个节点的配置文件的Servlet
- ReconfigurationTaskStatus ：重新配置的任务状态信息
- ReconfigurationUtil ：工具类，获得改变的属性
