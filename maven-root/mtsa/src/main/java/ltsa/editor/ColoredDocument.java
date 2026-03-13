package ltsa.editor;


import javax.swing.text.GapContent;
import javax.swing.text.PlainDocument;
/**
 * Simple extension of plain document to permit colored text
*/
public class ColoredDocument extends PlainDocument {
    ColoredScanner scanner;
	// CONSTRUCTOR-DESTRUCTOR
    public ColoredDocument() { 
      super(new GapContent(1024)); 
      scanner = new ColoredScanner(this);
      // Set the maximum line width
      putProperty(PlainDocument.lineLimitAttribute, new Integer(256)); 
      // Set TAB size
      putProperty(PlainDocument.tabSizeAttribute, new Integer(4)); 
    }

	/**
	 * return the lexical analyzer to produce colors for this document.
	 */
	public ColoredScanner getScanner() {
    return scanner;  
	} 

	// ...................................................................
	
    /**
     * Fetch a reasonable location to start scanning
     * given the desired start location.  This allows
     * for adjustments needed to accomodate multiline
     * comments.
     * Currently scans the complete document. This is not efficient
     * but it's safe
     */
	public int getScannerStart(int p) { return(0); }

	// ..................................................................
}