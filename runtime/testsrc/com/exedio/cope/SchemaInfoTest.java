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

import static com.exedio.cope.SchemaInfo.columnName;
import static com.exedio.cope.SchemaInfo.columnValue;
import static com.exedio.cope.SchemaInfo.foreignKeyConstraintName;
import static com.exedio.cope.SchemaInfo.primaryKeyColumnName;
import static com.exedio.cope.SchemaInfo.primaryKeyColumnNameQuoted;
import static com.exedio.cope.SchemaInfo.quoteName;
import static com.exedio.cope.SchemaInfo.superForeignKeyConstraintName;
import static com.exedio.cope.SchemaInfo.tableName;
import static com.exedio.cope.SchemaInfo.tableNameQuoted;
import static com.exedio.cope.SchemaInfo.typeColumnName;
import static com.exedio.cope.SchemaInfo.typeColumnNameQuoted;
import static com.exedio.cope.SchemaInfo.updateCounterColumnName;
import static com.exedio.cope.SchemaInfo.updateCounterColumnNameQuoted;
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
				() -> columnValue((Item)null),
				NullPointerException.class,
				"Cannot read field \"pk\" because \"value\" is null");

		// with sub types
		assertEquals("InstanceOfAItem", tableName(InstanceOfAItem.TYPE));
		assertEquals(q("InstanceOfAItem"), tableNameQuoted(InstanceOfAItem.TYPE));
		assertPrimaryKeySequenceName("InstanceOfAItem_this_Seq", InstanceOfAItem.TYPE);
		assertEquals(synthetic("this", "InstanceOfAItem"), primaryKeyColumnName(InstanceOfAItem.TYPE));
		assertEquals(q(synthetic("this", "InstanceOfAItem")), primaryKeyColumnNameQuoted(InstanceOfAItem.TYPE));
		assertEquals(synthetic("class", "InstanceOfAItem"), typeColumnName(InstanceOfAItem.TYPE));
		assertEquals(q(synthetic("class", "InstanceOfAItem")), typeColumnNameQuoted(InstanceOfAItem.TYPE));
		assertEquals("code", columnName(InstanceOfAItem.code));
		assertEquals("ref", columnName(InstanceOfRefItem.ref));
		assertEquals("refType", typeColumnName(InstanceOfRefItem.ref));
		assertEquals("InstanceOfRefItem_ref_Fk", foreignKeyConstraintName(InstanceOfRefItem.ref));

		assertFails (                  () -> superForeignKeyConstraintName(null), NullPointerException.class, "Cannot read field \"table\" because \"type\" is null");
		assertFails (                  () -> superForeignKeyConstraintName(InstanceOfAItem.TYPE), IllegalArgumentException.class, "no super type for InstanceOfAItem");
		assertEquals("InstanceOfB1Item_Sup", superForeignKeyConstraintName(InstanceOfB1Item.TYPE));
		assertEquals("InstanceOfB2Item_Sup", superForeignKeyConstraintName(InstanceOfB2Item.TYPE));
		assertEquals("InstanceOfC1Item_Sup", superForeignKeyConstraintName(InstanceOfC1Item.TYPE));

		// without sub types
		assertEquals("InstanceOfB2Item", tableName(InstanceOfB2Item.TYPE));
		assertEquals(q("InstanceOfB2Item"), tableNameQuoted(InstanceOfB2Item.TYPE));
		assertPrimaryKeySequenceName("InstanceOfAItem_this_Seq", InstanceOfB2Item.TYPE);
		assertEquals(synthetic("this", "InstanceOfB2Item"), primaryKeyColumnName(InstanceOfB2Item.TYPE));
		assertEquals(q(synthetic("this", "InstanceOfB2Item")), primaryKeyColumnNameQuoted(InstanceOfB2Item.TYPE));
		assertFails(
				() -> typeColumnName(InstanceOfB2Item.TYPE),
				IllegalArgumentException.class,
				"no type column for InstanceOfB2Item");
		assertFails(
				() -> typeColumnNameQuoted(InstanceOfB2Item.TYPE),
				IllegalArgumentException.class,
				"no type column for InstanceOfB2Item");
		assertEquals("textb2", columnName(InstanceOfB2Item.textb2));
		assertEquals("refb2", columnName(InstanceOfRefItem.refb2));
		assertFails(
				() -> typeColumnName(InstanceOfRefItem.refb2),
				IllegalArgumentException.class,
				"no type column for InstanceOfRefItem.refb2");

		assertFails(
				() -> updateCounterColumnName(InstanceOfAItem.TYPE),
				IllegalArgumentException.class,
				"no update counter for InstanceOfAItem");
		assertFails(
				() -> updateCounterColumnNameQuoted(InstanceOfAItem.TYPE),
				IllegalArgumentException.class,
				"no update counter for InstanceOfAItem");
		assertEquals(synthetic("catch", "InstanceOfB1Item"), updateCounterColumnName(InstanceOfB1Item.TYPE));
		assertEquals(q(synthetic("catch", "InstanceOfB1Item")), updateCounterColumnNameQuoted(InstanceOfB1Item.TYPE));

		assertCacheInfo(
			InstanceOfAItem.TYPE, InstanceOfB1Item.TYPE, InstanceOfB2Item.TYPE, InstanceOfC1Item.TYPE, InstanceOfRefItem.TYPE
		);
	}

	private String q(final String s)
	{
		return quoteName(model, s);
	}
}
