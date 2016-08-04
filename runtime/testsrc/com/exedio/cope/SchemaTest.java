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

import static com.exedio.cope.RuntimeTester.assertNotExistsConstraint;
import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.supportsNativeDate;
import static com.exedio.cope.SchemaItem.TYPE;
import static com.exedio.cope.SchemaItem.anEnum;
import static com.exedio.cope.SchemaItem.bool;
import static com.exedio.cope.SchemaItem.boolOpt;
import static com.exedio.cope.SchemaItem.data;
import static com.exedio.cope.SchemaItem.date;
import static com.exedio.cope.SchemaItem.dateOpt;
import static com.exedio.cope.SchemaItem.doub;
import static com.exedio.cope.SchemaItem.doubOpt;
import static com.exedio.cope.SchemaItem.enumOpt;
import static com.exedio.cope.SchemaItem.integ;
import static com.exedio.cope.SchemaItem.integOpt;
import static com.exedio.cope.SchemaItem.item;
import static com.exedio.cope.SchemaItem.itemOpt;
import static com.exedio.cope.SchemaItem.poly;
import static com.exedio.cope.SchemaItem.polyOpt;
import static com.exedio.cope.SchemaItem.string;
import static com.exedio.cope.SchemaItem.stringEmpty;
import static com.exedio.cope.SchemaItem.stringExact6;
import static com.exedio.cope.SchemaItem.stringLong;
import static com.exedio.cope.SchemaItem.stringMax4;
import static com.exedio.cope.SchemaItem.stringMin4;
import static com.exedio.cope.SchemaItem.stringMin4Max8;
import static com.exedio.cope.SchemaItem.stringOpt;
import static com.exedio.cope.SchemaItem.stringUpper6;
import static com.exedio.cope.SchemaItem.uniqueString;
import static com.exedio.dsmf.Node.Color.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.exedio.cope.tojunit.SchemaName;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import com.exedio.dsmf.Table;
import org.junit.Test;

