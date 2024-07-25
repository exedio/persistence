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

import static com.exedio.cope.StringConditionTest.MyItem.TYPE;
import static com.exedio.cope.StringConditionTest.MyItem.field;
import static com.exedio.cope.instrument.Visibility.NONE;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import java.util.List;
import org.junit.jupiter.api.Test;

public class StringConditionTest extends TestWithEnvironment
{
	public StringConditionTest()
	{
		super(MODEL);
	}

	@Test void testLikeEmpty()
	{
		final MyItem some = new MyItem("startEnd");
		final MyItem other = new MyItem("other");
		final MyItem empty = new MyItem("");
		new MyItem((String)null);

		assertEquals(asList(some), search(field.startsWith("start")));
		assertEquals(asList(some), search(field.  endsWith("End")));
		assertEquals(asList(some), search(field.  contains("tE")));
		assertEquals(asList(some), search(field.startsWithIgnoreCase("START")));
		assertEquals(asList(some), search(field.  endsWithIgnoreCase("END")));
		assertEquals(asList(some), search(field.  containsIgnoreCase("TE")));
		assertEquals(asList(some, other, empty), search(field.startsWith("")));
		assertEquals(asList(some, other, empty), search(field.  endsWith("")));
		assertEquals(asList(some, other, empty), search(field.  contains("")));
		assertEquals(asList(some, other, empty), search(field.startsWithIgnoreCase("")));
		assertEquals(asList(some, other, empty), search(field.  endsWithIgnoreCase("")));
		assertEquals(asList(some, other, empty), search(field.  containsIgnoreCase("")));

		// space padding
		final boolean pad = hsqldb && model.getConnectProperties().isSupportDisabledForPreparedStatements();
		assertEquals(asList(some ), search(field.like( "startEnd")));
		assertEquals(pad?asList(some):asList(), search(field.like("startEnd ")));
		assertEquals(asList(     ), search(field.like(" startEnd")));
		assertEquals(pad?asList(empty):asList(), search(field.like(" ")));
		assertEquals(asList(empty), search(field.like("")));
	}

	@Test void testLikeWildcard()
	{
		final MyItem some = new MyItem("startEnd");
		final MyItem someP = new MyItem("start%End");
		final MyItem someU = new MyItem("start_End");
		final MyItem someB = new MyItem("start\\End");
		final MyItem someX = new MyItem("startXEnd");
		final MyItem someXY = new MyItem("startXYEnd");
		new MyItem("other");
		new MyItem("");
		new MyItem((String)null);

		// https://hsqldb.org/doc/2.0/guide/dataaccess-chapt.html#dac_sql_predicates
		// https://dev.mysql.com/doc/refman/8.0/en/string-comparison-functions.html#operator_like
		// https://www.postgresql.org/docs/11/functions-matching.html#FUNCTIONS-LIKE
		assertEquals(asList(some                                    ), search(field.like("sta%tEnd")));
		assertEquals(asList(some                                    ), search(field.like("sta_tEnd")));
		assertEquals(asList(some                                    ), search(field.like("startEnd")));
		assertEquals(asList(some, someP, someU, someB, someX, someXY), search(field.like("start%End")));
		assertEquals(asList(      someP, someU, someB, someX        ), search(field.like("start_End")));
		assertEquals(asList(some ), search(field.like("start\\End")));
		assertEquals(asList(someB), search(field.like("start\\\\End")));
		assertEquals(asList(someP), search(field.like("start\\%End")));
		assertEquals(asList(someU), search(field.like("start\\_End")));

		assertEquals(asList(some, someP, someU, someB, someX, someXY), search(field.startsWith("start")));
		assertEquals(asList(                                        ), search(field.startsWith("st%t")));
		assertEquals(asList(                                        ), search(field.startsWith("st_rt")));
		assertEquals(asList(some                                    ), search(field.startsWith("startEn")));
		assertEquals(asList(      someP                             ), search(field.startsWith("start%E")));
		assertEquals(asList(             someU                      ), search(field.startsWith("start_E")));

		assertEquals(asList(some, someP, someU, someB, someX, someXY), search(field.endsWith("End")));
		assertEquals(asList(                                        ), search(field.endsWith("E%d")));
		assertEquals(asList(                                        ), search(field.endsWith("E_d")));
		assertEquals(asList(some                                    ), search(field.endsWith("rtEnd")));
		assertEquals(asList(      someP                             ), search(field.endsWith("rt%End")));
		assertEquals(asList(             someU                      ), search(field.endsWith("rt_End")));

		assertEquals(asList(some, someP, someU, someB, someX, someXY), search(field.contains("tart")));
		assertEquals(asList(                                        ), search(field.contains("ta%t")));
		assertEquals(asList(                                        ), search(field.contains("ta_t")));
		assertEquals(asList(some                                    ), search(field.contains("tartEn")));
		assertEquals(asList(      someP                             ), search(field.contains("tart%En")));
		assertEquals(asList(             someU                      ), search(field.contains("tart_En")));

		assertEquals(asList(), search(field.startsWith("start\\%E")));
		assertEquals(asList(), search(field.startsWith("start\\_E")));
		assertEquals(asList(), search(field.startsWith("start\\\\E")));
		assertEquals(asList(), search(field.endsWith("art\\%End")));
		assertEquals(asList(), search(field.endsWith("art\\_End")));
		assertEquals(asList(), search(field.endsWith("art\\\\End")));
		assertEquals(asList(), search(field.contains("art\\%En")));
		assertEquals(asList(), search(field.contains("art\\_En")));
		assertEquals(asList(), search(field.contains("art\\\\En")));
	}

