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

public class InstanceOfModelTest extends CopeAssert
{
	public static final Model MODEL = new Model(
			InstanceOfAItem.TYPE,
			InstanceOfB1Item.TYPE,
			InstanceOfB2Item.TYPE,
			InstanceOfC1Item.TYPE,
			InstanceOfRefItem.TYPE);

	public void testFeatures()
	{
		final Type<InstanceOfAItem > a  = InstanceOfAItem .TYPE;
		final Type<InstanceOfB1Item> b1 = InstanceOfB1Item.TYPE;
		final Type<InstanceOfB2Item> b2 = InstanceOfB2Item.TYPE;
		final Type<InstanceOfC1Item> c1 = InstanceOfC1Item.TYPE;
		final StringField code = InstanceOfAItem.code;
		final UniqueConstraint codeUnq = code.getImplicitUniqueConstraint();
		final StringField textb1 = InstanceOfB1Item.textb1;
		final StringField textb2 = InstanceOfB2Item.textb2;
		final StringField textc1 = InstanceOfC1Item.textc1;

		assertEquals(list(a .getThis(), code, codeUnq), a .getDeclaredFeatures());
		assertEquals(list(b1.getThis(), textb1),        b1.getDeclaredFeatures());
		assertEquals(list(b2.getThis(), textb2),        b2.getDeclaredFeatures());
		assertEquals(list(c1.getThis(), textc1),        c1.getDeclaredFeatures());

		assertEquals(list(a .getThis(), code, codeUnq),                 a .getFeatures());
		assertEquals(list(b1.getThis(), code, codeUnq, textb1),         b1.getFeatures());
		assertEquals(list(b2.getThis(), code, codeUnq, textb2),         b2.getFeatures());
		assertEquals(list(c1.getThis(), code, codeUnq, textb1, textc1), c1.getFeatures());

		assertEquals(list(code),   a .getDeclaredFields());
		assertEquals(list(textb1), b1.getDeclaredFields());
		assertEquals(list(textb2), b2.getDeclaredFields());
		assertEquals(list(textc1), c1.getDeclaredFields());

		assertEquals(list(code),                 a .getFields());
		assertEquals(list(code, textb1),         b1.getFields());
		assertEquals(list(code, textb2),         b2.getFields());
		assertEquals(list(code, textb1, textc1), c1.getFields());

		assertEquals(list(codeUnq),   a .getDeclaredUniqueConstraints());
		assertEquals(list(),          b1.getDeclaredUniqueConstraints());
		assertEquals(list(),          b2.getDeclaredUniqueConstraints());
		assertEquals(list(),          c1.getDeclaredUniqueConstraints());

		assertEquals(list(codeUnq), a .getUniqueConstraints());
		assertEquals(list(codeUnq), b1.getUniqueConstraints());
		assertEquals(list(codeUnq), b2.getUniqueConstraints());
		assertEquals(list(codeUnq), c1.getUniqueConstraints());
	}

	public void testAsSame()
	{
		assertSame(InstanceOfAItem.TYPE, InstanceOfAItem.TYPE.as(InstanceOfAItem.class));
		assertSame(InstanceOfAItem.TYPE, InstanceOfAItem.TYPE.asExtends(InstanceOfAItem.class));
		assertSame(InstanceOfRefItem.ref, InstanceOfRefItem.ref.as(InstanceOfAItem.class));
		assertSame(InstanceOfRefItem.ref, InstanceOfRefItem.ref.asExtends(InstanceOfAItem.class));
	}

