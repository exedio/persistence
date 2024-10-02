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

import static com.exedio.cope.CheckConstraintHierarchyItemBottom.bottom;
import static com.exedio.cope.CheckConstraintHierarchyItemBottom.cross;
import static com.exedio.cope.CheckConstraintHierarchyItemBottom.up;
import static com.exedio.cope.CheckConstraintHierarchyItemTop.top;
import static com.exedio.cope.CheckConstraintTest.assertFailsCheck;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CheckConstraintHierarchyTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(CheckConstraintHierarchyItemBottom.TYPE, CheckConstraintHierarchyItemTop.TYPE);

	static
	{
		MODEL.enableSerialization(CheckConstraintHierarchyTest.class, "MODEL");
	}

	public CheckConstraintHierarchyTest()
	{
		super(MODEL);
	}

	/**
	 * @see CheckConstraintHierarchyViolationTest#testTop()
	 */
	@Test void testTop()
	{
		final CheckConstraintHierarchyItemBottom item = new CheckConstraintHierarchyItemBottom();
		assertIt(item);

		assertFailsCheck(
				() -> item.setTop1(101),
				item,
				top);
		assertIt(item);
	}

	/**
	 * @see CheckConstraintHierarchyViolationTest#testUp()
	 */
	@Test void testUp()
	{
		final CheckConstraintHierarchyItemBottom item = new CheckConstraintHierarchyItemBottom();
		assertIt(item);

		assertFailsCheck(
				() -> item.setUp1(201),
				item,
				up);
		assertIt(item);
	}

	/**
	 * @see CheckConstraintHierarchyViolationTest#testBottom()
	 */
	@Test void testBottom()
	{
		final CheckConstraintHierarchyItemBottom item = new CheckConstraintHierarchyItemBottom();
		assertIt(item);

		assertFailsCheck(
				() -> item.setBottom1(301),
				item,
				bottom);
		assertIt(item);
	}

	/**
	 * @see CheckConstraintHierarchyViolationTest#testCross()
	 */
	@Test void testCross()
	{
		final CheckConstraintHierarchyItemBottom item = new CheckConstraintHierarchyItemBottom();
		assertIt(item);

		assertFailsCheck(
				() -> item.setCross1(401),
				item,
				cross);
		assertIt(item);
	}


	private static void assertIt(final CheckConstraintHierarchyItemBottom item)
	{
		assertEquals(100, item.getTop1());
		assertEquals(101, item.getTop2());
		assertEquals(200, item.getUp1());
		assertEquals(201, item.getUp2());
		assertEquals(300, item.getBottom1());
		assertEquals(301, item.getBottom2());
		assertEquals(400, item.getCross1());
		assertEquals(401, item.getCross2());
		assertEquals(0, top   .check());
		assertEquals(0, up    .check());
		assertEquals(0, bottom.check());
		assertEquals(0, cross .check());
	}
}
