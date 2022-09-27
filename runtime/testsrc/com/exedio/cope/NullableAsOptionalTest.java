package com.exedio.cope;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.NullableAsOptional;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

final class NullableAsOptionalTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TestItem.TYPE);

	NullableAsOptionalTest()
	{
		super(MODEL);
	}

	@Test
	void value()
	{
		final TestItem i = new TestItem();
		assertEquals(false, i.getText().isPresent());

		i.setText("?");
		assertEquals(true, i.getText().isPresent());
		assertEquals("?", i.getText().get());
	}

	@Test
	void reference()
	{
		final TestItem a = new TestItem();
		final TestItem b = new TestItem();
		a.setReference(b);
		a.setText("a");
		assertEquals(false, a.getReference().flatMap(TestItem::getText).isPresent());
		b.setText("b");
		assertEquals("b", a.getReference().flatMap(TestItem::getText).get());
		assertEquals(false, b.getReference().flatMap(TestItem::getText).isPresent());
	}

	@WrapperType(indent=2)
	static class TestItem extends Item
	{
		@Wrapper(wrap="*", nullableAsOptional=NullableAsOptional.YES)
		static final ItemField<TestItem> reference = ItemField.create(TestItem.class).optional();

		@Wrapper(wrap="*", nullableAsOptional=NullableAsOptional.YES)
		static final StringField text = new StringField().optional();

		/**
		 * Creates a new TestItem with all the fields initially needed.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		TestItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		/**
		 * Creates a new TestItem and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		protected TestItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #reference}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final java.util.Optional<TestItem> getReference()
		{
			return java.util.Optional.ofNullable(TestItem.reference.get(this));
		}

		/**
		 * Sets a new value for {@link #reference}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setReference(@javax.annotation.Nullable final TestItem reference)
		{
			TestItem.reference.set(this,reference);
		}

		/**
		 * Returns the value of {@link #text}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final java.util.Optional<java.lang.String> getText()
		{
			return java.util.Optional.ofNullable(TestItem.text.get(this));
		}

		/**
		 * Sets a new value for {@link #text}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setText(@javax.annotation.Nullable final java.lang.String text)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			TestItem.text.set(this,text);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for testItem.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<TestItem> TYPE = com.exedio.cope.TypesBound.newType(TestItem.class,TestItem::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		protected TestItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
