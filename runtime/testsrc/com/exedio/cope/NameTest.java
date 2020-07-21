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
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.getTypeColumnName;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.dsmf.Dialect.NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.dsmf.Node;
import com.exedio.dsmf.Schema;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class NameTest extends TestWithEnvironment
{
	public static final Model MODEL = new Model(
			NameLongItem.TYPE,
			NameSubItem.TYPE,
			NameCollisionlongaItem.TYPE,
			NameCollisionlongbItem.TYPE);

	public NameTest()
	{
		super(MODEL);
	}

	NameLongItem item;
	NameCollisionlongaItem itemca, itemcb;

	@BeforeEach final void setUp()
	{
		item = new NameLongItem("long name item");
		itemca = new NameCollisionlongaItem("collision A");
		itemcb = new NameCollisionlongaItem("collision B");
	}

	@Test void test()
	{
		final StringField NameLongItem_codeLongName =
			NameLongItem.codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName;
		final ItemField<NameLongItem> NameLongItem_pointerLongName =
			NameLongItem.pointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName;

		final IntegerField NameCollisionlongaItem_collisionlongaNumber =
			NameCollisionlongaItem.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber;
		final IntegerField NameCollisionlongaItem_collisionlongbNumber =
			NameCollisionlongaItem.collisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber;

		// test model

		assertEquals("NameLongNameLongNameLongNameLongNameLongNameLongItem", NameLongItem.TYPE.getID());
		assertEquals("this", NameLongItem.TYPE.getThis().getName());
		assertEquals("code", NameLongItem.code.getName());
		assertEquals("codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName", NameLongItem_codeLongName.getName());
		assertEquals("pointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName", NameLongItem_pointerLongName.getName());

		assertEquals("NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem", NameCollisionlongaItem.TYPE.getID());
		assertEquals("this", NameCollisionlongaItem.TYPE.getThis().getName());
		assertEquals("code", NameCollisionlongaItem.code.getName());
		assertEquals("collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber", NameCollisionlongaItem_collisionlongaNumber.getName());
		assertEquals("collisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber", NameCollisionlongaItem_collisionlongbNumber.getName());

		assertEquals("NameCollisionlooooooooooooooooooooooooooooooooooooooooongbItem", NameCollisionlongbItem.TYPE.getID());
		assertEquals("this", NameCollisionlongbItem.TYPE.getThis().getName());
		assertEquals("code", NameCollisionlongbItem.code.getName());

		assertEquals("Sub" , NameSubItem.TYPE.getID());
		assertEquals("this", NameSubItem.TYPE.getThis().getName());

		// test schema

		assertEquals(filterTableName("NameLongItem"), getTableName(NameLongItem.TYPE));
		assertEquals(synthetic("this", "NameLongItem"), getPrimaryKeyColumnName(NameLongItem.TYPE));
		assertPrimaryKeySequenceName("NameLongItem_this_Seq", NameLongItem.TYPE);
		assertEquals(synthetic("class", "NameLongItem"), getTypeColumnName(NameLongItem.TYPE));
		assertEquals("code", getColumnName(NameLongItem.code));
		assertEquals("codeLooooooooooooooooName", getColumnName(NameLongItem_codeLongName));
		assertEquals("pointerLoooooooooooooName", getColumnName(NameLongItem_pointerLongName));
		assertEquals("pointerLoooooooooNameType", getTypeColumnName(NameLongItem_pointerLongName));
		assertEquals(
				mysql && !propertiesLongConstraintNames()
				? "NameLongItem_poiLooNam_Fk"
				: "NameLongItem_pointerLooooooooooooooooooooooooooooooooName_Fk",
				getForeignKeyConstraintName(NameLongItem_pointerLongName));

		assertEquals(filterTableName("NameCollisionloooooooItem"), getTableName(NameCollisionlongaItem.TYPE));
		assertEquals(synthetic("this", "NameCollisionloooItem"), getPrimaryKeyColumnName(NameCollisionlongaItem.TYPE));
		assertPrimaryKeySequenceName("NameCollisioItem_this_Seq", "NameCollisiItem_this_Seq6", NameCollisionlongaItem.TYPE);
		assertEquals("code", getColumnName(NameCollisionlongaItem.code));
		assertEquals("collisionlongANumber", getColumnName(NameCollisionlongaItem_collisionlongaNumber));
		assertEquals("collisionlongBNumber", getColumnName(NameCollisionlongaItem_collisionlongbNumber));

		assertEquals(filterTableName("NameCollisionlongBItem"), getTableName(NameCollisionlongbItem.TYPE));
		assertEquals(synthetic("this", "NameCollisionlonBItem"), getPrimaryKeyColumnName(NameCollisionlongbItem.TYPE));
		assertPrimaryKeySequenceName("NameCollisiBItem_this_Seq", "NameCollisBItem_this_Seq6", NameCollisionlongbItem.TYPE);
		assertEquals("code", getColumnName(NameCollisionlongbItem.code));

		model.commit();

		{
			final Schema schema = model.getVerifiedSchema();
			final com.exedio.dsmf.Table nameSub = schema.getTable(getTableName(NameSubItem.TYPE));
			assertNotNull(nameSub);
			assertEquals(null, nameSub.getError());
			assertEquals(Node.Color.OK, nameSub.getParticularColor());

			assertEquals(synthetic("this", "Sub"), nameSub.getColumn(synthetic("this", "Sub"))   .getName());
			assertPkConstraint    (nameSub, "Sub_PK",      null, getPrimaryKeyColumnName(NameSubItem.TYPE));
			assertCheckConstraint (nameSub, "Sub_this_MN", q(synthetic("this", "Sub"))+">=0");
			assertCheckConstraint (nameSub, "Sub_this_MX", q(synthetic("this", "Sub"))+"<=2147483647");

			assertEquals("unique",  nameSub.getColumn("unique") .getName());
			assertEquals("integer", nameSub.getColumn("integer").getName());
			assertEquals("item",    nameSub.getColumn("item")   .getName());
			assertUniqueConstraint(nameSub, "Sub_unique_Unq",   "("+q("unique")+")");
			assertFkConstraint    (nameSub, "Sub_item_Fk",      "item", filterTableName("Sub"), getPrimaryKeyColumnName(NameSubItem.TYPE));
			assertUniqueConstraint(nameSub, "Sub_integers_Unq", "("+q("integer")+","+q("item")+")");
			assertCheckConstraint (nameSub, "Sub_unique_MN",    q("unique")+">=-2147483648");
			assertCheckConstraint (nameSub, "Sub_unique_MX",    q("unique")+"<=2147483647");
			assertCheckConstraint (nameSub, "Sub_integer_MN",   q("integer")+">=-2147483648");
			assertCheckConstraint (nameSub, "Sub_integer_MX",   q("integer")+"<=2147483647");
			assertCheckConstraint (nameSub, "Sub_item_MN",      q("item")+">=0");
			assertCheckConstraint (nameSub, "Sub_item_MX",      q("item")+"<=2147483647");

			assertEquals("uniqueY",  nameSub.getColumn("uniqueY") .getName());
			assertEquals("integerY", nameSub.getColumn("integerY").getName());
			assertEquals("itemY",    nameSub.getColumn("itemY")   .getName());
			assertUniqueConstraint(nameSub, "Sub_uniqueY_Unq",  "("+q("uniqueY")+")");
			assertFkConstraint    (nameSub, "Sub_itemY_Fk",     "itemY", filterTableName("Sub"), getPrimaryKeyColumnName(NameSubItem.TYPE));
			assertUniqueConstraint(nameSub, "Sub_integersY_Unq","("+q("integerY")+","+q("itemY")+")");
			assertCheckConstraint (nameSub, "Sub_uniqueY_MN",   q("uniqueY")+">=-2147483648");
			assertCheckConstraint (nameSub, "Sub_uniqueY_MX",   q("uniqueY")+"<=2147483647");
			assertCheckConstraint (nameSub, "Sub_integerY_MN",  q("integerY")+">=-2147483648");
			assertCheckConstraint (nameSub, "Sub_integerY_MX",  q("integerY")+"<=2147483647");
			assertCheckConstraint (nameSub, "Sub_itemY_MN",     q("itemY")+">=0");
			assertCheckConstraint (nameSub, "Sub_itemY_MX",     q("itemY")+"<=2147483647");

			assertEquals(null, nameSub.getColumn("unique").getError());
			assertEquals(null, nameSub.getColumn("uniqueY").getError());
			if(hsqldb)
			{
				assertEquals("INTEGER" + NOT_NULL, nameSub.getColumn("unique") .getType());
				assertEquals("INTEGER" + NOT_NULL, nameSub.getColumn("uniqueY").getType());
			}
		}

		startTransaction();

		// test persistence

		assertEquals("long name item", item.getCode());
		assertEquals(null, item.getPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName());

		item.setPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName(item);
		assertEquals(item, item.getPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName());

		item.setPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName(null);
		assertEquals(null, item.getPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName());

		item.setCodeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName("long name item");
		assertEquals(item, NameLongItem.forCodeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName("long name item"));

		assertEquals(null, itemca.getCollisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber());
		assertEquals(null, itemcb.getCollisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber());
		assertContains(itemca, itemcb, NameCollisionlongaItem.TYPE.search(NameCollisionlongaItem.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber.equal((Integer)null)));
		assertContains(NameCollisionlongaItem.TYPE.search(NameCollisionlongaItem.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber.equal(Integer.valueOf(5))));

		itemca.setCollisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber(Integer.valueOf(5));
		assertEquals(Integer.valueOf(5), itemca.getCollisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber());
		assertEquals(null, itemcb.getCollisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber());
		assertContains(itemcb, NameCollisionlongaItem.TYPE.search(NameCollisionlongaItem.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber.equal((Integer)null)));
		assertContains(itemca, NameCollisionlongaItem.TYPE.search(NameCollisionlongaItem.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber.equal(Integer.valueOf(5))));
	}

	private String q(final String name)
	{
		return SchemaInfo.quoteName(model, name);
	}
}
