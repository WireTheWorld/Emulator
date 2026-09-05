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

import java.util.Vector;

import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.i18n.client.NumberFormat;

// 电路元件类
public abstract class CircuitElm implements Editable {
    static double voltageRange = 5;
    static int colorScaleCount = 32;
    static Color colorScale[];
    static double currentMult, powerMult;
    
    // 方便的临时点
    static Point ps1, ps2;
    
    static CirSim sim;
    static Color whiteColor, selectColor, lightGrayColor;
    static Font unitsFont;

    static NumberFormat showFormat, shortFormat;//, noCommaFormat;
    static final double pi = 3.14159265358979323846;
    static CircuitElm mouseElmRef = null;

    static final int SCALE_AUTO = 0;
    static final int SCALE_1 = 1;
    static final int SCALE_M = 2;
    static final int SCALE_MU = 3;
 
    // 用户创建元件时的初始点。对于简单的两端元件，这是第一个节点/端点。
    int x, y;
    
    // 用户拖出元件到达的点。对于简单的两端元件，这是第二个节点/端点
    int x2, y2;
    
    int flags, nodes[], voltSource;
    
    // 沿 x 和 y 轴的长度，以及差值的符号
    int dx, dy, dsign;

    int lastHandleGrabbed=-1;
    
    // 元件长度
    double dn;
    
    double dpx1, dpy1;
    
    // (x,y) 和 (x2,y2) 作为 Point 对象
    Point point1, point2;
    
    // 引线点（简单两端元件的导线短线端点）  
    Point lead1, lead2;
    
    // 每个节点上的电压
    double volts[];
    
    double current, curcount;
    Rectangle boundingBox;
    
    // 如果子类将此设为 true，元件将只能水平或垂直放置 
    boolean noDiagonal;
    
    public boolean selected;
    
//    abstract int getDumpType();
    int getDumpType() {
	
	throw new IllegalStateException(); // 似乎是绕过一个看似编译器 bug 的必要手段
	// 该 bug 影响 OTAElm，确保此方法（本应抽象）抛出
	// 一个异常
 }
    
    // 从 java 遗留的，已不再做任何事。 
    Class getDumpClass() { return getClass(); }
    
    int getDefaultFlags() { return 0; }

    static void initClass(CirSim s) {
	unitsFont = new Font("SansSerif", 0, 12);
	sim = s;
	
	colorScale = new Color[colorScaleCount];
	
	
	ps1 = new Point();
	ps2 = new Point();

	showFormat=NumberFormat.getFormat("####.###");

	shortFormat=NumberFormat.getFormat("####.#");
    }

    static public Color positiveColor, negativeColor, neutralColor;
    
    static void setColorScale() {

	int i;

	if (positiveColor == null)
	    positiveColor = Color.green;
	if (negativeColor == null)
	    negativeColor = Color.red;
	if (neutralColor == null)
	    neutralColor = Color.gray;
	
	for (i = 0; i != colorScaleCount; i++) {
	    double v = i * 2. / colorScaleCount - 1;
	    if (v < 0) {
		colorScale[i] = new Color(neutralColor, negativeColor, -v);
	    } else {
		colorScale[i] = new Color(neutralColor, positiveColor, v);
	    }
	}

    }
    
    // 创建在 xx,yy 处有一个端点的元件，供用户拖拽
    CircuitElm(int xx, int yy) {
	x = x2 = xx;
	y = y2 = yy;
	flags = getDefaultFlags();
	allocNodes();
	initBoundingBox();
    }
    
    // 从 undump 创建位于 xa,ya 和 xb,yb 之间的元件
    CircuitElm(int xa, int ya, int xb, int yb, int f) {
	x = xa; y = ya; x2 = xb; y2 = yb; flags = f;
	allocNodes();
	initBoundingBox();
    }
    
    void initBoundingBox() {
	boundingBox = new Rectangle();
	boundingBox.setBounds(min(x, x2), min(y, y2),
			      abs(x2-x)+1, abs(y2-y)+1);
    }
    
    // 分配我们需要的节点/电压数组
    void allocNodes() {
	int n = getPostCount() + getInternalNodeCount();
	// 尽可能保留电压
	if (nodes == null || nodes.length != n) {
	    nodes = new int[n];
	    volts = new double[n];
	}
    }
    
    // 为导出/撤销转储元件状态
    String dump() {
	int t = getDumpType();
	return (t < 127 ? ((char)t)+" " : t+" ") + x + " " + y + " " +
	    x2 + " " + y2 + " " + flags;
    }
    
