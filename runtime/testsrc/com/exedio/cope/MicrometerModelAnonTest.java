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

import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.TestSources;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class MicrometerModelAnonTest
{
	@Test void test()
	{
		// initialize
		assertThrows(AbsentError.class, () -> meterOther(ID_INITIALIZE));

		// change listeners remove
		assertThrows(AbsentError.class, () -> meter(ID_CL_REMOVE));
		assertEquals(0, MODEL.getChangeListenersInfo().getRemoved());
		MODEL.addChangeListener(CHANGE_LISTENER);
		MODEL.removeChangeListener(CHANGE_LISTENER);
		assertThrows(AbsentError.class, () -> meter(ID_CL_REMOVE));
		assertEquals(1, MODEL.getChangeListenersInfo().getRemoved());
		MODEL.addChangeListener(CHANGE_LISTENER);
		MODEL.removeChangeListener(CHANGE_LISTENER);
		assertThrows(AbsentError.class, () -> meter(ID_CL_REMOVE));
		assertEquals(2, MODEL.getChangeListenersInfo().getRemoved());

		// transaction open
		assertThrows(AbsentError.class, () -> meter(ID_TX_OPEN));
		assertEquals(0, MODEL.getOpenTransactions().size());
		// transaction counter
		assertThrows(AbsentError.class, () -> meter(ID_TX_COUNT));
		assertEquals(0, MODEL.getTransactionCounters().getCommitWithoutConnection());

		try(TransactionTry tx = MODEL.startTransactionTry(getClass().getName()))
		{
			// transaction open
			assertThrows(AbsentError.class, () -> meter(ID_TX_OPEN));
			assertEquals(1, MODEL.getOpenTransactions().size());
			// transaction counter
			assertThrows(AbsentError.class, () -> meter(ID_TX_COUNT));
			assertEquals(0, MODEL.getTransactionCounters().getCommitWithoutConnection());
			tx.commit();
		}

		// transaction open
		assertThrows(AbsentError.class, () -> meter(ID_TX_OPEN));
		assertEquals(0, MODEL.getOpenTransactions().size());
		// transaction counter
		assertThrows(AbsentError.class, () -> meter(ID_TX_COUNT));
		assertEquals(1, MODEL.getTransactionCounters().getCommitWithoutConnection());

		MODEL.enableSerialization(getClass(), "MODEL");

		// initialize
		assertEquals(1.0, ((Gauge)meterOther(ID_INITIALIZE)).value());

		// change listeners remove TODO 2 are missing
		assertEquals(0, ((Counter)meter(ID_CL_REMOVE)).count());
		assertEquals(0, MODEL.getChangeListenersInfo().getRemoved());
		MODEL.addChangeListener(CHANGE_LISTENER);
		MODEL.removeChangeListener(CHANGE_LISTENER);
		assertEquals(1, ((Counter)meter(ID_CL_REMOVE)).count());
		assertEquals(1, MODEL.getChangeListenersInfo().getRemoved());
		MODEL.addChangeListener(CHANGE_LISTENER);
		MODEL.removeChangeListener(CHANGE_LISTENER);
		assertEquals(2, ((Counter)meter(ID_CL_REMOVE)).count());
		assertEquals(2, MODEL.getChangeListenersInfo().getRemoved());

		// transaction open
		assertEquals(0, ((Gauge)meter(ID_TX_OPEN)).value());
		assertEquals(0, MODEL.getOpenTransactions().size());
		// transaction counter
		assertEquals(0, ((Timer)meter(ID_TX_COUNT)).count()); // TODO 1 missing
		assertEquals(0, MODEL.getTransactionCounters().getCommitWithoutConnection()); // TODO 1 missing

		try(TransactionTry tx = MODEL.startTransactionTry(getClass().getName()))
		{
			// transaction open
			assertEquals(1, ((Gauge)meter(ID_TX_OPEN)).value());
			assertEquals(1, MODEL.getOpenTransactions().size());
			// transaction counter
			assertEquals(0, ((Timer)meter(ID_TX_COUNT)).count()); // TODO 1 missing
			assertEquals(0, MODEL.getTransactionCounters().getCommitWithoutConnection()); // TODO 1 missing
			tx.commit();
		}

		// transaction open
		assertEquals(0, ((Gauge)meter(ID_TX_OPEN)).value());
		assertEquals(0, MODEL.getOpenTransactions().size());
		// transaction counter
		assertEquals(1, ((Timer)meter(ID_TX_COUNT)).count()); // TODO 1 missing
		assertEquals(1, MODEL.getTransactionCounters().getCommitWithoutConnection()); // TODO 1 missing
	}

	private static final Tags MODEL_TAGS = Tags.of("model", MicrometerModelAnonTest.class.getName() + "#MODEL");

	private static final Meter.Id ID_CL_REMOVE = new Meter.Id(
			ChangeListener.class.getName() + ".remove", MODEL_TAGS.and("cause", "remove"),
			null, null, Meter.Type.COUNTER);

	private static final ChangeListener CHANGE_LISTENER = event -> {};

	private static final Meter.Id ID_TX_OPEN = new Meter.Id(
			Transaction.class.getName() + ".open", MODEL_TAGS,
			null, null, Meter.Type.GAUGE);

	private static final Meter.Id ID_TX_COUNT = new Meter.Id(
			Transaction.class.getName() + ".finished", MODEL_TAGS.and("end", "commit", "connection", "without"),
			null, null, Meter.Type.COUNTER);

	private static Meter meter(final Meter.Id id)
	{
		for(final Meter m : InfoRegistry.REGISTRY.getMeters())
			if(id.equals(m.getId()))
				return m;

		throw new AbsentError();
	}

	private static Meter meterOther(final Meter.Id id)
	{
		for(final Meter m : PrometheusMeterRegistrar.getMeters())
			if(id.equals(m.getId()))
				return m;

		throw new AbsentError();
	}

	private static final class AbsentError extends AssertionFailedError
	{
		private static final long serialVersionUID = 3435620421628273951L;
	}


	@WrapperType(indent=2, comments=false)
	private static final class MyItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL =
			new Model(MyItem.TYPE);

	private static final Meter.Id ID_INITIALIZE = new Meter.Id(
			Model.class.getName() + ".initialize", MODEL_TAGS.and("date", ISO_INSTANT.format(MODEL.getInitializeInstant())),
			null, null, Meter.Type.GAUGE);

	@BeforeEach
	final void setUp()
	{
		MODEL.connect(ConnectProperties.create(TestSources.minimal()));
	}

	@AfterEach
	final void tearDown()
	{
		MODEL.rollbackIfNotCommitted();
		MODEL.disconnect();
	}
}
