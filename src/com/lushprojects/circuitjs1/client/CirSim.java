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

// GWT conversion (c) 2015 by Iain Sharp



// 关于其背后的理论，请参阅 Electronic Circuit & System Simulation Methods by Pillage



import java.util.Vector;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.lang.Math;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.PopupPanel;
import static com.google.gwt.event.dom.client.KeyCodes.*;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.Navigator;

public class CirSim implements MouseDownHandler, MouseMoveHandler, MouseUpHandler,
ClickHandler, DoubleClickHandler, ContextMenuHandler, NativePreviewHandler,
MouseOutHandler, MouseWheelHandler {
    
    Random random;
    Button resetButton;
    Button runStopButton;
    Button dumpMatrixButton;
    MenuItem aboutItem;
    MenuItem importFromLocalFileItem, importFromTextItem,
    	exportAsUrlItem, exportAsLocalFileItem, exportAsTextItem, printItem, recoverItem, saveFileItem;
    MenuItem importFromDropboxItem;
    MenuItem undoItem, redoItem,
	cutItem, copyItem, pasteItem, selectAllItem, optionsItem;
    MenuBar optionsMenuBar;
    CheckboxMenuItem dotsCheckItem;
    CheckboxMenuItem voltsCheckItem;
    CheckboxMenuItem powerCheckItem;
    CheckboxMenuItem smallGridCheckItem;
    CheckboxMenuItem crossHairCheckItem;
    CheckboxMenuItem showValuesCheckItem;
    CheckboxMenuItem conductanceCheckItem;
    CheckboxMenuItem euroResistorCheckItem;
    CheckboxMenuItem euroGatesCheckItem;
    CheckboxMenuItem printableCheckItem;
    CheckboxMenuItem conventionCheckItem;
    CheckboxMenuItem noEditCheckItem;
    private Label powerLabel;
    private Label titleLabel;
    private Scrollbar speedBar;
    private Scrollbar currentBar;
    private Scrollbar powerBar;
    MenuBar elmMenuBar;
    MenuItem elmEditMenuItem;
    MenuItem elmCutMenuItem;
    MenuItem elmCopyMenuItem;
    MenuItem elmDeleteMenuItem;
    MenuItem elmScopeMenuItem;
    MenuItem elmFloatScopeMenuItem;
    MenuItem elmAddScopeMenuItem;
    MenuItem elmFlipMenuItem;
    MenuItem elmSplitMenuItem;
    MenuItem elmSliderMenuItem;
    MenuItem stackAllItem;
    MenuItem unstackAllItem;
    MenuItem combineAllItem;
    MenuItem separateAllItem;
    MenuBar mainMenuBar;
    MenuBar selectScopeMenuBar;
    MenuItem scopeRemovePlotMenuItem;
    MenuItem scopeSelectYMenuItem;
    ScopePopupMenu scopePopupMenu;
    static HashMap<String,String> localizationMap;
   
    String lastCursorStyle;
    boolean mouseWasOverSplitter = false; 

//    Class addingClass;
    PopupPanel contextPanel = null;
    int mouseMode = MODE_SELECT;
    int tempMouseMode = MODE_SELECT;
    String mouseModeStr = "Select";
    static final double pi = 3.14159265358979323846;
    static final int MODE_ADD_ELM = 0;
    static final int MODE_DRAG_ALL = 1;
    static final int MODE_DRAG_ROW = 2;
    static final int MODE_DRAG_COLUMN = 3;
    static final int MODE_DRAG_SELECTED = 4;
    static final int MODE_DRAG_POST = 5;
    static final int MODE_SELECT = 6;
    static final int MODE_DRAG_SPLITTER = 7;
    static final int infoWidth = 120;
    long myframes =1;
    long mytime=0;
    long myruntime=0;
    long mydrawtime=0;
    int dragGridX, dragGridY, dragScreenX, dragScreenY, initDragGridX, initDragGridY;
    long mouseDownTime;
    long zoomTime;
    int mouseCursorX = -1;
    int mouseCursorY = -1;
    Rectangle selectedArea;
    int gridSize, gridMask, gridRound;
    boolean dragging;
    boolean analyzeFlag;
    boolean dumpMatrix;
    boolean dcAnalysisFlag;
 //   boolean useBufferedImage;
    boolean isMac;
    String ctrlMetaKey;
    double t;
    int pause = 10;
    int scopeSelected = -1;
    int menuScope = -1;
    int menuPlot = -1;
    int hintType = -1, hintItem1, hintItem2;
    String stopMessage;
    
    // 当前时间步长（两次迭代之间的时间）
    double timeStep;
    
    // 最大时间步长（除非因收敛困难而减小，否则等于 timeStep）
    double maxTimeStep;
    double minTimeStep;
    
    // 自上次递增 timeStepCount 以来累计的时间
    double timeStepAccum;
    
    // 每次按 maxTimeStep 推进 t 时递增
    int timeStepCount;
    
    boolean adjustTimeStep;
    static final int HINT_LC = 1;
    static final int HINT_RC = 2;
    static final int HINT_3DB_C = 3;
    static final int HINT_TWINT = 4;
    static final int HINT_3DB_L = 5;
    Vector<CircuitElm> elmList;
    Vector<Adjustable> adjustables;
//    Vector setupList;
    CircuitElm dragElm, menuElm, stopElm;
    private CircuitElm mouseElm=null;
    boolean didSwitch = false;
    int mousePost = -1;
    CircuitElm plotXElm, plotYElm;
    int draggingPost;
    SwitchElm heldSwitchElm;
    double circuitMatrix[][], circuitRightSide[], lastNodeVoltages[], nodeVoltages[],
	origRightSide[], origMatrix[][];
    RowInfo circuitRowInfo[];
    int circuitPermute[];
    boolean simRunning;
    boolean circuitNonLinear;
    int voltageSourceCount;
    int circuitMatrixSize, circuitMatrixFullSize;
    boolean circuitNeedsMap;
 //   public boolean useFrame;
    int scopeCount;
    Scope scopes[];
    boolean showResistanceInVoltageSources;
   int scopeColCount[];
    static EditDialog editDialog, customLogicEditDialog, diodeModelEditDialog;
    static SliderDialog sliderDialog;
    static ImportFromDropbox importFromDropbox;
    static ScrollValuePopup scrollValuePopup;
    static DialogBox dialogShowing;
    static AboutBox aboutBox;
    static ImportFromDropboxDialog importFromDropboxDialog;
//    Class dumpTypes[], shortcuts[];
    String shortcuts[];
    static String muString = "\u03bc";
    static String ohmString = "\u03a9";
    String clipboard;
    String recovery;
    Rectangle circuitArea;
    Vector<String> undoStack, redoStack;
    double transform[];
    boolean unsavedChanges;

	DockLayoutPanel layoutPanel;
	MenuBar menuBar;
	MenuBar fileMenuBar;
	VerticalPanel verticalPanel;
	CellPanel buttonPanel;
	private boolean mouseDragging;
	double scopeHeightFraction=0.2;
	
	Vector<CheckboxMenuItem> mainMenuItems = new Vector<CheckboxMenuItem>();
	Vector<String> mainMenuItemNames = new Vector<String>();

	LoadFile loadFileInput;
	Frame iFrame;
	
    Canvas cv;
    Context2d cvcontext;
    Canvas backcv;
    Context2d backcontext;
    static final int MENUBARHEIGHT=30;
    static int VERTICALPANELWIDTH=166; // 默认值
    static final int POSTGRABSQ=25;
    static final int MINPOSTGRABSIZE = 256;
    final Timer timer = new Timer() {
	      public void run() {
	        updateCircuit();
	      }
	    };
	 final int FASTTIMER=16;
    
	int getrand(int x) {
		int q = random.nextInt();
		if (q < 0)
			q = -q;
		return q % x;
	}
	
	
    public void setCanvasSize(){
    	int width, height;
    	width=(int)RootLayoutPanel.get().getOffsetWidth();
    	height=(int)RootLayoutPanel.get().getOffsetHeight();
    	height=height-MENUBARHEIGHT;
    	width=width-VERTICALPANELWIDTH;
		if (cv != null) {
			cv.setWidth(width + "PX");
			cv.setHeight(height + "PX");
			cv.setCoordinateSpaceWidth(width);
			cv.setCoordinateSpaceHeight(height);
		}
		if (backcv != null) {
			backcv.setWidth(width + "PX");
			backcv.setHeight(height + "PX");
			backcv.setCoordinateSpaceWidth(width);
			backcv.setCoordinateSpaceHeight(height);
		}

    	setCircuitArea();
    }
    
    void setCircuitArea() {
    	int height = cv.getCanvasElement().getHeight();
    	int width = cv.getCanvasElement().getWidth();
		int h = (int) ((double)height * scopeHeightFraction);
		/*if (h < 128 && winSize.height > 300)
		  h = 128;*/
		circuitArea = new Rectangle(0, 0, width, height-h);
    }
    
    native String decompress(String dump) /*-{
        return $wnd.LZString.decompressFromEncodedURIComponent(dump);
    }-*/;

//    Circuit applet;

    CirSim() {
//	super("Circuit Simulator v1.6d");
//	applet = a;
//	useFrame = false;
	theSim = this;
    }

    String startCircuit = null;
    String startLabel = null;
    String startCircuitText = null;
    String startCircuitLink = null;
//    String baseURL = "http://www.falstad.com/circuit/";
    
    public void init() {


	boolean printable = false;
	boolean convention = true;
	boolean euroRes = false;
	boolean usRes = false;
	boolean running = true;
	boolean hideSidebar = false;
	boolean hideMenu = false;
	boolean noEditing = false;
	MenuBar m;

	CircuitElm.initClass(this);
	readRecovery();

	QueryParameters qp = new QueryParameters();
	String positiveColor = null;
	String negativeColor = null;

	try {
	    //baseURL = applet.getDocumentBase().getFile();
	    // 查找嵌入在 URL 中的电路
	    //		String doc = applet.getDocumentBase().toString();
	    String cct=qp.getValue("cct");
	    if (cct!=null)
		startCircuitText = cct.replace("%24", "$");
	    String ctz=qp.getValue("ctz");
	    if (ctz!= null)
		startCircuitText = decompress(ctz);
	    startCircuit = qp.getValue("startCircuit");
	    startLabel   = qp.getValue("startLabel");
	    startCircuitLink = qp.getValue("startCircuitLink");
	    euroRes = qp.getBooleanValue("euroResistors", false);
	    usRes = qp.getBooleanValue("usResistors",  false);
	    running = qp.getBooleanValue("running", true);
	    hideSidebar = qp.getBooleanValue("hideSidebar", false);
	    hideMenu = qp.getBooleanValue("hideMenu", false);
	    printable = qp.getBooleanValue("whiteBackground", getOptionFromStorage("whiteBackground", false));
	    convention = qp.getBooleanValue("conventionalCurrent",
		    getOptionFromStorage("conventionalCurrent", true));
	    noEditing = !qp.getBooleanValue("editable", true);
	    positiveColor = qp.getValue("positiveColor");
	    negativeColor = qp.getValue("negativeColor");
	} catch (Exception e) { }

	boolean euroSetting = false;
	if (euroRes)
	    euroSetting = true;
	else if (usRes)
	    euroSetting = false;
	else
	    euroSetting = getOptionFromStorage("euroResistors", !weAreInUS());
	boolean euroGates = getOptionFromStorage("euroGates", weAreInGermany());

	transform = new double[6];
	String os = Navigator.getPlatform();
	isMac = (os.toLowerCase().contains("mac"));
	ctrlMetaKey = (isMac) ? "Cmd" : "Ctrl";

	shortcuts = new String[127];

	layoutPanel = new DockLayoutPanel(Unit.PX);

	fileMenuBar = new MenuBar(true);
	if (isElectron())
	    fileMenuBar.addItem(iconMenuItem("clone", "New Window...", new MyCommand("file", "newwindow")));
	fileMenuBar.addItem(LS("New Blank Circuit"), new MyCommand("file", "newblankcircuit"));
	importFromLocalFileItem = iconMenuItem("folder", "Open File...", new MyCommand("file","importfromlocalfile"));
	importFromLocalFileItem.setEnabled(LoadFile.isSupported());
	fileMenuBar.addItem(importFromLocalFileItem);
	importFromTextItem = iconMenuItem("doc-text", "Import From Text...", new MyCommand("file","importfromtext"));
	fileMenuBar.addItem(importFromTextItem);
	importFromDropboxItem = iconMenuItem("dropbox", "Import From Dropbox...", new MyCommand("file", "importfromdropbox"));
	fileMenuBar.addItem(importFromDropboxItem);
	if (isElectron()) {
	    saveFileItem = fileMenuBar.addItem(iconMenuItem("floppy", "Save", new MyCommand("file", "save")));
	    fileMenuBar.addItem(iconMenuItem("floppy", "Save As...", new MyCommand("file", "saveas")));
	} else {
	    exportAsLocalFileItem = iconMenuItem("floppy", "Save As...", new MyCommand("file","exportaslocalfile"));
	    exportAsLocalFileItem.setEnabled(ExportAsLocalFileDialog.downloadIsSupported());
	    fileMenuBar.addItem(exportAsLocalFileItem);
	}
	exportAsUrlItem = iconMenuItem("export", "Export As Link...", new MyCommand("file","exportasurl"));
	fileMenuBar.addItem(exportAsUrlItem);
	exportAsTextItem = iconMenuItem("export", "Export As Text...", new MyCommand("file","exportastext"));
	fileMenuBar.addItem(exportAsTextItem);
	fileMenuBar.addItem(iconMenuItem("export", "Export As Image...", new MyCommand("file","exportasimage")));
	fileMenuBar.addItem(iconMenuItem("microchip", "Create Subcircuit...", new MyCommand("file","createsubcircuit")));
	fileMenuBar.addItem(iconMenuItem("magic", "Find DC Operating Point", new MyCommand("file", "dcanalysis")));
	recoverItem = iconMenuItem("back-in-time", "Recover Auto-Save", new MyCommand("file","recover"));
	recoverItem.setEnabled(recovery != null);
	fileMenuBar.addItem(recoverItem);
	printItem = iconMenuItem("print", "Print...", new MyCommand("file","print"));
	fileMenuBar.addItem(printItem);
	fileMenuBar.addSeparator();
	fileMenuBar.addItem(LS("Toggle Full Screen"), new MyCommand("view", "fullscreen"));
	fileMenuBar.addSeparator();
	aboutItem = iconMenuItem("info-circled", "About...", (Command)null);
	fileMenuBar.addItem(aboutItem);
	aboutItem.setScheduledCommand(new MyCommand("file","about"));

	int width=(int)RootLayoutPanel.get().getOffsetWidth();
	VERTICALPANELWIDTH = width/5;
	if (VERTICALPANELWIDTH > 166)
	    VERTICALPANELWIDTH = 166;
	if (VERTICALPANELWIDTH < 128)
	    VERTICALPANELWIDTH = 128;

	menuBar = new MenuBar();
	menuBar.addItem(LS("File"), fileMenuBar);
	verticalPanel=new VerticalPanel();

	// 如果有空间，则并排放置按钮
	buttonPanel=(VERTICALPANELWIDTH == 166) ? new HorizontalPanel() : new VerticalPanel();

	m = new MenuBar(true);
	m.addItem(undoItem = menuItemWithShortcut("ccw", LS("Undo"), LS("Ctrl-Z"), new MyCommand("edit","undo")));
	m.addItem(redoItem = menuItemWithShortcut("cw", LS("Redo"), LS("Ctrl-Y"), new MyCommand("edit","redo")));
	m.addSeparator();
	m.addItem(cutItem = menuItemWithShortcut("scissors", LS("Cut"), LS("Ctrl-X"), new MyCommand("edit","cut")));
	m.addItem(copyItem = menuItemWithShortcut("copy", LS("Copy"), LS("Ctrl-C"), new MyCommand("edit","copy")));
	m.addItem(pasteItem = menuItemWithShortcut("paste", LS("Paste"), LS("Ctrl-V"), new MyCommand("edit","paste")));
	pasteItem.setEnabled(false);

	m.addItem(menuItemWithShortcut("clone", LS("Duplicate"), LS("Ctrl-D"), new MyCommand("edit","duplicate")));

	m.addSeparator();
	m.addItem(selectAllItem = menuItemWithShortcut("select-all", LS("Select All"), LS("Ctrl-A"), new MyCommand("edit","selectAll")));
	m.addSeparator();
	m.addItem(iconMenuItem("target", weAreInUS() ? "Center Circuit" : "Centre Circuit", new MyCommand("edit", "centrecircuit")));
	m.addItem(menuItemWithShortcut("zoom-11", LS("Zoom 100%"), "0", new MyCommand("zoom", "zoom100")));
	m.addItem(menuItemWithShortcut("zoom-in", LS("Zoom In"), "+", new MyCommand("zoom", "zoomin")));
	m.addItem(menuItemWithShortcut("zoom-out", LS("Zoom Out"), "-", new MyCommand("zoom", "zoomout")));
	menuBar.addItem(LS("Edit"),m);

	MenuBar drawMenuBar = new MenuBar(true);
	drawMenuBar.setAutoOpen(true);

	menuBar.addItem(LS("Draw"), drawMenuBar);

	m = new MenuBar(true);
	m.addItem(stackAllItem = iconMenuItem("lines", "Stack All", new MyCommand("scopes", "stackAll")));
	m.addItem(unstackAllItem = iconMenuItem("columns", "Unstack All", new MyCommand("scopes", "unstackAll")));
	m.addItem(combineAllItem = iconMenuItem("object-group", "Combine All", new MyCommand("scopes", "combineAll")));
	m.addItem(separateAllItem = iconMenuItem("object-ungroup", "Separate All", new MyCommand("scopes", "separateAll")));
	menuBar.addItem(LS("Scopes"), m);

	optionsMenuBar = m = new MenuBar(true );
	menuBar.addItem(LS("Options"), optionsMenuBar);
	m.addItem(dotsCheckItem = new CheckboxMenuItem(LS("Show Current")));
	dotsCheckItem.setState(true);
	m.addItem(voltsCheckItem = new CheckboxMenuItem(LS("Show Voltage"),
		new Command() { public void execute(){
		    if (voltsCheckItem.getState())
			powerCheckItem.setState(false);
		    setPowerBarEnable();
		}
	}));
	voltsCheckItem.setState(true);
	m.addItem(powerCheckItem = new CheckboxMenuItem(LS("Show Power"),
		new Command() { public void execute(){
		    if (powerCheckItem.getState())
			voltsCheckItem.setState(false);
		    setPowerBarEnable();
		}
	}));
	m.addItem(showValuesCheckItem = new CheckboxMenuItem(LS("Show Values")));
	showValuesCheckItem.setState(true);
	//m.add(conductanceCheckItem = getCheckItem(LS("Show Conductance")));
	m.addItem(smallGridCheckItem = new CheckboxMenuItem(LS("Small Grid"),
		new Command() { public void execute(){
		    setGrid();
		}
	}));
	m.addItem(crossHairCheckItem = new CheckboxMenuItem(LS("Show Cursor Cross Hairs"),
		new Command() { public void execute(){
		    setOptionInStorage("crossHair", crossHairCheckItem.getState());
		}
	}));
	crossHairCheckItem.setState(getOptionFromStorage("crossHair", false));
	m.addItem(euroResistorCheckItem = new CheckboxMenuItem(LS("European Resistors"),
		new Command() { public void execute(){
		    setOptionInStorage("euroResistors", euroResistorCheckItem.getState());
		}
	}));
	euroResistorCheckItem.setState(euroSetting);
	m.addItem(euroGatesCheckItem = new CheckboxMenuItem(LS("IEC Gates"),
		new Command() { public void execute(){
		    setOptionInStorage("euroGates", euroGatesCheckItem.getState());
		    int i;
		    for (i = 0; i != elmList.size(); i++)
			getElm(i).setPoints();
		}
	}));
	euroGatesCheckItem.setState(euroGates);
	m.addItem(printableCheckItem = new CheckboxMenuItem(LS("White Background"),
		new Command() { public void execute(){
		    int i;
		    for (i=0;i<scopeCount;i++)
			scopes[i].setRect(scopes[i].rect);
		    setOptionInStorage("whiteBackground", printableCheckItem.getState());
		}
	}));
	printableCheckItem.setState(printable);

	m.addItem(conventionCheckItem = new CheckboxMenuItem(LS("Conventional Current Motion"),
		new Command() { public void execute(){
		    setOptionInStorage("conventionalCurrent", conventionCheckItem.getState());
		}
	}));
	conventionCheckItem.setState(convention);
	m.addItem(noEditCheckItem = new CheckboxMenuItem(LS("Disable Editing")));
	noEditCheckItem.setState(noEditing);


	m.addItem(new CheckboxAlignedMenuItem(LS("Shortcuts..."), new MyCommand("options", "shortcuts")));
	m.addItem(optionsItem = new CheckboxAlignedMenuItem(LS("Other Options..."), new MyCommand("options","other")));
	if (isElectron())
	    m.addItem(new CheckboxAlignedMenuItem(LS("Toggle Dev Tools"), new MyCommand("options","devtools")));

	mainMenuBar = new MenuBar(true);
	mainMenuBar.setAutoOpen(true);
	composeMainMenu(mainMenuBar);
	composeMainMenu(drawMenuBar);
	loadShortcuts();

	if (!hideMenu)
	    layoutPanel.addNorth(menuBar, MENUBARHEIGHT);

	if (hideSidebar)
	    VERTICALPANELWIDTH = 0;
	else
	    layoutPanel.addEast(verticalPanel, VERTICALPANELWIDTH);
	RootLayoutPanel.get().add(layoutPanel);

	cv =Canvas.createIfSupported();
	if (cv==null) {
	    RootPanel.get().add(new Label("Not working. You need a browser that supports the CANVAS element."));
	    return;
	}



	cvcontext=cv.getContext2d();
	backcv=Canvas.createIfSupported();
	backcontext=backcv.getContext2d();
	setCanvasSize();
	layoutPanel.add(cv);
	verticalPanel.add(buttonPanel);
	buttonPanel.add(resetButton = new Button(LS("Reset")));
	resetButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		resetAction();
	    }
	});
	resetButton.setStylePrimaryName("topButton");
	buttonPanel.add(runStopButton = new Button(LSHTML("<Strong>RUN</Strong>&nbsp;/&nbsp;Stop")));
	runStopButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		setSimRunning(!simIsRunning());
	    }
	});

	/*
	dumpMatrixButton = new Button("Dump Matrix");
	dumpMatrixButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) { dumpMatrix = true; }});
	verticalPanel.add(dumpMatrixButton);// IES for debugging
	 */

	if (LoadFile.isSupported())
	    verticalPanel.add(loadFileInput = new LoadFile(this));

	Label l;
	verticalPanel.add(l = new Label(LS("Simulation Speed")));
	l.addStyleName("topSpace");

	// 原来最大为 140
	verticalPanel.add( speedBar = new Scrollbar(Scrollbar.HORIZONTAL, 3, 1, 0, 260));

	verticalPanel.add( l = new Label(LS("Current Speed")));
	l.addStyleName("topSpace");
	currentBar = new Scrollbar(Scrollbar.HORIZONTAL, 50, 1, 1, 100);
	verticalPanel.add(currentBar);
	verticalPanel.add(powerLabel = new Label (LS("Power Brightness")));
	powerLabel.addStyleName("topSpace");
	verticalPanel.add(powerBar = new Scrollbar(Scrollbar.HORIZONTAL,
		50, 1, 1, 100));
	setPowerBarEnable();

	//	verticalPanel.add(new Label(""));
	//        Font f = new Font("SansSerif", 0, 10);
	l = new Label(LS("Current Circuit:"));
	l.addStyleName("topSpace");
	//        l.setFont(f);
	titleLabel = new Label("Label");
	//        titleLabel.setFont(f);
	verticalPanel.add(l);
	verticalPanel.add(titleLabel);

	verticalPanel.add(iFrame = new Frame("iframe.html"));
	iFrame.setWidth(VERTICALPANELWIDTH+"px");
	iFrame.setHeight("100 px");
	iFrame.getElement().setAttribute("scrolling", "no");

	setGrid();
	elmList = new Vector<CircuitElm>();
	adjustables = new Vector<Adjustable>();
	//	setupList = new Vector();
	undoStack = new Vector<String>();
	redoStack = new Vector<String>();


	scopes = new Scope[20];
	scopeColCount = new int[20];
	scopeCount = 0;

	random = new Random();
	//	cv.setBackground(Color.black);
	//	cv.setForeground(Color.lightGray);

	elmMenuBar = new MenuBar(true);
	elmMenuBar.setAutoOpen(true);
	selectScopeMenuBar = new MenuBar(true);
	elmMenuBar.addItem(elmEditMenuItem = new MenuItem(LS("Edit..."),new MyCommand("elm","edit")));
	elmMenuBar.addItem(elmScopeMenuItem = new MenuItem(LS("View in New Scope"), new MyCommand("elm","viewInScope")));
	elmMenuBar.addItem(elmFloatScopeMenuItem  = new MenuItem(LS("View in New Undocked Scope"), new MyCommand("elm","viewInFloatScope")));
	elmMenuBar.addItem(elmAddScopeMenuItem = new MenuItem(LS("Add to Existing Scope"), new MyCommand("elm", "addToScope0")));
	elmMenuBar.addItem(elmCutMenuItem = new MenuItem(LS("Cut"),new MyCommand("elm","cut")));
	elmMenuBar.addItem(elmCopyMenuItem = new MenuItem(LS("Copy"),new MyCommand("elm","copy")));
	elmMenuBar.addItem(elmDeleteMenuItem = new MenuItem(LS("Delete"),new MyCommand("elm","delete")));
	elmMenuBar.addItem(                    new MenuItem(LS("Duplicate"),new MyCommand("elm","duplicate")));
	elmMenuBar.addItem(elmFlipMenuItem = new MenuItem(LS("Swap Terminals"),new MyCommand("elm","flip")));
	elmMenuBar.addItem(elmSplitMenuItem = menuItemWithShortcut("", LS("Split Wire"), LS(ctrlMetaKey + "-click"), new MyCommand("elm","split")));
	elmMenuBar.addItem(elmSliderMenuItem = new MenuItem(LS("Sliders..."),new MyCommand("elm","sliders")));

	scopePopupMenu = new ScopePopupMenu();

	setColors(positiveColor, negativeColor);

	if (startCircuitText != null) {
	    getSetupList(false);
	    readCircuit(startCircuitText);
	    unsavedChanges = false;
	} else {
	    if (stopMessage == null && startCircuitLink!=null) {
		readCircuit("");
		getSetupList(false);
		ImportFromDropboxDialog.setSim(this);
		ImportFromDropboxDialog.doImportDropboxLink(startCircuitLink, false);
	    } else {
		readCircuit("");
		if (stopMessage == null && startCircuit != null) {
		    getSetupList(false);
		    readSetupFile(startCircuit, startLabel);
		}
		else
		    getSetupList(true);
	    }
	}




	enableUndoRedo();
	enablePaste();
	setiFrameHeight();
	cv.addMouseDownHandler(this);
	cv.addMouseMoveHandler(this);
	cv.addMouseOutHandler(this);
	cv.addMouseUpHandler(this);
	cv.addClickHandler(this);
	cv.addDoubleClickHandler(this);
	doTouchHandlers(cv.getCanvasElement());
	cv.addDomHandler(this, ContextMenuEvent.getType());	
	menuBar.addDomHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		doMainMenuChecks();
	    }
	}, ClickEvent.getType());	
	Event.addNativePreviewHandler(this);
	cv.addMouseWheelHandler(this);

	Window.addWindowClosingHandler(new Window.ClosingHandler() {
	    public void onWindowClosing(ClosingEvent event) {
		// electron 中存在一个 bug：如果给出此警告，应用将无法关闭
		if (unsavedChanges && !isElectron())
		    event.setMessage(LS("Are you sure?  There are unsaved changes."));
	    }
	});


	
	
	setSimRunning(running);
    }

    void setColors(String positiveColor, String negativeColor) {
        Storage stor = Storage.getLocalStorageIfSupported();
        if (stor != null) {
            if (positiveColor == null)
        	positiveColor = stor.getItem("positiveColor");
            if (negativeColor == null)
        	negativeColor = stor.getItem("negativeColor");
        }
	if (positiveColor != null)
	    CircuitElm.positiveColor = new Color(URL.decodeQueryString(positiveColor));
	else if (getOptionFromStorage("alternativeColor", false))
	    CircuitElm.positiveColor = Color.blue;
	if (negativeColor != null)
	    CircuitElm.negativeColor = new Color(URL.decodeQueryString(negativeColor));
	    
	CircuitElm.setColorScale();
    }
    
    MenuItem menuItemWithShortcut(String icon, String text, String shortcut, MyCommand cmd) {
	final String edithtml="<div style=\"white-space:nowrap\"><div style=\"display:inline-block;width:110px;\"><i class=\"cirjsicon-";
	String nbsp = "&nbsp;";
	if (icon=="") nbsp="";
	String sn=edithtml + icon + "\"></i>" + nbsp + text + "</div>" + shortcut + "</div>";
	return new MenuItem(SafeHtmlUtils.fromTrustedString(sn), cmd);
    }
    
    MenuItem iconMenuItem(String icon, String text, Command cmd) {
        String icoStr = "<i class=\"cirjsicon-" + icon + "\"></i>&nbsp;" + LS(text); //<i class="cirjsicon-"></i>&nbsp;
        return new MenuItem(SafeHtmlUtils.fromTrustedString(icoStr), cmd);
    }
    
    boolean getOptionFromStorage(String key, boolean val) {
        Storage stor = Storage.getLocalStorageIfSupported();
        if (stor == null)
            return val;
        String s = stor.getItem(key);
        if (s == null)
            return val;
        return s == "true";
    }

    void setOptionInStorage(String key, boolean val) {
        Storage stor = Storage.getLocalStorageIfSupported();
        if (stor == null)
            return;
        stor.setItem(key,  val ? "true" : "false");
    }
    
    // 将快捷键保存到本地存储
    void saveShortcuts() {
        Storage stor = Storage.getLocalStorageIfSupported();
        if (stor == null)
            return;
        String str = "1";
        int i;
        // 格式：version;code1=ClassName;code2=ClassName;等等
        for (i = 0; i != shortcuts.length; i++) {
            String sh = shortcuts[i];
            if (sh == null)
        		continue;
            str += ";" + i + "=" + sh;
        }
        stor.setItem("shortcuts", str);
    }
    
    // 从本地存储加载快捷键
    void loadShortcuts() {
        Storage stor = Storage.getLocalStorageIfSupported();
        if (stor == null)
            return;
        String str = stor.getItem("shortcuts");
        if (str == null)
            return;
        String keys[] = str.split(";");
        
        // 清除现有快捷键
        int i;
        for (i = 0; i != shortcuts.length; i++)
            shortcuts[i] = null;
        
        // 清除菜单中的快捷键
        for (i = 0; i != mainMenuItems.size(); i++) {
            CheckboxMenuItem item = mainMenuItems.get(i);
            // 遇到拖拽菜单项时停止
            if (item.getShortcut().length() > 1)
        		break;
            item.setShortcut("");
        }
        
        // 遍历键（跳过开头的版本号）
        for (i = 1; i < keys.length; i++) {
            String arr[] = keys[i].split("=");
            if (arr.length != 2)
        	continue;
            int c = Integer.parseInt(arr[0]);
            String className = arr[1];
            shortcuts[c] = className;
            
            // 查找菜单项并修正它
            int j;
            for (j = 0; j != mainMenuItems.size(); j++) {
        		if (mainMenuItemNames.get(j) == className) {
        		    CheckboxMenuItem item = mainMenuItems.get(j);
        		    item.setShortcut(Character.toString((char)c));
        		    break;
        		}
            }
        }
    }
    
    // 安装触摸事件处理器
    // 不想用 java 重写这段代码。况且，java 也不允许我们创建鼠标事件
    // 并分发它们。
    native void doTouchHandlers(CanvasElement cv) /*-{
	// 为移动设备等设置触摸事件
	var lastTap;
	var tmout;
	var sim = this;
	cv.addEventListener("touchstart", function (e) {
        	mousePos = getTouchPos(cv, e);
  		var touch = e.touches[0];
  		var etype = "mousedown";
  		clearTimeout(tmout);
  		if (e.timeStamp-lastTap < 300) {
     		    etype = "dblclick";
  		} else {
  		    tmout = setTimeout(function() {
  		        sim.@com.lushprojects.circuitjs1.client.CirSim::longPress()();
  		    }, 500);
  		}
  		lastTap = e.timeStamp;
  		
  		var mouseEvent = new MouseEvent(etype, {
    			clientX: touch.clientX,
    			clientY: touch.clientY
  		});
  		e.preventDefault();
  		cv.dispatchEvent(mouseEvent);
	}, false);
	cv.addEventListener("touchend", function (e) {
  		var mouseEvent = new MouseEvent("mouseup", {});
  		e.preventDefault();
  		clearTimeout(tmout);
  		cv.dispatchEvent(mouseEvent);
	}, false);
	cv.addEventListener("touchmove", function (e) {
  		var touch = e.touches[0];
  		var mouseEvent = new MouseEvent("mousemove", {
    			clientX: touch.clientX,
    			clientY: touch.clientY
  		});
  		e.preventDefault();
  		clearTimeout(tmout);
  		cv.dispatchEvent(mouseEvent);
	}, false);

	// 获取触摸点相对于画布的位置
	function getTouchPos(canvasDom, touchEvent) {
  		var rect = canvasDom.getBoundingClientRect();
  		return {
    			x: touchEvent.touches[0].clientX - rect.left,
    			y: touchEvent.touches[0].clientY - rect.top
  		};
	}
	
    }-*/;
    
    boolean shown = false;
    
    public void composeMainMenu(MenuBar mainMenuBar) {
    	mainMenuBar.addItem(getClassCheckItem(LS("Add Wire"), "WireElm"));
    	mainMenuBar.addItem(getClassCheckItem(LS("Add Resistor"), "ResistorElm"));

    	MenuBar passMenuBar = new MenuBar(true);
    	passMenuBar.addItem(getClassCheckItem(LS("Add Capacitor"), "CapacitorElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Capacitor (polarized)"), "PolarCapacitorElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Inductor"), "InductorElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Switch"), "SwitchElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Push Switch"), "PushSwitchElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add SPDT Switch"), "Switch2Elm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Make-Before-Break Switch"), "MBBSwitchElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Potentiometer"), "PotElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Transformer"), "TransformerElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Tapped Transformer"), "TappedTransformerElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Transmission Line"), "TransLineElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Relay"), "RelayElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Memristor"), "MemristorElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Spark Gap"), "SparkGapElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Fuse"), "FuseElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Custom Transformer"), "CustomTransformerElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Crystal"), "CrystalElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Photoresistor"), "LDRElm"));
    	passMenuBar.addItem(getClassCheckItem(LS("Add Thermistor"), "ThermistorNTCElm"));
    	mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml+LS("&nbsp;</div>Passive Components")), passMenuBar);

    	MenuBar inputMenuBar = new MenuBar(true);
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Ground"), "GroundElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Voltage Source (2-terminal)"), "DCVoltageElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add A/C Voltage Source (2-terminal)"), "ACVoltageElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Voltage Source (1-terminal)"), "RailElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add A/C Voltage Source (1-terminal)"), "ACRailElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Square Wave Source (1-terminal)"), "SquareRailElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Clock"), "ClockElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add A/C Sweep"), "SweepElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Variable Voltage"), "VarRailElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Antenna"), "AntennaElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add AM Source"), "AMElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add FM Source"), "FMElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Current Source"), "CurrentElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Noise Generator"), "NoiseElm"));
    	inputMenuBar.addItem(getClassCheckItem(LS("Add Audio Input"), "AudioInputElm"));

    	mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml+LS("&nbsp;</div>Inputs and Sources")), inputMenuBar);
    	
    	MenuBar outputMenuBar = new MenuBar(true);
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Analog Output"), "OutputElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add LED"), "LEDElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Lamp"), "LampElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Text"), "TextElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Box"), "BoxElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Voltmeter/Scobe Probe"), "ProbeElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Ohmmeter"), "OhmMeterElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Labeled Node"), "LabeledNodeElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Test Point"), "TestPointElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Ammeter"), "AmmeterElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Data Export"), "DataRecorderElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Audio Output"), "AudioOutputElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add LED Array"), "LEDArrayElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add Stop Trigger"), "StopTriggerElm"));
    	outputMenuBar.addItem(getClassCheckItem(LS("Add DC Motor"), "DCMotorElm"));
    	mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml+LS("&nbsp;</div>Outputs and Labels")), outputMenuBar);
    	
    	MenuBar activeMenuBar = new MenuBar(true);
    	activeMenuBar.addItem(getClassCheckItem(LS("Add Diode"), "DiodeElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add Zener Diode"), "ZenerElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add Transistor (bipolar, NPN)"), "NTransistorElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add Transistor (bipolar, PNP)"), "PTransistorElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add MOSFET (N-Channel)"), "NMosfetElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add MOSFET (P-Channel)"), "PMosfetElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add JFET (N-Channel)"), "NJfetElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add JFET (P-Channel)"), "PJfetElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add SCR"), "SCRElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add DIAC"), "DiacElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add TRIAC"), "TriacElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add Darlington Pair (NPN)"), "NDarlingtonElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add Darlington Pair (PNP)"), "PDarlingtonElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add Varactor/Varicap"), "VaractorElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add Tunnel Diode"), "TunnelDiodeElm"));
    	activeMenuBar.addItem(getClassCheckItem(LS("Add Triode"), "TriodeElm"));
    	mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml+LS("&nbsp;</div>Active Components")), activeMenuBar);

    	MenuBar activeBlocMenuBar = new MenuBar(true);
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Op Amp (ideal, - on top)"), "OpAmpElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Op Amp (ideal, + on top)"), "OpAmpSwapElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Op Amp (real)"), "OpAmpRealElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Analog Switch (SPST)"), "AnalogSwitchElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Analog Switch (SPDT)"), "AnalogSwitch2Elm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Tristate Buffer"), "TriStateElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Schmitt Trigger"), "SchmittElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Schmitt Trigger (Inverting)"), "InvertingSchmittElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add CCII+"), "CC2Elm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add CCII-"), "CC2NegElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Comparator (Hi-Z/GND output)"), "ComparatorElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add OTA (LM13700 style)"), "OTAElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Voltage-Controlled Voltage Source"), "VCVSElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Voltage-Controlled Current Source"), "VCCSElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Current-Controlled Voltage Source"), "CCVSElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Current-Controlled Current Source"), "CCCSElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Optocoupler"), "OptocouplerElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Time Delay Relay"), "TimeDelayRelayElm"));
    	activeBlocMenuBar.addItem(getClassCheckItem(LS("Add Subcircuit Instance"), "CustomCompositeElm"));
    	mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml+LS("&nbsp;</div>Active Building Blocks")), activeBlocMenuBar);
    	
    	MenuBar gateMenuBar = new MenuBar(true);
    	gateMenuBar.addItem(getClassCheckItem(LS("Add Logic Input"), "LogicInputElm"));
    	gateMenuBar.addItem(getClassCheckItem(LS("Add Logic Output"), "LogicOutputElm"));
    	gateMenuBar.addItem(getClassCheckItem(LS("Add Inverter"), "InverterElm"));
    	gateMenuBar.addItem(getClassCheckItem(LS("Add NAND Gate"), "NandGateElm"));
    	gateMenuBar.addItem(getClassCheckItem(LS("Add NOR Gate"), "NorGateElm"));
    	gateMenuBar.addItem(getClassCheckItem(LS("Add AND Gate"), "AndGateElm"));
    	gateMenuBar.addItem(getClassCheckItem(LS("Add OR Gate"), "OrGateElm"));
    	gateMenuBar.addItem(getClassCheckItem(LS("Add XOR Gate"), "XorGateElm"));
    	mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml+LS("&nbsp;</div>Logic Gates, Input and Output")), gateMenuBar);

    	MenuBar chipMenuBar = new MenuBar(true);
    	chipMenuBar.addItem(getClassCheckItem(LS("Add D Flip-Flop"), "DFlipFlopElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add JK Flip-Flop"), "JKFlipFlopElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add T Flip-Flop"), "TFlipFlopElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add 7 Segment LED"), "SevenSegElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add 7 Segment Decoder"), "SevenSegDecoderElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Multiplexer"), "MultiplexerElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Demultiplexer"), "DeMultiplexerElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add SIPO shift register"), "SipoShiftElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add PISO shift register"), "PisoShiftElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Counter"), "CounterElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Ring Counter"), "DecadeElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Latch"), "LatchElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Sequence generator"), "SeqGenElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Full Adder"), "FullAdderElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Half Adder"), "HalfAdderElm"));
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Custom Logic"), "UserDefinedLogicElm")); // 不要改动这里，否则会破坏用户已保存的快捷键
    	chipMenuBar.addItem(getClassCheckItem(LS("Add Static RAM"), "SRAMElm"));
    	mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml+LS("&nbsp;</div>Digital Chips")), chipMenuBar);
    	
    	MenuBar achipMenuBar = new MenuBar(true);
    	achipMenuBar.addItem(getClassCheckItem(LS("Add 555 Timer"), "TimerElm"));
    	achipMenuBar.addItem(getClassCheckItem(LS("Add Phase Comparator"), "PhaseCompElm"));
    	achipMenuBar.addItem(getClassCheckItem(LS("Add DAC"), "DACElm"));
    	achipMenuBar.addItem(getClassCheckItem(LS("Add ADC"), "ADCElm"));
    	achipMenuBar.addItem(getClassCheckItem(LS("Add VCO"), "VCOElm"));
    	achipMenuBar.addItem(getClassCheckItem(LS("Add Monostable"), "MonostableElm"));
    	mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml+LS("&nbsp;</div>Analog and Hybrid Chips")), achipMenuBar);
    	
    	MenuBar otherMenuBar = new MenuBar(true);
    	CheckboxMenuItem mi;
    	otherMenuBar.addItem(mi=getClassCheckItem(LS("Drag All"), "DragAll"));
    	mi.setShortcut(LS("(Alt-drag)"));
    	otherMenuBar.addItem(mi=getClassCheckItem(LS("Drag Row"), "DragRow"));
    	mi.setShortcut(LS("(A-S-drag)"));
    	otherMenuBar.addItem(mi=getClassCheckItem(LS("Drag Column"), "DragColumn"));
    	mi.setShortcut(isMac ? LS("(A-Cmd-drag)") : LS("(A-M-drag)"));
    	otherMenuBar.addItem(getClassCheckItem(LS("Drag Selected"), "DragSelected"));
    	otherMenuBar.addItem(mi=getClassCheckItem(LS("Drag Post"), "DragPost"));
    	mi.setShortcut("(" + ctrlMetaKey + "-drag)");

    	mainMenuBar.addItem(SafeHtmlUtils.fromTrustedString(CheckboxMenuItem.checkBoxHtml+LS("&nbsp;</div>Drag")), otherMenuBar);

    	mainMenuBar.addItem(mi=getClassCheckItem(LS("Select/Drag Sel"), "Select"));
    	mi.setShortcut(LS("(space or Shift-drag)"));
    }
    
    public void composeSelectScopeMenu(MenuBar sb) {
	sb.clearItems();
	for( int i = 0; i < scopeCount; i++) {
	    String s, l;
	    s = LS("Scope")+" "+ Integer.toString(i+1);
	    l=scopes[i].getScopeLabelOrText();
	    if (l!="")
		s+=" ("+SafeHtmlUtils.htmlEscape(l)+")";
	    sb.addItem(new MenuItem(s ,new MyCommand("elm", "addToScope"+Integer.toString(i))));
	}
	int c = countScopeElms();
	for (int j = 0; j < c; j++) {
	    String s,l;
	    s = LS("Undocked Scope")+" "+ Integer.toString(j+1);
	    l = getNthScopeElm(j).elmScope.getScopeLabelOrText();
	    if (l!="")
		s += " ("+SafeHtmlUtils.htmlEscape(l)+")";
	    sb.addItem(new MenuItem(s, new MyCommand("elm", "addToScope"+Integer.toString(scopeCount+j))));
	}
    }
    
    public void setiFrameHeight() {
    	if (iFrame==null)
    		return;
    	int i;
    	int cumheight=0;
    	for (i=0; i < verticalPanel.getWidgetIndex(iFrame); i++) {
    		if (verticalPanel.getWidget(i) !=loadFileInput) {
    			cumheight=cumheight+verticalPanel.getWidget(i).getOffsetHeight();
    			if (verticalPanel.getWidget(i).getStyleName().contains("topSpace"))
    					cumheight+=12;
    		}
    	}
    	int ih=RootLayoutPanel.get().getOffsetHeight()-MENUBARHEIGHT-cumheight;
    	if (ih<0)
    		ih=0;
    	iFrame.setHeight(ih+"px");
    }
    


    


    CheckboxMenuItem getClassCheckItem(String s, String t) {
    	// try {
    	//   Class c = Class.forName(t);
    	String shortcut="";
    	CircuitElm elm = constructElement(t, 0, 0);
    	CheckboxMenuItem mi;
    	//  register(c, elm);
    	if ( elm!=null ) {
    		if (elm.needsShortcut() ) {
    			shortcut += (char)elm.getShortcut();
    			if (shortcuts[elm.getShortcut()] != null && !shortcuts[elm.getShortcut()].equals(t))
    			    console("already have shortcut for " + (char)elm.getShortcut() + " " + elm);
    			shortcuts[elm.getShortcut()]=t;
    		}
    		elm.delete();
    	}
//    	else
//    		GWT.log("Coudn't create class: "+t);
    	//	} catch (Exception ee) {
    	//	    ee.printStackTrace();
    	//	}
    	if (shortcut=="")
    		mi= new CheckboxMenuItem(s);
    	else
    		mi = new CheckboxMenuItem(s, shortcut);
    	mi.setScheduledCommand(new MyCommand("main", t) );
    	mainMenuItems.add(mi);
    	mainMenuItemNames.add(t);
    	return mi;
    }
    
    

    
    void centreCircuit() {
	Rectangle bounds = getCircuitBounds();
	
    	double scale = 1;
    	
    	if (bounds != null)
    	    // 在边缘留出一些空间，因为边界计算并不完美
    	    scale = Math.min(circuitArea.width /(double)(bounds.width+140),
    			     circuitArea.height/(double)(bounds.height+100));
    	scale = Math.min(scale, 1.5); // 限制缩放比例，避免在大窗口中创建过大的电路

    	// 计算变换矩阵，使电路填满屏幕大部分区域
    	transform[0] = transform[3] = scale;
    	transform[1] = transform[2] = transform[4] = transform[5] = 0;
    	if (bounds != null) {
    	    transform[4] = (circuitArea.width -bounds.width *scale)/2 - bounds.x*scale;
    	    transform[5] = (circuitArea.height-bounds.height*scale)/2 - bounds.y*scale;
    	}
    }

    // 获取电路边界。注意这里不使用 setBbox()。它是在绘制电路时才计算的，
    // 但此方法需要在首次绘制前就绪，因此我们使用这种粗略的方法
    Rectangle getCircuitBounds() {
    	int i;
    	int minx = 1000, maxx = 0, miny = 1000, maxy = 0;
    	for (i = 0; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		// 居中文本在尝试使电路居中时会引起问题，
    		// 因此这里对它们做特殊处理
    		if (!ce.isCenteredText()) {
    			minx = min(ce.x, min(ce.x2, minx));
    			maxx = max(ce.x, max(ce.x2, maxx));
    		}
    		miny = min(ce.y, min(ce.y2, miny));
    		maxy = max(ce.y, max(ce.y2, maxy));
    	}
    	if (minx > maxx)
    	    return null;
    	return new Rectangle(minx, miny, maxx-minx, maxy-miny);
    }

    long lastTime = 0, lastFrameTime, lastIterTime, secTime = 0;
    int frames = 0;
    int steps = 0;
    int framerate = 0, steprate = 0;
    static CirSim theSim;

    
    public void setSimRunning(boolean s) {
    	if (s) {
    	    	if (stopMessage != null)
    	    	    return;
    		simRunning = true;
    		runStopButton.setHTML(LSHTML("<strong>RUN</strong>&nbsp;/&nbsp;Stop"));
    		runStopButton.setStylePrimaryName("topButton");
    		timer.scheduleRepeating(FASTTIMER);
    	} else {
    		simRunning = false;
    		runStopButton.setHTML(LSHTML("Run&nbsp;/&nbsp;<strong>STOP</strong>"));
    		runStopButton.setStylePrimaryName("topButton-red");
    		timer.cancel();
		repaint();
    	}
    }
    
    public boolean simIsRunning() {
    	return simRunning;
    }
    
    boolean needsRepaint;
    
    void repaint() {
	if (!needsRepaint) {
	    needsRepaint = true;
	    Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
		public boolean execute() {
		      updateCircuit();
		      needsRepaint = false;
		      return false;
		  }
	    }, FASTTIMER);
	}
    }
    
