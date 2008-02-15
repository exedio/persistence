/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

public class NameTest extends AbstractLibTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(
			NameLongNameLongNameLongNameLongNameLongNameLongItem.TYPE,
			NameSubItem.TYPE,
			NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem.TYPE,
			NameCollisionlooooooooooooooooooooooooooooooooooooooooongbItem.TYPE);
	
	public NameTest()
	{
		super(MODEL);
	}
	
	NameLongNameLongNameLongNameLongNameLongNameLongItem item;
	NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem itemca, itemcb;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new NameLongNameLongNameLongNameLongNameLongNameLongItem("long name item"));
		itemca = deleteOnTearDown(new NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem("collision A"));
		itemcb = deleteOnTearDown(new NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem("collision B"));
	}
	
	public void test()
	{
		final Type NameLongItem_TYPE = NameLongNameLongNameLongNameLongNameLongNameLongItem.TYPE;
		final Field NameLongItem_code = NameLongNameLongNameLongNameLongNameLongNameLongItem.code;
		final Field NameLongItem_codeLongName =
			NameLongNameLongNameLongNameLongNameLongNameLongItem.codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName;
		final ItemField NameLongItem_pointerLongName =
			NameLongNameLongNameLongNameLongNameLongNameLongItem.pointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName;
		
		final Type NameCollisionlongaItem_TYPE = NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem.TYPE;
		final Field NameCollisionlongaItem_code = NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem.code;
		final Field NameCollisionlongaItem_collisionlongaNumber =
			NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber;
		final Field NameCollisionlongaItem_collisionlongbNumber =
			NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem.collisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber;
		
		final Type NameCollisionlongbItem_TYPE = NameCollisionlooooooooooooooooooooooooooooooooooooooooongbItem.TYPE;
		final Field NameCollisionlongbItem_code =
			NameCollisionlooooooooooooooooooooooooooooooooooooooooongbItem.code;
		
		// test model
		
		assertEquals("NameLongNameLongNameLongNameLongNameLongNameLongItem", NameLongItem_TYPE.getID());
		assertEquals("this", NameLongItem_TYPE.getThis().getName());
		assertEquals("code", NameLongItem_code.getName());
		assertEquals("codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName", NameLongItem_codeLongName.getName());
		assertEquals("pointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName", NameLongItem_pointerLongName.getName());
		
		assertEquals("NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem", NameCollisionlongaItem_TYPE.getID());
		assertEquals("this", NameCollisionlongaItem_TYPE.getThis().getName());
		assertEquals("code", NameCollisionlongaItem_code.getName());
		assertEquals("collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber", NameCollisionlongaItem_collisionlongaNumber.getName());
		assertEquals("collisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber", NameCollisionlongaItem_collisionlongbNumber.getName());
		
		assertEquals("NameCollisionlooooooooooooooooooooooooooooooooooooooooongbItem", NameCollisionlongbItem_TYPE.getID());
		assertEquals("this", NameCollisionlongbItem_TYPE.getThis().getName());
		assertEquals("code", NameCollisionlongbItem_code.getName());
		
		assertEquals("NameSubItemX", NameSubItem.TYPE.getID());
		assertEquals("this", NameSubItem.TYPE.getThis().getName());
		
		// test schema
		
		assertEquals(mysqlLower("NameLongItem"), getTableName(NameLongItem_TYPE));
		assertEquals("this", getPrimaryKeyColumnName(NameLongItem_TYPE));
		assertEquals("class", getTypeColumnName(NameLongItem_TYPE));
		assertEquals("code", getColumnName(NameLongItem_code));
		assertEquals("codeLooooooooooooooooName", getColumnName(NameLongItem_codeLongName));
		assertEquals("pointerLoooooooooooooName", getColumnName(NameLongItem_pointerLongName));
		assertEquals("pointerLoooooooooNameType", getTypeColumnName(NameLongItem_pointerLongName));
		
		assertEquals(mysqlLower("NameCollisionloooooooItem"), getTableName(NameCollisionlongaItem_TYPE));
		assertEquals("this", getPrimaryKeyColumnName(NameCollisionlongaItem_TYPE));
		assertEquals("code", getColumnName(NameCollisionlongaItem_code));
		assertEquals("collisionlongANumber", getColumnName(NameCollisionlongaItem_collisionlongaNumber));
		assertEquals("collisionlongBNumber", getColumnName(NameCollisionlongaItem_collisionlongbNumber));
		
		assertEquals(mysqlLower("NameCollisionlongBItem"), getTableName(NameCollisionlongbItem_TYPE));
		assertEquals("this", getPrimaryKeyColumnName(NameCollisionlongbItem_TYPE));
		assertEquals("code", getColumnName(NameCollisionlongbItem_code));
		
		{
			final Schema schema = model.getVerifiedSchema();
			final com.exedio.dsmf.Table nameSub = schema.getTable(getTableName(NameSubItem.TYPE));
			assertNotNull(nameSub);
			assertEquals(null, nameSub.getError());
			assertEquals(Schema.Color.OK, nameSub.getParticularColor());
			
			assertEquals("this",    nameSub.getColumn("this")   .getName());
			assertEquals("unique",  nameSub.getColumn("unique") .getName());
			assertEquals("integer", nameSub.getColumn("integer").getName());
			assertEquals("item",    nameSub.getColumn("item")   .getName());
			assertPkConstraint    (nameSub, "NameSubItemX_Pk",           null, getPrimaryKeyColumnName(NameSubItem.TYPE));
			assertUniqueConstraint(nameSub, "NameSubItemX_unique_Unq",   "("+p("unique")+")");
			assertFkConstraint    (nameSub, "NameSubItemX_item_Fk",      "item", mysqlLower("NameSubItemX"), getPrimaryKeyColumnName(NameSubItem.TYPE));
			assertUniqueConstraint(nameSub, "NameSubItemX_integers_Unq", "("+p("integer")+","+p("item")+")");
			assertCheckConstraint (nameSub, "NameSubItemX_this_CkPk",    "("+p("this")+">=0) AND ("+p("this")+"<=2147483647)");
			assertCheckConstraint (nameSub, "NameSubItemX_unique_Ck",    "("+p("unique")+" IS NOT NULL) AND (("+p("unique")+">=-2147483648) AND ("+p("unique")+"<=2147483647))");
			assertCheckConstraint (nameSub, "NameSubItemX_integer_Ck",   "("+p("integer")+" IS NOT NULL) AND (("+p("integer")+">=-2147483648) AND ("+p("integer")+"<=2147483647))");
			assertCheckConstraint (nameSub, "NameSubItemX_item_Ck",      "("+p("item")+" IS NOT NULL) AND (("+p("item")+">=0) AND ("+p("item")+"<=2147483647))");
		}
		
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
	
	private final String p(final String name)
	{
		return model.getDatabase().getDriver().protectName(name);
	}
}
