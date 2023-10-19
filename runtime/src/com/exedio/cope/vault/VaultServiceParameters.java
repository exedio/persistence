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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.DataFieldVaultInfo;
import com.exedio.cope.util.MessageDigestFactory;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;

public final class VaultServiceParameters
{
	private final VaultProperties vaultProperties;
	private final String bucket;
	private final boolean writable;
	private final BooleanSupplier markPut;

	VaultServiceParameters(
			final VaultProperties vaultProperties,
			final String bucket,
			final boolean writable,
			final BooleanSupplier markPut)
	{
		this.vaultProperties = requireNonNull(vaultProperties);
		this.bucket = requireNonNull(bucket);
		this.writable = writable;
		this.markPut = requireNonNull(markPut);
	}

	@Nonnull
	public VaultProperties getVaultProperties()
	{
		return vaultProperties;
	}

	/**
	 * Returns the bucket this service is responsible for.
	 * Intended for logging, metrics and similar purposes.
	 * BEWARE:
	 * Do NOT let your functionality depend on this key.
	 * @see DataFieldVaultInfo#getBucket()
	 */
	public String getBucket()
	{
		return bucket;
	}


	public MessageDigestFactory getMessageDigestFactory()
	{
		return vaultProperties.getAlgorithmFactory();
	}

	public String getMessageDigestAlgorithm()
	{
		return getMessageDigestFactory().getAlgorithm();
	}

	public int getMessageDigestLengthHex()
	{
		return getMessageDigestFactory().getLengthHex();
	}

	public String getMessageDigestForEmptyByteSequenceHex()
	{
		return getMessageDigestFactory().getDigestForEmptyByteSequenceHex();
	}


	/**
	 * If this method returns false, put methods such as
	 * {@link VaultService#put(String, byte[], VaultPutInfo)}
	 * will not be called.
	 * This happens typically for {@link VaultReferenceService reference vaults}.
	 */
	public boolean isWritable()
	{
		return writable;
	}

	VaultServiceParameters withWritable(final boolean writable)
	{
		// result.writable -> writable&&this.writable
		return
				(this.writable && !writable)
				? new VaultServiceParameters(vaultProperties, bucket, false, markPut)
				: this;
	}

	/**
	 * Whenever {@link BooleanSupplier#getAsBoolean() getAsBoolean} of the result
	 * of this method returns true, an instance of {@link VaultService} (the service)
	 * must consider itself in <i>mark-put</i> mode.
	 * While <i>mark-put</i> mode is active a call to any put method for a certain hash
	 * must mark the hash entry of that certain hash within the services' storage.
	 * <p>
	 * This mark is implementation-specific.
	 * A process analyzing the services' storage must be able to distinguish between
	 * hash entries marked and hash entry not marked.
	 * <p>
	 * This functionality is required for garbage collection of the services' storage.
	 * <p>
	 * It is safe to store the result of this method
	 * (but not the result of {@link BooleanSupplier#getAsBoolean() getAsBoolean})
	 * for the lifetime of the service.
	 */
	public BooleanSupplier requiresToMarkPut()
	{
		return markPut;
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
