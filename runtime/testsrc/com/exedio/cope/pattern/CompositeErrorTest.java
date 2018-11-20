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

import static com.exedio.cope.pattern.CompositeField.create;
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


	@Test void testNonFinal()
	{
		assertFails(() ->
			create(NonFinal.class),
			IllegalArgumentException.class,
			"CompositeField requires a final class: " + NonFinal.class.getName());
	}

	@WrapperIgnore
	static class NonFinal extends Composite
	{
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

	@WrapperIgnore
	static final class NoConstructor extends Composite
	{
		private static final long serialVersionUID = 1l;
	}


	@Test void testNoFields()
	{
		assertFails(() ->
			create(NoFields.class),
			IllegalArgumentException.class,
			"composite has no templates: " + NoFields.class.getName());
	}

	@WrapperIgnore
	static final class NoFields extends Composite
	{
		private static final long serialVersionUID = 1l;
		private NoFields(final SetValue<?>[] setValues) { super(setValues); }
	}


	@Test void testNullField()
	{
		assertFails(() ->
			create(NullField.class),
			NullPointerException.class,
			NullField.class.getName() + "#nullField");
	}

	@WrapperIgnore
	static final class NullField extends Composite
	{
		private static final long serialVersionUID = 1l;
		private NullField(final SetValue<?>[] setValues) { super(setValues); }
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

	@WrapperIgnore
	static final class NotFunctionField extends Composite
	{
		private static final long serialVersionUID = 1l;
		private NotFunctionField(final SetValue<?>[] setValues) { super(setValues); }
		static final Feature notFunctionField = MapField.create(new StringField(), new StringField());
	}


	@Test void testCompositeItself()
	{
		assertFails(() ->
			create(Composite.class),
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

	@WrapperIgnore
	static final class FinalField extends Composite
	{
		private static final long serialVersionUID = 1l;
		private FinalField(final SetValue<?>[] setValues) { super(setValues); }
		static final BooleanField finalField = new BooleanField().toFinal();
	}


	@Test void testNonConstantDefaultField()
	{
		assertFails(() ->
			create(NonConstantDefaultField.class),
			IllegalArgumentException.class,
			"fields with non-constant defaults are not supported: " +
			NonConstantDefaultField.class.getName() + "#defaultNowField");
	}

	@WrapperIgnore
	static final class NonConstantDefaultField extends Composite
	{
		private static final long serialVersionUID = 1l;
		private NonConstantDefaultField(final SetValue<?>[] setValues) { super(setValues); }
		static final DateField defaultNowField = new DateField().defaultToNow();
	}


	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
	@Test void testNoComposite()
	{
		assertFails(() ->
			create((Class)CompositeErrorTest.class),
			IllegalArgumentException.class,
			"CompositeField requires a subclass of " + Composite.class.getName() + ": " +
			CompositeErrorTest.class.getName());
	}
}
