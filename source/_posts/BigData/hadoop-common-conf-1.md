---
title: Hadoop源码学习(3)——Hadoop-Common-Conf(1)
comments: true
toc: true
date: 2016-08-03 09:25:02
categories: BigData
tags : Hadoop
keywords: Hadoop，配置
---

>**本节内容：**本节学习Hadoop-Common-Project中的hadoop-common-conf模块，该模块主要实现了hadoop项目中的配置信息处理，从总体上了解configuration类。


<!-- more -->

## hadoop-common 源代码结构

hadoop-common是hadoop项目中的核心模块，在src/main中定义了很多基础功能，此外还有protobuf等定义，本文学习org.apache.hadoop.conf配置类的设计。

``` txt
java
├── org
│   └── apache
│       └── hadoop
│           ├── conf
│           ├── crypto
│           ├── fs
│           ├── ha
│           ├── HadoopIllegalArgumentException.java
│           ├── http
│           ├── io
│           ├── ipc
│           ├── jmx
│           ├── log
│           ├── metrics
│           ├── metrics2
│           ├── net
│           ├── record
│           ├── security
│           ├── service
│           ├── tools
│           ├── tracing
│           └── util
└── overview.html

21 directories, 2 files

```

## Java中的配置文件

JDK提供了java.util.Properties类，用于处理简单的配置文件，它只支持键值对，形式如下：`ENTRY=VALUE`。由于Properties是基于HashTable的，它部支持节，对配置文件进行分类。

java.util.Properties中处理属性列表的主要方法：
``` java
//用指定的键在此属性列表中搜索属性
public String getProperty(String key)

// 功能同上，参数defaultValue提供了默认值
public String getProperty(String key,String defaultValue)

// Properties.setProperty()最终调用Hashtable的方法put
public synchronized object setProperty(String key,String value)
```

由于Java本身提供的配置文件功能简单，Java社区出现了大量的配置信息的读/写方案，其中比较有名的是Apache Jakarta Commons提供的Commons Configuration，其支持文本，XML配置文件格式，支持加载多个配置文件，支持分层或多级配置等。

Hadoop没有使用Java自带的配置文件类以及第三方类，而是自己实现了一套独有的配置文件管理系统，并提供了自己的API，即使用org.apache.hadoop.conf.Configuration处理配置信息。

## Configuration配置文件概述

Hadoop配置文件采用ＸＭＬ格式，一个例子如下：

``` xml
<configuration>
<property>
    <name>mapreduce.framework.name</name>
    <value>yarn</value>
</property>
<property>
    <name>mapred.child.java.opts</name>
    <value>-Xmx4096m</value>
</property>
</configuration>
```

Hadoop配置文件的根元素是configuration，一般只包含子元素property。同时，Hadoop可以将多个配置文件合并，产生一个配置，如core-site.xml和core-default.xml，通过Configure类的loadResource()方法，把它们合并成一个配置：

``` java
Configuration conf = new Configuration();
conf.addResource("core-default.xml");
conf.addResource("core-site.xml");
```
如果这两个资源都包含了相同的配置项。并且前面的一个资源配置项没有标记为final，那么后面的配置将覆盖前面的配置。

Hadoop配置系统还有一个很重要的功能，就是属性扩展，即可以使用`${}`进行引用。

使用Configuration类的一般过程是：构造Configuration对象，通过类的addResource()方法添加需要加载的资源；然后通过get*和set*方法访问/设置配置项，资源会在第一次使用的时候自动加载到对象中。

## Configuration 类分析

- 类图

![hadoop-common-conf](/resource/blog/2016-08/hadoop-common-conf.png)

- Configuration.addSource

资源Resource由Object和Name构成。数组resources保存了所有通过addResource()方法添加Configuration对象的资源，Configuration.addSource()有如下六种形式：

  - public void addResource(String name) ：CLASSPATH资源,如 "core-site.xml"
  - public void addResource(URL url) : 如 "http://server.com/core-site.xml"
  - public void addResource(Path file) : hadoop文件路径，如 "hdfs://server:54300/conf/core-site.xml"
  - public void addResource(InputStream in) : 一个已经打开的输入流
  - public void addResource(InputStream in, String name) ：一个已经打开的输入流，传入Resource的名称
  - public void addResource(Configuration conf) ： Configuration对象

- 资源加载

