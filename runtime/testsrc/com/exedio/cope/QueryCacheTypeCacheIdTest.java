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

import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class QueryCacheTypeCacheIdTest
{
	private static final int MySuperItemId = 0;
	private static final int MyItemId = 1;
	private static final int MySubItemId = 2;
	private static final int MyOtherItemId = 3;

	@Test void testIds()
	{
		assertEquals(MySuperItemId, MySuperItem.TYPE.cacheIdTransiently);
		assertEquals(MyItemId, MyItem.TYPE.cacheIdTransiently);
		assertEquals(MySubItemId, MySubItem.TYPE.cacheIdTransiently);
		assertEquals(MyOtherItemId, MyOtherItem.TYPE.cacheIdTransiently);
	}

	@Test void testNormal()
	{
		final Query<?> q = MyItem.TYPE.newQuery();
		assertEquals(toList(MyItemId, MySuperItemId), toList(q.getTypeCacheIds()));
	}

	@Test void testSuper()
	{
		final Query<?> q = MySuperItem.TYPE.newQuery();
		assertEquals(toList(MySuperItemId), toList(q.getTypeCacheIds()));
	}

	@Test void testSub()
	{
		final Query<?> q = MySubItem.TYPE.newQuery();
		assertEquals(toList(MySubItemId, MyItemId, MySuperItemId), toList(q.getTypeCacheIds()));
	}

	@Test void testJoin()
	{
		final Query<?> q = MySuperItem.TYPE.newQuery();
		q.join(MyOtherItem.TYPE);
		assertEquals(toList(MyOtherItemId, MySuperItemId), toList(q.getTypeCacheIds()));
	}

	@Test void testJoinTwice()
	{
		final Query<?> q = MySuperItem.TYPE.newQuery();
		q.join(MyOtherItem.TYPE);
		q.join(MyOtherItem.TYPE);
		assertEquals(toList(MyOtherItemId, MySuperItemId), toList(q.getTypeCacheIds()));
	}

	@Test void testJoinSelf()
	{
		final Query<?> q = MySuperItem.TYPE.newQuery();
		q.join(MySuperItem.TYPE);
		assertEquals(toList(MySuperItemId), toList(q.getTypeCacheIds()));
	}


	private static List<Integer> toList(final int... array)
	{
		final ArrayList<Integer> result = new ArrayList<>();
		for(final int id : array)
			result.add(id);
		return result;
	}


	@WrapperType(constructor=NONE, indent=2, comments=false)
	private static class MySuperItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		protected MySuperItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MySuperItem> TYPE = com.exedio.cope.TypesBound.newType(MySuperItem.class);

		@com.exedio.cope.instrument.Generated
		protected MySuperItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	private static class MyItem extends MySuperItem
	{
		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	private static final class MySubItem extends MyItem
	{
		@com.exedio.cope.instrument.Generated
		private MySubItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MySubItem> TYPE = com.exedio.cope.TypesBound.newType(MySubItem.class);

		@com.exedio.cope.instrument.Generated
		private MySubItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	private static final class MyOtherItem extends MySuperItem
	{
		@com.exedio.cope.instrument.Generated
		private MyOtherItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyOtherItem> TYPE = com.exedio.cope.TypesBound.newType(MyOtherItem.class);

		@com.exedio.cope.instrument.Generated
		private MyOtherItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@SuppressWarnings("unused")
	private static final Model MODEL = new Model(
			MySuperItem.TYPE, MyItem.TYPE, MySubItem.TYPE, MyOtherItem.TYPE);
}
