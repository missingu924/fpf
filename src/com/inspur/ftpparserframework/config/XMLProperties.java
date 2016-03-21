/**
 *
 *Copyright (c) 2004 Langchao LG Information System Co.,Ltd. All Rights Reserved.
 *
 */
package com.inspur.ftpparserframework.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * XMLProperties
 * 处理使用简单XML元素文件的工具类(这里的元素是Element)。
 * 所有的元素格式为 X.Y.Z, 举例如下：
 * <pre>
 * &lt;X&gt;
 *     &lt;Y&gt;
 *         &lt;Z attr="ATTR"&gt;someValue&lt;/Z&gt;
 *     &lt;/Y&gt;
 * &lt;/X&gt;
 * </pre>
 * <p/>
 * 通过XML配置文件 来读取简单的配置信息
 *
 * @author Towncarl
 *         Date: 2004-2-16 11:46:24
 */

public class XMLProperties
{
   private boolean autoSave = true;
   private File file;
   private Document doc;
   private Map propertyCache = new HashMap();

   /**
    * 构造函数。提供是否验证XML文件合法性标志和是否自动存盘的标志。
    *
    * @param file     用来存取数据的XML文件名称
    * @param autoSave 是否自动存盘的标志
    * @param validate 是否需要验证XML文件合法性的标志
    * @throws JDOMException
    */
   public XMLProperties(String file, boolean autoSave, boolean validate) throws
       JDOMException
   {
      this.autoSave = autoSave;
      this.file = new File(file);
      SAXBuilder builder = new SAXBuilder(validate);
      try
      {
         doc = builder.build(file);
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
      }
      catch (JDOMException ex)
      {
         ex.printStackTrace();
         throw new JDOMException("构造过程出现错误！ " + ex);
      }
   }

   /**
    * 构造函数。默认值不验证，提供是否自动存盘的标志。
    *
    * @param file     用来存取数据的XML文件名称
    * @param autoSave 是否自动存盘的标志
    * @throws JDOMException
    */
   public XMLProperties(String file, boolean autoSave) throws JDOMException
   {
      this(file, autoSave, false);
   }

   /**
    * 构造函数。默认值不验证，不自动存盘。
    *
    * @param file 用来存取数据的XML文件名称
    * @throws JDOMException
    */
   public XMLProperties(String file) throws JDOMException
   {
      this(file, false, false);
   }

   /**
    * 查看是否设置自动存盘
    *
    * @return boolean 自动存盘的标志，布尔值
    */
   public boolean isAutoSave()
   {
      return autoSave;
   }

   /**
    * 设置自动存盘标识
    *
    * @param autoSave 自动存盘标识，布尔值
    */
   public void setAutoSave(boolean autoSave)
   {
      this.autoSave = autoSave;
   }

   /**
    * 把XML文档存盘，原有的文件加上后缀.bak
    *
    * @throws IOException
    */
   public synchronized void saveProperties() throws IOException
   {
      if (autoSave == false)
      {
         return;
      }
      Format fmt = Format.getPrettyFormat();
      fmt.setEncoding("GB2312");
      XMLOutputter out = new XMLOutputter(fmt);
      File temp = null;
      FileWriter writer = null;
      boolean isError = false;
      try
      {
         //先生成一个临时文件
         temp = new File(file.getParent(), file.getName() + ".tmp");
         writer = new FileWriter(temp);
         out.output(doc, writer);
      }
      catch (IOException ex)
      {
         isError = true;
         ex.printStackTrace();
         throw new IOException("生成临时文件出现错误！" + ex);
      }
      finally
      {
         writer.close();
      }
      //备份源文件，生成新文件
      if (isError == false)
      {
         File bak = new File(file.getParent(), file.getName() + ".bak");
         //删除原来的备份文件
         if (bak.exists())
         {
            bak.delete();
         }
         boolean isSuccess = file.renameTo(bak);
         file.delete();
         //临时文件变成正式文件
         isSuccess = temp.renameTo(file);
         if (isSuccess == false)
         {
            throw new IOException("备份文件过程出现错误！");
         }
      }
   }

