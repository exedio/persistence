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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CopeName;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.WrapperIgnore;
import org.junit.jupiter.api.Test;

public class CopeNameUtilTest
{
	@Test void testIt()
	{
		assertEquals("naked",        AnItem.naked.getName());
		assertEquals("nameAnno",     AnItem.name .getName());
		assertEquals("idAnno",       AnItem.id   .getName());
		assertEquals("bothAnnoName", AnItem.both .getName());
	}

	@WrapperIgnore
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	static final class AnItem extends Item
	{
		static final StringField naked = new StringField();

		@CopeName("nameAnno")
		static final StringField name = new StringField();

		@com.exedio.cope.CopeID("idAnno")
		static final StringField id = new StringField();

		@CopeName("bothAnnoName")
		@com.exedio.cope.CopeID("bothAnnoID")
		static final StringField both = new StringField();

		private static final long serialVersionUID = 1l;
		@SuppressWarnings("unused") // OK: TYPE without Model
		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);
		private AnItem(final ActivationParameters ap) { super(ap); }
	}
}
