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
	private final String bucket;
	private final String service;
	private final double getBytes;
	private final double getStream;
	private final double putInitial;
	private final double putRedundant;

	DataFieldVaultInfo(
			final DataField field,
			final String bucket,
			final VaultService service,
			final Counter getBytes,
			final Counter getStream,
			final Counter putInitial,
			final Counter putRedundant)
	{
		this.field = field;
		this.bucket = bucket;
		this.service = service.toString();
		this.getBytes = getBytes.count();
		this.getStream = getStream.count();
		this.putInitial = putInitial.count();
		this.putRedundant = putRedundant.count();
	}

	public DataField getField()
	{
		return field;
	}

	/**
	 * Never returns {@link Vault#NONE}.
	 */
	public String getBucket()
	{
		return bucket;
	}

	public String getService()
	{
		return service;
	}

	/**
	 * @deprecated returns 0, as VaultService#getLength has been dropped
	 */
	@Deprecated
	public long getGetLengthCount()
	{
		return 0;
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
		return count(getBytes) + count(getStream);
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


	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #getBucket()} instead.
	 */
	@Deprecated
	public String getServiceKey()
	{
		return getBucket();
	}
}
