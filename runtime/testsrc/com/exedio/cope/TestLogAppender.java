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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class TestLogAppender extends AppenderSkeleton
{
	private final List<LoggingEvent> events = new ArrayList<>();

	@Override
	protected void append(final LoggingEvent event)
	{
		events.add( event );
	}

	public boolean requiresLayout()
	{
		return false;
	}

	public void assertInfo(final String msg)
	{
		assertMessage(Level.INFO, msg);
	}

	public void assertWarn(final String msg)
	{
		assertMessage(Level.WARN, msg);
	}

	public void assertError(final String msg)
	{
		assertMessage(Level.ERROR, msg);
	}

	public void assertMessage(final Level level, final String msg)
	{
		assertTrue("empty", !events.isEmpty());
		final LoggingEvent event = events.remove(0);
		assertEquals(level, event.getLevel());
		assertEquals(msg, event.getRenderedMessage());
	}

	public void assertEmpty()
	{
		assertEquals(Collections.EMPTY_LIST, events);
	}

	public void close() throws SecurityException
	{
		throw new RuntimeException();
	}
}
