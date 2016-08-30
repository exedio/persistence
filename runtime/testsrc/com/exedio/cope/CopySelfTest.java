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
import static org.junit.Assert.fail;

import org.junit.Test;

public class CopySelfTest extends TestWithEnvironment
{
	public CopySelfTest()
	{
		super(CopySimpleModelTest.MODEL);
	}

	@Test public void testOk()
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

	@Test public void testWrong()
	{
		final CopyValue value1 = new CopyValue();
		final CopyValue value2 = new CopyValue();
		final CopySelfSource target = new CopySelfSource(null, value2);
		try
		{
			new CopySelfSource(target, value1);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertEquals(selfTemplateCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(value2, e.getExpectedValue());
			assertEquals(value1, e.getActualValue());
			assertEquals(target, e.getTargetItem());
			assertEquals(
					"copy violation on " + selfTemplateCopyFromTarget + ", " +
					"expected '" + value2.getCopeID() + "' " +
					"from target " + target.getCopeID() + ", " +
					"but was '" + value1.getCopeID() + "'",
				e.getMessage());
		}

		assertContains(target, TYPE.search());
		check();
	}

	@Test public void testWrongNullCopy()
	{
		final CopyValue value = new CopyValue();
		final CopySelfSource target = new CopySelfSource(null, value);
		try
		{
			new CopySelfSource(target, null);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertEquals(selfTemplateCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(value, e.getExpectedValue());
			assertEquals(null, e.getActualValue());
			assertEquals(target, e.getTargetItem());
			assertEquals(
					"copy violation on " + selfTemplateCopyFromTarget + ", " +
					"expected '" + value.getCopeID() + "' " +
					"from target " + target.getCopeID() + ", " +
					"but was null",
				e.getMessage());
		}

		assertContains(target, TYPE.search());
		check();
	}

	@Test public void testWrongNullTemplate()
	{
		final CopyValue value = new CopyValue();
		final CopySelfSource target = new CopySelfSource(null, null);
		try
		{
			new CopySelfSource(target, value);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertEquals(selfTemplateCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(null, e.getExpectedValue());
			assertEquals(value, e.getActualValue());
			assertEquals(target, e.getTargetItem());
			assertEquals(
					"copy violation on " + selfTemplateCopyFromTarget + ", " +
					"expected null " +
					"from target " + target.getCopeID() + ", " +
					"but was '" + value.getCopeID() + "'",
				e.getMessage());
		}

		assertContains(target, TYPE.search());
		check();
	}

	@Test public void testWrongOmittedCopy()
	{
		final CopyValue value = new CopyValue();
		final CopySelfSource target = new CopySelfSource((CopySelfSource)null, value);
		try
		{
			CopySelfSource.omitCopy(target);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertEquals(selfTemplateCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(value, e.getExpectedValue());
			assertEquals(null, e.getActualValue());
			assertEquals(target, e.getTargetItem());
			assertEquals(
					"copy violation on " + selfTemplateCopyFromTarget + ", " +
					"expected '" + value.getCopeID() + "' " +
					"from target " + target.getCopeID() + ", " +
					"but was null",
				e.getMessage());
		}

		assertContains(target, TYPE.search());
		check();
	}

	@Test public void testWrongOmittedTarget()
	{
		final CopyValue value = new CopyValue();

		final CopySelfSource source = CopySelfSource.omitTarget(value);
		assertEquals(null, source.getSelfTarget());
		assertEquals(value, source.getSelfTemplate());

		assertContains(source, TYPE.search());
		check();
	}

	@Test public void testWrongOmittedAll()
	{
		final CopySelfSource source = CopySelfSource.omitAll();
		assertEquals(null, source.getSelfTarget());
		assertEquals(null, source.getSelfTemplate());

		assertContains(source, TYPE.search());
		check();
	}

	private static final void check()
	{
		assertEquals(0, templateStringCopyFromTarget.check());
		assertEquals(0, templateItemCopyFromTarget.check());
		assertEquals(0, selfTemplateCopyFromTarget.check());
	}
}
