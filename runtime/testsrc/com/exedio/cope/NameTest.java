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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.getTypeColumnName;

import com.exedio.dsmf.Schema;

public class NameTest extends AbstractRuntimeTest
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

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new NameLongItem("long name item"));
		itemca = deleteOnTearDown(new NameCollisionlongaItem("collision A"));
		itemcb = deleteOnTearDown(new NameCollisionlongaItem("collision B"));
	}

	public void test()
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

		assertEquals("NameSubItemX", NameSubItem.TYPE.getID());
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

		assertEquals(filterTableName("NameCollisionloooooooItem"), getTableName(NameCollisionlongaItem.TYPE));
		assertEquals(synthetic("this", "NameCollisionloooItem"), getPrimaryKeyColumnName(NameCollisionlongaItem.TYPE));
		assertPrimaryKeySequenceName("NameCollisioItem_this_Seq", NameCollisionlongaItem.TYPE);
		assertEquals("code", getColumnName(NameCollisionlongaItem.code));
		assertEquals("collisionlongANumber", getColumnName(NameCollisionlongaItem_collisionlongaNumber));
		assertEquals("collisionlongBNumber", getColumnName(NameCollisionlongaItem_collisionlongbNumber));

		assertEquals(filterTableName("NameCollisionlongBItem"), getTableName(NameCollisionlongbItem.TYPE));
		assertEquals(synthetic("this", "NameCollisionlonBItem"), getPrimaryKeyColumnName(NameCollisionlongbItem.TYPE));
		assertPrimaryKeySequenceName("NameCollisiBItem_this_Seq", NameCollisionlongbItem.TYPE);
		assertEquals("code", getColumnName(NameCollisionlongbItem.code));

		model.commit();

		if(!postgresql)
		{
			final Schema schema = model.getVerifiedSchema();
			final com.exedio.dsmf.Table nameSub = schema.getTable(getTableName(NameSubItem.TYPE));
			assertNotNull(nameSub);
			assertEquals(null, nameSub.getError());
			assertEquals(Schema.Color.OK, nameSub.getParticularColor());

			assertEquals(synthetic("this", "NameSubItemX"),    nameSub.getColumn(synthetic("this", "NameSubItemX"))   .getName());
			assertPkConstraint    (nameSub, "NameSubItemX_Pk",           null, getPrimaryKeyColumnName(NameSubItem.TYPE));
			assertCheckConstraint (nameSub, "NameSubItemX_this_CkPk",    "("+q(synthetic("this", "NameSubItemX"))+">=0) AND ("+q(synthetic("this", "NameSubItemX"))+"<=2147483647)");

			assertEquals("unique",  nameSub.getColumn("unique") .getName());
			assertEquals("integer", nameSub.getColumn("integer").getName());
			assertEquals("item",    nameSub.getColumn("item")   .getName());
			assertUniqueConstraint(nameSub, "NameSubItemX_unique_Unq",   "("+q("unique")+")");
			assertFkConstraint    (nameSub, "NameSubItemX_item_Fk",      "item", filterTableName("NameSubItemX"), getPrimaryKeyColumnName(NameSubItem.TYPE));
			assertUniqueConstraint(nameSub, "NameSubItemX_integers_Unq", "("+q("integer")+","+q("item")+")");
			assertCheckConstraint (nameSub, "NameSubItemX_unique_Ck",    notNull(q("unique"), "("+q("unique")+">=-2147483648) AND ("+q("unique")+"<=2147483647)"));
			assertCheckConstraint (nameSub, "NameSubItemX_integer_Ck",   notNull(q("integer"), "("+q("integer")+">=-2147483648) AND ("+q("integer")+"<=2147483647)"));
			assertCheckConstraint (nameSub, "NameSubItemX_item_Ck",      notNull(q("item"), "("+q("item")+">=0) AND ("+q("item")+"<=2147483647)"));

			assertEquals("uniqueY",  nameSub.getColumn("uniqueY") .getName());
			assertEquals("integerY", nameSub.getColumn("integerY").getName());
			assertEquals("itemY",    nameSub.getColumn("itemY")   .getName());
			assertUniqueConstraint(nameSub, "NameSubItemX_uniqueY_Unq",  "("+q("uniqueY")+")");
			assertFkConstraint    (nameSub, "NameSubItemX_itemY_Fk",     "itemY", filterTableName("NameSubItemX"), getPrimaryKeyColumnName(NameSubItem.TYPE));
			assertUniqueConstraint(nameSub, "NameSubItemX_integerY_Unq", "("+q("integerY")+","+q("itemY")+")");
			assertCheckConstraint (nameSub, "NameSubItemX_uniqueY_Ck",   notNull(q("uniqueY"), "("+q("uniqueY")+">=-2147483648) AND ("+q("uniqueY")+"<=2147483647)"));
			assertCheckConstraint (nameSub, "NameSubItemX_integerY_Ck",  notNull(q("integerY"), "("+q("integerY")+">=-2147483648) AND ("+q("integerY")+"<=2147483647)"));
			assertCheckConstraint (nameSub, "NameSubItemX_itemY_Ck",     notNull(q("itemY"), "("+q("itemY")+">=0) AND ("+q("itemY")+"<=2147483647)"));

			assertEquals(null, nameSub.getColumn("unique").getError());
			assertEquals(null, nameSub.getColumn("uniqueY").getError());
			if(hsqldb)
			{
				assertEquals("integer", nameSub.getColumn("unique") .getType());
				assertEquals("integer", nameSub.getColumn("uniqueY").getType());
			}
		}

		model.startTransaction();

		// test persistence

		assertEquals("long name item", item.getCode());
		assertEquals(null, item.getPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName());

		item.setPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName(item);
		assertEquals(item, item.getPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName());

		item.setPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName(null);
		assertEquals(null, item.getPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName());

		item.setCodeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName("long name item");
		assertEquals(item, item.forCodeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName("long name item"));

		assertEquals(null, itemca.getCollisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber());
		assertEquals(null, itemcb.getCollisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber());
		assertContains(itemca, itemcb, itemca.TYPE.search(itemca.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber.equal((Integer)null)));
		assertContains(itemca.TYPE.search(itemca.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber.equal(Integer.valueOf(5))));

		itemca.setCollisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber(Integer.valueOf(5));
		assertEquals(Integer.valueOf(5), itemca.getCollisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber());
		assertEquals(null, itemcb.getCollisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber());
		assertContains(itemcb, itemca.TYPE.search(itemca.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber.equal((Integer)null)));
		assertContains(itemca, itemca.TYPE.search(itemca.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber.equal(Integer.valueOf(5))));
	}

	private final String q(final String name)
	{
		return SchemaInfo.quoteName(model, name);
	}
}
