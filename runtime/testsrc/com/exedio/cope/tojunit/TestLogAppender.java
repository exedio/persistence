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

package com.exedio.cope.tojunit;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class TestLogAppender extends AppenderSkeleton implements TestRule
{
	private final Logger logger;

	public TestLogAppender(final Logger logger)
	{
		this.logger = requireNonNull(logger, "logger");
	}

	public final Statement apply(final Statement base, final Description description)
	{
		final Logger logger = this.logger; // avoid synthetic-access warning
		return new Statement()
		{
			@Override
			public void evaluate() throws Throwable
			{
				logger.addAppender(TestLogAppender.this);
				try
				{
					base.evaluate();
				}
				finally
				{
					logger.removeAppender(TestLogAppender.this);
				}
			}
		};
	}

	private final List<LoggingEvent> events = new ArrayList<>();

	protected boolean filter(@SuppressWarnings("unused") final String msg)
	{
		return true;
	}

	@Override
	protected final void append(final LoggingEvent event)
	{
		if(filter((String)event.getMessage()))
			events.add( event );
	}

	public final boolean requiresLayout()
	{
		return false;
	}

	public final void assertInfo(final String msg)
	{
		assertMessage(Level.INFO, msg);
	}

	public final void assertWarn(final String msg)
	{
		assertMessage(Level.WARN, msg);
	}

	public final void assertError(final String msg)
	{
		assertMessage(Level.ERROR, msg);
	}

	public final void assertMessage(final Level level, final String msg)
	{
		assertTrue("empty", !events.isEmpty());
		final LoggingEvent event = events.remove(0);
		assertEquals(level, event.getLevel());
		assertEquals(msg, event.getRenderedMessage());
	}

	public final void assertMessageMs(final Level level, final String msg)
	{
		assertTrue("empty", !events.isEmpty());
		final LoggingEvent event = events.remove(0);
		assertEquals(level, event.getLevel());
		assertEquals(msg, event.getRenderedMessage().replaceAll("[0-9]ms", "XXms"));
	}

	public final void assertEmpty()
	{
		assertEquals(Collections.EMPTY_LIST, events);
	}

	public final void close() throws SecurityException
	{
		throw new RuntimeException();
	}
}
