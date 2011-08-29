/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.MediaType.forMagic;
import static com.exedio.cope.pattern.MediaType.forMagics;
import static com.exedio.cope.pattern.MediaType.forName;
import static com.exedio.cope.pattern.MediaType.forNameAndAliases;
import static com.exedio.cope.util.Hex.decodeLower;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.StrictFile;

public class MediaTypeTest extends CopeAssert
{
	private static final String JPEG = "ffd8ff";
	private static final String PNG = "89504e470d0a1a0a";
	private static final String ZIP = "504b0304";

	public void testForName()
	{
		final MediaType jpg = forName("image/jpeg");
		final MediaType png = forName("image/png");
		final MediaType js  = forName("application/javascript");

		assertNotNull(jpg);
		assertNotNull(png);
		assertSame(jpg, forName("image/jpeg"));
		assertSame(png, forName("image/png"));
		assertSame(js,  forName("application/javascript"));
		assertSame(null, forName("image/pjpeg"));
		assertSame(null, forName("text/javascript"));
		assertSame(null, forName("zack"));

		try
		{
			forName(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("name", e.getMessage());
		}

		assertEquals("image/jpeg", jpg.getName());
		assertEquals("image/png", png.getName());

		assertEqualsUnmodifiable(list("image/pjpeg"), jpg.getAliases());
		assertEqualsUnmodifiable(list(), png.getAliases());

		assertEquals("image/jpeg", jpg.toString());
		assertEquals("image/png", png.toString());
	}

	public void testForNameAlias()
	{
		final MediaType jpg = forName("image/jpeg");
		final MediaType png = forName("image/png");
		final MediaType js  = forName("application/javascript");

		assertSame(jpg, forNameAndAliases("image/jpeg"));
		assertSame(png, forNameAndAliases("image/png"));
		assertSame(jpg, forNameAndAliases("image/pjpeg"));
		assertSame(js,  forNameAndAliases("application/javascript"));
		assertSame(js,  forNameAndAliases("text/javascript"));
		assertSame(null, forNameAndAliases("zack"));

		try
		{
			forNameAndAliases(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("name", e.getMessage());
		}
	}

	public void testForMagic() throws IOException
	{
		assertEquals(8, MediaType.magicMaxLength());

		final MediaType jpg = forName("image/jpeg");
		final MediaType png = forName("image/png");
		final MediaType zip = forName("application/zip");
		final MediaType js  = forName("application/java-archive");

		assertMagic(JPEG,        jpg);
		assertMagic(JPEG + "aa", jpg);
		assertMagic(PNG,         png);
		assertMagic(PNG  + "bb", png);
		assertMagic(ZIP,         zip, js);
		assertMagic(ZIP  + "cc", zip, js);
		assertMagic(stealTail(JPEG));
		assertMagic(stealTail(PNG));
		assertMagic(stealTail(ZIP));

	}

	public void testForMagicFails() throws IOException
	{
		// byte
		try
		{
			forMagics((byte[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("magic", e.getMessage());
		}
		try
		{
			forMagics(new byte[0]);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("empty", e.getMessage());
		}
		// file
		try
		{
			forMagics((File)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("file", e.getMessage());
		}
		try
		{
			forMagics(file(new byte[]{}));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("empty", e.getMessage());
		}

		final File file = File.createTempFile("MediaTypeTest-", ".dat");
		StrictFile.delete(file);
		try
		{
			forMagics(file);
			fail();
		}
		catch(final FileNotFoundException e)
		{
			// ok
		}

		file.mkdir();
		try
		{
			forMagics(file);
			fail();
		}
		catch(final FileNotFoundException e)
		{
			// ok
		}
	}

	private static String stealTail(final String s)
	{
		return s.substring(0, s.length()-2);
	}

	private static void assertMagic(final String magic, final MediaType... types) throws IOException
	{
		final byte[] magicBytes = decodeLower(magic);
		assertSame(forMagic(magicBytes), types.length>0 ? types[0] : null);
		assertSame(forMagic(file(magicBytes)), types.length>0 ? types[0] : null);
		assertEqualsUnmodifiable(set(types), forMagics(magicBytes));
		assertEqualsUnmodifiable(set(types), forMagics(file(magicBytes)));
	}

	private static final Set<Object> set(final Object... o)
	{
		return new LinkedHashSet<Object>(Arrays.asList(o));
	}

	private static final void assertEqualsUnmodifiable(final Set<?> expected, final Collection<?> actual)
	{
		assertUnmodifiable(actual);
		assertEquals(expected, actual);
	}

	private static File file(final byte[] bytes) throws IOException
	{
		final File result = File.createTempFile("MediaTypeTest-", ".dat");
		final FileOutputStream stream = new FileOutputStream(result);
		try
		{
			stream.write(bytes);
		}
		finally
		{
			stream.close();
		}
		return result;
	}
}
