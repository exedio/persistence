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
import static java.nio.file.Files.delete;

import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.ServiceProperties;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceProperties(VaultFallbackService.Props.class)
public final class VaultFallbackService implements VaultService
{
	private final String bucket;
	private final VaultService main;
	private final VaultService[] fallbacks;
	private final boolean copyFallbackToMain;

	VaultFallbackService(
			final VaultServiceParameters parameters,
			final Props properties)
	{
		bucket = parameters.getBucket();
		main = properties.main.newService(parameters);
		fallbacks = properties.fallbacks.stream().map(s->s.newService(parameters)).toArray(VaultService[]::new);
		copyFallbackToMain = properties.copyFallbackToMain;
	}

	@Override
	public void purgeSchema(final JobContext ctx)
	{
		main.purgeSchema(ctx);
		for(final VaultService fallback : fallbacks)
			fallback.purgeSchema(ctx);
	}

	@Override
	public void close()
	{
		for (int i = fallbacks.length-1; i>=0; i--)
			fallbacks[i].close();
		main.close();
	}

	public VaultService getMainService()
	{
		return main;
	}

	/** @return the list of fallback services, with at least one element */
	public List<VaultService> getFallbackServices()
	{
		return List.of(fallbacks);
	}

	@Override
	public boolean contains(final String hash) throws VaultServiceUnsupportedOperationException
	{
		// TODO
		// Maybe I want to catch VaultServiceUnsupportedOperationException
		// and ask other fallbacks before giving up.
		// That means implementing a Three-valued_logic:
		// https://en.wikipedia.org/wiki/Three-valued_logic
		if(main.contains(hash))
			return true;

		for(final VaultService fallback : fallbacks)
			if(fallback.contains(hash))
				return true;

		return false;
	}

	@Override
	public byte[] get(final String hash) throws VaultNotFoundException
	{
		try
		{
			return main.get(hash);
		}
		catch(final VaultNotFoundException mainSuppressed)
		{
			//noinspection ReassignedVariable
			List<VaultNotFoundException> refSuppressed = null;
			for(int i = 0; i < fallbacks.length; i++)
			{
				final VaultService fallback = fallbacks[i];
				try
				{
					final byte[] result = fallback.get(hash);
					logGetFallback(i, hash);
					if(copyFallbackToMain)
						main.put(hash, result);
					return result;
				}
				catch(final VaultNotFoundException e)
				{
					if (i==fallbacks.length-1)
					{
						throw addSuppressed(e, mainSuppressed, refSuppressed);
					}
					else
					{
						if(refSuppressed == null)
							refSuppressed = new ArrayList<>(fallbacks.length);
						refSuppressed.add(e);
					}
				}
				catch(final RuntimeException e)
				{
					throw addSuppressed(e, mainSuppressed, refSuppressed);
				}
			}
			//noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
			throw new RuntimeException("must not reach");
		}
	}

	@Override
	public void get(final String hash, final OutputStream sink) throws VaultNotFoundException, IOException
	{
		try
		{
			main.get(hash, sink);
		}
		catch(final VaultNotFoundException mainSuppressed)
		{
			//noinspection ReassignedVariable
			List<VaultNotFoundException> fallbackSuppressed = null;
			for(int i = 0; i < fallbacks.length; i++)
			{
				final VaultService fallback = fallbacks[i];
				try
				{
					if(!copyFallbackToMain)
					{
						fallback.get(hash, sink);
						logGetFallback(i, hash);
						return;
					}

					final Path temp = createTempFileFromFallback(i, hash);
					main.put(hash, temp);
					Files.copy(temp, sink);
					delete(temp);
					return;
				}
				catch(final VaultNotFoundException e)
				{
					if (i==fallbacks.length-1)
					{
						throw addSuppressed(e, mainSuppressed, fallbackSuppressed);
					}
					else
					{
						if(fallbackSuppressed == null)
							fallbackSuppressed = new ArrayList<>(fallbacks.length);
						fallbackSuppressed.add(e);
					}
				}
				catch(final RuntimeException e)
				{
					throw addSuppressed(e, mainSuppressed, fallbackSuppressed);
				}
			}
			//noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
			throw new RuntimeException("must not reach");
		}
	}

	private static <T extends Exception> T addSuppressed(final T e,
														final VaultNotFoundException mainSuppressed,
														final List<VaultNotFoundException> refSuppressed)
	{
		e.addSuppressed(mainSuppressed);
		if (refSuppressed!=null)
			refSuppressed.forEach(e::addSuppressed);
		return e;
	}

