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

import static com.exedio.cope.pattern.MediaType.forFileName;
import static com.exedio.cope.pattern.MediaType.forMagics;
import static com.exedio.cope.pattern.MediaType.forName;
import static com.exedio.cope.pattern.MediaType.forNameAndAliases;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.util.Hex.decodeLower;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.MyTemporaryFolder;
import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class MediaTypeTest
{
	private static final String JPEG = "ffd8ff";
	private static final String PNG = "89504e470d0a1a0a";
	private static final String ZIP = "504b0304";

	private final MyTemporaryFolder files = new MyTemporaryFolder();

	@Test void testForFileName()
	{
		final MediaType jpg = forName("image/jpeg");
		final MediaType png = forName("image/png");
		final MediaType js  = forName("application/javascript");

		assertEqualsUnmodifiable(list(".jpg",".jpeg"), jpg.getExtensions());
		assertEqualsUnmodifiable(list(".png")        , png.getExtensions());
		assertEqualsUnmodifiable(list(".js")         , js .getExtensions());

		assertEquals(".jpg", jpg.getDefaultExtension());
		assertEquals(".png", png.getDefaultExtension());
		assertEquals(".js" , js .getDefaultExtension());

		assertSame(jpg, forFileName("eins.jpg"));
		assertSame(jpg, forFileName("vier.jpeg"));
		assertSame(png, forFileName("zwei.png"));
		assertSame(js,  forFileName("drei.js"));

		assertSame(null, forFileName("drei.zack"));
		assertSame(jpg, forFileName("e.jpg"));
		assertSame(null, forFileName(".jpg")); // this is ok, its not a extension
		assertSame(null, forFileName("jpg"));
		assertSame(null, forFileName("g"));
		assertSame(null, forFileName(""));
		assertSame(null, forFileName("."));
	}

	@Test void testForName()
	{
		final MediaType jpg = forName("image/jpeg");
		final MediaType png = forName("image/png");
		final MediaType gif = forName("image/gif");
		final MediaType js  = forName("application/javascript");

		assertNotNull(jpg);
		assertNotNull(png);
		assertSame(jpg, forName("image/jpeg"));
		assertSame(png, forName("image/png"));
		assertSame(js,  forName("application/javascript"));
		assertSame(null, forName("image/pjpeg"));
		assertSame(null, forName("text/javascript"));
		assertSame(null, forName("zack"));

		assertFails(
				() -> forName(null),
				NullPointerException.class, "name");

		assertTrue(jpg.hasMagic());
		assertFalse(js.hasMagic());

		assertEquals("image/jpeg", jpg.getName());
		assertEquals("image/png", png.getName());

		assertEqualsUnmodifiable(list("image/pjpeg"), jpg.getAliases());
		assertEqualsUnmodifiable(list("image/x-png"), png.getAliases());
		assertEqualsUnmodifiable(list(), gif.getAliases());

		assertEquals("image/jpeg", jpg.toString());
		assertEquals("image/png", png.toString());
	}

	@Test void testForNameAlias()
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

		assertFails(
				() -> forNameAndAliases(null),
				NullPointerException.class, "name");
	}

	@Test void testForMagic() throws IOException
	{
		assertEquals(8, MediaType.magicMaxLength());

		final MediaType jpg = forName("image/jpeg");
		final MediaType png = forName("image/png");
		final MediaType zip = forName("application/zip");
		final MediaType jar = forName("application/java-archive");
		final MediaType docx = forName("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		final MediaType xslx = forName("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

		assertMagic(JPEG,        jpg);
		assertMagic(JPEG + "aa", jpg);
		assertMagic(PNG,         png);
		assertMagic(PNG  + "bb", png);
		assertMagic(ZIP,         zip, jar, docx, xslx);
		assertMagic(ZIP  + "cc", zip, jar, docx, xslx);
		assertMagic(stealTail(JPEG));
		assertMagic(stealTail(PNG));
		assertMagic(stealTail(ZIP));
	}

	@Test void testForMagicFails() throws IOException
	{
		// byte
		assertFails(
				() -> forMagics((byte[])null),
				NullPointerException.class, "magic");
		assertFails(
				() -> forMagics(new byte[0]),
				IllegalArgumentException.class, "empty");
		// path
		assertFails(
				() -> forMagics((Path)null),
				NullPointerException.class, "path");
		assertFails(
				() -> forMagics(files.newPath(new byte[]{})),
				IllegalArgumentException.class, "empty");
		final Path path = files.newPathNotExists();
		assertThrows(
				NoSuchFileException.class,
				() -> forMagics(path));
		// file
		assertFails(
				() -> forMagics((File)null),
				NullPointerException.class, "file");
		assertFails(
				() -> forMagics(files.newFile(new byte[]{})),
				IllegalArgumentException.class, "empty");

		final File file = files.newFileNotExists();
		assertThrows(
				NoSuchFileException.class,
				() -> forMagics(file));
	}

	private static String stealTail(final String s)
	{
		return s.substring(0, s.length()-2);
	}

	private void assertMagic(final String magic, final MediaType... types) throws IOException
	{
		final byte[] magicBytes = decodeLower(magic);
		assertEqualsUnmodifiable(set(types), forMagics(magicBytes));
		assertEqualsUnmodifiable(set(types), forMagics(files.newFile(magicBytes)));
		assertEqualsUnmodifiable(set(types), forMagics(files.newPath(magicBytes)));
	}

	private static Set<Object> set(final MediaType... o)
	{
		return new LinkedHashSet<>(Arrays.asList(o));
	}
}
