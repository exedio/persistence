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

import static com.exedio.cope.util.Check.requireNonEmpty;
import static com.exedio.cope.util.Check.requireNonEmptyAndCopy;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.misc.ChangeHooks;

public final class ModelBuilder
{
	private String name;
	private Type<?>[] types;
	private TypeSet[] typeSets;
	private ChangeHook.Factory changeHook;
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
		requireNotYetAdded(this.types, "Type<?>...");
		this.types = requireNonEmptyAndCopy(types, "types");
		return this;
	}

	public ModelBuilder add(final TypeSet... typeSets)
	{
		requireNotYetAdded(this.typeSets, "TypeSet...");
		this.typeSets = requireNonEmptyAndCopy(typeSets, "typeSets");
		return this;
	}

	/**
	 * Installs a list of {@link ChangeHook change hooks}
	 * for the model to be built by this model builder.
	 * Hooks will be called in the order specified here.
	 * <p>
	 * If you do not call this method, a single {@link DefaultChangeHook} will be installed.
	 * If you want to use a {@link DefaultChangeHook} additionally to other hooks,
	 * you have specify it here together with the other hooks.
	 */
	public ModelBuilder changeHooks(final ChangeHook.Factory... hooks)
	{
		requireNotYetAdded(this.changeHook, "ChangeHook.Factory...");
		this.changeHook = ChangeHooks.cascade(hooks);
		return this;
	}

	public ModelBuilder add(final Revisions.Factory revisions)
	{
		requireNotYetAdded(this.revisions, "Revisions.Factory");
		this.revisions = requireNonNull(revisions, "revisions");
		return this;
	}

	private static void requireNotYetAdded(final Object o, final String parameter)
	{
		if(o!=null)
			throw new IllegalStateException("add(" +  parameter + ") already called, must be called at most once");
	}

	public Model build()
	{
		return new Model(
				name, revisions, typeSets, types,
				changeHook!=null ? changeHook : DefaultChangeHook.factory());
	}
}
