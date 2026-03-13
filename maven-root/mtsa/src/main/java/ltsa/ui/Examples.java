package ltsa.ui;

/* Creates the Example menu
*/
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class Examples {
	
	JMenu parent;
	HPWindow out;
	
	public Examples(JMenu parent, HPWindow out) {
	   this.parent = parent;
	   this.out = out;
	}
	
	public void getExamples() {
		  List chapters = getContents("example/contents.txt");
		  Iterator i = chapters.iterator();
		  while(i.hasNext()) {
		  	  String s = (String)i.next();
		  	  JMenu chapter = new JMenu(s.substring(0,s.indexOf('_')));
		  	  parent.add(chapter);
		  	  List examples = getContents("example/"+s+"/contents.txt");
		  	  Iterator j = examples.iterator();
		  	  while(j.hasNext()) {
		  	  	  String es = (String)j.next();
		  	  	  int dot = es.indexOf('.');
		  	  	  String exs = dot>0? es.substring(0,dot) : es;
		  	  	  JMenuItem example =new JMenuItem(exs);
		  	  	  example.addActionListener(new ExampleAction("example/"+s+"/",es));
		  	  	  chapter.add(example);
		  	  }
		  }
	}
	
	private List getContents(String resource) {
		List contents = new ArrayList(16);
		try {
		    InputStream fin = this.getClass().getResourceAsStream(resource);
         BufferedReader myInput = new BufferedReader(new InputStreamReader(fin));
				String thisLine;
			   while ((thisLine = myInput.readLine()) != null) {
					if (!thisLine.equals("")) contents.add(thisLine);
				 }
		} catch (Exception e) {
	       out.ltsOutput.outln("Error getting resource: "+resource);
		}
		return contents;
	}
	
	class ExampleAction implements ActionListener {
	  String dir, ex;
	  ExampleAction(String dir, String ex) {this.dir=dir; this.ex=ex;}
	  
    public void actionPerformed(ActionEvent e) {
      out.newExample (dir, ex);
    }
  }
  
}
  