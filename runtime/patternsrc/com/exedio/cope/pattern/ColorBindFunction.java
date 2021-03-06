/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.ColorField.toRGB;

import com.exedio.cope.Condition;
import com.exedio.cope.Join;
import com.exedio.cope.NumberFunction;
import java.awt.Color;

final class ColorBindFunction implements ColorFunction
{
	final NumberFunction<Integer> integer;

	ColorBindFunction(final ColorField function, final Join join)
	{
		integer = function.getRGB().bind(join);
	}

	@Override
	public Condition isNull()
	{
		return integer.isNull();
	}

	@Override
	public Condition isNotNull()
	{
		return integer.isNotNull();
	}

	@Override
	public Condition equal(final Color value)
	{
		return integer.equal(store(value));
	}

	@Override
	public Condition notEqual(final Color value)
	{
		return integer.notEqual(store(value));
	}

	private static Integer store(final Color c)
	{
		return c!=null ? toRGB(c) : null;
	}

	@Override
	public String toString()
	{
		return integer.toString();
	}
}
