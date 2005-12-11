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

import java.lang.reflect.Modifier;
import java.util.List;


final class CopeMedia extends CopeFeature
{
	public final String mimeMajor;
	public final String mimeMinor;
	public final Option setterOption;

	public CopeMedia(final JavaAttribute javaAttribute, final String docComment)
	{
		super(javaAttribute);
		this.mimeMajor = getString(javaAttribute.getInitializerArguments(), 1);
		this.mimeMinor = getString(javaAttribute.getInitializerArguments(), 2);
		this.setterOption = new Option(Injector.findDocTagLine(docComment, Instrumentor.ATTRIBUTE_SETTER), true);
	}

	private static String getString(final List initializerArguments, final int pos)
	{
		if(initializerArguments.size()>pos)
		{
			final String s = (String)initializerArguments.get(pos);
			if(!s.startsWith("\""))
				return null;
			if(!s.endsWith("\""))
				return null;
			return s.substring(1, s.length()-1);
		}
		else
			return null;
	}
	
	final String getName()
	{
		return name;
	}

	final int getGeneratedGetterModifier()
	{
		return modifier & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE);
	}

	final int getGeneratedSetterModifier()
	{
		return setterOption.getModifier(modifier);
	}
	
}
