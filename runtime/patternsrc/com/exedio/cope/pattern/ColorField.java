/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.IntegerField;
import com.exedio.cope.IntegerRangeViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import java.awt.Color;
import java.util.Set;

public final class ColorField extends Pattern implements Settable<Color>
{
	private static final long serialVersionUID = 1l;

	private final IntegerField rgb;
	private final boolean mandatory;
	private final boolean alphaAllowed;

	public ColorField()
	{
		this(new IntegerField().range(0, 0xffffff));
	}

	private ColorField(final IntegerField rgb)
	{
		addSource(this.rgb = rgb, "rgb");
		this.mandatory = rgb.isMandatory();
		this.alphaAllowed = (rgb.getMinimum()==Integer.MIN_VALUE);
		assert (alphaAllowed?Integer.MIN_VALUE:0       )==rgb.getMinimum();
		assert (alphaAllowed?Integer.MAX_VALUE:0xffffff)==rgb.getMaximum();
	}

	public ColorField optional()
	{
		return new ColorField(rgb.optional());
	}

	public ColorField defaultTo(final Color defaultConstant)
	{
		return new ColorField(rgb.defaultTo(rgb(defaultConstant, null)));
	}

	public ColorField allowAlpha()
	{
		return new ColorField(rgb.range(Integer.MIN_VALUE, Integer.MAX_VALUE));
	}

	public boolean isInitial()
	{
		return rgb.isInitial();
	}

	public boolean isFinal()
	{
		return rgb.isFinal();
	}

	public boolean isMandatory()
	{
		return mandatory;
	}

	public boolean isAlphaAllowed()
	{
		return alphaAllowed;
	}

	@Deprecated
	public Class<?> getInitialType()
	{
		return Color.class;
	}

	public Color getDefaultConstant()
	{
		return color(this.rgb.getDefaultConstant());
	}

	/**
	 * Returns the field containing <b>almost</b>
	 * the result of {@link Color#getRGB()}
	 * of the persisted color.
	 * The difference is as follows:
	 * <p>
	 * For {@link Color#getRGB()}
	 * an {@link Color#getAlpha() alpha value} of 255
	 * means that the color is completely opaque and
	 * an {@link Color#getAlpha() alpha value} of 0
	 * means that the color is completely transparent.
	 * <p>
	 * Values of the IntegerField returned by this method
	 * do have the reverse meaning:
	 * An alpha value of 255
	 * means that the color is completely transparent and
	 * an alpha value of 0
	 * means that the color is completely opaque.
	 * <p>
	 * This transformation ensures, that the persistent value
	 * of any opaque {@link Color} does not depend on whether
	 * the {@link ColorField}
	 * {@link #isAlphaAllowed() allows alpha} or not.
	 */
	public IntegerField getRGB()
	{
		return rgb;
	}

	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = rgb.getInitialExceptions();
		result.remove(IntegerRangeViolationException.class);
		if(!alphaAllowed)
			result.add(ColorAlphaViolationException.class);
		return result;
	}

	@Wrap(order=10,
			doc="Returns the value of {0}.")
	public Color get(final Item item)
	{
		return
			mandatory
			? new Color(rgb.getMandatory(item))
			: color(this.rgb.get(item));
	}

	private Color color(final Integer rgb)
	{
		if(rgb==null)
			return null;

		return new Color(alphaAllowed ? reverseAlpha(rgb) : rgb, alphaAllowed);
	}

	@Wrap(order=20,
			doc="Sets a new value for {0}.",
			hide=FinalSettableGetter.class,
			thrownGetter=InitialExceptionsSettableGetter.class)
	public void set(final Item item, final Color value)
	{
		rgb.set(item, rgb(value, item));
	}

	public SetValue<Color> map(final Color value)
	{
		return SetValue.map(this, value);
	}

	public SetValue<?>[] execute(final Color value, final Item exceptionItem)
	{
		return new SetValue<?>[]{ rgb.map(rgb(value, exceptionItem)) };
	}

	private Integer rgb(final Color value, final Item exceptionItem)
	{
		if(value==null)
		{
			if(mandatory)
				throw MandatoryViolationException.create(this, exceptionItem);

			return null;
		}
		else if(alphaAllowed)
		{
			return reverseAlpha(value.getRGB());
		}
		else
		{
			if(value.getAlpha()!=255)
				throw new ColorAlphaViolationException(this, exceptionItem, value);

			return value.getRGB() & 0xffffff;
		}
	}

	/**
	 * @see #getRGB()
	 */
	private static int reverseAlpha(final int rgb)
	{
		final int oldAlpha = (rgb >> 24) & 0xff;
		final int newAlpha = 255 - oldAlpha;
		final int result = (newAlpha << 24) | (rgb & 0xffffff);

		assert result==reverseAlphaAssert(rgb) : result;
		return result;
	}

	private static int reverseAlphaAssert(final int rgb)
	{
		final Color value = new Color(rgb, true);
		return new Color(value.getRed(), value.getGreen(), value.getBlue(), 255 - value.getAlpha()).getRGB();
	}
}
