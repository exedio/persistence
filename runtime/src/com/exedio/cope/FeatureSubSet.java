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

package com.exedio.cope;

import com.exedio.cope.misc.ListUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class FeatureSubSet<F extends Feature>
{
	static <F extends Feature> FeatureSubSet<F> features(
			final FeatureSubSet<F> inherited,
			final List<Feature> declaredFeatures,
			final Class<F> featureClass)
	{
		return new FeatureSubSet<>(inherited, declaredFeatures, featureClass);
	}

	final List<F> declared;
	final List<F> all;

	private FeatureSubSet(
			final FeatureSubSet<F> inherited,
			final List<Feature> declaredFeatures,
			final Class<F> featureClass)
	{
		{
			final ArrayList<F> declared = new ArrayList<>(declaredFeatures.size());
			for(final Feature feature : declaredFeatures)
			{
				if(featureClass.isInstance(feature))
					declared.add(featureClass.cast(feature));
			}
			this.declared = ListUtil.trimUnmodifiable(declared);
		}

		this.all = (inherited==null) ? this.declared : inherit(inherited.all, this.declared);
	}

	private static final <F extends Feature> List<F> inherit(final List<F> inherited, final List<F> declared)
	{
		assert inherited!=null;

		if(declared.isEmpty())
			return inherited;
		else if(inherited.isEmpty())
			return declared;
		else
		{
			final ArrayList<F> result = new ArrayList<>(inherited);
			result.addAll(declared);
			result.trimToSize();
			return Collections.<F>unmodifiableList(result);
		}
	}
}
