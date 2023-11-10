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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.misc.Conditions.equal;
import static com.exedio.cope.misc.Conditions.implies;
import static com.exedio.cope.misc.Conditions.unisonNull;
import static com.exedio.cope.misc.ConditionsTest.AnItem.name1;
import static com.exedio.cope.misc.ConditionsTest.AnItem.name2;
import static com.exedio.cope.misc.ConditionsTest.AnItem.name3;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class ConditionsTest
{
	@Test void testEqual()
	{
		assertEquals(
				"((AnItem.name1='alpha' and AnItem.name1='beta') or" +
				" (AnItem.name1<>'alpha' and AnItem.name1<>'beta'))",
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
				"(AnItem.name1<>'alpha' or AnItem.name1='beta')",
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
			"(AnItem.name1 is "+ "null and AnItem.name2 is "+ "null) or " +
			"(AnItem.name1 is not null and AnItem.name2 is not null))",
			unisonNull(asList(name1, name2)).toString());
		assertEquals("(" +
			"(AnItem.name1 is "+ "null and AnItem.name2 is "+ "null and AnItem.name3 is "+ "null) or " +
			"(AnItem.name1 is not null and AnItem.name2 is not null and AnItem.name3 is not null))",
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

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class AnItem extends Item
	{
		@WrapperIgnore static final StringField name1 = new StringField();
		@WrapperIgnore static final StringField name2 = new StringField();
		@WrapperIgnore static final StringField name3 = new StringField();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
