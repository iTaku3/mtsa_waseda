package ltsa.lts.ltsCompiler;

import IntegrationTests.TestLTSCompose;
import MTSAClient.ac.ic.doc.mtsa.MTSCompiler;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This class tests for every file in ltsa/dist/examples/ParserTest compile the
 * TEST label testing if the parser works well.
 *
 * @author lnahabedian
 */
public class ParserTests {

    @Test
    public void testPropertyInComposite() throws Exception {
        int errors = 0;
        File file;
        String resourceFolder = "ParserTests";
        URL resource = TestLTSCompose.class.getClassLoader().getResource(resourceFolder);
        String path = URLDecoder.decode(resource.getFile(), "UTF-8");
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                file = listOfFiles[i];
                String[] pathSplited = file.toString().split(File.separator);
                String filename = pathSplited[pathSplited.length - 1];
                System.out.println("TESTING FILE: " + filename);
                errors += RunTest(file);
                System.out.println(" ");
            }
        }
        if (errors == 0) {
            assertTrue(true);
        } else {
            System.out.println("From " + listOfFiles.length + " test, "
                    + new Integer(errors).toString() + " Fail");
        }

    }

    protected int RunTest(File file) throws IOException {

        String inputModel = getInputModel(file);
        MTSCompiler compiler = MTSCompiler.getInstance();

        try {
            compiler.compileCompositeState("TEST", inputModel);
        } catch (Exception e) {
            e.printStackTrace();
            String[] pathSplited = file.toString().split(File.separator);
            String filename = pathSplited[pathSplited.length - 1];
            System.out.println("The parser fault in file: " + filename);
            System.out.println(" ");
            return 1;
        }
        return 0;
    }

    protected String getInputModel(File file) throws IOException {
        String result = null;
        BufferedReader br = null;
        try {
            FileReader fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line = br.readLine();
            StringBuffer sb = new StringBuffer();
            while (line != null) {
                sb.append(line + " "); // that white space separates keywords
                // from one line to other
                line = br.readLine();
            }
            result = sb.toString();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            br.close();
        }
        return result;
    }

}
