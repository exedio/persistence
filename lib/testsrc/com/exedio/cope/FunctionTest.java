/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.testmodel.StringItem;

public class FunctionTest extends TestmodelTest
{
	StringItem item1;
	StringItem item2;
	
	private final StringItem newItem(final String min4, final String max4) throws Exception
	{
		final StringItem result = new StringItem();
		deleteOnTearDown(result);
		result.setMin4(min4);
		result.setMax4(max4);
		return result;
	}
	
	public void setUp() throws Exception
	{
		super.setUp();
		item1 = newItem("5ffff", "4ddd");
		item2 = newItem("6ggggg", "2b");
	}
	
	public void testFunctions()
	{
		assertEquals("5ffff", item1.getMin4());
		assertEquals("5FFFF", item1.getMin4Upper());
		assertEquals(new Integer(5), item1.getMin4UpperLength());
		assertEquals("4ddd", item1.getMax4());
		assertEquals("4DDD", item1.getMax4Upper());
		assertEquals(new Integer(4), item1.getMax4UpperLength());
		assertEquals(new Integer(9), item1.getMin4AndMax4UpperLength());

		assertEquals("6ggggg", item2.getMin4());
		assertEquals("6GGGGG", item2.getMin4Upper());
		assertEquals(new Integer(6), item2.getMin4UpperLength());
		assertEquals("2b", item2.getMax4());
		assertEquals("2B", item2.getMax4Upper());
		assertEquals(new Integer(2), item2.getMax4UpperLength());
		assertEquals(new Integer(8), item2.getMin4AndMax4UpperLength());
		
		assertContains(item1, item1.TYPE.search(item1.min4.equal("5ffff")));
		assertContains(item1, item1.TYPE.search(item1.min4Upper.equal("5FFFF")));
		assertContains(item1, item1.TYPE.search(item1.min4UpperLength.equal(5)));
		assertContains(item1, item1.TYPE.search(Cope.length(item1.min4Upper).equal(5)));
		assertContains(item1, item1.TYPE.search(item1.min4AndMax4UpperLength.equal(9)));
		assertContains(item1, item1.TYPE.search(Cope.sum(Cope.length(item1.min4Upper), Cope.length(item1.max4Upper)).equal(9)));
		
		assertContains(
				list("5ffff",  "5FFFF",  new Integer(5), "4ddd", "4DDD", new Integer(4), new Integer(9), new Integer(9)),
				list("6ggggg", "6GGGGG", new Integer(6), "2b",   "2B",   new Integer(2), new Integer(8), new Integer(8)),
				new Query(new Function[]{
						item1.min4, item1.min4Upper, item1.min4UpperLength,
						item1.max4, item1.max4Upper, item1.max4UpperLength,
						item1.min4AndMax4UpperLength,
						Cope.sum(Cope.length(item1.min4Upper), Cope.length(item1.max4Upper)),
						}, item1.TYPE, null).search()
				);
	}

}
