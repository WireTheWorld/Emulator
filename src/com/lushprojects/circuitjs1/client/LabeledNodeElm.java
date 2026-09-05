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

class LabeledNodeElm extends CircuitElm {
    final int FLAG_ESCAPE = 4;
    final int FLAG_INTERNAL = 1;
    
    public LabeledNodeElm(int xx, int yy) {
	super(xx, yy);
	text = "label";
    }
    public LabeledNodeElm(int xa, int ya, int xb, int yb, int f,
	    StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	text = st.nextToken();
	if ((flags & FLAG_ESCAPE) == 0) {
	    // 转义/反转义之前的旧式导出
	    while (st.hasMoreTokens())
		text += ' ' + st.nextToken();
	} else {
	    // 新式导出
	    text = CustomLogicModel.unescape(text); 
	}
    }
    String dump() {
	flags |= FLAG_ESCAPE;
	return super.dump() + " " + CustomLogicModel.escape(text);
    }

    String text;
    static HashMap<String,Integer> nodeList;
    int nodeNumber;
    boolean isInternal() { return (flags & FLAG_INTERNAL) != 0; }

    public static native void console(String text)
    /*-{
	    console.log(text);
	}-*/;

    static void resetNodeList() {
	nodeList = new HashMap<String,Integer>();
    }
    final int circleSize = 17;
    void setPoints() {
	super.setPoints();
	lead1 = interpPoint(point1, point2, 1-circleSize/dn);
    }
    void setNode(int p, int n) {
	super.setNode(p, n);
	if (p == 1) {
	    // 分配新节点
	    nodeList.put(text, new Integer(n));
	    nodeNumber = n;
	}
    }

    int getDumpType() { return 207; }
    int getPostCount() { return 1; }
    
    // 这基本上就是一根导线，因为它只是连接两个节点
    boolean isWire() { return true; }
    
    // 获取连接节点（对所有元件来说都与常规节点相同，但这个除外）。
    // 节点 0 是端子，节点 1 是所有同名节点共享的内部节点
    int getConnectionNode(int n) {
	if (n == 0)
	    return nodes[0];
	return nodeNumber;
    }
    int getConnectionNodeCount() { return 2; }
    
    int getInternalNodeCount() {
	// 这在启动时可能发生
	if (nodeList == null)
	    return 0;

	Integer nn = nodeList.get(text);

	// 节点已经分配了？
	if (nn != null) {
	    nodeNumber = nn.intValue();
	    return 0;
	}

	// 分配一个新的
	return 1;
    }
    void draw(Graphics g) {
	setVoltageColor(g, volts[0]);
	drawThickLine(g, point1, lead1);
	g.setColor(needsHighlight() ? selectColor : whiteColor);
	setPowerColor(g, false);
	String str = text;
	boolean lineOver = false;
	if (str.startsWith("/")) {
	    lineOver = true;
	    str = str.substring(1);
	}
	drawCenteredText(g, str, x2, y2, true);
	if (lineOver) {
	    int asc=(int)g.currentFontSize;
	    if (lineOver) {
		int ya = y2-asc;
                int sw=(int)g.context.measureText(str).getWidth();
                g.drawLine(x2-sw/2, ya, x2+sw/2, ya);
	    }
	}

	curcount = updateDotCount(current, curcount);
	drawDots(g, point1, lead1, curcount);
	interpPoint(point1, point2, ps2, 1+11./dn);
	setBbox(point1, ps2, circleSize);
	drawPosts(g);
    }
    double getCurrentIntoNode(int n) { return -current; }
    void setCurrent(int x, double c) { current = -c; }
    void stamp() {
	sim.stampVoltageSource(nodeNumber, nodes[0], voltSource, 0);
    }
    double getVoltageDiff() { return volts[0]; }
    int getVoltageSourceCount() { return 1; }
    void getInfo(String arr[]) {
	arr[0] = text;
	arr[1] = "I = " + getCurrentText(getCurrent());
	arr[2] = "V = " + getVoltageText(volts[0]);
    }

    public EditInfo getEditInfo(int n) {
	if (n == 0) {
	    EditInfo ei = new EditInfo("Text", 0, -1, -1);
	    ei.text = text;
	    return ei;
	}
        if (n == 1) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.checkbox = new Checkbox("Internal Node", isInternal());
            return ei;
        }
	return null;
    }
    public void setEditValue(int n, EditInfo ei) {
	if (n == 0)
	    text = ei.textf.getText();
	if (n == 1)
	    flags = ei.changeFlag(flags, FLAG_INTERNAL);
    }
}
