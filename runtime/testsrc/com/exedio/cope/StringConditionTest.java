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

	@Test void testLikeEmpty()
	{
		final StringItem some = new StringItem("startEnd", true);
		final StringItem other = new StringItem("other", true);
		final StringItem empty = new StringItem("", true);
		new StringItem(null, true);

		assertEquals(asList(some), search(any.startsWith("start")));
		assertEquals(asList(some), search(any.  endsWith("End")));
		assertEquals(asList(some), search(any.  contains("tE")));
		assertEquals(asList(some), search(any.startsWithIgnoreCase("START")));
		assertEquals(asList(some), search(any.  endsWithIgnoreCase("END")));
		assertEquals(asList(some), search(any.  containsIgnoreCase("TE")));
		assertEquals(asList(some, other, empty), search(any.startsWith("")));
		assertEquals(asList(some, other, empty), search(any.  endsWith("")));
		assertEquals(asList(some, other, empty), search(any.  contains("")));
		assertEquals(asList(some, other, empty), search(any.startsWithIgnoreCase("")));
		assertEquals(asList(some, other, empty), search(any.  endsWithIgnoreCase("")));
		assertEquals(asList(some, other, empty), search(any.  containsIgnoreCase("")));
	}

	@Test void testLikeWildcard()
	{
		final StringItem some = new StringItem("startEnd", true);
		final StringItem someP = new StringItem("start%End", true);
		final StringItem someU = new StringItem("start_End", true);
		final StringItem someB = new StringItem("start\\End", true);
		final StringItem someX = new StringItem("startXEnd", true);
		final StringItem someXY = new StringItem("startXYEnd", true);
		new StringItem("other", true);
		new StringItem("", true);
		new StringItem(null, true);

		// https://hsqldb.org/doc/2.0/guide/dataaccess-chapt.html#dac_sql_predicates
		// https://dev.mysql.com/doc/refman/8.0/en/string-comparison-functions.html#operator_like
		// https://www.postgresql.org/docs/11/functions-matching.html#FUNCTIONS-LIKE
		assertEquals(asList(some                                    ), search(any.like("sta%tEnd")));
		assertEquals(asList(some                                    ), search(any.like("sta_tEnd")));
		assertEquals(asList(some                                    ), search(any.like("startEnd")));
		assertEquals(asList(some, someP, someU, someB, someX, someXY), search(any.like("start%End")));
		assertEquals(asList(      someP, someU, someB, someX        ), search(any.like("start_End")));
		assertEquals(asList(some ), search(any.like("start\\End")));
		assertEquals(asList(someB), search(any.like("start\\\\End")));
		assertEquals(asList(someP), search(any.like("start\\%End")));
		assertEquals(asList(someU), search(any.like("start\\_End")));

		assertEquals(asList(some, someP, someU, someB, someX, someXY), search(any.startsWith("start")));
		assertEquals(asList(                                        ), search(any.startsWith("st%t")));
		assertEquals(asList(                                        ), search(any.startsWith("st_rt")));
		assertEquals(asList(some                                    ), search(any.startsWith("startEn")));
		assertEquals(asList(      someP                             ), search(any.startsWith("start%E")));
		assertEquals(asList(             someU                      ), search(any.startsWith("start_E")));

		assertEquals(asList(some, someP, someU, someB, someX, someXY), search(any.endsWith("End")));
		assertEquals(asList(                                        ), search(any.endsWith("E%d")));
		assertEquals(asList(                                        ), search(any.endsWith("E_d")));
		assertEquals(asList(some                                    ), search(any.endsWith("rtEnd")));
		assertEquals(asList(      someP                             ), search(any.endsWith("rt%End")));
		assertEquals(asList(             someU                      ), search(any.endsWith("rt_End")));

		assertEquals(asList(some, someP, someU, someB, someX, someXY), search(any.contains("tart")));
		assertEquals(asList(                                        ), search(any.contains("ta%t")));
		assertEquals(asList(                                        ), search(any.contains("ta_t")));
		assertEquals(asList(some                                    ), search(any.contains("tartEn")));
		assertEquals(asList(      someP                             ), search(any.contains("tart%En")));
		assertEquals(asList(             someU                      ), search(any.contains("tart_En")));

		assertEquals(asList(), search(any.startsWith("start\\%E")));
		assertEquals(asList(), search(any.startsWith("start\\_E")));
		assertEquals(asList(), search(any.startsWith("start\\\\E")));
		assertEquals(asList(), search(any.endsWith("art\\%End")));
		assertEquals(asList(), search(any.endsWith("art\\_End")));
		assertEquals(asList(), search(any.endsWith("art\\\\End")));
		assertEquals(asList(), search(any.contains("art\\%En")));
		assertEquals(asList(), search(any.contains("art\\_En")));
		assertEquals(asList(), search(any.contains("art\\\\En")));
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