    // 处理复位按钮
    void reset() {
	int i;
	for (i = 0; i != getPostCount()+getInternalNodeCount(); i++)
	    volts[i] = 0;
	curcount = 0;
    }
    void draw(Graphics g) {}
    
    // 将电压源 vn 的电流设置为 c。vn 与之前调用 setVoltageSource(n, vn) 时传入的值相同 
    void setCurrent(int vn, double c) { current = c; }
    
    // 获取一端或两端元件的电流
    double getCurrent() { return current; }

    void setParentList(Vector<CircuitElm> elmList) {}
    
    // 为线性元件填充矩阵值。
    // 对于非线性元件，用此方法填充每次迭代不变的值，并按需调用 stampRightSide() 或 stampNonLinear()
    void stamp() {}
    
    // 为非线性元件填充矩阵值
    void doStep() {}
    
    void delete() {
	if (mouseElmRef==this)
	    mouseElmRef=null;
	sim.deleteSliders(this);
    }
    void startIteration() {}
    
    // 获取第 x 个节点的电压
    double getPostVoltage(int x) { return volts[x]; }
    
    // 设置第 x 个节点的电压，由模拟器逻辑调用
    void setNodeVoltage(int n, double c) {
	volts[n] = c;
	calculateCurrent();
    }
    
    // 根据节点电压的变化计算电流
    void calculateCurrent() {}
    
    // 计算用于绘制的端点位置和其他便捷值。在元件被移动时调用 
    void setPoints() {
    	dx = x2-x; dy = y2-y;
    	dn = Math.sqrt(dx*dx+dy*dy);
    	dpx1 = dy/dn;
    	dpy1 = -dx/dn;
    	dsign = (dy == 0) ? sign(dx) : sign(dy);
    	point1 = new Point(x , y );
    	point2 = new Point(x2, y2);
    }
    
    // 计算长度为 len 的元件的引线点。对简单的两端元件很方便。
    // 端点是用户连接导线的地方；引线是元件内部绘制的导线短线端点。
    void calcLeads(int len) {
	if (dn < len || len == 0) {
	    lead1 = point1;
	    lead2 = point2;
	    return;
	}
	lead1 = interpPoint(point1, point2, (dn-len)/(2*dn));
	lead2 = interpPoint(point1, point2, (dn+len)/(2*dn));
    }

    // 计算 a 和 b 之间线性插值比例为 f 的点
    Point interpPoint(Point a, Point b, double f) {
	Point p = new Point();
	interpPoint(a, b, p, f);
	return p;
    }
    
    // 计算 a 和 b 之间线性插值比例为 f 的点，结果存于 c
    void interpPoint(Point a, Point b, Point c, double f) {
	c.x = (int) Math.floor(a.x*(1-f)+b.x*f+.48);
	c.y = (int) Math.floor(a.y*(1-f)+b.y*f+.48);
    }
    
    /**
     * 返回沿 a 与 b 连线、比例为 f 的点，并沿垂直方向偏移 g
     * @param a 1st Point
     * @param b 2nd Point
     * @param f Fraction along line
     * @param g Fraction perpendicular to line
     * 将插值后的点存入 c 并返回
     */
    void interpPoint(Point a, Point b, Point c, double f, double g) {
	int gx = b.y-a.y;
	int gy = a.x-b.x;
	g /= Math.sqrt(gx*gx+gy*gy);
	c.x = (int) Math.floor(a.x*(1-f)+b.x*f+g*gx+.48);
	c.y = (int) Math.floor(a.y*(1-f)+b.y*f+g*gy+.48);
    }
    
    /**
     * 返回沿 a 与 b 连线、比例为 f 的点，并沿垂直方向偏移 g
     * @param a 1st Point
     * @param b 2nd Point
     * @param f Fraction along line
     * @param g Fraction perpendicular to line
     * @return Interpolated point
     */
    Point interpPoint(Point a, Point b, double f, double g) {
	Point p = new Point();
	interpPoint(a, b, p, f, g);
	return p;
    }
    
    
    /**
     * 计算沿 a 与 b 连线、比例为 f 的两个点，并沿垂直方向偏移 +/-g
     * @param a 1st point (In)
     * @param b 2nd point (In)
     * @param c 1st point (Out)
     * @param d 2nd point (Out)
     * @param f Fraction along line
     * @param g Fraction perpendicular to line
     */
    void interpPoint2(Point a, Point b, Point c, Point d, double f, double g) {
//	int xpd = b.x-a.x;
//	int ypd = b.y-a.y;
	int gx = b.y-a.y;
	int gy = a.x-b.x;
	g /= Math.sqrt(gx*gx+gy*gy);
	c.x = (int) Math.floor(a.x*(1-f)+b.x*f+g*gx+.48);
	c.y = (int) Math.floor(a.y*(1-f)+b.y*f+g*gy+.48);
	d.x = (int) Math.floor(a.x*(1-f)+b.x*f-g*gx+.48);
	d.y = (int) Math.floor(a.y*(1-f)+b.y*f-g*gy+.48);
    }
    
