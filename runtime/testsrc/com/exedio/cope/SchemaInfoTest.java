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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getForeignKeyConstraintName;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnName;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnValueL;
import static com.exedio.cope.SchemaInfo.getSuperForeignKeyConstraintName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.getTypeColumnName;
import static com.exedio.cope.SchemaInfo.getUpdateCounterColumnName;
import static com.exedio.cope.SchemaInfo.quoteName;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SchemaInfoTest extends TestWithEnvironment
{
	public SchemaInfoTest()
	{
		super(InstanceOfModelTest.MODEL);
	}

	@Test void testSchemaInfo()
	{
		// quoteName
		final char q = mysql ? '`' : '"';
		assertEquals(q + "name" + q, quoteName(model, "name"));
		assertEquals(q + "x" + q, quoteName(model, "x"));
		assertFails(
				() -> quoteName(null, null),
				NullPointerException.class,
				"model");
		assertFails(
				() -> quoteName(model, null),
				NullPointerException.class,
				"name");
		assertFails(
				() -> quoteName(model, ""),
				IllegalArgumentException.class,
				"name must not be empty");
		assertFails(
				() -> quoteName(model, "\"`"),
				IllegalArgumentException.class,
				"database name contains forbidden characters: \"`");
		assertFails(
				() -> getPrimaryKeyColumnValueL(null),
				NullPointerException.class,
				"Cannot read field \"pk\" because \"item\" is null");

		// with sub types
		assertEquals("InstanceOfAItem", getTableName(InstanceOfAItem.TYPE));
		assertPrimaryKeySequenceName("InstanceOfAItem_this_Seq", InstanceOfAItem.TYPE);
		assertEquals(synthetic("this", "InstanceOfAItem"), getPrimaryKeyColumnName(InstanceOfAItem.TYPE));
		assertEquals(synthetic("class", "InstanceOfAItem"), getTypeColumnName(InstanceOfAItem.TYPE));
		assertEquals("code", getColumnName(InstanceOfAItem.code));
		assertEquals("ref", getColumnName(InstanceOfRefItem.ref));
		assertEquals("refType", getTypeColumnName(InstanceOfRefItem.ref));
		assertEquals("InstanceOfRefItem_ref_Fk", getForeignKeyConstraintName(InstanceOfRefItem.ref));

		assertFails (                  () -> getSuperForeignKeyConstraintName(null), NullPointerException.class, "Cannot read field \"table\" because \"type\" is null");
		assertFails (                  () -> getSuperForeignKeyConstraintName(InstanceOfAItem.TYPE), IllegalArgumentException.class, "no super type for InstanceOfAItem");
		assertEquals("InstanceOfB1Item_Sup", getSuperForeignKeyConstraintName(InstanceOfB1Item.TYPE));
		assertEquals("InstanceOfB2Item_Sup", getSuperForeignKeyConstraintName(InstanceOfB2Item.TYPE));
		assertEquals("InstanceOfC1Item_Sup", getSuperForeignKeyConstraintName(InstanceOfC1Item.TYPE));

		// without sub types
		assertEquals("InstanceOfB2Item", getTableName(InstanceOfB2Item.TYPE));
		assertPrimaryKeySequenceName("InstanceOfAItem_this_Seq", InstanceOfB2Item.TYPE);
		assertEquals(synthetic("this", "InstanceOfB2Item"), getPrimaryKeyColumnName(InstanceOfB2Item.TYPE));
		assertFails(
				() -> getTypeColumnName(InstanceOfB2Item.TYPE),
				IllegalArgumentException.class,
				"no type column for InstanceOfB2Item");
		assertEquals("textb2", getColumnName(InstanceOfB2Item.textb2));
		assertEquals("refb2", getColumnName(InstanceOfRefItem.refb2));
		assertFails(
				() -> getTypeColumnName(InstanceOfRefItem.refb2),
				IllegalArgumentException.class,
				"no type column for InstanceOfRefItem.refb2");

		assertFails(
				() -> getUpdateCounterColumnName(InstanceOfAItem.TYPE),
				IllegalArgumentException.class,
				"no update counter for InstanceOfAItem");
		assertEquals(synthetic("catch", "InstanceOfB1Item"), getUpdateCounterColumnName(InstanceOfB1Item.TYPE));

		assertCacheInfo(
			InstanceOfAItem.TYPE, InstanceOfB1Item.TYPE, InstanceOfB2Item.TYPE, InstanceOfC1Item.TYPE, InstanceOfRefItem.TYPE
		);
	}
}