资源加载通过对象的addResource()方法或类的静态addDefaultResource()方法添加到Configuration对象中，添加的资源不会被立即加载，而是通过reloadConfiguration()方法清空properties和finalParameters。

静态方法addDefaultResource()也能清空Configuration对象中的数据，这是通过静态成员REGISTRY作为媒介进行的。

``` java
  public void addResource(InputStream in, String name) {
    addResourceObject(new Resource(in, name));
  }

  public synchronized void reloadConfiguration() {
    properties = null;                            // trigger reload
    finalParameters.clear();                      // clear site-limits
  }

  private synchronized void addResourceObject(Resource resource) {
    resources.add(resource);                      // add to resources
    reloadConfiguration();
  }

  public static synchronized void addDefaultResource(String name) {
    if(!defaultResources.contains(name)) {
      defaultResources.add(name);
      for(Configuration conf : REGISTRY.keySet()) {
        if(conf.loadDefaults) {
          conf.reloadConfiguration();
        }
      }
    }
  }
```

成员变量properties中的数据，直到需要的时候才会加载进来，在getProps()方法中，如果发现properties为空，将触发loadResource()方法加载配置资源。

``` java
protected synchronized Properties getProps() {
    if (properties == null) {
      properties = new Properties();
      HashMap<String, String[]> backup =
        new HashMap<String, String[]>(updatingResource);
      loadResources(properties, resources, quietmode);
      if (overlay!= null) {
        properties.putAll(overlay);
        for (Map.Entry<Object,Object> item: overlay.entrySet()) {
          String key = (String)item.getKey();
          updatingResource.put(key, backup.get(key));
        }
      }
    }
    return properties;
  }
```

## XML处理

Hadoop的配置文件都是XML的形式，JAXP(Java API for XML Processing)是一种稳定的、可靠的XML处理API，支持SAX(Simple API for XML)和DOM(Document Object Model)两种XML处理方法。

SAX提供了一种流式的、时间驱动的XML处理方式，但是编写逻辑比较复杂，比较适合处理大的XML文件。

DOM和SAX不同，期工作方式是：首先将XML文档一次性的装入内存；然后根据文档中定义的元素和属性在内存中创建一个"树形结构"，也就是一个文档对象模型，将文档对象化，文档中每个节点对应着模型中一个对象；然后使用对象提供的编程接口，访问XML文档进而操作XML文档。由于Hadoop的配置文件都是很小的文件，因此Configuration使用DOM处理XML。

DOM加载部分的代码：

