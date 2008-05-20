/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.util.Map;
import java.util.Set;

public final class CopyField<E> extends Pattern implements Settable<E>
{
	private final ItemField target;
	private final FunctionField<E> copy;

	private CopyField(final ItemField target, final FunctionField<E> copy)
	{
		if(target==null)
			throw new NullPointerException("target must not be null");
		if(copy==null)
			throw new NullPointerException("copy must not be null");
		if(!target.isfinal)
			throw new IllegalArgumentException("target must be final");
		if(!copy.isfinal)
			throw new IllegalArgumentException("copy must be final");

		this.target = target;
		this.copy = copy;
		registerSource(copy);
	}
	
	public static final <E> CopyField<E> newField(final ItemField target, final FunctionField<E> copy)
	{
		return new CopyField<E>(target, copy);
	}

	public ItemField getTarget()
	{
		return target;
	}

	public FunctionField<E> getCopy()
	{
		return copy;
	}
	
	@Override
	public void initialize()
	{
		if(!copy.isInitialized())
			initialize(copy, getName() + "Copy");
	}
	
	public SetValue[] execute(final E value, final Item exceptionItem)
	{
		return copy.execute(value, exceptionItem);
	}

	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return copy.getInitialExceptions();
	}

	public Class getInitialType()
	{
		return copy.getInitialType();
	}

	public boolean isFinal()
	{
		return copy.isFinal();
	}

	public boolean isInitial()
	{
		return copy.isInitial();
	}

	public SetValue map(E value)
	{
		return new SetValue<E>(this, value);
	}
	
	void check(final SetValue v, final Map<Field, Object> fieldValues)
	{
		final Item targetItem = (Item)fieldValues.get(target);
		if(targetItem!=null)
		{
		final FunctionField templateField = (FunctionField)target.getValueType().getFeature(getName());
		if(templateField==null)
			throw new RuntimeException("not found on copy: " + targetItem + '/' + this);
		if(!templateField.isfinal)
			throw new RuntimeException("not final on copy: " + targetItem + '/' + this + '/' + templateField);
		final Object templateValue = templateField.get(targetItem);
		final Object copyValue = v.value;
		if(templateValue==null ? copyValue!=null : !templateValue.equals(copyValue))
			throw new IllegalArgumentException("mismatch on copy: " + targetItem + '/' + this + '/' + templateValue + '/' + copyValue);
		}
	}
}