// *****************************************************************
//                     更新电路
    
    public void updateCircuit() {
    long mystarttime;
    long myrunstarttime;
    long mydrawstarttime;
//	if (winSize == null || winSize.width == 0)
//	    return;
	mystarttime=System.currentTimeMillis();
	boolean didAnalyze = analyzeFlag;
	if (analyzeFlag || dcAnalysisFlag) {
	    analyzeCircuit();
	    analyzeFlag = false;
	}
//	if (editDialog != null && editDialog.elm instanceof CircuitElm)
//	    mouseElm = (CircuitElm) (editDialog.elm);
	if (stopElm != null && stopElm != mouseElm)
	    stopElm.setMouseElm(true);
	setupScopes();

	Graphics g=new Graphics(backcontext);
	
	CircuitElm.selectColor = Color.cyan;
	if (printableCheckItem.getState()) {
  	    CircuitElm.whiteColor = Color.black;
  	    CircuitElm.lightGrayColor = Color.black;
  	    g.setColor(Color.white);
	} else {
	    CircuitElm.whiteColor = Color.white;
	    CircuitElm.lightGrayColor = Color.lightGray;
	    g.setColor(Color.black);
	}
	g.fillRect(0, 0, g.context.getCanvas().getWidth(), g.context.getCanvas().getHeight());
	myrunstarttime=System.currentTimeMillis();
	if (simRunning) {
	    try {
		runCircuit(didAnalyze);
	    } catch (Exception e) {
		debugger();
		console("exception in runCircuit " + e);
		e.printStackTrace();
		return;
	    }
	 myruntime+=System.currentTimeMillis()-myrunstarttime;
}
	long sysTime = System.currentTimeMillis();
		if (simRunning) {
			
			if (lastTime != 0) {
				int inc = (int) (sysTime - lastTime);
				double c = currentBar.getValue();
				c = java.lang.Math.exp(c / 3.5 - 14.2);
				CircuitElm.currentMult = 1.7 * inc * c;
				 if (!conventionCheckItem.getState())
				 CircuitElm.currentMult = -CircuitElm.currentMult;
			}

			lastTime = sysTime;
		} else
			lastTime = 0;
		
		if (sysTime - secTime >= 1000) {
			framerate = frames;
			steprate = steps;
			frames = 0;
			steps = 0;
			secTime = sysTime;
		}
	   CircuitElm.powerMult = Math.exp(powerBar.getValue()/4.762-7);
	   
	
	int i;
//	Font oldfont = g.getFont();
	Font oldfont = CircuitElm.unitsFont;
	g.setFont(oldfont);
	
	// 这会在 Chrome 55 上导致不良行为
//	g.clipRect(0, 0, circuitArea.width, circuitArea.height);
	
	mydrawstarttime=System.currentTimeMillis();
	
	g.context.setLineCap(LineCap.ROUND);

	if (noEditCheckItem.getState())
	    g.drawLock(20, 30);
	g.setColor(Color.white);
	// 绘制元件
	backcontext.setTransform(transform[0], transform[1], transform[2],
				 transform[3], transform[4], transform[5]);
	for (i = 0; i != elmList.size(); i++) {
	    if (powerCheckItem.getState())
	    	g.setColor(Color.gray);
	    /*else if (conductanceCheckItem.getState())
	      g.setColor(Color.white);*/
	    getElm(i).draw(g);
	}
	mydrawtime+=System.currentTimeMillis()-mydrawstarttime;
	
	// 正常绘制端点（接线柱）
	if (mouseMode != CirSim.MODE_DRAG_ROW && mouseMode != CirSim.MODE_DRAG_COLUMN) {
	    for (i = 0; i != postDrawList.size(); i++)
		CircuitElm.drawPost(g, postDrawList.get(i));
	}
	
	// 对于某些鼠标模式，重要的不是端点而是元件末端（只有两端元件二者相同）。
	// 如果需要，现在绘制这些点
	if (tempMouseMode == MODE_DRAG_ROW || tempMouseMode == MODE_DRAG_COLUMN ||
			tempMouseMode == MODE_DRAG_POST || tempMouseMode == MODE_DRAG_SELECTED)
		for (i = 0; i != elmList.size(); i++) {

			CircuitElm ce = getElm(i);
//			ce.drawPost(g, ce.x , ce.y );
//			ce.drawPost(g, ce.x2, ce.y2);
			if (ce!=mouseElm || tempMouseMode!=MODE_DRAG_POST) {
				g.setColor(Color.gray);
				g.fillOval(ce.x-3, ce.y-3, 7, 7);
				g.fillOval(ce.x2-3, ce.y2-3, 7, 7);
			} else {
				ce.drawHandles(g, Color.cyan);
			}
		}
	// 为正在创建的元件绘制手柄
	if (tempMouseMode==MODE_SELECT && mouseElm!=null) {
		mouseElm.drawHandles(g, Color.cyan);
	}
	
	// 为正在拖动的元件绘制手柄
	if (dragElm != null &&
		      (dragElm.x != dragElm.x2 || dragElm.y != dragElm.y2)) {
		    	dragElm.draw(g);
		    	dragElm.drawHandles(g, Color.cyan);
		}

	// 绘制错误连接。最后绘制它们，以免被覆盖。
	for (i = 0; i != badConnectionList.size(); i++) {
	    Point cn = badConnectionList.get(i);
	    g.setColor(Color.red);
	    g.fillOval(cn.x-3, cn.y-3, 7, 7);
	}
	
	if (selectedArea != null) {
	    g.setColor(CircuitElm.selectColor);
	    g.drawRect(selectedArea.x, selectedArea.y, selectedArea.width, selectedArea.height);
	}

	if (crossHairCheckItem.getState() && mouseCursorX>=0
		&& mouseCursorX <= circuitArea.width && mouseCursorY <= circuitArea.height) {
	    g.setColor(Color.gray);
	    int x = snapGrid(inverseTransformX(mouseCursorX));
	    int y = snapGrid(inverseTransformY(mouseCursorY));
	    g.drawLine(x, inverseTransformY(0), x, inverseTransformY(circuitArea.height));
	    g.drawLine(inverseTransformX(0), y, inverseTransformX(circuitArea.width), y);
	}

	
	backcontext.setTransform(1, 0, 0, 1, 0, 0);

	if (printableCheckItem.getState())
	    g.setColor(Color.white);
	else
	    g.setColor(Color.black);
	g.fillRect(0, circuitArea.height, circuitArea.width, cv.getCoordinateSpaceHeight()-circuitArea.height);
//	g.restore();
	g.setFont(oldfont);
	int ct = scopeCount;
	if (stopMessage != null)
	    ct = 0;
	for (i = 0; i != ct; i++)
	    scopes[i].draw(g);
	if (mouseWasOverSplitter) {
		g.setColor(Color.cyan);
		g.setLineWidth(4.0);
		g.drawLine(0, circuitArea.height-2, circuitArea.width, circuitArea.height-2);
		g.setLineWidth(1.0);
	}
	g.setColor(CircuitElm.whiteColor);

	if (stopMessage != null) {
	    g.drawString(stopMessage, 10, circuitArea.height-10);
	} else {
	    String info[] = new String[10];
	    if (mouseElm != null) {
		if (mousePost == -1) {
		    mouseElm.getInfo(info);
		    info[0] = LS(info[0]);
		    if (info[1] != null)
			info[1] = LS(info[1]);
		} else
		    info[0] = "V = " +
			CircuitElm.getUnitText(mouseElm.getPostVoltage(mousePost), "V");
//		/* //shownodes
//		for (i = 0; i != mouseElm.getPostCount(); i++)
//		    info[0] += " " + mouseElm.nodes[i];
//		if (mouseElm.getVoltageSourceCount() > 0)
//		    info[0] += ";" + (mouseElm.getVoltageSource()+nodeList.size());
//		*/
		
	    } else {
	    	info[0] = "t = " + CircuitElm.getTimeText(t);
	    	info[1] = LS("time step = ") + CircuitElm.getTimeText(timeStep);
	    }
	    if (hintType != -1) {
		for (i = 0; info[i] != null; i++)
		    ;
		String s = getHint();
		if (s == null)
		    hintType = -1;
		else
		    info[i] = s;
	    }
	    int x = 0;
	    if (ct != 0)
		x = scopes[ct-1].rightEdge() + 20;
	    x = max(x, cv.getCoordinateSpaceWidth()*2/3);
	  //  x=cv.getCoordinateSpaceWidth()*2/3;
	    
	    // 统计数据行数
	    for (i = 0; info[i] != null; i++)
		;
	    int badnodes = badConnectionList.size();
	    if (badnodes > 0)
		info[i++] = badnodes + ((badnodes == 1) ?
					LS(" bad connection") : LS(" bad connections"));
	    
	    int ybase = circuitArea.height;
	    for (i = 0; info[i] != null; i++)
		g.drawString(info[i], x,
			     ybase+15*(i+1));
	}
	if (stopElm != null && stopElm != mouseElm)
	    stopElm.setMouseElm(false);
	frames++;
	
	g.setColor(Color.white);
//	g.drawString("Framerate: " + CircuitElm.showFormat.format(framerate), 10, 10);
//	g.drawString("Steprate: " + CircuitElm.showFormat.format(steprate),  10, 30);
//	g.drawString("Steprate/iter: " + CircuitElm.showFormat.format(steprate/getIterCount()),  10, 50);
//	g.drawString("iterc: " + CircuitElm.showFormat.format(getIterCount()),  10, 70);
//	g.drawString("Frames: "+ frames,10,90);
//	g.drawString("ms per frame (other): "+ CircuitElm.showFormat.format((mytime-myruntime-mydrawtime)/myframes),10,110);
//	g.drawString("ms per frame (sim): "+ CircuitElm.showFormat.format((myruntime)/myframes),10,130);
//	g.drawString("ms per frame (draw): "+ CircuitElm.showFormat.format((mydrawtime)/myframes),10,150);
	
	cvcontext.drawImage(backcontext.getCanvas(), 0.0, 0.0);
	
	// 如果我们做了直流分析，需要在清除该标志后重新分析电路。 
	if (dcAnalysisFlag) {
	    dcAnalysisFlag = false;
	    analyzeFlag = true;
	}

	lastFrameTime = lastTime;
	mytime=mytime+System.currentTimeMillis()-mystarttime;
	myframes++;
    }

    Color getBackgroundColor() {
	if (printableCheckItem.getState())
	    return Color.white;
	return Color.black;
    }
    
    void setupScopes() {
    	int i;

    	// 检查示波器，确保元件仍然存在，并移除
    	// 未使用的示波器/列
    	int pos = -1;
    	for (i = 0; i < scopeCount; i++) {
    	    	if (scopes[i].needToRemove()) {
    			int j;
    			for (j = i; j != scopeCount; j++)
    				scopes[j] = scopes[j+1];
    			scopeCount--;
    			i--;
    			continue;
    		}
    		if (scopes[i].position > pos+1)
    			scopes[i].position = pos+1;
    		pos = scopes[i].position;
    	}
    	while (scopeCount > 0 && scopes[scopeCount-1].getElm() == null)
    		scopeCount--;
    	int h = cv.getCoordinateSpaceHeight() - circuitArea.height;
    	pos = 0;
    	for (i = 0; i != scopeCount; i++)
    		scopeColCount[i] = 0;
    	for (i = 0; i != scopeCount; i++) {
    		pos = max(scopes[i].position, pos);
    		scopeColCount[scopes[i].position]++;
    	}
    	int colct = pos+1;
    	int iw = infoWidth;
    	if (colct <= 2)
    		iw = iw*3/2;
    	int w = (cv.getCoordinateSpaceWidth()-iw) / colct;
    	int marg = 10;
    	if (w < marg*2)
    		w = marg*2;
    	pos = -1;
    	int colh = 0;
    	int row = 0;
    	int speed = 0;
    	for (i = 0; i != scopeCount; i++) {
    		Scope s = scopes[i];
    		if (s.position > pos) {
    			pos = s.position;
    			colh = h / scopeColCount[pos];
    			row = 0;
    			speed = s.speed;
    		}
    		s.stackCount = scopeColCount[pos];
    		if (s.speed != speed) {
    			s.speed = speed;
    			s.resetGraph();
    		}
    		Rectangle r = new Rectangle(pos*w, cv.getCoordinateSpaceHeight()-h+colh*row,
    				w-marg, colh);
    		row++;
    		if (!r.equals(s.rect))
    			s.setRect(r);
    	}
    }
    
    String getHint() {
	CircuitElm c1 = getElm(hintItem1);
	CircuitElm c2 = getElm(hintItem2);
	if (c1 == null || c2 == null)
	    return null;
	if (hintType == HINT_LC) {
	    if (!(c1 instanceof InductorElm))
		return null;
	    if (!(c2 instanceof CapacitorElm))
		return null;
	    InductorElm ie = (InductorElm) c1;
	    CapacitorElm ce = (CapacitorElm) c2;
	    return LS("res.f = ") + CircuitElm.getUnitText(1/(2*pi*Math.sqrt(ie.inductance*
						    ce.capacitance)), "Hz");
	}
	if (hintType == HINT_RC) {
	    if (!(c1 instanceof ResistorElm))
		return null;
	    if (!(c2 instanceof CapacitorElm))
		return null;
	    ResistorElm re = (ResistorElm) c1;
	    CapacitorElm ce = (CapacitorElm) c2;
	    return "RC = " + CircuitElm.getUnitText(re.resistance*ce.capacitance,
					 "s");
	}
	if (hintType == HINT_3DB_C) {
	    if (!(c1 instanceof ResistorElm))
		return null;
	    if (!(c2 instanceof CapacitorElm))
		return null;
	    ResistorElm re = (ResistorElm) c1;
	    CapacitorElm ce = (CapacitorElm) c2;
	    return LS("f.3db = ") +
		CircuitElm.getUnitText(1/(2*pi*re.resistance*ce.capacitance), "Hz");
	}
	if (hintType == HINT_3DB_L) {
	    if (!(c1 instanceof ResistorElm))
		return null;
	    if (!(c2 instanceof InductorElm))
		return null;
	    ResistorElm re = (ResistorElm) c1;
	    InductorElm ie = (InductorElm) c2;
	    return LS("f.3db = ") +
		CircuitElm.getUnitText(re.resistance/(2*pi*ie.inductance), "Hz");
	}
	if (hintType == HINT_TWINT) {
	    if (!(c1 instanceof ResistorElm))
		return null;
	    if (!(c2 instanceof CapacitorElm))
		return null;
	    ResistorElm re = (ResistorElm) c1;
	    CapacitorElm ce = (CapacitorElm) c2;
	    return LS("fc = ") +
		CircuitElm.getUnitText(1/(2*pi*re.resistance*ce.capacitance), "Hz");
	}
	return null;
    }

