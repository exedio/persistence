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

import static com.exedio.cope.pattern.EnumMapFieldItem.Language.DE;
import static com.exedio.cope.pattern.EnumMapFieldItem.Language.EN;
import static com.exedio.cope.pattern.EnumMapFieldItem.Language.PL;
import static com.exedio.cope.pattern.EnumMapFieldItem.Language.SUBCLASS;
import static com.exedio.cope.pattern.EnumMapFieldMandatoryItem.TYPE;
import static com.exedio.cope.pattern.EnumMapFieldMandatoryItem.text;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.EnumMapFieldItem.Language;
import java.util.EnumMap;
import org.junit.jupiter.api.Test;

public class EnumMapFieldMandatoryTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	public EnumMapFieldMandatoryTest()
	{
		super(MODEL);
	}

	private static final EnumMap<Language, String> FULL  = new EnumMap<>(Language.class);
	private static final EnumMap<Language, String> EMPTY = new EnumMap<>(Language.class);

	static
	{
		FULL.put(DE, "full-de");
		FULL.put(EN, "full-en");
		FULL.put(PL, "full-pl");
		FULL.put(SUBCLASS, "full-subclass");
	}

	@Test void testModel()
	{
		assertEquals("defaultDE", text.getField(DE).getDefaultConstant());
		assertEquals("defaultEN", text.getField(EN).getDefaultConstant());
		assertEquals(null,        text.getField(PL).getDefaultConstant());
		assertEquals(false, text.isFinal());
		assertEquals(true,  text.isMandatory());
		assertEquals(true,  text.isInitial());
	}

	@Test void testSet()
	{
		final EnumMapFieldMandatoryItem item = new EnumMapFieldMandatoryItem(FULL);

		assertEquals("full-de", item.getText(DE));
		assertEquals("full-en", item.getText(EN));
		assertEquals("full-pl", item.getText(PL));

		try
		{
			item.setText(DE, null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(text.getField(DE), e.getFeature());
		}
		assertEquals("full-de", item.getText(DE));

		try
		{
			item.set(text.map(EMPTY));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(text.getField(DE), e.getFeature());
		}
		assertEquals("full-de", item.getText(DE));
	}

	@Test void testCreateEmpty()
	{
		final EnumMap<Language, String> value = new EnumMap<>(Language.class);
		value.put(PL, "initial-pl");
		try
		{
			new EnumMapFieldMandatoryItem(value);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(text.getField(DE), e.getFeature());
		}
	}

	@Test void testCreateWithoutMapping()
	{
		try
		{
			new EnumMapFieldMandatoryItem();
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(text.getField(PL), e.getFeature());
		}
	}

	@Test void testCreateNull()
	{
		try
		{
			new EnumMapFieldMandatoryItem(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(text, e.getFeature());
		}
	}
}
