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

import static com.exedio.cope.SchemaInfo.supportsUniqueViolation;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.ProxyPropertiesSource;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @see UniqueCacheItemFieldTest
 */
public class UniqueCacheTest extends TestWithEnvironment
{
	@Test void notExists()
	{
		assertEquals(null, AnItem.forUnique(valueOther));
		assertCount(0, 1, 0, 1);
		assertFails(
				() -> AnItem.forUniqueStrict(valueOther),
				IllegalArgumentException.class,
				"expected result of size one, but was empty for query: " +
				"select this from AnItem where unique='" + valueOther + "'");
		assertCount(0, 1, 1, 0);
	}
	@Test void notLoaded()
	{
		assertEquals(setupItem, AnItem.forUnique(value));
		assertCount(0, 1, 0, 1);
		assertEquals(setupItem, AnItem.forUniqueStrict(value));
		assertCount(0, 1, 1, 0);
	}
	@Test void loadedAndChange()
	{
		final var iu = model.getConnectProperties().itemCacheUnique;
		assertEquals(value, setupItem.getUnique()); // load item

		assertEquals(setupItem, AnItem.forUnique(value));
		assertCount(1, 0, 0, iu?0:1);
		assertEquals(setupItem, AnItem.forUniqueStrict(value));
		assertCount(1, 0, iu?0:1, 0);

		setupItem.setOther("otherValue"); // invalidate in this transaction

		assertEquals(setupItem, AnItem.forUnique(value));
		assertCount(0, 1, 0, 0);
		assertEquals(setupItem, AnItem.forUniqueStrict(value));
		assertCount(0, 1, 0, 0);

		restartTransaction();
		assertEquals(setupItem, AnItem.forUnique(value));
		assertCount(0, 1, 0, 1);
		assertEquals(setupItem, AnItem.forUniqueStrict(value));
		assertCount(0, 1, 1, 0);
	}
	@Test void loadedAndChangeSub()
	{
		final var iu = model.getConnectProperties().itemCacheUnique;
		assertEquals("setupSubItem", setupSubItem.getUnique()); // load item

		assertEquals(setupSubItem, AnItem.forUnique("setupSubItem"));
		assertCount(1, 0, 0, iu?0:1);
		assertEquals(setupSubItem, AnItem.forUniqueStrict("setupSubItem"));
		assertCount(1, 0, iu?0:1, 0);

		setupSubItem.setOtherSub("otherValue"); // invalidate in this transaction

		assertEquals(setupSubItem, AnItem.forUnique("setupSubItem"));
		assertCount(0, 1, 0, 0);
		assertEquals(setupSubItem, AnItem.forUniqueStrict("setupSubItem"));
		assertCount(0, 1, 0, 0);

		restartTransaction();
		assertEquals(setupSubItem, AnItem.forUnique("setupSubItem"));
		assertCount(0, 1, 0, 1);
		assertEquals(setupSubItem, AnItem.forUniqueStrict("setupSubItem"));
		assertCount(0, 1, 1, 0);
	}
	@Test void loadedAndChangeUnique()
	{
		final var iu = model.getConnectProperties().itemCacheUnique;
		assertEquals(value, setupItem.getUnique()); // load item

		assertEquals(setupItem, AnItem.forUnique(value));
		assertCount(1, 0, 0, iu?0:1);
		assertEquals(setupItem, AnItem.forUniqueStrict(value));
		assertCount(1, 0, iu?0:1, 0);
		assertEquals(null, AnItem.forUnique(valueOther));
		assertCount(0, 1, 0, 1);
		assertFails(
				() -> AnItem.forUniqueStrict(valueOther),
				IllegalArgumentException.class,
				"expected result of size one, but was empty for query: " +
				"select this from AnItem where unique='" + valueOther + "'");
		assertCount(0, 1, 1, 0);

		setupItem.setUnique(valueOther); // invalidate in this transaction

		assertEquals(null, AnItem.forUnique(value));
		assertCount(0, 1, supportsUniqueViolation(model)?0:1, 0);
		assertFails(
				() -> AnItem.forUniqueStrict(value),
				IllegalArgumentException.class,
				"expected result of size one, but was empty for query: select this from AnItem where unique='" + value + "'");
		assertCount(0, 1, 0, 0);
		assertEquals(setupItem, AnItem.forUnique(valueOther));
		assertCount(0, 1, 0, 0);
		assertEquals(setupItem, AnItem.forUniqueStrict(valueOther));
		assertCount(0, 1, 0, 0);

		restartTransaction();
		assertEquals(null, AnItem.forUnique(value));
		assertCount(0, 1, 0, 1);
		assertFails(
				() -> AnItem.forUniqueStrict(value),
				IllegalArgumentException.class,
				"expected result of size one, but was empty for query: select this from AnItem where unique='" + value + "'");
		assertCount(0, 1, 1, 0);
		assertEquals(setupItem, AnItem.forUnique(valueOther));
		assertCount(0, 1, 0, 1);
		assertEquals(setupItem, AnItem.forUniqueStrict(valueOther));
		assertCount(0, 1, 1, 0);

		assertEquals(valueOther, setupItem.getUnique()); // load item

		assertEquals(null, AnItem.forUnique(value));
		assertCount(0, 1, 1, 0);
		assertFails(
				() -> AnItem.forUniqueStrict(value),
				IllegalArgumentException.class,
				"expected result of size one, but was empty for query: select this from AnItem where unique='" + value + "'");
		assertCount(0, 1, 1, 0);
		assertEquals(setupItem, AnItem.forUnique(valueOther));
		assertCount(1, 0, iu?0:1, 0);
		assertEquals(setupItem, AnItem.forUniqueStrict(valueOther));
		assertCount(1, 0, iu?0:1, 0);
	}
	@Test void loadedAndDelete()
	{
		final var iu = model.getConnectProperties().itemCacheUnique;
		assertEquals(value, setupItem.getUnique()); // load item

		assertEquals(setupItem, AnItem.forUnique(value));
		assertCount(1, 0, 0, iu?0:1);
		assertEquals(setupItem, AnItem.forUniqueStrict(value));
		assertCount(1, 0, iu?0:1, 0);

		setupItem.deleteCopeItem(); // invalidate in this transaction

		assertEquals(null, AnItem.forUnique(value));
		assertCount(0, 1, 0, 0);
		assertFails(
				() -> AnItem.forUniqueStrict(value),
				IllegalArgumentException.class,
				"expected result of size one, but was empty for query: " +
				"select this from AnItem where unique='setupItem'");
		assertCount(0, 1, 0, 0);

		restartTransaction();
		assertEquals(null, AnItem.forUnique(value));
		assertCount(0, 1, 0, 1);
		assertFails(
				() -> AnItem.forUniqueStrict(value),
				IllegalArgumentException.class,
				"expected result of size one, but was empty for query: " +
				"select this from AnItem where unique='setupItem'");
		assertCount(0, 1, 1, 0);
	}
	@Test void loadedAndDeleteSub()
	{
		final var iu = model.getConnectProperties().itemCacheUnique;
		assertEquals("setupSubItem", setupSubItem.getUnique()); // load item

		assertEquals(setupSubItem, AnItem.forUnique("setupSubItem"));
		assertCount(1, 0, 0, iu?0:1);
		assertEquals(setupSubItem, AnItem.forUniqueStrict("setupSubItem"));
		assertCount(1, 0, iu?0:1, 0);

		setupSubItem.deleteCopeItem(); // invalidate in this transaction

		assertEquals(null, AnItem.forUnique("setupSubItem"));
		assertCount(0, 1, 0, 0);
		assertFails(
				() -> AnItem.forUniqueStrict("setupSubItem"),
				IllegalArgumentException.class,
				"expected result of size one, but was empty for query: " +
				"select this from AnItem where unique='setupSubItem'");
		assertCount(0, 1, 0, 0);

		restartTransaction();
		assertEquals(null, AnItem.forUnique("setupSubItem"));
		assertCount(0, 1, 0, 1);
		assertFails(
				() -> AnItem.forUniqueStrict("setupSubItem"),
				IllegalArgumentException.class,
				"expected result of size one, but was empty for query: " +
				"select this from AnItem where unique='setupSubItem'");
		assertCount(0, 1, 1, 0);
	}
	@Test void newItem()
	{
		final AnItem item = new AnItem(valueNew);
		assertCount(0, 0, 0, supportsUniqueViolation(model)?0:1);

		assertEquals(item, AnItem.forUnique(valueNew));
		assertCount(0, 1, 0, 0);
		assertEquals(item, AnItem.forUniqueStrict(valueNew));
		assertCount(0, 1, 0, 0);

		assertEquals(valueNew, item.getUnique()); // does not load item because new in this transaction
		assertCount(0, 0, 0, 0);

		assertEquals(item, AnItem.forUnique(valueNew));
		assertCount(0, 1, 0, 0);
		assertEquals(item, AnItem.forUniqueStrict(valueNew));
		assertCount(0, 1, 0, 0);
	}
	@Test void newItemSub()
	{
		final ASubItem item = new ASubItem("newSubItem");
		assertCount(0, 0, 0, 1);

		assertEquals(item, AnItem.forUnique("newSubItem"));
		assertCount(0, 1, 0, 0);
		assertEquals(item, AnItem.forUniqueStrict("newSubItem"));
		assertCount(0, 1, 0, 0);

		assertEquals("newSubItem", item.getUnique()); // does not load item because new in this transaction
		assertCount(0, 0, 0, 0);

		assertEquals(item, AnItem.forUnique("newSubItem"));
		assertCount(0, 1, 0, 0);
		assertEquals(item, AnItem.forUniqueStrict("newSubItem"));
		assertCount(0, 1, 0, 0);
	}
	@Test void eviction()
	{
		final var iu = model.getConnectProperties().itemCacheUnique;
		final AnItem item1 = new AnItem("newItem1");
		final AnItem item2 = new AnItem("newItem2");
		final AnItem item3 = new AnItem("newItem3");
		final AnItem item4 = new AnItem("newItem4");
		assertCount(0, 0, 0, supportsUniqueViolation(model)?0:1);
		restartTransaction();

		assertEquals("newItem1", item1.getUnique()); // load item
		assertEquals("newItem2", item2.getUnique()); // load item
		assertEquals("newItem3", item3.getUnique()); // load item

		assertEquals(item1, AnItem.forUnique("newItem1"));
		assertCount(1, 0, 0, iu?0:1);
		assertEquals(item2, AnItem.forUnique("newItem2"));
		assertCount(1, 0, 0, iu?0:1);
		assertEquals(item3, AnItem.forUnique("newItem3"));
		assertCount(1, 0, 0, iu?0:1);
		assertEquals(item4, AnItem.forUnique("newItem4"));
		assertCount(0, 1, 0, 1);

		assertEquals("newItem4", item4.getUnique()); // load item

		assertEquals(item1, AnItem.forUnique("newItem1"));
		assertCount(0, 1, iu?0:1, iu?1:0); // no longer in item cache for cache.item.limit=3
		assertEquals(item2, AnItem.forUnique("newItem2"));
		assertCount(1, 0, iu?0:1, 0);
		assertEquals(item3, AnItem.forUnique("newItem3"));
		assertCount(1, 0, iu?0:1, 0);
		assertEquals(item4, AnItem.forUnique("newItem4"));
		assertCount(1, 0, iu?0:1, 0);
	}
	@Test void evictionSub()
	{
		final var iu = model.getConnectProperties().itemCacheUnique;
		final ASubItem item1 = new ASubItem("newSubItem1");
		final ASubItem item2 = new ASubItem("newSubItem2");
		final ASubItem item3 = new ASubItem("newSubItem3");
		final ASubItem item4 = new ASubItem("newSubItem4");
		assertCount(0, 0, 0, 1);
		restartTransaction();

		assertEquals("newSubItem1", item1.getUnique()); // load item
		assertEquals("newSubItem2", item2.getUnique()); // load item
		assertEquals("newSubItem3", item3.getUnique()); // load item

		assertEquals(item1, AnItem.forUnique("newSubItem1"));
		assertCount(1, 0, 0, iu?0:1);
		assertEquals(item2, AnItem.forUnique("newSubItem2"));
		assertCount(1, 0, 0, iu?0:1);
		assertEquals(item3, AnItem.forUnique("newSubItem3"));
		assertCount(1, 0, 0, iu?0:1);
		assertEquals(item4, AnItem.forUnique("newSubItem4"));
		assertCount(0, 1, 0, 1);

		assertEquals("newSubItem4", item4.getUnique()); // load item

		assertEquals(item1, AnItem.forUnique("newSubItem1"));
		assertCount(0, 1, iu?0:1, iu?1:0); // no longer in item cache for cache.item.limit=3
		assertEquals(item2, AnItem.forUnique("newSubItem2"));
		assertCount(1, 0, iu?0:1, 0);
		assertEquals(item3, AnItem.forUnique("newSubItem3"));
		assertCount(1, 0, iu?0:1, 0);
		assertEquals(item4, AnItem.forUnique("newSubItem4"));
		assertCount(1, 0, iu?0:1, 0);
	}
	@Test void nullValue()
	{
		assertEquals(null, setupNullItem.getUnique()); // load item
		setupNullItem.setOther("otherValue"); // invalidate in this transaction
		restartTransaction(); // remove setupNullItem from cache
	}
	@Test void nullValueSub()
	{
		assertEquals(null, setupSubNullItem.getUnique()); // load item
		setupSubNullItem.setOther("otherValue"); // invalidate in this transaction
		restartTransaction(); // remove setupNullItem from cache
	}
	@Test void notComparable()
	{
		final var iu = model.getConnectProperties().itemCacheUnique;
		assertEquals(value, setupItem.getUnique()); // load item

		assertEquals(setupItem, AnItem.forUnique(value));
		assertCount(1, 0, 0, iu?0:1);
		assertEquals(setupItem, AnItem.forUniqueStrict(value));
		assertCount(1, 0, iu?0:1, 0);

		@SuppressWarnings({"unchecked", "rawtypes"})
		final FunctionField<Integer> uniqueWrong = (FunctionField<Integer>)(FunctionField)AnItem.unique;
		assertFails(
				() -> uniqueWrong.searchUnique(AnItem.class, 777),
				IllegalArgumentException.class,
				"AnItem.unique not comparable to '777' (java.lang.Integer)");
		assertCount(0, 0, 0, 0);
		assertFails(
				() -> uniqueWrong.searchUniqueStrict(AnItem.class, 888),
				IllegalArgumentException.class,
				"AnItem.unique not comparable to '888' (java.lang.Integer)");
		assertCount(0, 0, 0, 0);
	}


