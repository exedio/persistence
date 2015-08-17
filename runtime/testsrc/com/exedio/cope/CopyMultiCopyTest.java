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
import static com.exedio.cope.CopyMultiCopySourceItem.TYPE;

public class CopyMultiCopyTest extends AbstractRuntimeModelTest
{
	public CopyMultiCopyTest()
	{
		super(CopyMultiCopyModelTest.MODEL);
	}

	CopyMultiCopyTargetItem target;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		target  = new CopyMultiCopyTargetItem("targetValueA", "targetValueB");
	}

	public void testOk()
	{
		assertContains(TYPE.search());

		final CopyMultiCopySourceItem source = new CopyMultiCopySourceItem("targetValueA", "targetValueB", target);
		assertEquals("targetValueA", source.getCopyA());
		assertEquals("targetValueB", source.getCopyB());
		assertEquals(target, source.getTarget());
		assertContains(source, TYPE.search());
	}

	public void testWrongA()
	{
		try
		{
			new CopyMultiCopySourceItem("targetValueAx", "targetValueB", target);
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

	public void testWrongB()
	{
		try
		{
			new CopyMultiCopySourceItem("targetValueA", "targetValueBx", target);
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
