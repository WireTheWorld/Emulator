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

    class CapacitorElm extends CircuitElm {
	double capacitance;
	double compResistance, voltdiff;
	double initialVoltage;
	Point plate1[], plate2[];
	public static final int FLAG_BACK_EULER = 2;
	public CapacitorElm(int xx, int yy) {
	    super(xx, yy);
	    capacitance = 1e-5;
	    initialVoltage = 1e-3;
	}
	public CapacitorElm(int xa, int ya, int xb, int yb, int f,
			    StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    capacitance = new Double(st.nextToken()).doubleValue();
	    voltdiff = new Double(st.nextToken()).doubleValue();
	    initialVoltage = 1e-3;
	    try {
		initialVoltage = new Double(st.nextToken()).doubleValue();
	    } catch (Exception e) {}
	}
	boolean isTrapezoidal() { return (flags & FLAG_BACK_EULER) == 0; }
	void setNodeVoltage(int n, double c) {
	    super.setNodeVoltage(n, c);
	    voltdiff = volts[0]-volts[1];
	}
	void reset() {
	    super.reset();
	    current = curcount = curSourceValue = 0;
	    // 重置时给电容充入少量电荷，以启动振荡器
	    voltdiff = initialVoltage;
	}
	void shorted() {
	    super.reset();
	    voltdiff = current = curcount = curSourceValue = 0;
	}
	int getDumpType() { return 'c'; }
	String dump() {
	    return super.dump() + " " + capacitance + " " + voltdiff + " " + initialVoltage;
	}
	
	// 用于 PolarCapacitorElm
	Point platePoints[];
	
	void setPoints() {
	    super.setPoints();
	    double f = (dn/2-4)/dn;
	    // 计算引线
	    lead1 = interpPoint(point1, point2, f);
	    lead2 = interpPoint(point1, point2, 1-f);
	    // 计算极板
	    plate1 = newPointArray(2);
	    plate2 = newPointArray(2);
	    interpPoint2(point1, point2, plate1[0], plate1[1], f, 12);
	    interpPoint2(point1, point2, plate2[0], plate2[1], 1-f, 12);
	}
	
	void draw(Graphics g) {
	    int hs = 12;
	    setBbox(point1, point2, hs);
	    
	    // 绘制第一条引线和极板
	    setVoltageColor(g, volts[0]);
	    drawThickLine(g, point1, lead1);
	    setPowerColor(g, false);
	    drawThickLine(g, plate1[0], plate1[1]);
	    if (sim.powerCheckItem.getState())
		g.setColor(Color.gray);

	    // 绘制第二条引线和极板
	    setVoltageColor(g, volts[1]);
	    drawThickLine(g, point2, lead2);
	    setPowerColor(g, false);
	    if (platePoints == null)
		drawThickLine(g, plate2[0], plate2[1]);
	    else {
		int i;
		for (i = 0; i != 7; i++)
		    drawThickLine(g,  platePoints[i], platePoints[i+1]);
	    }
	    
	    updateDotCount();
	    if (sim.dragElm != this) {
		drawDots(g, point1, lead1, curcount);
		drawDots(g, point2, lead2, -curcount);
	    }
	    drawPosts(g);
	    if (sim.showValuesCheckItem.getState()) {
		String s = getShortUnitText(capacitance, "F");
		drawValues(g, s, hs);
	    }
	}
	void stamp() {
	    if (sim.dcAnalysisFlag) {
		// 求直流工作点时，将电容替换为 100M 电阻
		sim.stampResistor(nodes[0], nodes[1], 1e8);
		curSourceValue = 0;
		return;
	    }
	    
	    // 电容伴随模型采用梯形近似
	    // （诺顿等效）由一个电流源与一个电阻
	    // 并联组成。梯形法比后向欧拉法更精确，
	    // 但如果 RC 相对于时间步长较小，
	    // 则可能引起振荡行为。
	    if (isTrapezoidal())
		compResistance = sim.timeStep/(2*capacitance);
	    else
		compResistance = sim.timeStep/capacitance;
	    sim.stampResistor(nodes[0], nodes[1], compResistance);
	    sim.stampRightSide(nodes[0]);
	    sim.stampRightSide(nodes[1]);
	}
	void startIteration() {
	    if (isTrapezoidal())
		curSourceValue = -voltdiff/compResistance-current;
	    else
		curSourceValue = -voltdiff/compResistance;
	}
	void calculateCurrent() {
	    double voltdiff = volts[0] - volts[1];
	    if (sim.dcAnalysisFlag) {
		current = voltdiff/1e8;
		return;
	    }
	    // 我们检查 compResistance，因为此方法可能在
	    // stamp()（设置 compResistance）之前被调用，
	    // 否则会导致电流无穷大
	    if (compResistance > 0)
		current = voltdiff/compResistance + curSourceValue;
	}
	double curSourceValue;
	void doStep() {
	    if (sim.dcAnalysisFlag)
		return;
	    sim.stampCurrentSource(nodes[0], nodes[1], curSourceValue);
 	}
	void getInfo(String arr[]) {
	    arr[0] = "capacitor";
	    getBasicInfo(arr);
	    arr[3] = "C = " + getUnitText(capacitance, "F");
	    arr[4] = "P = " + getUnitText(getPower(), "W");
	    //double v = getVoltageDiff();
	    //arr[4] = "U = " + getUnitText(.5*capacitance*v*v, "J");
	}
	@Override
	String getScopeText(int v) {
	    return sim.LS("capacitor") + ", " + getUnitText(capacitance, "F");
	}
	public EditInfo getEditInfo(int n) {
	    if (n == 0)
		return new EditInfo("Capacitance (F)", capacitance, 0, 0);
	    if (n == 1) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Trapezoidal Approximation", isTrapezoidal());
		return ei;
	    }
	    if (n == 2)
		return new EditInfo("Initial Voltage (on Reset)", initialVoltage);
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 0 && ei.value > 0)
		capacitance = ei.value;
	    if (n == 1) {
		if (ei.checkbox.getState())
		    flags &= ~FLAG_BACK_EULER;
		else
		    flags |= FLAG_BACK_EULER;
	    }
	    if (n == 2)
		initialVoltage = ei.value;
	}
	int getShortcut() { return 'c'; }
	public double getCapacitance() { return capacitance; }
	public void setCapacitance(double c) { capacitance = c; }
    }
