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

import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnValueL;
import static com.exedio.cope.SchemaInfo.newConnection;
import static com.exedio.cope.SequenceCheckPrimaryKeyTest.AnItem.TYPE;
import static com.exedio.cope.SequenceCheckPrimaryKeyTest.AnItem.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.tojunit.SI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SequenceCheckPrimaryKeyTest extends TestWithEnvironment
{
	/**
	 * Do not use this model in any other test.
	 * Otherwise problems may be hidden, because
	 * model has been connected before.
	 */
	private static final Model MODEL = new Model(TYPE);

	public SequenceCheckPrimaryKeyTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	private boolean p;

	@BeforeEach void setUp()
	{
		final ConnectProperties props = model.getConnectProperties();
		model.disconnect();
		model.connect(props);
		p = MODEL.getConnectProperties().primaryKeyGenerator.persistent;
	}

	@Test void testWrongFromStart() throws SQLException
	{
		assertIt(0, null, (!p||!postgresql)?0:1); // known problem in PostgreSQL, see PostgresqlDialect#getNextSequence

		newManual(5, "first");
		assertIt(p ? !postgresql?6:5 : 0, 5, p ? !postgresql?0:1 : 6); // known problem in PostgreSQL, see PostgresqlDialect#getNextSequence

		newSequence(p?0:6, "second");
		assertIt(p?5:0, p?5:6, p?1:7);

		newSequence(p?1:7, "third");
		assertIt(p?4:0, p?5:7, p?2:8);

		newSequence(p?2:8, "fourth");
		assertIt(p?3:0, p?5:8, p?3:9);
	}

	@Test void testWrongFromStartWithoutCheck() throws SQLException
	{
		newManual(5, "first");
		assertIt(p ? !postgresql?6:5 : 0, 5, p ? !postgresql?0:1 : 6); // known problem in PostgreSQL, see PostgresqlDialect#getNextSequence

		newSequence(p?0:6, "second");
		assertIt(p?5:0, p?5:6, p?1:7);

		newSequence(p?1:7, "third");
		assertIt(p?4:0, p?5:7, p?2:8);

		newSequence(p?2:8, "fourth");
		assertIt(p?3:0, p?5:8, p?3:9);
	}

	@Test void testWrongLater() throws SQLException
	{
		assertIt(0, null, (!p||!postgresql)?0:1); // known problem in PostgreSQL, see PostgresqlDialect#getNextSequence

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
		final SequenceBehindInfo actual = TYPE.checkSequenceBehindPrimaryKey();
		assertEquals(
				"sequence behind maximum of AnItem.this: " + featureMaximum + ">=" + sequenceNext,
				actual.toString());
		assertSame  (TYPE.getThis(), actual.feature, "feature");
		assertEquals(featureMaximum != null ? featureMaximum.longValue() : null, actual.featureMaximum, "featureMaximum");
		assertEquals(sequenceNext, actual.sequenceNext, "sequenceNext");
		assertEquals(behindBy, actual.isBehindByL(), "behindBy");

		@SuppressWarnings("deprecation")
		final int behindByDeprecated = TYPE.checkPrimaryKey();
		assertEquals(behindBy, behindByDeprecated, "behindByDeprecated");
	}

	private static void newSequence(
			final int pk,
			final String field)
	{
		try(TransactionTry tx = MODEL.startTransactionTry(SequenceCheckPrimaryKeyTest.class.getName()))
		{
			assertEquals(pk, getPrimaryKeyColumnValueL(
				tx.commit(
					new AnItem(field))), "pk"
			);
		}
	}

	private static void newManual(
			final int pk,
			final String fieldValue)
	throws SQLException
	{
		try(
				Connection connection = newConnection(MODEL);
				PreparedStatement statement = connection.prepareStatement(
						"INSERT INTO " + SI.tab(TYPE) +
						" (" + SI.pk(TYPE) + "," + SI.col(field) + ") VALUES (?,?)"))
		{
			statement.setInt(1, pk);
			statement.setString(2, fieldValue);
			assertEquals(1, statement.executeUpdate());
		}
	}

	static final class AnItem extends Item
	{
		static final StringField field = new StringField().toFinal().optional();

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
