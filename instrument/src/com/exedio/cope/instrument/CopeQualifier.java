/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

final class CopeQualifier extends CopeFeature
{
	final String qualifierClassString;
	final String uniqueConstraintString;
	final String constraintName;

	public CopeQualifier(final JavaAttribute javaAttribute)
		throws InjectorParseException
	{
		super(javaAttribute);
		
		final List<String> initializerArguments = javaAttribute.getInitializerArguments();
		if(initializerArguments.size()!=1)
			throw new InjectorParseException("Qualifier must have 1 argument, but has "+initializerArguments);
		uniqueConstraintString = initializerArguments.get(0);

		final int dot = uniqueConstraintString.lastIndexOf('.');
		if(dot<0)
			throw new InjectorParseException("Qualifier argument must have dot, but is "+uniqueConstraintString);
		this.qualifierClassString = uniqueConstraintString.substring(0, dot);

		this.constraintName = uniqueConstraintString.substring(dot+1);
	}
	
	CopeType getQualifierClass()
	{
		return type.javaClass.file.repository.getCopeType(qualifierClassString);
	}
	
	CopeUniqueConstraint getUniqueConstraint() throws InjectorParseException
	{
		final CopeUniqueConstraint result = (CopeUniqueConstraint)getQualifierClass().getFeature(constraintName);
		if(result==null)
			throw new InjectorParseException("unique constraint not found "+uniqueConstraintString);
		return result;
	}
	
	CopeAttribute[] getKeyAttributes() throws InjectorParseException
	{
		final CopeAttribute[] uniqueAttributes = getUniqueConstraint().getAttributes();
		if(uniqueAttributes.length<2)
			throw new RuntimeException(uniqueAttributes.toString());
		
		final CopeAttribute[] result = new CopeAttribute[uniqueAttributes.length-1];
		for(int i = 0; i<result.length; i++)
			result[i] = uniqueAttributes[i+1];
		return result;
	}
}
