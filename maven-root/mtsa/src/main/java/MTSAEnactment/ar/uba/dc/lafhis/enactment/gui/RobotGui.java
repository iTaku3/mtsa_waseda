package MTSAEnactment.ar.uba.dc.lafhis.enactment.gui;


import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.*;

import org.jfree.ui.action.ActionConcentrator;
import org.jfree.ui.tabbedui.VerticalLayout;



import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.TakeFirstController;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.ITransitionEventListener;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.TransitionDispatcher;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.TransitionEvent;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.NXTRobot;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.nxt.NXTCommException;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.nxt.NXTRobotComm;


import lejos.nxt.ColorSensor.Color;


public class RobotGui extends JFrame implements ActionListener {

	Color lastColor;
	
    NXTRobot<String, String> nxtRobot;
	NXTRobotComm nxtRobotComm;
	
    JButton buttonFollow = new JButton("Follow");
    JButton buttonTurnBack = new JButton("Turn Back");
    JButton buttonTurnLeft = new JButton("Turn Left");
    JButton buttonTurnRight = new JButton("Turn Right");
    JButton buttonCalibrate = new JButton("Calibrate");
    JButton buttonReadColor = new JButton("Read Color");    
    JButton buttonLts = new JButton("Ejecutar LTS");
    JTextArea nocontrolableTextArea = new JTextArea(10, 20);
    JTextField txtTolerancia = new JTextField();
    JTextField txtToleranciaRGB = new JTextField();
    JButton buttonSetupVars = new JButton("Setup Vars");
    
	String actionFollow = "follow";
	String actionTurnLeft = "turnLeft";
	String actionTurnRight = "turnRight";
	String actionTurnBack = "turnArround";
	
	String actionSuccess = "Success";
	String actionFailure = "Failure";
	String actionLost = "Lost";
	String actionCalibrar = "Calibrar";
	String actionReadColor = "ReadColor";
	
	
	LTSAdapter<Long, String> ltsAdapter;
	TakeFirstController<Long, String> controllerScheduler;
	
	ControllerCircularView<Long, String> controllerCircularView;
	
    public RobotGui() {
        super("Robot GUI");
        setSize(500, 550);
        
        JLabel etiquetaLTS = new JLabel("LTS: ", JLabel.RIGHT);
        JTextArea lts = new JTextArea(10, 20);
        lts.setText("FOLLOW = (follow -> (success -> TURN | lost -> LOST)), TURN = (turnLeft -> (success -> FOLLOW | lost -> LOST)).");
        JScrollPane scrollPane = new JScrollPane(lts);
        
      //FlowLayout dis = new FlowLayout();
        setLayout(new VerticalLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        

        JPanel panelLTS = new JPanel();        
        panelLTS.add(etiquetaLTS);
        panelLTS.add(scrollPane);
        buttonLts.addActionListener(this);
        panelLTS.add(buttonLts);
        
        add(panelLTS);

        add(new JLabel("Controlables: ", JLabel.LEFT));
        
        JPanel panel = new JPanel();
        GridLayout miembros = new GridLayout(2, 2, 5, 5);        
        panel.setLayout(miembros);

        buttonFollow.addActionListener(this);
        buttonTurnBack.addActionListener(this);
        buttonTurnLeft.addActionListener(this);
        buttonTurnRight.addActionListener(this);
        buttonTurnRight.setBounds(100, 100, 100, 100);
        buttonCalibrate.addActionListener(this);
        buttonReadColor.addActionListener(this);
        panel.add(buttonFollow);
        panel.add(buttonTurnBack);
        panel.add(buttonTurnLeft);
        panel.add(buttonTurnRight);

        add(panel);
        

        JPanel logPanel = new JPanel();
        logPanel.add(new JLabel("No controlables:", JLabel.LEFT));        
        logPanel.add(nocontrolableTextArea);
        logPanel.setLayout(new GridLayout(1, 2, 5, 5));
        add(logPanel);

        
        add(new JLabel("Setup Robot and testing: ", JLabel.LEFT));
        
        JPanel panelMaint = new JPanel();
        panelMaint.setLayout(new GridLayout(1, 2, 5, 5));
        panelMaint.add(buttonCalibrate);
        panelMaint.add(buttonReadColor);   
        panelMaint.add(new JLabel("Tolerancia:"));
        txtTolerancia.setText("80");
        panelMaint.add(txtTolerancia);
        panelMaint.add(new JLabel("Tolerancia RGB:"));
        txtToleranciaRGB.setText("40");
        panelMaint.add(txtToleranciaRGB);
        buttonSetupVars.addActionListener(this);
        panelMaint.add(buttonSetupVars);
        add(panelMaint);

        
        setVisible(true);
    }

    public static void main(String[] args) {
        RobotGui gui = new RobotGui();

    }

    
    /**
     * Calcula la distancia entre dos colores
     * Funcion temporal de prueba. debe eliminarse de la GUI
     * @param e1
     * @param e2
     * @return
     */
    private double colorDistance(Color e1, Color e2)
	{
	    long rmean = ( (long)e1.getRed() + (long)e2.getRed() ) / 2;
	    long r = (long)e1.getRed() - (long)e2.getRed();
	    long g = (long)e1.getGreen() - (long)e2.getGreen();
	    long b = (long)e1.getBlue() - (long)e2.getBlue();
	    return Math.sqrt((((512+rmean)*r*r)>>8) + 4*g*g + (((767-rmean)*b*b)>>8));
	}
	private String colorToString(Color color)
	{
		return "(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ")";
	}
	
