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
import static com.exedio.cope.SchemaInfo.getTableName;

import java.io.File;

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

	@Override
	public ConnectProperties getProperties()
	{
		final File dpf = ConnectProperties.getDefaultPropertyFile();
		final java.util.Properties dp = ConnectProperties.loadProperties(dpf);
		
		dp.setProperty("database.forcename.NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem", "NameCollisionlongAItem_F");
		dp.setProperty("database.forcename.NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem_code_Unq", "NameCollisionA_code_Unq_F");
		dp.setProperty("database.forcename.NameCollisionlongAItem_F.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber", "collisionlongANumber_F");
		dp.setProperty("database.forcename.NaLoNaLoNaLoNaLoNaLoNaLoI_pointerLoooooooooooooName_Ck", "NmeLngIm_pointrLngNme_Ck");
		dp.setProperty("database.forcename.NaLoNaLoNaLoNaLoNaLoNaLoI_code_Ck", "NmeLngIm_code_Ck");
		
		return new ConnectProperties(dp, dpf.getAbsolutePath()+" plus NameTest forced names", ConnectProperties.getSystemPropertyContext());
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
		
		// test schema
		
		assertEquals(mysqlLower("NaLoNaLoNaLoNaLoNaLoNaLoI"), SchemaInfo.getTableName(NameLongItem_TYPE));
		assertEquals("this", SchemaInfo.getPrimaryKeyColumnName(NameLongItem_TYPE));
		assertEquals("class", SchemaInfo.getTypeColumnName(NameLongItem_TYPE));
		assertEquals("code", SchemaInfo.getColumnName(NameLongItem_code));
		assertEquals("codeLooooooooooooooooName", SchemaInfo.getColumnName(NameLongItem_codeLongName));
		assertEquals("pointerLoooooooooooooName", SchemaInfo.getColumnName(NameLongItem_pointerLongName));
		assertEquals("pointerLoooooooooNameType", SchemaInfo.getTypeColumnName(NameLongItem_pointerLongName));
		
		assertEquals(mysqlLower("NameCollisionlongAItem_F"), SchemaInfo.getTableName(NameCollisionlongaItem_TYPE));
		assertEquals("this", SchemaInfo.getPrimaryKeyColumnName(NameCollisionlongaItem_TYPE));
		assertEquals("code", SchemaInfo.getColumnName(NameCollisionlongaItem_code));
		assertEquals("collisionlongANumber_F", SchemaInfo.getColumnName(NameCollisionlongaItem_collisionlongaNumber));
		assertEquals("collisionloooooooooNumber", SchemaInfo.getColumnName(NameCollisionlongaItem_collisionlongbNumber));
		
		assertEquals(mysqlLower("NameCollisionloooooooItem"), SchemaInfo.getTableName(NameCollisionlongbItem_TYPE));
		assertEquals("this", SchemaInfo.getPrimaryKeyColumnName(NameCollisionlongbItem_TYPE));
		assertEquals("code", SchemaInfo.getColumnName(NameCollisionlongbItem_code));
		
		// test persistence
		
		assertEquals("long name item", item.getCode());
		assertEquals(null, item.getPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName());

		item.setPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName(item);
		assertEquals(item, item.getPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName());

		item.setPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName(null);
		assertEquals(null, item.getPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName());

		item.setCodeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName("long name item");
		assertEquals(item, item.forCodeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName("long name item"));
		
		assertEquals(mysqlLower("NameCollisionlongAItem_F"), getTableName(itemca.TYPE));
		assertEquals("collisionlongANumber_F", getColumnName(itemca.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber));
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
}