    void draw2Leads(Graphics g) {
	// 绘制第一条引线
	setVoltageColor(g, volts[0]);
	drawThickLine(g, point1, lead1);

	// 绘制第二条引线
	setVoltageColor(g, volts[1]);
	drawThickLine(g, lead2, point2);
    }
    Point [] newPointArray(int n) {
	Point a[] = new Point[n];
	while (n > 0)
	    a[--n] = new Point();
	return a;
    }

    // 从点 a 到 b 绘制电流点
    void drawDots(Graphics g, Point pa, Point pb, double pos) {
	 if ((!sim.simIsRunning()) || pos == 0 || !sim.dotsCheckItem.getState())
	    return;
	int dx = pb.x-pa.x;
	int dy = pb.y-pa.y;
	double dn = Math.sqrt(dx*dx+dy*dy);
	g.setColor(sim.conventionCheckItem.getState()?Color.yellow:Color.cyan);
	int ds = 16;
	pos %= ds;
	if (pos < 0)
	    pos += ds;
	double di = 0;
	for (di = pos; di < dn; di += ds) {
	    int x0 = (int) (pa.x+di*dx/dn);
	    int y0 = (int) (pa.y+di*dy/dn);
	    g.fillRect(x0-2, y0-2, 4, 4);
	}
    }

    Polygon calcArrow(Point a, Point b, double al, double aw) {
	Polygon poly = new Polygon();
	Point p1 = new Point();
	Point p2 = new Point();
	int adx = b.x-a.x;
	int ady = b.y-a.y;
	double l = Math.sqrt(adx*adx+ady*ady);
	poly.addPoint(b.x, b.y);
	interpPoint2(a, b, p1, p2, 1-al/l, aw);
	poly.addPoint(p1.x, p1.y);
	poly.addPoint(p2.x, p2.y);
	return poly;
    }
    Polygon createPolygon(Point a, Point b, Point c) {
	Polygon p = new Polygon();
	p.addPoint(a.x, a.y);
	p.addPoint(b.x, b.y);
	p.addPoint(c.x, c.y);
	return p;
    }
    Polygon createPolygon(Point a, Point b, Point c, Point d) {
	Polygon p = new Polygon();
	p.addPoint(a.x, a.y);
	p.addPoint(b.x, b.y);
	p.addPoint(c.x, c.y);
	p.addPoint(d.x, d.y);
	return p;
    }
    Polygon createPolygon(Point a[]) {
	Polygon p = new Polygon();
	int i;
	for (i = 0; i != a.length; i++)
	    p.addPoint(a[i].x, a[i].y);
	return p;
    }
    
    // 将第二个点拖到 xx, yy
    void drag(int xx, int yy) {
	xx = sim.snapGrid(xx);
	yy = sim.snapGrid(yy);
	if (noDiagonal) {
	    if (Math.abs(x-xx) < Math.abs(y-yy)) {
		xx = x;
	    } else {
		yy = y;
	    }
	}
	x2 = xx; y2 = yy;
	setPoints();
    }
    
    void move(int dx, int dy) {
	x += dx; y += dy; x2 += dx; y2 += dy;
	boundingBox.translate(dx, dy);
	setPoints();
    }

    // 元件拖拽创建完成时调用；如果尺寸为零且应被删除则返回 true
    boolean creationFailed() {
	return (x == x2 && y == y2);
    }

    // 用于设置内部元件的位置，以便在父元件内部绘制它
    void setPosition(int x_, int y_, int x2_, int y2_) {
	x = x_;
	y = y_;
	x2 = x2_;
	y2 = y2_;
	setPoints();
    }
    
