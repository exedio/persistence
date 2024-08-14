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

import java.util.function.BooleanSupplier;
import org.opentest4j.AssertionFailedError;

final class VaultTester
{
	static VaultServiceParameters serviceParameters(
			final VaultProperties vaultProperties,
			final String bucket,
			final boolean writable)
	{
		return serviceParameters(
				vaultProperties,
				bucket,
				writable,
				MARK_PUT_FAILS);
	}

	private static final BooleanSupplier MARK_PUT_FAILS = () ->
	{
		throw new AssertionFailedError();
	};

	static VaultServiceParameters serviceParameters(
			final VaultProperties vaultProperties,
			final String bucket,
			final boolean writable,
			final BooleanSupplier markPut)
	{
		return new VaultServiceParameters(
				vaultProperties.bucketProperties("default"),
				bucket,
				writable,
				markPut);
	}


	private VaultTester()
	{
		// prevent instantiation
	}
}
