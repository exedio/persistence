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
import java.util.Enumeration;
import java.util.HashSet;

import junit.framework.Test;
import junit.framework.TestSuite;

public class Main
{

	private static final void tearDown(final Model model)
	{
		//System.out.println("teardown " + model.getTypes());
		final File dpf = Properties.getDefaultPropertyFile();
		final java.util.Properties dp = Properties.loadProperties(dpf);
		
		dp.setProperty("database.forcename.StringItem", "STRINGITEMS");
		dp.setProperty("database.forcename.STRINGITEMS.this", "STRINGITEM_ID");
		dp.setProperty("database.forcename.STRINGITEMS.any", "ANY");
		dp.setProperty("database.forcename.STRINGITEMS.mandatory", "MANDATORY");
		dp.setProperty("database.forcename.STRINGITEMS.min4", "MIN_4");
		dp.setProperty("database.forcename.STRINGITEMS.max4", "MAX_4");
		dp.setProperty("database.forcename.STRINGITEMS.min4Max8", "MIN4_MAX8");
		dp.setProperty("database.forcename.STRINGITEMS.exact6", "EXACT_6");
		dp.setProperty("database.forcename.ItemWithSingleUnique", "UNIQUE_ITEMS");
		dp.setProperty("database.forcename.UNIQUE_ITEMS.this", "UNIQUE_ITEM_ID");
		dp.setProperty("database.forcename.UNIQUE_ITEMS.uniqueString", "UNIQUE_STRING");
		dp.setProperty("database.forcename.UNIQUE_ITEMS.otherString", "OTHER_STRING");
		dp.setProperty("database.forcename.ItemWithSingleUnique_uniqueString_Unq", "IX_ITEMWSU_US");
		dp.setProperty("database.forcename.NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem", "NameCollisionlongAItem_F");
		dp.setProperty("database.forcename.NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem_code_Unq", "NameCollisionA_code_Unq_F");
		dp.setProperty("database.forcename.NameCollisionlongAItem_F.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber", "collisionlongANumber_F");
		dp.setProperty("database.forcename.DefaultToItem_dateEighty_Ck", "DefltToItm_dateEighty_Ck");
		dp.setProperty("database.forcename.NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem", "NameCollisionlongAItem_F");
		dp.setProperty("database.forcename.NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem_code_Unq", "NameCollisionA_code_Unq_F");
		dp.setProperty("database.forcename.NameCollisionlongAItem_F.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber", "collisionlongANumber_F");
		dp.setProperty("database.forcename.NaLoNaLoNaLoNaLoNaLoNaLoI_pointerLoooooooooooooName_Ck", "NmeLngIm_pointrLngNme_Ck");
		
		model.connect(new Properties(dp, dpf.getAbsolutePath()+" plus teardown forced names"));
		model.tearDownDatabase();
	}
	
	private static final void collectModels(final TestSuite suite, final HashSet<Model> models)
	{
		for(Enumeration e = suite.tests(); e.hasMoreElements(); )
		{
			final Test test = (Test)e.nextElement();

			if(test instanceof com.exedio.cope.junit.CopeTest)
				models.add(((com.exedio.cope.junit.CopeTest)test).model);
			else if(test instanceof TestSuite)
				collectModels((TestSuite)test, models);
		}
	}
	
	public static void main(String[] args)
	{
		final HashSet<Model> models = new HashSet<Model>();
		collectModels(PackageTest.suite(), models);
		for(final Model m : models)
			tearDown(m);
	}
}
