/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.pattern.CompositeFinalItem.first;
import static com.exedio.cope.pattern.CompositeFinalItem.second;
import static com.exedio.cope.pattern.CompositeItem.eins;
import static com.exedio.cope.pattern.CompositeItem.zwei;
import static com.exedio.cope.pattern.CompositeOptionalItem.duo;
import static com.exedio.cope.pattern.CompositeOptionalItem.uno;
import static com.exedio.cope.pattern.CompositeValue.aString;
import static com.exedio.cope.pattern.CompositeValue.anEnum;
import static com.exedio.cope.pattern.CompositeValue.anInt;
import static com.exedio.cope.pattern.CompositeValue.anItem;

import java.util.Arrays;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Feature;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.pattern.CompositeValue.AnEnumClass;

public class CompositeFieldTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(CompositeItem.TYPE, CompositeOptionalItem.TYPE, CompositeFinalItem.TYPE);
	
	public CompositeFieldTest()
	{
		super(MODEL);
	}

	CompositeOptionalItem target1;
	CompositeOptionalItem target2;
	CompositeItem item;
	CompositeOptionalItem oItem;
	CompositeFinalItem fItem;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		target1 = deleteOnTearDown(new CompositeOptionalItem("target1"));
		target2 = deleteOnTearDown(new CompositeOptionalItem("target2"));
	}
	
	public void testIt()
	{
		// test model
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				item.TYPE.getThis(),
				item.code,
				eins,
				eins.of(aString), eins.of(anInt), eins.of(anEnum), eins.of(anItem),
				zwei,
				zwei.of(aString), zwei.of(anInt), zwei.of(anEnum), zwei.of(anItem),
			}), item.TYPE.getFeatures());
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				item.TYPE.getThis(),
				item.code,
				eins,
				eins.of(aString), eins.of(anInt), eins.of(anEnum), eins.of(anItem),
				zwei,
				zwei.of(aString), zwei.of(anInt), zwei.of(anEnum), zwei.of(anItem),
			}), item.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				oItem.TYPE.getThis(),
				oItem.code,
				uno,
				uno.of(aString), uno.of(anInt), uno.of(anEnum), uno.of(anItem),
				duo,
				duo.of(aString), duo.of(anInt), duo.of(anEnum), duo.of(anItem),
			}), oItem.TYPE.getFeatures());
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				oItem.TYPE.getThis(),
				oItem.code,
				uno,
				uno.of(aString), uno.of(anInt), uno.of(anEnum), uno.of(anItem),
				duo,
				duo.of(aString), duo.of(anInt), duo.of(anEnum), duo.of(anItem),
			}), oItem.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				fItem.TYPE.getThis(),
				fItem.code,
				first,
				first.of(aString),  first.of(anInt),  first.of(anEnum),  first.of(anItem),
				second,
				second.of(aString), second.of(anInt), second.of(anEnum), second.of(anItem),
			}), fItem.TYPE.getFeatures());
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				fItem.TYPE.getThis(),
				fItem.code,
				first,
				first.of(aString),  first.of(anInt),  first.of(anEnum),  first.of(anItem),
				second,
				second.of(aString), second.of(anInt), second.of(anEnum),second.of(anItem),
			}), fItem.TYPE.getDeclaredFeatures());

		assertEquals(oItem.TYPE, uno.of(aString).getType());
		assertEquals(oItem.TYPE, uno.getType());
		assertEquals("unoAString", uno.of(aString).getName());
		assertEquals("uno", uno.getName());
		assertEquals(uno, uno.of(aString).getPattern());
		assertEqualsUnmodifiable(list(uno.of(aString), uno.of(anInt), uno.of(anEnum), uno.of(anItem)), uno.getSourceFields());
		
		assertEquals(true,  eins.isInitial());
		assertEquals(false, eins.isFinal());
		assertEquals(true,  eins.isMandatory());
		assertEquals(true,  eins.of(aString).isInitial());
		assertEquals(false, eins.of(aString).isFinal());
		assertEquals(true,  eins.of(aString).isMandatory());
		assertEquals(false, uno.isInitial());
		assertEquals(false, uno.isFinal());
		assertEquals(false, uno.isMandatory());
		assertEquals(false, uno.of(aString).isInitial());
		assertEquals(false, uno.of(aString).isFinal());
		assertEquals(false, uno.of(aString).isMandatory());
		assertEquals(true, first.isInitial());
		assertEquals(true, first.isFinal());
		assertEquals(true, first.isMandatory());
		assertEquals(true, first.of(aString).isInitial());
		assertEquals(true, first.of(aString).isFinal());
		assertEquals(true, first.of(aString).isMandatory());
		
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				eins.of(aString),
				eins.of(anInt),
				eins.of(anEnum),
				eins.of(anItem),
			}), eins.getComponents());
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				uno.of(aString),
				uno.of(anInt),
				uno.of(anEnum),
				uno.of(anItem),
			}), uno.getComponents());
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				second.of(aString),
				second.of(anInt),
				second.of(anEnum),
				second.of(anItem),
			}), second.getComponents());
		
		// test type safety of getComponent
		second.of(aString).startsWith("zack");
		second.of(anInt).plus(1);
		
		try
		{
			uno.of(oItem.code);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("CompositeOptionalItem.code is not a template of CompositeOptionalItem.uno", e.getMessage());
		}
		
		try
		{
			aString.isAnnotationPresent(Computed.class);
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
		
		assertTrue(eins.of(aString).isAnnotationPresent(Computed.class));
		assertTrue(eins.of(anInt  ).isAnnotationPresent(Computed.class));
		assertTrue(eins.of(anEnum ).isAnnotationPresent(Computed.class));
		assertTrue(eins.of(anItem ).isAnnotationPresent(Computed.class));
		assertTrue(zwei.of(aString).isAnnotationPresent(Computed.class));
		assertTrue(zwei.of(anInt  ).isAnnotationPresent(Computed.class));
		assertTrue(zwei.of(anEnum ).isAnnotationPresent(Computed.class));
		assertTrue(zwei.of(anItem ).isAnnotationPresent(Computed.class));


		// test persistence
		oItem = deleteOnTearDown(new CompositeOptionalItem("optional1"));
		assertEquals("optional1", oItem.getCode());
		assertEquals(null, oItem.getUno());
		assertEquals(null, oItem.getDuo());
		
		fItem = deleteOnTearDown(
				new CompositeFinalItem("final1",
						new CompositeValue("firstString1",  1, AnEnumClass.anEnumConstant1, target1),
						new CompositeValue("secondString1", 2, AnEnumClass.anEnumConstant2, target2)));
		assertEquals("final1", fItem.getCode());
		assertEquals("firstString1", fItem.getFirst().getAString());
		assertEquals(1, fItem.getFirst().getAnInt());
		assertEquals(AnEnumClass.anEnumConstant1, fItem.getFirst().getAnEnum());
		assertEquals(target1, fItem.getFirst().getAnItem());
		assertEquals("secondString1", fItem.getSecond().getAString());
		assertEquals(2, fItem.getSecond().getAnInt());
		assertEquals(AnEnumClass.anEnumConstant2, fItem.getSecond().getAnEnum());
		assertEquals(target2, fItem.getSecond().getAnItem());
		
		oItem.setDuo(fItem.getFirst());
		assertEquals(null, oItem.getUno());
		assertEquals("firstString1", oItem.getDuo().getAString());
		assertEquals(1, oItem.getDuo().getAnInt());
		assertEquals(AnEnumClass.anEnumConstant1, oItem.getDuo().getAnEnum());
		assertEquals(target1, oItem.getDuo().getAnItem());
		
		oItem.setDuo(null);
		assertEquals(null, oItem.getUno());
		assertEquals(null, oItem.getDuo());
		assertEquals(null, oItem.duo.of(aString).get(oItem));
		assertEquals(null, oItem.duo.of(anInt  ).get(oItem));
		assertEquals(null, oItem.duo.of(anEnum ).get(oItem));
		assertEquals(null, oItem.duo.of(anItem ).get(oItem));
		
		item = deleteOnTearDown(
				new CompositeItem("default",
						new CompositeValue("einsString1", 1, AnEnumClass.anEnumConstant1, target1),
						new CompositeValue("zweiString1", 2, AnEnumClass.anEnumConstant2, target2)));
		try
		{
			item.setEins(null);
			fail();
		}
		catch(MandatoryViolationException e) 
		{
			assertEquals("mandatory violation on CompositeItem.0 for CompositeItem.einsAString", e.getMessage()); // TODO feature should be CompositeItem.eins
		}
		try
		{
			new CompositeItem("defaultFailure",
					new CompositeValue("einsString1", 1, AnEnumClass.anEnumConstant1, target1),
					null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals("mandatory violation on a newly created item for CompositeItem.zweiAString", e.getMessage()); // TODO feature should be CompositeItem.zwei
		}
		try
		{
			fItem.first.set(fItem, null);
			fail();
		}
		catch(FinalViolationException e)
		{
			assertEquals("final violation on CompositeFinalItem.0 for CompositeFinalItem.firstAString", e.getMessage()); // TODO feature should be CompositeFinalItem.first
		}
		try
		{
			fItem.first.set(fItem, new CompositeValue("finalViolation", 1, AnEnumClass.anEnumConstant1, target1));
			fail();
		}
		catch(FinalViolationException e)
		{
			assertEquals("final violation on CompositeFinalItem.0 for CompositeFinalItem.firstAString", e.getMessage()); // TODO feature should be CompositeFinalItem.first
		}

		// test value independence
		oItem.setDuo(fItem.getFirst());
		final CompositeValue value = oItem.getDuo();
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
		
		// test serialization
		final CompositeValue serializedValue = reserialize(value, 600);
		assertEquals(value, serializedValue);
		assertNotSame(value, serializedValue);
	}
}
