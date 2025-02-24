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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class DoubleFieldTest
{
	@Test void testIllegalRangeInfinity()
	{
		assertIllegalRange(POSITIVE_INFINITY, 44.22, "minimum must not be infinite, but was Infinity");
		assertIllegalRange(44.22, POSITIVE_INFINITY, "maximum must not be infinite, but was Infinity");
		assertIllegalRange(NEGATIVE_INFINITY, 44.22, "minimum must not be infinite, but was -Infinity");
		assertIllegalRange(44.22, NEGATIVE_INFINITY, "maximum must not be infinite, but was -Infinity");
	}

	@Test void testIllegalRangeNaN()
	{
		assertIllegalRange(NaN, 44.22, "minimum must not be NaN, but was NaN");
		assertIllegalRange(44.22, NaN, "maximum must not be NaN, but was NaN");
	}

	@Test void testIllegalRange()
	{
		assertIllegalRange( 0.0,  0.0,  "Redundant field with minimum==maximum (0.0) is probably a mistake. You may call method rangeEvenIfRedundant if you are sure this is ok.");
		assertIllegalRange(22.2, 22.2, "Redundant field with minimum==maximum (22.2) is probably a mistake. You may call method rangeEvenIfRedundant if you are sure this is ok.");
		assertIllegalRange(22.2, 21.1, "maximum must be at least minimum, but was 21.1 and 22.2");
		assertIllegalRange(MAX, MIN, "maximum must be at least minimum, but was " + MIN + " and " + MAX);
		assertIllegalRange(MIN, MIN, "Redundant field with minimum==maximum (" + MIN + ") is probably a mistake. You may call method rangeEvenIfRedundant if you are sure this is ok.");
		assertIllegalRange(MAX, MAX, "Redundant field with minimum==maximum (" + MAX + ") is probably a mistake. You may call method rangeEvenIfRedundant if you are sure this is ok.");
	}

	private static void assertIllegalRange(final double minimum, final double maximum, final String message)
	{
		final DoubleField f = new DoubleField().optional();
		assertFails(
				() -> f.range(minimum, maximum),
				IllegalArgumentException.class,
				message);
	}

	@Test void testRangeShortcutEqual()
	{
		final DoubleField f = new DoubleField().optional().range(-3, 5);
		assertEquals(f+" is null", f.equal((Double)null).toString());
		assertEquals("FALSE", f.equal(-3.1).toString());
		assertEquals(f+"='-3.0'", f.equal(-3d).toString());
		assertEquals(f+"='5.0'", f.equal(5d).toString());
		assertEquals("FALSE", f.equal(5.1).toString());
		assertEquals("FALSE", f.is(5.1).toString()); // test whether override works for new method
		assertEquals("FALSE", f.equal(MIN).toString());
		assertEquals("FALSE", f.equal(MAX).toString());

		final NumberFunction<Double> b = f.bind(AnItem.TYPE.newQuery().join(AnItem.TYPE, (Condition)null));
		assertEquals("a1."+f+" is null", b.equal((Double)null).toString());
		assertEquals("a1."+f+"='-3.1'", b.equal(-3.1).toString()); // TODO should be "FALSE"
		assertEquals("a1."+f+"='-3.0'", b.equal(-3d).toString());
		assertEquals("a1."+f+"='5.0'", b.equal(5d).toString());
		assertEquals("a1."+f+"='5.1'", b.equal(5.1).toString()); // TODO should be "FALSE"
		assertEquals("a1."+f+"='"+MIN+"'", b.equal(MIN).toString()); // TODO should be "FALSE"
		assertEquals("a1."+f+"='"+MAX+"'", b.equal(MAX).toString()); // TODO should be "FALSE"
	}

	@Test void testRangeShortcutNotEqual()
	{
		final DoubleField f = new DoubleField().optional().range(-3, 5);
		assertEquals(f+" is not null", f.notEqual((Double)null).toString());
		assertEquals("TRUE",  f.notEqual(-3.1).toString());
		assertEquals(f+"<>'-3.0'", f.notEqual(-3d).toString());
		assertEquals(f+"<>'5.0'", f.notEqual(5d).toString());
		assertEquals("TRUE", f.notEqual(5.1).toString());
		assertEquals("TRUE", f.isNot(5.1).toString()); // test whether override works for new method
		assertEquals("TRUE", f.notEqual(MIN).toString());
		assertEquals("TRUE", f.notEqual(MAX).toString());

		final NumberFunction<Double> b = f.bind(AnItem.TYPE.newQuery().join(AnItem.TYPE, (Condition)null));
		assertEquals("a1."+f+" is not null", b.notEqual((Double)null).toString());
		assertEquals("a1."+f+"<>'-3.1'", b.notEqual(-3.1).toString()); // TODO should be "TRUE"
		assertEquals("a1."+f+"<>'-3.0'", b.notEqual(-3d).toString());
		assertEquals("a1."+f+"<>'5.0'", b.notEqual(5d).toString());
		assertEquals("a1."+f+"<>'5.1'", b.notEqual(5.1).toString()); // TODO should be "TRUE"
		assertEquals("a1."+f+"<>'"+MIN+"'", b.notEqual(MIN).toString()); // TODO should be "TRUE"
		assertEquals("a1."+f+"<>'"+MAX+"'", b.notEqual(MAX).toString()); // TODO should be "TRUE"
	}

	private static final double MIN = -Double.MAX_VALUE;
	private static final double MAX = Double.MAX_VALUE;

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@SuppressWarnings("unused") // OK: just for initializing teh model
	private static final Model model = new Model(AnItem.TYPE);
}
