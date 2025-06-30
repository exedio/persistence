package com.exedio.cope.pattern;

import com.exedio.cope.FunctionField;

@SuppressWarnings("unused") // OK: Methods are tested on implementation classes but never used as a member of this interface.
public interface EnumMapFieldInterface<K extends Enum<K>, V> extends MapFieldInterface<K, V>
{
	FunctionField<V> getField(final K key);
}
