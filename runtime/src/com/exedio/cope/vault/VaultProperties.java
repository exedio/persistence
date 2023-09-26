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
import com.exedio.cope.util.Properties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public final class VaultProperties extends Properties
{
	private final MessageDigestFactory algorithm = valueMessageDigest("algorithm", "SHA-512");

	public MessageDigestFactory getAlgorithmFactory()
	{
		return algorithm;
	}

	public String getAlgorithm()
	{
		return algorithm.getAlgorithm();
	}

	public int getAlgorithmLength()
	{
		return algorithm.getLengthHex();
	}

	public String getAlgorithmDigestForEmptyByteSequence()
	{
		return algorithm.getDigestForEmptyByteSequenceHex();
	}



	final Map<String, BucketProperties> buckets;

	private Map<String, BucketProperties> valueBuckets(final boolean writable)
	{
		final ArrayList<String> bucketList = new ArrayList<>();
		{
			final String KEY = "buckets";
			for(final StringTokenizer tn = new StringTokenizer(value(KEY, Vault.DEFAULT), " ");
				 tn.hasMoreTokens(); )
				bucketList.add(tn.nextToken());
			if(bucketList.isEmpty())
				throw newException(KEY, "must not be empty");

			for(final String bucket : bucketList)
			{
				final int pos = VAULT_CHAR_SET.indexOfNotContains(bucket);
				if(pos>=0)
					throw newException(KEY,
							"must contain a space separates list of buckets " +
							"containing just " + VAULT_CHAR_SET + ", " +
							"but bucket >" + bucket + "< contained a forbidden character at position " + pos + '.');
			}
			if( new HashSet<>(bucketList).size() < bucketList.size() )
				throw newException(KEY, "must not contain duplicates");
		}

		final LinkedHashMap<String, BucketProperties> buckets = new LinkedHashMap<>();
		for(final String bucket : bucketList)
			buckets.put(bucket, valnp(bucket, s -> new BucketProperties(s, this, bucket, writable)));

		return Collections.unmodifiableMap(buckets);
	}

	/**
	 * TODO redundant to {@link DataField#VAULT_CHAR_SET}.
	 */
	@SuppressWarnings("JavadocReference")
	static final CharSet VAULT_CHAR_SET = new CharSet('-', '-', '0', '9', 'A', 'Z', 'a', 'z');

	public void checkServices(final Model model)
	{
		final LinkedHashSet<String> fieldServices = new LinkedHashSet<>();
		for(final Type<?> type : model.getTypesSortedByHierarchy())
			for(final com.exedio.cope.Field<?> field : type.getFields())
				if(field instanceof DataField)
				{
					final String fieldServiceExplicit = ((DataField)field).getAnnotatedVaultValue();
					final String fieldService =
							fieldServiceExplicit==null && isAppliedToAllFields
									? Vault.DEFAULT
									: fieldServiceExplicit;
					if(fieldService!=null)
						fieldServices.add(fieldService);
				}

		fieldServices.removeAll(buckets.keySet());
		if(!fieldServices.isEmpty())
			throw new IllegalArgumentException(
					"@Vault for " + fieldServices + " " +
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
			result.put(e.getKey(), resiliate(e.getValue()));
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
			result.put(key, buckets.get(key).service.newService(this, key, markPut));
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
			result.put(key, resiliate(e.getValue().service.newService(this, key, markPut.apply(key))));
		}
		return Collections.unmodifiableMap(result);
	}

	VaultResilientService resiliate(final VaultService service)
	{
		return new VaultResilientServiceProxy(service, this);
	}



	private final TrailProperties trail;

	private TrailProperties valueTrail()
	{
		return valnp("trail", TrailProperties::new);
	}

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

	public int getTrailStartLimit()
	{
		return trail.startLimit;
	}

	public int getTrailFieldLimit()
	{
		return trail.fieldLimit;
	}

	public int getTrailOriginLimit()
	{
		return trail.originLimit;
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

	static final class BucketTagNotSupported extends Exception
	{
		BucketTagNotSupported(final String message)
		{
			super(message);
		}

		private static final long serialVersionUID = 1l;
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
		buckets = valueBuckets(writable);
		trail = valueTrail();
		isAppliedToAllFields = valueIsAppliedToAllFields();
	}
}
