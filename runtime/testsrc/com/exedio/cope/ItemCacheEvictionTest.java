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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.minimal;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.dsmf.SQLRuntimeException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Tags;
import java.sql.SQLException;
import java.util.function.Supplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ItemCacheEvictionTest
{
	@Test void testEviction()
	{
		final AnItemA item1 = new AnItemA("itemA1");
		final AnItemA item2 = new AnItemA("itemA2");
		final AnItemA item3 = new AnItemA("itemA3");
		final AnItemA item4 = new AnItemA("itemA4");
		final AnItemA item5 = new AnItemA("itemA5");
		model.commit();
		model.startTransaction(ItemCacheEvictionTest.class.getName());
		assertMetrics(0, 0);
		assertEquals("itemA1", item1.getField()); // loads into cache
		assertMetrics(1, 0);
		assertEquals("itemA2", item2.getField()); // loads into cache
		assertMetrics(1, 0);
		assertEquals("itemA3", item3.getField()); // loads into cache
		assertMetrics(1, 0);
		assertEquals("itemA4", item4.getField()); // loads into cache
		assertMetrics(0, 1); // evicts
		assertEquals("itemA5", item5.getField()); // loads into cache
		assertMetrics(0, 1); // evicts
	}

	@Test void testDiscard() throws SQLException
	{
		final AnItemA item1 = new AnItemA("itemA1");
		final AnItemA item2 = new AnItemA("itemA2");
		model.commit();
		model.startTransaction(ItemCacheEvictionTest.class.getName());
		assertMetrics(0, 0);
		assertEquals("itemA1", item1.getField()); // loads into cache
		assertMetrics(1, 0);
		assertEquals("itemA2", item2.getField()); // loads into cache
		assertMetrics(1, 0);
		model.commit();

		model.startTransaction(ItemCacheEvictionTest.class.getName());
		item2.setField("itemA2X");
		assertMetrics(0, 0); // does not discard
		model.rollback(); // rollback to avoid cache invalidation

		try(var con = SchemaInfo.newConnection(model);
			var stmt = con.createStatement())
		{
			stmt.execute("ALTER TABLE \"AnItemA\" DROP COLUMN \"field\"");
		}
		assertMetrics(0, 0);

		model.startTransaction(ItemCacheEvictionTest.class.getName());
		assertFails(
				() -> item2.setField("itemA2Y"),
				SQLRuntimeException.class,
				"UPDATE \"AnItemA\" SET \"field\"=?'itemA2Y'?,\"catch\"=?1? " +
				"WHERE \"this\"=?1? AND \"catch\"=?0?");
		assertMetrics(-1, 0); // does discard
	}

	private void assertMetrics(
			final int sizeDelta,
			final int evictionsADelta)
	{
		assertAll(
				() -> size.assertValue(sizeDelta, "size"),
				() -> evictionsA.assertValue(evictionsADelta, "evictionsA"),
				() -> evictionsB.assertValue(0, "evictionsB"));
		@SuppressWarnings("deprecation") // OK: testing deprecated API
		final var stats = model.getItemCacheStatistics();
		assertEquals(size.get(), stats.getLevel());
		final var details = stats.getDetails();
		assertEquals(AnItemA.TYPE, details[0].getType());
		assertEquals(AnItemB.TYPE, details[1].getType());
		assertEquals(size.get(), details[0].getLevel());
		assertEquals(0,          details[1].getLevel());
		assertEquals(evictionsA.get(), details[0].getReplacementsL());
		assertEquals(0,                details[1].getReplacementsL());
		assertEquals(2, details.length);
	}

	private final GaugeTester size  = new GaugeTester(gauge(),  p -> true);
	private final CounterTester evictionsA  = new CounterTester(counter(AnItemA.TYPE),  p -> true);
	private final CounterTester evictionsB  = new CounterTester(counter(AnItemB.TYPE),  p -> true);

	private static Supplier<Gauge> gauge()
	{
		return () -> (Gauge)PrometheusMeterRegistrar.meter(ItemCache.class, "size", Tags.of(
				"model", model.toString()));
	}

	private static Supplier<Counter> counter(final Type<?> type)
	{
		return () -> (Counter)PrometheusMeterRegistrar.meter(ItemCache.class, "evictions", Tags.of(
				"type", type.getID(),
				"model", model.toString()));
	}

	@BeforeEach final void setUp()
	{
		final ConnectProperties properties = ConnectProperties.create(cascade(
				single("cache.item.limit", 3),
				minimal()
		));
		assertEquals(3, properties.getItemCacheLimit());
		model.connect(properties);
		assertSame(properties, model.getConnectProperties());
		size.setUp(model);
		evictionsA.setUp(model);
		evictionsB.setUp(model);

		model.createSchema();
		model.startTransaction(ItemCacheEvictionTest.class.getName());
	}

	@AfterEach final void tearDown()
	{
		model.rollbackIfNotCommitted();
		if(model.isConnected())
		{
			model.tearDownSchema();
			model.disconnect();
		}
	}

	static final Model model = new Model(AnItemA.TYPE, AnItemB.TYPE);

	@WrapperType(indent=2, comments=false)
	private static class AnItemA extends Item
	{
		static final StringField field = new StringField();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnItemA(
					@javax.annotation.Nonnull final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(AnItemA.field,field),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected AnItemA(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final java.lang.String getField()
		{
			return AnItemA.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setField(@javax.annotation.Nonnull final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			AnItemA.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItemA> TYPE = com.exedio.cope.TypesBound.newType(AnItemA.class,AnItemA::new);

		@com.exedio.cope.instrument.Generated
		protected AnItemA(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static class AnItemB extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnItemB()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected AnItemB(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItemB> TYPE = com.exedio.cope.TypesBound.newType(AnItemB.class,AnItemB::new);

		@com.exedio.cope.instrument.Generated
		protected AnItemB(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
