package MTSAEnactment.ar.uba.dc.lafhis.enactment.gui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JPanel;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.ITransitionEventListener;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.TransitionDispatcher;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.TransitionEvent;

public class ControllerCircularView<State, Action> extends JPanel 
	implements ITransitionEventListener<Action>, Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID 	= 6219261092721733809L;
	
	public static Color LINE_COLOR				= new Color(0x333333, false);
	public static Color FILL_COLOR				= new Color(0xeeeeee, false);
	public static Color ACTIVE_COLOR			= Color.RED;
	public static Color INITIAL_COLOR			= Color.BLUE;
	public static Color TEXT_COLOR				= new Color(0x000066, false);
	public static Color STATE_FILL_COLOR		= new Color(0xcccccc, false);
	
	public static Color INCOMING_ACTION_COLOR	= Color.DARK_GRAY;
	public static Color OUTGOING_ACTION_COLOR	= Color.RED;
	
	
	
	public static int UPDATE_GAP				= 33;
	
	public static int ARROW_LENGTH				= 6;
	public static float ARROW_WIDTH				= .3f;
	public static int ARROW_TAIL				= 8;
	
	public static int OUTER_MARGIN				= 2;
	public static double LTS_RADIUS_PRCNT		= .8f;
	public static int STATE_RADIUS				= 15;
	public static int INITIAL_STATE_RADIUS		= 25;
	public static int ACTIVE_STATE_RADIUS		= 20;
	public static double ACTIVE_STATE_JITTER	= .4f;
	
	public static double ANIMATION_STEP			= .03f;
	
	protected LTS<State, Action> lts;
	protected State initialState;
	protected State currentState;
	protected State previousState;
	protected TransitionDispatcher<Action> transitionDispatcher;
	
	protected double animationIndex;

	public ControllerCircularView(){
		animationIndex				= 0;		
		Thread	updateThread		= new Thread(this);
		updateThread.start();		
	}
	
	public synchronized void initialize(LTS<State, Action> lts, State initialState, TransitionDispatcher<Action> transitionDispatcher){
		if(this.transitionDispatcher != null)
			this.transitionDispatcher.removeTransitionEventListener(this);
		this.lts					= lts;
		this.initialState			= initialState;
		this.currentState			= initialState;
		this.previousState			= initialState;
		this.transitionDispatcher	= transitionDispatcher;
		this.transitionDispatcher.addTransitionEventListener(this);
		animationIndex				= 0;		
	}

	@Override
	public void handleTransitionEvent(TransitionEvent<Action> transitionEvent)
			throws Exception {
//		//System.out.println("taking action " + transitionEvent.getAction() + " from " + transitionEvent.getSource());
		Iterator<State> iterator	= lts.getTransitions(currentState).getImage(transitionEvent.getAction()).iterator();
		if(iterator.hasNext()){
			previousState	= currentState;
			currentState	= iterator.next();
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawContainer(g);
		drawLts(g);
		
	}
	
	protected synchronized void drawContainer(Graphics g){
		g.setColor(FILL_COLOR);
		g.fillRect(OUTER_MARGIN, OUTER_MARGIN, getWidth() - OUTER_MARGIN, getHeight() - OUTER_MARGIN);		
	}
	
	protected synchronized void drawLts(Graphics g){
		if(lts == null)
			return;
		Set<State> states	= lts.getStates();
		int stateIndex		= 0;
		for(State state : states){
			Color c		= getStateColor(stateIndex);
			int radius	= getStateRadius(stateIndex);
			int x		= (int)getStatePosition(stateIndex)[0];
			int y		= (int)getStatePosition(stateIndex)[1];
			
			g.setColor(STATE_FILL_COLOR);
			g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
			g.setColor(c);
			g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
			g.setColor(TEXT_COLOR);
			String stateText	= "s" + stateIndex;
			FontMetrics fm 		= g.getFontMetrics(g.getFont());
			int textWidth		= fm.stringWidth(stateText);
			int textHeight		= fm.getHeight();
			g.drawString(stateText, (int)(x - textWidth / 2), (int)(y + textHeight / 2));
			stateIndex++;
			Iterator<Pair<Action, State>> actionIterator	= lts.getTransitions(state).iterator();
			while(actionIterator.hasNext()){
				Pair<Action, State> actionToDraw	= actionIterator.next();
				State stateToArrive					= actionToDraw.getSecond();
				int stateToArriveIndex				= getStateIndex(stateToArrive);
				int stateToArriveRadius				= getStateRadius(stateToArriveIndex);
				Color actionColor					= LINE_COLOR;
				if(actionToDraw.getSecond().equals(currentState)){
					actionColor						= INCOMING_ACTION_COLOR;
				}else if(state.equals(currentState)){
					actionColor						= OUTGOING_ACTION_COLOR;
				}
				int aX			= x;
				int aY			= y;
				int bX			= (int)getStatePosition(stateToArriveIndex)[0];
				int bY			= (int)getStatePosition(stateToArriveIndex)[1];
				
				int dX			= bX - aX;
				int dY			= bY - aY;
				
				double theta	= Math.atan2(dY, dX);
				
				int textX		= bX - (dX / 2);
				int textY		= bY - (dY / 2);
				//textY			-= (int)(Math.sin(Math.PI / 2) * textHeight);				
				
				aX				= aX + (int)(Math.cos(theta) * radius);
				aY				= aY + (int)(Math.sin(theta) * radius);
				bX				= bX - (int)(Math.cos(theta) * stateToArriveRadius);
				bY				= bY - (int)(Math.sin(theta) * stateToArriveRadius);		
				

				stateText		= actionToDraw.getFirst().toString();
				textWidth		= fm.stringWidth(stateText);
					
				
				Graphics2D g2	= (Graphics2D)g;
				g2.translate(textX, textY);
				g2.rotate(theta);
				g2.drawString(stateText, -(textWidth/2), -(textHeight/2));
				g2.rotate(-theta);
				g2.translate(-textX, -textY);
				
				g.setColor(actionColor);
				g.drawLine(aX, aY, bX, bY);				
				
				int a1X			= bX - (int)(Math.cos(theta - ARROW_WIDTH) * ARROW_TAIL);
				int a1Y			= bY - (int)(Math.sin(theta - ARROW_WIDTH) * ARROW_TAIL);
				int a2X			= bX - (int)(Math.cos(theta + ARROW_WIDTH) * ARROW_TAIL);
				int a2Y			= bY - (int)(Math.sin(theta + ARROW_WIDTH) * ARROW_TAIL);
				
				g.drawLine(a1X, a1Y, bX, bY);
				g.drawLine(a2X, a2Y, bX, bY);
				
			}
		}
	}

	protected int getStateIndex(State state){
		if(lts == null)
			return -1;
		Set<State> states	= lts.getStates();
		int i				= 0;
		for(State s : states){
			if(state.equals(s))
				return i;
			i++;
		}
		return -1;
	}
	
	protected double[] getStatePosition(int stateIndex){
		if(lts == null)
			return null;		
		int stateCount					= lts.getStates().size();
		double statePercentage;
		double stateRadian;
		double halfWidth				= getWidth() / 2;
		double halfHeight				= getHeight() / 2;
		double smallestHalfSide			= halfWidth < halfHeight? halfWidth : halfHeight;
		double stateX, stateY;
		statePercentage		= ((double)stateIndex) / ((double)stateCount);
		stateRadian			= statePercentage * 2.0f * Math.PI;
		stateX				= halfWidth + smallestHalfSide * LTS_RADIUS_PRCNT * Math.cos(stateRadian);
		stateY				= halfHeight + smallestHalfSide * LTS_RADIUS_PRCNT * Math.sin(stateRadian);
		double pair[]		= {stateX, stateY};
		return pair;
	}
	
	protected int getStateRadius(int stateIndex){
		if(lts == null)
			return (int)0;
		int i				= 0;
		Set<State> states	= lts.getStates();
		for(State state : states){
			if(i == stateIndex){
				if(state.equals(initialState)){
					//Deshabilito animacion para estado inicial. Usuarios tienden a confundirlo con estado vigente
					//return (int)(INITIAL_STATE_RADIUS * (1 + ACTIVE_STATE_JITTER * animationIndex));
					return STATE_RADIUS;
				}else if(state.equals(currentState)){
					return (int)(STATE_RADIUS * (1 + ACTIVE_STATE_JITTER * animationIndex));					
				}else{					
					return STATE_RADIUS;
				}
			}
			i++;
		}	
		return 0;
	}
	
	protected Color getStateColor(int stateIndex){
		if(lts == null)
			return Color.BLACK;
		int i							= 0;
		Set<State> states	= lts.getStates();
		for(State state : states){
			if(i == stateIndex){
				if(state.equals(initialState)){
					return INITIAL_COLOR;
				}else if(state.equals(currentState)){
					return ACTIVE_COLOR;					
				}else{					
					return LINE_COLOR;
				}
			}
			stateIndex++;
		}	
		return Color.BLACK;
	}	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			animationIndex	= (animationIndex + ANIMATION_STEP);
			if(animationIndex < 0){
				animationIndex = 0;
				ANIMATION_STEP *= -1;
			}else if(animationIndex > 1){
				animationIndex = 1;
				ANIMATION_STEP *= -1;
			}
			invalidate();
			repaint();	
			try {
				Thread.sleep(UPDATE_GAP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}		
}
