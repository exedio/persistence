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

import java.io.File;


public class NameTest extends AbstractLibTest
{
	static final Model MODEL = new Model(
			NameLongNameLongNameLongNameLongNameLongNameLongItem.TYPE,
			NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem.TYPE,
			NameCollisionlooooooooooooooooooooooooooooooooooooooooongbItem.TYPE);
	
	public NameTest()
	{
		super(MODEL);
	}

	@Override
	public Properties getProperties()
	{
		final File dpf = Properties.getDefaultPropertyFile();
		final java.util.Properties dp = Properties.loadProperties(dpf);
		
		dp.setProperty("database.forcename.NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem", "NameCollisionlongAItem_F");
		dp.setProperty("database.forcename.NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem_code_Unq", "NameCollisionA_code_Unq_F");
		dp.setProperty("database.forcename.NameCollisionlongAItem_F.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber", "collisionlongANumber_F");
		dp.setProperty("database.forcename.NaLoNaLoNaLoNaLoNaLoNaLoI_pointerLoooooooooooooName_Ck", "NmeLngIm_pointrLngNme_Ck");
		
		return new Properties(dp, dpf.getAbsolutePath()+" plus NameTest forced names");
	}
	
	NameLongNameLongNameLongNameLongNameLongNameLongItem item;
	NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem itemca, itemcb;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new NameLongNameLongNameLongNameLongNameLongNameLongItem("long name item"));
		deleteOnTearDown(itemca = new NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem("collision A"));
		deleteOnTearDown(itemcb = new NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem("collision B"));
	}
	
	public void test()
	{
		assertEquals("long name item", item.getCode());
		assertEquals(null, item.getPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName());

		item.setPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName(item);
		assertEquals(item, item.getPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName());

		item.setPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName(null);
		assertEquals(null, item.getPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName());

		item.setCodeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName("long name item");
		assertEquals(item, item.findByCodeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName("long name item"));
		
		assertEquals("NameCollisionlongAItem_F", itemca.TYPE.getTableName());
		assertEquals("collisionlongANumber_F", itemca.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber.getColumnName());
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
