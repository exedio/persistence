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

import static com.exedio.cope.CopyMultiCopyModelTest.constraintA;
import static com.exedio.cope.CopyMultiCopyModelTest.constraintB;
import static com.exedio.cope.CopyMultiCopySource.TYPE;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class CopyMultiCopyTest extends TestWithEnvironment
{
	public CopyMultiCopyTest()
	{
		super(CopyMultiCopyModelTest.MODEL);
	}

	@Test public void testOk()
	{
		final CopyMultiCopyTarget target =
				new CopyMultiCopyTarget("targetValueA", "targetValueB");
		assertContains(TYPE.search());

		final CopyMultiCopySource source = new CopyMultiCopySource("targetValueA", "targetValueB", target);
		assertEquals("targetValueA", source.getCopyA());
		assertEquals("targetValueB", source.getCopyB());
		assertEquals(target, source.getTarget());
		assertContains(source, TYPE.search());
	}

	@Test public void testOkNullValue()
	{
		final CopyMultiCopyTarget target =
				new CopyMultiCopyTarget(null, null);
		assertContains(TYPE.search());

		final CopyMultiCopySource source = new CopyMultiCopySource(null, null, target);
		assertEquals(null, source.getCopyA());
		assertEquals(null, source.getCopyB());
		assertEquals(target, source.getTarget());
		assertContains(source, TYPE.search());
	}

	@Test public void testWrongA()
	{
		final CopyMultiCopyTarget target =
				new CopyMultiCopyTarget("targetValueA", "targetValueB");
		try
		{
			new CopyMultiCopySource("targetValueAx", "targetValueB", target);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertEquals(constraintA, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("targetValueA", e.getExpectedValue());
			assertEquals("targetValueAx", e.getActualValue());
			assertEquals(target, e.getTargetItem());
			assertEquals(
					"copy violation on " + constraintA + ", " +
					"expected 'targetValueA' " +
					"from target " + target.getCopeID() + ", " +
					"but was 'targetValueAx'",
				e.getMessage());
		}
		assertContains(TYPE.search());
	}

	@Test public void testWrongB()
	{
		final CopyMultiCopyTarget target =
				new CopyMultiCopyTarget("targetValueA", "targetValueB");
		try
		{
			new CopyMultiCopySource("targetValueA", "targetValueBx", target);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertEquals(constraintB, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("targetValueB", e.getExpectedValue());
			assertEquals("targetValueBx", e.getActualValue());
			assertEquals(target, e.getTargetItem());
			assertEquals(
					"copy violation on " + constraintB + ", " +
					"expected 'targetValueB' " +
					"from target " + target.getCopeID() + ", " +
					"but was 'targetValueBx'",
				e.getMessage());
		}
		assertContains(TYPE.search());
	}
}
