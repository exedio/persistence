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
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public final class VaultAncestry
{
	private final String hash;
	private List<String> path = null;

	VaultAncestry(final String hash)
	{
		this.hash = hash;
	}

	@Nonnull
	public String hash()
	{
		return hash;
	}

	void addToPath(final String pathElement)
	{
		if(path==null)
			path = new ArrayList<>();

		path.add(requireNonEmpty(pathElement, "pathElement"));
	}

	/**
	 * Top path element comes first in result list.
	 * @see com.exedio.cope.vault.VaultService#addToAncestryPath(String, java.util.function.Consumer)
	 */
	@Nonnull
	public List<String> path()
	{
		return path!=null ? unmodifiableList(path) : emptyList();
	}
}
