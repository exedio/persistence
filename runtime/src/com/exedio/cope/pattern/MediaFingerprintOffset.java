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

import static com.exedio.cope.misc.Check.requireNonNegative;

public final class MediaFingerprintOffset
{
	private final int initialValue;
	private int value;

	public MediaFingerprintOffset(final int value)
	{
		this.initialValue = requireNonNegative(value, "value");
		this.value = initialValue;
	}

	public int getInitialValue()
	{
		return initialValue;
	}

	public int getValue()
	{
		return value;
	}

	public void set(final int value)
	{
		this.value = requireNonNegative(value, "value");
	}
}
