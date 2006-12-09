/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.util.Iterator;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;

public class MigrationTest extends CopeAssert
{
	private static final Model model1 = new Model(MigrationItem1.TYPE);
	private static final Model model2 = new Model(MigrationItem2.TYPE);
	
	public void test()
	{
		final Properties props = new Properties();
		
		model1.connect(props);
		model1.createDatabase();

		assertSchema(model1.getVerifiedSchema(), false);
		model1.disconnect();
		
		model2.connect(props);
		assertSchema(model2.getVerifiedSchema(), true);
		
		model2.tearDownDatabase();
	}
	
	private void assertSchema(final Schema schema, final boolean model2)
	{
		final Iterator<Table> tables = schema.getTables().iterator();
		
		final Table table = tables.next();
		assertEquals("MigrationItem", table.getName());
		assertEquals(true, table.required());
		assertEquals(true, table.exists());
		assertFalse(tables.hasNext());
		final Iterator<Column> columns = table.getColumns().iterator();

		final Column columnThis = columns.next();
		assertEquals("this", columnThis.getName());
		assertEquals(true, columnThis.required());
		assertEquals(true, columnThis.exists());
		assertNotNull(columnThis.getType());
		
		final Column column1 = columns.next();
		assertEquals("field1", column1.getName());
		assertEquals(true, column1.required());
		assertEquals(true, column1.exists());
		assertNotNull(column1.getType());
		
		if(model2)
		{
			final Column column2 = columns.next();
			assertEquals("field2", column2.getName());
			assertEquals(true, column2.required());
			assertEquals(false, column2.exists());
			assertNotNull(column2.getType());
		}
		
		assertFalse(columns.hasNext());
	}
}
