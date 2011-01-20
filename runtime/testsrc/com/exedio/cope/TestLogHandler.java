/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;

public final class TestLogHandler extends Handler
{
	private final ArrayList<LogRecord> records = new ArrayList<LogRecord>();

	@Override
	public void publish(final LogRecord record)
	{
		records.add(record);
	}

	public void assertInfo(final String msg)
	{
		assertMessage(Level.INFO, msg);
	}

	public void assertMessage(final Level level, final String msg)
	{
		assertTrue("empty", !records.isEmpty());
		final LogRecord record = records.remove(0);
		assertEquals(level, record.getLevel());
		final String m = record.getMessage();
		final Object[] p = record.getParameters();
		assertEquals(msg, p!=null ? MessageFormat.format(m, p) : m);
	}

	public void assertEmpty()
	{
		assertEquals(Collections.EMPTY_LIST, records);
	}

	@Override
	public void close() throws SecurityException
	{
		throw new RuntimeException();
	}

	@Override
	public void flush()
	{
		throw new RuntimeException();
	}
}