public class SchemaTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE, SchemaTargetItem.TYPE, SchemaTargetPolymorphicItem.TYPE);

	public SchemaTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	@Test public void testSchema()
	{
		final boolean nativeDate = supportsNativeDate(model);
		final Schema schema = model.getVerifiedSchema();

		final Table table = schema.getTable(getTableName(TYPE));
		assertNotNull(table);
		assertEquals(null, table.getError());
		assertEquals(OK, table.getParticularColor());

		assertCheckConstraint(table, "Main_string_MN", l(string)+">=1");
		assertCheckConstraint(table, "Main_string_MX", l(string)+"<="+StringField.DEFAULT_MAXIMUM_LENGTH);
		assertCheckConstraint(table, "Main_integ_MN", q(integ)+">=-10");
		assertCheckConstraint(table, "Main_integ_MX", q(integ)+"<=10");
		assertCheckConstraint(table, "Main_doub_MN", q(doub)+">=-11.1");
		assertCheckConstraint(table, "Main_doub_MX", q(doub)+"<=11.1");
		assertCheckConstraint(table, "Main_date_MN", q(date)+">="+Long.MIN_VALUE, !nativeDate);
		assertCheckConstraint(table, "Main_date_MX", q(date)+"<="+Long.MAX_VALUE, !nativeDate);
		assertCheckConstraint(table, "Main_day_MN", null, false);
		assertCheckConstraint(table, "Main_day_MX", null, false);
		assertCheckConstraint(table, "Main_bool_EN", hp(q(bool  ))+" IN ("+hp("0")+","+hp("1")+")");
		assertCheckConstraint(table, "Main_anEnum_EN", hp(q(anEnum))+" IN ("+hp("10")+","+hp("20")+","+hp("30")+")");
		assertCheckConstraint(table, "Main_item_MN", q(item)+">=0");
		assertCheckConstraint(table, "Main_item_MX", q(item)+"<=567");
		assertCheckConstraint(table, "Main_poly_MN", q(poly)+">=0");
		assertCheckConstraint(table, "Main_poly_MX", q(poly)+"<=567");
		assertCheckConstraint(table, "Main_polyType_EN", hp(t(poly))+" IN ("+hp("'Polymorphic'")+","+hp("'Target'")+")");
		assertCheckConstraint(table, "Main_polyType_NS", null, false);

		assertCheckConstraint(table, "Main_stringOpt_MN", l(stringOpt)+">=1");
		assertCheckConstraint(table, "Main_stringOpt_MX", l(stringOpt)+"<="+StringField.DEFAULT_MAXIMUM_LENGTH);
		assertCheckConstraint(table, "Main_integOpt_MN", q(integOpt)+">=-10");
		assertCheckConstraint(table, "Main_integOpt_MX", q(integOpt)+"<=10");
		assertCheckConstraint(table, "Main_doubOpt_MN", q(doubOpt)+">=-11.1");
		assertCheckConstraint(table, "Main_doubOpt_MX", q(doubOpt)+"<=11.1");
		assertCheckConstraint(table, "Main_dateOpt_MN", q(dateOpt)+">="+Long.MIN_VALUE, !nativeDate);
		assertCheckConstraint(table, "Main_dateOpt_MX", q(dateOpt)+"<="+Long.MAX_VALUE, !nativeDate);
		assertCheckConstraint(table, "Main_dayOpt_MN", null, false);
		assertCheckConstraint(table, "Main_dayOpt_MX", null, false);
		assertCheckConstraint(table, "Main_boolOpt_EN", hp(q(boolOpt))+" IN ("+hp("0")+","+hp("1")+")");
		assertCheckConstraint(table, "Main_enumOpt_EN", hp(q(enumOpt))+" IN ("+hp("10")+","+hp("20")+","+hp("30")+")");
		assertCheckConstraint(table, "Main_itemOpt_MN", q(itemOpt)+">=0");
		assertCheckConstraint(table, "Main_itemOpt_MX", q(itemOpt)+"<=567");
		assertCheckConstraint(table, "Main_polyOpt_MN", q(polyOpt)+">=0");
		assertCheckConstraint(table, "Main_polyOpt_MX", q(polyOpt)+"<=567");
		assertCheckConstraint(table, "Main_polyOptType_EN", hp(t(polyOpt))+" IN ("+hp("'Polymorphic'")+","+hp("'Target'")+")");
		assertCheckConstraint(table, "Main_polyOptType_NS",
				"(("+t(polyOpt)+" IS NOT NULL) AND ("+q(polyOpt)+" IS NOT NULL))" +
				" OR (("+t(polyOpt)+" IS NULL) AND ("+q(polyOpt)+" IS NULL))");

		assertPkConstraint(table, "Main_Pk", null, getPrimaryKeyColumnName(TYPE));

		assertFkConstraint(table, "Main_item_Fk", getColumnName(item), getTableName(SchemaTargetItem.TYPE), getPrimaryKeyColumnName(SchemaTargetItem.TYPE));

		assertUniqueConstraint(table, "Main_uniqueString_Unq", "("+q(uniqueString)+")");

		assertUniqueConstraint(table, "Main_doubleUnique_Unq", "("+q(string)+","+q(anEnum)+")");

		final Column min4Max8Column = table.getColumn(getColumnName(stringMin4Max8));
		assertEquals(null, min4Max8Column.getError());
		assertEquals(OK, min4Max8Column.getParticularColor());

		final String mb4 = model.getConnectProperties().mysqlUtf8mb4 ? "mb4" : "";
		final String string8;
		switch(dialect)
		{
			case hsqldb:     string8 = "VARCHAR(8)"; break;
			case mysql :     string8 = "varchar(8) CHARACTER SET utf8"+mb4+" COLLATE utf8"+mb4+"_bin"; break;
			case oracle:     string8 = "VARCHAR2(24 BYTE)"; break; // varchar specifies bytes
			case postgresql: string8 = "character varying(8)"; break;
			default:
				throw new AssertionError(dialect.name());
		}
		assertEquals(string8, min4Max8Column.getType());

		final String upperSQL = mysql ? q(stringUpper6)+" REGEXP '^[A-Z]*$'" : "";

		assertCheckConstraint(table, "Main_stringMin4_MN", l(stringMin4)+">=4");
		assertCheckConstraint(table, "Main_stringMin4_MX", l(stringMin4)+"<="+StringField.DEFAULT_MAXIMUM_LENGTH);
		assertCheckConstraint(table, "Main_stringMax4_MN", l(stringMax4)+">=1");
		assertCheckConstraint(table, "Main_stringMax4_MX", l(stringMax4)+"<=4");
		assertCheckConstraint(table, "Main_stringMin4Max8_MN", l(stringMin4Max8)+">=4");
		assertCheckConstraint(table, "Main_stringMin4Max8_MX", l(stringMin4Max8)+"<=8");
		assertCheckConstraint(table, "Main_stringExact6_MN", l(stringExact6)+"=6");
		assertCheckConstraint(table, "Main_strinExact6_MX", null, false);
		assertCheckConstraint(table, "Main_stringUpper6_MN", l(stringUpper6)+"=6");
		assertCheckConstraint(table, "Main_stringUpper6_MX", null, false);
		assertCheckConstraint(table, "Main_stringUpper6_CS", upperSQL, mysql);
		assertCheckConstraint(table, "Main_stringEmpty_MN", null, false);
		assertCheckConstraint(table, "Main_stringEmpty_MX", l(stringEmpty)+"<="+StringField.DEFAULT_MAXIMUM_LENGTH);
		assertCheckConstraint(table, "Main_data_MX", l(data)+"<="+DataField.DEFAULT_LENGTH);

		final Column stringLongColumn = table.getColumn(getColumnName(stringLong));
		assertEquals(null, stringLongColumn.getError());
		assertEquals(OK, stringLongColumn.getParticularColor());

		final Sequence sequence        = schema.getSequence(filterTableName("Main_this_Seq"));
		final Sequence batchedSequence = schema.getSequence(filterTableName("Main_this_Seq6"));
		switch ( model.getConnectProperties().primaryKeyGenerator )
		{
			case memory:
				assertEquals(null, sequence);
				assertEquals(null, batchedSequence);
				break;
			case sequence:
				assertEquals(null, sequence.getError());
				assertEquals(OK, sequence.getParticularColor());
				assertEquals(null, batchedSequence);
				break;
			case batchedSequence:
				assertEquals(null, batchedSequence.getError());
				assertEquals(OK, batchedSequence.getParticularColor());
				assertEquals(null, sequence);
				break;
			default:
				fail();
		}

		assertEquals(OK, table.getCumulativeColor());
	}

	private static String q(final Field<?> f)
	{
		return SchemaName.column(f);
	}

	private static String t(final ItemField<?> f)
	{
		return SchemaName.columnType(f);
	}

	private final String l(final StringField f)
	{
		return model.connect().database.dialect.getStringLength() + '(' + q(f) + ')';
	}

	private final String l(final DataField f)
	{
		return model.connect().database.dialect.getBlobLength() + '(' + q(f) + ')';
	}

	protected final String hp(final String s)
	{
		if(hsqldb)
			return "(" + s + ")";
		else
			return s;
	}

	private final void assertCheckConstraint(
			final Table table,
			final String name,
			final String condition,
			final boolean exists)
	{
		if(exists)
			assertCheckConstraint(table, name, condition);
		else
			assertNotExistsConstraint(table, name);
	}
}
