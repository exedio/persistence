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

public final class CoalesceView<E> extends View<E>
{
	public static <E> CoalesceView<E> coalesce(final Function<E> parameter1, final E parameter2)
	{
		return new CoalesceView<E>(parameter1, parameter2);
	}


	private static final long serialVersionUID = 1l;

	private final Function<E> parameter1;
	private final E parameter2;

	@SuppressWarnings("unchecked")
	private CoalesceView(final Function<E> parameter1, final E parameter2)
	{
		super(new Function[]{parameter1}, "coalesce", parameter1.getValueClass());
		this.parameter1 = parameter1;
		this.parameter2 = parameter2;

		if(parameter2==null)
			throw new NullPointerException("parameter2");
	}

	public SelectType<E> getValueType()
	{
		return parameter1.getValueType();
	}

	@Override
	@SuppressWarnings("unchecked")
	public final E mapJava(final Object[] sourceValues)
	{
		final Object sourceValue = sourceValues[0];
		if(sourceValue!=null)
			return (E)sourceValue;
		return parameter2;
	}

	@Override
	void toStringNotMounted(final StringBuilder bf, final Type defaultType)
	{
		bf.append("coalesce(");
		parameter1.toString(bf, defaultType);
		bf.append(',');
		bf.append(parameter2);
		bf.append(')');
	}

	@Deprecated // OK: for internal use within COPE only
	public final void append(final Statement bf, final Join join)
	{
		bf.append("coalesce(");
		bf.append(parameter1, join);
		bf.append(',');
		bf.appendParameterAny(parameter2);
		bf.append(')');
	}
}
