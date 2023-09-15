package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.Item;

class PatternInSrcItem extends Item
{
	static final PatternInSrc p = new PatternInSrc();

	static PatternInSrcItem methodThatIsNotInInterimCode()
	{
		throw new RuntimeException("unexpected call");
	}

	/**
	 * Creates a new PatternInSrcItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	PatternInSrcItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new PatternInSrcItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected PatternInSrcItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="ishmael")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final com.exedio.cope.instrument.testmodel.PatternInSrcItem ishmael(@javax.annotation.Nonnull final java.lang.Integer a,final int b)
			throws
				java.lang.RuntimeException
	{
		return PatternInSrcItem.p.get(this,a,b);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for patternInSrcItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<PatternInSrcItem> TYPE = com.exedio.cope.TypesBound.newType(PatternInSrcItem.class,PatternInSrcItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected PatternInSrcItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
