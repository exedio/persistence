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

package com.exedio.cope.reflect;

import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.instrument.ConstructorComment;
import java.io.Serial;

@ConstructorComment("if {0} is forbidden.")
public final class ForbiddenFeatureException extends ConstraintViolationException
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final FeatureField<?> feature;
	private final Feature value;
	private final Class<? extends Feature> forbiddenValueClass;

	ForbiddenFeatureException(
			final FeatureField<?> feature,
			final Item item,
			final Feature value,
			final Class<? extends Feature> forbiddenValueClass)
	{
		super(item, null);
		this.feature = feature;
		this.value = value;
		this.forbiddenValueClass = forbiddenValueClass;
	}

	@Override
	public FeatureField<?> getFeature()
	{
		return feature;
	}

	/**
	 * Returns the value, that was attempted to be written.
	 */
	public Feature getValue()
	{
		return value;
	}

	/**
	 * @see FeatureField#getForbiddenValueClasses()
	 */
	public Class<? extends Feature> getForbiddenValueClass()
	{
		return forbiddenValueClass;
	}


	@Override
	public String getMessage(final boolean withFeature)
	{
		final StringBuilder sb = new StringBuilder();

		final Class<? extends Feature> valueClass = value.getClass();
		sb.append("forbidden feature ").append(value).
			append(" which is a ").	append(valueClass.getName()).
			append(getItemPhrase());

		if(withFeature)
			sb.append(" for ").
				append(feature);

		if(!valueClass.equals(forbiddenValueClass))
			sb.append(", is forbidden by ").
				append(forbiddenValueClass.getName());

		return sb.toString();
	}
}
