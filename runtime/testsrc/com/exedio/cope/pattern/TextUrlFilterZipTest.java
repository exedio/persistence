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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.TextUrlFilterItem.fertig;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.DataLengthViolationException;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.TextUrlFilter.Paste;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.MyTemporaryFolder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class TextUrlFilterZipTest extends TestWithEnvironment
{
	public TextUrlFilterZipTest()
	{
		super(TextUrlFilterTest.MODEL);
	}

	private final MyTemporaryFolder files = new MyTemporaryFolder();

	TextUrlFilterItem item;

	@BeforeEach final void setUp()
	{
		item = new TextUrlFilterItem();
	}

	@Test void testNormal() throws IOException
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

	@Test void testWrongContentType() throws IOException
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
			assertEquals("illegal content type 'image/jpeg' for TextUrlFilterItem-fertig.value, allowed is 'image/png' only", cause.getMessage());
		}
	}

	@Test void testUnknownContentType() throws IOException
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

	@Test void testLong() throws IOException
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
		final int read;
		try(InputStream stream = TextUrlFilterZipTest.class.getResourceAsStream(name))
		{
			assertNotNull(stream, name);
			read = stream.read(b);
		}
		assertTrue(read>0);

		final File result = files.newFile();
		try(FileOutputStream out = new FileOutputStream(result))
		{
			out.write(b, 0, read);
		}
		return result;
	}

	private static String string(final byte[] bytes)
	{
		return new String(bytes, US_ASCII);
	}
}
