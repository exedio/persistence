/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.copernica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

public class MediaServletTest extends TestCase
{
	private static final String NO_SUCH_PATH   = "no such path";
	private static final String NOT_AN_ITEM    = "not an item";
	private static final String NO_SUCH_ITEM   = "no such item";
	private static final String IS_NULL        = "is null";
	private static final String NOT_COMPUTABLE = "not computable";
	
	private static final String ITEM_TXT = "MediaServletItem-0";
	private static final String ITEM_EMP = "MediaServletItem-1";
	private static final String ITEM_PNG = "MediaServletItem-2";
	private static final String ITEM_JPG = "MediaServletItem-3";
	private static final String ITEM_UNK = "MediaServletItem-4";
	private static final String ITEM_GIF = "MediaServletItem-8";
	private static final String ITEM_NX  = "MediaServletItem-20";
	private static final String ITEM_TEXT_FILTER = "MediaServletItem-13";
	private static final String ITEM_NAME_OK  = "MediaServletItem-5";
	private static final String ITEM_NAME_NUL = "MediaServletItem-6";
	private static final String ITEM_NAME_ERR = "MediaServletItem-7";

	public void testIt() throws Exception
	{
		final String app = "http://localhost:" + System.getProperty("tomcat.port.http") + "/cope-runtime-servlet/";
		final URL init = new URL(app + "init");
		init.getContent();
		
		final String prefix = app + "media/MediaServletItem/";

		final long lmTxt = assertTxt(prefix + "content/" + ITEM_TXT + ".txt");
		final long lmPng = assertBin(prefix + "content/" + ITEM_PNG + ".png", "image/png" );
		final long lmJpg = assertBin(prefix + "content/" + ITEM_JPG + ".jpg", "image/jpeg");
		final long lmGif = assertBin(prefix + "content/" + ITEM_GIF + ".gif", "image/gif" );
		final long lmUnk = assertTxt(prefix + "content/" + ITEM_UNK         , "unknownma/unknownmi");
		
		assertEquals(lmTxt, assertTxt(prefix + "content/" + ITEM_TXT + ".txt"));
		assertMoved(prefix + "content/" + ITEM_TXT + ".jpg" , prefix + "content/" + ITEM_TXT + ".txt");
		assertMoved(prefix + "content/" + ITEM_TXT + ".zick", prefix + "content/" + ITEM_TXT + ".txt"); // TODO should be 404
		assertMoved(prefix + "content/" + ITEM_TXT + "."    , prefix + "content/" + ITEM_TXT + ".txt"); // TODO should be 404
		assertMoved(prefix + "content/" + ITEM_TXT          , prefix + "content/" + ITEM_TXT + ".txt");
		assertEquals(lmTxt, assertTxt(prefix + "content/" + ITEM_TXT + "/zick.txt" ));
		assertEquals(lmTxt, assertTxt(prefix + "content/" + ITEM_TXT + "/zick.jpg" ));
		assertEquals(lmTxt, assertTxt(prefix + "content/" + ITEM_TXT + "/zick.zack"));
		assertEquals(lmTxt, assertTxt(prefix + "content/" + ITEM_TXT + "/zick."    ));
		assertEquals(lmTxt, assertTxt(prefix + "content/" + ITEM_TXT + "/zick"     ));
		assertEquals(lmTxt, assertTxt(prefix + "content/" + ITEM_TXT + "/."        ));
		assertEquals(lmTxt, assertTxt(prefix + "content/" + ITEM_TXT + "/"         ));
		assertNotFound(app + "media/MeDiaServletItem/content/" + ITEM_TXT + "/", NO_SUCH_PATH);
		assertNotFound(app + "media/MediaServletItem/conTent/" + ITEM_TXT + "/", NO_SUCH_PATH);
		assertNotFound(app + "media//content/" + ITEM_TXT + "/", NO_SUCH_PATH);
		assertNotFound(app + "media/MediaServletItem//" + ITEM_TXT + "/", NO_SUCH_PATH);
		assertNotFound(app + "media///" + ITEM_TXT + "/", NO_SUCH_PATH);
		assertNotFound(app + "media////", NO_SUCH_PATH);
		assertNotFound(app + "media///" , NO_SUCH_PATH);
		assertNotFound(app + "media//"  , NO_SUCH_PATH);
		assertNotFound(app + "media/"   , NO_SUCH_PATH);
		assertNotFound(app + "media"    , NO_SUCH_PATH);
		assertNotFound(app + "media/MediaServletItem/content", NO_SUCH_PATH);
		assertNotFound(app + "media/MediaServletItem/c"      , NO_SUCH_PATH);
		assertNotFound(app + "media/MediaServletItem/"       , NO_SUCH_PATH);
		assertNotFound(app + "media/MediaServletItem"        , NO_SUCH_PATH);
		assertNotFound(app + "media/M"                       , NO_SUCH_PATH);
		assertNotFound(prefix + "c", NO_SUCH_PATH);
		assertNotFound(app + "media/MediaServletItem/c", NO_SUCH_PATH);
		assertNotFound(app + "media/MediaServletItem/" , NO_SUCH_PATH);
		assertNotFound(app + "media////", NO_SUCH_PATH);
		assertNotFound(app + "media///" , NO_SUCH_PATH);
		assertNotFound(app + "media//"  , NO_SUCH_PATH);
		assertNotFound(app + "media/"   , NO_SUCH_PATH);
		assertNotFound(app + "media"    , NO_SUCH_PATH);
		assertNotFound(app + "media/dingdangdong/////", NO_SUCH_PATH);
		assertNotFound(app + "media/dingdangdong////" , NO_SUCH_PATH);
		assertNotFound(app + "media/dingdangdong///"  , NO_SUCH_PATH);
		assertNotFound(app + "media/dingdangdong//"   , NO_SUCH_PATH);
		assertNotFound(prefix + "content/" + ITEM_NX + ".txt" , NO_SUCH_ITEM);
		assertNotFound(prefix + "content/" + ITEM_NX + ".zick", NO_SUCH_ITEM);
		assertNotFound(prefix + "content/" + ITEM_NX + "."    , NO_SUCH_ITEM);
		assertNotFound(prefix + "content/" + ITEM_NX          , NO_SUCH_ITEM);
		assertNotFound(prefix + "content/MediaServletItem.", NOT_AN_ITEM);
		assertNotFound(prefix + "content/MediaServletItem" , NOT_AN_ITEM);
		assertNotFound(prefix + "content/MediaZack", NOT_AN_ITEM);
		assertNotFound(prefix + "content/", NOT_AN_ITEM);
		assertNotFound(prefix + "content" , NO_SUCH_PATH);
		assertNotFound(prefix + "content/zapp", NOT_AN_ITEM);
		assertNotFound(prefix + "content/" + ITEM_EMP + ".jpg" , IS_NULL);
		assertNotFound(prefix + "content/" + ITEM_EMP + "."    , IS_NULL);
		assertNotFound(prefix + "content/" + ITEM_EMP          , IS_NULL);
		assertNotFound(prefix + "content/" + ITEM_EMP + ".zick", IS_NULL);
		assertNotFound(prefix + "content/" + ITEM_TXT + ".txt?x=y", NOT_AN_ITEM);
		
		assertTxt     (app + "media/MediaPatternItem/pattern-sourceFeature/MediaPatternItem-0.txt", "text/plain");
		assertNotFound(app + "media/MediaPatternItem/pattern-sourceFeature/MediaPatternItem-1.txt", NO_SUCH_ITEM);
		assertTxt     (app + "media/MediaPatternItem-pattern/value/MediaPatternItem-pattern-0.txt", "text/plain");
		assertTxt     (app + "media/MediaPatternItem-pattern/value/MediaPatternItem-pattern-1.txt", "text/plain");
		assertNotFound(app + "media/MediaPatternItem-pattern/value/MediaPatternItem-pattern-2.txt", NO_SUCH_ITEM);
		
		assertEquals(lmTxt, assertTxt(prefix + "content/" + ITEM_TXT + ".txt", lmTxt-1   , false));
		assertEquals(lmTxt, assertTxt(prefix + "content/" + ITEM_TXT + ".txt", lmTxt     , true ));
		assertEquals(lmTxt, assertTxt(prefix + "content/" + ITEM_TXT + ".txt", lmTxt+5000, true ));

		assertMoved(prefix + "content/" + ITEM_UNK + ".unknownma.unknownmi", prefix + "content/" + ITEM_UNK); // TODO should be 404
		assertMoved(prefix + "content/" + ITEM_UNK + ".jpg"                , prefix + "content/" + ITEM_UNK);
		assertMoved(prefix + "content/" + ITEM_UNK + "."                   , prefix + "content/" + ITEM_UNK); // TODO should be 404
		assertEquals(lmUnk, assertTxt(prefix + "content/" + ITEM_UNK                              , "unknownma/unknownmi"));
		assertEquals(lmUnk, assertTxt(prefix + "content/" + ITEM_UNK + "/zick.unknownma.unknownmi", "unknownma/unknownmi"));
		assertEquals(lmUnk, assertTxt(prefix + "content/" + ITEM_UNK + "/zick.jpg"                , "unknownma/unknownmi"));
		assertEquals(lmUnk, assertTxt(prefix + "content/" + ITEM_UNK + "/zick."                   , "unknownma/unknownmi"));
		assertEquals(lmUnk, assertTxt(prefix + "content/" + ITEM_UNK + "/zick"                    , "unknownma/unknownmi"));

		assertMoved(app + "media/MediaServletItemAlt1/content/"     + ITEM_JPG + ".jpg", prefix + "content/"  + ITEM_JPG + ".jpg");
		assertMoved(app + "media/MediaServletItemAlt2/content/"     + ITEM_JPG + ".jpg", prefix + "content/"  + ITEM_JPG + ".jpg");
		assertMoved(app + "media/MediaServletItem/contentAlt1/"     + ITEM_JPG + ".jpg", prefix + "content/"  + ITEM_JPG + ".jpg");
		assertMoved(app + "media/MediaServletItem/contentAlt2/"     + ITEM_JPG + ".jpg", prefix + "content/"  + ITEM_JPG + ".jpg");
		assertMoved(app + "media/MediaServletItemAlt1/contentAlt1/" + ITEM_JPG + ".jpg", prefix + "content/"  + ITEM_JPG + ".jpg");
		assertMoved(app + "media/MediaServletItemAlt2/contentAlt2/" + ITEM_JPG + ".jpg", prefix + "content/"  + ITEM_JPG + ".jpg");
		assertMoved(app + "media/MediaServletItemAlt1/contentAlt2/" + ITEM_JPG + ".jpg", prefix + "content/"  + ITEM_JPG + ".jpg");
		assertMoved(app + "media/MediaServletItemAlt2/contentAlt1/" + ITEM_JPG + ".jpg", prefix + "content/"  + ITEM_JPG + ".jpg");

		assertMoved(prefix + "redirect/" + ITEM_JPG + ".jpg", prefix + "content/"  + ITEM_JPG + ".jpg");
		assertMoved(prefix + "redirect/" + ITEM_JPG + "."   , prefix + "redirect/" + ITEM_JPG + ".jpg");
		assertMoved(prefix + "redirect/" + ITEM_JPG         , prefix + "redirect/" + ITEM_JPG + ".jpg");
		
		assertNotFound(prefix + "thumbnail/" + ITEM_TXT, NOT_COMPUTABLE);
		assertNotFound(prefix + "thumbnail/" + ITEM_EMP, IS_NULL);
		assertEquals(lmPng, assertBin(prefix + "thumbnail/" + ITEM_PNG + ".jpg", "image/jpeg"));
		assertEquals(lmJpg, assertBin(prefix + "thumbnail/" + ITEM_JPG + ".jpg", "image/jpeg"));
		assertEquals(lmGif, assertBin(prefix + "thumbnail/" + ITEM_GIF + ".jpg", "image/jpeg"));
		
		assertNotFound(prefix + "thumbnailMagick/" + ITEM_TXT, NOT_COMPUTABLE);
		assertNotFound(prefix + "thumbnailMagick/" + ITEM_EMP, IS_NULL);
		assertEquals(lmPng, assertBin(prefix + "thumbnailMagick/" + ITEM_PNG + ".jpg", "image/jpeg"));
		assertEquals(lmJpg, assertBin(prefix + "thumbnailMagick/" + ITEM_JPG + ".jpg", "image/jpeg"));
		assertEquals(lmGif, assertBin(prefix + "thumbnailMagick/" + ITEM_GIF + ".jpg", "image/jpeg"));
		
		assertNotFound(prefix + "thumbnailMagickPng/" + ITEM_TXT, NOT_COMPUTABLE);
		assertNotFound(prefix + "thumbnailMagickPng/" + ITEM_EMP, IS_NULL);
		assertEquals(lmPng, assertBin(prefix + "thumbnailMagickPng/" + ITEM_PNG + ".png", "image/png"));
		assertEquals(lmJpg, assertBin(prefix + "thumbnailMagickPng/" + ITEM_JPG + ".png", "image/png"));
		assertEquals(lmGif, assertBin(prefix + "thumbnailMagickPng/" + ITEM_GIF + ".png", "image/png"));
		
		assertNotFound(prefix + "html/" + ITEM_TXT, IS_NULL);
		assertNotFound(prefix + "html/" + ITEM_PNG, IS_NULL);
		assertNotFound(prefix + "html/" + ITEM_EMP, IS_NULL);
		final long lmFilter = assertBin(prefix + "content/" + ITEM_TEXT_FILTER + ".html", "text/html" );
		assertEquals(lmFilter, assertBin(prefix + "html/" + ITEM_TEXT_FILTER + ".html", "text/html"));
		assertMoved(                     prefix + "html/" + ITEM_TEXT_FILTER + ".htm" , prefix + "html/" + ITEM_TEXT_FILTER + ".html");
		
		assertNotFound(prefix + "content/schnickschnack", NOT_AN_ITEM);
		assertNotFound(prefix + "content/" + ITEM_NX + ".jpg", NO_SUCH_ITEM);
		assertNotFound(prefix + "content/" + ITEM_NX + "."   , NO_SUCH_ITEM);
		assertNotFound(prefix + "content/" + ITEM_NX         , NO_SUCH_ITEM);
		assertNotFound(prefix + "content/MediaServletItem.", NOT_AN_ITEM);
		assertNotFound(prefix + "content/MediaServletItem" , NOT_AN_ITEM);
		assertNotFound(prefix + "content/" + ITEM_NX + "/dingens.jpg", NO_SUCH_ITEM);
		assertNotFound(prefix + "content/" + ITEM_NX + "/."          , NO_SUCH_ITEM);
		assertNotFound(prefix + "content/" + ITEM_NX + "/"           , NO_SUCH_ITEM);
		assertNotFound(prefix + "content/", NOT_AN_ITEM);
		assertNotFound(prefix + "content", NO_SUCH_PATH);
		assertNotFound(prefix + "content/" + ITEM_EMP + ".jpg" , IS_NULL);
		assertNotFound(prefix + "content/" + ITEM_EMP + "."    , IS_NULL);
		assertNotFound(prefix + "content/" + ITEM_EMP          , IS_NULL);
		assertNotFound(prefix + "content/" + ITEM_EMP + ".zick", IS_NULL);

		assertNameURL(prefix + "nameServer/" + ITEM_NAME_OK + ".txt");
		assertMoved(prefix + "nameServer/" + ITEM_NAME_OK + ".", prefix + "nameServer/" + ITEM_NAME_OK + ".txt"); // TODO should be 404
		assertMoved(prefix + "nameServer/" + ITEM_NAME_OK , prefix + "nameServer/" + ITEM_NAME_OK + ".txt");
		assertNameURL(prefix + "nameServer/" + ITEM_NAME_OK + "/something.txt");
		assertNameURL(prefix + "nameServer/" + ITEM_NAME_OK + "/.");
		assertNameURL(prefix + "nameServer/" + ITEM_NAME_OK + "/");
		assertNotFound(prefix + "nameServer/" + ITEM_NAME_NUL + ".txt", IS_NULL);
		assertNotFound(prefix + "nameServer/" + ITEM_NAME_NUL + ".", IS_NULL);
		assertNotFound(prefix + "nameServer/" + ITEM_NAME_NUL, IS_NULL);
		assertNotFound(prefix + "nameServer/" + ITEM_NAME_NUL + "/bla.txt", IS_NULL);
		assertNotFound(prefix + "nameServer/" + ITEM_NAME_NUL + "/.", IS_NULL);
		assertNotFound(prefix + "nameServer/" + ITEM_NAME_NUL + "/", IS_NULL);
		assertNotFound(prefix + "nameServer/" + ITEM_NX + ".txt", NO_SUCH_ITEM);
		assertNotFound(prefix + "nameServer/" + ITEM_NX + ".", NO_SUCH_ITEM);
		assertNotFound(prefix + "nameServer/" + ITEM_NX, NO_SUCH_ITEM);
		assertNotFound(prefix + "nameServer/MediaServletItem.", NOT_AN_ITEM);
		assertNotFound(prefix + "nameServer/MediaServletItem", NOT_AN_ITEM);
		assertNotFound(prefix + "nameServer/", NOT_AN_ITEM);
		assertNotFound(prefix + "nameServer", NO_SUCH_PATH);

		assertInternalError(prefix + "nameServer/" + ITEM_NAME_ERR + ".txt");
		assertMoved(prefix + "nameServer/" + ITEM_NAME_ERR + ".", prefix + "nameServer/" + ITEM_NAME_ERR + ".txt"); // TODO should be 404
		assertMoved(prefix + "nameServer/" + ITEM_NAME_ERR      , prefix + "nameServer/" + ITEM_NAME_ERR + ".txt");
	}
	
