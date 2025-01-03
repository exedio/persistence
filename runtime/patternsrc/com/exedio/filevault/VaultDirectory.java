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

package com.exedio.filevault;

import static com.exedio.cope.vault.VaultNotFoundException.anonymiseHash;

import com.exedio.cope.vault.VaultServiceParameters;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

abstract class VaultDirectory
{
	static final VaultDirectory instance(
			final Properties properties,
			final VaultServiceParameters parameters)
	{
		if(properties==null)
			return FLAT;

		properties.check(parameters);
		return new PropertiesImpl(properties);
	}

	final String path(final String hash)
	{
		// mitigate Directory traversal attack
		// https://en.wikipedia.org/wiki/Directory_traversal_attack
		checkHash(hash, '.');
		checkHash(hash, '/');
		checkHash(hash, '\\');

		return pathSanitized(hash);
	}

	private static void checkHash(final String hash, final char ch)
	{
		final int pos = hash.indexOf(ch);
		if(pos>=0)
			throw new IllegalArgumentException(
					"illegal character >" + ch + "< at position " + pos + " " +
					"is likely a directory traversal attack " +
					"in >" + anonymiseHash(hash) + '<');
	}

	abstract String pathSanitized(String hash);

	// TODO change result type to List<String> to support more than one level in the future
	abstract String directoryToBeCreated(String hash);


	private static final VaultDirectory FLAT = new VaultDirectory()
	{
		@Override
		String pathSanitized(final String hash)
		{
			return hash;
		}
		@Override
		String directoryToBeCreated(final String hash)
		{
			return null;
		}
		@Override
		public String toString()
		{
			return "flat";
		}
	};

	private static final class PropertiesImpl extends VaultDirectory
	{
		private final int length;
		private final boolean premised;

		PropertiesImpl(final Properties properties)
		{
			this.length = properties.length;
			this.premised = properties.premised;
		}
		@Override
		String pathSanitized(final String hash)
		{
			return
					hash.substring(0, length) + '/' +
					hash.substring(length);
		}
		@Override
		String directoryToBeCreated(final String hash)
		{
			if(premised)
				return null;

			return hash.substring(0, length);
		}
		@Override
		public String toString()
		{
			return "l=" + length + (premised?" premised":"");
		}
	}


	static class Properties extends PosixProperties
	{
		/**
		 * This field is similar to directive {@code CacheDirLength} of Apache mod_cache_disk,
		 * however a value of {@code 2} in mod_cache_disk is equivalent to a value of {@code 3} here,
		 * as mod_cache_disk uses Base64 for encoding hashes and we use hexadecimal representation.
		 * <p>
		 * See <a href="https://httpd.apache.org/docs/2.4/mod/mod_cache_disk.html#cachedirlength">CacheDirLength Directive</a>
		 */
		final int length = value("length", 3, 1);

		// TODO implement levels equivalent to CacheDirLevels, default to 1

		void check(final VaultServiceParameters parameters)
		{
			final int algorithmLength = parameters.getMessageDigestLengthHex();
			if(length>=algorithmLength)
				throw new IllegalArgumentException(
						"directory.length must be less the length of algorithm " + parameters.getMessageDigestAlgorithm() + ", " +
						"but was " + length + ">=" + algorithmLength);
		}

		/**
		 * Specify, whether directories are created as needed on put operation.
		 * This is the default.
		 * May be set to {@code true} if all directories do exist already.
		 */
		final boolean premised;

		Properties(final Source source, final boolean writable)
		{
			super(source);
			//noinspection SimplifiableConditionalExpression
			premised = writable ? value("premised", false) : false;
		}

		Iterator<String> iterator()
		{
			return new DirIter(length);
		}
	}

	private static final class DirIter implements Iterator<String>
	{
		private char[] c;

		DirIter(final int length)
		{
			c = new char[length];
			Arrays.fill(c, '0');
		}

		@Override
		public boolean hasNext()
		{
			return c!=null;
		}

		@Override
		public String next()
		{
			if(c==null)
				throw new NoSuchElementException();

			final String result = new String(c);

			inc_loop: for(int i = c.length-1; i>=0; i--)
			{
				switch(c[i])
				{
					//noinspection DefaultNotLastCaseInSwitch
					default : c[i]++;     break inc_loop;
					case '9': c[i] = 'a'; break inc_loop;
					case 'f':
						if(i>0)
						{
							c[i] = '0';
							break;
						}
						else
						{
							c = null;
							break inc_loop;
						}
				}
			}

			return result;
		}
	}
}
