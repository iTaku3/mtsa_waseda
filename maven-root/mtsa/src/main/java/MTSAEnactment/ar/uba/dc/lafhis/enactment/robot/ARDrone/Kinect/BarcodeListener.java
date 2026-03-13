package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.Kinect;
import java.awt.image.BufferedImage;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.Result;

import de.yadrone.base.video.ImageListener;

public class BarcodeListener implements ImageListener {

	public static String NONE = "NONE";
	private String resultText = NONE;

	@Override
	public void imageUpdated(BufferedImage image) {
		
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		// decode the barcode (if only QR codes are used, the QRCodeReader might be a better choice)
		MultiFormatReader reader = new MultiFormatReader();
		
		try {
			Result result = reader.decode(bitmap);
			if(result != null){
				resultText = result.getText();
			}else{
				resultText = NONE;
			}
			
		} catch (NotFoundException e) {
			//e.printStackTrace();
			resultText = NONE;
		}
		
	}
	
	public String getResult(){
		return resultText;
	}	
}
