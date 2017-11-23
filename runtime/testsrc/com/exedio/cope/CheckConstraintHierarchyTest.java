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
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

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

	@Test void testIsSupportedBySchema()
	{
		assertEquals(true,  top   .isSupportedBySchemaIfSupportedByDialect());
		// TODO
		// The following should be true, since check constraint "up" can be translated
		// into a database check constraint, since it just refers to columns of one table.
		// This is not yet implemented.
		// An implementation additionally needs to restrict the type (column "class")
		// in the table of the super type.
		assertEquals(false, up    .isSupportedBySchemaIfSupportedByDialect());
		assertEquals(true,  bottom.isSupportedBySchemaIfSupportedByDialect());
		assertEquals(false, cross .isSupportedBySchemaIfSupportedByDialect());
	}

	@Test void testTop()
	{
		final CheckConstraintHierarchyItemBottom item = new CheckConstraintHierarchyItemBottom();
		assertIt(item);

		try
		{
			item.setTop1(101);
			fail();
		}
		catch(final CheckViolationException e)
		{
			assertSame(item, e.getItem());
			assertSame(top, e.getFeature());
		}
		assertIt(item);
	}

	@Test void testUp()
	{
		final CheckConstraintHierarchyItemBottom item = new CheckConstraintHierarchyItemBottom();
		assertIt(item);

		try
		{
			item.setUp1(201);
			fail();
		}
		catch(final CheckViolationException e)
		{
			assertSame(item, e.getItem());
			assertSame(up, e.getFeature());
		}
		assertIt(item);
	}

	@Test void testBottom()
	{
		final CheckConstraintHierarchyItemBottom item = new CheckConstraintHierarchyItemBottom();
		assertIt(item);

		try
		{
			item.setBottom1(301);
			fail();
		}
		catch(final CheckViolationException e)
		{
			assertSame(item, e.getItem());
			assertSame(bottom, e.getFeature());
		}
		assertIt(item);
	}


	@Test void testCross()
	{
		final CheckConstraintHierarchyItemBottom item = new CheckConstraintHierarchyItemBottom();
		assertIt(item);

		try
		{
			item.setCross1(401);
			fail();
		}
		catch(final CheckViolationException e)
		{
			assertSame(item, e.getItem());
			assertSame(cross, e.getFeature());
		}
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
