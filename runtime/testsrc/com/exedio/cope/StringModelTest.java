/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.testmodel.StringItem;
import com.exedio.cope.util.CharSet;

public class StringModelTest extends CopeAssert
{
	static final Model MODEL = new Model(StringItem.TYPE);

	static
	{
		MODEL.enableSerialization(StringModelTest.class, "MODEL");
	}

	StringItem item;

	public void testStrings()
	{
		// test model
		assertEquals(item.TYPE, item.any.getType());
		assertEquals("any", item.any.getName());
		assertEquals(false, item.any.isMandatory());
		assertEquals(null, item.any.getPattern());
		assertEquals(0, item.any.getMinimumLength());
		assertEquals(StringField.DEFAULT_MAXIMUM_LENGTH, item.any.getMaximumLength());
		assertEquals(null, item.any.getCharSet());

		assertEquals(item.TYPE, item.mandatory.getType());
		assertEquals("mandatory", item.mandatory.getName());
		assertEquals(true, item.mandatory.isMandatory());

		assertEquals(4, item.min4.getMinimumLength());
		assertEquals(StringField.DEFAULT_MAXIMUM_LENGTH, item.min4.getMaximumLength());

		assertEquals(0, item.max4.getMinimumLength());
		assertEquals(4, item.max4.getMaximumLength());
		assertEquals(null, item.max4.getCharSet());

		assertEquals(4, item.min4Max8.getMinimumLength());
		assertEquals(8, item.min4Max8.getMaximumLength());
		assertEquals(null, item.min4Max8.getCharSet());

		assertEquals(6, item.exact6.getMinimumLength());
		assertEquals(6, item.exact6.getMaximumLength());
		assertEquals(null, item.exact6.getCharSet());

		assertEquals(0, item.lowercase.getMinimumLength());
		assertEquals(StringField.DEFAULT_MAXIMUM_LENGTH, item.lowercase.getMaximumLength());
		assertEquals(new CharSet('a', 'z'), item.lowercase.getCharSet());

		assertEquals(item.TYPE, item.min4Upper.getType());
		assertEquals("min4Upper", item.min4Upper.getName());

		// test conditions
		assertEqualsStrict(item.any.equal("hallo"), item.any.equal("hallo"));
		assertNotEqualsStrict(item.any.equal("hallo"), item.any.equal("bello"));
		assertNotEqualsStrict(item.any.equal("hallo"), item.any.equal((String)null));
		assertNotEqualsStrict(item.any.equal("hallo"), item.any.like("hallo"));
		assertEqualsStrict(item.any.equal(item.mandatory), item.any.equal(item.mandatory));
		assertNotEqualsStrict(item.any.equal(item.mandatory), item.any.equal(item.any));

		// test convenience for conditions
		assertEquals(item.any.startsWith("hallo"), item.any.like("hallo%"));
		assertEquals(item.any.endsWith("hallo"), item.any.like("%hallo"));
		assertEquals(item.any.contains("hallo"), item.any.like("%hallo%"));
		assertEquals(item.any.equalIgnoreCase("hallo"), item.any.toUpperCase().equal("HALLO"));
		assertEquals(item.any.likeIgnoreCase("hallo%"), item.any.toUpperCase().like("HALLO%"));
		assertEquals(item.any.startsWithIgnoreCase("hallo"), item.any.toUpperCase().like("HALLO%"));
		assertEquals(item.any.endsWithIgnoreCase("hallo"), item.any.toUpperCase().like("%HALLO"));
		assertEquals(item.any.containsIgnoreCase("hallo"), item.any.toUpperCase().like("%HALLO%"));
	}
}