	private void assertCount(
			final int hit, final int miss,
			final int queryHit, final int queryMiss)
	{
		assertAll(
				() -> hitCounter.assertValue(hit, "hit"),
				() -> missCounter.assertValue(miss, "miss"),
				() -> queryHitCounter.assertValue(queryHit, "queryHit"),
				() -> queryMissCounter.assertValue(queryMiss, "queryMiss"));
	}

	private final CounterTester hitCounter  = new CounterTester(counter("hit"),  p -> p.getItemCacheLimit()>0 && p.itemCacheUnique);
	private final CounterTester missCounter = new CounterTester(counter("miss"), p -> p.getItemCacheLimit()>0 && p.itemCacheUnique);
	private final CounterTester queryHitCounter  = new CounterTester(queryCounter("hit"),  p -> p.getQueryCacheLimit()>0);
	private final CounterTester queryMissCounter = new CounterTester(queryCounter("miss"), p -> p.getQueryCacheLimit()>0);

	private static Supplier<Counter> counter(final String result)
	{
		return () -> (Counter)PrometheusMeterRegistrar.meter(UniqueConstraint.class, "cache", Tags.of(
				"model", MODEL.toString(),
				"feature", AnItem.unique.getImplicitUniqueConstraint().getID(),
				"result", result));
	}

