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

import static com.exedio.cope.CopySelfSource.TYPE;
import static com.exedio.cope.CopySimpleModelTest.selfTemplateCopyFromTarget;
import static com.exedio.cope.CopySimpleModelTest.templateItemCopyFromTarget;
import static com.exedio.cope.CopySimpleModelTest.templateStringCopyFromTarget;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CopySelfTest extends TestWithEnvironment
{
	public CopySelfTest()
	{
		super(CopySimpleModelTest.MODEL);
	}

	@Test public void testOk1()
	{
		final CopyValue value = new CopyValue();
		final CopySelfSource self = new CopySelfSource(null, value);
		assertContains(self, TYPE.search());
		check();

		final CopySelfSource source = new CopySelfSource(self, value);
		assertContains(self, source, TYPE.search());
		assertEquals(self, source.getSelfTarget());
		assertEquals(value, source.getSelfTemplate());
		check();
	}

	@Test public void testOk2()
	{
		final CopyValue value = new CopyValue();
		final CopySelfSource self = new CopySelfSource(null, value);

		final CopySelfSource source = new CopySelfSource(self, value);
		assertContains(self, source, TYPE.search());
		assertEquals(self, source.getSelfTarget());
		assertEquals(value, source.getSelfTemplate());
		check();
	}

	@Test public void testOkNullValue()
	{
		final CopySelfSource self = new CopySelfSource(null, null);

		final CopySelfSource source = new CopySelfSource(self, null);
		assertContains(self, source, TYPE.search());
		assertEquals(self, source.getSelfTarget());
		assertEquals(null, source.getSelfTemplate());
		check();
	}

	@Test public void testOkNullTarget()
	{
		final CopyValue value = new CopyValue();

		final CopySelfSource source = new CopySelfSource(null, value);
		assertContains(source, TYPE.search());
		assertEquals(null, source.getSelfTarget());
		assertEquals(value, source.getSelfTemplate());
		check();
	}

	private static final void check()
	{
		assertEquals(0, templateStringCopyFromTarget.check());
		assertEquals(0, templateItemCopyFromTarget.check());
		assertEquals(0, selfTemplateCopyFromTarget.check());
	}
}
