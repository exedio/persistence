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

final class DefaultNext extends DefaultSource<Integer>
{
	final int start;
	private SequenceX sequence;

	DefaultNext(final int start)
	{
		this.start = start;
	}

	void set(final SequenceX sequence)
	{
		assert sequence!=null;
		assert this.sequence==null;
		this.sequence = sequence;
	}

	@Override
	Integer make(final long now)
	{
		return sequence.next();
	}

	@Override
	boolean isNext()
	{
		return true;
	}

	@Override
	Integer getNextStart()
	{
		return start;
	}
}
