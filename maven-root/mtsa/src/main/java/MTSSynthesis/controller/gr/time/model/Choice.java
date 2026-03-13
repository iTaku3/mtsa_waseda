package MTSSynthesis.controller.gr.time.model;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class Choice <A>{
	private Set<A> choice;
	private Set<A> alternative;
	
	public Choice(){
		this.choice = new HashSet<A>();
		this.alternative = new HashSet<A>();
	}
	
	public Choice(Set<A> choice){
		this.choice = choice;
		this.alternative = new HashSet<A>();
	}
	
	public Choice(Set<A> choice, Set<A> alternative){
		this.choice = choice;
		this.alternative = alternative;
	}
	
	public Set<A> getChoice(){
		return this.choice;
	}
	
	public Set<A> getAlternative(){
		return this.alternative;
	}

	public boolean hasAlternative() {
		return !this.alternative.isEmpty();
	}
	
	public boolean hasChoice(){
		return !this.choice.isEmpty();
	}

	public Set<A> getAvailableLabels() {
		Set<A> res =  new HashSet<A>();
		res.addAll(choice);
		res.addAll(alternative);
		return res;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Choice<?>){
			Choice<?> other = (Choice<?>) obj;
			return this.getChoice().equals(other.getChoice()) &&
					this.getAlternative().equals(other.getAlternative()); 
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return	new HashCodeBuilder()
				.append(this.getChoice())
				.append(this.getAlternative())
				.toHashCode();
	}
	
	@Override
	public String toString() {
		return "Choice: " + this.choice.toString() + ", Alternative: " + this.alternative.toString();
	}
	
}
