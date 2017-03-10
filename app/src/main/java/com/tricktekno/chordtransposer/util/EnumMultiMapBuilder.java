package com.tricktekno.chordtransposer.util;

import java.util.EnumMap;
import java.util.List;
import java.util.Arrays;

/**
 * convenience class for creating EnumMaps whose values are lists.
 * @author nolan
 *
 */
public class EnumMultiMapBuilder<E extends Enum<E>,T> {

	private EnumMap<E, List<T>> map;
	
	public EnumMultiMapBuilder(Class<E> clazz) {
		map = new EnumMap<E,List<T>>(clazz);
	}
	
	public EnumMultiMapBuilder<E,T> put(E key, T... values) {
		map.put(key, Arrays.asList(values));
		return this;
	}
	
	public EnumMap<E,List<T>> build() {
		return map;
	}
}
