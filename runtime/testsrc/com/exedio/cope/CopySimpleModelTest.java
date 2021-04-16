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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

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

	@Test void testIt()
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

		assertEquals(false, templateString.isInitial());
		assertEquals(false, templateString.isFinal());
		assertEquals(String.class, templateString.getInitialType());
		assertContains(
				StringLengthViolationException.class,
				templateString.getInitialExceptions());
		assertEquals(false, templateItem.isInitial());
		assertEquals(false, templateItem.isFinal());
		assertEquals(CopyValue.class, templateItem.getInitialType());
		assertContains(templateItem.getInitialExceptions());

		assertSame(targetItem, templateStringCopyFromTarget.getTarget());
		assertSame(targetItem, templateItemCopyFromTarget.getTarget());
		assertSame(selfTarget, selfTemplateCopyFromTarget.getTarget());

		assertSame(CopySimpleTarget.templateString, templateStringCopyFromTarget.getTemplate());
		assertSame(CopySimpleTarget.templateItem,   templateItemCopyFromTarget.getTemplate());
		assertSame(selfTemplate, selfTemplateCopyFromTarget.getTemplate());

		assertEquals(false, templateStringCopyFromTarget.isChoice());
		assertEquals(false,   templateItemCopyFromTarget.isChoice());
		assertEquals(false,   selfTemplateCopyFromTarget.isChoice());

		assertSame(templateString, templateStringCopyFromTarget.getCopyField());
		assertSame(templateItem,     templateItemCopyFromTarget.getCopyField());
		assertSame(selfTemplate,     selfTemplateCopyFromTarget.getCopyField());

		assertSame(templateString, templateStringCopyFromTarget.getCopyFunction());
		assertSame(templateItem,     templateItemCopyFromTarget.getCopyFunction());
		assertSame(selfTemplate,     selfTemplateCopyFromTarget.getCopyFunction());

		assertSerializedSame(templateStringCopyFromTarget , 409);
		assertSerializedSame(templateItemCopyFromTarget   , 407);
		assertSerializedSame(selfTemplateCopyFromTarget   , 405);
	}

	@Test void testFailures()
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
	}
}
