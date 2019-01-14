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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public final class TypeSet
{
	private final Type<?>[] explicitTypes;
	private final List<Type<?>> explicitTypesList;

	public TypeSet(final Type<?>... explicitTypes)
	{
		check(explicitTypes);
		this.explicitTypes = com.exedio.cope.misc.Arrays.copyOf(explicitTypes);
		this.explicitTypesList = Collections.unmodifiableList(Arrays.asList(this.explicitTypes));
	}

	static void check(final Type<?>... explicitTypes)
	{
		if(explicitTypes==null)
			throw new NullPointerException("explicitTypes");
		if(explicitTypes.length==0)
			throw new IllegalArgumentException("explicitTypes must not be empty");
		final HashSet<Type<?>> set = new HashSet<>();
		for(final Type<?> type : explicitTypes)
		{
			if(type==null)
				throw new NullPointerException("explicitTypes");
			if(!set.add(type))
				throw new IllegalArgumentException("duplicate type " + type);
		}
	}

	// TODO implement public get and contains
	// TODO do not forget source types of features of types recursively

	Type<?>[] getTypesArray()
	{
		return com.exedio.cope.misc.Arrays.copyOf(explicitTypes);
	}

	void addTo(final ArrayList<Type<?>> target)
	{
		target.addAll(Arrays.asList(explicitTypes));
	}

	/**
	 * Returns the explicit types of this {@code TypeSet}.
	 * This is the list of types fed into {@link TypeSet#TypeSet(Type[]) the constructor}.
	 * It does not include types created by patterns of explicit types.
	 */
	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // explicitTypesList is unmodifiable
	public List<Type<?>> getExplicitTypes()
	{
		return explicitTypesList;
	}
}
