package ltsa.lts.util;

import java.math.BigDecimal;

import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import ltsa.lts.Symbol;

public class LTSUtils {

	public static boolean isOrSymbol(Symbol current) {
		return current.kind == Symbol.OR
			&& current.kind !=Symbol.PLUS_CA
			&& current.kind !=Symbol.PLUS_CR
			&& current.kind !=Symbol.MERGE;
	}

	public static boolean isCompositionExpression(Symbol current) {
		return current.kind == Symbol.OR
			|| current.kind == Symbol.PLUS_CA
			|| current.kind == Symbol.PLUS_CR
			|| current.kind == Symbol.MERGE;
	}

	public static int[] myclone(int[] x) {
		int[] tmp = new int[x.length];
		for (int i = 0; i < x.length; i++)
			tmp[i] = x[i];
		return tmp;
	}

	public static double[] myclone(double[] x) {
		double[] tmp = new double[x.length];
		for (int i = 0; i < x.length; i++)
			tmp[i] = x[i];
		return tmp;
	}

	public static BigDecimal[] myclone(BigDecimal[] x) {
		BigDecimal[] tmp = new BigDecimal[x.length];
		for (int i = 0; i < x.length; i++) {
			// BigDecimal is immutable
			tmp[i] = x[i];
		}
		return tmp;
	}

	public static boolean isInteger(BigDecimal value) {
		if (value.scale() <= 0)
			return true;
		else {
			try {
				value.toBigIntegerExact();
				return true;
			} catch (ArithmeticException e) {
				return false;
			}
		}
	}

	public static <S, A> LTS<S, A> toLts(MTS<S, A> mts) {
		return new LTSAdapter<S, A>(mts, MTS.TransitionType.REQUIRED);
	}
}
