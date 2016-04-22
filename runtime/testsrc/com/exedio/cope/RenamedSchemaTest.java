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

import static com.exedio.cope.RenamedSchemaItem.TYPE;
import static com.exedio.cope.RenamedSchemaItem.integer;
import static com.exedio.cope.RenamedSchemaItem.item;
import static com.exedio.cope.RenamedSchemaItem.sequence;
import static com.exedio.cope.RenamedSchemaItem.string;
import static com.exedio.cope.RenamedSchemaItem.uniqueDouble1;
import static com.exedio.cope.RenamedSchemaItem.uniqueDouble2;
import static com.exedio.cope.RenamedSchemaItem.uniqueSingle;
import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnName;
import static com.exedio.cope.SchemaInfo.getSequenceName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import com.exedio.dsmf.Table;
import java.util.Iterator;
import org.junit.Test;

public class RenamedSchemaTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE, RenamedSchemaTargetItem.TYPE);

	public RenamedSchemaTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	@Test public void testSchema()
	{
		assertEquals(filterTableName("ZackItem"), getTableName(TYPE));
		assertPrimaryKeySequenceName("ZackItem_this_Seq", TYPE);
		assertEquals("zackUniqueSingle", getColumnName(uniqueSingle));
		assertEquals("zackItem", getColumnName(item));
		assertEquals("uniqueDouble1", getColumnName(uniqueDouble1));
		assertEquals("uniqueDouble2", getColumnName(uniqueDouble2));
		assertEquals("zackString", getColumnName(string));
		assertEquals("zackInteger", getColumnName(integer));
		assertDefaultToNextSequenceName("ZackItem_zackInteger_Seq", integer);
		assertEquals(filterTableName("ZackItem_zackSequence"), getSequenceName(sequence));

		final Schema schema = model.getVerifiedSchema();

		final Table table = schema.getTable(getTableName(TYPE));
		assertNotNull(table);
		assertEquals(null, table.getError());
		assertEquals(Schema.Color.OK, table.getParticularColor());

		assertPkConstraint(table, "ZackItem_Pk", null, getPrimaryKeyColumnName(TYPE));

		assertFkConstraint(table, "ZackItem_zackItem_Fk", getColumnName(item), getTableName(RenamedSchemaTargetItem.TYPE), getPrimaryKeyColumnName(RenamedSchemaTargetItem.TYPE));

		assertUniqueConstraint(table, "ZackItem_zackUniqSing_Unq", "("+q(uniqueSingle)+")");

		assertUniqueConstraint(table, "ZackItem_zackUniqDoub_Unq", "("+q(uniqueDouble1)+","+q(uniqueDouble2)+")");

		assertCheckConstraint(table, "ZackItem_zackString_Ck", "(("+q(string)+" IS NOT NULL) AND (("+l(string)+">=1) AND ("+l(string)+"<=4))) OR ("+q(string)+" IS NULL)");

		final ConnectProperties props = model.getConnectProperties();
		final boolean cluster = props.primaryKeyGenerator.persistent;
		final Iterator<Sequence> sequences = schema.getSequences().iterator();
		if(cluster)
		{
			final Sequence sequence = sequences.next();
			assertEquals(primaryKeySequenceName("ZackItem_this_Seq"), sequence.getName());
			assertEquals(0, sequence.getStart());
		}
		{
			final Sequence sequence = sequences.next();
			assertEquals(filterTableName("ZackItem_zackInteger_Seq"), sequence.getName());
			assertEquals(1234, sequence.getStart());
		}
		{
			final Sequence sequence = sequences.next();
			assertEquals(props.filterTableName("ZackItem_zackSequence"), sequence.getName());
			assertEquals(555, sequence.getStart());
		}
		if(cluster)
		{
			final Sequence sequence = sequences.next();
			if ( model.getConnectProperties().primaryKeyGenerator==PrimaryKeyGenerator.batchedSequence )
			{
				assertEquals(primaryKeySequenceName("RenaScheTargItem_thi_Seq"), sequence.getName());
			}
			else
			{
				assertEquals(primaryKeySequenceName("RenamScheTargItem_thi_Seq"), sequence.getName());
			}
			assertEquals(0, sequence.getStart());
		}
		assertFalse(sequences.hasNext());
	}

	private final String q(final Field<?> f)
	{
		return SchemaInfo.quoteName(model, getColumnName(f));
	}

	private final String l(final StringField f)
	{
		return model.connect().database.dialect.getStringLength() + '(' + q(f) + ')';
	}
}