	private long assertTxt(final String url) throws IOException
	{
		return assertTxt(url, -1, false);
	}
	
	private long assertTxt(final String url, final String contentType) throws IOException
	{
		return assertTxt(url, contentType, -1, false);
	}
	
	private long assertTxt(final String url, final long ifModifiedSince, final boolean expectNotModified) throws IOException
	{
		return assertTxt(url, "text/plain", ifModifiedSince, expectNotModified);
	}

	private long assertTxt(final String url, final String contentType, final long ifModifiedSince, final boolean expectNotModified) throws IOException
	{
		final Date before = new Date();
		final HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
		conn.setFollowRedirects(false);
		if(ifModifiedSince>=0)
			conn.setIfModifiedSince(ifModifiedSince);
		conn.connect();
		assertEquals(expectNotModified ? conn.HTTP_NOT_MODIFIED : conn.HTTP_OK, conn.getResponseCode());
		assertEquals(expectNotModified ? "Not Modified" : "OK", conn.getResponseMessage());
		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithinHttpDate(before, after, new Date(date));
		final long lastModified = conn.getLastModified();
		//System.out.println("LastModified: "+new Date(lastModified));
		assertTrue((date+1000)>=lastModified);
		assertEquals(expectNotModified ? null : contentType, conn.getContentType());
		//System.out.println("Expires: "+new Date(textConn.getExpiration()));
		assertWithin(new Date(date+4000), new Date(date+6000), new Date(conn.getExpiration()));
		final String data = lines(
			"This is an example file",
			"for testing media data."
		);
		assertEquals(expectNotModified ? -1 : data.length(), conn.getContentLength());

		if ( expectNotModified )
		{
			assertEquals( -1, conn.getInputStream().read() );
		}
		else
		{
			assertEquals( data, getContentAsString(conn.getInputStream()) );
		}
		
		//textConn.setIfModifiedSince();
		return lastModified;
	}

