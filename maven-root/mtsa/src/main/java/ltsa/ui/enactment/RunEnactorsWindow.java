/**
 * 
 */
package ltsa.ui.enactment;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.BaseController;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.gui.GraphVisualizer;
import ltsa.updatingControllers.synthesis.UpdatingControllersAnimatorUtils;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * @author Julio
 *
 */
/**
 * @author Julio
 *
 * @param <State>
 * @param <Action>
 */
public class RunEnactorsWindow<State, Action> extends JFrame  {

	
    protected JPanel mainPanel;
    protected JScrollPane ltsOutputPane;
    protected JTextArea ltsOutputTextPane;

    protected GraphVisualizer<State, Action> ltsView;
    private JSplitPane splitPane;
    
    private JPanel panel;
    private JButton pauseButton;
    private JButton stopButton;
    
    
    
	public RunEnactorsWindow(BaseController<State, Action> controllerScheduler)
	{
		
		mainPanel = new JPanel();
		
		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setMinimumSize(new Dimension(10, 20));
		FlowLayout fl_panel = new FlowLayout(FlowLayout.LEADING, 5, 5);
		panel.setLayout(fl_panel);
		
		pauseButton = new JButton("Pause Controller Simulation");
		panel.add(pauseButton);
		
		stopButton = new JButton("Stop & Close");
		panel.add(stopButton);

         
        JButton btZoomIn = new JButton("Zoom in");
        btZoomIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
            	ltsView.zoomIn();
            }
        });
        panel.add(btZoomIn);
        JButton btZoomOut = new JButton("Zoom out");
        btZoomOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
            	ltsView.zoomOut();
            }
        });
        panel.add(btZoomOut);


        // time input
        SpinnerModel model = new SpinnerNumberModel(3,0,10,1);
        UpdatingControllersAnimatorUtils.controllerSpeed = new JSpinner(model);

        JLabel speedLabel = new JLabel("Controller Speed:");

        panel.add(speedLabel);
        panel.add(UpdatingControllersAnimatorUtils.controllerSpeed);
        //


        // splitting left and right panels
		splitPane = new JSplitPane();
		splitPane.setAlignmentY(Component.CENTER_ALIGNMENT);
		splitPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		splitPane.setResizeWeight(0.7);
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

		TitledBorder ltsOutBorder	= new TitledBorder("LTS trace");
        //
		
		ltsView					= new GraphVisualizer<State, Action>();

        // splitting left panel in top and bottom
        UpdatingControllersAnimatorUtils.actualControllableActionsPanel.setLayout(new BoxLayout(UpdatingControllersAnimatorUtils.actualControllableActionsPanel, BoxLayout.Y_AXIS));

        UpdatingControllersAnimatorUtils.actualControllableActionsPanel.setBackground(Color.LIGHT_GRAY);
        JScrollPane scrollPane = new JScrollPane(UpdatingControllersAnimatorUtils.actualControllableActionsPanel);

        JSplitPane splitPane_1 = new JSplitPane();
        splitPane_1.setResizeWeight(0.4);
        splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);

        splitPane_1.setLeftComponent(ltsView);
        splitPane_1.setRightComponent(scrollPane);
        splitPane_1.setBorder(BorderFactory.createEmptyBorder(0,5,5,0));
        //

		splitPane.setLeftComponent(splitPane_1);
		ltsView.setPreferredSize(new Dimension(300, 300));
		ltsOutputTextPane		= new JTextArea();
		ltsOutputTextPane.setToolTipText("LTS trace");
		ltsOutputTextPane.setBorder(ltsOutBorder);
		ltsOutputTextPane.setEditable(false);
		ltsOutputPane			= new JScrollPane(ltsOutputTextPane);
		ltsOutputPane.setMinimumSize(new Dimension(23, 150));
		splitPane.setRightComponent(ltsOutputPane);
		
				
		//Setup log output textPane
		LoggerAppender.setUiOutput(ltsOutputTextPane);
		
		ltsView.initialize(controllerScheduler.getLts(), controllerScheduler.getLts().getInitialState(), controllerScheduler);
		

		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));
		mainPanel.add(panel, BorderLayout.NORTH);
		mainPanel.add(splitPane, BorderLayout.CENTER);
		setTitle("Controller Simulation");
		setSize(1200, 600);
		setLocationRelativeTo(null);

		
		invalidate();
		repaint();
	    
		
		
	}

	/**
	 * @return the pauseButton
	 */
	public JButton getPauseButton() {
		return pauseButton;
	}



	/**
	 * @return the stopButton
	 */
	public JButton getStopButton() {
		return stopButton;
	}
	
	public void zoomToFit()
	{
		ltsView.zoomToFixViewPort();
	}

}
