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
import static com.exedio.cope.RenamedSchemaItem.min4;
import static com.exedio.cope.RenamedSchemaItem.integer;
import static com.exedio.cope.RenamedSchemaItem.string;
import static com.exedio.cope.RenamedSchemaItem.someItem;
import static com.exedio.cope.RenamedSchemaItem.someNotNullString;
import static com.exedio.cope.RenamedSchemaItem.uniqueString;
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
		
		assertEquals(filterTableName("SchemaItem"), getTableName(TYPE));
		assertEquals("UNIQUE_S", getColumnName(uniqueString));
		assertEquals("someItem", getColumnName(someItem));
		assertEquals("string", getColumnName(string));
		assertEquals("integer", getColumnName(integer));
		assertEquals("MIN_4", getColumnName(min4));
		
		final Schema schema = model.getVerifiedSchema();

		final Table table = schema.getTable(getTableName(TYPE));
		assertNotNull(table);
		assertEquals(null, table.getError());
		assertEquals(Schema.Color.OK, table.getParticularColor());

		assertCheckConstraint(table, "ScheItem_somNotNullStr_Ck", "(" +q(someNotNullString) +" IS NOT NULL) AND ("+l(someNotNullString)+"<="+StringField.DEFAULT_LENGTH+")");

		assertPkConstraint(table, "SchemaItem_Pk", null, getPrimaryKeyColumnName(TYPE));

		assertFkConstraint(table, "SchemaItem_someItem_Fk", getColumnName(someItem), filterTableName("RenamedSchemaTargetItem"), getPrimaryKeyColumnName(RenamedSchemaTargetItem.TYPE));

		assertUniqueConstraint(table, "SchemaItem_UNIQUE_S_Unq", "("+q(uniqueString)+")");
		
		assertUniqueConstraint(table, "SchemaItem_doublUniqu_Unq", "("+q(string)+","+q(integer)+")");
		
		assertCheckConstraint(table, "SchemaItem_MIN_4_Ck", "(("+q(min4)+" IS NOT NULL) AND (("+l(min4)+">=4) AND ("+l(min4)+"<="+StringField.DEFAULT_LENGTH+"))) OR ("+q(min4)+" IS NULL)");
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
