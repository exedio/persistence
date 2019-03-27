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
import static com.exedio.cope.CopySimpleTest.assertFails;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CopySelfTest extends TestWithEnvironment
{
	public CopySelfTest()
	{
		super(CopySimpleModelTest.MODEL);
	}

	@Test void testOk()
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

		final CopySelfSource sourceSet = new CopySelfSource(self, value);
		source.setSelfTarget(sourceSet);
		assertEquals(sourceSet, source.getSelfTarget());
		assertEquals(value, source.getSelfTemplate());
		check();
	}

	@Test void testOkNullValue()
	{
		final CopySelfSource self = new CopySelfSource(null, null);

		final CopySelfSource source = new CopySelfSource(self, null);
		assertContains(self, source, TYPE.search());
		assertEquals(self, source.getSelfTarget());
		assertEquals(null, source.getSelfTemplate());
		check();
	}

	@Test void testOkNullTarget()
	{
		final CopyValue value = new CopyValue();

		final CopySelfSource source = new CopySelfSource(null, value);
		assertContains(source, TYPE.search());
		assertEquals(null, source.getSelfTarget());
		assertEquals(value, source.getSelfTemplate());
		check();
	}

	@Test void testWrong()
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
			assertFails(
					selfTemplateCopyFromTarget, value2, value1, target,
					"copy violation for " + selfTemplateCopyFromTarget + ", " +
					"expected '" + value2.getCopeID() + "' " +
					"from target " + target.getCopeID() + ", " +
					"but was '" + value1.getCopeID() + "'", e);
		}

		assertContains(target, TYPE.search());
		check();

		// testing setter not needed as selfTemplate is final
	}

	@Test void testWrongNullCopy()
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
			assertFails(
					selfTemplateCopyFromTarget, value, null, target,
					"copy violation for " + selfTemplateCopyFromTarget + ", " +
					"expected '" + value.getCopeID() + "' " +
					"from target " + target.getCopeID() + ", " +
					"but was null", e);
		}

		assertContains(target, TYPE.search());
		check();
	}

	@Test void testWrongNullTemplate()
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
			assertFails(
					selfTemplateCopyFromTarget, null, value, target,
					"copy violation for " + selfTemplateCopyFromTarget + ", " +
					"expected null " +
					"from target " + target.getCopeID() + ", " +
					"but was '" + value.getCopeID() + "'", e);
		}

		assertContains(target, TYPE.search());
		check();
	}

	@Test void testOkOmittedCopy()
	{
		final CopyValue value = new CopyValue();
		final CopySelfSource target = new CopySelfSource(null, value);

		final CopySelfSource source = CopySelfSource.omitCopy(target);
		assertEquals(target, source.getSelfTarget());
		assertEquals(value, source.getSelfTemplate());

		assertContains(target, source, TYPE.search());
		check();

		final CopyValue valueSet = new CopyValue();
		final CopySelfSource targetSet = new CopySelfSource(null, valueSet);
		try
		{
			source.setSelfTarget(targetSet);
			fail();
		}
		catch(final CopyViolationException e)
		{
			// failure is ok, as selfTemplate is final and therefore cannot be set consistent to targetSet
			assertFails(
					selfTemplateCopyFromTarget, source, valueSet, value, targetSet,
					"copy violation on " + source.getCopeID() + " " +
					"for " + selfTemplateCopyFromTarget + ", " +
					"expected '" + valueSet.getCopeID() + "' " +
					"from target " + targetSet.getCopeID() + ", " +
					"but was '" + value.getCopeID() + "'", e);
		}
		assertEquals(target, source.getSelfTarget());
		assertEquals(value, source.getSelfTemplate());
		check();
	}

	@Test void testOkOmittedTarget()
	{
		final CopyValue value = new CopyValue();

		final CopySelfSource source = CopySelfSource.omitTarget(value);
		assertEquals(null, source.getSelfTarget());
		assertEquals(value, source.getSelfTemplate());

		assertContains(source, TYPE.search());
		check();

		// testing setter not needed as selfTemplate is final
	}

	@Test void testOkOmittedAll()
	{
		final CopySelfSource source = CopySelfSource.omitAll();
		assertEquals(null, source.getSelfTarget());
		assertEquals(null, source.getSelfTemplate());

		assertContains(source, TYPE.search());
		check();
	}

	private static void check()
	{
		assertEquals(0, templateStringCopyFromTarget.check());
		assertEquals(0, templateItemCopyFromTarget.check());
		assertEquals(0, selfTemplateCopyFromTarget.check());
	}
}
