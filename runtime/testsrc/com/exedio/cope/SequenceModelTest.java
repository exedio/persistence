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

import static com.exedio.cope.SequenceItem.TYPE;
import static com.exedio.cope.SequenceItem.full;
import static com.exedio.cope.SequenceItem.limited;
import static com.exedio.cope.tojunit.Assert.list;
import static java.lang.Integer.MAX_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class SequenceModelTest
{
	static final Model MODEL = Model.builder().
			name(SequenceModelTest.class).
			add(TYPE).
			build();

	@Test void testIt()
	{
		assertEquals(
				list(TYPE.getThis(), full, limited),
				TYPE.getDeclaredFeatures());

		assertEquals("SequenceItem.full", full.getID());
		assertEquals("full", full.getName());
		assertSame(TYPE, full.getType());

		assertEquals(0, full.getStart());
		assertEquals(MAX_VALUE, full.getEnd());

		assertEquals(5, limited.getStart());
		assertEquals(9, limited.getEnd());

		try
		{
			new Sequence(-1, MAX_VALUE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("start must be positive, but was -1.", e.getMessage());
		}
		try
		{
			new Sequence(5, 5);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("start must be less than end, but was 5 and 5.", e.getMessage());
		}
		try
		{
			new Sequence(6, 5);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("start must be less than end, but was 6 and 5.", e.getMessage());
		}
		try
		{
			MODEL.getSequenceInfo();
			fail();
		}
		catch(final Model.NotConnectedException e)
		{
			assertEquals(MODEL, e.getModel());
		}
		try
		{
			SchemaInfo.getPrimaryKeySequenceName(TYPE);
			fail();
		}
		catch(final Model.NotConnectedException e)
		{
			assertEquals(MODEL, e.getModel());
		}
		try
		{
			SchemaInfo.getSequenceName(full);
			fail();
		}
		catch(final Model.NotConnectedException e)
		{
			assertEquals(MODEL, e.getModel());
		}
	}
}
