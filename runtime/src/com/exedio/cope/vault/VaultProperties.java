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

import com.exedio.cope.DataField;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.Vault;
import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.MessageDigestFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nonnull;

public final class VaultProperties extends AbstractVaultProperties
{
	/**
	 * TODO move to {@link BucketProperties}.
	 */
	private final MessageDigestFactory algorithm = valueMessageDigest("algorithm", "SHA-512");

	/**
	 * To be deprecated when {@code algorithm} can be set per bucket.
	 * Use {@link VaultServiceParameters#getMessageDigestFactory()} instead.
	 */
	public MessageDigestFactory getAlgorithmFactory()
	{
		return algorithm;
	}

	/**
	 * @deprecated {@code algorithm} is set per bucket.
	 * Use {@link VaultServiceParameters#getMessageDigestAlgorithm()} instead.
	 */
	@Deprecated
	public String getAlgorithm()
	{
		return algorithm.getAlgorithm();
	}

	/**
	 * @deprecated {@code algorithm} is set per bucket.
	 * Use {@link VaultServiceParameters#getMessageDigestLengthHex()} instead.
	 */
	@Deprecated
	public int getAlgorithmLength()
	{
		return algorithm.getLengthHex();
	}

	/**
	 * @deprecated {@code algorithm} is set per bucket.
	 * Use {@link VaultServiceParameters#getMessageDigestForEmptyByteSequenceHex()} instead.
	 */
	@Deprecated
	public String getAlgorithmDigestForEmptyByteSequence()
	{
		return algorithm.getDigestForEmptyByteSequenceHex();
	}



	final Map<String, BucketProperties> buckets;

	private Map<String, BucketProperties> valueBuckets(final boolean writable)
	{
		final List<String> bucketList;
		{
			final String KEY = "buckets";
			bucketList = valuesSpaceSeparated(KEY, Vault.DEFAULT);
			if(bucketList.isEmpty())
				throw newException(KEY, "must not be empty");

			for(final String bucket : bucketList)
				checkBucket(bucket, message ->
						newException(KEY,
							"must contain a space-separated list of buckets, " +
							"but bucket >" + bucket + "< is illegal: " + message));

			if( new HashSet<>(bucketList).size() < bucketList.size() )
				throw newException(KEY, "must not contain duplicates");
		}

		final LinkedHashMap<String, BucketProperties> buckets = new LinkedHashMap<>();
		for(final String bucket : bucketList)
			buckets.put(bucket, valnp(bucket, s -> new BucketProperties(s, this, bucket, writable)));

		return Collections.unmodifiableMap(buckets);
	}

	@Nonnull
	public Bucket bucket(@Nonnull final String key)
	{
		return bucketProperties(key);
	}

	@Nonnull
	BucketProperties bucketProperties(@Nonnull final String key)
	{
		final BucketProperties result = buckets.get(requireNonNull(key, "key"));
		if(result==null)
			throw new IllegalArgumentException(
					"key must be one of " + buckets.keySet() + ", " +
					"but was >" + key + '<');
		return result;
	}

	/**
	 * Check a bucket name as specified by {@link Vault#value()}
	 * for correctness.
	 */
	public static void checkBucket(
			final String value,
			final Function<String, RuntimeException> exception)
	{
		requireNonNull(value, "bucket");
		requireNonNull(exception, "exception");
		if(value.isEmpty())
			throw exception.apply("must not be empty");

		final int pos = VAULT_CHAR_SET.indexOfNotContains(value);
		if(pos>=0)
			throw exception.apply(
					"must contain just " + VAULT_CHAR_SET + ", " +
					"but was >" + value + "< containing a forbidden character at position " + pos);
	}

	private static final CharSet VAULT_CHAR_SET = new CharSet('-', '-', '0', '9', 'A', 'Z', 'a', 'z');

	public void checkBuckets(final Model model)
	{
		final LinkedHashSet<String> fieldBuckets = new LinkedHashSet<>();
		for(final Type<?> type : model.getTypesSortedByHierarchy())
			for(final com.exedio.cope.Field<?> field : type.getFields())
				if(field instanceof DataField)
				{
					final String bucketExplicit = ((DataField)field).getAnnotatedVaultValue();
					final String bucket =
							bucketExplicit==null && isAppliedToAllFields
									? Vault.DEFAULT
									: bucketExplicit;
					if(bucket!=null)
						fieldBuckets.add(bucket);
				}

		fieldBuckets.removeAll(buckets.keySet());
		if(!fieldBuckets.isEmpty())
			throw new IllegalArgumentException(
					"@Vault for buckets " + fieldBuckets + " " +
					"not supported by ConnectProperties.");
	}

	/**
	 * @deprecated Use {@link #newServices(String[])} or {@link #newServices()} instead.
	 * @throws IllegalArgumentException if there is more than one service
	 */
	@Deprecated
	public VaultResilientService newService()
	{
		if(buckets.size()!=1)
			throw new IllegalArgumentException("is not allowed for more than one service: " + buckets.keySet());

		final Map<String, VaultResilientService> result = newServices();
		if(result.size()!=1)
			throw new RuntimeException();

		return result.values().iterator().next();
	}

