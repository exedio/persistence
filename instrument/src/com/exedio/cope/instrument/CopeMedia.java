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

import java.lang.reflect.Modifier;


final class CopeMedia extends CopeFeature
{

	public CopeMedia(final CopeType parent, final JavaAttribute javaAttribute)
	{
		super(parent, javaAttribute);
	}

	final int getGeneratedGetterModifier()
	{
		return modifier & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE) | Modifier.FINAL;
	}
	
	@Override
	boolean isBoxed()
	{
		return false;
	}
	
	@Override
	String getBoxedType()
	{
		throw new RuntimeException();
	}
}
