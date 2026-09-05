

package com.lushprojects.circuitjs1.client;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/* StringTokenizer -- breaks a String into tokens
Copyright (C) 1998, 1999, 2001, 2002, 2005  Free Software Foundation, Inc.

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */



/**
* 此类将字符串拆分为标记(token)。调用者可以设置按哪些
* 分隔符拆分字符串,以及是否返回分隔符。
* 这比 {@link java.io.StreamTokenizer} 简单得多。
*
* <p>可以通过调用 nextToken(String) 在运行中更改分隔符集合。
* 但语义相当复杂;它甚至取决于是否调用 <code>hasMoreTokens()</code>。
* 在调用 <code>hasMoreTokens()</code> 之前,最后一个标记之后的
* 旧分隔符都可能被返回。
*
* <p>如果想要获取分隔符,必须使用三参数构造函数。
* 分隔符会作为由单个字符组成的标记返回。
*
* @author Jochen Hoenicke
* @author Warren Levy (warrenl@cygnus.com)
* @see java.io.StreamTokenizer
* @status updated to 1.4
*/
public class StringTokenizer implements Enumeration<Object>
{
// 警告:StringTokenizer 是引导周期中的核心类。此事实带来的影响
// 参见 vm/reference/java/lang/Runtime 中的注释。

/**
* 我们在 str 中当前所处的位置。
*/
private int pos;

/**
* 要拆分为标记的字符串。
*/
private final String str;

/**
* 字符串的长度。
*/
private final int len;

/**
* 包含分隔符字符的字符串。
*/
private String delim;

/**
* 指示是否应返回分隔符。
*/
private final boolean retDelims;

/**
* 为字符串 <code>str</code> 创建一个新的 StringTokenizer,
* 它将按默认分隔符集合(空格、制表符、
* 换行、回车和换页)拆分,并且不返回
* 分隔符。
*
* @param str 要拆分的字符串
* @throws NullPointerException 如果 str 为 null
*/
public StringTokenizer(String str)
{
 this(str, " \t\n\r\f", false);
}

/**
* 创建一个新的 StringTokenizer,按给定的分隔符字符
* 拆分给定字符串。它不返回分隔符
* 字符。
*
* @param str 要拆分的字符串
* @param delim 包含所有分隔符字符的字符串
* @throws NullPointerException 如果任一参数为 null
*/
public StringTokenizer(String str, String delim)
{
 this(str, delim, false);
}

/**
* 创建一个新的 StringTokenizer,按给定的分隔符字符
* 拆分给定字符串。如果将 <code>returnDelims</code> 设为
* <code>true</code>,则分隔符字符会作为独立的标记返回。
* 分隔符标记始终由单个字符组成。
*
* @param str 要拆分的字符串
* @param delim 包含所有分隔符字符的字符串
* @param returnDelims 指示是否想要获取分隔符
* @throws NullPointerException 如果 str 或 delim 为 null
*/
public StringTokenizer(String str, String delim, boolean returnDelims)
{
 len = str.length();
 this.str = str;
 this.delim = delim;
 this.retDelims = returnDelims;
 this.pos = 0;
}

/**
* 指示是否还有更多标记。
*
* @return 如果下一次调用 nextToken() 将会成功,则返回 true
*/
public boolean hasMoreTokens()
{
 if (! retDelims)
   {
     while (pos < len && delim.indexOf(str.charAt(pos)) >= 0)
       pos++;
   }
 return pos < len;
}

/**
* 返回下一个标记,并将分隔符集合更改为给定的
* <code>delim</code>。分隔符集合的更改是
* 永久性的,即下一次调用 nextToken() 时,会使用同一个
* 分隔符集合。
*
* @param delim 包含新分隔符字符的字符串
* @return 相对于新分隔符字符的下一个标记
* @throws NoSuchElementException 如果没有更多标记
* @throws NullPointerException 如果 delim 为 null
*/
public String nextToken(String delim) throws NoSuchElementException
{
 this.delim = delim;
 return nextToken();
}

/**
* 返回字符串的下一个标记。
*
* @return 相对于当前分隔符字符的下一个标记
* @throws NoSuchElementException 如果没有更多标记
*/
public String nextToken() throws NoSuchElementException
{
 if (pos < len && delim.indexOf(str.charAt(pos)) >= 0)
   {
     if (retDelims)
       return str.substring(pos, ++pos);
     while (++pos < len && delim.indexOf(str.charAt(pos)) >= 0)
       ;
   }
 if (pos < len)
   {
     int start = pos;
     while (++pos < len && delim.indexOf(str.charAt(pos)) < 0)
       ;

     return str.substring(start, pos);
   }
 throw new NoSuchElementException();
}

/**
* 这与 hasMoreTokens 功能相同。这是
* <code>Enumeration</code> 接口的方法。
*
* @return 如果下一次调用 nextElement() 将会成功,则返回 true
* @see #hasMoreTokens()
*/
public boolean hasMoreElements()
{
 return hasMoreTokens();
}

/**
* 这与 nextToken() 功能相同。这是
* <code>Enumeration</code> 接口的方法。
*
* @return 相对于当前分隔符字符的下一个标记
* @throws NoSuchElementException 如果没有更多标记
* @see #nextToken()
*/
public Object nextElement() throws NoSuchElementException
{
 return nextToken();
}

/**
* 计算字符串中剩余标记的数量,相对于
* 当前分隔符集合。
*
* @return <code>nextToken()</code> 将会成功的次数
* @see #nextToken()
*/
public int countTokens()
{
 int count = 0;
 int delimiterCount = 0;
 boolean tokenFound = false; // 当找到非分隔符时置为 true
 int tmpPos = pos;

 // 出于效率考虑,我们累加计数分隔符,而不是每次遇到
 // 分隔符都检查 retDelims。这样一来,我们
 // 只需在方法末尾做一次条件判断
 while (tmpPos < len)
   {
     if (delim.indexOf(str.charAt(tmpPos++)) >= 0)
       {
         if (tokenFound)
           {
             // 已到达一个标记的末尾
             count++;
             tokenFound = false;
           }
         delimiterCount++; // 为此分隔符计数加一
       }
     else
       {
         tokenFound = true;
         // 到达该标记的末尾
         while (tmpPos < len
                && delim.indexOf(str.charAt(tmpPos)) < 0)
           ++tmpPos;
       }
   }

 // 确保计算最后一个标记
 if (tokenFound)
   count++;

 // 如果计算分隔符,则将它们计入标记总数
 return retDelims ? count + delimiterCount : count;
}
} // class StringTokenizer
