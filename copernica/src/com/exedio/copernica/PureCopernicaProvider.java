/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
package com.exedio.copernica;

import com.exedio.cope.Model;
import com.exedio.cope.Type;

final class PureCopernicaProvider extends TransientCopernicaProvider
{
	
	private final Model model;
	
	PureCopernicaProvider(final Model model)
	{
		this.model = model;

		final TransientUser admin = new TransientUser("admin", "nimda", "Administrator");
		setTransientUsers(
			new TransientUser[]{
				admin,
			}
		);
	}

	public Model getModel()
	{
		return model;
	}

	public boolean requiresAuthorization()
	{
		return false;
	}
	
	@Override
	public int getLimitCountCeiling(final Type type)
	{
		return Integer.MAX_VALUE;
	}
}
