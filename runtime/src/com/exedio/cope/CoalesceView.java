/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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
	@SuppressWarnings("unchecked")
	public static <E> CoalesceView<E> coalesce(final Function<E> parameter1, final E literal)
	{
		if(literal==null)
			throw new NullPointerException("literal");

		return new CoalesceView<E>(new Function[]{parameter1}, literal);
	}

	@SuppressWarnings("unchecked")
	public static <E> CoalesceView<E> coalesce(final Function<E> parameter1, final Function<E> parameter2, final E literal)
	{
		if(literal==null)
			throw new NullPointerException("literal");

		return new CoalesceView<E>(new Function[]{parameter1, parameter2}, literal);
	}

	@SuppressWarnings("unchecked")
	public static <E> CoalesceView<E> coalesce(final Function<E> parameter1, final Function<E> parameter2)
	{
		return new CoalesceView<E>(new Function[]{parameter1, parameter2}, null);
	}


	private static final long serialVersionUID = 1l;

	@edu.umd.cs.findbugs.annotations.SuppressWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final Function<E>[] parameters;
	private final E literal;

	private CoalesceView(final Function<E>[] parameters, final E literal)
	{
		super(parameters, "coalesce", parameters[0].getValueClass());
		this.parameters = parameters;
		this.literal = literal;
	}

	public SelectType<E> getValueType()
	{
		return parameters[0].getValueType();
	}

	@Override
	@SuppressWarnings("unchecked")
	public final E mapJava(final Object[] sourceValues)
	{
		for(final Object sourceValue : sourceValues)
			if(sourceValue!=null)
				return (E)sourceValue;
		return literal;
	}

	@Override
	void toStringNotMounted(final StringBuilder bf, final Type<?> defaultType)
	{
		bf.append("coalesce(");
		boolean first = true;
		for(final Function<E> parameter : parameters)
		{
			if(first)
				first = false;
			else
				bf.append(',');

			parameter.toString(bf, defaultType);
		}
		if(literal!=null)
		{
			bf.append(',');
			bf.append(literal);
		}
		bf.append(')');
	}

	@Deprecated // OK: for internal use within COPE only
	public final void append(final Statement bf, final Join join)
	{
		bf.append("coalesce(");
		boolean first = true;
		for(final Function<E> parameter : parameters)
		{
			if(first)
				first = false;
			else
				bf.append(',');

			bf.append(parameter, join);
		}
		if(literal!=null)
		{
			bf.append(',');
			bf.appendParameterAny(literal);
		}
		bf.append(')');
	}
}
