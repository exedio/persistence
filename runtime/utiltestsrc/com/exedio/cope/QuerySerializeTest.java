/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.util.List;

import com.exedio.cope.junit.CopeAssert;

public class QuerySerializeTest extends CopeAssert
{
	public void testSerialize()
	{
		final Query q = Item1.TYPE.newQuery(null);
		assertSerializedEquals(q, 794);

		q.setCondition(Item1.field1.isNotNull());
		assertSerializedEquals(q, 948);

		final Join j = q.join(Item1.TYPE);
		assertSerializedEquals(q, 1201);

		j.setCondition(Item1.field1.equal("zack"));
		assertSerializedEquals(q, 1449);

		q.addOrderBy(Item1.field1);
		assertSerializedEquals(q, 1524);

		q.setLimit(10, 20);
		assertSerializedEquals(q, 1524);

		final Query<List<Object>> qMulti = Query.newQuery(new Selectable[]{Item1.field1, Item1.TYPE.getThis()}, Item1.TYPE, null);
		assertSerializedEquals(qMulti, 864);
	}

	private static final void assertSerializedEquals(final Query value, final int expectedSize)
	{
		assertEquals(value.toString(), reserialize(value, expectedSize).toString());
	}

	static final class Item1 extends Item
	{
		static final StringField field1 = new StringField();
		static final Type<Item1> TYPE = TypesBound.newType(Item1.class);

		private Item1(final ActivationParameters ap) { super(ap); }
		private static final long serialVersionUID = 1l;
	}

	private static final Model MODEL = new Model(Item1.TYPE);

	static
	{
		MODEL.enableSerialization(QuerySerializeTest.class, "MODEL");
	}
}
