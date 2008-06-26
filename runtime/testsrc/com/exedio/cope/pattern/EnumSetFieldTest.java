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

package com.exedio.cope.pattern;

import java.util.EnumSet;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.BooleanField;
import com.exedio.cope.Model;

public class EnumSetFieldTest extends AbstractRuntimeTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(EnumSetFieldItem.TYPE);

	private static final EnumSetFieldItem.Language DE = EnumSetFieldItem.Language.DE;
	private static final EnumSetFieldItem.Language EN = EnumSetFieldItem.Language.EN;
	private static final EnumSetFieldItem.Language PL = EnumSetFieldItem.Language.PL;
	
	public EnumSetFieldTest()
	{
		super(MODEL);
	}
	
	EnumSetFieldItem item, itemX;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new EnumSetFieldItem());
		itemX = deleteOnTearDown(new EnumSetFieldItem());
	}
	
	public void testIt()
	{
		// test model
		assertEquals(item.TYPE, item.activeLanguage.getType());
		assertEquals("activeLanguage", item.activeLanguage.getName());

		assertEquals(EnumSetFieldItem.Language.class, item.activeLanguage.getElementClass());

		assertEquals(BooleanField.class, item.activeLanguage.getField(DE).getClass());
		assertEquals("activeLanguageDE", item.activeLanguage.getField(DE).getName());
		assertSame(item.TYPE, item.activeLanguage.getField(DE).getType());
		assertEqualsUnmodifiable(list(item.activeLanguage), item.activeLanguage.getField(DE).getPatterns());

		assertEqualsUnmodifiable(
				list(
						item.TYPE.getThis(),
						item.activeLanguage,
						item.activeLanguage.getField(DE), item.activeLanguage.getField(EN), item.activeLanguage.getField(PL)),
				item.TYPE.getFeatures());
		assertEqualsUnmodifiable(
				list(
						item.activeLanguage.getField(DE), item.activeLanguage.getField(EN), item.activeLanguage.getField(PL)),
				item.TYPE.getFields());

		assertEqualsUnmodifiable(list(item.TYPE), model.getTypes());
		assertEqualsUnmodifiable(list(item.TYPE), model.getTypesSortedByHierarchy());

		// test persistence
		assertEquals(false, item.containsActiveLanguage(DE));
		assertEquals(false, item.containsActiveLanguage(EN));
		assertEquals(false, item.containsActiveLanguage(PL));
		assertEquals(false, itemX.containsActiveLanguage(DE));
		assertEquals(EnumSet.noneOf(EnumSetFieldItem.Language.class), item.getActiveLanguage());
		assertEquals(EnumSet.noneOf(EnumSetFieldItem.Language.class), itemX.getActiveLanguage());

		item.addActiveLanguage(DE);
		assertEquals(true,  item.containsActiveLanguage(DE));
		assertEquals(false, item.containsActiveLanguage(EN));
		assertEquals(false, item.containsActiveLanguage(PL));
		assertEquals(false, itemX.containsActiveLanguage(DE));
		assertEquals(EnumSet.of(DE), item.getActiveLanguage());
		assertEquals(EnumSet.noneOf(EnumSetFieldItem.Language.class), itemX.getActiveLanguage());
		
		item.addActiveLanguage(EN);
		assertEquals(true,  item.containsActiveLanguage(DE));
		assertEquals(true,  item.containsActiveLanguage(EN));
		assertEquals(false, item.containsActiveLanguage(PL));
		assertEquals(false, itemX.containsActiveLanguage(DE));
		assertEquals(EnumSet.of(DE, EN), item.getActiveLanguage());
		assertEquals(EnumSet.noneOf(EnumSetFieldItem.Language.class), itemX.getActiveLanguage());
		
		item.addActiveLanguage(EN);
		assertEquals(true,  item.containsActiveLanguage(DE));
		assertEquals(true,  item.containsActiveLanguage(EN));
		assertEquals(false, item.containsActiveLanguage(PL));
		assertEquals(false, itemX.containsActiveLanguage(DE));
		assertEquals(EnumSet.of(DE, EN), item.getActiveLanguage());
		assertEquals(EnumSet.noneOf(EnumSetFieldItem.Language.class), itemX.getActiveLanguage());
		
		item.removeActiveLanguage(DE);
		assertEquals(false, item.containsActiveLanguage(DE));
		assertEquals(true,  item.containsActiveLanguage(EN));
		assertEquals(false, item.containsActiveLanguage(PL));
		assertEquals(false, itemX.containsActiveLanguage(DE));
		assertEquals(EnumSet.of(EN), item.getActiveLanguage());
		assertEquals(EnumSet.noneOf(EnumSetFieldItem.Language.class), itemX.getActiveLanguage());
		
		item.removeActiveLanguage(DE);
		assertEquals(false, item.containsActiveLanguage(DE));
		assertEquals(true,  item.containsActiveLanguage(EN));
		assertEquals(false, item.containsActiveLanguage(PL));
		assertEquals(false, itemX.containsActiveLanguage(DE));
		assertEquals(EnumSet.of(EN), item.getActiveLanguage());
		assertEquals(EnumSet.noneOf(EnumSetFieldItem.Language.class), itemX.getActiveLanguage());
		
		item.setActiveLanguage(EnumSet.of(EN, PL));
		assertEquals(false, item.containsActiveLanguage(DE));
		assertEquals(true,  item.containsActiveLanguage(EN));
		assertEquals(true,  item.containsActiveLanguage(PL));
		assertEquals(false, itemX.containsActiveLanguage(DE));
		assertEquals(EnumSet.of(EN, PL), item.getActiveLanguage());
		assertEquals(EnumSet.noneOf(EnumSetFieldItem.Language.class), itemX.getActiveLanguage());
		
		item.setActiveLanguage(EnumSet.noneOf(EnumSetFieldItem.Language.class));
		assertEquals(false, item.containsActiveLanguage(DE));
		assertEquals(false, item.containsActiveLanguage(EN));
		assertEquals(false, item.containsActiveLanguage(PL));
		assertEquals(false, itemX.containsActiveLanguage(DE));
		assertEquals(EnumSet.noneOf(EnumSetFieldItem.Language.class), item.getActiveLanguage());
		assertEquals(EnumSet.noneOf(EnumSetFieldItem.Language.class), itemX.getActiveLanguage());
		
		try
		{
			item.containsActiveLanguage(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("element must not be null", e.getMessage());
		}
		try
		{
			item.addActiveLanguage(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("element must not be null", e.getMessage());
		}
		try
		{
			item.removeActiveLanguage(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("element must not be null", e.getMessage());
		}
		try
		{
			item.setActiveLanguage(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked") // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			((EnumSetField)item.activeLanguage).contains(item, X.A);
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a com.exedio.cope.pattern.EnumSetFieldItem$Language, but was a com.exedio.cope.pattern.EnumSetFieldTest$X", e.getMessage());
		}
		try
		{
			((EnumSetField)item.activeLanguage).add(item, X.A);
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a com.exedio.cope.pattern.EnumSetFieldItem$Language, but was a com.exedio.cope.pattern.EnumSetFieldTest$X", e.getMessage());
		}
		try
		{
			((EnumSetField)item.activeLanguage).remove(item, X.A);
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a com.exedio.cope.pattern.EnumSetFieldItem$Language, but was a com.exedio.cope.pattern.EnumSetFieldTest$X", e.getMessage());
		}
	}
	
	enum X
	{
		A, B, C;
	}
}
