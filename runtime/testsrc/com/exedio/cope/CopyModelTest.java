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

import static com.exedio.cope.AbstractRuntimeTest.assertSerializedSame;
import static com.exedio.cope.AbstractRuntimeTest.getInitialType;

import java.util.Arrays;

import com.exedio.cope.junit.CopeAssert;

public class CopyModelTest extends CopeAssert
{
	public static final Model MODEL = new Model(CopySourceItem.TYPE, CopyTargetItem.TYPE, CopyValueItem.TYPE);

	static
	{
		MODEL.enableSerialization(CopyModelTest.class, "MODEL");
	}

	public void testIt()
	{
		assertEquals(Arrays.asList(new Feature[]{
				CopySourceItem.TYPE.getThis(),
				CopySourceItem.targetItem,
				CopySourceItem.templateString,
				CopySourceItem.templateItem,
				CopySourceItem.templateStringCopyFromTarget,
				CopySourceItem.templateItemCopyFromTarget,
				CopySourceItem.selfTargetItem,
				CopySourceItem.selfTemplateItem,
				CopySourceItem.selfTemplateItemCopyFromTarget,
			}), CopySourceItem.TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				CopySourceItem.TYPE.getThis(),
				CopySourceItem.targetItem,
				CopySourceItem.templateString,
				CopySourceItem.templateItem,
				CopySourceItem.templateStringCopyFromTarget,
				CopySourceItem.templateItemCopyFromTarget,
				CopySourceItem.selfTargetItem,
				CopySourceItem.selfTemplateItem,
				CopySourceItem.selfTemplateItemCopyFromTarget,
			}), CopySourceItem.TYPE.getDeclaredFeatures());

		assertEquals(CopySourceItem.TYPE, CopySourceItem.templateString.getType());
		assertEquals(CopySourceItem.TYPE, CopySourceItem.templateStringCopyFromTarget.getType());
		assertEquals(CopySourceItem.TYPE, CopySourceItem.templateItem.getType());
		assertEquals(CopySourceItem.TYPE, CopySourceItem.templateItemCopyFromTarget.getType());
		assertEquals(CopySourceItem.TYPE, CopySourceItem.selfTemplateItem.getType());
		assertEquals(CopySourceItem.TYPE, CopySourceItem.selfTemplateItemCopyFromTarget.getType());
		assertEquals("templateString", CopySourceItem.templateString.getName());
		assertEquals("templateStringCopyFromTarget", CopySourceItem.templateStringCopyFromTarget.getName());
		assertEquals("templateItem", CopySourceItem.templateItem.getName());
		assertEquals("templateItemCopyFromTarget", CopySourceItem.templateItemCopyFromTarget.getName());
		assertEquals("selfTemplateItem", CopySourceItem.selfTemplateItem.getName());
		assertEquals("selfTemplateItemCopyFromTarget", CopySourceItem.selfTemplateItemCopyFromTarget.getName());

		assertEqualsUnmodifiable(
				list(CopySourceItem.templateStringCopyFromTarget, CopySourceItem.templateItemCopyFromTarget, CopySourceItem.selfTemplateItemCopyFromTarget),
				CopySourceItem.TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(
				list(CopySourceItem.templateStringCopyFromTarget, CopySourceItem.templateItemCopyFromTarget, CopySourceItem.selfTemplateItemCopyFromTarget),
				CopySourceItem.TYPE.getCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyTargetItem.TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyTargetItem.TYPE.getCopyConstraints());

		assertEquals(null, CopySourceItem.templateString.getPattern());
		assertEquals(null, CopySourceItem.templateItem.getPattern());
		assertEquals(null, CopySourceItem.selfTemplateItem.getPattern());

		assertEquals(true, CopySourceItem.templateString.isInitial());
		assertEquals(true, CopySourceItem.templateString.isFinal());
		assertEquals(String.class, getInitialType(CopySourceItem.templateString));
		assertContains(
				StringLengthViolationException.class, FinalViolationException.class,
				CopySourceItem.templateString.getInitialExceptions());
		assertEquals(true, CopySourceItem.templateItem.isInitial());
		assertEquals(true, CopySourceItem.templateItem.isFinal());
		assertEquals(CopyValueItem.class, getInitialType(CopySourceItem.templateItem));
		assertContains(
				FinalViolationException.class,
				CopySourceItem.templateItem.getInitialExceptions());

		assertSame(CopySourceItem.targetItem, CopySourceItem.templateStringCopyFromTarget.getTarget());
		assertSame(CopySourceItem.targetItem, CopySourceItem.templateItemCopyFromTarget.getTarget());
		assertSame(CopySourceItem.selfTargetItem, CopySourceItem.selfTemplateItemCopyFromTarget.getTarget());

		assertSame(CopyTargetItem.templateString, CopySourceItem.templateStringCopyFromTarget.getTemplate());
		assertSame(CopyTargetItem.templateItem,   CopySourceItem.templateItemCopyFromTarget.getTemplate());
		assertSame(CopySourceItem.selfTemplateItem,   CopySourceItem.selfTemplateItemCopyFromTarget.getTemplate());

		assertSame(CopySourceItem.templateString, CopySourceItem.templateStringCopyFromTarget.getCopy());
		assertSame(CopySourceItem.templateItem,   CopySourceItem.templateItemCopyFromTarget.getCopy());
		assertSame(CopySourceItem.selfTemplateItem,   CopySourceItem.selfTemplateItemCopyFromTarget.getCopy());

		assertSerializedSame(CopySourceItem.templateStringCopyFromTarget  , 397);
		assertSerializedSame(CopySourceItem.templateItemCopyFromTarget    , 395);
		assertSerializedSame(CopySourceItem.selfTemplateItemCopyFromTarget, 399);

		try
		{
			new CopyConstraint(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("target", e.getMessage());
		}
		try
		{
			new CopyConstraint(ItemField.create(CopyValueItem.class), null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("copy", e.getMessage());
		}
		try
		{
			new CopyConstraint(ItemField.create(CopyValueItem.class), new StringField());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("target must be final", e.getMessage());
		}
		try
		{
			new CopyConstraint(ItemField.create(CopyValueItem.class).toFinal(), new StringField());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("copy must be final", e.getMessage());
		}
	}
}
