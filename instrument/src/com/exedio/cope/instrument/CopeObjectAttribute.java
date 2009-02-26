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

final class CopeObjectAttribute extends CopeAttribute
{
	
	public CopeObjectAttribute(
			final CopeType parent,
			final JavaAttribute javaAttribute)
		throws InjectorParseException
	{
		super(parent, javaAttribute, getPersistentType(javaAttribute));
	}
	
	private static final String getPersistentType(final JavaAttribute javaAttribute)
	{
		final String type = javaAttribute.type;
		final int lt = type.indexOf('<');
		if(lt<0)
			throw new RuntimeException("type " + type + " does not contain '<'");
		final int gt = type.indexOf('>', lt);
		if(gt<0)
			throw new RuntimeException("type " + type + " does not contain '<'");
		
		return type.substring(lt+1, gt);
	}

}
