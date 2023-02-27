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
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.WrapFeature;
import java.util.Set;

@WrapFeature
public final class SettableSyntheticField extends Pattern implements Settable<Integer>
{
	private static final long serialVersionUID = 1l;

	final IntegerField source;

	public SettableSyntheticField()
	{
		this(new IntegerField());
	}

	private SettableSyntheticField(final IntegerField source)
	{
		this.source = addSourceFeature(source, "source");
	}

	public SettableSyntheticField toFinal()
	{
		return new SettableSyntheticField(source.toFinal());
	}

	@Override
	public boolean isInitial()
	{
		return source.isInitial();
	}

	@Override
	public boolean isFinal()
	{
		return source.isFinal();
	}

	@Override
	public boolean isMandatory()
	{
		return source.isMandatory();
	}

	@Override
	public Class<?> getInitialType()
	{
		return Integer.class;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return source.getInitialExceptions();
	}

	@Override
	public SetValue<?>[] execute(final Integer value, final Item exceptionItem)
	{
		return new SetValue<?>[]{ SetValue.map(source, value) };
	}
}

