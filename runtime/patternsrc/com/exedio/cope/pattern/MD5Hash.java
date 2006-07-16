/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import java.util.SortedSet;

import com.exedio.cope.Item;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.StringAttribute;
import com.exedio.cope.Attribute.Option;

public final class MD5Hash extends JavaHash
{
	private static final String HASH = "MD5";
	private static final int LENGTH = 32;

	public MD5Hash(final StringAttribute storage)
	{
		super(storage, HASH);
	}

	public MD5Hash(final StringAttribute storage, final String encoding)
	{
		super(storage, HASH, encoding);
	}

	public MD5Hash()
	{
		this(Item.MANDATORY);
	}
	
	public MD5Hash(final Option storageOption)
	{
		this(new StringAttribute(storageOption).lengthExact(LENGTH));
	}

	@Override
	public SortedSet<Class> getSetterExceptions()
	{
		final SortedSet<Class> result = super.getSetterExceptions();
		final StringAttribute storage = getStorage();
		if(storage.getMinimumLength()<=LENGTH && storage.getMaximumLength()>=LENGTH)
			result.remove(LengthViolationException.class);
		return result;
	}
	
}
