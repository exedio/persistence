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

import static com.exedio.cope.PrometheusMeterRegistrar.meter;
import static com.exedio.cope.PrometheusMeterRegistrar.tag;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.TestSources;
import io.micrometer.core.instrument.Gauge;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClearCacheTest
{
	@Test void testClearQueryCache()
	{
		assertSizes(3, 5);
		MODEL.clearQueryCache();
		assertSizes(3, 0);
	}

	@Test void testClearItemCache()
	{
		assertSizes(3, 5);
		MODEL.clearItemCache();
		assertSizes(0, 5);
	}

	@Test void testClearCache()
	{
		assertSizes(3, 5);
		MODEL.clearCache();
		assertSizes(0, 0);
	}

	private static void assertSizes(
			final double itemCache,
			final double queryCache)
	{
		assertAll(
				() -> assertEquals( itemCache, gauge( ItemCache.class, "size"),  "itemCache"),
				() -> assertEquals(queryCache, gauge(QueryCache.class, "size"), "queryCache"));
	}

	private static double gauge(final Class<?> clazz, final String nameSuffix)
	{
		return ((Gauge)meter(clazz, nameSuffix, tag(MODEL))).value();
	}

	@BeforeEach void setUp()
	{
		MODEL.connect(ConnectProperties.create(TestSources.minimal()));
		MODEL.createSchema();

		final MyItem item1, item2, item3;
		assertSizes(0, 0);
		try(var tx = MODEL.startTransactionTry(ClearCacheTest.class.getName()))
		{
			item1 = new MyItem("item1");
			item2 = new MyItem("item2");
			item3 = new MyItem("item3");
			tx.commit();
		}
		assertSizes(0, 0);
		try(var tx = MODEL.startTransactionTry(ClearCacheTest.class.getName()))
		{
			assertEquals("item1", item1.getField());
			assertEquals("item2", item2.getField());
			assertEquals("item3", item3.getField());
			tx.commit();
		}
		assertSizes(3, 0);
		try(var tx = MODEL.startTransactionTry(ClearCacheTest.class.getName()))
		{
			assertEquals(item1, MyItem.TYPE.searchSingleton(MyItem.field.is("item1")));
			assertEquals(item2, MyItem.TYPE.searchSingleton(MyItem.field.is("item2")));
			assertEquals(null, MyItem.TYPE.searchSingleton(MyItem.field.is("itemA")));
			assertEquals(null, MyItem.TYPE.searchSingleton(MyItem.field.is("itemB")));
			assertEquals(null, MyItem.TYPE.searchSingleton(MyItem.field.is("itemC")));
			tx.commit();
		}
		assertSizes(3, 5);
	}

	@AfterEach void tearDown()
	{
		MODEL.rollbackIfNotCommitted();
		MODEL.tearDownSchema();
		MODEL.disconnect();
	}

	@WrapperType(indent=2, comments=false)
	private static class MyItem extends Item
	{
		static final StringField field = new StringField().toFinal();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
					@javax.annotation.Nonnull final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.field,field),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final java.lang.String getField()
		{
			return MyItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = new Model(MyItem.TYPE);

	static
	{
		MODEL.enableSerialization(ClearCacheTest.class, "MODEL");
	}
}
