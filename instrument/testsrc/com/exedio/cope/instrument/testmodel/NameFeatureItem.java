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
import static com.exedio.cope.instrument.Visibility.PROTECTED;
import static com.exedio.cope.instrument.Visibility.PUBLIC;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.testfeature.NameFeature;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("NM_METHOD_NAMING_CONVENTION")
public final class NameFeatureItem extends Item
{
	static final NameFeature feature = new NameFeature();

	@Wrapper(wrap="withinMethod", visibility=PUBLIC)
	static final NameFeature alpha = new NameFeature();

	@Wrapper(wrap="", visibility=PROTECTED)
	static final NameFeature beta = new NameFeature();

	@Wrapper(wrap="", visibility=NONE)
	@Wrapper(wrap="withinMethod", visibility=NONE)
	@SuppressWarnings("unused")
	static final NameFeature gamma = new NameFeature();

	/**
	 * Creates a new NameFeatureItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public NameFeatureItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new NameFeatureItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private NameFeatureItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="withinMethod")
	void withinFeatureMethod()
	{
		NameFeatureItem.feature.within0(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="withinMethod")
	void withinfeatureMethod()
	{
		NameFeatureItem.feature.within1(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="")
	void Feature()
	{
		NameFeatureItem.feature.only0(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="")
	void feature()
	{
		NameFeatureItem.feature.only1(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="withinMethod")
	public void withinAlphaMethod()
	{
		NameFeatureItem.alpha.within0(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="withinMethod")
	public void withinalphaMethod()
	{
		NameFeatureItem.alpha.within1(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="")
	void Alpha()
	{
		NameFeatureItem.alpha.only0(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="")
	void alpha()
	{
		NameFeatureItem.alpha.only1(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="withinMethod")
	void withinBetaMethod()
	{
		NameFeatureItem.beta.within0(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="withinMethod")
	void withinbetaMethod()
	{
		NameFeatureItem.beta.within1(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="")
	protected void Beta()
	{
		NameFeatureItem.beta.only0(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="")
	protected void beta()
	{
		NameFeatureItem.beta.only1(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for nameFeatureItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<NameFeatureItem> TYPE = com.exedio.cope.TypesBound.newType(NameFeatureItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private NameFeatureItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
