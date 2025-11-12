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

import static com.exedio.cope.Query.newQuery;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.testmodel.StringItem.TYPE;
import static com.exedio.cope.testmodel.StringItem.max4;
import static com.exedio.cope.testmodel.StringItem.max4Upper;
import static com.exedio.cope.testmodel.StringItem.max4UpperLength;
import static com.exedio.cope.testmodel.StringItem.min4;
import static com.exedio.cope.testmodel.StringItem.min4AndMax4UpperLength;
import static com.exedio.cope.testmodel.StringItem.min4Upper;
import static com.exedio.cope.testmodel.StringItem.min4UpperLength;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.testmodel.StringItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FunctionTest extends TestWithEnvironment
{
	public FunctionTest()
	{
		super(StringModelTest.MODEL);
	}

	StringItem item1;
	StringItem item2;

	private static StringItem newItem(final String min4, final String max4)
	{
		final StringItem result = new StringItem("FunctionTest");
		result.setMin4(min4);
		result.setMax4(max4);
		return result;
	}

	@BeforeEach final void setUp()
	{
		item1 = newItem("5ffff", "4ddd");
		item2 = newItem("6ggggg", "2b");
	}

	@Test void testFunctions()
	{
		assertSerializedSame(min4Upper, 384);
		assertSerializedSame(max4Upper, 384);
		assertSerializedSame(min4UpperLength, 390);
		assertSerializedSame(max4UpperLength, 390);
		assertSerializedSame(min4AndMax4UpperLength, 397);

		assertEquals("5ffff", item1.getMin4());
		assertEquals("5FFFF", item1.getMin4Upper());
		assertEquals(Integer.valueOf(5), item1.getMin4UpperLength());
		assertEquals("4ddd", item1.getMax4());
		assertEquals("4DDD", item1.getMax4Upper());
		assertEquals(Integer.valueOf(4), item1.getMax4UpperLength());
		assertEquals(Integer.valueOf(9), item1.getMin4AndMax4UpperLength());

		assertEquals("6ggggg", item2.getMin4());
		assertEquals("6GGGGG", item2.getMin4Upper());
		assertEquals(Integer.valueOf(6), item2.getMin4UpperLength());
		assertEquals("2b", item2.getMax4());
		assertEquals("2B", item2.getMax4Upper());
		assertEquals(Integer.valueOf(2), item2.getMax4UpperLength());
		assertEquals(Integer.valueOf(8), item2.getMin4AndMax4UpperLength());

		assertContains(item1, TYPE.search(min4.is("5ffff")));
		assertContains(item1, TYPE.search(min4Upper.is("5FFFF")));
		assertContains(item1, TYPE.search(min4UpperLength.is(5)));
		assertContains(item1, TYPE.search(min4Upper.length().is(5)));
		assertContains(item1, TYPE.search(min4AndMax4UpperLength.is(9)));
		assertContains(item1, TYPE.search(min4Upper.length().plus(max4Upper.length()).is(9)));

		assertContains(
				list("5ffff",  "5FFFF",  Integer.valueOf(5), "4ddd", "4DDD", Integer.valueOf(4), Integer.valueOf(9), Integer.valueOf(9)),
				list("6ggggg", "6GGGGG", Integer.valueOf(6), "2b",   "2B",   Integer.valueOf(2), Integer.valueOf(8), Integer.valueOf(8)),
				newQuery(new Function<?>[]{
						min4, min4Upper, min4UpperLength,
						max4, max4Upper, max4UpperLength,
						min4AndMax4UpperLength,
						min4Upper.length().plus(max4Upper.length()),
						}, TYPE, null).search()
				);
	}

}