//    public void toggleSwitch(int n) {
//	int i;
//	for (i = 0; i != elmList.size(); i++) {
//	    CircuitElm ce = getElm(i);
//	    if (ce instanceof SwitchElm) {
//		n--;
//		if (n == 0) {
//		    ((SwitchElm) ce).toggle();
//		    analyzeFlag = true;
//		    cv.repaint();
//		    return;
//		}
//	    }
//	}
//    }
    
    void needAnalyze() {
	analyzeFlag = true;
    	repaint();
    }
    
    Vector<CircuitNode> nodeList;
    Vector<Point> postDrawList = new Vector<Point>();
    Vector<Point> badConnectionList = new Vector<Point>();
    CircuitElm voltageSources[];

    public CircuitNode getCircuitNode(int n) {
	if (n >= nodeList.size())
	    return null;
	return nodeList.elementAt(n);
    }

    public CircuitElm getElm(int n) {
	if (n >= elmList.size())
	    return null;
	return elmList.elementAt(n);
    }
    
    public Adjustable findAdjustable(CircuitElm elm, int item) {
	int i;
	for (i = 0; i != adjustables.size(); i++) {
	    Adjustable a = adjustables.get(i);
	    if (a.elm == elm && a.editItem == item)
		return a;
	}
	return null;
    }
    
    public static native void console(String text)
    /*-{
	    console.log(text);
	}-*/;

    public static native void debugger() /*-{ debugger; }-*/;
    
    class NodeMapEntry {
	int node;
	NodeMapEntry() { node = -1; }
	NodeMapEntry(int n) { node = n; }
    }
    // 将点映射到节点编号
    HashMap<Point,NodeMapEntry> nodeMap;
    HashMap<Point,Integer> postCountMap;
    
    class WireInfo {
	WireElm wire;
	Vector<CircuitElm> neighbors;
	int post;
	WireInfo(WireElm w) {
	    wire = w;
	}
    }
    // 每条导线及其邻居的信息，用于计算导线电流
    Vector<WireInfo> wireInfoList;
    
    // 找出由导线连接的节点组，并将它们映射到同一节点。这会大大加快速度，
    // 因为它减小了矩阵的规模
    void calculateWireClosure() {
	int i;
	nodeMap = new HashMap<Point,NodeMapEntry>();
//	int mergeCount = 0;
	wireInfoList = new Vector<WireInfo>();
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    if (!(ce instanceof WireElm))
		continue;
	    WireElm we = (WireElm) ce;
	    we.hasWireInfo = false;
	    wireInfoList.add(new WireInfo(we));
	    NodeMapEntry cn  = nodeMap.get(ce.getPost(0));
	    NodeMapEntry cn2 = nodeMap.get(ce.getPost(1));
	    if (cn != null && cn2 != null) {
		// 合并节点；遍历映射，把所有指向 cn2 的键改为指向 cn
		for (Map.Entry<Point, NodeMapEntry> entry : nodeMap.entrySet()) {
		    if (entry.getValue() == cn2)
			entry.setValue(cn);
		}
//		mergeCount++;
		continue;
	    }
	    if (cn != null) {
		nodeMap.put(ce.getPost(1), cn);
		continue;
	    }
	    if (cn2 != null) {
		nodeMap.put(ce.getPost(0), cn2);
		continue;
	    }
	    // 新建条目
	    cn = new NodeMapEntry();
	    nodeMap.put(ce.getPost(0), cn);
	    nodeMap.put(ce.getPost(1), cn);
	}
	
