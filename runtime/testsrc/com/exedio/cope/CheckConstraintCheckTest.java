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
import static com.exedio.cope.tojunit.Assert.sensitive;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class CheckConstraintCheckTest
{
	@Test void testLess()
	{
		final CheckConstraint cc =
				new CheckConstraint(a.less(b));
		cc.check(map(a, 1, b, 2));
		cc.check(map(a, 1, b, null));
		cc.check(map(a, null, b, 2));
		cc.check(map(a, null, b, null));
		cc.check(Collections.emptyMap());

		assertFails(
				() -> cc.check(map(a, 2, b, 2)),
				CheckViolationException.class,
				"check violation for " + cc);
	}

	@Test void testPlus()
	{
		final CheckConstraint cc =
				new CheckConstraint(a.plus(b).greater(0));
		cc.check(map(a, 1, b, 0));
		cc.check(map(a, 0, b, 1));
		cc.check(map(a, 0, b, null));
		cc.check(map(a, null, b, 0));
		cc.check(map(a, null, b, null));
		cc.check(Collections.emptyMap());

		assertFails(
				() -> cc.check(map(a, 0, b, 0)),
				CheckViolationException.class,
				"check violation for " + cc);
	}

	private static final IntegerField a = new IntegerField();
	private static final IntegerField b = new IntegerField();

	private static Map<FunctionField<?>, Object> map(
			final IntegerField f1, final Integer v1,
			final IntegerField f2, final Integer v2)
	{
		final HashMap<FunctionField<?>, Object> values = new HashMap<>();
		values.put(f1, v1);
		values.put(f2, v2);
		@SuppressWarnings("unchecked")
		final Class<FunctionField<?>> functionFieldClass = (Class<FunctionField<?>>)(Class<?>)FunctionField.class;
		return sensitive(values, functionFieldClass, Object.class);
	}


	@Test void testNull()
	{
		final CheckConstraint cc =
				new CheckConstraint(a.plus(b).greater(0));
		assertFails(
				() -> cc.check((Map<FunctionField<?>, Object>)null),
				NullPointerException.class,
				null);
	}
}
