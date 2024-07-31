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

package com.exedio.cope.vaultmock;

import static com.exedio.cope.vault.VaultPropertiesTest.getVaultProperties;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.ServiceProperties;
import com.exedio.cope.vault.VaultNotFoundException;
import com.exedio.cope.vault.VaultProperties;
import com.exedio.cope.vault.VaultPutInfo;
import com.exedio.cope.vault.VaultService;
import com.exedio.cope.vault.VaultServiceParameters;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

@ServiceProperties(VaultMockService.Props.class)
public final class VaultMockService implements VaultService
{
	private final LinkedHashMap<String, String> store = new LinkedHashMap<>();
	private final StringBuilder history = new StringBuilder();
	public final VaultProperties vaultProperties;
	public final Props serviceProperties;
	public final String bucket;
	public final boolean writable;
	public final BooleanSupplier requiresToMarkPut;
	private boolean closed = false;

	private VaultMockService(
			final VaultServiceParameters parameters,
			final Props properties)
	{
		this.vaultProperties = getVaultProperties(parameters);
		this.serviceProperties = properties;
		this.bucket = parameters.getBucket();
		this.writable = parameters.isWritable();
		this.requiresToMarkPut = parameters.requiresToMarkPut();
		assertNotNull(vaultProperties);
		assertNotNull(serviceProperties);
		assertNotNull(bucket);
		assertNotNull(requiresToMarkPut);
		assertNotNull(parameters.getMessageDigestFactory());
		assertSame(vaultProperties.getAlgorithmFactory(), parameters.getMessageDigestFactory());
	}

	@Override
	public void purgeSchema(final JobContext ctx)
	{
		historyAppend("purgeSchema");
		assertNotNull(ctx);
		assertFalse(closed);
	}

	@Override
	public void close()
	{
		historyAppend("close");
		assertFalse(closed);
		closed = true;
	}

	public boolean isClosed()
	{
		return closed;
	}


	@Override
	@SuppressWarnings("unused") // OK: method will be dropped soon
	public long getLength(final String hash) throws VaultNotFoundException
	{
		historyAppend("getLength");

		return store(hash).length;
	}

	@Override
	public byte[] get(final String hash) throws VaultNotFoundException
	{
		historyAppend("getBytes");

		return store(hash);
	}

	@Override
	public void get(final String hash, final OutputStream sink) throws VaultNotFoundException, IOException
	{
		historyAppend("getStream");

		assertNotNull(sink);

		sink.write(store(hash));
	}

	private byte[] store(final String hash) throws VaultNotFoundException
	{
		assertHash(hash);
		assertFalse(closed);

		if(serviceProperties.failGet)
			throw new IllegalStateException("deliberately fail in VaultMockService#get");

		final String hex = store.get(hash);
		if(hex==null)
			throw new VaultNotFoundException(hash);
		return Hex.decodeLower(hex);
	}


	@Override
	public boolean put(final String hash, final byte[] value, final VaultPutInfo info)
	{
		historyAppend("putBytes");

		return putInternal(hash, value, info);
	}

	private boolean putInternal(final String hash, final byte[] value, final VaultPutInfo info)
	{
		assertHash(hash);
		assertNotNull(value);
		assertInfo(info);
		assertFalse(closed);

		assertFalse(
				value.length==0,
				"empty byte sequence is not handled by service implementations");
		assertEquals(hash, Hex.encodeLower(
				vaultProperties.getAlgorithmFactory().
						digest(value)));

		assertEquals(true, writable, "writable");

		if(serviceProperties.failPut)
			throw new IllegalStateException("deliberately fail in VaultMockService#put");

		return store.put(hash, Hex.encodeLower(value))==null;
	}

	@Override
	public boolean put(final String hash, final InputStream value, final VaultPutInfo info) throws IOException
	{
		historyAppend("putStream");

		return putInternal(hash, value, info);
	}

