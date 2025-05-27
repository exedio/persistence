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

package com.exedio.dsmf;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

final class Field<T>
{
	private final T required;
	private T existing;

	Field(final T value, final boolean required)
	{
		requireNonNull(value, "value");

		if(required)
		{
			this.required = value;
			this.existing = null;
		}
		else
		{
			this.required = null;
			this.existing = value;
		}
	}

	void notifyExists(final T value)
	{
		requireNonNull(value, "value");

		if(existing!=null && !existing.equals(value))
			throw new IllegalArgumentException(existing + "/" + value);

		this.existing = value;
	}

	T get()
	{
		if(required!=null)
			return required;
		else
			return existing;
	}

	boolean mismatches()
	{
		return
			required!=null &&
			existing!=null &&
			!required.equals(existing);
	}

	T getMismatching(final Node node)
	{
		return (!node.required() || !node.exists() || Objects.equals(required, existing))
			? null : existing;
	}

	T getRequired()
	{
		if(required==null)
			throw new IllegalStateException("not required");

		return required;
	}

	T getRequiredOrNull()
	{
		return required;
	}

	T getExisting()
	{
		if(existing==null)
			throw new IllegalStateException("not existing");

		return existing;
	}
}
