/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.mxsampler;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import java.lang.management.MemoryPoolMXBean;

final class MxSamplerMemoryPoolName extends Item
{
	private static final StringField name = new StringField().toFinal().unique();


	static boolean check(final MemoryPoolMXBean pool)
	{
		try
		{
			name.check(pool.getName());
			return true;
		}
		catch(final ConstraintViolationException e)
		{
			return false;
		}
	}

	static MxSamplerMemoryPoolName get(final MemoryPoolMXBean pool)
	{
		final String name = pool.getName();
		MxSamplerMemoryPoolName result = forName(name);
		if(result==null)
			result = TYPE.newItem(MxSamplerMemoryPoolName.name.map(name));

		return result;
	}


	String getID()
	{
		return name.get(this);
	}

	static MxSamplerMemoryPoolName forName(final String name)
	{
		return MxSamplerMemoryPoolName.name.searchUnique(MxSamplerMemoryPoolName.class, name);
	}

	private MxSamplerMemoryPoolName(final ActivationParameters ap) { super(ap); }

	private static final long serialVersionUID = 1l;

	static final Type<MxSamplerMemoryPoolName> TYPE = TypesBound.newType(MxSamplerMemoryPoolName.class);
}