	private void openLTS()
	{
		try {
//			ltsAdapter	= new LTSAdapter<Long, String>(
//					MTSCompiler.getInstance().compileMTS("controller", "FOLLOW = (follow -> (success -> TURN | lost -> LOST)), TURN = (turnLeft -> (success -> FOLLOW | lost -> LOST)).")
//					, TransitionType.REQUIRED);
//
//			HashSet<String> controllableActions = new HashSet<String>();
//			controllableActions.add(actionFollow);
//			controllableActions.add(actionTurnLeft);
//			controllableActions.add(actionTurnRight);
//			controllableActions.add(actionTurnBack);
//			
//			controllerScheduler	= new ControllerScheduler<Long, String>("controller scheduler"
//				, ltsAdapter, controllableActions);
//
//	    	nxtRobotComm = new NXTRobotComm();
//			nxtRobot = new NXTRobot<String, String>("Robot1", nxtRobotComm, actionSuccess, actionFailure, actionLost, actionFollow, actionTurnLeft, actionTurnRight, actionTurnBack, actionCalibrar);
//			//TODO: Temporal calibrar color action
//			nxtRobot.readColor = actionReadColor;
//			nxtRobot.addTransitionEventListener(new ITransitionEventListener<String>() {
//				
//				@Override
//				public void handleTransitionEvent(TransitionEvent<String> transitionEvent)
//						throws Exception {
//					System.out.println("Event received - " + transitionEvent.getAction());
//					nocontrolableTextArea.append("No controlable received: " + transitionEvent.getAction() + "\n");
//					
//					
//				}
//			});
//					
//
//			controllerScheduler.addTransitionDispatcher(nxtRobot);
//			controllerScheduler.addTransitionEventListener(nxtRobot);
//			
//			controllerCircularView = new ControllerCircularView<Long, String>();
//			controllerCircularView.initialize(ltsAdapter, 0L, controllerScheduler);
			
			nocontrolableTextArea.append("Operation not implemented\n");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

    @Override
    public void actionPerformed(ActionEvent evt) {
        
    	try
        {
        	
	        
	    	Object fuente = evt.getSource();
	        


	    	String enactmentCommand = null;;
	    	
			
	        if (fuente == buttonFollow) {
//	            System.out.println("Evento Follow");
	            enactmentCommand = actionFollow;
	        } else if (fuente == buttonTurnBack) {
//	            System.out.println("Evento Turn Back");
	            enactmentCommand = actionTurnBack;
	        } else if (fuente == buttonTurnLeft) {
//	            System.out.println("Evento Turn Left");
	            enactmentCommand = actionTurnLeft;
	        } else if (fuente == buttonTurnRight) {
//	            System.out.println("Evento Turn Right");
	            enactmentCommand = actionTurnRight;
	        } else if (fuente == buttonCalibrate) {
//	        	System.out.println("Evento Calibrate");
	        	enactmentCommand = actionCalibrar;
	        } else if (fuente == buttonReadColor) {
//	        	System.out.println("Evento Read Color");
	        	enactmentCommand = actionReadColor;
	        } else if (fuente == buttonLts) {
//	        	System.out.println("Evento Open LTS");
	        	
	        	openLTS();
	        } else if (fuente == buttonSetupVars) {
//	        	System.out.println("Evento Setup Vars");
	        	
	        	int tolerancia = Integer.parseInt(txtTolerancia.getText());
	        	double toleranciaRGB = Double.parseDouble(txtToleranciaRGB.getText());
	        	if (nxtRobotComm != null)
	        	{
	        		nxtRobotComm.setupVars(tolerancia, toleranciaRGB);
	        		
	        	}
	        }
	        if (enactmentCommand != null)
	        {
	        	if (nxtRobotComm == null)
	        	{
	        		
	
			    	nxtRobotComm = new NXTRobotComm();
					nxtRobot = new NXTRobot<String, String>("Robot1", nxtRobotComm, actionSuccess, actionFailure, actionLost, actionFollow, actionTurnLeft, actionTurnRight, actionTurnBack, actionCalibrar);
					//TODO: Temporal calibrar color action
					//nxtRobot.readColor = actionReadColor;
					nxtRobot.addTransitionEventListener(new ITransitionEventListener<String>() {
						
						@Override
						public void handleTransitionEvent(TransitionEvent<String> transitionEvent)
								throws Exception {
//							System.out.println("Event received - " + transitionEvent.getAction());
							nocontrolableTextArea.append("No controlable received: " + transitionEvent.getAction() + "\n");
							
							
						}
					});
							
					
					
	        	}
	        	
	        	//TODO: Revisar impacto remocion executeAction. Es necesario crear un TransitionEvent?
	        	//nxtRobot.executeAction(enactmentCommand);
	        }

        } catch (Exception e)
        {
        	e.printStackTrace();
        }

    	
    	repaint();
        
    }
    
}
