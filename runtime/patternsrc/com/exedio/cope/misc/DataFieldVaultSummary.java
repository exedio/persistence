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

package com.exedio.cope.misc;

import com.exedio.cope.DataFieldVaultInfo;

public final class DataFieldVaultSummary
{
	private final long getBytes;
	private final long getStream;
	private final long putInitial;
	private final long putRedundant;

	public DataFieldVaultSummary(final DataFieldVaultInfo[] infos)
	{
		long getBytes = 0;
		long getStream = 0;
		long putInitial = 0;
		long putRedundant = 0;

		for(final DataFieldVaultInfo info : infos)
		{
			getBytes     += info.getGetBytesCount();
			getStream    += info.getGetStreamCount();
			putInitial   += info.getPutInitialCount();
			putRedundant += info.getPutRedundantCount();
		}

		this.getBytes     = getBytes;
		this.getStream    = getStream;
		this.putInitial   = putInitial;
		this.putRedundant = putRedundant;
	}

	/**
	 * To be deprecated, returns 0, as VaultService#getLength has been dropped
	 */
	public long getGetLengthCount()
	{
		return 0;
	}

	public long getGetBytesCount()
	{
		return getBytes;
	}

	public long getGetStreamCount()
	{
		return getStream;
	}

	public long getGetCount()
	{
		return getBytes + getStream;
	}

	public long getPutInitialCount()
	{
		return putInitial;
	}

	public long getPutRedundantCount()
	{
		return putRedundant;
	}

	public long getPutCount()
	{
		return putInitial + putRedundant;
	}
}
