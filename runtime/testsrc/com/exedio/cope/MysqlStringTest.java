/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.MysqlStringItem.TYPE;
import static com.exedio.cope.MysqlStringItem.longMax;
import static com.exedio.cope.MysqlStringItem.longMin;
import static com.exedio.cope.MysqlStringItem.mediumMax;
import static com.exedio.cope.MysqlStringItem.mediumMin;
import static com.exedio.cope.MysqlStringItem.textMax;
import static com.exedio.cope.MysqlStringItem.textMin;
import static com.exedio.cope.MysqlStringItem.varcharMax;
import static com.exedio.cope.MysqlStringItem.varcharMin;
import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.supportsNotNull;

public class MysqlStringTest extends AbstractRuntimeModelTest
{
	static final Model MODEL = new Model(TYPE);

	public MysqlStringTest()
	{
		super(MODEL);
	}

	public void testSchemaTypes()
	{
		if(!mysql)
			return;

		assertType("varchar(1)" , varcharMin);
		assertType("varchar(85)", varcharMax);
		assertType("text", textMin);
		assertType("text", textMax);
		assertType("mediumtext", mediumMin);
		assertType("mediumtext", mediumMax);
		assertType("longtext", longMin);
		assertType("longtext", longMax);
	}

	private void assertType(final String type, final StringField field)
	{
		assertEquals(
				type + " CHARACTER SET utf8 COLLATE utf8_bin" + (supportsNotNull(model) ? " not null" : ""),
				model.getSchema().getTable(getTableName(TYPE)).getColumn(getColumnName(field)).getType());
	}

	public void testSchema()
	{
		assertSchema();
	}
}
