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
	private volatile State state;

	private static final class State
	{
		private final int initialValue;
		private final int value;
		private final int ramp;
		private static final int MAX_RAMP = 999;

		State(final int value)
		{
			this(value, value, 0);
		}

		private State(final int initialValue, final int value, final int ramp)
		{
			this.initialValue = requireNonNegative(initialValue, "value");
			this.value = requireNonNegative(value, "value");
			this.ramp = ramp;

			assert 0<=ramp && ramp<=MAX_RAMP : ramp;
		}

		State reset()
		{
			return new State(initialValue, initialValue, 0);
		}

		boolean isInitial()
		{
			return
				value==initialValue &&
				ramp==0;
		}

		String getInfo()
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

		State setValueAndResetRamp(final int value)
		{
			return new State(initialValue, value, 0);
		}

		State setRamp(final double ramp)
		{
			if(! (0.0<=ramp && ramp<=1.0) )
				throw new IllegalArgumentException("ramp must be between 0.0 and 1.0, but was " + String.valueOf(ramp));

			int rampInt = (int)Math.round( ramp*(MAX_RAMP+1) );
			if(rampInt>MAX_RAMP)
				rampInt = MAX_RAMP;

			return new State(initialValue, value, rampInt);
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
	}

	public MediaFingerprintOffset(final int value)
	{
		this.state = new State(value);
	}

	public void reset()
	{
		this.state = state.reset();
	}

	/**
	 * Returns true if calling {@link #reset()} would change anything.
	 */
	public boolean isInitial()
	{
		return state.isInitial();
	}

	public String getInfo()
	{
		return state.getInfo();
	}

	public void setValueAndResetRamp(final int value)
	{
		state = state.setValueAndResetRamp(value);
	}

	/**
	 * @param ramp a value between 0.0 and 1.0.
	 */
	public void setRamp(final double ramp)
	{
		state = state.setRamp(ramp);
	}

	int get(final Item item)
	{
		return state.get(item);
	}

	@Override
	public String toString()
	{
		return state.getInfo();
	}
}
