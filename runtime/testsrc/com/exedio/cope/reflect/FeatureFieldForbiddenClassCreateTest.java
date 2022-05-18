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

package com.exedio.cope.reflect;

import static com.exedio.cope.reflect.FeatureField.create;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySortedSet;
import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Feature;
import com.exedio.cope.Field;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Sequence;
import com.exedio.cope.StringField;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class FeatureFieldForbiddenClassCreateTest
{
	@Test void testIt()
	{
		final FeatureField<Feature> f = create();
		assertEquals(Feature.class, f.getValueClass());
		assertEqualsUnmodifiable(emptySortedSet(), f.getForbiddenValueClasses());

		final FeatureField<Feature> f2 = f.forbid(StringField.class);
		assertEquals(Feature.class, f.getValueClass());
		assertEquals(Feature.class, f2.getValueClass());
		assertEqualsUnmodifiable(emptySortedSet(), f.getForbiddenValueClasses());
		assertEqualsUnmodifiable(singleton(StringField.class), f2.getForbiddenValueClasses());

		final FeatureField<Feature> f3 = f2.forbid(Sequence.class);
		assertEquals(Feature.class, f.getValueClass());
		assertEquals(Feature.class, f2.getValueClass());
		assertEquals(Feature.class, f3.getValueClass());
		assertEqualsUnmodifiable(emptySortedSet(), f.getForbiddenValueClasses());
		assertEqualsUnmodifiable(singleton(StringField.class), f2.getForbiddenValueClasses());
		assertEqualsUnmodifiable(new HashSet<>(asList(StringField.class, Sequence.class)), f3.getForbiddenValueClasses());
	}
	@Test void testValueClassItself()
	{
		@SuppressWarnings("rawtypes")
		final FeatureField<Field> f = create(Field.class);
		assertEquals(Field.class, f.getValueClass());
		assertEqualsUnmodifiable(emptySortedSet(), f.getForbiddenValueClasses());

		assertFails(
				() -> f.forbid(Field.class),
				IllegalArgumentException.class,
				"expected a subclass of " + Field.class.getName() + ", " +
				"but was that class itself");
	}
	@Test void testValueClassSuper()
	{
		@SuppressWarnings("rawtypes")
		final FeatureField<Field> f = create(Field.class);
		assertEquals(Field.class, f.getValueClass());
		assertEqualsUnmodifiable(emptySortedSet(), f.getForbiddenValueClasses());

		@SuppressWarnings({"rawtypes","unchecked"})
		final Class<Field<?>> c = (Class<Field<?>>)(Class)Feature.class;
		assertFails(
				() -> f.forbid(c),
				ClassCastException.class,
				"expected a subclass of " + Field.class.getName() + ", " +
				"but was " + Feature.class.getName());
	}
	@Test void testAlreadyForbiddenSuper()
	{
		final FeatureField<Feature> f = create(Feature.class).forbid(FunctionField.class);

		assertFails(
				() -> f.forbid(Field.class),
				IllegalArgumentException.class,
				"com.exedio.cope.Field must not be a super class " +
				"of the already forbidden class com.exedio.cope.FunctionField");
	}
	@Test void testAlreadyForbiddenSub()
	{
		final FeatureField<Feature> f = create(Feature.class).forbid(FunctionField.class);

		assertFails(
				() -> f.forbid(StringField.class),
				IllegalArgumentException.class,
				"com.exedio.cope.StringField must not be a subclass " +
				"of the already forbidden class com.exedio.cope.FunctionField");
	}
	@Test void testNull()
	{
		final FeatureField<Feature> f = create();
		assertEquals(Feature.class, f.getValueClass());
		assertEqualsUnmodifiable(emptySortedSet(), f.getForbiddenValueClasses());

		assertFails(
				() -> f.forbid(null),
				NullPointerException.class,
				"valueClass");
	}
}
