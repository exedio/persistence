
package com.exedio.cope.instrument;

import java.util.Collections;
import java.util.List;

public final class PersistentMediaAttribute extends PersistentAttribute
{
	public final List mediaVariants;
	public final String mimeMajor;
	public final String mimeMinor;

	public PersistentMediaAttribute(
			final JavaAttribute javaAttribute,
			final List initializerArguments, final boolean mapped,
			final List qualifiers, final List mediaVariants)
		throws InjectorParseException
	{
		super(javaAttribute, MEDIA_TYPE, initializerArguments, mapped, qualifiers);
		this.mediaVariants = (mediaVariants!=null) ? Collections.unmodifiableList(mediaVariants) : null;

		this.mimeMajor = getString(initializerArguments, 1);
		this.mimeMinor = getString(initializerArguments, 2);
	}

	public static String getString(final List initializerArguments, final int pos)
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
