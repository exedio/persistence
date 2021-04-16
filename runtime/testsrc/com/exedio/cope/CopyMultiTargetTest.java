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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class CopyMultiTargetTest extends TestWithEnvironment
{
	public CopyMultiTargetTest()
	{
		super(CopyMultiTargetModelTest.MODEL);
	}

	@Test void testOk()
	{
		final CopyMultiTargetA targetA = new CopyMultiTargetA("targetValue");
		final CopyMultiTargetB targetB = new CopyMultiTargetB("targetValue");
		assertContains(TYPE.search());

		final CopyMultiTargetSource source = new CopyMultiTargetSource(targetA, targetB, "targetValue");
		assertEquals(targetA, source.getTargetA());
		assertEquals(targetB, source.getTargetB());
		assertEquals("targetValue", source.getCopy());
		assertContains(source, TYPE.search());

		final CopyMultiTargetA targetAset = new CopyMultiTargetA("targetValueSet");
		final CopyMultiTargetB targetBset = new CopyMultiTargetB("targetValueSet");
		source.setTargetABandCopy(targetAset, targetBset, "targetValueSet");
		assertEquals(targetAset, source.getTargetA());
		assertEquals(targetBset, source.getTargetB());
		assertEquals("targetValueSet", source.getCopy());
	}

	@Test void testOkNullValue()
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

	@Test void testOkNullTargetA()
	{
		final CopyMultiTargetB targetB = new CopyMultiTargetB("targetValue");
		assertContains(TYPE.search());

		final CopyMultiTargetSource source = new CopyMultiTargetSource(null, targetB, "targetValue");
		assertEquals(null, source.getTargetA());
		assertEquals(targetB, source.getTargetB());
		assertEquals("targetValue", source.getCopy());
		assertContains(source, TYPE.search());

		final CopyMultiTargetB targetBset = new CopyMultiTargetB("targetValueSet");
		source.setTargetBandCopy(targetBset, "targetValueSet");
		assertEquals(null, source.getTargetA());
		assertEquals(targetBset, source.getTargetB());
		assertEquals("targetValueSet", source.getCopy());
	}

	@Test void testOkNullTargetAB()
	{
		assertContains(TYPE.search());

		final CopyMultiTargetSource source = new CopyMultiTargetSource(null, null, "targetValue");
		assertEquals(null, source.getTargetA());
		assertEquals(null, source.getTargetB());
		assertEquals("targetValue", source.getCopy());
		assertContains(source, TYPE.search());

		source.setCopy("targetValueSet");
		assertEquals(null, source.getTargetA());
		assertEquals(null, source.getTargetB());
		assertEquals("targetValueSet", source.getCopy());
	}

	@Test void testWrongAcreate()
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
					"copy violation for " + constraintA + ", " +
					"expected 'targetValueAx' " +
					"from target " + targetA.getCopeID() + ", " +
					"but was 'targetValue'", e);
		}
		assertContains(TYPE.search());
	}

	@Test void testWrongAset()
	{
		final CopyMultiTargetA targetA = new CopyMultiTargetA("targetValue");
		final CopyMultiTargetB targetB = new CopyMultiTargetB("targetValue");

		final CopyMultiTargetSource source = new CopyMultiTargetSource(targetA, targetB, "targetValue");
		assertEquals(targetA, source.getTargetA());
		assertEquals(targetB, source.getTargetB());
		assertEquals("targetValue", source.getCopy());

		final CopyMultiTargetA targetAx = new CopyMultiTargetA("targetValueAx");
		try
		{
			source.setTargetA(targetAx);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
				constraintA, constraintB,
				source,
				"targetValueAx", "targetValue",
				targetAx, targetB,
				"copy violation on " + source + " " +
					"for " + constraintA + " and " + constraintB + ", " +
					"expected 'targetValueAx' from target " + targetAx.getCopeID() +
					" but also 'targetValue' from target " + targetB.getCopeID(),
				e
			);
		}
		assertEquals(targetA, source.getTargetA());
		assertEquals(targetB, source.getTargetB());
		assertEquals("targetValue", source.getCopy());
	}

	@Test void testWrongBcreate()
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
					"copy violation for " + constraintB + ", " +
					"expected 'targetValueBx' " +
					"from target " + targetB.getCopeID() + ", " +
					"but was 'targetValue'", e);
		}
		assertContains(TYPE.search());
	}

	@Test void testWrongBset()
	{
		final CopyMultiTargetA targetA = new CopyMultiTargetA("targetValue");
		final CopyMultiTargetB targetB = new CopyMultiTargetB("targetValue");

		final CopyMultiTargetSource source = new CopyMultiTargetSource(targetA, targetB, "targetValue");
		assertEquals(targetA, source.getTargetA());
		assertEquals(targetB, source.getTargetB());
		assertEquals("targetValue", source.getCopy());

		final CopyMultiTargetB targetBx = new CopyMultiTargetB("targetValueBx");
		try
		{
			source.setTargetB(targetBx);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
				constraintA, constraintB,
				source,
				"targetValue", "targetValueBx",
				targetA, targetBx,
				"copy violation on " + source + " " +
					"for " + constraintA + " and " + constraintB + ", " +
					"expected 'targetValue' from target " + targetA.getCopeID() +
					" but also 'targetValueBx' from target " + targetBx.getCopeID(),
				e
			);
		}
		assertEquals(targetA, source.getTargetA());
		assertEquals(targetB, source.getTargetB());
		assertEquals("targetValue", source.getCopy());
	}

	@Test void testWrongABcreate()
	{
		final CopyMultiTargetA targetA = new CopyMultiTargetA("targetValueAx");
		final CopyMultiTargetB targetB = new CopyMultiTargetB("targetValueBx");
		try
		{
			new CopyMultiTargetSource(targetA, targetB, "targetValue");
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
				constraintA,
				"targetValueAx", "targetValue",
				targetA,
				"copy violation for " + constraintA + ", " +
					"expected 'targetValueAx' from target " + targetA.getCopeID() + ", " +
					"but was 'targetValue'",
				e
			);
		}
		assertContains(TYPE.search());
	}

	@Test void testWrongABset()
	{
		final CopyMultiTargetA targetA = new CopyMultiTargetA("targetValue");
		final CopyMultiTargetB targetB = new CopyMultiTargetB("targetValue");

		final CopyMultiTargetSource source = new CopyMultiTargetSource(targetA, targetB, "targetValue");
		assertEquals(targetA, source.getTargetA());
		assertEquals(targetB, source.getTargetB());
		assertEquals("targetValue", source.getCopy());

		final CopyMultiTargetA targetAset = new CopyMultiTargetA("targetValueAx");
		final CopyMultiTargetB targetBset = new CopyMultiTargetB("targetValueBx");
		try
		{
			source.setTargetAB(targetAset, targetBset);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
				constraintA, constraintB,
				source,
				"targetValueAx", "targetValueBx",
				targetAset, targetBset,
				"copy violation on " + source + " " +
					"for " + constraintA + " and " + constraintB + ", " +
					"expected 'targetValueAx' from target " + targetAset.getCopeID() +
					" but also 'targetValueBx' from target " + targetBset.getCopeID(),
				e
			);
		}
		assertEquals(targetA, source.getTargetA());
		assertEquals(targetB, source.getTargetB());
		assertEquals("targetValue", source.getCopy());
	}

	@Test void testWrongNullTargetA()
	{
		final CopyMultiTargetB targetB = new CopyMultiTargetB("targetValueBx");
		try
		{
			new CopyMultiTargetSource(null, targetB, "targetValue");
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
				constraintB,
				"targetValueBx", "targetValue",
				targetB,
				"copy violation for " + constraintB + ", " +
					"expected 'targetValueBx' from target " + targetB.getCopeID() + ", " +
					"but was 'targetValue'",
				e
			);
		}
		assertContains(TYPE.search());
	}

	@Test void testOkOmittedCopyA()
	{
		final CopyMultiTargetA targetA = new CopyMultiTargetA("targetValue");

		final CopyMultiTargetSource source = CopyMultiTargetSource.omitCopy(targetA, null);
		assertEquals(targetA, source.getTargetA());
		assertEquals(null, source.getTargetB());
		assertEquals("targetValue", source.getCopy());
		assertContains(source, TYPE.search());

		final CopyMultiTargetA targetAset = new CopyMultiTargetA("targetValueSet");
		source.setTargetAandCopy(targetAset, "targetValueSet");
		assertEquals(targetAset, source.getTargetA());
		assertEquals(null, source.getTargetB());
		assertEquals("targetValueSet", source.getCopy());
	}

	@Test void testOkOmittedCopyAndTargetA()
	{
		final CopyMultiTargetA targetA = new CopyMultiTargetA("targetValue");

		final CopyMultiTargetSource source = CopyMultiTargetSource.omitCopy(targetA);
		assertEquals(targetA, source.getTargetA());
		assertEquals(null, source.getTargetB());
		assertEquals("targetValue", source.getCopy());
		assertContains(source, TYPE.search());

		final CopyMultiTargetA targetAset = new CopyMultiTargetA("targetValueSet");
		source.setTargetA(targetAset);
		assertEquals(targetAset, source.getTargetA());
		assertEquals(null, source.getTargetB());
		assertEquals("targetValueSet", source.getCopy());
	}

	@Test void testOkOmittedCopyB()
	{
		final CopyMultiTargetB targetB = new CopyMultiTargetB("targetValue");

		final CopyMultiTargetSource source = CopyMultiTargetSource.omitCopy(null, targetB);
		assertEquals(null, source.getTargetA());
		assertEquals(targetB, source.getTargetB());
		assertEquals("targetValue", source.getCopy());
		assertContains(source, TYPE.search());

		final CopyMultiTargetB targetBset = new CopyMultiTargetB("targetValueSet");
		source.setTargetBandCopy(targetBset, "targetValueSet");
		assertEquals(null, source.getTargetA());
		assertEquals(targetBset, source.getTargetB());
		assertEquals("targetValueSet", source.getCopy());
	}

	@Test void testOkOmittedCopyAndTargetB()
	{
		final CopyMultiTargetB targetB = new CopyMultiTargetB("targetValue");

		final CopyMultiTargetSource source = CopyMultiTargetSource.omitCopy(targetB);
		assertEquals(null, source.getTargetA());
		assertEquals(targetB, source.getTargetB());
		assertEquals("targetValue", source.getCopy());
		assertContains(source, TYPE.search());

		final CopyMultiTargetB targetBset = new CopyMultiTargetB("targetValueSet");
		source.setTargetB(targetBset);
		assertEquals(null, source.getTargetA());
		assertEquals(targetBset, source.getTargetB());
		assertEquals("targetValueSet", source.getCopy());
	}

	@Test void testOkOmittedCopyAB()
	{
		final CopyMultiTargetA targetA = new CopyMultiTargetA("targetValue");
		final CopyMultiTargetB targetB = new CopyMultiTargetB("targetValue");

		final CopyMultiTargetSource source = CopyMultiTargetSource.omitCopy(targetA, targetB);
		assertEquals(targetA, source.getTargetA());
		assertEquals(targetB, source.getTargetB());
		assertEquals("targetValue", source.getCopy());
		assertContains(source, TYPE.search());

		final CopyMultiTargetA targetAset = new CopyMultiTargetA("targetValueSet");
		final CopyMultiTargetB targetBset = new CopyMultiTargetB("targetValueSet");
		source.setTargetAB(targetAset, targetBset);
		assertEquals(targetAset, source.getTargetA());
		assertEquals(targetBset, source.getTargetB());
		assertEquals("targetValueSet", source.getCopy());
	}

	@Test void testOkOmittedCopyAndTargetAB()
	{
		final CopyMultiTargetSource source = CopyMultiTargetSource.omitCopy();
		assertEquals(null, source.getTargetA());
		assertEquals(null, source.getTargetB());
		assertEquals(null, source.getCopy());
		assertContains(source, TYPE.search());
	}

	@Test void testWrongOmittedCopyABCollideCreate()
	{
		final CopyMultiTargetA targetA = new CopyMultiTargetA("targetValueAx");
		final CopyMultiTargetB targetB = new CopyMultiTargetB("targetValueBx");
		try
		{
			CopyMultiTargetSource.omitCopy(targetA, targetB);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
				constraintA, constraintB,
				null,
				"targetValueAx", "targetValueBx",
				targetA, targetB,
				"copy violation for " + constraintA + " and " + constraintB + ", " +
					"expected 'targetValueAx' from target " + targetA.getCopeID() +
					" but also 'targetValueBx' from target " + targetB.getCopeID(),
				e
			);
		}
		assertContains(TYPE.search());
	}

	@Test void testWrongOmittedCopyABCollideSet()
	{
		final CopyMultiTargetA targetA = new CopyMultiTargetA("targetValue");
		final CopyMultiTargetB targetB = new CopyMultiTargetB("targetValue");

		final CopyMultiTargetSource source = CopyMultiTargetSource.omitCopy(targetA, targetB);
		assertEquals(targetA, source.getTargetA());
		assertEquals(targetB, source.getTargetB());
		assertEquals("targetValue", source.getCopy());

		final CopyMultiTargetA targetAset = new CopyMultiTargetA("targetValueAx");
		final CopyMultiTargetB targetBset = new CopyMultiTargetB("targetValueBx");
		try
		{
			source.setTargetAB(targetAset, targetBset);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
				constraintA, constraintB,
				source,
				"targetValueAx", "targetValueBx",
				targetAset, targetBset,
				"copy violation on " + source + " " +
					"for " + constraintA + " and " + constraintB + ", " +
					"expected 'targetValueAx' from target " + targetAset.getCopeID() +
					" but also 'targetValueBx' from target " + targetBset.getCopeID(),
				e
			);
		}
		assertEquals(targetA, source.getTargetA());
		assertEquals(targetB, source.getTargetB());
		assertEquals("targetValue", source.getCopy());
	}
}
