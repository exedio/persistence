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

@ServiceProperties(VaultReferenceService.Props.class)
public final class VaultReferenceService implements VaultService
{
	private final String bucket;
	private final VaultService main;
	private final VaultService[] references;
	private final boolean copyReferenceToMain;

	VaultReferenceService(
			final VaultServiceParameters parameters,
			final Props properties)
	{
		bucket = parameters.getBucket();
		main = properties.main.newService(parameters);
		references = properties.references.stream().map(s->s.newService(parameters)).toArray(VaultService[]::new);
		copyReferenceToMain = properties.copyReferenceToMain;
	}

	@Override
	public void purgeSchema(final JobContext ctx)
	{
		main.purgeSchema(ctx);
		for(final VaultService reference : references)
			reference.purgeSchema(ctx);
	}

	@Override
	public void close()
	{
		for (int i = references.length-1; i>=0; i--)
			references[i].close();
		main.close();
	}

	public VaultService getMainService()
	{
		return main;
	}

	/**
	 * @return getReferenceServices().get(0)
	 * @throws IllegalStateException if there is more than one reference service
	 * @deprecated use {@link #getReferenceServices()}
	 */
	@Deprecated
	public VaultService getReferenceService()
	{
		if (references.length!=1)
			throw new IllegalStateException("there are " + references.length + " reference services - use getReferenceServices()");
		return references[0];
	}

	/** @return the list of reference services, with at least one element */
	public List<VaultService> getReferenceServices()
	{
		return List.of(references);
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
			for(int i = 0; i < references.length; i++)
			{
				final VaultService reference = references[i];
				try
				{
					final byte[] result = reference.get(hash);
					logGetReference(i, hash);
					if(copyReferenceToMain)
						main.put(hash, result);
					return result;
				}
				catch(final VaultNotFoundException e)
				{
					if (i==references.length-1)
					{
						throw addSuppressed(e, mainSuppressed, refSuppressed);
					}
					else
					{
						if(refSuppressed == null)
							refSuppressed = new ArrayList<>(references.length);
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
			List<VaultNotFoundException> refSuppressed = null;
			for(int i = 0; i < references.length; i++)
			{
				final VaultService reference = references[i];
				try
				{
					if(!copyReferenceToMain)
					{
						reference.get(hash, sink);
						logGetReference(i, hash);
						return;
					}

					final Path temp = createTempFileFromReference(i, hash);
					main.put(hash, temp);
					Files.copy(temp, sink);
					delete(temp);
					return;
				}
				catch(final VaultNotFoundException e)
				{
					if (i==references.length-1)
					{
						throw addSuppressed(e, mainSuppressed, refSuppressed);
					}
					else
					{
						if(refSuppressed == null)
							refSuppressed = new ArrayList<>(references.length);
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

	private static <T extends Exception> T addSuppressed(final T e,
														final VaultNotFoundException mainSuppressed,
														final List<VaultNotFoundException> refSuppressed)
	{
		e.addSuppressed(mainSuppressed);
		if (refSuppressed!=null)
			refSuppressed.forEach(e::addSuppressed);
		return e;
	}

	private Path createTempFileFromReference(final int referenceIndex, final String hash)
			throws VaultNotFoundException, IOException
	{
		final Path result = Files.createTempFile("VaultReferenceService-" + referenceIndex + "-" + anonymiseHash(hash), ".dat");

		try(OutputStream s = Files.newOutputStream(result))
		{
			references[referenceIndex].get(hash, s);
		}
		logGetReference(referenceIndex, hash);

		return result;
	}

	private void logGetReference(final int referenceIndex, final String hash)
	{
		if(logger.isDebugEnabled())
			logger.debug("get from reference {} in {}: {}", referenceIndex, bucket, anonymiseHash(hash));
	}

	private static final Logger logger = LoggerFactory.getLogger(VaultReferenceService.class);


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
			for(int i=0; i<references.length; i++)
			{
				final VaultService reference = references[i];
				final boolean assumeRefContains;
				if (i==references.length-1)
				{
					assumeRefContains = true;
				}
				else
				{
					final int ifinal = i;
					assumeRefContains = contains(reference, hash, () -> ("reference service " + ifinal));
				}
				if (assumeRefContains)
				{
					sink.accept(ANCESTRY_PATH_REFERENCE+(i==0?"":i));
					reference.addToAncestryPath(hash, sink);
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
	public static final String ANCESTRY_PATH_REFERENCE = "reference";


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
		REFERENCE(bucket);
		return result;
	}
	/**
	 * This method has the sole purpose to appear in stack traces
	 * showing that any exception was caused by one of the reference services.
	 */
	private void REFERENCE(final String bucket) throws Exception
	{
		for(final VaultService reference : references)
		{
			reference.probeBucketTag(bucket);
		}
	}


	@Override
	public String toString()
	{
		return main +
				 " (reference" + (references.length>1 ? "s" : "") + " " +
				 Arrays.stream(references).map(Object::toString).collect(Collectors.joining(" ")) + ')';
	}


	static final class Props extends AbstractVaultProperties
	{
		private final Service main = valueService("main", true);
		private final List<Service> references = valueReferences();
		private final boolean copyReferenceToMain = value("copyReferenceToMain", true);

		Props(final Source source)
		{
			super(source);
			if(logger.isErrorEnabled() &&
				main.getServiceClass()==VaultReferenceService.class)
				logger.error(
						"do not nest another VaultReferenceService in main, " +
						"use multiple reference services instead");
		}

		private List<Service> valueReferences()
		{
			final int referenceCount = value("referenceCount", 1, 1);
			final Service[] referenceArray = new Service[referenceCount];
			for (int i=0; i<referenceCount; i++)
			{
				final String key = "reference" + (i == 0 ? "" : i );
				referenceArray[i] = valueService(key, false);
				if(referenceArray[i].getServiceClass()==VaultReferenceService.class)
					throw newException(key,
							"must not nest another VaultReferenceService, use multiple reference services instead");
			}
			return List.of(referenceArray);
		}
	}
}
