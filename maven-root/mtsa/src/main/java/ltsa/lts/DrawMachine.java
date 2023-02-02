package ltsa.lts;
import javax.swing.*;
import java.awt.*;
import java.util.BitSet;

public class DrawMachine {

    
    public static int MAXDRAWSTATES = 200; // maximum drawable size
    final static int STATESIZE = 30;
    
    Font labelFont;  //used for drawing labels
    Font nameFont;   //used for displaying names
    Font stateFont  = new Font("SansSerif",Font.BOLD,18);

      
    protected boolean displayName = false; // draw machine name
    protected boolean newLabelFormat = true; //draw new label format
    protected boolean selectedMachine    = false; // true if this machine is selected
    
    int SEPARATION;  //state separation
    int ARCINC;      //arc separation
      
    int topX =0;          //X - top corner of bounding rectangle
    int topY =0;          //Y - top corner of bounding rectangle

    int zeroX;            //X - start X for drawing states
    int zeroY;            //Y - start Y for drawing states
      
    int heightAboveCenter;   // zeroX - topX
    int nameWidth = 0;       // width of machine name
      
    Dimension size;          // the size of this machine
      
    private int errorState = 0;   //1 if refers to error state
      
    protected int lastselected= -3; //last selected state
    protected int selected = 0;     //current selected state
    protected String lastaction;    //action to be high lit
      
    CompactState mach = null;     //the machine to draw
    
    BitSet accepting;          // the set of states which are accepting
      
    JPanel parent;
    

    public DrawMachine(CompactState m, JPanel p, 
                       Font fn, Font fl, 
                       boolean dn, boolean nl,
                       int separation, int arcIncrement) {
        mach = m;
        parent = p;
        nameFont = fn;
        labelFont = fl;
        displayName = dn;
        newLabelFormat = nl;
        SEPARATION =  separation;
        ARCINC = arcIncrement;
        accepting = mach.accepting();
        if (newLabelFormat) initCompactLabels();
        size = computeDimension(mach);
  	}
    
    public void setDrawName(boolean flag) {
        displayName = flag;
        size = computeDimension(mach);
    }
    
    public void setNewLabelFormat(boolean flag) {
        newLabelFormat = flag;
        if (newLabelFormat) initCompactLabels();
        size = computeDimension(mach);
    }

    
    public void setFonts(Font fn, Font fl) {
        nameFont = fn;
        labelFont = fl;
        size = computeDimension(mach);
    }
    
    public void setStretch(boolean absolute, int separation, int arcIncrement) {
      if (absolute) {
         SEPARATION = separation;
         ARCINC = arcIncrement;
      } else {
         if (SEPARATION + separation >10) SEPARATION += separation;
         if (ARCINC + arcIncrement >5 ) ARCINC += arcIncrement;
      }
      size = computeDimension(mach);
    }
    
    public void select(int last, int current, String name) {
        lastselected = last;
        selected = current;
        lastaction = name;
    }
    
    public void setPos(int x, int y) {
      topX = x;
      topY = y;
    }
    
    public boolean isSelected() {return selectedMachine;}
    public void setSelected(boolean b) {selectedMachine = b;}
    
    public Dimension getSize() {return size;}
    
    public void getRect(Rectangle r) {
        r.x = topX; 
        r.y = topY; 
        r.width = size.width; 
        r.height= size.height;
    }
    
    public CompactState getMachine(){return mach;}
    