	private String getContentAsString( InputStream is ) throws IOException
	{
		final BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder builder = new StringBuilder();
		String s;
		while ( (s=br.readLine())!=null )
		{
			builder.append( s + System.getProperty("line.separator") );
		}
		br.close();
		return builder.toString();
	}

	private void assertMoved(final String url, final String target) throws IOException
	{
		final Date before = new Date();
		final HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
		conn.setFollowRedirects(false);
		conn.connect();
		assertEquals(conn.HTTP_MOVED_PERM, conn.getResponseCode());
		assertEquals("Moved Permanently", conn.getResponseMessage());
		assertEquals(target, conn.getHeaderField("Location"));
		assertEquals(null, conn.getContentType());
		assertEquals(0, conn.getContentLength());
		final InputStream is = conn.getInputStream();
		assertEquals(-1, is.read());
		is.close();
		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithinHttpDate(before, after, new Date(date));
	}

	private void assertNotFound(final String url, final String detail) throws IOException
	{
		final Date before = new Date();
		final HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
		conn.setFollowRedirects(false);
		conn.connect();
		if(conn.HTTP_NOT_FOUND!=conn.getResponseCode())
			print(conn, url);
		assertEquals(conn.HTTP_NOT_FOUND, conn.getResponseCode());
		assertEquals("Not Found", conn.getResponseMessage());
		assertEquals("text/html", conn.getContentType());
		
		final BufferedReader is = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		assertEquals("<html>", is.readLine());
		assertEquals("<head>", is.readLine());
		assertEquals("<title>Not Found</title>", is.readLine());
		assertEquals("<meta name=\"generator\" content=\"cope media servlet\">", is.readLine());
		assertEquals("</head>", is.readLine());
		assertEquals("<body>", is.readLine());
		assertEquals("<h1>Not Found</h1>", is.readLine());
		assertEquals("The requested URL was not found on this server ("+detail+").", is.readLine());
		assertEquals("</body>", is.readLine());
		assertEquals("</html>", is.readLine());
		assertEquals(null, is.readLine());
		is.close();

		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithinHttpDate(before, after, new Date(date));
	}

