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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings("ProtectedMemberInFinalClass")
@SuppressFBWarnings({"CI_CONFUSED_INHERITANCE","UPM_UNCALLED_PRIVATE_METHOD"})
public final class OptionFeatureItem extends OptionFeatureSuperItem
{
	// default visibility

	private   static final OptionFeature barePrivate   = new OptionFeature();
             static final OptionFeature barePackage   = new OptionFeature();
	protected static final OptionFeature bareProtected = new OptionFeature();
	public    static final OptionFeature barePublic    = new OptionFeature();


	// override visibility

	@Wrapper(wrap="simple", visibility=NONE)
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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	OptionFeatureItem(
				final boolean booleanAs)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			OptionFeatureItem.booleanAs.map(booleanAs),
		});
	}

	/**
	 * Creates a new OptionFeatureItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private OptionFeatureItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	private void simpleBarePrivate()
	{
		OptionFeatureItem.barePrivate.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	void simpleBarePackage()
	{
		OptionFeatureItem.barePackage.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	protected void simpleBareProtected()
	{
		OptionFeatureItem.bareProtected.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	public void simpleBarePublic()
	{
		OptionFeatureItem.barePublic.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	private void simpleOverrideInternalInternal()
	{
		OptionFeatureItem.overrideInternal.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	private void simpleOverridePrivate()
	{
		OptionFeatureItem.overridePrivate.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	void simpleOverridePackage()
	{
		OptionFeatureItem.overridePackage.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	protected void simpleOverrideProtected()
	{
		OptionFeatureItem.overrideProtected.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	public void simpleOverridePublic()
	{
		OptionFeatureItem.overridePublic.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	private void simpleInternalPrivateInternal()
	{
		OptionFeatureItem.internalPrivate.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	void simpleInternalPackageInternal()
	{
		OptionFeatureItem.internalPackage.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	protected void simpleInternalProtectedInternal()
	{
		OptionFeatureItem.internalProtected.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	public void simpleInternalPublicInternal()
	{
		OptionFeatureItem.internalPublic.simple(this);
	}

	/**
	 * Returns the value of {@link #booleanAs}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	boolean isBooleanAs()
	{
		return OptionFeatureItem.booleanAs.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #booleanAs}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setBooleanAs(final boolean booleanAs)
	{
		OptionFeatureItem.booleanAs.set(this,booleanAs);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	void getBooleanAsIsNoField()
	{
		OptionFeatureItem.booleanAsIsNoField.get(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	void simpleBooleanAsIsNotAplicable()
	{
		OptionFeatureItem.booleanAsIsNotAplicable.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	void simpleNonFinal()
	{
		OptionFeatureItem.nonFinal.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	private void simpleNonFinalPrivate()
	{
		OptionFeatureItem.nonFinalPrivate.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	@java.lang.Override
	void simpleOverride()
	{
		OptionFeatureItem.override.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	void simpleWrapTheIgnored()
	{
		OptionFeatureItem.wrapTheIgnored.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for optionFeatureItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	@SuppressWarnings("hiding")
	public static final com.exedio.cope.Type<OptionFeatureItem> TYPE = com.exedio.cope.TypesBound.newType(OptionFeatureItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private OptionFeatureItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
