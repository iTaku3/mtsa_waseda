package ltsa.ui;

/**
 * Created by mbrassesco on 6/23/17.
 */

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Charsets;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileBrowser implements Runnable {

    private DefaultMutableTreeNode root;

    private DefaultTreeModel treeModel;

    private JTree tree;

    public URL fileRoot;

    public String mySelection;

    public FileBrowser(String examplesDir, String result) {
        //fileRoot = examplesDir;
        mySelection = result;

        this.fileRoot = getClass().getClassLoader().getResource(examplesDir);
        /*
        Path path;
        try {
            path = Paths.get(fileRoot.toURI());
            Files.walk(path, 5).forEach(p -> System.out.printf("- %s%n", p.toString()));
        } catch (URISyntaxException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */

    }

    @Override
    public void run() {
        JFrame frame = new JFrame("MTSA Examples");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        root = new DefaultMutableTreeNode(fileRoot.getPath());
        //root = new DefaultMutableTreeNode("root");
        
        CreateChildNodes ccn = new CreateChildNodes(fileRoot, root);
        //new Thread(ccn).start();
        ccn.run();
        
        
        treeModel = new DefaultTreeModel(root);

        tree = new JTree(treeModel);

        // TODO: fix open the selected urlFile

        tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    if (node == null)
                        return;
                    mySelection = node.getUserObject().toString();
                    TreeNode[] path = node.getPath();
                    String plainPath = toPlainString(path);
                    if (mySelection.endsWith("lts")) {
                        HPWindow.instance.newExample(plainPath, mySelection);
                        frame.setVisible(false);
                        frame.dispose();
                    }
                        
                }
            }
        });

        tree.setShowsRootHandles(true);
        JScrollPane scrollPane = new JScrollPane(tree);

        frame.add(scrollPane);
        frame.setLocationByPlatform(true);
        frame.setSize(640, 480);

        

        frame.setVisible(true);

    }

    private String toPlainString(TreeNode[] path) {
        String res = "";
        for (int i = 0; i < path.length - 1; i++) {
            res = res + String.valueOf(path[i]) + "/";
        }
        return res;
    }

    public class CreateChildNodes implements Runnable {

        private DefaultMutableTreeNode root;

        private URL fileRoot;

        public CreateChildNodes(URL fileRoot, DefaultMutableTreeNode root) {
            this.fileRoot = fileRoot;
            this.root = root;
            
            if (fileRoot.getProtocol() == "jar")
                try {
                    this.fileRoot = this.extractExamples();
                    this.root.setUserObject(this.fileRoot.getPath());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }

        private URL extractExamples() throws IOException {
            JarURLConnection connection = (JarURLConnection) fileRoot.openConnection();
            //String destDir = "UnpackagedExamples";
            String destDir = "examples";
            Path examplesDestination = Paths.get(destDir);
            if(!Files.exists(examplesDestination)) {
                new java.io.File(destDir).mkdir();
                java.util.jar.JarFile jar = connection.getJarFile();
                java.util.Enumeration enumEntries = jar.entries();
                while (enumEntries.hasMoreElements()) {
                    java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();
                    if (file.getName().startsWith(destDir)) {
                        //java.io.File f = new java.io.File(destDir + java.io.File.separator + file.getName());
                        java.io.File f = new java.io.File(file.getName());
                        if (file.isDirectory()) { // if its a directory, create it
                            f.mkdir();
                            continue;
                        } else if (!file.getName().endsWith("lts")) {
                            continue;
                        }
                        f.getParentFile().mkdirs();
                        java.io.InputStream is = jar.getInputStream(file); // get the input stream
                        java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
                        while (is.available() > 0) {  // write contents of 'is' to 'fos'
                            fos.write(is.read());
                        }
                        fos.close();
                        is.close();
                    }
                }
                jar.close();
                
            }
            
            return examplesDestination.toUri().toURL();
        }

        
        @Override
        public void run() {
            try {
                createChildren(fileRoot.getPath(), root);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }    
        }

        private void createChildren(String newFileRoot, DefaultMutableTreeNode node) throws IOException {
            File[] a = new File(newFileRoot).listFiles();
            Arrays.sort(a);
            
            for (File inner : a) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(inner.getName());
                node.add(childNode);
                
                System.out.println(inner);
                if (inner.isDirectory()) {
                    createChildren(inner.getAbsolutePath(), childNode);
                }
            }
        }
    }

    public class FileNode {

        private URL urlFile;

        public FileNode(URL urlFile) {
            this.urlFile = urlFile;
        }

        @Override
        public String toString() {
            String name = urlFile.toExternalForm();
            if (name.equals("")) {
                return urlFile.getPath();
            } else {
                return name;
            }
        }
    }

}