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

import org.junit.Test;

public class SequenceInfoTest extends TestWithEnvironment
{
	/**
	 * Do not use this model in any other test.
	 * Otherwise problems may be hidden, because
	 * model has been connected before.
	 */
	private static final Model MODEL = new Model(TYPE);

	public SequenceInfoTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	@Test public void testIt()
	{
		assertInfo(model.getSequenceInfo(), TYPE.getThis(), next);

		assertInfo(TYPE, TYPE.getPrimaryKeyInfo());
		assertInfo(next, next.getDefaultToNextInfo());

		newItem("first", 5);
		assertInfo(TYPE, 1, 0, 0, TYPE.getPrimaryKeyInfo());
		assertInfo(next, next.getDefaultToNextInfo());

		newItem("second");
		assertInfo(TYPE, 2, 0, 1, TYPE.getPrimaryKeyInfo());
		assertInfo(next, 1, 0, 0, next.getDefaultToNextInfo());

		newItem("third");
		assertInfo(TYPE, 3, 0, 2, TYPE.getPrimaryKeyInfo());
		assertInfo(next, 2, 0, 1, next.getDefaultToNextInfo());
	}

	private static AnItem newItem(
			final String field,
			final int next)
	{
		try(TransactionTry tx = MODEL.startTransactionTry(SequenceInfoTest.class.getName()))
		{
			return tx.commit(
					new AnItem(field, next)
			);
		}
	}

	private static AnItem newItem(
			final String field)
	{
		try(TransactionTry tx = MODEL.startTransactionTry(SequenceInfoTest.class.getName()))
		{
			return tx.commit(
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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.String getField()
	{
		return AnItem.field.get(this);
	}

	/**
	 * Returns the value of {@link #next}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.Integer getNext()
	{
		return AnItem.next.get(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
}
