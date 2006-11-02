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

import com.exedio.cope.pattern.CustomItem;
import com.exedio.cope.pattern.DAttribute;
import com.exedio.cope.pattern.DEnumValue;
import com.exedio.cope.pattern.DType;
import com.exedio.cope.pattern.DTypeItem;
import com.exedio.cope.pattern.FieldListItem;
import com.exedio.cope.pattern.FieldListLimitedItem;
import com.exedio.cope.pattern.FieldMapItem;
import com.exedio.cope.pattern.FieldMapLimitedItem;
import com.exedio.cope.pattern.FieldSetItem;
import com.exedio.cope.pattern.HashItem;
import com.exedio.cope.pattern.JavaViewItem;
import com.exedio.cope.pattern.MD5Item;
import com.exedio.cope.pattern.MediaItem;
import com.exedio.cope.pattern.RelationItem;
import com.exedio.cope.pattern.RelationSelfItem;
import com.exedio.cope.pattern.RelationSourceItem;
import com.exedio.cope.pattern.RelationTargetItem;
import com.exedio.cope.pattern.SerializerItem;
import com.exedio.cope.pattern.ThumbnailItem;
import com.exedio.cope.pattern.VectorRelationItem;


public class Main
{
	public static final Model itemSerializationModel = new Model(ItemSerializationItem.TYPE);
	public static final Model deleteModel = new Model(DeleteItem.TYPE, DeleteOtherItem.TYPE);
	public static final Model deleteHierarchyModel = new Model(DeleteHierarchySource.TYPE, DeleteHierarchyTargetSuper.TYPE, DeleteHierarchyTargetSub.TYPE);
	public static final Model defaultToModel = new Model(DefaultToItem.TYPE);
	public static final Model enumModel = new Model(EnumItem.TYPE, EnumItem2.TYPE);
	public static final Model dayModel = new Model(DayItem.TYPE);
	public static final Model dataModel = new Model(DataItem.TYPE, DataSubItem.TYPE);
	public static final Model mediaModel = new Model(MediaItem.TYPE);
	public static final Model thumbnailModel = new Model(ThumbnailItem.TYPE);
	public static final Model hashModel = new Model(HashItem.TYPE);
	public static final Model md5Model = new Model(MD5Item.TYPE);
	public static final Model fieldListLimitedModel = new Model(FieldListLimitedItem.TYPE);
	public static final Model fieldListModel = new Model(FieldListItem.TYPE);
	public static final Model fieldSetModel = new Model(FieldSetItem.TYPE);
	public static final Model fieldMapLimitedModel = new Model(FieldMapLimitedItem.TYPE);
	public static final Model fieldMapModel = new Model(FieldMapItem.TYPE);
	public static final Model serializerModel = new Model(SerializerItem.TYPE);
	public static final Model customModel = new Model(CustomItem.TYPE, JavaViewItem.TYPE);
	public static final Model cacheIsolationModel = new Model(CacheIsolationItem.TYPE);
	public static final Model compareConditionModel = new Model(CompareConditionItem.TYPE);
	public static final Model compareFunctionConditionModel = new Model(CompareFunctionConditionItem.TYPE);
	public static final Model typeInConditionModel = new Model(TypeInConditionAItem.TYPE, TypeInConditionB1Item.TYPE, TypeInConditionB2Item.TYPE, TypeInConditionC1Item.TYPE, TypeInConditionRefItem.TYPE);
	public static final Model relationModel = new Model(RelationItem.TYPE, RelationSelfItem.TYPE, VectorRelationItem.TYPE, RelationSourceItem.TYPE, RelationTargetItem.TYPE);
	public static final Model nameModel = new Model(
			NameLongNameLongNameLongNameLongNameLongNameLongItem.TYPE,
			NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem.TYPE,
			NameCollisionlooooooooooooooooooooooooooooooooooooooooongbItem.TYPE);
	public static final Model matchModel = new Model(MatchItem.TYPE);
	public static final Model hierarchyModel = new Model(
			HierarchyFirstSub.TYPE,
			HierarchySecondSub.TYPE,
			HierarchySuper.TYPE, // deliberately put this type below it's sub types to test correct functionality
			HierarchySingleSuper.TYPE,
			HierarchySingleSub.TYPE
		);
	public static final Model hierarchyEmptyModel = new Model(HierarchyEmptySub.TYPE, HierarchyEmptySuper.TYPE);
	public static final Model joinFunctionModel = new Model(JoinFunctionItem.TYPE, JoinFunctionItemSingle.TYPE);
	public static final Model hardJoinModel = new Model(HardJoinA1Item.TYPE, HardJoinA2Item.TYPE, HardJoinA3Item.TYPE, HardJoinB1Item.TYPE, HardJoinB2Item.TYPE, HardJoinB3Item.TYPE);
	public static final Model dtypeModel = new Model(DType.TYPE, DAttribute.TYPE, DTypeItem.TYPE, DEnumValue.TYPE);
	public static final Model hiddenFeatureModel = new Model(HiddenFeatureSuperItem.TYPE, HiddenFeatureSubItem.TYPE);

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

			if(test instanceof AbstractLibTest)
				models.add(((AbstractLibTest)test).model);
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
