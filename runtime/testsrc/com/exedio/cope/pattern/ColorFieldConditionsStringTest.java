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

import static com.exedio.cope.pattern.ColorFieldItem.optional;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Join;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import java.awt.Color;
import org.junit.jupiter.api.Test;

public class ColorFieldConditionsStringTest
{
	private static final Color
			vN = null,
			v1 = Color.RED;

	private static final ColorField f = optional;
	private static final String s = f.getRGB().getID();

	@Test void testCondition()
	{
		final Query<?> q = ColorFieldItem.TYPE.newQuery();
		final Join j = q.join(ColorFieldItem.TYPE);
		final ColorFunction b = f.bind(j);

		assertEquals("c1."+s, b.toString());

		assertEquals(      s+" is null"    , f.isNull()    .toString());
		assertEquals("c1."+s+" is null"    , b.isNull()    .toString());
		assertEquals("c1."+s+" is null"    , f.isNull().bind(j).toString());
		assertEquals(      s+" is not null", f.isNotNull() .toString());
		assertEquals("c1."+s+" is not null", b.isNotNull() .toString());
		assertEquals("c1."+s+" is not null", f.isNotNull().bind(j).toString());
		assertEquals(      s+" is null"    , f.equal(vN)   .toString());
		assertEquals("c1."+s+" is null"    , b.equal(vN)   .toString());
		assertEquals("c1."+s+" is null"    , f.equal(vN).bind(j).toString());
		assertEquals(      s+" is not null", f.notEqual(vN).toString());
		assertEquals("c1."+s+" is not null", b.notEqual(vN).toString());
		assertEquals("c1."+s+" is not null", f.notEqual(vN).bind(j).toString());
		assertEquals(      s+"='16711680'" , f.equal(v1)   .toString());
		assertEquals("c1."+s+"='16711680'" , b.equal(v1)   .toString());
		assertEquals("c1."+s+"='16711680'" , f.equal(v1).bind(j).toString());
		assertEquals(      s+"<>'16711680'", f.notEqual(v1).toString());
		assertEquals("c1."+s+"<>'16711680'", b.notEqual(v1).toString());
		assertEquals("c1."+s+"<>'16711680'", f.notEqual(v1).bind(j).toString());
	}

	@Test void testJoinNull()
	{
		assertFails(
				() -> f.bind(null),
				NullPointerException.class, "join");
	}

	@SuppressWarnings("unused") // OK: initializes types
	private static final Model MODEL = ColorFieldModelTest.MODEL;
}
