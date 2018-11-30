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

package com.exedio.cope;

import static com.exedio.cope.DateFieldDefaultToNowItem.TYPE;
import static com.exedio.cope.DateFieldDefaultToNowItem.mandatory;
import static com.exedio.cope.DateFieldDefaultToNowItem.none;
import static com.exedio.cope.DateFieldDefaultToNowItem.optional;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import org.junit.jupiter.api.Test;

public class DateFieldDefaultToNowModelTest
{
	public static final Model MODEL = new Model(TYPE);

	@Test void testIt()
	{
		assertEquals(list(
				TYPE.getThis(),
				mandatory, optional, none
				), TYPE.getDeclaredFeatures());

		assertEquals(true,  mandatory.hasDefault());
		assertEquals(true,  optional.hasDefault());
		assertEquals(false, none.hasDefault());

		assertEquals(null, mandatory.getDefaultConstant());
		assertEquals(null, optional.getDefaultConstant());
		assertEquals(null, none.getDefaultConstant());

		assertEquals(true,  mandatory.isDefaultNow());
		assertEquals(true,  optional.isDefaultNow());
		assertEquals(false, none.isDefaultNow());

		{
			final DateField feature = mandatory.defaultTo(new Date(444));
			assertEquals(true, feature.hasDefault());
			assertEquals(new Date(444), feature.getDefaultConstant());
			assertEquals(false, feature.isDefaultNow());
		}
	}
}
