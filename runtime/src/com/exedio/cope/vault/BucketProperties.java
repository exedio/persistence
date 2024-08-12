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

final class BucketProperties extends AbstractVaultProperties implements Bucket
{
	private final VaultProperties parent;
	private final String bucket;
	final Service service;


	private final TrailProperties trail;

	private TrailProperties valueTrail()
	{
		return valnp("trail", TrailProperties::new);
	}

	@Override
	public int getTrailStartLimit()
	{
		return trail.startLimit;
	}

	@Override
	public int getTrailFieldLimit()
	{
		return trail.fieldLimit;
	}

	@Override
	public int getTrailOriginLimit()
	{
		return trail.originLimit;
	}


	BucketProperties(
			final Source source,
			final VaultProperties parent,
			final String bucket,
			final boolean writable)
	{
		super(source);
		this.parent = parent;
		this.bucket = bucket;
		service = valueService("service", writable);
		trail = valueTrail();
	}

	@Probe Object probeContract()
	{
		return new ContractProbe(parent, bucket, service).call();
	}

	@Probe(name="BucketTag", order=1)
	Object probeBucketTag() throws Exception
	{
		try(VaultService s = service.newService(parent, bucket, () -> false))
		{
			return s.probeBucketTag(bucket);
		}
		catch(final BucketTagNotSupported e)
		{
			throw newProbeAbortedException(e.getMessage());
		}
	}
}
