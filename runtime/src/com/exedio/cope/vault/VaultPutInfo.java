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

public interface VaultPutInfo
{
	@Nullable
	default DataField getField()
	{
		return null;
	}

	@Nullable
	default String getFieldString()
	{
		final DataField field = getField();
		return field!=null ? field.getID() : null;
	}

	@Nullable
	default Item getItem()
	{
		return null;
	}

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