    // 判断将此元件移动 (dx,dy) 是否会与另一个元件重叠
    boolean allowMove(int dx, int dy) {
	int nx = x+dx;
	int ny = y+dy;
	int nx2 = x2+dx;
	int ny2 = y2+dy;
	int i;
	for (i = 0; i != sim.elmList.size(); i++) {
	    CircuitElm ce = sim.getElm(i);
	    if (ce.x == nx && ce.y == ny && ce.x2 == nx2 && ce.y2 == ny2)
		return false;
	    if (ce.x == nx2 && ce.y == ny2 && ce.x2 == nx && ce.y2 == ny)
		return false;
	}
	return true;
    }
    
    void movePoint(int n, int dx, int dy) {
    	// 由 IES 修改，防止用户拖动点创建出零尺寸的节点
    	// 这些节点随后会渲染异常
    	int oldx=x;
    	int oldy=y;
    	int oldx2=x2;
    	int oldy2=y2;
    	if (n == 0) {
    		x += dx; y += dy;
    	} else {
    		x2 += dx; y2 += dy;
    	}
    	if (x==x2 && y==y2) {
    		x=oldx;
    		y=oldy;
    		x2=oldx2;
    		y2=oldy2;
    	}
    	setPoints();
    }
    
    void drawPosts(Graphics g) {
	// 我们现在通常在 updateCircuit() 中做这件事，因为那里的逻辑更复杂。
	// 我们只处理必须绘制所有端点的情况。这发生在
	// 该元件被选中或正在被创建时
	if (sim.dragElm == null && !needsHighlight())
	    return;
	if (sim.mouseMode == CirSim.MODE_DRAG_ROW ||
	    sim.mouseMode == CirSim.MODE_DRAG_COLUMN)
	    return;
	int i;
	for (i = 0; i != getPostCount(); i++) {
	    Point p = getPost(i);
	    drawPost(g, p);
	}
    }
    
    void drawHandles(Graphics g, Color c) {
    	g.setColor(c);
    	if (lastHandleGrabbed==-1)
    		g.fillRect(x-3, y-3, 7, 7);
    	else if (lastHandleGrabbed==0)
    		g.fillRect(x-4, y-4, 9, 9);
    	if (getPostCount() > 1 || this instanceof ScopeElm) {
    		if (lastHandleGrabbed==-1)
    			g.fillRect(x2-3, y2-3, 7, 7);
    		else if (lastHandleGrabbed==1)
    			g.fillRect(x2-4, y2-4, 9, 9);
    	}
    }
    
    int getHandleGrabbedClose(int xtest, int ytest, int deltaSq, int minSize) {
    	lastHandleGrabbed=-1;
    	if ( Graphics.distanceSq(x , y , x2, y2)>=minSize) {
    		if (Graphics.distanceSq(x, y, xtest,ytest) <= deltaSq)
    			lastHandleGrabbed=0;
    		else if (Graphics.distanceSq(x2, y2, xtest,ytest) <= deltaSq)
    			lastHandleGrabbed=1;
    	}
    	return lastHandleGrabbed;
    }
    
    // 该元件需要的电压源数量 
    int getVoltageSourceCount() { return 0; }
    
    // 内部节点数量（UI 中不可见但实现所需的节点）
    int getInternalNodeCount() { return 0; }
    
    // 通知此元件其第 p 个节点为 n。值 n 可传递给 stampMatrix()
    void setNode(int p, int n) { nodes[p] = n; }
    
    // 通知此元件其第 n 个电压源为 v。值 v 可传递给 stampVoltageSource() 等，并会在 setCurrent() 调用时传回
    void setVoltageSource(int n, int v) {
	// 默认实现仅对只有一个电压源的子类有意义。如果有 0 个则不会用到，如果大于 1 个则无法工作 
	voltSource = v;
    }
    
//    int getVoltageSource() { return voltSource; } // 除被注释掉的调试代码外从未使用过
    
    double getVoltageDiff() {
	return volts[0] - volts[1];
    }
    boolean nonLinear() { return false; }
    int getPostCount() { return 2; }
    
    // 获取第 n 个节点的（全局）节点编号
    int getNode(int n) { return nodes[n]; }
    
    // 获取第 n 个节点的位置
    Point getPost(int n) {
	return (n == 0) ? point1 : (n == 1) ? point2 : null;
    }
    
    int getNodeAtPoint(int xp, int yp) {
	if (getPostCount() == 2)
	    return (x == xp && y == yp) ? 0 : 1;
	int i;
	for (i = 0; i != getPostCount(); i++) {
	    Point p = getPost(i);
	    if (p.x == xp && p.y == yp)
		return i;
	}
	return 0;
    }
    
