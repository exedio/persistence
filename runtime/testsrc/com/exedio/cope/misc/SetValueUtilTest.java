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

package com.exedio.cope.misc;

import static com.exedio.cope.misc.SetValueUtil.add;
import static com.exedio.cope.misc.SetValueUtil.getFirst;
import static com.exedio.cope.misc.SetValueUtil.getFirstMapping;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.StringField;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SetValueUtilTest
{
	@Test void testGetFirst()
	{
		final StringField f1 = new StringField();
		final StringField f2 = new StringField();
		final ArrayList<SetValue<?>> l = new ArrayList<>();

		final SetValue<String> m1 = f1.map("value1a");
		l.add(m1);
		assertGetFirst(m1, "value1a", l, f1);
		assertGetFirst(null, null,    l, f2);

		l.add(f1.map("value1b"));
		assertGetFirst(m1, "value1a", l, f1);
		assertGetFirst(null, null,    l, f2);

		final SetValue<String> m2 = f2.map("value2");
		l.add(m2);
		assertGetFirst(m1, "value1a", l, f1);
		assertGetFirst(m2, "value2",  l, f2);
	}

	private static <E> void assertGetFirst(
			final SetValue<E> expectedMapping,
			final E expectedValue,
			final List<SetValue<?>> setValues,
			final Settable<E> settable)
	{
		final SetValue<?>[] a = new SetValue<?>[]{};
		assertSame  (expectedMapping, getFirstMapping(setValues, settable));
		assertEquals(expectedValue,   getFirst       (setValues, settable));
		assertSame  (expectedMapping, getFirstMapping(setValues.toArray(a), settable));
		assertEquals(expectedValue,   getFirst       (setValues.toArray(a), settable));
	}

	@Test void testGetFirstNullSetValues()
	{
		final StringField f = new StringField();
		try
		{
			getFirst((SetValue<?>[])null, f);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			getFirst(asList((SetValue<?>[])null), f);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test void testGetFirstNullSettable()
	{
		// Needed because with just null I do get a compiler warning on JDK 1.8:
		// warning: [deprecation] assertEquals(Object[],Object[]) in Assert has been deprecated
		final Settable<Object> nullSettable = null;

		// TODO should rather throw an exception
		assertEquals(null, getFirst(new SetValue<?>[]{}, nullSettable));
		assertEquals(null, getFirst(asList(new SetValue<?>[]{}), nullSettable));
	}

	@Test void testAdd()
	{
		final SetValue<?> m1 = new StringField().map("v1");
		final SetValue<?> m2 = new StringField().map("v2");
		final SetValue<?> mX = new StringField().map("vX");

		assertEquals(asList(mX        ), asList(add(new SetValue<?>[]{      }, mX)));
		assertEquals(asList(m1,     mX), asList(add(new SetValue<?>[]{m1    }, mX)));
		assertEquals(asList(m1, m2, mX), asList(add(new SetValue<?>[]{m1, m2}, mX)));
	}

	@Test void testAddNullSetValues()
	{
		final SetValue<?> m = new StringField().map("vX");
		try
		{
			add(null, m);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test void testAddNullValue()
	{
		try
		{
			add(new SetValue<?>[]{}, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("value", e.getMessage());
		}
	}
}
