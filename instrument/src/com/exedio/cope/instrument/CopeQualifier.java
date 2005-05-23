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

final class CopeQualifier
{
	final String name;
	final String qualifierClassString;

	final CopeClass qualifierClass;
	final CopeUniqueConstraint uniqueConstraint;

	final CopeAttribute[] keyAttributes;

	public CopeQualifier(final String name, final CopeClass copeClass, final List initializerArguments)
		throws InjectorParseException
	{
		this.name = name;
		if(initializerArguments.size()!=1)
			throw new InjectorParseException("Qualifier must have 1 argument, but has "+initializerArguments);
		final String uniqueConstraintString = (String)initializerArguments.get(0);

		final int dot = uniqueConstraintString.lastIndexOf('.');
		if(dot<0)
			throw new InjectorParseException("Qualifier argument must have dot, but is "+uniqueConstraintString);
		this.qualifierClassString = uniqueConstraintString.substring(0, dot);

		//System.out.println("--------- qualifierClassString: "+qualifierClassString);
		//Sstem.out.println("--------- key: "+key);
		//System.out.println("--------- qualifyUnique: "+qualifyUnique);
		this.qualifierClass = copeClass.javaClass.file.repository.getCopeClass(qualifierClassString);
		//System.out.println("--------- qualifierClass: "+qualifierClass.javaClass.name);
		
		final String constraintName = uniqueConstraintString.substring(dot+1);
		//System.out.println("--------- keyString: "+keyString);
		
		this.uniqueConstraint = qualifierClass.getCopeUniqueConstraint(constraintName);
		if(uniqueConstraint==null)
			throw new InjectorParseException("unique constraint not found "+uniqueConstraintString);
		
		final CopeAttribute[] uniqueAttributes = uniqueConstraint.copeAttributes;
		if(uniqueAttributes.length<2)
			throw new RuntimeException(uniqueAttributes.toString());
		
		this.keyAttributes = new CopeAttribute[uniqueAttributes.length-1];
		for(int i = 0; i<this.keyAttributes.length; i++)
			this.keyAttributes[i] = uniqueAttributes[i+1];

		copeClass.add(this);
	}

}