    /*
    void drawPost(Graphics g, int x0, int y0, int n) {
	if (sim.dragElm == null && !needsHighlight() &&
	    sim.getCircuitNode(n).links.size() == 2)
	    return;
	if (sim.mouseMode == CirSim.MODE_DRAG_ROW ||
	    sim.mouseMode == CirSim.MODE_DRAG_COLUMN)
	    return;
	drawPost(g, x0, y0);
    }
    */
    static void drawPost(Graphics g, Point pt) {
	g.setColor(whiteColor);
	g.fillOval(pt.x-3, pt.y-3, 7, 7);
    }
    
    // 设置/调整用于选择元件的包围盒。getCircuitBounds() 不使用它！
    void setBbox(int x1, int y1, int x2, int y2) {
	if (x1 > x2) { int q = x1; x1 = x2; x2 = q; }
	if (y1 > y2) { int q = y1; y1 = y2; y2 = q; }
	boundingBox.setBounds(x1, y1, x2-x1+1, y2-y1+1);
    }
    
    // 为从 p1 到 p2、宽度为 w 的元件设置包围盒
    void setBbox(Point p1, Point p2, double w) {
	setBbox(p1.x, p1.y, p2.x, p2.y);
	int dpx = (int) (dpx1*w);
	int dpy = (int) (dpy1*w);
	adjustBbox(p1.x+dpx, p1.y+dpy, p1.x-dpx, p1.y-dpy);
    }

    // 扩大包围盒以包含另一个矩形
    void adjustBbox(int x1, int y1, int x2, int y2) {
	if (x1 > x2) { int q = x1; x1 = x2; x2 = q; }
	if (y1 > y2) { int q = y1; y1 = y2; y2 = q; }
	x1 = min(boundingBox.x, x1);
	y1 = min(boundingBox.y, y1);
	x2 = max(boundingBox.x+boundingBox.width,  x2);
	y2 = max(boundingBox.y+boundingBox.height, y2);
	boundingBox.setBounds(x1, y1, x2-x1, y2-y1);
    }
    void adjustBbox(Point p1, Point p2) {
	adjustBbox(p1.x, p1.y, p2.x, p2.y);
    }
    
    // 计算电路边界所需（需要对居中文本元件做特殊处理）
    boolean isCenteredText() { return false; }
    
    void drawCenteredText(Graphics g, String s, int x, int y, boolean cx) {
	// FontMetrics fm = g.getFontMetrics();
	//int w = fm.stringWidth(s);
//    	int w=0;
//	if (cx)
//	    x -= w/2;
//	g.drawString(s, x, y+fm.getAscent()/2);
//	adjustBbox(x, y-fm.getAscent()/2,
//		   x+w, y+fm.getAscent()/2+fm.getDescent());
    	int w=(int)g.context.measureText(s).getWidth();
    	int h2=(int)g.currentFontSize/2;
		g.context.save();
		g.context.setTextBaseline("middle");
		if (cx) {
			g.context.setTextAlign("center");
			adjustBbox(x-w/2,y-h2,x+w/2,y+h2);
		} else {
			adjustBbox(x,y-h2,x+w,y+h2);
		}
		
		if (cx)
			g.context.setTextAlign("center");
		g.drawString(s, x, y);
		g.context.restore();
    }
    
    // 绘制元件参数值（如电阻的欧姆数等）。hs = 偏移量
    void drawValues(Graphics g, String s, double hs) {
	if (s == null)
	    return;
	g.setFont(unitsFont);
	//FontMetrics fm = g.getFontMetrics();
	int w = (int)g.context.measureText(s).getWidth();
	g.setColor(whiteColor);
	int ya = (int)g.currentFontSize/2;
	int xc, yc;
	if (this instanceof RailElm || this instanceof SweepElm) {
	    xc = x2;
	    yc = y2;
	} else {
	    xc = (x2+x)/2;
	    yc = (y2+y)/2;
	}
	int dpx = (int) (dpx1*hs);
	int dpy = (int) (dpy1*hs);
	if (dpx == 0)
	    g.drawString(s, xc-w/2, yc-abs(dpy)-2);
	else {
	    int xx = xc+abs(dpx)+2;
	     if (this instanceof VoltageElm || (x < x2 && y > y2))
		xx = xc-(w+abs(dpx)+2);
	    g.drawString(s, xx, yc+dpy+ya);
	}
    }
    void drawCoil(Graphics g, int hs, Point p1, Point p2,
		  double v1, double v2) {
	double len = distance(p1, p2);

	g.context.save();
	g.context.setLineWidth(3.0);
	g.context.transform(((double)(p2.x-p1.x))/len, ((double)(p2.y-p1.y))/len,
		-((double)(p2.y-p1.y))/len,((double)(p2.x-p1.x))/len,p1.x,p1.y);
	if (sim.voltsCheckItem.getState() ) {
	    CanvasGradient grad = g.context.createLinearGradient(0,0,len,0);
	    grad.addColorStop(0, getVoltageColor(g,v1).getHexValue());
	    grad.addColorStop(1.0, getVoltageColor(g,v2).getHexValue());
	    g.context.setStrokeStyle(grad);
	}
	g.context.setLineCap(LineCap.ROUND);
	g.context.scale(1, hs > 0 ? 1 : -1);

	int loop;
	// 线圈更长时绘制更多环
	int loopCt = (int)Math.ceil(len/11);
	for (loop = 0; loop != loopCt; loop++) {
	    g.context.beginPath();
	    double start = len*loop/loopCt;
	    g.context.moveTo(start,0);
	    g.context.arc(len*(loop+.5)/loopCt, 0, len/(2*loopCt), Math.PI, Math.PI*2);
	    g.context.lineTo(len*(loop+1)/loopCt, 0);
	    g.context.stroke();
	}

	g.context.restore();
    }
    
