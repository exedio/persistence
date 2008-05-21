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

public class CopyTest extends AbstractLibTest
{
	static final Model MODEL = new Model(CopySourceItem.TYPE, CopyTargetItem.TYPE, CopyValueItem.TYPE);
	
	public CopyTest()
	{
		super(MODEL);
	}
	
	CopyValueItem value1, value2, valueX;
	CopyTargetItem target1, target2, targetN;
	
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
	}
	
	public void testIt()
	{
		// test model
		assertEquals(Arrays.asList(new Feature[]{
				CopySourceItem.TYPE.getThis(),
				CopySourceItem.targetItem,
				CopySourceItem.templateString,
				CopySourceItem.templateString.getCopy(),
				CopySourceItem.templateItem,
				CopySourceItem.templateItem.getCopy(),
			}), CopySourceItem.TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				CopySourceItem.TYPE.getThis(),
				CopySourceItem.targetItem,
				CopySourceItem.templateString,
				CopySourceItem.templateString.getCopy(),
				CopySourceItem.templateItem,
				CopySourceItem.templateItem.getCopy(),
			}), CopySourceItem.TYPE.getDeclaredFeatures());

		assertEquals(CopySourceItem.TYPE, CopySourceItem.templateString.getCopy().getType());
		assertEquals(CopySourceItem.TYPE, CopySourceItem.templateString.getType());
		assertEquals(CopySourceItem.TYPE, CopySourceItem.templateItem.getCopy().getType());
		assertEquals(CopySourceItem.TYPE, CopySourceItem.templateItem.getType());
		assertEquals("templateStringCopy", CopySourceItem.templateString.getCopy().getName());
		assertEquals("templateString", CopySourceItem.templateString.getName());
		assertEquals("templateItemCopy", CopySourceItem.templateItem.getCopy().getName());
		assertEquals("templateItem", CopySourceItem.templateItem.getName());

		assertEqualsUnmodifiable(list(CopySourceItem.templateString), CopySourceItem.templateString.getCopy().getPatterns());
		assertEqualsUnmodifiable(list(CopySourceItem.templateItem),   CopySourceItem.templateItem.getCopy().getPatterns());
		
		assertEquals(true, CopySourceItem.templateString.isInitial());
		assertEquals(true, CopySourceItem.templateString.isFinal());
		assertEquals(String.class, CopySourceItem.templateString.getInitialType());
		assertContains(
				LengthViolationException.class, FinalViolationException.class,
				CopySourceItem.templateString.getInitialExceptions());
		assertEquals(true, CopySourceItem.templateItem.isInitial());
		assertEquals(true, CopySourceItem.templateItem.isFinal());
		assertEquals(CopyValueItem.class, CopySourceItem.templateItem.getInitialType());
		assertContains(
				FinalViolationException.class,
				CopySourceItem.templateItem.getInitialExceptions());
		
		assertSame(CopySourceItem.targetItem, CopySourceItem.templateString.getTarget());
		assertSame(CopySourceItem.targetItem, CopySourceItem.templateItem.getTarget());
		assertSame(CopyTargetItem.templateString, CopySourceItem.templateString.getTemplate());
		assertSame(CopyTargetItem.templateItem,   CopySourceItem.templateItem.getTemplate());
		
		try
		{
			CopyField.newField(null, null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("target must not be null", e.getMessage());
		}
		try
		{
			CopyField.newField(Item.newItemField(CopyValueItem.class), null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("copy must not be null", e.getMessage());
		}
		try
		{
			CopyField.newField(Item.newItemField(CopyValueItem.class), new StringField());
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("target must be final", e.getMessage());
		}
		try
		{
			CopyField.newField(Item.newItemField(CopyValueItem.class).toFinal(), new StringField());
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("copy must be final", e.getMessage());
		}
		
		// test persistence
		assertContains(CopySourceItem.TYPE.search());
		
		final CopySourceItem source1 = deleteOnTearDown(new CopySourceItem(target1, "template1", value1));
		assertEquals(target1, source1.getTargetItem());
		assertEquals("template1", source1.getTemplateString());
		assertEquals(value1, source1.getTemplateItem());
		assertContains(source1, CopySourceItem.TYPE.search());
		
		final CopySourceItem source2 = deleteOnTearDown(new CopySourceItem(target2, "template2", value2));
		assertEquals(target2, source2.getTargetItem());
		assertEquals("template2", source2.getTemplateString());
		assertEquals(value2, source2.getTemplateItem());
		assertContains(source1, source2, CopySourceItem.TYPE.search());
		
		final CopySourceItem sourceN = deleteOnTearDown(new CopySourceItem(targetN, null, null));
		assertEquals(targetN, sourceN.getTargetItem());
		assertEquals(null, sourceN.getTemplateString());
		assertEquals(null, sourceN.getTemplateItem());
		assertContains(source1, source2, sourceN, CopySourceItem.TYPE.search());
		
		final CopySourceItem sourceNT = deleteOnTearDown(new CopySourceItem(null, "templateN", value2));
		assertEquals(null, sourceNT.getTargetItem());
		assertEquals("templateN", sourceNT.getTemplateString());
		assertEquals(value2, sourceNT.getTemplateItem());
		assertContains(source1, source2, sourceN, sourceNT, CopySourceItem.TYPE.search());
		
		try
		{
			new CopySourceItem(target2, "template1", value2);
			fail();
		}
		catch(CopyViolationException e)
		{
			assertEquals(CopySourceItem.templateString, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("template2", e.getExpectedValue());
			assertEquals("template1", e.getActualValue());
			assertEquals(target2, e.getTargetItem());
			assertEquals(
					"mismatch on copy field " + CopySourceItem.templateString + ", " +
					"expected 'template2' " +
					"from target " + target2.getCopeID() + ", " +
					"but was 'template1'",
				e.getMessage());
		}
		assertContains(source1, source2, sourceN, sourceNT, CopySourceItem.TYPE.search());
		
		try
		{
			new CopySourceItem(target2, null, value2);
			fail();
		}
		catch(CopyViolationException e)
		{
			assertEquals(CopySourceItem.templateString, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("template2", e.getExpectedValue());
			assertEquals(null, e.getActualValue());
			assertEquals(target2, e.getTargetItem());
			assertEquals(
					"mismatch on copy field " + CopySourceItem.templateString + ", " +
					"expected 'template2' " +
					"from target " + target2.getCopeID() + ", " +
					"but was null",
				e.getMessage());
		}
		assertContains(source1, source2, sourceN, sourceNT, CopySourceItem.TYPE.search());
		
		try
		{
			new CopySourceItem(target2, "template2", value1);
			fail();
		}
		catch(CopyViolationException e)
		{
			assertEquals(CopySourceItem.templateItem, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(value2, e.getExpectedValue());
			assertEquals(value1, e.getActualValue());
			assertEquals(target2, e.getTargetItem());
			assertEquals(
					"mismatch on copy field " + CopySourceItem.templateItem + ", " +
					"expected '" + value2.getCopeID() + "' " +
					"from target " + target2.getCopeID() + ", " +
					"but was '" + value1.getCopeID() + "'",
				e.getMessage());
		}
		assertContains(source1, source2, sourceN, sourceNT, CopySourceItem.TYPE.search());
		
		try
		{
			new CopySourceItem(target2, "template2", null);
			fail();
		}
		catch(CopyViolationException e)
		{
			assertEquals(CopySourceItem.templateItem, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(value2, e.getExpectedValue());
			assertEquals(null, e.getActualValue());
			assertEquals(target2, e.getTargetItem());
			assertEquals(
					"mismatch on copy field " + CopySourceItem.templateItem + ", " +
					"expected '" + value2.getCopeID() + "' " +
					"from target " + target2.getCopeID() + ", " +
					"but was null",
				e.getMessage());
		}
		assertContains(source1, source2, sourceN, sourceNT, CopySourceItem.TYPE.search());
		
		try
		{
			new CopySourceItem(targetN, "template1", value2);
			fail();
		}
		catch(CopyViolationException e)
		{
			assertEquals(CopySourceItem.templateString, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(null, e.getExpectedValue());
			assertEquals("template1", e.getActualValue());
			assertEquals(targetN, e.getTargetItem());
			assertEquals(
					"mismatch on copy field " + CopySourceItem.templateString + ", " +
					"expected null " +
					"from target " + targetN.getCopeID() + ", " +
					"but was 'template1'",
				e.getMessage());
		}
		assertContains(source1, source2, sourceN, sourceNT, CopySourceItem.TYPE.search());
		
		try
		{
			new CopySourceItem(targetN, null, value1);
			fail();
		}
		catch(CopyViolationException e)
		{
			assertEquals(CopySourceItem.templateItem, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(null, e.getExpectedValue());
			assertEquals(value1, e.getActualValue());
			assertEquals(targetN, e.getTargetItem());
			assertEquals(
					"mismatch on copy field " + CopySourceItem.templateItem + ", " +
					"expected null " +
					"from target " + targetN.getCopeID() + ", " +
					"but was '" + value1.getCopeID() + "'",
				e.getMessage());
		}
		assertContains(source1, source2, sourceN, sourceNT, CopySourceItem.TYPE.search());
	}
}