   /**
    * 返回指定元素的内容，字符串格式
    *
    * @param name 元素的名称，格式为 X.Y.Z。
    * @return String 元素的内容，null表示没有匹配的元素
    */
   public String getPorperty(String name)
   {
      if (propertyCache.containsKey(name))
      {
         return propertyCache.get(name).toString();
      }

      //查找指定的元素元素
      Element element = this.findOnly(name);
      if (element == null)
      {
         return null;
      }

      String value = element.getTextTrim();
      propertyCache.put(name, value);
      return value;
   }

   /**
    * 返回指定元素的Attribute内容，字符串格式
    *
    * @param name 元素的名称，格式为 X.Y.Z。
    * @param attr Attribute名称
    * @return String Attribute的内容，null表示没有匹配的元素(Attribute)
    */
   public String getPorperty(String name, String attr)
   {
      String nameAttr = name + ":" + attr;
      if (propertyCache.containsKey(nameAttr))
      {
         return propertyCache.get(nameAttr).toString();
      }

      //查找指定的元素元素
      Element element = this.findOnly(name);
      if (element == null)
      {
         return null;
      }

      String value = element.getAttributeValue(attr);
      propertyCache.put(nameAttr, value);
      return value;
   }

   /**
    * 设置一个元素值。如果指定的元素不存在，就自动创建该元素。
    *
    * @param name  需要赋值的元素名称，格式为 X.Y.Z
    * @param value 元素值
    * @throws IOException
    */
   public void setProperty(String name, String value) throws IOException
   {
      propertyCache.put(name, value);

      //查找指定的元素元素
      Element element = this.findCreate(name);
      element.setText(value);
      saveProperties();
   }

   /**
    * 设置一个元素的Attribute值。如果指定的元素不存在，就自动创建该元素。
    *
    * @param name  需要赋值的元素名称，格式为 X.Y.Z
    * @param attr  Attribute名称
    * @param value 元素值
    * @throws IOException
    */
   public void setProperty(String name, String attr, String value) throws
       IOException
   {
      String nameAttr = name + ":" + attr;
      propertyCache.put(nameAttr, value);

      //查找指定的元素元素
      Element element = this.findCreate(name);
      element.setAttribute(attr, value);
      saveProperties();
   }

   /**
    * 删除指定的元素，如果该元素不存在，不做任何事情
    *
    * @param name 要删除的元素的名称，格式为 X.Y.Z
    * @throws IOException
    */
   public void deleteProperty(String name) throws IOException
   {
      if (propertyCache.containsKey(name))
      {
         propertyCache.remove(name);
      }

      Element element = this.findOnly(name);
      if (element != null)
      {
         element.detach();
      }
      saveProperties();
   }

   /**
    * 删除指定的元素的Attribute，如果该元素或者Attribute不存在，不做任何事情
    *
    * @param name 元素的名称，格式为 X.Y.Z
    * @param attr Attribute的名称
    * @throws IOException
    */
   public void deleteProperty(String name, String attr) throws IOException
   {
      String nameAttr = name + ":" + attr;
      if (propertyCache.containsKey(nameAttr))
      {
         propertyCache.remove(nameAttr);
      }

      Element element = this.findOnly(name);
      if (element != null)
      {
         element.removeAttribute(attr);
      }
      saveProperties();
   }

