//package FSP2MTS.ac.ic.doc.mtstools.model.impl;
//
//import java.io.*;
//import java.util.Collection;
//import java.util.HashSet;
//import java.util.Set;
//
//import org.apache.commons.io.DirectoryWalker;
//
//import FSP2MTS.ac.ic.doc.mtstools.test.util.LTSATestUtils;
//import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestBase;
//import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestUtils;
//
//public class MTSCompileBookExamplesTest extends MTSTestBase {
//	protected LTSATestUtils ltsaTestUtils;
//	protected MTSTestUtils testUtils;
//	
//	@Override
//	protected void setUp() throws Exception {
//		super.setUp();
//		this.ltsaTestUtils = new LTSATestUtils();
//		this.testUtils = new MTSTestUtils(); 
//	}
//	
//	public void testExample() throws Exception {
//		final Set<File> examples = new HashSet<File>();		
//		getAllExamples(examples);
//		for (File exampleFile : examples) {
//			FileReader fileReader = new FileReader(exampleFile);
//			BufferedReader reader = new BufferedReader(fileReader);
//			StringBuffer fileRep = new StringBuffer();
//			char[] actualChar = new char[2000]; 
//			reader.read(actualChar);
//			fileRep.append(actualChar);
//			System.out.println("Compiling automata: " + exampleFile.getName());
//			assertNotNull(testUtils.buildCompositeState(fileRep.toString(), ltsaTestUtils, ltsOutput));
//		}
//		
//		
//	}
//
//	private void getAllExamples(final Set<File> examples) throws IOException {
//		File file = new File("");
//		String absolutePath = file.getAbsolutePath();
////		String examplesPath = absolutePath.substring(0, absolutePath.indexOf("FSP2MTS")) + "ltsa\\examples\\book";
//		String examplesPath = absolutePath + "\\test\\examples\\";
//		final File examplesRootPathFile = new File(examplesPath);
//		assertTrue(examplesRootPathFile.isDirectory());
//		
//		getAllFiles(examplesRootPathFile, examples);
//	}
//
//	private void getAllFiles(final File example, final Set<File> examples) throws IOException {
//		new DirectoryWalker(){
//			public void testAllExamples() throws IOException {
//				this.walk(example, examples);
//			}
//			@Override
//			protected void handleFile(File file, int depth, Collection results) throws IOException {
//				if (file.getName().endsWith(".lts")) {
//					results.add(file);
//				}
//			}
//		}.testAllExamples();
//	}
//	
//	
//}
