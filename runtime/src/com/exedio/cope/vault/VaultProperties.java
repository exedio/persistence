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

import static java.util.Collections.singletonList;
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
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

public final class VaultProperties extends AbstractVaultProperties
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



	final Map<String, Service> services;

	private Map<String, Service> valueServices(final boolean writable)
	{
		final ArrayList<String> serviceKeys = new ArrayList<>();
		{
			final String KEY = "services";
			for(final StringTokenizer tn = new StringTokenizer(value(KEY, Vault.DEFAULT), " ");
				 tn.hasMoreTokens(); )
				serviceKeys.add(tn.nextToken());
			if(serviceKeys.isEmpty())
				throw newException(KEY, "must not be empty");

			for(final String s : serviceKeys)
			{
				final int pos = VAULT_CHAR_SET.indexOfNotContains(s);
				if(pos>=0)
					throw newException(KEY,
							"must contain a space separates list of services " +
							"containing just " + VAULT_CHAR_SET + ", " +
							"but service >" + s + "< contained a forbidden character at position " + pos + '.');
			}
			if( new HashSet<>(serviceKeys).size() < serviceKeys.size() )
				throw newException(KEY, "must not contain duplicates");
		}

		final LinkedHashMap<String, Service> services = new LinkedHashMap<>();
		if(singletonList(Vault.DEFAULT).equals(serviceKeys))
		{
			services.put(Vault.DEFAULT, valueService("service", writable));
		}
		else
		{
			for(final String service : serviceKeys)
				services.put(service, valueService("service." + service, writable));
		}

		return Collections.unmodifiableMap(services);
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

		fieldServices.removeAll(services.keySet());
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
	public VaultService newService()
	{
		if(services.size()!=1)
			throw new IllegalArgumentException("is not allowed for more than one service: " + services.keySet());

		final Map<String, VaultService> result = newServices();
		if(result.size()!=1)
			throw new RuntimeException();

		return result.values().iterator().next();
	}

	public Map<String, VaultService> newServices(final String... keys)
	{
		final LinkedHashMap<String, VaultService> result = new LinkedHashMap<>();
		for(final Map.Entry<String, VaultService> e : newServicesUnsanitized(keys).entrySet())
		{
			result.put(e.getKey(), sanitize(e.getValue()));
		}
		return Collections.unmodifiableMap(result);
	}

	public Map<String, VaultService> newServicesUnsanitized(final String... keys)
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

			if(!services.containsKey(key))
				throw new IllegalArgumentException(
						"keys[" + keyIndex + "] must be one of " + services.keySet() + ", " +
						"but was >" + key + '<');

			keyIndex++;
		}

		final LinkedHashMap<String, VaultService> result = new LinkedHashMap<>();
		for(final String key : keys)
		{
			result.put(key, services.get(key).newService(this, key));
		}
		return Collections.unmodifiableMap(result);
	}

	public Map<String, VaultService> newServices()
	{
		final LinkedHashMap<String, VaultService> result = new LinkedHashMap<>();
		for(final Map.Entry<String, Service> e : services.entrySet())
		{
			final String key = e.getKey();
			result.put(key, sanitize(e.getValue().newService(this, key)));
		}
		return Collections.unmodifiableMap(result);
	}

	VaultService sanitize(final VaultService service)
	{
		return new VaultSanitizedService(service, this);
	}



	private final TrailProperties trail;

	private TrailProperties valueTrail()
	{
		return value("trail", true, TrailProperties::new);
	}

	public boolean isTrailEnabled()
	{
		return trail!=null;
	}

	public int getTrailStartLimit()
	{
		return trail().startLimit;
	}

	public int getTrailFieldLimit()
	{
		return trail().fieldLimit;
	}

	public int getTrailOriginLimit()
	{
		return trail().originLimit;
	}

	private TrailProperties trail()
	{
		if(trail==null)
			throw new IllegalStateException("trail is disabled");

		return trail;
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
		for(final Callable<?> probe : probeMore())
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
		return result.size()==1 ? result.get(0) : result.toString();
	}

	@Override
	public List<? extends Callable<?>> probeMore()
	{
		final ArrayList<Callable<?>> result = new ArrayList<>();
		for(final Map.Entry<String, Service> e : services.entrySet())
		{
			result.add(new VaultProbe(this, e));
			result.add(new GenuineServiceKeyProbe(e));
		}
		return result;
	}

	final class GenuineServiceKeyProbe implements Callable<Object>
	{
		private final String key;
		private final AbstractVaultProperties.Service service;

		GenuineServiceKeyProbe(
				final Map.Entry<String, AbstractVaultProperties.Service> e)
		{
			this.key = e.getKey();
			this.service = e.getValue();
		}

		@Override
		public Object call() throws Exception
		{
			try(VaultService s = service.newService(VaultProperties.this, key))
			{
				return s.probeGenuineServiceKey(key);
			}
			catch(final GenuineServiceKeyProbeNotSupported e)
			{
				throw newProbeAbortedException(e.getMessage());
			}
		}

		@Override
		public String toString()
		{
			return key + ".genuineServiceKey";
		}
	}

	static final class GenuineServiceKeyProbeNotSupported extends Exception
	{
		GenuineServiceKeyProbeNotSupported(final String message)
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
		services = valueServices(writable);
		trail = valueTrail();
		isAppliedToAllFields = valueIsAppliedToAllFields();
	}
}