    static void drawThickLine(Graphics g, int x, int y, int x2, int y2) {
    	g.setLineWidth(3.0);
    	g.drawLine(x,y,x2,y2);
    	g.setLineWidth(1.0);
    }

    static void drawThickLine(Graphics g, Point pa, Point pb) {
    	g.setLineWidth(3.0);
    	g.drawLine(pa.x, pa.y, pb.x, pb.y);
    	g.setLineWidth(1.0);
    }

    static void drawThickPolygon(Graphics g, int xs[], int ys[], int c) {
//	int i;
//	for (i = 0; i != c-1; i++)
//	    drawThickLine(g, xs[i], ys[i], xs[i+1], ys[i+1]);
//	drawThickLine(g, xs[i], ys[i], xs[0], ys[0]);
    	g.setLineWidth(3.0);
    	g.drawPolyline(xs, ys, c);
    	g.setLineWidth(1.0);
    }
    
    static void drawThickPolygon(Graphics g, Polygon p) {
	drawThickPolygon(g, p.xpoints, p.ypoints, p.npoints);
    }
    
    static void drawPolygon(Graphics g, Polygon p) {
    	g.drawPolyline(p.xpoints, p.ypoints, p.npoints);
/*	int i;
	int xs[] = p.xpoints;
	int ys[] = p.ypoints;
	int np = p.npoints;
	np -= 3;
	for (i = 0; i != np-1; i++)
	    g.drawLine(xs[i], ys[i], xs[i+1], ys[i+1]);
	g.drawLine(xs[i], ys[i], xs[0], ys[0]);*/
    }
    
    static void drawThickCircle(Graphics g, int cx, int cy, int ri) {
    	g.setLineWidth(3.0);
    	g.context.beginPath();
    	g.context.arc(cx, cy, ri*.98, 0, 2*Math.PI);
    	g.context.stroke();
    	g.setLineWidth(1.0);
    }
    
    Polygon getSchmittPolygon(float gsize, float ctr) {
	Point pts[] = newPointArray(6);
	float hs = 3*gsize;
	float h1 = 3*gsize;
	float h2 = h1*2;
	double len = distance(lead1, lead2);
	pts[0] = interpPoint(lead1, lead2, ctr-h2/len, hs);
	pts[1] = interpPoint(lead1, lead2, ctr+h1/len,  hs);
	pts[2] = interpPoint(lead1, lead2, ctr+h1/len, -hs);
	pts[3] = interpPoint(lead1, lead2, ctr+h2/len, -hs);
	pts[4] = interpPoint(lead1, lead2, ctr-h1/len, -hs);
	pts[5] = interpPoint(lead1, lead2, ctr-h1/len, hs);
	return createPolygon(pts); 
    }

    static String getVoltageDText(double v) {
	return getUnitText(Math.abs(v), "V");
    }
    static String getVoltageText(double v) {
	return getUnitText(v, "V");
    }
    static String getTimeText(double v) {
	if (v >= 60) {
	    double h = Math.floor(v/3600);
	    v -= 3600*h;
	    double m = Math.floor(v/60);
	    v -= 60*m;
	    if (h == 0)
		return m + ":" + ((v >= 10) ? "" : "0") + showFormat.format(v);
	    return h + ":" + ((m >= 10) ? "" : "0") + m + ":" + ((v >= 10) ? "" : "0") + showFormat.format(v); 
	}
	return getUnitText(v, "s");
    }
    
