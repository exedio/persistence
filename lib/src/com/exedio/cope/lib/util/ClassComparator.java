package com.exedio.cope.lib.util;

import java.util.Comparator;

public final class ClassComparator implements Comparator
{
	private static final ClassComparator instance=new ClassComparator();

	public static final ClassComparator getInstance()
	{
		return instance;
	}

	private ClassComparator()
	{
	}

	public int compare(Object o1, Object o2)
	{
		final Class c1 = (Class)o1;
		final Class c2 = (Class)o2;
		return c1.getName().compareTo(c2.getName());
	}	
}
