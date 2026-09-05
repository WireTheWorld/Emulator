package com.lushprojects.circuitjs1.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;

// 带滑块的数值
public class Adjustable implements Command {
    CircuitElm elm;
    double minValue, maxValue;
    String sliderText;
    
    // 该滑块控制的 getEditInfo() 列表中数值的索引
    int editItem;
    
    Label label;
    Scrollbar slider;
    boolean settingValue;
    
    Adjustable(CircuitElm ce, int item) {
	minValue = 1;
	maxValue = 1000;
	elm = ce;
	editItem = item;
    }

    // 反序列化（undump）
    Adjustable(StringTokenizer st, CirSim sim) {
	int e = new Integer(st.nextToken()).intValue();
	if (e == -1)
	    return;
	elm = sim.getElm(e);
	editItem = new Integer(st.nextToken()).intValue();
	minValue = new Double(st.nextToken()).doubleValue();
	maxValue = new Double(st.nextToken()).doubleValue();
	sliderText = CustomLogicModel.unescape(st.nextToken());
    }
    
    void createSlider(CirSim sim) {
	double value = elm.getEditInfo(editItem).value;
	createSlider(sim, value);
    }

    void createSlider(CirSim sim, double value) {
        sim.addWidgetToVerticalPanel(label = new Label(sim.LS(sliderText)));
        label.addStyleName("topSpace");
        int intValue = (int) ((value-minValue)*100/(maxValue-minValue));
        sim.addWidgetToVerticalPanel(slider = new Scrollbar(Scrollbar.HORIZONTAL, intValue, 1, 0, 101, this, elm));
    }

    void setSliderValue(double value) {
        int intValue = (int) ((value-minValue)*100/(maxValue-minValue));
        settingValue = true; // 在 execute() 中不要递归地再次设置数值
        slider.setValue(intValue);
        settingValue = false;
    }
    
    public void execute() {
	elm.sim.analyzeFlag = true;
	if (settingValue)
	    return;
	EditInfo ei = elm.getEditInfo(editItem);
	ei.value = getSliderValue();
	elm.setEditValue(editItem, ei);
	elm.sim.repaint();
    }
    
    double getSliderValue() {
	return minValue + (maxValue-minValue)*slider.getValue()/100;
    }
    
    void deleteSlider(CirSim sim) {
        sim.removeWidgetFromVerticalPanel(label);
        sim.removeWidgetFromVerticalPanel(slider);
    }
    
    String dump() {
	return elm.sim.locateElm(elm) + " " + editItem + " " + minValue + " " + maxValue + " " + CustomLogicModel.escape(sliderText);
    }
}
