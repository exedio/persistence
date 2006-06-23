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


public class HiddenFeatureTest extends AbstractLibTest
{
	public HiddenFeatureTest()
	{
		super(Main.hiddenFeatureModel);
	}
	
	public void testHierarchy()
	{
		final HiddenFeatureSuperItem sp = null;
		final HiddenFeatureSubItem sb = null;

		// test model
		assertNotSame(sp.hiddenSame,  sb.hiddenSame);
		assertNotSame(sp.hiddenOther, sb.hiddenOther);
		
		assertEquals(list(sp.nonHiddenSuper, sp.hiddenSame, sp.hiddenOther), sp.TYPE.getDeclaredFeatures());
		assertEquals(list(sp.nonHiddenSuper, sp.hiddenSame, sp.hiddenOther), sp.TYPE.getFeatures());
		assertEquals(list(sp.nonHiddenSuper, sp.hiddenSame, sp.hiddenOther), sp.TYPE.getDeclaredAttributes());
		assertEquals(list(sp.nonHiddenSuper, sp.hiddenSame, sp.hiddenOther), sp.TYPE.getAttributes());
		assertSame(sp.TYPE, sp.nonHiddenSuper.getType());
		assertSame(sp.TYPE, sp.hiddenSame.getType());
		assertSame(sp.TYPE, sp.hiddenOther.getType());
		assertEquals("nonHiddenSuper", sp.nonHiddenSuper.getName());
		assertEquals("hiddenSame",     sp.hiddenSame.getName());
		assertEquals("hiddenOther",    sp.hiddenOther.getName());
		assertSame(sp.nonHiddenSuper, sp.TYPE.getDeclaredFeature("nonHiddenSuper"));
		assertSame(sp.hiddenSame,     sp.TYPE.getDeclaredFeature("hiddenSame"));
		assertSame(sp.hiddenOther,    sp.TYPE.getDeclaredFeature("hiddenOther"));
		assertSame(null,              sp.TYPE.getDeclaredFeature("nonHiddenSub"));
		assertSame(sp.nonHiddenSuper, sp.TYPE.getFeature("nonHiddenSuper"));
		assertSame(sp.hiddenSame,     sp.TYPE.getFeature("hiddenSame"));
		assertSame(sp.hiddenOther,    sp.TYPE.getFeature("hiddenOther"));
		assertSame(null,              sp.TYPE.getFeature("nonHiddenSub"));

		assertEquals(list(sb.nonHiddenSub, sb.hiddenSame, sb.hiddenOther), sb.TYPE.getDeclaredFeatures());
		assertEquals(list(sp.nonHiddenSuper, sp.hiddenSame, sp.hiddenOther, sb.nonHiddenSub, sb.hiddenSame, sb.hiddenOther), sb.TYPE.getFeatures());
		assertEquals(list(sb.nonHiddenSub, sb.hiddenSame, sb.hiddenOther), sb.TYPE.getDeclaredAttributes());
		assertEquals(list(sp.nonHiddenSuper, sp.hiddenSame, sp.hiddenOther, sb.nonHiddenSub, sb.hiddenSame, sb.hiddenOther), sb.TYPE.getAttributes());
		assertSame(sb.TYPE, sb.nonHiddenSub.getType());
		assertSame(sb.TYPE, sb.hiddenSame.getType());
		assertSame(sb.TYPE, sb.hiddenOther.getType());
		assertEquals("nonHiddenSub", sb.nonHiddenSub.getName());
		assertEquals("hiddenSame", sb.hiddenSame.getName());
		assertEquals("hiddenOther", sb.hiddenOther.getName());
		assertSame(null,              sb.TYPE.getDeclaredFeature("nonHiddenSuper"));
		assertSame(sb.hiddenSame,     sb.TYPE.getDeclaredFeature("hiddenSame"));
		assertSame(sb.hiddenOther,    sb.TYPE.getDeclaredFeature("hiddenOther"));
		assertSame(sb.nonHiddenSub,   sb.TYPE.getDeclaredFeature("nonHiddenSub"));
		assertSame(sp.nonHiddenSuper, sb.TYPE.getFeature("nonHiddenSuper"));
		assertSame(sb.hiddenSame,     sb.TYPE.getFeature("hiddenSame"));
		assertSame(sb.hiddenOther,    sb.TYPE.getFeature("hiddenOther"));
		assertSame(sb.nonHiddenSub,   sb.TYPE.getFeature("nonHiddenSub"));

		assertEquals("HiddenFeatureSuperItem.nonHiddenSuper", sp.nonHiddenSuper.getID());
		assertEquals("HiddenFeatureSuperItem.hiddenSame",     sp.hiddenSame.getID());
		assertEquals("HiddenFeatureSuperItem.hiddenOther",    sp.hiddenOther.getID());
		assertEquals("HiddenFeatureSubItem.nonHiddenSub", sb.nonHiddenSub.getID());
		assertEquals("HiddenFeatureSubItem.hiddenSame",   sb.hiddenSame.getID());
		assertEquals("HiddenFeatureSubItem.hiddenOther",  sb.hiddenOther.getID());
		assertSame(sp.nonHiddenSuper, model.findFeatureByID("HiddenFeatureSuperItem.nonHiddenSuper"));
		assertSame(sp.hiddenSame,     model.findFeatureByID("HiddenFeatureSuperItem.hiddenSame"));
		assertSame(sp.hiddenOther,    model.findFeatureByID("HiddenFeatureSuperItem.hiddenOther"));
		assertSame(sb.nonHiddenSub, model.findFeatureByID("HiddenFeatureSubItem.nonHiddenSub"));
		assertSame(sb.hiddenSame,   model.findFeatureByID("HiddenFeatureSubItem.hiddenSame"));
		assertSame(sb.hiddenOther,  model.findFeatureByID("HiddenFeatureSubItem.hiddenOther"));
		assertSame(null, model.findFeatureByID("HiddenFeatureSuperItem.hiddenOtherx"));
		assertSame(null, model.findFeatureByID("HiddenFeatureSuperItemx.hiddenOther"));
		assertSame(null, model.findFeatureByID("HiddenFeatureSuperItemhiddenOther"));
		assertSame(null, model.findFeatureByID("HiddenFeatureSuperItem.nonHiddenSub"));
		assertSame(null, model.findFeatureByID("HiddenFeatureSubItem.nonHiddenSuper"));
	}
	
}
