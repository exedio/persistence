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

import static com.exedio.cope.SequenceInfoAssert.assertInfo;
import static com.exedio.cope.SequenceInfoTest.AnItem.TYPE;
import static com.exedio.cope.SequenceInfoTest.AnItem.next;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SequenceInfoTest extends TestWithEnvironment
{
	/**
	 * Do not use this model in any other test.
	 * Otherwise problems may be hidden, because
	 * model has been connected before.
	 */
	private static final Model MODEL = Model.builder().
			name(SequenceInfoTest.class).
			add(TYPE).
			build();

	public SequenceInfoTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	@Test void testIt()
	{
		assertInfo(model.getSequenceInfo(), TYPE.getThis(), next);

		assertInfo(TYPE, TYPE.getPrimaryKeyInfo());
		assertInfo(next, next.getDefaultToNextInfoX());

		newItem("first", 5);
		assertInfo(TYPE, 1, 0, 0, TYPE.getPrimaryKeyInfo());
		assertInfo(next, next.getDefaultToNextInfoX());

		newItem("second");
		assertInfo(TYPE, 2, 0, 1, TYPE.getPrimaryKeyInfo());
		assertInfo(next, 1, 0, 0, next.getDefaultToNextInfoX());

		newItem("third");
		assertInfo(TYPE, 3, 0, 2, TYPE.getPrimaryKeyInfo());
		assertInfo(next, 2, 0, 1, next.getDefaultToNextInfoX());
	}

	private static void newItem(
			final String field,
			final int next)
	{
		try(TransactionTry tx = MODEL.startTransactionTry(SequenceInfoTest.class.getName()))
		{
			assertEquals(next,
				tx.commit(
					new AnItem(field, next).getNext().intValue()), "next"
			);
		}
	}

	private static void newItem(
			final String field)
	{
		try(TransactionTry tx = MODEL.startTransactionTry(SequenceInfoTest.class.getName()))
		{
			tx.commit(
					new AnItem(field)
			);
		}
	}

	static final class AnItem extends Item
	{
		static final StringField field = new StringField().toFinal().optional();
		static final IntegerField next = new IntegerField().toFinal().optional().defaultToNext(0);

		AnItem(
				final String field,
				final int next)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				AnItem.field.map(field),
				AnItem.next.map(next),
			});
		}

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 * @throws com.exedio.cope.StringLengthViolationException if field violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	AnItem(
				@javax.annotation.Nullable final java.lang.String field)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.field.map(field),
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #field}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getField()
	{
		return AnItem.field.get(this);
	}

	/**
	 * Returns the value of {@link #next}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getNext()
	{
		return AnItem.next.get(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
}
