
package com.exedio.cope.instrument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class PersistentMediaAttribute extends PersistentAttribute
{
	private final List mediaVariants;
	public final String mimeMajor;
	public final String mimeMinor;

	public PersistentMediaAttribute(
			final JavaAttribute javaAttribute,
			final Class typeClass,
			final List initializerArguments,
			final String setterOptionString,
			final List qualifiers)
		throws InjectorParseException
	{
		super(javaAttribute, typeClass, MEDIA_TYPE, initializerArguments, setterOptionString, qualifiers);
		this.mediaVariants = new ArrayList();

		this.mimeMajor = getString(initializerArguments, 1);
		this.mimeMinor = getString(initializerArguments, 2);
	}
	
	void addVariant(final PersistentMediaVariant variant)
	{
		mediaVariants.add(variant);
	}
	
	List getVariants()
	{
		return Collections.unmodifiableList(mediaVariants);
	}

	private static String getString(final List initializerArguments, final int pos)
		throws InjectorParseException
	{
		if(initializerArguments.size()>pos)
		{
			final String s = (String)initializerArguments.get(pos);
			if(!s.startsWith("\""))
				throw new InjectorParseException(">"+s+"<");
			if(!s.endsWith("\""))
				throw new InjectorParseException(">"+s+"<");
			return s.substring(1, s.length()-1);
		}
		else
			return null;
	}

}
