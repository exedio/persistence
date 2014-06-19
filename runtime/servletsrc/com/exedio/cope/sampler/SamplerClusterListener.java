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

package com.exedio.cope.sampler;

import static com.exedio.cope.sampler.Util.map;

import com.exedio.cope.ClusterListenerInfo;
import com.exedio.cope.IntegerField;
import com.exedio.cope.SetValue;
import com.exedio.cope.pattern.Composite;

final class SamplerClusterListener extends Composite
{
	private static final long serialVersionUID = 1l;

	private static final IntegerField exception    = new IntegerField().min(0);
	private static final IntegerField missingMagic = new IntegerField().min(0);
	private static final IntegerField wrongSecret  = new IntegerField().min(0);
	private static final IntegerField fromMyself   = new IntegerField().min(0);

	public SamplerClusterListener(
			final ClusterListenerInfo from,
			final ClusterListenerInfo to)
	{
		this(
			map(exception,    from.getException   (), to.getException   ()),
			map(missingMagic, from.getMissingMagic(), to.getMissingMagic()),
			map(wrongSecret,  from.getWrongSecret (), to.getWrongSecret ()),
			map(fromMyself,   from.getFromMyself  (), to.getFromMyself  ()));
	}

	private SamplerClusterListener(final SetValue<?>... setValues)
	{
		super(setValues);
	}
}
