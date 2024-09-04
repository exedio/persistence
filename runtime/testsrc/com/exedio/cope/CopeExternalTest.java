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

import static com.exedio.cope.RuntimeTester.getItemCacheStatistics;
import static com.exedio.cope.RuntimeTester.getQueryCacheInfo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class CopeExternalTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(
		CachedItem.TYPE,
		NoCacheItem.TYPE
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
		model.commit();
		assertCacheInfo(CachedItem.TYPE);
		if (model.getConnectProperties().getItemCacheLimit()>0)
		{
			assertEquals(0, getItemCacheStatistics(model).getDetails()[0].getLevel());
		}
		else
		{
			assertEquals(0, getItemCacheStatistics(model).getDetails().length);
		}

		try (TransactionTry tx=model.startTransactionTry("NoCacheTest"))
		{
			cachedItem.activeCopeItem();
			noCacheItem.activeCopeItem();
			tx.commit();
		}
		if (model.getConnectProperties().getItemCacheLimit()>0)
		{
			final ItemCacheInfo[] itemCacheInfo = getItemCacheStatistics(model).getDetails();
			assertEquals(1, itemCacheInfo.length);
			assertEquals(1, itemCacheInfo[0].getLevel());
			assertEquals(CachedItem.TYPE, itemCacheInfo[0].getType());
		}
		else
		{
			assertEquals(0, getItemCacheStatistics(model).getDetails().length);
		}
	}

	@Test void testQueries()
	{
		final int oneIfCacheActive=model.getConnectProperties().getQueryCacheLimit()>0 ? 1 : 0;

		CachedItem.TYPE.search();
		assertEquals(oneIfCacheActive, getQueryCacheInfo(model).getLevel());
		model.clearCache();

		NoCacheItem.TYPE.search();
		assertEquals(0, getQueryCacheInfo(model).getLevel());

		searchJoin(CachedItem.TYPE, CachedItem.TYPE);
		assertEquals(oneIfCacheActive, getQueryCacheInfo(model).getLevel());
		model.clearCache();

		searchJoin(CachedItem.TYPE, NoCacheItem.TYPE);
		assertEquals(0, getQueryCacheInfo(model).getLevel());

		searchJoin(CachedItem.TYPE, NoCacheItem.TYPE);
		assertEquals(0, getQueryCacheInfo(model).getLevel());

		searchJoin(NoCacheItem.TYPE, CachedItem.TYPE);
		assertEquals(0, getQueryCacheInfo(model).getLevel());
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
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		CachedItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected CachedItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<CachedItem> TYPE = com.exedio.cope.TypesBound.newType(CachedItem.class,CachedItem::new);

		@com.exedio.cope.instrument.Generated
		protected CachedItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	@CopeExternal
	static class NoCacheItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		NoCacheItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected NoCacheItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<NoCacheItem> TYPE = com.exedio.cope.TypesBound.newType(NoCacheItem.class,NoCacheItem::new);

		@com.exedio.cope.instrument.Generated
		protected NoCacheItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
