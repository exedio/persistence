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
import static com.exedio.cope.SchemaItem.aLong;
import static com.exedio.cope.SchemaItem.anEnum;
import static com.exedio.cope.SchemaItem.bool;
import static com.exedio.cope.SchemaItem.boolOpt;
import static com.exedio.cope.SchemaItem.data;
import static com.exedio.cope.SchemaItem.date;
import static com.exedio.cope.SchemaItem.dateOpt;
import static com.exedio.cope.SchemaItem.day;
import static com.exedio.cope.SchemaItem.dayOpt;
import static com.exedio.cope.SchemaItem.doub;
import static com.exedio.cope.SchemaItem.doubOpt;
import static com.exedio.cope.SchemaItem.doubRed;
import static com.exedio.cope.SchemaItem.enumOpt;
import static com.exedio.cope.SchemaItem.enumRed;
import static com.exedio.cope.SchemaItem.integ;
import static com.exedio.cope.SchemaItem.integOpt;
import static com.exedio.cope.SchemaItem.integRed;
import static com.exedio.cope.SchemaItem.item;
import static com.exedio.cope.SchemaItem.itemOpt;
import static com.exedio.cope.SchemaItem.longRed;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.tojunit.SI;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import com.exedio.dsmf.Table;
import org.junit.jupiter.api.Test;

