package com.exedio.cope.instrument.testmodel;

import static com.exedio.cope.instrument.Visibility.NONE;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.instrument.testfeature.SimpleSettable;

@WrapperType(constructorSuppressWarnings="JustOneConstructor", type=NONE)
class SuppressWarningsItem extends Item
{
	@Wrapper(wrap="*", suppressWarnings="JustOne")
	static final SimpleSettable featureWithOneSuppress = new SimpleSettable();

	@Wrapper(wrap="one", suppressWarnings={"Afirst", "Zlast"}) // tests alphabetic order merged with global parameters
	static final SimpleSettable featureWithTwoSuppresses = new SimpleSettable();

	@Wrapper(wrap="one", suppressWarnings="TypeParameterExtendsFinalClass") // tests suppress redundant to global parameters
	static final SimpleSettable featureWithRedundantSuppresses = new SimpleSettable();


	/**
	 * Creates a new SuppressWarningsItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"JustOneConstructor","RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	SuppressWarningsItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new SuppressWarningsItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected SuppressWarningsItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="one")
	@java.lang.SuppressWarnings({"JustOne","RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final java.lang.String oneFeatureWithOneSuppress()
	{
		return SuppressWarningsItem.featureWithOneSuppress.one(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="one")
	@java.lang.SuppressWarnings({"Afirst","RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage","Zlast"})
	final java.lang.String oneFeatureWithTwoSuppresses()
	{
		return SuppressWarningsItem.featureWithTwoSuppresses.one(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="one")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final java.lang.String oneFeatureWithRedundantSuppresses()
	{
		return SuppressWarningsItem.featureWithRedundantSuppresses.one(this);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected SuppressWarningsItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
