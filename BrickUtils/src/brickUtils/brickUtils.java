/*
	Copyright 2013-2017 Mario Pascucci <mpascucci@gmail.com>
	This file is part of BrickUtils.

	BrickUtils is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	BrickUtils is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with BrickUtils.  If not, see <http://www.gnu.org/licenses/>.

*/

package brickUtils;




import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.prefs.BackingStoreException;
import java.util.zip.ZipException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import bricksnspace.appsettings.AppSettings;
import bricksnspace.appsettings.AppVersion;
import bricksnspace.appsettings.OptionsDialog;
import bricksnspace.brickMapping.BrickMapping;
import bricksnspace.brickMapping.PartMapping;
import bricksnspace.bricklinklib.BLCategoryImporter;
import bricksnspace.bricklinklib.BLPartImporter;
import bricksnspace.bricklinklib.BLSetImporter;
import bricksnspace.bricklinklib.BricklinkColor;
import bricksnspace.bricklinklib.BricklinkLib;
import bricksnspace.bricklinklib.BricklinkPart;
import bricksnspace.busydialog.BusyDialog;
import bricksnspace.dbconnector.DBConnector;
import bricksnspace.ldrawlib.LDLibManageDlg;
import bricksnspace.ldrawlib.LDrawDBImportTask;
import bricksnspace.ldrawlib.LDrawLib;
import bricksnspace.ldrawlib.LDrawPart;



/*
 * 
 * TODO: a function in "My bricks" that copy selected bricks to working list
 * TODO: add a filter to see obsolete parts on ldraw/bricklink
 */





public class brickUtils implements ActionListener, ListSelectionListener {


	private static final String BRICKDBURL = "https://github.com/Bricksnspace/brickDB/raw/master/brickutils/brickutils.h2.db";
	private static final String BRICKUTILSDB = "brickutils.h2.db";
	private static final String appName = "BrickUtils";
	private static final String VERSIONURL = "https://github.com/Bricksnspace/brickutils/raw/master/BrickUtils/VERSION";
	private static final String DEF_LDR_URL = "http://www.ldraw.org/library/updates/complete.zip";
	private static final String DEF_UNOFF_LDR_URL = "http://www.ldraw.org/library/unofficial/ldrawunf.zip";
	private static final String DEF_MAP_URL = "https://github.com/Bricksnspace/brickDB/raw/master/brickutils/";
	private static final String LDR_LIB_PATH = "complete.zip";
	private static final String LDR_UNOFF_LIB_PATH = "ldrawunf.zip";
	
	//private BrickDB brickDB;
	private DBConnector dbc;
	private LDrawLib ldrlib;
	private JFrame frame;
	private JTabbedPane tabPane;
	private JPanel workingPane;
	private WorkingTableModel workingTableModel;
	private JTable workingTable;
	private TableRowSorter<WorkingTableModel> workingSorter;
	private JToolBar brickListTool;
	private JButton btnBlImport;
	private JFileChooser fileBl;
	private SmartFileChooser fileExport;
	private JFileChooser fileLdd;
	private Timer splashTimer;
	private JLabel splashmsg;
	private InitDb dbInitTask;
	private JDialog splashDialog;
	private JMenu mnUpdateCatalogs;
	private JMenuItem mntmBlCategories;
	private JMenuItem mntmBlSets;
	private JMenuItem mntmBlParts;
	private JPanel brickToolPane;
	private JToolBar brickEditTool;
	private JButton btnAddBrickBl;
	private JButton btnDupBrick;
	private JButton btnDelBrick;
	private JButton btnSearchBL;
	private JButton btnSearchLdr;
	private JMenuItem mntmBlColors;
	private JButton btnBrickColor;
//	private HashMap<String,AppPref> prefs = new HashMap<String,AppPref>();
	private JMenu mnProgram;
	private JMenuItem mntmOptions;
	private JMenuItem mntmExit;
	private JButton btnSearchLdd;
	private JButton btnAddBrickLdd;
	private JButton btnAddBrickLdr;
	private JMenuItem mntmBlImport;
	private JMenuItem mntmLddImport;
	private JPopupMenu importPopup;
	private JButton btnSetData;
	private JPanel workingDetails;
	private JLabel setType;
	private JLabel setid;
	private JLabel setName;
	private JLabel setNotes;
	private JLabel setCategory;
	private JLabel setYear;
	private BrickShapePanel workShapePanel,catalogShapePanel;
	private JLabel setAvailable;
	private JButton btnAddToSet;
	//private JButton btnAddToCatalog;
	private BrickSet currentSet;
	private JPanel setPane;
	private JPanel catalogPane;
	private CatalogTableModel catalogTableModel;
	private JTable catalogTable;
	private SetTableModel setTableModel;
	private JTable setTable;
	//private JButton btnCatDelBrick;
	private JButton btnDelSet;
	private JButton btnSetExport;
	private JButton btnSetImport;
	private JButton btnSetCopy;
	private JButton btnSetMove;
	private JMenuItem mntmCheckUpdates;
	private ImageIcon[] icnImg = new ImageIcon[4];
	private JMenuItem mntmLdrParts;
	private JMenuItem mntmPyImport;
	private JButton btnWhichSet;
	private JButton btnNewList;
	private JButton btnBlExport;
	private JPopupMenu exportPopup;
	private JMenuItem mntmBlExport;
	private JMenuItem mntmHtmlExport;
	private JButton btnCheckBuild;
	private JMenuItem mntmAbout;
	private JFileChooser fileLdraw;
	private JMenuItem mntmLdrawImport;
	private JLabel brickCount;
	private JLabel partCount;
	private LddSearch lddSearchDlg;
	private BlSearch blSearchDlg;
	private LdrSearch ldrSearchDlg;
	private JMenuItem mntmTemplateExport;
	private JFileChooser fileVelocity;
	private JFileChooser fileTemplateExport;
	private FileHandler logFile;
	private JMenuItem mntmLibs;
	

	
	
	private class InitDb extends SwingWorker<Integer, Void>  {

		@Override
		protected Integer doInBackground() throws Exception {

			setProgress(0);
			dbc = new DBConnector("brickutils","brickutils","IFEXISTS=TRUE");
			BrickDB.initDB(dbc);
			setProgress(30);
			BricklinkLib.Init(dbc);
			setProgress(40);
			// init LDraw part libraries
			ldrlib = loadLDrawLibs(dbc);
			setProgress(60);
			BrickMapping.Init(dbc);
			setProgress(70);
			Brick.setDb(dbc);
			setProgress(90);
			BrickSet.setDb(dbc);
			setProgress(100);
			return 100;
		}
		
	}
	
	
	
