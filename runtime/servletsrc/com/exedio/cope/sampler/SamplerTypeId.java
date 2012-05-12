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

package com.exedio.cope.sampler;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;

final class SamplerTypeId extends Item
{
	private static final StringField id = new StringField().toFinal().unique();


	static SamplerTypeId get(final Type type)
	{
		final String id = type.getID();
		SamplerTypeId result = forId(id);
		if(result==null)
			result = TYPE.newItem(SamplerTypeId.id.map(id));
		return result;
	}


	@SuppressWarnings("unused")
	private SamplerTypeId(final ActivationParameters ap)
	{
		super(ap);
	}

	String getID()
	{
		return id.get(this);
	}

	static SamplerTypeId forId(final String id)
	{
		return SamplerTypeId.id.searchUnique(SamplerTypeId.class, id);
	}

	private static final long serialVersionUID = 1l;

	static final Type<SamplerTypeId> TYPE = TypesBound.newType(SamplerTypeId.class);
}
