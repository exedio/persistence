package com.exedio.cope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.exedio.cope.instrument.WrapperType;
import org.junit.Test;

public class NoCacheTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(
		CachedItem.TYPE,
		NoCacheItem.TYPE,
		WeightZeroItem.TYPE
	);

	public NoCacheTest()
	{
		super(MODEL);
	}

	@Test
	public void testItemCacheInfo()
	{
		assertCacheInfo(new Type<?>[]{CachedItem.TYPE}, new int[]{1});
	}

	@Test
	public void testInstances()
	{
		final CachedItem cachedItem=new CachedItem();
		final NoCacheItem noCacheItem=new NoCacheItem();
		final WeightZeroItem weightZeroItem=new WeightZeroItem();
		model.commit();
		assertCacheInfo(new Type<?>[]{CachedItem.TYPE}, new int[]{1});
		if (model.getConnectProperties().getItemCacheLimit()>0)
		{
			assertEquals(0, model.getItemCacheInfo()[0].getLevel());
		}
		else
		{
			assertEquals(0, model.getItemCacheInfo().length);
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
			final ItemCacheInfo[] itemCacheInfo=model.getItemCacheInfo();
			assertEquals(1, itemCacheInfo.length);
			assertEquals(1, itemCacheInfo[0].getLevel());
		}
		else
		{
			assertEquals(0, model.getItemCacheInfo().length);
		}
	}

	@Test
	public void testQueries()
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

	private void searchJoin(Type<?> queryType, Type<?> joinType)
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
		protected CachedItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<CachedItem> TYPE = com.exedio.cope.TypesBound.newType(CachedItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected CachedItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	@CopeNoCache
	static class NoCacheItem extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		NoCacheItem()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected NoCacheItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<NoCacheItem> TYPE = com.exedio.cope.TypesBound.newType(NoCacheItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected NoCacheItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
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
		protected WeightZeroItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<WeightZeroItem> TYPE = com.exedio.cope.TypesBound.newType(WeightZeroItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected WeightZeroItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
