/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.EqualsAssert.assertNotEqualsAndHash;
import static com.exedio.cope.SetValue.map;

import java.lang.reflect.Type;
import java.util.Set;
import junit.framework.TestCase;

public class SetValueTest extends TestCase
{
	public void testNormal()
	{
		final MockSettable settable = new MockSettable("alpha");
		final SetValue<?> value = map(settable, "alphaValue");
		assertEquals("alpha=alphaValue", value.toString());
	}

	public void testNormal2()
	{
		final MockSettable settable = new MockSettable("beta");
		final SetValue<?> value = map(settable, "betaValue");
		assertEquals("beta=betaValue", value.toString());
	}

	public void testNullValue()
	{
		final MockSettable settable = new MockSettable("gamma");
		final SetValue<?> value = map(settable, null);
		assertEquals("gamma=null", value.toString());
	}

	public void testNullValueAndNullToString()
	{
		final MockSettable settable = new MockSettable(null);
		final SetValue<?> value = map(settable, null);
		assertEquals("null=null", value.toString());
	}

	public void testEqualsAndHash()
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

	public void testNullFeature()
	{
		try
		{
			// NOTE
			// The <String,StringField> below is needed for
			// javac of JDK 1.6, but not for JDK 1.7
			SetValue.<String,StringField>map(null, "nullValue");
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("settable", e.getMessage());
		}
	}

	private static final class MockSettable extends Feature implements Settable<String>
	{
		private static final long serialVersionUID = 1l;

		private final String toString;

		MockSettable(final String toString)
		{
			this.toString = toString;
		}

		public SetValue<?>[] execute(final String value, final Item exceptionItem)
		{
			throw new RuntimeException();
		}

		public Set<Class<? extends Throwable>> getInitialExceptions()
		{
			throw new RuntimeException();
		}

		public Type getInitialType()
		{
			throw new RuntimeException();
		}

		public boolean isFinal()
		{
			throw new RuntimeException();
		}

		public boolean isMandatory()
		{
			throw new RuntimeException();
		}

		public boolean isInitial()
		{
			throw new RuntimeException();
		}

		public SetValue<String> map(final String value)
		{
			throw new RuntimeException();
		}

		@Override
		void toStringNotMounted(final StringBuilder bf, final com.exedio.cope.Type<?> defaultType)
		{
			bf.append(toString);
		}
	}
}
