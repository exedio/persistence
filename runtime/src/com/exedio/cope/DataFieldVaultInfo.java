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
	private final String serviceKey;
	private final String service;
	private final double getLength;
	private final double getBytes;
	private final double getStream;
	private final double putInitial;
	private final double putRedundant;

	DataFieldVaultInfo(
			final DataField field,
			final String serviceKey,
			final VaultService service,
			final Counter getLength,
			final Counter getBytes,
			final Counter getStream,
			final Counter putInitial,
			final Counter putRedundant)
	{
		this.field = field;
		this.serviceKey = serviceKey;
		this.service = service.toString();
		this.getLength = getLength.count();
		this.getBytes = getBytes.count();
		this.getStream = getStream.count();
		this.putInitial = putInitial.count();
		this.putRedundant = putRedundant.count();
	}

	public DataField getField()
	{
		return field;
	}

	public String getServiceKey()
	{
		return serviceKey;
	}

	public String getService()
	{
		return service;
	}

	public long getGetLengthCount()
	{
		return count(getLength);
	}

	public long getGetBytesCount()
	{
		return count(getBytes);
	}

	public long getGetStreamCount()
	{
		return count(getStream);
	}

	public long getGetCount()
	{
		return count(getLength) + count(getBytes) + count(getStream);
	}

	public long getPutInitialCount()
	{
		return count(putInitial);
	}

	public long getPutRedundantCount()
	{
		return count(putRedundant);
	}

	public long getPutCount()
	{
		return count(putInitial) + count(putRedundant);
	}
}