	public Map<String, VaultResilientService> newServices(final String... keys)
	{
		final LinkedHashMap<String, VaultResilientService> result = new LinkedHashMap<>();
		for(final Map.Entry<String, VaultService> e : newServicesNonResilient(keys).entrySet())
		{
			result.put(e.getKey(), bucketProperties(e.getKey()).resiliate(e.getValue())); // TODO reuse e.getKey()
		}
		return Collections.unmodifiableMap(result);
	}

	/**
	 * @deprecated Use {@link #newServicesNonResilient(String...)} instead.
	 */
	@Deprecated
	public Map<String, VaultService> newServicesUnsanitized(final String... keys)
	{
		return newServicesNonResilient(keys);
	}

	public Map<String, VaultService> newServicesNonResilient(final String... keys)
	{
		return newServicesNonResilient(() -> false, keys);
	}

	/**
	 * @deprecated Use {@link #newServicesNonResilient(BooleanSupplier, String...)} instead.
	 */
	@Deprecated
	public Map<String, VaultService> newServicesUnsanitized(
			final BooleanSupplier markPut,
			final String... keys)
	{
		return newServicesNonResilient(markPut, keys);
	}

	public Map<String, VaultService> newServicesNonResilient(
			final BooleanSupplier markPut,
			final String... keys)
	{
		requireNonNull(keys, "keys");
		int keyIndex = 0;
		final HashMap<String, Integer> keysSeen = new HashMap<>();
		for(final String key : keys)
		{
			if(key==null)
				throw new NullPointerException("keys[" + keyIndex + ']');

			final Integer collision = keysSeen.put(key, keyIndex);
			if(collision!=null)
				throw new IllegalArgumentException(
						"keys[" + keyIndex + "] is a duplicate of index " + collision + ": >" + key + '<');

			if(!buckets.containsKey(key))
				throw new IllegalArgumentException(
						"keys[" + keyIndex + "] must be one of " + buckets.keySet() + ", " +
						"but was >" + key + '<');

			keyIndex++;
		}

		final LinkedHashMap<String, VaultService> result = new LinkedHashMap<>();
		for(final String key : keys)
		{
			result.put(key, buckets.get(key).service.newService(buckets.get(key), key, markPut)); // TODO reuse buckets.get(key)
		}
		return Collections.unmodifiableMap(result);
	}

	public Map<String, VaultResilientService> newServices()
	{
		return newServices(key -> () -> false);
	}

	public Map<String, VaultResilientService> newServices(final Function<String, BooleanSupplier> markPut)
	{
		final LinkedHashMap<String, VaultResilientService> result = new LinkedHashMap<>();
		for(final Map.Entry<String, BucketProperties> e : buckets.entrySet())
		{
			final String key = e.getKey();
			result.put(key, e.getValue().resiliate(e.getValue().service.newService(e.getValue(), key, markPut.apply(key)))); // TODO reuse e.getValue()
		}
		return Collections.unmodifiableMap(result);
	}



	final TrailProperties trail;

	/**
	 * @deprecated
	 * This method always returns true.
	 * Disabling vault trail is no longer supported.
	 */
	@Deprecated
	public boolean isTrailEnabled()
	{
		return true;
	}



	private final boolean isAppliedToAllFields;

	private boolean valueIsAppliedToAllFields()
	{
		return value("isAppliedToAllFields", false);
	}

	public boolean isAppliedToAllFields()
	{
		return isAppliedToAllFields;
	}



	@Deprecated
	public String probe()
	{
		final ArrayList<String> result = new ArrayList<>();
		for(final Map.Entry<String, BucketProperties> e : buckets.entrySet())
		{
			final BucketProperties bp = e.getValue();
			probe(result, bp::probeContract);
			probe(result, bp::probeBucketTag);
		}
		return result.size()==1 ? result.get(0) : result.toString();
	}

	private static void probe(
			final ArrayList<String> result,
			final Callable<?> probe)
	{
		Object probeResult = null;
		try
		{
			probeResult = probe.call();
		}
		catch(final ProbeAbortedException ignored)
		{
		}
		catch(final RuntimeException e)
		{
			throw e;
		}
		catch(final Exception e)
		{
			throw new RuntimeException(probe.toString(), e);
		}
		if(probeResult!=null)
			result.add(probeResult.toString());
	}



	public static Factory<VaultProperties> factory()
	{
		return factory(true);
	}

	public static Factory<VaultProperties> factory(final boolean writable)
	{
		return s -> new VaultProperties(s, writable);
	}

	private VaultProperties(final Source source, final boolean writable)
	{
		super(source);
		trail = valueTrail(null);
		buckets = valueBuckets(writable);
		isAppliedToAllFields = valueIsAppliedToAllFields();
	}


	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #checkBuckets(Model)} instead.
	 */
	@Deprecated
	public void checkServices(final Model model)
	{
		checkBuckets(model);
	}
}
