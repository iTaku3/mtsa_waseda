package MTSSynthesis.controller.gr.time;

import java.util.Random;


public class DeltaGenerator {
	
	 private int deltaId = 0;
	 
	 private String symbol = "a";
	 
	 private CountingSemaphore semaphore = new CountingSemaphore(1);

	 public synchronized String getNextDelta(){
		 String delta = "";
		 try {
			semaphore.take();
			deltaId++;
			delta = symbol+ deltaId;//java.util.UUID.randomUUID();;//+"_"+CID;
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally {
				  try {
					semaphore.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		 return delta;
	 }
	 
	 public void setSymbol(String s){
		 symbol = s;
	 }
	 
	 public synchronized void reset(){
		 deltaId = 0;
	 }
	 
	 public void changeSymbol(){
		 Random r = new Random();
		 Character c = (char) (r.nextInt(26) + 'a');
		 symbol = c.toString();
	 }

}