	private long assertBin(final String url, final String contentType) throws IOException
	{
		final Date before = new Date();
		final HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
		conn.setFollowRedirects(false);
		conn.connect();
		assertEquals("url="+url.toString(), conn.HTTP_OK, conn.getResponseCode());
		assertEquals("OK", conn.getResponseMessage());
		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithinHttpDate(before, after, new Date(date));
		final long lastModified = conn.getLastModified();
		//System.out.println("LastModified: "+new Date(lastModified));
		assertTrue((date+1000)>=lastModified);
		if(!contentType.equals(conn.getContentType()))
			print(conn, url);
		assertEquals(contentType, conn.getContentType());
		//System.out.println("Expires: "+new Date(textConn.getExpiration()));
		assertWithin(new Date(date+3000), new Date(date+6000), new Date(conn.getExpiration()));
		
		return lastModified;
	}

	private void assertInternalError(final String url) throws IOException
	{
		final Date before = new Date();
		final HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
		conn.setFollowRedirects(false);
		conn.connect();
		if(conn.HTTP_INTERNAL_ERROR!=conn.getResponseCode())
			print(conn, url);
		assertEquals(conn.HTTP_INTERNAL_ERROR, conn.getResponseCode());
		assertEquals("Internal Server Error", conn.getResponseMessage());
		assertEquals("text/html", conn.getContentType());
		
		final BufferedReader is = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		assertEquals("<html>", is.readLine());
		assertEquals("<head>", is.readLine());
		assertEquals("<title>Internal Server Error</title>", is.readLine());
		assertEquals("<meta name=\"generator\" content=\"cope media servlet\">", is.readLine());
		assertEquals("</head>", is.readLine());
		assertEquals("<body>", is.readLine());
		assertEquals("<h1>Internal Server Error</h1>", is.readLine());
		assertEquals("An internal error occured on the server.", is.readLine());
		assertEquals("</body>", is.readLine());
		assertEquals("</html>", is.readLine());
		assertEquals(null, is.readLine());
		is.close();

		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithinHttpDate(before, after, new Date(date));
	}

