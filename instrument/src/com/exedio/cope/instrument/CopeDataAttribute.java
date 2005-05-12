/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.instrument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class CopeDataAttribute extends CopeAttribute
{
	private final List mediaVariants;
	public final String mimeMajor;
	public final String mimeMinor;

	public CopeDataAttribute(
			final JavaAttribute javaAttribute,
			final Class typeClass,
			final List initializerArguments,
			final String setterOption,
			final String getterOption)
		throws InjectorParseException
	{
		super(javaAttribute, typeClass, MEDIA_TYPE, initializerArguments, getterOption, setterOption);
		this.mediaVariants = new ArrayList();

		this.mimeMajor = getString(initializerArguments, 1);
		this.mimeMinor = getString(initializerArguments, 2);
	}
	
	void addVariant(final CopeDataVariant variant)
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
