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

package com.exedio.cope.vault;

import com.exedio.cope.DataField;
import com.exedio.cope.Item;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.annotation.Nullable;

/**
 * Did supply additional information to put methods such as
 * {@link VaultService#put(String, byte[])}.
 * Implementations of {@link VaultService} may choose to
 * store this information for statistical purposes,
 * but no functionality should depend on it.
 * @deprecated No longer used
 */
@Deprecated
public interface VaultPutInfo
{
	/**
	 * The {@link DataField field} this vault entry is put for.
	 * <p>
	 * Implementation may not be able to implement this method,
	 * but only {@link #getFieldString()}.
	 */
	@Nullable
	default DataField getField()
	{
		return null;
	}

	/**
	 * The {@link com.exedio.cope.Feature#getID() id} of the
	 * {@link DataField field} this vault entry is put for.
	 *
	 * @see #getField()
	 */
	@Nullable
	default String getFieldString()
	{
		final DataField field = getField();
		return field!=null ? field.getID() : null;
	}

	/**
	 * The {@link Item item} this vault entry is put for.
	 * <p>
	 * Implementation may not be able to implement this method,
	 * but only {@link #getItemString()}.
	 */
	@Nullable
	default Item getItem()
	{
		return null;
	}

	/**
	 * The {@link Item#getCopeID() id} of the
	 * {@link Item item} this vault entry is put for.
	 *
	 * @see #getItem()
	 */
	@Nullable
	default String getItemString()
	{
		final Item item = getItem();
		return item!=null ? item.getCopeID() : null;
	}

	/**
	 * A description of the origin of the put request.
	 * Could be the host name or source IP address for instance.
	 * The default implementation returns {@link #getOriginDefault()}.
	 */
	@Nullable
	default String getOrigin()
	{
		return getOriginDefault();
	}

	/**
	 * Returns the local host name.
	 * Is the default implementation of {@link #getOrigin()}.
	 */
	static String getOriginDefault()
	{
		try
		{
			return InetAddress.getLocalHost().getHostName();
		}
		catch(final UnknownHostException ignored)
		{
			return null;
		}
	}
}
