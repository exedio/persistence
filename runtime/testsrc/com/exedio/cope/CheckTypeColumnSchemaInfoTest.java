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

import static com.exedio.cope.SchemaInfo.checkCompleteness;
import static com.exedio.cope.SchemaInfo.checkTypeColumn;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.SI;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class CheckTypeColumnSchemaInfoTest extends TestWithEnvironment
{
	public CheckTypeColumnSchemaInfoTest()
	{
		super(InstanceOfModelTest.MODEL);
		copeRule.omitTransaction();
	}

	@Test void test()
	{
		assertFalse(model.hasCurrentTransaction());
		assertFails(
				() -> checkTypeColumn(InstanceOfAItem.TYPE.getThis()),
				IllegalArgumentException.class,
				"no check for type column needed for InstanceOfAItem.this");
		assertEquals(
				"SELECT COUNT(*) FROM " + SI.tab(InstanceOfB1Item.TYPE) +"," + SI.tab(InstanceOfAItem.TYPE) + " " +
				"WHERE " + SI.pkq(InstanceOfB1Item.TYPE) + "=" + SI.pkq(InstanceOfAItem.TYPE) + " " +
				"AND " + SI.typeq(InstanceOfB1Item.TYPE) + "<>" + SI.typeq(InstanceOfAItem.TYPE),
				checkTypeColumn(InstanceOfB1Item.TYPE.getThis()));
		assertEquals(
				"SELECT COUNT(*) FROM " + SI.tab(InstanceOfB2Item.TYPE) + "," + SI.tab(InstanceOfAItem.TYPE) + " " +
				"WHERE " + SI.pkq(InstanceOfB2Item.TYPE) + "=" + SI.pkq(InstanceOfAItem.TYPE) + " " +
				"AND 'InstanceOfB2Item'<>" + SI.typeq(InstanceOfAItem.TYPE),
				checkTypeColumn(InstanceOfB2Item.TYPE.getThis()));
		assertEquals(
				"SELECT COUNT(*) FROM " + SI.tab(InstanceOfC1Item.TYPE) + "," + SI.tab(InstanceOfB1Item.TYPE) + " " +
				"WHERE " + SI.pkq(InstanceOfC1Item.TYPE) + "=" + SI.pkq(InstanceOfB1Item.TYPE) + " " +
				"AND 'InstanceOfC1Item'<>" + SI.typeq(InstanceOfB1Item.TYPE),
				checkTypeColumn(InstanceOfC1Item.TYPE.getThis()));
		final String alias1 = SchemaInfo.quoteName(model, "return");
		final String alias2 = SchemaInfo.quoteName(model, "break");
		assertEquals(
				"SELECT COUNT(*) FROM " + SI.tab(InstanceOfRefItem.TYPE) + " " + alias1 + "," + SI.tab(InstanceOfAItem.TYPE) + " " + alias2 + " " +
				"WHERE " + alias1 + "." + SI.col(InstanceOfRefItem.ref) + "=" + alias2 + "." + SI.pk(InstanceOfAItem.TYPE) + " " +
				"AND " + alias1 + "." + SI.type(InstanceOfRefItem.ref) + "<>" + alias2 + "." + SI.type(InstanceOfAItem.TYPE),
				checkTypeColumn(InstanceOfRefItem.ref));
		assertFails(
				() -> checkTypeColumn(InstanceOfRefItem.refb2),
				IllegalArgumentException.class,
				"no check for type column needed for InstanceOfRefItem.refb2");
		assertFails(
				() -> checkTypeColumn(null),
				NullPointerException.class,
				"function");
		assertEquals(
				"SELECT COUNT(*) FROM " + SI.tab(InstanceOfAItem.TYPE) + " " +
				"LEFT JOIN " + SI.tab(InstanceOfB1Item.TYPE) + " " +
				"ON " + SI.pkq(InstanceOfAItem.TYPE) + "=" + SI.pkq(InstanceOfB1Item.TYPE) + " " +
				"WHERE " + SI.pkq(InstanceOfB1Item.TYPE) + " IS NULL " +
				"AND " + SI.typeq(InstanceOfAItem.TYPE) + "='InstanceOfB1Item'",
				checkCompleteness(InstanceOfAItem.TYPE, InstanceOfB1Item.TYPE));
		assertEquals(
				"SELECT COUNT(*) FROM " + SI.tab(InstanceOfB1Item.TYPE) + " " +
				"LEFT JOIN " + SI.tab(InstanceOfC1Item.TYPE) + " " +
				"ON " + SI.pkq(InstanceOfB1Item.TYPE) + "=" + SI.pkq(InstanceOfC1Item.TYPE) + " " +
				"WHERE " + SI.pkq(InstanceOfC1Item.TYPE) + " IS NULL " +
				"AND " + SI.typeq(InstanceOfB1Item.TYPE) + "='InstanceOfC1Item'",
				checkCompleteness(InstanceOfB1Item.TYPE, InstanceOfC1Item.TYPE));
		assertFails(
				() -> checkCompleteness(null, null),
				NullPointerException.class,
				"type");
		assertFails(
				() -> checkCompleteness(InstanceOfAItem.TYPE, null),
				NullPointerException.class,
				"subType");
		assertFails(
				() -> checkCompleteness(InstanceOfAItem.TYPE, InstanceOfAItem.TYPE),
				IllegalArgumentException.class,
				"expected instantiable subtype of InstanceOfAItem, " +
				"but was InstanceOfAItem");
	}

	@SuppressWarnings({"unchecked","rawtypes"}) // OK: testing unchecked usage of api
	@Test void testUnchecked()
	{
		assertFails(
				() -> checkCompleteness(InstanceOfB1Item.TYPE, (Type)InstanceOfAItem.TYPE),
				IllegalArgumentException.class,
				"expected instantiable subtype of InstanceOfB1Item, " +
				"but was InstanceOfAItem");
		assertFails(
				() -> checkCompleteness(InstanceOfB1Item.TYPE, (Type)InstanceOfB2Item.TYPE),
				IllegalArgumentException.class,
				"expected instantiable subtype of InstanceOfB1Item, " +
				"but was InstanceOfB2Item");
	}
}
