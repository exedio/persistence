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

import java.lang.reflect.Type;
import java.util.Set;
import junit.framework.TestCase;

public class SetValueTest extends TestCase
{
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

		@Deprecated
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

	public void testIt()
	{
		final MockSettable alpha = new MockSettable("alpha");
		final SetValue<?> alphaValue = SetValue.map(alpha, "alphaValue");
		assertEquals("alpha=alphaValue", alphaValue.toString());

		final MockSettable beta = new MockSettable("beta");
		final SetValue<?> betaValue = SetValue.map(beta, "betaValue");
		assertEquals("beta=betaValue", betaValue.toString());

		final MockSettable nulla = new MockSettable(null);
		final SetValue<?> nullValue = SetValue.map(nulla, null);
		assertEquals("null=null", nullValue.toString());
	}
}