	public void testAsChildReverse()
	{
		try
		{
			InstanceOfAItem.TYPE.as(InstanceOfB1Item.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertAsTypeFails(InstanceOfB1Item.class, InstanceOfAItem.class, e);
		}
		try
		{
			InstanceOfAItem.TYPE.asExtends(InstanceOfB1Item.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertAsTypeFailsExtends(InstanceOfB1Item.class, InstanceOfAItem.class, e);
		}
		try
		{
			InstanceOfRefItem.ref.as(InstanceOfB1Item.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertAsFieldFails(InstanceOfB1Item.class, InstanceOfAItem.class, e);
		}
		try
		{
			InstanceOfRefItem.ref.asExtends(InstanceOfB1Item.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertAsFieldFailsExtends(InstanceOfB1Item.class, InstanceOfAItem.class, e);
		}
	}

	public void testAsSameSub()
	{
		assertSame(InstanceOfB2Item.TYPE, InstanceOfB2Item.TYPE.as(InstanceOfB2Item.class));
		assertSame(InstanceOfB2Item.TYPE, InstanceOfB2Item.TYPE.asExtends(InstanceOfB2Item.class));
		assertSame(InstanceOfRefItem.refb2, InstanceOfRefItem.refb2.as(InstanceOfB2Item.class));
		assertSame(InstanceOfRefItem.refb2, InstanceOfRefItem.refb2.asExtends(InstanceOfB2Item.class));
	}

	public void testAsBrother()
	{
		try
		{
			InstanceOfB2Item.TYPE.as(InstanceOfB1Item.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertAsTypeFails(InstanceOfB1Item.class, InstanceOfB2Item.class, e);
		}
		try
		{
			InstanceOfB2Item.TYPE.asExtends(InstanceOfB1Item.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertAsTypeFailsExtends(InstanceOfB1Item.class, InstanceOfB2Item.class, e);
		}
		try
		{
			InstanceOfRefItem.refb2.as(InstanceOfB1Item.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertAsFieldFails(InstanceOfB1Item.class, InstanceOfB2Item.class, e);
		}
		try
		{
			InstanceOfRefItem.refb2.asExtends(InstanceOfB1Item.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertAsFieldFailsExtends(InstanceOfB1Item.class, InstanceOfB2Item.class, e);
		}
	}

	public void testAsSameChild()
	{
		try
		{
			InstanceOfB2Item.TYPE.as(InstanceOfAItem.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertAsTypeFails(InstanceOfAItem.class, InstanceOfB2Item.class, e);
		}
		assertSame(InstanceOfB2Item.TYPE, InstanceOfB2Item.TYPE.asExtends(InstanceOfAItem.class));
		try
		{
			InstanceOfRefItem.refb2.as(InstanceOfAItem.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertAsFieldFails(InstanceOfAItem.class, InstanceOfB2Item.class, e);
		}
		assertSame(InstanceOfRefItem.refb2, InstanceOfRefItem.refb2.asExtends(InstanceOfAItem.class));
	}

	private static void assertAsTypeFails(
			final Class<? extends InstanceOfAItem> expected,
			final Class<? extends InstanceOfAItem> actual,
			final ClassCastException failure)
	{
		assertEquals(
				"expected " + expected.getName() + ", " +
				"but was " + actual.getName(),
			failure.getMessage());
	}

	private static void assertAsTypeFailsExtends(
			final Class<? extends InstanceOfAItem> expected,
			final Class<? extends InstanceOfAItem> actual,
			final ClassCastException failure)
	{
		assertEquals(
				"expected ? extends " + expected.getName() + ", " +
				"but was " + actual.getName(),
			failure.getMessage());
	}

	private static void assertAsFieldFails(
			final Class<? extends InstanceOfAItem> expected,
			final Class<? extends InstanceOfAItem> actual,
			final ClassCastException failure)
	{
		assertEquals(
				"expected a " + ItemField.class.getName() + '<' + expected.getName() + ">, " +
				"but was a " + ItemField.class.getName() + '<' + actual.getName() + '>',
			failure.getMessage());
	}

	private static void assertAsFieldFailsExtends(
			final Class<? extends InstanceOfAItem> expected,
			final Class<? extends InstanceOfAItem> actual,
			final ClassCastException failure)
	{
		assertEquals(
				"expected a " + ItemField.class.getName() + "<? extends " + expected.getName() + ">, " +
				"but was a " + ItemField.class.getName() + '<' + actual.getName() + '>',
			failure.getMessage());
	}

	public void testGetSubTypes()
	{
		assertEqualsUnmodifiable(
				list(InstanceOfB1Item.TYPE, InstanceOfB2Item.TYPE), InstanceOfAItem.TYPE.getSubtypes());
		assertEqualsUnmodifiable(
				list(InstanceOfC1Item.TYPE), InstanceOfB1Item.TYPE.getSubtypes());
		assertEqualsUnmodifiable(
				list(), InstanceOfB2Item.TYPE.getSubtypes());
		assertEqualsUnmodifiable(
				list(), InstanceOfC1Item.TYPE.getSubtypes());
	}

	public void testGetSubTypesTransitively()
	{
		assertEqualsUnmodifiable(
				list(InstanceOfAItem.TYPE, InstanceOfB1Item.TYPE, InstanceOfC1Item.TYPE, InstanceOfB2Item.TYPE),
				InstanceOfAItem.TYPE.getSubtypesTransitively());
		assertEqualsUnmodifiable(
				list(InstanceOfB1Item.TYPE, InstanceOfC1Item.TYPE),
				InstanceOfB1Item.TYPE.getSubtypesTransitively());
		assertEqualsUnmodifiable(
				list(InstanceOfB2Item.TYPE),
				InstanceOfB2Item.TYPE.getSubtypesTransitively());
		assertEqualsUnmodifiable(
				list(InstanceOfC1Item.TYPE),
				InstanceOfC1Item.TYPE.getSubtypesTransitively());
	}

	public void testTypesOfInstances()
	{
		assertEqualsUnmodifiable(
				list(InstanceOfAItem.TYPE, InstanceOfB1Item.TYPE, InstanceOfC1Item.TYPE, InstanceOfB2Item.TYPE),
				InstanceOfAItem.TYPE.getTypesOfInstances());
		assertEqualsUnmodifiable(
				list(InstanceOfB1Item.TYPE, InstanceOfC1Item.TYPE),
				InstanceOfB1Item.TYPE.getTypesOfInstances());
		assertEqualsUnmodifiable(
				list(InstanceOfB2Item.TYPE),
				InstanceOfB2Item.TYPE.getTypesOfInstances());
		assertEqualsUnmodifiable(
				list(InstanceOfC1Item.TYPE),
				InstanceOfC1Item.TYPE.getTypesOfInstances());
	}

	public void testCastTypeExtends()
	{
		final Type<? extends InstanceOfAItem> t = InstanceOfAItem.TYPE.castTypeExtends(InstanceOfB1Item.TYPE);
		assertSame(InstanceOfB1Item.TYPE, t);

		final Type<? extends InstanceOfAItem> ts = InstanceOfAItem.TYPE.castTypeExtends(InstanceOfAItem.TYPE);
		assertSame(InstanceOfAItem.TYPE, ts);

		try
		{
			InstanceOfB1Item.TYPE.castTypeExtends(InstanceOfAItem.TYPE);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a InstanceOfB1Item, but was a InstanceOfAItem", e.getMessage());
		}

		assertEquals(null, InstanceOfB1Item.TYPE.castTypeExtends(null));
	}
}
