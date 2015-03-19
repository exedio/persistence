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

import com.exedio.cope.junit.CopeAssert;
import java.util.List;

public class QuerySerializeTest extends CopeAssert
{
	public void testSerialize()
	{
		final Query<?> q = Item1.TYPE.newQuery(null);
		assertSerializedEquals(q, 784);

		q.setCondition(Item1.field1.isNotNull());
		assertSerializedEquals(q, 938);

		final Join j = q.join(Item1.TYPE);
		assertSerializedEquals(q, 1191);

		j.setCondition(Item1.field1.equal("zack"));
		assertSerializedEquals(q, 1439);

		q.addOrderBy(Item1.field1);
		assertSerializedEquals(q, 1516);

		q.setHaving(Item1.field1.equal("zick"));
		assertSerializedEquals(q, 1538);

		q.setLimit(10, 20);
		assertSerializedEquals(q, 1538);

		final Query<List<Object>> qMulti = Query.newQuery(new Selectable<?>[]{Item1.field1, Item1.TYPE.getThis()}, Item1.TYPE, null);
		assertSerializedEquals(qMulti, 854);
	}

	private static final void assertSerializedEquals(final Query<?> value, final int expectedSize)
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