	public brickUtils() {
		
		AppVersion.setMyVersion("0.3.0");
		
		// logging system init
		try {
			logFile = new FileHandler(appName+"-%g.log",0,3);
			logFile.setLevel(Level.ALL);
			logFile.setFormatter(new SimpleFormatter());
			Logger.getGlobal().addHandler(logFile);
			Logger.getGlobal().log(Level.INFO, "Logger started");
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		

		
		initPrefs();
		
		// images for busy animation
		icnImg[0] = new ImageIcon(this.getClass().getResource("images/f0.png"));
		icnImg[1] = new ImageIcon(this.getClass().getResource("images/f1.png"));
		icnImg[2] = new ImageIcon(this.getClass().getResource("images/f2.png"));
		icnImg[3] = new ImageIcon(this.getClass().getResource("images/f3.png"));

		// find if all files are "in"
		File db = new File(BRICKUTILSDB);
		if (!db.exists() || AppSettings.get(MySettings.LIBOFFICIAL).equals("")) {
			firstRun();
		}
		// checks for db
//		File db = new File(BRICKUTILSDB);
//		if (!db.exists()) {
//			int res = JOptionPane.showConfirmDialog(null, "You need some files to get program working:\n"+
//					"database and LDraw part libraries.\n"+
//					"Do you want to download from Internet?\n"+
//					"(Hit 'No' to exit program)", "First run", JOptionPane.YES_NO_OPTION);
//			if (res == JOptionPane.NO_OPTION) {
//				System.exit(0);
//			}
//			BusyDialog dlg = new BusyDialog(null,"Retrieve Database",true,icnImg);
//			GetFileFromURL task = null;
//			try {
//				task = new GetFileFromURL(new URL(BRICKDBURL),
//						db, dlg);
//			} catch (MalformedURLException e1) {
//				JOptionPane.showMessageDialog(null, "Problem with SF database URL","Internal error",JOptionPane.ERROR_MESSAGE);
//				Logger.getGlobal().log(Level.SEVERE,"Error reading database download URL",e1);
//				System.exit(1);
//			}
//			dlg.setTask(task);
//			dlg.startTask();
//			// after completing task return here
//			try {
//				task.get(10, TimeUnit.MILLISECONDS);
//				//JOptionPane.showMessageDialog(null, "Downloaded "+r+" bytes.","Download Database",JOptionPane.INFORMATION_MESSAGE);
//			}
//			catch (ExecutionException e) {
//				JOptionPane.showMessageDialog(null, "Unable to download Database from Internet\nReason: "+e.getLocalizedMessage()+
//						"\nPlease retry later.", 
//						"Download error",JOptionPane.ERROR_MESSAGE);
//				Logger.getGlobal().log(Level.SEVERE,"Error downloading database",e);
//				System.exit(1);
//			} catch (InterruptedException e) {
//				JOptionPane.showMessageDialog(null, "Task interrupted!\n Reason: "+e.getLocalizedMessage(), "Task interrupted",JOptionPane.ERROR_MESSAGE);
//				Logger.getGlobal().log(Level.SEVERE,"Database download interrupted",e);
//				System.exit(1);
//			} catch (TimeoutException e) {
//				JOptionPane.showMessageDialog(null, "Timeout retrieving task output\nReason: "+e.getLocalizedMessage(), "Task timeout",JOptionPane.ERROR_MESSAGE);
//				Logger.getGlobal().log(Level.SEVERE,"Unknown error in database downloading",e);
//				System.exit(1);
//			}
//		}
		dbInitTask = new InitDb();
		splashTimer = new Timer(1000,this);
		dbInitTask.execute();
		splashTimer.start();
		splashDialog = new JDialog();
		splashDialog.setUndecorated(true);
		splashDialog.setTitle("BrickUtils");
		splashmsg = new JLabel("Starting");
		JLabel img = new JLabel(new ImageIcon(this.getClass().getResource("images/BrickUtils.png")));
		img.setBorder(BorderFactory.createTitledBorder("BrickUtils"));
		splashDialog.getContentPane().setLayout(new BoxLayout(splashDialog.getContentPane(), BoxLayout.Y_AXIS));
		splashDialog.getContentPane().add(img);
		splashDialog.getContentPane().add(splashmsg);
		splashDialog.pack();
		splashDialog.setLocationRelativeTo(null);
		splashDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		splashDialog.setVisible(true);
		if (AppSettings.getBool(MySettings.APP_CHECK_UPDATE) && AppVersion.compareVersion(AppSettings.get(MySettings.APP_UPDATE_URL)) > 0) {
			JOptionPane.showMessageDialog(null, 
					"There is a new version of BrickUtils.\n"
					+ "Please update",
					"Program", JOptionPane.INFORMATION_MESSAGE);
		}
		initialize();
		
	}

	
	/**
	 * 
	 */
	private void initialize() {

		fileBl = new JFileChooser(".");
		fileExport = new SmartFileChooser(".",".html");
		fileExport.setDialogType(JFileChooser.SAVE_DIALOG);
		fileLdd = new JFileChooser(".");
		fileLdraw = new JFileChooser(".");
		fileVelocity = new JFileChooser(".");
		fileVelocity.setDialogTitle("Choose a template");
		FileFilter ff = new FileNameExtensionFilter("Velocity template","vm");
		fileVelocity.setFileFilter(ff);
		fileTemplateExport = new JFileChooser(".");
		fileTemplateExport.setDialogType(JFileChooser.SAVE_DIALOG);
		fileTemplateExport.setDialogTitle("Select output file (with extension)");

		frame = new JFrame();
		JMenuBar menuBar;

		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds(100, 100, screenSize.width-200 > 1280 ? 1100:screenSize.width-200 , screenSize.height-200);
		frame.setTitle(appName+" - Brick utilities");
		frame.setIconImage(new ImageIcon(this.getClass().getResource("images/BrickUtils.png")).getImage());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				closeApp();
			}
		});
		
		// main menu
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		mnUpdateCatalogs = new JMenu("Update Catalogs");
		menuBar.add(mnUpdateCatalogs);
		
		mntmBlCategories = new JMenuItem("BrickLink Categories");
		mntmBlCategories.addActionListener(this);
		mnUpdateCatalogs.add(mntmBlCategories);
	
		mntmBlSets = new JMenuItem("BrickLink Sets");
		mntmBlSets.addActionListener(this);
		mnUpdateCatalogs.add(mntmBlSets);
		
		mntmBlColors = new JMenuItem("BrickLink Colors");
		mntmBlColors.addActionListener(this);
		mnUpdateCatalogs.add(mntmBlColors);
		
		mntmBlParts = new JMenuItem("BrickLink Parts");
		mntmBlParts.addActionListener(this);
		mnUpdateCatalogs.add(mntmBlParts);
		
		mntmLdrParts = new JMenuItem("LDraw part libraries");
		mntmLdrParts.addActionListener(this);
		mnUpdateCatalogs.add(mntmLdrParts);
		
		// last menu in top right 
		mnProgram = new JMenu("Program");
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(mnProgram);
		
		mntmAbout = new JMenuItem("About...");
		mntmAbout.addActionListener(this);
		mnProgram.add(mntmAbout);
		mnProgram.add(new JSeparator());

		
		mntmOptions = new JMenuItem("Preferences...");
		mntmOptions.addActionListener(this);
		mnProgram.add(mntmOptions);
		
		mntmCheckUpdates = new JMenuItem("Check for DB updates...");
		mntmCheckUpdates.addActionListener(this);
		mnProgram.add(mntmCheckUpdates);

		mntmLibs = new JMenuItem("LDraw libraries...");
		mntmLibs.addActionListener(this);
		mnProgram.add(mntmLibs);


		Container masterPane = frame.getContentPane();
		tabPane = new JTabbedPane();
		workingPane = new JPanel();
		catalogPane = new JPanel();
		setPane = new JPanel();
		masterPane.add(tabPane);
		tabPane.addTab("Working List",workingPane);
		tabPane.addTab("My bricks", catalogPane);
		tabPane.addTab("My sets", setPane);
		
		
 		/* *******************************************
 		 * Working list pane setup
 		 *********************************************/
		
		workingPane.setLayout(new BorderLayout(0, 0));
		JScrollPane workingScrollPane = new JScrollPane();
		workingPane.add(workingScrollPane,BorderLayout.CENTER);
		workingDetails = new JPanel();
		workingPane.add(workingDetails,BorderLayout.SOUTH);
		
		workingTableModel = new WorkingTableModel();
		workingTable = new JTable(workingTableModel);
		workingTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		workingTable.setAutoCreateRowSorter(true);
		TableColumnModel tcl = workingTable.getColumnModel();
		workingSorter = new TableRowSorter<WorkingTableModel>(workingTableModel);
		workingTable.setRowSorter(workingSorter);
		tcl.getColumn(0).setPreferredWidth(50);
		tcl.getColumn(8).setPreferredWidth(350);
		tcl.getColumn(6).setPreferredWidth(50);
		tcl.getColumn(9).setPreferredWidth(50);
		tcl.getColumn(5).setCellRenderer(new BrickColorCellRenderer(true));
		
		refreshWorkList();
		workingScrollPane.setViewportView(workingTable);
		
		workingTable.getSelectionModel().addListSelectionListener(this);

		workShapePanel = new BrickShapePanel(ldrlib);
		workShapePanel.setBorder(BorderFactory.createTitledBorder("Brick shape"));

		tcl.getColumn(5).setCellEditor(new BrickColorCellEditor(workShapePanel));
		
		brickToolPane = new JPanel();
		brickToolPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		workingPane.add(brickToolPane, BorderLayout.NORTH);
		
		// Brick list import tools
		brickListTool = new JToolBar();
		brickToolPane.add(brickListTool);
		brickListTool.setToolTipText("Brick list tools");

		ImageIcon newListIcon = new ImageIcon(this.getClass().getResource("images/new-list.png"));
		btnNewList = new JButton(newListIcon);
		btnNewList.setToolTipText("New working list");
		btnNewList.addActionListener(this);
		brickListTool.add(btnNewList);
		
		ImageIcon lddImportIcon = new ImageIcon(this.getClass().getResource("images/import-ldd.png"));
		btnBlImport = new JButton(lddImportIcon);
		importPopup = new JPopupMenu("Import from...");
		mntmLddImport = new JMenuItem("LDD project (LXF/LXFML)", lddImportIcon);
		mntmLddImport.addActionListener(this);
		importPopup.add(mntmLddImport);
		mntmBlImport = new JMenuItem("Bricklink XML dump", 
				new ImageIcon(this.getClass().getResource("images/import-bl.png")));
		mntmBlImport.addActionListener(this);
		importPopup.add(mntmBlImport);
		
		mntmLdrawImport = new JMenuItem("LDraw project", 
				new ImageIcon(this.getClass().getResource("images/import-ldr.png")));
		mntmLdrawImport.addActionListener(this);
		importPopup.add(mntmLdrawImport);
		
		btnBlImport.addActionListener(this);
		btnBlImport.setToolTipText("Import bricks");
		brickListTool.add(btnBlImport);
		
		ImageIcon blExportIcon = new ImageIcon(this.getClass().getResource("images/export-icon.png"));
		btnBlExport = new JButton(blExportIcon);
		exportPopup = new JPopupMenu("Export to...");
		mntmBlExport = new JMenuItem("Bricklink mass upload (XML)", 
				new ImageIcon(this.getClass().getResource("images/export-blxml.png")));
		mntmBlExport.addActionListener(this);
		exportPopup.add(mntmBlExport);
		mntmHtmlExport = new JMenuItem("HTML list (printable)", 
				new ImageIcon(this.getClass().getResource("images/export-html.png")));
		mntmHtmlExport.addActionListener(this);
		exportPopup.add(mntmHtmlExport);
		
		mntmTemplateExport = new JMenuItem("Export using a template", 
				new ImageIcon(this.getClass().getResource("images/export-template.png")));
		mntmTemplateExport.addActionListener(this);
		exportPopup.add(mntmTemplateExport);
		
		btnBlExport.addActionListener(this);
		btnBlExport.setToolTipText("Export bricks");
		brickListTool.add(btnBlExport);
		
		ImageIcon setDataIcon = new ImageIcon(this.getClass().getResource("images/set-data.png"));
		btnSetData = new JButton(setDataIcon);
		btnSetData.setToolTipText("Set brick list name");
		btnSetData.addActionListener(this);
		brickListTool.add(btnSetData);
		
		ImageIcon addToSetIcon = new ImageIcon(this.getClass().getResource("images/add-to-set.png"));
		btnAddToSet = new JButton(addToSetIcon);
		btnAddToSet.setToolTipText("Add to Catalog");
		btnAddToSet.addActionListener(this);
		brickListTool.add(btnAddToSet);
		
		ImageIcon checkBuildIcon = new ImageIcon(this.getClass().getResource("images/check-build.png"));
		btnCheckBuild = new JButton(checkBuildIcon);
		btnCheckBuild.setToolTipText("Check if can build");
		btnCheckBuild.addActionListener(this);
		brickListTool.add(btnCheckBuild);
		
		// brick list edit tools
		brickEditTool = new JToolBar();
		brickToolPane.add(brickEditTool);
		brickEditTool.setToolTipText("Edit tools");
		
		ImageIcon addLddIcon = new ImageIcon(this.getClass().getResource("images/add-ldd.png"));
		btnAddBrickLdd = new JButton(addLddIcon);
		btnAddBrickLdd.setToolTipText("Add brick from LDD catalog");
		btnAddBrickLdd.addActionListener(this);
		brickEditTool.add(btnAddBrickLdd);
		
		ImageIcon addBlIcon = new ImageIcon(this.getClass().getResource("images/add-bl.png"));
		btnAddBrickBl = new JButton(addBlIcon);
		btnAddBrickBl.setToolTipText("Add brick from Bricklink catalog");
		btnAddBrickBl.addActionListener(this);
		brickEditTool.add(btnAddBrickBl);
		
		ImageIcon addLdrIcon = new ImageIcon(this.getClass().getResource("images/add-ldr.png"));
		btnAddBrickLdr = new JButton(addLdrIcon);
		btnAddBrickLdr.setToolTipText("Add brick from LDraw library");
		btnAddBrickLdr.addActionListener(this);
		brickEditTool.add(btnAddBrickLdr);
		
		ImageIcon dupIcon = new ImageIcon(this.getClass().getResource("images/copy.png"));
		btnDupBrick = new JButton(dupIcon);
		btnDupBrick.setToolTipText("Duplicate selected brick");
		btnDupBrick.addActionListener(this);
		brickEditTool.add(btnDupBrick);

		ImageIcon delIcon = new ImageIcon(this.getClass().getResource("images/delete.png"));
		btnDelBrick = new JButton(delIcon);
		btnDelBrick.setToolTipText("Delete selected brick(s)");
		btnDelBrick.addActionListener(this);
		brickEditTool.add(btnDelBrick);

		ImageIcon colorIcon = new ImageIcon(this.getClass().getResource("images/colors.png"));
		btnBrickColor = new JButton(colorIcon);
		btnBrickColor.setToolTipText("Change brick color");
		btnBrickColor.addActionListener(this);
		brickEditTool.add(btnBrickColor);
		
		ImageIcon searchLddIcon = new ImageIcon(this.getClass().getResource("images/lddsearch.png"));
		btnSearchLdd = new JButton(searchLddIcon);
		btnSearchLdd.setToolTipText("Search in LDD catalog");
		btnSearchLdd.addActionListener(this);
		brickEditTool.add(btnSearchLdd);
	
		
		ImageIcon searchBlIcon = new ImageIcon(this.getClass().getResource("images/blsearch.png"));
		btnSearchBL = new JButton(searchBlIcon);
		btnSearchBL.setToolTipText("Search in Bricklink catalog");
		btnSearchBL.addActionListener(this);
		brickEditTool.add(btnSearchBL);
		
		ImageIcon searchLdrIcon = new ImageIcon(this.getClass().getResource("images/ldrsearch.png"));
		btnSearchLdr = new JButton(searchLdrIcon);
		btnSearchLdr.setToolTipText("Search in LDraw part library");
		btnSearchLdr.addActionListener(this);
		brickEditTool.add(btnSearchLdr);

		// list data
		workingDetails.setLayout(new GridBagLayout());
		//workingDetails.setLayout(new GridLayout());
		JPanel listDetails = new JPanel();
		listDetails.setBorder(BorderFactory.createTitledBorder("List identification"));
		listDetails.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.ipady = 2;
		gbc.ipadx = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		
		// set type
		gbc.gridx = 0;
		gbc.gridy = 0;
		JLabel setTypeLab = new JLabel("Set type: ",SwingConstants.RIGHT);
		setType = new JLabel("-");
		listDetails.add(setTypeLab, gbc);
		gbc.gridx = 1;
		listDetails.add(setType, gbc);
		
		// set id
		gbc.gridx = 0;
		gbc.gridy = 1;
		JLabel setidLab = new JLabel("Set id: ",SwingConstants.RIGHT);
		listDetails.add(setidLab, gbc);
		setid = new JLabel("-");
		gbc.gridx = 1;
		listDetails.add(setid,gbc);
		
		// set name
		gbc.gridx = 0;
		gbc.gridy = 2;
		JLabel setNameLab = new JLabel("Set name: ", SwingConstants.RIGHT);
		listDetails.add(setNameLab,gbc);
		gbc.gridx = 1;
		setName = new JLabel("-");
		listDetails.add(setName, gbc);

		// set category
		gbc.gridx = 0;
		gbc.gridy = 3;
		JLabel setCategoryLab = new JLabel("Category: ", SwingConstants.RIGHT);
		listDetails.add(setCategoryLab,gbc);
		gbc.gridx = 1;
		setCategory = new JLabel("-");
		listDetails.add(setCategory, gbc);
		
		// set year
		gbc.gridx = 0;
		gbc.gridy = 4;
		JLabel setYearLab = new JLabel("Set year: ", SwingConstants.RIGHT);
		listDetails.add(setYearLab,gbc);
		gbc.gridx = 1;
		setYear = new JLabel("-");
		listDetails.add(setYear, gbc);

		// set notes
		gbc.gridx = 0;
		gbc.gridy = 5;
		JLabel setNotesLab = new JLabel("Notes: ", SwingConstants.RIGHT);
		listDetails.add(setNotesLab,gbc);
		gbc.gridx = 1;
		setNotes = new JLabel("-");
		listDetails.add(setNotes, gbc);
		
		// set available flag
		gbc.gridx = 0;
		gbc.gridy = 6;
		JLabel setAvailLab = new JLabel("Available for building: ",SwingConstants.RIGHT);
		listDetails.add(setAvailLab, gbc);
		gbc.gridx = 1;
		setAvailable = new JLabel("-");
		listDetails.add(setAvailable, gbc);
		
		// total bricks
		gbc.gridx = 0;
		gbc.gridy = 7;
		JLabel brickCountLab = new JLabel("Brick count: ",SwingConstants.RIGHT);
		listDetails.add(brickCountLab, gbc);
		gbc.gridx = 1;
		brickCount = new JLabel("-");
		listDetails.add(brickCount, gbc);
		
		// total parts
		gbc.gridx = 0;
		gbc.gridy = 8;
		JLabel partCountLab = new JLabel("Part count: ",SwingConstants.RIGHT);
		listDetails.add(partCountLab, gbc);
		gbc.gridx = 1;
		partCount = new JLabel("-");
		listDetails.add(partCount, gbc);
		
		
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.VERTICAL;
		workingDetails.add(listDetails,gbc);
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		workingDetails.add(Box.createHorizontalGlue(),gbc);
		gbc.gridx = 2;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weightx = 0.0;
		workingDetails.add(workShapePanel,gbc);

		
		
		/* **********************************
		 *  catalog pane setup
		 ************************************/
		
		catalogPane.setLayout(new BorderLayout(0, 0));
		JScrollPane catalogScrollPane = new JScrollPane();
		catalogPane.add(catalogScrollPane,BorderLayout.CENTER);
		JPanel catalogDetails = new JPanel();
		catalogDetails.setLayout(new GridBagLayout());
		
		catalogPane.add(catalogDetails,BorderLayout.SOUTH);
		
		catalogTableModel = new CatalogTableModel();
		catalogTable = new JTable(catalogTableModel);
		catalogTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		catalogTable.setAutoCreateRowSorter(true);
		TableColumnModel tcm = catalogTable.getColumnModel();
		TableRowSorter<CatalogTableModel> catalogSorter = new TableRowSorter<CatalogTableModel>(catalogTableModel);
		catalogTable.setRowSorter(catalogSorter);
		tcm.getColumn(0).setPreferredWidth(50);
		tcm.getColumn(8).setPreferredWidth(350);
		tcm.getColumn(6).setPreferredWidth(50);
		tcm.getColumn(5).setCellRenderer(new BrickColorCellRenderer(true));
		
		refreshCatalogList();
		catalogScrollPane.setViewportView(catalogTable);
		
		catalogTable.getSelectionModel().addListSelectionListener(this);

		catalogShapePanel = new BrickShapePanel(ldrlib);
		catalogShapePanel.setBorder(BorderFactory.createTitledBorder("Brick shape"));

		tcm.getColumn(5).setCellEditor(new BrickColorCellEditor(catalogShapePanel));
		
		JPanel catalogToolPane = new JPanel();
		catalogToolPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		catalogPane.add(catalogToolPane, BorderLayout.NORTH);
		
		JToolBar catEditTool = new JToolBar();
		catEditTool.setToolTipText("Catalog tools");
		catalogToolPane.add(catEditTool);
		
