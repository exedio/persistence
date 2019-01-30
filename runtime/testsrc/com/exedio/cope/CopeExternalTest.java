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

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class CopeExternalTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(
		CachedItem.TYPE,
		NoCacheItem.TYPE,
		WeightZeroItem.TYPE
	);

	public CopeExternalTest()
	{
		super(MODEL);
	}

	@Test void testItemCacheInfo()
	{
		assertCacheInfo(CachedItem.TYPE);
	}

	@Test void testInstances()
	{
		final CachedItem cachedItem=new CachedItem();
		final NoCacheItem noCacheItem=new NoCacheItem();
		final WeightZeroItem weightZeroItem=new WeightZeroItem();
		model.commit();
		assertCacheInfo(CachedItem.TYPE);
		if (model.getConnectProperties().getItemCacheLimit()>0)
		{
			assertEquals(0, model.getItemCacheStatistics().getDetails()[0].getLevel());
		}
		else
		{
			assertEquals(0, model.getItemCacheStatistics().getDetails().length);
		}

		try (TransactionTry tx=model.startTransactionTry("NoCacheTest"))
		{
			cachedItem.activeCopeItem();
			noCacheItem.activeCopeItem();
			weightZeroItem.activeCopeItem();
			tx.commit();
		}
		if (model.getConnectProperties().getItemCacheLimit()>0)
		{
			final ItemCacheInfo[] itemCacheInfo=model.getItemCacheStatistics().getDetails();
			assertEquals(1, itemCacheInfo.length);
			assertEquals(1, itemCacheInfo[0].getLevel());
			assertEquals(CachedItem.TYPE, itemCacheInfo[0].getType());
		}
		else
		{
			assertEquals(0, model.getItemCacheStatistics().getDetails().length);
		}
	}

	@Test void testQueries()
	{
		final int oneIfCacheActive=model.getConnectProperties().getQueryCacheLimit()>0 ? 1 : 0;

		CachedItem.TYPE.search();
		assertEquals(oneIfCacheActive, model.getQueryCacheInfo().getLevel());
		model.clearCache();

		NoCacheItem.TYPE.search();
		assertEquals(0, model.getQueryCacheInfo().getLevel());

		WeightZeroItem.TYPE.search();
		assertEquals(oneIfCacheActive, model.getQueryCacheInfo().getLevel());
		model.clearCache();

		searchJoin(CachedItem.TYPE, CachedItem.TYPE);
		assertEquals(oneIfCacheActive, model.getQueryCacheInfo().getLevel());
		model.clearCache();

		searchJoin(CachedItem.TYPE, NoCacheItem.TYPE);
		assertEquals(0, model.getQueryCacheInfo().getLevel());

		searchJoin(CachedItem.TYPE, NoCacheItem.TYPE);
		assertEquals(0, model.getQueryCacheInfo().getLevel());

		searchJoin(NoCacheItem.TYPE, CachedItem.TYPE);
		assertEquals(0, model.getQueryCacheInfo().getLevel());
	}

	private static void searchJoin(final Type<?> queryType, final Type<?> joinType)
	{
		final Query<?> q=queryType.newQuery();
		q.join(joinType);
		q.search();
	}



	@WrapperType(indent=2, comments=false)
	static class CachedItem extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		CachedItem()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected CachedItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<CachedItem> TYPE = com.exedio.cope.TypesBound.newType(CachedItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected CachedItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	@CopeExternal
	static class NoCacheItem extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		NoCacheItem()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected NoCacheItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<NoCacheItem> TYPE = com.exedio.cope.TypesBound.newType(NoCacheItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected NoCacheItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	@SuppressWarnings("deprecation")
	@CopeCacheWeight(0)
	static class WeightZeroItem extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		WeightZeroItem()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected WeightZeroItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<WeightZeroItem> TYPE = com.exedio.cope.TypesBound.newType(WeightZeroItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected WeightZeroItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
