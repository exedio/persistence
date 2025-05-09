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

import static com.exedio.cope.Cope.betweenAndCast;
import static com.exedio.cope.Cope.equalAndCast;
import static com.exedio.cope.Cope.greaterAndCast;
import static com.exedio.cope.Cope.greaterOrEqualAndCast;
import static com.exedio.cope.Cope.lessAndCast;
import static com.exedio.cope.Cope.lessOrEqualAndCast;
import static com.exedio.cope.Cope.multiply;
import static com.exedio.cope.Cope.notEqualAndCast;
import static com.exedio.cope.Cope.plus;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CopeClassTest
{
	@Test void testPlus2()
	{
		final IntegerField f1 = new IntegerField();
		final IntegerField f2 = new IntegerField();
		assertEquals(
				"plus(" + f1 + "," + f2 + ")",
				plus(f1, f2).toString());
	}
	@Test void testPlus3()
	{
		final IntegerField f1 = new IntegerField();
		final IntegerField f2 = new IntegerField();
		final IntegerField f3 = new IntegerField();
		assertEquals(
				"plus(" + f1 + "," + f2 + "," + f3 + ")",
				plus(f1, f2, f3).toString());
	}
	@Test void testMultiply2()
	{
		final IntegerField f1 = new IntegerField();
		final IntegerField f2 = new IntegerField();
		assertEquals(
				"multiply(" + f1 + "," + f2 + ")",
				multiply(f1, f2).toString());
	}
	@Test void testMultiply3()
	{
		final IntegerField f1 = new IntegerField();
		final IntegerField f2 = new IntegerField();
		final IntegerField f3 = new IntegerField();
		assertEquals(
				"multiply(" + f1 + "," + f2 + "," + f3 + ")",
				multiply(f1, f2, f3).toString());
	}
	@Test void testEqualAndCast()
	{
		final IntegerField f = new IntegerField();
		assertEquals(
				f + "='55'",
				equalAndCast(f, 55).toString());
		assertFails(
				() -> equalAndCast(f, 55l),
				ClassCastException.class,
				"Cannot cast java.lang.Long to java.lang.Integer");
	}
	@Test void testNotEqualAndCast()
	{
		final IntegerField f = new IntegerField();
		assertEquals(
				f + "<>'55'",
				notEqualAndCast(f, 55).toString());
		assertFails(
				() -> notEqualAndCast(f, 55l),
				ClassCastException.class,
				"Cannot cast java.lang.Long to java.lang.Integer");
	}
	@Test void testLessAndCast()
	{
		final IntegerField f = new IntegerField();
		assertEquals(
				f + "<'55'",
				lessAndCast(f, 55).toString());
		assertFails(
				() -> lessAndCast(f, 55l),
				ClassCastException.class,
				"Cannot cast java.lang.Long to java.lang.Integer");
	}
	@Test void testLessOrEqualAndCast()
	{
		final IntegerField f = new IntegerField();
		assertEquals(
				f + "<='55'",
				lessOrEqualAndCast(f, 55).toString());
		assertFails(
				() -> lessOrEqualAndCast(f, 55l),
				ClassCastException.class,
				"Cannot cast java.lang.Long to java.lang.Integer");
	}
	@Test void testGreaterAndCast()
	{
		final IntegerField f = new IntegerField();
		assertEquals(
				f + ">'55'",
				greaterAndCast(f, 55).toString());
		assertFails(
				() -> greaterAndCast(f, 55l),
				ClassCastException.class,
				"Cannot cast java.lang.Long to java.lang.Integer");
	}
	@Test void testGreaterOrEqualAndCast()
	{
		final IntegerField f = new IntegerField();
		assertEquals(
				f + ">='55'",
				greaterOrEqualAndCast(f, 55).toString());
		assertFails(
				() -> greaterOrEqualAndCast(f, 55l),
				ClassCastException.class,
				"Cannot cast java.lang.Long to java.lang.Integer");
	}
	@Test void testBetweenAndCast()
	{
		final IntegerField f = new IntegerField();
		assertEquals(
				"(" + f + ">='55' and " + f + "<='66')",
				betweenAndCast(f, 55, 66).toString());
		assertFails(
				() -> betweenAndCast(f, 55l, 66),
				ClassCastException.class,
				"Cannot cast java.lang.Long to java.lang.Integer");
		assertFails(
				() -> betweenAndCast(f, 55, 66l),
				ClassCastException.class,
				"Cannot cast java.lang.Long to java.lang.Integer");
	}
}
