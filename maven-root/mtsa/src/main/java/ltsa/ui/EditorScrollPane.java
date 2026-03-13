package ltsa.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * 
 * @author Philipp Wilhelm Provides a JScrollPane with line-numbers
 */
public class EditorScrollPane extends JScrollPane {

    private static final long serialVersionUID = 1L;

    private JEditorPane inputArea;
    private String indentation = "  ";
    private JTextPane lineNumbersPane;

    private Font currentFont;

    private int currentNumberOfLines = 0;

    /*
     * Here the constructor creates a TextPane as an editor-field and another
     * TextPane for the line-numbers.
     */
    public EditorScrollPane(JEditorPane input) {

        super(input, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // Editor-field
        inputArea = input;
        Document doc = inputArea.getDocument();

        // Line-numbers
        lineNumbersPane = new JTextPane();
        lineNumbersPane.setBackground(Color.lightGray);
        lineNumbersPane.setEditable(false);

        // Line-numbers should be right-aligned
        SimpleAttributeSet rightAlign = new SimpleAttributeSet();
        StyleConstants.setAlignment(rightAlign, StyleConstants.ALIGN_RIGHT);
        lineNumbersPane.setParagraphAttributes(rightAlign, true);

        doc.addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                lineNumbers();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                lineNumbers();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                lineNumbers();
            }
        });

        lineNumbersPane.setMaximumSize(new Dimension(lineNumbersPane.getWidth(), Integer.MAX_VALUE));
        
        this.setRowHeaderView(lineNumbersPane);
        this.getVerticalScrollBar().setUnitIncrement(16);
    }

    private void lineNumbers() {
        try {
            String str = inputArea.getText();
            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setFontSize(attr, currentFont.getSize());

            // Calculating the number of lines
            int length = str.length() - str.replaceAll("\n", "").length() + 1;
            if (length != this.currentNumberOfLines) {
                // Remove all from document
                Document doc = lineNumbersPane.getDocument();
                doc.remove(0, doc.getLength());

                // Adding line-numbers
                for (int i = 1; i <= length; i++) {
                    doc.insertString(doc.getLength(), i + "\n", attr);
                }
            }

            this.currentNumberOfLines = length;

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void setFont(Font aFont) {
        currentFont = aFont;
        if (inputArea == null || lineNumbersPane == null) {

        } else {
            inputArea.setFont(aFont);
            this.updateLineNumbersFont(lineNumbersPane, aFont);
        }

    }

    public void updateLineNumbersFont(JTextPane pane, Font aFont) {
        pane.setFont(aFont);
        StyledDocument doc = pane.getStyledDocument();

        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setFontSize(attr, currentFont.getSize());
        doc.setCharacterAttributes(0, doc.getLength() + 1, attr, false);

    }

    /*
     * Setting indentation size in editor-field
     */
    public void setIndentationSize(int size) {
        String cache = indentation;
        indentation = "";
        for (int i = 0; i < size; i++) {
            indentation += " ";
        }
        // Replace all previous indentations (at beginning of lines)
        inputArea.setText(inputArea.getText().replaceAll(cache, indentation));
    }

    /*
     * Overrides the method getText().
     */
    public String getText() {
        return inputArea.getText();
    }

    /*
     * Overrides the method setText().
     */
    public void setText(String str) {
        inputArea.setText(str);
    }
}