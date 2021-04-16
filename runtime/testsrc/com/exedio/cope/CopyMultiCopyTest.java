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
import static com.exedio.cope.CopyMultiCopySource.create;
import static com.exedio.cope.CopyMultiCopySource.createA;
import static com.exedio.cope.CopyMultiCopySource.createAB;
import static com.exedio.cope.CopyMultiCopySource.createB;
import static com.exedio.cope.CopySimpleTest.assertFails;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class CopyMultiCopyTest extends TestWithEnvironment
{
	public CopyMultiCopyTest()
	{
		super(CopyMultiCopyModelTest.MODEL);
	}

	@Test void testOk()
	{
		final CopyMultiCopyTarget target =
				new CopyMultiCopyTarget("targetValueA", "targetValueB");
		assertContains(TYPE.search());

		final CopyMultiCopySource source = createAB("targetValueA", "targetValueB", target);
		assertEquals("targetValueA", source.getCopyA());
		assertEquals("targetValueB", source.getCopyB());
		assertEquals(target, source.getTarget());
		assertContains(source, TYPE.search());

		final CopyMultiCopyTarget targetSet =
				new CopyMultiCopyTarget("targetValueAset", "targetValueBset");
		source.setCopyAB("targetValueAset", "targetValueBset", targetSet);
		assertEquals("targetValueAset", source.getCopyA());
		assertEquals("targetValueBset", source.getCopyB());
		assertEquals(targetSet, source.getTarget());
	}

	@Test void testOkValueNull()
	{
		final CopyMultiCopyTarget target =
				new CopyMultiCopyTarget(null, null);
		assertContains(TYPE.search());

		final CopyMultiCopySource source = createAB(null, null, target);
		assertEquals(null, source.getCopyA());
		assertEquals(null, source.getCopyB());
		assertEquals(target, source.getTarget());
		assertContains(source, TYPE.search());
	}

	@Test void testOkTargetNull()
	{
		final CopyMultiCopySource source = createAB("targetValueA", "targetValueB", null);
		assertEquals("targetValueA", source.getCopyA());
		assertEquals("targetValueB", source.getCopyB());
		assertEquals(null, source.getTarget());
		assertContains(source, TYPE.search());

		source.setCopyAB("targetValueAset", "targetValueBset", null);
		assertEquals("targetValueAset", source.getCopyA());
		assertEquals("targetValueBset", source.getCopyB());
		assertEquals(null, source.getTarget());
	}

	@Test void testOkTargetOmitted()
	{
		final CopyMultiCopySource source = createAB("targetValueA", "targetValueB");
		assertEquals("targetValueA", source.getCopyA());
		assertEquals("targetValueB", source.getCopyB());
		assertEquals(null, source.getTarget());
		assertContains(source, TYPE.search());

		source.setCopyAB("targetValueAset", "targetValueBset");
		assertEquals("targetValueAset", source.getCopyA());
		assertEquals("targetValueBset", source.getCopyB());
		assertEquals(null, source.getTarget());
	}

	@Test void testWrongAcreate()
	{
		final CopyMultiCopyTarget target =
				new CopyMultiCopyTarget("targetValueA", "targetValueB");
		try
		{
			createAB("targetValueAx", "targetValueB", target);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					constraintA, "targetValueA", "targetValueAx", target,
					"copy violation for " + constraintA + ", " +
					"expected 'targetValueA' " +
					"from target " + target.getCopeID() + ", " +
					"but was 'targetValueAx'", e);
		}
		assertContains(TYPE.search());
	}

	@Test void testWrongAset()
	{
		final CopyMultiCopyTarget target =
				new CopyMultiCopyTarget("targetValueA", "targetValueB");
		final CopyMultiCopySource source = createAB("targetValueA", "targetValueB", target);
		assertEquals("targetValueA", source.getCopyA());
		assertEquals("targetValueB", source.getCopyB());
		assertEquals(target, source.getTarget());
		try
		{
			source.setCopyA("targetValueAx");
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					constraintA, source, "targetValueA", "targetValueAx", target,
					"copy violation on " + source + " " +
					"for " + constraintA + ", " +
					"expected 'targetValueA' " +
					"from target " + target.getCopeID() + ", " +
					"but was 'targetValueAx'", e);
		}
		assertEquals("targetValueA", source.getCopyA());
		assertEquals("targetValueB", source.getCopyB());
		assertEquals(target, source.getTarget());
	}

	@Test void testWrongBcreate()
	{
		final CopyMultiCopyTarget target =
				new CopyMultiCopyTarget("targetValueA", "targetValueB");
		try
		{
			createAB("targetValueA", "targetValueBx", target);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					constraintB, "targetValueB", "targetValueBx", target,
					"copy violation for " + constraintB + ", " +
					"expected 'targetValueB' " +
					"from target " + target.getCopeID() + ", " +
					"but was 'targetValueBx'", e);
		}
		assertContains(TYPE.search());
	}

	@Test void testWrongBset()
	{
		final CopyMultiCopyTarget target =
				new CopyMultiCopyTarget("targetValueA", "targetValueB");
		final CopyMultiCopySource source = createAB("targetValueA", "targetValueB", target);
		assertEquals("targetValueA", source.getCopyA());
		assertEquals("targetValueB", source.getCopyB());
		assertEquals(target, source.getTarget());
		try
		{
			source.setCopyB("targetValueBx");
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					constraintB, source, "targetValueB", "targetValueBx", target,
					"copy violation on " + source + " " +
					"for " + constraintB + ", " +
					"expected 'targetValueB' " +
					"from target " + target.getCopeID() + ", " +
					"but was 'targetValueBx'", e);
		}
		assertEquals("targetValueA", source.getCopyA());
		assertEquals("targetValueB", source.getCopyB());
		assertEquals(target, source.getTarget());
	}

	@Test void testWrongCopyANull()
	{
		final CopyMultiCopyTarget target =
				new CopyMultiCopyTarget("targetValueA", "targetValueB");
		try
		{
			createAB(null, "targetValueB", target);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					constraintA, "targetValueA", null, target,
					"copy violation for " + constraintA + ", " +
					"expected 'targetValueA' " +
					"from target " + target.getCopeID() + ", " +
					"but was null", e);
		}
		assertContains(TYPE.search());
	}

	@Test void testOkCopyAOmitted()
	{
		final CopyMultiCopyTarget target =
				new CopyMultiCopyTarget("targetValueA", "targetValueB");

		final CopyMultiCopySource source = createB("targetValueB", target);
		assertEquals("targetValueA", source.getCopyA());
		assertEquals("targetValueB", source.getCopyB());
		assertEquals(target, source.getTarget());
		assertContains(source, TYPE.search());

		final CopyMultiCopyTarget targetSet =
				new CopyMultiCopyTarget("targetValueAset", "targetValueBset");
		source.setCopyB("targetValueBset", targetSet);
		assertEquals("targetValueAset", source.getCopyA());
		assertEquals("targetValueBset", source.getCopyB());
		assertEquals(targetSet, source.getTarget());
	}

	@Test void testWrongCopyBNull()
	{
		final CopyMultiCopyTarget target =
				new CopyMultiCopyTarget("targetValueA", "targetValueB");
		try
		{
			createAB("targetValueA", null, target);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					constraintB, "targetValueB", null, target,
					"copy violation for " + constraintB + ", " +
					"expected 'targetValueB' " +
					"from target " + target.getCopeID() + ", " +
					"but was null", e);
		}
		assertContains(TYPE.search());
	}

	@Test void testOkCopyBOmitted()
	{
		final CopyMultiCopyTarget target =
				new CopyMultiCopyTarget("targetValueA", "targetValueB");

		final CopyMultiCopySource source = createA("targetValueA", target);
		assertEquals("targetValueA", source.getCopyA());
		assertEquals("targetValueB", source.getCopyB());
		assertEquals(target, source.getTarget());
		assertContains(source, TYPE.search());

		final CopyMultiCopyTarget targetSet =
				new CopyMultiCopyTarget("targetValueAset", "targetValueBset");
		source.setCopyA("targetValueAset", targetSet);
		assertEquals("targetValueAset", source.getCopyA());
		assertEquals("targetValueBset", source.getCopyB());
		assertEquals(targetSet, source.getTarget());
	}

	@Test void testWrongCopyABNull()
	{
		final CopyMultiCopyTarget target =
				new CopyMultiCopyTarget("targetValueA", "targetValueB");
		try
		{
			createAB(null, null, target);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					constraintA, "targetValueA", null, target,
					"copy violation for " + constraintA + ", " +
					"expected 'targetValueA' " +
					"from target " + target.getCopeID() + ", " +
					"but was null", e);
		}
		assertContains(TYPE.search());
	}

	@Test void testOkCopyABOmitted()
	{
		final CopyMultiCopyTarget target =
				new CopyMultiCopyTarget("targetValueA", "targetValueB");

		final CopyMultiCopySource source = create(target);
		assertEquals("targetValueA", source.getCopyA());
		assertEquals("targetValueB", source.getCopyB());
		assertEquals(target, source.getTarget());
		assertContains(source, TYPE.search());

		final CopyMultiCopyTarget targetSet =
				new CopyMultiCopyTarget("targetValueAset", "targetValueBset");
		source.setTarget(targetSet);
		assertEquals("targetValueAset", source.getCopyA());
		assertEquals("targetValueBset", source.getCopyB());
		assertEquals(targetSet, source.getTarget());
	}
}