	private void assertNameURL(final String url) throws IOException
	{
		final HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
		conn.setFollowRedirects(false);
		conn.connect();
		assertEquals(200, conn.getResponseCode());
		assertEquals("text/plain", conn.getContentType());
		assertEquals(12, conn.getContentLength());
		
		final BufferedReader is = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		assertEquals("media item 1", is.readLine());
		assertEquals(null, is.readLine());
		is.close();
	}

	private void print(final HttpURLConnection conn, final String url) throws IOException
	{
		System.out.println("--------------------------------");
		System.out.println("url="+url);
		System.out.println("responseCode="+conn.getResponseCode());
		System.out.println("responseMessage="+conn.getResponseMessage());
		System.out.println("contentType="+conn.getContentType());
		System.out.println("contentLength="+conn.getContentLength());
		System.out.println("content");
		print(conn.getInputStream());
		System.out.println("error");
		print(conn.getErrorStream());
		System.out.println("--------------------------------");
	}

	private void print(final InputStream in) throws IOException
	{
		if(in==null)
		{
			System.out.println("-----------leer---------------");
			return;
		}
		else
		{
			final byte[] b = new byte[20000];
			final File f = File.createTempFile("MediaServletTest-", ".tmp");
			System.out.println("----------- " + f.getAbsolutePath() + " ---------------");
			final OutputStream out = new FileOutputStream(f);
			for(int len = in.read(b); len>=0; len = in.read(b))
				out.write(b, 0, len);
	
			out.close();
			in.close();
		}
	}
	