//		btnCatDelBrick = new JButton(delIcon);
//		btnCatDelBrick.setToolTipText("Delete selected brick(s) if empty");
//		btnCatDelBrick.addActionListener(this);
//		catEditTool.add(btnCatDelBrick);

		ImageIcon whichSetIcon = new ImageIcon(this.getClass().getResource("images/select-set.png"));
		btnWhichSet = new JButton(whichSetIcon);
		btnWhichSet.setToolTipText("Select sets which use this brick");
		btnWhichSet.addActionListener(this);
		catEditTool.add(btnWhichSet);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		
		catalogDetails.add(Box.createHorizontalGlue(),gbc);
		gbc.gridx = 1;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.VERTICAL;
		catalogDetails.add(catalogShapePanel,gbc);

		/* **********************************
		 *  sets pane setup
		 ************************************/
		
		setPane.setLayout(new BorderLayout(0, 0));
		JScrollPane setScrollPane = new JScrollPane();
		setPane.add(setScrollPane,BorderLayout.CENTER);
		JPanel setDetails = new JPanel();
		setPane.add(setDetails,BorderLayout.SOUTH);
		
		setTableModel = new SetTableModel();
		setTable = new JTable(setTableModel);
		setTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		setTable.setAutoCreateRowSorter(true);
		TableColumnModel tcs = setTable.getColumnModel();
		TableRowSorter<SetTableModel> setSorter = new TableRowSorter<SetTableModel>(setTableModel);
		Comparator<Timestamp> timeStampComparator = new Comparator<Timestamp>() {
		    public int compare(Timestamp s1, Timestamp s2) {
		    	if (s1.equals(s2))
		    		return 0;
		    	if (s1.before(s2))
		    		return -1;
		    	else 
		    		return 1;
		    }
		};
		setSorter.setComparator(9, timeStampComparator);
		setTable.setRowSorter(setSorter);
		tcs.getColumn(0).setPreferredWidth(50);
		tcs.getColumn(3).setPreferredWidth(350);
		tcs.getColumn(6).setPreferredWidth(350);

		refreshSetList();
		setScrollPane.setViewportView(setTable);
		
		setTable.getSelectionModel().addListSelectionListener(this);

		JPanel setToolPane = new JPanel();
		setToolPane.setLayout(new FlowLayout(FlowLayout.LEFT));
		setPane.add(setToolPane, BorderLayout.NORTH);
		
		JToolBar setEditTool = new JToolBar();
		setEditTool.setToolTipText("Set tools");
		setToolPane.add(setEditTool);
		
		ImageIcon delSetIcon = new ImageIcon(this.getClass().getResource("images/set-delete.png"));
		btnDelSet = new JButton(delSetIcon);
		btnDelSet.setToolTipText("Delete selected set(s)");
		btnDelSet.addActionListener(this);
		setEditTool.add(btnDelSet);

		ImageIcon setCopyIcon = new ImageIcon(this.getClass().getResource("images/set-copy-edit.png"));
		btnSetCopy = new JButton(setCopyIcon);
		btnSetCopy.setToolTipText("Copy set to working list");
		btnSetCopy.addActionListener(this);
		setEditTool.add(btnSetCopy);

		ImageIcon setMoveIcon = new ImageIcon(this.getClass().getResource("images/set-move-edit.png"));
		btnSetMove = new JButton(setMoveIcon);
		btnSetMove.setToolTipText("Move set to working list");
		btnSetMove.addActionListener(this);
		setEditTool.add(btnSetMove);

		ImageIcon setExportIcon = new ImageIcon(this.getClass().getResource("images/set-export.png"));
		btnSetExport = new JButton(setExportIcon);
		btnSetExport.setToolTipText("Export/backup set list");
		btnSetExport.addActionListener(this);
		setEditTool.add(btnSetExport);

		ImageIcon setImportIcon = new ImageIcon(this.getClass().getResource("images/set-import.png"));
		btnSetImport = new JButton(setImportIcon);
		btnSetImport.setToolTipText("Import/restore set list");
		btnSetImport.addActionListener(this);
		setEditTool.add(btnSetImport);
		
		// reusable dialogs
		lddSearchDlg = new LddSearch(frame,"Search in LDD Catalog",true,ldrlib);
		blSearchDlg = new BlSearch(frame,"Search in Bricklink Catalog",true,ldrlib);
		ldrSearchDlg = new LdrSearch(frame,"Search LDraw Library",true,ldrlib);

		
		// final setup
		if (catalogTableModel.getRowCount() == 0 && setTableModel.getRowCount() == 0) {
			// import from brickutils is available only on empty catalog
			mnProgram.add(new JSeparator());
	
			mntmPyImport = new JMenuItem("Import from pyBrickUtils...");
			mntmPyImport.addActionListener(this);
			mnProgram.add(mntmPyImport);
		}
		
		mnProgram.add(new JSeparator());
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(this);
		mnProgram.add(mntmExit);
		
		updateCounts();
		updateDetails();
	}

	
	
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == splashTimer) {
			splashmsg.setText(splashmsg.getText()+".");
			if (!dbInitTask.isDone()) 
				return;
			splashTimer.stop();
			splashDialog.dispose();
			try {
				dbInitTask.get(10, TimeUnit.MILLISECONDS);
			}
			catch (ExecutionException ex) {
				int response = JOptionPane.showConfirmDialog(frame, "Program is not fully functional\nReason: "+
						ex.getLocalizedMessage() + "\nContinue anyway?", "Program init error",JOptionPane.OK_CANCEL_OPTION,JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"Unable to init database",ex);
				if (response != JOptionPane.OK_OPTION) 
					System.exit(1);
			} catch (InterruptedException e1) {
				JOptionPane.showMessageDialog(frame, "Task interrupted!\n Reason: "+e1.getLocalizedMessage(), "Task interrupted",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"Unable to init database, interrupted",e1);
			} catch (TimeoutException e1) {
				JOptionPane.showMessageDialog(frame, "Timeout retrieving task output\n Reason: "+e1.getLocalizedMessage(), "Task timeout",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"Unable to init database, timeout",e1);
			}
			// place here all db setup in classes
			try {
				currentSet = BrickSet.getCurrent();
				if (AppSettings.getBool(MySettings.MAP_UPDATE)) {
					checkUpdate(true);
				}
			}
			catch (SQLException ex) {
				JOptionPane.showMessageDialog(frame, "Problems with database\n Reason: "+ex.getLocalizedMessage(), "Database error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"Unable to init database",ex);
			}
			return;
		}
		else if (e.getSource() == btnBlImport) {
			importPopup.show((Component)e.getSource(), 20, 30);
		}
		else if (e.getSource() == btnBlExport) {
			exportPopup.show((Component)e.getSource(), 20, 30);
		}
		else if (e.getSource() == mntmBlExport) {
			doBlExportWorkingList();
		}
		else if (e.getSource() == mntmHtmlExport) {
			doHtmlExportWorkingList();
		}
		else if (e.getSource() == mntmTemplateExport) {
			doTemplateExport();
		}
		else if (e.getSource() == mntmBlImport) {
			do_importBlXml();
		}
		else if (e.getSource() == mntmBlCategories) {
			doImportBlCategories();
		}
		else if (e.getSource() == mntmBlSets) {
			doImportBlSets();
		}
		else if (e.getSource() == mntmBlColors) {
			doImportBlColors();
		}
		else if (e.getSource() == btnBrickColor) {
			doColorSelect();
		}
		else if (e.getSource() == btnAddBrickBl) {
			doAddBrickBl();
		}
		else if (e.getSource() == btnAddBrickLdd) {
			doAddBrickLdd();
		}
		else if (e.getSource() == btnAddBrickLdr) {
			doAddBrickLdr();
		}
		else if (e.getSource() == btnDupBrick) {
			doDupBrick();
		}
		else if (e.getSource() == btnDelBrick) {
			doDelBrick();
		}
		else if (e.getSource() == mntmBlParts) {
			doImportBlParts();
		}
		else if (e.getSource() == mntmLdrParts) {
			int response = JOptionPane.showConfirmDialog(frame, "Updating LDraw part libraries: are you sure?", 
					"Confirm update", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response != JOptionPane.YES_OPTION) 
				return;
			if (doDownloadLdrParts()) {
				doReadLdrParts(ldrlib);
			}
		}
		else if (e.getSource() == btnWhichSet) {
			doSelectWhichSet();
		}
		else if (e.getSource() == mntmPyImport) {
			doPyBrickImport();
		}
		else if (e.getSource() == mntmOptions) {
			
			OptionsDialog dlg = new OptionsDialog(frame,"Preferences",true);
			dlg.setVisible(true);
		}
		else if (e.getSource() == btnSearchBL) {
			doSearchBl();
		}
		else if (e.getSource() == btnSearchLdd) {
			doSearchLdd();
		}
		else if (e.getSource() == btnSearchLdr) {
			doSearchLdr();
		}
		else if (e.getSource() == btnNewList) {
			doNewList();
		}
		else if (e.getSource() == btnSetData) {
			SetDataDialog dlg = new SetDataDialog(frame,"Define brick list data",true);
			try {
				dlg.setData();
				dlg.setVisible(true);
				if (dlg.getResponse() == JOptionPane.OK_OPTION) {
					currentSet = dlg.getData();
					if (BrickSet.getCurrent() != null) {
						currentSet.update();
					}
					else {
						int index = currentSet.insert();
						AppSettings.putInt(MySettings.CURRENT_SET, index);
					}
					updateDetails();
				}
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(frame, "Problems with database\n Reason: "+e1.getLocalizedMessage(), 
						"Database error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[btnSetData] Database error",e1);
			}
		}
		else if (e.getSource() == btnAddToSet) {
			doAddToSet();
		}
		else if (e.getSource() == btnDelSet) {
			doDeleteSet();
		}
		else if (e.getSource() == btnSetMove) {
			doMoveSet();
		}
		else if (e.getSource() == btnSetCopy) {
			doCopySet();
		}
		else if (e.getSource() == mntmLdrawImport) {
			doImportLdrawProject();
		}
		else if (e.getSource() == btnSetExport) {
			doExportSet();
		}
		else if (e.getSource() == btnSetImport) {
			doImportSet();
		}
		else if (e.getSource() == btnCheckBuild) {
			doCheckBuildable();
		}
		else if (e.getSource() == mntmLddImport) {
			doImportLddProject();
		}
		else if (e.getSource() == mntmCheckUpdates) {
			checkUpdate(false);
		}
		else if (e.getSource() == mntmLibs) {
			LDLibManageDlg dlg = new LDLibManageDlg(frame, "Manage LDraw libraries", true, ldrlib);
			dlg.setVisible(true);
			saveLibs(ldrlib);
		}
		else if (e.getSource() == mntmAbout) {
			AboutDialog dlg = new AboutDialog(frame, "About", 
					new ImageIcon(this.getClass().getResource("images/BrickUtils.png")));
			dlg.setVisible(true);
		}
		else if (e.getSource() == mntmExit) {
			closeApp();
		}
		else
			return;
		updateCounts();
	}

	

	private void closeApp() {
		try {
			AppSettings.savePreferences();
		} catch (IOException | BackingStoreException e) {
			Logger.getGlobal().log(Level.SEVERE,"Unable to save preferences",e);
		}
		frame.dispose();
//		logFile.flush();
		System.exit(0);
	}
	