	private static Supplier<Counter> queryCounter(final String result)
	{
		return () -> (Counter)PrometheusMeterRegistrar.meter(QueryCache.class, "gets", Tags.of(
				"model", MODEL.toString(),
				"result", result));
	}

	@BeforeEach
	void setUp()
	{
		setupItem = new AnItem(value);
		new AnItem("setupItemNotToBeFound");
		setupNullItem = new AnItem((String)null);
		setupSubItem = new ASubItem("setupSubItem");
		new ASubItem("setupSubItemNotToBeFound");
		setupSubNullItem = new ASubItem((String)null);
		restartTransaction();
		hitCounter .setUp(model);
		missCounter.setUp(model);
		queryHitCounter .setUp(model);
		queryMissCounter.setUp(model);
	}

	private static final String value = "setupItem";
	private static final String valueOther = "setupItemX";
	private static final String valueNew = "newItem";
	private AnItem setupItem, setupNullItem;
	private ASubItem setupSubItem, setupSubNullItem;

	@Override
	public Source override(final Source s)
	{
		return new ProxyPropertiesSource(s)
		{
			@Override
			public String get(final String key)
			{
				final String value = super.get(key);
				if("cache.item.limit".equals(key))
				{
					if(value==null || Integer.parseInt(value)!=0)
						return "3";
				}
				return value;
			}
			@Override
			protected ProxyPropertiesSource reload(final Source reloadedTarget)
			{
				throw new AssertionError();
			}
		};
	}


