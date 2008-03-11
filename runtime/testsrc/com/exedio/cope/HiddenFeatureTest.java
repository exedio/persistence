/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
	public/*for web.xml*/ static final Model MODEL = new Model(HiddenFeatureSuperItem.TYPE, HiddenFeatureSubItem.TYPE);

	public HiddenFeatureTest()
	{
		super(MODEL);
	}
	
	public void testHierarchy()
	{
		HiddenFeatureSuperItem sp = null;
		HiddenFeatureSubItem sb = null;
		final Type.This spt = sp.TYPE.getThis();
		final Type.This sbt = sb.TYPE.getThis();

		// test model
		assertNotSame(sp.hiddenSame,  sb.hiddenSame);
		assertNotSame(sp.hiddenOther, sb.hiddenOther);
		
		assertEquals(list(spt, sp.nonHiddenSuper, sp.hiddenSame, sp.hiddenOther), sp.TYPE.getDeclaredFeatures());
		assertEquals(list(spt, sp.nonHiddenSuper, sp.hiddenSame, sp.hiddenOther), sp.TYPE.getFeatures());
		assertEquals(list(sp.nonHiddenSuper, sp.hiddenSame, sp.hiddenOther), sp.TYPE.getDeclaredFields());
		assertEquals(list(sp.nonHiddenSuper, sp.hiddenSame, sp.hiddenOther), sp.TYPE.getFields());
		assertSame(sp.TYPE, spt.getType());
		assertSame(sp.TYPE, sp.nonHiddenSuper.getType());
		assertSame(sp.TYPE, sp.hiddenSame.getType());
		assertSame(sp.TYPE, sp.hiddenOther.getType());
		assertEquals("this",           spt.getName());
		assertEquals("nonHiddenSuper", sp.nonHiddenSuper.getName());
		assertEquals("hiddenSame",     sp.hiddenSame.getName());
		assertEquals("hiddenOther",    sp.hiddenOther.getName());
		assertSame(spt,               sp.TYPE.getDeclaredFeature("this"));
		assertSame(sp.nonHiddenSuper, sp.TYPE.getDeclaredFeature("nonHiddenSuper"));
		assertSame(sp.hiddenSame,     sp.TYPE.getDeclaredFeature("hiddenSame"));
		assertSame(sp.hiddenOther,    sp.TYPE.getDeclaredFeature("hiddenOther"));
		assertSame(null,              sp.TYPE.getDeclaredFeature("nonHiddenSub"));
		assertSame(spt,               sp.TYPE.getFeature("this"));
		assertSame(sp.nonHiddenSuper, sp.TYPE.getFeature("nonHiddenSuper"));
		assertSame(sp.hiddenSame,     sp.TYPE.getFeature("hiddenSame"));
		assertSame(sp.hiddenOther,    sp.TYPE.getFeature("hiddenOther"));
		assertSame(null,              sp.TYPE.getFeature("nonHiddenSub"));

		assertEquals(list(sbt, sb.nonHiddenSub, sb.hiddenSame, sb.hiddenOther), sb.TYPE.getDeclaredFeatures());
		assertEquals(list(sbt, sp.nonHiddenSuper, sp.hiddenSame, sp.hiddenOther, sb.nonHiddenSub, sb.hiddenSame, sb.hiddenOther), sb.TYPE.getFeatures());
		assertEquals(list(sb.nonHiddenSub, sb.hiddenSame, sb.hiddenOther), sb.TYPE.getDeclaredFields());
		assertEquals(list(sp.nonHiddenSuper, sp.hiddenSame, sp.hiddenOther, sb.nonHiddenSub, sb.hiddenSame, sb.hiddenOther), sb.TYPE.getFields());
		assertSame(sb.TYPE, sbt.getType());
		assertSame(sb.TYPE, sb.nonHiddenSub.getType());
		assertSame(sb.TYPE, sb.hiddenSame.getType());
		assertSame(sb.TYPE, sb.hiddenOther.getType());
		assertEquals("this", sbt.getName());
		assertEquals("nonHiddenSub", sb.nonHiddenSub.getName());
		assertEquals("hiddenSame", sb.hiddenSame.getName());
		assertEquals("hiddenOther", sb.hiddenOther.getName());
		assertSame(sbt,               sb.TYPE.getDeclaredFeature("this"));
		assertSame(null,              sb.TYPE.getDeclaredFeature("nonHiddenSuper"));
		assertSame(sb.hiddenSame,     sb.TYPE.getDeclaredFeature("hiddenSame"));
		assertSame(sb.hiddenOther,    sb.TYPE.getDeclaredFeature("hiddenOther"));
		assertSame(sb.nonHiddenSub,   sb.TYPE.getDeclaredFeature("nonHiddenSub"));
		assertSame(sbt,               sb.TYPE.getFeature("this"));
		assertSame(sp.nonHiddenSuper, sb.TYPE.getFeature("nonHiddenSuper"));
		assertSame(sb.hiddenSame,     sb.TYPE.getFeature("hiddenSame"));
		assertSame(sb.hiddenOther,    sb.TYPE.getFeature("hiddenOther"));
		assertSame(sb.nonHiddenSub,   sb.TYPE.getFeature("nonHiddenSub"));

		assertEquals("HiddenFeatureSuperItem.this",           spt.getID());
		assertEquals("HiddenFeatureSuperItem.nonHiddenSuper", sp.nonHiddenSuper.getID());
		assertEquals("HiddenFeatureSuperItem.hiddenSame",     sp.hiddenSame.getID());
		assertEquals("HiddenFeatureSuperItem.hiddenOther",    sp.hiddenOther.getID());
		assertEquals("HiddenFeatureSubItem.this",         sbt.getID());
		assertEquals("HiddenFeatureSubItem.nonHiddenSub", sb.nonHiddenSub.getID());
		assertEquals("HiddenFeatureSubItem.hiddenSame",   sb.hiddenSame.getID());
		assertEquals("HiddenFeatureSubItem.hiddenOther",  sb.hiddenOther.getID());
		assertSame(spt,               model.getFeature("HiddenFeatureSuperItem.this"));
		assertSame(sp.nonHiddenSuper, model.getFeature("HiddenFeatureSuperItem.nonHiddenSuper"));
		assertSame(sp.hiddenSame,     model.getFeature("HiddenFeatureSuperItem.hiddenSame"));
		assertSame(sp.hiddenOther,    model.getFeature("HiddenFeatureSuperItem.hiddenOther"));
		assertSame(sbt,             model.getFeature("HiddenFeatureSubItem.this"));
		assertSame(sb.nonHiddenSub, model.getFeature("HiddenFeatureSubItem.nonHiddenSub"));
		assertSame(sb.hiddenSame,   model.getFeature("HiddenFeatureSubItem.hiddenSame"));
		assertSame(sb.hiddenOther,  model.getFeature("HiddenFeatureSubItem.hiddenOther"));
		assertSame(null, model.getFeature("HiddenFeatureSuperItem.hiddenOtherx"));
		assertSame(null, model.getFeature("HiddenFeatureSuperItemx.hiddenOther"));
		assertSame(null, model.getFeature("HiddenFeatureSuperItemhiddenOther"));
		assertSame(null, model.getFeature("HiddenFeatureSuperItem.nonHiddenSub"));
		assertSame(null, model.getFeature("HiddenFeatureSubItem.nonHiddenSuper"));

		assertEquals(map(
				HiddenFeatureSubItem.hiddenOther, HiddenFeatureSuperItem.hiddenOther,
				HiddenFeatureSubItem.hiddenSame, HiddenFeatureSuperItem.hiddenSame),
				model.getHiddenFeatures());

		// test persistence
		sp = deleteOnTearDown(new HiddenFeatureSuperItem());
		sp.setHiddenSame("hiddenSameSuperSuper");
		sp.setHiddenOther("hiddenOtherSuperSuper");
		sb = deleteOnTearDown(new HiddenFeatureSubItem());
		sb.setHiddenSame("hiddenSameSuperSub");
		sb.setHiddenOther("hiddenOtherSuperSub");
		sb.hiddenSame.set(sb, "hiddenSameSub");
		sb.hiddenOther.set(sb, 55);

		restartTransaction();
		assertEquals("hiddenSameSuperSuper", sp.getHiddenSame());
		assertEquals("hiddenOtherSuperSuper", sp.getHiddenOther());
		assertEquals("hiddenSameSuperSub", sb.getHiddenSame());
		assertEquals("hiddenOtherSuperSub", sb.getHiddenOther());
		assertEquals("hiddenSameSub", sb.hiddenSame.get(sb));
		assertEquals(new Integer(55), sb.hiddenOther.get(sb));
		
		assertContains(sp, sp.TYPE.search(sp.hiddenSame.equal("hiddenSameSuperSuper")));
		assertContains(sp, sp.TYPE.search(sp.hiddenOther.equal("hiddenOtherSuperSuper")));
		assertContains(sb, sp.TYPE.search(sp.hiddenSame.equal("hiddenSameSuperSub")));
		assertContains(sb, sp.TYPE.search(sp.hiddenOther.equal("hiddenOtherSuperSub")));

		if(!noJoinParentheses)
		{
			assertContains(sb.TYPE.search(sp.hiddenSame.equal("hiddenSameSuperSuper")));
			assertContains(sb.TYPE.search(sp.hiddenOther.equal("hiddenOtherSuperSuper")));
			assertContains(sb, sb.TYPE.search(sp.hiddenSame.equal("hiddenSameSuperSub")));
			assertContains(sb, sb.TYPE.search(sp.hiddenOther.equal("hiddenOtherSuperSub")));
		}

		assertContains(sb, sb.TYPE.search(sb.hiddenSame.equal("hiddenSameSub")));
		assertContains(sb, sb.TYPE.search(sb.hiddenOther.equal(55)));
	}
	
}
