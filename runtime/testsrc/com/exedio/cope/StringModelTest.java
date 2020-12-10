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

import static com.exedio.cope.RuntimeTester.assertFieldsCovered;
import static com.exedio.cope.testmodel.StringItem.TYPE;
import static com.exedio.cope.testmodel.StringItem.any;
import static com.exedio.cope.testmodel.StringItem.exact6;
import static com.exedio.cope.testmodel.StringItem.lowercase;
import static com.exedio.cope.testmodel.StringItem.mandatory;
import static com.exedio.cope.testmodel.StringItem.max4;
import static com.exedio.cope.testmodel.StringItem.min4;
import static com.exedio.cope.testmodel.StringItem.min4Max8;
import static com.exedio.cope.testmodel.StringItem.min4Upper;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.util.Arrays.asList;
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

	@Test void testModel()
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

	@Test void testFieldsCovered()
	{
		assertFieldsCovered(asList(any), any.like("a"));
		assertFieldsCovered(asList(mandatory), mandatory.likeIgnoreCase("a"));
	}

	@Test void testConditions()
	{
		assertEqualsAndHash(any.equal("hallo"), any.equal("hallo"));
		assertEqualsAndHash(any.equal(mandatory), any.equal(mandatory));
		assertNotEqualsAndHash(
				any.equal("hallo"),
				any.equal("bello"),
				any.equal((String)null),
				any.like("hallo"),
				any.equal(mandatory),
				any.equal(any));
	}

	@Test void testConditionsConvenience()
	{
		assertEquals(any.startsWith("lowerUPPER"), any.like( "lowerUPPER%"));
		assertEquals(any.  endsWith("lowerUPPER"), any.like("%lowerUPPER" ));
		assertEquals(any.  contains("lowerUPPER"), any.like("%lowerUPPER%"));
		assertEquals(any.     equalIgnoreCase("lowerUPPER" ), any.toUpperCase().equal( "LOWERUPPER" ));
		assertEquals(any.      likeIgnoreCase("lowerUPPER%"), any.toUpperCase().like ( "LOWERUPPER%"));
		assertEquals(any.startsWithIgnoreCase("lowerUPPER" ), any.toUpperCase().like ( "LOWERUPPER%"));
		assertEquals(any.  endsWithIgnoreCase("lowerUPPER" ), any.toUpperCase().like ("%LOWERUPPER" ));
		assertEquals(any.  containsIgnoreCase("lowerUPPER" ), any.toUpperCase().like ("%LOWERUPPER%"));
	}
}
