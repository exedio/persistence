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

import static com.exedio.cope.RuntimeAssert.assertCondition;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @see DoubleFieldRangeTest
 * @see LongFieldRangeTest
 */
public class IntegerFieldRangeTest extends TestWithEnvironment
{
	public IntegerFieldRangeTest()
	{
		super(model);
	}

	private AnItem iMin, iNrm, iMax, iNul;

	@BeforeEach final void setUp()
	{
		iMin = new AnItem(20);
		iNrm = new AnItem(25);
		iMax = new AnItem(30);
		iNul = new AnItem((Integer)null);
	}

	@Test void test()
	{
		final var f = AnItem.field;

		assertIt(of(    ), of(iMin, iNrm, iMax, iNul), f.is(19)); // TODO COND iNul is wrong because of bug: ofFalse.not() with null
		assertIt(of(iMin), of(      iNrm, iMax      ), f.is(20));
		assertIt(of(iNrm), of(iMin,       iMax      ), f.is(25));
		assertIt(of(    ), of(iMin, iNrm, iMax      ), f.is(26));
		assertIt(of(iMax), of(iMin, iNrm            ), f.is(30));
		assertIt(of(    ), of(iMin, iNrm, iMax, iNul), f.is(31)); // TODO COND iNul is wrong because of bug: ofFalse.not() with null
		assertIt(of(iNul), of(iMin, iNrm, iMax      ), f.is((Integer)null));

		assertIt(of(iMin, iNrm, iMax, iNul), of(    ), f.isNot(19)); // TODO COND iNul is wrong because of bug: ofTrue.not() with null
		assertIt(of(      iNrm, iMax      ), of(iMin), f.isNot(20));
		assertIt(of(iMin,       iMax      ), of(iNrm), f.isNot(25));
		assertIt(of(iMin, iNrm, iMax      ), of(    ), f.isNot(26));
		assertIt(of(iMin, iNrm            ), of(iMax), f.isNot(30));
		assertIt(of(iMin, iNrm, iMax, iNul), of(    ), f.isNot(31)); // TODO COND iNul is wrong because of bug: ofTrue.not() with null
		assertIt(of(iMin, iNrm, iMax      ), of(iNul), f.isNot((Integer)null));
	}

	private static void assertIt(
			final List<AnItem> expected,
			final List<AnItem> expectedNot,
			final Condition condition)
	{
		final Condition conditionNot = condition.not();
		assertCondition(expected,    AnItem.TYPE, condition);
		assertCondition(expectedNot, AnItem.TYPE, conditionNot);
		assertEquals(condition, conditionNot.not());
	}

	@WrapperType(indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@WrapperInitial
		static final IntegerField field = new IntegerField().optional().range(20, 30);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnItem(
					@javax.annotation.Nullable final java.lang.Integer field)
				throws
					com.exedio.cope.IntegerRangeViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(AnItem.field,field),
			});
		}

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.Integer getField()
		{
			return AnItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nullable final java.lang.Integer field)
				throws
					com.exedio.cope.IntegerRangeViolationException
		{
			AnItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model model = new Model(AnItem.TYPE);
}
