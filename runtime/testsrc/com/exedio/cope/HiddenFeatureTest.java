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

import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.misc.HiddenFeatures;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class HiddenFeatureTest extends TestWithEnvironment
{
	public static final Model MODEL = new Model(HiddenFeatureSuperItem.TYPE, HiddenFeatureSubItem.TYPE);

	public HiddenFeatureTest()
	{
		super(MODEL);
	}

	@Test void testHierarchy()
	{
		final This<?> spt = HiddenFeatureSuperItem.TYPE.getThis();
		final This<?> sbt = HiddenFeatureSubItem.TYPE.getThis();

		// test model
		assertNotSame(HiddenFeatureSuperItem.hiddenSame,  HiddenFeatureSubItem.hiddenSame);
		//noinspection AssertBetweenInconvertibleTypes
		assertNotSame(HiddenFeatureSuperItem.hiddenOther, HiddenFeatureSubItem.hiddenOther);

		assertEquals(list(spt, HiddenFeatureSuperItem.nonHiddenSuper, HiddenFeatureSuperItem.hiddenSame, HiddenFeatureSuperItem.hiddenOther), HiddenFeatureSuperItem.TYPE.getDeclaredFeatures());
		assertEquals(list(spt, HiddenFeatureSuperItem.nonHiddenSuper, HiddenFeatureSuperItem.hiddenSame, HiddenFeatureSuperItem.hiddenOther), HiddenFeatureSuperItem.TYPE.getFeatures());
		assertEquals(list(HiddenFeatureSuperItem.nonHiddenSuper, HiddenFeatureSuperItem.hiddenSame, HiddenFeatureSuperItem.hiddenOther), HiddenFeatureSuperItem.TYPE.getDeclaredFields());
		assertEquals(list(HiddenFeatureSuperItem.nonHiddenSuper, HiddenFeatureSuperItem.hiddenSame, HiddenFeatureSuperItem.hiddenOther), HiddenFeatureSuperItem.TYPE.getFields());
		assertSame(HiddenFeatureSuperItem.TYPE, spt.getType());
		assertSame(HiddenFeatureSuperItem.TYPE, HiddenFeatureSuperItem.nonHiddenSuper.getType());
		assertSame(HiddenFeatureSuperItem.TYPE, HiddenFeatureSuperItem.hiddenSame.getType());
		assertSame(HiddenFeatureSuperItem.TYPE, HiddenFeatureSuperItem.hiddenOther.getType());
		assertEquals("this",           spt.getName());
		assertEquals("nonHiddenSuper", HiddenFeatureSuperItem.nonHiddenSuper.getName());
		assertEquals("hiddenSame",     HiddenFeatureSuperItem.hiddenSame.getName());
		assertEquals("hiddenOther",    HiddenFeatureSuperItem.hiddenOther.getName());
		assertSame(spt,               HiddenFeatureSuperItem.TYPE.getDeclaredFeature("this"));
		assertSame(HiddenFeatureSuperItem.nonHiddenSuper, HiddenFeatureSuperItem.TYPE.getDeclaredFeature("nonHiddenSuper"));
		assertSame(HiddenFeatureSuperItem.hiddenSame,     HiddenFeatureSuperItem.TYPE.getDeclaredFeature("hiddenSame"));
		assertSame(HiddenFeatureSuperItem.hiddenOther,    HiddenFeatureSuperItem.TYPE.getDeclaredFeature("hiddenOther"));
		assertSame(null,              HiddenFeatureSuperItem.TYPE.getDeclaredFeature("nonHiddenSub"));
		assertSame(spt,               HiddenFeatureSuperItem.TYPE.getFeature("this"));
		assertSame(HiddenFeatureSuperItem.nonHiddenSuper, HiddenFeatureSuperItem.TYPE.getFeature("nonHiddenSuper"));
		assertSame(HiddenFeatureSuperItem.hiddenSame,     HiddenFeatureSuperItem.TYPE.getFeature("hiddenSame"));
		assertSame(HiddenFeatureSuperItem.hiddenOther,    HiddenFeatureSuperItem.TYPE.getFeature("hiddenOther"));
		assertSame(null,              HiddenFeatureSuperItem.TYPE.getFeature("nonHiddenSub"));

		assertEquals(list(sbt, HiddenFeatureSubItem.nonHiddenSub, HiddenFeatureSubItem.hiddenSame, HiddenFeatureSubItem.hiddenOther), HiddenFeatureSubItem.TYPE.getDeclaredFeatures());
		assertEquals(list(sbt, HiddenFeatureSuperItem.nonHiddenSuper, HiddenFeatureSuperItem.hiddenSame, HiddenFeatureSuperItem.hiddenOther, HiddenFeatureSubItem.nonHiddenSub, HiddenFeatureSubItem.hiddenSame, HiddenFeatureSubItem.hiddenOther), HiddenFeatureSubItem.TYPE.getFeatures());
		assertEquals(list(HiddenFeatureSubItem.nonHiddenSub, HiddenFeatureSubItem.hiddenSame, HiddenFeatureSubItem.hiddenOther), HiddenFeatureSubItem.TYPE.getDeclaredFields());
		assertEquals(list(HiddenFeatureSuperItem.nonHiddenSuper, HiddenFeatureSuperItem.hiddenSame, HiddenFeatureSuperItem.hiddenOther, HiddenFeatureSubItem.nonHiddenSub, HiddenFeatureSubItem.hiddenSame, HiddenFeatureSubItem.hiddenOther), HiddenFeatureSubItem.TYPE.getFields());
		assertSame(HiddenFeatureSubItem.TYPE, sbt.getType());
		assertSame(HiddenFeatureSubItem.TYPE, HiddenFeatureSubItem.nonHiddenSub.getType());
		assertSame(HiddenFeatureSubItem.TYPE, HiddenFeatureSubItem.hiddenSame.getType());
		assertSame(HiddenFeatureSubItem.TYPE, HiddenFeatureSubItem.hiddenOther.getType());
		assertEquals("this", sbt.getName());
		assertEquals("nonHiddenSub", HiddenFeatureSubItem.nonHiddenSub.getName());
		assertEquals("hiddenSame", HiddenFeatureSubItem.hiddenSame.getName());
		assertEquals("hiddenOther", HiddenFeatureSubItem.hiddenOther.getName());
		assertSame(sbt,               HiddenFeatureSubItem.TYPE.getDeclaredFeature("this"));
		assertSame(null,              HiddenFeatureSubItem.TYPE.getDeclaredFeature("nonHiddenSuper"));
		assertSame(HiddenFeatureSubItem.hiddenSame,     HiddenFeatureSubItem.TYPE.getDeclaredFeature("hiddenSame"));
		assertSame(HiddenFeatureSubItem.hiddenOther,    HiddenFeatureSubItem.TYPE.getDeclaredFeature("hiddenOther"));
		assertSame(HiddenFeatureSubItem.nonHiddenSub,   HiddenFeatureSubItem.TYPE.getDeclaredFeature("nonHiddenSub"));
		assertSame(sbt,               HiddenFeatureSubItem.TYPE.getFeature("this"));
		assertSame(HiddenFeatureSuperItem.nonHiddenSuper, HiddenFeatureSubItem.TYPE.getFeature("nonHiddenSuper"));
		assertSame(HiddenFeatureSubItem.hiddenSame,     HiddenFeatureSubItem.TYPE.getFeature("hiddenSame"));
		assertSame(HiddenFeatureSubItem.hiddenOther,    HiddenFeatureSubItem.TYPE.getFeature("hiddenOther"));
		assertSame(HiddenFeatureSubItem.nonHiddenSub,   HiddenFeatureSubItem.TYPE.getFeature("nonHiddenSub"));

		assertEquals("HiddenFeatureSuperItem.this",           spt.getID());
		assertEquals("HiddenFeatureSuperItem.nonHiddenSuper", HiddenFeatureSuperItem.nonHiddenSuper.getID());
		assertEquals("HiddenFeatureSuperItem.hiddenSame",     HiddenFeatureSuperItem.hiddenSame.getID());
		assertEquals("HiddenFeatureSuperItem.hiddenOther",    HiddenFeatureSuperItem.hiddenOther.getID());
		assertEquals("HiddenFeatureSubItem.this",         sbt.getID());
		assertEquals("HiddenFeatureSubItem.nonHiddenSub", HiddenFeatureSubItem.nonHiddenSub.getID());
		assertEquals("HiddenFeatureSubItem.hiddenSame",   HiddenFeatureSubItem.hiddenSame.getID());
		assertEquals("HiddenFeatureSubItem.hiddenOther",  HiddenFeatureSubItem.hiddenOther.getID());
		assertSame(spt,               model.getFeature("HiddenFeatureSuperItem.this"));
		assertSame(HiddenFeatureSuperItem.nonHiddenSuper, model.getFeature("HiddenFeatureSuperItem.nonHiddenSuper"));
		assertSame(HiddenFeatureSuperItem.hiddenSame,     model.getFeature("HiddenFeatureSuperItem.hiddenSame"));
		assertSame(HiddenFeatureSuperItem.hiddenOther,    model.getFeature("HiddenFeatureSuperItem.hiddenOther"));
		assertSame(sbt,             model.getFeature("HiddenFeatureSubItem.this"));
		assertSame(HiddenFeatureSubItem.nonHiddenSub, model.getFeature("HiddenFeatureSubItem.nonHiddenSub"));
		assertSame(HiddenFeatureSubItem.hiddenSame,   model.getFeature("HiddenFeatureSubItem.hiddenSame"));
		assertSame(HiddenFeatureSubItem.hiddenOther,  model.getFeature("HiddenFeatureSubItem.hiddenOther"));
		assertSame(null, model.getFeature("HiddenFeatureSuperItem.hiddenOtherx"));
		assertSame(null, model.getFeature("HiddenFeatureSuperItemx.hiddenOther"));
		assertSame(null, model.getFeature("HiddenFeatureSuperItemhiddenOther"));
		assertSame(null, model.getFeature("HiddenFeatureSuperItem.nonHiddenSub"));
		assertSame(null, model.getFeature("HiddenFeatureSubItem.nonHiddenSuper"));

		assertEquals(Map.of(
				HiddenFeatureSubItem.hiddenOther, HiddenFeatureSuperItem.hiddenOther,
				HiddenFeatureSubItem.hiddenSame, HiddenFeatureSuperItem.hiddenSame),
				HiddenFeatures.get(model));

		// test persistence
		final HiddenFeatureSuperItem sp = new HiddenFeatureSuperItem();
		sp.setHiddenSame("hiddenSameSuperSuper");
		sp.setHiddenOther("hiddenOtherSuperSuper");
		final HiddenFeatureSubItem sb = new HiddenFeatureSubItem();
		sb.setHiddenSame("hiddenSameSuperSub");
		sb.setHiddenOther("hiddenOtherSuperSub");
		HiddenFeatureSubItem.hiddenSame.set(sb, "hiddenSameSub");
		HiddenFeatureSubItem.hiddenOther.set(sb, 55);

		restartTransaction();
		assertEquals("hiddenSameSuperSuper", sp.getHiddenSame());
		assertEquals("hiddenOtherSuperSuper", sp.getHiddenOther());
		assertEquals("hiddenSameSuperSub", sb.getHiddenSame());
		assertEquals("hiddenOtherSuperSub", sb.getHiddenOther());
		assertEquals("hiddenSameSub", HiddenFeatureSubItem.hiddenSame.get(sb));
		assertEquals(Integer.valueOf(55), HiddenFeatureSubItem.hiddenOther.get(sb));

		assertContains(sp, HiddenFeatureSuperItem.TYPE.search(HiddenFeatureSuperItem.hiddenSame.equal("hiddenSameSuperSuper")));
		assertContains(sp, HiddenFeatureSuperItem.TYPE.search(HiddenFeatureSuperItem.hiddenOther.equal("hiddenOtherSuperSuper")));
		assertContains(sb, HiddenFeatureSuperItem.TYPE.search(HiddenFeatureSuperItem.hiddenSame.equal("hiddenSameSuperSub")));
		assertContains(sb, HiddenFeatureSuperItem.TYPE.search(HiddenFeatureSuperItem.hiddenOther.equal("hiddenOtherSuperSub")));

		assertContains(HiddenFeatureSubItem.TYPE.search(HiddenFeatureSuperItem.hiddenSame.equal("hiddenSameSuperSuper")));
		assertContains(HiddenFeatureSubItem.TYPE.search(HiddenFeatureSuperItem.hiddenOther.equal("hiddenOtherSuperSuper")));
		assertContains(sb, HiddenFeatureSubItem.TYPE.search(HiddenFeatureSuperItem.hiddenSame.equal("hiddenSameSuperSub")));
		assertContains(sb, HiddenFeatureSubItem.TYPE.search(HiddenFeatureSuperItem.hiddenOther.equal("hiddenOtherSuperSub")));

		assertContains(sb, HiddenFeatureSubItem.TYPE.search(HiddenFeatureSubItem.hiddenSame.equal("hiddenSameSub")));
		assertContains(sb, HiddenFeatureSubItem.TYPE.search(HiddenFeatureSubItem.hiddenOther.equal(55)));
	}

}
