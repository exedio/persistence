package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.NullableAsOptional;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.testfeature.NullabilityFeature;

class NullableAsOptionalItem extends Item
{
	static final NullabilityFeature wrapDefault = new NullabilityFeature(true);

	@Wrapper(wrap="*", nullableAsOptional=NullableAsOptional.YES)
	static final NullabilityFeature wrapped = new NullabilityFeature(true);

	@Wrapper(wrap="*", nullableAsOptional=NullableAsOptional.NO)
	static final NullabilityFeature notWrapped = new NullabilityFeature(true);

	/**
	 * Creates a new NullableAsOptionalItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	NullableAsOptionalItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new NullableAsOptionalItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected NullableAsOptionalItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCanReturnNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static final java.lang.Object allWrapDefaultCanReturnNull()
	{
		return NullableAsOptionalItem.wrapDefault.allCanReturnNull();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCannotReturnNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.lang.Object allWrapDefaultCannotReturnNull()
	{
		return NullableAsOptionalItem.wrapDefault.allCannotReturnNull();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="onlyOptionalsCanReturnNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static final java.lang.Object onlyWrapDefaultOptionalsCanReturnNull()
	{
		return NullableAsOptionalItem.wrapDefault.onlyOptionalsCanReturnNull();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCanTakeNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static final void allWrapDefaultCanTakeNull(@javax.annotation.Nullable final java.lang.Object wrapDefault)
	{
		NullableAsOptionalItem.wrapDefault.allCanTakeNull(wrapDefault);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCannotTakeNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static final void allWrapDefaultCannotTakeNull(@javax.annotation.Nonnull final java.lang.Object wrapDefault)
	{
		NullableAsOptionalItem.wrapDefault.allCannotTakeNull(wrapDefault);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="onlyOptionalsCanTakeNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void onlyWrapDefaultOptionalsCanTakeNull(@javax.annotation.Nullable final java.lang.Object wrapDefault)
	{
		NullableAsOptionalItem.wrapDefault.onlyOptionalsCanTakeNull(this,wrapDefault);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCanReturnNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.util.Optional<java.lang.Object> allWrappedCanReturnNull()
	{
		return java.util.Optional.ofNullable(NullableAsOptionalItem.wrapped.allCanReturnNull());
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCannotReturnNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.lang.Object allWrappedCannotReturnNull()
	{
		return NullableAsOptionalItem.wrapped.allCannotReturnNull();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="onlyOptionalsCanReturnNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.util.Optional<java.lang.Object> onlyWrappedOptionalsCanReturnNull()
	{
		return java.util.Optional.ofNullable(NullableAsOptionalItem.wrapped.onlyOptionalsCanReturnNull());
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCanTakeNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static final void allWrappedCanTakeNull(@javax.annotation.Nullable final java.lang.Object wrapped)
	{
		NullableAsOptionalItem.wrapped.allCanTakeNull(wrapped);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCannotTakeNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static final void allWrappedCannotTakeNull(@javax.annotation.Nonnull final java.lang.Object wrapped)
	{
		NullableAsOptionalItem.wrapped.allCannotTakeNull(wrapped);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="onlyOptionalsCanTakeNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void onlyWrappedOptionalsCanTakeNull(@javax.annotation.Nullable final java.lang.Object wrapped)
	{
		NullableAsOptionalItem.wrapped.onlyOptionalsCanTakeNull(this,wrapped);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCanReturnNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static final java.lang.Object allNotWrappedCanReturnNull()
	{
		return NullableAsOptionalItem.notWrapped.allCanReturnNull();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCannotReturnNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.lang.Object allNotWrappedCannotReturnNull()
	{
		return NullableAsOptionalItem.notWrapped.allCannotReturnNull();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="onlyOptionalsCanReturnNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static final java.lang.Object onlyNotWrappedOptionalsCanReturnNull()
	{
		return NullableAsOptionalItem.notWrapped.onlyOptionalsCanReturnNull();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCanTakeNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static final void allNotWrappedCanTakeNull(@javax.annotation.Nullable final java.lang.Object notWrapped)
	{
		NullableAsOptionalItem.notWrapped.allCanTakeNull(notWrapped);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCannotTakeNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static final void allNotWrappedCannotTakeNull(@javax.annotation.Nonnull final java.lang.Object notWrapped)
	{
		NullableAsOptionalItem.notWrapped.allCannotTakeNull(notWrapped);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="onlyOptionalsCanTakeNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void onlyNotWrappedOptionalsCanTakeNull(@javax.annotation.Nullable final java.lang.Object notWrapped)
	{
		NullableAsOptionalItem.notWrapped.onlyOptionalsCanTakeNull(this,notWrapped);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for nullableAsOptionalItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<NullableAsOptionalItem> TYPE = com.exedio.cope.TypesBound.newType(NullableAsOptionalItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected NullableAsOptionalItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
