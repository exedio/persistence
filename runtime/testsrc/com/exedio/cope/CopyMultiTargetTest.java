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
import static com.exedio.cope.CopyMultiTargetSource.TYPE;
import static com.exedio.cope.CopySimpleTest.assertFails;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class CopyMultiTargetTest extends TestWithEnvironment
{
	public CopyMultiTargetTest()
	{
		super(CopyMultiTargetModelTest.MODEL);
	}

	@Test public void testOk()
	{
		final CopyMultiTargetA targetA = new CopyMultiTargetA("targetValue");
		final CopyMultiTargetB targetB = new CopyMultiTargetB("targetValue");
		assertContains(TYPE.search());

		final CopyMultiTargetSource source = new CopyMultiTargetSource(targetA, targetB, "targetValue");
		assertEquals(targetA, source.getTargetA());
		assertEquals(targetB, source.getTargetB());
		assertEquals("targetValue", source.getCopy());
		assertContains(source, TYPE.search());
	}

	@Test public void testOkNullValue()
	{
		final CopyMultiTargetA targetA = new CopyMultiTargetA(null);
		final CopyMultiTargetB targetB = new CopyMultiTargetB(null);
		assertContains(TYPE.search());

		final CopyMultiTargetSource source = new CopyMultiTargetSource(targetA, targetB, null);
		assertEquals(targetA, source.getTargetA());
		assertEquals(targetB, source.getTargetB());
		assertEquals(null, source.getCopy());
		assertContains(source, TYPE.search());
	}

	@Test public void testWrongA()
	{
		final CopyMultiTargetA targetA = new CopyMultiTargetA("targetValueAx");
		final CopyMultiTargetB targetB = new CopyMultiTargetB("targetValue");
		try
		{
			new CopyMultiTargetSource(targetA, targetB, "targetValue");
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					constraintA, "targetValueAx", "targetValue", targetA,
					"copy violation on " + constraintA + ", " +
					"expected 'targetValueAx' " +
					"from target " + targetA.getCopeID() + ", " +
					"but was 'targetValue'", e);
		}
		assertContains(TYPE.search());
	}

	@Test public void testWrongB()
	{
		final CopyMultiTargetA targetA = new CopyMultiTargetA("targetValue");
		final CopyMultiTargetB targetB = new CopyMultiTargetB("targetValueBx");
		try
		{
			new CopyMultiTargetSource(targetA, targetB, "targetValue");
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					constraintB, "targetValueBx", "targetValue", targetB,
					"copy violation on " + constraintB + ", " +
					"expected 'targetValueBx' " +
					"from target " + targetB.getCopeID() + ", " +
					"but was 'targetValue'", e);
		}
		assertContains(TYPE.search());
	}
}
