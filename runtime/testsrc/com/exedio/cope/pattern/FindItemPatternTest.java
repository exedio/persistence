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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.ListFieldItem.strings;
import static com.exedio.cope.pattern.ListFieldItem.stringsParent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.Item;
import com.exedio.cope.NoSuchIDException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class FindItemPatternTest extends AbstractRuntimeModelTest
{
	public FindItemPatternTest()
	{
		super(ListFieldTest.MODEL);
	}

	ListFieldItem item;

	@Before public final void setUp()
	{
		item = new ListFieldItem();
		item.addToStrings("xxzero");
		item.addToStrings("xxone");
		item.addToStrings("xxtwo");
	}

	@Test public void test() throws NoSuchIDException
	{
		final List<? extends Item> items = strings.getRelationType().search(
				stringsParent().equal(item),
				strings.getOrder(),
				true);
		final Item item0 = items.get(0);
		final Item item1 = items.get(1);
		final Item item2 = items.get(2);

		assertEquals("ListFieldItem-strings-0", item0.getCopeID());
		assertEquals("ListFieldItem-strings-1", item1.getCopeID());
		assertEquals("ListFieldItem-strings-2", item2.getCopeID());

		assertSame(item0, model.getItem("ListFieldItem-strings-0")); // important to test with zero as well
		assertSame(item1, model.getItem("ListFieldItem-strings-1"));
		assertSame(item2, model.getItem("ListFieldItem-strings-2"));

		assertIDFails("ListFieldItem-strings-10", "item <10> does not exist", false);
		assertIDFails("ListFieldItem-strings10",  "wrong number format <strings10>", true);
	}
}
