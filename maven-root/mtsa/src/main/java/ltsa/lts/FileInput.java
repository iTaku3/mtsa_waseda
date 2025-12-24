package ltsa.lts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/** An implementation of LTSInput which is convinient for command-line execution. */
public class FileInput extends LTSInputAbstract {

  public FileInput(File f) throws IOException {
    FileInputStream s = new FileInputStream(f);
    byte[] bytes = new byte[(int) f.length()];
    s.read(bytes);
    fSrc = new String(bytes);
  }
}
