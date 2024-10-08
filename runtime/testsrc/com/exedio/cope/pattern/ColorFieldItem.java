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

package com.exedio.cope.pattern;

import com.exedio.cope.Item;
import java.awt.Color;

final class ColorFieldItem extends Item
{
	static final ColorField mandatory = new ColorField();
	static final ColorField optional = new ColorField().optional();
	static final ColorField defaultTo = new ColorField().defaultTo(new Color(22, 33, 44));
	static final ColorField alpha = new ColorField().optional().allowAlpha().defaultTo(new Color(77, 88, 99, 254));
	static final ColorField mandatoryAlpha = new ColorField().allowAlpha().defaultTo(new Color(122, 133, 199, 253));
	static final ColorField finalColor = new ColorField().optional().toFinal();


	void setOptionalAndAlpha(final Color color)
	{
		setOptional(color);
		setAlpha(color);
	}

	int getOptionalRGB()
	{
		return optional.getRGB().get(this);
	}

	int getAlphaRGB()
	{
		return alpha.getRGB().get(this);
	}

	ColorFieldItem(
			@javax.annotation.Nonnull final java.awt.Color mandatory)
					throws com.exedio.cope.MandatoryViolationException
	{
		this(mandatory, null);
	}

	/**
	 * Creates a new ColorFieldItem with all the fields initially needed.
	 * @param mandatory the initial value for field {@link #mandatory}.
	 * @param finalColor the initial value for field {@link #finalColor}.
	 * @throws com.exedio.cope.MandatoryViolationException if mandatory is null.
	 * @throws com.exedio.cope.pattern.ColorAlphaViolationException if mandatory, finalColor violates its alpha constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	ColorFieldItem(
				@javax.annotation.Nonnull final java.awt.Color mandatory,
				@javax.annotation.Nullable final java.awt.Color finalColor)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.pattern.ColorAlphaViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(ColorFieldItem.mandatory,mandatory),
			com.exedio.cope.SetValue.map(ColorFieldItem.finalColor,finalColor),
		});
	}

	/**
	 * Creates a new ColorFieldItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private ColorFieldItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #mandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.awt.Color getMandatory()
	{
		return ColorFieldItem.mandatory.get(this);
	}

	/**
	 * Sets a new value for {@link #mandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setMandatory(@javax.annotation.Nonnull final java.awt.Color mandatory)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.pattern.ColorAlphaViolationException
	{
		ColorFieldItem.mandatory.set(this,mandatory);
	}

	/**
	 * Returns the value of {@link #optional}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.awt.Color getOptional()
	{
		return ColorFieldItem.optional.get(this);
	}

	/**
	 * Sets a new value for {@link #optional}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setOptional(@javax.annotation.Nullable final java.awt.Color optional)
			throws
				com.exedio.cope.pattern.ColorAlphaViolationException
	{
		ColorFieldItem.optional.set(this,optional);
	}

	/**
	 * Returns the value of {@link #defaultTo}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.awt.Color getDefaultTo()
	{
		return ColorFieldItem.defaultTo.get(this);
	}

	/**
	 * Sets a new value for {@link #defaultTo}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDefaultTo(@javax.annotation.Nonnull final java.awt.Color defaultTo)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.pattern.ColorAlphaViolationException
	{
		ColorFieldItem.defaultTo.set(this,defaultTo);
	}

	/**
	 * Returns the value of {@link #alpha}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.awt.Color getAlpha()
	{
		return ColorFieldItem.alpha.get(this);
	}

	/**
	 * Sets a new value for {@link #alpha}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setAlpha(@javax.annotation.Nullable final java.awt.Color alpha)
	{
		ColorFieldItem.alpha.set(this,alpha);
	}

	/**
	 * Returns the value of {@link #mandatoryAlpha}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.awt.Color getMandatoryAlpha()
	{
		return ColorFieldItem.mandatoryAlpha.get(this);
	}

	/**
	 * Sets a new value for {@link #mandatoryAlpha}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setMandatoryAlpha(@javax.annotation.Nonnull final java.awt.Color mandatoryAlpha)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		ColorFieldItem.mandatoryAlpha.set(this,mandatoryAlpha);
	}

	/**
	 * Returns the value of {@link #finalColor}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.awt.Color getFinalColor()
	{
		return ColorFieldItem.finalColor.get(this);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for colorFieldItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<ColorFieldItem> TYPE = com.exedio.cope.TypesBound.newType(ColorFieldItem.class,ColorFieldItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private ColorFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
