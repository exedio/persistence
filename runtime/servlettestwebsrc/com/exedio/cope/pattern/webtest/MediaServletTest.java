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

package com.exedio.cope.pattern.webtest;

import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static java.io.File.createTempFile;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NOT_MODIFIED;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.util.StrictFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MediaServletTest
{
	private static final String NO_SUCH_PATH   = "no such path";
	private static final String GUESSED_URL    = "guessed url";
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
	private static final String ITEM_TEXT_CATCH = "MediaServletItem-14";
	private static final String ITEM_NAME_OK  = "MediaServletItem-5";
	private static final String ITEM_NAME_NUL = "MediaServletItem-6";
	private static final String ITEM_NAME_ERR = "MediaServletItem-7";
	private static final String ITEM_NAME_ERR_LM = "MediaServletItem-15";

	private static final String CACHE_CONTROL = "Cache-Control";
	private static final String EXPIRES = "Expires";

	private static final File onException = new File("tomcat/bin/MediaTestServlet.log");

	private String schemeAndHost;

	@BeforeEach final void setUp()
	{
		schemeAndHost = "http://localhost:" + System.getProperty("tomcat.port.http");
		//noinspection ResultOfMethodCallIgnored OK if does not yet exists
		onException.delete();
	}

	@Test void testIt() throws Exception
	{
		final String app = "/cope-runtime-servlet/";
		final URL init = new URL(schemeAndHost + app + "init");
		init.getContent();

		final String prefix = app + "media/MediaServletItem/";
		final String itemTxt      = prefix + "content/"    + ITEM_TXT + ".txt";
		final String itemTxtCatch = prefix + "content/"    + ITEM_TEXT_CATCH + "/zick.txt";
		final String itemUnkown   = prefix + "content/"    + ITEM_UNK;
		final String itemName     = prefix + "nameServer/" + ITEM_NAME_OK + ".txt";

		assertTxt(itemTxt, hour8(0));
		assertBin(prefix + "content/" + ITEM_PNG + ".png", "image/png" , hour8(2));
		assertBin(prefix + "content/" + ITEM_JPG + ".jpg", "image/jpeg", hour8(3));
		assertBin(prefix + "content/" + ITEM_GIF + ".gif", "image/gif" , hour8(8));
		assertTxt(prefix + "content/" + ITEM_UNK         , "unknownma/unknownmi", hour8(4));

		assertTxt(itemTxt, hour8(0));
		assertMoved(prefix + "content/" + ITEM_TXT + ".jpg"      , itemTxt);
		assertMoved(prefix + "content/" + ITEM_TXT + ".zick"     , itemTxt); // TODO should be 404
		assertMoved(prefix + "content/" + ITEM_TXT + "."         , itemTxt); // TODO should be 404
		assertMoved(prefix + "content/" + ITEM_TXT               , itemTxt);
		assertMoved(prefix + "content/" + ITEM_TXT + "/zick.txt" , itemTxt);
		assertMoved(prefix + "content/" + ITEM_TXT + "/zack.txt" , itemTxt);
		assertMoved(prefix + "content/" + ITEM_TXT + "/zick.jpg" , itemTxt);
		assertMoved(prefix + "content/" + ITEM_TXT + "/zick.zack", itemTxt);
		assertMoved(prefix + "content/" + ITEM_TXT + "/zick."    , itemTxt);
		assertMoved(prefix + "content/" + ITEM_TXT + "/zick"     , itemTxt);
		assertMoved(prefix + "content/" + ITEM_TXT + "/."        , itemTxt);
		assertMoved(prefix + "content/" + ITEM_TXT + "/"         , itemTxt);

		assertMoved(prefix + "content/" + ITEM_TEXT_CATCH + ".txt" , itemTxtCatch);
		assertMoved(prefix + "content/" + ITEM_TEXT_CATCH + ".jpg" , itemTxtCatch);
		assertMoved(prefix + "content/" + ITEM_TEXT_CATCH + ".zick", itemTxtCatch); // TODO should be 404
		assertMoved(prefix + "content/" + ITEM_TEXT_CATCH + "."    , itemTxtCatch); // TODO should be 404
		assertMoved(prefix + "content/" + ITEM_TEXT_CATCH          , itemTxtCatch);
		assertTxt(itemTxtCatch, hour8(14));
		assertMoved(prefix + "content/" + ITEM_TEXT_CATCH + "/zack.txt" , itemTxtCatch);
		assertMoved(prefix + "content/" + ITEM_TEXT_CATCH + "/zick.jpg" , itemTxtCatch);
		assertMoved(prefix + "content/" + ITEM_TEXT_CATCH + "/zick.zack", itemTxtCatch);
		assertMoved(prefix + "content/" + ITEM_TEXT_CATCH + "/zick."    , itemTxtCatch);
		assertMoved(prefix + "content/" + ITEM_TEXT_CATCH + "/zick"     , itemTxtCatch);
		assertMoved(prefix + "content/" + ITEM_TEXT_CATCH + "/."        , itemTxtCatch);
		assertMoved(prefix + "content/" + ITEM_TEXT_CATCH + "/"         , itemTxtCatch);

		assertTxt     (prefix + "contentLarge/" + ITEM_TXT + ".txt", hour8(0));
		assertNotFound(prefix + "contentLarge/" + ITEM_EMP + ".txt", IS_NULL);

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

		assertTxt     (app + "media/MediaPatternItem/pattern-sourceFeature/MediaPatternItem-0.txt", "text/plain", hour9(10));
		assertNotFound(app + "media/MediaPatternItem/pattern-sourceFeature/MediaPatternItem-1.txt", NO_SUCH_ITEM);
		assertTxt     (app + "media/MediaPatternItem-pattern/value/MediaPatternItem-pattern-0.txt", "text/plain", hour9(20));
		assertTxt     (app + "media/MediaPatternItem-pattern/value/MediaPatternItem-pattern-1.txt", "text/plain", hour9(21));
		assertNotFound(app + "media/MediaPatternItem-pattern/value/MediaPatternItem-pattern-2.txt", NO_SUCH_ITEM);

		assertTxt(prefix + "content/" + ITEM_TXT + ".txt", hour8(0), addMillis(hour8(0),    -1), false);
		assertTxt(prefix + "content/" + ITEM_TXT + ".txt", hour8(0), addMillis(hour8(0),     0), true );
		assertTxt(prefix + "content/" + ITEM_TXT + ".txt", hour8(0), addMillis(hour8(0), +5000), true );

		assertMoved(prefix + "content/" + ITEM_UNK + ".unknownma.unknownmi", itemUnkown); // TODO should be 404
		assertMoved(prefix + "content/" + ITEM_UNK + ".jpg"                , itemUnkown);
		assertMoved(prefix + "content/" + ITEM_UNK + "."                   , itemUnkown); // TODO should be 404
		assertTxt(itemUnkown, "unknownma/unknownmi", hour8(4));
		assertMoved(prefix + "content/" + ITEM_UNK + "/zick.unknownma.unknownmi", itemUnkown);
		assertMoved(prefix + "content/" + ITEM_UNK + "/zick.jpg"                , itemUnkown);
		assertMoved(prefix + "content/" + ITEM_UNK + "/zick."                   , itemUnkown);
		assertMoved(prefix + "content/" + ITEM_UNK + "/zick"                    , itemUnkown);

		assertMoved(app + "media/MediaServletItem/contentAlt1/" + ITEM_JPG + ".jpg", prefix + "content/"  + ITEM_JPG + ".jpg");
		assertMoved(app + "media/MediaServletItem/contentAlt2/" + ITEM_JPG + ".jpg", prefix + "content/"  + ITEM_JPG + ".jpg");
		// test wrong extension
		// TODO redirect directly to .jpg
		assertMoved(app + "media/MediaServletItem/contentAlt1/" + ITEM_JPG + ".png", prefix + "content/"  + ITEM_JPG + ".png");
		assertMoved(app + "media/MediaServletItem/contentAlt1/" + ITEM_JPG + ".x"  , prefix + "content/"  + ITEM_JPG + ".x"  );
		assertMoved(app + "media/MediaServletItem/contentAlt1/" + ITEM_JPG + "."   , prefix + "content/"  + ITEM_JPG + "."   );
		assertMoved(app + "media/MediaServletItem/contentAlt1/" + ITEM_JPG         , prefix + "content/"  + ITEM_JPG         );

		assertNotFound(prefix + "thumbnail/" + ITEM_TXT, NOT_COMPUTABLE, hour8(0));
		assertNotFound(prefix + "thumbnail/" + ITEM_EMP, IS_NULL);
		assertBin(prefix + "thumbnail/" + ITEM_PNG + ".jpg", "image/jpeg", hour8(2));
		assertBin(prefix + "thumbnail/" + ITEM_JPG + ".jpg", "image/jpeg", hour8(3));
		assertBin(prefix + "thumbnail/" + ITEM_GIF + ".jpg", "image/jpeg", hour8(8));

		assertNotFound(prefix + "html/" + ITEM_TXT, NOT_COMPUTABLE, hour8(0));
		assertNotFound(prefix + "html/" + ITEM_PNG, NOT_COMPUTABLE, hour8(2));
		assertNotFound(prefix + "html/" + ITEM_EMP, IS_NULL);
		assertBin  (prefix + "content/" + ITEM_TEXT_FILTER + ".html", "text/html", hour8(13));
		assertBin  (prefix + "html/"    + ITEM_TEXT_FILTER + ".html", "text/html;charset=UTF-8", hour8(13));
		assertMoved(prefix + "html/"    + ITEM_TEXT_FILTER + ".htm" , prefix + "html/" + ITEM_TEXT_FILTER + ".html");

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

		// tokened
		final String TOKEN;
		// to use the token below, enable media.url.secret in
		// runtime/servlettestweb/WEB-INF/cope.properties
		//TOKEN = "74466680090a38495c89";
		TOKEN = "MediaServletItem.tokened-" + ITEM_JPG;
		assertBinPrivate(prefix + "tokened/.t" + TOKEN + "/" + ITEM_JPG +      ".jpg", "image/jpeg", hour8(3));
		assertMoved(prefix + "tokened/.t" + TOKEN + "/" + ITEM_JPG + "/name.jpg", prefix + "tokened/.t" + TOKEN + "/" + ITEM_JPG + ".jpg");

		assertNotFound(prefix + "tokened/"      + ITEM_JPG + ".jpg", GUESSED_URL);
		assertNotFound(prefix + "tokened/.t/"   + ITEM_JPG + ".jpg", GUESSED_URL);
		assertNotFound(prefix + "tokened/.t1/"  + ITEM_JPG + ".jpg", GUESSED_URL);
		assertNotFound(prefix + "tokened/.t12/" + ITEM_JPG + ".jpg", GUESSED_URL);

		assertNotFound(prefix + "tokened/"      + ITEM_JPG + ".png", GUESSED_URL);
		assertNotFound(prefix + "tokened/.t12/" + ITEM_JPG + ".png", GUESSED_URL);
		assertNotFound(prefix + "tokened/"      + ITEM_JPG         , GUESSED_URL);
		assertNotFound(prefix + "tokened/.t12/" + ITEM_JPG         , GUESSED_URL);
		assertMoved(prefix + "tokened/.t" + TOKEN + "/" + ITEM_JPG + ".png",
						prefix + "tokened/.t" + TOKEN + "/" + ITEM_JPG + ".jpg");
		assertNotFound(prefix + "tokened/.t" + TOKEN + "/" + ITEM_JPG + ".jpg?x=y", NOT_AN_ITEM);
		assertNotFound(prefix + "tokened/.t" + TOKEN + "/" + ITEM_JPG + ".jpg?t=y", NOT_AN_ITEM);

		assertNotFound(prefix + "tokened/.t" + TOKEN + "/" + ITEM_TXT + ".jpg", GUESSED_URL);
		assertNotFound(prefix + "tokened/.t" + TOKEN + "/" + ITEM_EMP + ".jpg", GUESSED_URL);
		assertNotFound(prefix + "tokened/.t" + TOKEN + "/" + ITEM_NX  + ".jpg", GUESSED_URL);

		// token on a media without @PreventUrlGuessing causes redirect to canonical url
		assertMoved(prefix + "content/.t" + TOKEN + "/" + ITEM_JPG + ".jpg",
						prefix + "content/"                 + ITEM_JPG + ".jpg");
		assertMoved(prefix + "content/.tzack/"          + ITEM_JPG + ".jpg",
						prefix + "content/"                 + ITEM_JPG + ".jpg");
		assertMoved(prefix + "content/.t/"              + ITEM_JPG + ".jpg",
						prefix + "content/"                 + ITEM_JPG + ".jpg");

		// finger

		final int ALMOST_ONE_YEAR = 31363200;
		assertBin  (prefix + "finger/.fjeCiepS/" + ITEM_PNG + ".jpg", "image/jpeg", hour8(2), "max-age="+ALMOST_ONE_YEAR);
		assertBin  (prefix + "finger/.fjYxvepS/" + ITEM_JPG + ".jpg", "image/jpeg", hour8(3), "max-age="+ALMOST_ONE_YEAR);
		assertMoved(prefix + "finger/.fjYxVepS/" + ITEM_JPG + ".jpg",
						prefix + "finger/.fjYxvepS/" + ITEM_JPG + ".jpg");
		assertMoved(prefix + "finger/"           + ITEM_JPG + ".jpg",
						prefix + "finger/.fjYxvepS/" + ITEM_JPG + ".jpg");

		// tokenedFinger

		final String TOKEN_FINGER;
		//TOKEN_FINGER = "6e436f1b4c764ac049ad";
		TOKEN_FINGER = "MediaServletItem.tokenedFinger-" + ITEM_JPG;
		assertBin     (prefix + "tokenedFinger/.fjYxvepS/.t" + TOKEN_FINGER + "/" + ITEM_JPG +      ".jpg", "image/jpeg", hour8(3), "private,max-age=31363200");
		assertMoved   (prefix + "tokenedFinger/.fjYxvepS/.t" + TOKEN_FINGER + "/" + ITEM_JPG + "/name.jpg", prefix + "tokenedFinger/.fjYxvepS/.t" + TOKEN_FINGER + "/" + ITEM_JPG + ".jpg");
		assertNotFound(prefix + "tokenedFinger/.fjYxvepS/.tx/" + ITEM_JPG +      ".jpg", GUESSED_URL);
		assertNotFound(prefix + "tokenedFinger/.fjYxvepS/.tx/" + ITEM_JPG + "/name.jpg", GUESSED_URL);

		// nameServer

		assertNameURL(prefix + "nameServer/" + ITEM_NAME_OK + ".txt");
		assertMoved(prefix + "nameServer/" + ITEM_NAME_OK + ".", prefix + "nameServer/" + ITEM_NAME_OK + ".txt"); // TODO should be 404
		assertMoved(prefix + "nameServer/" + ITEM_NAME_OK , prefix + "nameServer/" + ITEM_NAME_OK + ".txt");
		assertMoved(prefix + "nameServer/" + ITEM_NAME_OK + "/something.txt", itemName);
		assertMoved(prefix + "nameServer/" + ITEM_NAME_OK + "/.", itemName);
		assertMoved(prefix + "nameServer/" + ITEM_NAME_OK + "/", itemName);
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
		assertInternalError(prefix + "nameServer/" + ITEM_NAME_ERR_LM + ".txt");
	}

	private void assertTxt(final String url, final Date lastModified) throws IOException
	{
		assertTxt(url, lastModified, -1, false);
	}

	private void assertTxt(final String url, final String contentType, final Date lastModified) throws IOException
	{
		assertTxt(url, contentType, lastModified, -1, false);
	}

	private void assertTxt(
			final String url,
			final Date lastModified,
			final long ifModifiedSince,
			final boolean expectNotModified)
		throws IOException
	{
		assertTxt(url, "text/plain", lastModified, ifModifiedSince, expectNotModified);
	}

	private void assertTxt(
			final String url,
			final String contentType,
			final Date lastModified,
			final long ifModifiedSince,
			final boolean expectNotModified)
		throws IOException
	{
		final Date before = new Date();
		final HttpURLConnection conn = (HttpURLConnection)new URL(schemeAndHost + url).openConnection();
		HttpURLConnection.setFollowRedirects(false);
		if(ifModifiedSince>=0)
			conn.setIfModifiedSince(ifModifiedSince);
		conn.connect();
		assertEquals(expectNotModified ? HTTP_NOT_MODIFIED : HTTP_OK, conn.getResponseCode());
		assertEquals(null, conn.getResponseMessage()); // is null since tomcat 8.5
		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithinHttpDate(before, after, new Date(date));
		final long actualLastModified = conn.getLastModified();
		//System.out.println("LastModified: "+new Date(actualLastModified));
		assertEqualsDate(lastModified, new Date(actualLastModified));
		assertEquals(expectNotModified ? null : contentType, conn.getContentType());
		assertEquals(null, conn.getHeaderField(EXPIRES));
		assertEquals("max-age=5", conn.getHeaderField(CACHE_CONTROL));
		final String data = lines(
			"This is an example file",
			"for testing media data."
		);
		assertEquals(expectNotModified ? -1 : data.length(), conn.getContentLength());

		if ( expectNotModified )
		{
			//noinspection resource
			assertEquals( -1, conn.getInputStream().read() );
		}
		else
		{
			assertEquals( data, getContentAsString(conn.getInputStream()) );
		}

		//textConn.setIfModifiedSince();
		assertOnExceptionEmpty();
	}

	@SuppressWarnings("HardcodedLineSeparator")
	private static String getContentAsString( final InputStream is ) throws IOException
	{
		final StringBuilder builder = new StringBuilder();
		try(BufferedReader br = new BufferedReader(new InputStreamReader(is, US_ASCII)))
		{
			String s;
			while ( (s=br.readLine())!=null )
			{
				builder.append(s).append('\n');
			}
		}
		return builder.toString();
	}

	private void assertMoved(final String url, final String target) throws IOException
	{
		final Date before = new Date();
		final HttpURLConnection conn = (HttpURLConnection)new URL(schemeAndHost + url).openConnection();
		HttpURLConnection.setFollowRedirects(false);
		conn.connect();
		assertEquals(HTTP_MOVED_PERM, conn.getResponseCode(), "responseCode");
		assertEquals(null, conn.getResponseMessage(), "responseMessage"); // is null since tomcat 8.5
		assertEquals(target, conn.getHeaderField("Location"), "location");
		assertEquals(null, conn.getContentType(), "contentType");
		assertEquals(null, conn.getHeaderField(EXPIRES));
		assertEquals(null, conn.getHeaderField(CACHE_CONTROL), "cacheControl");
		assertEquals(0, conn.getContentLength(), "contentLength");
		try(InputStream is = conn.getInputStream())
		{
			assertEquals(-1, is.read());
		}
		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithinHttpDate(before, after, new Date(date));
		assertOnExceptionEmpty();
	}

	private void assertNotFound(final String url, final String reason) throws IOException
	{
		assertNotFound(url, reason, null);
	}

	private void assertNotFound(
			final String url, final String reason,
			final Date lastModified) throws IOException
	{
		final Date before = new Date();
		final HttpURLConnection conn = (HttpURLConnection)new URL(schemeAndHost + url).openConnection();
		HttpURLConnection.setFollowRedirects(false);
		conn.connect();
		if(HTTP_NOT_FOUND!=conn.getResponseCode())
			print(conn, url);
		assertEquals(HTTP_NOT_FOUND, conn.getResponseCode());
		assertEquals(null, conn.getResponseMessage()); // is null since tomcat 8.5
		assertEquals("text/html;charset=us-ascii", conn.getContentType());
		if(lastModified!=null)
			assertEquals(lastModified, new Date(conn.getLastModified()), "lastModified");
		else
			assertEquals(0, conn.getLastModified(), "lastModified");

		assertEquals(null, conn.getHeaderField(EXPIRES));
		assertFalse(conn.getHeaderField(CACHE_CONTROL) != null && conn.getHeaderField(CACHE_CONTROL).contains("private"), "private");

		try(BufferedReader is = new BufferedReader(new InputStreamReader(conn.getErrorStream(), US_ASCII)))
		{
			assertEquals("<html>", is.readLine());
			assertEquals("<head>", is.readLine());
			assertEquals("<title>Not Found</title>", is.readLine());
			assertEquals("<meta http-equiv=\"content-type\" content=\"text/html;charset=us-ascii\">", is.readLine());
			assertEquals("</head>", is.readLine());
			assertEquals("<body>", is.readLine());
			assertEquals("<h1>Not Found</h1>", is.readLine());
			assertEquals("The requested URL was not found on this server (" + reason + ").", is.readLine());
			assertEquals("</body>", is.readLine());
			assertEquals("</html>", is.readLine());
			assertEquals(null, is.readLine());
		}

		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithinHttpDate(before, after, new Date(date));

		assertEquals(null, conn.getHeaderField(EXPIRES));
		if(lastModified!=null)
			assertEquals("max-age=5", conn.getHeaderField(CACHE_CONTROL));
		else
			assertEquals(null, conn.getHeaderField(CACHE_CONTROL));
		assertOnExceptionEmpty();
	}

	private void assertBin(
			final String url,
			final String contentType,
			final Date lastModified)
		throws IOException
	{
		assertBin(url, contentType, lastModified, "max-age=5");
	}

	private void assertBinPrivate(
			final String url,
			final String contentType,
			final Date lastModified)
		throws IOException
	{
		assertBin(url, contentType, lastModified, "private,max-age=5");
	}

	private void assertBin(
			final String url,
			final String contentType,
			final Date lastModified,
			final String cacheControl)
		throws IOException
	{
		final Date before = new Date();
		final HttpURLConnection conn = (HttpURLConnection)new URL(schemeAndHost + url).openConnection();
		HttpURLConnection.setFollowRedirects(false);
		conn.connect();
		assertEquals(HTTP_OK, conn.getResponseCode(), "url=" + url);
		assertEquals(null, conn.getResponseMessage()); // is null since tomcat 8.5
		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithinHttpDate(before, after, new Date(date));
		final long actualLastModified = conn.getLastModified();
		//System.out.println("LastModified: "+new Date(actualLastModified));
		assertEqualsDate(lastModified, new Date(actualLastModified));
		if(!contentType.equals(conn.getContentType()))
			print(conn, url);
		assertEquals(contentType, conn.getContentType());
		assertEquals(cacheControl, conn.getHeaderField(CACHE_CONTROL));
		assertEquals(null, conn.getHeaderField(EXPIRES));

		assertOnExceptionEmpty();
	}

	private void assertInternalError(final String url) throws IOException
	{
		final Date before = new Date();
		final HttpURLConnection conn = (HttpURLConnection)new URL(schemeAndHost + url).openConnection();
		HttpURLConnection.setFollowRedirects(false);
		conn.connect();
		if(HTTP_INTERNAL_ERROR!=conn.getResponseCode())
			print(conn, url);
		assertEquals(HTTP_INTERNAL_ERROR, conn.getResponseCode());
		assertEquals(null, conn.getResponseMessage()); // is null since tomcat 8.5
		assertEquals("text/html;charset=us-ascii", conn.getContentType());
		assertEquals(0, conn.getLastModified(), "LastModified");
		assertEquals(null, conn.getHeaderField(EXPIRES));
		assertEquals(null, conn.getHeaderField(CACHE_CONTROL));

		try(BufferedReader is = new BufferedReader(new InputStreamReader(conn.getErrorStream(), US_ASCII)))
		{
			assertEquals("<html>", is.readLine());
			assertEquals("<head>", is.readLine());
			assertEquals("<title>Internal Server Error</title>", is.readLine());
			assertEquals("<meta http-equiv=\"content-type\" content=\"text/html;charset=us-ascii\">", is.readLine());
			assertEquals("</head>", is.readLine());
			assertEquals("<body>", is.readLine());
			assertEquals("<h1>Internal Server Error</h1>", is.readLine());
			assertEquals("An internal error occured on the server.", is.readLine());
			assertEquals("</body>", is.readLine());
			assertEquals("</html>", is.readLine());
			assertEquals(null, is.readLine());
		}

		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithinHttpDate(before, after, new Date(date));

		assertTrue(onException.exists(), onException.getAbsolutePath());
		final String data = lines(
				"java.lang.RuntimeException",
				"test error in MediaNameServer"
		);
		assertEquals(data, getContentAsString(new FileInputStream(onException)));
		StrictFile.delete(onException);
	}

	private void assertNameURL(final String url) throws IOException
	{
		final HttpURLConnection conn = (HttpURLConnection)new URL(schemeAndHost + url).openConnection();
		HttpURLConnection.setFollowRedirects(false);
		conn.connect();
		assertEquals(200, conn.getResponseCode());
		assertEquals("text/plain;charset=UTF-8", conn.getContentType());
		assertEquals(null, conn.getHeaderField(EXPIRES));
		assertEquals(null, conn.getHeaderField(CACHE_CONTROL));
		assertEquals(12, conn.getContentLength());

		try(BufferedReader is = new BufferedReader(new InputStreamReader(conn.getInputStream(), US_ASCII)))
		{
			assertEquals("media item 1", is.readLine());
			assertEquals(null, is.readLine());
		}
	}

	private static void print(final HttpURLConnection conn, final String url) throws IOException
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

	private static void print(final InputStream in) throws IOException
	{
		if(in==null)
		{
			System.out.println("-----------leer---------------");
		}
		else
		{
			final byte[] b = new byte[20000];
			final File f = createTempFile(MediaServletTest.class.getName(), ".tmp");
			System.out.println("----------- " + f.getAbsolutePath() + " ---------------");
			try(OutputStream out = new FileOutputStream(f))
			{
				for(int len = in.read(b); len>=0; len = in.read(b))
					out.write(b, 0, len);
			}
			in.close();
			StrictFile.delete(f);
		}
	}

	private static Date hour8(final int hour) throws ParseException
	{
		return df().parse("2010-08-11 " + new DecimalFormat("00").format(hour) + ":23:56.000");
	}

	private static Date hour9(final int hour) throws ParseException
	{
		return df().parse("2010-09-11 " + new DecimalFormat("00").format(hour) + ":23:56.000");
	}

	private static SimpleDateFormat df()
	{
		final SimpleDateFormat result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
		result.setTimeZone(getTimeZone("Europe/Berlin"));
		result.setLenient(false);
		return result;
	}

	private static long addMillis(final Date date, final int millis)
	{
		return date.getTime() + millis;
	}

	// ----------------------------------- adapted from Assert

	private static final String DATE_FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.SSS";

	private static void assertEqualsDate(final Date expected, final Date actual)
	{
		final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL, Locale.ENGLISH);
		assertEquals(expected, actual, "expected " + df.format(expected) + ", but got " + df.format(actual));
	}

	private static void assertWithinHttpDate(final Date expectedBefore, final Date expectedAfter, final Date actual)
	{
		final long resolution = 1000;
		final long leftTolerance = 995;
		final Date expectedBeforeFloor = new Date(((expectedBefore.getTime()-leftTolerance) / resolution) * resolution);
		final Date expectedAfterCeil   = new Date(((expectedAfter.getTime() / resolution) * resolution) + resolution);

		final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL, Locale.ENGLISH);
		final String message =
			"expected date within " + df.format(expectedBeforeFloor) + " (" + df.format(expectedBefore) + ")" +
			" and " + df.format(expectedAfterCeil) + " (" + df.format(expectedAfter) + ")" +
			", but was " + df.format(actual);

		assertTrue(!expectedBeforeFloor.after(actual), message);
		assertTrue(!expectedAfterCeil.before(actual), message);
	}

	@SuppressWarnings("HardcodedLineSeparator")
	private static String lines( final String... lines )
	{
		final StringBuilder builder = new StringBuilder();
		for ( final String line: lines )
		{
			builder.append( line );
			builder.append( '\n' );
		}
		return builder.toString();
	}

	private static void assertOnExceptionEmpty()
	{
		assertFalse(onException.exists(), onException.getAbsolutePath());
	}
}
