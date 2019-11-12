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

import static com.exedio.cope.InfoRegistry.count;

import com.exedio.cope.vault.VaultService;
import io.micrometer.core.instrument.Counter;

public final class DataFieldVaultInfo
{
	private final DataField field;
	private final String service;
	private final long getLength;
	private final long getBytes;
	private final long getStream;
	private final long putInitial;
	private final long putRedundant;

	DataFieldVaultInfo(
			final DataField field,
			final VaultService service,
			final Counter getLength,
			final Counter getBytes,
			final Counter getStream,
			final Counter putInitial,
			final Counter putRedundant)
	{
		this.field = field;
		this.service = service.toString();
		this.getLength = count(getLength);
		this.getBytes = count(getBytes);
		this.getStream = count(getStream);
		this.putInitial = count(putInitial);
		this.putRedundant = count(putRedundant);
	}

	public DataField getField()
	{
		return field;
	}

	public String getService()
	{
		return service;
	}

	public long getGetLengthCount()
	{
		return getLength;
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
		return getLength + getBytes + getStream;
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
