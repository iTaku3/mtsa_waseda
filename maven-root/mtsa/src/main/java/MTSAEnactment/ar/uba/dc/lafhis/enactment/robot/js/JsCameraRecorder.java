package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.js.JsCamera.ImageReceivedEvent;

/**
 * Writes images from the {@link JsCamera camera} to files.
 * 
 * @author Timo G&uuml;nther
 */
public class JsCameraRecorder implements JsCamera.Listener {

	/** Directory to store frames from the drone's video stream in. */
	private File outDir;

	/**
	 * Constructs a new instance of this class.
	 * @param outDir output directory
	 */
	public JsCameraRecorder(File outDir) {
		setOutDir(outDir);
	}

	@Override
	public void onImageReceived(ImageReceivedEvent e) {
		try {
			Files.write(Paths.get(outDir.getPath(), String.format("%d.jpg", e.getTime())), e.getImageData());
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Sets the output directory.
	 * @param outDir output directory
	 */
	public void setOutDir(File outDir) {
		outDir.mkdirs();
		this.outDir = outDir;
	}
}