	@WrapperType(indent=2, comments=false)
	private static class AnItem extends Item
	{
		@WrapperInitial
		static final StringField unique = new StringField().unique().optional();
		static final StringField other = new StringField().optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnItem(
					@javax.annotation.Nullable final java.lang.String unique)
				throws
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.UniqueViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(AnItem.unique,unique),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final java.lang.String getUnique()
		{
			return AnItem.unique.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setUnique(@javax.annotation.Nullable final java.lang.String unique)
				throws
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			AnItem.unique.set(this,unique);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		static final AnItem forUnique(@javax.annotation.Nonnull final java.lang.String unique)
		{
			return AnItem.unique.searchUnique(AnItem.class,unique);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static final AnItem forUniqueStrict(@javax.annotation.Nonnull final java.lang.String unique)
				throws
					java.lang.IllegalArgumentException
		{
			return AnItem.unique.searchUniqueStrict(AnItem.class,unique);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final java.lang.String getOther()
		{
			return AnItem.other.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setOther(@javax.annotation.Nullable final java.lang.String other)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			AnItem.other.set(this,other);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		protected AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static class ASubItem extends AnItem
	{
		static final StringField otherSub = new StringField().optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private ASubItem(
					@javax.annotation.Nullable final java.lang.String unique)
				throws
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.UniqueViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(com.exedio.cope.UniqueCacheTest.AnItem.unique,unique),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected ASubItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final java.lang.String getOtherSub()
		{
			return ASubItem.otherSub.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setOtherSub(@javax.annotation.Nullable final java.lang.String otherSub)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			ASubItem.otherSub.set(this,otherSub);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ASubItem> TYPE = com.exedio.cope.TypesBound.newType(ASubItem.class,ASubItem::new);

		@com.exedio.cope.instrument.Generated
		protected ASubItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(AnItem.TYPE, ASubItem.TYPE);

	public UniqueCacheTest()
	{
		super(MODEL);
	}
}
