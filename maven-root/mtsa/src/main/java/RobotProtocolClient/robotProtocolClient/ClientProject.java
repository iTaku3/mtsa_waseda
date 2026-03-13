package RobotProtocolClient.robotProtocolClient;

import java.io.*;
import java.util.*;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import ltsa.ui.LTSABatch;

import MTSAClient.ac.ic.doc.mtsa.MTSCompiler;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.TakeFirstController;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.ITransitionEventListener;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.TransitionEvent;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.gui.ControllerCircularView;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6Robot;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.NXTRobot;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.IMessageListener;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.Message;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.MessageEvent;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.RobotProtocolSession;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.SetMotorSpeedMessage;


import jssc.SerialPort;
import jssc.SerialPortException;

public class ClientProject extends JFrame implements IMessageListener
	, ITransitionEventListener<String>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3188159298528152001L;
	protected static Thread updateThread;
	protected static ClientProject instance;
	protected Set<String> failureSet;
	protected List<RobotRun<String>> runs;
	protected RobotRun<String> currentRun;
	
    protected JPanel mainPanel;
    protected JPanel ltsPanel;
    protected JPanel inputLtsPanel;
    protected JPanel inputLtsButtonsPanel;
    protected JScrollPane inputLtsPane;
    protected JTextPane inputLtsTextPane;
    protected JButton inputLtsAcceptButton;
    protected JButton inputLtsStopButton;
    protected JButton saveRunsButton;
    protected JScrollPane ltsOutputPane;
    protected JTextPane ltsOutputTextPane;
    
    protected StyledDocument inputLtsDocument;
    protected StyledDocument ltsOutputDocument;
    
    protected N6Robot<Long, String> n6;
    protected ITransitionEventListener<String> thisListener;
    
    protected ControllerCircularView<Long, String> ltsView;
    
    protected RobotProtocolSession session;
    
    TakeFirstController<Long, String> controllerScheduler;
    
    public ClientProject() {
         initGui();       
         
         //initSerial("/dev/ttyACM0");
         thisListener	= this;
         
         runs		= new ArrayList<RobotRun<String>>();
         
         failureSet	= new HashSet<String>();
         failureSet.add("failure");
         failureSet.add("lost");
         
    }        

    public void start(){
        while(true){ 
       	 //update();
       	 try {
				Thread.sleep(33);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }    	
    }
    
    protected void initGui()
    {
		mainPanel				= new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		StyleContext context 	= new StyleContext();
		Style style 			= context.getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
		StyleConstants.setFontSize(style, 10);
		StyleConstants.setSpaceAbove(style, 4);
		StyleConstants.setSpaceBelow(style, 4);
		
		inputLtsDocument		= new DefaultStyledDocument(context);
		ltsOutputDocument		= new DefaultStyledDocument(context);
		
		inputLtsPanel			= new JPanel();
		inputLtsPanel.setLayout(new BoxLayout(inputLtsPanel, BoxLayout.Y_AXIS));
		inputLtsButtonsPanel	= new JPanel();
		inputLtsButtonsPanel.setLayout(new BoxLayout(inputLtsButtonsPanel, BoxLayout.X_AXIS));
		
		TitledBorder ltsBorder	= new TitledBorder("Input LTS");
		inputLtsTextPane		= new JTextPane(inputLtsDocument);
		try {
			inputLtsDocument.insertString(0,
					//"BACKANDFORTH=(follow->success->turnLeft->success->turnLeft->success->BACKANDFORTH)."
					//"FOLLOW=(follow->success->FOLLOW)."
					"SQUARELEFT=(follow->success->turnLeft->success->SQUARELEFT)."
					//"SQUARERIGHT=(follow->success->turnRight->success->SQUARERIGHT)."
					//"TURN=(turnLeft->success->TURN)."
					//"USSENSOR = (readUSSensor -> (readUSSensorReply -> USSENSOR | lost -> LOST))."
					//"TEST=(follow -> success -> follow -> success -> turnRight -> success -> follow -> success -> follow -> success ->  follow -> success -> turnLeft -> success -> follow -> success -> turnLeft -> success -> follow -> success -> turnLeft -> success -> follow -> success -> TEST)."
					//"LOOP = (follow -> (success -> (turnLeft -> (success -> (follow -> (success -> (turnLeft -> (success -> (follow -> (success -> (turnLeft -> (success -> (follow -> (success -> (follow -> (success -> (turnRight -> (success -> (follow -> (success -> (turnRight -> (success -> (follow -> (success -> (turnRight -> (success -> (follow -> (success -> LOOP | lost -> LOST)) | lost -> LOST)) | lost -> LOST)) | lost -> LOST)) | lost -> LOST)) | lost -> LOST)) | lost -> LOST)) | lost -> LOST)) | lost -> LOST)) | lost -> LOST)) | lost -> LOST)) | lost -> LOST)) | lost -> LOST)) | lost -> LOST))."
					//"READ = (readSensors -> (lost -> LOST | readSensorsReply -> READ))."
					//"FOLLOW = (follow -> (success -> TURN | lost -> LOST)), TURN = (turnLeft -> (success -> FOLLOW | lost -> LOST))."
					
					/*"FOLLOW = (follow -> (success -> TURNL1 | lost -> LOST)), TURNL1 = (turnLeft -> (success -> TURNL2 | lost -> LOST))"+
						", TURNL2 = (turnLeft -> (success -> FOLLOW2 | lost -> LOST)) "+
						", FOLLOW2 = (follow -> (success -> TURNR1 | lost -> LOST)), TURNR1 = (turnRight -> (success -> TURNR2 | lost -> LOST))"+
						", TURNR2 = (turnRight -> (success -> FOLLOW | lost -> LOST))."
						*/
					//"CONTROL = (follow -> Q1), Q1 = (success -> CONTROL | failure -> FAIL | lost -> LOST))."
					, null);
		} catch (BadLocationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		inputLtsTextPane.setToolTipText("Input LTS text");
		inputLtsTextPane.setBorder(ltsBorder);
		inputLtsTextPane.setEditable(true);
		inputLtsPane			= new JScrollPane(inputLtsTextPane);

		inputLtsAcceptButton	= new JButton("Accept LTS");
		inputLtsAcceptButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
		    	openConnection();
			}
		});
		inputLtsStopButton			= new JButton("Stop enactment");
		inputLtsStopButton.setEnabled(false);
		inputLtsStopButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {	
		    	closeConnection();
			}
		});		
		saveRunsButton			= new JButton("Save enacted traces");
		saveRunsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {	
				saveToFiles();
			}
		});	
		
		
		inputLtsAcceptButton.setPreferredSize(new Dimension(40,40));
		
		inputLtsPanel.add(inputLtsTextPane);
		inputLtsButtonsPanel.add(inputLtsAcceptButton);
		inputLtsButtonsPanel.add(inputLtsStopButton);
		inputLtsButtonsPanel.add(saveRunsButton);
		inputLtsPanel.add(inputLtsButtonsPanel);
		
		TitledBorder ltsOutBorder	= new TitledBorder("LTS trace");
		ltsOutputTextPane		= new JTextPane(ltsOutputDocument);
		ltsOutputTextPane.setToolTipText("LTS trace");
		ltsOutputTextPane.setBorder(ltsOutBorder);
		ltsOutputTextPane.setEditable(false);
		ltsOutputPane			= new JScrollPane(ltsOutputTextPane);		
		
		   
		
		ltsView					= new ControllerCircularView<Long, String>();
		ltsView.setPreferredSize(new Dimension(300, 300));
		
		mainPanel.add(ltsView);
		mainPanel.add(inputLtsPanel);
		mainPanel.add(ltsOutputPane);
		
		add(mainPanel);

		setTitle("Robot Protocol Client");
		setSize(800, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		setVisible(true);
		invalidate();
		repaint();

    }
   
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
            	instance = new ClientProject();
            	instance.setVisible(true);
            }
        });
	}
	
	private void openConnection(){
        
        session	= new RobotProtocolSession(1, 4, "/dev/ttyACM0", "/dev/ttyACM1", "/dev/ttyACM2");
        try {
    		currentRun							= new RobotRun<String>();
            Thread.sleep(1000);
            session.open();
            Thread.sleep(1000);
    		for(String failure :failureSet){
    			currentRun.addFailureAction(failure);
    		}
    		runs.add(currentRun);
	    	inputLtsAcceptButton.setEnabled(false);

    		LTSAdapter<Long, String> ltsAdapter	= new LTSAdapter<Long, String>(
					MTSCompiler.getInstance().compileMTS("controller", inputLtsTextPane.getText())
					, TransitionType.REQUIRED);
			initializeScheduler(ltsAdapter);
			ltsView.initialize(ltsAdapter, ltsAdapter.getInitialState(), controllerScheduler);
	    	inputLtsStopButton.setEnabled(true);					
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}        
	}
	
	private void closeConnection(){
		try {
	    	inputLtsStopButton.setEnabled(false);
			//controllerScheduler.removeTransitionDispatcher(n6);	    	
			controllerScheduler.removeTransitionEventListener(n6);
			controllerScheduler.removeTransitionEventListener(thisListener);			
			n6.removeTransitionEventListener(controllerScheduler);
			session.close();
			Thread.sleep(1000);
	    	inputLtsAcceptButton.setEnabled(true);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
	}
	
	public void saveToFiles(){
    	try {
    		List<Integer> firstFailure		= new ArrayList<Integer>();
    		JFileChooser chooser			= new JFileChooser();
    		chooser.setCurrentDirectory(new java.io.File("."));
    		chooser.setDialogType(JFileChooser.DIRECTORIES_ONLY);
    		
    		chooser.setDialogTitle("Save traces");
    		chooser.setAcceptAllFileFilterUsed(false);		    		
    		int option 						= chooser.showSaveDialog(mainPanel);  
    			if(option == JFileChooser.APPROVE_OPTION){  
    				if(chooser.getSelectedFile()!=null){  
	    			File f					= chooser.getCurrentDirectory();
	    			if(f.isDirectory()){
	    				int runIndex		= 0;
	    				for(RobotRun<String> run : runs){
	    					firstFailure.add(run.getMinTraceToFailure());
	    					run.saveFilesToLocation(f.getAbsolutePath(), chooser.getSelectedFile().getName() + runIndex++);
	    				}
	    				File resumeFile		= new File(f.getAbsolutePath() + "/resume.run");
    					//if(!resumeFile.exists())
	    				resumeFile.createNewFile();
    					PrintWriter out		= new PrintWriter(resumeFile);
    					String resumeText	= "MEAN TRANSITION TO FAILURE\tSAMPLE SIZE\n";
    					int totalActions	= 0;
    					for(Integer transitionToFailure : firstFailure){
    						if(transitionToFailure >= 0)
    							totalActions	+= transitionToFailure;
    					}
    					resumeText			+= (totalActions/firstFailure.size())+ "\t" + firstFailure.size() + "\n";
    					out.print(resumeText);
    					out.close();
	    				
	    			}
    				
    			}
    		}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
	}

	@Override
	public void eventHandler(MessageEvent messageEvent) {
		
		try {
			ltsOutputDocument.insertString(ltsOutputDocument.getLength(), ((Message)messageEvent.getSource()).getPackedMessage()
					+ "\n"
					, new SimpleAttributeSet());
			ltsOutputPane.invalidate();
			ltsOutputPane.repaint();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	protected void initializeScheduler(LTSAdapter<Long, String> ltsAdapter){
		HashSet<String> controllableActions = new HashSet<String>();
		controllableActions.add("follow");
		controllableActions.add("turnLeft");
		controllableActions.add("turnRight");
		controllableActions.add("turnAround");
		controllableActions.add("readSensors");
		controllableActions.add("readUSSensor");
		controllerScheduler	= new TakeFirstController<Long, String>("controller scheduler"
				, ltsAdapter, controllableActions);
		n6	= new N6Robot<Long, String>("n6"
				, "success", "failure","lost"
				,"follow", "turnLeft", "turnRight","turnAround"
				,"readSensors", "readSensorsReply", "readUSSensor", "readUSSensorReply"
				, "retry", "successRetry", session);
		try {			
			//controllerScheduler.addTransitionDispatcher(n6);
			
			controllerScheduler.addTransitionEventListener(n6);
			controllerScheduler.addTransitionEventListener(this);
			n6.addTransitionEventListener(controllerScheduler);
			controllerScheduler.takeNextAction();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void handleTransitionEvent(TransitionEvent<String> transitionEvent)
			throws Exception {
		currentRun.addAction(transitionEvent.getAction());
		ltsOutputDocument.insertString(ltsOutputDocument.getLength(), transitionEvent.getAction() + "\n"
				, null);
				ltsOutputPane.invalidate();
				ltsOutputPane.repaint();	
		
	}
}
