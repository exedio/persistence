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

import static com.exedio.cope.BindTest.AnItem.TYPE;
import static com.exedio.cope.BindTest.AnItem.x;
import static com.exedio.cope.BindTest.AnItem.y;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.TestSources;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BindTest
{
	@Test void test()
	{
		assertIt("x", "AnItem0.\"x\"", x);
		assertIt("a1.x", "AnItem1.\"x\"", x.bind(a1));
		assertIt("a1.x", "AnItem1.\"x\"", x.bind(a1).bind(a2)); // idempotence
		assertIt("plus(x,y)", "(AnItem0.\"x\"+AnItem0.\"y\")", x.plus(y));
		assertIt("plus(a1.x,y)", "(AnItem1.\"x\"+AnItem0.\"y\")", x.bind(a1).plus(y));
		assertIt("a2.plus(a1.x,y)", "(AnItem1.\"x\"+AnItem2.\"y\")", x.bind(a1).plus(y).bind(a2));
		assertIt("plus(a1.x,a2.y)", "(AnItem1.\"x\"+AnItem2.\"y\")", x.bind(a1).plus(y.bind(a2)));
		assertIt("a1.plus(x,y)", "(AnItem1.\"x\"+AnItem1.\"y\")", x.plus(y).bind(a1));
		assertIt("a1.plus(x,y)", "(AnItem1.\"x\"+AnItem1.\"y\")", x.plus(y).bind(a1).bind(a2)); // idempotence
		assertIt("a2.plus(a1.x,a1.y)", "(AnItem1.\"x\"+AnItem1.\"y\")", x.bind(a1).plus(y.bind(a1)).bind(a2));

		assertIt("max(x)", "MAX(AnItem0.\"x\")", x.max());
		assertIt("max(a1.x)", "MAX(AnItem1.\"x\")", x.bind(a1).max());
		assertIt("a2.max(x)", "MAX(AnItem2.\"x\")", x.max().bind(a2));
		assertIt("a2.max(x)", "MAX(AnItem2.\"x\")", x.max().bind(a2).bind(a1)); // idempotence
		assertIt("a2.max(a1.x)", "MAX(AnItem1.\"x\")", x.bind(a1).max().bind(a2));
	}

	private Query<Integer> query;
	private Join a1;
	private Join a2;

	@BeforeEach void before()
	{
		query = new Query<>(x);

		a1 = query.join(TYPE);
		assertEquals(" join AnItem a1", a1.toString());

		a2 = query.join(TYPE);
		assertEquals(" join AnItem a2", a2.toString());

		MODEL.connect(ConnectProperties.create(TestSources.minimal()));
	}

	@AfterEach void after()
	{
		MODEL.disconnect();
	}

	private void assertIt(final String s, final String sql, final Function<Integer> function)
	{
		query.setSelect(function);
		assertEquals(
				"select " + s + " from AnItem join AnItem a1 join AnItem a2",
				query.toString());
		assertEquals(
				"SELECT " + sql + " FROM \"AnItem\" AnItem0 CROSS JOIN \"AnItem\" AnItem1 CROSS JOIN \"AnItem\" AnItem2",
				SchemaInfo.search(query));
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class AnItem extends Item
	{
		@WrapperIgnore
		static final IntegerField x = new IntegerField();
		@WrapperIgnore
		static final IntegerField y = new IntegerField();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		protected AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(TYPE);
}
