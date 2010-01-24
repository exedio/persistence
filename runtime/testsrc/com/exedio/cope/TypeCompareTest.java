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

import static com.exedio.cope.TypesBound.newType;

import com.exedio.cope.junit.CopeAssert;

public class TypeCompareTest extends CopeAssert
{
	public void testType()
	{
		final Type<AnItem> type1 = newType(AnItem.class);
		final Type<AnotherItem> type2 = newType(AnotherItem.class);
		try
		{
			type1.compareTo(type2);
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("model not set for type AnItem, probably you forgot to put this type into the model.", e.getMessage());
		}
		try
		{
			type2.compareTo(type1);
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("model not set for type AnotherItem, probably you forgot to put this type into the model.", e.getMessage());
		}
		
		new Model(type1, type2);
		assertEquals(0, type1.compareTo(type1));
		assertEquals(0, type2.compareTo(type2));
		assertEquals(-1, type1.compareTo(type2));
		assertEquals( 1, type2.compareTo(type1));
		
		final Type<AnotherModelItem> typeOtherModel = newType(AnotherModelItem.class);
		try
		{
			type1.compareTo(typeOtherModel);
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("model not set for type AnotherModelItem, probably you forgot to put this type into the model.", e.getMessage());
		}
		try
		{
			typeOtherModel.compareTo(type1);
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("model not set for type AnotherModelItem, probably you forgot to put this type into the model.", e.getMessage());
		}
		
		new Model(typeOtherModel);
		try
		{
			type1.compareTo(typeOtherModel);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("types are not comparable, because they do not belong to the same model: AnItem,AnotherModelItem", e.getMessage());
		}
		try
		{
			typeOtherModel.compareTo(type1);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("types are not comparable, because they do not belong to the same model: AnotherModelItem,AnItem", e.getMessage());
		}
	}
	
	static class AnItem extends Item
	{
		private static final long serialVersionUID = 1l;
		
		protected AnItem(final ActivationParameters ap)
		{
			super(ap);
		}
		
		static final IntegerField intField = new IntegerField();
	}
	
	static class AnotherItem extends Item
	{
		private static final long serialVersionUID = 1l;
		
		private AnotherItem(final ActivationParameters ap)
		{
			super(ap);
		}
		
		static final IntegerField intField = new IntegerField();
	}
	
	static class AnotherModelItem extends Item
	{
		private static final long serialVersionUID = 1l;
		
		private AnotherModelItem(final ActivationParameters ap)
		{
			super(ap);
		}
		
		static final IntegerField intField = new IntegerField();
	}
}
