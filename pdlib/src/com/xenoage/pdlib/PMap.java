package com.xenoage.pdlib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import pcollections.HashPMap;
import pcollections.HashTreePMap;


/**
 * Persistent hash map.
 * 
 * This map contains efficient producers to create modified
 * versions of this one.
 * 
 * Deprecated annotations are used to warn the programmer of calling
 * unsupported methods.
 * 
 * @author Andreas Wenger
 */
public final class PMap<K, V>
	implements Map<K, V>
{
	
	private final HashPMap<K, V> data;
	
	//cache for sorted keys
	private PVector<K> keysSorted = null;
	
	
	public PMap(Map<? extends K, ? extends V> map)
	{
		this.data = HashTreePMap.from(map);
	}
	
	
	public PMap()
	{
		this.data = HashTreePMap.empty();
	}
	
	
	private PMap(HashPMap<K, V> data)
	{
		this.data = data;
	}
	
	
	@Deprecated @Override public V put(K key, V value)
	{
		throw new UnsupportedOperationException("Use plus method instead");
	}
	
	
	public PMap<K, V> plus(K key, V value)
	{
		return new PMap<K, V>(data.plus(key, value));
	}
	
	
	@Deprecated @Override public void putAll(Map<? extends K, ? extends V> map)
	{
		throw new UnsupportedOperationException("Use plusAll method instead");
	}
	
	
	public PMap<K, V> plusAll(Map<? extends K, ? extends V> map)
	{
		return new PMap<K, V>(data.plusAll(map));
	}
	

	@Deprecated @Override public void clear()
	{
		throw new UnsupportedOperationException("Create an empty instance instead");
	}

	
	@Override public boolean containsKey(Object key)
	{
		return data.containsKey(key);
	}
	
	
	@Override public boolean containsValue(Object value)
	{
		for (V v : data.values())
		{
			if (v.equals(value))
				return true;
		}
		return false;
	}
	
	
	@Override public V get(Object key)
	{
		return data.get(key);
	}

	
	@Override public boolean isEmpty()
	{
		return data.isEmpty();
	}

	
	@Deprecated @Override public V remove(Object key)
	{
		throw new UnsupportedOperationException("Use minus method instead");
	}
	
	
	public PMap<K, V> minus(Object key)
	{
		return new PMap<K, V>(data.minus(key));
	}
	
	
	public PMap<K, V> minusAll(Collection<?> keys)
	{
		return new PMap<K, V>(data.minusAll(keys));
	}

	
	@Override public int size()
	{
		return data.size();
	}
	
	
	@Override public Set<java.util.Map.Entry<K, V>> entrySet()
	{
		return data.entrySet();
	}


	@Override public Set<K> keySet()
	{
		return data.keySet();
	}
	
	
	/**
	 * Gets a sorted list of all keys.
	 * This may be slow when called the first time. From the second time on,
	 * it is very fast since the list is internally cached.
	 */
	public PVector<K> keySortedList(Comparator<K> comparator)
	{
		if (keysSorted == null)
		{
			ArrayList<K> keys = new ArrayList<K>(data.keySet());
			Collections.sort(keys, comparator);
			keysSorted = new PVector<K>(keys);
		}
		return keysSorted;
	}


	@Override public Collection<V> values()
	{
		return data.values();
	}
	
	
	/**
	 * Returns true, if the given collection has the same values as this one,
	 * otherwise false.
	 */
	@Override public boolean equals(Object o)
	{
		return data.equals(o);
	}
	
	
	@Override public int hashCode()
	{
		return data.hashCode();
	}
	
	
	@Override public String toString()
	{
		return data.toString();
	}


}