public class SchemaTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE, SchemaTargetItem.TYPE, SchemaTargetPolymorphicItem.TYPE);

	public SchemaTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	@Test void testSchema()
	{
		final boolean nativeDate = supportsNativeDate(model);
		final String dateMinimum;
		final String dateMaximum;
		final String dayMinimum;
		final String dayMaximum;
		//noinspection EnumSwitchStatementWhichMissesCases,SwitchStatementWithTooFewBranches OK: prepares more branches
		switch(dialect)
		{
			case postgresql ->
			{
				dateMinimum = "'1600-01-01 00:00:00" +"'::timestamp without time zone";
				dateMaximum = "'9999-12-31 23:59:59.999'::timestamp without time zone";
				dayMinimum = "'1600-01-01'::\"date\"";
				dayMaximum = "'9999-12-31'::\"date\"";
			}
			default ->
			{
				dateMinimum = "TIMESTAMP'1600-01-01 00:00:00.000'";
				dateMaximum = "TIMESTAMP'9999-12-31 23:59:59.999'";
				dayMinimum = "DATE'1600-01-01'";
				dayMaximum = "DATE'9999-12-31'";
			}
		}
		final boolean dataVault = data.getVaultInfo()!=null;
		final Schema schema = model.getVerifiedSchema();

		final Table table = schema.getTable(getTableName(TYPE));
		assertNotNull(table);
		assertEquals(null, table.getError());
		assertEquals(OK, table.getParticularColor());

		assertCheckConstraint(table, "Main_string_MN", "CHAR_LENGTH("+q(string)+")>=1");
		assertCheckConstraint(table, "Main_string_MX", "CHAR_LENGTH("+q(string)+")<="+StringField.DEFAULT_MAXIMUM_LENGTH);
		assertCheckConstraint(table, "Main_integ_MN", q(integ)+">=-10");
		assertCheckConstraint(table, "Main_integ_MX", q(integ)+"<=10");
		assertCheckConstraint(table, "Main_aLong_MN", q(aLong)+">=-12");
		assertCheckConstraint(table, "Main_aLong_MX", q(aLong)+"<=12");
		assertCheckConstraint(table, "Main_doub_MN", q(doub)+">=-11.1");
		assertCheckConstraint(table, "Main_doub_MX", q(doub)+"<=11.1");
		assertCheckConstraint(table, "Main_date_MN", q(date)+">="+(nativeDate?dateMinimum:DateField.getDefaultMinimum().getTime()));
		assertCheckConstraint(table, "Main_date_MX", q(date)+"<="+(nativeDate?dateMaximum:DateField.getDefaultMaximum().getTime()));
		assertCheckConstraint(table, "Main_day_MN", q(day)+">="+dayMinimum);
		assertCheckConstraint(table, "Main_day_MX", q(day)+"<="+dayMaximum);
		assertCheckConstraint(table, "Main_bool_EN", q(bool)+" IN (0,1)");
		assertCheckConstraint(table, "Main_anEnum_EN", q(anEnum)+" IN (10,20,30)");
		assertCheckConstraint(table, "Main_item_MN", q(item)+">=0");
		assertCheckConstraint(table, "Main_item_MX", q(item)+"<=567");
		assertCheckConstraint(table, "Main_poly_MN", q(poly)+">=0");
		assertCheckConstraint(table, "Main_poly_MX", q(poly)+"<=567");
		assertCheckConstraint(table, "Main_polyType_EN", t(poly)+" IN ('Polymorphic','Target')");
		assertCheckConstraint(table, "Main_polyType_NS", null, false);

		assertCheckConstraint(table, "Main_integRed_RD", q(integRed)+"=110");
		assertCheckConstraint(table, "Main_integRed_MN", null, false);
		assertCheckConstraint(table, "Main_integRed_MX", null, false);
		assertCheckConstraint(table, "Main_longRed_RD", q(longRed)+"=112");
		assertCheckConstraint(table, "Main_longRed_MN", null, false);
		assertCheckConstraint(table, "Main_longRed_MX", null, false);
		assertCheckConstraint(table, "Main_doubRed_RD", q(doubRed)+"=111.1");
		assertCheckConstraint(table, "Main_doubRed_MN", null, false);
		assertCheckConstraint(table, "Main_doubRed_MX", null, false);
		assertCheckConstraint(table, "Main_enumRed_RD", q(enumRed)+"=10");
		assertCheckConstraint(table, "Main_enumRed_EN", null, false);

		assertCheckConstraint(table, "Main_stringOpt_MN", "CHAR_LENGTH("+q(stringOpt)+")>=1");
		assertCheckConstraint(table, "Main_stringOpt_MX", "CHAR_LENGTH("+q(stringOpt)+")<="+StringField.DEFAULT_MAXIMUM_LENGTH);
		assertCheckConstraint(table, "Main_integOpt_MN", q(integOpt)+">=-10");
		assertCheckConstraint(table, "Main_integOpt_MX", q(integOpt)+"<=10");
		assertCheckConstraint(table, "Main_doubOpt_MN", q(doubOpt)+">=-11.1");
		assertCheckConstraint(table, "Main_doubOpt_MX", q(doubOpt)+"<=11.1");
		assertCheckConstraint(table, "Main_dateOpt_MN", q(dateOpt)+">="+(nativeDate?dateMinimum:DateField.getDefaultMinimum().getTime()));
		assertCheckConstraint(table, "Main_dateOpt_MX", q(dateOpt)+"<="+(nativeDate?dateMaximum:DateField.getDefaultMaximum().getTime()));
		assertCheckConstraint(table, "Main_dayOpt_MN", q(dayOpt)+">="+dayMinimum);
		assertCheckConstraint(table, "Main_dayOpt_MX", q(dayOpt)+"<="+dayMaximum);
		assertCheckConstraint(table, "Main_boolOpt_EN", q(boolOpt)+" IN (0,1)");
		assertCheckConstraint(table, "Main_enumOpt_EN", q(enumOpt)+" IN (10,20,30)");
		assertCheckConstraint(table, "Main_itemOpt_MN", q(itemOpt)+">=0");
		assertCheckConstraint(table, "Main_itemOpt_MX", q(itemOpt)+"<=567");
		assertCheckConstraint(table, "Main_polyOpt_MN", q(polyOpt)+">=0");
		assertCheckConstraint(table, "Main_polyOpt_MX", q(polyOpt)+"<=567");
		assertCheckConstraint(table, "Main_polyOptType_EN", t(polyOpt)+" IN ('Polymorphic','Target')");
		assertCheckConstraint(table, "Main_polyOptType_NS",
				"(("+t(polyOpt)+" IS NOT NULL) AND ("+q(polyOpt)+" IS NOT NULL))" +
				" OR (("+t(polyOpt)+" IS NULL) AND ("+q(polyOpt)+" IS NULL))");

		assertPkConstraint(table, "Main_PK", null, getPrimaryKeyColumnName(TYPE));

		assertFkConstraint(table, "Main_item_Fk", getColumnName(item), getTableName(SchemaTargetItem.TYPE), getPrimaryKeyColumnName(SchemaTargetItem.TYPE));

		assertUniqueConstraint(table, "Main_uniqueString_Unq", "("+q(uniqueString)+")");

		assertUniqueConstraint(table, "Main_doubleUnique_Unq", "("+q(string)+","+q(anEnum)+")");

		final Column min4Max8Column = table.getColumn(getColumnName(stringMin4Max8));
		assertEquals(null, min4Max8Column.getError());
		assertEquals(OK, min4Max8Column.getParticularColor());

		assertEquals(
				switch(dialect)
				{
					case hsqldb     -> "VARCHAR(8)";
					case mysql      -> "varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin";
					case postgresql -> "character varying(8) COLLATE \"ucs_basic\"";
				},
				min4Max8Column.getType());

		final boolean icu = mysql && atLeastMysql8();
		final String regexpBegin = icu ? "\\A": "^";
		final String regexpEnd   = icu ? "\\z": "$";
		final String upperSQL = mysql ? q(stringUpper6)+" REGEXP '"+regexpBegin+"[A-Z]*"   +regexpEnd+"'" : "";
		final String hexSQL   = mysql ? q(data)        +" REGEXP '"+regexpBegin+"[0-9a-f]*"+regexpEnd+"'" : "";
		final String regexpSQL =
				switch(dialect)
				{
					case hsqldb -> "REGEXP_MATCHES("+q(stringUpper6)+",'(?s)\\A[A-B]*\\z')";
					case mysql -> icu
							? q(stringUpper6)+" REGEXP CAST('(?s)\\A[A-B]*\\z' AS CHAR)"
							: q(stringUpper6)+" REGEXP CAST('^[A-B]*$' AS CHAR)";
					case postgresql -> q(stringUpper6)+" ~ '^[A-B]*$'";
				};

		assertCheckConstraint(table, "Main_stringMin4_MN", "CHAR_LENGTH("+q(stringMin4)+")>=4");
		assertCheckConstraint(table, "Main_stringMin4_MX", "CHAR_LENGTH("+q(stringMin4)+")<="+StringField.DEFAULT_MAXIMUM_LENGTH);
		assertCheckConstraint(table, "Main_stringMax4_MN", "CHAR_LENGTH("+q(stringMax4)+")>=1");
		assertCheckConstraint(table, "Main_stringMax4_MX", "CHAR_LENGTH("+q(stringMax4)+")<=4");
		assertCheckConstraint(table, "Main_stringMin4Max8_MN", "CHAR_LENGTH("+q(stringMin4Max8)+")>=4");
		assertCheckConstraint(table, "Main_stringMin4Max8_MX", "CHAR_LENGTH("+q(stringMin4Max8)+")<=8");
		assertCheckConstraint(table, "Main_stringExact6_MN", "CHAR_LENGTH("+q(stringExact6)+")=6");
		assertCheckConstraint(table, "Main_strinExact6_MX", null, false);
		assertCheckConstraint(table, "Main_stringUpper6_MN", "CHAR_LENGTH("+q(stringUpper6)+")=6");
		assertCheckConstraint(table, "Main_stringUpper6_MX", null, false);
		assertCheckConstraint(table, "Main_stringUpper6_CS", upperSQL, mysql);
		assertCheckConstraint(table, "Main_stringUpper6_RE", regexpSQL);
		assertCheckConstraint(table, "Main_stringEmpty_MN", null, false);
		assertCheckConstraint(table, "Main_stringEmpty_MX", "CHAR_LENGTH("+q(stringEmpty)+")<="+StringField.DEFAULT_MAXIMUM_LENGTH);
		assertCheckConstraint(table, "Main_data_MX", "OCTET_LENGTH("+q(data)+")<="+DataField.DEFAULT_LENGTH, !dataVault);
		assertCheckConstraint(table, "Main_data_MN", "CHAR_LENGTH("+q(data)+")=128", dataVault);
		assertCheckConstraint(table, "Main_data_CS", hexSQL, dataVault && mysql);

		final Column stringLongColumn = table.getColumn(getColumnName(stringLong));
		assertEquals(null, stringLongColumn.getError());
		assertEquals(OK, stringLongColumn.getParticularColor());

		final Sequence sequence        = schema.getSequence("Main_this_Seq");
		final Sequence batchedSequence = schema.getSequence("Main_this_Seq6");
		switch ( model.getConnectProperties().primaryKeyGenerator )
		{
			case memory -> {
				assertEquals(null, sequence);
				assertEquals(null, batchedSequence);
			}
			case sequence -> {
				assertEquals(null, sequence.getError());
				assertEquals(OK, sequence.getParticularColor());
				assertEquals(null, batchedSequence);
			}
			case batchedSequence -> {
				assertEquals(null, batchedSequence.getError());
				assertEquals(OK, batchedSequence.getParticularColor());
				assertEquals(null, sequence);
			}
			default ->
				fail();
		}

		assertEquals(OK, table.getCumulativeColor());
	}

	private static String q(final Field<?> f)
	{
		return SI.col(f);
	}

	private static String t(final ItemField<?> f)
	{
		return SI.type(f);
	}

	private void assertCheckConstraint(
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
