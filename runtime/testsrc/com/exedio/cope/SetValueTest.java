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

import static com.exedio.cope.SetValue.map;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.Serial;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class SetValueTest
{
	@Test void testNormal()
	{
		final MockSettable settable = new MockSettable("alpha");
		final SetValue<String> value = map(settable, "alphaValue");
		assertSame(settable, value.settable);
		assertSame("alphaValue", value.value);
		assertEquals("alpha=alphaValue", value.toString());
	}

	@Test void testNormal2()
	{
		final MockSettable settable = new MockSettable("beta");
		final SetValue<String> value = map(settable, "betaValue");
		assertSame(settable, value.settable);
		assertSame("betaValue", value.value);
		assertEquals("beta=betaValue", value.toString());
	}

	@Test void testNullValue()
	{
		final MockSettable settable = new MockSettable("gamma");
		final SetValue<String> value = map(settable, null);
		assertSame(settable, value.settable);
		assertSame(null, value.value);
		assertEquals("gamma=null", value.toString());
	}

	@Test void testNullValueAndNullToString()
	{
		final MockSettable settable = new MockSettable(null);
		final SetValue<String> value = map(settable, null);
		assertSame(settable, value.settable);
		assertSame(null, value.value);
		assertEquals("null=null", value.toString());
	}

	@Test void testEqualsAndHash()
	{
		final MockSettable settable = new MockSettable("alpha");
		assertEqualsAndHash(
				map(settable, "alphaValue"),
				map(settable, "alphaValue"));
		assertNotEqualsAndHash(
				map(settable, "alphaValue"),
				map(new MockSettable("other"), "alphaValue"),
				map(settable, "betaValue"),
				map(settable, null));
	}

	@Test void testNullFeature()
	{
		assertFails(
				() -> map((Settable<String>)null, "nullValue"),
				NullPointerException.class,
				"settable");
	}

	@Test void testMapAndCastToFeature()
	{
		final MockSettable settable = new MockSettable("alpha");
		final SetValue<String> value = mapAndCastToFeature(settable, "alphaValue");
		assertSame(settable, value.settable);
		assertSame("alphaValue", value.value);
		assertEquals("alpha=alphaValue", value.toString());
	}

	@Test void testMapAndCastToFeatureSettableOnly()
	{
		final MockSettableOnly settable = new MockSettableOnly();
		final SetValue<?> value = mapAndCastToFeature(settable, "alphaValue");
		assertSame(settable, value.settable);
		assertEquals("alphaValue", value.value);
		//noinspection ObjectToString
		assertEquals(settable+"=alphaValue", value.toString());
	}

	@Test void testMapAndCastToFeatureNullFeature()
	{
		assertFails(
				() -> mapAndCastToFeature(null, "nullValue"),
				NullPointerException.class,
				"settable");
	}

	private static final class MockSettable extends AbstractFeature implements Settable<String>
	{
		@Serial
		private static final long serialVersionUID = 1l;

		private final String toString;

		MockSettable(final String toString)
		{
			this.toString = toString;
		}

		@Override
		public SetValue<?>[] execute(final String value, final Item exceptionItem)
		{
			throw new RuntimeException();
		}

		@Override
		public Set<Class<? extends Throwable>> getInitialExceptions()
		{
			throw new RuntimeException();
		}

		@Override
		public Type getInitialType()
		{
			throw new RuntimeException();
		}

		@Override
		public boolean isFinal()
		{
			throw new RuntimeException();
		}

		@Override
		public boolean isMandatory()
		{
			throw new RuntimeException();
		}

		@Override
		public boolean isInitial()
		{
			throw new RuntimeException();
		}

		@Override
		@SuppressWarnings({"deprecation","unused"}) // OK: testing deprecated API
		public SetValue<String> map(final String value)
		{
			throw new RuntimeException();
		}

		@Override
		void toStringNotMounted(final StringBuilder sb, final com.exedio.cope.Type<?> defaultType)
		{
			sb.append(toString);
		}
	}

	private static final class MockSettableOnly implements Settable<String>
	{
		@Override
		public SetValue<?>[] execute(final String value, final Item exceptionItem)
		{
			throw new RuntimeException();
		}

		@Override
		public Set<Class<? extends Throwable>> getInitialExceptions()
		{
			throw new RuntimeException();
		}

		@Override
		public Type getInitialType()
		{
			throw new RuntimeException();
		}

		@Override
		public boolean isFinal()
		{
			throw new RuntimeException();
		}

		@Override
		public boolean isMandatory()
		{
			throw new RuntimeException();
		}

		@Override
		public boolean isInitial()
		{
			throw new RuntimeException();
		}

		@Override
		@SuppressWarnings({"deprecation","unused"}) // OK: testing deprecated API
		public SetValue<String> map(final String value)
		{
			throw new RuntimeException();
		}

		@Override
		public void mount(
				final AbstractType<?> type,
				final String name,
				final String string,
				final Serializable serializable,
				final AnnotatedElement annotationSource)
		{
			throw new AssertionError();
		}

		@Override
		public AbstractType<?> getAbstractType()
		{
			throw new AssertionError();
		}

		@Override
		public com.exedio.cope.Type<?> getType()
		{
			throw new AssertionError();
		}

		@Override
		public String getName()
		{
			throw new AssertionError();
		}

		@Override
		public String getID()
		{
			throw new AssertionError();
		}

		@Override
		public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
		{
			throw new AssertionError();
		}

		@Override
		public <A extends Annotation> A getAnnotation(final Class<A> annotationClass)
		{
			throw new AssertionError();
		}

		@Override
		public List<String> getLocalizationKeys()
		{
			throw new AssertionError();
		}

		@Override
		public void toString(final StringBuilder sb, final com.exedio.cope.Type<?> defaultType)
		{
			throw new AssertionError();
		}

		@Override
		public Pattern getPattern()
		{
			throw new AssertionError();
		}

		@Serial
		private static final long serialVersionUID = 1l;
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	private static <E> SetValue<E> mapAndCastToFeature(final Settable<E> settable, final E value)
	{
		return SetValue.mapAndCastToFeature(settable, value);
	}
}
