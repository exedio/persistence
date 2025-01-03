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
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceProperties(VaultReferenceService.Props.class)
public final class VaultReferenceService implements VaultService
{
	private final String bucket;
	private final VaultService main, reference;
	private final boolean copyReferenceToMain;

	VaultReferenceService(
			final VaultServiceParameters parameters,
			final Props properties)
	{
		bucket = parameters.getBucket();
		main = properties.main.newService(parameters);
		reference = properties.reference.newService(parameters);
		copyReferenceToMain = properties.copyReferenceToMain;
	}

	@Override
	public void purgeSchema(final JobContext ctx)
	{
		main.purgeSchema(ctx);
		reference.purgeSchema(ctx);
	}

	@Override
	public void close()
	{
		reference.close();
		main.close();
	}

	public VaultService getMainService()
	{
		return main;
	}

	public VaultService getReferenceService()
	{
		return reference;
	}


	@Override
	public byte[] get(final String hash) throws VaultNotFoundException
	{
		try
		{
			return main.get(hash);
		}
		catch(final VaultNotFoundException suppressed)
		{
			try
			{
				final byte[] result = reference.get(hash);
				logGetReference(hash);
				if(copyReferenceToMain)
					main.put(hash, result);
				return result;
			}
			catch(final Exception e)
			{
				e.addSuppressed(suppressed);
				throw e;
			}
		}
	}

	@Override
	public void get(final String hash, final OutputStream sink) throws VaultNotFoundException, IOException
	{
		try
		{
			main.get(hash, sink);
		}
		catch(final VaultNotFoundException suppressed)
		{
			try
			{
				if(!copyReferenceToMain)
				{
					reference.get(hash, sink);
					logGetReference(hash);
					return;
				}

				final Path temp = createTempFileFromReference(hash);
				main.put(hash, temp);
				Files.copy(temp, sink);
				delete(temp);
			}
			catch(final Exception e)
			{
				e.addSuppressed(suppressed);
				throw e;
			}
		}
	}

	private Path createTempFileFromReference(final String hash)
			throws VaultNotFoundException, IOException
	{
		final Path result = Files.createTempFile("VaultReferenceService-" + anonymiseHash(hash), ".dat");

		try(OutputStream s = Files.newOutputStream(result))
		{
			reference.get(hash, s);
		}
		logGetReference(hash);

		return result;
	}

	private void logGetReference(final String hash)
	{
		if(logger.isDebugEnabled())
			logger.debug("get from reference in {}: {}", bucket, anonymiseHash(hash));
	}

	private static final Logger logger = LoggerFactory.getLogger(VaultReferenceService.class);


	@Override
	public void addToAncestryPath(
			@Nonnull final String hash,
			@Nonnull final Consumer<String> sink)
	{
		if(!(main instanceof VaultServiceContains))
			throw new IllegalArgumentException(
					"main service " + main.getClass().getName() + " does not support VaultServiceContains");

		final boolean isMain = ((VaultServiceContains)main).contains(hash);
		sink.accept(isMain ? ANCESTRY_PATH_MAIN : ANCESTRY_PATH_REFERENCE);
		(isMain?main:reference).addToAncestryPath(hash, sink);
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
	 * showing that any exception was caused by the reference service.
	 */
	private void REFERENCE(final String bucket) throws Exception
	{
		reference.probeBucketTag(bucket);
	}


	@Override
	public String toString()
	{
		return main + " (reference " + reference + ')';
	}


	static final class Props extends AbstractVaultProperties
	{
		private final Service main = valueService("main", true);
		private final Service reference = valueService("reference", false);
		private final boolean copyReferenceToMain = value("copyReferenceToMain", true);

		Props(final Source source)
		{
			super(source);
			if(reference.getServiceClass()==VaultReferenceService.class)
				throw newException("reference",
						"must not nest another VaultReferenceService, nest into main instead");
		}
	}
}
