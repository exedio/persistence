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
	CopySourceItem self1, self2, selfN;

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
		self1 = new CopySourceItem(null, null, null, null, value1);
		self2 = new CopySourceItem(null, null, null, null, value2);
		selfN = new CopySourceItem(null, null, null, null, null);
	}

	public void testIt()
	{
		assertContains(self1, self2, selfN, TYPE.search());
		check();

		final CopySourceItem source1 = new CopySourceItem(target1, "template1", value1, self1, value1);
		assertEquals(target1, source1.getTargetItem());
		assertEquals("template1", source1.getTemplateString());
		assertEquals(value1, source1.getTemplateItem());
		assertContains(self1, self2, selfN, source1, TYPE.search());
		assertEquals(self1, source1.getSelfTargetItem());
		assertEquals(value1, source1.getSelfTemplateItem());
		check();

		final CopySourceItem source2 = new CopySourceItem(target2, "template2", value2, self2, value2);
		assertEquals(target2, source2.getTargetItem());
		assertEquals("template2", source2.getTemplateString());
		assertEquals(value2, source2.getTemplateItem());
		assertContains(self1, self2, selfN, source1, source2, TYPE.search());
		assertEquals(self2, source2.getSelfTargetItem());
		assertEquals(value2, source2.getSelfTemplateItem());
		check();

		final CopySourceItem sourceN = new CopySourceItem(targetN, null, null, selfN, null);
		assertEquals(targetN, sourceN.getTargetItem());
		assertEquals(null, sourceN.getTemplateString());
		assertEquals(null, sourceN.getTemplateItem());
		assertContains(self1, self2, selfN, source1, source2, sourceN, TYPE.search());
		assertEquals(selfN, sourceN.getSelfTargetItem());
		assertEquals(null, sourceN.getSelfTemplateItem());
		check();

		final CopySourceItem sourceNT = new CopySourceItem(null, "templateN", value2, null, value1);
		assertEquals(null, sourceNT.getTargetItem());
		assertEquals("templateN", sourceNT.getTemplateString());
		assertEquals(value2, sourceNT.getTemplateItem());
		assertContains(self1, self2, selfN, source1, source2, sourceN, sourceNT, TYPE.search());
		assertEquals(null, sourceNT.getSelfTargetItem());
		assertEquals(value1, sourceNT.getSelfTemplateItem());
		check();

		try
		{
			new CopySourceItem(target2, "template1", value2, null, null);
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
		assertContains(self1, self2, selfN, source1, source2, sourceN, sourceNT, TYPE.search());
		check();

		try
		{
			new CopySourceItem(target2, null, value2, null, null);
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
		assertContains(self1, self2, selfN, source1, source2, sourceN, sourceNT, TYPE.search());
		check();

		try
		{
			new CopySourceItem(target2, "template2", value1, null, null);
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
		assertContains(self1, self2, selfN, source1, source2, sourceN, sourceNT, TYPE.search());
		check();

		try
		{
			new CopySourceItem(target2, "template2", null, null, null);
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
		assertContains(self1, self2, selfN, source1, source2, sourceN, sourceNT, TYPE.search());
		check();

		try
		{
			new CopySourceItem(targetN, "template1", value2, null, null);
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
		assertContains(self1, self2, selfN, source1, source2, sourceN, sourceNT, TYPE.search());
		check();

		try
		{
			new CopySourceItem(targetN, null, value1, null, null);
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
		assertContains(self1, self2, selfN, source1, source2, sourceN, sourceNT, TYPE.search());
		check();

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
		assertContains(self1, self2, selfN, source1, source2, sourceN, sourceNT, TYPE.search());
		check();
	}

	private static final void check()
	{
		assertEquals(0, templateStringCopyFromTarget.check());
		assertEquals(0, templateItemCopyFromTarget.check());
		assertEquals(0, selfTemplateItemCopyFromTarget.check());
	}
}
