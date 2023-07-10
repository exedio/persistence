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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.tojunit.SI;
import com.exedio.dsmf.Node;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import com.exedio.dsmf.Table;
import java.util.Iterator;
import org.junit.jupiter.api.Test;

public class RenamedSchemaTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE, RenamedSchemaTargetItem.TYPE);

	public RenamedSchemaTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	@Test void testSchema()
	{
		assertEquals("Zain", getTableName(TYPE));
		assertPrimaryKeySequenceName("Zain_this_Seq", TYPE);
		assertEquals("zuniqueSingle", getColumnName(uniqueSingle));
		assertEquals("zitem", getColumnName(item));
		assertEquals("uniqueDouble1", getColumnName(uniqueDouble1));
		assertEquals("uniqueDouble2", getColumnName(uniqueDouble2));
		assertEquals("zring", getColumnName(string));
		assertEquals("zinteger", getColumnName(integer));
		assertDefaultToNextSequenceName("Zain_zinteger_Seq", integer);
		assertEquals("Zain_zequence", getSequenceName(sequence));

		final Schema schema = model.getVerifiedSchema();

		final Table table = schema.getTable(getTableName(TYPE));
		assertNotNull(table);
		assertEquals(null, table.getError());
		assertEquals(Node.Color.OK, table.getParticularColor());

		assertPkConstraint(table, "Zain_PK", null, getPrimaryKeyColumnName(TYPE));

		assertFkConstraint(table, "Zain_zitem_Fk", getColumnName(item), getTableName(RenamedSchemaTargetItem.TYPE), getPrimaryKeyColumnName(RenamedSchemaTargetItem.TYPE));

		assertUniqueConstraint(table, "Zain_zuniqueSingle_Unq", "("+SI.col(uniqueSingle)+")");

		assertUniqueConstraint(table, "Zain_zuniqueDouble_Unq", "("+SI.col(uniqueDouble1)+","+SI.col(uniqueDouble2)+")");

		assertCheckConstraint(table, "Zain_zring_MN", l(string)+">=1");
		assertCheckConstraint(table, "Zain_zring_MX", l(string)+"<=4");

		final ConnectProperties props = model.getConnectProperties();
		final boolean cluster = props.primaryKeyGenerator.persistent;
		final Iterator<Sequence> sequences = schema.getSequences().iterator();
		if(cluster)
			assertIt(sequences.next(), primaryKeySequenceName("Zain_this_Seq"), 0);
		assertIt(sequences.next(), "Zain_zinteger_Seq", 1234);
		assertIt(sequences.next(), "Zain_zequence", 555);
		if(cluster)
			assertIt(sequences.next(), primaryKeySequenceName("Target_this_Seq"), 0);
		assertFalse(sequences.hasNext());
	}

	private static void assertIt(final Sequence sequence, final String name, final int start)
	{
		assertEquals(name, sequence.getName());
		assertEquals(start, sequence.getStartL());
	}

	private static String l(final StringField f)
	{
		return "CHAR_LENGTH" + '(' + SI.col(f) + ')';
	}
}
