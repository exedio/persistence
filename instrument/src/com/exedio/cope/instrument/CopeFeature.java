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

import com.exedio.cope.Feature;


class CopeFeature
{
	private final JavaAttribute javaAttribute;
	final String name;
	final int modifier;
	final int accessModifier;
	// TODO rename to parent
	final CopeType type;
	private Feature value;
	
	CopeFeature(final CopeType parent, final JavaAttribute javaAttribute, final String name)
	{
		this.javaAttribute = javaAttribute;
		this.name = name;
		this.modifier = javaAttribute.modifier;
		this.accessModifier = javaAttribute.getAccessModifier();
		this.type = parent;
		type.register(this);
	}
	
	CopeFeature(final CopeType type, final JavaAttribute javaAttribute)
	{
		this(type, javaAttribute, javaAttribute.name);
	}
	
	final JavaClass getParent()
	{
		return javaAttribute.parent;
	}
	
	final Feature getInstance()
	{
		if(value==null)
			value = (Feature)javaAttribute.evaluate();
		
		return value;
	}
	
}
