
package com.exedio.cope.instrument;

import java.util.List;

final class CopeObjectAttribute extends CopeAttribute
{
	public CopeObjectAttribute(
			final JavaAttribute javaAttribute,
			final Class typeClass,
			final List initializerArguments,
			final String setterOptionString)
		throws InjectorParseException
	{
		super(javaAttribute, typeClass, getPersistentType(initializerArguments), initializerArguments, setterOptionString);
	}
	
	private static final String getPersistentType(final List initializerArguments)
	{
		if(initializerArguments.size()<=1)
			throw new RuntimeException("second argument required");
		final String secondArgument =  (String)initializerArguments.get(1);
		if(!secondArgument.endsWith(".class"))
			throw new RuntimeException("second argument must end with .class: \'"+secondArgument+'\'');
		return secondArgument.substring(0, secondArgument.length()-".class".length());
	}

}
