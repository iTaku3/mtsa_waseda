package ltsa.lts;

import java.math.BigDecimal;



/* -------------------------------------------------------------------------------*/

class Value {
    private BigDecimal val;
    private String sval;
    private boolean sonly;

    public Value(double i) {
    	this(new BigDecimal(i));
    }
    
    public Value(BigDecimal i) {
        val = i;
        sonly = false;
        sval = String.valueOf(i);
    }
    
    public Value(String s) {   //convert string to integer of possible
        sval = s;
        try {
            val = new BigDecimal(s);
            sonly = false;
        } catch (NumberFormatException e) {
            sonly = true;
        }
    }

    public String toString() {
        return sval;
    }
    
    public BigDecimal doubleValue() {
    	return val;
    }

    public boolean isNumeric() {
        return !sonly;
    }

    public boolean isLabel() {
        return sonly;
    }
}