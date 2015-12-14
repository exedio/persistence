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
import static com.exedio.cope.SchemaItem.TYPE;
import static com.exedio.cope.SchemaItem.anEnum;
import static com.exedio.cope.SchemaItem.bool;
import static com.exedio.cope.SchemaItem.boolOpt;
import static com.exedio.cope.SchemaItem.data;
import static com.exedio.cope.SchemaItem.doub;
import static com.exedio.cope.SchemaItem.doubOpt;
import static com.exedio.cope.SchemaItem.enumOpt;
import static com.exedio.cope.SchemaItem.integ;
import static com.exedio.cope.SchemaItem.integOpt;
import static com.exedio.cope.SchemaItem.item;
import static com.exedio.cope.SchemaItem.itemOpt;
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

import com.exedio.dsmf.Column;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import com.exedio.dsmf.Table;
import junit.framework.AssertionFailedError;

public class SchemaTest extends AbstractRuntimeModelTest
{
	static final Model MODEL = new Model(TYPE, SchemaTargetItem.TYPE);

	public SchemaTest()
	{
		super(MODEL);
	}

	@Override
	protected boolean doesManageTransactions()
	{
		return false;
	}

	@Test public void testSchema()
	{
		final Schema schema = model.getVerifiedSchema();

		final Table table = schema.getTable(getTableName(TYPE));
		assertNotNull(table);
		assertEquals(null, table.getError());
		assertEquals(Schema.Color.OK, table.getParticularColor());

		assertCheckConstraint(table, "SchemaItem_string_Ck", "("+l(string)+">=1) AND (" + l(string)+"<="+StringField.DEFAULT_MAXIMUM_LENGTH+")");
		assertCheckConstraint(table, "SchemaItem_integ_Ck" , "("+q(integ )+">=-10) AND ("+q(integ)+"<=10)");
		if(!oracle) // TODO
			assertCheckConstraint(table, "SchemaItem_doub_Ck", "("+q(doub  )+">=-11.1) AND ("+q(doub)+"<=11.1)");
		else
			assertNotExistsConstraint(table, "SchemaItem_doub_Ck");
		assertCheckConstraint(table, "SchemaItem_bool_Ck"  , hp(q(bool  ))+" IN ("+hp("0")+","+hp("1")+")");
		assertCheckConstraint(table, "SchemaItem_anEnum_Ck", hp(q(anEnum))+" IN ("+hp("10")+","+hp("20")+","+hp("30")+")");
		assertCheckConstraint(table, "SchemaItem_item_Ck"  , "("+q(item  )+">=0) AND ("+q(item)+"<="+Integer.MAX_VALUE+")");

		assertCheckConstraint(table, "SchemaItem_stringOpt_Ck","(("+q(stringOpt)+" IS NOT NULL) AND (("+l(stringOpt)+">=1) AND ("+l(stringOpt)+"<="+StringField.DEFAULT_MAXIMUM_LENGTH+")))"+" OR ("+q(stringOpt)+" IS NULL)");
		assertCheckConstraint(table, "SchemaItem_integOpt_Ck" ,"(("+q(integOpt )+" IS NOT NULL) AND (("+q(integOpt)+">=-10) AND ("+q(integOpt)+"<=10)))"                 +" OR ("+q(integOpt )+" IS NULL)");
		if(!oracle) // TODO
		assertCheckConstraint(table, "SchemaItem_doubOpt_Ck"  ,"(("+q(doubOpt  )+" IS NOT NULL) AND (("+q(doubOpt)+">=-11.1) AND ("+q(doubOpt)+"<=11.1)))"               +" OR ("+q(doubOpt  )+" IS NULL)");
		assertCheckConstraint(table, "SchemaItem_boolOpt_Ck"  ,"(("+q(boolOpt  )+" IS NOT NULL) AND ("+hp(q(boolOpt))+" IN ("+hp("0")+","+hp("1")+")))"                  +" OR ("+q(boolOpt  )+" IS NULL)");
		assertCheckConstraint(table, "SchemaItem_enumOpt_Ck"  ,"(("+q(enumOpt  )+" IS NOT NULL) AND ("+hp(q(enumOpt))+" IN ("+hp("10")+","+hp("20")+","+hp("30")+")))"   +" OR ("+q(enumOpt  )+" IS NULL)");
		assertCheckConstraint(table, "SchemaItem_itemOpt_Ck"  ,"(("+q(itemOpt  )+" IS NOT NULL) AND (("+q(itemOpt)  +">=0) AND ("+q(itemOpt)+"<="+Integer.MAX_VALUE+")))"+" OR ("+q(itemOpt  )+" IS NULL)");

		assertPkConstraint(table, "SchemaItem_Pk", null, getPrimaryKeyColumnName(TYPE));

		assertFkConstraint(table, "SchemaItem_item_Fk", getColumnName(item), getTableName(SchemaTargetItem.TYPE), getPrimaryKeyColumnName(SchemaTargetItem.TYPE));

		assertUniqueConstraint(table, "SchemaItem_uniquStrin_Unq", "("+q(uniqueString)+")");

		assertUniqueConstraint(table, "SchemaItem_doublUniqu_Unq", "("+q(string)+","+q(anEnum)+")");

		final Column min4Max8Column = table.getColumn(getColumnName(stringMin4Max8));
		assertEquals(null, min4Max8Column.getError());
		assertEquals(Schema.Color.OK, min4Max8Column.getParticularColor());

		final String mb4 = model.getConnectProperties().mysqlUtf8mb4 ? "mb4" : "";
		final String string8;
		switch(dialect)
		{
			case hsqldb:     string8 = "VARCHAR(8)"; break;
			case mysql :     string8 = "varchar(8) CHARACTER SET utf8"+mb4+" COLLATE utf8"+mb4+"_bin"; break;
			case oracle:     string8 = "VARCHAR2(24 BYTE)"; break; // varchar specifies bytes
			case postgresql: string8 = "varchar(8)"; break;
			default:
				throw new AssertionFailedError(dialect.name());
		}
		assertEquals(string8, min4Max8Column.getType());

		final String upperSQL = mysql ? " AND ("+q(stringUpper6)+" REGEXP '^[A-Z]*$')" : "";

		assertCheckConstraint(table, "SchemaItem_stringMin4_Ck",  "(("+q(stringMin4)    +" IS NOT NULL) AND (("+l(stringMin4)+">=4) AND ("+l(stringMin4)+"<="+StringField.DEFAULT_MAXIMUM_LENGTH+"))) OR ("+q(stringMin4)+" IS NULL)");
		assertCheckConstraint(table, "SchemaItem_stringMax4_Ck",  "(("+q(stringMax4)    +" IS NOT NULL) AND (("+l(stringMax4)+">=1) AND (" +l(stringMax4)+"<=4))) OR ("+q(stringMax4)+" IS NULL)");
		assertCheckConstraint(table, "SchemItem_striMin4Max8_Ck", "(("+q(stringMin4Max8)+" IS NOT NULL) AND (("+l(stringMin4Max8)+">=4) AND ("+l(stringMin4Max8)+"<=8))) OR ("+q(stringMin4Max8)+" IS NULL)");
		assertCheckConstraint(table, "SchemaItem_strinExact6_Ck", "(("+q(stringExact6)  +" IS NOT NULL) AND (" +l(stringExact6)+"=6)) OR ("+q(stringExact6)+" IS NULL)");
		assertCheckConstraint(table, "SchemaItem_strinUpper6_Ck", "(("+q(stringUpper6)  +" IS NOT NULL) AND (" +l(stringUpper6)+"=6" + upperSQL + ")) OR ("+q(stringUpper6)+" IS NULL)");
		assertCheckConstraint(table, "SchemaItem_stringEmpty_Ck", "(("+q(stringEmpty)   +" IS NOT NULL) AND (" +l(stringEmpty)+"<="+StringField.DEFAULT_MAXIMUM_LENGTH+")) OR ("+q(stringEmpty)+" IS NULL)");
		assertCheckConstraint(table, "SchemaItem_data_Ck",        "(("+q(data)          +" IS NOT NULL) AND (" +l(data)+"<="+(DataField.DEFAULT_LENGTH)+")) OR ("+q(data)+" IS NULL)");

		final Column stringLongColumn = table.getColumn(getColumnName(stringLong));
		assertEquals(null, stringLongColumn.getError());
		assertEquals(Schema.Color.OK, stringLongColumn.getParticularColor());

		switch ( model.getConnectProperties().primaryKeyGenerator )
		{
			case memory:
				assertEquals( null, schema.getSequence(filterTableName("SchemaItem_this_Seq")) );
				assertEquals( null, schema.getSequence(filterTableName("SchemaItem_this_Seq6")) );
				break;
			case sequence:
				final Sequence sequence = schema.getSequence(filterTableName("SchemaItem_this_Seq"));
				assertEquals(null, sequence.getError());
				assertEquals(Schema.Color.OK, sequence.getParticularColor());
				assertEquals( null, schema.getSequence(filterTableName("SchemaItem_this_Seq6")) );
				break;
			case batchedSequence:
				final Sequence batchedSequence = schema.getSequence(filterTableName("SchemaItem_this_Seq6"));
				assertEquals(null, batchedSequence.getError());
				assertEquals(Schema.Color.OK, batchedSequence.getParticularColor());
				assertEquals( null, schema.getSequence(filterTableName("SchemaItem_this_Seq")) );
				break;
			default:
				fail();
		}

		assertEquals(Schema.Color.OK, table.getCumulativeColor());
	}

	private final String q(final Field<?> f)
	{
		return SchemaInfo.quoteName(model, getColumnName(f));
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
}
