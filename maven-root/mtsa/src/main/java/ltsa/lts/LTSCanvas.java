package ltsa.lts;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

public class LTSCanvas extends JPanel implements Scrollable {

    public static boolean fontFlag = false;
    public static boolean displayName = false;
    public static boolean newLabelFormat = true;
    Dimension initial = new Dimension(10,10);

    Font nameFont;
    Font labelFont;
    
    final static int SEPARATION = 80;
    final static int ARCINC = 30;
    
    protected boolean singleMode = false;
 
    DrawMachine drawing[];
    DrawMachine focus;
    
    protected MouseInputListener mouse;
    
    public LTSCanvas(boolean mode) {
        super();
        setBigFont(fontFlag);
        setBackground(Color.white);
        singleMode = mode;
        if (!singleMode) {
          mouse = new MyMouse();
          this.addMouseListener(mouse);
          this.addMouseMotionListener(mouse);
        }
  	}
    
    public void setMode(boolean mode) {
        if (mode == singleMode) return;
        focus = null;
        if (drawing!=null) {
            int len = drawing.length;
            drawing = new DrawMachine[len];
        }
        singleMode = mode;
        if (!singleMode) {
          mouse = new MyMouse();
          this.addMouseListener(mouse);
          this.addMouseMotionListener(mouse);
        } else {
          if (mouse!=null) {
            this.removeMouseListener(mouse);
            this.removeMouseMotionListener(mouse);
            mouse = null;
          }
        }
        setPreferredSize(initial);
        revalidate();
        repaint();
    }
    
    public void setBigFont(boolean flag) {
       if (flag) {
            labelFont = new Font("Serif",Font.BOLD,14);
            nameFont  = new Font("SansSerif",Font.BOLD,18);
       } else {
            labelFont = new Font("Serif",Font.PLAIN,12);
            nameFont =  new Font("SansSerif", Font.BOLD,14);
       }
       if (drawing!=null) 
          for(int i=0; i<drawing.length; i++)
               if (drawing[i]!=null) drawing[i].setFonts(nameFont, labelFont);
       repaint();
    }
    
    public void setDrawName(boolean flag) {
        displayName = flag;
        if (drawing!=null) 
          for(int i=0; i<drawing.length; i++)
               if (drawing[i]!=null) drawing[i].setDrawName(displayName);
        repaint();
    }
    
    public void setNewLabelFormat(boolean flag) {
        newLabelFormat = flag;
        if (drawing!=null) 
          for(int i=0; i<drawing.length; i++)
               if (drawing[i]!=null) drawing[i].setNewLabelFormat(newLabelFormat);
        repaint();
    }

    public void setMachines(int n) {
      focus = null;
      if (n>0) 
         drawing =  new DrawMachine[n];
      else 
         drawing = null;
      setPreferredSize(initial);
      revalidate();
      repaint();
    }

    public void draw(int id, CompactState m, int last, int current, String name) {
        if (m == null || id>=drawing.length) {drawing = null; repaint(); return;}
        if (drawing[id]==null) {
           drawing[id] = new DrawMachine
                      (m, this, nameFont, labelFont, displayName, newLabelFormat, SEPARATION,ARCINC);
           if (!singleMode) {
               setEmptyPosition(id);
           }
        }
        if (singleMode) focus = drawing[id];
        
        drawing[id].select(last,current,name);
        Dimension d = drawing[id].getSize();
        Dimension e = getPreferredSize();
        setPreferredSize
          (new Dimension(Math.max(e.width,d.width),Math.max(e.height,d.height))); 
        revalidate();
        repaint();
    }

    private void setEmptyPosition(int drawingId) {
        int y = 0;
        
        for (int i = 0; i < drawing.length; i++) {
            if (i!=drawingId && drawing[i] != null) {
                DrawMachine draw = drawing[i];
                Dimension d = draw.size;
                y += d.height;
            }
        }

        drawing[drawingId].setPos(0, y);
    }

    public void draw_composite(int id, CompactState m, int[] last, int[] current, String name) {
        if (m == null || id>=drawing.length) {drawing = null; repaint(); return;}
        if (drawing[id]==null) {
           drawing[id] = new DrawCompositeMachine
                      (m, this, nameFont, labelFont, displayName, newLabelFormat, SEPARATION,ARCINC);
           if (!singleMode) setEmptyPosition(id);
           
        }
        if (singleMode) focus = drawing[id];
        
        int ls = last == null? 0: m.getCompositionStateFrom(last);
        int cs = current == null ? 0 : m.getCompositionStateFrom(current);

        drawing[id].select(ls, cs, name);
        Dimension d = drawing[id].getSize();
        Dimension e = getPreferredSize();
        setPreferredSize
          (new Dimension(Math.max(e.width,d.width),Math.max(e.height,d.height))); 
        revalidate();
        repaint();
    }    
    
    public void clear(int id) {
        if (drawing[id] == focus) {
            focus = null;
        }
        drawing[id] = null;
        repaint();
    }
    
    public int clearSelected() {
        if (focus == null || singleMode || drawing ==null) return -1;
        int ret;
        for(ret = 0; drawing[ret]!=focus; ++ret);
        focus = null;
        drawing[ret] = null;
        repaint();
        return ret;
    }
         
