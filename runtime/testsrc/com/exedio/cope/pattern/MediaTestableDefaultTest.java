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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.AbstractType;
import com.exedio.cope.Pattern;
import com.exedio.cope.Type;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class MediaTestableDefaultTest
{
	@Test void testOk() throws Exception
	{
		//noinspection SerializableInnerClassWithNonSerializableOuterClass OK: just a test
		final var tests = new MyFeature() {
			@Deprecated
			@Override
			public void test()
			{
			}
			@Serial private static final long serialVersionUID = 1l;
		}.getTests();
		final Callable<?> test = tests.get(0);
		assertEquals("all", test.toString());
		assertEquals(null, test.call());
		assertEquals(1, tests.size());
	}
	@Test void testFailure()
	{
		//noinspection SerializableInnerClassWithNonSerializableOuterClass OK: just a test
		final var tests = new MyFeature() {
			@Deprecated
			@Override
			public void test() throws Exception
			{
				throw new IOException("ZACK");
			}
			@Serial private static final long serialVersionUID = 1l;
		}.getTests();
		final Callable<?> test = tests.get(0);
		assertEquals("all", test.toString());
		assertFails(test::call, IOException.class, "ZACK");
		assertEquals(1, tests.size());
	}

	private abstract static class MyFeature implements MediaTestable
	{
		@Serial
		private static final long serialVersionUID = 1l;

		@Override
		public void mount(
				final AbstractType<?> type,
				final String name,
				final String string,
				final Serializable serializable,
				final AnnotatedElement annotationSource)
		{
			throw new AssertionFailedError();
		}

		@Override
		public AbstractType<?> getAbstractType()
		{
			throw new AssertionFailedError();
		}

		@Override
		public Type<?> getType()
		{
			throw new AssertionFailedError();
		}

		@Override
		public String getName()
		{
			throw new AssertionFailedError();
		}

		@Override
		public String getID()
		{
			throw new AssertionFailedError();
		}

		@Override
		public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
		{
			throw new AssertionFailedError();
		}

		@Override
		public <A extends Annotation> A getAnnotation(final Class<A> annotationClass)
		{
			throw new AssertionFailedError();
		}

		@Override
		public List<String> getLocalizationKeys()
		{
			throw new AssertionFailedError();
		}

		@Override
		public void toString(final StringBuilder sb, final Type<?> defaultType)
		{
			throw new AssertionFailedError();
		}

		@Override
		public Pattern getPattern()
		{
			throw new AssertionFailedError();
		}
	}
}
