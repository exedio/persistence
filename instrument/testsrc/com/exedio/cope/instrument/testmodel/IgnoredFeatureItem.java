package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.CopeIgnore;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.testfeature.FeatureWithComponent;

public class IgnoredFeatureItem extends Item
{
	@WrapInterim
	@CopeIgnore
	@WrapperIgnore
	@SuppressWarnings({"FeatureNotStaticFinal", "CanBeFinal"})
	static IntegerField notAItemFeature = new IntegerField(); // should be final - but then it's considered an item feature in interim code, and gets double-mounted

	@SuppressWarnings({"NonFinalStaticVariableUsedInClassInitialization", "unused"})
	static final FeatureWithComponent withComponent = new FeatureWithComponent(new StringField(), notAItemFeature);

	/**
	 * Creates a new IgnoredFeatureItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public IgnoredFeatureItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new IgnoredFeatureItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected IgnoredFeatureItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for ignoredFeatureItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<IgnoredFeatureItem> TYPE = com.exedio.cope.TypesBound.newType(IgnoredFeatureItem.class,IgnoredFeatureItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected IgnoredFeatureItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
