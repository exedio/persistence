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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnName;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnValue;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.newConnection;
import static com.exedio.cope.SchemaInfo.quoteName;
import static com.exedio.cope.SequenceCheckPrimaryKeyTest.AnItem.TYPE;
import static com.exedio.cope.SequenceCheckPrimaryKeyTest.AnItem.field;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.Before;
import org.junit.Test;

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

	@Before public void setUp()
	{
		final ConnectProperties props = model.getConnectProperties();
		model.disconnect();
		model.connect(props);
		p = MODEL.getConnectProperties().primaryKeyGenerator.persistent;
	}

	@Test public void testWrongFromStart() throws SQLException
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

	@Test public void testWrongFromStartWithoutCheck() throws SQLException
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

	@Test public void testWrongLater() throws SQLException
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
			final int check,
			final Integer featureMaximum,
			final int sequenceNext)
	{
		final SequenceBehindException e = TYPE.checkBehindPrimaryKey();
		assertEquals(
				"sequence behind maximum of AnItem.this: " + featureMaximum + ">=" + sequenceNext,
				e.getMessage());
		assertSame  ("feature", TYPE.getThis(), e.feature);
		assertEquals("featureMaximum", featureMaximum, e.featureMaximum);
		assertEquals("sequenceNext", sequenceNext, e.sequenceNext);

		@SuppressWarnings("deprecation")
		final int error = TYPE.checkPrimaryKey();
		assertEquals("check", check, error);
	}

	private static final void newSequence(
			final int pk,
			final String field)
	{
		try(TransactionTry tx = MODEL.startTransactionTry(SequenceCheckPrimaryKeyTest.class.getName()))
		{
			assertEquals("pk", pk, getPrimaryKeyColumnValue(
				tx.commit(
					new AnItem(field)))
			);
		}
	}

	@SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
	private static final void newManual(
			final int pk,
			final String fieldValue)
	throws SQLException
	{
		try(
				Connection connection = newConnection(MODEL);
				PreparedStatement statement = connection.prepareStatement(
						"INSERT INTO " + q(getTableName(TYPE)) +
						" (" + q(getPrimaryKeyColumnName(TYPE)) +
						","  + q(getColumnName(field)) +
						") VALUES (?,?)"))
		{
			statement.setInt(1, pk);
			statement.setString(2, fieldValue);
			assertEquals(1, statement.executeUpdate());
		}
	}

	private static String q(final String name)
	{
		return quoteName(MODEL, name);
	}

	static final class AnItem extends Item
	{
		static final StringField field = new StringField().toFinal().optional();

		/**

	 **
	 * Creates a new AnItem with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 * @throws com.exedio.cope.StringLengthViolationException if field violates its length constraint.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	AnItem(
				final java.lang.String field)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.field.map(field),
		});
	}/**

	 **
	 * Creates a new AnItem and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private AnItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #field}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.lang.String getField()
	{
		return AnItem.field.get(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for anItem.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}
}
