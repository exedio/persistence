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

import static com.exedio.cope.CheckConstraintItem.TYPE;
import static com.exedio.cope.CheckConstraintItem.alphaToBeta;
import static com.exedio.cope.CheckConstraintSuperItem.einsToZwei;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class CheckConstraintTest extends TestWithEnvironment
{
	public CheckConstraintTest()
	{
		super(CheckConstraintModelTest.MODEL);
	}

	@Test void testIsSupportedBySchema()
	{
		assertEquals(true, einsToZwei .isSupportedBySchemaIfSupportedByDialect());
		assertEquals(true, alphaToBeta.isSupportedBySchemaIfSupportedByDialect());
	}

	@Test void testSet()
	{
		final CheckConstraintItem item = new CheckConstraintItem(102, 101, 103, 4, 5, 6, 7);
		assertIt(102, 101, 103, 4, 5, 6, 7, item);

		assertFailsCheck(
				() -> item.setAlpha(5),
				item,
				alphaToBeta);
		assertIt(102, 101, 103, 4, 5, 6, 7, item);

		assertFailsCheck(
				() ->item.setBeta(4),
				item,
				alphaToBeta);
		assertIt(102, 101, 103, 4, 5, 6, 7, item);

		item.setGamma(7);
		assertIt(102, 101, 103, 4, 5, 7, 7, item);

		item.setAlpha(3);
		assertIt(102, 101, 103, 3, 5, 7, 7, item);

		item.setBeta(6);
		assertIt(102, 101, 103, 3, 6, 7, 7, item);
	}

	@Test void testSetSuper()
	{
		final CheckConstraintItem item = new CheckConstraintItem(102, 101, 103, 4, 5, 6, 7);
		assertIt(102, 101, 103, 4, 5, 6, 7, item);

		assertFailsCheck(
				() -> item.setEins(100),
				item,
				einsToZwei);
		assertIt(102, 101, 103, 4, 5, 6, 7, item);

		assertFailsCheck(
				() -> item.setZwei(103),
				item,
				einsToZwei);
		assertIt(102, 101, 103, 4, 5, 6, 7, item);

		item.setDrei(104);
		assertIt(102, 101, 104, 4, 5, 6, 7, item);

		item.setEins(103);
		assertIt(103, 101, 104, 4, 5, 6, 7, item);

		item.setZwei(100);
		assertIt(103, 100, 104, 4, 5, 6, 7, item);
	}

	@Test void testSetMulti()
	{
		final CheckConstraintItem item = new CheckConstraintItem(102, 101, 103, 4, 5, 6, 7);
		assertIt(102, 101, 103, 4, 5, 6, 7, item);

		assertFailsCheck(
				() -> item.setAlphaBeta(5, 4),
				item,
				alphaToBeta);
		assertIt(102, 101, 103, 4, 5, 6, 7, item);

		assertFailsCheck(
				() -> item.setBetaGamma(4, 6),
				item,
				alphaToBeta);
		assertIt(102, 101, 103, 4, 5, 6, 7, item);

		item.setAlphaBeta(6, 7);
		assertIt(102, 101, 103, 6, 7, 6, 7, item);

		item.setAlphaBeta(4, 5);
		assertIt(102, 101, 103, 4, 5, 6, 7, item);

		item.setBetaGamma(8, 9);
		assertIt(102, 101, 103, 4, 8, 9, 7, item);
	}

	@Test void testSetMultiSuper()
	{
		final CheckConstraintItem item = new CheckConstraintItem(102, 101, 103, 4, 5, 6, 7);
		assertIt(102, 101, 103, 4, 5, 6, 7, item);

		assertFailsCheck(
				() -> item.setEinsZwei(101, 102),
				item,
				einsToZwei);
		assertIt(102, 101, 103, 4, 5, 6, 7, item);

		assertFailsCheck(
				() -> item.setZweiDrei(103, 104),
				item,
				einsToZwei);
		assertIt(102, 101, 103, 4, 5, 6, 7, item);

		item.setEinsZwei(106, 105);
		assertIt(106, 105, 103, 4, 5, 6, 7, item);

		item.setEinsZwei(102, 101);
		assertIt(102, 101, 103, 4, 5, 6, 7, item);

		item.setZweiDrei(100, 105);
		assertIt(102, 100, 105, 4, 5, 6, 7, item);
	}

	@Test void testCreate()
	{
		assertFailsCheck(
				() -> new CheckConstraintItem(102, 101, 103, 5, 4, 6, 7),
				null,
				alphaToBeta);
		assertEquals(list(), TYPE.search());
	}

	@Test void testCreateSuper()
	{
		assertFailsCheck(
				() -> new CheckConstraintItem(101, 102, 103, 4, 5, 6, 7),
				null,
				einsToZwei);
		assertEquals(list(), TYPE.search());
	}

	private static void assertIt(
			final Integer eins,
			final Integer zwei,
			final Integer drei,
			final Integer alpha,
			final Integer beta,
			final Integer gamma,
			final Integer delta,
			final CheckConstraintItem item)
	{
		assertEquals(eins,  item.getEins ());
		assertEquals(zwei,  item.getZwei ());
		assertEquals(drei,  item.getDrei ());
		assertEquals(alpha, item.getAlpha());
		assertEquals(beta,  item.getBeta ());
		assertEquals(gamma, item.getGamma());
		assertEquals(delta, item.getDelta());
		assertEquals(0, einsToZwei .check());
		assertEquals(0, alphaToBeta.check());
	}

	static void assertFailsCheck(
			final Executable executable,
			final Item item,
			final CheckConstraint constraint)
	{
		final CheckViolationException e = assertFails(
				executable,
				CheckViolationException.class,
				item!=null
				? ("check violation on " + item.getCopeID() + " for " + constraint.getID())
				: ("check violation"                        + " for " + constraint.getID())
		);
		assertSame(item, e.getItem());
		assertSame(constraint, e.getFeature());
	}
}
