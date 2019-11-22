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
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

public class LogRule extends MainRule
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
		assertBeforeCalled();
		setLevel(Level.DEBUG);
	}

	private void setLevel(final Level level)
	{
		assertBeforeCalled();

		if(levelBefore==null)
			levelBefore = logger.getLevel();

		logger.setLevel(level);
	}


	private Level levelBefore = null;

	@Override
	protected final void before()
	{
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
		assertMessage(Level.DEBUG, msg, Function.identity());
	}

	public final void assertInfo(final String msg)
	{
		assertMessage(Level.INFO, msg, Function.identity());
	}

	public final void assertInfoMS(final String msg)
	{
		assertMessage(Level.INFO, msg, milliSecondsFilter);
	}

	public final void assertWarn(final String msg)
	{
		assertMessage(Level.WARN, msg, Function.identity());
	}

	public final void assertError(final String msg)
	{
		assertMessage(Level.ERROR, msg, Function.identity());
	}

	public final void assertErrorNS(final String msg)
	{
		assertMessage(Level.ERROR, msg, nanoSecondsFilter);
	}

	private void assertMessage(
			final Level level,
			final String msg,
			final Function<String, String> msgFilter)
	{
		assertBeforeCalled();
		assertTrue(!events.isEmpty(), "empty");
		final LoggingEvent event = events.remove(0);
		assertAll(
				() -> assertEquals(level, event.getLevel()),
				() -> assertEquals(msg, msgFilter.apply(event.getRenderedMessage())));
	}

	static final Function<String, String> milliSecondsFilter = s -> s.replaceAll(" [0-9]{1,2}ms",     " XXms");
	static final Function<String, String> nanoSecondsFilter  = s -> s.replaceAll(" [.,[0-9]]{1,8}ns", " XXns");

	public final void assertEmpty()
	{
		assertBeforeCalled();
		assertEquals(Collections.emptyList(), events);
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

		@Override
		public boolean requiresLayout()
		{
			return false;
		}

		@Override
		public void close() throws SecurityException
		{
			throw new RuntimeException();
		}
	}
}
