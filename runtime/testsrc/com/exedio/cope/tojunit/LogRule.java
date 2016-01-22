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
import org.junit.rules.ExternalResource;

public class LogRule extends ExternalResource
{
	private final Logger logger;

	public LogRule(final Logger logger)
	{
		this.logger = requireNonNull(logger, "logger");
	}

	public LogRule(final Class<?> clazz)
	{
		this(Logger.getLogger(requireNonNull(clazz, "clazz")));
	}

	public LogRule(final String name)
	{
		this(Logger.getLogger(requireNonNull(name, "name")));
	}


	public final void setLevelDebug()
	{
		before.assertCalled();
		setLevel(Level.DEBUG);
	}

	private void setLevel(final Level level)
	{
		before.assertCalled();

		if(levelBefore==null)
			levelBefore = logger.getLevel();

		logger.setLevel(level);
	}


	private final BeforeCall before = new BeforeCall();
	private Level levelBefore = null;

	@Override
	protected final void before()
	{
		before.onCall();
		logger.addAppender(appender);
	}

	@Override
	protected final void after()
	{
		if(levelBefore!=null)
			logger.setLevel(levelBefore);

		logger.removeAppender(appender);
	}


	private final List<LoggingEvent> events = new ArrayList<>();

	protected boolean filter(@SuppressWarnings("unused") final String msg)
	{
		return true;
	}

	public final void assertDebug(final String msg)
	{
		before.assertCalled();
		assertMessage(Level.DEBUG, msg);
	}

	public final void assertInfo(final String msg)
	{
		before.assertCalled();
		assertMessage(Level.INFO, msg);
	}

	public final void assertInfoWithoutMilliseconds(final String msg)
	{
		before.assertCalled();
		assertMessageWithoutMilliseconds(Level.INFO, msg);
	}

	public final void assertWarn(final String msg)
	{
		before.assertCalled();
		assertMessage(Level.WARN, msg);
	}

	public final void assertError(final String msg)
	{
		before.assertCalled();
		assertMessage(Level.ERROR, msg);
	}

	private final void assertMessage(final Level level, final String msg)
	{
		assertTrue("empty", !events.isEmpty());
		final LoggingEvent event = events.remove(0);
		assertEquals(
				"" + level + ' ' + msg,
				"" + event.getLevel() + ' ' + event.getRenderedMessage());
		assertEquals(level, event.getLevel());
		assertEquals(msg, event.getRenderedMessage());
	}

	private final void assertMessageWithoutMilliseconds(final Level level, final String msg)
	{
		assertTrue("empty", !events.isEmpty());
		final LoggingEvent event = events.remove(0);
		assertEquals(level, event.getLevel());
		assertEquals(msg, event.getRenderedMessage().replaceAll("[0-9]ms", "XXms"));
	}

	public final void assertEmpty()
	{
		before.assertCalled();
		assertEquals(Collections.EMPTY_LIST, events);
	}


	private final Appender appender = new Appender(events);

	private final class Appender extends AppenderSkeleton
	{
		final List<LoggingEvent> events;

		Appender(final List<LoggingEvent> events)
		{
			this.events = events;
		}

		@Override
		protected void append(final LoggingEvent event)
		{
			if(filter((String)event.getMessage()))
				events.add( event );
		}

		public boolean requiresLayout()
		{
			return false;
		}

		public void close() throws SecurityException
		{
			throw new RuntimeException();
		}
	}
}