/*	
 * App init functions
 */	

	
	
	private void initPrefs() {
		AppSettings.openPreferences(this);
		
		AppSettings.addPref(MySettings.APP_CHECK_UPDATE, "Checks for program updates at startup", AppSettings.BOOLEAN);
		AppSettings.defBool(MySettings.APP_CHECK_UPDATE, true);
		AppSettings.addPref(MySettings.APP_UPDATE_URL,"BrickUtils update URL: ",AppSettings.STRING);
		AppSettings.defString(MySettings.APP_UPDATE_URL,VERSIONURL);
		AppSettings.addPref(MySettings.IN_PRODUCTION, "Use only colors currently in production", AppSettings.BOOLEAN);
		AppSettings.defBool(MySettings.IN_PRODUCTION, true);
		AppSettings.addPref(MySettings.LDR_OFFICIAL_URL, "LDraw official library download URL", AppSettings.STRING);
		AppSettings.defString(MySettings.LDR_OFFICIAL_URL,DEF_LDR_URL);
		AppSettings.addPref(MySettings.LDR_UNOFFICIAL_URL,"LDraw unofficial library URL: ", AppSettings.STRING);
		AppSettings.defString(MySettings.LDR_UNOFFICIAL_URL,DEF_UNOFF_LDR_URL);
		AppSettings.addPref(MySettings.MAP_UPDATE, "Checks for mapping updates at startup", AppSettings.BOOLEAN);
		AppSettings.defBool(MySettings.MAP_UPDATE, true);
		AppSettings.addPref(MySettings.MAP_UPDATE_URL,"Mapping update URL: ",AppSettings.STRING);
		AppSettings.defString(MySettings.MAP_UPDATE_URL,DEF_MAP_URL);
//private properties
		AppSettings.addPrivatePref(MySettings.CURRENT_SET, "Current set", AppSettings.INTEGER);
		AppSettings.addPrivatePref(MySettings.UPDATE_SERIAL, "Mapping update serial", AppSettings.INTEGER);
		AppSettings.defInt(MySettings.UPDATE_SERIAL, 1);
		AppSettings.addPrivatePref(MySettings.LDR_LIB_PATH, "Official lib filename", AppSettings.STRING);
		AppSettings.defString(MySettings.LDR_LIB_PATH, LDR_LIB_PATH);
		AppSettings.addPrivatePref(MySettings.LDR_UNOFF_LIB_PATH, "Unofficial lib filename", AppSettings.STRING);
		AppSettings.defString(MySettings.LDR_UNOFF_LIB_PATH, LDR_UNOFF_LIB_PATH);
		AppSettings.addPrivatePref(MySettings.LIBLIST, "Unofficial lib filename", AppSettings.STRING);
		AppSettings.addPrivatePref(MySettings.LIBOFFICIAL, "Default official LDraw lib path", AppSettings.STRING);
	}
	
	
	
	private void firstRun() {
		
		LDrawLib ldl = null;
		
		File db = new File(BRICKUTILSDB);
		if (!db.exists()) {
			int res = JOptionPane.showConfirmDialog(null, "You need database file to get program working:\n"+
					"do you want to download from Internet?\n"+
					"(Hit 'No' to exit program)", "First run", JOptionPane.YES_NO_OPTION);
			if (res == JOptionPane.NO_OPTION) {
				System.exit(0);
			}
			BusyDialog dlg = new BusyDialog(null,"Retrieve Database",true,icnImg);
			GetFileFromURL task = null;
			try {
				task = new GetFileFromURL(new URL(BRICKDBURL), db, dlg);
			} catch (MalformedURLException e1) {
				JOptionPane.showMessageDialog(null, "Problem with SF database URL","Internal error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"Error reading database download URL",e1);
				System.exit(1);
			}
			dlg.setTask(task);
			dlg.startTask();
			// after completing task return here
			try {
				task.get(10, TimeUnit.MILLISECONDS);
				//JOptionPane.showMessageDialog(null, "Downloaded "+r+" bytes.","Download Database",JOptionPane.INFORMATION_MESSAGE);
			}
			catch (ExecutionException e) {
				JOptionPane.showMessageDialog(null, "Unable to download Database from Internet\nReason: "+e.getLocalizedMessage()+
						"\nPlease retry later.", 
						"Download error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"Error downloading database",e);
				System.exit(1);
			} catch (InterruptedException e) {
				JOptionPane.showMessageDialog(null, "Task interrupted!\n Reason: "+e.getLocalizedMessage(), "Task interrupted",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"Database download interrupted",e);
				System.exit(1);
			} catch (TimeoutException e) {
				JOptionPane.showMessageDialog(null, "Timeout retrieving task output\nReason: "+e.getLocalizedMessage(), "Task timeout",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"Unknown error in database downloading",e);
				System.exit(1);
			}
		}
		try {
			dbc = new DBConnector("brickutils","brickutils","IFEXISTS=TRUE");
		} catch (ClassNotFoundException | SQLException e1) {
			JOptionPane.showMessageDialog(null, 
					"Unable to establish a connection with database.\n"
					+"You can't use program without it.\n"
					+ "Program now exits.",
					"Database error", JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[MAIN] Unable to establish a connection with database.",e1);
			System.exit(1);
		}
		if (AppSettings.get(MySettings.LIBOFFICIAL).equals("")) {
			// there is a standard LDraw library installed?
			if (LDrawLib.isAlreadyInstalled()) {
				int res = JOptionPane.showConfirmDialog(frame, 
						"There is a standard LDraw library installed.\nDo you want to use it?", 
						"Standard library found", JOptionPane.YES_NO_OPTION);
				if (res == JOptionPane.YES_OPTION) {
					try {
						ldl = new LDrawLib(LDrawLib.getInstalled(),dbc);
						if (!ldl.isLDrawStd(LDrawLib.OFFICIALINDEX)) 
							throw new IOException("Installed LDraw library isn't an official lib");
//						BusyDialog busyDialog = new BusyDialog(frame,"Import LDraw library",true,icnImg);
//						LDrawDBImportTask task = new LDrawDBImportTask(ldl, LDrawLib.OFFICIALINDEX);
//						busyDialog.setTask(task);
//						busyDialog.setMsg("Reading LDraw library...");
//						busyDialog.startTask();
//						// after completing task return here
//						busyDialog.dispose();
//						int r = task.get(10, TimeUnit.MILLISECONDS);
//						JOptionPane.showMessageDialog(frame, "Imported "+r+" parts from library.","LDraw Library",JOptionPane.INFORMATION_MESSAGE);
					}
					catch (IOException | SQLException ex) {
						JOptionPane.showMessageDialog(frame, "Unable to use LDraw library\nReason: "+ex.getLocalizedMessage(), 
								"Library error",JOptionPane.ERROR_MESSAGE);
						Logger.getGlobal().log(Level.SEVERE,"Unable to use LDraw installed library", ex);
					}
				}
			}
			// no standard library
			if (ldl == null) {
				File f = new File(AppSettings.get(MySettings.LDR_LIB_PATH));
				if (f.exists()) {
					try {
						ldl = new LDrawLib(AppSettings.get(MySettings.LDR_LIB_PATH), dbc);
						if (!ldl.isLDrawStd(LDrawLib.OFFICIALINDEX))
							throw new IOException("LDraw Library in default path isn't a standard library");
						File fu = new File(AppSettings.get(MySettings.LDR_UNOFF_LIB_PATH));
						if (fu.exists())
							ldl.addLDLib(AppSettings.get(MySettings.LDR_UNOFF_LIB_PATH), true);
					} catch (SQLException | IOException ex) {
						Logger.getGlobal().log(Level.WARNING, "LDraw library files in default path can't be used", ex);
						f.delete();
					}
				}
				if (!f.exists()) {
					int res = JOptionPane.showConfirmDialog(frame, 
							"No LDraw libs found, download defaults?\n(reply 'No' to manually select library)", 
							"No library found", JOptionPane.YES_NO_OPTION);
					if (res == JOptionPane.YES_OPTION) {
						if (!doDownloadLdrParts()) {
							JOptionPane.showMessageDialog(frame, "Unable to download LDraw libraries\nExiting now...", 
									"Library error",JOptionPane.ERROR_MESSAGE);
							Logger.getGlobal().log(Level.SEVERE,"LDraw Library error, unable to download files");
							System.exit(1);
						}
						try {
							ldl = new LDrawLib(AppSettings.get(MySettings.LDR_LIB_PATH), dbc);
							if (!ldl.isLDrawStd(LDrawLib.OFFICIALINDEX))
								throw new IOException("LDraw Library in default path isn't a standard library");
							File fu = new File(AppSettings.get(MySettings.LDR_UNOFF_LIB_PATH));
							if (fu.exists())
								ldl.addLDLib(AppSettings.get(MySettings.LDR_UNOFF_LIB_PATH), true);
						} catch (IOException | SQLException e) {
							JOptionPane.showMessageDialog(frame, "Unable to use downloaded LDraw libraries\nExiting now...", 
									"Library error",JOptionPane.ERROR_MESSAGE);
							Logger.getGlobal().log(Level.SEVERE,"LDraw Library error, unable to use downloaded library", e);
							System.exit(1);
						}
					}
					else {
						// manually select a library
						JFileChooser jfc = new JFileChooser(".");
						FileFilter ff = new FileNameExtensionFilter("LDraw Library ZIP", "zip");
						jfc.setFileFilter(ff);
						jfc.setDialogTitle("Select Official LDraw library (ZIP or folder)");
						jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
						while (true) {
							res = jfc.showOpenDialog(null);
							if (res == JFileChooser.APPROVE_OPTION) {
								try {							
									ldl = new LDrawLib(jfc.getSelectedFile().getPath(),dbc);
									if (!ldl.isLDrawStd(LDrawLib.OFFICIALINDEX)) {
										JOptionPane.showMessageDialog(null,
												"File/folder "+jfc.getSelectedFile().getName()+"\n" +
												"isn't an official LDraw library.\n"+
												"Try again.",
												"Unknown folder/file", JOptionPane.ERROR_MESSAGE);
										continue;
									}
									break;
								} catch (IOException | SQLException e) {
									JOptionPane.showMessageDialog(null,
											"Unable to use library "+jfc.getSelectedFile().getName()+"\n" +
											"Cause: "+e.getLocalizedMessage()+"\n"+
											"Try again.",
											"Library error", JOptionPane.ERROR_MESSAGE);
									Logger.getGlobal().log(Level.WARNING,"[firstRun] Error selecting library: "+jfc.getSelectedFile(),e);
								}
							}
							else {
								res = JOptionPane.showConfirmDialog(null, 
										"Do you want to exit program?\n",
										"Confirm", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
								if (res == JOptionPane.YES_OPTION) {
									System.exit(0);
								}
							}
						}
					}
				}
				
			}
			doReadLdrParts(ldl);
			LDLibManageDlg dlg = new LDLibManageDlg(frame, "Edit/add other libraries to use", true, ldl);
			dlg.setVisible(true);
			saveLibs(ldl);
		}

	}
	
	
	
	private LDrawLib loadLDrawLibs(DBConnector dbconn) throws ZipException, IOException, SQLException {
		
		LDrawLib l = new LDrawLib(AppSettings.get(MySettings.LIBOFFICIAL),dbc);
		String preflib = AppSettings.get(MySettings.LIBLIST);
		String[] libs = preflib.split("\\|");
		int n = 0;
		while (n < libs.length) {
			// add library and set if enabled (string == "t" is for "true")
			l.addLDLib(libs[n],libs[n+1].equals("t"));
			n += 2;
		}
		return l;
	}
	
	
	
	
	public void saveLibs(LDrawLib l) {
		
		String preflib = "";
		
		AppSettings.put(MySettings.LIBOFFICIAL, l.getPath(LDrawLib.OFFICIALINDEX));
		for (int i=1;i<l.count();i++) {
			if (i > 1) 
				preflib += "|";	// adds a separator
			preflib += l.getPath(i)+"|"+(l.isEnabled(i)?"t":"f"); 
		}
		AppSettings.put(MySettings.LIBLIST, preflib);
	}


	
	
//	public LDrawLib loadLibs() throws IOException, SQLException {
//		
//		LDrawLib l = new LDrawLib(dbc);
//		String preflib = AppSettings.get(MySettings.LIBLIST);
//		String[] libs = preflib.split("\\|");
//		int n = 0;
//		while (n < libs.length) {
//			// add library and set if official and enabled (string == "t" is for "true")
//			l.addLDLib(libs[n],libs[n+1].equals("t"),libs[n+2].equals("t"));
//			n += 3;
//		}
//		return l;
//	}
//	

	
	
	
	private void updateCounts() {
		
		int[] c;
		try {
			c = Brick.countWork();
		}
		catch (SQLException ex) {
			c = new int[] {0,0};
		}
		brickCount.setText(Integer.toString(c[1]));
		partCount.setText(Integer.toString(c[0]));
	}


	private void doCheckBuildable() {

		if (workingTableModel.getRowCount() == 0)
			return;
		CheckBuildableDialog dlg = new CheckBuildableDialog(frame,"Check if buildable",true,ldrlib,fileExport,icnImg);
		dlg.setVisible(true);
		refreshWorkList();
	}


	private void doHtmlExportWorkingList() {
		
		if (workingTableModel.getRowCount() == 0)
			return;
		fileExport.setExtension(".html");
		HTMLExporter he = new HTMLExporter(frame,workingTableModel.getParts(), fileExport,
				currentSet,icnImg);
		he.doExport();
	}


	private void doTemplateExport() {
		
		if (workingTableModel.getRowCount() == 0)
			return;
		int res = fileVelocity.showOpenDialog(frame);
		if (res != JFileChooser.APPROVE_OPTION) {
			return;
		}
		TemplateExportDialog dlg = new TemplateExportDialog(frame);
		dlg.setVisible(true);
		if (dlg.getResponse() != JOptionPane.OK_OPTION) {
			return;
		}
		// choose output file
		res = fileTemplateExport.showSaveDialog(frame);
		if (res != JFileChooser.APPROVE_OPTION) {
			return;
		}
		// do template build 
		dlg.doExport(fileVelocity.getSelectedFile().getName(),fileTemplateExport.getSelectedFile(),
				workingTableModel.getParts(),currentSet,ldrlib,icnImg);
	}


	private void doBlExportWorkingList() {
		
		if (workingTableModel.getRowCount() == 0)
			return;
		fileExport.setExtension(".xml");
		BricklinkExporter be = new BricklinkExporter(frame, workingTableModel.getParts(), fileExport);
		be.doExport();
	}



	
	private void doNewList() {

		if (workingTable.getRowCount() != 0) {
			int response = JOptionPane.showConfirmDialog(frame, "Working list contains bricks: proceed?", 
					"Confirm new list", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response != JOptionPane.YES_OPTION) 
				return;
		}
		try {
			Brick.emptyWork();
			currentSet = BrickSet.getCurrent();
			if (currentSet != null) {
				currentSet.delete();
			}
			refreshWorkList();
			updateDetails();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, "Problems with database\nReason: "+e.getLocalizedMessage(), 
					"Database error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doNewList] Database error",e);
		}
		
	}
	
	
	
	private void doImportLdrawProject() {
		
		if (workingTable.getRowCount() != 0) {
			int response = JOptionPane.showConfirmDialog(frame, "Working list contains bricks: replace with imported bricks?", 
					"Confirm replace", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response != JOptionPane.YES_OPTION) 
				return;
		}
		// check if work is full
		fileLdraw.setDialogTitle("Select an LDraw file (.ldr/.mpd)");
		int retval = fileLdraw.showOpenDialog(frame);
		if (retval != JFileChooser.APPROVE_OPTION) 
			return;
		File fname = fileLdraw.getSelectedFile();
		BusyDialog busyDialog = new BusyDialog(frame,"Import LDraw project file",true,icnImg);
		busyDialog.setLocationRelativeTo(frame);
		ImportLDrawProjectTask task = new ImportLDrawProjectTask(fname,ldrlib);
		busyDialog.setTask(task);
		busyDialog.setMsg("Reading LDraw project file...");
		busyDialog.startTask();
		// after completing task return here
		busyDialog.dispose();
		try {
			int r = task.get(10, TimeUnit.MILLISECONDS);
			if (task.isWarnings()) {
				JOptionPane.showMessageDialog(frame, "Imported "+r+" bricks, but\nthere are some errors. See logs for details.", 
						"LDraw import warnings",JOptionPane.WARNING_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(frame, "Imported "+r+" bricks.","LDraw Import",JOptionPane.INFORMATION_MESSAGE);
			}
			Brick.saveTmpToWork();
		}
		catch (ExecutionException e) {
			JOptionPane.showMessageDialog(frame, "Unable to read LDraw project\nReason: "+e.getLocalizedMessage(), 
					"Import error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doImportLrawProject] LDraw import error, file:"+fname,e);
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(frame, "Task interrupted!\n Reason: "+e.getLocalizedMessage(), "Task interrupted",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doImportLrawProject] Interrupted task",e);
		} catch (TimeoutException e) {
			JOptionPane.showMessageDialog(frame, "Timeout retrieving task output\nReason: "+e.getLocalizedMessage(), "Task timeout",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doImportLrawProject] Timeout error",e);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, "Problems with database\nReason: "+e.getLocalizedMessage(), 
					"Database error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doImportLrawProject] Database error",e);
		}
		refreshWorkList();
	
	}




	private void doImportLddProject() {
		
		if (workingTable.getRowCount() != 0) {
			int response = JOptionPane.showConfirmDialog(frame, "Working list contains bricks: replace with imported bricks?", 
					"Confirm replace", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response != JOptionPane.YES_OPTION) 
				return;
		}
		// check if work is full
		fileLdd.setDialogTitle("Select an LDD file (lxf/LXFML)");
		int retval = fileLdd.showOpenDialog(frame);
		if (retval != JFileChooser.APPROVE_OPTION) 
			return;
		File fname = fileLdd.getSelectedFile();
		BusyDialog busyDialog = new BusyDialog(frame,"Import LDD project file",true,icnImg);
		busyDialog.setLocationRelativeTo(frame);
		ImportLddProjectTask task = new ImportLddProjectTask(fname);
		busyDialog.setTask(task);
		busyDialog.setMsg("Reading LDD project file...");
		busyDialog.startTask();
		// after completing task return here
		busyDialog.dispose();
		try {
			int r = task.get(10, TimeUnit.MILLISECONDS);
			JOptionPane.showMessageDialog(frame, "Imported "+r+" bricks.","LDD Import",JOptionPane.INFORMATION_MESSAGE);
			Brick.saveTmpToWork();
		}
		catch (ExecutionException e) {
			JOptionPane.showMessageDialog(frame, "Unable to read LDD project\nReason: "+e.getLocalizedMessage(), 
					"Import error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doImportLddProject] LDD import error, file:"+fname,e);
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(frame, "Task interrupted!\n Reason: "+e.getLocalizedMessage(), "Task interrupted",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doImportLddProject] Import error",e);
		} catch (TimeoutException e) {
			JOptionPane.showMessageDialog(frame, "Timeout retrieving task output\nReason: "+e.getLocalizedMessage(), "Task timeout",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doImportLddProject] Task timeout error",e);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, "Problems with database\nReason: "+e.getLocalizedMessage(), 
					"Database error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doImportLddProject] Database error",e);
		}
		refreshWorkList();
	
	}

	

	private void doImportSet() {
		
		fileExport.setDialogType(JFileChooser.OPEN_DIALOG);
		fileExport.setDialogTitle("Select BrickUtils exported file");
		int retval = fileExport.showOpenDialog(frame);
		if (retval != JFileChooser.APPROVE_OPTION) 
			return;
		File fname = fileExport.getSelectedFile();
		BusyDialog busyDialog = new BusyDialog(frame,"Import BrickUtils export/backup file",true,icnImg);
		busyDialog.setLocationRelativeTo(frame);
		ImportBUtilsFile task = new ImportBUtilsFile(fname);
		busyDialog.setTask(task);
		busyDialog.setMsg("Reading BrickUtils brick catalog XML file...");
		busyDialog.startTask();
		// after completing task return here
		busyDialog.dispose();
		try {
			Integer[] r = task.get(10, TimeUnit.MILLISECONDS);
			JOptionPane.showMessageDialog(frame, "Imported "+r[1]+" set, with "+r[0]+" bricks.","Set Import",JOptionPane.INFORMATION_MESSAGE);
		}
		catch (ExecutionException e) {
			JOptionPane.showMessageDialog(frame, "Unable to read parts\nReason: "+e.getLocalizedMessage(), 
					"Import error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doImportSet] Set import error, file:"+fname,e);
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(frame, "Task interrupted!\n Reason: "+e.getLocalizedMessage(), "Task interrupted",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doImportSet] Task interrupted",e);
		} catch (TimeoutException e) {
			JOptionPane.showMessageDialog(frame, "Timeout retrieving task output\nReason: "+e.getLocalizedMessage(), "Task timeout",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doImportSet] Task timeout",e);
		}
		refreshSetList();
		refreshCatalogList();
	}


	
	private void doExportSet() {
			
		if (setTable.getSelectedRowCount() == 0)
			return;
		
		fileExport.setExtension(".xml");
		fileExport.setDialogType(JFileChooser.SAVE_DIALOG);
		fileExport.setDialogTitle("Choose a name for export");
		int retval = fileExport.showSaveDialog(frame);
		if (retval != JFileChooser.APPROVE_OPTION) 
			return;
		ArrayList<BrickSet> selSet = new ArrayList<BrickSet>();
		int[] sel = setTable.getSelectedRows();
		for (int r : sel) {
			selSet.add(setTableModel.getSet(setTable.convertRowIndexToModel(r)));
		}
		XMLOutputFactory output = XMLOutputFactory.newInstance();
		try {
			XMLStreamWriter writer = output.createXMLStreamWriter(new FileOutputStream(fileExport.getSelectedFile()),"UTF-8");
			writer.writeStartDocument("utf-8", "1.0");
			writer.writeCharacters("\n");
			// a comment for update serial and date
			writer.writeComment("Exported: "+fileExport.getSelectedFile().getName()+
					" Date: "+DateFormat.getInstance().format(Calendar.getInstance().getTime()));
			writer.writeCharacters("\n");
			// global start tag
			writer.writeStartElement("brickutils2");
			writer.writeCharacters("\n");
			for (BrickSet s:selSet) {
				s.XMLWrite(writer);
			}
			writer.writeEndElement();
			writer.writeCharacters("\n");
			writer.writeEndDocument();
			writer.flush();
			writer.close();
			JOptionPane.showMessageDialog(frame, "Exported "+selSet.size()+" set.","Set Export",JOptionPane.INFORMATION_MESSAGE);
		} catch (XMLStreamException e) {
			JOptionPane.showMessageDialog(frame, "Unable to write export file\nReason: "+e.getLocalizedMessage(), 
					"XML I/O error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doExportSet] XML stream error error, file:"+fileExport.getSelectedFile(),e);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Unable to write export file\nReason: "+e.getLocalizedMessage(), 
					"I/O error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doExportSet] Export I/O error, file:"+fileExport.getSelectedFile(),e);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, "Problems with database\nReason: "+e.getLocalizedMessage(), 
					"Database error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doExportSet] Database error",e);
		}
	}


	private void doSelectWhichSet() {
		
		if (catalogTable.getSelectedRowCount() != 1)
			return;
		int row = catalogTable.convertRowIndexToModel(catalogTable.getSelectedRow());
		Brick b = catalogTableModel.getBrick(row);
		try {
			ArrayList<BrickSet> bs = BrickSet.getByBrick(b.id);
			if (bs.size() == 0)
				throw new IllegalStateException("Program error: inconsistency in brick catalog, brick doesn't belong to any set");
			BrickSet.clearSelection();
			for (BrickSet s : bs) {
				s.select();
			}
			JOptionPane.showMessageDialog(frame, "Selected "+bs.size()+" set", 
					"Brick Use",JOptionPane.INFORMATION_MESSAGE);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, "Problems with database\nReason: "+e.getLocalizedMessage(), 
					"Database error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doSelectWhichSet] Database error",e);
		} catch (IllegalStateException e) {
			JOptionPane.showMessageDialog(frame, e.getLocalizedMessage(), 
					"Internal error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doSelectWhichSet] Internal error, part:"+b,e);
		}
		refreshSetList();
	}


	private void doPyBrickImport() {
	
		int bricks = 0, sets = 0;
		
		if (catalogTableModel.getRowCount() != 0) {
			JOptionPane.showMessageDialog(frame, "Importing bricks/set catalog from pyBrickUtils\n"+
					"is available only when your catalog is empty.\n" +
					"Please refer to program manual for details.", 
					"Import not available",JOptionPane.ERROR_MESSAGE);
			return;

		}
		
		fileBl.setDialogTitle("Select pyBrickUtils brick file");
		int retVal = fileBl.showOpenDialog(frame);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File fname = fileBl.getSelectedFile();
			BusyDialog busyDialog = new BusyDialog(frame,"Import pyBrickUtils bricks",true,icnImg);
			busyDialog.setLocationRelativeTo(frame);
			ImportPyBricks pybtask = new ImportPyBricks(fname);
			busyDialog.setTask(pybtask);
			busyDialog.setMsg("Reading pyBrickUtils brick catalog XML file...");
			busyDialog.startTask();
			// after completing task return here
			busyDialog.dispose();
			try {
				bricks = pybtask.get(10, TimeUnit.MILLISECONDS);
			}
			catch (ExecutionException ex) {
				JOptionPane.showMessageDialog(frame, "Unable to read parts\nReason: "+ex.getLocalizedMessage(), 
						"Import error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[doPyBrickImport] Parts import error, file:"+fname,ex);
				return;
			} catch (InterruptedException e1) {
				JOptionPane.showMessageDialog(frame, "Task interrupted!\n Reason: "+e1.getLocalizedMessage(), "Task interrupted",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[doPyBrickImport] Interrupted task, file:"+fname,e1);
				return;
			} catch (TimeoutException e1) {
				JOptionPane.showMessageDialog(frame, "Timeout retrieving task output\nReason: "+e1.getLocalizedMessage(), "Task timeout",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[doPyBrickImport] Task timeout, file:"+fname,e1);
				return;
			}
			fileBl.setDialogTitle("Select pyBrickUtils set file");
			retVal = fileBl.showOpenDialog(frame);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				File fsname = fileBl.getSelectedFile();
				busyDialog = new BusyDialog(frame,"Import pyBrickUtils sets",true,icnImg);
				busyDialog.setLocationRelativeTo(frame);
				ImportPySets pysettask = new ImportPySets(fsname);
				busyDialog.setTask(pysettask);
				busyDialog.setMsg("Reading pyBrickUtils sets catalog XML file...");
				busyDialog.startTask();
				// after completing task return here
				busyDialog.dispose();
				try {
					sets = pysettask.get(10, TimeUnit.MILLISECONDS);
					JOptionPane.showMessageDialog(frame, "Imported "+bricks+" parts\n in "+sets+" sets\nfrom pyBrickUtils Catalog.", 
							"Import Ok",JOptionPane.INFORMATION_MESSAGE);
				}
				catch (ExecutionException ex) {
					JOptionPane.showMessageDialog(frame, "Unable to read sets\nReason: "+ex.getLocalizedMessage(), 
							"Import error",JOptionPane.ERROR_MESSAGE);
					Logger.getGlobal().log(Level.SEVERE,"[doPyBrickImport] Set import error, file:"+fname,ex);
				} catch (InterruptedException e1) {
					JOptionPane.showMessageDialog(frame, "Task interrupted!\nReason: "+e1.getLocalizedMessage(), "Task interrupted",JOptionPane.ERROR_MESSAGE);
					Logger.getGlobal().log(Level.SEVERE,"[doPyBrickImport] Interrupted task, file:"+fname,e1);
				} catch (TimeoutException e1) {
					JOptionPane.showMessageDialog(frame, "Timeout retrieving task output\nReason: "+e1.getLocalizedMessage(), "Task timeout",JOptionPane.ERROR_MESSAGE);
					Logger.getGlobal().log(Level.SEVERE,"[doPyBrickImport] Task timeout, file:"+fname,e1);
				}
			}
			refreshSetList();
			refreshWorkList();
			refreshCatalogList();
		}
	}


