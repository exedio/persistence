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

@ServiceProperties(VaultReferenceService.Props.class)
public final class VaultReferenceService implements VaultService
{
	private final VaultService main, reference;

	VaultReferenceService(
			final VaultServiceParameters parameters,
			final Props properties)
	{
		main = properties.main.newService(parameters.getVaultProperties());
		reference = properties.reference.newService(parameters.getVaultProperties());
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
	public long getLength(final String hash) throws VaultNotFoundException
	{
		try
		{
			return main.getLength(hash);
		}
		catch(final VaultNotFoundException ignored)
		{
			try
			{
				final Path tmp = createTempFileFromReference(hash);
				final long result = Files.size(tmp);
				main.put(hash, tmp, PUT_INFO);
				delete(tmp);
				return result;
			}
			catch(final IOException er)
			{
				throw new RuntimeException(er);
			}
		}
	}

	@Override
	public byte[] get(final String hash) throws VaultNotFoundException
	{
		try
		{
			return main.get(hash);
		}
		catch(final VaultNotFoundException ignored)
		{
			final byte[] result = reference.get(hash);
			main.put(hash, result, PUT_INFO);
			return result;
		}
	}

	@Override
	public void get(final String hash, final OutputStream sink) throws VaultNotFoundException, IOException
	{
		try
		{
			main.get(hash, sink);
		}
		catch(final VaultNotFoundException ignored)
		{
			final Path temp = createTempFileFromReference(hash);
			main.put(hash, temp, PUT_INFO);
			try(InputStream in = Files.newInputStream(temp))
			{
				final byte[] b = new byte[50*1024];
				for(int len = in.read(b); len>=0; len = in.read(b))
				{
					sink.write(b, 0, len);
				}
			}
			delete(temp);
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

		return result;
	}

	private static final VaultPutInfo PUT_INFO = new VaultPutInfoString(VaultReferenceService.class.getName());


	@Override
	public boolean put(final String hash, final byte[] value, final VaultPutInfo info)
	{
		return main.put(hash, value, info);
	}

	@Override
	public boolean put(final String hash, final InputStream value, final VaultPutInfo info) throws IOException
	{
		return main.put(hash, value, info);
	}

	@Override
	public boolean put(final String hash, final Path value, final VaultPutInfo info) throws IOException
	{
		return main.put(hash, value, info);
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

		Props(final Source source)
		{
			super(source);
			if(reference.getServiceClass()==VaultReferenceService.class)
				throw newException("reference",
						"must not nest another VaultReferenceService, nest into main instead");
		}
	}
}
