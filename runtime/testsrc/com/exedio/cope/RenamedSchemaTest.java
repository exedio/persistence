/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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
import static com.exedio.cope.RenamedSchemaItem.string;
import static com.exedio.cope.RenamedSchemaItem.uniqueDouble2;
import static com.exedio.cope.RenamedSchemaItem.uniqueDouble1;
import static com.exedio.cope.RenamedSchemaItem.item;
import static com.exedio.cope.RenamedSchemaItem.uniqueSingle;
import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;

import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;

public class RenamedSchemaTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(TYPE, RenamedSchemaTargetItem.TYPE);
	
	public RenamedSchemaTest()
	{
		super(MODEL);
	}

	public void testSchema()
	{
		if(postgresql) return;
		
		assertEquals(filterTableName("ZackItem"), getTableName(TYPE));
		assertEquals("zackSingleUnique", getColumnName(uniqueSingle));
		assertEquals("zackItem", getColumnName(item));
		assertEquals("uniqueDouble1", getColumnName(uniqueDouble1));
		assertEquals("uniqueDouble2", getColumnName(uniqueDouble2));
		assertEquals("zackString", getColumnName(string));
		
		final Schema schema = model.getVerifiedSchema();

		final Table table = schema.getTable(getTableName(TYPE));
		assertNotNull(table);
		assertEquals(null, table.getError());
		assertEquals(Schema.Color.OK, table.getParticularColor());

		assertPkConstraint(table, "ZackItem_Pk", null, getPrimaryKeyColumnName(TYPE));

		assertFkConstraint(table, "ZackItem_zackItem_Fk", getColumnName(item), filterTableName("RenamedSchemaTargetItem"), getPrimaryKeyColumnName(RenamedSchemaTargetItem.TYPE));

		assertUniqueConstraint(table, "ZackItem_zackSingUniq_Unq", "("+q(uniqueSingle)+")");
		
		assertUniqueConstraint(table, "ZackItem_zackDoubUniq_Unq", "("+q(uniqueDouble1)+","+q(uniqueDouble2)+")");
		
		assertCheckConstraint(table, "ZackItem_zackString_Ck", "(("+q(string)+" IS NOT NULL) AND ("+l(string)+"<=4)) OR ("+q(string)+" IS NULL)");
	}
	
	private final String q(final Field f)
	{
		return SchemaInfo.quoteName(model, getColumnName(f));
	}
	
	private final String l(final StringField f)
	{
		return model.connect().database.dialect.stringLength + '(' + q(f) + ')';
	}
}
