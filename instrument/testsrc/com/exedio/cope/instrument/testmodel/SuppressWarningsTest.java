package com.exedio.cope.instrument.testmodel;

import static com.exedio.cope.instrument.Visibility.NONE;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Item;
import com.exedio.cope.instrument.CopeWarnings;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;

@SuppressWarnings("unused") // ok: testing instrumentor
class SuppressWarningsTest
{
	@WrapperType(indent = 2, comments = false, constructor = NONE, genericConstructor = NONE)
	private static class NonSuppressedItem extends Item
	{
		@Wrapper(wrap = "*", visibility = NONE)
		@SuppressWarnings(CopeWarnings.FEATURE_NOT_STATIC_FINAL) // ok: test that instrumentor produces no warning
		private final BooleanField suppressed = new BooleanField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<NonSuppressedItem> TYPE = com.exedio.cope.TypesBound.newType(NonSuppressedItem.class,NonSuppressedItem::new);

		@com.exedio.cope.instrument.Generated
		protected NonSuppressedItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent = 2, comments = false, constructor = NONE, genericConstructor = NONE)
	@SuppressWarnings(CopeWarnings.FEATURE_NOT_STATIC_FINAL) // ok: test that instrumentor produces no warning
	private static class SuppressedItem extends Item
	{
		@Wrapper(wrap = "*", visibility = NONE)
		private final BooleanField nonSuppressed = new BooleanField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<SuppressedItem> TYPE = com.exedio.cope.TypesBound.newType(SuppressedItem.class,SuppressedItem::new);

		@com.exedio.cope.instrument.Generated
		protected SuppressedItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
