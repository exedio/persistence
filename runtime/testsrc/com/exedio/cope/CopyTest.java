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

public class CopyTest extends AbstractRuntimeModelTest
{
	public CopyTest()
	{
		super(CopyModelTest.MODEL);
	}

	CopyValueItem value1, value2, valueX;
	CopyTargetItem target1, target2, targetN;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		value1 = new CopyValueItem("value1");
		value2 = new CopyValueItem("value2");
		valueX = new CopyValueItem("valueX");
		target1 = new CopyTargetItem("target1", "template1", "otherString1", value1, valueX);
		target2 = new CopyTargetItem("target2", "template2", "otherString2", value2, valueX);
		targetN = new CopyTargetItem("targetN", null, "otherString2", null, valueX);
	}

	public void testOk1()
	{
		assertContains(TYPE.search());
		check();

		final CopySourceItem source = new CopySourceItem(target1, "template1", value1);
		assertEquals(target1, source.getTargetItem());
		assertEquals("template1", source.getTemplateString());
		assertEquals(value1, source.getTemplateItem());
		assertContains(source, TYPE.search());
		check();
	}

	public void testOk2()
	{
		final CopySourceItem source = new CopySourceItem(target2, "template2", value2);
		assertEquals(target2, source.getTargetItem());
		assertEquals("template2", source.getTemplateString());
		assertEquals(value2, source.getTemplateItem());
		assertContains(source, TYPE.search());
		check();
	}

	public void testOkNullValue()
	{
		final CopySourceItem source = new CopySourceItem(targetN, null, null);
		assertEquals(targetN, source.getTargetItem());
		assertEquals(null, source.getTemplateString());
		assertEquals(null, source.getTemplateItem());
		assertContains(source, TYPE.search());
		check();
	}

	public void testOkNullTarget()
	{
		final CopySourceItem source = new CopySourceItem(null, "templateN", value2);
		assertEquals(null, source.getTargetItem());
		assertEquals("templateN", source.getTemplateString());
		assertEquals(value2, source.getTemplateItem());
		assertContains(source, TYPE.search());
		check();
	}

	public void testWrongString()
	{
		try
		{
			new CopySourceItem(target2, "template1", value2);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertEquals(templateStringCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("template2", e.getExpectedValue());
			assertEquals("template1", e.getActualValue());
			assertEquals(target2, e.getTargetItem());
			assertEquals(
					"copy violation on " + templateStringCopyFromTarget + ", " +
					"expected 'template2' " +
					"from target " + target2.getCopeID() + ", " +
					"but was 'template1'",
				e.getMessage());
		}
		assertContains(TYPE.search());
		check();
	}

	public void testWrongStringNullCopy()
	{
		try
		{
			new CopySourceItem(target2, null, value2);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertEquals(templateStringCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("template2", e.getExpectedValue());
			assertEquals(null, e.getActualValue());
			assertEquals(target2, e.getTargetItem());
			assertEquals(
					"copy violation on " + templateStringCopyFromTarget + ", " +
					"expected 'template2' " +
					"from target " + target2.getCopeID() + ", " +
					"but was null",
				e.getMessage());
		}
		assertContains(TYPE.search());
		check();
	}

	public void testWrongItem()
	{
		try
		{
			new CopySourceItem(target2, "template2", value1);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertEquals(templateItemCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(value2, e.getExpectedValue());
			assertEquals(value1, e.getActualValue());
			assertEquals(target2, e.getTargetItem());
			assertEquals(
					"copy violation on " + templateItemCopyFromTarget + ", " +
					"expected '" + value2.getCopeID() + "' " +
					"from target " + target2.getCopeID() + ", " +
					"but was '" + value1.getCopeID() + "'",
				e.getMessage());
		}
		assertContains(TYPE.search());
		check();
	}

	public void testWrongItemNullCopy()
	{
		try
		{
			new CopySourceItem(target2, "template2", null);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertEquals(templateItemCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(value2, e.getExpectedValue());
			assertEquals(null, e.getActualValue());
			assertEquals(target2, e.getTargetItem());
			assertEquals(
					"copy violation on " + templateItemCopyFromTarget + ", " +
					"expected '" + value2.getCopeID() + "' " +
					"from target " + target2.getCopeID() + ", " +
					"but was null",
				e.getMessage());
		}
		assertContains(TYPE.search());
		check();
	}

	public void testWrongStringNullTemplate()
	{
		try
		{
			new CopySourceItem(targetN, "template1", value2);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertEquals(templateStringCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(null, e.getExpectedValue());
			assertEquals("template1", e.getActualValue());
			assertEquals(targetN, e.getTargetItem());
			assertEquals(
					"copy violation on " + templateStringCopyFromTarget + ", " +
					"expected null " +
					"from target " + targetN.getCopeID() + ", " +
					"but was 'template1'",
				e.getMessage());
		}
		assertContains(TYPE.search());
		check();
	}

	public void testWrongItemNullTemplate()
	{
		try
		{
			new CopySourceItem(targetN, null, value1);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertEquals(templateItemCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(null, e.getExpectedValue());
			assertEquals(value1, e.getActualValue());
			assertEquals(targetN, e.getTargetItem());
			assertEquals(
					"copy violation on " + templateItemCopyFromTarget + ", " +
					"expected null " +
					"from target " + targetN.getCopeID() + ", " +
					"but was '" + value1.getCopeID() + "'",
				e.getMessage());
		}
		assertContains(TYPE.search());
		check();
	}

	public void testWrongStringOmittedCopy()
	{
		try
		{
			new CopySourceItem(target1);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertEquals(templateStringCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("template1", e.getExpectedValue());
			assertEquals(null, e.getActualValue());
			assertEquals(target1, e.getTargetItem());
			assertEquals(
					"copy violation on " + templateStringCopyFromTarget + ", " +
					"expected 'template1' " +
					"from target " + target1.getCopeID() + ", " +
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
