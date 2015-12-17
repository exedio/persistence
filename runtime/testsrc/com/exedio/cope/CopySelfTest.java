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

import static com.exedio.cope.Assert.assertContains;
import static com.exedio.cope.CopyModelTest.selfTemplateItemCopyFromTarget;
import static com.exedio.cope.CopyModelTest.templateItemCopyFromTarget;
import static com.exedio.cope.CopyModelTest.templateStringCopyFromTarget;
import static com.exedio.cope.CopySelfSourceItem.TYPE;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CopySelfTest extends AbstractRuntimeModelTest
{
	public CopySelfTest()
	{
		super(CopyModelTest.MODEL);
	}

	@Test public void testOk1()
	{
		final CopyValueItem value = new CopyValueItem();
		final CopySelfSourceItem self = new CopySelfSourceItem(null, value);
		assertContains(self, TYPE.search());
		check();

		final CopySelfSourceItem source = new CopySelfSourceItem(self, value);
		assertContains(self, source, TYPE.search());
		assertEquals(self, source.getSelfTargetItem());
		assertEquals(value, source.getSelfTemplateItem());
		check();
	}

	@Test public void testOk2()
	{
		final CopyValueItem value = new CopyValueItem();
		final CopySelfSourceItem self = new CopySelfSourceItem(null, value);

		final CopySelfSourceItem source = new CopySelfSourceItem(self, value);
		assertContains(self, source, TYPE.search());
		assertEquals(self, source.getSelfTargetItem());
		assertEquals(value, source.getSelfTemplateItem());
		check();
	}

	@Test public void testOkNullValue()
	{
		final CopySelfSourceItem self = new CopySelfSourceItem(null, null);

		final CopySelfSourceItem source = new CopySelfSourceItem(self, null);
		assertContains(self, source, TYPE.search());
		assertEquals(self, source.getSelfTargetItem());
		assertEquals(null, source.getSelfTemplateItem());
		check();
	}

	@Test public void testOkNullTarget()
	{
		final CopyValueItem value = new CopyValueItem();

		final CopySelfSourceItem source = new CopySelfSourceItem(null, value);
		assertContains(source, TYPE.search());
		assertEquals(null, source.getSelfTargetItem());
		assertEquals(value, source.getSelfTemplateItem());
		check();
	}

	private static final void check()
	{
		assertEquals(0, templateStringCopyFromTarget.check());
		assertEquals(0, templateItemCopyFromTarget.check());
		assertEquals(0, selfTemplateItemCopyFromTarget.check());
	}
}