    protected Dimension computeDimension(CompactState m) {
        // now compute size of drawing
        // work out length & height of name
        int nameHeight = 0;
        if (displayName) {
          Graphics g = parent.getGraphics();
          if (g!=null) {
            g.setFont(nameFont);
            FontMetrics fm = g.getFontMetrics();
            nameWidth = fm.stringWidth(mach.name);
            nameHeight = fm.getHeight();
          } else {
            nameWidth = SEPARATION; //kludge should never happen
          }
        } else
          nameWidth = 0;
        //if tooo big return
          if (m.maxStates>MAXDRAWSTATES) return new Dimension(220+nameWidth,50);
        // first compute length of largest end label if any
        // TODO take into account larger probabilistic labels
        String largestEndLabel = null;
        if (!newLabelFormat) {
          EventState p = m.states[m.maxStates-1];
          while (p!=null) {
              EventState tr = p;
              while(tr!=null) {
                  if (tr.next==(m.maxStates-1)) {
                    if (largestEndLabel == null) {
                      largestEndLabel = m.alphabet[tr.event];
                    } else {
                      String s = m.alphabet[tr.event];
                      if (s.length()>largestEndLabel.length()) largestEndLabel = s;
                    }
                  }
                  tr=tr.nondet;
              }
              p=p.list;
          }
        } else 
           largestEndLabel = labels[m.maxStates][m.maxStates];
        int endWidth = 10;
        if (largestEndLabel != null) {
          Graphics g = parent.getGraphics();
          if (g!=null) {
            g.setFont(labelFont);
            FontMetrics fm = g.getFontMetrics();
            endWidth = fm.stringWidth(largestEndLabel);
            endWidth += SEPARATION/3;
          } else {
            endWidth = SEPARATION; // kludge should never happen
          }
        }
        // check if machine has an error state
        errorState=0;
        for (int i=0; i<m.maxStates; i++ )
            if (EventState.hasState(m.states[i],Declaration.ERROR))
                {errorState=1;}
        // now compute maximum forward and backward arcs
        // TODO take into account probabilistic labels & transitions
        int maxFwd = 0;
        int maxFwdLabels = 0;
        int maxBwd = 0;
        int maxBwdLabels = 0;
        for (int i=0; i<m.maxStates; i++ ) {
                int[] ntrans =  new int[m.maxStates+1];
                int fwdToState = 0;
                int bwdToState = 0;
                boolean fwd = false;
                boolean bwd = false;
                EventState p = m.states[i];
                while (p!=null) {
                    EventState tr = p;
                    while(tr!=null) {
                        ntrans[tr.next+1]++;
                        int diff = tr.next - i;
                        if (diff>maxFwd || (diff==maxFwd && ntrans[tr.next+1] > maxFwdLabels)) 
                          {maxFwd = diff; fwdToState = tr.next+1; fwd = true; }
                        if (diff<maxBwd || (diff==maxBwd && ntrans[tr.next+1] > maxBwdLabels))  
                          {maxBwd = diff; bwdToState = tr.next+1; bwd = true; }                
                        tr=tr.nondet;
                    }
                    p=p.list;
                }
                if (fwd) maxFwdLabels = newLabelFormat?1:ntrans[fwdToState];
                if (bwd) maxBwdLabels = newLabelFormat?1:ntrans[bwdToState];
            }
        if (m.maxStates==1) maxFwdLabels =0;
        int fheight = 10;
        Graphics g = parent.getGraphics();
        if (g!=null) {
            g.setFont(labelFont);
            FontMetrics fm = g.getFontMetrics();
            fheight = fm.getHeight();
        }
        heightAboveCenter = (maxFwd!=0)? (ARCINC*maxFwd)/2:(STATESIZE/2 + nameHeight);
        heightAboveCenter = heightAboveCenter + maxFwdLabels*fheight + 10;
        int heightBelowCenter = (maxBwd!=0)? ARCINC*Math.abs(maxBwd)/2:STATESIZE/2;
        heightBelowCenter = heightBelowCenter +maxBwdLabels * fheight +10;
        int pwidth  = errorState==0 
                        ?10+nameWidth+STATESIZE+endWidth+(m.maxStates-1)*SEPARATION
                        : 10 +STATESIZE+endWidth+m.maxStates*SEPARATION;
        int pheight = heightAboveCenter + heightBelowCenter;
        return new Dimension(pwidth,pheight);
    }
    
    public void fileDraw(Graphics g) {
        int saveX = topX;
        int saveY = topY;
        boolean sm = selectedMachine;
        topX =0; topY =0; selectedMachine = false;
        draw(g);
        topX = saveX;
        topY = saveY;
        selectedMachine = sm;
    }

