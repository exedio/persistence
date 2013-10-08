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

	public ColorField()
	{
		this(new IntegerField().range(0, 0xffffff));
	}

	private ColorField(final IntegerField rgb)
	{
		addSource(this.rgb = rgb, "rgb");
		this.mandatory = rgb.isMandatory();
	}

	public ColorField optional()
	{
		return new ColorField(rgb.optional());
	}

	public ColorField defaultTo(final Color defaultConstant)
	{
		return new ColorField(rgb.defaultTo(rgb(defaultConstant, null)));
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

	@Deprecated
	public Class<?> getInitialType()
	{
		return Color.class;
	}

	public Color getDefaultConstant()
	{
		return color(this.rgb.getDefaultConstant());
	}

	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = rgb.getInitialExceptions();
		result.remove(IntegerRangeViolationException.class);
		return result;
	}

	@Wrap(order=10,
			doc="Returns the value of {0}.")
	public Color get(final Item item)
	{
		if(mandatory)
		{
			return new Color(rgb.getMandatory(item));
		}
		else
		{
			return color(this.rgb.get(item));
		}
	}

	private static Color color(final Integer rgb)
	{
		return rgb!=null ? new Color(rgb) : null;
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
		else
		{
			if(value.getAlpha()!=255)
				throw new ColorTransparencyViolationException(this, exceptionItem, value);

			return value.getRGB() & 0xffffff;
		}
	}
}
