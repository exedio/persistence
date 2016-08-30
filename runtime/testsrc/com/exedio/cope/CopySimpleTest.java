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
			assertEquals(templateStringCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("template2", e.getExpectedValue());
			assertEquals("template1", e.getActualValue());
			assertEquals(target, e.getTargetItem());
			assertEquals(
					"copy violation on " + templateStringCopyFromTarget + ", " +
					"expected 'template2' " +
					"from target " + target.getCopeID() + ", " +
					"but was 'template1'",
				e.getMessage());
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
			assertEquals(templateStringCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("template2", e.getExpectedValue());
			assertEquals(null, e.getActualValue());
			assertEquals(target, e.getTargetItem());
			assertEquals(
					"copy violation on " + templateStringCopyFromTarget + ", " +
					"expected 'template2' " +
					"from target " + target.getCopeID() + ", " +
					"but was null",
				e.getMessage());
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
			assertEquals(templateItemCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(value2, e.getExpectedValue());
			assertEquals(value1, e.getActualValue());
			assertEquals(target, e.getTargetItem());
			assertEquals(
					"copy violation on " + templateItemCopyFromTarget + ", " +
					"expected '" + value2.getCopeID() + "' " +
					"from target " + target.getCopeID() + ", " +
					"but was '" + value1.getCopeID() + "'",
				e.getMessage());
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
			assertEquals(templateItemCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(value, e.getExpectedValue());
			assertEquals(null, e.getActualValue());
			assertEquals(target, e.getTargetItem());
			assertEquals(
					"copy violation on " + templateItemCopyFromTarget + ", " +
					"expected '" + value.getCopeID() + "' " +
					"from target " + target.getCopeID() + ", " +
					"but was null",
				e.getMessage());
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
			assertEquals(templateStringCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(null, e.getExpectedValue());
			assertEquals("template1", e.getActualValue());
			assertEquals(target, e.getTargetItem());
			assertEquals(
					"copy violation on " + templateStringCopyFromTarget + ", " +
					"expected null " +
					"from target " + target.getCopeID() + ", " +
					"but was 'template1'",
				e.getMessage());
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
			assertEquals(templateItemCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(null, e.getExpectedValue());
			assertEquals(value, e.getActualValue());
			assertEquals(target, e.getTargetItem());
			assertEquals(
					"copy violation on " + templateItemCopyFromTarget + ", " +
					"expected null " +
					"from target " + target.getCopeID() + ", " +
					"but was '" + value.getCopeID() + "'",
				e.getMessage());
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
			new CopySimpleSource(target);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertEquals(templateStringCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("template1", e.getExpectedValue());
			assertEquals(null, e.getActualValue());
			assertEquals(target, e.getTargetItem());
			assertEquals(
					"copy violation on " + templateStringCopyFromTarget + ", " +
					"expected 'template1' " +
					"from target " + target.getCopeID() + ", " +
					"but was null",
				e.getMessage());
		}
		assertBeforeNewCopeItem(
				CopySimpleSource.targetItem.map(target));

		assertContains(TYPE.search());
		check();
	}

	@Test public void testWrongStringOmittedTarget()
	{
		final CopyValue value = new CopyValue();

		final CopySimpleSource source = new CopySimpleSource("template1", value);
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
		final CopySimpleSource source = new CopySimpleSource();
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
}
