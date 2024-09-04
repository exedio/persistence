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

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.StringField;
import java.io.Serial;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

@com.exedio.cope.instrument.WrapFeature
public final class FeatureWithComponent extends Pattern implements Settable<Boolean>
{
	@Serial
	private static final long serialVersionUID = 1L;

	private final StringField firstComponent = new StringField();
	private final IntegerField secondComponent = new IntegerField();

	public FeatureWithComponent()
	{
		addSourceFeature(firstComponent, "first");
		addSourceFeature(secondComponent, "second");
	}

	public Object firstComponent()
	{
		return firstComponent;
	}

	public Object secondComponent()
	{
		return secondComponent;
	}

	@Override
	public boolean isInitial()
	{
		return false;
	}

	@Override
	public boolean isMandatory()
	{
		return false;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return Collections.emptySet();
	}

	@Override
	public SetValue<?>[] execute(final Boolean value, final Item exceptionItem)
	{
		throw new AssertionError();
	}

	@Override
	public boolean isFinal()
	{
		throw new AssertionError();
	}

	@Override
	public Type getInitialType()
	{
		throw new AssertionError();
	}
}
