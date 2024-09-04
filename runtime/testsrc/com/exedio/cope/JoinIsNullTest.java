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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.instrument.Wrapper.ALL_WRAPS;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests, whether some weird behaviour encountered by Dirk B. happens
 * on databases tested by cope. This test cannot reproduce any
 * weird behaviour.
 */
public class JoinIsNullTest extends TestWithEnvironment
{
	public JoinIsNullTest()
	{
		super(MODEL);
	}

	@Test void testIt()
	{
		final Source isNull = new Source((Integer)null);
		final Source noTarget = new Source(33);
		final Source hasTarget = new Source(88);
		new Target((Integer)null);
		new Target(88);

		{
			final Query<Source> query = Source.TYPE.newQuery();
			query.joinOuterLeft(Target.TYPE, Source.field.equal(Target.field));
			query.setOrderByThis(true);

			assertEquals(
					"select this from Source " +
					"left join Target t1 on field=Target.field " +
					"order by this",
					query.toString());
			assertEquals(List.of(isNull, noTarget, hasTarget), query.search());

			query.setCondition(Target.TYPE.getThis().isNull());
			assertEquals(
					"select this from Source " +
					"left join Target t1 on field=Target.field " +
					"where Target.this is null " +
					"order by this",
					query.toString());
			assertEquals(List.of(isNull, noTarget), query.search());

			query.setCondition(Target.TYPE.getThis().isNull().or(Source.field.isNull()));
			assertEquals(
					"select this from Source " +
					"left join Target t1 on field=Target.field " +
					"where (Target.this is null or field is null) " +
					"order by this",
					query.toString());
			assertEquals(List.of(isNull, noTarget), query.search());
		}
		{
			final Query<List<Object>> query = Query.newQuery(
					new Selectable<?>[]{Source.field, Source.TYPE.getThis()},
					Source.TYPE, null);
			query.joinOuterLeft(Target.TYPE, Source.field.equal(Target.field));
			query.setOrderByThis(true);

			assertEquals(
					"select field,this from Source " +
					"left join Target t1 on field=Target.field " +
					"order by this",
					query.toString());
			assertEquals(List.of(asList(null, isNull), List.of(33, noTarget), List.of(88, hasTarget)), query.search());

			query.setCondition(Target.TYPE.getThis().isNull());
			assertEquals(
					"select field,this from Source " +
					"left join Target t1 on field=Target.field " +
					"where Target.this is null " +
					"order by this",
					query.toString());
			assertEquals(List.of(asList(null, isNull), List.of(33, noTarget)), query.search());

			query.setCondition(Target.TYPE.getThis().isNull().or(Source.field.isNull()));
			assertEquals(
					"select field,this from Source " +
					"left join Target t1 on field=Target.field " +
					"where (Target.this is null or field is null) " +
					"order by this",
					query.toString());
			assertEquals(List.of(asList(null, isNull), List.of(33, noTarget)), query.search());
		}
	}

	@WrapperType(indent=2, comments=false)
	private static final class Source extends Item
	{
		@WrapperInitial
		@Wrapper(wrap=ALL_WRAPS, visibility=NONE)
		static final IntegerField field = new IntegerField().toFinal().optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Source(
					@javax.annotation.Nullable final java.lang.Integer field)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(Source.field,field),
			});
		}

		@com.exedio.cope.instrument.Generated
		private Source(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Source> TYPE = com.exedio.cope.TypesBound.newType(Source.class,Source::new);

		@com.exedio.cope.instrument.Generated
		private Source(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static final class Target extends Item
	{
		@WrapperInitial
		@Wrapper(wrap=ALL_WRAPS, visibility=NONE)
		static final IntegerField field = new IntegerField().toFinal().optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Target(
					@javax.annotation.Nullable final java.lang.Integer field)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(Target.field,field),
			});
		}

		@com.exedio.cope.instrument.Generated
		private Target(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Target> TYPE = com.exedio.cope.TypesBound.newType(Target.class,Target::new);

		@com.exedio.cope.instrument.Generated
		private Target(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(Source.TYPE, Target.TYPE);
}