//	console("got " + (groupCount-mergeCount) + " groups with " + nodeMap.size() + " nodes " + mergeCount);
    }
    
    // 生成计算导线电流所需的信息。大多数其他元件使用其端子节点上的电压
    // 来计算电流。但导线两端的电压相同，因此我们需要改用邻居的电流。
    // 我们过去把导线当作零电压源来简化处理，但这样做效率很低，因为
    // 每条导线都会使矩阵多出 2 行。我们改为创建 WireInfo 对象列表来帮助我们
    // 计算导线电流，这样降低了矩阵的复杂度，而且只在需要时才计算
    // 导线电流
    // （每帧一次，而不是每次子迭代一次）
    boolean calcWireInfo() {
	int i;
	int moved = 0;
	for (i = 0; i != wireInfoList.size(); i++) {
	    WireInfo wi = wireInfoList.get(i);
	    WireElm wire = wi.wire;
	    CircuitNode cn1 = nodeList.get(wire.getNode(0));  // 导线两端具有相同的节点编号
	    int j;

	    Vector<CircuitElm> neighbors0 = new Vector<CircuitElm>();
	    Vector<CircuitElm> neighbors1 = new Vector<CircuitElm>();
	    boolean isReady0 = true, isReady1 = true;
	    
	    // 遍历与这条导线共享节点的元件（可能通过其他导线间接连接，
	    // 但至少比遍历所有元件要快）
	    for (j = 0; j != cn1.links.size(); j++) {
		CircuitNodeLink cnl = cn1.links.get(j);
		CircuitElm ce = cnl.elm;
		if (ce == wire)
		    continue;
		Point pt = cnl.elm.getPost(cnl.num);
		
		// 这是还没有导线信息的导线吗？如果是，则不能使用它。
		// 那会造成循环依赖
		boolean notReady = (ce instanceof WireElm && !((WireElm) ce).hasWireInfo);
		
		// 如果存在的话，这个元件连接到哪个端点？
		if (pt.x == wire.x && pt.y == wire.y) {
		    neighbors0.add(ce);
		    if (notReady) isReady0 = false;
		} else if (pt.x == wire.x2 && pt.y == wire.y2) {
		    neighbors1.add(ce);
		    if (notReady) isReady1 = false;
		}
	    }

	    // 某个端点是否已具备计算电流所需的全部信息
	    if (isReady0) {
		wi.neighbors = neighbors0;
		wi.post = 0;
		wire.hasWireInfo = true;
		moved = 0;
	    } else if (isReady1) {
		wi.neighbors = neighbors1;
		wi.post = 1;
		wire.hasWireInfo = true;
		moved = 0;
	    } else {
		// 移到列表末尾，稍后重试
		wireInfoList.add(wireInfoList.remove(i--));
		moved++; 
		if (moved > wireInfoList.size() * 2) {
		    stop("wire loop detected", wire);
		    return false;
		}
	    }
	}
	
	return true;
    }

    // 查找或分配接地节点
    void setGroundNode() {
	int i;
	boolean gotGround = false;
	boolean gotRail = false;
	CircuitElm volt = null;
	    
	//System.out.println("ac1");
	// 查找电压源或接地元件
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    if (ce instanceof GroundElm) {
		gotGround = true;
		break;
	    }
	    if (ce instanceof RailElm)
	    	gotRail = true;
	    if (volt == null && ce instanceof VoltageElm)
	    	volt = ce;
	}

	// 如果没有接地，也没有电源轨，则电压源元件的第一个端子
	// 就是接地
	if (!gotGround && volt != null && !gotRail) {
	    CircuitNode cn = new CircuitNode();
	    Point pt = volt.getPost(0);
	    nodeList.addElement(cn);

	    // 更新节点映射
	    NodeMapEntry cln = nodeMap.get(pt);
	    if (cln != null)
		cln.node = 0;
	    else
		nodeMap.put(pt, new NodeMapEntry(0));
	} else {
	    // 否则为接地分配额外节点
	    CircuitNode cn = new CircuitNode();
	    nodeList.addElement(cn);
	}
    }

    // 生成节点列表
    void makeNodeList() {
	int i, j;
	int vscount = 0;
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    int inodes = ce.getInternalNodeCount();
	    int ivs = ce.getVoltageSourceCount();
	    int posts = ce.getPostCount();
	    
	    // 为每个端点分配一个节点，并将端点与节点匹配
	    for (j = 0; j != posts; j++) {
		Point pt = ce.getPost(j);
		Integer g = postCountMap.get(pt);
		postCountMap.put(pt, g == null ? 1 : g+1);
		NodeMapEntry cln = nodeMap.get(pt);
		
		// 这个节点还不在映射中？或者节点编号尚未分配？
		// （在此之前我们不分配节点，因为改变节点的分配顺序
		// 会改变电路行为并破坏向后兼容性；
		// 下面连接未连接节点的代码可能把不同的节点接到地上） 
		if (cln == null || cln.node == -1) {
		    CircuitNode cn = new CircuitNode();
		    CircuitNodeLink cnl = new CircuitNodeLink();
		    cnl.num = j;
		    cnl.elm = ce;
		    cn.links.addElement(cnl);
		    ce.setNode(j, nodeList.size());
		    if (cln != null)
			cln.node = nodeList.size();
		    else
			nodeMap.put(pt, new NodeMapEntry(nodeList.size()));
		    nodeList.addElement(cn);
		} else {
		    int n = cln.node;
		    CircuitNodeLink cnl = new CircuitNodeLink();
		    cnl.num = j;
		    cnl.elm = ce;
		    getCircuitNode(n).links.addElement(cnl);
		    ce.setNode(j, n);
		    // 如果是接地节点，确保节点电压为 0，
		    // 因为之后可能不会被设置
		    if (n == 0)
			ce.setNodeVoltage(j, 0);
		}
	    }
	    for (j = 0; j != inodes; j++) {
		CircuitNode cn = new CircuitNode();
		cn.internal = true;
		CircuitNodeLink cnl = new CircuitNodeLink();
		cnl.num = j+posts;
		cnl.elm = ce;
		cn.links.addElement(cnl);
		ce.setNode(cnl.num, nodeList.size());
		nodeList.addElement(cn);
	    }
	    
	    // 同时统计电压源数量，以便分配数组
	    vscount += ivs;
	}
	
        voltageSources = new CircuitElm[vscount];
    }
    
    Vector<Integer> unconnectedNodes;
    
    void findUnconnectedNodes() {
	int i, j;
	
	// 确定没有间接连接到地的节点。
	// 所有节点都必须以某种方式连接到地，否则
	// 会出现矩阵错误。
	boolean closure[] = new boolean[nodeList.size()];
	boolean changed = true;
	unconnectedNodes = new Vector<Integer>();
	closure[0] = true;
	while (changed) {
	    changed = false;
	    for (i = 0; i != elmList.size(); i++) {
		CircuitElm ce = getElm(i);
		if (ce instanceof WireElm)
		    continue;
		// 遍历 ce 的所有节点，看它们是否连接到了
		// 闭包之外的其它节点
		for (j = 0; j < ce.getConnectionNodeCount(); j++) {
		    if (!closure[ce.getConnectionNode(j)]) {
			if (ce.hasGroundConnection(j))
			    closure[ce.getConnectionNode(j)] = changed = true;
			continue;
		    }
		    int k;
		    for (k = 0; k != ce.getConnectionNodeCount(); k++) {
			if (j == k)
			    continue;
			int kn = ce.getConnectionNode(k);
			if (ce.getConnection(j, k) && !closure[kn]) {
			    closure[kn] = true;
			    changed = true;
			}
		    }
		}
	    }
	    if (changed)
		continue;

	    // 用一个很大的电阻把某个未连接节点接到地上，然后重试
	    for (i = 0; i != nodeList.size(); i++)
		if (!closure[i] && !getCircuitNode(i).internal) {
		    unconnectedNodes.add(i);
		    console("node " + i + " unconnected");
//		    stampResistor(0, i, 1e8);   // do this later in connectUnconnectedNodes()
		    closure[i] = true;
		    changed = true;
		    break;
		}
	}
    }
    
    // 取出之前识别出的未连接节点列表，用一个大电阻把它们接到地上。
    // 否则会出现矩阵错误
    void connectUnconnectedNodes() {
	int i;
	for (i = 0; i != unconnectedNodes.size(); i++) {
	    int n = unconnectedNodes.get(i);
	    stampResistor(0, n, 1e8);
	}
    }
    
    boolean validateCircuit() {
	int i, j;
	
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    // 查找没有电流通路的电感
	    if (ce instanceof InductorElm) {
		FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce,
						    ce.getNode(1));
		if (!fpi.findPath(ce.getNode(0))) {
//		    console(ce + " no path");
		    ce.reset();
		}
	    }
	    // 查找没有电流通路的电流源
	    if (ce instanceof CurrentElm) {
		CurrentElm cur = (CurrentElm) ce;
		FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce,
						    ce.getNode(1));
		cur.setBroken(!fpi.findPath(ce.getNode(0)));
	    }
	    if (ce instanceof VCCSElm) {
		VCCSElm cur = (VCCSElm) ce;
		FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce,
						    cur.getOutputNode(0));
		if (cur.hasCurrentOutput() && !fpi.findPath(cur.getOutputNode(1))) {
		    cur.broken = true;
		} else
		    cur.broken = false;
	    }
	    
	    // 查找电压源或导线回路。我们只对电压源或类导线元件做此检查（不包括真正的导线，
	    // 因为它们已被优化掉，findPath 无法工作）
	    if (ce.getPostCount() == 2) {
		if (ce instanceof VoltageElm || (ce.isWire() && !(ce instanceof WireElm))) {
		    FindPathInfo fpi = new FindPathInfo(FindPathInfo.VOLTAGE, ce,
						    ce.getNode(1));
		    if (fpi.findPath(ce.getNode(0))) {
			stop("Voltage source/wire loop with no resistance!", ce);
			return false;
		    }
		}
	    } else if (ce instanceof Switch2Elm) {
		// 对于 Switch2Elm，我们需要额外检查导线回路
		FindPathInfo fpi = new FindPathInfo(FindPathInfo.VOLTAGE, ce, ce.getNode(0));
		for (j = 1; j < ce.getPostCount(); j++)
		    if (ce.getConnection(0, j) && fpi.findPath(ce.getNode(j))) {
			stop("Voltage source/wire loop with no resistance!", ce);
			return false;
		    }
	    }

	    // 查找从电源轨到地的通路
	    if (ce instanceof RailElm || ce instanceof LogicInputElm) {
		FindPathInfo fpi = new FindPathInfo(FindPathInfo.VOLTAGE, ce,
			    ce.getNode(0));
		if (fpi.findPath(0)) {
		    stop("Path to ground with no resistance!", ce);
		    return false;
		}
	    }
	    
	    // 查找短路的电容，或带电压但没有电阻的电容
	    if (ce instanceof CapacitorElm) {
		FindPathInfo fpi = new FindPathInfo(FindPathInfo.SHORT, ce,
						    ce.getNode(1));
		if (fpi.findPath(ce.getNode(0))) {
		    console(ce + " shorted");
		    ((CapacitorElm) ce).shorted();
		} else {
		    // 电容回路过去会导致矩阵错误。但我们更改了电容模型，
		    // 现在可以正常工作。唯一的问题是，如果一个电容与另一个
		    // 带有非零电压的电容并联，除非我们把两个电容都重置为
		    // 相同的电压，否则会产生振荡。与其检查这种情况，我们干脆
		    // 报错。
		    fpi = new FindPathInfo(FindPathInfo.CAP_V, ce, ce.getNode(1));
		    if (fpi.findPath(ce.getNode(0))) {
			stop("Capacitor loop with no resistance!", ce);
			return false;
		    }
		}
	    }
	}
	return true;
    }
    
    // 当电路发生变化时进行分析，以便进行仿真
    void analyzeCircuit() {
	if (elmList.isEmpty()) {
	    postDrawList = new Vector<Point>();
	    badConnectionList = new Vector<Point>();
	    return;
	}
	stopMessage = null;
	stopElm = null;
	int i, j;
	nodeList = new Vector<CircuitNode>();
	postCountMap = new HashMap<Point,Integer>();

	calculateWireClosure();
	setGroundNode();

	// 分配节点和电压源
	LabeledNodeElm.resetNodeList();
	makeNodeList();
	
	makePostDrawList();
	if (!calcWireInfo())
	    return;
	nodeMap = null; // 已处理完毕
	
	int vscount = 0;
	circuitNonLinear = false;

	// 判断电路是否非线性。同时设置电压源
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    if (ce.nonLinear())
		circuitNonLinear = true;
	    int ivs = ce.getVoltageSourceCount();
	    for (j = 0; j != ivs; j++) {
		voltageSources[vscount] = ce;
		ce.setVoltageSource(j, vscount++);
	    }
	}
	voltageSourceCount = vscount;

	// 如果只有一个电压源，则显示其内阻。
	// 这里不能使用 voltageSourceCount，因为它会统计内部电压源，比如 GroundElm 中的那个
	boolean gotVoltageSource = false;
	showResistanceInVoltageSources = true;
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    if (ce instanceof VoltageElm) {
		if (gotVoltageSource)
		    showResistanceInVoltageSources = false;
		else
		    gotVoltageSource = true;
	    }
	}

	findUnconnectedNodes();
	if (!validateCircuit())
	    return;

	timeStep = maxTimeStep;
	try {
	    stampCircuit();
	} catch (Exception e) {
	    stop("Exception in stampCircuit()", null);
	}
    }
    
    // 为矩阵做 stamp 操作，即按仿真电路所需填充矩阵（至少针对所有线性元件）
    void stampCircuit() {
	int i;
	int matrixSize = nodeList.size()-1 + voltageSourceCount;
	circuitMatrix = new double[matrixSize][matrixSize];
	circuitRightSide = new double[matrixSize];
	nodeVoltages = new double[nodeList.size()-1];
	if (lastNodeVoltages == null || lastNodeVoltages.length != nodeVoltages.length)
	    lastNodeVoltages = new double[nodeList.size()-1];
	origMatrix = new double[matrixSize][matrixSize];
	origRightSide = new double[matrixSize];
	circuitMatrixSize = circuitMatrixFullSize = matrixSize;
	circuitRowInfo = new RowInfo[matrixSize];
	circuitPermute = new int[matrixSize];
	for (i = 0; i != matrixSize; i++)
	    circuitRowInfo[i] = new RowInfo();
	circuitNeedsMap = false;
	
	connectUnconnectedNodes();

	// 为线性电路元件做 stamp 操作
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    ce.setParentList(elmList);
	    ce.stamp();
	}

	if (!simplifyMatrix(matrixSize))
	    return;
	
	// 检查是否调用了 stop()
	if (circuitMatrix == null)
	    return;
	
	// 如果矩阵是线性的，可以在这里做 lu_factor，
	// 而不必每帧都做
	if (!circuitNonLinear) {
	    if (!lu_factor(circuitMatrix, circuitMatrixSize, circuitPermute)) {
		stop("Singular matrix!", null);
		return;
	    }
	}
    }

    // 简化矩阵；这会大大加快速度，尤其是对数字电路。
    // 至少在加入导线移除功能之前是这样的
    boolean simplifyMatrix(int matrixSize) {
	int i, j;
	for (i = 0; i != matrixSize; i++) {
	    int qp = -1;
	    double qv = 0;
	    RowInfo re = circuitRowInfo[i];
	    /*System.out.println("row " + i + " " + re.lsChanges + " " + re.rsChanges + " " +
			       re.dropRow);*/
	    
//	    if (qp != -100) continue;   // uncomment this line to disable matrix simplification for debugging purposes
	    
	    if (re.lsChanges || re.dropRow || re.rsChanges)
		continue;
	    double rsadd = 0;

	    // 查找可以移除的行
	    for (j = 0; j != matrixSize; j++) {
		double q = circuitMatrix[i][j];
		if (circuitRowInfo[j].type == RowInfo.ROW_CONST) {
		    // 保持已被移除的常数值的
		    // 累计总和
		    rsadd -= circuitRowInfo[j].value*q;
		    continue;
		}
		// 忽略零值
		if (q == 0)
		    continue;
		// 记录第一个非 ROW_CONST 的非零元素
		if (qp == -1) {
		    qp = j;
		    qv = q;
		    continue;
		}
		// 不止一个非零元素？放弃
		break;
	    }
	    if (j == matrixSize) {
		if (qp == -1) {
		    // 可能是奇异矩阵，可尝试禁用上面的矩阵简化来检查这一点
		    stop("Matrix error", null);
		    return false;
		}
		RowInfo elt = circuitRowInfo[qp];
		// 我们找到了一行只有一个非零非 const 元素的记录；该值
		// 就是一个常量
		if (elt.type != RowInfo.ROW_NORMAL) {
		    System.out.println("type already " + elt.type + " for " + qp + "!");
		    continue;
		}
		elt.type = RowInfo.ROW_CONST;
//		console("ROW_CONST " + i + " " + rsadd);
		elt.value = (circuitRightSide[i]+rsadd)/qv;
		circuitRowInfo[i].dropRow = true;
		i = -1; // 从头重新开始
	    }
	}
	//System.out.println("ac7");

	// 计算新矩阵的大小
	int nn = 0;
	for (i = 0; i != matrixSize; i++) {
	    RowInfo elt = circuitRowInfo[i];
	    if (elt.type == RowInfo.ROW_NORMAL) {
		elt.mapCol = nn++;
		//System.out.println("col " + i + " maps to " + elt.mapCol);
		continue;
	    }
	    if (elt.type == RowInfo.ROW_CONST)
		elt.mapCol = -1;
	}

	// 生成新的、简化后的矩阵
	int newsize = nn;
	double newmatx[][] = new double[newsize][newsize];
	double newrs  []   = new double[newsize];
	int ii = 0;
	for (i = 0; i != matrixSize; i++) {
	    RowInfo rri = circuitRowInfo[i];
	    if (rri.dropRow) {
		rri.mapRow = -1;
		continue;
	    }
	    newrs[ii] = circuitRightSide[i];
	    rri.mapRow = ii;
	    //System.out.println("Row " + i + " maps to " + ii);
	    for (j = 0; j != matrixSize; j++) {
		RowInfo ri = circuitRowInfo[j];
		if (ri.type == RowInfo.ROW_CONST)
		    newrs[ii] -= ri.value*circuitMatrix[i][j];
		else
		    newmatx[ii][ri.mapCol] += circuitMatrix[i][j];
	    }
	    ii++;
	}

//	console("old size = " + matrixSize + " new size = " + newsize);
	
	circuitMatrix = newmatx;
	circuitRightSide = newrs;
	matrixSize = circuitMatrixSize = newsize;
	for (i = 0; i != matrixSize; i++)
	    origRightSide[i] = circuitRightSide[i];
	for (i = 0; i != matrixSize; i++)
	    for (j = 0; j != matrixSize; j++)
		origMatrix[i][j] = circuitMatrix[i][j];
	circuitNeedsMap = true;
	return true;
    }
    
    // 生成需要绘制的端点列表。被 2 个元件共享的端点应该隐藏，其余
    // 所有端点都要绘制。我们不能再为此使用节点列表了，因为导线
    // 两端的节点编号相同。
    void makePostDrawList() {
	postDrawList = new Vector<Point>();
	badConnectionList = new Vector<Point>();
	for (Map.Entry<Point, Integer> entry : postCountMap.entrySet()) {
	    if (entry.getValue() != 2)
		postDrawList.add(entry.getKey());
	    
	    // 查找错误连接：未连接到其他元件却与其它元件边界框
	    // 相交的端点
	    if (entry.getValue() == 1) {
		int j;
		boolean bad = false;
		Point cn = entry.getKey();
		for (j = 0; j != elmList.size() && !bad; j++) {
		    CircuitElm ce = getElm(j);
		    if ( ce instanceof GraphicElm )
			continue;
		    // 这个端点是否与元件的边界框相交？
		    if (!ce.boundingBox.contains(cn.x, cn.y))
			continue;
		    int k;
		    // 这个端点是否属于该元件？
		    int pc = ce.getPostCount();
		    for (k = 0; k != pc; k++)
			if (ce.getPost(k).equals(cn))
			    break;
		    if (k == pc)
			bad = true;
		}
		if (bad)
		    badConnectionList.add(cn);
	    }
	}
	postCountMap = null;
    }

    class FindPathInfo {
	static final int INDUCT  = 1;
	static final int VOLTAGE = 2;
	static final int SHORT   = 3;
	static final int CAP_V   = 4;
	boolean visited[];
	int dest;
	CircuitElm firstElm;
	int type;

	// 状态对象，用于在各种条件下（取决于 type_）帮助查找电路中的回路
	// elm_ = 起点和终点元件。dest_ = 目标节点。
	FindPathInfo(int type_, CircuitElm elm_, int dest_) {
	    dest = dest_;
	    type = type_;
	    firstElm = elm_;
	    visited  = new boolean[nodeList.size()];
	}

	// 在电路中查找从 firstElm 的 n1 节点出发、回到
	// firstElm 的 dest 节点的回路路径
	boolean findPath(int n1) {
	    if (n1 == dest)
		return true;

	    // 深度优先搜索，不需要重访已访问过的节点！
	    if (visited[n1])
		return false;

	    visited[n1] = true;
	    int i;
	    for (i = 0; i != elmList.size(); i++) {
		CircuitElm ce = getElm(i);
		if (ce == firstElm)
		    continue;
		if (type == INDUCT) {
		    // 电感需要一条不含电流源的通路
		    if (ce instanceof CurrentElm)
			continue;
		}
		if (type == VOLTAGE) {
		    // 检查电压回路时，我们只关心电压源/导线/接地
		    if (!(ce.isWire() || ce instanceof VoltageElm || ce instanceof GroundElm))
			continue;
		}
		// 检查短路时，只检查导线
		if (type == SHORT && !ce.isWire())
		    continue;
		if (type == CAP_V) {
		    // 检查电容/电压源回路
		    if (!(ce.isWire() || ce instanceof CapacitorElm || ce instanceof VoltageElm))
			continue;
		}
		if (n1 == 0) {
		    // 查找有接地连接的端点；
		    // 我们的路径可以经过接地
		    int j;
		    for (j = 0; j != ce.getConnectionNodeCount(); j++)
			if (ce.hasGroundConnection(j) && findPath(ce.getConnectionNode(j)))
			    return true;
		}
		int j;
		for (j = 0; j != ce.getConnectionNodeCount(); j++) {
		    if (ce.getConnectionNode(j) == n1)
			break;
		}
		if (j == ce.getConnectionNodeCount())
		    continue;
		if (ce.hasGroundConnection(j) && findPath(0)) {
		    return true;
		}
		if (type == INDUCT && ce instanceof InductorElm) {
		    // 电感可以使用与其他电流匹配的电感构成的通路
		    double c = ce.getCurrent();
		    if (j == 0)
			c = -c;
		    if (Math.abs(c-firstElm.getCurrent()) > 1e-10)
			continue;
		}
		int k;
		for (k = 0; k != ce.getConnectionNodeCount(); k++) {
		    if (j == k)
			continue;
		    if (ce.getConnection(j, k) && findPath(ce.getConnectionNode(k))) {
			//System.out.println("got findpath " + n1);
			return true;
		    }
		}
	    }
	    return false;
	}
    }

    void stop(String s, CircuitElm ce) {
	stopMessage = LS(s);
	circuitMatrix = null;  // 触发异常
	stopElm = ce;
	setSimRunning(false);
	analyzeFlag = false;
//	cv.repaint();
    }
    
    // 受控电压源 vs，其电压等于 n1 到 n2 之间的电压（还必须
    // 调用 stampVoltageSource()）
    void stampVCVS(int n1, int n2, double coef, int vs) {
	int vn = nodeList.size()+vs;
	stampMatrix(vn, n1, coef);
	stampMatrix(vn, n2, -coef);
    }
    
    // 为独立电压源 #vs 做 stamp 操作，从 n1 到 n2，电压值为 v
    void stampVoltageSource(int n1, int n2, int vs, double v) {
	int vn = nodeList.size()+vs;
	stampMatrix(vn, n1, -1);
	stampMatrix(vn, n2, 1);
	stampRightSide(vn, v);
	stampMatrix(n1, vn, 1);
	stampMatrix(n2, vn, -1);
    }

    // 如果电压值将在 doStep() 中通过 updateVoltageSource() 更新，则使用此方法
    void stampVoltageSource(int n1, int n2, int vs) {
	int vn = nodeList.size()+vs;
	stampMatrix(vn, n1, -1);
	stampMatrix(vn, n2, 1);
	stampRightSide(vn);
	stampMatrix(n1, vn, 1);
	stampMatrix(n2, vn, -1);
    }
    
    // 在 doStep() 中更新电压源
    void updateVoltageSource(int n1, int n2, int vs, double v) {
	int vn = nodeList.size()+vs;
	stampRightSide(vn, v);
    }
    
    void stampResistor(int n1, int n2, double r) {
	double r0 = 1/r;
	if (Double.isNaN(r0) || Double.isInfinite(r0)) {
	    System.out.print("bad resistance " + r + " " + r0 + "\n");
	    int a = 0;
	    a /= a;
	}
	stampMatrix(n1, n1, r0);
	stampMatrix(n2, n2, r0);
	stampMatrix(n1, n2, -r0);
	stampMatrix(n2, n1, -r0);
    }

    void stampConductance(int n1, int n2, double r0) {
	stampMatrix(n1, n1, r0);
	stampMatrix(n2, n2, r0);
	stampMatrix(n1, n2, -r0);
	stampMatrix(n2, n1, -r0);
    }

    // 指定从 cn1 到 cn2 的电流等于 vn1 到 vn2 的电压除以 g
    void stampVCCurrentSource(int cn1, int cn2, int vn1, int vn2, double g) {
	stampMatrix(cn1, vn1, g);
	stampMatrix(cn2, vn2, g);
	stampMatrix(cn1, vn2, -g);
	stampMatrix(cn2, vn1, -g);
    }

    void stampCurrentSource(int n1, int n2, double i) {
	stampRightSide(n1, -i);
	stampRightSide(n2, i);
    }

    // 为从 n1 到 n2 的电流源做 stamp 操作，其电流取决于流过 vs 的电流
    void stampCCCS(int n1, int n2, int vs, double gain) {
	int vn = nodeList.size()+vs;
	stampMatrix(n1, vn, gain);
	stampMatrix(n2, vn, -gain);
    }

    // 在矩阵的第 i 行、第 j 列写入值 x，表示节点 j 的电压变化
    // dv 将使流入节点 i 的电流增加 x dv。
    // （除非 i 或 j 是电压源节点。）
    void stampMatrix(int i, int j, double x) {
	if (Double.isInfinite(x))
	    debugger();
	if (i > 0 && j > 0) {
	    if (circuitNeedsMap) {
		i = circuitRowInfo[i-1].mapRow;
		RowInfo ri = circuitRowInfo[j-1];
		if (ri.type == RowInfo.ROW_CONST) {
		    //System.out.println("Stamping constant " + i + " " + j + " " + x);
		    circuitRightSide[i] -= x*ri.value;
		    return;
		}
		j = ri.mapCol;
		//System.out.println("stamping " + i + " " + j + " " + x);
	    } else {
		i--;
		j--;
	    }
	    circuitMatrix[i][j] += x;
	}
    }

    // 在第 i 行的右侧写入值 x，表示一个流入节点 i 的
    // 独立电流源
    void stampRightSide(int i, double x) {
	if (i > 0) {
	    if (circuitNeedsMap) {
		i = circuitRowInfo[i-1].mapRow;
		//System.out.println("stamping " + i + " " + x);
	    } else
		i--;
	    circuitRightSide[i] += x;
	}
    }

    // 表示第 i 行右侧的值将在 doStep() 中变化
    void stampRightSide(int i) {
	//System.out.println("rschanges true " + (i-1));
	if (i > 0)
	    circuitRowInfo[i-1].rsChanges = true;
    }
    
    // 表示第 i 行左侧的值将在 doStep() 中变化
    void stampNonLinear(int i) {
	if (i > 0)
	    circuitRowInfo[i-1].lsChanges = true;
    }

    double getIterCount() {
    	// IES - 移除交互
	if (speedBar.getValue() == 0)
	   return 0;

	 return .1*Math.exp((speedBar.getValue()-61)/24.);

    }

    // 如果有人正在示波器中查看导线，我们需要在每次迭代时计算导线电流。
    // 否则可以每帧只计算一次。
    boolean canDelayWireProcessing() {
	int i;
	for (i = 0; i != scopeCount; i++)
	    if (scopes[i].viewingWire())
		return false;
	for (i=0; i != elmList.size(); i++)
	    if (getElm(i) instanceof ScopeElm && ((ScopeElm)getElm(i)).elmScope.viewingWire())
		return false;
	return true;
    }
    
    boolean converged;
    int subIterations;
    
    void runCircuit(boolean didAnalyze) {
	if (circuitMatrix == null || elmList.size() == 0) {
	    circuitMatrix = null;
	    return;
	}
	int iter;
	//int maxIter = getIterCount();
	boolean debugprint = dumpMatrix;
	dumpMatrix = false;
	long steprate = (long) (160*getIterCount());
	long tm = System.currentTimeMillis();
	long lit = lastIterTime;
	if (lit == 0) {
	    lastIterTime = tm;
	    return;
	}
	
	// 检查是否需要运行仿真（针对非常慢的仿真速度）。
	// 如果电路发生了变化，至少执行一次迭代以确保一切一致。
	if (1000 >= steprate*(tm-lastIterTime) && !didAnalyze)
	    return;
	
	boolean delayWireProcessing = canDelayWireProcessing();
	
	int timeStepCountAtFrameStart = timeStepCount;
	
	// 跟踪未出现收敛问题的迭代次数
	int goodIterations = 100;
	boolean goodIteration = true;
	
	for (iter = 1; ; iter++) {
	    if (goodIterations >= 3 && timeStep < maxTimeStep && goodIteration) {
		// 一切进展顺利，将时间步长加倍
		timeStep = Math.min(timeStep*2, maxTimeStep);
		console("timestep up = " + timeStep + " at " + t);
		stampCircuit();
		goodIterations = 0;
	    }
	    
	    int i, j, subiter;
	    for (i = 0; i != elmList.size(); i++) {
		CircuitElm ce = getElm(i);
		ce.startIteration();
	    }
	    steps++;
	    int subiterCount = (adjustTimeStep && timeStep/2 > minTimeStep) ? 100 : 5000;
	    for (subiter = 0; subiter != subiterCount; subiter++) {
		converged = true;
		subIterations = subiter;
//		if (t % .030 < .002 && timeStep > 1e-6)  // force nonconvergence for debugging
//		    converged = false;
		for (i = 0; i != circuitMatrixSize; i++)
		    circuitRightSide[i] = origRightSide[i];
		if (circuitNonLinear) {
		    for (i = 0; i != circuitMatrixSize; i++)
			for (j = 0; j != circuitMatrixSize; j++)
			    circuitMatrix[i][j] = origMatrix[i][j];
		}
		for (i = 0; i != elmList.size(); i++) {
		    CircuitElm ce = getElm(i);
		    ce.doStep();
		}
		if (stopMessage != null)
		    return;
		boolean printit = debugprint;
		debugprint = false;
		for (j = 0; j != circuitMatrixSize; j++) {
		    for (i = 0; i != circuitMatrixSize; i++) {
			double x = circuitMatrix[i][j];
			if (Double.isNaN(x) || Double.isInfinite(x)) {
			    stop("nan/infinite matrix!", null);
			    console("circuitMatrix " + i + " " + j + " is " + x);
			    return;
			}
		    }
		}
		if (printit) {
		    for (j = 0; j != circuitMatrixSize; j++) {
			String x = "";
			for (i = 0; i != circuitMatrixSize; i++)
			    x += circuitMatrix[j][i] + ",";
			x += "\n";
			console(x);
		    }
		    console("done");
		}
		if (circuitNonLinear) {
		    // 如果已收敛则停止（元件在 doStep() 中检查收敛）
		    if (converged && subiter > 0)
			break;
		    if (!lu_factor(circuitMatrix, circuitMatrixSize,
				  circuitPermute)) {
			stop("Singular matrix!", null);
			return;
		    }
		}
		lu_solve(circuitMatrix, circuitMatrixSize, circuitPermute,
			 circuitRightSide);
		applySolvedRightSide(circuitRightSide);
		if (!circuitNonLinear)
		    break;
	    }
	    if (subiter == subiterCount) {
		// 收敛失败
		goodIterations = 0;
		if (adjustTimeStep) {
		    timeStep /= 2;
		    console("timestep down to " + timeStep + " at " + t);
		}
		if (timeStep < minTimeStep || !adjustTimeStep) {
		    console("convergence failed after " + subiter + " iterations");
		    stop("Convergence failed!", null);
		    break;
		}
		// 我们减小了时间步长。将电路状态重置为迭代开始时的状态
		setNodeVoltages(lastNodeVoltages);
		stampCircuit();
		continue;
	    }
	    if (subiter > 5 || timeStep < maxTimeStep)
		console("converged after " + subiter + " iterations, timeStep = " + timeStep);
	    if (subiter < 3 && goodIteration)
		goodIterations++;
	    else
		goodIterations = 0;
	    t += timeStep;
	    timeStepAccum += timeStep;
	    goodIteration = true;
	    if (timeStepAccum >= maxTimeStep) {
		timeStepAccum -= maxTimeStep;
		timeStepCount++;
	    }
	    for (i = 0; i != elmList.size(); i++)
		getElm(i).stepFinished();
	    if (!delayWireProcessing)
		calcWireCurrents();
	    for (i = 0; i != scopeCount; i++)
	    	scopes[i].timeStep();
	    for (i=0; i != elmList.size(); i++)
		if (getElm(i) instanceof ScopeElm)
		    ((ScopeElm)getElm(i)).stepScope();
	    // 保存上一次的节点电压，以便必要时重启下一次迭代
	    for (i = 0; i != lastNodeVoltages.length; i++)
		lastNodeVoltages[i] = nodeVoltages[i];
//	    console("set lastrightside at " + t + " " + lastNodeVoltages);
		
	    tm = System.currentTimeMillis();
	    lit = tm;
	    // 检查是否已经过了足够的时间，可以在已完成的那些迭代之后
	    // 再执行一次*额外*迭代。
	    if ((timeStepCount-timeStepCountAtFrameStart)*1000 >= steprate*(tm-lastIterTime) || (tm-lastFrameTime > 500))
		break;
	    if (!simRunning)
		break;
	} // for (iter = 1; ; iter++)
	lastIterTime = lit;
	if (delayWireProcessing)
	    calcWireCurrents();
//	System.out.println((System.currentTimeMillis()-lastFrameTime)/(double) iter);
    }

    // 根据求解矩阵得到的右侧值设置节点电压
    void applySolvedRightSide(double rs[]) {
//	console("setvoltages " + rs);
	int j;
	for (j = 0; j != circuitMatrixFullSize; j++) {
	    RowInfo ri = circuitRowInfo[j];
	    double res = 0;
	    if (ri.type == RowInfo.ROW_CONST)
		res = ri.value;
	    else
		res = rs[ri.mapCol];
	    if (Double.isNaN(res)) {
		converged = false;
		break;
	    }
	    if (j < nodeList.size()-1) {
		nodeVoltages[j] = res;
	    } else {
		int ji = j-(nodeList.size()-1);
		voltageSources[ji].setCurrent(ji, res);
	    }
	}
	
	setNodeVoltages(nodeVoltages);
    }
    
    // 根据节点电压数组设置每个元件中的节点电压
    void setNodeVoltages(double nv[]) {
	int j, k;
	for (j = 0; j != nv.length; j++) {
	    double res = nv[j];
	    CircuitNode cn = getCircuitNode(j+1);
	    for (k = 0; k != cn.links.size(); k++) {
		CircuitNodeLink cnl = (CircuitNodeLink)
			cn.links.elementAt(k);
		cnl.elm.setNodeVoltage(cnl.num, res);
	    }
	}
    }
    
    // 我们从矩阵中移除了导线以加快速度。为了显示导线电流，
    // 现在需要计算它们。
    void calcWireCurrents() {
	int i;
	
	// 用于调试
	//for (i = 0; i != wireInfoList.size(); i++)
	 //   wireInfoList.get(i).wire.setCurrent(-1, 1.23);
	
	for (i = 0; i != wireInfoList.size(); i++) {
	    WireInfo wi = wireInfoList.get(i);
	    double cur = 0;
	    int j;
	    Point p = wi.wire.getPost(wi.post);
	    for (j = 0; j != wi.neighbors.size(); j++) {
		CircuitElm ce = wi.neighbors.get(j);
		int n = ce.getNodeAtPoint(p.x, p.y);
		cur += ce.getCurrentIntoNode(n);
	    }
	    if (wi.post == 0)
		wi.wire.setCurrent(-1, cur);
	    else
		wi.wire.setCurrent(-1, -cur);
	}
    }
    
    int min(int a, int b) { return (a < b) ? a : b; }
    int max(int a, int b) { return (a > b) ? a : b; }
    
    public void resetAction(){
    	int i;
    	analyzeFlag = true;
    	if (t == 0)
    	    setSimRunning(true);
    	t = timeStepAccum = 0;
    	timeStepCount = 0;
    	for (i = 0; i != elmList.size(); i++)
		getElm(i).reset();
	for (i = 0; i != scopeCount; i++)
		scopes[i].resetGraph(true);
    	repaint();
    }
    
    static void electronSaveAsCallback(String s) {
	s = s.substring(s.lastIndexOf('/')+1);
	s = s.substring(s.lastIndexOf('\\')+1);
	theSim.setCircuitTitle(s);
	theSim.allowSave(true);
    }
    
    static native void electronSaveAs(String dump) /*-{
        $wnd.showSaveDialog().then(function (file) {
            if (file.canceled)
            	return;
            $wnd.saveFile(file, dump);
            @com.lushprojects.circuitjs1.client.CirSim::electronSaveAsCallback(Ljava/lang/String;)(file.filePath.toString());
        });
    }-*/;

    static native void electronSave(String dump) /*-{
        $wnd.saveFile(null, dump);
    }-*/;
    
    static void electronOpenFileCallback(String text, String name) {
	LoadFile.doLoadCallback(text, name);
	theSim.allowSave(true);
    }
    
    static native void electronOpenFile() /*-{
        $wnd.openFile(function (text, name) {
            @com.lushprojects.circuitjs1.client.CirSim::electronOpenFileCallback(Ljava/lang/String;Ljava/lang/String;)(text, name);
        });
    }-*/;
    
    static native void toggleDevTools() /*-{
        $wnd.toggleDevTools();
    }-*/;
    
    static native boolean isElectron() /*-{
        return ($wnd.openFile != undefined);
    }-*/;    
    
    void allowSave(boolean b) {
	if (saveFileItem != null)
	    saveFileItem.setEnabled(b);
    }
    
    public void menuPerformed(String menu, String item) {
	if ((menu=="edit" || menu=="main" || menu=="scopes") && noEditCheckItem.getState()) {
	    Window.alert(LS("Editing disabled.  Re-enable from the Options menu."));
	    return;
	}
    	if (item=="about")
    		aboutBox = new AboutBox(circuitjs1.versionString);
    	if (item=="importfromlocalfile") {
    		pushUndo();
    		if (isElectron())
    		    electronOpenFile();
    		else
    		    loadFileInput.click();
    	}
    	if (item=="newwindow") {
    	    Window.open(Document.get().getURL(), "_blank", "");
    	}
    	if (item=="save")
    	    electronSave(dumpCircuit());
    	if (item=="saveas")
    	    electronSaveAs(dumpCircuit());
    	if (item=="importfromtext") {
    		dialogShowing = new ImportFromTextDialog(this);
    	}
    	if (item=="importfromdropbox") {
    		importFromDropboxDialog = new ImportFromDropboxDialog(this);
    	}
    	if (item=="exportasurl") {
    		doExportAsUrl();
    		unsavedChanges = false;
    	}
    	if (item=="exportaslocalfile") {
    		doExportAsLocalFile();
    		unsavedChanges = false;
    	}
    	if (item=="exportastext") {
    		doExportAsText();
    		unsavedChanges = false;
    	}
    	if (item=="exportasimage")
		doExportAsImage();
    	if (item=="createsubcircuit")
		doCreateSubcircuit();
    	if (item=="dcanalysis")
    	    	doDCAnalysis();
    	if (item=="print")
    	    	doPrint();
    	if (item=="recover")
    	    	doRecover();

    	if ((menu=="elm" || menu=="scopepop") && contextPanel!=null)
    		contextPanel.hide();
    	if (menu=="options" && item=="shortcuts") {
    	    	dialogShowing = new ShortcutsDialog(this);
    	    	dialogShowing.show();
    	}
    	if (menu=="options" && item=="other")
    		doEdit(new EditOptions(this));
    	if (item=="devtools")
    	    toggleDevTools();
    	if (item=="undo")
    		doUndo();
    	if (item=="redo")
    		doRedo();
    	
    	// 如果鼠标悬停在某个元件上且按下了快捷键，则对该元件进行操作（把它当作上下文菜单项的选择）
    	if (menu == "key" && mouseElm != null) {
    	    menuElm = mouseElm;
    	    menu = "elm";
    	}
    	
    	if (item == "cut") {
    		if (menu!="elm")
    			menuElm = null;
    		doCut();
    	}
    	if (item == "copy") {
    		if (menu!="elm")
    			menuElm = null;
    		doCopy();
    	}
    	if (item=="paste")
    		doPaste(null);
    	if (item=="duplicate") {
		if (menu!="elm")
			menuElm = null;
    	    	doDuplicate();
    	}
    	if (item=="flip")
    	    doFlip();
    	if (item=="split")
    	    doSplit(menuElm);
    	if (item=="selectAll")
    		doSelectAll();
    	//	if (e.getSource() == exitItem) {
    	//	    destroyFrame();
    	//	    return;
    	//	}
    	
    	if (item=="centrecircuit") {
    		pushUndo();
    		centreCircuit();
    	}
    	if (item=="stackAll")
    		stackAll();
    	if (item=="unstackAll")
    		unstackAll();
    	if (item=="combineAll")
		combineAll();
    	if (item=="separateAll")
		separateAll();
    	if (item=="zoomin")
    	    zoomCircuit(20);
    	if (item=="zoomout")
    	    zoomCircuit(-20);
    	if (item=="zoom100")
    	    setCircuitScale(1);
    	if (menu=="elm" && item=="edit")
    		doEdit(menuElm);
    	if (item=="delete") {
    		if (menu!="elm")
    			menuElm = null;
    		pushUndo();
    		doDelete(true);
    	}
    	if (item=="sliders")
    	    doSliders(menuElm);

    	if (item=="viewInScope" && menuElm != null) {
    		int i;
    		for (i = 0; i != scopeCount; i++)
    			if (scopes[i].getElm() == null)
    				break;
    		if (i == scopeCount) {
    			if (scopeCount == scopes.length)
    				return;
    			scopeCount++;
    			scopes[i] = new Scope(this);
    			scopes[i].position = i;
    			//handleResize();
    		}
    		scopes[i].setElm(menuElm);
    		if (i > 0)
    		    scopes[i].speed = scopes[i-1].speed;
    	}
    	
    	if (item=="viewInFloatScope" && menuElm != null) {
    	    ScopeElm newScope = new ScopeElm(snapGrid(menuElm.x+50), snapGrid(menuElm.y+50));
    	    elmList.addElement(newScope);
    	    newScope.setScopeElm(menuElm);
	}
    	
    	if (item.substring(0,10)=="addToScope" && menuElm != null) {
    	    int n;
    	    n = Integer.parseInt(item.substring(10));
    	    if (n < scopeCount + countScopeElms()) {
    		if (n < scopeCount )
    		    scopes[n].addElm(menuElm);
    		else
    		    getNthScopeElm(n-scopeCount).elmScope.addElm(menuElm);
    	    }
    	}
    	
    	if (menu=="scopepop") {
    		pushUndo();
    		Scope s;
		if (menuScope != -1 )
		    	s= scopes[menuScope];
		else
		    	s= ((ScopeElm)mouseElm).elmScope;

    		if (item=="dock") {
            		if (scopeCount == scopes.length)
            			return;
            		scopes[scopeCount] = ((ScopeElm)mouseElm).elmScope;
            		((ScopeElm)mouseElm).clearElmScope();
            		scopes[scopeCount].position = scopeCount;
            		scopeCount++;
            		doDelete(false);
    		}
    		if (item=="undock") {
    	    	    ScopeElm newScope = new ScopeElm(snapGrid(menuElm.x+50), snapGrid(menuElm.y+50));
    	    	    elmList.addElement(newScope);
    	    	    newScope.setElmScope(scopes[menuScope]);
    	    	    
    	    	    int i;
    	    	    // 从列表中移除示波器。setupScopes() 会修正位置
    	    	    for (i = menuScope; i < scopeCount; i++)
    	    		scopes[i] = scopes[i+1];
    	    	    scopeCount--;
    		}
    		if (item=="remove")
    		    	s.setElm(null);  // setupScopes() 会将其清理
    		if (item=="removeplot")
			s.removePlot(menuPlot);
    		if (item=="speed2")
    			s.speedUp();
    		if (item=="speed1/2")
    			s.slowDown();
//    		if (item=="scale")
//    			scopes[menuScope].adjustScale(.5);
    		if (item=="maxscale")
    			s.maxScale();
    		if (item=="stack")
    			stackScope(menuScope);
    		if (item=="unstack")
    			unstackScope(menuScope);
    		if (item=="combine")
			combineScope(menuScope);
    		if (item=="selecty")
    			s.selectY();
    		if (item=="reset")
    			s.resetGraph(true);
    		if (item=="properties")
			s.properties();
    		deleteUnusedScopeElms();
    	}
    	if (menu=="circuits" && item.indexOf("setup ") ==0) {
    		pushUndo();
    		int sp = item.indexOf(' ', 6);
    		readSetupFile(item.substring(6, sp), item.substring(sp+1));
    	}
    	if (item=="newblankcircuit") {
    	    pushUndo();
    	    readSetupFile("blank.txt", "Blank Circuit");
    	}
    		
    	//	if (ac.indexOf("setup ") == 0) {
    	//	    pushUndo();
    	//	    readSetupFile(ac.substring(6),
    	//			  ((MenuItem) e.getSource()).getLabel());
    	//	}

    	// IES: 从 itemStateChanged() 移过来
    	if (menu=="main") {
    		if (contextPanel!=null)
    			contextPanel.hide();
    		//	MenuItem mmi = (MenuItem) mi;
    		//		int prevMouseMode = mouseMode;
    		setMouseMode(MODE_ADD_ELM);
    		String s = item;
    		if (s.length() > 0)
    			mouseModeStr = s;
    		if (s.compareTo("DragAll") == 0)
    			setMouseMode(MODE_DRAG_ALL);
    		else if (s.compareTo("DragRow") == 0)
    			setMouseMode(MODE_DRAG_ROW);
    		else if (s.compareTo("DragColumn") == 0)
    			setMouseMode(MODE_DRAG_COLUMN);
    		else if (s.compareTo("DragSelected") == 0)
    			setMouseMode(MODE_DRAG_SELECTED);
    		else if (s.compareTo("DragPost") == 0)
    			setMouseMode(MODE_DRAG_POST);
    		else if (s.compareTo("Select") == 0)
    			setMouseMode(MODE_SELECT);
    		//		else if (s.length() > 0) {
    		//			try {
    		//				addingClass = Class.forName(s);
    		//			} catch (Exception ee) {
    		//				ee.printStackTrace();
    		//			}
    		//		}
    		//		else
    		//			setMouseMode(prevMouseMode);
    		tempMouseMode = mouseMode;
    	}
    	if (item=="fullscreen") {
    	    if (! Graphics.isFullScreen)
    		Graphics.viewFullScreen();
    	    else
    		Graphics.exitFullScreen();
    	    centreCircuit();
    	}
    
	repaint();
    }
    
    int countScopeElms() {
	int c = 0;
	for (int i = 0; i != elmList.size(); i++) {
	    if ( elmList.get(i) instanceof ScopeElm)
		c++;
	}
	return c;
    }
    
    ScopeElm getNthScopeElm(int n) {
	for (int i = 0; i != elmList.size(); i++) {
	    if ( elmList.get(i) instanceof ScopeElm) {
		n--;
		if (n<0)
		    return (ScopeElm) elmList.get(i);
	    }
	}
	return (ScopeElm) null;
    }
    
    
    boolean canStackScope(int s) {
	if (scopeCount < 2) 
	    return false;
	if (s==0)
	    s=1;
    	if (scopes[s].position == scopes[s-1].position)
    	    return false;
	return true;
    }
    
    boolean canCombineScope(int s) {
	return scopeCount >=2;
    }
    
    boolean canUnstackScope(int s) {
	if (scopeCount < 2) 
	    return false;
	if (s==0)
	    s=1;
    	if (scopes[s].position != scopes[s-1].position) {
        	if ( s + 1 < scopeCount && scopes[s+1].position == scopes[s].position) // 允许通过选择堆叠中最顶部的示波器来取消堆叠
        	    return true;
        	else
        	    return false;
    	}
	return true;
    }

    void stackScope(int s) {
	if (! canStackScope(s) )
	    return;
    	if (s == 0) {
    		s = 1;
    	}
    	scopes[s].position = scopes[s-1].position;
    	for (s++; s < scopeCount; s++)
    		scopes[s].position--;
    }

    void unstackScope(int s) {
	if (! canUnstackScope(s) )
	    return;
    	if (s == 0) {
    		s = 1;
    	}
    	if (scopes[s].position != scopes[s-1].position) // 允许通过选择堆叠中最顶部的示波器来取消堆叠
    	    s++;
    	for (; s < scopeCount; s++)
    		scopes[s].position++;
    }

    void combineScope(int s) {
	if (! canCombineScope(s))
	    return;
    	if (s == 0) {
    		s = 1;
    	}
    	scopes[s-1].combine(scopes[s]);
    	scopes[s].setElm(null);
    }
    

    void stackAll() {
    	int i;
    	for (i = 0; i != scopeCount; i++) {
    		scopes[i].position = 0;
    		scopes[i].showMax = scopes[i].showMin = false;
    	}
    }

    void unstackAll() {
    	int i;
    	for (i = 0; i != scopeCount; i++) {
    		scopes[i].position = i;
    		scopes[i].showMax = true;
    	}
    }

    void combineAll() {
    	int i;
    	for (i = scopeCount-2; i >= 0; i--) {
    	    scopes[i].combine(scopes[i+1]);
    	    scopes[i+1].setElm(null);
    	}
    }
    
    void separateAll() {
    	int i;
	Scope newscopes[] = new Scope[20];
	int ct = 0;
    	for (i = 0; i < scopeCount; i++)
    	    ct = scopes[i].separate(newscopes, ct);
	scopes = newscopes;
	scopeCount = ct;
    }

    void doEdit(Editable eable) {
    	clearSelection();
    	pushUndo();
    	if (editDialog != null) {
    //		requestFocus();
    		editDialog.setVisible(false);
    		editDialog = null;
    	}
    	editDialog = new EditDialog(eable, this);
    	editDialog.show();
    }
    
    void doSliders(CircuitElm ce) {
	clearSelection();
	pushUndo();
	if (sliderDialog != null) {
	    sliderDialog.setVisible(false);
	    sliderDialog = null;
	}
	sliderDialog = new SliderDialog(ce, this);
	sliderDialog.show();
    }


    void doExportAsUrl()
    {
    	String dump = dumpCircuit();
	dialogShowing = new ExportAsUrlDialog(dump);
	dialogShowing.show();
    }
    
    void doExportAsText()
    {
    	String dump = dumpCircuit();
    	dialogShowing = new ExportAsTextDialog(this, dump);
    	dialogShowing.show();
    }

    void doExportAsImage()
    {
    	dialogShowing = new ExportAsImageDialog();
    	dialogShowing.show();
    }
    
    void doCreateSubcircuit()
    {
    	EditCompositeModelDialog dlg = new EditCompositeModelDialog();
    	if (!dlg.createModel())
    	    return;
    	dlg.createDialog();
    	dialogShowing = dlg;
    	dialogShowing.show();
    }
    
    void doExportAsLocalFile() {
    	String dump = dumpCircuit();
    	dialogShowing = new ExportAsLocalFileDialog(dump);
    	dialogShowing.show();
    }
    

    String dumpCircuit() {
	int i;
	CustomLogicModel.clearDumpedFlags();
	CustomCompositeModel.clearDumpedFlags();
	DiodeModel.clearDumpedFlags();
	TransistorModel.clearDumpedFlags();
	int f = (dotsCheckItem.getState()) ? 1 : 0;
	f |= (smallGridCheckItem.getState()) ? 2 : 0;
	f |= (voltsCheckItem.getState()) ? 0 : 4;
	f |= (powerCheckItem.getState()) ? 8 : 0;
	f |= (showValuesCheckItem.getState()) ? 0 : 16;
	// 32 = afilter 中的线性刻度
	f |= adjustTimeStep ? 64 : 0;
	String dump = "$ " + f + " " +
	    maxTimeStep + " " + getIterCount() + " " +
	    currentBar.getValue() + " " + CircuitElm.voltageRange + " " +
	    powerBar.getValue() + " " + minTimeStep + "\n";
		
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    String m = ce.dumpModel();
	    if (m != null && !m.isEmpty())
		dump += m + "\n";
	    dump += ce.dump() + "\n";
	}
	for (i = 0; i != scopeCount; i++) {
	    String d = scopes[i].dump();
	    if (d != null)
		dump += d + "\n";
	}
	for (i = 0; i != adjustables.size(); i++) {
	    Adjustable adj = adjustables.get(i);
	    dump += "38 " + adj.dump() + "\n";
	}
	if (hintType != -1)
	    dump += "h " + hintType + " " + hintItem1 + " " +
		hintItem2 + "\n";
	return dump;
    }

    void getSetupList(final boolean openDefault) {

    	String url;
    	url = GWT.getModuleBaseURL()+"setuplist.txt"+"?v="+random.nextInt(); 
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					Window.alert(LS("Can't load circuit list!"));
					GWT.log("File Error Response", exception);
				}

				public void onResponseReceived(Request request, Response response) {
					// 在这里进行处理
					if (response.getStatusCode()==Response.SC_OK) {
					String text = response.getText();
					processSetupList(text.getBytes(), openDefault);
					// 处理结束
					}
					else { 
						Window.alert(LS("Can't load circuit list!"));
						GWT.log("Bad file server response:"+response.getStatusText() );
					}
				}
			});
		} catch (RequestException e) {
			GWT.log("failed file reading", e);
		}
    }
		
    void processSetupList(byte b[], final boolean openDefault) {
	int len = b.length;
    	MenuBar currentMenuBar;
    	MenuBar stack[] = new MenuBar[6];
    	int stackptr = 0;
    	currentMenuBar=new MenuBar(true);
    	currentMenuBar.setAutoOpen(true);
    	menuBar.addItem(LS("Circuits"), currentMenuBar);
    	stack[stackptr++] = currentMenuBar;
    	int p;
    	for (p = 0; p < len; ) {
    		int l;
    		for (l = 0; l != len-p; l++)
    			if (b[l+p] == '\n') {
    				l++;
    				break;
    			}
    		String line = new String(b, p, l-1);
    		if (line.charAt(0) == '#')
    			;
    		else if (line.charAt(0) == '+') {
    		//	MenuBar n = new Menu(line.substring(1));
    			MenuBar n = new MenuBar(true);
    			n.setAutoOpen(true);
    			currentMenuBar.addItem(LS(line.substring(1)),n);
    			currentMenuBar = stack[stackptr++] = n;
    		} else if (line.charAt(0) == '-') {
    			currentMenuBar = stack[--stackptr-1];
    		} else {
    			int i = line.indexOf(' ');
    			if (i > 0) {
    				String title = LS(line.substring(i+1));
    				boolean first = false;
    				if (line.charAt(0) == '>')
    					first = true;
    				String file = line.substring(first ? 1 : 0, i);
    				currentMenuBar.addItem(new MenuItem(title,
    					new MyCommand("circuits", "setup "+file+" " + title)));
    				if (file.equals(startCircuit) && startLabel == null) {
    				    startLabel = title;
    				    titleLabel.setText(title);
    				}
    				if (first && startCircuit == null) {
    					startCircuit = file;
    					startLabel = title;
    					if (openDefault && stopMessage == null)
    						readSetupFile(startCircuit, startLabel);
    				}
    			}
    		}
    		p += l;
    	}
}

    void readCircuit(String text, int flags) {
	readCircuit(text.getBytes(), flags);
	if ((flags & RC_KEEP_TITLE) == 0)
	    titleLabel.setText(null);
    }

    void readCircuit(String text) {
	readCircuit(text.getBytes(), 0);
	titleLabel.setText(null);
    }

    void setCircuitTitle(String s) {
	if (s != null)
	    titleLabel.setText(s);
    }
    
	void readSetupFile(String str, String title) {
		// TODO: 也许可以考虑更好的缓存管理方法！
		String url=GWT.getModuleBaseURL()+"circuits/"+str+"?v="+random.nextInt(); 
		loadFileFromURL(url);
		if (title != null)
		    titleLabel.setText(title);
		unsavedChanges = false;
	}
	
	void loadFileFromURL(String url) {
	    RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
	    
	    try {
		requestBuilder.sendRequest(null, new RequestCallback() {
		    public void onError(Request request, Throwable exception) {
			Window.alert(LS("Can't load circuit!"));
			GWT.log("File Error Response", exception);
		    }

		    public void onResponseReceived(Request request, Response response) {
			if (response.getStatusCode()==Response.SC_OK) {
			    String text = response.getText();
			    readCircuit(text, RC_KEEP_TITLE);
			    allowSave(false);
			    unsavedChanges = false;
			}
			else { 
			    Window.alert(LS("Can't load circuit!"));
			    GWT.log("Bad file server response:"+response.getStatusText() );
			}
		    }
		});
	    } catch (RequestException e) {
		GWT.log("failed file reading", e);
	    }

	}

    static final int RC_RETAIN = 1;
    static final int RC_NO_CENTER = 2;
    static final int RC_SUBCIRCUITS = 4;
    static final int RC_KEEP_TITLE = 8;

    void readCircuit(byte b[], int flags) {
	int i;
	int len = b.length;
	if ((flags & RC_RETAIN) == 0) {
	    clearMouseElm();
	    for (i = 0; i != elmList.size(); i++) {
		CircuitElm ce = getElm(i);
		ce.delete();
	    }
	    t = timeStepAccum = 0;
	    elmList.removeAllElements();
	    hintType = -1;
	    maxTimeStep = 5e-6;
	    minTimeStep = 50e-12;
	    dotsCheckItem.setState(false);
	    smallGridCheckItem.setState(false);
	    powerCheckItem.setState(false);
	    voltsCheckItem.setState(true);
	    showValuesCheckItem.setState(true);
	    setGrid();
	    speedBar.setValue(117); // 57
	    currentBar.setValue(50);
	    powerBar.setValue(50);
	    CircuitElm.voltageRange = 5;
	    scopeCount = 0;
	    lastIterTime = 0;
	}
	boolean subs = (flags & RC_SUBCIRCUITS) != 0;
	//cv.repaint();
	int p;
	for (p = 0; p < len; ) {
	    int l;
	    int linelen = len-p; // IES - 修改为允许最后一行不以分隔符结尾
	    for (l = 0; l != len-p; l++)
		if (b[l+p] == '\n' || b[l+p] == '\r') {
		    linelen = l++;
		    if (l+p < b.length && b[l+p] == '\n')
			l++;
		    break;
		}
	    String line = new String(b, p, linelen);
	    StringTokenizer st = new StringTokenizer(line, " +\t\n\r\f");
	    while (st.hasMoreTokens()) {
		String type = st.nextToken();
		int tint = type.charAt(0);
		try {
		    if (subs && tint != '.')
			continue;
		    if (tint == 'o') {
			Scope sc = new Scope(this);
			sc.position = scopeCount;
			sc.undump(st);
			scopes[scopeCount++] = sc;
			break;
		    }
		    if (tint == 'h') {
			readHint(st);
			break;
		    }
		    if (tint == '$') {
			readOptions(st);
			break;
		    }
		    if (tint == '!') {
			CustomLogicModel.undumpModel(st);
			break;
		    }
		    if (tint == '%' || tint == '?' || tint == 'B') {
			// 忽略 afilter 特有的内容
			break;
		    }
		    // 未经测试导出为链接，请勿在此添加新符号
		    
		    // 如果第一个字符是数字，则将类型解析为数字
		    if (tint >= '0' && tint <= '9')
			tint = new Integer(type).intValue();
		    
		    if (tint == 34) {
			DiodeModel.undumpModel(st);
			break;
		    }
		    if (tint == 32) {
			TransistorModel.undumpModel(st);
			break;
		    }
		    if (tint == 38) {
			Adjustable adj = new Adjustable(st, this);
			adjustables.add(adj);
			break;
		    }
		    if (tint == '.') {
			CustomCompositeModel.undumpModel(st);
			break;
		    }
		    int x1 = new Integer(st.nextToken()).intValue();
		    int y1 = new Integer(st.nextToken()).intValue();
		    int x2 = new Integer(st.nextToken()).intValue();
		    int y2 = new Integer(st.nextToken()).intValue();
		    int f  = new Integer(st.nextToken()).intValue();
		    
		    CircuitElm newce = createCe(tint, x1, y1, x2, y2, f, st);
		    if (newce==null) {
				System.out.println("unrecognized dump type: " + type);
				break;
			    }
		    newce.setPoints();
		    elmList.addElement(newce);
		} catch (Exception ee) {
		    ee.printStackTrace();
		    console("exception while undumping " + ee);
		    break;
		}
		break;
	    }
	    p += l;
	    
	}
	setPowerBarEnable();
	enableItems();
	if ((flags & RC_RETAIN) == 0) {
	    // 根据需要创建滑块
	    for (i = 0; i != adjustables.size(); i++)
		adjustables.get(i).createSlider(this);
	}
//	if (!retain)
	//    handleResize(); // for scopes
	needAnalyze();
	if ((flags & RC_NO_CENTER) == 0)
		centreCircuit();
	if ((flags & RC_SUBCIRCUITS) != 0)
	    updateModels();
	
	AudioInputElm.clearCache();  // 以节省内存
    }

    // 删除元件的滑块
    void deleteSliders(CircuitElm elm) {
	int i;
	if (adjustables == null)
	    return;
	for (i = adjustables.size()-1; i >= 0; i--) {
	    Adjustable adj = adjustables.get(i);
	    if (adj.elm == elm) {
		adj.deleteSlider(this);
		adjustables.remove(i);
	    }
	}
    }
    
    void readHint(StringTokenizer st) {
	hintType  = new Integer(st.nextToken()).intValue();
	hintItem1 = new Integer(st.nextToken()).intValue();
	hintItem2 = new Integer(st.nextToken()).intValue();
    }

    void readOptions(StringTokenizer st) {
	int flags = new Integer(st.nextToken()).intValue();
	dotsCheckItem.setState((flags & 1) != 0);
	smallGridCheckItem.setState((flags & 2) != 0);
	voltsCheckItem.setState((flags & 4) == 0);
	powerCheckItem.setState((flags & 8) == 8);
	showValuesCheckItem.setState((flags & 16) == 0);
	adjustTimeStep = (flags & 64) != 0;
	maxTimeStep = timeStep = new Double (st.nextToken()).doubleValue();
	double sp = new Double(st.nextToken()).doubleValue();
	int sp2 = (int) (Math.log(10*sp)*24+61.5);
	//int sp2 = (int) (Math.log(sp)*24+1.5);
	speedBar.setValue(sp2);
	currentBar.setValue(new Integer(st.nextToken()).intValue());
	CircuitElm.voltageRange = new Double (st.nextToken()).doubleValue();

	try {
	    powerBar.setValue(new Integer(st.nextToken()).intValue());
	    minTimeStep = Double.parseDouble(st.nextToken());
	} catch (Exception e) {
	}
	setGrid();
    }
    
    int snapGrid(int x) {
	return (x+gridRound) & gridMask;
    }

	boolean doSwitch(int x, int y) {
		if (mouseElm == null || !(mouseElm instanceof SwitchElm))
			return false;
		SwitchElm se = (SwitchElm) mouseElm;
		if (!se.getSwitchRect().contains(x, y))
		    return false;
		se.toggle();
		if (se.momentary)
			heldSwitchElm = se;
		needAnalyze();
		return true;
	}

    int locateElm(CircuitElm elm) {
	int i;
	for (i = 0; i != elmList.size(); i++)
	    if (elm == elmList.elementAt(i))
		return i;
	return -1;
    }
    
    public void mouseDragged(MouseMoveEvent e) {
    	// 忽略不带修饰键的右键（PC 上需要）
    	if (e.getNativeButton()==NativeEvent.BUTTON_RIGHT) {
    		if (!(e.isMetaKeyDown() ||
    				e.isShiftKeyDown() ||
    				e.isControlKeyDown() ||
    				e.isAltKeyDown()))
    			return;
    	}
    	
    	if (tempMouseMode==MODE_DRAG_SPLITTER) {
    		dragSplitter(e.getX(), e.getY());
    		return;
    	}
    	int gx = inverseTransformX(e.getX());
    	int gy = inverseTransformY(e.getY());
    	if (!circuitArea.contains(e.getX(), e.getY()))
    	    return;
    	boolean changed = false;
    	if (dragElm != null)
    	    dragElm.drag(gx, gy);
    	boolean success = true;
    	switch (tempMouseMode) {
    	case MODE_DRAG_ALL:
    		dragAll(e.getX(), e.getY());
    		break;
    	case MODE_DRAG_ROW:
    		dragRow(snapGrid(gx), snapGrid(gy));
    		changed = true;
    		break;
    	case MODE_DRAG_COLUMN:
		dragColumn(snapGrid(gx), snapGrid(gy));
    		changed = true;
    		break;
    	case MODE_DRAG_POST:
    		if (mouseElm != null) {
    		    dragPost(snapGrid(gx), snapGrid(gy));
    		    changed = true;
    		}
    		break;
    	case MODE_SELECT:
    		if (mouseElm == null)
    		    selectArea(gx, gy);
    		else if (!noEditCheckItem.getState()) {
    		    // 拖动前等待短暂延迟。这是为了修复在移动设备上点击时开关被意外
    		    // 拖动的问题
    		    if (System.currentTimeMillis()-mouseDownTime < 150)
    			return;
    		
    		    tempMouseMode = MODE_DRAG_SELECTED;
    		    changed = success = dragSelected(gx, gy);
    		}
    		break;
    	case MODE_DRAG_SELECTED:
    		changed = success = dragSelected(gx, gy);
    		break;

    	}
    	dragging = true;
    	if (success) {
    	    dragScreenX = e.getX();
    	    dragScreenY = e.getY();
    //	    console("setting dragGridx in mousedragged");
    	    dragGridX = inverseTransformX(dragScreenX);
    	    dragGridY = inverseTransformY(dragScreenY);
    	    if (!(tempMouseMode == MODE_DRAG_SELECTED && onlyGraphicsElmsSelected())) {
    		dragGridX = snapGrid(dragGridX);
    		dragGridY = snapGrid(dragGridY);
    	    }
   	}
    	if (changed)
    	    writeRecoveryToStorage();
    	repaint();
    }
    
    void dragSplitter(int x, int y) {
    	double h = (double) cv.getCanvasElement().getHeight();
    	if (h<1)
    		h=1;
    	scopeHeightFraction=1.0-(((double)y)/h);
    	if (scopeHeightFraction<0.1)
    		scopeHeightFraction=0.1;
    	if (scopeHeightFraction>0.9)
    		scopeHeightFraction=0.9;
    	setCircuitArea();
    	repaint();
    }

    void dragAll(int x, int y) {
    	int dx = x-dragScreenX;
    	int dy = y-dragScreenY;
    	if (dx == 0 && dy == 0)
    		return;
    	transform[4] += dx;
    	transform[5] += dy;
    	dragScreenX = x;
    	dragScreenY = y;
    }

    void dragRow(int x, int y) {
    	int dy = y-dragGridY;
    	if (dy == 0)
    		return;
    	int i;
    	for (i = 0; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		if (ce.y  == dragGridY)
    			ce.movePoint(0, 0, dy);
    		if (ce.y2 == dragGridY)
    			ce.movePoint(1, 0, dy);
    	}
    	removeZeroLengthElements();
    }

    void dragColumn(int x, int y) {
    	int dx = x-dragGridX;
    	if (dx == 0)
    		return;
    	int i;
    	for (i = 0; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		if (ce.x  == dragGridX)
    			ce.movePoint(0, dx, 0);
    		if (ce.x2 == dragGridX)
    			ce.movePoint(1, dx, 0);
    	}
    	removeZeroLengthElements();
    }

    boolean onlyGraphicsElmsSelected() {
	if (mouseElm!=null && !(mouseElm instanceof GraphicElm))
	    return false;
    	int i;
    	for (i = 0; i != elmList.size(); i++) {
    	    CircuitElm ce = getElm(i);
    	    if ( ce.isSelected() && !(ce instanceof GraphicElm) )
    		return false;
    	}
    	return true;
    }
    
    boolean dragSelected(int x, int y) {
    	boolean me = false;
    	int i;
    	if (mouseElm != null && !mouseElm.isSelected())
    	    mouseElm.setSelected(me = true);

    	if (! onlyGraphicsElmsSelected()) {
    //	    console("Snapping x and y");
    	    x = snapGrid(x);
    	    y = snapGrid(y);
    	}

    	int dx = x-dragGridX;
  //  	console("dx="+dx+"dragGridx="+dragGridX);
    	int dy = y-dragGridY;
    	if (dx == 0 && dy == 0) {
    	    // 如果我们刚刚选中了 mouseElm，不要让它保持选中状态
    	    if (me)
    		mouseElm.setSelected(false);
    	    return false;
    	}
    	boolean allowed = true;

    	// 检查移动是否被允许
    	for (i = 0; allowed && i != elmList.size(); i++) {
    	    CircuitElm ce = getElm(i);
    	    if (ce.isSelected() && !ce.allowMove(dx, dy))
    		allowed = false;
    	}

    	if (allowed) {
    	    for (i = 0; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		if (ce.isSelected())
    		    ce.move(dx, dy);
    	    }
    	    needAnalyze();
    	}

    	// 如果我们刚刚选中了 mouseElm，不要让它保持选中状态
    	if (me)
    		mouseElm.setSelected(false);

    	return allowed;
    }

    void dragPost(int x, int y) {
    	if (draggingPost == -1) {
    		draggingPost =
    				(Graphics.distanceSq(mouseElm.x , mouseElm.y , x, y) >
    				Graphics.distanceSq(mouseElm.x2, mouseElm.y2, x, y)) ? 1 : 0;
    	}
    	int dx = x-dragGridX;
    	int dy = y-dragGridY;
    	if (dx == 0 && dy == 0)
    		return;
    	mouseElm.movePoint(draggingPost, dx, dy);
    	needAnalyze();
    }

    void doFlip() {
	menuElm.flipPosts();
    	needAnalyze();
    }
    
    void doSplit(CircuitElm ce) {
	int x = snapGrid(inverseTransformX(menuX));
	int y = snapGrid(inverseTransformY(menuY));
	if (ce == null || !(ce instanceof WireElm))
	    return;
	if (ce.x == ce.x2)
	    x = ce.x;
	else
	    y = ce.y;
	
	// 不要创建零长度的导线
	if (x == ce.x && y == ce.y || x == ce.x2 && y == ce.y2)
	    return;
	
	WireElm newWire = new WireElm(x, y);
	newWire.drag(ce.x2, ce.y2);
	ce.drag(x, y);
	elmList.addElement(newWire);
	needAnalyze();
    }
    
    void selectArea(int x, int y) {
    	int x1 = min(x, initDragGridX);
    	int x2 = max(x, initDragGridX);
    	int y1 = min(y, initDragGridY);
    	int y2 = max(y, initDragGridY);
    	selectedArea = new Rectangle(x1, y1, x2-x1, y2-y1);
    	int i;
    	for (i = 0; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		ce.selectRect(selectedArea);
    	}
    }

