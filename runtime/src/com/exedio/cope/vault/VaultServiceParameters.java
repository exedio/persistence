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

import com.exedio.cope.util.Properties;
import javax.annotation.Nonnull;

public final class VaultServiceParameters
{
	private final VaultProperties vaultProperties;
	private final Properties serviceProperties;
	private final boolean writable;

	VaultServiceParameters(
			final VaultProperties vaultProperties,
			final Properties serviceProperties,
			final boolean writable)
	{
		this.vaultProperties = requireNonNull(vaultProperties, "vaultProperties");
		this.serviceProperties = requireNonNull(serviceProperties, "serviceProperties");
		this.writable = writable;
	}

	@Nonnull
	public VaultProperties getVaultProperties()
	{
		return vaultProperties;
	}

	/**
	 * @return an instance of the class specified by {@link VaultServiceProperties}.
	 */
	@Nonnull
	public Properties getServiceProperties()
	{
		return serviceProperties;
	}

	/**
	 * If this method returns false, put methods such as
	 * {@link VaultService#put(String, byte[])}
	 * will not be called.
	 * This happens typically for {@link VaultReferenceService reference vaults}.
	 */
	public boolean isWritable()
	{
		return writable;
	}
}
