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

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;

import junit.framework.TestCase;

public class SetValueTest extends TestCase
{
	private static final class MockSettable extends Feature implements Settable<String>
	{
		MockSettable()
		{
			// make non-private
		}

		public SetValue[] execute(final String value, final Item exceptionItem)
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

		public SetValue map(final String value)
		{
			throw new RuntimeException();
		}
	}

	public void testIt()
	{
		final MockSettable alpha = new MockSettable();
		final SetValue alphaValue = SetValue.map(alpha, "alphaValue");
		assertEquals(alpha.toString()+"=alphaValue", alphaValue.toString());

		final MockSettable beta = new MockSettable();
		final SetValue betaValue = SetValue.map(beta, "betaValue");
		assertEquals(beta.toString()+"=betaValue", betaValue.toString());

		final MockSettable nulla = new MockSettable();
		final SetValue nullValue = SetValue.map(nulla, null);
		assertEquals(nulla.toString()+"=null", nullValue.toString());

		assertEquals(
				"["+alpha.toString()+"=alphaValue, "+beta.toString()+"=betaValue, "+nulla.toString()+"=null]",
				Arrays.asList(alphaValue, betaValue, nullValue).toString());
	}
}