    public void draw(Graphics g) {
        //draw transitions
        CompactState m = mach;
        if (m==null) return;
        if (selectedMachine) {
          g.setColor(Color.white);
          g.fillRect(topX,topY,size.width,size.height);
        }
        int aw = 0; //width allowed for name
        if (displayName && errorState==0) aw =nameWidth;
        zeroX = topX+ 10 +errorState*SEPARATION+aw;
        zeroY = topY + heightAboveCenter-STATESIZE/2;
        if (m.maxStates>MAXDRAWSTATES) {
            g.setColor(Color.black);
            g.setFont(nameFont);
            g.drawString(m.name+" -- too many states: "+m.maxStates, topX, topY+20);
        } else {
             // display name
            g.setFont(nameFont);
            FontMetrics fm = g.getFontMetrics();
	        int nw = fm.stringWidth(m.name);
	        g.setColor(Color.black);
	        if(displayName) g.drawString(m.name,zeroX-nw,zeroY-5);
            //draw transitions - lines
            for (int i=0; i<m.maxStates; i++ ) {
                int [] ntrans = new int[m.maxStates+1]; //count transtions between 2 states
                EventState p = m.states[i];
                while (p!=null) {
                    EventState tr = p;
                    String  event = m.alphabet[tr.event];
                    if (event.charAt(0)!='@')
                    while(tr!=null) {
                        ntrans[tr.next+1]++;
                        // drawTransition(g,i,tr.next,event,ntrans[tr.next+1],
                        //               i==lastselected && tr.next==selected && lastaction!=null,false);
                        drawTransition(g, i, tr, event, ntrans[tr.next+1],
                                i==lastselected && tr.next==selected && lastaction!=null, false);
                        if (tr instanceof ProbabilisticEventState) {
                        	ProbabilisticEventState probTr= (ProbabilisticEventState) ((ProbabilisticEventState) tr).probTr;
                        	while (probTr != null) {
                        		ntrans[probTr.next+1]++;
                        		drawTransition(g, i, probTr, event, ntrans[probTr.next+1],
                                        i==lastselected && probTr.next==selected && lastaction!=null, false);
                        		probTr= (ProbabilisticEventState) probTr.probTr;
                        	}
                        }
                        tr=tr.nondet;
                    }
                    p=p.list;
                }
            }
            //draw transitions - text
            for (int i=0; i<m.maxStates; i++ ) {
                int [] ntrans = new int[m.maxStates+1]; //count transtions between 2 states
                EventState p = m.states[i];
                while (p!=null) {
                    EventState tr = p;
                    String  event = m.alphabet[tr.event];
                    if (event.charAt(0)!='@')
                    while(tr!=null) {
                        ntrans[tr.next+1]++;
                        if (!newLabelFormat) {
                          // drawTransition(g,i,tr.next,event,ntrans[tr.next+1],
                          //               i==lastselected && tr.next==selected,true);
                        	drawTransition(g, i, tr, event, ntrans[tr.next+1], i==lastselected && tr.next==selected, true);
                            if (tr instanceof ProbabilisticEventState) {
                            	ProbabilisticEventState probTr= (ProbabilisticEventState) ((ProbabilisticEventState) tr).probTr;
                            	while (probTr != null) {
                            		ntrans[probTr.next+1]++;
                            		drawTransition(g, i, probTr, event, ntrans[probTr.next+1],
                                            i==lastselected && probTr.next==selected, true);
                            		probTr= (ProbabilisticEventState) probTr.probTr;
                            	}
                            }
                        } else {
								if (ntrans[tr.next + 1] == 1) {
									// drawTransition(g,i,tr.next,labels[i+1][tr.next+1],ntrans[tr.next+1],
									// i==lastselected && tr.next==selected &&
									// lastaction!=null,true);
									drawTransition(g, i, tr, labels[i + 1][tr.next + 1], ntrans[tr.next + 1],
												   i == lastselected && tr.next == selected && lastaction != null, true);
									if (tr instanceof ProbabilisticEventState) {
										ProbabilisticEventState probTr= (ProbabilisticEventState) ((ProbabilisticEventState) tr).probTr;
										while (probTr != null) {
											ntrans[probTr.next + 1]++;
											if (ntrans[probTr.next + 1] == 1) {
												drawTransition(g, i, probTr, labels[i+1][probTr.next+1], ntrans[probTr.next + 1],
															   i == lastselected && probTr.next == selected && lastaction != null, true);
											}
											probTr= (ProbabilisticEventState) probTr.probTr;
										}
									}
								}
                        }
                        tr=tr.nondet;
                    }
                    p=p.list;
                }
            }

            for (int i=-errorState; i<m.maxStates; i++ )
                drawState(g,i,i==selected);
        }
        if (selectedMachine) {
        	g.setColor(Color.gray);
        	g.drawRect(topX,topY,size.width,size.height);
        }
    }



