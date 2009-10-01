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

package com.exedio.cope;

import java.util.LinkedHashMap;

import com.exedio.cope.junit.CopeAssert;

public class FeatureTest extends CopeAssert
{
	public void testType()
	{
		final StringField f = new StringField().lengthRange(5, 8);
		
		try
		{
			f.getType();
			fail();
		}
		catch(FeatureNotInitializedException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			f.getName();
			fail();
		}
		catch(FeatureNotInitializedException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			f.getID();
			fail();
		}
		catch(FeatureNotInitializedException e)
		{
			assertEquals(null, e.getMessage());
		}
		assertTrue(f.toString().startsWith("com.exedio.cope.StringField@"));
		assertTrue(toString(f, null).startsWith("com.exedio.cope.StringField@"));
		assertEquals(5, f.getMinimumLength());
		assertEquals(8, f.getMaximumLength());
		
		final LinkedHashMap<String, Feature> features = new LinkedHashMap<String, Feature>();
		features.put("featureName", f);
		final Type<AnItem> t = new Type<AnItem>(AnItem.class, false, "typeId", (Pattern)null, false, (Type<Item>)null, features);
		
		assertSame(t, f.getType());
		assertEquals("featureName", f.getName());
		assertEquals("typeId.featureName", f.getID());
		assertEquals("typeId.featureName", f.toString());
		assertEquals("typeId.featureName", toString(f, null));
		assertEquals("featureName", toString(f, t));
		assertEquals(5, f.getMinimumLength());
		assertEquals(8, f.getMaximumLength());
		
		try
		{
			new Type<AnItem>(AnItem.class, false, "typeId", (Pattern)null, false, (Type<Item>)null, features);
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("feature already initialized: typeId.featureName", e.getMessage());
		}
	}
	
	private static final String toString(final Feature f, final Type defaultType)
	{
		final StringBuilder bf = new StringBuilder();
		f.toString(bf, defaultType);
		return bf.toString();
	}
	
	static class AnItem extends Item
	{
		private static final long serialVersionUID = 1l;
		
		private AnItem(final ActivationParameters ap)
		{
			super(ap);
		}
	}
}
