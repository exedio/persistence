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

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.ComputedElement;
import java.io.Serial;
import javax.annotation.Nonnull;

@WrapFeature
public final class Singleton extends Pattern
{
	@Serial
	private static final long serialVersionUID = 1l;

	private static final int THE_ONE = 42;
	private static final Integer THE_ONE_OBJECT = THE_ONE;

	private final IntegerField source =
		new IntegerField().toFinal().unique().
				defaultTo(THE_ONE_OBJECT).rangeEvenIfRedundant(THE_ONE, THE_ONE);

	public Singleton()
	{
		addSourceFeature(source, "once", ComputedElement.get());
	}

	public IntegerField getSource()
	{
		return source;
	}

	@Wrap(order=10,
			name="instance",
			doc={"Gets the single instance of {2}.", "Creates an instance, if none exists."},
			docReturn="never returns null.")
	@Nonnull
	public <P extends Item> P instance(@Nonnull final Class<P> typeClass)
	{
		final Type<P> type =
				requireParentClass(typeClass, "typeClass");
		final P found = type.searchSingleton(source.equal(THE_ONE_OBJECT));
		if(found!=null)
			return found;
		else
			return type.newItem();
	}
}
