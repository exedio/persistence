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

import com.exedio.cope.util.JobContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import javax.annotation.Nonnull;

/**
 * Implementations of VaultService must conform to the requirements set by
 * {@link com.exedio.cope.util.Properties#valueService(String, Class, Class) Properties.valueService}.
 * <p>
 * It is highly recommended overriding {@link Object#toString() toString} with a informative message
 * containing essential configuration of the service.
 * For instance, this message is included into the result of probes returned by
 * {@link VaultProperties#probeMore()}.
 * <p>
 * All methods are guaranteed not to be called for the empty byte sequence -
 * this special case must be handled by the caller -
 * unless your instance of {@code VaultService} implements {@link VaultResilientService} as well.
 */
@SuppressWarnings("JavadocReference") // OK: protected member of public class is part of public API
public interface VaultService extends AutoCloseable
{
	/**
	 * Gives this service the chance to purge / cleanup whatever it needs to.
	 * Is called by {@link com.exedio.cope.Model#purgeSchema(JobContext)}.
	 * The default implementation does nothing.
	 */
	default void purgeSchema(@Nonnull final JobContext ctx) {}

	/**
	 * Overrides method from {@link AutoCloseable} to add empty default implementation.
	 * Also requires implementations not to declare any checked exception to be thrown.
	 */
	@Override
	default void close() {}


	long getLength(@Nonnull String hash) throws VaultNotFoundException;
	byte[] get(@Nonnull String hash) throws VaultNotFoundException;

	/**
	 * Must not
	 * {@link OutputStream#close() close} or
	 * {@link OutputStream#flush() flush} {@code sink}.
	 * <p>
	 * If this method throws a {@link VaultNotFoundException},
	 * it MUST not have modified {@code sink} in any way.
	 * In particular, it must not have written any byte to {@code sink}.
	 */
	void get(@Nonnull String hash, @Nonnull OutputStream sink) throws VaultNotFoundException, IOException;

	/**
	 * Is not called, if service instance was created with
	 * {@link VaultServiceParameters#isWritable()}==false.
	 * @return {@code true} if {@code hash} has been initially stored in the vault by this call.
	 * The result is used for statistics only.
	 * If the implementation does not have this information available, simply return {@code true}.
	 */
	boolean put(@Nonnull String hash, @Nonnull byte[] value, @Nonnull VaultPutInfo info);

	/**
	 * Is not called, if service instance was created with
	 * {@link VaultServiceParameters#isWritable()}==false.
	 * @return {@code true} if {@code hash} has been initially stored in the vault by this call.
	 * The result is used for statistics only.
	 * If the implementation does not have this information available, simply return {@code true}.
	 */
	boolean put(@Nonnull String hash, @Nonnull InputStream value, @Nonnull VaultPutInfo info) throws IOException;

	/**
	 * The caller must make sure, that {@code value} is not modified during the call.
	 * Is not called, if service instance was created with
	 * {@link VaultServiceParameters#isWritable()}==false.
	 * @return {@code true} if {@code hash} has been initially stored in the vault by this call.
	 * The result is used for statistics only.
	 * If the implementation does not have this information available, simply return {@code true}.
	 */
	boolean put(@Nonnull String hash, @Nonnull Path value, @Nonnull VaultPutInfo info) throws IOException;


	/**
	 * Default implementation aborts the probe.
	 */
	default Object probeGenuineServiceKey(@Nonnull final String bucket) throws Exception
	{
		throw newProbeAborter("not supported by " + getClass().getName());
	}

	static Exception newProbeAborter(@Nonnull final String message)
	{
		return new BucketTagNotSupported(message);
	}

	/**
	 * A string constant to be used in conjunction with {@link #probeGenuineServiceKey(String)}.
	 * Could be used as file name, table name or url segment.
	 */
	String VAULT_GENUINE_SERVICE_KEY = "VaultGenuineServiceKey";
}
