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

import static com.exedio.cope.PrometheusMeterRegistrar.meter;
import static com.exedio.cope.PrometheusMeterRegistrar.tag;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.instrument.Visibility.PACKAGE;
import static com.exedio.cope.pattern.MediaGzipTest.COMPRESSED;
import static com.exedio.cope.pattern.MediaGzipTest.CONTENT_TYPE;
import static com.exedio.cope.pattern.MediaGzipTest.PLAIN;
import static com.exedio.cope.util.Hex.decodeLower;
import static com.exedio.cope.util.Hex.encodeLower;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.PrometheusMeterRegistrar;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serial;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public final class MediaGzipServletTest extends TestWithEnvironment
{
	public static final Model MODEL = new Model(AnItem.TYPE);

	public MediaGzipServletTest()
	{
		super(MODEL);
	}

	private MyMediaServlet servlet;

	@BeforeEach void setUp()
	{
		servlet = new MyMediaServlet();
		servlet.initPaths(MODEL);
		servlet.initConnected(MODEL);
	}

	@AfterEach void tearDown()
	{
		servlet.destroy();
	}

	@Test void test() throws ServletException, IOException
	{
		assertEquals(true,  AnItem.gzipped .isBodySmall);
		assertEquals(false, AnItem.gzLarge .isBodySmall);
		assertEquals(true,  AnItem.identity.isBodySmall);

		final AnItem item = new AnItem(
				Media.toValue(decodeLower(PLAIN), CONTENT_TYPE),
				Media.toValue(decodeLower(PLAIN), CONTENT_TYPE),
				Media.toValue(decodeLower(PLAIN), CONTENT_TYPE));
		assertEquals(COMPRESSED, item.getGzippedRaw());
		assertEquals(COMPRESSED, item.getGzLargeRaw());
		assertEquals(PLAIN,      item.getIdentityRaw());

		{
			final String path = "/" + item.getGzippedLocator().getPath();
			assertCounters(0, AnItem.gzipped, true);
			service(new Request(path, null   )).assertOk(PLAIN, null);
			assertCounters(1, AnItem.gzipped, true);
			service(new Request(path, "other")).assertOk(PLAIN, null);
			assertCounters(2, AnItem.gzipped, true);
			service(new Request(path, "gzip" )).assertOk(COMPRESSED, "gzip");
			assertCounters(2, AnItem.gzipped, true);
			service(new Request(path, "GziP" )).assertOk(COMPRESSED, "gzip");
			assertCounters(2, AnItem.gzipped, true);
		}
		{
			final String path = "/" + item.getGzLargeLocator().getPath();
			assertCounters(0, AnItem.gzLarge, true);
			service(new Request(path, null   )).assertOk(PLAIN, null);
			assertCounters(1, AnItem.gzLarge, true);
			service(new Request(path, "other")).assertOk(PLAIN, null);
			assertCounters(2, AnItem.gzLarge, true);
			service(new Request(path, "gzip" )).assertOk(COMPRESSED, "gzip");
			assertCounters(2, AnItem.gzLarge, true);
			service(new Request(path, "GziP" )).assertOk(COMPRESSED, "gzip");
			assertCounters(2, AnItem.gzLarge, true);
		}
		{
			final String path = "/" + item.getIdentityLocator().getPath();
			assertCounters(0, AnItem.identity, false);
			service(new Request(path, null   )).assertOk(PLAIN, null);
			assertCounters(0, AnItem.identity, false);
			service(new Request(path, "other")).assertOk(PLAIN, null);
			assertCounters(0, AnItem.identity, false);
			service(new Request(path, "gzip" )).assertOk(PLAIN, null);
			assertCounters(0, AnItem.identity, false);
			service(new Request(path, "GziP" )).assertOk(PLAIN, null);
			assertCounters(0, AnItem.identity, false);
		}
		assertCounters(2, AnItem.gzipped, true);
		assertCounters(2, AnItem.gzLarge, true);
		assertCounters(0, AnItem.identity, false);
	}

	private Response service(final Request request)
		throws ServletException, IOException
	{
		MODEL.commit();
		final Response response = new Response();
		servlet.service(request, response);
		MODEL.startTransaction("MediaPathTest");
		return response;
	}

	private static final class Request extends AssertionFailedHttpServletRequest
	{
		private final String pathInfo;
		private final String acceptEncoding;

		Request(
				final String pathInfo,
				final String acceptEncoding)
		{
			this.pathInfo = pathInfo;
			this.acceptEncoding = acceptEncoding;
		}

		@Override
		public String getMethod()
		{
			return "GET";
		}

		@Override
		public String getHeader(final String name)
		{
			if("Accept-Encoding".equals(name))
				return acceptEncoding;
			else
				return super.getHeader(name);
		}

		@Override
		public long getDateHeader(final String name)
		{
			if("If-Modified-Since".equals(name))
				return -1;
			else
				return super.getDateHeader(name);
		}

		@Override
		public String getPathInfo()
		{
			return pathInfo;
		}

		@Override
		public String getQueryString()
		{
			return null;
		}
	}

	private static final class Response extends AssertionFailedHttpServletResponse
	{
		Response()
		{
			// make package private
		}

		private String contentEncoding;

		@Override
		public void setHeader(final String name, final String value)
		{
			if("Cache-Control".equals(name))
			{
				assertEquals("max-age="+MODEL.getConnectProperties().getMediaMaxAge(), value);
			}
			else if("Content-Encoding".equals(name))
			{
				assertNotNull(value);
				assertEquals(null, this.contentEncoding);
				assertNull(out);
				this.contentEncoding = value;
			}
			else
				super.setHeader(name, value);
		}


		@Override
		public void setDateHeader(final String name, final long date)
		{
			if(!"Last-Modified".equals(name))
				super.setDateHeader(name, date);
		}


		@Override
		public void setContentType(final String contentType)
		{
			assertEquals(CONTENT_TYPE, contentType);
		}


		long contentLength = Long.MIN_VALUE;

		@Override
		public void setContentLength(final int contentLength)
		{
			assertFalse(contentLength==Integer.MIN_VALUE);
			assertEquals(Long.MIN_VALUE, this.contentLength);
			assertNull(out);
			this.contentLength = contentLength;
		}

		@Override
		public void setContentLengthLong(final long contentLength)
		{
			assertFalse(contentLength==Integer.MIN_VALUE);
			assertEquals(Long.MIN_VALUE, this.contentLength);
			assertNull(out);
			this.contentLength = contentLength;
		}


		@Override
		public boolean isCommitted()
		{
			assertNull(out);

			return false;
		}

		@Override
		public void reset()
		{
			assertNull(out);
		}

		ByteArrayOutputStream out = null;

		String outString()
		{
			return
					out!=null
					? encodeLower(out.toByteArray())
					: null;
		}

		@Override
		public ServletOutputStream getOutputStream()
		{
			assertFalse(MODEL.hasCurrentTransaction());
			assertNull(out);

			final ByteArrayOutputStream myOut = new ByteArrayOutputStream();
			out = myOut;

			return new AssertionFailedServletOutputStream()
			{
				@Override
				public void write(final byte[] b, final int off, final int len)
				{
					myOut.write(b, off, len);
				}
			};
		}


		void assertOk(final String body, final String contentEncoding)
		{
			assertAll(
					() -> assertEquals(body,            this.outString(),     "content"),
					() -> assertEquals(body.length()/2, this.contentLength,   "contentLength"),
					() -> assertEquals(contentEncoding, this.contentEncoding, "contentEncoding"));
		}
	}

	private static final class MyMediaServlet extends MediaServlet
	{
		MyMediaServlet()
		{
			// make package private
		}

		@Override
		protected void onException(
				final HttpServletRequest request,
				final Exception exception)
		{
			throw new AssertionFailedError("", exception);
		}

		@Serial
		private static final long serialVersionUID = 1L;
	}

	private static void assertCounters(final int inflate, final Media media, final boolean present)
	{
		assertEquals(inflate, count(media, "inflate", Tags.empty(), present));
	}

	private static double count(
			final Media feature, final String nameSuffix,
			final Tags tags,
			final boolean present)
	{
		return count(nameSuffix, tags.and(tag(feature)), present);
	}

	private static double count(
			final String nameSuffix,
			final Tags tags,
			final boolean present)
	{
		if(present)
		{
			return ((Counter)meter(MediaPath.class, nameSuffix, tags)).count();
		}
		else
		{
			assertThrows(
					PrometheusMeterRegistrar.NotFound.class,
					() -> meter(MediaPath.class, nameSuffix, tags));
			return 0;
		}
	}

	@WrapperType(indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@Wrapper(wrap="getLocator", visibility=PACKAGE)
		@Wrapper(wrap="*", visibility=NONE)
		static final Media gzipped = new Media().gzip();

		@Wrapper(wrap="getLocator", visibility=PACKAGE)
		@Wrapper(wrap="*", visibility=NONE)
		static final Media gzLarge = new Media().gzip().lengthMax(Media.DEFAULT_LENGTH+1);

		@Wrapper(wrap="getLocator", visibility=PACKAGE)
		@Wrapper(wrap="*", visibility=NONE)
		static final Media identity = new Media();

		String getGzippedRaw()
		{
			return encodeLower(gzipped.getBody().getArray(this));
		}

		String getGzLargeRaw()
		{
			return encodeLower(gzLarge.getBody().getArray(this));
		}

		String getIdentityRaw()
		{
			return encodeLower(identity.getBody().getArray(this));
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnItem(
					@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value gzipped,
					@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value gzLarge,
					@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value identity)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(AnItem.gzipped,gzipped),
				com.exedio.cope.SetValue.map(AnItem.gzLarge,gzLarge),
				com.exedio.cope.SetValue.map(AnItem.identity,identity),
			});
		}

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		com.exedio.cope.pattern.MediaPath.Locator getGzippedLocator()
		{
			return AnItem.gzipped.getLocator(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		com.exedio.cope.pattern.MediaPath.Locator getGzLargeLocator()
		{
			return AnItem.gzLarge.getLocator(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		com.exedio.cope.pattern.MediaPath.Locator getIdentityLocator()
		{
			return AnItem.identity.getLocator(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
