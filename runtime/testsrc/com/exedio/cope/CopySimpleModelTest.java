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

import static com.exedio.cope.CopySelfSource.selfTarget;
import static com.exedio.cope.CopySelfSource.selfTemplate;
import static com.exedio.cope.CopySimpleSource.TYPE;
import static com.exedio.cope.CopySimpleSource.targetItem;
import static com.exedio.cope.CopySimpleSource.templateItem;
import static com.exedio.cope.CopySimpleSource.templateString;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import org.junit.Test;

public class CopySimpleModelTest
{
	public static final Model MODEL = new Model(TYPE, CopySimpleTarget.TYPE, CopyValue.TYPE, CopySelfSource.TYPE);

	static
	{
		MODEL.enableSerialization(CopySimpleModelTest.class, "MODEL");
	}

	static final CopyConstraint templateStringCopyFromTarget = (CopyConstraint)TYPE.getFeature("templateStringCopyFromtargetItem");
	static final CopyConstraint templateItemCopyFromTarget   = (CopyConstraint)TYPE.getFeature("templateItemCopyFromtargetItem");
	static final CopyConstraint selfTemplateCopyFromTarget   = (CopyConstraint)CopySelfSource.TYPE.getFeature("selfTemplateCopyFromselfTarget");

	@Test public void testIt()
	{
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				targetItem,
				templateString,
				templateStringCopyFromTarget,
				templateItem,
				templateItemCopyFromTarget,
			}), TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				targetItem,
				templateString,
				templateStringCopyFromTarget,
				templateItem,
				templateItemCopyFromTarget,
			}), TYPE.getDeclaredFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				CopySelfSource.TYPE.getThis(),
				selfTarget,
				selfTemplate,
				selfTemplateCopyFromTarget,
			}), CopySelfSource.TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				CopySelfSource.TYPE.getThis(),
				selfTarget,
				selfTemplate,
				selfTemplateCopyFromTarget,
			}), CopySelfSource.TYPE.getDeclaredFeatures());

		assertEquals(TYPE, templateString.getType());
		assertEquals(TYPE, templateStringCopyFromTarget.getType());
		assertEquals(TYPE, templateItem.getType());
		assertEquals(TYPE, templateItemCopyFromTarget.getType());
		assertEquals(CopySelfSource.TYPE, selfTemplate.getType());
		assertEquals(CopySelfSource.TYPE, selfTemplateCopyFromTarget.getType());
		assertEquals("templateString", templateString.getName());
		assertEquals("templateStringCopyFromtargetItem", templateStringCopyFromTarget.getName());
		assertEquals("templateItem", templateItem.getName());
		assertEquals("templateItemCopyFromtargetItem", templateItemCopyFromTarget.getName());
		assertEquals("selfTemplate", selfTemplate.getName());
		assertEquals("selfTemplateCopyFromselfTarget", selfTemplateCopyFromTarget.getName());

		assertEqualsUnmodifiable(
				list(templateStringCopyFromTarget, templateItemCopyFromTarget),
				TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(
				list(templateStringCopyFromTarget, templateItemCopyFromTarget),
				TYPE.getCopyConstraints());
		assertEqualsUnmodifiable(
				list(selfTemplateCopyFromTarget),
				CopySelfSource.TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(
				list(selfTemplateCopyFromTarget),
				CopySelfSource.TYPE.getCopyConstraints());
		assertEqualsUnmodifiable(list(), CopySimpleTarget.TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(list(), CopySimpleTarget.TYPE.getCopyConstraints());

		assertEquals(null, templateString.getPattern());
		assertEquals(null, templateItem.getPattern());
		assertEquals(null, selfTemplate.getPattern());

		assertEquals(true, templateString.isInitial());
		assertEquals(true, templateString.isFinal());
		assertEquals(String.class, templateString.getInitialType());
		assertContains(
				StringLengthViolationException.class, FinalViolationException.class,
				templateString.getInitialExceptions());
		assertEquals(true, templateItem.isInitial());
		assertEquals(true, templateItem.isFinal());
		assertEquals(CopyValue.class, templateItem.getInitialType());
		assertContains(
				FinalViolationException.class,
				templateItem.getInitialExceptions());

		assertSame(targetItem, templateStringCopyFromTarget.getTarget());
		assertSame(targetItem, templateItemCopyFromTarget.getTarget());
		assertSame(selfTarget, selfTemplateCopyFromTarget.getTarget());

		assertSame(CopySimpleTarget.templateString, templateStringCopyFromTarget.getTemplate());
		assertSame(CopySimpleTarget.templateItem,   templateItemCopyFromTarget.getTemplate());
		assertSame(selfTemplate, selfTemplateCopyFromTarget.getTemplate());

		assertSame(templateString, templateStringCopyFromTarget.getCopy());
		assertSame(templateItem,   templateItemCopyFromTarget.getCopy());
		assertSame(selfTemplate,   selfTemplateCopyFromTarget.getCopy());

		assertSerializedSame(templateStringCopyFromTarget , 409);
		assertSerializedSame(templateItemCopyFromTarget   , 407);
		assertSerializedSame(selfTemplateCopyFromTarget   , 405);
	}

	@SuppressWarnings("deprecation") // OK testing deprecated api
	@Test public void testDeprecated()
	{
		assertEqualsUnmodifiable(list(templateStringCopyFromTarget), templateString  .getImplicitCopyConstraints());
		assertEqualsUnmodifiable(list(templateItemCopyFromTarget  ), templateItem    .getImplicitCopyConstraints());
		assertEqualsUnmodifiable(list(selfTemplateCopyFromTarget  ), selfTemplate    .getImplicitCopyConstraints());

		assertEquals(templateStringCopyFromTarget, templateString.getImplicitCopyConstraint());
		assertEquals(templateItemCopyFromTarget  , templateItem  .getImplicitCopyConstraint());
		assertEquals(selfTemplateCopyFromTarget  , selfTemplate  .getImplicitCopyConstraint());
		try
		{
			new CopyConstraint(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("target", e.getMessage());
		}
		final ItemField<CopyValue> target = ItemField.create(CopyValue.class);
		try
		{
			new CopyConstraint(target, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("copy", e.getMessage());
		}
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test public void testFailures()
	{
		final StringField copy = new StringField();
		try
		{
			copy.copyFrom(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("copyFrom", e.getMessage());
		}
		final ItemField<CopyValue> target = ItemField.create(CopyValue.class);
		try
		{
			copy.copyFrom(target);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("target must be final", e.getMessage());
		}
		final ItemField<CopyValue> targetFinal = ItemField.create(CopyValue.class).toFinal();
		try
		{
			copy.copyFrom(targetFinal);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("copy must be final", e.getMessage());
		}
	}
}