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

import static com.exedio.cope.vault.VaultNotFoundException.anonymiseHash;

import com.exedio.cope.util.Hex;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

final class ContractProbe
{
	private final BucketProperties properties;

	ContractProbe(
			final BucketProperties properties)
	{
		this.properties = properties;
	}

	String call()
	{
		try(VaultResilientService s = properties.newService())
		{
			return probe(s);
		}
	}

	private String probe(final VaultResilientService service)
	{
		final String info = service.toString();
		String hostname;
		try
		{
			hostname = InetAddress.getLocalHost().getHostName();
		}
		catch(final UnknownHostException ignored)
		{
			hostname = "UNKNOWN";
		}

		// Must be consistent to VaultFileToTrail#CONTRACT_PROBE_PREFIX
		final byte[] value = (
				"Test file for " + VaultProperties.class.getName() + "#probe " +
				"from " + hostname + " " +
				"on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z (Z)", Locale.ENGLISH).format(new Date())).
				getBytes(StandardCharsets.US_ASCII);
		final String hash = Hex.encodeLower(properties.getAlgorithmFactory().digest(value));

		try
		{
			final byte[] gotValue = service.get(hash);
			throw new RuntimeException(
					info + ": get should have thrown VaultNotFoundException, " +
					"but got " + encodeValue(gotValue));
		}
		catch(final VaultNotFoundException e)
		{
			if(!hash.equals(e.getHashComplete()))
				throw new RuntimeException(
						info + ": VaultNotFoundException should have matching hash " +
						anonymiseHash(hash) + " vs. " + e.getHashAnonymous());
		}
		probeContains(service, hash, false, info);

		probeGetAndPut(service, hash, value, true,  info);
		probeGetAndPut(service, hash, value, false, info);

		return info + ' ' + anonymiseHash(hash);
	}

	private static void probeGetAndPut(
			final VaultResilientService service,
			final String hash,
			final byte[] value,
			final boolean putResult,
			final String info)
	{
		if(service.put(hash, value)!=putResult)
			throw new RuntimeException(info + ": put should have returned " + putResult);

		final byte[] gotValue;
		try
		{
			gotValue = service.get(hash);
		}
		catch(final VaultNotFoundException e)
		{
			throw new RuntimeException(info + ": get should have returned value", e);
		}
		if(!Arrays.equals(value, gotValue))
			throw new RuntimeException(
					info + ": get should have returned matching value " +
					encodeValue(value) + " vs. " +
					encodeValue(gotValue));

		probeContains(service, hash, true, info);
	}

	private static void probeContains(
			final VaultResilientService service,
			final String hash,
			final boolean value,
			final String info)
	{
		try
		{
			if(value!=service.contains(hash))
				throw new RuntimeException(info + ": contains should have returned " + value);
		}
		catch(final VaultServiceUnsupportedOperationException ignored)
		{
		}
	}

	private static String encodeValue(final byte[] value)
	{
		final int length = value.length;
		return
				length<=50
				? Hex.encodeLower(value)
				: Hex.encodeLower(value, 0, 50) + "...(" + value.length + ')';
	}
}
