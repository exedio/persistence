/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.misc.Conditions.unisonNull;
import static java.util.Arrays.asList;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Function;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.junit.CopeAssert;
import java.util.Collections;

public class ConditionsTest extends CopeAssert
{
	public void testIt()
	{
		assertEquals("TRUE", unisonNull(Collections.<Function<?>>emptyList()).toString());
		assertEquals("TRUE", unisonNull(asList(AnItem.name1)).toString());
		assertEquals("(" +
			"(AnItem.name1 is "+ "null AND AnItem.name2 is "+ "null) OR " +
			"(AnItem.name1 is not null AND AnItem.name2 is not null))",
			unisonNull(asList(AnItem.name1, AnItem.name2)).toString());
		assertEquals("(" +
			"(AnItem.name1 is "+ "null AND AnItem.name2 is "+ "null AND AnItem.name3 is "+ "null) OR " +
			"(AnItem.name1 is not null AND AnItem.name2 is not null AND AnItem.name3 is not null))",
			unisonNull(asList(AnItem.name1, AnItem.name2, AnItem.name3)).toString());
	}

	public void testError()
	{
		try
		{
			Conditions.unisonNull(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	static final class AnItem extends Item
	{
		static final StringField name1 = new StringField();
		static final StringField name2 = new StringField();
		static final StringField name3 = new StringField();

		private static final long serialVersionUID = 1l;
		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);
		private AnItem(final ActivationParameters ap) { super(ap); }
	}
}
