
package com.exedio.cope.instrument;

import java.util.List;

public final class PersistentEnumerationAttribute extends PersistentAttribute
{
	public PersistentEnumerationAttribute(
			final JavaAttribute javaAttribute,
			final List initializerArguments, final boolean mapped,
			final List qualifiers)
	{
		super(javaAttribute, getPersistentType(initializerArguments), initializerArguments, mapped, qualifiers);
	}
	
	public static final String getPersistentType(final List initializerArguments)
	{
		if(initializerArguments.size()<=1)
			throw new RuntimeException("second argument required");
		final String secondArgument =  (String)initializerArguments.get(1);
		if(!secondArgument.endsWith(".class"))
			throw new RuntimeException("second argument must end with .class: \'"+secondArgument+'\'');
		return secondArgument.substring(0, secondArgument.length()-".class".length());
	}

}
