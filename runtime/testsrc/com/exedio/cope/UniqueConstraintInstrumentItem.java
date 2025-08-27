package com.exedio.cope;

import static com.exedio.cope.instrument.Visibility.NONE;

import com.exedio.cope.instrument.NullableAsOptional;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;

@WrapperType(constructor=NONE, genericConstructor=NONE)
final class UniqueConstraintInstrumentItem extends Item
{
	@Wrapper(wrap="*", visibility=NONE)
	@Wrapper(wrap="for")
	private static final IntegerField a = new IntegerField().unique();

	@Wrapper(wrap="*", visibility=NONE)
	@Wrapper(wrap="for", nullableAsOptional=NullableAsOptional.YES)
	private static final IntegerField b = new IntegerField().unique();

	@Wrapper(wrap="*", visibility=NONE)
	private static final IntegerField c = new IntegerField();

	private static final UniqueConstraint aAndB = UniqueConstraint.create(a, b);

	@Wrapper(wrap="*", nullableAsOptional=NullableAsOptional.YES)
	private static final UniqueConstraint bAndCAsOptional = UniqueConstraint.create(b, c);


	/**
	 * Finds a uniqueConstraintInstrumentItem by its {@link #a}.
	 * @param a shall be equal to field {@link #a}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	private static UniqueConstraintInstrumentItem forA(final int a)
	{
		return UniqueConstraintInstrumentItem.a.searchUnique(UniqueConstraintInstrumentItem.class,a);
	}

	/**
	 * Finds a uniqueConstraintInstrumentItem by its {@link #b}.
	 * @param b shall be equal to field {@link #b}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	private static java.util.Optional<UniqueConstraintInstrumentItem> forB(final int b)
	{
		return java.util.Optional.ofNullable(UniqueConstraintInstrumentItem.b.searchUnique(UniqueConstraintInstrumentItem.class,b));
	}

	/**
	 * Finds a uniqueConstraintInstrumentItem by its unique fields.
	 * @param a shall be equal to field {@link #a}.
	 * @param b shall be equal to field {@link #b}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="finder")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	private static UniqueConstraintInstrumentItem forAAndB(final int a,final int b)
	{
		return UniqueConstraintInstrumentItem.aAndB.search(UniqueConstraintInstrumentItem.class,a,b);
	}

	/**
	 * Finds a uniqueConstraintInstrumentItem by its unique fields.
	 * @param a shall be equal to field {@link #a}.
	 * @param b shall be equal to field {@link #b}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="finderStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	private static UniqueConstraintInstrumentItem forAAndBStrict(final int a,final int b)
			throws
				java.lang.IllegalArgumentException
	{
		return UniqueConstraintInstrumentItem.aAndB.searchStrict(UniqueConstraintInstrumentItem.class,a,b);
	}

	/**
	 * Finds a uniqueConstraintInstrumentItem by its unique fields.
	 * @param b shall be equal to field {@link #b}.
	 * @param c shall be equal to field {@link #c}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="finder")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	private static java.util.Optional<UniqueConstraintInstrumentItem> forBAndCAsOptional(final int b,final int c)
	{
		return java.util.Optional.ofNullable(UniqueConstraintInstrumentItem.bAndCAsOptional.search(UniqueConstraintInstrumentItem.class,b,c));
	}

	/**
	 * Finds a uniqueConstraintInstrumentItem by its unique fields.
	 * @param b shall be equal to field {@link #b}.
	 * @param c shall be equal to field {@link #c}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="finderStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	private static UniqueConstraintInstrumentItem forBAndCAsOptionalStrict(final int b,final int c)
			throws
				java.lang.IllegalArgumentException
	{
		return UniqueConstraintInstrumentItem.bAndCAsOptional.searchStrict(UniqueConstraintInstrumentItem.class,b,c);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for uniqueConstraintInstrumentItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<UniqueConstraintInstrumentItem> TYPE = com.exedio.cope.TypesBound.newType(UniqueConstraintInstrumentItem.class,UniqueConstraintInstrumentItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private UniqueConstraintInstrumentItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
