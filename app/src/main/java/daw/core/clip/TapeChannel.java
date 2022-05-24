package daw.core.clip;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.util.Pair;

/**
 * A {@link RPTapeChannel} implemented with a Map.
 */
public class TapeChannel implements RPTapeChannel {
	
	/**
	 * The clips of this tape channel.
	 */
	private final Map<Double, RPClip<?>> timeline;
	
	/**
	 * Creates an empty tape channel.
	 */
	public TapeChannel() {
		this.timeline = new HashMap<>();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws  IllegalStateException  {@inheritDoc}
	 * 
	 * @throws  IllegalArgumentException  {@inheritDoc}
	 */
	@Override
	public void insertRPClip(RPClip<?> clip, double time) {
		if(this.timeline.containsValue(clip)) {
			throw new IllegalStateException("This clip already exists in this channel");
		}
		if(this.timeline.containsKey(time)) {
			throw new IllegalStateException("Another clip with the same time in already exists. Choose another time or remove the other clip.");
		} else if(time<0) {
			throw new IllegalArgumentException("Time must be zero or a positive value");
		}
		this.clearBetween(time, this.calculateTimeOut(time, clip.getDuration())); 
		this.timeline.put(time, clip);
	}	
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws ClipNotFoundException {@inheritDoc}
	 */
	@Override
	public void removeClip(double clipTimeIn) throws ClipNotFoundException {
		RPClip<?> removed = this.timeline.remove(clipTimeIn);
		if(removed == null) {
			throw new ClipNotFoundException("No clip found at the specified time in");
		}	
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void clearTape() {
		timeline.clear();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return this.timeline.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Pair<Double, RPClip<?>>> getClipAt(double time) {
		return this.timeline.entrySet().stream().filter(x->{
			return (x.getKey()<=time) && (this.calculateTimeOut(x.getKey(), x.getValue().getDuration())>time);
		}).map(x->(new Pair<Double, RPClip<?>>(x.getKey(), x.getValue()))).findFirst();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Pair<Double, RPClip<?>>> getClipWithTimeIterator() {	
		return this.getClipWithTimeIteratorFiltered(x->true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Pair<Double, RPClip<?>>> getClipWithTimeIteratorFiltered(Predicate<? super Entry<Double, RPClip<?>>> predicate) {
		return this.timeline.entrySet().stream()
				.filter(predicate)
				.sorted((x1, x2)->Double.compare(x1.getKey(), x2.getKey()))
				.map(x->(new Pair<Double, RPClip<?>>(x.getKey(), x.getValue())))
				.iterator();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws  ClipNotFoundException {@inheritDoc}
	 */
	@Override
	public void move(double initialClipTimeIn, double finalClipTimeIn) throws ClipNotFoundException {
		RPClip<?> clip = this.timeline.get(initialClipTimeIn);
		if(clip == null) {
			throw new ClipNotFoundException("No clip found at the specified time in");
		}
		this.removeClip(initialClipTimeIn);
		this.insertRPClip(clip, finalClipTimeIn);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws  ClipNotFoundException {@inheritDoc}
	 * 
	 * @throws  IllegalArgumentException {@inheritDoc}
	 */
	@Override
	public void setTimeOut(double initialClipTimeIn, double finalClipTimeOut) throws ClipNotFoundException {
		RPClip<?> clip = this.timeline.get(initialClipTimeIn);
		if(clip == null) {
			throw new ClipNotFoundException("No clip found at the specified time in");
		}
		double newDuration = finalClipTimeOut - initialClipTimeIn;
		if(newDuration>clip.getDuration()) {
			this.clearBetween(this.getClipTimeOut(initialClipTimeIn), finalClipTimeOut);
		}
		clip.setDuration(newDuration);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws ClipNotFoundException  {@inheritDoc}
	 */
	@Override
	public void setTimeIn(double initialClipTimeIn, double finalClipTimeIn) throws ClipNotFoundException {
		RPClip<?> clip = this.timeline.get(initialClipTimeIn);
		if(clip == null) {
			throw new ClipNotFoundException("No clip found at the specified time in");
		}
		double newDuration = this.getClipTimeOut(initialClipTimeIn) - finalClipTimeIn;
		this.removeClip(initialClipTimeIn);
		clip.setContentPosition(clip.getContentPosition()+(clip.getDuration()-newDuration));
		clip.setDuration(newDuration);
		this.insertRPClip(clip, finalClipTimeIn);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws ClipNotFoundException  {@inheritDoc}
	 * 
	 * @throws IllegalArgumentException  {@inheritDoc}
	 */
	@Override
	public void split(double initialClipTimeIn, double splittingTime) throws ClipNotFoundException {
		RPClip<?> clip = this.timeline.get(initialClipTimeIn);
		if(clip == null) {
			throw new ClipNotFoundException("No clip found at the specified time in");
		}
		if(splittingTime >= this.getClipTimeOut(initialClipTimeIn) || splittingTime<= initialClipTimeIn) {
			throw new IllegalArgumentException("splittingTime must be within the clip in/out");
		}
		RPClip<?> duplicate;
		try {
			duplicate = clip.duplicate();
			this.setTimeIn(initialClipTimeIn, splittingTime);
			duplicate.setDuration(splittingTime-initialClipTimeIn);
			this.insertRPClip(duplicate, initialClipTimeIn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws ClipNotFoundException  {@inheritDoc}
	 */
	@Override
	public double getClipTimeOut(double clipTimeIn) throws ClipNotFoundException {
		var clip = this.timeline.get(clipTimeIn);
		if(clip == null) {
			throw new ClipNotFoundException("There's no clip at the specified time in");
		}
		return calculateTimeOut(clipTimeIn, clip.getDuration());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateTimeOut(double timeIn, double duration) {
		return timeIn+duration;
	}
	
	private void clearBetween(double initialTime, double finalTime) {
		var iterator = this.getIteratorOfClipBetween(initialTime, finalTime);
		iterator.forEachRemaining(x->{
			try {
				if(x.getKey()<initialTime) {
					if(this.calculateTimeOut(x.getKey(), x.getValue().getDuration())<=finalTime) {
						//case (in<initialTime and out<=finalTime) A
						this.setTimeOut(x.getKey(), initialTime);
					} else {
						//case (in<initialTime and out>finalTime) D
						this.split(x.getKey(), finalTime);
						this.setTimeOut(this.getClipAt(initialTime).get().getKey(), initialTime);
					}
				} else {
					if(this.calculateTimeOut(x.getKey(), x.getValue().getDuration())<=finalTime) {
						//case (in>=initialTime and out<=finalTime) B
						this.removeClip(x.getKey());
					} else {
						//case (in>=initialTime and out>finalTime) C
						this.setTimeIn(x.getKey(), finalTime);
					}
				}	
			} catch(ClipNotFoundException e) {
				e.printStackTrace();
			}
		});	
	}

	private Iterator<Pair<Double, RPClip<?>>> getIteratorOfClipBetween(double initialTime, double finalTime) {
		return this.getClipWithTimeIteratorFiltered(x->{
			try {
				return x.getKey()<finalTime && this.getClipTimeOut(x.getKey())>initialTime;
			} catch (ClipNotFoundException e) {
				e.printStackTrace();
				return false;
			}
		});
	}
}
