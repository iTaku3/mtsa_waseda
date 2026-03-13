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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.nxt.NXTCommException;


import jssc.SerialPort;
import jssc.SerialPortException;

public class NXTClientProject extends JFrame implements IMessageListener
	, ITransitionEventListener<String>{
	protected static Thread updateThread;
	protected static NXTClientProject instance;
	
	//protected SerialPort serial;
	
    protected JPanel mainPanel;
    protected JPanel ltsPanel;
    protected JPanel inputLtsPanel;
    protected JScrollPane inputLtsPane;
    protected JTextPane inputLtsTextPane;
    protected JButton inputLtsAcceptButton;
    protected JScrollPane ltsOutputPane;
    protected JTextPane ltsOutputTextPane;
    protected JTextField txtTolerancia = new JTextField();
    protected JTextField txtToleranciaRGB = new JTextField();
    protected JButton buttonSetupVars = new JButton("Setup Vars");
    
    protected StyledDocument inputLtsDocument;
    protected StyledDocument ltsOutputDocument;
    
    
    
    protected ControllerCircularView<Long, String> ltsView;
    protected NXTRobot<Long, String> nxt;
    
    TakeFirstController<Long, String> controllerScheduler;
    
    //TODO: Address bt NXT - temporal
    //private final String bt_address = "001653151dd0";
    private final String bt_address = "0016531b6519";
    
    public NXTClientProject() {
         initGui();       
         
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
		
		TitledBorder ltsBorder	= new TitledBorder("Input LTS");
		inputLtsTextPane		= new JTextPane(inputLtsDocument);
		try {
			inputLtsDocument.insertString(0,
					"FOLLOW = (follow -> (success -> TURN | lost -> LOST)), TURN = (turnLeft -> (success -> FOLLOW | lost -> LOST))."
					//"START = (calibrate -> success -> FOLLOW ), FOLLOW = (follow -> (success -> TURN | lost -> LOST)), TURN = (turnLeft -> (success -> FOLLOW | lost -> LOST))."
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
		    	inputLtsAcceptButton.setEnabled(false);	
		    	try {
					LTSAdapter<Long, String> ltsAdapter	= new LTSAdapter<Long, String>(
							MTSCompiler.getInstance().compileMTS("controller", inputLtsTextPane.getText())
							, TransitionType.REQUIRED);
					initializeScheduler(ltsAdapter);
					ltsView.initialize(ltsAdapter, ltsAdapter.getInitialState(), controllerScheduler);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		inputLtsAcceptButton.setPreferredSize(new Dimension(40,40));
		
		inputLtsPanel.add(inputLtsTextPane);
		inputLtsPanel.add(inputLtsAcceptButton);
		
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
		
		JPanel toleranciaPanel = new JPanel();
		toleranciaPanel.add(new JLabel("Tolerancia:"));
	        txtTolerancia.setText("80");
	        toleranciaPanel.add(txtTolerancia);
	        toleranciaPanel.add(new JLabel("Tolerancia RGB:"));
	        txtToleranciaRGB.setText("40");
	        toleranciaPanel.add(txtToleranciaRGB);
	        buttonSetupVars.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int tolerancia = Integer.parseInt(txtTolerancia.getText());
		        	double toleranciaRGB = Double.parseDouble(txtToleranciaRGB.getText());
		        	setupAdapter();		
		        	if (nxt != null && nxt.getComm() != null)
		        	{
		        		try {
							nxt.getComm().setupVars(tolerancia, toleranciaRGB);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NXTCommException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		        		
		        	}
					
				}
			});
	        toleranciaPanel.add(buttonSetupVars);
	        JButton btCalibrar = new JButton("Calibrar");
	        btCalibrar.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					setupAdapter();		
					if (nxt != null && nxt.getComm() != null)
					{
						try {
							nxt.getComm().calibrar();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NXTCommException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
						
					
				}
			});
	        
	        //mainPanel.add(btCalibrar);
		add(mainPanel);
		//mainPanel.add(toleranciaPanel);
		
		setTitle("Robot Protocol Client - " + bt_address);
		setSize(800, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//addKeyListener(this);
		
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
            	instance = new NXTClientProject();
            	instance.setVisible(true);
            }
        });
	}

	@Override
	public void eventHandler(MessageEvent messageEvent) {
		try {
			inputLtsDocument.insertString(inputLtsDocument.getLength(), ((Message)messageEvent.getSource()).getPackedMessage()
					+ "\n"
					, new SimpleAttributeSet());
			inputLtsPanel.invalidate();
			inputLtsPanel.repaint();
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
		controllableActions.add("calibrate");
		controllerScheduler	= new TakeFirstController<Long, String>("controller scheduler"
				, ltsAdapter, controllableActions);
		
		
		try {
			setupAdapter();		
			
			
			//controllerScheduler.addTransitionDispatcher(nxt);
			nxt.addTransitionEventListener(controllerScheduler);
			controllerScheduler.addTransitionEventListener(nxt);
			
			//Calibro el robot
			//nxt.getComm().calibrar();

			controllerScheduler.takeNextAction();
		} catch (NXTCommException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void handleTransitionEvent(TransitionEvent<String> transitionEvent)
			throws Exception {
		ltsOutputDocument.insertString(ltsOutputDocument.getLength(), transitionEvent.getAction() + "\n"
				, null);
				ltsOutputPane.invalidate();
				ltsOutputPane.repaint();		
	}
	
	private void setupAdapter() 
	{
		if (nxt == null)
		{
			try {
				nxt = new NXTRobot<Long, String>("nxt"
						, "success", "error","lost"
						,"follow", "turnLeft", "turnright","turnAround", "calibrate", bt_address, "");
			} catch (NXTCommException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
}
