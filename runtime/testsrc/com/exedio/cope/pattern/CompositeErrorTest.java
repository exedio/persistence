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

package com.exedio.cope.pattern;

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.pattern.CompositeField.create;
import static com.exedio.cope.pattern.CompositeType.get;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.BooleanField;
import com.exedio.cope.DateField;
import com.exedio.cope.Feature;
import com.exedio.cope.Field;
import com.exedio.cope.FunctionField;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class CompositeErrorTest
{
	@Test void testNull()
	{
		assertFails(() ->
			create(null),
			NullPointerException.class,
			"javaClass");
	}

	@Test void testNullGet()
	{
		assertFails(() ->
			get(null),
			NullPointerException.class,
			"javaClass");
	}


	@Test void testNonFinal()
	{
		assertFails(() ->
			create(NonFinal.class),
			IllegalArgumentException.class,
			"CompositeField requires a final class: " + NonFinal.class.getName());
	}

	@Test void testNonFinalGet()
	{
		assertFails(() ->
			get(NonFinal.class),
			IllegalArgumentException.class,
			"CompositeField requires a final class: " + NonFinal.class.getName());
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2, comments=false)
	static class NonFinal extends Composite
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}


	@Test void testNoConstructor()
	{
		final Throwable e = assertFails(() ->
			create(NoConstructor.class),
			IllegalArgumentException.class,
			NoConstructor.class.getName() + " does not have a constructor NoConstructor(" +
			SetValue.class.getName() + "[])");
		assertEquals(NoSuchMethodException.class, e.getCause().getClass());
	}

	@Test void testNoConstructorGet()
	{
		final Throwable e = assertFails(() ->
			get(NoConstructor.class),
			IllegalArgumentException.class,
			NoConstructor.class.getName() + " does not have a constructor NoConstructor(" +
			SetValue.class.getName() + "[])");
		assertEquals(NoSuchMethodException.class, e.getCause().getClass());
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class NoConstructor extends Composite
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}


	@Test void testNoFields()
	{
		assertFails(() ->
			create(NoFields.class),
			IllegalArgumentException.class,
			"composite has no templates: " + NoFields.class.getName());
	}

	@Test void testNoFieldsGet()
	{
		assertFails(() ->
			get(NoFields.class),
			IllegalArgumentException.class,
			"composite has no templates: " + NoFields.class.getName());
	}

	@WrapperType(type=NONE, constructor=NONE, indent=2, comments=false)
	static final class NoFields extends Composite
	{
		@com.exedio.cope.instrument.Generated
		private NoFields(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}


	@Test void testNullField()
	{
		assertFails(() ->
			create(NullField.class),
			NullPointerException.class,
			NullField.class.getName() + "#nullField");
	}

	@Test void testNullFieldGet()
	{
		assertFails(() ->
			get(NullField.class),
			NullPointerException.class,
			NullField.class.getName() + "#nullField");
	}

	@WrapperIgnore // instrumentor fails on null field
	static final class NullField extends Composite
	{
		private static final long serialVersionUID = 1l;
		private NullField(final SetValue<?>[] setValues) { super(setValues); }
		@SuppressWarnings("unused") // OK: test bad API usage
		static final Field<?> nullField = null;
	}


	@Test void testNotFunctionField()
	{
		assertFails(() ->
			create(NotFunctionField.class),
			IllegalArgumentException.class,
			NotFunctionField.class.getName() + "#notFunctionField must be an instance of " +
			FunctionField.class);
	}

	@Test void testNotFunctionFieldGet()
	{
		assertFails(() ->
			get(NotFunctionField.class),
			IllegalArgumentException.class,
			NotFunctionField.class.getName() + "#notFunctionField must be an instance of " +
			FunctionField.class);
	}

	@WrapperType(type=NONE, constructor=NONE, indent=2, comments=false)
	static final class NotFunctionField extends Composite
	{
		@SuppressWarnings("unused") // OK: test bad API usage
		static final Feature notFunctionField = MapField.create(new StringField(), new StringField());

		@com.exedio.cope.instrument.Generated
		private NotFunctionField(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}


	@Test void testCompositeItself()
	{
		assertFails(() ->
			create(Composite.class),
			IllegalArgumentException.class,
			"CompositeField requires a subclass of " + Composite.class.getName() +
			" but not Composite itself");
	}

	@Test void testCompositeItselfGet()
	{
		assertFails(() ->
			get(Composite.class),
			IllegalArgumentException.class,
			"CompositeField requires a subclass of " + Composite.class.getName() +
			" but not Composite itself");
	}


	@Test void testFinalField()
	{
		assertFails(() ->
			create(FinalField.class),
			IllegalArgumentException.class,
			"final fields not supported: " + FinalField.class.getName() + "#finalField");
	}

	@Test void testFinalFieldGet()
	{
		assertFails(() ->
			get(FinalField.class),
			IllegalArgumentException.class,
			"final fields not supported: " + FinalField.class.getName() + "#finalField");
	}

	@WrapperType(type=NONE, constructor=NONE, indent=2, comments=false)
	static final class FinalField extends Composite
	{
		@SuppressWarnings("unused") // OK: test bad API usage
		@WrapperIgnore static final BooleanField finalField = new BooleanField().toFinal();

		@com.exedio.cope.instrument.Generated
		private FinalField(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}


	@Test void testNonConstantDefaultField()
	{
		assertFails(() ->
			create(NonConstantDefaultField.class),
			IllegalArgumentException.class,
			"fields with non-constant defaults are not supported: " +
			NonConstantDefaultField.class.getName() + "#defaultNowField");
	}

	@Test void testNonConstantDefaultFieldGet()
	{
		assertFails(() ->
			get(NonConstantDefaultField.class),
			IllegalArgumentException.class,
			"fields with non-constant defaults are not supported: " +
			NonConstantDefaultField.class.getName() + "#defaultNowField");
	}

	@WrapperType(type=NONE, constructor=NONE, indent=2, comments=false)
	static final class NonConstantDefaultField extends Composite
	{
		@SuppressWarnings("unused") // OK: test bad API usage
		@WrapperIgnore static final DateField defaultNowField = new DateField().defaultToNow();

		@com.exedio.cope.instrument.Generated
		private NonConstantDefaultField(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}


	@SuppressWarnings("unchecked") // OK: test bad API usage
	@Test void testNoComposite()
	{
		assertFails(() ->
			create((Class)CompositeErrorTest.class),
			IllegalArgumentException.class,
			"CompositeField requires a subclass of " + Composite.class.getName() + ": " +
			CompositeErrorTest.class.getName());
	}

	@SuppressWarnings("unchecked") // OK: test bad API usage
	@Test void testNoCompositeGet()
	{
		assertFails(() ->
			get((Class)CompositeErrorTest.class),
			IllegalArgumentException.class,
			"CompositeField requires a subclass of " + Composite.class.getName() + ": " +
			CompositeErrorTest.class.getName());
	}
}
