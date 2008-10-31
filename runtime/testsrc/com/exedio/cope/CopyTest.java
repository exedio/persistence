/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.util.Arrays;

public class CopyTest extends AbstractRuntimeTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(CopySourceItem.TYPE, CopyTargetItem.TYPE, CopyValueItem.TYPE);
	
	public CopyTest()
	{
		super(MODEL);
	}
	
	CopyValueItem value1, value2, valueX;
	CopyTargetItem target1, target2, targetN;
	CopySourceItem self1, self2, selfN;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		value1 = deleteOnTearDown(new CopyValueItem("value1"));
		value2 = deleteOnTearDown(new CopyValueItem("value2"));
		valueX = deleteOnTearDown(new CopyValueItem("valueX"));
		target1 = deleteOnTearDown(new CopyTargetItem("target1", "template1", "otherString1", value1, valueX));
		target2 = deleteOnTearDown(new CopyTargetItem("target2", "template2", "otherString2", value2, valueX));
		targetN = deleteOnTearDown(new CopyTargetItem("targetN", null, "otherString2", null, valueX));
		self1 = deleteOnTearDown(new CopySourceItem(null, null, null, null, value1));
		self2 = deleteOnTearDown(new CopySourceItem(null, null, null, null, value2));
		selfN = deleteOnTearDown(new CopySourceItem(null, null, null, null, null));
	}
	
	public void testIt()
	{
		// test model
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
		assertEquals(String.class, CopySourceItem.templateString.getInitialType());
		assertContains(
				StringLengthViolationException.class, FinalViolationException.class,
				CopySourceItem.templateString.getInitialExceptions());
		assertEquals(true, CopySourceItem.templateItem.isInitial());
		assertEquals(true, CopySourceItem.templateItem.isFinal());
		assertEquals(CopyValueItem.class, CopySourceItem.templateItem.getInitialType());
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
		
		try
		{
			new CopyConstraint(null, null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("target must not be null", e.getMessage());
		}
		try
		{
			new CopyConstraint(Item.newItemField(CopyValueItem.class), null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("copy must not be null", e.getMessage());
		}
		try
		{
			new CopyConstraint(Item.newItemField(CopyValueItem.class), new StringField());
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("target must be final", e.getMessage());
		}
		try
		{
			new CopyConstraint(Item.newItemField(CopyValueItem.class).toFinal(), new StringField());
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("copy must be final", e.getMessage());
		}
		
		// test persistence
		assertContains(self1, self2, selfN, CopySourceItem.TYPE.search());
		check();
		
		final CopySourceItem source1 = deleteOnTearDown(new CopySourceItem(target1, "template1", value1, self1, value1));
		assertEquals(target1, source1.getTargetItem());
		assertEquals("template1", source1.getTemplateString());
		assertEquals(value1, source1.getTemplateItem());
		assertContains(self1, self2, selfN, source1, CopySourceItem.TYPE.search());
		assertEquals(self1, source1.getSelfTargetItem());
		assertEquals(value1, source1.getSelfTemplateItem());
		check();
		
		final CopySourceItem source2 = deleteOnTearDown(new CopySourceItem(target2, "template2", value2, self2, value2));
		assertEquals(target2, source2.getTargetItem());
		assertEquals("template2", source2.getTemplateString());
		assertEquals(value2, source2.getTemplateItem());
		assertContains(self1, self2, selfN, source1, source2, CopySourceItem.TYPE.search());
		assertEquals(self2, source2.getSelfTargetItem());
		assertEquals(value2, source2.getSelfTemplateItem());
		check();
		
		final CopySourceItem sourceN = deleteOnTearDown(new CopySourceItem(targetN, null, null, selfN, null));
		assertEquals(targetN, sourceN.getTargetItem());
		assertEquals(null, sourceN.getTemplateString());
		assertEquals(null, sourceN.getTemplateItem());
		assertContains(self1, self2, selfN, source1, source2, sourceN, CopySourceItem.TYPE.search());
		assertEquals(selfN, sourceN.getSelfTargetItem());
		assertEquals(null, sourceN.getSelfTemplateItem());
		check();
		
		final CopySourceItem sourceNT = deleteOnTearDown(new CopySourceItem(null, "templateN", value2, null, value1));
		assertEquals(null, sourceNT.getTargetItem());
		assertEquals("templateN", sourceNT.getTemplateString());
		assertEquals(value2, sourceNT.getTemplateItem());
		assertContains(self1, self2, selfN, source1, source2, sourceN, sourceNT, CopySourceItem.TYPE.search());
		assertEquals(null, sourceNT.getSelfTargetItem());
		assertEquals(value1, sourceNT.getSelfTemplateItem());
		check();
		
		try
		{
			new CopySourceItem(target2, "template1", value2, null, null);
			fail();
		}
		catch(CopyViolationException e)
		{
			assertEquals(CopySourceItem.templateStringCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("template2", e.getExpectedValue());
			assertEquals("template1", e.getActualValue());
			assertEquals(target2, e.getTargetItem());
			assertEquals(
					"copy violation on " + CopySourceItem.templateStringCopyFromTarget + ", " +
					"expected 'template2' " +
					"from target " + target2.getCopeID() + ", " +
					"but was 'template1'",
				e.getMessage());
		}
		assertContains(self1, self2, selfN, source1, source2, sourceN, sourceNT, CopySourceItem.TYPE.search());
		check();
		
		try
		{
			new CopySourceItem(target2, null, value2, null, null);
			fail();
		}
		catch(CopyViolationException e)
		{
			assertEquals(CopySourceItem.templateStringCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("template2", e.getExpectedValue());
			assertEquals(null, e.getActualValue());
			assertEquals(target2, e.getTargetItem());
			assertEquals(
					"copy violation on " + CopySourceItem.templateStringCopyFromTarget + ", " +
					"expected 'template2' " +
					"from target " + target2.getCopeID() + ", " +
					"but was null",
				e.getMessage());
		}
		assertContains(self1, self2, selfN, source1, source2, sourceN, sourceNT, CopySourceItem.TYPE.search());
		check();
		
		try
		{
			new CopySourceItem(target2, "template2", value1, null, null);
			fail();
		}
		catch(CopyViolationException e)
		{
			assertEquals(CopySourceItem.templateItemCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(value2, e.getExpectedValue());
			assertEquals(value1, e.getActualValue());
			assertEquals(target2, e.getTargetItem());
			assertEquals(
					"copy violation on " + CopySourceItem.templateItemCopyFromTarget + ", " +
					"expected '" + value2.getCopeID() + "' " +
					"from target " + target2.getCopeID() + ", " +
					"but was '" + value1.getCopeID() + "'",
				e.getMessage());
		}
		assertContains(self1, self2, selfN, source1, source2, sourceN, sourceNT, CopySourceItem.TYPE.search());
		check();
		
		try
		{
			new CopySourceItem(target2, "template2", null, null, null);
			fail();
		}
		catch(CopyViolationException e)
		{
			assertEquals(CopySourceItem.templateItemCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(value2, e.getExpectedValue());
			assertEquals(null, e.getActualValue());
			assertEquals(target2, e.getTargetItem());
			assertEquals(
					"copy violation on " + CopySourceItem.templateItemCopyFromTarget + ", " +
					"expected '" + value2.getCopeID() + "' " +
					"from target " + target2.getCopeID() + ", " +
					"but was null",
				e.getMessage());
		}
		assertContains(self1, self2, selfN, source1, source2, sourceN, sourceNT, CopySourceItem.TYPE.search());
		check();
		
		try
		{
			new CopySourceItem(targetN, "template1", value2, null, null);
			fail();
		}
		catch(CopyViolationException e)
		{
			assertEquals(CopySourceItem.templateStringCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(null, e.getExpectedValue());
			assertEquals("template1", e.getActualValue());
			assertEquals(targetN, e.getTargetItem());
			assertEquals(
					"copy violation on " + CopySourceItem.templateStringCopyFromTarget + ", " +
					"expected null " +
					"from target " + targetN.getCopeID() + ", " +
					"but was 'template1'",
				e.getMessage());
		}
		assertContains(self1, self2, selfN, source1, source2, sourceN, sourceNT, CopySourceItem.TYPE.search());
		check();
		
		try
		{
			new CopySourceItem(targetN, null, value1, null, null);
			fail();
		}
		catch(CopyViolationException e)
		{
			assertEquals(CopySourceItem.templateItemCopyFromTarget, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(null, e.getExpectedValue());
			assertEquals(value1, e.getActualValue());
			assertEquals(targetN, e.getTargetItem());
			assertEquals(
					"copy violation on " + CopySourceItem.templateItemCopyFromTarget + ", " +
					"expected null " +
					"from target " + targetN.getCopeID() + ", " +
					"but was '" + value1.getCopeID() + "'",
				e.getMessage());
		}
		assertContains(self1, self2, selfN, source1, source2, sourceN, sourceNT, CopySourceItem.TYPE.search());
		check();
	}
	
	private static final void check()
	{
		assertEquals(0, CopySourceItem.templateStringCopyFromTarget.check());
		assertEquals(0, CopySourceItem.templateItemCopyFromTarget.check());
		assertEquals(0, CopySourceItem.selfTemplateItemCopyFromTarget.check());
	}
}