//    void setSelectedElm(CircuitElm cs) {
//    	int i;
//    	for (i = 0; i != elmList.size(); i++) {
//    		CircuitElm ce = getElm(i);
//    		ce.setSelected(ce == cs);
//    	}
//    	mouseElm = cs;
//    }
    
    void setMouseElm(CircuitElm ce) {
    	if (ce!=mouseElm) {
    		if (mouseElm!=null)
    			mouseElm.setMouseElm(false);
    		if (ce!=null)
    			ce.setMouseElm(true);
    		mouseElm=ce;
    	}
    }

    void removeZeroLengthElements() {
    	int i;
    	boolean changed = false;
    	for (i = elmList.size()-1; i >= 0; i--) {
    		CircuitElm ce = getElm(i);
    		if (ce.x == ce.x2 && ce.y == ce.y2) {
    			elmList.removeElementAt(i);
    			ce.delete();
    			changed = true;
    		}
    	}
    	needAnalyze();
    }
    
    boolean mouseIsOverSplitter(int x, int y) {
    	boolean isOverSplitter;
    	isOverSplitter =((x>=0) && (x<circuitArea.width) && 
    			(y>=circuitArea.height-5) && (y<circuitArea.height));
    	if (isOverSplitter!=mouseWasOverSplitter){
    		if (isOverSplitter)
    			setCursorStyle("cursorSplitter");
    		else
    			setMouseMode(mouseMode);
    	}
    	mouseWasOverSplitter=isOverSplitter;
    	return isOverSplitter;
    }

    public void onMouseMove(MouseMoveEvent e) {
    	e.preventDefault();
    	mouseCursorX=e.getX();
    	mouseCursorY=e.getY();
    	if (mouseDragging) {
    		mouseDragged(e);
    		return;
    	}
    	mouseSelect(e);
    }
    
    // 通过电路变换的逆变换把屏幕坐标转换为网格坐标
    int inverseTransformX(double x) {
	return (int) ((x-transform[4])/transform[0]);
    }

    int inverseTransformY(double y) {
	return (int) ((y-transform[5])/transform[3]);
    }
    
    // 把网格坐标转换为屏幕坐标
    int transformX(double x) {
	return (int) ((x*transform[0]) + transform[4]);
    }
    
    int transformY(double y) {
	return (int) ((y*transform[3]) + transform[5]);
    }
    
    

    // 需要把这个拆分成单独的例程来处理选择，
    // 因为在移动设备上我们收不到鼠标移动事件
    public void mouseSelect(MouseEvent<?> e) {
    	// 以下内容来自原版，但在 GWT 中似乎不起作用/不需要
    	//    	if (e.getNativeButton()==NativeEvent.BUTTON_LEFT)
    	//	    return;
    	CircuitElm newMouseElm=null;
    	mouseCursorX=e.getX();
    	mouseCursorY=e.getY();
    	int sx = e.getX();
    	int sy = e.getY();
    	int gx = inverseTransformX(sx);
    	int gy = inverseTransformY(sy);
   // 	console("Settingd draggridx in mouseEvent");
    	dragGridX = snapGrid(gx);
    	dragGridY = snapGrid(gy);
    	dragScreenX = sx;
    	dragScreenY = sy;
    	draggingPost = -1;
    	int i;
    	//	CircuitElm origMouse = mouseElm;

    	mousePost = -1;
    	plotXElm = plotYElm = null;
    	
    	if (mouseIsOverSplitter(sx, sy)) {
    		setMouseElm(null);
    		return;
    	}
    	
    	if (mouseElm!=null && ( mouseElm.getHandleGrabbedClose(gx, gy, POSTGRABSQ, MINPOSTGRABSIZE)>=0)) {
    		newMouseElm=mouseElm;
    	} else {
    		int bestDist = 100000000;
    		int bestArea = 100000000;
    		for (i = 0; i != elmList.size(); i++) {
    			CircuitElm ce = getElm(i);
    			if (ce.boundingBox.contains(gx, gy)) {
    				int j;
    				int area = ce.boundingBox.width * ce.boundingBox.height;
    				int jn = ce.getPostCount();
    				if (jn > 2)
    					jn = 2;
    				for (j = 0; j != jn; j++) {
    					Point pt = ce.getPost(j);
    					int dist = Graphics.distanceSq(gx, gy, pt.x, pt.y);

    					// 如果多个元件的边界框重叠，
    					// 我们优先选择端点靠近鼠标指针
    					// 且边界框面积较小的元件，
    					// 以便更准确地命中目标。
    					if (dist <= bestDist && area <= bestArea) {
    						bestDist = dist;
    						bestArea = area;
    						newMouseElm = ce;
    					}
    				}
    				// 优先选择边界框面积小的元件（对于
    				// 没有端点的元件）
    				if (ce.getPostCount() == 0 && area <= bestArea) {
    				    newMouseElm = ce;
    				    bestArea = area;
    				}
    			}
    		} // for
    	}
    	scopeSelected = -1;
    	if (newMouseElm == null) {
    		for (i = 0; i != scopeCount; i++) {
    			Scope s = scopes[i];
    			if (s.rect.contains(sx, sy)) {
    				newMouseElm=s.getElm();
    		    	if (s.plotXY) {
    		    		plotXElm = s.getXElm();
    		    		plotYElm = s.getYElm();
    		    	}
    				scopeSelected = i;
    			}
    		}
    		//	    // 鼠标指针不在任何边界框内，但我们
    		//	    // 可能仍然靠近某个端点
    		for (i = 0; i != elmList.size(); i++) {
    			CircuitElm ce = getElm(i);
    			if (mouseMode==MODE_DRAG_POST ) {
    				if (ce.getHandleGrabbedClose(gx, gy, POSTGRABSQ, 0)> 0)
    				{
    					newMouseElm = ce;
    					break;
    				}
    			}
    			int j;
    			int jn = ce.getPostCount();
    			for (j = 0; j != jn; j++) {
    				Point pt = ce.getPost(j);
    				//   int dist = Graphics.distanceSq(x, y, pt.x, pt.y);
    				if (Graphics.distanceSq(pt.x, pt.y, gx, gy) < 26) {
    					newMouseElm = ce;
    					mousePost = j;
    					break;
    				}
    			}
    		}
    	} else {
    		mousePost = -1;
    		// 查找靠近鼠标指针的端点
    		for (i = 0; i != newMouseElm.getPostCount(); i++) {
    			Point pt = newMouseElm.getPost(i);
    			if (Graphics.distanceSq(pt.x, pt.y, gx, gy) < 26)
    				mousePost = i;
    		}
    	}
    	repaint();
    	setMouseElm(newMouseElm);
    }



    public void onContextMenu(ContextMenuEvent e) {
    	e.preventDefault();
    	if (!dialogIsShowing()) {
        	menuClientX = e.getNativeEvent().getClientX();
        	menuClientY = e.getNativeEvent().getClientY();
        	doPopupMenu();
    	}
    }
    
    @SuppressWarnings("deprecation")
    void doPopupMenu() {
	if (noEditCheckItem.getState() || dialogIsShowing())
	    return;
    	menuElm = mouseElm;
    	menuScope=-1;
    	menuPlot=-1;
    	int x, y;
    	if (scopeSelected!=-1) {
    	    	if (scopes[scopeSelected].canMenu()) {
    	    	    menuScope=scopeSelected;
    	    	    menuPlot=scopes[scopeSelected].selectedPlot;
    	    	    scopePopupMenu.doScopePopupChecks(false, canStackScope(scopeSelected), canCombineScope(scopeSelected), 
    	    		    canUnstackScope(scopeSelected), scopes[scopeSelected]);
    	    	    contextPanel=new PopupPanel(true);
    	    	    contextPanel.add(scopePopupMenu.getMenuBar());
    	    	    y=Math.max(0, Math.min(menuClientY,cv.getCoordinateSpaceHeight()-160));
    	    	    contextPanel.setPopupPosition(menuClientX, y);
    	    	    contextPanel.show();
    		}
    	} else if (mouseElm != null) {
    	    	if (! (mouseElm instanceof ScopeElm)) {
    	    	    elmScopeMenuItem.setEnabled(mouseElm.canViewInScope());
    	    	    elmFloatScopeMenuItem.setEnabled(mouseElm.canViewInScope());
    	    	    if ((scopeCount + countScopeElms()) <= 1) {
    	    		elmAddScopeMenuItem.setCommand(new MyCommand("elm", "addToScope0"));
    	    		elmAddScopeMenuItem.setSubMenu(null);
    	    	    	elmAddScopeMenuItem.setEnabled(mouseElm.canViewInScope() && (scopeCount + countScopeElms())> 0);
    	    	    }
    	    	    else {
    	    		composeSelectScopeMenu(selectScopeMenuBar);
    	    		elmAddScopeMenuItem.setCommand(null);
    	    		elmAddScopeMenuItem.setSubMenu(selectScopeMenuBar);
    	    	    	elmAddScopeMenuItem.setEnabled(mouseElm.canViewInScope() );
    	    	    }
    	    	    elmEditMenuItem .setEnabled(mouseElm.getEditInfo(0) != null);
    	    	    elmFlipMenuItem .setEnabled(mouseElm.getPostCount() == 2);
    	    	    elmSplitMenuItem.setEnabled(canSplit(mouseElm));
    	    	    elmSliderMenuItem.setEnabled(sliderItemEnabled(mouseElm));
    	    	    contextPanel=new PopupPanel(true);
    	    	    contextPanel.add(elmMenuBar);
    	    	    contextPanel.setPopupPosition(menuClientX, menuClientY);
    	    	    contextPanel.show();
    	    	} else {
    	    	    ScopeElm s = (ScopeElm) mouseElm;
    	    	    if (s.elmScope.canMenu()) {
    	    		menuPlot = s.elmScope.selectedPlot;
    	    		scopePopupMenu.doScopePopupChecks(true, false, false, false, s.elmScope);
    			contextPanel=new PopupPanel(true);
    			contextPanel.add(scopePopupMenu.getMenuBar());
    			contextPanel.setPopupPosition(menuClientX, menuClientY);
    			contextPanel.show();
    	    	    }
    	    	}
    	} else {
    		doMainMenuChecks();
    		contextPanel=new PopupPanel(true);
    		contextPanel.add(mainMenuBar);
    		x=Math.max(0, Math.min(menuClientX, cv.getCoordinateSpaceWidth()-400));
    		y=Math.max(0, Math.min(menuClientY,cv.getCoordinateSpaceHeight()-450));
    		contextPanel.setPopupPosition(x,y);
    		contextPanel.show();
    	}
    }

    boolean canSplit(CircuitElm ce) {
	if (!(ce instanceof WireElm))
	    return false;
	WireElm we = (WireElm) ce;
	if (we.x == we.x2 || we.y == we.y2)
	    return true;
	return false;
    }
    
    // 检查用户是否可以为该元件创建滑块
    boolean sliderItemEnabled(CircuitElm elm) {
	int i;
	
	// 防止混淆
	if (elm instanceof VarRailElm || elm instanceof PotElm)
	    return false;
	
	for (i = 0; ; i++) {
	    EditInfo ei = elm.getEditInfo(i);
	    if (ei == null)
		return false;
	    if (ei.canCreateAdjustable())
		return true;
	}
    }

    void longPress() {
	doPopupMenu();
    }
    
