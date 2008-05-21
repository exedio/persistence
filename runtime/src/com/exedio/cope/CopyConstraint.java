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

public final class CopyConstraint extends Feature
{
	private final ItemField target;
	private final FunctionField copy;

	public CopyConstraint(final ItemField target, final FunctionField copy)
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
	}
	
	public ItemField getTarget()
	{
		return target;
	}

	public FunctionField getCopy()
	{
		return copy;
	}
	
	private FunctionField template = null;
	
	public FunctionField getTemplate()
	{
		if(template!=null)
			return template;
		
		final Feature feature = target.getValueType().getFeature(copy.getName());
		if(feature==null)
			throw new RuntimeException("not found on copy: " + this);
		if(!(feature instanceof FunctionField))
			throw new ClassCastException("not a FunctionField on copy: " + this + '/' + feature + '/' + feature.getClass().getName());
		final FunctionField result = (FunctionField)feature;
		if(!result.isfinal)
			throw new RuntimeException("not final on copy: " + this + '/' + result);
		
		template = result;
		return result;
	}
	
	void check(final Map<Field, Object> fieldValues)
	{
		final Item targetItem = (Item)fieldValues.get(target);
		if(targetItem!=null)
		{
			final Object expectedValue = getTemplate().get(targetItem);
			final Object actualValue = fieldValues.get(copy);
			if(expectedValue==null ? actualValue!=null : !expectedValue.equals(actualValue))
				throw new CopyViolationException(targetItem, this, expectedValue, actualValue);
		}
	}
}