``` java
private Resource loadResource(Properties properties, Resource wrapper, boolean quiet) {
    String name = UNKNOWN_RESOURCE;
    try {
      Object resource = wrapper.getResource();
      name = wrapper.getName();

      // 得到用于创建DOM解析器的工厂
      DocumentBuilderFactory docBuilderFactory
        = DocumentBuilderFactory.newInstance();
      //忽略xml中的注释
      docBuilderFactory.setIgnoringComments(true);

      //提供对XML名称空间的支持，允许xml中的Include机制
      docBuilderFactory.setNamespaceAware(true);
      try {
          docBuilderFactory.setXIncludeAware(true);
      } catch (UnsupportedOperationException e) {
        LOG.error("Failed to set setXIncludeAware(true) for parser "
                + docBuilderFactory
                + ":" + e,
                e);
      }

      // 获取解析XML的DocumentBuilder对象
      DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
      Document doc = null;
      Element root = null;
      boolean returnCachedProperties = false;

      // 根据不同资源，做预处理并调用相应形式的DocumentBuilder.parse
      if (resource instanceof URL) {                  // an URL resource
        doc = parse(builder, (URL)resource);
      } else if (resource instanceof String) {        // a CLASSPATH resource
        URL url = getResource((String)resource);
        doc = parse(builder, url);
      } else if (resource instanceof Path) {          // a file resource
        // Can't use FileSystem API or we get an infinite loop
        // since FileSystem uses Configuration API.  Use java.io.File instead.
        File file = new File(((Path)resource).toUri().getPath())
          .getAbsoluteFile();
        if (file.exists()) {
          if (!quiet) {
            LOG.debug("parsing File " + file);
          }
          doc = parse(builder, new BufferedInputStream(
              new FileInputStream(file)), ((Path)resource).toString());
        }
      } else if (resource instanceof InputStream) {
        doc = parse(builder, (InputStream) resource, null);
        returnCachedProperties = true;
      } else if (resource instanceof Properties) {
        overlay(properties, (Properties)resource);
      } else if (resource instanceof Element) {
        root = (Element)resource;
      }

      // root 获取文档元素
      if (root == null) {
        if (doc == null) {
          if (quiet) {
            return null;
          }
          throw new RuntimeException(resource + " not found");
        }
        root = doc.getDocumentElement();
      }
      Properties toAddTo = properties;
      if(returnCachedProperties) {
        toAddTo = new Properties();
      }

      //确保根节点是configuration
      if (!"configuration".equals(root.getTagName()))
        LOG.fatal("bad conf file: top-level element not <configuration>");

      //获取根节点的所有子节点
      NodeList props = root.getChildNodes();
      DeprecationContext deprecations = deprecationContext.get();
      for (int i = 0; i < props.getLength(); i++) {
        Node propNode = props.item(i);
        if (!(propNode instanceof Element))  // 如果子节点不是Element,忽略
          continue;
        Element prop = (Element)propNode;
        if ("configuration".equals(prop.getTagName())) {
          // 如果子节点是configuration，递归调用loadResource进行处理
          // 这意味着configuration的子节点可以是configuration
          loadResource(toAddTo, new Resource(prop, name), quiet);
          continue;
        }

        // 子节点是property
        if (!"property".equals(prop.getTagName()))
          LOG.warn("bad conf file: element not <property>");
        NodeList fields = prop.getChildNodes();
        String attr = null;
        String value = null;
        boolean finalParameter = false;
        LinkedList<String> source = new LinkedList<String>();

        //查找name、value、final的值
        for (int j = 0; j < fields.getLength(); j++) {
          Node fieldNode = fields.item(j);
          if (!(fieldNode instanceof Element))
            continue;
          Element field = (Element)fieldNode;
          if ("name".equals(field.getTagName()) && field.hasChildNodes())
            attr = StringInterner.weakIntern(
                ((Text)field.getFirstChild()).getData().trim());
          if ("value".equals(field.getTagName()) && field.hasChildNodes())
            value = StringInterner.weakIntern(
                ((Text)field.getFirstChild()).getData());
          if ("final".equals(field.getTagName()) && field.hasChildNodes())
            finalParameter = "true".equals(((Text)field.getFirstChild()).getData());
          if ("source".equals(field.getTagName()) && field.hasChildNodes())
            source.add(StringInterner.weakIntern(
                ((Text)field.getFirstChild()).getData()));
        }
        source.add(name);

        // Ignore this parameter if it has already been marked as 'final'
        // 替换弃用key,忽略finalParameter
        if (attr != null) {
          if (deprecations.getDeprecatedKeyMap().containsKey(attr)) {
            DeprecatedKeyInfo keyInfo =
                deprecations.getDeprecatedKeyMap().get(attr);
            keyInfo.clearAccessed();
            for (String key:keyInfo.newKeys) {
              // update new keys with deprecated key's value
              loadProperty(toAddTo, name, key, value, finalParameter,
                  source.toArray(new String[source.size()]));
            }
          }
          else {
            loadProperty(toAddTo, name, attr, value, finalParameter,
                source.toArray(new String[source.size()]));
          }
        }
      }

      if (returnCachedProperties) {
        overlay(properties, toAddTo);
        return new Resource(toAddTo, name);
      }
      return null;
    } catch (IOException e) {
      LOG.fatal("error parsing conf " + name, e);
      throw new RuntimeException(e);
    } catch (DOMException e) {
      LOG.fatal("error parsing conf " + name, e);
      throw new RuntimeException(e);
    } catch (SAXException e) {
      LOG.fatal("error parsing conf " + name, e);
      throw new RuntimeException(e);
    } catch (ParserConfigurationException e) {
      LOG.fatal("error parsing conf " + name , e);
      throw new RuntimeException(e);
    }
  }

 // 载入属性
 private void loadProperty(Properties properties, String name, String attr,
      String value, boolean finalParameter, String[] source) {
    if (value != null) {
      if (!finalParameters.contains(attr)) {
        properties.setProperty(attr, value);
        updatingResource.put(attr, source);
      } else if (!value.equals(properties.getProperty(attr))) {
        LOG.warn(name+":an attempt to override final parameter: "+attr
            +";  Ignoring.");
      }
    }
    if (finalParameter) {
      finalParameters.add(attr);
    }
  }

```
