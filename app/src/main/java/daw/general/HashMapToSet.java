package daw.general;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HashMapToSet<X, Y> implements MapToSet<X, Y> {
	
	private final Map<X,Set<Y>> map = new HashMap<>();

	@Override
	public boolean put(X key, Y value) {
		if(!map.containsKey(key)) {
			map.put(key, new HashSet<Y>());
		}
		return map.get(key).add(value);
	}

	@Override
	public boolean remove(X key, Y value) {
		if(map.containsKey(key)) {
			var removed = map.get(key).remove(value);
			if(map.get(key).isEmpty() && removed) {
				return this.removeSet(key) != null;
			}
			return removed;
		} 
		return false;
	}

	@Override
	public Set<Y> removeSet(X Key) {
		 return map.remove(Key);
	}

	@Override
	public Set<Y> get(X Key) {
		return map.get(Key);
	}

	@Override
	public boolean containsKey(X time) {
		return this.map.containsKey(time);
	}
	
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	@Override
	public Set<Entry<X,Set<Y>>> entrySet() {
		return this.map.entrySet();
	}
	
}
