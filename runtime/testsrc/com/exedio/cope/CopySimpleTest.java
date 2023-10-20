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
import static com.exedio.cope.CopySimpleSource.assertBeforeSetCopeItem;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.tojunit.SI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CopySimpleTest extends TestWithEnvironment
{
	public CopySimpleTest()
	{
		super(CopySimpleModelTest.MODEL);
	}

	@Test void testOk1()
	{
		assertEquals(
				"SELECT COUNT(*) FROM " + SI.tab(TYPE) + " " +
				"JOIN " + SI.tab(CopySimpleTarget.TYPE) + " " +
				"ON " + SI.colq(CopySimpleSource.targetItem) + "=" + SI.pkq(CopySimpleTarget.TYPE) + " " +
				"WHERE ("  + SI.colq(CopySimpleSource.templateString) +               "<>"    + SI.colq(CopySimpleTarget.templateString) + ") " +
					"OR ((" + SI.colq(CopySimpleSource.templateString) + " IS "+ "NULL) AND (" + SI.colq(CopySimpleTarget.templateString) + " IS NOT NULL)) " +
					"OR ((" + SI.colq(CopySimpleSource.templateString) + " IS NOT NULL) AND (" + SI.colq(CopySimpleTarget.templateString) + " IS NULL))",
				SchemaInfo.check(templateStringCopyFromTarget));
		final CopyValue value = new CopyValue();
		final CopySimpleTarget target = new CopySimpleTarget("template1", "otherString1", value, new CopyValue());
		assertContains(TYPE.search());
		check();

		final CopySimpleSource source = new CopySimpleSource(target, "template1", value);
		assertEquals(target, source.getTargetItem());
		assertEquals("template1", source.getTemplateString());
		assertEquals(value, source.getTemplateItem());
		assertBeforeNewCopeItem(
				SetValue.map(CopySimpleSource.targetItem, target),
				SetValue.map(CopySimpleSource.templateString, "template1"),
				SetValue.map(CopySimpleSource.templateItem, value));

		assertContains(source, TYPE.search());
		check();
	}

	@Test void testOk2()
	{
		final CopyValue value = new CopyValue();
		final CopySimpleTarget target = new CopySimpleTarget("template2", "otherString2", value, new CopyValue());

		final CopySimpleSource source = new CopySimpleSource(target, "template2", value);
		assertEquals(target, source.getTargetItem());
		assertEquals("template2", source.getTemplateString());
		assertEquals(value, source.getTemplateItem());
		assertBeforeNewCopeItem(
				SetValue.map(CopySimpleSource.targetItem, target),
				SetValue.map(CopySimpleSource.templateString, "template2"),
				SetValue.map(CopySimpleSource.templateItem, value));

		assertContains(source, TYPE.search());
		check();
	}

	@Test void testOkNullValue()
	{
		final CopySimpleTarget target = new CopySimpleTarget(null, "otherString2", null, new CopyValue());

		final CopySimpleSource source = new CopySimpleSource(target, null, null);
		assertEquals(target, source.getTargetItem());
		assertEquals(null, source.getTemplateString());
		assertEquals(null, source.getTemplateItem());
		assertBeforeNewCopeItem(
				SetValue.map(CopySimpleSource.targetItem, target),
				SetValue.map(CopySimpleSource.templateString, null),
				SetValue.map(CopySimpleSource.templateItem, null));

		assertContains(source, TYPE.search());
		check();
	}

	@Test void testOkNullTarget()
	{
		final CopyValue value = new CopyValue();

		final CopySimpleSource source = new CopySimpleSource(null, "templateN", value);
		assertEquals(null, source.getTargetItem());
		assertEquals("templateN", source.getTemplateString());
		assertEquals(value, source.getTemplateItem());
		assertBeforeNewCopeItem(
				SetValue.map(CopySimpleSource.targetItem, null),
				SetValue.map(CopySimpleSource.templateString, "templateN"),
				SetValue.map(CopySimpleSource.templateItem, value));

		assertContains(source, TYPE.search());
		check();
	}

	@Test void testWrongStringCreate()
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
					"copy violation for " + templateStringCopyFromTarget + ", " +
					"expected 'template2' " +
					"from target " + target.getCopeID() + ", " +
					"but was 'template1'", e);
		}
		assertBeforeNewCopeItem(
				SetValue.map(CopySimpleSource.targetItem, target),
				SetValue.map(CopySimpleSource.templateString, "template1"),
				SetValue.map(CopySimpleSource.templateItem, value));

		assertContains(TYPE.search());
		check();
	}

	@Test void testWrongStringSetCopy()
	{
		final CopyValue value = new CopyValue();
		final CopySimpleTarget target = new CopySimpleTarget("template2", "otherString2", value, new CopyValue());
		final CopySimpleSource source = new CopySimpleSource(target, "template2", value);
		assertBeforeNewCopeItem(
				SetValue.map(CopySimpleSource.targetItem, target),
				SetValue.map(CopySimpleSource.templateString, "template2"),
				SetValue.map(CopySimpleSource.templateItem, value));
		try
		{
			source.setTemplateString("template1");
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					templateStringCopyFromTarget, source, "template2", "template1", target,
					"copy violation on " + source.getCopeID() + " " +
					"for " + templateStringCopyFromTarget + ", " +
					"expected 'template2' " +
					"from target " + target.getCopeID() + ", " +
					"but was 'template1'", e);
		}
		assertBeforeSetCopeItem(source,
				SetValue.map(CopySimpleSource.templateString, "template1"));
		assertEquals("template2", source.getTemplateString());
		check();
	}

	@Test void testWrongStringSetTarget()
	{
		final CopyValue value1 = new CopyValue();
		final CopySimpleTarget target1 = new CopySimpleTarget("template1", "otherString1", value1, new CopyValue());
		final CopySimpleSource source = new CopySimpleSource(target1, "template1", value1);
		assertBeforeNewCopeItem(
				SetValue.map(CopySimpleSource.targetItem, target1),
				SetValue.map(CopySimpleSource.templateString, "template1"),
				SetValue.map(CopySimpleSource.templateItem, value1));
		assertEquals(target1, source.getTargetItem());
		assertEquals("template1", source.getTemplateString());
		assertEquals(value1, source.getTemplateItem());
		check();

		final CopyValue value2 = new CopyValue();
		final CopySimpleTarget target2 = new CopySimpleTarget("template2", "otherString2", value2, new CopyValue());
		source.setTargetItem(target2);
		// wrong string is automatically fixed by cope when setting target
		assertBeforeSetCopeItem(source,
				SetValue.map(CopySimpleSource.targetItem, target2));
		assertEquals(target2, source.getTargetItem());
		assertEquals("template2", source.getTemplateString());
		assertEquals(value2, source.getTemplateItem());
		check();
	}

	@Test void testOkStringSetCopyAndTarget()
	{
		final CopyValue value1 = new CopyValue();
		final CopySimpleTarget target1 = new CopySimpleTarget("template1", "otherString1", value1, new CopyValue());
		final CopySimpleSource source = new CopySimpleSource(target1, "template1", value1);
		assertBeforeNewCopeItem(
				SetValue.map(CopySimpleSource.targetItem, target1),
				SetValue.map(CopySimpleSource.templateString, "template1"),
				SetValue.map(CopySimpleSource.templateItem, value1));
		assertEquals(target1, source.getTargetItem());
		assertEquals("template1", source.getTemplateString());
		assertEquals(value1, source.getTemplateItem());
		check();

		final CopyValue value2 = new CopyValue();
		final CopySimpleTarget target2 = new CopySimpleTarget("template2", "otherString2", value2, new CopyValue());
		source.setTemplateStringAndTargetItem("template2", target2);
		assertBeforeSetCopeItem(source,
				SetValue.map(CopySimpleSource.templateString, "template2"),
				SetValue.map(CopySimpleSource.targetItem, target2));
		assertEquals(target2, source.getTargetItem());
		assertEquals("template2", source.getTemplateString());
		assertEquals(value2, source.getTemplateItem());
		check();
	}

	@Test void testWrongStringSetCopyAndTarget()
	{
		final CopyValue value1 = new CopyValue();
		final CopySimpleTarget target1 = new CopySimpleTarget("template1", "otherString1", value1, new CopyValue());
		final CopySimpleSource source = new CopySimpleSource(target1, "template1", value1);
		assertBeforeNewCopeItem(
				SetValue.map(CopySimpleSource.targetItem, target1),
				SetValue.map(CopySimpleSource.templateString, "template1"),
				SetValue.map(CopySimpleSource.templateItem, value1));
		assertEquals(target1, source.getTargetItem());
		assertEquals("template1", source.getTemplateString());
		assertEquals(value1, source.getTemplateItem());
		check();

		final CopyValue value2 = new CopyValue();
		final CopySimpleTarget target2 = new CopySimpleTarget("template2", "otherString2", value2, new CopyValue());
		try
		{
			source.setTemplateStringAndTargetItem("template1", target2);
			fail();
		}
		catch(final CopyViolationException e)
		{
			assertFails(
					templateStringCopyFromTarget, source, "template2", "template1", target2,
					"copy violation on " + source.getCopeID() + " " +
					"for " + templateStringCopyFromTarget + ", " +
					"expected 'template2' " +
					"from target " + target2.getCopeID() + ", " +
					"but was 'template1'", e);
		}
		assertBeforeSetCopeItem(source,
				SetValue.map(CopySimpleSource.templateString, "template1"),
				SetValue.map(CopySimpleSource.targetItem, target2));
		assertEquals(target1, source.getTargetItem());
		assertEquals("template1", source.getTemplateString());
		assertEquals(value1, source.getTemplateItem());
		check();
	}

	@Test void testWrongStringNullCopy()
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
					"copy violation for " + templateStringCopyFromTarget + ", " +
					"expected 'template2' " +
					"from target " + target.getCopeID() + ", " +
					"but was null", e);
		}
		assertBeforeNewCopeItem(
				SetValue.map(CopySimpleSource.targetItem, target),
				SetValue.map(CopySimpleSource.templateString, null),
				SetValue.map(CopySimpleSource.templateItem, value));

		assertContains(TYPE.search());
		check();
	}

	@Test void testWrongItem()
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
					"copy violation for " + templateItemCopyFromTarget + ", " +
					"expected '" + value2.getCopeID() + "' " +
					"from target " + target.getCopeID() + ", " +
					"but was '" + value1.getCopeID() + "'", e);
		}
		assertBeforeNewCopeItem(
				SetValue.map(CopySimpleSource.targetItem, target),
				SetValue.map(CopySimpleSource.templateString, "template2"),
				SetValue.map(CopySimpleSource.templateItem, value1));

		assertContains(TYPE.search());
		check();
	}

	@Test void testWrongItemNullCopy()
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
					"copy violation for " + templateItemCopyFromTarget + ", " +
					"expected '" + value.getCopeID() + "' " +
					"from target " + target.getCopeID() + ", " +
					"but was null", e);
		}
		assertBeforeNewCopeItem(
				SetValue.map(CopySimpleSource.targetItem, target),
				SetValue.map(CopySimpleSource.templateString, "template2"),
				SetValue.map(CopySimpleSource.templateItem, null));

		assertContains(TYPE.search());
		check();
	}

	@Test void testWrongStringNullTemplate()
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
					"copy violation for " + templateStringCopyFromTarget + ", " +
					"expected null " +
					"from target " + target.getCopeID() + ", " +
					"but was 'template1'", e);
		}
		assertBeforeNewCopeItem(
				SetValue.map(CopySimpleSource.targetItem, target),
				SetValue.map(CopySimpleSource.templateString, "template1"),
				SetValue.map(CopySimpleSource.templateItem, value));

		assertContains(TYPE.search());
		check();
	}

	@Test void testWrongItemNullTemplate()
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
					"copy violation for " + templateItemCopyFromTarget + ", " +
					"expected null " +
					"from target " + target.getCopeID() + ", " +
					"but was '" + value.getCopeID() + "'", e);
		}
		assertBeforeNewCopeItem(
				SetValue.map(CopySimpleSource.targetItem, target),
				SetValue.map(CopySimpleSource.templateString, null),
				SetValue.map(CopySimpleSource.templateItem, value));

		assertContains(TYPE.search());
		check();
	}

	@Test void testOkStringOmittedCopy()
	{
		final CopyValue value = new CopyValue();
		final CopySimpleTarget target = new CopySimpleTarget("template1", "otherString1", value, new CopyValue());

		final CopySimpleSource source = CopySimpleSource.omitCopy(target);
		assertEquals(target, source.getTargetItem());
		assertEquals("template1", source.getTemplateString());
		assertEquals(value, source.getTemplateItem());
		assertBeforeNewCopeItem(
				SetValue.map(CopySimpleSource.targetItem, target));

		assertContains(source, TYPE.search());
		check();

		final CopySimpleTarget target2 = new CopySimpleTarget("template2", "otherString2", value, new CopyValue());
		source.setTargetItem(target2);
		assertBeforeSetCopeItem(source,
				SetValue.map(CopySimpleSource.targetItem, target2));
		assertEquals(target2, source.getTargetItem());
		assertEquals("template2", source.getTemplateString());
		assertEquals(value, source.getTemplateItem());
	}

	@Test void testOkStringOmittedTarget()
	{
		final CopyValue value = new CopyValue();

		final CopySimpleSource source = CopySimpleSource.omitTarget("template1", value);
		assertEquals(null, source.getTargetItem());
		assertEquals("template1", source.getTemplateString());
		assertEquals(value, source.getTemplateItem());
		assertBeforeNewCopeItem(
				SetValue.map(CopySimpleSource.templateString, "template1"),
				SetValue.map(CopySimpleSource.templateItem, value));

		assertContains(source, TYPE.search());
		check();
	}

	@Test void testOkStringOmittedAll()
	{
		final CopySimpleSource source = CopySimpleSource.omitAll();
		assertEquals(null, source.getTargetItem());
		assertEquals(null, source.getTemplateString());
		assertEquals(null, source.getTemplateItem());
		assertBeforeNewCopeItem();

		assertContains(source, TYPE.search());
		check();
	}

	private static void check()
	{
		assertEquals(0, templateStringCopyFromTarget.check());
		assertEquals(0, templateItemCopyFromTarget.check());
		assertEquals(0, selfTemplateCopyFromTarget.check());
	}


	@BeforeEach void before()
	{
		CopySimpleSource.clearBeforeCopeItemLog();
	}

	@AfterEach void after()
	{
		CopySimpleSource.clearBeforeCopeItemLog();
	}

	static <E> void assertFails(
			final CopyConstraint feature,
			final E expectedValue,
			final E actualValue,
			final Item targetItem,
			final String message,
			final CopyViolationException actual)
	{
		assertFails(feature, null, expectedValue, actualValue, targetItem, message, actual);
	}

	static <E> void assertFails(
			final CopyConstraint feature,
			final Item item,
			final E expectedValue,
			final E actualValue,
			final Item targetItem,
			final String message,
			final CopyViolationException actual)
	{
		assertFails(feature, null, item, expectedValue, actualValue, targetItem, null, message, actual);
	}

	static <E> void assertFails(
			final CopyConstraint feature,
			final CopyConstraint additionalFeature,
			final Item item,
			final E expectedValue,
			final E actualValue,
			final Item targetItem,
			final Item additionalTargetItem,
			final String message,
			final CopyViolationException actual)
	{
		assertSame  (feature, actual.getFeature(), "feature");
		assertSame  (additionalFeature, actual.getAdditionalFeature(), "additionalFeature");
		assertEquals(item, actual.getItem(), "item");
		assertEquals(expectedValue, actual.getExpectedValue(), "expectedValue");
		assertEquals(actualValue, actual.getActualValue(), "actualValue");
		assertEquals(targetItem, actual.getTargetItem(), "targetItem");
		assertEquals(additionalTargetItem, actual.getAdditionalTargetItem(), "additionalTargetItem");
		assertEquals(message, actual.getMessage(), "message");
	}
}
