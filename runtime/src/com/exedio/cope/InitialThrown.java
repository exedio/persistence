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

package com.exedio.cope;

import com.exedio.cope.instrument.ThrownGetter;
import java.util.Set;

final class InitialThrown implements ThrownGetter<FunctionField<?>>
{
	@Override
	public Set<Class<? extends Throwable>> get(final FunctionField<?> feature)
	{
		final Set<Class<? extends Throwable>> result = feature.getInitialExceptions();
		if(feature.isPrimitive())
			result.remove(MandatoryViolationException.class);
		return result;
	}
}
