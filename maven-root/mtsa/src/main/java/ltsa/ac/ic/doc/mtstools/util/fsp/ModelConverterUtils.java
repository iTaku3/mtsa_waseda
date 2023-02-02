package ltsa.ac.ic.doc.mtstools.util.fsp;

import java.util.HashMap;
import java.util.Map;

/**
 * @author srdipi
 *
 */ 
public class ModelConverterUtils {

	private Map<byte[],Long> rankCache = new HashMap<byte[],Long>();

	private Map<Long,byte[]> unrankCache = new HashMap<Long,byte[]>();
	
	Long rank(byte[] code) {
		Long result = this.rankCache.get(code);
		if (result!=null) {
			return result;
		}
		
		int x = 0;
		for (int i = 3; i >= 0; --i) {
			x |= (int) (code[i]) & 0xFF;
			if (i > 0)
				x = x << 8;
		}
		result = (long)x;
		this.rankCache.put(code,result);
		this.unrankCache.put(result,code);
		return result;
	}

	byte[] unrank(Long code) {
		return this.unrankCache.get(code);
	}
}
