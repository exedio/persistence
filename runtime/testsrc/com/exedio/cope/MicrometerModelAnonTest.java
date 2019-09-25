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
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.TestSources;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tags;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class MicrometerModelAnonTest
{
	@Test void test()
	{
		assertThrows(AbsentError.class, () -> meter(ID_TX_OPEN));
		assertEquals(0, MODEL.getOpenTransactions().size());

		try(TransactionTry tx = MODEL.startTransactionTry(getClass().getName()))
		{
			assertThrows(AbsentError.class, () -> meter(ID_TX_OPEN));
			assertEquals(1, MODEL.getOpenTransactions().size());
			tx.commit();
		}

		assertThrows(AbsentError.class, () -> meter(ID_TX_OPEN));
		assertEquals(0, MODEL.getOpenTransactions().size());

		MODEL.enableSerialization(getClass(), "MODEL");

		assertEquals(0, ((Gauge)meter(ID_TX_OPEN)).value());
		assertEquals(0, MODEL.getOpenTransactions().size());

		try(TransactionTry tx = MODEL.startTransactionTry(getClass().getName()))
		{
			assertEquals(1, ((Gauge)meter(ID_TX_OPEN)).value());
			assertEquals(1, MODEL.getOpenTransactions().size());
			tx.commit();
		}

		assertEquals(0, ((Gauge)meter(ID_TX_OPEN)).value());
		assertEquals(0, MODEL.getOpenTransactions().size());
	}

	private static final Tags MODEL_TAGS = Tags.of("model", MicrometerModelAnonTest.class.getName() + "#MODEL");

	private static final Meter.Id ID_TX_OPEN = new Meter.Id(
			Transaction.class.getName() + ".open", MODEL_TAGS,
			null, null, Meter.Type.GAUGE);

	private static Meter meter(final Meter.Id id)
	{
		for(final Meter m : InfoRegistry.REGISTRY.getMeters())
			if(id.equals(m.getId()))
				return m;

		throw new AbsentError();
	}

	private static final class AbsentError extends AssertionFailedError
	{
		private static final long serialVersionUID = 3435620421628273951L;
	}


	@WrapperType(indent=2, comments=false)
	static final class MyItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		MyItem()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL =
			new Model(MyItem.TYPE);

	@SuppressWarnings("static-method")
	@BeforeEach
	final void setUp()
	{
		MODEL.connect(ConnectProperties.create(TestSources.minimal()));
	}

	@SuppressWarnings("static-method")
	@AfterEach
	final void tearDown()
	{
		MODEL.rollbackIfNotCommitted();
		MODEL.disconnect();
	}
}
