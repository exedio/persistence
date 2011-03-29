/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.Item.newEnumField;

import com.exedio.cope.junit.CopeAssert;

public class EnumClassTest extends CopeAssert
{
	public void testNormal()
	{
		final EnumField<Normal> f = newEnumField(Normal.class);
		assertSame(Normal.class, f.getValueClass());
		assertSame(Normal.class, f.getValueType().getJavaClass());
		assertEquals(list(Normal.Eins, Normal.Zwei, Normal.Drei), f.getValues());
	}

	enum Normal
	{
		Eins, Zwei, Drei;
	}


	@SuppressWarnings({"unchecked"}) // OK: test bad api usage
	public void testUnchecked()
	{
		try
		{
			newEnumField((Class)SomeClass.class);
			fail();
		}
		catch(final RuntimeException e) // TODO
		{
			assertEquals("is not a subclass of " + Enum.class.getName() + ": " + SomeClass.class.getName(), e.getMessage());
			assertSame(RuntimeException.class, e.getClass());
		}
	}

	class SomeClass
	{
		// is empty
	}


	public void testSubclass()
	{
		final EnumField<Subclass> f = newEnumField(Subclass.class);
		assertSame(Subclass.class, f.getValueClass());
		assertSame(Subclass.class, f.getValueType().getJavaClass());
		assertEquals(list(Subclass.Eins, Subclass.Zwei), f.getValues());
	}

	@SuppressWarnings({"unchecked"}) // OK: test bad api usage
	public void testSubclassWrong()
	{
		try
		{
			newEnumField((Class)Subclass.Eins.getClass());
			fail();
		}
		catch(final RuntimeException e) // TODO
		{
			assertEquals("must be an enum: class " + Subclass.Eins.getClass().getName(), e.getMessage());
			assertSame(RuntimeException.class, e.getClass());
		}
	}

	enum Subclass
	{
		Eins {@Override int zack(){ return 1; } },
		Zwei {@Override int zack(){ return 2; } };
		abstract int zack();
	}
}
