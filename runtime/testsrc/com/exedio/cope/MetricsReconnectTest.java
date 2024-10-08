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
import static com.exedio.cope.RuntimeTester.getItemCacheStatistics;
import static com.exedio.cope.tojunit.TestSources.setupSchemaMinimal;
import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.TestSources;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class MetricsReconnectTest
{
	@Test void testCounter()
	{
		assertThrows(Model.NotConnectedException.class, () -> getItemCacheStatistics(MODEL));

		connect();
		assertEquals(0, hits());
		assertEquals(0, itemCache().getHits());
		setupSchemaMinimal(MODEL);

		MODEL.startTransaction(MetricsReconnectTest.class.getName());
		final AnItem item = new AnItem();
		MODEL.commit();
		assertEquals(0, hits());
		assertEquals(0, itemCache().getHits());

		MODEL.startTransaction(MetricsReconnectTest.class.getName());
		item.existsCopeItem();
		MODEL.commit();
		assertEquals(0, hits());
		assertEquals(0, itemCache().getHits());

		MODEL.startTransaction(MetricsReconnectTest.class.getName());
		item.existsCopeItem();
		MODEL.commit();
		assertEquals(1, hits());
		assertEquals(1, itemCache().getHits());

		MODEL.startTransaction(MetricsReconnectTest.class.getName());
		item.existsCopeItem();
		MODEL.commit();
		assertEquals(2, hits());
		assertEquals(2, itemCache().getHits());

		MODEL.disconnect();
		assertEquals(2, hits());
		assertThrows(Model.NotConnectedException.class, () -> getItemCacheStatistics(MODEL));

		connect();
		assertEquals(2, hits());
		assertEquals(2, itemCache().getHits());

		MODEL.startTransaction(MetricsReconnectTest.class.getName());
		item.existsCopeItem();
		MODEL.commit();
		assertEquals(2, hits());
		assertEquals(2, itemCache().getHits());

		MODEL.startTransaction(MetricsReconnectTest.class.getName());
		item.existsCopeItem();
		MODEL.commit();
		assertEquals(3, hits());
		assertEquals(3, itemCache().getHits());

		MODEL.startTransaction(MetricsReconnectTest.class.getName());
		item.existsCopeItem();
		MODEL.commit();
		assertEquals(4, hits());
		assertEquals(4, itemCache().getHits());

		MODEL.startTransaction(MetricsReconnectTest.class.getName());
		item.existsCopeItem();
		MODEL.commit();
		assertEquals(5, hits());
		assertEquals(5, itemCache().getHits());
	}

	@Test void testGauge()
	{
		assertThrows(Model.NotConnectedException.class, () -> getItemCacheStatistics(MODEL));

		connect();
		assertEquals(0, level());
		assertEquals(0, itemCache().getLevel());
		setupSchemaMinimal(MODEL);

		MODEL.startTransaction(MetricsReconnectTest.class.getName());
		final AnItem item1 = new AnItem();
		final AnItem item2 = new AnItem();
		final AnItem item3 = new AnItem();
		MODEL.commit();
		assertEquals(0, level());
		assertEquals(0, itemCache().getLevel());

		MODEL.startTransaction(MetricsReconnectTest.class.getName());
		item1.existsCopeItem();
		MODEL.commit();
		assertEquals(1, level());
		assertEquals(1, itemCache().getLevel());

		MODEL.startTransaction(MetricsReconnectTest.class.getName());
		item2.existsCopeItem();
		MODEL.commit();
		assertEquals(2, level());
		assertEquals(2, itemCache().getLevel());

		MODEL.startTransaction(MetricsReconnectTest.class.getName());
		item3.existsCopeItem();
		MODEL.commit();
		assertEquals(3, level());
		assertEquals(3, itemCache().getLevel());

		MODEL.disconnect();
		assertEquals(NaN, level());
		assertThrows(Model.NotConnectedException.class, () -> getItemCacheStatistics(MODEL));

		connect();
		assertEquals(0, level());
		assertEquals(0, itemCache().getLevel());

		MODEL.startTransaction(MetricsReconnectTest.class.getName());
		item1.existsCopeItem();
		MODEL.commit();
		assertEquals(1, level());
		assertEquals(1, itemCache().getLevel());

		MODEL.startTransaction(MetricsReconnectTest.class.getName());
		item2.existsCopeItem();
		MODEL.commit();
		assertEquals(2, level());
		assertEquals(2, itemCache().getLevel());

		MODEL.startTransaction(MetricsReconnectTest.class.getName());
		item3.existsCopeItem();
		MODEL.commit();
		assertEquals(3, level());
		assertEquals(3, itemCache().getLevel());
	}

	private static double hits()
	{
		return ((Counter)meter(
				ItemCache.class, "gets",
				tag(AnItem.TYPE).and("result", "hit"))).count();
	}

	private static double level()
	{
		return ((Gauge)meter(
				ItemCache.class, "size",
				tag(MODEL))).value();
	}

	private static ItemCacheInfo itemCache()
	{
		final ItemCacheInfo result = getItemCacheStatistics(MODEL).getDetails()[0];
		assertSame(AnItem.TYPE, result.getType());
		return result;
	}




	void connect()
	{
		MODEL.connect(ConnectProperties.create(TestSources.minimal()));
	}

	@AfterEach final void tearDown()
	{
		MODEL.rollbackIfNotCommitted();
		if(MODEL.isConnected())
		{
			MODEL.tearDownSchema();
			MODEL.disconnect();
		}
	}


	static final Model MODEL = new Model(AnItem.TYPE);

	@WrapperType(indent=2, comments=false)
	private static class AnItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		protected AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static
	{
		PrometheusMeterRegistrar.load();
	}

	static
	{
		MODEL.enableSerialization(MetricsReconnectTest.class, "MODEL");
	}
}
