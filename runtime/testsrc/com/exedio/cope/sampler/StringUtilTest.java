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

package com.exedio.cope.sampler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import org.junit.jupiter.api.Test;

public class StringUtilTest
{
	@Test void testNormal()
	{
		final StringField f = new StringField().lengthMax(8);
		assertIt(null, f, null);
		assertIt(null, f, "");
		assertIt("1",        f, "1");
		assertIt("12",       f, "12");
		assertIt("123",      f, "123");
		assertIt("1234",     f, "1234");
		assertIt("12345",    f, "12345");
		assertIt("123456",   f, "123456");
		assertIt("1234567",  f, "1234567");
		assertIt("12345678", f, "12345678");
		assertIt("1234 ...", f, "123456789");
		assertIt("1234 ...", f, "1234567890");
	}

	@Test void testMinimal()
	{
		final StringField f = new StringField().lengthMax(4);
		assertIt(null, f, null);
		assertIt(null, f, "");
		assertIt("1",    f, "1");
		assertIt("12",   f, "12");
		assertIt("123",  f, "123");
		assertIt("1234", f, "1234");
		assertIt(" ...", f, "12345");
		assertIt(" ...", f, "123456");
	}

	private static void assertIt(final String expected, final StringField f, final String s)
	{
		final SetValue<?> sv = Util.maC(f, s);
		assertSame(f, sv.settable);
		assertEquals(expected, sv.value);
	}
}
