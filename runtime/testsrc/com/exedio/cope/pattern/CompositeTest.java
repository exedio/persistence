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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;

public class CompositeTest extends AbstractLibTest
{
	static final Model MODEL = new Model(CompositeOptionalItem.TYPE, CompositeFinalItem.TYPE);
	
	public CompositeTest()
	{
		super(MODEL);
	}

	CompositeOptionalItem target1;
	CompositeOptionalItem target2;
	CompositeOptionalItem oItem;
	CompositeFinalItem fItem;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		target1 = deleteOnTearDown(new CompositeOptionalItem("target1"));
		target2 = deleteOnTearDown(new CompositeOptionalItem("target2"));
	}
	
	public void testIt() throws IOException, ClassNotFoundException
	{
		// test model
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				oItem.TYPE.getThis(),
				oItem.code,
				oItem.uno,
				oItem.uno.getComponent(CompositeValue.aString),
				oItem.uno.getComponent(CompositeValue.anInt),
				oItem.uno.getComponent(CompositeValue.anItem),
				oItem.duo,
				oItem.duo.getComponent(CompositeValue.aString),
				oItem.duo.getComponent(CompositeValue.anInt),
				oItem.duo.getComponent(CompositeValue.anItem),
			}), oItem.TYPE.getFeatures());
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				oItem.TYPE.getThis(),
				oItem.code,
				oItem.uno,
				oItem.uno.getComponent(CompositeValue.aString),
				oItem.uno.getComponent(CompositeValue.anInt),
				oItem.uno.getComponent(CompositeValue.anItem),
				oItem.duo,
				oItem.duo.getComponent(CompositeValue.aString),
				oItem.duo.getComponent(CompositeValue.anInt),
				oItem.duo.getComponent(CompositeValue.anItem),
			}), oItem.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				fItem.TYPE.getThis(),
				fItem.code,
				fItem.first,
				fItem.first.getComponent(CompositeValue.aString),
				fItem.first.getComponent(CompositeValue.anInt),
				fItem.first.getComponent(CompositeValue.anItem),
				fItem.second,
				fItem.second.getComponent(CompositeValue.aString),
				fItem.second.getComponent(CompositeValue.anInt),
				fItem.second.getComponent(CompositeValue.anItem),
			}), fItem.TYPE.getFeatures());
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				fItem.TYPE.getThis(),
				fItem.code,
				fItem.first,
				fItem.first.getComponent(CompositeValue.aString),
				fItem.first.getComponent(CompositeValue.anInt),
				fItem.first.getComponent(CompositeValue.anItem),
				fItem.second,
				fItem.second.getComponent(CompositeValue.aString),
				fItem.second.getComponent(CompositeValue.anInt),
				fItem.second.getComponent(CompositeValue.anItem),
			}), fItem.TYPE.getDeclaredFeatures());

		assertEquals(oItem.TYPE, oItem.uno.getComponent(CompositeValue.aString).getType());
		assertEquals(oItem.TYPE, oItem.uno.getType());
		assertEquals("unoAString", oItem.uno.getComponent(CompositeValue.aString).getName());
		assertEquals("uno", oItem.uno.getName());
		assertEqualsUnmodifiable(list(oItem.uno), oItem.uno.getComponent(CompositeValue.aString).getPatterns());
		
		assertEquals(false, oItem.uno.isInitial());
		assertEquals(false, oItem.uno.isFinal());
		assertEquals(false, oItem.uno.getComponent(CompositeValue.aString).isInitial());
		assertEquals(false, oItem.uno.getComponent(CompositeValue.aString).isFinal());
		assertEquals(false, oItem.uno.getComponent(CompositeValue.aString).isMandatory());
		assertEquals(true, fItem.first.isInitial());
		assertEquals(true, fItem.first.isFinal());
		assertEquals(true, fItem.first.getComponent(CompositeValue.aString).isInitial());
		assertEquals(true, fItem.first.getComponent(CompositeValue.aString).isFinal());
		assertEquals(true, fItem.first.getComponent(CompositeValue.aString).isMandatory());
		
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				oItem.uno.getComponent(CompositeValue.aString),
				oItem.uno.getComponent(CompositeValue.anInt),
				oItem.uno.getComponent(CompositeValue.anItem),
			}), oItem.uno.getComponents());
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				fItem.second.getComponent(CompositeValue.aString),
				fItem.second.getComponent(CompositeValue.anInt),
				fItem.second.getComponent(CompositeValue.anItem),
			}), fItem.second.getComponents());
		
		try
		{
			oItem.uno.getComponent(oItem.code);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("CompositeOptionalItem.code is not a template of CompositeOptionalItem.uno", e.getMessage());
		}

		// test persistence
		oItem = deleteOnTearDown(new CompositeOptionalItem("optional1"));
		assertEquals("optional1", oItem.getCode());
		assertEquals(null, oItem.getUno().getAString());
		assertEquals(null, oItem.getUno().getAnInt());
		assertEquals(null, oItem.getUno().getAnItem());
		assertEquals(null, oItem.getDuo().getAString());
		assertEquals(null, oItem.getDuo().getAnInt());
		assertEquals(null, oItem.getDuo().getAnItem());
		
		fItem = deleteOnTearDown(
				new CompositeFinalItem("final1",
						new CompositeValue("firstString1", 1, target1),
						new CompositeValue("secondString1", 2, target2)));
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
		final CompositeValue serializedValue = reserialize(value);
		assertEquals(value, serializedValue);
		assertNotSame(value, serializedValue);
	}
	
	@SuppressWarnings("unchecked")
	private static final <S> S reserialize(final S value) throws IOException, ClassNotFoundException
	{
		if(value==null)
			throw new NullPointerException();
		
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(value);
		oos.close();

		final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		final Object result = ois.readObject();
		ois.close();
		return (S)result;
	}
}
