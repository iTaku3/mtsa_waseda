package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.Kinect;

import javax.swing.*;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.exceptions.ARDroneException;

import java.awt.*;
import java.awt.event.*;
public class GUIfordrone extends JPanel{
	DroneAPI drone=null;
	private String text="aaa";

	KeyCheck k;
	public GUIfordrone(String title,DroneAPI drone){
		this.drone=drone;
		JFrame  f =new JFrame();
		f.getContentPane().add(this);
		f.addKeyListener(new KeyCheck());
		this.setSize(320,180);
		f.setSize(320, 180);
		f.setVisible(true);
	}
	
	public void setText(String t){
		this.text=t;
	}
	public void paint(Graphics g)
	{
		g.clearRect(0, 0, 320, 180);
		g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
		g.setColor(Color.black);
		g.drawString(text, 10, 20);
	}
	
	class KeyCheck implements KeyListener{
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			check(e);	
		}

		@Override
		public void keyReleased(KeyEvent e) {
			drone.hover(500);
		}
		void check(KeyEvent e){
			char keyChar = e.getKeyChar();
//			System.out.println("KeyChar:"+keyChar);			
			int keyCode=e.getKeyCode();
			try {
				switch(keyCode){
				case KeyEvent.VK_U:
					drone.upSimply();
					break;
				case KeyEvent.VK_D:
					drone.downSimply();
					break;
				case KeyEvent.VK_T:
					drone.takeoff();
					drone.hover(500);
					break;
				case KeyEvent.VK_C:
					drone.landing();
					break;
				case KeyEvent.VK_UP:
					drone.forwardManu();
					break;
				case KeyEvent.VK_DOWN:
					drone.backWardManu();
					break;
				case KeyEvent.VK_LEFT:
					drone.goLeftManu();
					break;
				case KeyEvent.VK_RIGHT:
					drone.goRightManu();
					break;
				case KeyEvent.VK_R:
					drone.spinRight();
					break;
				case KeyEvent.VK_L:
					drone.spinLeft();
					break;
				case KeyEvent.VK_0:
					drone.move(0, 0);
					break;
				case KeyEvent.VK_1:
					drone.move(0, 1);
					break;
				case KeyEvent.VK_2:
					drone.move(0, 2);
					break;
				case KeyEvent.VK_3:
					drone.move(1, 1);
					break;
				case KeyEvent.VK_P:
					int[] a=drone.getPosition();
					float[] b=drone.getPositionDebug();
					if(a==null){
						setText("failed");
					}else {
//						System.out.println(a[0]+","+a[1]);
//						System.out.println(b[0]+","+b[1]);
						setText(a[0]+"/"+b[0]+","+a[1]+"/"+b[1]);
					};
					repaint();
					break;
				case KeyEvent.VK_N:
					drone.turn(Direct.NORTH);
					break;
				case KeyEvent.VK_S:
					drone.turn(Direct.SOUTH);
					break;
				case KeyEvent.VK_E:
					drone.turn(Direct.EAST);
					break;
				case KeyEvent.VK_W:
					drone.turn(Direct.WEST);
					break;
				case KeyEvent.VK_H:
					drone.hover(50);
					break;
				case KeyEvent.VK_5:
					drone.setCurrentDirectAsNorth();
					break;
				}
			}catch (ARDroneException e1) {
				e1.printStackTrace();
			}
		}
	}
}