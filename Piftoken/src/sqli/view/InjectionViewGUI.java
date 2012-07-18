package sqli.view;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import database.model.Column;
import database.model.Database;
import database.model.ElementDatabase;
import database.model.Table;
import sqli.controller.InjectionController;
import sqli.model.InjectionModel;
import sqli.model.ObserverEvent;
import sqli.view.action.ActionHandler;
import sqli.view.jtabbedpane.JTabbedPaneHeader;
import sqli.view.jtabbedpane.JTablePane;
import sqli.view.jtree.TreeNodeEditor;
import sqli.view.jtree.TreeNodeModel;
import sqli.view.jtree.TreeNodeRenderer;

public class InjectionViewGUI extends JFrame implements Observer {
	private static final long serialVersionUID = 9164724117078636255L;
	
	private static final boolean IS_SELECTED = true;
	private static final String INFO_DEFAULT_VALUE = " -";

	private InjectionController controller;
	
	private JRadioButton radioGET = new JRadioButton("", IS_SELECTED);
	private JRadioButton radioPOST = new JRadioButton();
	private JRadioButton radioCookie = new JRadioButton();
	private JRadioButton radioHeader = new JRadioButton();
	
	private JTextField textGET = new JTextField("http://127.0.0.1/simulate_get.php?lib=");
	private JTextField textPOST = new JTextField();
	private JTextField textCookie = new JTextField();
	private JTextField textHeader = new JTextField();
	private JTextField textProxyAdress = new JTextField("127.0.0.1");
	private JTextField textProxyPort = new JTextField("8118");
	
	private JLabel labelDBVersion = new JLabel(INFO_DEFAULT_VALUE);
	private JLabel labelCurrentDB = new JLabel(INFO_DEFAULT_VALUE);
	private JLabel labelCurrentUser = new JLabel(INFO_DEFAULT_VALUE);
	private JLabel labelAuthenticatedUser = new JLabel(INFO_DEFAULT_VALUE);

	private JCheckBox checkboxIsProxy = new JCheckBox("Proxy", IS_SELECTED);
	private final ImageIcon squareIcon = new ImageIcon(getClass().getResource("/bullet_square_grey.png"));
	
	private JLabel labelNormal = new JLabel("Normal", squareIcon, SwingConstants.LEFT);
	private JLabel labelErrorBase = new JLabel("ErrorBase", squareIcon, SwingConstants.LEFT);
	private JLabel labelBlind = new JLabel("Blind", squareIcon, SwingConstants.LEFT);
	
	private JTree databaseTree;
	private JTabbedPane valuesTabbedPane = new JTabbedPane();

	public JTextArea consoleArea = new JTextArea();

