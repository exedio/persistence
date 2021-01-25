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

package com.exedio.cope.instrument;

import java.lang.reflect.Field;

final class ExternalCopeFeature extends CopeFeature
{
	private final Field field;

	ExternalCopeFeature(final ExternalCopeType parent, final Field field)
	{
		super(parent);
		this.field=field;
	}

	@Override
	String getName()
	{
		return field.getName();
	}

	@Override
	int getModifier()
	{
		return field.getModifiers();
	}

	@Override
	Boolean getInitialByConfiguration()
	{
		final WrapperInitial annotation=field.getAnnotation(WrapperInitial.class);
		return annotation==null?null:annotation.value();
	}

	@Override
	Object evaluate()
	{
		field.setAccessible(true);
		try
		{
			return field.get(null);
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	String getJavadocReference()
	{
		return link(getName());
	}

	@Override
	String applyTypeShortcuts(final String type)
	{
		return type;
	}
}