    private void drawState(Graphics g,int id, boolean highlight) {
        int x = zeroX + id*SEPARATION;
        int y = zeroY;
        if (highlight)
            g.setColor(Color.red);
        else
             g.setColor(Color.cyan);
        if (id>=0 && accepting.get(id))
           g.fillArc(x-3,y-3,STATESIZE+6,STATESIZE+6,0,360);
        else
           g.fillArc(x,y,STATESIZE,STATESIZE,0,360);
        g.setColor(Color.black);
        g.setFont(stateFont);
        if (id>=0 && accepting.get(id))
        	  g.drawArc(x-3,y-3,STATESIZE+6,STATESIZE+6,0,360);
        g.drawArc(x,y,STATESIZE,STATESIZE,0,360);
        FontMetrics fm = g.getFontMetrics();
        String sid = (id==mach.endseq) ?"E":""+id;
        int px = x + STATESIZE/2 - fm.stringWidth(sid)/2;
        int py = y + STATESIZE/2 + fm.getHeight()/3;
        g.drawString(sid, px, py);
    }

//     private void drawTransition(Graphics g,int from, int to, String s, int n, boolean highlight, boolean dotext) {
    private void drawTransition(Graphics g, int from, EventState toState, String s, int n, boolean highlight, boolean dotext) {
    	int to= toState.next;

        if (highlight)
            g.setColor(Color.red);
        else
            g.setColor(Color.black);
        int sign = (to<=from)?-1:1;
        int start= (to<from)?to:from;
        int x = zeroX + start*SEPARATION + STATESIZE/2;
        int w = (to!=from)? (SEPARATION*Math.abs(from-to)): SEPARATION/3;
        int h = (to!=from)? (ARCINC*Math.abs(from-to)):STATESIZE-5;
        int y = zeroY - (h - STATESIZE)/2;
        if (n==1 && !dotext){  //only draw arc for first transition to->from
            if (from !=to) {
                g.drawArc(x,y,w,h,0,180*sign);
                if (sign>0)
                   drawArrow(g,x+w/2,y,arrowForward);
                else
                   drawArrow(g,x+w/2,y+h-1,arrowBackward);
            } else {
                g.drawArc(x,y,w,h,0,360);
                drawArrow(g,x+w,y+h/2,arrowDown);
            }
        }
        if (!dotext) return;
        n +=1;   // shift up to permit arrow
        g.setFont(labelFont);
        FontMetrics fm = g.getFontMetrics();
        int drop = fm.getMaxAscent()/3;
        int px = x +w/2 - fm.stringWidth(s)/2;
        if (to==from) px = x +w+2;
        int py =(sign>0)?y+drop:(y + h+drop);
        if (to==from) py = y+h/2+drop;
        if (n>1) {
              py = py - (n-1)*fm.getHeight()*sign;
        }
        g.setColor(Color.white);
        g.fillRect(px,py-fm.getMaxAscent(),fm.stringWidth(s),fm.getHeight());
        if (highlight && ((lastaction!=null && lastaction.equals(s)) || newLabelFormat))
            g.setColor(Color.red);
        else
            g.setColor(Color.black);

        // TODO move away if possible
        if (toState instanceof ProbabilisticEventState) {
        	ProbabilisticEventState probToState= (ProbabilisticEventState) toState;
        	s= s + " {" + probToState.getBundle() + ":" + probToState.getProbability() + "}";
        }
        g.drawString(s, px, py);
    }


    private int arrowX[] = new int[3];
    private int arrowY[] = new int[3];

    private static int arrowForward = 1;
    private static int arrowBackward = 2;
    private static int arrowDown = 3;

    private void drawArrow(Graphics g, int x, int y, int direction) {
        if (direction == arrowForward) {
            arrowX[0] = x-5; arrowY[0] = y-5;
            arrowX[1] = x+5; arrowY[1] = y;
            arrowX[2] = x-5; arrowY[2] = y+5;
        }  else if (direction == arrowBackward){
            arrowX[0] = x+5; arrowY[0] = y-5;
            arrowX[1] = x-5; arrowY[1] = y;
            arrowX[2] = x+5; arrowY[2] = y+5;
        }  else if (direction == arrowDown){
            arrowX[0] = x-5; arrowY[0] = y-5;
            arrowX[1] = x+5; arrowY[1] = y-5;
            arrowX[2] = x; arrowY[2] = y+5;
        }
        g.fillPolygon(arrowX,arrowY,3);
    }
    
    String [][] labels;  // from -- to
    
    private void initCompactLabels () {
      if (mach == null) return;
      if (mach.maxStates>MAXDRAWSTATES) return;
      labels =  new String[mach.maxStates+1][mach.maxStates+1];
      for (int i=0; i<mach.maxStates; i++ ) {
          EventState current = EventStateUtils.transpose(mach.states[i]);
          while (current != null) {
            String[] events = EventState.eventsToNextNoAccept(current,mach.alphabet);
            Alphabet a = new Alphabet(events);
            labels[i+1][current.next+1] = a.toString();
            current = current.list;
          }
      }
    }
      
}