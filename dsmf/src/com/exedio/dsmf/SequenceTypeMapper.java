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

package com.exedio.dsmf;

import static java.util.Objects.requireNonNull;

final class SequenceTypeMapper
{
	private final String bit31;
	private final String bit63;

	SequenceTypeMapper(final String bit31, final String bit63)
	{
		this.bit31 = requireNonNull(bit31);
		this.bit63 = requireNonNull(bit63);
	}

	String map(final Sequence.Type type)
	{
		switch(type)
		{
			case bit31: return bit31;
			case bit63: return bit63;
			default:
				throw new RuntimeException("" + type);
		}
	}

	Sequence.Type unmap(final String string, final String message)
	{
		if(string.equals(bit31))
			return Sequence.Type.bit31;
		else if(string.equals(bit63))
			return Sequence.Type.bit63;
		else
			throw new IllegalArgumentException(string + '/' + message);
	}
}
