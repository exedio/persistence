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

import com.exedio.cope.instrument.ConstructorComment;
import java.io.Serial;
import java.sql.SQLException;
import java.util.List;

/**
 * Signals, that an attempt to write an field has been failed,
 * and the value to be set violated a unique constraint.
 * <p>
 * This exception will be thrown by {@link Item#set(FunctionField,Object) Item.set}
 * and item constructors
 * if that field is covered by a {@link UniqueConstraint unique constraint}
 * and the value to be set violated the uniqueness.
 *
 * @author Ralf Wiebicke
 */
@ConstructorComment("if {0} is not unique.")
public final class UniqueViolationException extends ConstraintViolationException
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final UniqueConstraint feature;

	/**
	 * Creates a new UniqueViolationException with the necessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param feature initializes, what is returned by {@link #getFeature()}.
	 * @throws NullPointerException if {@code constraint} is null.
	 */
	UniqueViolationException(
			final UniqueConstraint feature,
			final Item item,
			final SQLException cause)
	{
		super(item, cause);
		this.feature = feature;
	}

	/**
	 * Returns the violated constraint.
	 */
	@Override
	public UniqueConstraint getFeature()
	{
		return feature;
	}

	public Feature getFeatureForDescription()
	{
		final List<FunctionField<?>> fields = feature.getFields();
		return fields.size()==1 ? fields.get(0) : feature;
	}

	public List<FunctionField<?>> getFields()
	{
		return feature.getFields();
	}

	@Override
	public String getMessage(final boolean withFeature)
	{
		return "unique violation" + getItemPhrase() + (withFeature ? (" for " + feature) : "");
	}
}
