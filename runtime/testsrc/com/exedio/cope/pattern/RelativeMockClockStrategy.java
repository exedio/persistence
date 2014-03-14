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

import com.exedio.cope.util.Clock;
import java.util.Collections;
import java.util.LinkedList;
import org.junit.Assert;

final class RelativeMockClockStrategy implements Clock.Strategy
{
	private final LinkedList<Long> events = new LinkedList<Long>();
	private long date = 1000l*60*60*24*1000;

	@Override
	public long currentTimeMillis()
	{
		Assert.assertFalse("no pending clock events", events.isEmpty());
		return events.removeFirst();
	}

	public long addNow()
	{
		return addOffset(0);
	}

	public long addOffset(final long date)
	{
		this.date += date;
		events.add(this.date);
		return this.date;
	}

	public void assertEmpty()
	{
		Assert.assertEquals("pending clock events", Collections.EMPTY_LIST, events);
	}
}
