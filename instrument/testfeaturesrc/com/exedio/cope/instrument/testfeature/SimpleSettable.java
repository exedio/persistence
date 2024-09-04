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

package com.exedio.cope.instrument.testfeature;

import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import java.io.Serial;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

@WrapFeature
public class SimpleSettable extends Feature implements Settable<String>
{
	final boolean initial;

	public SimpleSettable()
	{
		this(false);
	}

	public SimpleSettable(final boolean initial)
	{
		this.initial=initial;
	}

	@Wrap(order=10)
	public String one(@SuppressWarnings("unused") final Item item)
	{
		return null;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return Collections.emptySet();
	}

	@Override
	public boolean isInitial()
	{
		return initial;
	}

	@Override
	public SetValue<?>[] execute(final String value, final Item exceptionItem)
	{
		throw new RuntimeException();
	}

	@Override
	public boolean isFinal()
	{
		throw new RuntimeException();
	}

	@Override
	public boolean isMandatory()
	{
		return false;
	}

	@Override
	public Type getInitialType()
	{
		return String.class;
	}

	@Serial
	private static final long serialVersionUID = 1l;
}
