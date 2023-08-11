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

import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.CopyMapper;
import com.exedio.cope.Copyable;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.IntegerField;
import com.exedio.cope.IntegerRangeViolationException;
import com.exedio.cope.IsNullCondition;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import com.exedio.cope.misc.instrument.NullableIfOptional;
import java.awt.Color;
import java.util.Set;
import javax.annotation.Nonnull;

@WrapFeature
public final class ColorField extends Pattern implements Settable<Color>, Copyable, ColorFunction
{
	private static final long serialVersionUID = 1l;

	private final IntegerField rgb;
	private final boolean isfinal;
	private final boolean mandatory;
	private final boolean alphaAllowed;

	public ColorField()
	{
		this(new IntegerField().range(0, 0xffffff));
	}

	private ColorField(final IntegerField rgb)
	{
		this.rgb = addSourceFeature(rgb, "rgb");
		this.isfinal = rgb.isFinal();
		this.mandatory = rgb.isMandatory();
		this.alphaAllowed = (rgb.getMinimum()==Integer.MIN_VALUE);
		assert (alphaAllowed?Integer.MIN_VALUE:0       )==rgb.getMinimum();
		assert (alphaAllowed?Integer.MAX_VALUE:0xffffff)==rgb.getMaximum();
	}

	@Override
	public ColorField copy(final CopyMapper mapper)
	{
		return new ColorField(mapper.copy(rgb));
	}

	public ColorField toFinal()
	{
		return new ColorField(rgb.toFinal());
	}

	public ColorField optional()
	{
		return new ColorField(rgb.optional());
	}

	public ColorField defaultTo(final Color defaultConstant)
	{
		return new ColorField(rgb.defaultTo(rgb(defaultConstant, null)));
	}

	/**
	 * @see #isAlphaAllowed()
	 */
	public ColorField allowAlpha()
	{
		return new ColorField(rgb.range(Integer.MIN_VALUE, Integer.MAX_VALUE));
	}

	@Override
	public boolean isInitial()
	{
		return rgb.isInitial();
	}

	@Override
	public boolean isFinal()
	{
		return isfinal;
	}

	@Override
	public boolean isMandatory()
	{
		return mandatory;
	}

	@Override
	public Class<?> getInitialType()
	{
		return Color.class;
	}

	/**
	 * If this method returns false, this ColorField
	 * allows opaque colors only.
	 * This means, the {@link Color#getAlpha() alpha value}
	 * must be 255.
	 * If this method returns true, this ColorField
	 * allows any colors with any {@link Color#getAlpha() alpha value}.
	 */
	public boolean isAlphaAllowed()
	{
		return alphaAllowed;
	}

	public Color getDefaultConstant()
	{
		return fromRGB(rgb.getDefaultConstant());
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
	 * the {@code ColorField}
	 * {@link #isAlphaAllowed() allows alpha} or not.
	 */
	public IntegerField getRGB()
	{
		return rgb;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = rgb.getInitialExceptions();
		result.remove(IntegerRangeViolationException.class);
		if(!alphaAllowed)
			result.add(ColorAlphaViolationException.class);
		return result;
	}

	@Wrap(order=10,
			doc=Wrap.GET_DOC, nullability=NullableIfOptional.class)
	public Color get(@Nonnull final Item item)
	{
		return
			mandatory
			? fromRGB(rgb.getMandatory(item))
			: fromRGB(rgb.get(item));
	}

	private static Color fromRGB(final Integer rgb)
	{
		return (rgb!=null) ? fromRGB(rgb.intValue()) : null;
	}

	@Wrap(order=20,
			doc=Wrap.SET_DOC,
			hide=FinalSettableGetter.class,
			thrownGetter=InitialExceptionsSettableGetter.class)
	public void set(@Nonnull final Item item, @Parameter(nullability=NullableIfOptional.class) final Color value)
	{
		FinalViolationException.check(this, item);

		rgb.set(item, rgb(value, item));
	}

	@Override
	public SetValue<?>[] execute(final Color value, final Item exceptionItem)
	{
		return new SetValue<?>[]{ SetValue.map(rgb, rgb(value, exceptionItem)) };
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
	 * Returns the value for field {@link #getRGB()}
	 * representing the given color.
	 */
	public static int toRGB(final Color color)
	{
		return reverseAlpha(color.getRGB());
	}

	/**
	 * Converts a value for field {@link #getRGB()}
	 * into a color.
	 */
	public static Color fromRGB(final int rgb)
	{
		final boolean hasAlpha = rgb > 0xffffff || rgb < 0;
		return new Color(hasAlpha ? reverseAlpha(rgb) : rgb, hasAlpha);
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

	// convenience methods for conditions and views ---------------------------------

	public ColorFunction bind(final Join join)
	{
		return new ColorBindFunction(this, join);
	}

	@Override
	public IsNullCondition<?> isNull()
	{
		return rgb.isNull();
	}

	@Override
	public IsNullCondition<?> isNotNull()
	{
		return rgb.isNotNull();
	}

	@Override
	public Condition equal(final Color value)
	{
		if (value == null)
			return rgb.isNull();
		if (!alphaAllowed && value.getAlpha() != 255)
			return Condition.ofFalse();
		return rgb.equal(reverseAlpha(value.getRGB()));
	}

	/**
	 * NOT EQUAL Condition.
	 * <p>
	 * Note: according to SQL, a NULL value is evaluated to unknown, so a NOT EQUAL using a non null RHS is false for null values
	 */
	@Override
	public Condition notEqual(final Color value)
	{
		if (value == null)
			return rgb.isNotNull();
		if (!alphaAllowed && value.getAlpha() != 255)
			// ensure the fact:
			// a null value in DB is neither equal nor not equal to a given non-null param,
			// independent if this is in value range or not
			return rgb.isNotNull();
		return rgb.notEqual(reverseAlpha(value.getRGB()));
	}

	public Condition isOpaque()
	{
		return Cope.and(rgb.lessOrEqual(0xFFFFFF), rgb.greaterOrEqual(0));
	}

	public Condition isNotOpaque()
	{
		return Cope.or(rgb.greater(0xFFFFFF), rgb.less(0));
	}
}
