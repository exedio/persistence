/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.FunctionField;
import com.exedio.cope.UniqueConstraint;


/**
 * For constraints covering more than one attribute only.
 */
final class CopeUniqueConstraint extends CopeFeature
{
	CopeUniqueConstraint(final CopeType parent, final JavaAttribute javaAttribute)
	{
		super(parent, javaAttribute);
	}
	
	CopeAttribute[] getAttributes() throws InjectorParseException
	{
		final ArrayList<String> attributeList = new ArrayList<String>();
		
		final UniqueConstraint instance = (UniqueConstraint)getInstance();
		for(final FunctionField attributeInstance : instance.getFields())
			attributeList.add(javaAttribute.parent.getAttributeByInstance(attributeInstance).name);
		
		final String[] attributes = attributeList.toArray(new String[attributeList.size()]);
		
		final CopeAttribute[] result = new CopeAttribute[attributes.length];
		for(int i = 0; i<attributes.length; i++ )
		{
			final CopeFeature feature = parent.getFeature(attributes[i]);
			if(feature==null)
				throw new InjectorParseException("attribute >"+attributes[i]+"< in unique constraint "+name+" not found.");
			if(!(feature instanceof CopeAttribute))
				throw new InjectorParseException("attribute >"+attributes[i]+"< in unique constraint "+name+" is not an attribute, but "+feature.getClass().getName());
			final CopeAttribute attribute = (CopeAttribute)feature;
			result[i] = attribute;
		}
		return result;
	}
}
