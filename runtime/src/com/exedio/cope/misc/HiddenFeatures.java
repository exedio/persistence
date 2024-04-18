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

package com.exedio.cope.misc;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.This;
import com.exedio.cope.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;

public final class HiddenFeatures
{
	public static Map<Feature, Feature> get(final Model model)
	{
		final HashMap<Feature, Feature> result = new HashMap<>();
		accept(model, (f,hidden) ->
		{
			final Feature previous = result.putIfAbsent(f, hidden);
			assert previous==null;
		});
		return result;
	}

	public static void accept(
			@Nonnull final Model model,
			@Nonnull final BiConsumer<Feature, Feature> consumer)
	{
		for(final Type<?> t : model.getTypes())
		{
			final Type<?> st = t.getSupertype();
			if(st==null)
				continue;

			for(final Feature f : t.getDeclaredFeatures())
			{
				if(f instanceof This<?>)
					continue;

				final Feature hidden = st.getFeature(f.getName());
				if(hidden!=null)
					consumer.accept(f, hidden);
			}
		}
	}


	private HiddenFeatures()
	{
		// prevent instantiation
	}
}
