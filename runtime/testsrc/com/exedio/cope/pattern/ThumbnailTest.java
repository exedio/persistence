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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.pattern.MediaLocatorAssert.assertLocator;
import static com.exedio.cope.pattern.MediaType.GIF;
import static com.exedio.cope.pattern.MediaType.JPEG;
import static com.exedio.cope.pattern.MediaType.PNG;
import static com.exedio.cope.pattern.MediaType.WEBP;
import static com.exedio.cope.pattern.ThumbnailItem.TYPE;
import static com.exedio.cope.pattern.ThumbnailItem.file;
import static com.exedio.cope.pattern.ThumbnailItem.thumb;
import static com.exedio.cope.pattern.ThumbnailItem.thumbLarge;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertUnmodifiable;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class ThumbnailTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(ThumbnailTest.class, "MODEL");
	}

	public ThumbnailTest()
	{
		super(MODEL);
	}

	private ThumbnailItem jpg, png, gif, wep, txt, emp;
	private final byte[] data  = {-86,122,-8,23};

	// Ok, because Media#set(Item,InputStream,String) closes the stream.
	@BeforeEach void setUp() throws IOException
	{
		jpg = new ThumbnailItem();
		png = new ThumbnailItem();
		gif = new ThumbnailItem();
		wep = new ThumbnailItem();
		txt = new ThumbnailItem();
		emp = new ThumbnailItem();
		jpg.setFile(ThumbnailTest.class.getResourceAsStream("thumbnail-test.jpg"), JPEG);
		png.setFile(ThumbnailTest.class.getResourceAsStream("thumbnail-test.png"), PNG);
		gif.setFile(ThumbnailTest.class.getResourceAsStream("thumbnail-test.gif"), GIF);
		wep.setFile(ThumbnailTest.class.getResourceAsStream("thumbnail-test.webp"), WEBP);
		txt.setFile(data, "text/plain");
	}

	@Test void testThumbs() throws IOException
	{
		// test model
		assertEqualsUnmodifiable(asList(new Feature[]{
				TYPE.getThis(),
				file,
				file.getBody(),
				file.getContentType(),
				file.getLastModified(),
				file.getUnison(),
				thumb,
				thumbLarge,
			}), TYPE.getFeatures());
		assertEquals(TYPE, thumb.getType());
		assertEquals("thumb", thumb.getName());
		assertSame(file, thumb.getSource());
		assertEquals(20, thumb.getBoundX());
		assertEquals(30, thumb.getBoundY());
		final Set<String> sct = thumb.getSupportedSourceContentTypes();
		assertTrue(sct.contains(JPEG), sct.toString());
		assertTrue(sct.contains("image/pjpeg"), sct.toString());
		assertTrue(sct.contains(PNG),  sct.toString());
		assertTrue(sct.contains(GIF),  sct.toString());
		assertUnmodifiable(sct);

		assertEquals(file.isNull(), thumb.isNull());
		assertEquals(file.isNotNull(), thumb.isNotNull());

		assertSerializedSame(thumb, 381);

		try
		{
			new MediaThumbnail(null, 80, 80);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("source", e.getMessage());
		}
		try
		{
			new MediaThumbnail(file, 4, 80);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("boundX must be 5 or greater, but was 4", e.getMessage());
		}
		try
		{
			new MediaThumbnail(file, 80, 4);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("boundY must be 5 or greater, but was 4", e.getMessage());
		}

		// test sizing algorithm
		assertBB(40, 60, 20, 30);
		assertBB(40, 50, 20, 25);
		assertBB(30, 60, 15, 30);
		assertBB(20, 30, 20, 30);
		assertBB(10, 10, 20, 20);

		// test content type
		assertEquals(JPEG, jpg.getThumbContentType());
		assertEquals(JPEG, png.getThumbContentType());
		assertEquals(JPEG, gif.getThumbContentType());
		assertEquals(null, wep.getThumbContentType());
		assertEquals(null, txt.getThumbContentType());
		assertEquals(null, emp.getThumbContentType());

		// url
		assertLocator("ThumbnailItem/thumb/" + jpg.getCopeID() + ".jpg", jpg.getThumbLocator());
		assertLocator("ThumbnailItem/thumb/" + png.getCopeID() + ".jpg", png.getThumbLocator());
		assertLocator("ThumbnailItem/thumb/" + gif.getCopeID() + ".jpg", gif.getThumbLocator());
		assertLocator(null, wep.getThumbLocator());
		assertLocator(null, txt.getThumbLocator());
		assertLocator(null, emp.getThumbLocator());

		// url fallback
		assertEquals(jpg.getThumbLocator().getURLByConnect(), jpg.getThumbURLWithFallbackToSource());
		assertEquals(png.getThumbLocator().getURLByConnect(), png.getThumbURLWithFallbackToSource());
		assertEquals(gif.getThumbLocator().getURLByConnect(), gif.getThumbURLWithFallbackToSource());
		assertEquals(wep.getFileLocator ().getURLByConnect(), wep.getThumbURLWithFallbackToSource());
		assertEquals(txt.getFileLocator ().getURLByConnect(), txt.getThumbURLWithFallbackToSource());
		assertEquals(null, emp.getThumbURLWithFallbackToSource());

		// locator fallback
		assertEquals("ThumbnailItem/thumb/" + jpg.getCopeID() + ".jpg", jpg.getThumbLocatorWithFallbackToSource().getPath());
		assertEquals("ThumbnailItem/thumb/" + png.getCopeID() + ".jpg", png.getThumbLocatorWithFallbackToSource().getPath());
		assertEquals("ThumbnailItem/thumb/" + gif.getCopeID() + ".jpg", gif.getThumbLocatorWithFallbackToSource().getPath());
		assertEquals("ThumbnailItem/file/"  + wep.getCopeID() + ".webp",wep.getThumbLocatorWithFallbackToSource().getPath());
		assertEquals("ThumbnailItem/file/"  + txt.getCopeID() + ".txt", txt.getThumbLocatorWithFallbackToSource().getPath());
		assertEquals(null, emp.getThumbLocatorWithFallbackToSource());

		// isNull
		assertContains(emp, TYPE.search(file.isNull()));
		assertContains(jpg, png, gif, wep, txt, TYPE.search(file.isNotNull()));
		assertContains(emp , TYPE.search(thumb.isNull())); // TODO check for getSupportedSourceContentTypes, add text
		assertContains(jpg, png, gif, wep, txt, TYPE.search(thumb.isNotNull())); // TODO check for getSupportedSourceContentTypes, remove text

		// test get
		assertAndWrite(jpg.getThumb(), "thumbnail-test-jpg.jpg");
		assertAndWrite(png.getThumb(), "thumbnail-test-png.jpg");
		assertAndWrite(gif.getThumb(), "thumbnail-test-gif.jpg");
		assertNull(wep.getThumb());
		assertNull(txt.getThumb());
		assertNull(emp.getThumb());
	}

	// Ok, because Media#set(Item,InputStream,String) closes the stream.
	@Test void testThumbsLarge() throws IOException
	{
		jpg.setFile(ThumbnailTest.class.getResourceAsStream("thumbnail-antialias.jpg"), JPEG);
		assertAndWrite(jpg.getThumbLarge(), "thumbnail-antialias-jpg.jpg");

		jpg.setFile(ThumbnailTest.class.getResourceAsStream("thumbnail-antialias.png"), PNG);
		assertAndWrite(jpg.getThumbLarge(), "thumbnail-antialias-png.jpg");

		jpg.setFile(ThumbnailTest.class.getResourceAsStream("thumbnail-gif.gif"), GIF);
		assertAndWrite(jpg.getThumbLarge(), "thumbnail-gif.jpg");

		jpg.setFile(ThumbnailTest.class.getResourceAsStream("thumbnail-transparency.png"), PNG);
		assertAndWrite(jpg.getThumbLarge(), "thumbnail-transparency-png.jpg");
	}

	private static void assertBB(final int srcX, final int srcY, final int tgtX, final int tgtY)
	{
		final int[] bb = thumb.boundingBox(srcX, srcY);
		assertEquals(tgtX, bb[0], "width");
		assertEquals(tgtY, bb[1], "height");
	}

	private static void assertAndWrite(
			final byte[] actualContent,
			final String filename) throws IOException
	{
		assertNotNull(actualContent);
		final Path buildDir = Paths.get("build");
		if(!Files.isDirectory(buildDir))
			Files.createDirectory(buildDir);
		final Path dir = buildDir.resolve("ThumbnailTest");
		if(!Files.isDirectory(dir))
			Files.createDirectory(dir);
		final Path path = dir.resolve(filename);
		// writes files for human visual inspection
		try(OutputStream out = Files.newOutputStream(path))
		{
			out.write(actualContent);
		}
		assertEquals(new HashSet<>(asList(MediaType.forName(JPEG))), MediaType.forMagics(path));
	}
}
