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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.EnumItem.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnumTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(EnumItem.TYPE, EnumItem2.TYPE);

	EnumItem item;
	EnumItem2 item2;

	private static final EnumItem.Status status1 = EnumItem.Status.status1;
	private static final EnumItem2.Status state1 = EnumItem2.Status.state1;

	public EnumTest()
	{
		super(MODEL);
	}

	@BeforeEach final void setUp()
	{
		item = new EnumItem(EnumItem.Status.status1);
		item2 = new EnumItem2(EnumItem2.Status.state1);
	}

	@Test void testIt()
	{
		assertEquals(EnumItem.Status.class, EnumItem.status.getValueClass());
		assertEquals(EnumItem2.Status.class, EnumItem2.status.getValueClass());

		assertSame(EnumItem.status, EnumItem.status.as(EnumItem.Status.class));
		try
		{
			EnumItem.status.as(EnumItem2.Status.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a " + EnumField.class.getName() + '<' + EnumItem2.Status.class.getName() + ">, " +
					"but was a " + EnumField.class.getName() + '<' + EnumItem.Status.class.getName() + '>',
				e.getMessage());
		}

		assertEquals(status1, item.getStatus());
		assertEquals(state1, item2.getStatus());

		assertEquals(null, item.getSingle());
		item.setSingle(Single.single);
		assertEquals(Single.single, item.getSingle());
		item.setSingle(null);
		assertEquals(null, item.getSingle());
	}
}
