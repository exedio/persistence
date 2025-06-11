package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.instrument.WrapInterim;

@SuppressWarnings("unused") // test instrumentation
class EnumWithField
{
	@WrapInterim
	enum FieldInitializedInInterim
	{
		A(1), B;

		@SuppressWarnings("FieldCanBeLocal") // test instrumentation
		// @WrapInterim would break instrumentation
		private final int field;

		@WrapInterim(methodBody = false) // true would break instrumentation
		FieldInitializedInInterim()
		{
			this(0);
		}

		// @WrapInterim would break instrumentation
		FieldInitializedInInterim(final int field)
		{
			this.field = field;
		}
	}

	@WrapInterim
	enum FieldNotInitializedInInterim
	{
		A(1);

		@SuppressWarnings("FieldCanBeLocal") // test instrumentation
		private final int field;

		FieldNotInitializedInInterim(final int field)
		{
			this.field = field;
		}
	}
}
