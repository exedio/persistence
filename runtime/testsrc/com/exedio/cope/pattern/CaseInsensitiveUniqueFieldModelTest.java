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

import static com.exedio.cope.pattern.CaseInsensitiveUniqueField.create;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.StringField;
import com.exedio.cope.util.CharSet;
import org.junit.jupiter.api.Test;

public class CaseInsensitiveUniqueFieldModelTest
{
	@Test void pristineUnique()
	{
		final var p = new StringField().unique();
		assertFails(
				() -> create(p),
				IllegalArgumentException.class,
				"pristine must not be unique");
	}
	@Test void mandatory()
	{
		final var p = new StringField();
		final var f = create(p);
		final var l = f.getLowerCase();
		assertEquals(null, f.getUnisonNull());
		assertEquals("lower(" + p + ")=" + l, f.getUnisonCase().getCondition().toString());
	}
	@Test void optional()
	{
		final var p = new StringField().optional();
		final var f = create(p);
		final var l = f.getLowerCase();
		assertEquals("((" + p + " is null and " + l + " is null) or (" + p + " is not null and " + l + " is not null))", f.getUnisonNull().getCondition().toString());
		assertEquals("lower(" + p + ")=" + l, f.getUnisonCase().getCondition().toString());
	}
	@Test void withCharset()
	{
		final var p = new StringField().charSet(new CharSet(' ', '~'));
		final var f = create(p);
		assertEquals(new CharSet(' ', '~'), f.getPristine().getCharSet());
		assertEquals(new CharSet(' ', (char)('A'-1), (char)('Z'+1), '~'), f.getLowerCase().getCharSet());
	}
	@Test void withoutCharset()
	{
		final var p = new StringField();
		final var f = create(p);
		assertEquals(null, f.getPristine().getCharSet());
		assertEquals(new CharSet('\u0000', (char)('A'-1), (char)('Z'+1), '\uFFFF'), f.getLowerCase().getCharSet());
	}
}
