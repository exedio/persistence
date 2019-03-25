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

package com.exedio.cope.misc;

import static com.exedio.cope.misc.Conditions.equal;
import static com.exedio.cope.misc.Conditions.implies;
import static com.exedio.cope.misc.Conditions.unisonNull;
import static com.exedio.cope.misc.ConditionsTest.AnItem.name1;
import static com.exedio.cope.misc.ConditionsTest.AnItem.name2;
import static com.exedio.cope.misc.ConditionsTest.AnItem.name3;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Condition;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class ConditionsTest
{
	@Test void testEqual()
	{
		assertEquals(
				"((AnItem.name1='alpha' AND AnItem.name1='beta') OR" +
				" (!(AnItem.name1='alpha') AND !(AnItem.name1='beta')))",
				equal(name1.equal("alpha"), name1.equal("beta")).toString());
	}

	@Test void testEqualNull()
	{
		final Condition c = name1.equal("beta");
		try
		{
			equal(null, c);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			equal(c, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("conditions[1]", e.getMessage());
		}
	}

	@Test void testImplies()
	{
		assertEquals(
				"(!(AnItem.name1='alpha') OR AnItem.name1='beta')",
				implies(name1.equal("alpha"), name1.equal("beta")).toString());
	}

	@Test void testImpliesNull()
	{
		final Condition c = name1.equal("beta");
		try
		{
			implies(null, c);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			implies(c, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("conditions[1]", e.getMessage());
		}
	}

	@Test void testUnisonNull()
	{
		assertEquals("TRUE", unisonNull(Collections.emptyList()).toString());
		assertEquals("TRUE", unisonNull(asList(name1)).toString());
		assertEquals("(" +
			"(AnItem.name1 is "+ "null AND AnItem.name2 is "+ "null) OR " +
			"(AnItem.name1 is not null AND AnItem.name2 is not null))",
			unisonNull(asList(name1, name2)).toString());
		assertEquals("(" +
			"(AnItem.name1 is "+ "null AND AnItem.name2 is "+ "null AND AnItem.name3 is "+ "null) OR " +
			"(AnItem.name1 is not null AND AnItem.name2 is not null AND AnItem.name3 is not null))",
			unisonNull(asList(name1, name2, name3)).toString());
	}

	@Test void testUnisonNullNull()
	{
		try
		{
			unisonNull(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@com.exedio.cope.instrument.WrapperIgnore // TODO use import, but this is not accepted by javac
	static final class AnItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final StringField name1 = new StringField();
		static final StringField name2 = new StringField();
		static final StringField name3 = new StringField();

		private static final long serialVersionUID = 1l;
		@SuppressWarnings("unused") // OK: TYPE without Model
		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);
		private AnItem(final ActivationParameters ap) { super(ap); }
	}
}