//    public void mouseClicked(MouseEvent e) {
    public void onClick(ClickEvent e) {
    	e.preventDefault();
//    	//IES - remove inteaction
////	if ( e.getClickCount() == 2 && !didSwitch )
////	    doEditMenu(e);
//	if (e.getNativeButton() == NativeEvent.BUTTON_LEFT) {
//	    if (mouseMode == MODE_SELECT || mouseMode == MODE_DRAG_SELECTED)
//		clearSelection();
//	}	
    	if ((e.getNativeButton() == NativeEvent.BUTTON_MIDDLE))
    		scrollValues(e.getNativeEvent().getClientX(), e.getNativeEvent().getClientY(), 0);
    }
    
    public void onDoubleClick(DoubleClickEvent e){
    	e.preventDefault();
 //   	if (!didSwitch && mouseElm != null)
    	if (mouseElm != null && !(mouseElm instanceof SwitchElm) && !noEditCheckItem.getState())
    		doEdit(mouseElm);
    }
    
//    public void mouseEntered(MouseEvent e) {
//    }
    
    public void onMouseOut(MouseOutEvent e) {
    	mouseCursorX=-1;
    }

    void clearMouseElm() {
    	scopeSelected = -1;
    	setMouseElm(null);
    	plotXElm = plotYElm = null;
    }
    
    int menuClientX, menuClientY;
    int menuX, menuY;
    
    public void onMouseDown(MouseDownEvent e) {
//    public void mousePressed(MouseEvent e) {
    	e.preventDefault();
    	
	stopElm = null; // 如果已停止，允许用户选择其他元件来修复电路 
    	menuX = menuClientX = e.getX();
    	menuY = menuClientY = e.getY();
    	mouseDownTime = System.currentTimeMillis();
    	
    	// 也许有人在另一个窗口做了复制？实际上应该在
    	// 窗口获得焦点时执行
    	enablePaste();
    	
    	if (e.getNativeButton() != NativeEvent.BUTTON_LEFT && e.getNativeButton() != NativeEvent.BUTTON_MIDDLE)
    		return;
    	
    	// 在移动设备上时设置 mouseElm
    	mouseSelect(e);
    	
    	mouseDragging=true;
    	didSwitch = false;
	
    	if (mouseWasOverSplitter) {
    		tempMouseMode = MODE_DRAG_SPLITTER;
    		return;
    	}
	if (e.getNativeButton() == NativeEvent.BUTTON_LEFT) {
//	    // 左键
	    tempMouseMode = mouseMode;
	    if (e.isAltKeyDown() && e.isMetaKeyDown())
		tempMouseMode = MODE_DRAG_COLUMN;
	    else if (e.isAltKeyDown() && e.isShiftKeyDown())
		tempMouseMode = MODE_DRAG_ROW;
	    else if (e.isShiftKeyDown())
		tempMouseMode = MODE_SELECT;
	    else if (e.isAltKeyDown())
		tempMouseMode = MODE_DRAG_ALL;
	    else if (e.isControlKeyDown() || e.isMetaKeyDown())
		tempMouseMode = MODE_DRAG_POST;
	} else
	    tempMouseMode = MODE_DRAG_ALL;
	

	if (noEditCheckItem.getState())
	    tempMouseMode = MODE_SELECT;
	
	if (!(dialogIsShowing()) && ((scopeSelected != -1 && scopes[scopeSelected].cursorInSettingsWheel()) ||
		( scopeSelected == -1 && mouseElm instanceof ScopeElm && ((ScopeElm)mouseElm).elmScope.cursorInSettingsWheel()))){
	    if (noEditCheckItem.getState())
		return;
	    Scope s;
	    if (scopeSelected != -1)
		s=scopes[scopeSelected];
	    else 
		s=((ScopeElm)mouseElm).elmScope;
	    s.properties();
	    clearSelection();
	    mouseDragging=false;
	    return;
	}

	int gx = inverseTransformX(e.getX());
	int gy = inverseTransformY(e.getY());
	if (doSwitch(gx, gy)) {
	    // 在把鼠标模式改为 MODE_DRAG_POST 之前做这件事！否则当我们点击逻辑输入时，
	    // 它们会在整个电路上添加圆点！
            didSwitch = true;
	    return;
	}
	
	// IES - 在选中模式下，如果调整大小手柄间距足够远且鼠标在其上方，则抓取它们
	if (tempMouseMode == MODE_SELECT && mouseElm!=null && !noEditCheckItem.getState() &&
		mouseElm.getHandleGrabbedClose(gx, gy, POSTGRABSQ, MINPOSTGRABSIZE) >=0 &&
		!anySelectedButMouse() && (mouseElm instanceof ScopeElm || mouseElm.getPostCount() > 1))
	    tempMouseMode = MODE_DRAG_POST;
	
	if (tempMouseMode != MODE_SELECT && tempMouseMode != MODE_DRAG_SELECTED)
	    clearSelection();

	pushUndo();
	initDragGridX = gx;
	initDragGridY = gy;
	dragging = true;
	if (tempMouseMode !=MODE_ADD_ELM)
		return;
//	
	int x0 = snapGrid(gx);
	int y0 = snapGrid(gy);
	if (!circuitArea.contains(e.getX(), e.getY()))
	    return;

	try {
	    dragElm = constructElement(mouseModeStr, x0, y0);
	} catch (Exception ex) {
	    debugger();
	}
    }

 
    
    
    
    void doMainMenuChecks() {
	// 用于在电路不可编辑时禁用绘制菜单项的代码，但此版本未使用，因为它
	// 会弹出一个对话框来代替（见 menuPerformed）。
//    	int c = mainMenuItems.size();
//    	int i;
//    	for (i=0; i<c ; i++) {
//    	    	String s = mainMenuItemNames.get(i);
//    		mainMenuItems.get(i).setState(s==mouseModeStr);
//    		if (s.length() > 3 && s.substring(s.length()-3)=="Elm")
//    		    mainMenuItems.get(i).setEnabled(!noEditCheckItem.getState());
//    	}
    	stackAllItem.setEnabled(scopeCount > 1 && scopes[scopeCount-1].position > 0);
    	unstackAllItem.setEnabled(scopeCount > 1 && scopes[scopeCount-1].position != scopeCount -1);
    	combineAllItem.setEnabled(scopeCount > 1);
    	separateAllItem.setEnabled(scopeCount > 0);
    }
    
 
    public void onMouseUp(MouseUpEvent e) {
    	e.preventDefault();
    	mouseDragging=false;
    	
    	// 单击以清除选择
    	if (tempMouseMode == MODE_SELECT && selectedArea == null)
    	    clearSelection();

    	// cmd+点击 = 拆分导线
    	if (tempMouseMode == MODE_DRAG_POST && draggingPost == -1)
    	    doSplit(mouseElm);
    	
    	tempMouseMode = mouseMode;
    	selectedArea = null;
    	dragging = false;
    	boolean circuitChanged = false;
    	if (heldSwitchElm != null) {
    		heldSwitchElm.mouseUp();
    		heldSwitchElm = null;
    		circuitChanged = true;
    	}
    	if (dragElm != null) {
    		// 如果元件大小为零则不创建它
    		// IES - 并禁用之前的任何选择
    	    	if (dragElm.creationFailed()) {
    			dragElm.delete();
    			if (mouseMode == MODE_SELECT || mouseMode == MODE_DRAG_SELECTED)
    				clearSelection();
    		}
    		else {
    			elmList.addElement(dragElm);
    			dragElm.draggingDone();
    			circuitChanged = true;
    			writeRecoveryToStorage();
    			unsavedChanges = true;
    		}
    		dragElm = null;
    	}
    	if (circuitChanged)
    		needAnalyze();
    	if (dragElm != null)
    		dragElm.delete();
    	dragElm = null;
    	repaint();
    }
    
    public void onMouseWheel(MouseWheelEvent e) {
    	e.preventDefault();
    	
    	// 一旦开始缩放，在一段时间内不允许鼠标滚轮的其他用途，
    	// 以免缩放时不小心修改电阻值
    	boolean zoomOnly = System.currentTimeMillis() < zoomTime+1000;
    	
    	if (noEditCheckItem.getState())
    	    zoomOnly = true;
    	
    	if (!zoomOnly)
    	    scrollValues(e.getNativeEvent().getClientX(), e.getNativeEvent().getClientY(), e.getDeltaY());
    	
    	if (mouseElm instanceof MouseWheelHandler && !zoomOnly)
    		((MouseWheelHandler) mouseElm).onMouseWheel(e);
    	else if (scopeSelected != -1 && !zoomOnly)
    	    scopes[scopeSelected].onMouseWheel(e);
    	else if (!dialogIsShowing()) {
    	    zoomCircuit(-e.getDeltaY());
    	    zoomTime = System.currentTimeMillis();
   	}
    	repaint();
    }

    void zoomCircuit(int dy) {
	double newScale;
    	double oldScale = transform[0];
    	double val = dy*.01;
    	newScale = Math.max(oldScale+val, .2);
    	newScale = Math.min(newScale, 2.5);
    	setCircuitScale(newScale);
    }
    
    void setCircuitScale(double newScale) {
	int cx = inverseTransformX(circuitArea.width/2);
	int cy = inverseTransformY(circuitArea.height/2);
	transform[0] = transform[3] = newScale;

	// 调整平移量以保持屏幕中心不变
	// 逆变换 = (x-t4)/t0
	transform[4] = circuitArea.width /2 - cx*newScale;
	transform[5] = circuitArea.height/2 - cy*newScale;
    }
    
    void setPowerBarEnable() {
    	if (powerCheckItem.getState()) {
    	    powerLabel.setStyleName("disabled", false);
    	    powerBar.enable();
    	} else {
    	    powerLabel.setStyleName("disabled", true);
    	    powerBar.disable();
    	}
    }

    void scrollValues(int x, int y, int deltay) {
    	if (mouseElm!=null && !dialogIsShowing() && scopeSelected == -1)
    		if (mouseElm instanceof ResistorElm || mouseElm instanceof CapacitorElm ||  mouseElm instanceof InductorElm) {
    			scrollValuePopup = new ScrollValuePopup(x, y, deltay, mouseElm, this);
    		}
    }
    
    void enableItems() {
    }
    
    void setGrid() {
	gridSize = (smallGridCheckItem.getState()) ? 8 : 16;
	gridMask = ~(gridSize-1);
	gridRound = gridSize/2-1;
    }

    void pushUndo() {
    	redoStack.removeAllElements();
    	String s = dumpCircuit();
    	if (undoStack.size() > 0 &&
    			s.compareTo(undoStack.lastElement()) == 0)
    		return;
    	undoStack.add(s);
    	enableUndoRedo();
    }

    void doUndo() {
    	if (undoStack.size() == 0)
    		return;
    	redoStack.add(dumpCircuit());
    	String s = undoStack.remove(undoStack.size()-1);
    	readCircuit(s, RC_NO_CENTER);
    	enableUndoRedo();
    }

    void doRedo() {
    	if (redoStack.size() == 0)
    		return;
    	undoStack.add(dumpCircuit());
    	String s = redoStack.remove(redoStack.size()-1);
    	readCircuit(s, RC_NO_CENTER);
    	enableUndoRedo();
    }

    void doRecover() {
	pushUndo();
	readCircuit(recovery);
	allowSave(false);
	recoverItem.setEnabled(false);
    }
    
    void enableUndoRedo() {
    	redoItem.setEnabled(redoStack.size() > 0);
    	undoItem.setEnabled(undoStack.size() > 0);
    }

    void setMouseMode(int mode)
    {
    	mouseMode = mode;
    	if ( mode == MODE_ADD_ELM ) {
    		setCursorStyle("cursorCross");
    	} else {
    		setCursorStyle("cursorPointer");
    	}
    }
    
    void setCursorStyle(String s) {
    	if (lastCursorStyle!=null)
    		cv.removeStyleName(lastCursorStyle);
    	cv.addStyleName(s);
    	lastCursorStyle=s;
    }
    


    void setMenuSelection() {
    	if (menuElm != null) {
    		if (menuElm.selected)
    			return;
    		clearSelection();
    		menuElm.setSelected(true);
    	}
    }

    void doCut() {
    	int i;
    	pushUndo();
    	setMenuSelection();
    	clipboard = "";
    	for (i = elmList.size()-1; i >= 0; i--) {
    		CircuitElm ce = getElm(i);
    		// ScopeElm 的剪切粘贴效果不好，因为它们在 dump 中通过编号
    		// 引用父元件时会出错。暂时先忽略它们，
    		// 直到我想出更好的办法
    		if (willDelete(ce) && !(ce instanceof ScopeElm) ) {
    			clipboard += ce.dump() + "\n";
    		}
    	}
    	writeClipboardToStorage();
    	doDelete(true);
    	enablePaste();
    }

    void writeClipboardToStorage() {
    	Storage stor = Storage.getLocalStorageIfSupported();
    	if (stor == null)
    		return;
    	stor.setItem("circuitClipboard", clipboard);
    }
    
    void readClipboardFromStorage() {
    	Storage stor = Storage.getLocalStorageIfSupported();
    	if (stor == null)
    		return;
    	clipboard = stor.getItem("circuitClipboard");
    }

    void writeRecoveryToStorage() {
	console("write recovery");
    	Storage stor = Storage.getLocalStorageIfSupported();
    	if (stor == null)
    		return;
    	String s = dumpCircuit();
    	stor.setItem("circuitRecovery", s);
    }

    void readRecovery() {
	Storage stor = Storage.getLocalStorageIfSupported();
	if (stor == null)
		return;
	recovery = stor.getItem("circuitRecovery");
    }


    void deleteUnusedScopeElms() {
	// 移除不再存在的元件对应的 scopeElm
	for (int i = elmList.size()-1; i >= 0; i--) {
    		CircuitElm ce = getElm(i);
    		if (ce instanceof ScopeElm && (((ScopeElm) ce).elmScope.needToRemove() )) {
    			ce.delete();
    			elmList.removeElementAt(i);
    		}
    	}
	
    }
    
    void doDelete(boolean pushUndoFlag) {
    	int i;
    	if (pushUndoFlag)
    	    pushUndo();
    	boolean hasDeleted = false;

    	for (i = elmList.size()-1; i >= 0; i--) {
    		CircuitElm ce = getElm(i);
    		if (willDelete(ce)) {
    		    	if (ce.isMouseElm())
    		    	    setMouseElm(null);
    			ce.delete();
    			elmList.removeElementAt(i);
    			hasDeleted = true;
    		}
    	}
    	if ( hasDeleted ) {
    	    deleteUnusedScopeElms();
    	    needAnalyze();
    	    writeRecoveryToStorage();
    	}    
    }
    
    boolean willDelete( CircuitElm ce ) {
	// 这个元件是否在要删除的列表中。
	// 这与之前版本的逻辑不同：旧版本最初只删除选中的元件
	//（可能包括 mouseElm），然后如果没有选中的元件，
	// 则删除 mouseElm。不确定这对用户体验
	// 有没有实际帮助。
	//
	// 顺便说一句，旧逻辑还可能导致 mouseElm 指向已删除的元件。
	return ce.isSelected() || ce.isMouseElm();
    }
    
    String copyOfSelectedElms() {
	String r="";
	CustomLogicModel.clearDumpedFlags();
	CustomCompositeModel.clearDumpedFlags();
	DiodeModel.clearDumpedFlags();
	TransistorModel.clearDumpedFlags();
	for (int i = elmList.size()-1; i >= 0; i--) {
	    CircuitElm ce = getElm(i);
	    String m = ce.dumpModel();
	    if (m != null && !m.isEmpty())
		r += m + "\n";
	    // 关于为什么不复制 ScopeElm，请参阅 doCut 中的说明
	    if (ce.isSelected() && !(ce instanceof ScopeElm))
		r += ce.dump() + "\n";
	}
	return r;
    }
    
    void doCopy() {
    	// 如果使用上下文菜单复制单个元件，完成后清除选择
    	boolean clearSel = (menuElm != null && !menuElm.selected);
    	
    	setMenuSelection();
    	clipboard=copyOfSelectedElms();
    	
    	if (clearSel)
    	    clearSelection();
    	
    	writeClipboardToStorage();
    	enablePaste();
    }

    void enablePaste() {
    	if (clipboard == null || clipboard.length() == 0)
    		readClipboardFromStorage();
    	pasteItem.setEnabled(clipboard != null && clipboard.length() > 0);
    }

    void doDuplicate() {
    	String s;
    	setMenuSelection();
    	s=copyOfSelectedElms();
    	doPaste(s);
    }
    
    void doPaste(String dump) {
    	pushUndo();
    	clearSelection();
    	int i;
    	Rectangle oldbb = null;
    	
    	// 获取旧边界框
    	for (i = 0; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		Rectangle bb = ce.getBoundingBox();
    		if (oldbb != null)
    			oldbb = oldbb.union(bb);
    		else
    			oldbb = bb;
    	}
    	
    	// 添加新元件
    	int oldsz = elmList.size();
    	if (dump != null)
    	    readCircuit(dump, RC_RETAIN);
    	else {
    	    readClipboardFromStorage();
    	    readCircuit(clipboard, RC_RETAIN);
    	}

    	// 选中新元件并获取其边界框
    	Rectangle newbb = null;
    	for (i = oldsz; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		ce.setSelected(true);
    		Rectangle bb = ce.getBoundingBox();
    		if (newbb != null)
    			newbb = newbb.union(bb);
    		else
    			newbb = bb;
    	}
    	
    	if (oldbb != null && newbb != null && oldbb.intersects(newbb)) {
    		// 为新元件在边缘找一个位置
    		int dx = 0, dy = 0;
    		int spacew = circuitArea.width - oldbb.width - newbb.width;
    		int spaceh = circuitArea.height - oldbb.height - newbb.height;
    		if (spacew > spaceh)
    			dx = snapGrid(oldbb.x + oldbb.width  - newbb.x + gridSize);
    		else
    			dy = snapGrid(oldbb.y + oldbb.height - newbb.y + gridSize);
    		
    		// 如果可能，把新元件移到鼠标附近
    		if (mouseCursorX > 0 && circuitArea.contains(mouseCursorX, mouseCursorY)) {
    	    	    int gx = inverseTransformX(mouseCursorX);
    	    	    int gy = inverseTransformY(mouseCursorY);
    	    	    int mdx = snapGrid(gx-(newbb.x+newbb.width/2));
    	    	    int mdy = snapGrid(gy-(newbb.y+newbb.height/2));
    	    	    for (i = oldsz; i != elmList.size(); i++) {
    	    		if (!getElm(i).allowMove(mdx, mdy))
    	    		    break;
    	    	    }
    	    	    if (i == elmList.size()) {
    	    		dx = mdx;
    	    		dy = mdy;
    	    	    }
    		}
    		
    		// 移动新元件
    		for (i = oldsz; i != elmList.size(); i++) {
    			CircuitElm ce = getElm(i);
    			ce.move(dx, dy);
    		}
    		
    		// 使电路居中
    	//	handleResize();
    	}
    	needAnalyze();
    	writeRecoveryToStorage();
    }

    void clearSelection() {
	int i;
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    ce.setSelected(false);
	}
    }
    
    void doSelectAll() {
    	int i;
    	for (i = 0; i != elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		ce.setSelected(true);
    	}
    }
    
    boolean anySelectedButMouse() {
    	for (int i=0; i != elmList.size(); i++)
    		if (getElm(i)!= mouseElm && getElm(i).selected)
    			return true;
    	return false;
    }

