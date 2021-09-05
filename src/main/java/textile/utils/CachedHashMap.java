package textile.utils;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CachedHashMap<K, V> extends HashMap<K, V> {
	private Map<K, V> cache = Map.of();
	
	public void updateCache() {
		Map<K, V> map = new HashMap<>();
		for(K key : keySet()) {
			map.put(key, get(key));
		}
		
		cache = map;
	}
	
	public Map<K, V> getCache() {
		return cache;
	}
}
