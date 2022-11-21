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
