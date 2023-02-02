package MTSSynthesis.controller.gr;

import MTSSynthesis.controller.model.Rank;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


public class GRRank implements Rank {

    public static final int DEFAULT_INITIAL_HEIGHT = 0;
    public static final int DEFAULT_INITIAL_WIDTH = 1;

    private volatile int value;
    private volatile int assume;
    private GRRankContext context;

    public GRRank(GRRankContext context) {
        this(context, DEFAULT_INITIAL_HEIGHT, DEFAULT_INITIAL_WIDTH);
        this.context = context;
    }

    public GRRank(GRRankContext context, int value, int assume) {
        this.context = context;
        Validate.isTrue(this.context.inRange(value, assume), "Rank value and/or assume are out of range.");
        this.value = value;
        this.assume = assume;
    }

    public GRRank(GRRank other) {
        this.context = other.getContext();
        this.value = other.getValue();
        this.assume = other.getAssume();
    }

    /* (non-Javadoc)
     * @see controller.game.gr.Rank#increase()
     */
    public void increase() {
        if (this.context.getHeight() == value) {
            return;
        } else if (this.context.getWidth() == assume) {
            value++;
            assume = 1;
        } else {
            assume++;
        }
    }

    public void setToInfinity() {
        value = this.context.getHeight();
        assume = 1;
    }

    public void set(int value, int assumption) {
        Validate.isTrue(this.getContext().inRange(value, assumption), "The rank values are out of range.");
        this.value = value;
        this.assume = assumption;
    }

    public void set(Rank rank) {
        boolean instance = rank instanceof GRRank;
        Validate.isTrue(instance, "Ranks are incompatible. [" + this + ", " + rank + "]");
        GRRank grRank = (GRRank) rank;
            Validate.isTrue(this.getContext().equals(grRank.getContext()), "Rank contexts are incompatible. [" + this + ", " + rank + "]");
            Validate.isTrue(this.getContext().inRange(grRank.getValue(), grRank.getAssume()), "The rank is out of range.");
            this.set(grRank.getValue(), grRank.getAssume());
    }

    /* (non-Javadoc)
     * @see controller.game.gr.Rank#isInfinity()
     */
    public boolean isInfinity() {
        return value == (this.context.getHeight());
    }

    /* (non-Javadoc)
     * @see controller.game.gr.Rank#getValue()
     */
    public int getValue() {
        return value;
    }

    /* (non-Javadoc)
     * @see controller.game.gr.Rank#getAssume()
     */
    public int getAssume() {
        return assume;
    }

    protected GRRankContext getContext() {
        return context;
    }

    /* (non-Javadoc)
     * @see controller.game.gr.Rank#compareTo(controller.game.gr.GRRank)
     */
    @Override
    public int compareTo(Rank other) {
        if (this.equals(other)) {
            return 0;
        }
        boolean instance = other instanceof GRRank;
        Validate.isTrue(instance, "Ranks are not comparable. [" + this + ", " + other + "]");
        GRRank grOther = (GRRank) other;
        Validate.isTrue(this.getContext().equals(grOther.getContext()),
                "Ranks with different contexts cannot be compared." + "[" + this + ", " + grOther + "]");

        if (this.value < grOther.getValue() ||
                (this.value == grOther.getValue() && this.assume < grOther.getAssume())) {
            return -1;
        } else if (this.value > grOther.getValue() ||
                (this.value == grOther.getValue() && this.assume > grOther.getAssume())) {
            return 1;
        } else { // this.equals(other)
            return 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        GRRank grRank = (GRRank) obj;
        return new EqualsBuilder().append(this.value, grRank.getValue()).append(this.assume, grRank.getAssume()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 13).append(this.assume).append(this.value).toHashCode();
    }


    @Override
    public String toString() {
        return "[assume:" + this.assume + ", value:" + this.value + "]";
    }

    public static GRRank getInfinityFor(GRRankContext ctx) {
        GRRank rank = new GRRank(ctx);
        rank.setToInfinity();
        return rank;
    }
}
