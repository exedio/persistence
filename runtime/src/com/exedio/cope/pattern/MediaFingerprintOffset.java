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

import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnValue;
import static com.exedio.cope.misc.Check.requireNonNegative;

import com.exedio.cope.Item;

public final class MediaFingerprintOffset
{
	private final int initialValue;
	private int value;
	private int ramp;
	private static final int MAX_RAMP = 999;

	public MediaFingerprintOffset(final int value)
	{
		this.initialValue = requireNonNegative(value, "value");
		reset();
	}

	public void reset()
	{
		this.value = initialValue;
		this.ramp = 0;
	}

	/**
	 * Returns true if calling {@link reset()} would change anything.
	 */
	public boolean isInitial()
	{
		return
			value==initialValue &&
			ramp==0;
	}

	public String getInfo()
	{
		final StringBuilder bf = new StringBuilder();
		bf.append(value);

		if(ramp!=0)
			bf.append(" ramp ").
				append(ramp).
				append('/').
				append(MAX_RAMP+1);

		if(value!=initialValue)
			bf.append(" initially ").
				append(initialValue);

		return bf.toString();
	}

	public void setValueAndResetRamp(final int value)
	{
		requireNonNegative(value, "value");
		this.value = value;
		this.ramp = 0; // TODO do some synchronization
	}

	/**
	 * @param ramp a value between 0.0 and 1.0.
	 */
	public void setRamp(final double ramp)
	{
		if(! (0.0<=ramp && ramp<=1.0) )
			throw new IllegalArgumentException("ramp must be between 0.0 and 1.0, but was " + String.valueOf(ramp));

		int rampInt = (int)Math.round( ramp*(MAX_RAMP+1) );
		if(rampInt>MAX_RAMP)
			rampInt = MAX_RAMP;

		this.ramp = rampInt;
	}

	int get(final Item item)
	{
		if(ramp==0)
			return value;

		return
			(getPrimaryKeyColumnValue(item)<ramp)
			? (value + 1)
			: value;
	}

	@Override
	public String toString()
	{
		return getInfo();
	}
}
