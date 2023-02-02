package ltsa.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LTSABlanker extends Window {
  final Window thisWindow;

  // SplashScreen's constructor
  public LTSABlanker(Window owner){
    super(owner);
    thisWindow = this;
    setBackground(Color.black);
    pack();
    // Plonk it on center of screen
    Dimension WindowSize=getSize(),ScreenSize=Toolkit.getDefaultToolkit().getScreenSize();
    setBounds(0,0,ScreenSize.width,ScreenSize.height);
    this.addMouseListener(new Mouse());
    setVisible(true);  
  }
  
  class Mouse extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
      thisWindow.setVisible(false);
      thisWindow.dispose();
    }
  }
      
}