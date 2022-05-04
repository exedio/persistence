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

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.Visibility;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class MinusViewTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(Supplier.TYPE, StockAccounting.TYPE);

	static
	{
		MODEL.enableSerialization(MinusViewTest.class, "MODEL");
	}

	public MinusViewTest()
	{
		super(MODEL);
	}

	@Test void testGreater()
	{
		final Query<Supplier> q = Supplier.TYPE.newQuery(Supplier.stockable.equal("product1"));
		q.join(StockAccounting.TYPE, StockAccounting.supplier.equalTarget());
		q.setGroupBy(Supplier.TYPE.getThis(), Supplier.keepSupplierStockReserve);
		q.setHaving(StockAccounting.amount.sum().greater(Supplier.keepSupplierStockReserve));
		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals(false, q.exists());

		final Supplier s = new Supplier("product1", 5);
		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals(false, q.exists());

		new StockAccounting(s, 5);
		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals(false, q.exists());

		new StockAccounting(s, 1);
		assertEquals(asList(s), q.search());
		assertEquals(1, q.total());
		assertEquals(true, q.exists());
	}

	@Test void testMinus()
	{
		final Query<Supplier> q = Supplier.TYPE.newQuery(Supplier.stockable.equal("product1"));
		q.join(StockAccounting.TYPE, StockAccounting.supplier.equalTarget());
		q.setGroupBy(Supplier.TYPE.getThis(), Supplier.keepSupplierStockReserve);
		q.setHaving(StockAccounting.amount.sum().minus(Supplier.keepSupplierStockReserve).greater(0));
		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals(false, q.exists());

		final Supplier s = new Supplier("product1", 5);
		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals(false, q.exists());

		new StockAccounting(s, 5);
		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals(false, q.exists());

		new StockAccounting(s, 1);
		assertEquals(asList(s), q.search());
		assertEquals(1, q.total());
		assertEquals(true, q.exists());
	}


	@WrapperType(indent=2, comments=false)
	private static final class Supplier extends Item
	{
		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=Visibility.NONE)
		static final StringField stockable = new StringField();

		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=Visibility.NONE)
		static final IntegerField keepSupplierStockReserve = new IntegerField();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Supplier(
					@javax.annotation.Nonnull final java.lang.String stockable,
					final int keepSupplierStockReserve)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(Supplier.stockable,stockable),
				com.exedio.cope.SetValue.map(Supplier.keepSupplierStockReserve,keepSupplierStockReserve),
			});
		}

		@com.exedio.cope.instrument.Generated
		private Supplier(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Supplier> TYPE = com.exedio.cope.TypesBound.newType(Supplier.class,Supplier::new);

		@com.exedio.cope.instrument.Generated
		private Supplier(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static final class StockAccounting extends Item
	{
		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=Visibility.NONE)
		static final ItemField<Supplier> supplier = ItemField.create(Supplier.class);

		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=Visibility.NONE)
		static final IntegerField amount = new IntegerField();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private StockAccounting(
					@javax.annotation.Nonnull final Supplier supplier,
					final int amount)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(StockAccounting.supplier,supplier),
				com.exedio.cope.SetValue.map(StockAccounting.amount,amount),
			});
		}

		@com.exedio.cope.instrument.Generated
		private StockAccounting(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<StockAccounting> TYPE = com.exedio.cope.TypesBound.newType(StockAccounting.class,StockAccounting::new);

		@com.exedio.cope.instrument.Generated
		private StockAccounting(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

