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

import com.exedio.cope.util.MessageDigestFactory;
import java.util.function.BooleanSupplier;

final class BucketProperties extends AbstractVaultProperties implements Bucket
{
	private final String bucket;


	private final MessageDigestFactory algorithm;

	@Override
	public MessageDigestFactory getAlgorithmFactory()
	{
		return algorithm;
	}

	@Override
	public String getAlgorithm()
	{
		return algorithm.getAlgorithm();
	}

	@Override
	public int getAlgorithmLength()
	{
		return algorithm.getLengthHex();
	}

	@Override
	public String getAlgorithmDigestForEmptyByteSequence()
	{
		return algorithm.getDigestForEmptyByteSequenceHex();
	}

	VaultResilientService resiliate(final VaultService service)
	{
		return new VaultResilientServiceProxy(service, algorithm);
	}


	private final Service service;

	VaultService newService(final BooleanSupplier markPut)
	{
		return service.newService(this, bucket, markPut);
	}


	private final TrailProperties trail;

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
		this.bucket = bucket;
		this.algorithm = parent.getAlgorithmFactory();
		service = valueService("service", writable);
		trail = valueTrail(parent.trail);
	}

	@Probe Object probeContract()
	{
		return new ContractProbe(this).call();
	}

	@Probe(name="BucketTag", order=1)
	Object probeBucketTag() throws Exception
	{
		try(VaultService s = service.newService(this, bucket, () -> false))
		{
			return s.probeBucketTag(bucket);
		}
		catch(final BucketTagNotSupported e)
		{
			throw newProbeAbortedException(e.getMessage());
		}
	}
}
