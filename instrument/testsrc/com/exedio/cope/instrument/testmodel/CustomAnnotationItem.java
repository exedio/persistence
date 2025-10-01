package com.exedio.cope.instrument.testmodel;

import static com.exedio.cope.instrument.Visibility.NONE;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.instrument.testfeature.SimpleSettable;

@SuppressWarnings("unused")
@WrapperType(constructor=NONE, genericConstructor=NONE, type=NONE)
class CustomAnnotationItem extends Item
{
	@interface MyAnnotation
	{
	}

	@interface MyOtherAnnotation
	{
		@SuppressWarnings("UnusedReturnValue")
		int parameter();
	}

	@Wrapper(wrap="*", annotate="@MyAnnotation")
	static final SimpleSettable featureWithOneAnnotation = new SimpleSettable();

	@Wrapper(wrap="one", annotate={"@MyAnnotation", "@MyOtherAnnotation(parameter=42)"})
	static final SimpleSettable featureWithTwoAnnotations = new SimpleSettable();

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="one")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@MyAnnotation
	final java.lang.String oneFeatureWithOneAnnotation()
	{
		return CustomAnnotationItem.featureWithOneAnnotation.one(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="one")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@MyAnnotation
	@MyOtherAnnotation(parameter=42)
	final java.lang.String oneFeatureWithTwoAnnotations()
	{
		return CustomAnnotationItem.featureWithTwoAnnotations.one(this);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected CustomAnnotationItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
