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

package com.exedio.cope;

import static com.exedio.cope.CopyMultiModelTest.constraintA;
import static com.exedio.cope.CopyMultiModelTest.constraintB;
import static com.exedio.cope.CopyMultiSourceItem.TYPE;

public class CopyMultiTest extends AbstractRuntimeTest
{
	public CopyMultiTest()
	{
		super(CopyMultiModelTest.MODEL);
	}

	CopyMultiTargetItemA targetA, targetAx;
	CopyMultiTargetItemB targetB, targetBx;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		targetA  = deleteOnTearDown(new CopyMultiTargetItemA("targetValue"));
		targetAx = deleteOnTearDown(new CopyMultiTargetItemA("targetValueAx"));
		targetB  = deleteOnTearDown(new CopyMultiTargetItemB("targetValue"));
		targetBx = deleteOnTearDown(new CopyMultiTargetItemB("targetValueBx"));
	}

	public void testIt()
	{
		assertContains(TYPE.search());

		final CopyMultiSourceItem source1 = deleteOnTearDown(new CopyMultiSourceItem(targetA, targetB, "targetValue"));
		assertEquals(targetA, source1.getTargetA());
		assertEquals(targetB, source1.getTargetB());
		assertEquals("targetValue", source1.getCopy());
		assertContains(source1, TYPE.search());

		try
		{
			new CopyMultiSourceItem(targetAx, targetB, "targetValue");
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertEquals(constraintA, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("targetValueAx", e.getExpectedValue());
			assertEquals("targetValue", e.getActualValue());
			assertEquals(targetAx, e.getTargetItem());
			assertEquals(
					"copy violation on " + constraintA + ", " +
					"expected 'targetValueAx' " +
					"from target " + targetAx.getCopeID() + ", " +
					"but was 'targetValue'",
				e.getMessage());
		}
		assertContains(source1, TYPE.search());

		try
		{
			new CopyMultiSourceItem(targetA, targetBx, "targetValue");
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertEquals(constraintB, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("targetValueBx", e.getExpectedValue());
			assertEquals("targetValue", e.getActualValue());
			assertEquals(targetBx, e.getTargetItem());
			assertEquals(
					"copy violation on " + constraintB + ", " +
					"expected 'targetValueBx' " +
					"from target " + targetBx.getCopeID() + ", " +
					"but was 'targetValue'",
				e.getMessage());
		}
		assertContains(source1, TYPE.search());
	}
}
