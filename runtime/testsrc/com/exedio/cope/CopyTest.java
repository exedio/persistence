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

import static com.exedio.cope.CopyModelTest.selfTemplateItemCopyFromTarget;
import static com.exedio.cope.CopyModelTest.templateItemCopyFromTarget;
import static com.exedio.cope.CopyModelTest.templateStringCopyFromTarget;
import static com.exedio.cope.CopySourceItem.TYPE;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class CopyTest extends AbstractRuntimeModelTest
{
	public CopyTest()
	{
		super(CopyModelTest.MODEL);
	}

	@Test public void testOk1()
	{
		final CopyValueItem value = new CopyValueItem();
		final CopyTargetItem target = new CopyTargetItem("template1", "otherString1", value, new CopyValueItem());
		assertContains(TYPE.search());
		check();

		final CopySourceItem source = new CopySourceItem(target, "template1", value);
		assertEquals(target, source.getTargetItem());
		assertEquals("template1", source.getTemplateString());
		assertEquals(value, source.getTemplateItem());
		assertContains(source, TYPE.search());
		check();
	}

	@Test public void testOk2()
	{
		final CopyValueItem value = new CopyValueItem();
		final CopyTargetItem target = new CopyTargetItem("template2", "otherString2", value, new CopyValueItem());

		final CopySourceItem source = new CopySourceItem(target, "template2", value);
		assertEquals(target, source.getTargetItem());
		assertEquals("template2", source.getTemplateString());
		assertEquals(value, source.getTemplateItem());
		assertContains(source, TYPE.search());
		check();
	}

	@Test public void testOkNullValue()
	{
		final CopyTargetItem target = new CopyTargetItem(null, "otherString2", null, new CopyValueItem());

		final CopySourceItem source = new CopySourceItem(target, null, null);
		assertEquals(target, source.getTargetItem());
		assertEquals(null, source.getTemplateString());
		assertEquals(null, source.getTemplateItem());
		assertContains(source, TYPE.search());
		check();
	}

	@Test public void testOkNullTarget()
	{
		final CopyValueItem value = new CopyValueItem();

		final CopySourceItem source = new CopySourceItem(null, "templateN", value);
		assertEquals(null, source.getTargetItem());
		assertEquals("templateN", source.getTemplateString());
		assertEquals(value, source.getTemplateItem());
		assertContains(source, TYPE.search());
		check();
	}

	@Test public void testWrongString()
	{
		final CopyValueItem value = new CopyValueItem();
		final CopyTargetItem target = new CopyTargetItem("template2", "otherString2", value, new CopyValueItem());
		try
		{
			new CopySourceItem(target, "template1", value);
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
		assertContains(TYPE.search());
		check();
	}

	@Test public void testWrongStringNullCopy()
	{
		final CopyValueItem value = new CopyValueItem();
		final CopyTargetItem target = new CopyTargetItem("template2", "otherString2", value, new CopyValueItem());
		try
		{
			new CopySourceItem(target, null, value);
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
		assertContains(TYPE.search());
		check();
	}

	@Test public void testWrongItem()
	{
		final CopyValueItem value1 = new CopyValueItem();
		final CopyValueItem value2 = new CopyValueItem();
		final CopyTargetItem target = new CopyTargetItem("template2", "otherString2", value2, new CopyValueItem());
		try
		{
			new CopySourceItem(target, "template2", value1);
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
		assertContains(TYPE.search());
		check();
	}

	@Test public void testWrongItemNullCopy()
	{
		final CopyValueItem value = new CopyValueItem();
		final CopyTargetItem target = new CopyTargetItem("template2", "otherString2", value, new CopyValueItem());
		try
		{
			new CopySourceItem(target, "template2", null);
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
		assertContains(TYPE.search());
		check();
	}

	@Test public void testWrongStringNullTemplate()
	{
		final CopyValueItem value = new CopyValueItem();
		final CopyTargetItem target = new CopyTargetItem(null, "otherString2", null, new CopyValueItem());
		try
		{
			new CopySourceItem(target, "template1", value);
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
		assertContains(TYPE.search());
		check();
	}

	@Test public void testWrongItemNullTemplate()
	{
		final CopyValueItem value = new CopyValueItem();
		final CopyTargetItem target = new CopyTargetItem(null, "otherString2", null, new CopyValueItem());
		try
		{
			new CopySourceItem(target, null, value);
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
		assertContains(TYPE.search());
		check();
	}

	@Test public void testWrongStringOmittedCopy()
	{
		final CopyValueItem value = new CopyValueItem();
		final CopyTargetItem target = new CopyTargetItem("template1", "otherString1", value, new CopyValueItem());
		try
		{
			new CopySourceItem(target);
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
		assertContains(TYPE.search());
		check();
	}

	private static final void check()
	{
		assertEquals(0, templateStringCopyFromTarget.check());
		assertEquals(0, templateItemCopyFromTarget.check());
		assertEquals(0, selfTemplateItemCopyFromTarget.check());
	}
}