	// ----------------------------------- adapted from CopeAssert
	
	private static final String DATE_FORMAT_FULL = "dd.MM.yyyy HH:mm:ss.SSS";

	public final static void assertWithinHttpDate(final Date expectedBefore, final Date expectedAfter, final Date actual)
	{
		final long resolution = 1000;
		final long leftTolerance = 995;
		final Date expectedBeforeFloor = new Date(((expectedBefore.getTime()-leftTolerance) / resolution) * resolution);
		final Date expectedAfterCeil   = new Date(((expectedAfter.getTime() / resolution) * resolution) + resolution);

		final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL);
		final String message =
			"expected date within " + df.format(expectedBeforeFloor) + " (" + df.format(expectedBefore) + ")" +
			" and " + df.format(expectedAfterCeil) + " (" + df.format(expectedAfter) + ")" +
			", but was " + df.format(actual);

		assertTrue(message, !expectedBeforeFloor.after(actual));
		assertTrue(message, !expectedAfterCeil.before(actual));
	}

	public final static void assertWithin(final Date expectedBefore, final Date expectedAfter, final Date actual)
	{
		final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL);
		final String message =
			"expected date within " + df.format(expectedBefore) +
			" and " + df.format(expectedAfter) +
			", but was " + df.format(actual);

		assertTrue(message, !expectedBefore.after(actual));
		assertTrue(message, !expectedAfter.before(actual));
	}

	private String lines( String... lines )
	{
		StringBuilder builder = new StringBuilder();
		for ( String line: lines )
		{
			builder.append( line );
			builder.append( '\n' );
		}
		return builder.toString();
	}
}
