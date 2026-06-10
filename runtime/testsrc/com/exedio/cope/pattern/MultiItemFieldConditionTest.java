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

import static com.exedio.cope.RuntimeAssert.assertCondition;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.WrapImplementsInterim;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MultiItemFieldConditionTest extends TestWithEnvironment
{
	public MultiItemFieldConditionTest()
	{
		super(model);
	}

	private ValueA vA1, vA2;
	private ValueB vB1;
	private ValueX vXX;
	private AnItem iA1a, iA1b, iA2a, iB1a, iNul;

	@BeforeEach final void setUp()
	{
		vA1 = new ValueA();
		vA2 = new ValueA();
		vB1 = new ValueB();
		vXX = new ValueX();
		iA1a = new AnItem(vA1);
		iA1b = new AnItem(vA1);
		iA2a = new AnItem(vA2);
		iB1a = new AnItem(vB1);
		iNul = new AnItem((MyInterface)null);
	}

	@Test void test()
	{
		final var f = AnItem.field;
		assertIt(of(iA1a, iA1b), of(            iA2a            ), f.is(vA1)); // TODO COND iB1a is missing in expectedNot because of bug: just selecting one component does not work for not
		assertIt(of(iA2a),       of(iA1a, iA1b                  ), f.is(vA2)); // TODO COND iB1a is missing in expectedNot because of bug: just selecting one component does not work for not
		assertIt(of(iB1a),       of(                            ), f.is(vB1)); // TODO COND iA1? is missing in expectedNot because of bug: just selecting one component does not work for not
		assertIt(of(    ),       of(iA1a, iA1b, iA2a, iB1a, iNul), f.is(vXX)); // TODO COND iNul is wrong because of bug: ofFalse without canBe does not work for not
		assertIt(of(iNul),       of(iA1a, iA1b, iA2a, iB1a      ), f.is(null));
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

	@WrapImplementsInterim
	@SuppressWarnings("MarkerInterface") // OK: empty interface is enough for test
	private interface MyInterface {}

	@WrapperType(indent=2, comments=false)
	private static final class ValueA extends Item implements MyInterface
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private ValueA()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		private ValueA(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ValueA> TYPE = com.exedio.cope.TypesBound.newType(ValueA.class,ValueA::new);

		@com.exedio.cope.instrument.Generated
		private ValueA(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static final class ValueB extends Item implements MyInterface
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private ValueB()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		private ValueB(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ValueB> TYPE = com.exedio.cope.TypesBound.newType(ValueB.class,ValueB::new);

		@com.exedio.cope.instrument.Generated
		private ValueB(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static final class ValueX extends Item implements MyInterface
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private ValueX()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		private ValueX(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ValueX> TYPE = com.exedio.cope.TypesBound.newType(ValueX.class,ValueX::new);

		@com.exedio.cope.instrument.Generated
		private ValueX(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@WrapperInitial
		static final MultiItemField<MyInterface> field = MultiItemField.create(MyInterface.class).optional().
				canBe(ValueA.class).canBe(ValueB.class);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnItem(
					@javax.annotation.Nullable final MyInterface field)
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
		MyInterface getField()
		{
			return AnItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nullable final MyInterface field)
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

	private static final Model model = new Model(AnItem.TYPE, ValueA.TYPE, ValueB.TYPE, ValueX.TYPE);
}
