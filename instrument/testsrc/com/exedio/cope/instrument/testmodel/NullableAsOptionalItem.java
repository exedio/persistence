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

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="defaultReturnAndParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void defaultWrapDefaultReturnAndParameter(final java.lang.Object wrapDefault)
	{
		NullableAsOptionalItem.wrapDefault.defaultReturnAndParameter(this,wrapDefault);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="conditionalNullableReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static final java.lang.Object conditionalWrapDefaultNullableReturn()
	{
		return NullableAsOptionalItem.wrapDefault.conditionalNullableReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="conditionalNullableParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void conditionalWrapDefaultNullableParameter(@javax.annotation.Nullable final java.lang.Object wrapDefault)
	{
		NullableAsOptionalItem.wrapDefault.conditionalNullableParameter(this,wrapDefault);
	}

	/**
	 * This method can return null.
	 * @return may be null
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNullableReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static final java.lang.Object javaxWrapDefaultNullableReturn()
	{
		return NullableAsOptionalItem.wrapDefault.javaxNullableReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNonnullReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.lang.Object javaxWrapDefaultNonnullReturn()
	{
		return NullableAsOptionalItem.wrapDefault.javaxNonnullReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNullableNonnullReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.lang.Object javaxWrapDefaultNullableNonnullReturn()
	{
		return NullableAsOptionalItem.wrapDefault.javaxNullableNonnullReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNullableParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static final void javaxWrapDefaultNullableParameter(@javax.annotation.Nullable final java.lang.Object wrapDefault)
	{
		NullableAsOptionalItem.wrapDefault.javaxNullableParameter(wrapDefault);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNonnullParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static final void javaxWrapDefaultNonnullParameter(@javax.annotation.Nonnull final java.lang.Object wrapDefault)
	{
		NullableAsOptionalItem.wrapDefault.javaxNonnullParameter(wrapDefault);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="jspecifyNullableReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static final java.lang.Object jspecifyWrapDefaultNullableReturn()
	{
		return NullableAsOptionalItem.wrapDefault.jspecifyNullableReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="jspecifyNonnullReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.lang.Object jspecifyWrapDefaultNonnullReturn()
	{
		return NullableAsOptionalItem.wrapDefault.jspecifyNonnullReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="jspecifyNullableNonnullReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.lang.Object jspecifyWrapDefaultNullableNonnullReturn()
	{
		return NullableAsOptionalItem.wrapDefault.jspecifyNullableNonnullReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="jspecifyNullableParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void jspecifyWrapDefaultNullableParameter(@javax.annotation.Nullable final java.lang.Object wrapDefault)
	{
		NullableAsOptionalItem.wrapDefault.jspecifyNullableParameter(this,wrapDefault);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="jspecifyNonnullParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void jspecifyWrapDefaultNonnullParameter(@javax.annotation.Nonnull final java.lang.Object wrapDefault)
	{
		NullableAsOptionalItem.wrapDefault.jspecifyNonnullParameter(this,wrapDefault);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="defaultReturnAndParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void defaultWrappedReturnAndParameter(final java.lang.Object wrapped)
	{
		NullableAsOptionalItem.wrapped.defaultReturnAndParameter(this,wrapped);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="conditionalNullableReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.util.Optional<java.lang.Object> conditionalWrappedNullableReturn()
	{
		return java.util.Optional.ofNullable(NullableAsOptionalItem.wrapped.conditionalNullableReturn());
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="conditionalNullableParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void conditionalWrappedNullableParameter(@javax.annotation.Nullable final java.lang.Object wrapped)
	{
		NullableAsOptionalItem.wrapped.conditionalNullableParameter(this,wrapped);
	}

	/**
	 * This method can return an empty {@link java.util.Optional}.
	 * @return may be an empty {@link java.util.Optional}
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNullableReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.util.Optional<java.lang.Object> javaxWrappedNullableReturn()
	{
		return java.util.Optional.ofNullable(NullableAsOptionalItem.wrapped.javaxNullableReturn());
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNonnullReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.lang.Object javaxWrappedNonnullReturn()
	{
		return NullableAsOptionalItem.wrapped.javaxNonnullReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNullableNonnullReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.lang.Object javaxWrappedNullableNonnullReturn()
	{
		return NullableAsOptionalItem.wrapped.javaxNullableNonnullReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNullableParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static final void javaxWrappedNullableParameter(@javax.annotation.Nullable final java.lang.Object wrapped)
	{
		NullableAsOptionalItem.wrapped.javaxNullableParameter(wrapped);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNonnullParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static final void javaxWrappedNonnullParameter(@javax.annotation.Nonnull final java.lang.Object wrapped)
	{
		NullableAsOptionalItem.wrapped.javaxNonnullParameter(wrapped);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="jspecifyNullableReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.util.Optional<java.lang.Object> jspecifyWrappedNullableReturn()
	{
		return java.util.Optional.ofNullable(NullableAsOptionalItem.wrapped.jspecifyNullableReturn());
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="jspecifyNonnullReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.lang.Object jspecifyWrappedNonnullReturn()
	{
		return NullableAsOptionalItem.wrapped.jspecifyNonnullReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="jspecifyNullableNonnullReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.lang.Object jspecifyWrappedNullableNonnullReturn()
	{
		return NullableAsOptionalItem.wrapped.jspecifyNullableNonnullReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="jspecifyNullableParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void jspecifyWrappedNullableParameter(@javax.annotation.Nullable final java.lang.Object wrapped)
	{
		NullableAsOptionalItem.wrapped.jspecifyNullableParameter(this,wrapped);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="jspecifyNonnullParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void jspecifyWrappedNonnullParameter(@javax.annotation.Nonnull final java.lang.Object wrapped)
	{
		NullableAsOptionalItem.wrapped.jspecifyNonnullParameter(this,wrapped);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="defaultReturnAndParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void defaultNotWrappedReturnAndParameter(final java.lang.Object notWrapped)
	{
		NullableAsOptionalItem.notWrapped.defaultReturnAndParameter(this,notWrapped);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="conditionalNullableReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static final java.lang.Object conditionalNotWrappedNullableReturn()
	{
		return NullableAsOptionalItem.notWrapped.conditionalNullableReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="conditionalNullableParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void conditionalNotWrappedNullableParameter(@javax.annotation.Nullable final java.lang.Object notWrapped)
	{
		NullableAsOptionalItem.notWrapped.conditionalNullableParameter(this,notWrapped);
	}

	/**
	 * This method can return null.
	 * @return may be null
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNullableReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static final java.lang.Object javaxNotWrappedNullableReturn()
	{
		return NullableAsOptionalItem.notWrapped.javaxNullableReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNonnullReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.lang.Object javaxNotWrappedNonnullReturn()
	{
		return NullableAsOptionalItem.notWrapped.javaxNonnullReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNullableNonnullReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.lang.Object javaxNotWrappedNullableNonnullReturn()
	{
		return NullableAsOptionalItem.notWrapped.javaxNullableNonnullReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNullableParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static final void javaxNotWrappedNullableParameter(@javax.annotation.Nullable final java.lang.Object notWrapped)
	{
		NullableAsOptionalItem.notWrapped.javaxNullableParameter(notWrapped);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNonnullParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static final void javaxNotWrappedNonnullParameter(@javax.annotation.Nonnull final java.lang.Object notWrapped)
	{
		NullableAsOptionalItem.notWrapped.javaxNonnullParameter(notWrapped);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="jspecifyNullableReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static final java.lang.Object jspecifyNotWrappedNullableReturn()
	{
		return NullableAsOptionalItem.notWrapped.jspecifyNullableReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="jspecifyNonnullReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.lang.Object jspecifyNotWrappedNonnullReturn()
	{
		return NullableAsOptionalItem.notWrapped.jspecifyNonnullReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="jspecifyNullableNonnullReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.lang.Object jspecifyNotWrappedNullableNonnullReturn()
	{
		return NullableAsOptionalItem.notWrapped.jspecifyNullableNonnullReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="jspecifyNullableParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void jspecifyNotWrappedNullableParameter(@javax.annotation.Nullable final java.lang.Object notWrapped)
	{
		NullableAsOptionalItem.notWrapped.jspecifyNullableParameter(this,notWrapped);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="jspecifyNonnullParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void jspecifyNotWrappedNonnullParameter(@javax.annotation.Nonnull final java.lang.Object notWrapped)
	{
		NullableAsOptionalItem.notWrapped.jspecifyNonnullParameter(this,notWrapped);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for nullableAsOptionalItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<NullableAsOptionalItem> TYPE = com.exedio.cope.TypesBound.newType(NullableAsOptionalItem.class,NullableAsOptionalItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected NullableAsOptionalItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
