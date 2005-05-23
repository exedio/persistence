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
package com.exedio.cope;


public final class DataAttributeVariant extends TypeComponent
{
	final DataAttribute attribute;

	/**
	 * @see Item#dataAttributeVariant(DataAttribute)
	 */
	DataAttributeVariant(final DataAttribute attribute)
	{
		this.attribute = attribute;
		attribute.addVariant(this);
	}
	
	public DataAttribute getAttribute()
	{
		return attribute;
	}

	// second initialization phase ---------------------------------------------------

	public final void initialize(final Type type, final String name)
	{
		super.initialize(type, makeName(attribute.getName(), name));
	}
	
	private static final String makeName(final String prefix, final String name)
	{
		if(name.startsWith(prefix))
		{
			final int prefixLength = prefix.length();
			final char start = name.charAt(prefixLength);
			if(Character.isUpperCase(start) &&
					(name.length()<=(prefixLength+1) || Character.isLowerCase(name.charAt(prefixLength+1))))
				return Character.toLowerCase(start) + name.substring(prefixLength+1);
			else
				return name.substring(prefixLength);
		}
		else
			return name;
	}
	
}
