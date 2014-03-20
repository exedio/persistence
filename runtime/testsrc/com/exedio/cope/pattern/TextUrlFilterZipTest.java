/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.pattern.TextUrlFilterItem.fertig;
import static java.io.File.createTempFile;

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.DataLengthViolationException;
import com.exedio.cope.pattern.TextUrlFilter.Paste;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

public class TextUrlFilterZipTest extends AbstractRuntimeModelTest
{
	public TextUrlFilterZipTest()
	{
		super(TextUrlFilterTest.MODEL);
	}

	TextUrlFilterItem item;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		item = new TextUrlFilterItem();
	}

	public void testNormal() throws IOException
	{
		item.putFertigPastesFromZip(file("TextUrlFilterZipTest.zip"));
		{
			final Iterator<Paste> pastes = fertig.getPasteType().search(null, fertig.getPasteType().getThis(), true).iterator();
			{
				final Paste paste = pastes.next();
				assertEquals("ABC.png", paste.getKey());
				assertEquals("image/png", paste.getContentType());
				assertEquals("abc", string(paste.getBody()));
			}
			{
				final Paste paste = pastes.next();
				assertEquals("DEF.png", paste.getKey());
				assertEquals("image/png", paste.getContentType());
				assertEquals("def", string(paste.getBody()));
			}
			assertFalse(pastes.hasNext());
		}
		item.putFertigPastesFromZip(file("TextUrlFilterZipTest2.zip"));
		{
			final Iterator<Paste> pastes = fertig.getPasteType().search(null, fertig.getPasteType().getThis(), true).iterator();
			{
				final Paste paste = pastes.next();
				assertEquals("ABC.png", paste.getKey());
				assertEquals("image/png", paste.getContentType());
				assertEquals("abc", string(paste.getBody()));
			}
			{
				final Paste paste = pastes.next();
				assertEquals("DEF.png", paste.getKey());
				assertEquals("image/png", paste.getContentType());
				assertEquals("deF", string(paste.getBody()));
			}
			{
				final Paste paste = pastes.next();
				assertEquals("GHI.png", paste.getKey());
				assertEquals("image/png", paste.getContentType());
				assertEquals("ghi", string(paste.getBody()));
			}
			assertFalse(pastes.hasNext());
		}
	}

	@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
	public void testWrongContentType() throws IOException
	{
		final File file = file("TextUrlFilterZipTest-wrongContentType.zip");
		try
		{
			item.putFertigPastesFromZip(file);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("xxx.jpg", e.getMessage());
			final IllegalContentTypeException cause = (IllegalContentTypeException)e.getCause();
			assertEquals("illegal content type 'image/jpeg' for TextUrlFilterItem-fertig.value, allowed is 'image/png' only.", cause.getMessage());
		}
	}

	public void testUnknownContentType() throws IOException
	{
		final File file = file("TextUrlFilterZipTest-unknownContentType.zip");
		try
		{
			item.putFertigPastesFromZip(file);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("unknown content type for entry xxx.zack", e.getMessage());
		}
	}

	@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
	public void testLong() throws IOException
	{
		final File file = file("TextUrlFilterZipTest-long.zip");
		try
		{
			item.putFertigPastesFromZip(file);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("long.png", e.getMessage());
			final DataLengthViolationException cause = (DataLengthViolationException)e.getCause();
			assertEquals("length violation, 4 bytes is too long for TextUrlFilterItem-fertig.value-body", cause.getMessage());
		}
	}

	private File file(final String name) throws IOException
	{
		final byte[] b = new byte[2000];
		final InputStream stream = TextUrlFilterZipTest.class.getResourceAsStream(name);
		assertNotNull(name, stream);
		final int read;
		try
		{
			read = stream.read(b);
		}
		finally
		{
			stream.close();
		}
		assertTrue(read>0);

		final File result = deleteOnTearDown(createTempFile(TextUrlFilterZipTest.class.getName(), ""));
		try(FileOutputStream out = new FileOutputStream(result))
		{
			out.write(b, 0, read);
		}
		return result;
	}

	private static String string(final byte[] bytes)
	{
		try
		{
			return new String(bytes, "us-ascii");
		}
		catch(final UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}
}
