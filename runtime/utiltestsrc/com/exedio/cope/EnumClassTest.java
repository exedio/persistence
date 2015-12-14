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

import com.exedio.cope.junit.CopeAssert;
import org.junit.Test;

public class EnumClassTest extends CopeAssert
{
	@Test public void testNormal()
	{
		final EnumField<Normal> f = EnumField.create(Normal.class);
		assertSame(Normal.class, f.getValueClass());
		assertSame(Normal.class, f.getValueType().getJavaClass());
		assertEquals(list(Normal.Eins, Normal.Zwei, Normal.Drei), f.getValues());
	}

	enum Normal
	{
		Eins, Zwei, Drei;
	}


	@Test public void testNull()
	{
		try
		{
			EnumField.create((Class<Normal>)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("valueClass", e.getMessage());
		}
	}


	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad api usage
	@Test public void testUnchecked()
	{
		try
		{
			EnumField.create((Class)SomeClass.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not an enum: " + SomeClass.class, e.getMessage());
		}
	}

	class SomeClass
	{
		// is empty
	}


	@Test public void testSubclass()
	{
		final EnumField<Subclass> f = EnumField.create(Subclass.class);
		assertSame(Subclass.class, f.getValueClass());
		assertSame(Subclass.class, f.getValueType().getJavaClass());
		assertEquals(list(Subclass.Eins, Subclass.Zwei), f.getValues());
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad api usage
	@Test public void testSubclassWrong()
	{
		try
		{
			EnumField.create((Class)Subclass.Eins.getClass());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not an enum: " + Subclass.Eins.getClass(), e.getMessage());
		}
	}

	enum Subclass
	{
		Eins {@Override int zack(){ return 1; } },
		Zwei {@Override int zack(){ return 2; } };
		abstract int zack();
	}

	@SuppressWarnings("unchecked") // OK: test bad api usage
	@Test public void testEnumItself()
	{
		try
		{
			EnumField.create(Enum.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not an enum: " + Enum.class, e.getMessage());
		}
	}
}
