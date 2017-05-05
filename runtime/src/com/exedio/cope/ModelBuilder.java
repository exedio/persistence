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

import static com.exedio.cope.misc.Check.requireNonEmpty;
import static com.exedio.cope.misc.Check.requireNonEmptyAndCopy;
import static java.util.Objects.requireNonNull;

public final class ModelBuilder
{
	private String name;
	private Type<?>[] types;
	private TypeSet[] typeSets;
	private Revisions.Factory revisions;

	ModelBuilder()
	{
		// just make package private
	}

	public ModelBuilder name(final String name)
	{
		this.name = requireNonEmpty(name, "name");
		return this;
	}

	public ModelBuilder name(final Class<?> clazz)
	{
		return name(clazz.getName());
	}

	public ModelBuilder add(final Type<?>... types)
	{
		requireNull(this.types);
		this.types = requireNonEmptyAndCopy(types, "types");
		return this;
	}

	public ModelBuilder add(final TypeSet... typeSets)
	{
		requireNull(this.typeSets);
		this.typeSets = requireNonEmptyAndCopy(typeSets, "typeSets");
		return this;
	}

	public ModelBuilder add(final Revisions.Factory revisions)
	{
		requireNull(this.revisions);
		this.revisions = requireNonNull(revisions, "revisions");
		return this;
	}

	private static void requireNull(final Object o)
	{
		if(o!=null)
			throw new IllegalStateException("already set");
	}

	public Model build()
	{
		return new Model(name, revisions, typeSets, types);
	}
}
