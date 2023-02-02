package ltsa.MultiCore;

import MTSAClient.ac.ic.doc.mtsa.MTSCompiler;
import jargs.gnu.CmdLineParser;
import ltsa.lts.FileInput;
import ltsa.ui.StandardOutput;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Virginia Brassesco on 11/2/16.
 */
public class TestMultiCore {


    public static void main(String[] args) {

        CmdLineParser cmdParser = new CmdLineParser();
        CmdLineParser.Option fileInput = cmdParser.addStringOption('f', "file");
        CmdLineParser.Option directoryInput = cmdParser.addStringOption('d', "directory");
        CmdLineParser.Option workerInput = cmdParser.addIntegerOption('w', "worker");

        if (args.length > 0) {
            try {
                cmdParser.parse(args);

                Integer worker = (int) cmdParser.getOptionValue(workerInput, new Integer(1));
                File file = new File(String.valueOf(cmdParser.getOptionValue(fileInput)));
                File directory = new File(String.valueOf(cmdParser.getOptionValue(directoryInput)));


                if (directory != null && directory.exists() && directory.isDirectory()) {
                    iterateDirectory(directory, worker);
                } else if (file != null && file.exists()) {
                    buildControllerWithNWorkers(file, worker);
                }

            } catch (CmdLineParser.OptionException e) {
                showUsage();
                System.exit(0);
            }
        } else {
            showUsage();
            System.exit(0);
        }
    }

    private static void buildControllerWithNWorkers(File inputFile, int worker) {
        ComputerOptions.getInstance().setAllowedThreads(worker);


        FileInput inputLTS;
        try {
            inputLTS = new FileInput(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            MTSCompiler.getInstance().compileComposite("C", inputLTS, new StandardOutput());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static void iterateDirectory(File inputDirectory, int worker) {
        for (File fileEntry : inputDirectory.listFiles()) {
            String filename = fileEntry.getName();
            if (fileEntry.isFile() && fileEntry.exists() && FilenameUtils.isExtension(filename, "lts")) {
                buildControllerWithNWorkers(fileEntry, worker);
            }
        }
    }


    private static void showUsage() {
        String usage = "Test Multi Core usage:\n\t" +
                "Log with results will be written to local Log Directory = \"/log/\"\n" +
                "TestMultiCore --worker NUMBER_OF_WORKERS --file LTSFile\n\n" +
                "Available options:\n\n" +
                "\t(--file | -f) LTSFile: Specify the file .lts\n" +
                "\t(--directory | -d) Specify a directory with some .lts files\n" +
                "\t(--worker | -w) <NUMBER_OF_WORKERS> : Specify number of Workers to calculate game rank, default 1.\n";
//        System.out.print(usage);
    }


}



