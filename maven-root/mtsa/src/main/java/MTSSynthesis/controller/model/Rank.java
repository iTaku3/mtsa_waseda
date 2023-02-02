package MTSSynthesis.controller.model;


public interface Rank extends Comparable<Rank> {
	public abstract boolean isInfinity();
	public abstract void increase();
	public abstract void setToInfinity();
	public abstract void set(Rank rank);
}