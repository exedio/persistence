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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.Condition;
import com.exedio.cope.DataField;
import com.exedio.cope.util.Hex;
import java.util.Arrays;

final class StartsWith
{
	private final byte[] value;

	StartsWith(final byte... value)
	{
		this.value = requireNonNull(value);

		if(value.length<3 || value.length>MAX_LENGTH)
			throw new IllegalArgumentException(toString());
	}

	static final int MAX_LENGTH = 8;

	boolean matches(final byte[] magic)
	{
		final int l = value.length;
		if(magic.length<l)
			return false;

		for(int i = 0; i<l; i++)
			if(value[i]!=magic[i])
				return false;

		return true;
	}

	Condition matchesIfSupported(final DataField field)
	{
		return field.startsWithIfSupported(value);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(this==other)
			return true;
		if(!(other instanceof StartsWith))
			return false;

		final StartsWith o = (StartsWith)other;
		return Arrays.equals(value, o.value);
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode(value);
	}

	@Override
	public String toString()
	{
		return Hex.encodeLower(value);
	}
}