   /**
    * 获得指定元素的所有子元素名称。例如有X.Y.A, X.Y.B 和X.Y.C，使用
    * getChildrenProperties("X.Y")将获得一个包含三个字符串的数组。如果没有
    * 匹配的元素，返回一个空的数组。
    *
    * @param parent 元素的名称，格式为 X.Y.Z
    * @return String[] 指定元素的所有子元素名称的数组，或者空数组。
    */
   public String[] getChildrenProperties(String parent)
   {
      //分解元素的名称
      String[] propName = parsePropertyName(parent);
      Element element = doc.getRootElement();
      //遍历搜索匹配的元素
      for (int i = 0; i < propName.length; i++)
      {
         element = element.getChild(propName[i]);
         //没有匹配的元素，返回一个空的数组
         if (element == null)
         {
            return new String[]
                {};
         }
      }
      //找到啦！
      List children = element.getChildren();
      String[] childrenName = new String[children.size()];
      for (int i = 0; i < children.size(); i++)
      {
         childrenName[i] = ( (Element) children.get(i)).getName();
      }
      return childrenName;
   }

   /**
    * 根据指定元素，获得与值匹配的属性值
    * @param parent   父元素名称
    * @param seName   源元素名称
    * @param teName   目标属性名称
    * @param attributeName   属性名称
    * @param seValue      要匹配的源元素值
    * @param attrValue     要匹配的目标属性值
    * @return
    */
   public String getProperties(String parent, String seName, String teName,
                               String attributeName, String seValue,
                               String attrValue)
   {
      //分解元素的名称
      String[] propName = parsePropertyName(parent);
      Element element = doc.getRootElement();

      //遍历搜索匹配的元素
      for (int i = 0; i < propName.length; i++)
      {
         List list = element.getChildren(propName[i]);

         //没有匹配的元素，返回一个空的数组
         if (list == null)
         {
            return null;
         }
         else
         {
            for (Iterator iter = list.iterator(); ; iter.hasNext())
            {
               Element lastelement = (Element) iter.next();
               if (seValue.equals(lastelement.getChildText(seName)))
               {
                  if (attributeName == null)
                  {
                     //直接返回第一个元素值
                     return lastelement.getChildText(teName);

                  }
                  else
                  {
                     //多个可能值，需要根据属性过滤
                     List implList = lastelement.getChildren(teName);
                     for (Iterator implIter = implList.iterator(); ;
                          implIter.hasNext())
                     {
                        Element implClass = (Element) implIter.next();
                        if (attrValue.equals(implClass.getAttributeValue(
                            attributeName)))
                        {
                           return implClass.getTextTrim();
                        }
                     }
                  }
               }
            }
         }
      }
      return "";
   }

   /**
    * helper方法，返回一个字符串数组，代表分解后的元素名称。 形如 "prop.name.is.this"
    * 的元素会返回包含四个字符串的数组。
    *
    * @param name 元素的名称，格式为 X.Y.Z
    * @return String[] 代表分解后元素名称的字符串数组
    */
   private String[] parsePropertyName(String name)
   {
      //因为符号"."是保留字符，所以不能使用String.split()
      //所以先进行一次替代
      return name.replace('.', '#').split("#");
   }

   /**
    * helper方法，查找一个指定的元素
    *
    * @param name 元素名称，格式为 X.Y.Z
    * @return Element 如果找到就返回这个元素，否则返回null
    */
   private Element findOnly(String name)
   {
      //分解元素的名称
      String[] propName = parsePropertyName(name);
      Element element = this.doc.getRootElement();
      //遍历搜索匹配的元素
      for (int i = 0; i < propName.length; i++)
      {
         element = element.getChild(propName[i]);
         if (element == null)
         {
            return null;
         }
      }
      //找到啦！
      return element;
   }

   /**
    * helper方法，查找一个指定的元素,如果这个元素不存在，创建它
    *
    * @param name 元素名称，格式为 X.Y.Z
    * @return Element 找到就返回这个元素，否则返回创建的新元素
    */
   private Element findCreate(String name)
   {
      //分解元素的名称
      String[] propName = parsePropertyName(name);
      Element element = doc.getRootElement();
      //遍历搜索匹配的元素，不存在则创建它
      for (int i = 0; i < propName.length; i++)
      {
         if (element.getChild(propName[i]) == null)
         {
            //自动创建该元素
            element.addContent(new Element(propName[i]));
         }
         element = element.getChild(propName[i]);
      }
      //找到啦！
      return element;
   }
}