	@Test void testIgnoreCase()
	{
		final MyItem mixed = new MyItem("lowerUPPER");
		final MyItem lower = new MyItem("lowerupper");
		final MyItem upper = new MyItem("LOWERUPPER");
		new MyItem((String)null);

		assertEquals(asList(mixed, lower, upper), search(field.     equalIgnoreCase("lowerUPPER" )));
		assertEquals(asList(mixed, lower, upper), search(field.      likeIgnoreCase("lowerUPPER%")));
		assertEquals(asList(mixed, lower, upper), search(field.startsWithIgnoreCase("lowerUPPER" )));
		assertEquals(asList(mixed, lower, upper), search(field.  endsWithIgnoreCase("lowerUPPER" )));
		assertEquals(asList(mixed, lower, upper), search(field.  containsIgnoreCase("lowerUPPER" )));

		assertEquals(asList(), search(field.     equalIgnoreCase("lowerUPPEX" )));
		assertEquals(asList(), search(field.      likeIgnoreCase("lowerUPPEX%")));
		assertEquals(asList(), search(field.startsWithIgnoreCase("lowerUPPEX" )));
		assertEquals(asList(), search(field.  endsWithIgnoreCase("lowerUPPEX" )));
		assertEquals(asList(), search(field.  containsIgnoreCase("lowerUPPEX" )));
	}

	@Test void testIgnoreCaseSZ()
	{
		final MyItem mixed = new MyItem("lower\u00dfUPPER");
		final MyItem lower = new MyItem("lower\u00dfupper");
		final MyItem upper = new MyItem("LOWER\u00dfUPPER");
		new MyItem("lowerUPPER");
		new MyItem((String)null);
		assertEquals(asList(mixed, lower, upper), search(field.equalIgnoreCase("lower\u00dfUPPER")));
		assertEquals(asList(mixed, lower, upper), search(field. likeIgnoreCase("lower\u00dfUPPER")));
	}

	@Test void testIgnoreCaseFunction()
	{
		final MyItem same = new MyItem("lowerUPPER", "lowerUPPER");
		final MyItem lower = new MyItem("lowerUPPER", "lowerupper");
		final MyItem upper = new MyItem("lowerUPPER", "LOWERUPPER");
		new MyItem("lowerUPPER", "LOWERXPPER");
		new MyItem(null, "lowerUPPER");
		new MyItem("lowerUPPER", null);
		new MyItem(null, null);

		assertEquals(List.of(same, lower, upper), search(field.equalIgnoreCase(MyItem.right)));
	}

	private static List<MyItem> search(final Condition condition)
	{
		final Query<MyItem> q = TYPE.newQuery(condition);
		q.setOrderByThis(true);
		return q.search();
	}

	@WrapperType(indent=2, comments=false)
	static final class MyItem extends Item
	{
		@WrapperInitial
		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=NONE)
		static final StringField field = new StringField().optional().lengthMin(0);

		@WrapperInitial
		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=NONE)
		static final StringField right = new StringField().optional().lengthMin(0);

		MyItem(final String field)
		{
			this(field, "RIGHT_DUMMY_VALUE");
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		MyItem(
					@javax.annotation.Nullable final java.lang.String field,
					@javax.annotation.Nullable final java.lang.String right)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.field,field),
				com.exedio.cope.SetValue.map(MyItem.right,right),
			});
		}

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(TYPE);
}
