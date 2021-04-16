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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.EnumSetFieldItem.Language.DE;
import static com.exedio.cope.pattern.EnumSetFieldItem.Language.EN;
import static com.exedio.cope.pattern.EnumSetFieldItem.Language.PL;
import static com.exedio.cope.pattern.EnumSetFieldItem.Language.SUBCLASS;
import static com.exedio.cope.pattern.EnumSetFieldItem.TYPE;
import static com.exedio.cope.pattern.EnumSetFieldItem.activeLanguage;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.SetValue;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.EnumSetFieldItem.Language;
import java.util.EnumSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnumSetFieldTest extends TestWithEnvironment
{
	public EnumSetFieldTest()
	{
		super(EnumSetFieldModelTest.MODEL);
	}

	EnumSetFieldItem item, itemX;

	@BeforeEach final void setUp()
	{
		item = new EnumSetFieldItem();
		itemX = new EnumSetFieldItem();
	}

	@Test void testIt()
	{
		assertEquals(false, item.containsActiveLanguage(DE));
		assertEquals(false, item.containsActiveLanguage(EN));
		assertEquals(false, item.containsActiveLanguage(PL));
		assertEquals(false, itemX.containsActiveLanguage(DE));
		assertEquals(EnumSet.noneOf(EnumSetFieldItem.Language.class), item.getActiveLanguage());
		assertEquals(EnumSet.noneOf(EnumSetFieldItem.Language.class), itemX.getActiveLanguage());
		assertContains(TYPE.search(activeLanguage.contains(DE)));
		assertContains(TYPE.search(activeLanguage.contains(EN)));
		assertContains(item, itemX, TYPE.search(activeLanguage.isEmpty()));
		assertContains(TYPE.search(activeLanguage.isNotEmpty()));

		item.addActiveLanguage(DE);
		assertEquals(true,  item.containsActiveLanguage(DE));
		assertEquals(false, item.containsActiveLanguage(EN));
		assertEquals(false, item.containsActiveLanguage(PL));
		assertEquals(false, itemX.containsActiveLanguage(DE));
		assertEquals(EnumSet.of(DE), item.getActiveLanguage());
		assertEquals(EnumSet.noneOf(EnumSetFieldItem.Language.class), itemX.getActiveLanguage());
		assertContains(item, TYPE.search(activeLanguage.contains(DE)));
		assertContains(TYPE.search(activeLanguage.contains(EN)));
		assertContains(itemX, TYPE.search(activeLanguage.isEmpty()));
		assertContains(item, TYPE.search(activeLanguage.isNotEmpty()));

		item.addActiveLanguage(EN);
		assertEquals(true,  item.containsActiveLanguage(DE));
		assertEquals(true,  item.containsActiveLanguage(EN));
		assertEquals(false, item.containsActiveLanguage(PL));
		assertEquals(false, itemX.containsActiveLanguage(DE));
		assertEquals(EnumSet.of(DE, EN), item.getActiveLanguage());
		assertEquals(EnumSet.noneOf(EnumSetFieldItem.Language.class), itemX.getActiveLanguage());
		assertContains(item, TYPE.search(activeLanguage.contains(DE)));
		assertContains(item, TYPE.search(activeLanguage.contains(EN)));
		assertContains(itemX, TYPE.search(activeLanguage.isEmpty()));
		assertContains(item, TYPE.search(activeLanguage.isNotEmpty()));

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
		catch(final NullPointerException e)
		{
			assertEquals("element", e.getMessage());
		}
		try
		{
			item.addActiveLanguage(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("element", e.getMessage());
		}
		try
		{
			item.removeActiveLanguage(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("element", e.getMessage());
		}
		try
		{
			activeLanguage.contains(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("element", e.getMessage());
		}
	}

	@Test void testSettable()
	{
		final EnumSet<Language> value = EnumSet.noneOf(EnumSetFieldItem.Language.class);

		value.add(PL);
		final EnumSetFieldItem initialItem = new EnumSetFieldItem(value);
		assertEquals(false, initialItem.containsActiveLanguage(DE));
		assertEquals(false, initialItem.containsActiveLanguage(EN));
		assertEquals(true,  initialItem.containsActiveLanguage(PL));

		value.add(EN);
		value.remove(PL);
		initialItem.set(activeLanguage.map(value));
		assertEquals(false, initialItem.containsActiveLanguage(DE));
		assertEquals(true,  initialItem.containsActiveLanguage(EN));
		assertEquals(false, initialItem.containsActiveLanguage(PL));
	}

	@Test void testSetSettableNull()
	{
		item.setActiveLanguage(EnumSet.of(DE, EN));
		final SetValue<EnumSet<Language>> map = activeLanguage.map(null);

		try
		{
			item.set(map);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(activeLanguage, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertEquals(EnumSet.of(DE, EN), item.getActiveLanguage());
	}

	@Test void testSetSetNull()
	{
		item.setActiveLanguage(EnumSet.of(DE, EN));

		try
		{
			item.setActiveLanguage(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(activeLanguage, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertEquals(EnumSet.of(DE, EN), item.getActiveLanguage());
	}

	@Test void testSubClass()
	{
		assertEquals(false, item.containsActiveLanguage(SUBCLASS));

		item.addActiveLanguage(SUBCLASS);
		assertEquals(true, item.containsActiveLanguage(SUBCLASS));
	}

	@SuppressWarnings({"unchecked","rawtypes"}) // OK: test bad API usage
	@Test void testUnchecked()
	{
		try
		{
			((EnumSetField)activeLanguage).contains(item, X.A);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a com.exedio.cope.pattern.EnumSetFieldItem$Language, " +
					"but was a A of com.exedio.cope.pattern.EnumSetFieldTest$X",
					e.getMessage());
		}
		try
		{
			((EnumSetField)activeLanguage).add(item, X.A);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a com.exedio.cope.pattern.EnumSetFieldItem$Language, " +
					"but was a A of com.exedio.cope.pattern.EnumSetFieldTest$X",
					e.getMessage());
		}
		try
		{
			((EnumSetField)activeLanguage).remove(item, X.A);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a com.exedio.cope.pattern.EnumSetFieldItem$Language, " +
					"but was a A of com.exedio.cope.pattern.EnumSetFieldTest$X",
					e.getMessage());
		}
		try
		{
			((EnumSetField)activeLanguage).contains(X.A);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a com.exedio.cope.pattern.EnumSetFieldItem$Language, " +
					"but was a A of com.exedio.cope.pattern.EnumSetFieldTest$X",
					e.getMessage());
		}
		try
		{
			((EnumSetField)activeLanguage).contains(item, X.SUBCLASS);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a com.exedio.cope.pattern.EnumSetFieldItem$Language, " +
					"but was a SUBCLASS of com.exedio.cope.pattern.EnumSetFieldTest$X",
					e.getMessage());
		}
	}

	enum X
	{
		A,
		SUBCLASS
		{
			@SuppressWarnings("unused")
			void zack()
			{
				// empty
			}
		}
	}
}
