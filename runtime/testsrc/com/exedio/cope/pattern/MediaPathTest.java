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

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.pattern.MediaPathFeature.Result;
import com.exedio.cope.util.Clock;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

public final class MediaPathTest extends AbstractRuntimeModelTest
{
	public static final Model MODEL = new Model(MediaPathItem.TYPE);

	static
	{
		MODEL.enableSerialization(MediaPathTest.class, "MODEL");
	}

	public MediaPathTest()
	{
		super(MODEL);
	}

	private MediaPathItem item;
	private String id;
	private MyMediaServlet servlet;
	private AbsoluteMockClockStrategy clock;
	private MediaInfo normalInfo = null;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = new MediaPathItem();
		id = item.getCopeID();
		servlet = new MyMediaServlet();
		servlet.initPathes(MODEL);
		servlet.initConnected(MODEL);
		clock = new AbsoluteMockClockStrategy();
		Clock.override(clock);
		normalInfo = MediaPathItem.normal.getInfo();
	}

	@Override
	public void tearDown() throws Exception
	{
		Clock.clearOverride();
		servlet.destroy();
		servlet = null;
		for(final Type<?> type : MODEL.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof MediaPathFeature)
					((MediaPathFeature)feature).reset();
		super.tearDown();
	}

	public void testNotFound() throws ServletException, IOException
	{
		item.setNormalContentType("blah/foo");
		assertNotFound("zack", "no such path");
		assertNotFound("zack/zick", "no such path");
		assertNotFound("/MediaPathItem/normal", "no such path");
		assertNotFound("/MediaPathItem/normal/", "not an item");
		assertNotFound("/MediaPathItem/normal/x", "not an item");
		assertNotFound("/MediaPathItem/normal/x.jpg", "not an item");
		assertNotFound("/MediaPathItem/normal/MediaPathItem-x.jpg", "not an item");
		assertNotFound("/MediaPathItem/normal/MediaPathItem-9999.jpg", "no such item");

		final String pathInfo = "/MediaPathItem/normal/" + id;
		assertOk(pathInfo);

		item.setNormalResult(Result.notFoundIsNull);
		assertNotFound(pathInfo, "is null");

		item.setNormalResult(Result.notFoundNotComputable);
		assertNotFound(pathInfo, "not computable");
	}

	public void testException() throws ServletException, IOException
	{
		final String pathInfo = "/MediaPathItem/normal/" + id;
		assertOk(pathInfo);

		servlet.failOnException = false;

		item.setNormalResult(Result.IOException);
		assertError(pathInfo);

		item.setNormalResult(Result.RuntimeException);
		assertError(pathInfo);
	}

	private static final String prefix = "/testContextPath/testServletPath";

	public void testRedirectFrom() throws ServletException, IOException
	{
		item.setNormalContentType("blah/foo");
		assertEquals("MediaPathItem/normal/" + id, item.getNormalLocator().getPath());
		assertOk("/MediaPathItem/normal/" + id);
		assertRedirect("/MediaPathItem/normalRedirect1/" + id,                 prefix + "/MediaPathItem/normal/" + id);
		assertRedirect("/MediaPathItem/normalRedirect2/" + id,                 prefix + "/MediaPathItem/normal/" + id);
		assertRedirect("/MediaPathItem/normalRedirect2/" + id + ".jpg",        prefix + "/MediaPathItem/normal/" + id + ".jpg");
		assertRedirect("/MediaPathItem/normalRedirect2/" + id + "/phrase",     prefix + "/MediaPathItem/normal/" + id + "/phrase");
		assertRedirect("/MediaPathItem/normalRedirect2/" + id + "/phrase.jpg", prefix + "/MediaPathItem/normal/" + id + "/phrase.jpg");
	}

	public void testRedirectFromExtension() throws ServletException, IOException
	{
		item.setNormalContentType("image/jpeg");
		assertEquals("MediaPathItem/normal/" + id + ".jpg", item.getNormalLocator().getPath());
		assertOk("/MediaPathItem/normal/" + id + ".jpg");
		assertRedirect("/MediaPathItem/normalRedirect1/" + id,                 prefix + "/MediaPathItem/normal/" + id);
		assertRedirect("/MediaPathItem/normalRedirect2/" + id,                 prefix + "/MediaPathItem/normal/" + id);
		assertRedirect("/MediaPathItem/normalRedirect2/" + id + ".jpg",        prefix + "/MediaPathItem/normal/" + id + ".jpg");
		assertRedirect("/MediaPathItem/normalRedirect2/" + id + "/phrase",     prefix + "/MediaPathItem/normal/" + id + "/phrase");
		assertRedirect("/MediaPathItem/normalRedirect2/" + id + "/phrase.jpg", prefix + "/MediaPathItem/normal/" + id + "/phrase.jpg");
	}

	public void testRedirectFromPhrase() throws ServletException, IOException
	{
		item.setNormalContentType("blah/foo");
		item.setCatchphrase("phrase");
		assertEquals("MediaPathItem/normal/" + id + "/phrase", item.getNormalLocator().getPath());
		assertOk("/MediaPathItem/normal/" + id + "/phrase");
		assertRedirect("/MediaPathItem/normalRedirect1/" + id,                 prefix + "/MediaPathItem/normal/" + id);
		assertRedirect("/MediaPathItem/normalRedirect2/" + id,                 prefix + "/MediaPathItem/normal/" + id);
		assertRedirect("/MediaPathItem/normalRedirect2/" + id + ".jpg",        prefix + "/MediaPathItem/normal/" + id + ".jpg");
		assertRedirect("/MediaPathItem/normalRedirect2/" + id + "/phrase",     prefix + "/MediaPathItem/normal/" + id + "/phrase");
		assertRedirect("/MediaPathItem/normalRedirect2/" + id + "/phrase.jpg", prefix + "/MediaPathItem/normal/" + id + "/phrase.jpg");
	}

	public void testRedirectFromPhraseExtension() throws ServletException, IOException
	{
		item.setNormalContentType("image/jpeg");
		item.setCatchphrase("phrase");
		assertEquals("MediaPathItem/normal/" + id + "/phrase.jpg", item.getNormalLocator().getPath());
		assertOk("/MediaPathItem/normal/" + id + "/phrase.jpg");
		assertRedirect("/MediaPathItem/normalRedirect1/" + id,                 prefix + "/MediaPathItem/normal/" + id);
		assertRedirect("/MediaPathItem/normalRedirect2/" + id,                 prefix + "/MediaPathItem/normal/" + id);
		assertRedirect("/MediaPathItem/normalRedirect2/" + id + ".jpg",        prefix + "/MediaPathItem/normal/" + id + ".jpg");
		assertRedirect("/MediaPathItem/normalRedirect2/" + id + "/phrase",     prefix + "/MediaPathItem/normal/" + id + "/phrase");
		assertRedirect("/MediaPathItem/normalRedirect2/" + id + "/phrase.jpg", prefix + "/MediaPathItem/normal/" + id + "/phrase.jpg");
	}

	public void testRedirectFromFinger() throws ServletException, IOException
	{
		item.setFingerContentType("image/jpeg");
		item.setFingerLastModified(new Date(333338888));
		assertRedirect("/MediaPathItem/fingerRedirect1/.f/"          + id, prefix + "/MediaPathItem/finger/.f/"          + id);
		assertRedirect("/MediaPathItem/fingerRedirect2/.f/"          + id, prefix + "/MediaPathItem/finger/.f/"          + id);
		assertRedirect("/MediaPathItem/fingerRedirect1/.fx/"         + id, prefix + "/MediaPathItem/finger/.fx/"         + id);
		assertRedirect("/MediaPathItem/fingerRedirect1/.f333338888/" + id, prefix + "/MediaPathItem/finger/.f333338888/" + id);
		assertRedirect("/MediaPathItem/fingerRedirect1/.f333339000/" + id, prefix + "/MediaPathItem/finger/.f333339000/" + id);
	}

	public void testCatchphrase() throws ServletException, IOException
	{
		item.setNormalContentType("blah/foo");
		item.setCatchphrase("phrase");
		final String ok = "/MediaPathItem/normal/" + id + "/phrase";
		assertEquals(ok, "/" + item.getNormalLocator().getPath());
		assertOk(ok);
		assertRedirect("/MediaPathItem/normal/" + id,                      prefix + ok);
		assertRedirect("/MediaPathItem/normal/" + id + "/otherPhrase",     prefix + ok);
		assertRedirect("/MediaPathItem/normal/" + id + "/phrase.jpg",      prefix + ok);
		assertRedirect("/MediaPathItem/normal/" + id + "/otherPhrase.jpg", prefix + ok);
		assertRedirect("/MediaPathItem/normal/" + id + "/phrase.png",      prefix + ok);
	}

	public void testCatchphraseExtension() throws ServletException, IOException
	{
		item.setNormalContentType("image/jpeg");
		item.setCatchphrase("phrase");
		final String ok = "/MediaPathItem/normal/" + id + "/phrase.jpg";
		assertEquals(ok, "/" + item.getNormalLocator().getPath());
		assertOk(ok);
		assertRedirect("/MediaPathItem/normal/" + id,                      prefix + ok);
		assertRedirect("/MediaPathItem/normal/" + id + "/otherPhrase",     prefix + ok);
		assertRedirect("/MediaPathItem/normal/" + id + "/phrase.png",      prefix + ok);
		assertRedirect("/MediaPathItem/normal/" + id + "/otherPhrase.jpg", prefix + ok);
		assertRedirect("/MediaPathItem/normal/" + id + "/phrase.png",      prefix + ok);
	}

	public void testFingerNotFound() throws ServletException, IOException
	{
		item.setNormalContentType("blah/foo");
		assertNotFound("/MediaPathItem/normal/."   , "invalid special");
		assertNotFound("/MediaPathItem/normal/.X"  , "invalid special");
		assertNotFound("/MediaPathItem/normal/.X/" , "invalid special");
		assertNotFound("/MediaPathItem/normal/.f"  , "invalid special");
		assertNotFound("/MediaPathItem/normal/.f/" , "not an item");
		assertNotFound("/MediaPathItem/normal/.fx" , "invalid special");
		assertNotFound("/MediaPathItem/normal/.fx/", "not an item");
	}

	public void testFingerWithoutLastModified() throws ServletException, IOException
	{
		item.setFingerContentType("image/jpeg");
		item.setCatchphrase("phrase");
		final String ok = "/MediaPathItem/finger/" + id + "/phrase.jpg";
		assertEquals(ok, "/" + item.getFingerLocator().getPath());
		assertOk(ok);
		assertRedirect("/MediaPathItem/finger/" + id,                      prefix + ok);
		assertRedirect("/MediaPathItem/finger/" + id + "/otherPhrase",     prefix + ok);
		assertRedirect("/MediaPathItem/finger/" + id + "/phrase.png",      prefix + ok);
		assertRedirect("/MediaPathItem/finger/" + id + "/otherPhrase.jpg", prefix + ok);
		assertRedirect("/MediaPathItem/finger/" + id + "/phrase.png",      prefix + ok);
	}

	public void testFinger() throws ServletException, IOException
	{
		item.setFingerContentType("image/jpeg");
		item.setFingerLastModified(new Date(333338888));
		item.setCatchphrase("phrase");
		final int ALMOST_ONE_YEAR = 31363200;
		final String ok = "/MediaPathItem/finger/.fIkl3T/" + id + "/phrase.jpg";
		assertEquals(ok, "/" + item.getFingerLocator().getPath());
		service(new Request(ok)).assertLastModified(333339000l).assertOkAndCacheControl("max-age="+ALMOST_ONE_YEAR);

		assertRedirect("/MediaPathItem/finger/" + id,                      prefix + ok);
		assertRedirect("/MediaPathItem/finger/" + id + "/otherPhrase",     prefix + ok);
		assertRedirect("/MediaPathItem/finger/" + id + "/phrase.png",      prefix + ok);
		assertRedirect("/MediaPathItem/finger/" + id + "/otherPhrase.jpg", prefix + ok);
		assertRedirect("/MediaPathItem/finger/" + id + "/phrase.png",      prefix + ok);

		assertRedirect("/MediaPathItem/finger/.fx/" + id,                      prefix + ok);
		assertRedirect("/MediaPathItem/finger/.fx/" + id + "/otherPhrase",     prefix + ok);
		assertRedirect("/MediaPathItem/finger/.fx/" + id + "/phrase.png",      prefix + ok);
		assertRedirect("/MediaPathItem/finger/.fx/" + id + "/otherPhrase.jpg", prefix + ok);
		assertRedirect("/MediaPathItem/finger/.fx/" + id + "/phrase.png",      prefix + ok);
	}

	public void testConditional() throws ServletException, IOException
	{
		item.setNormalContentType("image/jpeg");
		item.setCatchphrase("phrase");
		final String ok = "/MediaPathItem/normal/" + id + "/phrase.jpg";
		assertEquals(ok, "/" + item.getNormalLocator().getPath());
		assertOk(ok);

		item.setNormalLastModified(new Date(77771000l));
		service(new Request(ok)).assertOkAndCache(77771000l);

		item.setNormalLastModified(new Date(77771001l));
		service(new Request(ok)).assertOkAndCache(77772000l);

		item.setNormalLastModified(new Date(77771999l));
		service(new Request(ok)).assertOkAndCache(77772000l);

		item.setNormalLastModified(new Date(77772000l));
		service(new Request(ok)).assertOkAndCache(77772000l);

		item.setNormalLastModified(new Date(77772001l));
		service(new Request(ok)).assertOkAndCache(77773000l);

		item.setNormalLastModified(new Date(77772000l));
		service(new Request(ok).ifModifiedSince(77771999l)).assertOkAndCache (77772000l);
		service(new Request(ok).ifModifiedSince(77772000l)).assertNotModified(77772000l);
		service(new Request(ok).ifModifiedSince(77772001l)).assertNotModified(77772000l);
	}

	public void testExpires() throws ServletException, IOException
	{
		item.setNormalContentType("image/jpeg");
		item.setCatchphrase("phrase");
		final String ok = "/MediaPathItem/normal/" + id + "/phrase.jpg";
		assertEquals(ok, "/" + item.getNormalLocator().getPath());
		service(new Request(ok)).assertOk();

		item.setNormalLastModified(new Date(77772000l));
		final Response response = service(new Request(ok));
		response.assertOkAndCache(77772000l);
	}

	/**
	 * This test became useless, as there is no connection between
	 * toFinal and Expires anymore.
	 */
	public void testExpiresFinal() throws ServletException, IOException
	{
		MediaPathItem.normal.setFinal(true);
		item.setNormalContentType("image/jpeg");
		item.setNormalLastModified(new Date(333338888));
		final String ok = "/MediaPathItem/normal/" + id + ".jpg";
		assertEquals(ok, "/" + item.getNormalLocator().getPath());
		final Response response = service(new Request(ok));
		response.assertOkAndCache(333339000l);
	}

	public void testGuess() throws ServletException, IOException
	{
		item.setGuessContentType("image/jpeg");
		final String ok = "/MediaPathItem/guess/" + id + ".jpg";
		assertEquals(ok + "?t=MediaPathItem.guess-" + id, "/" + item.getGuessLocator().getPath());
		service(new Request(ok).token("MediaPathItem.guess-" + id)).assertOkAndCacheControl("private");

		assertNotFound(ok, "guessed url", "zack");
		assertNotFound(ok, "guessed url", "");
		assertNotFound(ok, "guessed url");
	}

	public void testGuessAndAge() throws ServletException, IOException
	{
		item.setGuessContentType("image/jpeg");
		item.setGuessLastModified(new Date(333338888));
		final String ok = "/MediaPathItem/guess/" + id + ".jpg";
		assertEquals(ok + "?t=MediaPathItem.guess-" + id, "/" + item.getGuessLocator().getPath());
		service(new Request(ok).token("MediaPathItem.guess-" + id)).assertOkAndCacheControl(
				MODEL.getConnectProperties().getMediaOffsetExpires()>0
				? "private,max-age=5"
				: "private");
	}

	public void testAccessControlAllowOriginWildcard() throws ServletException, IOException
	{
		item.setNormalContentType("image/jpeg");
		final String ok = "/MediaPathItem/normal/" + id + ".jpg";
		assertEquals(ok, "/" + item.getNormalLocator().getPath());
		service(new Request(ok)).assertOk();

		item.setAccessControlAllowOriginWildcard(true);
		service(new Request(ok)).assertOkAndAccessControlAllowOrigin("*");
	}

	// TODO testInfo with others

	public void testInfoNotAnItem() throws ServletException, IOException
	{
		assertNotFound("/MediaPathItem/normal/x", "not an item");
		assertInfo(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0);
	}

	public void testInfoNoSuchItem() throws ServletException, IOException
	{
		assertNotFound("/MediaPathItem/normal/MediaPathItem-9999.jpg", "no such item");
		assertInfo(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0);
	}

	public void testInfoMoved() throws ServletException, IOException
	{
		item.setNormalContentType("image/jpeg");
		assertRedirect("/MediaPathItem/normal/" + id, prefix + "/MediaPathItem/normal/" + id + ".jpg");
		assertInfo(0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
	}

	public void testInfoIsNull() throws ServletException, IOException
	{
		item.setNormalResult(Result.notFoundIsNull);
		assertNotFound("/MediaPathItem/normal/" + id, "is null");
		assertInfo(0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0);
	}

	public void testInfoNotComputable() throws ServletException, IOException
	{
		item.setNormalResult(Result.notFoundNotComputable);
		assertNotFound("/MediaPathItem/normal/" + id, "not computable");
		assertInfo(0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
	}

	// TODO testInfoNotModified

	public void testInfoDelivered() throws ServletException, IOException
	{
		assertOk("/MediaPathItem/normal/" + id);
		assertInfo(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
	}

	private void assertInfo(
			final int noSuchPath,
			final int redirectFrom,
			final int exception,
			final int invalidSpecial,
			final int guessedUrl,
			final int notAnItem,
			final int noSuchItem,
			final int moved,
			final int isNull,
			final int notComputable,
			final int notModified,
			final int delivered)
	{
		final MediaInfo i = MediaPathItem.normal.getInfo();
		assertEquals("noSuchPath",     noSuchPath,     0);
		assertEquals("redirectFrom",   redirectFrom,   i.getRedirectFrom()   - normalInfo.getRedirectFrom());
		assertEquals("exception",      exception,      i.getException()      - normalInfo.getException());
		assertEquals("invalidSpecial", invalidSpecial, i.getInvalidSpecial() - normalInfo.getInvalidSpecial());
		assertEquals("guessedUrl",     guessedUrl,     i.getGuessedUrl()     - normalInfo.getGuessedUrl());
		assertEquals("notAnItem",      notAnItem,      i.getNotAnItem()      - normalInfo.getNotAnItem());
		assertEquals("noSuchItem",     noSuchItem,     i.getNoSuchItem()     - normalInfo.getNoSuchItem());
		assertEquals("moved",          moved,          i.getMoved()          - normalInfo.getMoved());
		assertEquals("isNull",         isNull,         i.getIsNull()         - normalInfo.getIsNull());
		assertEquals("notComputable",  notComputable,  i.getNotComputable()  - normalInfo.getNotComputable());
		assertEquals("notModified",    notModified,    i.getNotModified()    - normalInfo.getNotModified());
		assertEquals("delivered",      delivered,      i.getDelivered()      - normalInfo.getDelivered());
	}

	private void assertOk(
			final String pathInfo)
		throws ServletException, IOException
	{
		service(new Request(pathInfo)).assertOk();
	}

	private void assertNotFound(
			final String pathInfo,
			final String reason)
		throws ServletException, IOException
	{
		assertNotFound(pathInfo, reason, null);
	}

	private void assertNotFound(
			final String pathInfo,
			final String reason,
			final String token)
		throws ServletException, IOException
	{
		service(new Request(pathInfo).token(token)).assertError(
				SC_NOT_FOUND, "us-ascii", "text/html",
				"<html>\n" +
				"<head>\n" +
				"<title>Not Found</title>\n" +
				"<meta http-equiv=\"content-type\" content=\"text/html;charset=us-ascii\">\n" +
				"<meta name=\"generator\" content=\"cope media servlet\">\n" +
				"</head>\n" +
				"<body>\n" +
				"<h1>Not Found</h1>\n" +
				"The requested URL was not found on this server (" + reason + ").\n" +
				"</body>\n" +
				"</html>\n");
	}

	private void assertError(
			final String pathInfo)
		throws ServletException, IOException
	{
		service(new Request(pathInfo)).assertError(
				SC_INTERNAL_SERVER_ERROR, "us-ascii", "text/html",
				"<html>\n" +
				"<head>\n" +
				"<title>Internal Server Error</title>\n" +
				"<meta http-equiv=\"content-type\" content=\"text/html;charset=us-ascii\">\n" +
				"<meta name=\"generator\" content=\"cope media servlet\">\n" +
				"</head>\n" +
				"<body>\n" +
				"<h1>Internal Server Error</h1>\n" +
				"An internal error occured on the server.\n" +
				"</body>\n" +
				"</html>\n");
	}

	private void assertRedirect(
			final String pathInfo,
			final String location)
		throws ServletException, IOException
	{
		service(new Request(pathInfo)).assertRedirect(location);
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

	private static final class Request extends HttpServletRequestDummy
	{
		private final String pathInfo;
		private String token;
		private long ifModifiedSince = -1;

		Request(final String pathInfo)
		{
			this.pathInfo = pathInfo;
		}

		Request token(final String token)
		{
			this.token = token;
			return this;
		}

		Request ifModifiedSince(final long ifModifiedSince)
		{
			this.ifModifiedSince = ifModifiedSince;
			return this;
		}

		@Override()
		public String getRemoteAddr()
		{
			return "testRemoteAddr";
		}

		@Override()
		public boolean isSecure()
		{
			return false;
		}

		@Override
		public String getMethod()
		{
			return "GET";
		}

		@Override()
		public String getHeader(final String name)
		{
			if("Host".equals(name))
				return "testHostHeader";
			else if("Referer".equals(name))
				return "testReferer";
			else if("User-Agent".equals(name))
				return "testUserAgent";
			else
				return super.getHeader(name);
		}

		@Override()
		public long getDateHeader(final String name)
		{
			if("If-Modified-Since".equals(name))
				return ifModifiedSince;
			else
				return super.getDateHeader(name);
		}

		@Override()
		public String getContextPath()
		{
			return "/testContextPath";
		}

		@Override()
		public String getServletPath()
		{
			return "/testServletPath";
		}

		@Override
		public String getPathInfo()
		{
			return pathInfo;
		}

		@Override
		public String getQueryString()
		{
			return token!=null ? ("t=" + token) : null;
		}

		@Override()
		@SuppressFBWarnings("PZLA_PREFER_ZERO_LENGTH_ARRAYS")
		public String[] getParameterValues(final String name)
		{
			if(!"t".equals(name))
				return super.getParameterValues(name);

			return token!=null ? new String[]{token} : null;
		}

		@Override()
		public Enumeration<?> getParameterNames()
		{
			return token!=null ? new Vector<>(Arrays.asList("t")).elements() : null;
		}

		@Override()
		public String getParameter(final String name)
		{
			if("t".equals(name))
				return token;
			else
				return super.getParameter(name);
		}
	}

	private static final class Response extends HttpServletResponseDummy
	{
		Response()
		{
			// make package private
		}

		private final int mediaOffsetExpires = MODEL.getConnectProperties().getMediaOffsetExpires();
		private String location;
		private String cacheControl;
		private String accessControlAllowOrigin;

		@Override()
		public void setHeader(final String name, final String value)
		{
			if("Location".equals(name))
			{
				assertNotNull(value);
				assertEquals(null, this.location);
				assertNull(out);
				this.location = value;
			}
			else if("Cache-Control".equals(name))
			{
				assertNotNull(value);
				assertEquals(null, this.cacheControl);
				assertNull(out);
				this.cacheControl = value;
			}
			else if("Access-Control-Allow-Origin".equals(name))
			{
				assertNotNull(value);
				assertEquals(null, this.accessControlAllowOrigin);
				assertNull(out);
				this.accessControlAllowOrigin = value;
			}
			else
				super.setHeader(name, value);
		}


		private long lastModified = Long.MIN_VALUE;

		@Override()
		public void setDateHeader(final String name, final long date)
		{
			if("Last-Modified".equals(name))
			{
				assertFalse(date==Long.MIN_VALUE);
				assertEquals(Long.MIN_VALUE, this.lastModified);
				assertNull(out);
				this.lastModified = date;
			}
			else
				super.setDateHeader(name, date);
		}


		private int status = Integer.MIN_VALUE;

		@Override
		public void setStatus(final int sc)
		{
			assertFalse(sc==Integer.MIN_VALUE);
			assertEquals(Integer.MIN_VALUE, this.status);
			assertNull(out);
			this.status = sc;
		}


		private String charset = null;

		@Override
		public void setCharacterEncoding(final String charset)
		{
			assertNotNull(charset);
			assertEquals(null, this.charset);
			assertNull(out);
			this.charset = charset;
		}


		String contentType = null;

		@Override
		public void setContentType(final String contentType)
		{
			assertNotNull(contentType);
			assertEquals(null, this.contentType);
			assertNull(out);
			this.contentType = contentType;
		}


		int contentLength = Integer.MIN_VALUE;

		@Override
		public void setContentLength(final int contentLength)
		{
			assertFalse(contentLength==Integer.MIN_VALUE);
			assertEquals(Integer.MIN_VALUE, this.contentLength);
			assertNull(out);
			this.contentLength = contentLength;
		}


		@Override()
		public boolean isCommitted()
		{
			assertNull(out);

			return false;
		}

		@Override()
		public void reset()
		{
			assertNull(out);
		}

		ByteArrayOutputStream out = null;

		String outString()
		{
			return
					out!=null
					? new String(out.toByteArray(), StandardCharsets.US_ASCII)
					: null;
		}

		@Override
		public ServletOutputStream getOutputStream()
		{
			assertFalse(MODEL.hasCurrentTransaction());
			assertNull(out);

			final ByteArrayOutputStream myOut = new ByteArrayOutputStream();
			out = myOut;

			return new ServletOutputStream()
			{
				@Override
			   public void write(final byte b[], final int off, final int len)
			   {
					myOut.write(b, off, len);
			   }

				@Override
				public void write(final int b)
				{
					throw new RuntimeException();
				}
			};
		}


		private int flushBufferCount = 0;

		@Override
		public void flushBuffer()
		{
			assertNull(out);
			flushBufferCount++;
		}


		private String maxAge()
		{
			return
				mediaOffsetExpires>0
				? "max-age="+(mediaOffsetExpires/1000l)
				: null;
		}


		void assertOk()
		{
			assertEquals("location",      null, this.location);
			assertEquals("lastModified",  Long.MIN_VALUE, this.lastModified);
			assertEquals("sc",            Integer.MIN_VALUE, this.status);
			assertEquals("charset",       null, this.charset);
			assertEquals("contentType",   null, this.contentType);
			assertEquals("content",       "responseBody", this.outString());
			assertEquals("contentLength", 10011, this.contentLength);
			assertEquals("cacheControl",  null, this.cacheControl);
			assertEquals("accessControlAllowOrigin", null, this.accessControlAllowOrigin);
			assertEquals("flushBuffer",   0, this.flushBufferCount);
		}

		void assertOkAndCache(final long lastModified)
		{
			assertEquals("location",      null, this.location);
			assertEquals("lastModified",  lastModified, this.lastModified);
			assertEquals("sc",            Integer.MIN_VALUE, this.status);
			assertEquals("charset",       null, this.charset);
			assertEquals("contentType",   null, this.contentType);
			assertEquals("content",       "responseBody", this.outString());
			assertEquals("contentLength", 10011, this.contentLength);
			assertEquals("cacheControl",  maxAge(), this.cacheControl);
			assertEquals("accessControlAllowOrigin", null, this.accessControlAllowOrigin);
			assertEquals("flushBuffer",   0, this.flushBufferCount);
		}

		void assertNotModified(final long lastModified)
		{
			assertEquals("location",      null, this.location);
			assertEquals("lastModified",  lastModified, this.lastModified);
			assertEquals("sc",            SC_NOT_MODIFIED, this.status);
			assertEquals("charset",       null, this.charset);
			assertEquals("contentType",   null, this.contentType);
			assertEquals("content",       null, this.outString());
			assertEquals("contentLength", Integer.MIN_VALUE, this.contentLength);
			assertEquals("cacheControl",  maxAge(), this.cacheControl);
			assertEquals("accessControlAllowOrigin", null, this.accessControlAllowOrigin);
			assertEquals("flushBuffer",   1, this.flushBufferCount);
		}

		void assertOkAndCacheControl(final String value)
		{
			assertEquals("location",      null, this.location);
			assertEquals("sc",            Integer.MIN_VALUE, this.status);
			assertEquals("charset",       null, this.charset);
			assertEquals("contentType",   null, this.contentType);
			assertEquals("content",       "responseBody", this.outString());
			assertEquals("contentLength", 10011, this.contentLength);
			assertEquals("cacheControl",  value, this.cacheControl);
			assertEquals("accessControlAllowOrigin", null, this.accessControlAllowOrigin);
			assertEquals("flushBuffer",   0, this.flushBufferCount);
		}

		void assertOkAndAccessControlAllowOrigin(final String value)
		{
			assertEquals("location",      null, this.location);
			assertEquals("lastModified",  Long.MIN_VALUE, this.lastModified);
			assertEquals("sc",            Integer.MIN_VALUE, this.status);
			assertEquals("charset",       null, this.charset);
			assertEquals("contentType",   null, this.contentType);
			assertEquals("content",       "responseBody", this.outString());
			assertEquals("contentLength", 10011, this.contentLength);
			assertEquals("cacheControl",  null, this.cacheControl);
			assertEquals("accessControlAllowOrigin", value, this.accessControlAllowOrigin);
			assertEquals("flushBuffer",   0, this.flushBufferCount);
		}

		void assertError(
				final int sc,
				final String charset,
				final String contentType,
				final String content)
		{
			assertEquals("location",      null,             this.location);
			assertEquals("lastModified",  Long.MIN_VALUE,   this.lastModified);
			assertEquals("sc",            sc,               this.status);
			assertEquals("charset",       charset,          this.charset);
			assertEquals("contentType",   contentType,      this.contentType);
			assertEquals("content",       content,          this.outString());
			assertEquals("contentLength", content.length(), this.contentLength);
			assertEquals("cacheControl",  null,             this.cacheControl);
			assertEquals("accessControlAllowOrigin", null, this.accessControlAllowOrigin);
			assertEquals("flushBuffer",   0, this.flushBufferCount);
		}

		void assertRedirect(final String location)
		{
			assertEquals("location",      location, this.location);
			assertEquals("lastModified",  Long.MIN_VALUE, this.lastModified);
			assertEquals("sc",            SC_MOVED_PERMANENTLY, this.status);
			assertEquals("charset",       null, this.charset);
			assertEquals("contentType",   null, this.contentType);
			assertEquals("content",       null, this.outString());
			assertEquals("contentLength", Integer.MIN_VALUE, this.contentLength);
			assertEquals("cacheControl",  null, this.cacheControl);
			assertEquals("accessControlAllowOrigin", null, this.accessControlAllowOrigin);
			assertEquals("flushBuffer",   0, this.flushBufferCount);
		}

		Response assertLastModified(final long lastModified)
		{
			assertEquals("lastModified", lastModified, this.lastModified);
			return this;
		}
	}

	private static final class MyMediaServlet extends MediaServlet
	{
		@SuppressFBWarnings("MSF_MUTABLE_SERVLET_FIELD")
		boolean failOnException = true;

		MyMediaServlet()
		{
			// make package private
		}

		@Override
		protected boolean isAccessControlAllowOriginWildcard(
				final MediaPath path,
				final Item item)
		{
			assertTrue(MODEL.hasCurrentTransaction());
			return ((MediaPathItem)item).getAccessControlAllowOriginWildcard();
		}

		@Override
		protected boolean doFlushBufferOnNotModified(
				final MediaPath path,
				final Item item)
		{
			assertFalse(MODEL.hasCurrentTransaction());
			return super.doFlushBufferOnNotModified(path, item);
		}

		@Override
		protected void onException(
				final HttpServletRequest request,
				final Exception exception)
		{
			assertFalse(MODEL.hasCurrentTransaction());
			if(failOnException)
				throw new RuntimeException(exception);
		}
		private static final long serialVersionUID = 1L;
	}
}
