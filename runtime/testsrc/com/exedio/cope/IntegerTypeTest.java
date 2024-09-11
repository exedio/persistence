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
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnValueL;
import static com.exedio.cope.SchemaInfo.getPrimaryKeySequenceName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.newConnection;
import static com.exedio.cope.SchemaInfo.quoteName;
import static com.exedio.dsmf.Dialect.NOT_NULL;
import static com.exedio.dsmf.Sequence.Type.bit31;
import static com.exedio.dsmf.Sequence.Type.bit63;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.tojunit.SI;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import com.exedio.dsmf.Table;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.Test;

public class IntegerTypeTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(
			IntegerTypeTinyIntItem.TYPE,
			IntegerTypeSmallIntItem.TYPE,
			IntegerTypeMediumIntItem.TYPE,
			IntegerTypeIntItem.TYPE,
			IntegerTypeBigIntItem.TYPE,
			IntegerTypeSubItem.TYPE
	);

	public IntegerTypeTest()
	{
		super(MODEL);
	}

	@Test void testItemCreation()
	{
		{
			final IntegerTypeBigIntItem item = new IntegerTypeBigIntItem();
			item.setItemReference(item);
			assertEquals(item, item.getItemReference());
			item.setValue(9223372036854775807L);
			assertEquals(9223372036854775807L, item.getValue().longValue());
			item.setValue(-9223372036854775808L);
			assertEquals(-9223372036854775808L, item.getValue().longValue());

			checkDataType(
					IntegerTypeBigIntItem.TYPE,
					IntegerTypeBigIntItem.itemReference,
					IntegerTypeBigIntItem.value,
					"BIGINT", "bigint", "bigint");
		}
		{
			final IntegerTypeSubItem item = new IntegerTypeSubItem();
			item.setItemReference(item);
			item.setItemReference2(item);
			assertEquals(item, item.getItemReference());
			assertEquals(item, item.getItemReference2());

			item.setValue2(9223372036854775807L);
			assertEquals(9223372036854775807L, item.getValue2().longValue());
			item.setValue2(-9223372036854775808L);
			assertEquals(-9223372036854775808L, item.getValue2().longValue());

			checkDataType(
					IntegerTypeSubItem.TYPE,
					IntegerTypeSubItem.itemReference2,
					IntegerTypeSubItem.value2,
					"BIGINT", "bigint", "bigint");
		}
		{
			final IntegerTypeIntItem item = new IntegerTypeIntItem();
			item.setItemReference(item);
			assertEquals(item, item.getItemReference());

			item.setValue(2147483647L);
			assertEquals(2147483647L, item.getValue().longValue());
			item.setValue(-2147483648L);
			assertEquals(-2147483648L, item.getValue().longValue());

			checkDataType(
					IntegerTypeIntItem.TYPE,
					IntegerTypeIntItem.itemReference,
					IntegerTypeIntItem.value,
					"INTEGER", "int", "integer");
		}
		{
			final IntegerTypeMediumIntItem item = new IntegerTypeMediumIntItem();
			item.setItemReference(item);
			assertEquals(item, item.getItemReference());

			item.setValue(8388607L);
			assertEquals(8388607L, item.getValue().longValue());
			item.setValue(-8388608L);
			assertEquals(-8388608L, item.getValue().longValue());

			checkDataType(
					IntegerTypeMediumIntItem.TYPE,
					IntegerTypeMediumIntItem.itemReference,
					IntegerTypeMediumIntItem.value,
					"INTEGER", "mediumint", "integer");
		}
		{
			final IntegerTypeSmallIntItem item = new IntegerTypeSmallIntItem();
			item.setItemReference(item);
			assertEquals(item, item.getItemReference());

			item.setValue(32767L);
			assertEquals(32767L, item.getValue().longValue());
			item.setValue(-32768L);
			assertEquals(-32768L, item.getValue().longValue());

			checkDataType(
					IntegerTypeSmallIntItem.TYPE,
					IntegerTypeSmallIntItem.itemReference,
					IntegerTypeSmallIntItem.value,
					"SMALLINT", "smallint", "smallint");
		}
		{
			final IntegerTypeTinyIntItem item = new IntegerTypeTinyIntItem();
			item.setItemReference(item);
			assertEquals(item, item.getItemReference());

			item.setValue(127L);
			assertEquals(127L, item.getValue().longValue());
			item.setValue(-128L);
			assertEquals(-128L, item.getValue().longValue());

			checkDataType(
					IntegerTypeTinyIntItem.TYPE,
					IntegerTypeTinyIntItem.itemReference,
					IntegerTypeTinyIntItem.value,
					"TINYINT", "tinyint", "smallint");
		}
	}

	@SuppressWarnings("AssignmentReplaceableWithOperatorAssignment")
	private void setPkTo(final Type<?> type, final long lastUsedPkValue) throws SQLException
	{
		long itemsToCreate = 0L;
		if (!model.getConnectProperties().primaryKeyGenerator.persistent)
		{
			type.newItem();
			model.commit();

			final String update = "UPDATE " + SI.tab(type) + " SET " + SI.pk(type) + " = ? ";
			try (
					Connection connection = newConnection(model);
					PreparedStatement statement = connection.prepareStatement(update))
			{
				statement.setLong(1, lastUsedPkValue);
				assertEquals(1, statement.executeUpdate());
			}
		}
		else
		{
			model.commit();

			final String sequenceName = getPrimaryKeySequenceName(type);

			final StringBuilder bf = new StringBuilder();
			long sequenceValue = lastUsedPkValue+1L;
			if (PrimaryKeyGenerator.batchedSequence==model.getConnectProperties().primaryKeyGenerator)
			{
				itemsToCreate = sequenceValue % SequenceImplBatchedSequence.BATCH_SIZE;
				sequenceValue = sequenceValue / SequenceImplBatchedSequence.BATCH_SIZE;
			}

			MODEL.connect().database.dialect.deleteSequence(bf,
					quoteName(MODEL, sequenceName),
					sequenceValue);

			try (
					Connection connection = newConnection(model);
					Statement statement = connection.createStatement())
			{
				statement.execute(bf.toString());
			}
		}
		model.disconnect();
		model.connect(copeRule.getConnectProperties());

		model.startTransaction("tx:" + getClass().getName());

		for (int i = 0; i < itemsToCreate; i++)
			type.newItem();
	}

	@Test void testIntegerLongTransition() throws NoSuchIDException, SQLException
	{
		setPkTo(IntegerTypeBigIntItem.TYPE, Integer.MAX_VALUE-1);

		final IntegerTypeBigIntItem item = new IntegerTypeBigIntItem();
		item.setItemReference(item);
		assertEquals(item, item.getItemReference());
		assertEquals("IntegerTypeBigIntItem-2147483647", item.getCopeID());
		assertEquals(item, model.getItem("IntegerTypeBigIntItem-2147483647"));
		assertEquals(Integer.MAX_VALUE, getPrimaryKeyColumnValueL(item));

		final IntegerTypeBigIntItem item2 = new IntegerTypeBigIntItem();
		item2.setItemReference(item2);
		assertEquals(item2, item2.getItemReference());
		assertEquals("IntegerTypeBigIntItem-2147483648", item2.getCopeID());
		assertEquals(item2, model.getItem("IntegerTypeBigIntItem-2147483648"));
		assertEquals(Integer.MAX_VALUE + 1l, getPrimaryKeyColumnValueL(item2));
	}

	@Test void testBigIntPk() throws NoSuchIDException, SQLException
	{
		setPkTo(IntegerTypeBigIntItem.TYPE, 9223372036854770000L);

		final IntegerTypeBigIntItem item = new IntegerTypeBigIntItem();
		item.setItemReference(item);
		assertEquals(item, item.getItemReference());
		assertEquals("IntegerTypeBigIntItem-9223372036854770001", item.getCopeID());
		assertEquals(item, model.getItem("IntegerTypeBigIntItem-9223372036854770001"));
		assertEquals(9223372036854770001l, getPrimaryKeyColumnValueL(item));
	}

	@Test void testIntPk() throws SQLException
	{
		setPkTo(IntegerTypeIntItem.TYPE, 2147480000);

		final IntegerTypeIntItem item = new IntegerTypeIntItem();
		item.setItemReference(item);
		assertEquals(item, item.getItemReference());
		assertEquals("IntegerTypeIntItem-2147480001", item.getCopeID());
	}

	@Test void testMediumIntPk() throws SQLException
	{
		setPkTo(IntegerTypeMediumIntItem.TYPE, 8380000);

		final IntegerTypeMediumIntItem item = new IntegerTypeMediumIntItem();
		item.setItemReference(item);
		assertEquals(item, item.getItemReference());
		assertEquals("IntegerTypeMediumIntItem-8380001", item.getCopeID());
	}

	@Test void testSmallIntPk() throws SQLException
	{
		setPkTo(IntegerTypeSmallIntItem.TYPE, 32000);

		final IntegerTypeSmallIntItem item = new IntegerTypeSmallIntItem();
		item.setItemReference(item);
		assertEquals(item, item.getItemReference());
		assertEquals("IntegerTypeSmallIntItem-32001", item.getCopeID());
	}

	@Test void testTinyIntPk() throws SQLException
	{
		setPkTo(IntegerTypeTinyIntItem.TYPE, 20);

		final IntegerTypeTinyIntItem item = new IntegerTypeTinyIntItem();
		item.setItemReference(item);
		assertEquals(item, item.getItemReference());
		assertEquals("IntegerTypeTinyIntItem-21", item.getCopeID());
	}


	protected void checkDataType(
			final Type<?> type, final ItemField<?> itemField, final LongField longField,
			final String expectedHsqldbDataType,
			final String expectedMysqlDataType,
			final String expectedPostgresqlDataType)
	{
		final String tableName = getTableName(type);
		final Schema schema = model.getSchema();
		final Table table = schema.getTable(tableName);

		for (final String columnName : new String[] {
				getPrimaryKeyColumnName(type),
				getColumnName(itemField),
				getColumnName(longField)})
		{
			final String expectedDataType =
			switch(dialect)
			{
				case hsqldb ->
					expectedHsqldbDataType;

				case mysql ->
					expectedMysqlDataType;

				case postgresql ->
					expectedPostgresqlDataType;



			};

			assertEquals(
					expectedDataType + (columnName.equals(getPrimaryKeyColumnName(type))?NOT_NULL:""),
					table.getColumn(columnName).getRequiredType(),
					"tableName: " + tableName + " columnName: " + columnName);
		}
	}

	@Test void testSchema()
	{
		assertSchema();

		if(!model.getConnectProperties().primaryKeyGenerator.persistent)
			return;

		final Schema schema = model.getSchema();
		assertSequence(schema, IntegerTypeTinyIntItem  .TYPE, bit31);
		assertSequence(schema, IntegerTypeSmallIntItem .TYPE, bit31);
		assertSequence(schema, IntegerTypeMediumIntItem.TYPE, bit31);
		assertSequence(schema, IntegerTypeIntItem      .TYPE, bit31);
		assertSequence(schema, IntegerTypeBigIntItem   .TYPE, bit63);
		assertSequence(schema, IntegerTypeSubItem      .TYPE, bit63);
	}

	private static void assertSequence(
			final Schema schema,
			final Type<?> expected,
			final Sequence.Type actual)
	{
		final Sequence sequence = schema.getSequence(getPrimaryKeySequenceName(expected));
		assertEquals(actual, sequence.getType());
		assertEquals(0, sequence.getStartL());
	}
}
