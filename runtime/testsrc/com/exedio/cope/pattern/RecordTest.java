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

package com.exedio.cope.pattern;

import java.util.Arrays;

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;

public class RecordTest extends AbstractLibTest
{
	static final Model MODEL = new Model(RecordOptionalItem.TYPE, RecordFinalItem.TYPE);
	
	public RecordTest()
	{
		super(MODEL);
	}

	RecordOptionalItem target1;
	RecordOptionalItem target2;
	RecordOptionalItem oItem;
	RecordFinalItem fItem;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		target1 = deleteOnTearDown(new RecordOptionalItem("target1"));
		target2 = deleteOnTearDown(new RecordOptionalItem("target2"));
	}
	
	public void testIt()
	{
		// test model
		assertEquals(Arrays.asList(new Feature[]{
				oItem.TYPE.getThis(),
				oItem.code,
				oItem.uno,
				oItem.uno.getSource(RecordValue.aString),
				oItem.uno.getSource(RecordValue.anInt),
				oItem.uno.getSource(RecordValue.anItem),
				oItem.duo,
				oItem.duo.getSource(RecordValue.aString),
				oItem.duo.getSource(RecordValue.anInt),
				oItem.duo.getSource(RecordValue.anItem),
			}), oItem.TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				oItem.TYPE.getThis(),
				oItem.code,
				oItem.uno,
				oItem.uno.getSource(RecordValue.aString),
				oItem.uno.getSource(RecordValue.anInt),
				oItem.uno.getSource(RecordValue.anItem),
				oItem.duo,
				oItem.duo.getSource(RecordValue.aString),
				oItem.duo.getSource(RecordValue.anInt),
				oItem.duo.getSource(RecordValue.anItem),
			}), oItem.TYPE.getDeclaredFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				fItem.TYPE.getThis(),
				fItem.code,
				fItem.first,
				fItem.first.getSource(RecordValue.aString),
				fItem.first.getSource(RecordValue.anInt),
				fItem.first.getSource(RecordValue.anItem),
				fItem.second,
				fItem.second.getSource(RecordValue.aString),
				fItem.second.getSource(RecordValue.anInt),
				fItem.second.getSource(RecordValue.anItem),
			}), fItem.TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				fItem.TYPE.getThis(),
				fItem.code,
				fItem.first,
				fItem.first.getSource(RecordValue.aString),
				fItem.first.getSource(RecordValue.anInt),
				fItem.first.getSource(RecordValue.anItem),
				fItem.second,
				fItem.second.getSource(RecordValue.aString),
				fItem.second.getSource(RecordValue.anInt),
				fItem.second.getSource(RecordValue.anItem),
			}), fItem.TYPE.getDeclaredFeatures());

		assertEquals(oItem.TYPE, oItem.uno.getSource(RecordValue.aString).getType());
		assertEquals(oItem.TYPE, oItem.uno.getType());
		assertEquals("unoAString", oItem.uno.getSource(RecordValue.aString).getName());
		assertEquals("uno", oItem.uno.getName());
		assertEqualsUnmodifiable(list(oItem.uno), oItem.uno.getSource(RecordValue.aString).getPatterns());
		
		assertEquals(false, oItem.uno.isInitial());
		assertEquals(false, oItem.uno.isFinal());
		assertEquals(false, oItem.uno.getSource(RecordValue.aString).isInitial());
		assertEquals(false, oItem.uno.getSource(RecordValue.aString).isFinal());
		assertEquals(false, oItem.uno.getSource(RecordValue.aString).isMandatory());
		assertEquals(true, fItem.first.isInitial());
		assertEquals(true, fItem.first.isFinal());
		assertEquals(true, fItem.first.getSource(RecordValue.aString).isInitial());
		assertEquals(true, fItem.first.getSource(RecordValue.aString).isFinal());
		assertEquals(true, fItem.first.getSource(RecordValue.aString).isMandatory());
		
		
		try
		{
			oItem.uno.getSource(oItem.code);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("RecordOptionalItem.code is not a template of RecordOptionalItem.uno", e.getMessage());
		}

		// test persistence
		oItem = deleteOnTearDown(new RecordOptionalItem("optional1"));
		assertEquals("optional1", oItem.getCode());
		assertEquals(null, oItem.getUno().getAString());
		assertEquals(null, oItem.getUno().getAnInt());
		assertEquals(null, oItem.getUno().getAnItem());
		assertEquals(null, oItem.getDuo().getAString());
		assertEquals(null, oItem.getDuo().getAnInt());
		assertEquals(null, oItem.getDuo().getAnItem());
		
		fItem = deleteOnTearDown(
				new RecordFinalItem("final1",
						new RecordValue("firstString1", 1, target1),
						new RecordValue("secondString1", 2, target2)));
		assertEquals("final1", fItem.getCode());
		assertEquals("firstString1", fItem.getFirst().getAString());
		assertEquals(new Integer(1), fItem.getFirst().getAnInt());
		assertEquals(target1, fItem.getFirst().getAnItem());
		assertEquals("secondString1", fItem.getSecond().getAString());
		assertEquals(new Integer(2), fItem.getSecond().getAnInt());
		assertEquals(target2, fItem.getSecond().getAnItem());
		
		oItem.setDuo(fItem.getFirst());
		assertEquals(null, oItem.getUno().getAString());
		assertEquals(null, oItem.getUno().getAnInt());
		assertEquals(null, oItem.getUno().getAnItem());
		assertEquals("firstString1", oItem.getDuo().getAString());
		assertEquals(new Integer(1), oItem.getDuo().getAnInt());
		assertEquals(target1, oItem.getDuo().getAnItem());

		// test value independence
		final RecordValue value = oItem.getDuo();
		assertEquals("firstString1", value.getAString());
		assertEquals("firstString1", oItem.getDuo().getAString());
		assertEquals("firstString1", fItem.getFirst().getAString());
		
		value.setAString("firstString1X");
		assertEquals("firstString1X", value.getAString());
		assertEquals("firstString1", oItem.getDuo().getAString());
		assertEquals("firstString1", fItem.getFirst().getAString());
		
		oItem.setDuo(value);
		assertEquals("firstString1X", value.getAString());
		assertEquals("firstString1X", oItem.getDuo().getAString());
		assertEquals("firstString1", fItem.getFirst().getAString());

		// test hashCode
		assertEquals(value, value);
		assertEquals(fItem.getFirst(), fItem.getFirst());
		assertNotSame(fItem.getFirst(), fItem.getFirst());
		assertFalse(fItem.getFirst().equals(oItem.getDuo()));
		assertFalse(fItem.getFirst().equals(null));
		assertFalse(fItem.getFirst().equals("hallo"));
		// test hashCode
		assertEquals(fItem.getFirst().hashCode(), fItem.getFirst().hashCode());
		assertFalse(fItem.getFirst().hashCode()==oItem.getDuo().hashCode());
	}
}
