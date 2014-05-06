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

package com.exedio.cope.instrument.testfeature;

import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SettableFixedParamImpl extends Pattern implements SettableFixedParamInterface
{
	private static final long serialVersionUID = 1l;

	@Override
	public boolean isInitial()
	{
		return true;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return Collections.<Class<? extends Throwable>>emptySet();
	}




	@Override
	public SetValue<AtomicReference<AtomicBoolean>> map(final AtomicReference<AtomicBoolean> value)
	{
		throw new AssertionError();
	}

	@Override
	public SetValue<?>[] execute(final AtomicReference<AtomicBoolean> value, final Item exceptionItem)
	{
		throw new AssertionError();
	}

	@Override
	public boolean isFinal()
	{
		throw new AssertionError();
	}

	@Override
	public boolean isMandatory()
	{
		throw new AssertionError();
	}

	@Override
	public Type getInitialType()
	{
		throw new AssertionError();
	}
}
