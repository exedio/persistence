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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.Model;
import com.exedio.cope.util.AssertionErrorJobContext;

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
		final StackTraceElement[] st = Thread.currentThread().getStackTrace();
		assertIt(Thread.class, "getStackTrace", st[0]);
		assertIt(DeleteJobContext.class, "stopIfRequested", st[1]);
		assertIt(Delete.class, "delete", st[2+stopIfRequestedStackTraceOffset()]);

		switch(st[2+stopIfRequestedStackTraceOffset()].getLineNumber()) // Au weia !!!
		{
			case 44:
				assertFalse(model.hasCurrentTransaction());
				break;
			case 55:
				assertTrue(model.hasCurrentTransaction());
				break;
			default:
				fail(st[2].toString());
		}
	}

	protected int stopIfRequestedStackTraceOffset()
	{
		return 0;
	}

	private int progress = 0;

	@Override
	public void incrementProgress()
	{
		final StackTraceElement[] st = Thread.currentThread().getStackTrace();
		assertIt(Thread.class, "getStackTrace", st[0]);
		assertIt(DeleteJobContext.class, "incrementProgress", st[1]);
		assertIt(Delete.class, "delete", st[2]);

		progress++;
	}

	public int getProgress()
	{
		return progress;
	}


	private static void assertIt(
			final Class<?> expectedClass,
			final String expectedMethodName,
			final StackTraceElement actual)
	{
		assertEquals("class",      expectedClass.getName(), actual.getClassName());
		assertEquals("methodName", expectedMethodName,      actual.getMethodName());
	}
}