	private boolean putInternal(final String hash, final InputStream value, final VaultPutInfo info) throws IOException
	{
		assertHash(hash);
		assertNotNull(value);
		assertInfo(info);
		assertFalse(closed);

		return putInternal(hash, value.readAllBytes(), info);
	}

	@Override
	public boolean put(final String hash, final Path value, final VaultPutInfo info) throws IOException
	{
		historyAppend("putFile");

		assertHash(hash);
		assertNotNull(value);

		try(InputStream s = Files.newInputStream(value))
		{
			return putInternal(hash, s, info);
		}
	}


	private void assertHash(final String hash)
	{
		assertNotNull(hash);
		assertEquals(vaultProperties.getAlgorithmLength(), hash.length(), hash);
		assertEquals(-1, CharSet.HEX_LOWER.indexOfNotContains(hash),      hash);
		assertNotEquals(hash, vaultProperties.getAlgorithmDigestForEmptyByteSequence(), "empty byte sequence is not handled by service implementations");
	}

	private void assertInfo(final VaultPutInfo info)
	{
		assertNotNull(info);
		if(serviceProperties.assertInfoResilient)
		{
			assertAll(
					() -> assertEquals(null, info.getField(),       "field"),
					() -> assertEquals(null, info.getFieldString(), "fieldString"),
					() -> assertEquals(null, info.getItem(),        "item"),
					() -> assertEquals(null, info.getItemString(),  "itemString"),
					() -> assertEquals(null, info.getOrigin(),      "origin"),
					() -> assertEquals(assertInfoString, info.toString(), "toString"));
		}
	}

	public String assertInfoString = "VaultResilientServicePutInfo";

	public void clear()
	{
		store.clear();
	}

	public void put(final String hash, final String value)
	{
		assertHash(hash);
		assertNotNull(value);

		assertEquals(hash, Hex.encodeLower(
				vaultProperties.getAlgorithmFactory().
						digest(Hex.decodeLower(value))));

		store.put(hash, value);
	}

	@Override
	public String probeGenuineServiceKey(final String bucket) throws Exception
	{
		final String matcher = serviceProperties.bucketTagAction;
		final String result = matcher.equals(bucket) ? matcher : (matcher + "(" + bucket + ")");
		if(matcher.contains("ABORT"))
			throw VaultService.newProbeAborter(result);
		if(matcher.contains("FAIL"))
			throw new IllegalStateException(result);
		else
			return "mock:" + result;
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ':' + serviceProperties.example;
	}


	public static final class Props extends Properties
	{
		public final String example = value("example", "exampleDefault");
		final boolean failGet = value("fail.get", false);
		final boolean failPut = value("fail.put", false);
		final String probeResult = value("probe.result", "probeMockResult");
		final String bucketTagAction = value("bucketTagAction", "default");
		private final boolean assertInfoResilient = value("assertInfoResilient", true);

		Props(final Source source)
		{
			super(source);
		}

		@Probe private String probeMock()
		{
			return probeResult;
		}
	}


	public void assertIt(final String history)
	{
		assertIt(Collections.emptyMap(), history);
	}

	public void assertIt(
			final String hash, final String value,
			final String history)
	{
		assertIt(Collections.singletonMap(hash, value), history);
	}

	public void assertIt(
			final String hash1, final String value1,
			final String hash2, final String value2,
			final String history)
	{
		final LinkedHashMap<String,String> expected = new LinkedHashMap<>();
		expected.put(hash1, value1);
		expected.put(hash2, value2);
		assertIt(expected, history);
	}

	private void assertIt(
			final Map<String, String> store,
			final String history)
	{
		assertEquals(store,   this.store,              "store");
		assertEquals(history, this.history.toString(), "history");
		this.history.setLength(0);
	}

	private void historyAppend(final String event)
	{
		if(history.length()>0)
			history.append(' ');
		history.append(event);
	}
}