//    public void keyPressed(KeyEvent e) {}
//    public void keyReleased(KeyEvent e) {}
    
    boolean dialogIsShowing() {
    	if (editDialog!=null && editDialog.isShowing())
    		return true;
    	if (sliderDialog!=null && sliderDialog.isShowing())
		return true;
    	if (customLogicEditDialog!=null && customLogicEditDialog.isShowing())
		return true;
    	if (diodeModelEditDialog!=null && diodeModelEditDialog.isShowing())
		return true;
       	if (dialogShowing != null && dialogShowing.isShowing())
       		return true;
    	if (contextPanel!=null && contextPanel.isShowing())
    		return true;
    	if (scrollValuePopup != null && scrollValuePopup.isShowing())
    		return true;
    	if (aboutBox !=null && aboutBox.isShowing())
    		return true;
    	if (importFromDropboxDialog != null && importFromDropboxDialog.isShowing())
    		return true;
    	return false;
    }
    
    public void onPreviewNativeEvent(NativePreviewEvent e) {
    	int cc=e.getNativeEvent().getCharCode();
    	int t=e.getTypeInt();
    	int code=e.getNativeEvent().getKeyCode();
    	if (dialogIsShowing()) {
    		if (scrollValuePopup != null && scrollValuePopup.isShowing() &&
    				(t & Event.ONKEYDOWN)!=0) {
    			if (code==KEY_ESCAPE || code==KEY_SPACE)
    				scrollValuePopup.close(false);
    			if (code==KEY_ENTER)
    				scrollValuePopup.close(true);
    		}
    		
    		// 处理编辑对话框的 escape/enter 键
    		// 可能同时显示多个编辑对话框，选择最前面的一个
    		EditDialog dlg = editDialog;
    		if (diodeModelEditDialog != null)
    		    dlg = diodeModelEditDialog;
    		if (customLogicEditDialog != null)
    		    dlg = customLogicEditDialog;
    		if (dlg!=null && dlg.isShowing() &&
    				(t & Event.ONKEYDOWN)!=0) {
    			if (code==KEY_ESCAPE)
    			    dlg.closeDialog();
    			if (code==KEY_ENTER)
    			    dlg.enterPressed();
    		}
    		return;
    	}
    	
    	if ((t&Event.ONKEYPRESS)!=0) {
		if (cc=='-') {
    		    menuPerformed("key", "zoomout");
    		    e.cancel();
    		}
    		if (cc=='+' || cc == '=') {
    		    menuPerformed("key", "zoomin");
    		    e.cancel();
    		}
		if (cc=='0') {
    		    menuPerformed("key", "zoom100");
    		    e.cancel();
		}
    	}
    	
    	// 禁用编辑时忽略所有其他快捷键
    	if (noEditCheckItem.getState())
    	    return;

    	if ((t & Event.ONKEYDOWN)!=0) {
    		if (code==KEY_BACKSPACE || code==KEY_DELETE) {
    		    if (scopeSelected != -1) {
    			// 将选中示波器时按 DELETE 键视为“移除示波器”，而不是删除
    			scopes[scopeSelected].setElm(null);
    			scopeSelected = -1;
    		    } else {
    		    	menuElm = null;
    		    	pushUndo();
    			doDelete(true);
    			e.cancel();
    		    }
    		}
    		if (code==KEY_ESCAPE){
    			setMouseMode(MODE_SELECT);
    			mouseModeStr = "Select";
    			tempMouseMode = mouseMode;
    			e.cancel();
    		}

    		if (e.getNativeEvent().getCtrlKey() || e.getNativeEvent().getMetaKey()) {
    			if (code==KEY_C) {
    				menuPerformed("key", "copy");
    				e.cancel();
    			}
    			if (code==KEY_X) {
    				menuPerformed("key", "cut");
    				e.cancel();
    			}
    			if (code==KEY_V) {
    				menuPerformed("key", "paste");
    				e.cancel();
    			}
    			if (code==KEY_Z) {
    				menuPerformed("key", "undo");
    				e.cancel();
    			}
    			if (code==KEY_Y) {
    				menuPerformed("key", "redo");
    				e.cancel();
    			}
    			if (code==KEY_D) {
    			    	menuPerformed("key", "duplicate");
    			    	e.cancel();
    			}
    			if (code==KEY_A) {
    				menuPerformed("key", "selectAll");
    				e.cancel();
    			}
    		}
    	}
    	if ((t&Event.ONKEYPRESS)!=0) {
    		if (cc>32 && cc<127){
    			String c=shortcuts[cc];
    			e.cancel();
    			if (c==null)
    				return;
    			setMouseMode(MODE_ADD_ELM);
    			mouseModeStr=c;
    			tempMouseMode = mouseMode;
    		}
    		if (cc==32) {
			    setMouseMode(MODE_SELECT);
			    mouseModeStr = "Select";
			    tempMouseMode = mouseMode;
			    e.cancel();    			
    		}
    	}
    }
    
    // 通过高斯消元法把矩阵分解为上三角矩阵和下三角矩阵。
    // 进入时，a[0..n-1][0..n-1] 是要分解的
    // 矩阵。ipvt[] 返回一个主元索引的整数向量，
    // 供 lu_solve() 例程使用。
    static boolean lu_factor(double a[][], int n, int ipvt[]) {
	int i,j,k;
	
	// 通过扫描行来检查可能的奇异矩阵，
	// 即全为零的行
	for (i = 0; i != n; i++) { 
	    boolean row_all_zeros = true;
	    for (j = 0; j != n; j++) {
		if (a[i][j] != 0) {
		    row_all_zeros = false;
		    break;
		}
	    }
	    // 如果全为零，则是奇异矩阵
	    if (row_all_zeros)
		return false;
	}
	
        // 使用 Crout 方法；遍历各列
	for (j = 0; j != n; j++) {
	    
	    // 计算这一列的上三角元素
	    for (i = 0; i != j; i++) {
		double q = a[i][j];
		for (k = 0; k != i; k++)
		    q -= a[i][k]*a[k][j];
		a[i][j] = q;
	    }

	    // 计算这一列的下三角元素
	    double largest = 0;
	    int largestRow = -1;
	    for (i = j; i != n; i++) {
		double q = a[i][j];
		for (k = 0; k != j; k++)
		    q -= a[i][k]*a[k][j];
		a[i][j] = q;
		double x = Math.abs(q);
		if (x >= largest) {
		    largest = x;
		    largestRow = i;
		}
	    }
	    
	    // 主元选取
	    if (j != largestRow) {
		double x;
		for (k = 0; k != n; k++) {
		    x = a[largestRow][k];
		    a[largestRow][k] = a[j][k];
		    a[j][k] = x;
		}
	    }

	    // 记录行的交换
	    ipvt[j] = largestRow;

	    // 避免出现零
	    if (a[j][j] == 0.0) {
		System.out.println("avoided zero");
		a[j][j]=1e-18;
	    }

	    if (j != n-1) {
		double mult = 1.0/a[j][j];
		for (i = j+1; i != n; i++)
		    a[i][j] *= mult;
	    }
	}
	return true;
    }

    // 使用 lu_factor 预先执行的 LU 分解求解 n 个线性方程组。
    // 输入时，b[0..n-1] 是方程组的右端项，
    // 输出时包含解。
    static void lu_solve(double a[][], int n, int ipvt[], double b[]) {
	int i;

	// 查找第一个非零的 b 元素
	for (i = 0; i != n; i++) {
	    int row = ipvt[i];

	    double swap = b[row];
	    b[row] = b[i];
	    b[i] = swap;
	    if (swap != 0)
		break;
	}
	
	int bi = i++;
	for (; i < n; i++) {
	    int row = ipvt[i];
	    int j;
	    double tot = b[row];
	    
	    b[row] = b[i];
	    // 使用下三角矩阵进行前代
	    for (j = bi; j < i; j++)
		tot -= a[i][j]*b[j];
	    b[i] = tot;
	}
	for (i = n-1; i >= 0; i--) {
	    double tot = b[i];
	    
	    // 使用上三角矩阵进行回代
	    int j;
	    for (j = i+1; j != n; j++)
		tot -= a[i][j]*b[j];
	    b[i] = tot/a[i][i];
	}
    }

    
    void createNewLoadFile() {
    	// 这是一个 hack，用来修复我认为是 <INPUT FILE 元素中的 bug
    	// 重新加载同一个文件不会产生 change 事件，所以除非销毁原来的
    	// input 元素并用新的替换，否则无法两次导入同一个文件
    	int idx=verticalPanel.getWidgetIndex(loadFileInput);
    	LoadFile newlf=new LoadFile(this);
    	verticalPanel.insert(newlf, idx);
    	verticalPanel.remove(idx+1);
    	loadFileInput=newlf;
    }

    void addWidgetToVerticalPanel(Widget w) {
    	if (iFrame!=null) {
    		int i=verticalPanel.getWidgetIndex(iFrame);
    		verticalPanel.insert(w, i);
    		setiFrameHeight();
    	}
    	else
    		verticalPanel.add(w);
    }
    
    void removeWidgetFromVerticalPanel(Widget w){
    	verticalPanel.remove(w);
    	if (iFrame!=null)
    		setiFrameHeight();
    }
    
    public static CircuitElm createCe(int tint, int x1, int y1, int x2, int y2, int f, StringTokenizer st) {
	switch (tint) {
    	case 'A': return new AntennaElm(x1, y1, x2, y2, f, st);
    	case 'I': return new InverterElm(x1, y1, x2, y2, f, st);
    	case 'L': return new LogicInputElm(x1, y1, x2, y2, f, st);
    	case 'M': return new LogicOutputElm(x1, y1, x2, y2, f, st);
    	case 'O': return new OutputElm(x1, y1, x2, y2, f, st);
    	case 'R': return new RailElm(x1, y1, x2, y2, f, st);
    	case 'S': return new Switch2Elm(x1, y1, x2, y2, f, st);
    	case 'T': return new TransformerElm(x1, y1, x2, y2, f, st);
    	case 'a': return new OpAmpElm(x1, y1, x2, y2, f, st);
    	case 'b': return new BoxElm(x1, y1, x2, y2, f, st);
    	case 'c': return new CapacitorElm(x1, y1, x2, y2, f, st);   	
    	case 'd': return new DiodeElm(x1, y1, x2, y2, f, st);
    	case 'f': return new MosfetElm(x1, y1, x2, y2, f, st);
    	case 'g': return new GroundElm(x1, y1, x2, y2, f, st);
    	case 'i': return new CurrentElm(x1, y1, x2, y2, f, st);
    	case 'j': return new JfetElm(x1, y1, x2, y2, f, st);
    	case 'l': return new InductorElm(x1, y1, x2, y2, f, st);
    	case 'm': return new MemristorElm(x1, y1, x2, y2, f, st);
    	case 'n': return new NoiseElm(x1, y1, x2, y2, f, st);
    	case 'p': return new ProbeElm(x1, y1, x2, y2, f, st);
    	case 'r': return new ResistorElm(x1, y1, x2, y2, f, st);
    	case 's': return new SwitchElm(x1, y1, x2, y2, f, st);
    	case 't': return new TransistorElm(x1, y1, x2, y2, f, st);
    	case 'v': return new VoltageElm(x1, y1, x2, y2, f, st);
    	case 'w': return new WireElm(x1, y1, x2, y2, f, st);
    	case 'x': return new TextElm(x1, y1, x2, y2, f, st);
    	case 'z': return new ZenerElm(x1, y1, x2, y2, f, st);
    	case 150: return new AndGateElm(x1, y1, x2, y2, f, st);
    	case 151: return new NandGateElm(x1, y1, x2, y2, f, st);
    	case 152: return new OrGateElm(x1, y1, x2, y2, f, st);
    	case 153: return new NorGateElm(x1, y1, x2, y2, f, st);
    	case 154: return new XorGateElm(x1, y1, x2, y2, f, st);
    	case 155: return new DFlipFlopElm(x1, y1, x2, y2, f, st);
    	case 156: return new JKFlipFlopElm(x1, y1, x2, y2, f, st);
    	case 157: return new SevenSegElm(x1, y1, x2, y2, f, st);
    	case 158: return new VCOElm(x1, y1, x2, y2, f, st);
    	case 159: return new AnalogSwitchElm(x1, y1, x2, y2, f, st);
    	case 160: return new AnalogSwitch2Elm(x1, y1, x2, y2, f, st);
    	case 161: return new PhaseCompElm(x1, y1, x2, y2, f, st);
    	case 162: return new LEDElm(x1, y1, x2, y2, f, st);
    	case 163: return new RingCounterElm(x1, y1, x2, y2, f, st);
    	case 164: return new CounterElm(x1, y1, x2, y2, f, st);
    	case 165: return new TimerElm(x1, y1, x2, y2, f, st);
    	case 166: return new DACElm(x1, y1, x2, y2, f, st);
    	case 167: return new ADCElm(x1, y1, x2, y2, f, st);
    	case 168: return new LatchElm(x1, y1, x2, y2, f, st);
    	case 169: return new TappedTransformerElm(x1, y1, x2, y2, f, st);
    	case 170: return new SweepElm(x1, y1, x2, y2, f, st);
    	case 171: return new TransLineElm(x1, y1, x2, y2, f, st);
    	case 172: return new VarRailElm(x1, y1, x2, y2, f, st);
    	case 173: return new TriodeElm(x1, y1, x2, y2, f, st);
    	case 174: return new PotElm(x1, y1, x2, y2, f, st);
    	case 175: return new TunnelDiodeElm(x1, y1, x2, y2, f, st);
    	case 176: return new VaractorElm(x1, y1, x2, y2, f, st);
    	case 177: return new SCRElm(x1, y1, x2, y2, f, st);
    	case 178: return new RelayElm(x1, y1, x2, y2, f, st);
    	case 179: return new CC2Elm(x1, y1, x2, y2, f, st);
    	case 180: return new TriStateElm(x1, y1, x2, y2, f, st);
    	case 181: return new LampElm(x1, y1, x2, y2, f, st);
    	case 182: return new SchmittElm(x1, y1, x2, y2, f, st);
    	case 183: return new InvertingSchmittElm(x1, y1, x2, y2, f, st);
    	case 184: return new MultiplexerElm(x1, y1, x2, y2, f, st);
    	case 185: return new DeMultiplexerElm(x1, y1, x2, y2, f, st);
    	case 186: return new PisoShiftElm(x1, y1, x2, y2, f, st);
    	case 187: return new SparkGapElm(x1, y1, x2, y2, f, st);
    	case 188: return new SeqGenElm(x1, y1, x2, y2, f, st);
    	case 189: return new SipoShiftElm(x1, y1, x2, y2, f, st);
    	case 193: return new TFlipFlopElm(x1, y1, x2, y2, f, st);
    	case 194: return new MonostableElm(x1, y1, x2, y2, f, st);
    	case 195: return new HalfAdderElm(x1, y1, x2, y2, f, st);
    	case 196: return new FullAdderElm(x1, y1, x2, y2, f, st);
    	case 197: return new SevenSegDecoderElm(x1, y1, x2, y2, f, st);
    	case 200: return new AMElm(x1, y1, x2, y2, f, st);
    	case 201: return new FMElm(x1, y1, x2, y2, f, st);
    	case 203: return new DiacElm(x1, y1, x2, y2, f, st);
    	case 206: return new TriacElm(x1, y1, x2, y2, f, st);
    	case 207: return new LabeledNodeElm(x1, y1, x2, y2, f, st);
    	case 208: return new CustomLogicElm(x1, y1, x2, y2, f, st);
    	case 209: return new PolarCapacitorElm(x1, y1, x2, y2, f, st);   	
    	case 210: return new DataRecorderElm(x1, y1, x2, y2, f, st);
    	case 211: return new AudioOutputElm(x1, y1, x2, y2, f, st);
    	case 212: return new VCVSElm(x1, y1, x2, y2, f, st);
    	case 213: return new VCCSElm(x1, y1, x2, y2, f, st);
    	case 214: return new CCVSElm(x1, y1, x2, y2, f, st);
    	case 215: return new CCCSElm(x1, y1, x2, y2, f, st);
    	case 216: return new OhmMeterElm(x1, y1, x2, y2, f, st);
	case 350: return new ThermistorNTCElm(x1, y1, x2, y2, f, st);
    	case 368: return new TestPointElm(x1, y1, x2, y2, f, st);
    	case 370: return new AmmeterElm(x1, y1, x2, y2, f, st);
	case 374: return new LDRElm(x1, y1, x2, y2, f, st);
    	case 400: return new DarlingtonElm(x1, y1, x2, y2, f, st);
    	case 401: return new ComparatorElm(x1, y1, x2, y2, f, st);
    	case 402: return new OTAElm(x1, y1, x2, y2, f, st);
    	case 403: return new ScopeElm(x1, y1, x2, y2, f, st);
    	case 404: return new FuseElm(x1, y1, x2, y2, f, st);
    	case 405: return new LEDArrayElm(x1, y1, x2, y2, f, st);
    	case 406: return new CustomTransformerElm(x1, y1, x2, y2, f, st);
    	case 407: return new OptocouplerElm(x1, y1, x2, y2, f, st);
    	case 408: return new StopTriggerElm(x1, y1, x2, y2, f, st);
    	case 409: return new OpAmpRealElm(x1, y1, x2, y2, f, st);
    	case 410: return new CustomCompositeElm(x1, y1, x2, y2, f, st);
    	case 411: return new AudioInputElm(x1, y1, x2, y2, f, st);
    	case 412: return new CrystalElm(x1, y1, x2, y2, f, st);
    	case 413: return new SRAMElm(x1, y1, x2, y2, f, st);
    	case 414: return new TimeDelayRelayElm(x1, y1, x2, y2, f, st);
	case 415: return new DCMotorElm(x1, y1, x2, y2, f, st);
	case 416: return new MBBSwitchElm(x1, y1, x2, y2, f, st);
        }
    	return null;
    }

    public static CircuitElm constructElement(String n, int x1, int y1){
    	if (n=="GroundElm")
    		return (CircuitElm) new GroundElm(x1, y1);
    	if (n=="ResistorElm")
    		return (CircuitElm) new ResistorElm(x1, y1);
    	if (n=="RailElm")
    		return (CircuitElm) new RailElm(x1, y1);
    	if (n=="SwitchElm")
    		return (CircuitElm) new SwitchElm(x1, y1);
    	if (n=="Switch2Elm")
    		return (CircuitElm) new Switch2Elm(x1, y1);
    	if (n=="MBBSwitchElm")
    		return (CircuitElm) new MBBSwitchElm(x1, y1);
    	if (n=="NTransistorElm" || n == "TransistorElm")
    		return (CircuitElm) new NTransistorElm(x1, y1);
    	if (n=="PTransistorElm")
    		return (CircuitElm) new PTransistorElm(x1, y1);
    	if (n=="WireElm")
    		return (CircuitElm) new WireElm(x1, y1);
    	if (n=="CapacitorElm")
    		return (CircuitElm) new CapacitorElm(x1, y1);
    	if (n=="PolarCapacitorElm")
		return (CircuitElm) new PolarCapacitorElm(x1, y1);
    	if (n=="InductorElm")
    		return (CircuitElm) new InductorElm(x1, y1);
    	if (n=="DCVoltageElm" || n=="VoltageElm")
    		return (CircuitElm) new DCVoltageElm(x1, y1);
    	if (n=="VarRailElm")
    		return (CircuitElm) new VarRailElm(x1, y1);
    	if (n=="PotElm")
    		return (CircuitElm) new PotElm(x1, y1);
    	if (n=="OutputElm")
    		return (CircuitElm) new OutputElm(x1, y1);
    	if (n=="CurrentElm")
    		return (CircuitElm) new CurrentElm(x1, y1);
    	if (n=="ProbeElm")
    		return (CircuitElm) new ProbeElm(x1, y1);
    	if (n=="DiodeElm")
    		return (CircuitElm) new DiodeElm(x1, y1);
    	if (n=="ZenerElm")
    		return (CircuitElm) new ZenerElm(x1, y1);
    	if (n=="ACVoltageElm")
    		return (CircuitElm) new ACVoltageElm(x1, y1);
    	if (n=="ACRailElm")
    		return (CircuitElm) new ACRailElm(x1, y1);
    	if (n=="SquareRailElm")
    		return (CircuitElm) new SquareRailElm(x1, y1);
    	if (n=="SweepElm")
    		return (CircuitElm) new SweepElm(x1, y1);
    	if (n=="LEDElm")
    		return (CircuitElm) new LEDElm(x1, y1);
    	if (n=="AntennaElm")
    		return (CircuitElm) new AntennaElm(x1, y1);
    	if (n=="LogicInputElm")
    		return (CircuitElm) new LogicInputElm(x1, y1);
    	if (n=="LogicOutputElm")
    		return (CircuitElm) new LogicOutputElm(x1, y1);
    	if (n=="TransformerElm")
    		return (CircuitElm) new TransformerElm(x1, y1);
    	if (n=="TappedTransformerElm")
    		return (CircuitElm) new TappedTransformerElm(x1, y1);
    	if (n=="TransLineElm")
    		return (CircuitElm) new TransLineElm(x1, y1);
    	if (n=="RelayElm")
    		return (CircuitElm) new RelayElm(x1, y1);
    	if (n=="MemristorElm")
    		return (CircuitElm) new MemristorElm(x1, y1);
    	if (n=="SparkGapElm")
    		return (CircuitElm) new SparkGapElm(x1, y1);
    	if (n=="ClockElm")
    		return (CircuitElm) new ClockElm(x1, y1);
    	if (n=="AMElm")
    		return (CircuitElm) new AMElm(x1, y1);
    	if (n=="FMElm")
    		return (CircuitElm) new FMElm(x1, y1);
    	if (n=="LampElm")
    		return (CircuitElm) new LampElm(x1, y1);
    	if (n=="PushSwitchElm")
    		return (CircuitElm) new PushSwitchElm(x1, y1);
    	if (n=="OpAmpElm")
    		return (CircuitElm) new OpAmpElm(x1, y1);
    	if (n=="OpAmpSwapElm")
    		return (CircuitElm) new OpAmpSwapElm(x1, y1);
    	if (n=="NMosfetElm" || n == "MosfetElm")
    		return (CircuitElm) new NMosfetElm(x1, y1);
    	if (n=="PMosfetElm")
    		return (CircuitElm) new PMosfetElm(x1, y1);
    	if (n=="NJfetElm" || n == "JfetElm")
    		return (CircuitElm) new NJfetElm(x1, y1);
    	if (n=="PJfetElm")
    		return (CircuitElm) new PJfetElm(x1, y1);
    	if (n=="AnalogSwitchElm")
    		return (CircuitElm) new AnalogSwitchElm(x1, y1);
    	if (n=="AnalogSwitch2Elm")
    		return (CircuitElm) new AnalogSwitch2Elm(x1, y1);
    	if (n=="SchmittElm")
    		return (CircuitElm) new SchmittElm(x1, y1);
    	if (n=="InvertingSchmittElm")
    		return (CircuitElm) new InvertingSchmittElm(x1, y1);
    	if (n=="TriStateElm")
    		return (CircuitElm) new TriStateElm(x1, y1);
    	if (n=="SCRElm")
    		return (CircuitElm) new SCRElm(x1, y1);
    	if (n=="DiacElm")
    		return (CircuitElm) new DiacElm(x1, y1);
    	if (n=="TriacElm")
    		return (CircuitElm) new TriacElm(x1, y1);
    	if (n=="TriodeElm")
    		return (CircuitElm) new TriodeElm(x1, y1);
    	if (n=="VaractorElm")
    	    	return (CircuitElm) new VaractorElm(x1, y1);
    	if (n=="TunnelDiodeElm")
    		return (CircuitElm) new TunnelDiodeElm(x1, y1);
    	if (n=="CC2Elm")
    		return (CircuitElm) new CC2Elm(x1, y1);
    	if (n=="CC2NegElm")
    		return (CircuitElm) new CC2NegElm(x1, y1);
    	if (n=="InverterElm")
    		return (CircuitElm) new InverterElm(x1, y1);
    	if (n=="NandGateElm")
    		return (CircuitElm) new NandGateElm(x1, y1);
    	if (n=="NorGateElm")
    		return (CircuitElm) new NorGateElm(x1, y1);
    	if (n=="AndGateElm")
    		return (CircuitElm) new AndGateElm(x1, y1);
    	if (n=="OrGateElm")
    		return (CircuitElm) new OrGateElm(x1, y1);
    	if (n=="XorGateElm")
    		return (CircuitElm) new XorGateElm(x1, y1);
    	if (n=="DFlipFlopElm")
    		return (CircuitElm) new DFlipFlopElm(x1, y1);
    	if (n=="JKFlipFlopElm")
    		return (CircuitElm) new JKFlipFlopElm(x1, y1);
    	if (n=="SevenSegElm")
    		return (CircuitElm) new SevenSegElm(x1, y1);
    	if (n=="MultiplexerElm")
    		return (CircuitElm) new MultiplexerElm(x1, y1);
    	if (n=="DeMultiplexerElm")
    		return (CircuitElm) new DeMultiplexerElm(x1, y1);
    	if (n=="SipoShiftElm")
    		return (CircuitElm) new SipoShiftElm(x1, y1);
    	if (n=="PisoShiftElm")
    		return (CircuitElm) new PisoShiftElm(x1, y1);
    	if (n=="PhaseCompElm")
    		return (CircuitElm) new PhaseCompElm(x1, y1);
    	if (n=="CounterElm")
    		return (CircuitElm) new CounterElm(x1, y1);
    	
	// 如果移除 RingCounterElm，会破坏子电路
    	// 如果移除 DecadeElm，会破坏菜单和用户保存的快捷键
    	if (n=="DecadeElm" || n=="RingCounterElm")
    		return (CircuitElm) new RingCounterElm(x1, y1);
    	
    	if (n=="TimerElm")
    		return (CircuitElm) new TimerElm(x1, y1);
    	if (n=="DACElm")
    		return (CircuitElm) new DACElm(x1, y1);
    	if (n=="ADCElm")
    		return (CircuitElm) new ADCElm(x1, y1);
    	if (n=="LatchElm")
    		return (CircuitElm) new LatchElm(x1, y1);
    	if (n=="SeqGenElm")
    		return (CircuitElm) new SeqGenElm(x1, y1);
    	if (n=="VCOElm")
    		return (CircuitElm) new VCOElm(x1, y1);
    	if (n=="BoxElm")
    		return (CircuitElm) new BoxElm(x1, y1);
    	if (n=="TextElm")
    		return (CircuitElm) new TextElm(x1, y1);
    	if (n=="TFlipFlopElm")
    		return (CircuitElm) new TFlipFlopElm(x1, y1);
    	if (n=="SevenSegDecoderElm")
    		return (CircuitElm) new SevenSegDecoderElm(x1, y1);
    	if (n=="FullAdderElm")
    		return (CircuitElm) new FullAdderElm(x1, y1);
    	if (n=="HalfAdderElm")
    		return (CircuitElm) new HalfAdderElm(x1, y1);
    	if (n=="MonostableElm")
    		return (CircuitElm) new MonostableElm(x1, y1);
    	if (n=="LabeledNodeElm")
    		return (CircuitElm) new LabeledNodeElm(x1, y1);
    	
    	// 如果移除 UserDefinedLogicElm，会破坏用户保存的快捷键
    	if (n=="UserDefinedLogicElm" || n=="CustomLogicElm")
    	    	return (CircuitElm) new CustomLogicElm(x1, y1);
    	
    	if (n=="TestPointElm")
    	    	return new TestPointElm(x1, y1);
    	if (n=="AmmeterElm")
	    	return new AmmeterElm(x1, y1);
    	if (n=="DataRecorderElm")
		return (CircuitElm) new DataRecorderElm(x1, y1);
    	if (n=="AudioOutputElm")
		return (CircuitElm) new AudioOutputElm(x1, y1);
    	if (n=="NDarlingtonElm" || n == "DarlingtonElm")
		return (CircuitElm) new NDarlingtonElm(x1, y1);
    	if (n=="PDarlingtonElm")
		return (CircuitElm) new PDarlingtonElm(x1, y1);
    	if (n=="ComparatorElm")
		return (CircuitElm) new ComparatorElm(x1, y1);
    	if (n=="OTAElm")
		return (CircuitElm) new OTAElm(x1, y1);
    	if (n=="NoiseElm")
		return (CircuitElm) new NoiseElm(x1, y1);
    	if (n=="VCVSElm")
		return (CircuitElm) new VCVSElm(x1, y1);
    	if (n=="VCCSElm")
		return (CircuitElm) new VCCSElm(x1, y1);
    	if (n=="CCVSElm")
		return (CircuitElm) new CCVSElm(x1, y1);
    	if (n=="CCCSElm")
		return (CircuitElm) new CCCSElm(x1, y1);
    	if (n=="OhmMeterElm")
		return (CircuitElm) new OhmMeterElm(x1, y1);
    	if (n=="ScopeElm")
    	    	return (CircuitElm) new ScopeElm(x1,y1);
    	if (n=="FuseElm")
	    	return (CircuitElm) new FuseElm(x1,y1);
    	if (n=="LEDArrayElm")
    	    	return (CircuitElm) new LEDArrayElm(x1, y1);
    	if (n=="CustomTransformerElm")
    	    	return (CircuitElm) new CustomTransformerElm(x1, y1);
    	if (n=="OptocouplerElm")
		return (CircuitElm) new OptocouplerElm(x1, y1);
    	if (n=="StopTriggerElm")
		return (CircuitElm) new StopTriggerElm(x1, y1);
    	if (n=="OpAmpRealElm")
		return (CircuitElm) new OpAmpRealElm(x1, y1);
    	if (n=="CustomCompositeElm")
		return (CircuitElm) new CustomCompositeElm(x1, y1);
    	if (n=="AudioInputElm")
		return (CircuitElm) new AudioInputElm(x1, y1);
    	if (n=="CrystalElm")
		return (CircuitElm) new CrystalElm(x1, y1);
    	if (n=="SRAMElm")
		return (CircuitElm) new SRAMElm(x1, y1);
    	if (n=="TimeDelayRelayElm")
		return (CircuitElm) new TimeDelayRelayElm(x1, y1);
    	if (n=="DCMotorElm")
		return (CircuitElm) new DCMotorElm(x1, y1);
    	if (n=="LDRElm")
		return (CircuitElm) new LDRElm(x1, y1);
    	if (n=="ThermistorNTCElm")
		return (CircuitElm) new ThermistorNTCElm(x1, y1);
    	return null;
    }
    
    public void updateModels() {
	int i;
	for (i = 0; i != elmList.size(); i++)
	    elmList.get(i).updateModels();
    }
    

    
    
    native boolean weAreInUS() /*-{
    try {
	l = navigator.languages ? navigator.languages[0] : (navigator.language || navigator.userLanguage) ;  
    	if (l.length > 2) {
    		l = l.slice(-2).toUpperCase();
    		return (l == "US" || l=="CA");
    	} else {
    		return 0;
    	}

    } catch (e) { return 0;
    }
    }-*/;

    native boolean weAreInGermany() /*-{
    try {
	l = navigator.languages ? navigator.languages[0] : (navigator.language || navigator.userLanguage) ;
	return (l.toUpperCase().startsWith("DE"));
    } catch (e) { return 0;
    }
    }-*/;
    
    static String LS(String s) {
	if (s == null)
	    return null;
	String sm = localizationMap.get(s);
	if (sm != null)
	    return sm;
	
	// 使用尾部 ~ 来区分在英语中相同但需要不同翻译的字符串。
	// 如果没有对应的翻译，则移除它们。
	int ix = s.indexOf('~');
	if (ix < 0)
	    return s;
	s = s.substring(0, ix);
	sm = localizationMap.get(s);
	if (sm != null)
	    return sm;
	return s;
    }
    static SafeHtml LSHTML(String s) { return SafeHtmlUtils.fromTrustedString(LS(s)); }
    
    
    // 用于调试
    void dumpNodelist() {

	CircuitNode nd;
	CircuitElm e;
	int i,j;
	String s;
	String cs;
//
//	for(i=0; i<nodeList.size(); i++) {
//	    s="Node "+i;
//	    nd=nodeList.get(i);
//	    for(j=0; j<nd.links.size();j++) {
//		s=s+" " + nd.links.get(j).num + " " +nd.links.get(j).elm.getDumpType();
//	    }
//	    console(s);
//	}
	console("Elm list Dump");
	for (i=0;i<elmList.size(); i++) {
	    e=elmList.get(i);
	    cs = e.getDumpClass().toString();
	    int p = cs.lastIndexOf('.');
	    cs = cs.substring(p+1);
	    if (cs=="WireElm") 
		continue;
	    if (cs=="LabeledNodeElm")
		cs = cs+" "+((LabeledNodeElm)e).text;
	    if (cs=="TransistorElm") {
		if (((TransistorElm)e).pnp == -1)
		    cs= "PTransistorElm";
		else
		    cs = "NTransistorElm";
	    }
	    s=cs;
	    for(j=0; j<e.getPostCount(); j++) {
		s=s+" "+e.nodes[j];
	    }
	    console(s);
	}
    }
    
	native void printCanvas(CanvasElement cv) /*-{
	    var img    = cv.toDataURL("image/png");
	    var win = window.open("", "print", "height=500,width=500,status=yes,location=no");
	    win.document.title = "Print Circuit";
	    win.document.open();
	    win.document.write('<img src="'+img+'"/>');
	    win.document.close();
	    setTimeout(function(){win.print();},1000);
	}-*/;

	void doDCAnalysis() {
	    dcAnalysisFlag = true;
	    resetAction();
	}
	
	void doPrint() {
	    Canvas cv = getCircuitAsCanvas(true);
	    printCanvas(cv.getCanvasElement());
	}
	
	public Canvas getCircuitAsCanvas(boolean print) {
	    	// 创建用于绘制电路的画布
	    	Canvas cv = Canvas.createIfSupported();
	    	Rectangle bounds = getCircuitBounds();
	    	
		// 在边缘留出一些空间，因为边界计算并不完美
	    	int wmargin = 140;
	    	int hmargin = 100;
	    	int w = (bounds.width+wmargin) ;
	    	int h = (bounds.height+hmargin) ;
	    	cv.setCoordinateSpaceWidth(w);
	    	cv.setCoordinateSpaceHeight(h);
	    	double oldTransform[] = Arrays.copyOf(transform, 6);
	    
		Context2d context = cv.getContext2d();
		Graphics g = new Graphics(context);
		context.setTransform(1, 0, 0, 1, 0, 0);
	        
	        double scale = 1;
	        
		// 打开白色背景，关闭电流显示
		boolean p = printableCheckItem.getState();
		boolean c = dotsCheckItem.getState();
		if (print)
		    printableCheckItem.setState(true);
	        if (printableCheckItem.getState()) {
	            CircuitElm.whiteColor = Color.black;
	            CircuitElm.lightGrayColor = Color.black;
	            g.setColor(Color.white);
	        } else {
	            CircuitElm.whiteColor = Color.white;
	            CircuitElm.lightGrayColor = Color.lightGray;
	            g.setColor(Color.black);
	            g.fillRect(0, 0, g.context.getCanvas().getWidth(), g.context.getCanvas().getHeight());
	        }
		dotsCheckItem.setState(false);

	        if (bounds != null)
	            scale = Math.min(w /(double)(bounds.width+wmargin),
	                             h/(double)(bounds.height+hmargin));
	        scale = Math.min(scale, 1.5); // 限制缩放比例，避免在大窗口中创建过大的电路
	        
	        // ScopeElm 需要更新变换数组
		transform[0] = transform[3] = scale;
		transform[4] = -(bounds.x-wmargin/2);
		transform[5] = -(bounds.y-hmargin/2);
		context.scale(scale, scale);
		context.translate(transform[4], transform[5]);
		
		// 绘制元件
		int i;
		for (i = 0; i != elmList.size(); i++) {
		    getElm(i).draw(g);
		}
		for (i = 0; i != postDrawList.size(); i++) {
		    CircuitElm.drawPost(g, postDrawList.get(i));
		}

		// 恢复一切
		printableCheckItem.setState(p);
		dotsCheckItem.setState(c);
		transform = oldTransform;
		return cv;
	}
	
	boolean isSelection() {
	    for (int i = 0; i != elmList.size(); i++)
		if (getElm(i).isSelected())
		    return true;
	    return false;
	}
	
	public CustomCompositeModel getCircuitAsComposite() {
	    int i;
	    String nodeDump = "";
	    String dump = "";
//	    String models = "";
	    CustomLogicModel.clearDumpedFlags();
	    DiodeModel.clearDumpedFlags();
	    TransistorModel.clearDumpedFlags();
	    Vector<ExtListEntry> extList = new Vector<ExtListEntry>();
	    boolean sel = isSelection();
	    
	    // 节点标签 -> 节点编号的映射
	    HashMap<String, Integer> nodeNameHash = new HashMap<String, Integer>();
	    
	    // 节点编号 -> 等价节点编号的映射（如果它们有相同的标签）
	    HashMap<Integer, Integer> nodeNumberHash = new HashMap<Integer, Integer>();
	    
	    boolean used[] = new boolean[nodeList.size()];
	    
	    // 找出所有带标签的节点，获取它们的列表，并创建节点编号映射
	    for (i = 0; i != elmList.size(); i++) {
		CircuitElm ce = getElm(i);
		if (sel && !ce.isSelected())
		    continue;
		if (ce instanceof LabeledNodeElm) {
		    LabeledNodeElm lne = (LabeledNodeElm) ce;
		    String label = lne.text;
		    Integer map = nodeNameHash.get(label);
		    
		    // 这个节点名之前出现过？把新节点编号映射到旧的那个
		    if (map != null) {
			Integer val = nodeNumberHash.get(lne.getNode(0));
			if (val != null && !val.equals(map)) {
			    Window.alert("Can't have a node with two labels!");
			    return null;
			}
			nodeNumberHash.put(lne.getNode(0), map); 
			continue;
		    }
		    nodeNameHash.put(label, lne.getNode(0));
		    // 在 nodeNumberHash 中放入一个条目，以便以后检测我们是否试图把它映射到其他节点
		    nodeNumberHash.put(lne.getNode(0), lne.getNode(0));
		    if (lne.isInternal())
			continue;
		    // 为外部节点创建 ext 列表条目
		    ExtListEntry ent = new ExtListEntry(label, ce.getNode(0));
		    extList.add(ent);
		}
	    }
	    
	    // 输出所有元件
	    for (i = 0; i != elmList.size(); i++) {
		CircuitElm ce = getElm(i);
		if (sel && !ce.isSelected())
		    continue;
		// 不需要导出这些元件
		if (ce instanceof WireElm || ce instanceof LabeledNodeElm || ce instanceof ScopeElm)
		    continue;
		if (ce instanceof GraphicElm)
		    continue;
		int j;
		if (nodeDump.length() > 0)
		    nodeDump += "\r";
		nodeDump += ce.getClass().getSimpleName();
		for (j = 0; j != ce.getPostCount(); j++) {
		    int n = ce.getNode(j);
		    Integer nobj = nodeNumberHash.get(n);
		    int n0 = (nobj == null) ? n : nobj;
		    used[n0] = true;
		    nodeDump += " " + n0;
		}
		
	        // 保存位置
                int x1 = ce.x;  int y1 = ce.y;
                int x2 = ce.x2; int y2 = ce.y2;
                
                // 把它们设为 0，便于移除
                ce.x = ce.y = ce.x2 = ce.y2 = 0;

                String tstring = ce.dump();
                tstring = tstring.replaceFirst("[A-Za-z0-9]+ 0 0 0 0 ", ""); // 移除内部元件未使用的 tint_x1 y1 x2 y2 坐标
                
                // 恢复位置
                ce.x = x1; ce.y = y1; ce.x2 = x2; ce.y2 = y2;
                if (dump.length() > 0)
                    dump += " ";
                dump += CustomLogicModel.escape(tstring);
	    }
	    
	    for (i = 0; i != extList.size(); i++) {
		ExtListEntry ent = extList.get(i);
		if (!used[ent.node]) {
		    Window.alert("Node \"" + ent.name + "\" is not used!");
		    return null;
		}
	    }
		    
	    CustomCompositeModel ccm = new CustomCompositeModel();
	    ccm.nodeList = nodeDump;
	    ccm.elmDump = dump;
	    ccm.extList = extList;
	    return ccm;
	}
	
	static void invertMatrix(double a[][], int n) {
	    int ipvt[] = new int[n];
	    lu_factor(a, n, ipvt);
	    int i, j;
	    double b[] = new double[n];
	    double inva[][] = new double[n][n];
	    
	    // 对单位矩阵的每一列求解
	    for (i = 0; i != n; i++) {
		for (j = 0; j != n; j++)
		    b[j] = 0;
		b[i] = 1;
		lu_solve(a, n, ipvt, b);
		for (j = 0; j != n; j++)
		    inva[j][i] = b[j];
	    }
	    
	    // 返回到原矩阵中
	    for (i = 0; i != n; i++)
		for (j = 0; j != n; j++)
		    a[i][j] = inva[i][j];
	}
}
