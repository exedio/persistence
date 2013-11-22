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

import static com.exedio.cope.pattern.MediaType.GIF;
import static com.exedio.cope.pattern.MediaType.JPEG;
import static com.exedio.cope.pattern.MediaType.PNG;
import static com.exedio.cope.pattern.ThumbnailMagickItem.TYPE;
import static com.exedio.cope.pattern.ThumbnailMagickItem.file;
import static com.exedio.cope.pattern.ThumbnailMagickItem.thumb;
import static com.exedio.cope.pattern.ThumbnailMagickItem.thumbFull;
import static com.exedio.cope.pattern.ThumbnailMagickItem.thumbSame;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.junit.CopeTest;
import com.exedio.cope.pattern.MediaPath.NotFound;
import com.exedio.cope.util.Properties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import javax.servlet.ServletOutputStream;

public final class ThumbnailMagickTest extends CopeTest
{
	static final Model MODEL = ThumbnailMagickModelTest.MODEL;

	public ThumbnailMagickTest()
	{
		super(MODEL);
	}

	private ThumbnailMagickItem jpg, jpgX, png, pngX, gif, txt, emp;
	private final byte[] data  = {-86,122,-8,23};

	// Ok, because Media#set(Item,InputStream,String) closes the stream.
	@SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		jpg = deleteOnTearDown(new ThumbnailMagickItem());
		jpgX= deleteOnTearDown(new ThumbnailMagickItem());
		png = deleteOnTearDown(new ThumbnailMagickItem());
		pngX= deleteOnTearDown(new ThumbnailMagickItem());
		gif = deleteOnTearDown(new ThumbnailMagickItem());
		txt = deleteOnTearDown(new ThumbnailMagickItem());
		emp = deleteOnTearDown(new ThumbnailMagickItem());
		jpg.setFile(resource("thumbnail-test.jpg"), JPEG);
		png.setFile(resource("thumbnail-test.png"), PNG);
		gif.setFile(resource("thumbnail-test.gif"), GIF);
		jpgX.setFile(resource("thumbnail-test.jpg"), "image/pjpeg");
		pngX.setFile(resource("thumbnail-test.png"), "image/x-png");
		txt.setFile(data, "text/plain");
	}

	@Override
	public ConnectProperties getConnectProperties()
	{
		return new ConnectProperties(new Properties.Source()
		{
			@Override
			public Collection<String> keySet()
			{
				return null;
			}

			@Override
			public String getDescription()
			{
				return getClass().toString();
			}

			@Override
			public String get(final String key)
			{
				if("connection.url".equals(key))
					return "jdbc:hsqldb:mem:copetest";
				else if("connection.username".equals(key))
					return "sa";
				else if("connection.password".equals(key))
					return "";
				else
					return null;
			}
		}, null);
	}

	private static InputStream resource(final String name)
	{
		return ThumbnailMagickTest.class.getResourceAsStream(name);
	}

	public void testThumbs() throws IOException, NotFound
	{
		// if not enabled, subsequent tests will fail as well
		assertTrue(MediaImageMagickFilter.isEnabled());

		// content type
		assertEquals(JPEG, jpg.getThumbContentType());
		assertEquals(JPEG, png.getThumbContentType());
		assertEquals(JPEG, gif.getThumbContentType());
		assertEquals(JPEG, jpgX.getThumbContentType());
		assertEquals(JPEG, pngX.getThumbContentType());
		assertEquals(null, txt.getThumbContentType());
		assertEquals(null, emp.getThumbContentType());

		assertEquals(PNG, jpg.getThumbFullContentType());
		assertEquals(PNG, png.getThumbFullContentType());
		assertEquals(PNG, gif.getThumbFullContentType());
		assertEquals(PNG, jpgX.getThumbFullContentType());
		assertEquals(PNG, pngX.getThumbFullContentType());
		assertEquals(null, txt.getThumbFullContentType());
		assertEquals(null, emp.getThumbFullContentType());

		assertEquals(JPEG, jpg.getThumbSameContentType());
		assertEquals(PNG,  png.getThumbSameContentType());
		assertEquals(GIF,  gif.getThumbSameContentType());
		assertEquals(JPEG, jpgX.getThumbSameContentType());
		assertEquals(PNG,  pngX.getThumbSameContentType());
		assertEquals(null, txt.getThumbSameContentType());
		assertEquals(null, emp.getThumbSameContentType());

		// get
		assertType(JPEG, jpg.getThumb());
		assertType(JPEG, png.getThumb());
		assertType(JPEG, gif.getThumb());
		assertType(JPEG, jpgX.getThumb());
		assertType(JPEG, pngX.getThumb());
		assertNull(txt.getThumb());
		assertNull(emp.getThumb());

		assertType(PNG, jpg.getThumbFull());
		assertType(PNG, png.getThumbFull());
		assertType(PNG, gif.getThumbFull());
		assertType(PNG, jpgX.getThumbFull());
		assertType(PNG, pngX.getThumbFull());
		assertNull(txt.getThumbFull());
		assertNull(emp.getThumbFull());

		assertType(JPEG, jpg.getThumbSame());
		assertType(PNG,  png.getThumbSame());
		assertType(GIF,  gif.getThumbSame());
		assertType(JPEG, jpgX.getThumbSame());
		assertType(PNG,  pngX.getThumbSame());
		assertNull(txt.getThumbSame());
		assertNull(emp.getThumbSame());

		// doGet
		assertDoGet(JPEG, thumb, jpg);
		assertDoGet(JPEG, thumb, png);
		assertDoGet(JPEG, thumb, gif);
		assertDoGet(JPEG, thumb, jpgX);
		assertDoGet(JPEG, thumb, pngX);
		assertDoGet404("not computable", thumb, txt);
		assertDoGet404("is null",        thumb, emp);

		assertDoGet(PNG, thumbFull, jpg);
		assertDoGet(PNG, thumbFull, png);
		assertDoGet(PNG, thumbFull, gif);
		assertDoGet(PNG, thumbFull, jpgX);
		assertDoGet(PNG, thumbFull, pngX);
		assertDoGet404("not computable", thumbFull, txt);
		assertDoGet404("is null",        thumbFull, emp);

		assertDoGet(JPEG, thumbSame, jpg);
		assertDoGet(PNG,  thumbSame, png);
		assertDoGet(GIF,  thumbSame, gif);
		assertDoGet(JPEG, thumbSame, jpgX);
		assertDoGet(PNG,  thumbSame, pngX);
		assertDoGet404("not computable", thumbSame, txt);
		assertDoGet404("is null",        thumbSame, emp);

		// url
		assertLocator("ThumbnailMagickItem/thumb/" + jpg.getCopeID() + ".jpg", jpg.getThumbLocator());
		assertLocator("ThumbnailMagickItem/thumb/" + png.getCopeID() + ".jpg", png.getThumbLocator());
		assertLocator("ThumbnailMagickItem/thumb/" + gif.getCopeID() + ".jpg", gif.getThumbLocator());
		assertLocator(null, txt.getThumbLocator());
		assertLocator(null, emp.getThumbLocator());

		assertLocator("ThumbnailMagickItem/thumbFull/" + jpg.getCopeID() + ".png", jpg.getThumbFullLocator());
		assertLocator("ThumbnailMagickItem/thumbFull/" + png.getCopeID() + ".png", png.getThumbFullLocator());
		assertLocator("ThumbnailMagickItem/thumbFull/" + gif.getCopeID() + ".png", gif.getThumbFullLocator());
		assertLocator(null, txt.getThumbFullLocator());
		assertLocator(null, emp.getThumbFullLocator());

		// url fallback
		assertEquals(jpg.getThumbLocator().getURLByConnect(), jpg.getThumbURLWithFallbackToSource());
		assertEquals(png.getThumbLocator().getURLByConnect(), png.getThumbURLWithFallbackToSource());
		assertEquals(gif.getThumbLocator().getURLByConnect(), gif.getThumbURLWithFallbackToSource());
		assertEquals(txt.getFileLocator ().getURLByConnect(), txt.getThumbURLWithFallbackToSource());
		assertEquals(null, emp.getThumbURLWithFallbackToSource());

		assertEquals(jpg.getThumbFullLocator().getURLByConnect(), jpg.getThumbFullURLWithFallbackToSource());
		assertEquals(png.getThumbFullLocator().getURLByConnect(), png.getThumbFullURLWithFallbackToSource());
		assertEquals(gif.getThumbFullLocator().getURLByConnect(), gif.getThumbFullURLWithFallbackToSource());
		assertEquals(txt.getFileLocator     ().getURLByConnect(), txt.getThumbFullURLWithFallbackToSource());
		assertEquals(null, emp.getThumbFullURLWithFallbackToSource());

		// locator fallback
		assertEquals("ThumbnailMagickItem/thumb/" + jpg.getCopeID() + ".jpg", jpg.getThumbLocatorWithFallbackToSource().getPath());
		assertEquals("ThumbnailMagickItem/thumb/" + png.getCopeID() + ".jpg", png.getThumbLocatorWithFallbackToSource().getPath());
		assertEquals("ThumbnailMagickItem/thumb/" + gif.getCopeID() + ".jpg", gif.getThumbLocatorWithFallbackToSource().getPath());
		assertEquals("ThumbnailMagickItem/file/"  + txt.getCopeID() + ".txt", txt.getThumbLocatorWithFallbackToSource().getPath());
		assertEquals(null, emp.getThumbLocatorWithFallbackToSource());

		assertEquals("ThumbnailMagickItem/thumbFull/" + jpg.getCopeID() + ".png", jpg.getThumbFullLocatorWithFallbackToSource().getPath());
		assertEquals("ThumbnailMagickItem/thumbFull/" + png.getCopeID() + ".png", png.getThumbFullLocatorWithFallbackToSource().getPath());
		assertEquals("ThumbnailMagickItem/thumbFull/" + gif.getCopeID() + ".png", gif.getThumbFullLocatorWithFallbackToSource().getPath());
		assertEquals("ThumbnailMagickItem/file/"  + txt.getCopeID() + ".txt", txt.getThumbFullLocatorWithFallbackToSource().getPath());
		assertEquals(null, emp.getThumbFullLocatorWithFallbackToSource());

		// isNull
		assertContains(emp, TYPE.search(file.isNull()));
		assertContains(jpg, jpgX, png, pngX, gif, txt, TYPE.search(file.isNotNull()));
		assertContains(emp , TYPE.search(thumb.isNull())); // TODO check for getSupportedSourceContentTypes, add text
		assertContains(jpg, jpgX, png, pngX, gif, txt, TYPE.search(thumb.isNotNull())); // TODO check for getSupportedSourceContentTypes, remove text
	}

	private static final void assertType(final String expectedContentType, final byte[] actualBody)
	{
		assertNotNull(expectedContentType);
		assertNotNull(actualBody);
		assertEquals(
				Collections.singleton(MediaType.forName(expectedContentType)),
				MediaType.forMagics(actualBody));
	}

	private final void assertDoGet(
			final String expectedContentType,
			final MediaImageMagickThumbnail feature,
			final ThumbnailMagickItem item) throws IOException, NotFound
	{
		assertNotNull(expectedContentType);
		assertNotNull(feature);
		assertNotNull(item);

		final Response response = new Response();
		final int delivered = feature.getInfo().getDelivered();
		feature.doGetAndCommit(null, response, item);
		assertFalse(model.hasCurrentTransaction());
		assertEquals(delivered, feature.getInfo().getDelivered());
		response.assertIt(expectedContentType);
		model.startTransaction(ThumbnailMagickTest.class.getName());
	}

	private static final class Response extends HttpServletResponseUtilDummy
	{
		Response()
		{
		}

		private int contentLength = Integer.MIN_VALUE;
		private String contentType = null;
		private ByteArrayOutputStream body = null;

		void assertIt(final String expectedContentType)
		{
			assertTrue(contentLength>0);
			assertEquals(expectedContentType, this.contentType);
			assertNotNull(body);
			assertEquals(
					Collections.singleton(MediaType.forName(contentType)),
					MediaType.forMagics(body.toByteArray()));
			assertEquals(contentLength, body.size());
		}

		@Override
		public void setContentLength(final int len)
		{
			assertTrue(len>0);
			assertEquals(Integer.MIN_VALUE, contentLength);
			assertNull(body);
			contentLength = len;
		}

		@Override
		public void setContentType(final String type)
		{
			assertNotNull(type);
			assertNull(contentType);
			assertNull(body);
			contentType = type;
		}

		@Override
		public ServletOutputStream getOutputStream()
		{
			assertNull(body);
			final ByteArrayOutputStream body = this.body = new ByteArrayOutputStream();

			return new ServletOutputStream()
			{
				@Override
				public void write(final int b)
				{
					body.write(b);
				}
			};
		}
	}

	private final void assertDoGet404(
			final String expectedResult,
			final MediaImageMagickThumbnail feature,
			final ThumbnailMagickItem item)
		throws IOException
	{
		assertNotNull(feature);
		assertNotNull(item);

		final HttpServletResponseUtilDummy response = new HttpServletResponseUtilDummy();

		try
		{
			feature.doGetAndCommit(null, response, item);
			fail();
		}
		catch(final NotFound e)
		{
			assertEquals(expectedResult, e.getMessage());
		}
		assertTrue(model.hasCurrentTransaction());
	}

	private static void assertLocator(
			final String expectedPath,
			final MediaPath.Locator actualLocator)
	{
		assertEquals(expectedPath, actualLocator!=null ? actualLocator.getPath() : null);
	}
}
