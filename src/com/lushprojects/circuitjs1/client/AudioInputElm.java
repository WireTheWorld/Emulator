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

import com.google.gwt.core.client.JsArrayNumber;
import java.util.HashMap;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FileUpload;

class AudioFileEntry {
    String fileName;
    JsArrayNumber data;
}

class AudioInputElm extends RailElm {
    	JsArrayNumber data;
    	double timeOffset;
    	int samplingRate;
    	int fileNum;
    	String fileName;
    	double maxVoltage;
    	double startPosition;
    	
    	static int lastSamplingRate;
    	
    	// 缓存音频数据，以便在剪切/粘贴或撤销/重做时保留数据
    	static int fileNumCounter = 1;
    	static HashMap<Integer, AudioFileEntry> audioFileMap = new HashMap<Integer, AudioFileEntry>();
    	
	public AudioInputElm(int xx, int yy) {
	    super(xx, yy, WF_AC);
	    maxVoltage = 5;
	}
	
	public AudioInputElm(int xa, int ya, int xb, int yb, int f,
		       StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    waveform = WF_AC;
	    maxVoltage = Double.parseDouble(st.nextToken());
	    startPosition = Double.parseDouble(st.nextToken());
	    fileNum = Integer.parseInt(st.nextToken());

	    AudioFileEntry ent = audioFileMap.get(fileNum);
	    if (ent != null) {
		fileName = ent.fileName;
		data = ent.data;
	    }
	    samplingRate = lastSamplingRate;
	}
	
	double fmphase;
	
	String dump() {
	    // 在导出数据中添加文件编号，以便在剪切粘贴或撤销/重做时保留音频文件数据
	    // 我们不会将整个文件保存在导出数据中，因为文件太大了。
	    if (data != null) {
		if (fileNum == 0)
		    fileNum = fileNumCounter++;
		AudioFileEntry ent = new AudioFileEntry();
		ent.fileName = fileName;
		ent.data = data;
		audioFileMap.put(fileNum, ent);
	    }
	    return super.dump() + " " + maxVoltage + " " + startPosition + " " + fileNum;
	}
	
	void reset() {
	    timeOffset = startPosition;
	}
	
	void drawRail(Graphics g) {
	    drawRailText(g, fileName == null ? "No file" : fileName);
	}
	
	String getRailText() {
	    return fileName == null ? "No file" : fileName;
	}
	
	void setSamplingRate(int sr) {
	    samplingRate = sr;
	}
	
	double getVoltage() {
	    if (data == null)
		return 0;
	    if (timeOffset < startPosition)
		timeOffset = startPosition;
	    int ptr = (int) (timeOffset * samplingRate);
	    if (ptr >= data.length()) {
		ptr = 0;
		timeOffset = 0;
	    }
	    return data.get(ptr) * maxVoltage;
	}
	
	void stepFinished() {
	    timeOffset += sim.timeStep;
	}
	
	int getDumpType() { return 411; }
	int getShortcut() { return 0; }
	
	public EditInfo getEditInfo(int n) {
            if (n == 0) {
                EditInfo ei = new EditInfo("", 0, -1, -1);
                final AudioInputElm thisElm = this;
                final FileUpload file = new FileUpload();
                ei.widget = file;
                file.addChangeHandler(new ChangeHandler() {
			    public void onChange(ChangeEvent event) {
				fileName = file.getFilename().replaceAll("^.*\\\\", "").replaceAll("\\.[^.]*$", "");
				AudioInputElm.fetchLoadFileData(thisElm, file.getElement());
			    }
			  });
                return ei;
            }
            if (n == 1)
                return new EditInfo("Max Voltage", maxVoltage);
            if (n == 2)
                return new EditInfo("Start Position (s)", startPosition);
	    return null;
	}
	
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 1)
		maxVoltage = ei.value;
	    if (n == 2)
		startPosition = ei.value;
	}
	
	// 获取所选文件的音频数据
	static native String fetchLoadFileData(AudioInputElm elm, Element uploadElement) /*-{
	    var oFiles = uploadElement.files;
       	    var context = new (window.AudioContext || window.webkitAudioContext)();
       	    elm.@com.lushprojects.circuitjs1.client.AudioInputElm::setSamplingRate(I)(context.sampleRate);
	    if (oFiles.length >= 1) {
                        var reader = new FileReader();
                        reader.onload = function(e) {
                		context.decodeAudioData(reader.result, function(buffer) {
                    			var data = buffer.getChannelData(0); 
                    			elm.@com.lushprojects.circuitjs1.client.AudioInputElm::gotAudioData(*)(data);
                		},
                		function(e){ console.log("Error with decoding audio data" + e.err); });
                        };

                        reader.readAsArrayBuffer(oFiles[0]);
	    }
	}-*/;
	
	void gotAudioData(JsArrayNumber d) {
	    data = d;
	    lastSamplingRate = samplingRate;
	    AudioOutputElm.lastSamplingRate = samplingRate;
	}

	void getInfo(String arr[]) {
	    arr[0] = "audio input";
	    if (data == null) {
		arr[1] = "no file loaded";
		return;
	    }
	    arr[1] = "V = " + getVoltageText(volts[0]);
	    arr[2] = "pos = " + getUnitText(timeOffset, "s");
	    double dur = data.length() / (double)samplingRate;
	    arr[3] = "dur = " + getUnitText(dur, "s");
	}
	
	public static void clearCache() {
	    audioFileMap.clear();
	}
    }
