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
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.StringField;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import org.junit.Test;

public class SetValueUtilTest
{
	@Test public void testGetFirst()
	{
		final StringField f1 = new StringField();
		final StringField f2 = new StringField();
		final ArrayList<SetValue<?>> l = new ArrayList<>();
		final SetValue<?>[] a = new SetValue<?>[]{};

		l.add(f1.map("value1a"));
		assertEquals("value1a", getFirst(l, f1));
		assertEquals("value1a", getFirst(l.toArray(a), f1));
		assertEquals(null, getFirst(l, f2));
		assertEquals(null, getFirst(l.toArray(a), f2));

		l.add(f1.map("value1b"));
		assertEquals("value1a", getFirst(l, f1));
		assertEquals("value1a", getFirst(l.toArray(a), f1));
		assertEquals(null, getFirst(l, f2));
		assertEquals(null, getFirst(l.toArray(a), f2));

		l.add(f2.map("value2"));
		assertEquals("value1a", getFirst(l, f1));
		assertEquals("value1a", getFirst(l.toArray(a), f1));
		assertEquals("value2", getFirst(l, f2));
		assertEquals("value2", getFirst(l.toArray(a), f2));
	}

	@Test public void testGetFirstNullSetValues()
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

	@SuppressFBWarnings("NP_LOAD_OF_KNOWN_NULL_VALUE")
	@Test public void testGetFirstNullSettable()
	{
		// Needed because with just null I do get a compiler warning on JDK 1.8:
		// warning: [deprecation] assertEquals(Object[],Object[]) in Assert has been deprecated
		final Settable<Object> nullSettable = null;

		// TODO should rather throw an exception
		assertEquals(null, getFirst(new SetValue<?>[]{}, nullSettable));
		assertEquals(null, getFirst(asList(new SetValue<?>[]{}), nullSettable));
	}

	@Test public void testAdd()
	{
		final SetValue<?> m1 = new StringField().map("v1");
		final SetValue<?> m2 = new StringField().map("v2");
		final SetValue<?> mX = new StringField().map("vX");

		assertEquals(asList(mX        ), asList(add(new SetValue<?>[]{      }, mX)));
		assertEquals(asList(m1,     mX), asList(add(new SetValue<?>[]{m1    }, mX)));
		assertEquals(asList(m1, m2, mX), asList(add(new SetValue<?>[]{m1, m2}, mX)));
	}

	@Test public void testAddNullSetValues()
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

	@Test public void testAddNullValue()
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