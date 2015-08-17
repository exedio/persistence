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

import static com.exedio.cope.CopyMultiTargetModelTest.constraintA;
import static com.exedio.cope.CopyMultiTargetModelTest.constraintB;
import static com.exedio.cope.CopyMultiTargetSourceItem.TYPE;

public class CopyMultiTargetTest extends AbstractRuntimeModelTest
{
	public CopyMultiTargetTest()
	{
		super(CopyMultiTargetModelTest.MODEL);
	}

	public void testOk()
	{
		final CopyMultiTargetItemA targetA = new CopyMultiTargetItemA("targetValue");
		final CopyMultiTargetItemB targetB = new CopyMultiTargetItemB("targetValue");
		assertContains(TYPE.search());

		final CopyMultiTargetSourceItem source = new CopyMultiTargetSourceItem(targetA, targetB, "targetValue");
		assertEquals(targetA, source.getTargetA());
		assertEquals(targetB, source.getTargetB());
		assertEquals("targetValue", source.getCopy());
		assertContains(source, TYPE.search());
	}

	public void testOkNullValue()
	{
		final CopyMultiTargetItemA targetA = new CopyMultiTargetItemA(null);
		final CopyMultiTargetItemB targetB = new CopyMultiTargetItemB(null);
		assertContains(TYPE.search());

		final CopyMultiTargetSourceItem source = new CopyMultiTargetSourceItem(targetA, targetB, null);
		assertEquals(targetA, source.getTargetA());
		assertEquals(targetB, source.getTargetB());
		assertEquals(null, source.getCopy());
		assertContains(source, TYPE.search());
	}

	public void testWrongA()
	{
		final CopyMultiTargetItemA targetA = new CopyMultiTargetItemA("targetValueAx");
		final CopyMultiTargetItemB targetB = new CopyMultiTargetItemB("targetValue");
		try
		{
			new CopyMultiTargetSourceItem(targetA, targetB, "targetValue");
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertEquals(constraintA, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("targetValueAx", e.getExpectedValue());
			assertEquals("targetValue", e.getActualValue());
			assertEquals(targetA, e.getTargetItem());
			assertEquals(
					"copy violation on " + constraintA + ", " +
					"expected 'targetValueAx' " +
					"from target " + targetA.getCopeID() + ", " +
					"but was 'targetValue'",
				e.getMessage());
		}
		assertContains(TYPE.search());
	}

	public void testWrongB()
	{
		final CopyMultiTargetItemA targetA = new CopyMultiTargetItemA("targetValue");
		final CopyMultiTargetItemB targetB = new CopyMultiTargetItemB("targetValueBx");
		try
		{
			new CopyMultiTargetSourceItem(targetA, targetB, "targetValue");
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertEquals(constraintB, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("targetValueBx", e.getExpectedValue());
			assertEquals("targetValue", e.getActualValue());
			assertEquals(targetB, e.getTargetItem());
			assertEquals(
					"copy violation on " + constraintB + ", " +
					"expected 'targetValueBx' " +
					"from target " + targetB.getCopeID() + ", " +
					"but was 'targetValue'",
				e.getMessage());
		}
		assertContains(TYPE.search());
	}
}
