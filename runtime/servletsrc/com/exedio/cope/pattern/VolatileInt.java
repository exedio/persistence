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

package com.exedio.cope.pattern;

import java.io.Serializable;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

final class VolatileInt implements Serializable
{
	private static final long serialVersionUID = 1l;

	@SuppressFBWarnings("VO_VOLATILE_INCREMENT")
	private volatile int value = 0;

	void inc()
	{
		value++;
	}

	void inc(final int addend)
	{
		if(addend<0)
			throw new IllegalArgumentException();

		if(addend>0)
			value += addend;
	}

	int get()
	{
		return value;
	}
}
