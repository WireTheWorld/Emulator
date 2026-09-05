/*    
    Copyright (C) Paul Falstad and Iain Sharp
    
    This file is part of CircuitJS1.

    CircuitJS1 is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    CircuitJS1 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CircuitJS1.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.lushprojects.circuitjs1.client;

import java.util.HashMap;

//CirSim.java (c) 2010 - 2017 作者 Paul Falstad
//GWT 转换 (c) 2015 - 2017 作者 Iain Sharp

//版本历史
//v1.9.1js 16-11-06 Iain Sharp
//添加从支持 CORS 的链接导入文件
//v1.9.0js 16-11-06 Iain Sharp
// 添加 URL 缩短器和 Dropbox 集成
//v1.8.0js 16-10-30 Iain Sharp
// 整合 Falstad 的最新更新。改进 UI 并修复错误
//v1.0.1 15-06-15
//将源代码转换为 GPLv2
//将示例文件纳入项目
//v1.0.0 15-06-05
//文本导入/导出现已修复
//v0.1.3 15-06-03
//拖动时元件上出现操作手柄
//改进电位器和可变电压轨与滑块的集成 - 颜色变化以及对
//滚轮的支持。
//v0.1.2 15-06-01
//在选择模式下，当用户靠近手柄时自动选择拖动后模式
//手柄的视觉效果已更改
//接受 "2k2" 风格的工程师元件值简写
//美化菜单
//v0.1.1 
//修复 PNP 晶体管及粘贴相关的错误
//v0.1.0 - 
//在 Web 上的初始测试发布


//待办事项
// 示波器改进
//UI 改进
//电位器 - 改进绘制代码
//线圈绘制 - 查明我的替代代码为何不起作用

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;

public class circuitjs1 implements EntryPoint {
	
	public static final String versionString="2.4.2js";
	
	// 如果服务器在与电路模拟器相同的目录中运行 shortrelay.php 文件，则设为 true
	public static final boolean shortRelaySupported = true;

	static CirSim mysim;
	HashMap<String,String> localizationMap;
	
  public void onModuleLoad() {
      localizationMap = new HashMap<String,String>();
      
      loadLocale();
  }

  native String language()  /*-{ // 已修改以支持 Electron，它为 navigator.languages 返回空数组
      if (navigator.languages) {
        if (navigator.languages.length>0)
          return navigator.languages[0];
        else
          return "en-US";
      } else {
      	return  (navigator.language || navigator.userLanguage) ;  
      }
  }-*/;

  void loadLocale() {
  	String url;
	QueryParameters qp = new QueryParameters();
	String lang = qp.getValue("lang");
	if (lang == null) {
	    Storage stor = Storage.getLocalStorageIfSupported();
	    if (stor != null)
		lang = stor.getItem("language");
	    if (lang == null)
		lang = language();
	}
  	GWT.log("got language " + lang);
//  	lang = "pl";
  	lang = lang.replaceFirst("-.*", "");
  	if (lang.startsWith("en")) {
  	    // 英语无需加载语言文件
  	    loadSimulator();
  	    return;
  	}
  	url = GWT.getModuleBaseURL()+"locale_" + lang + ".txt";
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("File Error Response", exception);
				}

				public void onResponseReceived(Request request, Response response) {
					// 在此处进行处理
					if (response.getStatusCode()==Response.SC_OK) {
					String text = response.getText();
					processLocale(text);
					// 处理结束
					}
					else {
						GWT.log("Bad file server response:"+response.getStatusText() );
						loadSimulator();
					}
				}
			});
		} catch (RequestException e) {
			GWT.log("failed file reading", e);
		}

  }
  
  void processLocale(String data) {
      String lines[] = data.split("\r?\n");
      int i;
      for (i = 0; i != lines.length; i++) {
	  String line = lines[i];
	  if (line.length() == 0)
	      continue;
	  if (line.charAt(0) != '"') {
	      CirSim.console("ignoring line in string catalog: " + line);
	      continue;
	  }
	  int q2 = line.indexOf('"', 1);
	  if (q2 < 0 || line.charAt(q2+1) != '=' || line.charAt(q2+2) != '"' ||
		  line.charAt(line.length()-1) != '"') {
	      CirSim.console("ignoring line in string catalog: " + line);
	      continue;
	  }
	  String str1 = line.substring(1, q2);
	  String str2 = line.substring(q2+3, line.length()-1);
	  localizationMap.put(str1, str2);
      }
      loadSimulator();
  }
  
  public void loadSimulator() {
	  mysim = new CirSim();
	  mysim.localizationMap = localizationMap;
	  mysim.init();

	    Window.addResizeHandler(new ResizeHandler() {
	    	 
            public void onResize(ResizeEvent event)
            {               
            	mysim.setCanvasSize();
                mysim.setiFrameHeight();	
                	
            }
        });
	    
	    /*
	    Window.addWindowClosingHandler(new Window.ClosingHandler() {

	        public void onWindowClosing(ClosingEvent event) {
	            event.setMessage("Are you sure?");
	        }
	    });
	     */

	  mysim.updateCircuit();
	  

	  
  	}
  
  }
	  