    static String format(double v, boolean sf) {
//	if (sf && Math.abs(v) > 10)
//	    return shortFormat.format(Math.round(v));
	return (sf ? shortFormat : showFormat).format(v);
    }
    
    static String getUnitText(double v, String u) {
    	return getUnitText(v,u, false);
    }

    static String getShortUnitText(double v, String u) {
    	return getUnitText(v,u, true);
    }
    
    private static String getUnitText(double v, String u, boolean sf) {
	String sp = sf ? "" : " ";
	double va = Math.abs(v);
	if (va < 1e-14)
	    // 这里原本返回 null，但那样导线在 0V 时会显示 "null"
	    return "0" + sp + u;
	if (va < 1e-9)
	    return format(v*1e12, sf) + sp + "p" + u;
	if (va < 1e-6)
	    return format(v*1e9, sf) + sp + "n" + u;
	if (va < 1e-3)
	    return format(v*1e6, sf) + sp + CirSim.muString + u;
	if (va < 1)
	    return format(v*1e3, sf) + sp + "m" + u;
	if (va < 1e3)
	    return format(v, sf) + sp + u;
	if (va < 1e6)
	    return format(v*1e-3, sf) + sp + "k" + u;
	if (va < 1e9)
	    return format(v*1e-6, sf) + sp + "M" + u;
	return format(v*1e-9, sf) + sp + "G" + u;
    }
    
    static String getCurrentText(double i) {
	return getUnitText(i, "A");
    }
    static String getCurrentDText(double i) {
	return getUnitText(Math.abs(i), "A");
    }

    static String getUnitTextWithScale(double val, String utext, int scale) {
	if (scale == SCALE_1)
	    return showFormat.format(val) + " " + utext;
	if (scale == SCALE_M)
	    return showFormat.format(1e3*val) + " m" + utext;
	if (scale == SCALE_MU)
	    return showFormat.format(1e6*val) + " " + CirSim.muString + utext;
	return getUnitText(val, utext);
    }

    // 更新电流绘制用的点位置 (curcount)（单电流的简单情况）
    void updateDotCount() {
	curcount = updateDotCount(current, curcount);
    }

    // 更新电流绘制用的点位置 (curcount)（多电流的一般情况）
    double updateDotCount(double cur, double cc) {
  
	 if (!sim.simIsRunning())
	    return cc;
	double cadd = cur*currentMult;
	/*if (cur != 0 && cadd <= .05 && cadd >= -.05)
	  cadd = (cadd < 0) ? -.05 : .05;*/
	cadd %= 8;
	/*if (cadd > 8)
	  cadd = 8;
	  if (cadd < -8)
	  cadd = -8;*/
	return cc + cadd;
    }
    
    // 更新并绘制简单两端元件的电流
    void doDots(Graphics g) {
	updateDotCount();
	if (sim.dragElm != this)
	    drawDots(g, point1, point2, curcount);
    }
    
    void doAdjust() {}
    void setupAdjust() {}
    
    // 获取用于右下角显示的元件信息
    void getInfo(String arr[]) {
    }
    
    int getBasicInfo(String arr[]) {
	arr[1] = "I = " + getCurrentDText(getCurrent());
	arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
	return 3;
    }
    String getScopeText(int v) {
        String info[] = new String[10];
        getInfo(info);
        return info[0];
    }
    
    Color getVoltageColor(Graphics g, double volts) {
    	if (needsHighlight()) {
    	    	return (selectColor);
    	}
    	if (!sim.voltsCheckItem.getState()) {
    	    	return(whiteColor);
    	}
    	int c = (int) ((volts+voltageRange)*(colorScaleCount-1)/
    		       (voltageRange*2));
    	if (c < 0)
    	    c = 0;
    	if (c >= colorScaleCount)
    	    c = colorScaleCount-1;
    	return (colorScale[c]);
    }
    
    void setVoltageColor(Graphics g, double volts) {
    	g.setColor(getVoltageColor(g, volts));
    }
    
    // yellow 参数未使用，记不清当初为何加它
    void setPowerColor(Graphics g, boolean yellow) {

	/*if (conductanceCheckItem.getState()) {
	  setConductanceColor(g, current/getVoltageDiff());
	  return;
	  }*/
	if (!sim.powerCheckItem.getState() )
	    return;
	setPowerColor(g, getPower());
    }
    