	public InjectionViewGUI(InjectionController newController){		
		super("jSQL Injection");
		
		
//		//Check the SystemTray support
//        if (!SystemTray.isSupported()) {
//            System.out.println("SystemTray is not supported");
//            return;
//        }
//        final PopupMenu popup = new PopupMenu();
//        final TrayIcon trayIcon =
//                new TrayIcon(new ImageIcon(InjectionViewGUI.class.getResource("/loader.gif")).getImage());
//        final SystemTray tray = SystemTray.getSystemTray();
//         
//        // Create a popup menu components
//        MenuItem aboutItem = new MenuItem("About");
//        CheckboxMenuItem cb1 = new CheckboxMenuItem("Set auto size");
//        CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
//        Menu displayMenu = new Menu("Display");
//        MenuItem errorItem = new MenuItem("Error");
//        MenuItem warningItem = new MenuItem("Warning");
//        MenuItem infoItem = new MenuItem("Info");
//        MenuItem noneItem = new MenuItem("None");
//        MenuItem exitItem = new MenuItem("Exit");
//         
//        //Add components to popup menu
//        popup.add(aboutItem);
//        popup.addSeparator();
//        popup.add(cb1);
//        popup.add(cb2);
//        popup.addSeparator();
//        popup.add(displayMenu);
//        displayMenu.add(errorItem);
//        displayMenu.add(warningItem);
//        displayMenu.add(infoItem);
//        displayMenu.add(noneItem);
//        popup.add(exitItem);
//         
//        trayIcon.setPopupMenu(popup);
//         
//        try {
//            tray.add(trayIcon);
//        } catch (AWTException e) {
//            System.out.println("TrayIcon could not be added.");
//            return;
//        }
//         
//        trayIcon.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(null,
//                        "This dialog box is run from System Tray");
//            }
//        });
//         
//        aboutItem.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(null,
//                        "This dialog box is run from the About menu item");
//            }
//        });
//         
//        cb1.addItemListener(new ItemListener() {
//            public void itemStateChanged(ItemEvent e) {
//                int cb1Id = e.getStateChange();
//                if (cb1Id == ItemEvent.SELECTED){
//                    trayIcon.setImageAutoSize(true);
//                } else {
//                    trayIcon.setImageAutoSize(false);
//                }
//            }
//        });
//         
//        cb2.addItemListener(new ItemListener() {
//            public void itemStateChanged(ItemEvent e) {
//                int cb2Id = e.getStateChange();
//                if (cb2Id == ItemEvent.SELECTED){
//                    trayIcon.setToolTip("Sun TrayIcon");
//                } else {
//                    trayIcon.setToolTip(null);
//                }
//            }
//        });
//         
//        ActionListener listener = new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                MenuItem item = (MenuItem)e.getSource();
//                //TrayIcon.MessageType type = null;
//                System.out.println(item.getLabel());
//                if ("Error".equals(item.getLabel())) {
//                    //type = TrayIcon.MessageType.ERROR;
//                    trayIcon.displayMessage("Sun TrayIcon Demo",
//                            "This is an error message", TrayIcon.MessageType.ERROR);
//                     
//                } else if ("Warning".equals(item.getLabel())) {
//                    //type = TrayIcon.MessageType.WARNING;
//                    trayIcon.displayMessage("Sun TrayIcon Demo",
//                            "This is a warning message", TrayIcon.MessageType.WARNING);
//                     
//                } else if ("Info".equals(item.getLabel())) {
//                    //type = TrayIcon.MessageType.INFO;
//                    trayIcon.displayMessage("Sun TrayIcon Demo",
//                            "This is an info message", TrayIcon.MessageType.INFO);
//                     
//                } else if ("None".equals(item.getLabel())) {
//                    //type = TrayIcon.MessageType.NONE;
//                    trayIcon.displayMessage("Sun TrayIcon Demo",
//                            "This is an ordinary message", TrayIcon.MessageType.NONE);
//                }
//            }
//        };
//         
//        errorItem.addActionListener(listener);
//        warningItem.addActionListener(listener);
//        infoItem.addActionListener(listener);
//        noneItem.addActionListener(listener);
//         
//        exitItem.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                tray.remove(trayIcon);
//                System.exit(0);
//            }
//        });
		
		
		ToolTipManager.sharedInstance().setDismissDelay(60000);
		textGET.setToolTipText("<html>The connection url: <b>http://hostname:port/path</b><br>" +
				"Add optional GET query: <b>http://hostname:port/path?parameter1=value1&parameterN=valueN</b><br><br>" +
				"<b><u>If you know injection works with GET</u></b>, select corresponding radio on the right,<br><br>" +
				"<b><u>Last parameter</u></b> in GET query is the injection parameter (non-intuitive, but all the rest of this application should be intuitive),<br><br>" +
				"You can omit the value of the last parameter and let the application find the best one:<br>" +
				"<b>http://hostname:port/path?parameter1=value1&parameter2=</b><br><br>" +
				"Or you can force the value if you think it's the legit one:<br>" +
				"<b>http://hostname:port/path?parameter1=value1&parameter2=0'</b></html>");
		textPOST.setToolTipText("<html>Add optional POST data: <b>parameter1=value1&parameterN=valueN</b><br>" +
				"<br><b><u>If you know injection works with POST</u></b>, select corresponding radio on the right,<br><br>" +
				"<b><u>Last parameter</u></b> in POST data is the injection parameter (non-intuitive, but all the rest of this application should be intuitive),<br><br>" +
				"You can omit the value of the last parameter and let the application find the best one:<br>" +
				"<b>parameter1=value1&parameter2=</b><br><br>" +
				"Or you can force the value if you think it's the legit one:<br>" +
				"<b>parameter1=value1&parameter2=0'</b></html>");
		textCookie.setToolTipText("<html>Add optional Cookie data: <b>parameter1=value1;parameterN=valueN</b><br>" +
				"<br><b><u>If you know injection works with Cookie</u></b>, select corresponding radio on the right,<br><br>" +
				"<b><u>Last parameter</u></b> in Cookie data is the injection parameter (non-intuitive, but all the rest of this application should be intuitive),<br><br>" +
				"You can omit the value of the last parameter and let the application find the best one:<br>" +
				"<b>parameter1=value1;parameter2=</b><br><br>" +
				"Or you can force the value if you think it's the legit one:<br>" +
				"<b>parameter1=value1;parameter2=0'</b></html>");
		textHeader.setToolTipText("<html>Add optional Header data: <b>parameter1:value1\\r\\nparameterN:valueN</b><br>" +
				"<br><b><u>If you know injection works with Header</u></b>, select corresponding radio on the right,<br><br>" +
				"<b><u>Last parameter</u></b> in Header data is the injection parameter (non-intuitive, but all the rest of this application should be intuitive),<br><br>" +
				"You can omit the value of the last parameter and let the application find the best one:<br>" +
				"<b>parameter1:value1\\r\\nparameterN:</b><br><br>" +
				"Or you can force the value if you think it's the legit one:<br>" +
				"<b>parameter1:value1\\r\\nparameterN:0'</b></html>");
		
		radioGET.setToolTipText("Inject via GET data");
		radioPOST.setToolTipText("Inject via POST data");
		radioCookie.setToolTipText("Inject via cookie data");
		radioHeader.setToolTipText("Inject via header data");
		
		checkboxIsProxy.setToolTipText("Use proxy connection");

		this.controller = newController;
		
//		//Where the GUI is created:
//		JMenuBar menuBar = new JMenuBar();
//		JMenu menuFile = new JMenu("File");
//		menuFile.add(new JMenuItem("New connection..."));
//		menuFile.add(new JSeparator());
//		menuFile.add(new JMenuItem("Exit"));
//		
//		JMenu menuHelp = new JMenu("Help");
//		menuHelp.add(new JMenuItem("About"));
//		menuHelp.add(new JSeparator());
//		menuHelp.add(new JMenuItem("Preferences"));
//		
//		menuBar.add(menuFile);
//		menuBar.add(menuHelp);
//		
//		this.setJMenuBar(menuBar);
		
		this.getContentPane().setLayout( new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS) );
		JPanel mainPanel = new JPanel(new GridLayout(1,0));
		mainPanel.add(new OutputPanel());
		
