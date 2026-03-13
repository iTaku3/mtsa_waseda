package ltsa.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ltsa.lts.Alphabet;
import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.lts.EventClient;
import ltsa.lts.EventManager;
import ltsa.lts.LTSEvent;
import ltsa.lts.LTSOutput;

public class AlphabetWindow extends JSplitPane implements LTSOutput, EventClient {

    JTextArea output;
    JList list;
    JScrollPane left,right;
    EventManager eman;
    int Nmach;
    int selectedMachine = 0;
    Alphabet current = null;
    int expandLevel = 0;
	  CompactState [] sm; //an array of machines
    AlphabetWindow thisWindow;
    private final static int MAXPRINT = 400;

    public AlphabetWindow (CompositeState cs,EventManager eman) {
      super();
      this.eman = eman;
      thisWindow = this;
      //scrollable output pane
      output = new JTextArea(23,50);
      output.setEditable(false);
      right = new JScrollPane
                          (output,
                             JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                             JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                            );
		  output.setBackground(Color.white);
      output.setBorder(new EmptyBorder(0,5,0,0));
      //scrollable list pane
      list = new JList();
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      list.addListSelectionListener(new PrintAction());
      left = new JScrollPane
                          (list,
                             JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                             JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                            );
      JPanel fortools = new JPanel(new BorderLayout());
      fortools.add("Center",right);
      // tool bar
      JToolBar tools = new JToolBar();
      tools.setOrientation(JToolBar.VERTICAL);
      fortools.add("West",tools);
      tools.add(HPWindow.instance.createTool("icon/expanded.gif","Expand Most",new ExpandMostAction()));
      tools.add(HPWindow.instance.createTool("icon/expand.gif","Expand",new ExpandMoreAction()));
      tools.add(HPWindow.instance.createTool("icon/collapse.gif","Collapse",new ExpandLessAction()));
      tools.add(HPWindow.instance.createTool("icon/collapsed.gif","Most Concise",new ExpandLeastAction()));
	    if (eman!=null) eman.addClient(this);
	    new_machines(cs);
      setLeftComponent(left);
      setRightComponent(fortools);
      setDividerLocation(150);
      validate();
    }

	//------------------------------------------------------------------------

  class ExpandMoreAction implements ActionListener {
     public void actionPerformed(ActionEvent e) { 
       if (current == null) return;
       if(expandLevel<current.maxLevel) ++expandLevel;
       clearOutput();
       current.printExpand(thisWindow,expandLevel);
     }
  }
  
 class ExpandLessAction implements ActionListener {
     public void actionPerformed(ActionEvent e) { 
       if (current == null) return;
       if(expandLevel>0) --expandLevel;
       clearOutput();
       current.printExpand(thisWindow,expandLevel);
     }
  }
 
  class ExpandMostAction implements ActionListener {
     public void actionPerformed(ActionEvent e) { 
       if (current == null) return;
       expandLevel = current.maxLevel;
       clearOutput();
       current.printExpand(thisWindow,expandLevel);
     }
  }

  class ExpandLeastAction implements ActionListener {
     public void actionPerformed(ActionEvent e) { 
       if (current == null) return;
       expandLevel = 0;
       clearOutput();
       current.printExpand(thisWindow,expandLevel);
     }
  }

  class PrintAction implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent e) {
        int machine = list.getSelectedIndex();
        if (machine<0 || machine >=Nmach) return;
        selectedMachine = machine;
        clearOutput();
        current=new Alphabet(sm[machine],true);
        if (expandLevel>current.maxLevel) expandLevel = current.maxLevel;
        current.printExpand(thisWindow,expandLevel);
    }
  }

/*---------LTS event broadcast action-----------------------------*/
	public void ltsAction(LTSEvent e ) {
	    switch(e.kind){
	    case LTSEvent.NEWSTATE:
    	    break;
    	case LTSEvent.INVALID:
    	    new_machines((CompositeState)e.info);
		    break;
		  case LTSEvent.KILL:
		    break;
        default:
        }
	}

//------------------------------------------------------------------------

	public void out ( String str ) {
		output.append(str);
	}

	public void outln ( String str ) {
		output.append(str+"\n");
	}

	public void clearOutput () {
		output.setText("");
	}

	private void new_machines(CompositeState cs){
     int hasC = (cs!=null && cs.composition!=null)?1:0;
     if (cs !=null && cs.machines !=null && cs.machines.size()>0) { // get set of machines
         sm = new CompactState[cs.machines.size()+hasC];
         Enumeration e = cs.machines.elements();
         for(int i=0; e.hasMoreElements(); i++)
            sm[i] = (CompactState)e.nextElement();
         Nmach = sm.length;
         if (hasC==1)
            sm[Nmach-1]=cs.composition;
     } else
         Nmach = 0;
	  DefaultListModel lm = new DefaultListModel();
		for(int i=0;i<Nmach;i++) {
		    if (hasC==1 && i== (Nmach-1))
		        lm.addElement("||"+sm[i].name);
		    else
		        lm.addElement(sm[i].name);
		}
    list.setModel(lm);
    if (selectedMachine>=Nmach) selectedMachine = 0;
    current=null;
	  clearOutput();
	}

//------------------------------------------------------------------------

  public void removeClient() {
   if (eman!=null) eman.removeClient(this);
  }
  
  public void copy() {
    output.copy();
  }
  
  //------------------------------------------------------------------------

    public void saveFile () {

    FileDialog fd = new FileDialog ((Frame)getTopLevelAncestor(), "Save text in:", FileDialog.SAVE);
      if (Nmach>0) {
        String fname = sm[selectedMachine].name;
        int colon = fname.indexOf(':',0);
        if (colon>0) fname = fname.substring(0,colon);
        fd.setFile(fname+".txt");
    }
    fd.show();
    String file = fd.getFile();
        if (file != null)
    try {
        int i = file.indexOf('.',0);
        file = file.substring(0,i) + "."+"txt";
      FileOutputStream fout =  new FileOutputStream(fd.getDirectory()+file);
      // now convert the FileOutputStream into a PrintStream
      PrintStream myOutput = new PrintStream(fout);
      String text = output.getText();
      myOutput.print(text);
      myOutput.close();
      fout.close();
      //outln("Saved in: "+ fd.getDirectory()+file);
    }
    catch (IOException e) {
      outln("Error saving file: " + e);
    }
  }

}