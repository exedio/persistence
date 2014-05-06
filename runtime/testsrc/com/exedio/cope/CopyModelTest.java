/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.CopySourceItem.TYPE;
import static com.exedio.cope.CopySourceItem.selfTargetItem;
import static com.exedio.cope.CopySourceItem.selfTemplateItem;
import static com.exedio.cope.CopySourceItem.targetItem;
import static com.exedio.cope.CopySourceItem.templateItem;
import static com.exedio.cope.CopySourceItem.templateString;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;

import com.exedio.cope.junit.CopeAssert;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;

public class CopyModelTest extends CopeAssert
{
	public static final Model MODEL = new Model(TYPE, CopyTargetItem.TYPE, CopyValueItem.TYPE);

	static
	{
		MODEL.enableSerialization(CopyModelTest.class, "MODEL");
	}

	static final CopyConstraint templateStringCopyFromTarget   = (CopyConstraint)TYPE.getFeature("templateStringCopyFromtargetItem");
	static final CopyConstraint templateItemCopyFromTarget     = (CopyConstraint)TYPE.getFeature("templateItemCopyFromtargetItem");
	static final CopyConstraint selfTemplateItemCopyFromTarget = (CopyConstraint)TYPE.getFeature("selfTemplateItemCopyFromselfTargetItem");

	public void testIt()
	{
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				targetItem,
				templateString,
				templateStringCopyFromTarget,
				templateItem,
				templateItemCopyFromTarget,
				selfTargetItem,
				selfTemplateItem,
				selfTemplateItemCopyFromTarget,
			}), TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				targetItem,
				templateString,
				templateStringCopyFromTarget,
				templateItem,
				templateItemCopyFromTarget,
				selfTargetItem,
				selfTemplateItem,
				selfTemplateItemCopyFromTarget,
			}), TYPE.getDeclaredFeatures());

		assertEquals(TYPE, templateString.getType());
		assertEquals(TYPE, templateStringCopyFromTarget.getType());
		assertEquals(TYPE, templateItem.getType());
		assertEquals(TYPE, templateItemCopyFromTarget.getType());
		assertEquals(TYPE, selfTemplateItem.getType());
		assertEquals(TYPE, selfTemplateItemCopyFromTarget.getType());
		assertEquals("templateString", templateString.getName());
		assertEquals("templateStringCopyFromtargetItem", templateStringCopyFromTarget.getName());
		assertEquals("templateItem", templateItem.getName());
		assertEquals("templateItemCopyFromtargetItem", templateItemCopyFromTarget.getName());
		assertEquals("selfTemplateItem", selfTemplateItem.getName());
		assertEquals("selfTemplateItemCopyFromselfTargetItem", selfTemplateItemCopyFromTarget.getName());

		assertEqualsUnmodifiable(
				list(templateStringCopyFromTarget, templateItemCopyFromTarget, selfTemplateItemCopyFromTarget),
				TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(
				list(templateStringCopyFromTarget, templateItemCopyFromTarget, selfTemplateItemCopyFromTarget),
				TYPE.getCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyTargetItem.TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyTargetItem.TYPE.getCopyConstraints());

		assertEquals(null, templateString.getPattern());
		assertEquals(null, templateItem.getPattern());
		assertEquals(null, selfTemplateItem.getPattern());

		assertEquals(true, templateString.isInitial());
		assertEquals(true, templateString.isFinal());
		assertEquals(String.class, templateString.getInitialType());
		assertContains(
				StringLengthViolationException.class, FinalViolationException.class,
				templateString.getInitialExceptions());
		assertEquals(true, templateItem.isInitial());
		assertEquals(true, templateItem.isFinal());
		assertEquals(CopyValueItem.class, templateItem.getInitialType());
		assertContains(
				FinalViolationException.class,
				templateItem.getInitialExceptions());

		assertSame(targetItem, templateStringCopyFromTarget.getTarget());
		assertSame(targetItem, templateItemCopyFromTarget.getTarget());
		assertSame(selfTargetItem, selfTemplateItemCopyFromTarget.getTarget());

		assertSame(CopyTargetItem.templateString, templateStringCopyFromTarget.getTemplate());
		assertSame(CopyTargetItem.templateItem,   templateItemCopyFromTarget.getTemplate());
		assertSame(selfTemplateItem,   selfTemplateItemCopyFromTarget.getTemplate());

		assertSame(templateString, templateStringCopyFromTarget.getCopy());
		assertSame(templateItem,   templateItemCopyFromTarget.getCopy());
		assertSame(selfTemplateItem,   selfTemplateItemCopyFromTarget.getCopy());

		assertSerializedSame(templateStringCopyFromTarget  , 401);
		assertSerializedSame(templateItemCopyFromTarget    , 399);
		assertSerializedSame(selfTemplateItemCopyFromTarget, 407);
	}

	@SuppressWarnings("deprecation") // OK testing deprecated api
	public void testDeprecated()
	{
		assertEqualsUnmodifiable(list(templateStringCopyFromTarget  ), templateString  .getImplicitCopyConstraints());
		assertEqualsUnmodifiable(list(templateItemCopyFromTarget    ), templateItem    .getImplicitCopyConstraints());
		assertEqualsUnmodifiable(list(selfTemplateItemCopyFromTarget), selfTemplateItem.getImplicitCopyConstraints());

		assertEquals(templateStringCopyFromTarget  , templateString  .getImplicitCopyConstraint());
		assertEquals(templateItemCopyFromTarget    , templateItem    .getImplicitCopyConstraint());
		assertEquals(selfTemplateItemCopyFromTarget, selfTemplateItem.getImplicitCopyConstraint());
		try
		{
			new CopyConstraint(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("target", e.getMessage());
		}
		final ItemField<CopyValueItem> target = ItemField.create(CopyValueItem.class);
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
	public void testFailures()
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
		final ItemField<CopyValueItem> target = ItemField.create(CopyValueItem.class);
		try
		{
			copy.copyFrom(target);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("target must be final", e.getMessage());
		}
		final ItemField<CopyValueItem> targetFinal = ItemField.create(CopyValueItem.class).toFinal();
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
