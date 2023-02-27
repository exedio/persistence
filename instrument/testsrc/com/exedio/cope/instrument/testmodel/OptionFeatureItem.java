/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.instrument.testmodel;

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.instrument.Visibility.PACKAGE;
import static com.exedio.cope.instrument.Visibility.PRIVATE;
import static com.exedio.cope.instrument.Visibility.PROTECTED;
import static com.exedio.cope.instrument.Visibility.PUBLIC;

import com.exedio.cope.BooleanField;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.testfeature.FilterFeature;
import com.exedio.cope.instrument.testfeature.OptionFeature;
import com.exedio.cope.instrument.testfeature.OptionFeatureGet;

@SuppressWarnings("ProtectedMemberInFinalClass")
public final class OptionFeatureItem extends OptionFeatureSuperItem
{
	// default visibility

	private   static final OptionFeature barePrivate   = new OptionFeature();
             static final OptionFeature barePackage   = new OptionFeature();
	protected static final OptionFeature bareProtected = new OptionFeature();
	public    static final OptionFeature barePublic    = new OptionFeature();


	// override visibility

	@Wrapper(wrap="simple", visibility=NONE)
	@SuppressWarnings("unused") // OK: just for testing instrumentor
	static final OptionFeature none = new OptionFeature();

	@Wrapper(wrap="simple", internal=true)
	static final OptionFeature overrideInternal = new OptionFeature();

	@Wrapper(wrap="simple", visibility=PRIVATE)
	static final OptionFeature overridePrivate = new OptionFeature();

	@Wrapper(wrap="simple", visibility=PACKAGE)
	public static final OptionFeature overridePackage = new OptionFeature();

	@Wrapper(wrap="simple", visibility=PROTECTED)
	static final OptionFeature overrideProtected = new OptionFeature();

	@Wrapper(wrap="simple", visibility=PUBLIC)
	static final OptionFeature overridePublic = new OptionFeature();


	// override visibility and internal

	@Wrapper(wrap="simple", internal=true, visibility=NONE)
	@SuppressWarnings("unused") // OK: just for testing instrumentor
	static final OptionFeature internalNone = new OptionFeature();

	@Wrapper(wrap="simple", internal=true, visibility=PRIVATE)
	static final OptionFeature internalPrivate = new OptionFeature();

	@Wrapper(wrap="simple", internal=true, visibility=PACKAGE)
	public static final OptionFeature internalPackage = new OptionFeature();

	@Wrapper(wrap="simple", internal=true, visibility=PROTECTED)
	static final OptionFeature internalProtected = new OptionFeature();

	@Wrapper(wrap="simple", internal=true, visibility=PUBLIC)
	static final OptionFeature internalPublic = new OptionFeature();


	// boolean-as-is

	@Wrapper(wrap="get", booleanAsIs=true)
	@Wrapper(wrap="set", booleanAsIs=true)
	static final BooleanField booleanAs = new BooleanField();

	@Wrapper(wrap="get", booleanAsIs=true)
	static final OptionFeatureGet booleanAsIsNoField = new OptionFeatureGet();

	static final OptionFeature booleanAsIsNotAplicable = new OptionFeature();


	// miscellaneous

	@Wrapper(wrap="simple", asFinal=false)
	static final OptionFeature nonFinal = new OptionFeature();

	@Wrapper(wrap="simple", asFinal=false)
	private static final OptionFeature nonFinalPrivate = new OptionFeature();

	@Wrapper(wrap="simple", override=true)
	static final OptionFeature override = new OptionFeature();

	@WrapperIgnore
	static final OptionFeature ignore = new OptionFeature().fail();

	@Wrapper(wrap="*", visibility=NONE)
	static final OptionFeature ignoreDontFail = new OptionFeature();

	static final FilterFeature wrapTheIgnored = new FilterFeature(ignoreDontFail).sourceNotNull();

	@SuppressWarnings("unused") // OK: just for testing instrumentor
	void suppressUnusedWarnings()
	{
		simpleBarePrivate();
		simpleOverrideInternalInternal();
		simpleOverridePrivate();
	}

	/**
	 * Creates a new OptionFeatureItem with all the fields initially needed.
	 * @param booleanAs the initial value for field {@link #booleanAs}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	OptionFeatureItem(
				final boolean booleanAs)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(OptionFeatureItem.booleanAs,booleanAs),
		});
	}

	/**
	 * Creates a new OptionFeatureItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private OptionFeatureItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void simpleBarePrivate()
	{
		OptionFeatureItem.barePrivate.simple(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void simpleBarePackage()
	{
		OptionFeatureItem.barePackage.simple(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	protected void simpleBareProtected()
	{
		OptionFeatureItem.bareProtected.simple(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void simpleBarePublic()
	{
		OptionFeatureItem.barePublic.simple(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void simpleOverrideInternalInternal()
	{
		OptionFeatureItem.overrideInternal.simple(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void simpleOverridePrivate()
	{
		OptionFeatureItem.overridePrivate.simple(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void simpleOverridePackage()
	{
		OptionFeatureItem.overridePackage.simple(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	protected void simpleOverrideProtected()
	{
		OptionFeatureItem.overrideProtected.simple(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void simpleOverridePublic()
	{
		OptionFeatureItem.overridePublic.simple(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void simpleInternalPrivateInternal()
	{
		OptionFeatureItem.internalPrivate.simple(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void simpleInternalPackageInternal()
	{
		OptionFeatureItem.internalPackage.simple(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	protected void simpleInternalProtectedInternal()
	{
		OptionFeatureItem.internalProtected.simple(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void simpleInternalPublicInternal()
	{
		OptionFeatureItem.internalPublic.simple(this);
	}

	/**
	 * Returns the value of {@link #booleanAs}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean isBooleanAs()
	{
		return OptionFeatureItem.booleanAs.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #booleanAs}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setBooleanAs(final boolean booleanAs)
	{
		OptionFeatureItem.booleanAs.set(this,booleanAs);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void getBooleanAsIsNoField()
	{
		OptionFeatureItem.booleanAsIsNoField.get(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void simpleBooleanAsIsNotAplicable()
	{
		OptionFeatureItem.booleanAsIsNotAplicable.simple(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void simpleNonFinal()
	{
		OptionFeatureItem.nonFinal.simple(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void simpleNonFinalPrivate()
	{
		OptionFeatureItem.nonFinalPrivate.simple(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@java.lang.Override
	void simpleOverride()
	{
		OptionFeatureItem.override.simple(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void simpleWrapTheIgnored()
	{
		OptionFeatureItem.wrapTheIgnored.simple(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for optionFeatureItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<OptionFeatureItem> TYPE = com.exedio.cope.TypesBound.newType(OptionFeatureItem.class,OptionFeatureItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private OptionFeatureItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
