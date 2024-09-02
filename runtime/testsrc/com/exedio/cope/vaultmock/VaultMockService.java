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
import com.exedio.cope.vault.Bucket;
import com.exedio.cope.vault.VaultNotFoundException;
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
public class VaultMockService implements VaultService
{
	private final LinkedHashMap<String, String> store = new LinkedHashMap<>();
	private final StringBuilder history = new StringBuilder();
	public final Bucket bucketProperties;
	public final Props serviceProperties;
	public final String bucket;
	public final boolean writable;
	public final BooleanSupplier requiresToMarkPut;
	private boolean closed = false;

	protected VaultMockService(
			final VaultServiceParameters parameters,
			final Props properties)
	{
		this.bucketProperties = parameters.getBucketProperties();
		this.serviceProperties = properties;
		this.bucket = parameters.getBucket();
		this.writable = parameters.isWritable();
		this.requiresToMarkPut = parameters.requiresToMarkPut();
		assertNotNull(bucketProperties);
		assertNotNull(serviceProperties);
		assertNotNull(bucket);
		assertNotNull(requiresToMarkPut);
		assertNotNull(parameters.getMessageDigestFactory());
		assertSame(bucketProperties.getAlgorithmFactory(), parameters.getMessageDigestFactory());
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

		final String hex = store.get(hash);
		if(hex==null)
			throw new VaultNotFoundException(hash);
		return Hex.decodeLower(hex);
	}


	@Override
	public boolean put(final String hash, final byte[] value)
	{
		historyAppend("putBytes");

		return putInternal(hash, value);
	}

	private boolean putInternal(final String hash, final byte[] value)
	{
		assertHash(hash);
		assertNotNull(value);
		assertFalse(closed);

		assertFalse(
				value.length==0,
				"empty byte sequence is not handled by service implementations");
		assertEquals(hash, Hex.encodeLower(
				bucketProperties.getAlgorithmFactory().
						digest(value)));

		assertEquals(true, writable, "writable");

		return store.put(hash, Hex.encodeLower(value))==null;
	}

	@Override
	public boolean put(final String hash, final InputStream value) throws IOException
	{
		historyAppend("putStream");

		return putInternal(hash, value);
	}

	private boolean putInternal(final String hash, final InputStream value) throws IOException
	{
		assertHash(hash);
		assertNotNull(value);
		assertFalse(closed);

		return putInternal(hash, value.readAllBytes());
	}

	@Override
	public boolean put(final String hash, final Path value) throws IOException
	{
		historyAppend("putFile");

		assertHash(hash);
		assertNotNull(value);

		try(InputStream s = Files.newInputStream(value))
		{
			return putInternal(hash, s);
		}
	}


	private void assertHash(final String hash)
	{
		assertNotNull(hash);
		assertEquals(bucketProperties.getAlgorithmLength(), hash.length(), hash);
		assertEquals(-1, CharSet.HEX_LOWER.indexOfNotContains(hash),      hash);
		assertNotEquals(hash, bucketProperties.getAlgorithmDigestForEmptyByteSequence(), "empty byte sequence is not handled by service implementations");
	}

	public void clear()
	{
		store.clear();
	}

	public void put(final String hash, final String value)
	{
		assertHash(hash);
		assertNotNull(value);

		assertEquals(hash, Hex.encodeLower(
				bucketProperties.getAlgorithmFactory().
						digest(Hex.decodeLower(value))));

		store.put(hash, value);
	}

	@Override
	public String probeBucketTag(final String bucket) throws Exception
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
		final String probeResult = value("probe.result", "probeMockResult");
		final String bucketTagAction = value("bucketTagAction", "default");

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
		if(!history.isEmpty())
			history.append(' ');
		history.append(event);
	}
}
