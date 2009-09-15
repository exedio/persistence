/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.exedio.cope.Item;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.util.ReactivationConstructorDummy;

final class PatternItem extends Item
{
	private static final long serialVersionUID = 1l;

	PatternItem(final SetValue[] setValues, final Type<? extends Item> type)
	{
		super(setValues, type);
		assert type!=null;
	}

	PatternItem(final ReactivationConstructorDummy reactivationDummy, final int pk, final Type<? extends Item> type)
	{
		super(reactivationDummy, pk, type);
	}
	
	/**
	 * @param out must be there to be called by serialization
	 */
	private void writeObject(final ObjectOutputStream out) throws IOException
	{
		// TODO
		throw new NotSerializableException("not yet implemented for " + getClass());
	}
	
	/**
	 * @param in must be there to be called by serialization
	 */
	private void readObject(final ObjectInputStream in) throws IOException
	{
		// TODO
		throw new NotSerializableException("not yet implemented for " + getClass());
	}
}
