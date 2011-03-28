/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.util.Date;

import com.exedio.cope.util.Day;

final class SimpleSelectType<E> implements SelectType<E>
{
	final Class<E> javaClass;

	private SimpleSelectType(final Class<E> javaClass)
	{
		this.javaClass = javaClass;
		assert javaClass!=null;
	}

	public final Class<E> getJavaClass()
	{
		return javaClass;
	}

	@Override
	public String toString()
	{
		return javaClass.getName();
	}


	static final SimpleSelectType<String > String  = new SimpleSelectType<String >(String .class);
	static final SimpleSelectType<Boolean> Boolean = new SimpleSelectType<Boolean>(Boolean.class);
	static final SimpleSelectType<Integer> Integer = new SimpleSelectType<Integer>(Integer.class);
	static final SimpleSelectType<Long   > Long    = new SimpleSelectType<Long   >(Long   .class);
	static final SimpleSelectType<Double > Double  = new SimpleSelectType<Double >(Double .class);
	static final SimpleSelectType<Date   > Date    = new SimpleSelectType<Date   >(Date   .class);
	static final SimpleSelectType<Day    > Day     = new SimpleSelectType<Day    >(Day    .class);
}
