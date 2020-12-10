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
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.testmodel.StringItem;
import java.util.List;
import org.junit.jupiter.api.Test;

public class StringConditionTest extends TestWithEnvironment
{
	public StringConditionTest()
	{
		super(StringModelTest.MODEL);
	}

	@Test void testIgnoreCase()
	{
		final StringItem mixed = new StringItem("lowerUPPER", true);
		final StringItem lower = new StringItem("lowerupper", true);
		final StringItem upper = new StringItem("LOWERUPPER", true);
		new StringItem(null, true);

		assertEquals(asList(mixed, lower, upper), search(any.     equalIgnoreCase("lowerUPPER" )));
		assertEquals(asList(mixed, lower, upper), search(any.      likeIgnoreCase("lowerUPPER%")));
		assertEquals(asList(mixed, lower, upper), search(any.startsWithIgnoreCase("lowerUPPER" )));
		assertEquals(asList(mixed, lower, upper), search(any.  endsWithIgnoreCase("lowerUPPER" )));
		assertEquals(asList(mixed, lower, upper), search(any.  containsIgnoreCase("lowerUPPER" )));

		assertEquals(asList(), search(any.     equalIgnoreCase("lowerUPPEX" )));
		assertEquals(asList(), search(any.      likeIgnoreCase("lowerUPPEX%")));
		assertEquals(asList(), search(any.startsWithIgnoreCase("lowerUPPEX" )));
		assertEquals(asList(), search(any.  endsWithIgnoreCase("lowerUPPEX" )));
		assertEquals(asList(), search(any.  containsIgnoreCase("lowerUPPEX" )));
	}

	@Test void testIgnoreCaseSZ()
	{
		final StringItem mixed = new StringItem("lower\u00dfUPPER", true);
		final StringItem lower = new StringItem("lower\u00dfupper", true);
		final StringItem upper = new StringItem("LOWER\u00dfUPPER", true);
		new StringItem("lowerUPPER", true);
		new StringItem(null, true);
		assertEquals(asList(mixed, lower, upper), search(any.equalIgnoreCase("lower\u00dfUPPER")));
		assertEquals(asList(mixed, lower, upper), search(any. likeIgnoreCase("lower\u00dfUPPER")));
	}

	private static List<StringItem> search(final Condition condition)
	{
		final Query<StringItem> q = TYPE.newQuery(condition);
		q.setOrderByThis(true);
		return q.search();
	}
}
