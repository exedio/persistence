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

import static com.exedio.cope.pattern.EnumMapFieldFinalItem.TYPE;
import static com.exedio.cope.pattern.EnumMapFieldFinalItem.text;
import static com.exedio.cope.pattern.EnumMapFieldItem.Language.DE;
import static com.exedio.cope.pattern.EnumMapFieldItem.Language.EN;
import static com.exedio.cope.pattern.EnumMapFieldItem.Language.PL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.EnumMapFieldItem.Language;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.EnumMap;
import org.junit.Test;

public class EnumMapFieldFinalTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	public EnumMapFieldFinalTest()
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
	}


	@Test public void testModel()
	{
		assertEquals(null, text.getField(DE).getDefaultConstant());
		assertEquals(null, text.getField(EN).getDefaultConstant());
		assertEquals(null, text.getField(PL).getDefaultConstant());
		assertEquals(true,  text.isFinal());
		assertEquals(true,  text.isMandatory());
		assertEquals(true,  text.isInitial());
	}

	@Test public void testEmpty()
	{
		final EnumMapFieldFinalItem item = new EnumMapFieldFinalItem(EMPTY);
		assertEquals(null, item.getText(DE));
		assertEquals(null, item.getText(EN));
		assertEquals(null, item.getText(PL));

		try
		{
			item.setText(DE, "zack");
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(text.getField(DE), e.getFeature());
		}

		try
		{
			item.set(text.map(FULL));
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(text.getField(DE), e.getFeature());
		}
	}

	@Test public void testCreateWithoutMapping()
	{
		final EnumMapFieldFinalItem item = new EnumMapFieldFinalItem();
		assertEquals(null, item.getText(DE));
		assertEquals(null, item.getText(EN));
		assertEquals(null, item.getText(PL));
	}

	@SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
	@Test public void testCreateNull()
	{
		try
		{
			new EnumMapFieldFinalItem(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(text, e.getFeature());
		}
	}

	@Test public void testFallbackFails()
	{
		final EnumMapFieldFinalItem item = new EnumMapFieldFinalItem();
		try
		{
			item.getTextWithFallback(DE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("field EnumMapFieldFinalItem.text has no fallbacks", e.getMessage());
		}
		try
		{
			item.getTextMapWithFallback();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("field EnumMapFieldFinalItem.text has no fallbacks", e.getMessage());
		}
	}
}
