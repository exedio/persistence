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

import static com.exedio.cope.tojunit.Assert.assertFails;

import org.junit.jupiter.api.Test;

public class DefaultToMethodTest
{
	@Test void testConstraintViolated()
	{
		final StringField f = new StringField().lengthMax(3);
		assertFails(
				() -> f.defaultTo("1234"),
				IllegalArgumentException.class,
				"The default constant '1234' of the field does not comply to one of it's own constraints, " +
				"caused a StringLengthViolationException: " +
				"length violation, '1234' is too long, " +
				"must be at most 3 characters, but was 4");
	}
	@Test void testRandomNull()
	{
		final LongField l = new LongField();
		assertFails(
				() -> l.defaultToRandom(null),
				NullPointerException.class,
				"source");
	}
}
