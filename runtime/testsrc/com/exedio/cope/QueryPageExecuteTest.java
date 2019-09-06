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

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

public class QueryPageExecuteTest extends TestWithEnvironment
{
	@Test void test()
	{
		assertIt(0, 0, 0);
		assertIt(0, 0, 1);
		assertIt(0, 0, 2);
		assertIt(0, 0   );
		assertIt(0, 1, 0);
		assertIt(0, 1, 1);
		assertIt(0, 1, 2);
		assertIt(0, 1   );
		assertIt(0, 2, 0);
		assertIt(0, 2, 1);
		assertIt(0, 2, 2);
		assertIt(0, 2   );

		final AnItem i1 = new AnItem();
		assertIt(1, 0, 0);
		assertIt(1, 0, 1, i1);
		assertIt(1, 0, 2, i1);
		assertIt(1, 0,    i1);
		assertIt(1, 1, 0);
		assertIt(1, 1, 1);
		assertIt(1, 1, 2);
		assertIt(1, 1   );
		assertIt(1, 2, 0);
		assertIt(1, 2, 1);
		assertIt(1, 2, 2);
		assertIt(1, 2   );

		final AnItem i2 = new AnItem();
		assertIt(2, 0, 0);
		assertIt(2, 0, 1, i1);
		assertIt(2, 0, 2, i1, i2);
		assertIt(2, 0, 3, i1, i2);
		assertIt(2, 0,    i1, i2);
		assertIt(2, 1, 0);
		assertIt(2, 1, 1, i2);
		assertIt(2, 1, 2, i2);
		assertIt(2, 1,    i2);
		assertIt(2, 2, 0);
		assertIt(2, 2, 1);
		assertIt(2, 2, 2);
		assertIt(2, 2   );

		final AnItem i3 = new AnItem();
		assertIt(3, 0, 0);
		assertIt(3, 0, 1, i1);
		assertIt(3, 0, 2, i1, i2);
		assertIt(3, 0, 3, i1, i2, i3);
		assertIt(3, 0, 4, i1, i2, i3);
		assertIt(3, 0,    i1, i2, i3);
		assertIt(3, 1, 0);
		assertIt(3, 1, 1, i2);
		assertIt(3, 1, 2, i2, i3);
		assertIt(3, 1, 3, i2, i3);
		assertIt(3, 1,    i2, i3);
		assertIt(3, 2, 0);
		assertIt(3, 2, 1, i3);
		assertIt(3, 2, 2, i3);
		assertIt(3, 2   , i3);
	}

	private static void assertIt(final int total, final int offset, final int limit, final AnItem... search)
	{
		assertIt(total, q -> q.setPage(offset, limit), search);
	}

	private static void assertIt(final int total, final int offset, final AnItem... search)
	{
		assertIt(total, q -> q.setPageUnlimited(offset), search);
	}

	private static void assertIt(final int total, final Consumer<Query<AnItem>> page, final AnItem[] search)
	{
		final Query<AnItem> query = AnItem.TYPE.newQuery(null);
		query.setOrderByThis(true);
		page.accept(query);
		assertEquals(asList(search), query.search(), "search");
		assertEquals(total, query.total(), "total");
		assertEquals(total>0, query.exists(), "exists");
	}

	@WrapperType(indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnItem()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(AnItem.TYPE);

	QueryPageExecuteTest()
	{
		super(MODEL);
	}
}
