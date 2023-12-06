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

package com.exedio.cope.misc;

import static java.lang.Thread.currentThread;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Model;
import com.exedio.cope.util.AssertionErrorJobContext;
import com.exedio.cope.util.JobContext;
import java.time.Duration;
import java.util.Iterator;

public class DeleteJobContext extends AssertionErrorJobContext
{
	private final Model model;

	public DeleteJobContext(final Model model)
	{
		this.model = model;
		assertNotNull(model);
	}

	@Override
	public void stopIfRequested()
	{
		final Iterator<StackTraceElement> st = asList(currentThread().getStackTrace()).iterator();
		assertIt(Thread.class, "getStackTrace", st.next());
		assertIt(DeleteJobContext.class, "stopIfRequested", st.next());
		stopIfRequestedStackTraceOffset(st);
		if(!model.hasCurrentTransaction())
			assertIt(JobContext.class, "deferOrStopIfRequested", st.next());
		final StackTraceElement inDelete =
				assertIt(Delete.class, "delete", st.next());

		switch(inDelete.getLineNumber()) // Au weia !!!
		{
			case 50:
				assertFalse(model.hasCurrentTransaction());
				break;
			case 65:
				assertTrue(model.hasCurrentTransaction());
				break;
			default:
				fail(inDelete.toString());
		}
	}

	@Override
	public Duration requestsDeferral()
	{
		final Iterator<StackTraceElement> st = asList(currentThread().getStackTrace()).iterator();
		assertIt(Thread.class, "getStackTrace", st.next());
		assertIt(DeleteJobContext.class, "requestsDeferral", st.next());
		assertIt(JobContext.class, "deferOrStopIfRequested", st.next());
		assertIt(Delete.class, "delete", st.next());

		assertFalse(model.hasCurrentTransaction());
		return Duration.ZERO;
	}

	protected void stopIfRequestedStackTraceOffset(final Iterator<StackTraceElement> st)
	{
		// empty
	}

	private int progress = 0;

	@Override
	public void incrementProgress()
	{
		final StackTraceElement[] st = currentThread().getStackTrace();
		assertIt(Thread.class, "getStackTrace", st[0]);
		assertIt(DeleteJobContext.class, "incrementProgress", st[1]);
		assertIt(Delete.class, "delete", st[2]);

		progress++;
	}

	public int getProgress()
	{
		return progress;
	}


	static StackTraceElement assertIt(
			final Class<?> expectedClass,
			final String expectedMethodName,
			final StackTraceElement actual)
	{
		assertEquals(expectedClass.getName(), actual.getClassName(),  "class");
		assertEquals(expectedMethodName,      actual.getMethodName(), "methodName");
		return actual;
	}
}
