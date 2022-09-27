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

import static com.exedio.cope.SequenceCheckIntegerTest.AnItem.TYPE;
import static com.exedio.cope.SequenceCheckIntegerTest.AnItem.next;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class SequenceCheckIntegerTest extends TestWithEnvironment
{
	/**
	 * Do not use this model in any other test.
	 * Otherwise problems may be hidden, because
	 * model has been connected before.
	 */
	private static final Model MODEL = new Model(TYPE);

	public SequenceCheckIntegerTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	@Test void testWrongFromStart()
	{
		assertIt(0, null, !postgresql?0:1); // known problem in PostgreSQL, see PostgresqlDialect#getNextSequence

		newManual(5, "first");
		assertIt(!postgresql?6:5, 5, !postgresql?0:1); // known problem in PostgreSQL, see PostgresqlDialect#getNextSequence

		newSequence(0, "second");
		assertIt(5, 5, 1);

		newSequence(1, "third");
		assertIt(4, 5, 2);

		newSequence(2, "fourth");
		assertIt(3, 5, 3);
	}

	@Test void testWrongFromStartWithoutCheck()
	{
		newManual(5, "first");
		assertIt(!postgresql?6:5, 5, !postgresql?0:1); // known problem in PostgreSQL, see PostgresqlDialect#getNextSequence

		newSequence(0, "second");
		assertIt(5, 5, 1);

		newSequence(1, "third");
		assertIt(4, 5, 2);

		newSequence(2, "fourth");
		assertIt(3, 5, 3);
	}

	@Test void testWrongLater()
	{
		assertIt(0, null, !postgresql?0:1); // known problem in PostgreSQL, see PostgresqlDialect#getNextSequence

		newSequence(0, "ok0");
		assertIt(0, 0, 1);

		newSequence(1, "ok1");
		assertIt(0, 1, 2);

		newManual(5, "first");
		assertIt(4, 5, 2);

		newSequence(2, "second");
		assertIt(3, 5, 3);

		newSequence(3, "third");
		assertIt(2, 5, 4);

		newSequence(4, "fourth");
		assertIt(1, 5, 5);
	}

	private static void assertIt(
			final int behindBy,
			final Integer featureMaximum,
			final int sequenceNext)
	{
		final SequenceBehindInfo actual = next.checkSequenceBehindDefaultToNextX();
		assertEquals(
				"sequence behind maximum of AnItem.next: " + featureMaximum + ">=" + sequenceNext,
				actual.toString());
		assertSame  (next, actual.feature, "feature");
		assertEquals(featureMaximum!=null ? featureMaximum.longValue() : null, actual.featureMaximum, "featureMaximum");
		assertEquals(sequenceNext, actual.sequenceNext, "sequenceNext");
		assertEquals(behindBy, actual.isBehindByL(), "behindBy");

		@SuppressWarnings("deprecation")
		final int behindByDeprecated = next.checkDefaultToNext();
		assertEquals(behindBy, behindByDeprecated, "behindByDeprecated");
	}

	private static void newManual(
			final int next,
			final String field)
	{
		try(TransactionTry tx = MODEL.startTransactionTry(SequenceCheckIntegerTest.class.getName()))
		{
			assertEquals(next,
				tx.commit(
					new AnItem(field, next).getNext().intValue()), "next"
			);
		}
	}

	private static void newSequence(
			final int next,
			final String field)
	{
		try(TransactionTry tx = MODEL.startTransactionTry(SequenceCheckIntegerTest.class.getName()))
		{
			assertEquals(next,
				tx.commit(
					new AnItem(field).getNext().intValue()), "next"
			);
		}
	}

	static final class AnItem extends Item
	{
		static final IntegerField next = new IntegerField().toFinal().optional().defaultToNext(0);
		static final StringField field = new StringField().toFinal().optional();

		AnItem(
				final String field,
				final int next)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				AnItem.next.map(next),
				AnItem.field.map(field),
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
	 * Returns the value of {@link #next}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getNext()
	{
		return AnItem.next.get(this);
	}

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

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
}
