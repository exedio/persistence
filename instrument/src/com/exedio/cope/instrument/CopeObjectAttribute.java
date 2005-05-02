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

import java.util.List;

final class CopeObjectAttribute extends CopeAttribute
{
	public CopeObjectAttribute(
			final JavaAttribute javaAttribute,
			final Class typeClass,
			final List initializerArguments,
			final String setterOption,
			final String getterOption)
		throws InjectorParseException
	{
		super(javaAttribute, typeClass, getPersistentType(initializerArguments), initializerArguments, getterOption, setterOption);
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
