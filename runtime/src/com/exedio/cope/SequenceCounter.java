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

package com.exedio.cope;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.util.Objects.requireNonNull;

final class SequenceCounter
{
	private final Feature feature;
	private final int start;
	private final int minimum;
	private final int maximum;

	SequenceCounter(final Feature feature, final int start, final int minimum, final int maximum)
	{
		if(start<minimum || start>maximum)
			throw new IllegalArgumentException(String.valueOf(start) + '/' + String.valueOf(minimum) + '/' + String.valueOf(maximum));

		this.feature = requireNonNull(feature);
		this.start = start;
		this.minimum = minimum;
		this.maximum = maximum;
	}

	private volatile int count = 0;
	private volatile int first = MAX_VALUE;
	private volatile int last  = MIN_VALUE;

	void next(final int result)
	{
		if(result<minimum || result>maximum)
			throw new IllegalStateException(
					"sequence overflow to " + result + " in " + feature +
					" limited to " + minimum + ',' + maximum);
		if((count++)==0)
			first = result;
		last = result;
	}

	void flush()
	{
		count = 0;
		first = MAX_VALUE;
		last  = MIN_VALUE;
	}

	SequenceInfo getInfo()
	{
		final int count = this.count;
		final int first = this.first;
		final int last  = this.last;
		return
			count!=0 && first!=MAX_VALUE && last!=MIN_VALUE
			? new SequenceInfo(feature, start, minimum, maximum, count, first, last)
			: new SequenceInfo(feature, start, minimum, maximum);
	}
}
