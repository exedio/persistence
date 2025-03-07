package com.exedio.cope;

import java.util.Arrays;

/**
 * reduced list-like collection
 */
final class IdentityList<T>
{
	private static final int DEFAULT_CAPACITY = 5;

	private int size;
	private Object[] elementData = new Object[DEFAULT_CAPACITY];

	boolean contains(final T e)
	{
		for (int i=0; i<size; i++)
		{
			if (elementData[i]==e)
				return true;
		}
		return false;
	}

	void add(final T e)
	{
		if (elementData.length==size)
		{
			elementData = Arrays.copyOf(elementData, elementData.length * 2);
		}
		elementData[size] = e;
		size++;
	}
}
