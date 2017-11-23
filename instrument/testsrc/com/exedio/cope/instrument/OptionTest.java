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

package com.exedio.cope.instrument;

import static com.exedio.cope.instrument.Tags.forFeature;
import static com.exedio.cope.instrument.Tags.forIgnore;
import static com.exedio.cope.instrument.Tags.forInitial;
import static com.exedio.cope.instrument.Tags.forType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Locale;
import org.junit.jupiter.api.Test;

public class OptionTest
{
	@Test public void testType()
	{
		assertEquals("public,default,default,default,1", desc(forType("@cope.type public")));
		assertEquals("default,public,default,default,1", desc(forType("@cope.constructor public")));
		assertEquals("default,default,public,default,1", desc(forType("@cope.generic.constructor public")));
		assertEquals("default,default,default,public,1", desc(forType("@cope.activation.constructor public")));
		assertEquals("default,default,default,default,333", desc(forType("@cope.indent 333")));
	}
	@Test public void testTypeVisibility()
	{
		assertEquals("none,default,default,default,1",      desc(forType("@cope.type none")));
		assertEquals("private,default,default,default,1",   desc(forType("@cope.type private")));
		assertEquals("package,default,default,default,1",   desc(forType("@cope.type package")));
		assertEquals("protected,default,default,default,1", desc(forType("@cope.type protected")));
		assertEquals("public,default,default,default,1",    desc(forType("@cope.type public")));
	}
	@Test public void testTypeEmpty()
	{
		assertEquals(null, forType(null));
		assertEquals(null, forType(""));
	}
	private static String desc(final WrapperType ann)
	{
		return
				desc(ann.type()) + "," +
				desc(ann.constructor()) + "," +
				desc(ann.genericConstructor()) + "," +
				desc(ann.activationConstructor()) + "," +
				ann.indent();
	}

	@Test public void testFeatureEmpty()
	{
		assertEquals(null, forFeature(null, "zack"));
		assertEquals(null, forFeature("", "zack"));
		assertEquals(null, forFeature("@cope.zack", "zack"));
		assertEquals(null, forFeature("@cope.zack public", "zick"));
	}
	@Test public void testFeature()
	{
		assertEquals("default",           desc(forFeature("@cope.zack ", "zack")));
		assertEquals("public",            desc(forFeature("@cope.zack public", "zack")));
		assertEquals("default,internal",  desc(forFeature("@cope.zack internal", "zack")));
		assertEquals("default,boolean",   desc(forFeature("@cope.zack boolean-as-is", "zack")));
		assertEquals("default,non-final", desc(forFeature("@cope.zack non-final", "zack")));
		assertEquals("default,override",  desc(forFeature("@cope.zack override", "zack")));
	}
	private static String desc(final Wrapper ann)
	{
		return
				desc(ann.visibility()) +
				desc(ann.internal(), "internal") +
				desc(ann.booleanAsIs(), "boolean") +
				desc(!ann.asFinal(), "non-final") +
				desc(ann.override(), "override");
	}
	private static String desc(final boolean value, final String name)
	{
		if(!value)
			return "";

		return "," + name;
	}
	private static String desc(final Visibility value)
	{
		return value.name().toLowerCase(Locale.ENGLISH);
	}

	@Test public void testIgnore()
	{
		assertNull(forIgnore(null));
		assertNull(forIgnore(""));
		assertNotNull(forIgnore("@cope.ignore"));
		assertNotNull(forIgnore("@cope.ignore "));
		assertNull(forIgnore("@cope.ignorex"));
	}

	@Test public void testInitial()
	{
		assertNull(forInitial(null));
		assertNull(forInitial(""));
		assertNotNull(forInitial("@cope.initial"));
		assertNotNull(forInitial("@cope.initial "));
		assertNull(forInitial("@cope.initialx"));
	}
}
