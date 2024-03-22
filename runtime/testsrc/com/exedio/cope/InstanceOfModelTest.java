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

import static com.exedio.cope.InstanceOfRefItem.ref;
import static com.exedio.cope.RuntimeTester.assertFieldsCovered;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class InstanceOfModelTest
{
	public static final Model MODEL = new Model(
			InstanceOfAItem.TYPE,
			InstanceOfB1Item.TYPE,
			InstanceOfB2Item.TYPE,
			InstanceOfC1Item.TYPE,
			InstanceOfRefItem.TYPE);

	private final Type<InstanceOfAItem > TYPE_A  = InstanceOfAItem .TYPE;
	private final Type<InstanceOfB1Item> TYPE_B1 = InstanceOfB1Item.TYPE;
	private final Type<InstanceOfB2Item> TYPE_B2 = InstanceOfB2Item.TYPE;
	private final Type<InstanceOfC1Item> TYPE_C1 = InstanceOfC1Item.TYPE;

	@Test void testFeatures()
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

	@Test void testFieldsCovered()
	{
		assertFieldsCovered(asList(ref), ref.instanceOf(TYPE_B1));
		assertFieldsCovered(asList(), TYPE_A.getThis().instanceOf(TYPE_B1));
	}

	@Test void testNot()
	{
		final Condition pos = TYPE_A.getThis().   instanceOf(TYPE_C1);
		final Condition neg = TYPE_A.getThis().notInstanceOf(TYPE_C1);
		assertEquals("InstanceOfAItem.this "+ "instanceOf InstanceOfC1Item", pos      .toString());
		assertEquals("!(InstanceOfAItem.this not instanceOf InstanceOfC1Item)", neg.not().toString());
		assertEquals("InstanceOfAItem.this not instanceOf InstanceOfC1Item", neg      .toString());
		assertEquals("!(InstanceOfAItem.this instanceOf InstanceOfC1Item)",  pos.not().toString());
	}

	@Test void testEqualsHashCode()
	{
		assertNotEqualsAndHash(
				TYPE_A.getThis().instanceOf(TYPE_B1),
				TYPE_A.getThis().instanceOf(TYPE_B1, TYPE_B2),
				ref             .instanceOf(TYPE_B1),
				TYPE_A.getThis().notInstanceOf(TYPE_B1));
		assertEqualsAndHash(
				TYPE_A.getThis().instanceOf(TYPE_B1),
				TYPE_A.getThis().instanceOf(TYPE_B1));
		assertEqualsAndHash(
				TYPE_A.getThis().instanceOf(TYPE_B1, TYPE_B2),
				TYPE_A.getThis().instanceOf(TYPE_B1, TYPE_B2));
		assertEqualsAndHash(
				ref.instanceOf(TYPE_B1),
				ref.instanceOf(TYPE_B1));
	}

	@Test void testToString()
	{
		assertEquals("InstanceOfAItem.this instanceOf InstanceOfC1Item", TYPE_A.getThis().instanceOf(TYPE_C1).toString());
		assertEquals("InstanceOfAItem.this instanceOf [InstanceOfC1Item, InstanceOfB1Item]", TYPE_A.getThis().instanceOf(TYPE_C1, TYPE_B1).toString());
		assertEquals("InstanceOfAItem.this not instanceOf InstanceOfC1Item", TYPE_A.getThis().notInstanceOf(TYPE_C1).toString());
		assertEquals("InstanceOfAItem.this not instanceOf [InstanceOfC1Item, InstanceOfB1Item]", TYPE_A.getThis().notInstanceOf(TYPE_C1, TYPE_B1).toString());
	}

	@Test void testFails()
	{
		try
		{
			//noinspection rawtypes OK: otherwise "Overly strong type cast" complains
			TYPE_A.getThis().instanceOf((Type[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("types", e.getMessage());
		}
		try
		{
			TYPE_A.getThis().instanceOf(new Type<?>[]{});
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("types must not be empty", e.getMessage());
		}
		try
		{
			TYPE_A.getThis().instanceOf(new Type<?>[]{null});
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("types[0]", e.getMessage());
		}
	}

	@Test void testAsTypeSame()
	{
		assertSame(InstanceOfAItem.TYPE, InstanceOfAItem.TYPE.as(InstanceOfAItem.class));
	}
	@Test void testAsTypeSameExtends()
	{
		assertSame(InstanceOfAItem.TYPE, InstanceOfAItem.TYPE.asExtends(InstanceOfAItem.class));
	}
	@Test void testAsTypeSameSuper()
	{
		assertSame(InstanceOfAItem.TYPE, InstanceOfAItem.TYPE.asSuper(InstanceOfAItem.class));
	}
	@Test void testAsFieldSame()
	{
		assertSame(ref, ref.as(InstanceOfAItem.class));
	}
	@Test void testAsFieldSameExtends()
	{
		assertSame(ref, ref.asExtends(InstanceOfAItem.class));
	}
	@Test void testAsFieldSameSuper()
	{
		assertSame(ref, ref.asSuper(InstanceOfAItem.class));
	}

	@Test void testAsTypeChildReverse()
	{
		assertFails(() ->
			InstanceOfAItem.TYPE.as(InstanceOfB1Item.class),
			ClassCastException.class,
			messageAsTypeFails(InstanceOfB1Item.class, InstanceOfAItem.class));
	}
	@Test void testAsTypeChildReverseExtends()
	{
		assertFails(() ->
			InstanceOfAItem.TYPE.asExtends(InstanceOfB1Item.class),
			ClassCastException.class,
			messageAsTypeFailsExtends(InstanceOfB1Item.class, InstanceOfAItem.class));
	}
	@Test void testAsTypeChildReverseSuper()
	{
		assertSame(InstanceOfAItem.TYPE, InstanceOfAItem.TYPE.asSuper(InstanceOfB1Item.class));
	}
	@Test void testAsFieldChildReverse()
	{
		assertFails(() ->
			ref.as(InstanceOfB1Item.class),
			ClassCastException.class,
			messageAsFieldFails(InstanceOfB1Item.class, InstanceOfAItem.class));
	}
	@Test void testAsFieldChildReverseExtends()
	{
		assertFails(() ->
			ref.asExtends(InstanceOfB1Item.class),
			ClassCastException.class,
			messageAsFieldFailsExtends(InstanceOfB1Item.class, InstanceOfAItem.class));
	}
	@Test void testAsFieldChildReverseSuper()
	{
		assertSame(ref, ref.asSuper(InstanceOfB1Item.class));
	}

	@Test void testAsTypeSameSub()
	{
		assertSame(InstanceOfB2Item.TYPE, InstanceOfB2Item.TYPE.as(InstanceOfB2Item.class));
	}
	@Test void testAsTypeSameSubExtends()
	{
		assertSame(InstanceOfB2Item.TYPE, InstanceOfB2Item.TYPE.asExtends(InstanceOfB2Item.class));
	}
	@Test void testAsTypeSameSubSuper()
	{
		assertSame(InstanceOfB2Item.TYPE, InstanceOfB2Item.TYPE.asSuper(InstanceOfB2Item.class));
	}
	@Test void testAsFieldSameSub()
	{
		assertSame(InstanceOfRefItem.refb2, InstanceOfRefItem.refb2.as(InstanceOfB2Item.class));
	}
	@Test void testAsFieldSameSubExtends()
	{
		assertSame(InstanceOfRefItem.refb2, InstanceOfRefItem.refb2.asExtends(InstanceOfB2Item.class));
	}
	@Test void testAsFieldSameSubSuper()
	{
		assertSame(InstanceOfRefItem.refb2, InstanceOfRefItem.refb2.asSuper(InstanceOfB2Item.class));
	}

	@Test void testAsTypeBrother()
	{
		assertFails(() ->
			InstanceOfB2Item.TYPE.as(InstanceOfB1Item.class),
			ClassCastException.class,
			messageAsTypeFails(InstanceOfB1Item.class, InstanceOfB2Item.class));
	}
	@Test void testAsTypeBrotherExtends()
	{
		assertFails(() ->
			InstanceOfB2Item.TYPE.asExtends(InstanceOfB1Item.class),
			ClassCastException.class,
			messageAsTypeFailsExtends(InstanceOfB1Item.class, InstanceOfB2Item.class));
	}
	@Test void testAsTypeBrotherSuper()
	{
		assertFails(() ->
			InstanceOfB2Item.TYPE.asSuper(InstanceOfB1Item.class),
			ClassCastException.class,
			messageAsTypeFailsSuper(InstanceOfB1Item.class, InstanceOfB2Item.class));
	}
	@Test void testAsFieldBrother()
	{
		assertFails(() ->
			InstanceOfRefItem.refb2.as(InstanceOfB1Item.class),
			ClassCastException.class,
			messageAsFieldFails(InstanceOfB1Item.class, InstanceOfB2Item.class));
	}
	@Test void testAsFieldBrotherExtends()
	{
		assertFails(() ->
			InstanceOfRefItem.refb2.asExtends(InstanceOfB1Item.class),
			ClassCastException.class,
			messageAsFieldFailsExtends(InstanceOfB1Item.class, InstanceOfB2Item.class));
	}
	@Test void testAsFieldBrotherSuper()
	{
		assertFails(() ->
			InstanceOfRefItem.refb2.asSuper(InstanceOfB1Item.class),
			ClassCastException.class,
			messageAsFieldFailsSuper(InstanceOfB1Item.class, InstanceOfB2Item.class));
	}

	@Test void testAsTypeSameChildExtends()
	{
		assertFails(() ->
			InstanceOfB2Item.TYPE.as(InstanceOfAItem.class),
			ClassCastException.class,
			messageAsTypeFails(InstanceOfAItem.class, InstanceOfB2Item.class));
	}
	@Test void testAsTypeSameChildOkSuper()
	{
		assertFails(() ->
			InstanceOfB2Item.TYPE.asSuper(InstanceOfAItem.class),
			ClassCastException.class,
			messageAsTypeFailsSuper(InstanceOfAItem.class, InstanceOfB2Item.class));
	}
	@Test void testAsTypeSameChildOk()
	{
		assertSame(InstanceOfB2Item.TYPE, InstanceOfB2Item.TYPE.asExtends(InstanceOfAItem.class));
	}
	@Test void testAsFieldSameChildExtends()
	{
		assertFails(() ->
			InstanceOfRefItem.refb2.as(InstanceOfAItem.class),
			ClassCastException.class,
			messageAsFieldFails(InstanceOfAItem.class, InstanceOfB2Item.class));
	}
	@Test void testAsFieldSameChildSuper()
	{
		assertFails(() ->
			InstanceOfRefItem.refb2.asSuper(InstanceOfAItem.class),
			ClassCastException.class,
			messageAsFieldFailsSuper(InstanceOfAItem.class, InstanceOfB2Item.class));
	}
	@Test void testAsFieldSameChildOk()
	{
		assertSame(InstanceOfRefItem.refb2, InstanceOfRefItem.refb2.asExtends(InstanceOfAItem.class));
	}

	private static String messageAsTypeFails(
			final Class<? extends InstanceOfAItem> expected,
			final Class<? extends InstanceOfAItem> actual)
	{
		return
				"expected " + expected.getName() + ", " +
				"but was " + actual.getName();
	}

	private static String messageAsTypeFailsExtends(
			final Class<? extends InstanceOfAItem> expected,
			final Class<? extends InstanceOfAItem> actual)
	{
		return
				"expected ? extends " + expected.getName() + ", " +
				"but was " + actual.getName();
	}

	private static String messageAsTypeFailsSuper(
			final Class<? extends InstanceOfAItem> expected,
			final Class<? extends InstanceOfAItem> actual)
	{
		return
				"expected ? super " + expected.getName() + ", " +
				"but was " + actual.getName();
	}

	private static String messageAsFieldFails(
			final Class<? extends InstanceOfAItem> expected,
			final Class<? extends InstanceOfAItem> actual)
	{
		return
				"expected a " + ItemField.class.getName() + '<' + expected.getName() + ">, " +
				"but was a " + ItemField.class.getName() + '<' + actual.getName() + '>';
	}

	private static String messageAsFieldFailsExtends(
			final Class<? extends InstanceOfAItem> expected,
			final Class<? extends InstanceOfAItem> actual)
	{
		return
				"expected a " + ItemField.class.getName() + "<? extends " + expected.getName() + ">, " +
				"but was a " + ItemField.class.getName() + '<' + actual.getName() + '>';
	}

	private static String messageAsFieldFailsSuper(
			final Class<? extends InstanceOfAItem> expected,
			final Class<? extends InstanceOfAItem> actual)
	{
		return
				"expected a " + ItemField.class.getName() + "<? super " + expected.getName() + ">, " +
				"but was a " + ItemField.class.getName() + '<' + actual.getName() + '>';
	}

	@Test void testGetSubTypes()
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

	@Test void testGetSubTypesTransitively()
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

	@Test void testTypesOfInstances()
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

	@Test void testCastTypeExtends()
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
