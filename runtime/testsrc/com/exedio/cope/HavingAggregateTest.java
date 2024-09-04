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

import static com.exedio.cope.HavingAggregateTest.MyItem.aggregated;
import static com.exedio.cope.HavingAggregateTest.MyItem.group;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import java.util.List;
import org.junit.jupiter.api.Test;

public class HavingAggregateTest extends TestWithEnvironment
{
	private static final String group1 = "group1";
	private static final String group2 = "group2";
	private static final String group3 = "group3";
	private static final String group4 = "group4";
	private static final String groupN = "groupN";

	public HavingAggregateTest()
	{
		super( MODEL );
	}

	@Test void testIt()
	{
		final Count cnt = new Count();
		final Count cds = aggregated.distinct().count();
		final Aggregate<Integer,Integer> min = aggregated.min();
		final Aggregate<Integer,Integer> max = aggregated.max();
		final Aggregate<Integer,Integer> sum = aggregated.sum();
		final Aggregate<Double ,Integer> avg = aggregated.average();

		new MyItem(group1, 11);
		new MyItem(group2, 21);
		new MyItem(group2, 22);
		new MyItem(group3, 31);
		new MyItem(group3, 32);
		new MyItem(group3, 33);
		new MyItem(group4, 42);
		new MyItem(group4, 42);
		new MyItem(group4, 42);
		new MyItem(group4, null);
		new MyItem(groupN, null);

		final Query<List<Object>> cntQ = newQuery(cnt);
		final Query<List<Object>> cdsQ = newQuery(cds);
		final Query<List<Object>> minQ = newQuery(min);
		final Query<List<Object>> maxQ = newQuery(max);
		final Query<List<Object>> sumQ = newQuery(sum);
		final Query<List<Object>> avgQ = newQuery(avg);

		assertIt(asList(asList(group1,  1  ), asList(group2,  2  ), asList(group3,  3  ), asList(group4,   4  ), asList(groupN,    1)), cntQ);
		assertIt(asList(asList(group1,  1  ), asList(group2,  2  ), asList(group3,  3  ), asList(group4,   1  ), asList(groupN,    0)), cdsQ);
		assertIt(asList(asList(group1, 11  ), asList(group2, 21  ), asList(group3, 31  ), asList(group4,  42  ), asList(groupN, null)), minQ);
		assertIt(asList(asList(group1, 11  ), asList(group2, 22  ), asList(group3, 33  ), asList(group4,  42  ), asList(groupN, null)), maxQ);
		assertIt(asList(asList(group1, 11  ), asList(group2, 43  ), asList(group3, 96  ), asList(group4, 126  ), asList(groupN, null)), sumQ);
		assertIt(asList(asList(group1, 11.0), asList(group2, 21.5), asList(group3, 32.0), asList(group4,  42.0), asList(groupN, null)), avgQ);

		cntQ.setHaving(cnt.equal( 2));
		cdsQ.setHaving(cds.equal( 2));
		minQ.setHaving(min.equal(21));
		maxQ.setHaving(max.equal(22));
		sumQ.setHaving(sum.equal(43));
		avgQ.setHaving(avg.equal(21.5));
		assertIt(asList(asList(group2,  2  )), cntQ);
		assertIt(asList(asList(group2,  2  )), cdsQ);
		assertIt(asList(asList(group2, 21  )), minQ);
		assertIt(asList(asList(group2, 22  )), maxQ);
		assertIt(asList(asList(group2, 43  )), sumQ);
		assertIt(asList(asList(group2, 21.5)), avgQ);

		cntQ.setHaving(cnt.notEqual( 2));
		cdsQ.setHaving(cnt.notEqual( 2));
		minQ.setHaving(min.notEqual(21));
		maxQ.setHaving(max.notEqual(22));
		sumQ.setHaving(sum.notEqual(43));
		avgQ.setHaving(avg.notEqual(21.5));
		assertIt(asList(asList(group1,  1  ), asList(group3,  3  ), asList(group4,   4  ), asList(groupN, 1)), cntQ);
		assertIt(asList(asList(group1,  1  ), asList(group3,  3  ), asList(group4,   1  ), asList(groupN,    0)), cdsQ);
		assertIt(asList(asList(group1, 11  ), asList(group3, 31  ), asList(group4,  42  )), minQ);
		assertIt(asList(asList(group1, 11  ), asList(group3, 33  ), asList(group4,  42  )), maxQ);
		assertIt(asList(asList(group1, 11  ), asList(group3, 96  ), asList(group4, 126  )), sumQ);
		assertIt(asList(asList(group1, 11.0), asList(group3, 32.0), asList(group4,  42.0)), avgQ);

		cntQ.setHaving(cnt.equal((Integer)null));
		cdsQ.setHaving(cnt.equal((Integer)null));
		minQ.setHaving(min.equal((Integer)null));
		maxQ.setHaving(max.equal((Integer)null));
		sumQ.setHaving(sum.equal((Integer)null));
		avgQ.setHaving(avg.equal((Double) null));
		assertIt(asList(                    ), cntQ);
		assertIt(asList(                    ), cdsQ);
		assertIt(asList(asList(groupN, null)), minQ);
		assertIt(asList(asList(groupN, null)), maxQ);
		assertIt(asList(asList(groupN, null)), sumQ);
		assertIt(asList(asList(groupN, null)), avgQ);

		cntQ.setHaving(cnt.notEqual( 2));
		cdsQ.setHaving(cds.notEqual((Integer)null));
		minQ.setHaving(min.notEqual((Integer)null));
		maxQ.setHaving(max.notEqual((Integer)null));
		sumQ.setHaving(sum.notEqual((Integer)null));
		avgQ.setHaving(avg.notEqual((Double) null));
		assertIt(asList(asList(group1,  1  ), asList(group3,  3  ), asList(group4,  4  ), asList(groupN,   1  )), cntQ);
		assertIt(asList(asList(group1,  1  ), asList(group2,  2  ), asList(group3,  3  ), asList(group4,   1  ), asList(groupN,    0)), cdsQ);
		assertIt(asList(asList(group1, 11  ), asList(group2, 21  ), asList(group3, 31  ), asList(group4,  42  )), minQ);
		assertIt(asList(asList(group1, 11  ), asList(group2, 22  ), asList(group3, 33  ), asList(group4,  42  )), maxQ);
		assertIt(asList(asList(group1, 11  ), asList(group2, 43  ), asList(group3, 96  ), asList(group4, 126  )), sumQ);
		assertIt(asList(asList(group1, 11.0), asList(group2, 21.5), asList(group3, 32.0), asList(group4,  42.0)), avgQ);

		cntQ.setHaving(cnt.less( 2));
		cdsQ.setHaving(cds.less( 2));
		minQ.setHaving(min.less(21));
		maxQ.setHaving(max.less(22));
		sumQ.setHaving(sum.less(43));
		avgQ.setHaving(avg.less(21.5));
		assertIt(asList(asList(group1,  1  ), asList(groupN, 1)), cntQ);
		assertIt(asList(asList(group1,  1  ), asList(group4, 1), asList(groupN, 0)), cdsQ);
		assertIt(asList(asList(group1, 11  )), minQ);
		assertIt(asList(asList(group1, 11  )), maxQ);
		assertIt(asList(asList(group1, 11  )), sumQ);
		assertIt(asList(asList(group1, 11.0)), avgQ);

		cntQ.setHaving(cnt.lessOrEqual( 2));
		cdsQ.setHaving(cds.lessOrEqual( 2));
		minQ.setHaving(min.lessOrEqual(21));
		maxQ.setHaving(max.lessOrEqual(22));
		sumQ.setHaving(sum.lessOrEqual(43));
		avgQ.setHaving(avg.lessOrEqual(21.5));
		assertIt(asList(asList(group1,  1  ), asList(group2,  2  ), asList(groupN, 1)), cntQ);
		assertIt(asList(asList(group1,  1  ), asList(group2,  2  ), asList(group4, 1), asList(groupN, 0)), cdsQ);
		assertIt(asList(asList(group1, 11  ), asList(group2, 21  )), minQ);
		assertIt(asList(asList(group1, 11  ), asList(group2, 22  )), maxQ);
		assertIt(asList(asList(group1, 11  ), asList(group2, 43  )), sumQ);
		assertIt(asList(asList(group1, 11.0), asList(group2, 21.5)), avgQ);

		cntQ.setHaving(cnt.greater( 2));
		cdsQ.setHaving(cds.greater( 2));
		minQ.setHaving(min.greater(21));
		maxQ.setHaving(max.greater(22));
		sumQ.setHaving(sum.greater(43));
		avgQ.setHaving(avg.greater(21.5));
		assertIt(asList(asList(group3,  3  ), asList(group4,   4  )), cntQ);
		assertIt(asList(asList(group3,  3  )),                        cdsQ);
		assertIt(asList(asList(group3, 31  ), asList(group4,  42  )), minQ);
		assertIt(asList(asList(group3, 33  ), asList(group4,  42  )), maxQ);
		assertIt(asList(asList(group3, 96  ), asList(group4, 126  )), sumQ);
		assertIt(asList(asList(group3, 32.0), asList(group4,  42.0)), avgQ);

		cntQ.setHaving(cnt.greaterOrEqual( 2));
		cdsQ.setHaving(cds.greaterOrEqual( 2));
		minQ.setHaving(min.greaterOrEqual(21));
		maxQ.setHaving(max.greaterOrEqual(22));
		sumQ.setHaving(sum.greaterOrEqual(43));
		avgQ.setHaving(avg.greaterOrEqual(21.5));
		assertIt(asList(asList(group2,  2  ), asList(group3,  3  ), asList(group4,   4 )), cntQ);
		assertIt(asList(asList(group2,  2  ), asList(group3,  3  )),                       cdsQ);
		assertIt(asList(asList(group2, 21  ), asList(group3, 31  ), asList(group4,  42 )), minQ);
		assertIt(asList(asList(group2, 22  ), asList(group3, 33  ), asList(group4,  42 )), maxQ);
		assertIt(asList(asList(group2, 43  ), asList(group3, 96  ), asList(group4, 126 )), sumQ);
		assertIt(asList(asList(group2, 21.5), asList(group3, 32.0), asList(group4, 42.0)), avgQ);

		cntQ.setHaving(cnt.between( 2,    3  ));
		cdsQ.setHaving(cds.between( 2,    3  ));
		minQ.setHaving(min.between(21,   31  ));
		maxQ.setHaving(max.between(22,   33  ));
		sumQ.setHaving(sum.between(43,   96  ));
		avgQ.setHaving(avg.between(21.5, 32.0));
		assertIt(asList(asList(group2,  2  ), asList(group3,  3  )), cntQ);
		assertIt(asList(asList(group2,  2  ), asList(group3,  3  )), cdsQ);
		assertIt(asList(asList(group2, 21  ), asList(group3, 31  )), minQ);
		assertIt(asList(asList(group2, 22  ), asList(group3, 33  )), maxQ);
		assertIt(asList(asList(group2, 43  ), asList(group3, 96  )), sumQ);
		assertIt(asList(asList(group2, 21.5), asList(group3, 32.0)), avgQ);
	}


	private static Query<List<Object>> newQuery(final Selectable<?> aggregate)
	{
		final Query<List<Object>> result =
				Query.newQuery(new Selectable<?>[]{group, aggregate}, MyItem.TYPE, null);
		result.setGroupBy(group);
		result.setOrderBy(group, true);
		return result;
	}

	private static void assertIt(
			final List<List<Object>> expected,
			final Query<List<Object>> actual)
	{
		assertEquals(expected, actual.search());
		assertEquals(expected.size(), actual.total());
		assertEquals(!expected.isEmpty(), actual.exists());
	}

	@WrapperType(indent=2, comments=false)
	static final class MyItem extends Item
	{
		static final StringField group = new StringField().toFinal();
		static final IntegerField aggregated = new IntegerField().toFinal().optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		MyItem(
					@javax.annotation.Nonnull final java.lang.String group,
					@javax.annotation.Nullable final java.lang.Integer aggregated)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.group,group),
				com.exedio.cope.SetValue.map(MyItem.aggregated,aggregated),
			});
		}

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getGroup()
		{
			return MyItem.group.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.Integer getAggregated()
		{
			return MyItem.aggregated.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = new Model(MyItem.TYPE);
}