//	private void doDeleteCatBrick() {
//
//		try {
//			if (catalogTable.getSelectedRowCount() >= 1) {
//				if (JOptionPane.showConfirmDialog(frame, "Only empty brick will be deleted.\nProceed? (deleted bricks are unrecoverable)", 
//						"Confirm delete",JOptionPane.OK_CANCEL_OPTION,
//						JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
//					if (catalogTable.getSelectedRowCount() == 1) {
//						catalogTableModel.deleteRow(catalogTable.convertRowIndexToModel(catalogTable.getSelectedRow()));
//					}
//					else {
//						int rows[] = catalogTable.getSelectedRows();
//						int delrows[] = new int[rows.length];
//						for (Integer i=0;i<rows.length;i++) {
//							delrows[i] = catalogTable.convertRowIndexToModel(rows[i]);
//						}
//						catalogTableModel.deleteRows(delrows);
//					}
//				}
//			}	
//		} catch (SQLException e) {
//			JOptionPane.showMessageDialog(frame, "Unable to delete part\n"+e.toString(), 
//					"Database error",JOptionPane.ERROR_MESSAGE);
//			e.printStackTrace();
//		}
//		refreshCatalogList();		
//	}


	private void refreshWorkList() {
		
		try {
			workingTableModel.setParts(Brick.getWork());
		} catch (SQLException e1) {
			Logger.getGlobal().log(Level.SEVERE,"[refreshWorkList] Database error",e1);
		}
	}
	
	
	
	private void refreshCatalogList() {
		
		try {
			catalogTableModel.setParts(Brick.catalogGet());
		} catch (SQLException e1) {
			Logger.getGlobal().log(Level.SEVERE,"[refreshCatalogList] Database error",e1);
		}
	}
	
	
	private void refreshSetList() {
		
		try {
			setTableModel.setParts(BrickSet.get());
		} catch (SQLException e1) {
			Logger.getGlobal().log(Level.SEVERE,"[refreshSetList] Database error",e1);
		}
	}
	
	
	

	private void doMoveSet() {
		
		if (setTable.getSelectedRowCount() != 1)
			return;
		int response = JOptionPane.showConfirmDialog(frame, "Brick and set will be deleted from catalog, are you sure?", 
				"Confirm move", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (response != JOptionPane.YES_OPTION) 
			return;
		if (workingTable.getRowCount() != 0) {
			response = JOptionPane.showConfirmDialog(frame, "Working list contains bricks: replace with set bricks?", 
					"Confirm replace", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response != JOptionPane.YES_OPTION) 
				return;
		}
		BrickSet bs = setTableModel.getSet(setTable.convertRowIndexToModel(setTable.getSelectedRow()));
		try {
			currentSet = BrickSet.getCurrent();
			if (currentSet != null) {
				currentSet.delete();
			}
			AppSettings.putInt(MySettings.CURRENT_SET, bs.id);
			Brick.emptyWork();
			bs.getBricksWork();
			bs.delBricks(true);
			updateDetails();
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, "Problems with database\nReason: "+e.getLocalizedMessage(), 
					"Database error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doMoveSet] Database error",e);
		}
		refreshSetList();
		refreshWorkList();
		refreshCatalogList();
	}
	
	
	

	private void doCopySet() {
		
		if (setTable.getSelectedRowCount() != 1)
			return;
		if (workingTable.getRowCount() != 0) {
			int response = JOptionPane.showConfirmDialog(frame, "Working list contains bricks: replace with set bricks?", 
					"Confirm replace", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response != JOptionPane.YES_OPTION) 
				return;
		}
		BrickSet bs = setTableModel.getSet(setTable.convertRowIndexToModel(setTable.getSelectedRow()));
		try {
			Brick.emptyWork();
			bs.getBricksWork();
			currentSet = BrickSet.getCurrent();
			if (currentSet != null) {
				// replace current set data with copied set
				bs.id = currentSet.id;
				bs.update();
			}
			else {
				// creates a new current set
				int index = bs.insert();
				AppSettings.putInt(MySettings.CURRENT_SET, index);
			}
			updateDetails();
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, "Problems with database\nReason: "+e.getLocalizedMessage(), 
					"Database error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doCopySet] Database error",e);
		}
		refreshSetList();
		refreshWorkList();
	}
	
	
	

	private void doDeleteSet() {
		
		if (setTable.getSelectedRowCount() != 1)
			return;
		int response = JOptionPane.showConfirmDialog(frame, "Delete: set and bricks are not recoverable\nAre you sure?", 
				"Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (response != JOptionPane.YES_OPTION) 
			return;
		int row = setTable.convertRowIndexToModel(setTable.getSelectedRow());
		BrickSet bs = setTableModel.getSet(row);
		try {
			bs.delBricks(true);
			bs.delete();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, "Problems with database\nReason: "+e.getLocalizedMessage(), 
					"Database error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doDeleteSet] Database error",e);
		}
		refreshSetList();
		refreshCatalogList();

	}

	

	private boolean doDownloadLdrParts() {

		BusyDialog dlg = new BusyDialog(frame,"Update LDraw libs",true,icnImg);
		LDrawLibUpdate chk = new LDrawLibUpdate(dlg);
		dlg.setTask(chk);
		dlg.startTask();
		// after completing task return here
		try {
			// gets LDraw parts from Internet
			chk.get(10, TimeUnit.MILLISECONDS);
			return true;
		}
		catch (ExecutionException ex) {
			JOptionPane.showMessageDialog(frame, "Unable to get updates\nReason: "+ex.getLocalizedMessage(), 
					"Update error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doDownloadLdrParts] Update error, library file:"+MySettings.LDR_LIB_PATH+
					" - "+MySettings.LDR_UNOFF_LIB_PATH,ex);
		} catch (InterruptedException e1) {
			JOptionPane.showMessageDialog(frame, "Task interrupted!\nReason: "+e1.getLocalizedMessage(), "Task interrupted",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doDownloadLdrParts] Task interrupted",e1);
		} catch (CancellationException ex) {
			JOptionPane.showMessageDialog(frame,"Cancelled by user\nReason: "+ex.getLocalizedMessage(), "Task cancel",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.WARNING,"[doDownloadLdrParts] User cancel task",ex);
		} catch (TimeoutException e1) {
			JOptionPane.showMessageDialog(frame, "Timeout retrieving task output\nReason: "+e1.getLocalizedMessage(), "Task timeout",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doDownloadLdrParts] Task timeout",e1);
		}
		return false;
	}


	
	
	private boolean doReadLdrParts(LDrawLib ldl) {

		try {
			BusyDialog dlg = new BusyDialog(frame, "Update parts database", true, icnImg);
			// update parts
			int total = 0;
			int official = 0;
			for (int i=0;i<ldl.count();i++) {
				LDrawDBImportTask ldrTask = new LDrawDBImportTask(ldl,i);
				if (i == LDrawLib.OFFICIALINDEX) 
					dlg.setMsg("Updates official parts DB");
				else
					dlg.setMsg("Updates unofficial parts #"+i);
				dlg.setProgress(0);
				dlg.setTask(ldrTask);
				dlg.startTask();
				int num = ldrTask.get(100, TimeUnit.MILLISECONDS);
				if (LDrawLib.OFFICIALINDEX == i)
					official = num;
				total += num;
			}
			JOptionPane.showMessageDialog(frame, "LDraw libraries updated.\n"+
					official+" official part\n"+total+" total parts.", 
				"Update Ok",JOptionPane.INFORMATION_MESSAGE);
			return true;
		}
		catch (ExecutionException ex) {
			JOptionPane.showMessageDialog(frame, "Unable to get updates\nReason: "+ex.getLocalizedMessage(), 
					"Update error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doReadLdrParts] Update error, library file:"+MySettings.LDR_LIB_PATH+
					" - "+MySettings.LDR_UNOFF_LIB_PATH,ex);
		} catch (InterruptedException e1) {
			JOptionPane.showMessageDialog(frame, "Task interrupted!\nReason: "+e1.getLocalizedMessage(), "Task interrupted",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doReadLdrParts] Task interrupted",e1);
		} catch (CancellationException ex) {
			JOptionPane.showMessageDialog(frame,"Cancelled by user\nReason: "+ex.getLocalizedMessage(), "Task cancel",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.WARNING,"[doReadLdrParts] User cancel task",ex);
		} catch (TimeoutException e1) {
			JOptionPane.showMessageDialog(frame, "Timeout retrieving task output\nReason: "+e1.getLocalizedMessage(), "Task timeout",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doReadLdrParts] Task timeout",e1);
		}
		return false;
	}


	
	
	private void doAddToSet() {
		
		if (workingTableModel.getParts().size() == 0)
			return;
		if ((currentSet == null) ||
			(currentSet.setid.length() == 0 && currentSet.type == BrickSet.OFFICIAL_SET) ||
			(currentSet.name.length() == 0 && (currentSet.type == BrickSet.GENERIC_LOT || currentSet.type == BrickSet.MOC)) ||
			(currentSet.setid.length() == 0 && currentSet.name.length() == 0 && currentSet.notes.length() == 0)) {
			JOptionPane.showMessageDialog(frame, "Please add some set/lot/moc description", 
					"No set data", JOptionPane.WARNING_MESSAGE);
			return;
		}
		AddSetDialog dlg = new AddSetDialog(frame, "Add bricks in list as Set/Lot/MOC",	true);
		dlg.setVisible(true);
		if (dlg.getResponse() == JOptionPane.OK_OPTION) {
			// add to set
			try {
				if (dlg.deleteSetData()) {
					currentSet.update();
				}
				else {
					currentSet.insert();
				}
				// add to catalog
				for (Brick b : workingTableModel.getParts()) {
					if (!dlg.addExtra() && b.extra)
						continue;
					// delete, if needed
					if (dlg.removeFromList()) {
						b.deleteWork();
					}
					currentSet.addBrick(b);
				}
				if (dlg.deleteSetData()) {
					AppSettings.putInt(MySettings.CURRENT_SET, 0);
					currentSet = null;
					updateDetails();
				}
				refreshCatalogList();
				refreshSetList();
				refreshWorkList();
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(frame, "Problem adding set\n"+e.toString(), 
						"Database error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[doAddToSet] Database error",e);
			}
		
		}
	}


	

	private void updateDetails() {
		
		try {
			currentSet = BrickSet.getCurrent();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, "Problems with database\nReason: "+e.getLocalizedMessage(), 
					"Database error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[updateDetails] Database error",e);
		}
		if (currentSet != null) {
			setid.setText(currentSet.setid);
			switch (currentSet.type) {
			case 1:
				setType.setText("Official set");
				break;
			case 2:
				setType.setText("Generic Lot");
				break;
			case 3:
				setType.setText("MOC (built)");
				break;
			}
			setCategory.setText(currentSet.category+" ("+currentSet.catid+")");
			setName.setText(currentSet.name);
			setNotes.setText(currentSet.notes);
			setYear.setText(Integer.toString(currentSet.year));
			setAvailable.setText(currentSet.available && currentSet.type!=3 ?"yes":"no");
		}
		else {
			setid.setText("-");
			setType.setText("-");
			setCategory.setText("-");
			setName.setText("-");
			setNotes.setText("-");
			setYear.setText("-");
			setAvailable.setText("-");			
		}
	}
	

	
	private void checkUpdate(boolean onStartup) {
				
		BusyDialog busyDialog = new BusyDialog(frame,"Update Parts and Colors",false,icnImg);
		busyDialog.setLocationRelativeTo(frame);
		CheckUpdate chk = new CheckUpdate(busyDialog, AppSettings.getInt(MySettings.UPDATE_SERIAL),AppSettings.get(MySettings.MAP_UPDATE_URL));
		busyDialog.setTask(chk);
		busyDialog.startTask();
		// after completing task return here
		try {
			Integer[] i = chk.get(10, TimeUnit.MILLISECONDS);
			if (i[0] == 0 && i[1] == 0) {
				if (! onStartup) {
					JOptionPane.showMessageDialog(frame, "No new updates to catalogs.", 
							"Check OK",JOptionPane.INFORMATION_MESSAGE);
				}
			}
			else {
				JOptionPane.showMessageDialog(frame, "Updated:\nParts: "+i[0]+"\nColors: "+i[1], 
					"Update Ok",JOptionPane.INFORMATION_MESSAGE);
			}
			AppSettings.putInt(MySettings.UPDATE_SERIAL, chk.getNextUpdate());
		}
		catch (ExecutionException ex) {
			JOptionPane.showMessageDialog(frame, "Unable to get updates\nReason: "+ex.getLocalizedMessage(), 
					"Update error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[checkUpdate] Unable to check/get updates",ex);
		} catch (InterruptedException e1) {
			JOptionPane.showMessageDialog(frame, "Task interrupted!\nReason: "+e1.getLocalizedMessage(), "Task interrupted",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[checkUpdate] Task interrupted",e1);
		} catch (TimeoutException e1) {
			JOptionPane.showMessageDialog(frame, "Timeout retrieving task output\nReason: "+e1.getLocalizedMessage(), "Task timeout",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[checkUpdate] Task timeout",e1);
		}
		busyDialog.dispose();
	}
	
	
	
	private void doDelBrick() {
		try {
			if (workingTable.getSelectedRowCount() >= 1) {
				if (JOptionPane.showConfirmDialog(frame, "Deleting "+workingTable.getSelectedRowCount()+
						" brick from working list. Proceed? (deleted rows are unrecoverable)", 
						"Confirm delete",JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
					if (workingTable.getSelectedRowCount() == 1) {
						workingTableModel.deleteRow(workingTable.convertRowIndexToModel(workingTable.getSelectedRow()));
					}
					else {
						int rows[] = workingTable.getSelectedRows();
						int delrows[] = new int[rows.length];
						for (int i=0;i<rows.length;i++) {
							delrows[i] = workingTable.convertRowIndexToModel(rows[i]);
						}
						workingTableModel.deleteRows(delrows);
					}
				}
			}	
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, "Unable to delete part\n"+e.toString(), 
					"Database error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doDelBrick] Database error",e);
		}
	}
	

	private void doAddBrickLdd() {
		
		int row;
		Brick b;
		
		lddSearchDlg.setAutoconvert(true);
		lddSearchDlg.setLocationRelativeTo(frame);
		lddSearchDlg.setVisible(true);
		if (lddSearchDlg.getResponse() == JOptionPane.OK_OPTION) {
			PartMapping pm = lddSearchDlg.getSelected();
			if (pm == null)
				return;
			if (!lddSearchDlg.convertIds()) {
				b = new Brick(pm.getDesignid());
				b.decorID = pm.getDecorid();
				b.name = pm.getName();
				b.color = 1;
				b.ldrawID = "";
				b.blID = "";
			}
			else {
				b = lddSearchDlg.getConvertedBrick();
			}
			try {
				row =  workingTable.convertRowIndexToView(workingTableModel.addRow(b));
			} catch (SQLException e) {
				row = 0;
				JOptionPane.showMessageDialog(frame, "Problem with database.\nReason: "+
						e.getLocalizedMessage(), "Database error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[doAddBrickLdd] Database error",e);
			}
			workingTable.setRowSelectionInterval(row, row);
			workingTable.scrollRectToVisible(workingTable.getCellRect(row,0, true)); 
		}
		
	}
	

	
	private void doAddBrickBl() {
		
		int row;
		Brick b;
		
		blSearchDlg.setAutoconvert(true);
		blSearchDlg.setLocationRelativeTo(frame);
		blSearchDlg.setVisible(true);
		if (blSearchDlg.getResponse() == JOptionPane.OK_OPTION) {
			BricklinkPart blp = blSearchDlg.getSelected();
			if (blp == null)
				return;
			if (!blSearchDlg.convertIds()) {
				b = new Brick();
				b.blID = blp.getBlid();
				b.name = blp.getName();
				b.color = 1;
				b.ldrawID = "";
				b.masterID = "";
				b.decorID = "";
				b.designID = "";
			}
			else {
				b = blSearchDlg.getConvertedBrick();
			}
			try {
				row =  workingTable.convertRowIndexToView(workingTableModel.addRow(b));
			} catch (SQLException e) {
				row = 0;
				JOptionPane.showMessageDialog(frame, "Problem with database.\nReason: "+
						e.getLocalizedMessage(), "Database error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[doAddBrickBl] Database error",e);
			}
			workingTable.setRowSelectionInterval(row, row);
			workingTable.scrollRectToVisible(workingTable.getCellRect(row,0, true)); 
		}
		
	}
	

	private void doAddBrickLdr() {
		
		int row;
		Brick b;
		
		ldrSearchDlg.setAutoconvert(true);
		ldrSearchDlg.setLocationRelativeTo(frame);
		ldrSearchDlg.setVisible(true);
		if (ldrSearchDlg.getResponse() == JOptionPane.OK_OPTION) {
			LDrawPart ldr = ldrSearchDlg.getSelected();
			if (ldr == null)
				return;
			if (!ldrSearchDlg.convertIds()) {
				b = new Brick();
				b.ldrawID = ldr.getLdrawId();
				b.name = ldr.getDescription();
				b.designID = "";
				b.masterID = "";
				b.decorID = "";
				b.color = 1;
				b.blID = "";
			}
			else {
				b = ldrSearchDlg.getConvertedBrick();
			}
			try {
				row =  workingTable.convertRowIndexToView(workingTableModel.addRow(b));
			} catch (SQLException e) {
				row = 0;
				JOptionPane.showMessageDialog(frame, "Problem with database.\nReason: "+
						e.getLocalizedMessage(), "Database error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[doAddBrickLdr] Database error",e);
			}
			workingTable.setRowSelectionInterval(row, row);
			workingTable.scrollRectToVisible(workingTable.getCellRect(row,0, true)); 
		}
		
	}
	

	
	private void doDupBrick() {
		
		try {
			if (workingTable.getSelectedRowCount() >= 1) {
				int idx;
				idx = workingTable.convertRowIndexToView(workingTableModel.dupRow(workingTable.convertRowIndexToModel(workingTable.getSelectedRow())));
				workingTable.setRowSelectionInterval(idx, idx);
				workingTable.scrollRectToVisible(workingTable.getCellRect(idx,0, true)); 
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, "Unable to add part mapping in database\n"+e.toString(), 
					"Database error",JOptionPane.ERROR_MESSAGE);
			Logger.getGlobal().log(Level.SEVERE,"[doDupBrick] Database error",e);
		}
		//partScrollPane.getVerticalScrollBar().setValue(partScrollPane.getVerticalScrollBar().getMinimum());
		// partScrollPane.getVerticalScrollBar().setValue(partScrollPane.getVerticalScrollBar().getMaximum());
		
	}
	

	
	
	
	private void doColorSelect() {
		
		if (workingTable.getSelectedRowCount() == 1) {
			int colid;
			int row = workingTable.convertRowIndexToModel(workingTable.getSelectedRow());
			try {
				colid = (Integer) workingTableModel.getValueAt(row,5);
			} catch (ClassCastException e) {
				// if selected column is not an Integer
				colid = 0;
			}
			ColorChooseDialog dlg = new ColorChooseDialog(frame, "Select Brick Color", true,colid,AppSettings.getBool(MySettings.IN_PRODUCTION));
			dlg.setVisible(true);
			if (dlg.getResponse() == JOptionPane.OK_OPTION) {
				workingTableModel.setValueAt(dlg.getSelected(),row ,5);
				workShapePanel.changeColor(dlg.getSelected());
				
			}
		}
	}
	
	
	
	private void doImportBlParts() {
		
		fileBl.setDialogTitle("Select BrickLink Parts XML file");
		int retVal = fileBl.showOpenDialog(frame);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File fname = fileBl.getSelectedFile();
			BusyDialog busyDialog = new BusyDialog(frame,"Update Bricklink parts",true,icnImg);
			busyDialog.setLocationRelativeTo(frame);
			BLPartImporter blptask = new BLPartImporter(fname);
			busyDialog.setTask(blptask);
			busyDialog.setMsg("Reading Bricklink parts from XML file...");
			busyDialog.startTask();
			// after completing task return here
			busyDialog.dispose();
			try {
				Integer i = blptask.get(10, TimeUnit.MILLISECONDS);
				JOptionPane.showMessageDialog(frame, "Updated "+i+" Bricklink parts.", 
						"Update Ok",JOptionPane.INFORMATION_MESSAGE);
			}
			catch (ExecutionException ex) {
				try {
					BricklinkPart.abortUpdate();
				} catch (SQLException e) {
					Logger.getGlobal().log(Level.SEVERE,"[doImportBlParts] Database error",e);
				}
				JOptionPane.showMessageDialog(frame, "Unable to update\nReason: "+ex.getLocalizedMessage(), 
						"Update error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[doImportBlParts] Import error, file:"+fname,ex);
			} catch (InterruptedException e1) {
				JOptionPane.showMessageDialog(frame, "Task interrupted!\nReason: "+e1.getLocalizedMessage(), "Task interrupted",JOptionPane.ERROR_MESSAGE);
				try {
					BricklinkPart.abortUpdate();
				} catch (SQLException e) {
					Logger.getGlobal().log(Level.SEVERE,"[doImportBlParts] Database error",e);
				}
				Logger.getGlobal().log(Level.SEVERE,"[doImportBlParts] Task interrupted",e1);
			} catch (TimeoutException e1) {
				JOptionPane.showMessageDialog(frame, "Timeout retrieving task output\nReason: "+e1.getLocalizedMessage(), "Task timeout",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[doImportBlParts] Task timeout",e1);
			}
		}
		
	}


	
	private void doImportBlSets() {
		
		File fname;

		fileBl.setDialogTitle("Select BrickLink Sets XML file");
		int retVal = fileBl.showOpenDialog(frame);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			fname = fileBl.getSelectedFile();
			BusyDialog busyDialog = new BusyDialog(frame,"Update Bricklink sets",true,icnImg);
			busyDialog.setLocationRelativeTo(frame);
			BLSetImporter blstask = new BLSetImporter(fname);
			busyDialog.setTask(blstask);
			busyDialog.setMsg("Reading Bricklink sets from XML file...");
			busyDialog.startTask();
			// after completing task return here
			busyDialog.dispose();
			try {
				Integer i = blstask.get(10, TimeUnit.MILLISECONDS);
				JOptionPane.showMessageDialog(frame, "Updated "+i+" Bricklink sets.", 
						"Update Ok",JOptionPane.INFORMATION_MESSAGE);
			}
			catch (ExecutionException ex) {
				JOptionPane.showMessageDialog(frame, "Unable to update\nReason: "+ex.getLocalizedMessage(), 
						"Update error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[doImportBlSets] Import error, file:"+fname,ex);
			} catch (InterruptedException e1) {
				JOptionPane.showMessageDialog(frame, "Task interrupted!\nReason: "+e1.getLocalizedMessage(), "Task interrupted",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[doImportBlSets] Task interrupted",e1);
			} catch (TimeoutException e1) {
				JOptionPane.showMessageDialog(frame, "Timeout retrieving task output\nReason: "+e1.getLocalizedMessage(), "Task timeout",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[doImportBlSets] Task timeout",e1);
			}
		}
	}


	private void doImportBlColors() {
		
		File fname;

		fileBl.setDialogTitle("Select BrickLink Colors XML file");
		int retVal = fileBl.showOpenDialog(frame);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			fname = fileBl.getSelectedFile();
			BricklinkColor bs = new BricklinkColor();
			int i;
			try {
				i = bs.doImport(fname);
				JOptionPane.showMessageDialog(frame, "Imported "+i+" Bricklink color", "Task Complete",JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(frame, "Unable to read file: "+fname.getPath()+"\nReason: "+
						e.getLocalizedMessage(), "File error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[doImportBlColors] File read error, file:"+fname,e);
			} catch (XMLStreamException e) {
				JOptionPane.showMessageDialog(frame, "Unable to read file: "+fname.getPath()+"\nReason: "+
						e.getLocalizedMessage(), "File error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[doImportBlColors] XML read error, file:"+fname,e);
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(frame, "Problem with database.\nReason: "+
						e.getLocalizedMessage(), "Database error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[doImportBlColors] Database error",e);
			}
		}
	}

	

	
	
	private void doImportBlCategories() {
		
		File fname;

		fileBl.setDialogTitle("Select BrickLink Categories XML file");
		int retVal = fileBl.showOpenDialog(frame);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			fname = fileBl.getSelectedFile();
			BusyDialog busyDialog = new BusyDialog(frame,"Update Bricklink categories",true,icnImg);
			busyDialog.setLocationRelativeTo(frame);
			BLCategoryImporter blctask = new BLCategoryImporter(fname);
			busyDialog.setTask(blctask);
			busyDialog.setMsg("Reading Bricklink categories from XML file...");
			busyDialog.startTask();
			// after completing task return here
			busyDialog.dispose();
			try {
				Integer i = blctask.get(10, TimeUnit.MILLISECONDS);
				JOptionPane.showMessageDialog(frame, "Updated "+i+" Bricklink categories.", 
						"Update Ok",JOptionPane.INFORMATION_MESSAGE);
			}
			catch (ExecutionException ex) {
				JOptionPane.showMessageDialog(frame, "Unable to update\nReason: "+ex.getLocalizedMessage(), 
						"Update error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[doImportBlCategories] File read error, file:"+fname,ex);
			} catch (InterruptedException e1) {
				JOptionPane.showMessageDialog(frame, "Task interrupted!\nReason: "+e1.getLocalizedMessage(), "Task interrupted",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[doImportBlCategories] Task interrupted",e1);
			} catch (TimeoutException e1) {
				JOptionPane.showMessageDialog(frame, "Timeout retrieving task output\nReason: "+e1.getLocalizedMessage(), "Task timeout",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[doImportBlCategories] Task timeout",e1);
			}
		}
	}


	
	private void doSearchLdd() {

		if (workingTable.getSelectedRowCount() == 1) {
			String part;
			try {
				part = (String) workingTableModel.getValueAt(
						workingTable.convertRowIndexToModel(workingTable.getSelectedRow()),
						workingTable.convertColumnIndexToModel(workingTable.getSelectedColumn())
						);
				if (part.endsWith(".dat")) {
					part = part.substring(0, part.indexOf(".dat"));
				}
			} catch (ClassCastException e) {
				// if selected column is not a string
				part = null;
			}
			lddSearchDlg.setPart(part);
			lddSearchDlg.setAutoconvert(false);
			lddSearchDlg.setLocationRelativeTo(frame);
			lddSearchDlg.setVisible(true);
			if (lddSearchDlg.getResponse() == JOptionPane.OK_OPTION) {
				PartMapping pm = lddSearchDlg.getSelected();
				if (pm == null)
					return;
				int row = workingTable.convertRowIndexToModel(workingTable.getSelectedRow());
				if (lddSearchDlg.convertIds()) {
					// must convert part to other catalog codes, if any
					Brick b = lddSearchDlg.getConvertedBrick();
					if (b != null) {
						try {
							workingTableModel.changeBrick(row, b);
						} catch (SQLException e) {
							JOptionPane.showMessageDialog(frame, "Problem with database.\nReason: "+
									e.getLocalizedMessage(), "Database error",JOptionPane.ERROR_MESSAGE);
							Logger.getGlobal().log(Level.SEVERE,"[doSearchLdd] Database error",e);
						}
					}
					
				}
				else {
					workingTableModel.setValueAt(pm.getDesignid(),row ,2);
					if (pm.getMasterid().length() > 0)
						workingTableModel.setValueAt(pm.getMasterid(),row ,1);
					else
						workingTableModel.setValueAt(pm.getDesignid(),row ,1);
					workingTableModel.setValueAt(pm.getDecorid(),row ,7);
					if (workingTableModel.getValueAt(row,8).equals("") ||
							((String)workingTableModel.getValueAt(row,8)).equals("##newpart")) 
					{
						// if description is empty or "##newpart" copy part description 
						workingTableModel.setValueAt(pm.getName(), row,8);
					}
				}
				ListSelectionEvent l = new ListSelectionEvent(workingTable.getSelectionModel(), row, row, false);
				valueChanged(l);
			}
		}	
	}



	private void doSearchBl() {

		if (workingTable.getSelectedRowCount() == 1) {
			String part;
			try {
				part = (String) workingTableModel.getValueAt(
						workingTable.convertRowIndexToModel(workingTable.getSelectedRow()),
						workingTable.convertColumnIndexToModel(workingTable.getSelectedColumn())
						);
			} catch (ClassCastException e) {
				// if selected column is not a string
				part = null;
			}
			blSearchDlg.setPart(part);
			blSearchDlg.setAutoconvert(false);
			blSearchDlg.setLocationRelativeTo(frame);
			blSearchDlg.setVisible(true);
			if (blSearchDlg.getResponse() == JOptionPane.OK_OPTION) {
				BricklinkPart blp = blSearchDlg.getSelected();
				if (blp == null)
					return;
				int row = workingTable.convertRowIndexToModel(workingTable.getSelectedRow());
				if (blSearchDlg.convertIds()) {
					// must convert part to other catalog codes, if any
					Brick b = blSearchDlg.getConvertedBrick();
					if (b != null) {
						try {
							workingTableModel.changeBrick(row, b);
						} catch (SQLException e) {
							JOptionPane.showMessageDialog(frame, "Problem with database.\nReason: "+
									e.getLocalizedMessage(), "Database error",JOptionPane.ERROR_MESSAGE);
							Logger.getGlobal().log(Level.SEVERE,"[doSearchBl] Database error",e);
						}
					}
					
				}
				else {
					workingTableModel.setValueAt(blp.getBlid(),row ,3);
					if (workingTableModel.getValueAt(row,8).equals("") ||
							((String)workingTableModel.getValueAt(row,8)).equals("##newpart")) 
					{
						// if description is empty or "##newpart" copy part description 
						workingTableModel.setValueAt(blp.getName(), row,8);
					}
				}
				ListSelectionEvent l = new ListSelectionEvent(workingTable.getSelectionModel(), row, row, false);
				valueChanged(l);
			}
		}	
	}


	private void doSearchLdr() {

		if (workingTable.getSelectedRowCount() == 1) {
			String part;
			try {
				part = (String) workingTableModel.getValueAt(workingTable.convertRowIndexToModel(workingTable.getSelectedRow()),
						workingTable.convertColumnIndexToModel(workingTable.getSelectedColumn()));
			} catch (ClassCastException e) {
				// if selected column is not a string
				part = null;
			}
			ldrSearchDlg.setPart(part);
			ldrSearchDlg.setAutoconvert(false);
			ldrSearchDlg.setLocationRelativeTo(frame);
			ldrSearchDlg.setVisible(true);
			if (ldrSearchDlg.getResponse() == JOptionPane.OK_OPTION) {
				bricksnspace.ldrawlib.LDrawPart ldr = ldrSearchDlg.getSelected();
				if (ldr == null)
					return;
				int row = workingTable.convertRowIndexToModel(workingTable.getSelectedRow());
				if (ldrSearchDlg.convertIds()) {
					// must convert part to other catalog codes, if any
					Brick b = ldrSearchDlg.getConvertedBrick();
					if (b != null) {
						try {
							workingTableModel.changeBrick(row, b);
						} catch (SQLException e) {
							JOptionPane.showMessageDialog(frame, "Problem with database.\nReason: "+
									e.getLocalizedMessage(), "Database error",JOptionPane.ERROR_MESSAGE);
							Logger.getGlobal().log(Level.SEVERE,"[doSearchLdr] Database error",e);
						}
					}
				}
				else {
					workingTableModel.setValueAt(ldr.getLdrawId(), row,4);
					if (workingTableModel.getValueAt(row,8).equals("") ||
							workingTableModel.getValueAt(row,8).equals("##newpart")) 
					{
						// if description is empty or "##newpart" copy part description 
						workingTableModel.setValueAt(ldr.getDescription(), row,8);
					}
				}
				ListSelectionEvent l = new ListSelectionEvent(workingTable.getSelectionModel(), row, row, false);
				valueChanged(l);
			}
		}	
	}

	
	

	
	private void do_importBlXml() {

		File fname;
		
		if (workingTable.getRowCount() != 0) {
			int response = JOptionPane.showConfirmDialog(frame, "Working list contains bricks: replace with set bricks?", 
					"Confirm replace", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response != JOptionPane.YES_OPTION) 
				return;
		}
		fileBl.setDialogTitle("Select Bricklink XML dump file");
		int retVal = fileBl.showOpenDialog(frame);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			fname = fileBl.getSelectedFile();
			BusyDialog busyDialog = new BusyDialog(frame,"Import Bricklink parts list",true,icnImg);
			busyDialog.setLocationRelativeTo(frame);
			ImportBlXMLTask blpltask = new ImportBlXMLTask(fname);
			busyDialog.setTask(blpltask);
			busyDialog.setMsg("Reading Bricklink parts list from XML file...");
			busyDialog.startTask();
			// after completing task return here
			busyDialog.dispose();
			try {
				Integer i = blpltask.get(10, TimeUnit.MILLISECONDS);
				JOptionPane.showMessageDialog(frame, "Imported "+i+" Bricklink parts from list.", 
						"Import Ok",JOptionPane.INFORMATION_MESSAGE);
			}
			catch (ExecutionException ex) {
				JOptionPane.showMessageDialog(frame, "Unable to read parts\nReason: "+ex.getLocalizedMessage(), 
						"Import error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[do_ImportBlXml] Import error, file:"+fname,ex);
			} catch (InterruptedException e1) {
				JOptionPane.showMessageDialog(frame, "Task interrupted!\nReason: "+e1.getLocalizedMessage(), "Task interrupted",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[do_ImportBlXml] Task interrupted error",e1);
			} catch (TimeoutException e1) {
				JOptionPane.showMessageDialog(frame, "Timeout retrieving task output\nReason: "+e1.getLocalizedMessage(), "Task timeout",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[do_ImportBlXml] Task timeout",e1);
			}
			try {
				BlImportDialog dlg = new BlImportDialog(frame, "Imported brick list", true);
				dlg.setVisible(true);
				workingTableModel.setParts(Brick.getWork());
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(frame, "Problem with database.\nReason: "+
						e1.getLocalizedMessage(), "Database error",JOptionPane.ERROR_MESSAGE);
				Logger.getGlobal().log(Level.SEVERE,"[do_ImportBlXml] Database error",e1);
			}
		}


	}

		

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
		            // Set cross-platform Java L&F (also called "Metal")
			        UIManager.setLookAndFeel(
			            UIManager.getCrossPlatformLookAndFeelClassName());
					brickUtils window = new brickUtils();
					window.frame.setVisible(true);
				} catch (Exception e) {
					Logger.getGlobal().log(Level.SEVERE,"[main] Exception in main",e);
					e.printStackTrace();
				}
			}
		});

	}


	@Override
	public void valueChanged(ListSelectionEvent e) {
		
		Brick b;

		if (e.getValueIsAdjusting())
			return;
		if (e.getSource() == workingTable.getSelectionModel()) {
			if (workingTable.getSelectedRow() < 0)
				return;
			b = workingTableModel.getBrick(workingTable.convertRowIndexToModel(workingTable.getSelectedRow()));
			if (b.ldrawID.length() > 0) {
				workShapePanel.setColor(b.color);
				workShapePanel.setLdrawid(b.ldrawID);
			}
			else if (b.designID.length() > 0) {
				workShapePanel.setColor(b.color);
				workShapePanel.setLddid(b.designID,b.decorID);
			}
			else if (b.blID.length() > 0) {
				workShapePanel.setColor(b.color);
				workShapePanel.setBlid(b.blID);
			}
		}
		if (e.getSource() == catalogTable.getSelectionModel()) {
			if (catalogTable.getSelectedRow() < 0)
				return;
			b = catalogTableModel.getBrick(catalogTable.convertRowIndexToModel(catalogTable.getSelectedRow()));
			if (b.ldrawID.length() > 0) {
				catalogShapePanel.setColor(b.color);
				catalogShapePanel.setLdrawid(b.ldrawID);
			}
			else if (b.designID.length() > 0) {
				catalogShapePanel.setColor(b.color);
				catalogShapePanel.setLddid(b.designID,b.decorID);
			}
			else if (b.blID.length() > 0) {
				catalogShapePanel.setColor(b.color);
				catalogShapePanel.setBlid(b.blID);
			}
		}
	}


}
