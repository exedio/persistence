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

package com.exedio.cope;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

final class ItemWithoutJavaClass extends Item
{
	private static final long serialVersionUID = -8395452345350687651l;

	ItemWithoutJavaClass(final SetValue[] setValues, final Type<? extends Item> type)
	{
		super(setValues, type);
		assert type!=null;
	}

	ItemWithoutJavaClass(final int pk, final Type<? extends Item> type)
	{
		super(pk, type);
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
