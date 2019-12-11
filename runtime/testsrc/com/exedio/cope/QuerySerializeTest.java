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
import static com.exedio.cope.QuerySerializeTest.AnItem.enumField;
import static com.exedio.cope.QuerySerializeTest.AnItem.field;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.reserialize;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperIgnore;
import java.util.List;
import org.junit.jupiter.api.Test;

public class QuerySerializeTest
{
	@Test void testSerialize()
	{
		final Query<?> q = TYPE.newQuery(null);
		int size=793;
		assertSerializedEquals(q, size);

		q.setCondition(field.isNotNull());
		assertSerializedEquals(q, size += 129);

		final Join j = q.join(TYPE);
		assertSerializedEquals(q, size += 253);

		j.setCondition(field.equal("zack"));
		assertSerializedEquals(q, size += 248);

		q.addOrderBy(field);
		assertSerializedEquals(q, size += 77);

		q.setHaving(field.equal("zick"));
		assertSerializedEquals(q, size += 22);

		q.setPage(10, 20);
		assertSerializedEquals(q, size);

		final Query<List<Object>> qMulti = Query.newQuery(new Selectable<?>[]{field, TYPE.getThis()}, TYPE, null);
		assertSerializedEquals(qMulti, size - 660);
	}

	@Test void aggregateOfStringQuery()
	{
		final Query<String> q = new Query<>(field.min());
		assertSerializedEquals(q, 1128);
	}

	@Test void aggregateOfEnumQuery()
	{
		final Query<AnEnum> q = new Query<>(enumField.min());
		assertSerializedEquals(q, 1184);
	}

	@Test void countQuery()
	{
		final Query<Integer> q = new Query<>(new Count(), TYPE, null);
		assertSerializedEquals(q, 744);
	}


	private static void assertSerializedEquals(final Query<?> value, final int expectedSize)
	{
		assertEquals(value.toString(), reserialize(value, expectedSize).toString());
	}

	@SuppressWarnings("unused")
	enum AnEnum
	{
		one, two
	}

	@com.exedio.cope.instrument.WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class AnItem extends Item
	{
		@WrapperIgnore static final StringField field = new StringField();
		@WrapperIgnore static final EnumField<AnEnum> enumField = EnumField.create(AnEnum.class);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(QuerySerializeTest.class, "MODEL");
	}
}
