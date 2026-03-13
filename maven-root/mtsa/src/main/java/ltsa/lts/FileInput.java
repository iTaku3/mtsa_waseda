package ltsa.lts;

import ltsa.lts.LTSInput;
import ltsa.lts.LTSInputAbstract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * An implementation of LTSInput which is convinient for command-line execution.
 */
public class FileInput extends LTSInputAbstract{


    public FileInput(File f) throws IOException {
        FileInputStream s = new FileInputStream(f);
        byte[] bytes = new byte[(int) f.length()];
        s.read(bytes);
        fSrc = new String(bytes);
    }

}
