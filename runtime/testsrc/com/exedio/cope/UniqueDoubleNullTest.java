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

import static com.exedio.cope.UniqueDoubleNullItem.TYPE;

import java.util.Arrays;
import org.junit.Test;

/**
 * See http://bugs.mysql.com/bug.php?id=8173 as well.
 */
public class UniqueDoubleNullTest extends AbstractRuntimeModelTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(UniqueDoubleNullTest.class, "MODEL");
	}

	public UniqueDoubleNullTest()
	{
		super(MODEL);
	}

	@Test public void test()
	{
		if(oracle) // Oracle violates SQL standard about this
			return;

		assertEquals(list(), TYPE.search(null, TYPE.getThis(), true));

		final UniqueDoubleNullItem aN1 = new UniqueDoubleNullItem("a", null);
		assertEquals("a", aN1.getString());
		assertAll(aN1);

		final UniqueDoubleNullItem aN2 = new UniqueDoubleNullItem("a", null);
		assertEquals("a", aN2.getString());
		assertAll(aN1, aN2);

		final UniqueDoubleNullItem N11 = new UniqueDoubleNullItem(null, 1);
		assertAll(aN1, aN2, N11);

		final UniqueDoubleNullItem N12 = new UniqueDoubleNullItem(null, 1);
		assertAll(aN1, aN2, N11, N12);

		final UniqueDoubleNullItem NN1 = new UniqueDoubleNullItem(null, null);
		assertAll(aN1, aN2, N11, N12, NN1);

		final UniqueDoubleNullItem NN2 = new UniqueDoubleNullItem(null, null);
		assertAll(aN1, aN2, N11, N12, NN1, NN2);

		aN1.setString("b");
		assertEquals("b", aN1.getString());

		aN2.setString("b");
		assertEquals("b", aN2.getString());

		aN1.setString(null);
		assertEquals(null, aN1.getString());

		aN2.setString(null);
		assertEquals(null, aN2.getString());
	}

	private static void assertAll(final UniqueDoubleNullItem... expected)
	{
		assertEquals(Arrays.asList(expected), TYPE.search(null, TYPE.getThis(), true));
	}
}