	private Path createTempFileFromFallback(final int fallbackIndex, final String hash)
			throws VaultNotFoundException, IOException
	{
		final Path result = Files.createTempFile("VaultFallbackService-" + fallbackIndex + "-" + anonymiseHash(hash), ".dat");

		try(OutputStream s = Files.newOutputStream(result))
		{
			fallbacks[fallbackIndex].get(hash, s);
		}
		logGetFallback(fallbackIndex, hash);

		return result;
	}

	private void logGetFallback(final int fallbackIndex, final String hash)
	{
		if(logger.isDebugEnabled())
			logger.debug("get from fallback {} in {}: {}", fallbackIndex, bucket, anonymiseHash(hash));
	}

	private static final Logger logger = LoggerFactory.getLogger(VaultFallbackService.class);


	@Override
	public void addToAncestryPath(
			@Nonnull final String hash,
			@Nonnull final Consumer<String> sink)
	{
		final boolean isMain = contains(main, hash, () -> "main service");
		if (isMain)
		{
			sink.accept(ANCESTRY_PATH_MAIN);
			main.addToAncestryPath(hash, sink);
		}
		else
		{
			for(int i=0; i<fallbacks.length; i++)
			{
				final VaultService fallback = fallbacks[i];
				final boolean assumeRefContains;
				if (i==fallbacks.length-1)
				{
					assumeRefContains = true;
				}
				else
				{
					final int ifinal = i;
					assumeRefContains = contains(fallback, hash, () -> ("fallback service " + ifinal));
				}
				if (assumeRefContains)
				{
					sink.accept(ANCESTRY_PATH_FALLBACK+(i==0?"":i));
					fallback.addToAncestryPath(hash, sink);
					return;
				}
			}
			throw new RuntimeException("must not reach");
		}
	}

	private static boolean contains(
			@Nonnull final VaultService service,
			@Nonnull final String hash,
			@Nonnull final Supplier<String> name)
	{
		try
		{
			return service.contains(hash);
		}
		catch(final VaultServiceUnsupportedOperationException e)
		{
			throw new IllegalArgumentException(
					name.get() + " (" + service.getClass().getName() + ") does not support contains", e);
		}
	}

	public static final String ANCESTRY_PATH_MAIN = "main";
	public static final String ANCESTRY_PATH_FALLBACK = "fallback";


	@Override
	public boolean put(final String hash, final byte[] value)
	{
		return main.put(hash, value);
	}

	@Override
	public boolean put(final String hash, final InputStream value) throws IOException
	{
		return main.put(hash, value);
	}

	@Override
	public boolean put(final String hash, final Path value) throws IOException
	{
		return main.put(hash, value);
	}


	@Override
	public Object probeBucketTag(final String bucket) throws Exception
	{
		final Object result = main.probeBucketTag(bucket);
		FALLBACK(bucket, 0);
		return result;
	}
	/**
	 * This method has the sole purpose to appear in stack traces
	 * showing that any exception was caused by which of the fallback services.
	 */
	private void FALLBACK(final String bucket, int index) throws Exception
	{
		fallbacks[index++].probeBucketTag(bucket);

		if(index<fallbacks.length)
			//noinspection TailRecursion OK: recursion is the sole prupuse of this method
			FALLBACK(bucket, index);
	}


	@Override
	public String toString()
	{
		return main +
				 " (fallback" + (fallbacks.length>1 ? "s" : "") + " " +
				 Arrays.stream(fallbacks).map(Object::toString).collect(Collectors.joining(" ")) + ')';
	}


	static final class Props extends AbstractVaultProperties
	{
		private final Service main = valueService("main", true);
		private final List<Service> fallbacks = valueFallbacks();
		private final boolean copyFallbackToMain = value("copyReferenceToMain", true);

		Props(final Source source)
		{
			super(source);
			if(logger.isErrorEnabled() &&
				main.getServiceClass()==VaultFallbackService.class)
				logger.error(
						"do not nest another VaultFallbackService in main, " +
						"use multiple fallback services instead");
		}

		private List<Service> valueFallbacks()
		{
			final int count = value("referenceCount", 1, 1);
			final Service[] array = new Service[count];
			for (int i=0; i<count; i++)
			{
				final String key = "reference" + (i == 0 ? "" : i );
				array[i] = valueService(key, false);
				if(array[i].getServiceClass()==VaultFallbackService.class)
					throw newException(key,
							"must not nest another VaultFallbackService, use multiple fallback services instead");
			}
			return List.of(array);
		}
	}
}
