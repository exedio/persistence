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

import static com.exedio.cope.QuerySerializeTest.AnItem.TYPE;
import static com.exedio.cope.QuerySerializeTest.AnItem.field;
import static com.exedio.cope.junit.CopeAssert.reserialize;

import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;

public class QuerySerializeTest extends TestCase
{
	@Test public void testSerialize()
	{
		final Query<?> q = TYPE.newQuery(null);
		int size=785;
		assertSerializedEquals(q, size);

		q.setCondition(field.isNotNull());
		assertSerializedEquals(q, size += 153);

		final Join j = q.join(TYPE);
		assertSerializedEquals(q, size += 253);

		j.setCondition(field.equal("zack"));
		assertSerializedEquals(q, size += 248);

		q.addOrderBy(field);
		assertSerializedEquals(q, size += 77);

		q.setHaving(field.equal("zick"));
		assertSerializedEquals(q, size += 22);

		q.setLimit(10, 20);
		assertSerializedEquals(q, size);

		final Query<List<Object>> qMulti = Query.newQuery(new Selectable<?>[]{field, TYPE.getThis()}, TYPE, null);
		assertSerializedEquals(qMulti, size - 684);
	}

	private static final void assertSerializedEquals(final Query<?> value, final int expectedSize)
	{
		assertEquals(value.toString(), reserialize(value, expectedSize).toString());
	}

	static final class AnItem extends Item
	{
		static final StringField field = new StringField();
		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);

		private AnItem(final ActivationParameters ap) { super(ap); }
		private static final long serialVersionUID = 1l;
	}

	private static final Model MODEL = new Model(AnItem.TYPE);

	static
	{
		MODEL.enableSerialization(QuerySerializeTest.class, "MODEL");
	}
}
