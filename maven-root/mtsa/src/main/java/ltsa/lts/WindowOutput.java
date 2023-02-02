package ltsa.lts;

import javax.swing.*;

public class WindowOutput implements LTSOutput {
    // ------------------------------------------------------------------------
    JTextArea output;

    public WindowOutput(JTextArea o) {
        output = o;
    }

    public void out(String str) {
        SwingUtilities.invokeLater(new OutputAppend(str));
    }

    public void outln(String str) {
        SwingUtilities.invokeLater(new OutputAppend(str + "\n"));
    }

    public void clearOutput() {
        SwingUtilities.invokeLater(new OutputClear());
    }


    class OutputAppend implements Runnable {
        String text;

        OutputAppend(String text) {
            this.text = text;
        }

        public void run() {
            output.append(text);
        }
    }

    class OutputClear implements Runnable {
        public void run() {
            output.setText("");
        }
    }


}