    void setPowerColor(Graphics g, double w0) {
	if (!sim.powerCheckItem.getState() )
	    return;
    	if (needsHighlight()) {
	    	g.setColor(selectColor);
	    	return;
    	}
	w0 *= powerMult;
	//System.out.println(w);
	int i = (int) ((colorScaleCount/2)+(colorScaleCount/2)*-w0);
	if (i<0)
	    i=0;
	if (i>=colorScaleCount)
	    i=colorScaleCount-1;
	 g.setColor(colorScale[i]);
    }
    void setConductanceColor(Graphics g, double w0) {
	w0 *= powerMult;
	//System.out.println(w);
	double w = (w0 < 0) ? -w0 : w0;
	if (w > 1)
	    w = 1;
	int rg = (int) (w*255);
	g.setColor(new Color(rg, rg, rg));
    }
    double getPower() { return getVoltageDiff()*current; }
    double getScopeValue(int x) {
	return (x == Scope.VAL_CURRENT) ? getCurrent() :
	    (x == Scope.VAL_POWER) ? getPower() : getVoltageDiff();
    }
    int getScopeUnits(int x) {
	return (x == Scope.VAL_CURRENT) ? Scope.UNITS_A :
	    (x == Scope.VAL_POWER) ? Scope.UNITS_W : Scope.UNITS_V;
    }
    public EditInfo getEditInfo(int n) { return null; }
    public void setEditValue(int n, EditInfo ei) {}
    
    // 获取可通过 getConnectionNode() 检索的节点数量
    int getConnectionNodeCount() { return getPostCount(); }
    
    // 获取可传递给 getConnection() 的节点，用于测试此元件是否连接
    // 这两个节点；除带标签的节点外，这与 getNode() 相同。
    int getConnectionNode(int n) { return getNode(n); }
    
    // n1 和 n2 是否由此元件连接？这用于确定
    // 未连接的节点，并查找回路
    boolean getConnection(int n1, int n2) { return true; }
    
    // n1 是否以某种方式接地？
    boolean hasGroundConnection(int n1) { return false; }
    
    // 这是导线还是等效于导线？
    boolean isWire() { return false; }
    
    boolean canViewInScope() { return getPostCount() <= 2; }
    boolean comparePair(int x1, int x2, int y1, int y2) {
	return ((x1 == y1 && x2 == y2) || (x1 == y2 && x2 == y1));
    }
    boolean needsHighlight() { 
	return mouseElmRef==this || selected || sim.plotYElm == this ||
		// 测试当前 mouseElm 是否为 ScopeElm，如果是，它是否属于此元件
		(mouseElmRef instanceof ScopeElm && ((ScopeElm) mouseElmRef).elmScope.getElm()==this); 
    }
    boolean isSelected() { return selected; }
    boolean canShowValueInScope(int v) { return false; }
    void setSelected(boolean x) { selected = x; }
    void selectRect(Rectangle r) {
	selected = r.intersects(boundingBox);
    }
    static int abs(int x) { return x < 0 ? -x : x; }
    static int sign(int x) { return (x < 0) ? -1 : (x == 0) ? 0 : 1; }
    static int min(int a, int b) { return (a < b) ? a : b; }
    static int max(int a, int b) { return (a > b) ? a : b; }
    static double distance(Point p1, Point p2) {
	double x = p1.x-p2.x;
	double y = p1.y-p2.y;
	return Math.sqrt(x*x+y*y);
    }
    Rectangle getBoundingBox() { return boundingBox; }
    boolean needsShortcut() { return getShortcut() > 0; }
    int getShortcut() { return 0; }

    boolean isGraphicElmt() { return false; }
    
    void setMouseElm(boolean v) {
	if (v)
	    mouseElmRef=this;
	else if (mouseElmRef==this)
	    mouseElmRef=null;
    }
    void draggingDone() {}
    
    String dumpModel() { return null; }
    
    boolean isMouseElm() {
	return mouseElmRef==this; 
    }
    
    void updateModels() {}
    void stepFinished() {}
    
    double getCurrentIntoNode(int n) {
	// 如果去掉 getPostCount() == 2 这个条件，对电源轨会得出错误的值
	if (n==0 && getPostCount() == 2)
	    return -current;
	else
	    return current;
    }
    
    void flipPosts() {
	int oldx = x;
	int oldy = y;
	x = x2;
	y = y2;
	x2 = oldx;
	y2 = oldy;
	setPoints();
    }
}
