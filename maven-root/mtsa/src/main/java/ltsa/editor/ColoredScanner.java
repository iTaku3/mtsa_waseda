package ltsa.editor;

import java.awt.Color;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;

import ltsa.lts.LTSInput;
import ltsa.lts.Lex;
import ltsa.lts.Symbol;

public class ColoredScanner implements LTSInput {
	private Document         doc;   //current document
	private int              d_Pos; //current position	in d_text
  private Segment           d_text; //current text contents
  private int              d_offset; //start location of string in document
	private Lex              lex;
  private Symbol           current; //current Symbol
	
	// .......................................................................
	
	// CONSTRUCTOR-DESTRUCTOR
	ColoredScanner(Document document) { 
    d_text = new Segment();
	  doc = document;
		d_Pos = -1;
    d_offset = 0; 
    if (doc.getLength()>0)
    try {          
       document.getText(0,doc.getLength(),d_text);
    } catch (BadLocationException e) {}
    lex = new Lex(this,false);
	} 

	// ..........................................................................
	
  
  
  //move symbol on one position
	public void next() { 
		try { current = lex.in_sym();
        } 
    catch(Exception ex) {  }
	} 
	
	// .......................................................................
	
	/**
	 * Sets the range of the scanner.  This should be called
	 * to reinitialize the scanner to the desired range of
	 * coverage.
	 */
	public void setRange(int beginPos, int endPos) throws BadLocationException {
    doc.getText(beginPos, endPos-beginPos, d_text);
    d_Pos = -1;
    d_offset = beginPos;
    current = null;
	} 

	// ..........................................................................
	
	/**
	 * This fetches the starting location of the current
	 * token in the document.
	 */
	public final int getStartOffset(){
     if (current == null) return d_offset+d_Pos;
	   return current.startPos+d_offset;
	} 

	/**
	 * This fetches the ending location of the current
	 * token in the document.
	 */
	public final int getEndOffset() {
    if (current == null) return (d_offset + d_Pos+1);
	  return current.endPos+d_offset+1;
	} 
  
  /** 
   * get the desired color of the current token
   */
  public  Color getColor() {
     if (current == null) return Color.black;
     return current.getColor();
  }
  /** -------------------------
  * Implementation of LTSInput
  */
  
	public char nextChar () {
    d_Pos = d_Pos + 1;
    if (d_Pos < d_text.count) {
      return d_text.array[d_Pos];
    } else {
      return '\u0000';
    }
  }


  public char backChar () {
    char ch;
    d_Pos = d_Pos - 1;
    if (d_Pos < 0) {
      d_Pos = -1;
      return '\u0000';
    }
    else
      return d_text.array [d_Pos];
  }


  public int getMarker () {
    return d_Pos;
  }
  
	// >>> AMES: Enhanced Modularity
	public void resetMarker() {
		d_Pos = -1;
	}
	// <<< AMES
	
} 