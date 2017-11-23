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

import static com.exedio.cope.testmodel.StringItem.TYPE;
import static com.exedio.cope.testmodel.StringItem.any;
import static com.exedio.cope.testmodel.StringItem.exact6;
import static com.exedio.cope.testmodel.StringItem.lowercase;
import static com.exedio.cope.testmodel.StringItem.mandatory;
import static com.exedio.cope.testmodel.StringItem.max4;
import static com.exedio.cope.testmodel.StringItem.min4;
import static com.exedio.cope.testmodel.StringItem.min4Max8;
import static com.exedio.cope.testmodel.StringItem.min4Upper;
import static com.exedio.cope.tojunit.Assert.assertEqualsStrict;
import static com.exedio.cope.tojunit.Assert.assertNotEqualsStrict;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.CharSet;
import org.junit.jupiter.api.Test;

public class StringModelTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(StringModelTest.class, "MODEL");
	}

	@Test public void testModel()
	{
		assertEquals(TYPE, any.getType());
		assertEquals("any", any.getName());
		assertEquals(false, any.isMandatory());
		assertEquals(null, any.getPattern());
		assertEquals(0, any.getMinimumLength());
		assertEquals(StringField.DEFAULT_MAXIMUM_LENGTH, any.getMaximumLength());
		assertEquals(null, any.getCharSet());

		assertEquals(TYPE, mandatory.getType());
		assertEquals("mandatory", mandatory.getName());
		assertEquals(true, mandatory.isMandatory());

		assertEquals(4, min4.getMinimumLength());
		assertEquals(StringField.DEFAULT_MAXIMUM_LENGTH, min4.getMaximumLength());

		assertEquals(0, max4.getMinimumLength());
		assertEquals(4, max4.getMaximumLength());
		assertEquals(null, max4.getCharSet());

		assertEquals(4, min4Max8.getMinimumLength());
		assertEquals(8, min4Max8.getMaximumLength());
		assertEquals(null, min4Max8.getCharSet());

		assertEquals(6, exact6.getMinimumLength());
		assertEquals(6, exact6.getMaximumLength());
		assertEquals(null, exact6.getCharSet());

		assertEquals(0, lowercase.getMinimumLength());
		assertEquals(StringField.DEFAULT_MAXIMUM_LENGTH, lowercase.getMaximumLength());
		assertEquals(new CharSet('a', 'z'), lowercase.getCharSet());

		assertEquals(TYPE, min4Upper.getType());
		assertEquals("min4Upper", min4Upper.getName());
	}

	@Test public void testConditions()
	{
		assertEqualsStrict(any.equal("hallo"), any.equal("hallo"));
		assertNotEqualsStrict(any.equal("hallo"), any.equal("bello"));
		assertNotEqualsStrict(any.equal("hallo"), any.equal((String)null));
		assertNotEqualsStrict(any.equal("hallo"), any.like("hallo"));
		assertEqualsStrict(any.equal(mandatory), any.equal(mandatory));
		assertNotEqualsStrict(any.equal(mandatory), any.equal(any));
	}

	@Test public void testConditionsConvenience()
	{
		assertEquals(any.startsWith("hallo"), any.like("hallo%"));
		assertEquals(any.endsWith("hallo"), any.like("%hallo"));
		assertEquals(any.contains("hallo"), any.like("%hallo%"));
		assertEquals(any.equalIgnoreCase("hallo"), any.toUpperCase().equal("HALLO"));
		assertEquals(any.likeIgnoreCase("hallo%"), any.toUpperCase().like("HALLO%"));
		assertEquals(any.startsWithIgnoreCase("hallo"), any.toUpperCase().like("HALLO%"));
		assertEquals(any.endsWithIgnoreCase("hallo"), any.toUpperCase().like("%HALLO"));
		assertEquals(any.containsIgnoreCase("hallo"), any.toUpperCase().like("%HALLO%"));
	}
}
