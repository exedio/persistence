/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
import com.exedio.cope.pattern.Qualifier;

final class CopeQualifier extends CopeFeature
{

	public CopeQualifier(final CopeType parent, final JavaAttribute javaAttribute)
		throws InjectorParseException
	{
		super(parent, javaAttribute);
	}
	
	CopeAttribute[] getAttributes() throws InjectorParseException
	{
		final ArrayList<String> attributeList = new ArrayList<String>();
		
		final Qualifier instance = (Qualifier)getInstance();
		for(final FunctionField attributeInstance : instance.getUniqueConstraint().getFields())
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
	
	CopeAttribute getQualifierParent() throws InjectorParseException
	{
		return getAttributes()[0];
	}
	
	CopeAttribute[] getKeyAttributes() throws InjectorParseException
	{
		final CopeAttribute[] uniqueAttributes = getAttributes();
		if(uniqueAttributes.length<2)
			throw new RuntimeException(uniqueAttributes.toString());
		
		final CopeAttribute[] result = new CopeAttribute[uniqueAttributes.length-1];
		for(int i = 0; i<result.length; i++)
			result[i] = uniqueAttributes[i+1];
		return result;
	}

	@Override
	void endBuildStage()
	{
		javaAttribute.file.repository.getCopeType(getQualifierParent().getBoxedType()).
		addQualifier(this);
	}
}
