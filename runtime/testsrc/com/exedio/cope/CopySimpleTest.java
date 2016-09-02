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

import static com.exedio.cope.CopySimpleModelTest.selfTemplateCopyFromTarget;
import static com.exedio.cope.CopySimpleModelTest.templateItemCopyFromTarget;
import static com.exedio.cope.CopySimpleModelTest.templateStringCopyFromTarget;
import static com.exedio.cope.CopySimpleSource.TYPE;
import static com.exedio.cope.CopySimpleSource.assertBeforeNewCopeItem;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CopySimpleTest extends TestWithEnvironment
{
	public CopySimpleTest()
	{
		super(CopySimpleModelTest.MODEL);
	}

	@Test public void testOk1()
	{
		final CopyValue value = new CopyValue();
		final CopySimpleTarget target = new CopySimpleTarget("template1", "otherString1", value, new CopyValue());
		assertContains(TYPE.search());
		check();

		final CopySimpleSource source = new CopySimpleSource(target, "template1", value);
		assertEquals(target, source.getTargetItem());
		assertEquals("template1", source.getTemplateString());
		assertEquals(value, source.getTemplateItem());
		assertBeforeNewCopeItem(
				CopySimpleSource.targetItem.map(target),
				CopySimpleSource.templateString.map("template1"),
				CopySimpleSource.templateItem.map(value));

		assertContains(source, TYPE.search());
		check();
	}

	@Test public void testOk2()
	{
		final CopyValue value = new CopyValue();
		final CopySimpleTarget target = new CopySimpleTarget("template2", "otherString2", value, new CopyValue());

		final CopySimpleSource source = new CopySimpleSource(target, "template2", value);
		assertEquals(target, source.getTargetItem());
		assertEquals("template2", source.getTemplateString());
		assertEquals(value, source.getTemplateItem());
		assertBeforeNewCopeItem(
				CopySimpleSource.targetItem.map(target),
				CopySimpleSource.templateString.map("template2"),
				CopySimpleSource.templateItem.map(value));

		assertContains(source, TYPE.search());
		check();
	}

	@Test public void testOkNullValue()
	{
		final CopySimpleTarget target = new CopySimpleTarget(null, "otherString2", null, new CopyValue());

		final CopySimpleSource source = new CopySimpleSource(target, null, null);
		assertEquals(target, source.getTargetItem());
		assertEquals(null, source.getTemplateString());
		assertEquals(null, source.getTemplateItem());
		assertBeforeNewCopeItem(
				CopySimpleSource.targetItem.map(target),
				CopySimpleSource.templateString.map(null),
				CopySimpleSource.templateItem.map(null));

		assertContains(source, TYPE.search());
		check();
	}

	@Test public void testOkNullTarget()
	{
		final CopyValue value = new CopyValue();

		final CopySimpleSource source = new CopySimpleSource(null, "templateN", value);
		assertEquals(null, source.getTargetItem());
		assertEquals("templateN", source.getTemplateString());
		assertEquals(value, source.getTemplateItem());
		assertBeforeNewCopeItem(
				CopySimpleSource.targetItem.map(null),
				CopySimpleSource.templateString.map("templateN"),
				CopySimpleSource.templateItem.map(value));

		assertContains(source, TYPE.search());
		check();
	}

	@Test public void testWrongString()
	{
		final CopyValue value = new CopyValue();
		final CopySimpleTarget target = new CopySimpleTarget("template2", "otherString2", value, new CopyValue());
		try
		{
			new CopySimpleSource(target, "template1", value);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					templateStringCopyFromTarget, "template2", "template1", target,
					"copy violation on " + templateStringCopyFromTarget + ", " +
					"expected 'template2' " +
					"from target " + target.getCopeID() + ", " +
					"but was 'template1'", e);
		}
		assertBeforeNewCopeItem(
				CopySimpleSource.targetItem.map(target),
				CopySimpleSource.templateString.map("template1"),
				CopySimpleSource.templateItem.map(value));

		assertContains(TYPE.search());
		check();
	}

	@Test public void testWrongStringNullCopy()
	{
		final CopyValue value = new CopyValue();
		final CopySimpleTarget target = new CopySimpleTarget("template2", "otherString2", value, new CopyValue());
		try
		{
			new CopySimpleSource(target, null, value);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					templateStringCopyFromTarget, "template2", null, target,
					"copy violation on " + templateStringCopyFromTarget + ", " +
					"expected 'template2' " +
					"from target " + target.getCopeID() + ", " +
					"but was null", e);
		}
		assertBeforeNewCopeItem(
				CopySimpleSource.targetItem.map(target),
				CopySimpleSource.templateString.map(null),
				CopySimpleSource.templateItem.map(value));

		assertContains(TYPE.search());
		check();
	}

	@Test public void testWrongItem()
	{
		final CopyValue value1 = new CopyValue();
		final CopyValue value2 = new CopyValue();
		final CopySimpleTarget target = new CopySimpleTarget("template2", "otherString2", value2, new CopyValue());
		try
		{
			new CopySimpleSource(target, "template2", value1);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					templateItemCopyFromTarget, value2, value1, target,
					"copy violation on " + templateItemCopyFromTarget + ", " +
					"expected '" + value2.getCopeID() + "' " +
					"from target " + target.getCopeID() + ", " +
					"but was '" + value1.getCopeID() + "'", e);
		}
		assertBeforeNewCopeItem(
				CopySimpleSource.targetItem.map(target),
				CopySimpleSource.templateString.map("template2"),
				CopySimpleSource.templateItem.map(value1));

		assertContains(TYPE.search());
		check();
	}

	@Test public void testWrongItemNullCopy()
	{
		final CopyValue value = new CopyValue();
		final CopySimpleTarget target = new CopySimpleTarget("template2", "otherString2", value, new CopyValue());
		try
		{
			new CopySimpleSource(target, "template2", null);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					templateItemCopyFromTarget, value, null, target,
					"copy violation on " + templateItemCopyFromTarget + ", " +
					"expected '" + value.getCopeID() + "' " +
					"from target " + target.getCopeID() + ", " +
					"but was null", e);
		}
		assertBeforeNewCopeItem(
				CopySimpleSource.targetItem.map(target),
				CopySimpleSource.templateString.map("template2"),
				CopySimpleSource.templateItem.map(null));

		assertContains(TYPE.search());
		check();
	}

	@Test public void testWrongStringNullTemplate()
	{
		final CopyValue value = new CopyValue();
		final CopySimpleTarget target = new CopySimpleTarget(null, "otherString2", null, new CopyValue());
		try
		{
			new CopySimpleSource(target, "template1", value);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					templateStringCopyFromTarget, null, "template1", target,
					"copy violation on " + templateStringCopyFromTarget + ", " +
					"expected null " +
					"from target " + target.getCopeID() + ", " +
					"but was 'template1'", e);
		}
		assertBeforeNewCopeItem(
				CopySimpleSource.targetItem.map(target),
				CopySimpleSource.templateString.map("template1"),
				CopySimpleSource.templateItem.map(value));

		assertContains(TYPE.search());
		check();
	}

	@Test public void testWrongItemNullTemplate()
	{
		final CopyValue value = new CopyValue();
		final CopySimpleTarget target = new CopySimpleTarget(null, "otherString2", null, new CopyValue());
		try
		{
			new CopySimpleSource(target, null, value);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					templateItemCopyFromTarget, null, value, target,
					"copy violation on " + templateItemCopyFromTarget + ", " +
					"expected null " +
					"from target " + target.getCopeID() + ", " +
					"but was '" + value.getCopeID() + "'", e);
		}
		assertBeforeNewCopeItem(
				CopySimpleSource.targetItem.map(target),
				CopySimpleSource.templateString.map(null),
				CopySimpleSource.templateItem.map(value));

		assertContains(TYPE.search());
		check();
	}

	@Test public void testWrongStringOmittedCopy()
	{
		final CopyValue value = new CopyValue();
		final CopySimpleTarget target = new CopySimpleTarget("template1", "otherString1", value, new CopyValue());
		try
		{
			CopySimpleSource.omitCopy(target);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					templateStringCopyFromTarget, "template1", null, target,
					"copy violation on " + templateStringCopyFromTarget + ", " +
					"expected 'template1' " +
					"from target " + target.getCopeID() + ", " +
					"but was null", e);
		}
		assertBeforeNewCopeItem(
				CopySimpleSource.targetItem.map(target));

		assertContains(TYPE.search());
		check();
	}

	@Test public void testWrongStringOmittedTarget()
	{
		final CopyValue value = new CopyValue();

		final CopySimpleSource source = CopySimpleSource.omitTarget("template1", value);
		assertEquals(null, source.getTargetItem());
		assertEquals("template1", source.getTemplateString());
		assertEquals(value, source.getTemplateItem());
		assertBeforeNewCopeItem(
				CopySimpleSource.templateString.map("template1"),
				CopySimpleSource.templateItem.map(value));

		assertContains(source, TYPE.search());
		check();
	}

	@Test public void testWrongStringOmittedAll()
	{
		final CopySimpleSource source = CopySimpleSource.omitAll();
		assertEquals(null, source.getTargetItem());
		assertEquals(null, source.getTemplateString());
		assertEquals(null, source.getTemplateItem());
		assertBeforeNewCopeItem();

		assertContains(source, TYPE.search());
		check();
	}

	private static final void check()
	{
		assertEquals(0, templateStringCopyFromTarget.check());
		assertEquals(0, templateItemCopyFromTarget.check());
		assertEquals(0, selfTemplateCopyFromTarget.check());
	}


	@Before public void before()
	{
		CopySimpleSource.clearBeforeNewCopeItemLog();
	}

	@After public void after()
	{
		CopySimpleSource.clearBeforeNewCopeItemLog();
	}

	static <E> void assertFails(
			final CopyConstraint feature,
			final E expectedValue,
			final E actualValue,
			final Item targetItem,
			final String message,
			final CopyViolationException actual)
	{
		assertSame  ("feature", feature, actual.getFeature());
		assertEquals("item", null, actual.getItem());
		assertEquals("expectedValue", expectedValue, actual.getExpectedValue());
		assertEquals("actualValue", actualValue, actual.getActualValue());
		assertEquals("targetItem", targetItem, actual.getTargetItem());
		assertEquals("message", message, actual.getMessage());
	}
}
