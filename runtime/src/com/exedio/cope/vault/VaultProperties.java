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

import com.exedio.cope.DataField;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.Vault;
import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.MessageDigestFactory;
import java.util.ArrayList;
import java.util.Collections;
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



	final Map<String, Service> services = valueServices();

	private Map<String, Service> valueServices()
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
			services.put(Vault.DEFAULT, valueService("service", true));
		}
		else
		{
			for(final String service : serviceKeys)
				services.put(service, valueService("service." + service, true));
		}

		return Collections.unmodifiableMap(services);
	}

	/**
	 * TODO redundant to {@link DataField#VAULT_CHAR_SET}.
	 */
	@SuppressWarnings("JavadocReference")
	private static final CharSet VAULT_CHAR_SET = new CharSet('-', '-', '0', '9', 'A', 'Z', 'a', 'z');

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
	 * @deprecated Use {@link #newServices()} instead.
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

	public Map<String, VaultService> newServices()
	{
		final LinkedHashMap<String, VaultService> result = new LinkedHashMap<>();
		for(final Map.Entry<String, Service> e : services.entrySet())
		{
			result.put(e.getKey(), e.getValue().newService(this, e.getKey()));
		}
		return Collections.unmodifiableMap(result);
	}



	private final boolean isAppliedToAllFields = value("isAppliedToAllFields", false);

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
			try
			{
				result.add(probe.call().toString());
			}
			catch(final RuntimeException e)
			{
				throw e;
			}
			catch(final Exception e)
			{
				throw new RuntimeException(probe.toString(), e);
			}
		}
		return result.size()==1 ? result.get(0) : result.toString();
	}

	@Override
	public List<? extends Callable<?>> probeMore()
	{
		final ArrayList<Callable<String>> result = new ArrayList<>();
		for(final Map.Entry<String, Service> e : services.entrySet())
			result.add(new VaultProbe(this, e));
		return result;
	}



	public static Factory<VaultProperties> factory()
	{
		return VaultProperties::new;
	}

	private VaultProperties(final Source source)
	{
		super(source);
	}
}
