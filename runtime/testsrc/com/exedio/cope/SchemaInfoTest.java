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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.getTypeColumnName;
import static com.exedio.cope.SchemaInfo.quoteName;

public class SchemaInfoTest extends AbstractRuntimeTest
{
	public SchemaInfoTest()
	{
		super(InstanceOfTest.MODEL);
	}
	
	public void testSchemaInfo()
	{
		// quoteName
		final char q = mysql ? '`' : '"';
		assertEquals(q + "name" + q, quoteName(model, "name"));
		assertEquals(q + "x" + q, quoteName(model, "x"));
		try
		{
			quoteName(null, null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("model", e.getMessage());
		}
		try
		{
			quoteName(model, null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("name", e.getMessage());
		}
		try
		{
			quoteName(model, "");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("name must not be empty", e.getMessage());
		}
		try
		{
			quoteName(model, "\"`");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("database name contains forbidden characters: \"`", e.getMessage());
		}
		
		// with sub types
		assertEquals(mysqlLower("InstanceOfAItem"), getTableName(InstanceOfAItem.TYPE));
		assertEquals("this", getPrimaryKeyColumnName(InstanceOfAItem.TYPE));
		assertEquals("class", getTypeColumnName(InstanceOfAItem.TYPE));
		assertEquals("code", getColumnName(InstanceOfAItem.code));
		assertEquals("ref", getColumnName(InstanceOfRefItem.ref));
		assertEquals("refType", getTypeColumnName(InstanceOfRefItem.ref));

		// without sub types
		assertEquals(mysqlLower("InstanceOfB2Item"), getTableName(InstanceOfB2Item.TYPE));
		assertEquals("this", getPrimaryKeyColumnName(InstanceOfB2Item.TYPE));
		try
		{
			getTypeColumnName(InstanceOfB2Item.TYPE);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("no type column for InstanceOfB2Item", e.getMessage());
		}
		assertEquals("textb2", getColumnName(InstanceOfB2Item.textb2));
		assertEquals("refb2", getColumnName(InstanceOfRefItem.refb2));
		try
		{
			getTypeColumnName(InstanceOfRefItem.refb2);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("no type column for InstanceOfRefItem.refb2", e.getMessage());
		}
		assertCacheInfo(
			new Type[]{InstanceOfAItem.TYPE, InstanceOfB1Item.TYPE, InstanceOfC1Item.TYPE, InstanceOfRefItem.TYPE},
			new int []{62500, 12500, 12500, 12500});
	}
}