		this.add(new InputPanel());
		this.add(mainPanel);
		this.add(new StatusPanel());
        
		this.pack(); // nécessaire après le masquage des param proxy
        this.setSize(1024, 768);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		new ActionHandler(this.getRootPane(), valuesTabbedPane);
	}
	
	private class InputPanel extends JPanel implements ActionListener{
		private static final long serialVersionUID = -1242173041381245542L;

		private ButtonGroup methodSelected = new ButtonGroup();
		
		public InputPanel(){
			this.setLayout( new BoxLayout(this, BoxLayout.PAGE_AXIS) );
			
			JPanel connectionPanel = new JPanel();
			GroupLayout connectionLayout = new GroupLayout(connectionPanel);
			connectionPanel.setLayout(connectionLayout);
			
			connectionPanel.setBorder(
				BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Connection"),
                        BorderFactory.createEmptyBorder()
				));
		    
			this.add(connectionPanel);
			
			final JPanel settingPanel = new JPanel();
			GroupLayout settingLayout = new GroupLayout(settingPanel);
			settingPanel.setLayout(settingLayout);
			settingPanel.setVisible(false);
			
			settingPanel.setBorder(
				BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Proxy Setting"),
                        BorderFactory.createEmptyBorder()
				));
		                
			this.add(settingPanel);
			
	           radioGET.setActionCommand("GET");
	          radioPOST.setActionCommand("POST");
	        radioCookie.setActionCommand("COOKIE");
	        radioHeader.setActionCommand("HEADER");

			methodSelected.add(radioGET);
	        methodSelected.add(radioPOST);
	        methodSelected.add(radioCookie);
	        methodSelected.add(radioHeader);
	        
	        JPanel buttonsPanel = new JPanel();
	        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	        buttonsPanel.setLayout( new BoxLayout(buttonsPanel, BoxLayout.X_AXIS) );
	        
	        JButton submitButton = new JButton("Submit");
			submitButton.addActionListener(this);	
			
			final JButton settingButton = new JButton("Show Settings");
			settingButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					settingPanel.setVisible(!settingPanel.isVisible());
					settingButton.setText(settingPanel.isVisible()?"Hide Settings":"Show Settings");
				}
			});
			
			buttonsPanel.add(submitButton);
			buttonsPanel.add(Box.createHorizontalGlue());
			buttonsPanel.add(settingButton);
			
			this.add(buttonsPanel);
			
			   JLabel labelGET = new JLabel("Url  ");
			  JLabel labelPOST = new JLabel("POST  ");
			JLabel labelCookie = new JLabel("Cookie  ");
			JLabel labelHeader = new JLabel("Header  ");
			
			JLabel labelProxyAdress = new JLabel("Proxy adress  ");
			JLabel labelProxyPort = new JLabel("Proxy port  ");

			Font plainFont = new Font(labelDBVersion.getFont().getName(),Font.PLAIN,labelDBVersion.getFont().getSize());
			labelGET.setFont(plainFont);
			labelPOST.setFont(plainFont);
			labelCookie.setFont(plainFont);
			labelHeader.setFont(plainFont);
			labelProxyAdress.setFont(plainFont);
			labelProxyPort.setFont(plainFont);
			checkboxIsProxy.setFont(plainFont);
			
	        connectionLayout.setHorizontalGroup(
		        connectionLayout.createSequentialGroup()
		        		.addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.TRAILING,false)
		        				.addComponent(labelGET)
		        				.addComponent(labelPOST)
		        				.addComponent(labelCookie)
		        				.addComponent(labelHeader)
		        				.addComponent(labelProxyAdress)
		        				.addComponent(labelProxyPort))
		        		.addGroup(connectionLayout.createParallelGroup()
		        				.addComponent(textGET)
		        				.addComponent(textPOST)
		        				.addComponent(textCookie)
		        				.addComponent(textHeader)
		        				.addComponent(textProxyAdress)
		        				.addComponent(textProxyPort))
		        		.addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.LEADING,false)
		        				.addComponent(radioGET)
		        				.addComponent(radioPOST)
		        				.addComponent(radioCookie)
		        				.addComponent(radioHeader)
		        				.addComponent(checkboxIsProxy))
		 	);
		        
	        connectionLayout.setVerticalGroup(
	    		connectionLayout.createSequentialGroup()
		        		.addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		        				.addComponent(labelGET)
		        				.addComponent(textGET)
		        				.addComponent(radioGET))
		        		.addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		        				.addComponent(labelPOST)
		        				.addComponent(textPOST)
		        				.addComponent(radioPOST))
		        		.addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		        				.addComponent(labelCookie)
		        				.addComponent(textCookie)
		        				.addComponent(radioCookie))
		        		.addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		        				.addComponent(labelHeader)
		        				.addComponent(textHeader)
		        				.addComponent(radioHeader))
		        		.addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		        				.addComponent(labelProxyAdress)
		        				.addComponent(textProxyAdress)
		        				.addComponent(checkboxIsProxy))
		        		.addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		        				.addComponent(labelProxyPort)
		        				.addComponent(textProxyPort))  	    	    	        		
			);
	        
	        settingLayout.setHorizontalGroup(
		        settingLayout.createSequentialGroup()
		        		.addGroup(settingLayout.createParallelGroup(GroupLayout.Alignment.TRAILING,false)
		        				.addComponent(labelProxyAdress)
		        				.addComponent(labelProxyPort))
		        		.addGroup(settingLayout.createParallelGroup()
		        				.addComponent(textProxyAdress)
		        				.addComponent(textProxyPort))
		        		.addGroup(settingLayout.createParallelGroup(GroupLayout.Alignment.LEADING,false)
		        				.addComponent(checkboxIsProxy))
		 	);
	        
	        settingLayout.setVerticalGroup(
	    		settingLayout.createSequentialGroup()
	        		.addGroup(settingLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	        				.addComponent(labelProxyAdress)
	        				.addComponent(textProxyAdress)
	        				.addComponent(checkboxIsProxy))
	        		.addGroup(settingLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	        				.addComponent(labelProxyPort)
	        				.addComponent(textProxyPort))
			);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int option = JOptionPane.showConfirmDialog(null, 
					"Start a new injection?", "New injection", JOptionPane.OK_CANCEL_OPTION);
			
			if(option == JOptionPane.OK_OPTION){
				controller.controlInput(
						   textGET.getText(), 
						  textPOST.getText(), 
						textCookie.getText(), 
						textHeader.getText(), 
						 methodSelected.getSelection().getActionCommand(),
						checkboxIsProxy.isSelected(),
						textProxyAdress.getText(),
						  textProxyPort.getText()
					);
				
				DefaultTreeModel treeModel = (DefaultTreeModel) databaseTree.getModel();
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
				
				consoleArea.setText("");
				valuesTabbedPane.removeAll();
				root.removeAllChildren();
				treeModel.nodeChanged(root);
				treeModel.reload();
				
				labelDBVersion.setText(INFO_DEFAULT_VALUE);
				labelCurrentDB.setText(INFO_DEFAULT_VALUE);
				labelCurrentUser.setText(INFO_DEFAULT_VALUE);
				labelAuthenticatedUser.setText(INFO_DEFAULT_VALUE);
				
				labelNormal.setIcon(squareIcon);
				labelErrorBase.setIcon(squareIcon);
				labelBlind.setIcon(squareIcon);
			}
		}
	}

	private class OutputPanel extends JSplitPane{
		private static final long serialVersionUID = -5696939494054282278L;

		public OutputPanel(){
			super(JSplitPane.VERTICAL_SPLIT, true);
			
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("Databases");
			databaseTree = new JTree(root);
			
			TreeNodeRenderer renderer = new TreeNodeRenderer();
			databaseTree.setCellRenderer(renderer);
			
		    TreeNodeEditor editor = new TreeNodeEditor(databaseTree, controller, valuesTabbedPane);
		    databaseTree.setCellEditor(editor);
		    
		    databaseTree.setEditable(true);	// allows repaint nodes
		    databaseTree.setShowsRootHandles(true);
			databaseTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		    
		    databaseTree.getModel().addTreeModelListener(new TreeModelListener() { // allows repaint progressbar
				
				@Override public void treeStructureChanged(TreeModelEvent arg0) {}
				@Override public void treeNodesRemoved(TreeModelEvent arg0) {}
				@Override public void treeNodesInserted(TreeModelEvent arg0) {}
				@Override
				public void treeNodesChanged(TreeModelEvent arg0) {
					if(arg0 != null){
						databaseTree.firePropertyChange(
							JTree.ROOT_VISIBLE_PROPERTY, 
							!databaseTree.isRootVisible(), 
							databaseTree.isRootVisible()
						);
					}
				}
				
			});
			
			JSplitPane treeAndTableSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
			treeAndTableSplitPane.setLeftComponent( new JScrollPane(databaseTree) );
			treeAndTableSplitPane.setRightComponent( valuesTabbedPane );
			treeAndTableSplitPane.setDividerLocation(285);
			treeAndTableSplitPane.setOneTouchExpandable(true);
			
	        this.setTopComponent(treeAndTableSplitPane);
	        this.setBottomComponent( new JScrollPane(consoleArea) );
	        this.setDividerLocation(280);
	        this.setOneTouchExpandable(true);
	        
	        this.setResizeWeight(1); // defines left and bottom pane
	        
	        consoleArea.setFont(new Font("Courier New",Font.PLAIN,consoleArea.getFont().getSize()));
	        consoleArea.setEditable(false);
	        consoleArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		}
   	}
	
	private class StatusPanel extends JPanel{
		private static final long serialVersionUID = -5439904812395393271L;

		public StatusPanel(){
			this.setLayout( new BoxLayout(this, BoxLayout.LINE_AXIS) );
			this.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			
			JPanel connectionInfos = new JPanel();
			GroupLayout layout = new GroupLayout(connectionInfos);

			connectionInfos.setLayout(layout);
			connectionInfos.setAlignmentX(Component.LEFT_ALIGNMENT);

			this.add(connectionInfos);
			this.add(Box.createHorizontalGlue());
			
			Font plainFont = new Font(labelDBVersion.getFont().getName(),Font.PLAIN,labelDBVersion.getFont().getSize());
//				    labelDBVersion.setFont(plainFont);
//				    labelCurrentDB.setFont(plainFont);
//			      labelCurrentUser.setFont(plainFont);
//			labelAuthenticatedUser.setFont(plainFont);
			
			labelNormal.setFont(plainFont);
			labelErrorBase.setFont(plainFont);
			labelBlind.setFont(plainFont);
			
			JLabel titleDatabaseVersion = new JLabel("Database version");
			JLabel titleCurrentDB = new JLabel("Current db");
			JLabel titleCurrentUser = new JLabel("Current user");
			JLabel titleAuthenticatedUser = new JLabel("Authenticated user");
			
			titleDatabaseVersion.setFont(plainFont);
			titleCurrentDB.setFont(plainFont);
			titleCurrentUser.setFont(plainFont);
			titleAuthenticatedUser.setFont(plainFont);
			
			JPanel injectionType = new JPanel();
			injectionType.setLayout( new BoxLayout(injectionType, BoxLayout.PAGE_AXIS) );
			injectionType.add(labelNormal);
			injectionType.add(labelErrorBase);
			injectionType.add(labelBlind);
			
			this.add(injectionType);
			
	        layout.setHorizontalGroup(
	        	layout.createSequentialGroup()
	        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING,false)
	        				.addComponent(titleDatabaseVersion)
	        				.addComponent(titleCurrentDB)
	        				.addComponent(titleCurrentUser)
	        				.addComponent(titleAuthenticatedUser))
	        		.addGroup(layout.createParallelGroup()
	        				.addComponent(labelDBVersion)
	        				.addComponent(labelCurrentDB)
	        				.addComponent(labelCurrentUser)
	        				.addComponent(labelAuthenticatedUser))
		 	);
		        
	        layout.setVerticalGroup(
	    		layout.createSequentialGroup()
	        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	        				.addComponent(titleDatabaseVersion)
	        				.addComponent(labelDBVersion))
	        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	        				.addComponent(titleCurrentDB)
	        				.addComponent(labelCurrentDB))
	        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	        				.addComponent(titleCurrentUser)
	        				.addComponent(labelCurrentUser))
	        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	        				.addComponent(titleAuthenticatedUser)
	        				.addComponent(labelAuthenticatedUser))
		    	    	    	        		
			);			
		}
	}
	
	Map<ElementDatabase,DefaultMutableTreeNode> 
		treeNodeModels = new HashMap<ElementDatabase,DefaultMutableTreeNode>();
	
	@Override
	public void update(Observable arg0, Object arg1) {
		InjectionModel model = (InjectionModel) arg0;
		ObserverEvent oEvent = (ObserverEvent) arg1;
		DefaultTreeModel treeModel = (DefaultTreeModel) databaseTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
		
		if( "console-message".equals(""+oEvent) ){
			consoleArea.append(""+oEvent.getArg());
			consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
			
		}else if( "update-progressbar".equals(""+oEvent) ){
			Object[] progressionData = (Object[]) oEvent.getArg();
			ElementDatabase dataElementDatabase = (ElementDatabase) progressionData[0];
			int dataCount = (Integer) progressionData[1];
			
			TreeNodeModel<?> progressingTreeNodeModel = 
					(TreeNodeModel<?>) treeNodeModels.get(dataElementDatabase).getUserObject();
			progressingTreeNodeModel.childUpgradeCount = dataCount;
			
			treeModel.nodeChanged(treeNodeModels.get(dataElementDatabase)); // update progressbar
			
		}else if( "start-indeterminate-progress".equals(""+oEvent) ){
			ElementDatabase dataElementDatabase = (ElementDatabase) oEvent.getArg();

			TreeNodeModel<?> progressingTreeNodeModel = 
					(TreeNodeModel<?>) treeNodeModels.get(dataElementDatabase).getUserObject();
			progressingTreeNodeModel.hasIndeterminatedProgress = true;
			
//			treeModel.nodeStructureChanged((TreeNode) treeNodeModels.get(dataElementDatabase)); // update progressbar
			treeModel.nodeChanged(treeNodeModels.get(dataElementDatabase)); // update progressbar
			
		}else if( "end-indeterminate-progress".equals(""+oEvent) ){
			ElementDatabase dataElementDatabase = (ElementDatabase) oEvent.getArg();

			TreeNodeModel<?> progressingTreeNodeModel = 
					(TreeNodeModel<?>) treeNodeModels.get(dataElementDatabase).getUserObject();
			progressingTreeNodeModel.hasIndeterminatedProgress = false;
			
			treeModel.nodeChanged((TreeNode) treeNodeModels.get(dataElementDatabase)); // update progressbar
			
		}else if( "start-progress".equals(""+oEvent) ){
			ElementDatabase dataElementDatabase = (ElementDatabase) oEvent.getArg();

			TreeNodeModel<?> progressingTreeNodeModel = 
					(TreeNodeModel<?>) treeNodeModels.get(dataElementDatabase).getUserObject();
			progressingTreeNodeModel.hasProgress = true;
			
//			treeModel.nodeStructureChanged((TreeNode) treeNodeModels.get(dataElementDatabase)); // update progressbar
			treeModel.nodeChanged(treeNodeModels.get(dataElementDatabase)); // update progressbar
			
		}else if( "end-progress".equals(""+oEvent) ){
			ElementDatabase dataElementDatabase = (ElementDatabase) oEvent.getArg();

			TreeNodeModel<?> progressingTreeNodeModel = 
					(TreeNodeModel<?>) treeNodeModels.get(dataElementDatabase).getUserObject();
			progressingTreeNodeModel.hasProgress = false;
			progressingTreeNodeModel.childUpgradeCount = 0;
			
			treeModel.nodeChanged((TreeNode) treeNodeModels.get(dataElementDatabase)); // update progressbar
			
		}else if( "add-info".equals(""+oEvent) ){
			labelDBVersion.setText( " "+model.getVersionDB() );
			labelCurrentDB.setText( " "+model.getCurrentDB() );
			labelCurrentUser.setText( " "+model.getCurrentUser() );
			labelAuthenticatedUser.setText( " "+model.getAuthenticatedUser() );
			
		}else if( "add-databases".equals(""+oEvent) ){
			List<?> newDatabases = (ArrayList<?>) oEvent.getArg();
			for(Object o: newDatabases){
				Database d = (Database) o;
				TreeNodeModel<Database> newTreeNodeModel = new TreeNodeModel<Database>(d);
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( newTreeNodeModel );
				treeNodeModels.put(d, newNode);
				root.add(newNode);
			}
			
			treeModel.reload(root); // partial nodes, forces reload
			databaseTree.expandPath( new TreePath(root.getPath()) ); // expands root
			
		}else if( "add-tables".equals(""+oEvent) ){
			List<?> newTables = (ArrayList<?>) oEvent.getArg();
			DefaultMutableTreeNode databaseNode = null;
			
			for(Object o: newTables){
				Table t = (Table) o;
				TreeNodeModel<Table> newTreeNodeModel = new TreeNodeModel<Table>(t);
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( newTreeNodeModel );
				treeNodeModels.put(t, newNode);
				
				databaseNode = treeNodeModels.get(t.getParent());
		        treeModel.insertNodeInto(newNode, databaseNode, databaseNode.getChildCount());
			}
			
			if(databaseNode != null)
				databaseTree.expandPath( new TreePath(databaseNode.getPath()) );

		}else if( "add-columns".equals(""+oEvent) ){
			List<?> newColumns = (List<?>) oEvent.getArg();
			DefaultMutableTreeNode tableNode = null;
			
			for(Object o: newColumns){
				Column c = (Column) o;
				TreeNodeModel<Column> newTreeNodeModel = new TreeNodeModel<Column>(c);
				newTreeNodeModel.hasCheckBox = true;
				
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( newTreeNodeModel );
				tableNode = treeNodeModels.get(c.getParent());
				
		        treeModel.insertNodeInto(newNode, tableNode, tableNode.getChildCount());
			}
			
			if(tableNode != null)
				databaseTree.expandPath( new TreePath(tableNode.getPath()) );

		}else if( "add-values".equals(""+oEvent) ){
			Object[] observerEventData = (Object[]) oEvent.getArg();
	        
			String[] columnNames = (String[]) observerEventData[0];
			String[][] data = (String[][]) observerEventData[1];
			ElementDatabase table = (ElementDatabase) observerEventData[2];
			
			TreeNodeModel<?> progressingTreeNodeModel = 
					(TreeNodeModel<?>) treeNodeModels.get(table).getUserObject();
			progressingTreeNodeModel.childUpgradeCount = table.getCount(); // ends progress

			JTablePane newTableJPanel = new JTablePane(data, columnNames, valuesTabbedPane);
			
			valuesTabbedPane.addTab(table.getParent()+"."+table+" "+
					"("+(columnNames.length-2)+" fields) ",newTableJPanel);
			valuesTabbedPane.setSelectedComponent(newTableJPanel);
			
			JTabbedPaneHeader header = new JTabbedPaneHeader(valuesTabbedPane);
			valuesTabbedPane.setTabComponentAt(valuesTabbedPane.indexOfComponent(newTableJPanel), header);
			
		}else if( "add-normal".equals(""+oEvent) ){
			labelNormal.setIcon(new ImageIcon(getClass().getResource("/gradeit_icon.png")));
			
		}else if( "add-errorbase".equals(""+oEvent) ){
			labelErrorBase.setIcon(new ImageIcon(getClass().getResource("/gradeit_icon.png")));
			
		}else if( "add-blind".equals(""+oEvent) ){
			labelBlind.setIcon(new ImageIcon(getClass().getResource("/gradeit_icon.png")));
			
		}		
	}
}
