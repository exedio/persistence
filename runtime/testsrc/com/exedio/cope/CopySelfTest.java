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
import static com.exedio.cope.CopySelfSourceItem.TYPE;

public class CopySelfTest extends AbstractRuntimeModelTest
{
	public CopySelfTest()
	{
		super(CopyModelTest.MODEL);
	}

	public void testOk1()
	{
		final CopyValueItem value1 = new CopyValueItem("value1");
		final CopySelfSourceItem self1 = new CopySelfSourceItem(null, value1);
		assertContains(self1, TYPE.search());
		check();

		final CopySelfSourceItem source = new CopySelfSourceItem(self1, value1);
		assertContains(self1, source, TYPE.search());
		assertEquals(self1, source.getSelfTargetItem());
		assertEquals(value1, source.getSelfTemplateItem());
		check();
	}

	public void testOk2()
	{
		final CopyValueItem value2 = new CopyValueItem("value2");
		final CopySelfSourceItem self2 = new CopySelfSourceItem(null, value2);

		final CopySelfSourceItem source = new CopySelfSourceItem(self2, value2);
		assertContains(self2, source, TYPE.search());
		assertEquals(self2, source.getSelfTargetItem());
		assertEquals(value2, source.getSelfTemplateItem());
		check();
	}

	public void testOkNullValue()
	{
		final CopySelfSourceItem selfN = new CopySelfSourceItem(null, null);

		final CopySelfSourceItem source = new CopySelfSourceItem(selfN, null);
		assertContains(selfN, source, TYPE.search());
		assertEquals(selfN, source.getSelfTargetItem());
		assertEquals(null, source.getSelfTemplateItem());
		check();
	}

	public void testOkNullTarget()
	{
		final CopyValueItem value1 = new CopyValueItem("value1");

		final CopySelfSourceItem source = new CopySelfSourceItem(null, value1);
		assertContains(source, TYPE.search());
		assertEquals(null, source.getSelfTargetItem());
		assertEquals(value1, source.getSelfTemplateItem());
		check();
	}

	private static final void check()
	{
		assertEquals(0, templateStringCopyFromTarget.check());
		assertEquals(0, templateItemCopyFromTarget.check());
		assertEquals(0, selfTemplateItemCopyFromTarget.check());
	}
}
