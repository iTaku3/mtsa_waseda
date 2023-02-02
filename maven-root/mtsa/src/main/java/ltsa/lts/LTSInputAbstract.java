package ltsa.lts;

public abstract class LTSInputAbstract implements LTSInput {
    String fSrc;
    int fPos = -1;

    public char backChar() {
        fPos = fPos - 1;
        if (fPos < 0) {
            fPos = 0;
            return '\u0000';
        } else
            return fSrc.charAt(fPos);
    }

    public int getMarker() {
        return fPos;
    }

    public char nextChar() {
        fPos = fPos + 1;
        if (fPos < fSrc.length()) {
            return fSrc.charAt(fPos);
        } else {
            //fPos = fPos - 1;
            return '\u0000';
        }
    }


    public void resetMarker() {
        fPos = -1;
    }

    public String getSource() {
        return fSrc;
    }
}