    private Rectangle rr = new Rectangle();
    
    public void stretchHorizontal(int d){
        if (focus!=null) {
          focus.setStretch(false,d,0);
          focus.getRect(rr);
          Dimension p = getPreferredSize();
          setPreferredSize
               (new Dimension(Math.max(p.width,rr.x + rr.width),
                              Math.max(p.height,rr.y + rr.height))); 
          revalidate();
          repaint();
        } 
    }
        
    public void stretchVertical(int d){
        if (focus!=null) {
          focus.setStretch(false,0,d);
          focus.getRect(rr);
          Dimension p = getPreferredSize();
          setPreferredSize
               (new Dimension(Math.max(p.width,rr.x + rr.width),
                              Math.max(p.height,rr.y + rr.height))); 
          revalidate();
          repaint();
        } 
    }

    public void select(int n, int last[], int current[], String name) {
      if (drawing == null) return;
      for (int i=0; i<n; i++) {
    	DrawMachine drawMachine = drawing[i];
        if (drawMachine!=null) {
           int ls = last!=null? last[i]:0;
           int cs = current!=null?current[i]:0;
           if (drawMachine.getClass() == DrawCompositeMachine.class) {
        	   
               if ((drawMachine.selected == 0) && (cs == 0)) {
               	
               } else {
                   ls = ((DrawCompositeMachine) drawMachine).getMachine().getCompositionStateFrom(last);
                   cs = ((DrawCompositeMachine) drawMachine).getMachine().getCompositionStateFrom(current);
               }
           }
           drawing[i].select(ls,cs,name);
        }
      }
      repaint();    
    }
    
    public DrawMachine getDrawing() {return focus;}

    public void paintComponent(Graphics g){
      super.paintComponent(g);
	    Graphics2D g2d = (Graphics2D)g;
	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	        	               RenderingHints.VALUE_ANTIALIAS_ON);
	    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
	        	               RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	    g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
	        	               RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
      if (drawing!=null && !singleMode) {          
           for(int i=0; i<drawing.length; i++)
               if (drawing[i]!=null) {
                 if (drawing[i]!=focus || focus == null) drawing[i].draw(g);  
               }
      }  
      if (focus!=null) focus.draw(g);
   }

/*-------------Mouse handler ---------------------------*/

    class MyMouse extends MouseInputAdapter {
    	  Point start = null;
    	  Rectangle r = new Rectangle();  //avoid unnecessary garbage
        	  
    	  public void mousePressed(MouseEvent e) {
    	  	 if (drawing!=null) {
             if (focus!=null) {
               focus.setSelected(false);
               focus = null;
               repaint();
             }
             for(int i=0; i<drawing.length; i++) 
               if (drawing[i]!=null) {
                  drawing[i].getRect(r);
    	  	 	      if (r.contains(e.getPoint())) {
                     focus = drawing[i];
    	  	 	  	      focus.setSelected(true);
    	  	 	  	      start = e.getPoint();
    	  	 	  	      repaint();
                     return;
                 }
               }
           }
        }
    	  
    	  public void mouseDragged(MouseEvent e) {
    	  	 if (focus!=null) {
    	  	 	  focus.getRect(r);
    	  	 	  Point current = e.getPoint();
    	  	 	  if (start!=null) {
    	  	 	  	  double xoff = current.getX() - start.getX();
    	  	 	  	  int newX = (int)(r.x + xoff);
    	  	 	  	  double yoff = current.getY() - start.getY();
    	  	 	  	  int newY = (int)(r.y + yoff);
    	  	 	  	  focus.setPos(newX>0?newX:0,newY>0?newY:0);
    	  	 	  	  start = current;
    	  	 	  	  repaint();
    	  	 	  }
    	  	 }
    	  }

    	  public void mouseReleased(MouseEvent e) {
    	  	 start = null;
    	  	 if (focus!=null) {
    	  	 	  focus.getRect(r);
    	  	 	  if (!r.contains(e.getPoint())) {
                 focus.setSelected(false);
                 focus = null;
    	  	 	  	  repaint();
    	  	 	  } else {
                Dimension p = getPreferredSize();
                setPreferredSize
                    (new Dimension(Math.max(p.width,r.x + r.width),Math.max(p.height,r.y + r.height))); 
                  revalidate();
    	  	 	  }
    	  	 }
    	  }
    }

/*-------------Scrollable Implementation ---------------*/
  private int maxUnitIncrement = 1;
  
  public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();    
  }
  
  public int getScrollableUnitIncrement(Rectangle visibleRect,
                                          int orientation,
                                          int direction) {
     return maxUnitIncrement;
  }
 
  public int getScrollableBlockIncrement(Rectangle visibleRect,
                                        int orientation,
                                        int direction) 
 {
   if (orientation == SwingConstants.HORIZONTAL)
          return visibleRect.width - SEPARATION;        
   else
          return visibleRect.height - ARCINC;    
 }
 
 public boolean getScrollableTracksViewportWidth() {        
   return false;
 }    
 
 public boolean getScrollableTracksViewportHeight() {
   return false;    
 }    
 
 public void setMaxUnitIncrement(int pixels) {
        maxUnitIncrement = pixels;    
 }
 
 /*--------------------------------------------*/
 public boolean isFocusTraversable() {return true;}

 }