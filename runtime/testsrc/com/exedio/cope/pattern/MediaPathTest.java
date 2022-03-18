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

import static com.exedio.cope.PrometheusMeterRegistrar.meterCope;
import static com.exedio.cope.PrometheusMeterRegistrar.tag;
import static com.exedio.cope.pattern.MediaPath.getNoSuchPath;
import static com.exedio.cope.pattern.MediaPath.getNoSuchPathLogs;
import static com.exedio.cope.tojunit.Assert.assertUnmodifiable;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.PrometheusMeterRegistrar;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.MediaPathFeature.Result;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class MediaPathTest extends TestWithEnvironment
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
	private int noSuchPathBefore;
	private MediaInfo normalBefore = null;
	private MediaInfo mandatBefore = null;
	private MediaInfo guessBefore  = null;

	@BeforeEach void setUp()
	{
		item = new MediaPathItem();
		id = item.getCopeID();
		servlet = new MyMediaServlet();
		servlet.initPathes(MODEL);
		servlet.initConnected(MODEL);
		noSuchPathBefore = getNoSuchPath();
		normalBefore = MediaPathItem.normal.getInfo();
		mandatBefore = MediaPathItem.mandat.getInfo();
		guessBefore  = MediaPathItem.guess .getInfo();
	}

	@AfterEach void tearDown()
	{
		servlet.destroy();
	}

	@Test void testNotFound() throws ServletException, IOException
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
		assertNotFound(pathInfo, "is null late");
	}

	@Test void testNotFoundContentTypeNull() throws ServletException, IOException
	{
		assertNotFound("zack", "no such path");
		assertNotFound("zack/zick", "no such path");
		assertNotFound("/MediaPathItem/normal", "no such path");
		assertNotFound("/MediaPathItem/normal/", "not an item");
		assertNotFound("/MediaPathItem/normal/x", "not an item");
		assertNotFound("/MediaPathItem/normal/x.jpg", "not an item");
		assertNotFound("/MediaPathItem/normal/MediaPathItem-x.jpg", "not an item");
		assertNotFound("/MediaPathItem/normal/MediaPathItem-9999.jpg", "no such item");

		final String pathInfo = "/MediaPathItem/normal/" + id;
		assertNotFound(pathInfo, "is null");

		item.setNormalResult(Result.notFoundIsNull);
		assertNotFound(pathInfo, "is null");
	}

	@Test void testException() throws ServletException, IOException
	{
		final String pathInfo = "/MediaPathItem/normal/" + id;
		item.setNormalContentType("major/minor");
		assertOk(pathInfo);

		servlet.failOnException = false;

		item.setNormalResult(Result.IOException);
		assertError(pathInfo);

		item.setNormalResult(Result.RuntimeException);
		assertError(pathInfo);
	}

	private static final String prefix = "/testContextPath/testServletPath";

	@Test void testSpecialMultiple() throws ServletException, IOException
	{
		item.setNormalContentType("major/minor");
		final String ok = "/MediaPathItem/normal/" + id;
		assertEquals(ok, "/" + item.getNormalLocator().getPath());
		assertOk      ("/MediaPathItem/normal/"             + id);
		assertRedirect("/MediaPathItem/normal/.fFFF/"       + id, prefix + ok);
		assertNotFound("/MediaPathItem/normal/.fFF1/.fFF2/" + id, "invalid special"); // duplicate fingerprint
		assertRedirect("/MediaPathItem/normal/.tTTT/"       + id, prefix + ok);
		assertNotFound("/MediaPathItem/normal/.tTT1/.tTT2/" + id, "invalid special"); // duplicate token
		assertRedirect("/MediaPathItem/normal/.fFFF/.tTTT/" + id, prefix + ok);
		assertNotFound("/MediaPathItem/normal/.tTTT/.fFFF/" + id, "invalid special"); // wrong order
	}

	@Test void testRedirectFrom() throws ServletException, IOException
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

	@Test void testRedirectFromExtension() throws ServletException, IOException
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

	@Test void testRedirectFromPhrase() throws ServletException, IOException
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

	@Test void testRedirectFromPhraseExtension() throws ServletException, IOException
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

	@Test void testRedirectFromFinger() throws ServletException, IOException
	{
		item.setFingerContentType("image/jpeg");
		item.setFingerLastModified(new Date(333338888));
		assertRedirect("/MediaPathItem/fingerRedirect1/.f/"          + id, prefix + "/MediaPathItem/finger/.f/"          + id);
		assertRedirect("/MediaPathItem/fingerRedirect2/.f/"          + id, prefix + "/MediaPathItem/finger/.f/"          + id);
		assertRedirect("/MediaPathItem/fingerRedirect1/.fx/"         + id, prefix + "/MediaPathItem/finger/.fx/"         + id);
		assertRedirect("/MediaPathItem/fingerRedirect1/.f333338888/" + id, prefix + "/MediaPathItem/finger/.f333338888/" + id);
		assertRedirect("/MediaPathItem/fingerRedirect1/.f333339000/" + id, prefix + "/MediaPathItem/finger/.f333339000/" + id);
	}

	@Test void testCatchphrase() throws ServletException, IOException
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

	@Test void testCatchphraseExtension() throws ServletException, IOException
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

	@Test void testFingerNotFound() throws ServletException, IOException
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

	@Test void testFingerWithoutLastModified() throws ServletException, IOException
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

	@Test void testFinger() throws ServletException, IOException
	{
		item.setFingerContentType("image/jpeg");
		item.setFingerLastModified(new Date(333338888));
		item.setCatchphrase("phrase");
		final int ALMOST_ONE_YEAR = 31363200;
		final String ok = "/MediaPathItem/finger/.fIkl3T/" + id + "/phrase.jpg";
		assertEquals(ok, "/" + item.getFingerLocator().getPath());
		service(new Request(ok)).assertLastModified(333339000l).assertOkAndCacheControl("max-age="+ALMOST_ONE_YEAR+",immutable");

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

		assertNotFound("/MediaPathItem/finger/.fIkl3T/.fIkl3T/" + id + "/phrase.jpg", "invalid special");
		assertNotFound("/MediaPathItem/finger/.fIkl3T/.fxxxxx/" + id + "/phrase.jpg", "invalid special");
		assertNotFound("/MediaPathItem/finger/.fxxxxx/.fIkl3T/" + id + "/phrase.jpg", "invalid special");
		assertNotFound("/MediaPathItem/finger/.fxxxxx/.fxxxxx/" + id + "/phrase.jpg", "invalid special");
	}

	@Test void testConditional() throws ServletException, IOException
	{
		item.setNormalContentType("image/jpeg");
		item.setCatchphrase("phrase");
		item.setCacheControlMaximumAge("PT456S");
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

	@Test void testExpires() throws ServletException, IOException
	{
		item.setNormalContentType("image/jpeg");
		item.setCatchphrase("phrase");
		item.setCacheControlMaximumAge("PT456S");
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
	@Test void testExpiresFinal() throws ServletException, IOException
	{
		item.setNormalContentType("image/jpeg");
		item.setNormalLastModified(new Date(333338888));
		item.setCacheControlMaximumAge("PT456S");
		final String ok = "/MediaPathItem/normal/" + id + ".jpg";
		assertEquals(ok, "/" + item.getNormalLocator().getPath());
		final Response response = service(new Request(ok));
		response.assertOkAndCache(333339000l);
	}

	@Test void testGuess() throws ServletException, IOException
	{
		item.setGuessContentType("image/jpeg");
		final String ok = "/MediaPathItem/guess/.tMediaPathItem.guess-" + id + "/" + id + ".jpg";
		assertEquals(ok, "/" + item.getGuessLocator().getPath());
		assertOk(ok);

		assertNotFound("/MediaPathItem/guess/.tzack/" + id + ".jpg", "guessed url");
		assertNotFound("/MediaPathItem/guess/.t/"     + id + ".jpg", "guessed url");
		assertNotFound("/MediaPathItem/guess/"        + id + ".jpg", "guessed url");
	}

	@Test void testGuessMultipleTokens() throws ServletException, IOException
	{
		item.setGuessContentType("major/minor");
		final String token = "MediaPathItem.guess-" + id;

		assertOk(      "/MediaPathItem/guess/.t" + token + "/"                 + id);
		assertNotFound("/MediaPathItem/guess/.t" + "xxx" + "/"                 + id, "guessed url");
		assertNotFound("/MediaPathItem/guess/.t" + token + "/.t" + token + "/" + id, "invalid special");
		assertNotFound("/MediaPathItem/guess/.t" + "xxx" + "/.t" + token + "/" + id, "invalid special");
		assertNotFound("/MediaPathItem/guess/.t" + token + "/.t" + "xxx" + "/" + id, "invalid special");
		assertNotFound("/MediaPathItem/guess/.t" + "xxx" + "/.t" + "xxx" + "/" + id, "invalid special");
	}

	@Test void testGuessAndAge() throws ServletException, IOException
	{
		item.setGuessContentType("image/jpeg");
		item.setGuessLastModified(new Date(333338888));
		item.setCacheControlMaximumAge("PT8765432S");
		final String ok = "/MediaPathItem/guess/.tMediaPathItem.guess-" + id + "/" + id + ".jpg";
		assertEquals(ok, "/" + item.getGuessLocator().getPath());
		service(new Request(ok)).assertOkAndCacheControl("max-age=8765432");
	}

	@Test void testFingerGuess() throws ServletException, IOException
	{
		item.setFingerGuessContentType("image/jpeg");
		item.setFingerGuessLastModified(new Date(333338888));
		final int ALMOST_ONE_YEAR = 31363200;
		final String token = "MediaPathItem.fingerGuess-" + id;
		final String ok = "/MediaPathItem/fingerGuess/.fIkl3T/.t" + token + "/" + id + ".jpg";
		assertEquals(ok, "/" + item.getFingerGuessLocator().getPath());
		service(new Request(ok)).
				assertLastModified(333339000l).
				assertOkAndCacheControl("max-age="+ALMOST_ONE_YEAR+",immutable");

		assertNotFound("/MediaPathItem/fingerGuess/.fIkl3T/.tzack/" + id + ".jpg", "guessed url");
		assertNotFound("/MediaPathItem/fingerGuess/.fIkl3T/.t/"     + id + ".jpg", "guessed url");
		assertNotFound("/MediaPathItem/fingerGuess/.fIkl3T/"        + id + ".jpg", "guessed url");

		assertRedirect("/MediaPathItem/fingerGuess/.t" + token + "/" + id,                      prefix + ok);
		assertRedirect("/MediaPathItem/fingerGuess/.t" + token + "/" + id + "/otherPhrase",     prefix + ok);
		assertRedirect("/MediaPathItem/fingerGuess/.t" + token + "/" + id + "/phrase.png",      prefix + ok);
		assertRedirect("/MediaPathItem/fingerGuess/.t" + token + "/" + id + "/otherPhrase.jpg", prefix + ok);

		assertRedirect("/MediaPathItem/fingerGuess/.fx/.t" + token + "/" + id,                      prefix + ok);
		assertRedirect("/MediaPathItem/fingerGuess/.fx/.t" + token + "/" + id + "/otherPhrase",     prefix + ok);
		assertRedirect("/MediaPathItem/fingerGuess/.fx/.t" + token + "/" + id + "/phrase.png",      prefix + ok);
		assertRedirect("/MediaPathItem/fingerGuess/.fx/.t" + token + "/" + id + "/otherPhrase.jpg", prefix + ok);

		assertNotFound("/MediaPathItem/fingerGuess/.tzack/" + id,                      "guessed url");
		assertNotFound("/MediaPathItem/fingerGuess/.tzack/" + id + "/otherPhrase",     "guessed url");
		assertNotFound("/MediaPathItem/fingerGuess/.tzack/" + id + "/phrase.png",      "guessed url");
		assertNotFound("/MediaPathItem/fingerGuess/.tzack/" + id + "/otherPhrase.jpg", "guessed url");

		assertNotFound("/MediaPathItem/fingerGuess/.fx/.tzack/" + id,                      "guessed url");
		assertNotFound("/MediaPathItem/fingerGuess/.fx/.tzack/" + id + "/otherPhrase",     "guessed url");
		assertNotFound("/MediaPathItem/fingerGuess/.fx/.tzack/" + id + "/phrase.png",      "guessed url");
		assertNotFound("/MediaPathItem/fingerGuess/.fx/.tzack/" + id + "/otherPhrase.jpg", "guessed url");

		assertNotFound("/MediaPathItem/fingerGuess/" + id,                      "guessed url");
		assertNotFound("/MediaPathItem/fingerGuess/" + id + "/otherPhrase",     "guessed url");
		assertNotFound("/MediaPathItem/fingerGuess/" + id + "/phrase.png",      "guessed url");
		assertNotFound("/MediaPathItem/fingerGuess/" + id + "/otherPhrase.jpg", "guessed url");

		assertNotFound("/MediaPathItem/fingerGuess/.fx/" + id,                      "guessed url");
		assertNotFound("/MediaPathItem/fingerGuess/.fx/" + id + "/otherPhrase",     "guessed url");
		assertNotFound("/MediaPathItem/fingerGuess/.fx/" + id + "/phrase.png",      "guessed url");
		assertNotFound("/MediaPathItem/fingerGuess/.fx/" + id + "/otherPhrase.jpg", "guessed url");
	}

	@Test void testCacheControl() throws ServletException, IOException
	{
		item.setNormalContentType("image/jpeg");
		item.setNormalLastModified(new Date(333338888));
		item.setCacheControlMaximumAge("PT7654321.888888888S");
		final String ok = "/MediaPathItem/normal/" + id + ".jpg";
		assertEquals(ok, "/" + item.getNormalLocator().getPath());
		service(new Request(ok)).assertOkAndCacheControl("max-age=7654321");

		item.setCacheControlPrivate(true);
		service(new Request(ok)).assertOkAndCacheControl("private,max-age=7654321");

		item.setCacheControlMaximumAge("PT0S");
		item.setCacheControlPrivate(false);
		service(new Request(ok)).assertOkAndCacheControl("max-age=0");

		item.setCacheControlPrivate(true);
		service(new Request(ok)).assertOkAndCacheControl("private,max-age=0");

		item.setCacheControlMaximumAge("PT-1S"); // negative values are treated like zero
		service(new Request(ok)).assertOkAndCacheControl("private,max-age=0");

		item.setCacheControlMaximumAge("PT"+Integer.MIN_VALUE+"S"); // negative values are treated like zero
		service(new Request(ok)).assertOkAndCacheControl("private,max-age=0");

		item.setCacheControlMaximumAge(null);
		item.setCacheControlPrivate(false);
		service(new Request(ok)).assertOkAndCacheControl(null);

		item.setCacheControlPrivate(true);
		service(new Request(ok)).assertOkAndCacheControl("private");

		item.setCacheControlNoTransform(true);
		service(new Request(ok)).assertOkAndCacheControl("private,no-transform");

		item.setCacheControlPrivate(false);
		service(new Request(ok)).assertOkAndCacheControl("no-transform");
	}

	@Test void testHeaders() throws ServletException, IOException
	{
		item.setNormalContentType("image/jpeg");
		final String ok = "/MediaPathItem/normal/" + id + ".jpg";
		assertEquals(ok, "/" + item.getNormalLocator().getPath());
		service(new Request(ok)).assertOk();

		item.setHeaders(asList("One", "OneValue"));
		service(new Request(ok)).assertOkAndHeaders("One", "OneValue");
	}

	@Test void testHeadersMultiple() throws ServletException, IOException
	{
		item.setNormalContentType("image/jpeg");
		final String ok = "/MediaPathItem/normal/" + id + ".jpg";
		assertEquals(ok, "/" + item.getNormalLocator().getPath());
		service(new Request(ok)).assertOk();

		item.setHeaders(asList("One", "OneValueA", "One", "OneValueB", "Two", "TwoValue"));
		service(new Request(ok)).assertOkAndHeaders("One", "OneValueA", "One", "OneValueB", "Two", "TwoValue");
	}

	@Test void testAccessControlAllowOriginWildcard() throws ServletException, IOException
	{
		item.setNormalContentType("image/jpeg");
		final String ok = "/MediaPathItem/normal/" + id + ".jpg";
		assertEquals(ok, "/" + item.getNormalLocator().getPath());
		service(new Request(ok)).assertOk();

		item.setAccessControlAllowOriginWildcard(true);
		service(new Request(ok)).assertOkAndHeaders("Access-Control-Allow-Origin", "*");
	}

	@Test void testInfoNoSuchPath() throws ServletException, IOException
	{
		assertNotFound("/MediaPathItem/zack/x", "no such path");
		assertInfo(1, MediaPathItem.normal, normalBefore, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	@Test void testInfoRedirectFrom() throws ServletException, IOException
	{
		item.setNormalContentType("major/minor");
		assertRedirect("/MediaPathItem/normalRedirect1/" + id, prefix + "/MediaPathItem/normal/" + id);
		assertInfo(0, MediaPathItem.normal, normalBefore, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	@Test void testInfoException() throws ServletException, IOException
	{
		item.setNormalContentType("major/minor");
		item.setNormalResult(Result.RuntimeException);
		servlet.failOnException = false;
		assertError("/MediaPathItem/normal/" + id);
		assertInfo(0, MediaPathItem.normal, normalBefore, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	@Test void testInfoInvalidSpecial() throws ServletException, IOException
	{
		assertNotFound("/MediaPathItem/normal/.x", "invalid special");
		assertInfo(0, MediaPathItem.normal, normalBefore, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0);
	}

	@Test void testInfoGuessedUrl() throws ServletException, IOException
	{
		assertNotFound("/MediaPathItem/guess/x", "guessed url");
		assertInfo(0, MediaPathItem.guess, guessBefore, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0);
	}

	@Test void testInfoNotAnItem() throws ServletException, IOException
	{
		assertNotFound("/MediaPathItem/normal/x", "not an item");
		assertInfo(0, MediaPathItem.normal, normalBefore, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0);
	}

	@Test void testInfoNoSuchItem() throws ServletException, IOException
	{
		assertNotFound("/MediaPathItem/normal/MediaPathItem-9999.jpg", "no such item");
		assertInfo(0, MediaPathItem.normal, normalBefore, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
	}

	@Test void testInfoMoved() throws ServletException, IOException
	{
		item.setNormalContentType("image/jpeg");
		assertRedirect("/MediaPathItem/normal/" + id, prefix + "/MediaPathItem/normal/" + id + ".jpg");
		assertInfo(0, MediaPathItem.normal, normalBefore, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0);
	}

	@Test void testInfoIsNull() throws ServletException, IOException
	{
		item.setNormalResult(Result.notFoundIsNull);
		assertNotFound("/MediaPathItem/normal/" + id, "is null");
		assertInfo(0, MediaPathItem.normal, normalBefore, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
	}

	@Test void testInfoIsNullLate() throws ServletException, IOException
	{
		item.setNormalContentType("major/minor");
		item.setNormalResult(Result.notFoundIsNull);
		assertNotFound("/MediaPathItem/normal/" + id, "is null late");
		assertInfo(0, MediaPathItem.normal, normalBefore, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
	}

	// TODO testInfoNotModified

	@Test void testInfoDelivered() throws ServletException, IOException
	{
		item.setNormalContentType("major/minor");
		assertOk("/MediaPathItem/normal/" + id);
		assertInfo(0, MediaPathItem.normal, normalBefore, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
	}

	@Test void testInfoDeliveredMandatory() throws ServletException, IOException
	{
		item.setMandatContentType("major/minor");
		assertOk("/MediaPathItem/mandat/" + id);
		assertInfo(0, MediaPathItem.mandat, mandatBefore, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
	}

	private void assertInfo(
			final int noSuchPath,
			final MediaPathFeature feature,
			final MediaInfo before,
			final int redirectFrom,
			final int exception,
			final int invalidSpecial,
			final int guessedUrl,
			final int notAnItem,
			final int noSuchItem,
			final int moved,
			final int isNull,
			final int notModified,
			final int delivered)
	{
		final MediaInfo i = feature.getInfo();

		assertEquals(noSuchPath,     getNoSuchPath()       - noSuchPathBefore,           "noSuchPath");
		assertEquals(redirectFrom,   i.getRedirectFrom()   - before.getRedirectFrom(),   "redirectFrom");
		assertEquals(exception,      i.getException()      - before.getException(),      "exception");
		assertEquals(invalidSpecial, i.getInvalidSpecial() - before.getInvalidSpecial(), "invalidSpecial");
		assertEquals(guessedUrl,     i.getGuessedUrl()     - before.getGuessedUrl(),     "guessedUrl");
		assertEquals(notAnItem,      i.getNotAnItem()      - before.getNotAnItem(),      "notAnItem");
		assertEquals(noSuchItem,     i.getNoSuchItem()     - before.getNoSuchItem(),     "noSuchItem");
		assertEquals(moved,          i.getMoved()          - before.getMoved(),          "moved");
		assertEquals(isNull,         i.getIsNull()         - before.getIsNull(),         "isNull");
		assertEquals(notModified,    i.getNotModified()    - before.getNotModified(),    "notModified");
		assertEquals(delivered,      i.getDelivered()      - before.getDelivered(),      "delivered");

		assertIt(getNoSuchPath(),       getNoSuchPathLogs());
		assertIt(i.getException(),      feature.getExceptionLogs());
		assertIt(i.getInvalidSpecial(), feature.getInvalidSpecialLogs());
		assertIt(i.getGuessedUrl(),     feature.getGuessedUrlLogs());
		assertIt(i.getNotAnItem(),      feature.getNotAnItemLogs());
		assertIt(i.getNoSuchItem(),     feature.getNoSuchItemLogs());
		assertIt(i.getIsNull(),         feature.getIsNullLogs());

		assertEquals(getNoSuchPath(),       count(         "notFound",    Tags.of("cause", "noSuchPath", "feature", "NONE"), true));
		assertEquals(i.getRedirectFrom(),   count(feature, "moved",       Tags.of("cause", "RedirectFrom"), feature.isAnnotationPresent(RedirectFrom.class)));
		assertEquals(i.getException(),      count(feature, "failure",     Tags.empty()));
		assertEquals(i.getInvalidSpecial(), count(feature, "notFound",    Tags.of("cause", "invalidSpecial")));
		assertEquals(i.getGuessedUrl(),     count(feature, "notFound",    Tags.of("cause", "PreventUrlGuessing"), feature.isUrlGuessingPrevented()));
		assertEquals(i.getNotAnItem(),      count(feature, "notFound",    Tags.of("cause", "notAnItem")));
		assertEquals(i.getNoSuchItem(),     count(feature, "notFound",    Tags.of("cause", "noSuchItem")));
		assertEquals(i.getMoved(),          count(feature, "moved",       Tags.of("cause", "canonize")));
		assertEquals(i.getIsNull(),         count(feature, "notFound",    Tags.of("cause", "isNull"), !feature.isMandatory()));
		assertEquals(i.getNotModified(),    count(feature, "notModified", Tags.empty()));
		assertEquals(i.getDelivered(),      timer(feature, "ok"));
	}

	private static double count(
			final MediaPathFeature feature, final String nameSuffix,
			final Tags tags)
	{
		return count(feature, nameSuffix, tags, true);
	}

	private static double count(
			final MediaPathFeature feature, final String nameSuffix,
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
			return ((Counter)meterCope(MediaPath.class, nameSuffix, tags)).count();
		}
		else
		{
			assertThrows(
					PrometheusMeterRegistrar.NotFound.class,
					() -> meterCope(MediaPath.class, nameSuffix, tags));
			return 0;
		}
	}

	private static double timer(
			final MediaPathFeature feature, final String nameSuffix)
	{
		return ((Timer)meterCope(MediaPath.class, nameSuffix, tag(feature))).count();
	}

	private static void assertIt(final int expected, final List<MediaRequestLog> actual)
	{
		assertEquals(expected, actual.size());
		assertUnmodifiable(actual);
	}

	private void assertOk(
			final String pathInfo)
		throws ServletException, IOException
	{
		service(new Request(pathInfo)).assertOk();
	}

	@SuppressWarnings("HardcodedLineSeparator") // OK unix newline in html
	private void assertNotFound(
			final String pathInfo,
			final String reason)
		throws ServletException, IOException
	{
		service(new Request(pathInfo)).assertError(
				SC_NOT_FOUND, "us-ascii", "text/html",
				"<html>\n" +
				"<head>\n" +
				"<title>Not Found</title>\n" +
				"<meta http-equiv=\"content-type\" content=\"text/html;charset=us-ascii\">\n" +
				"</head>\n" +
				"<body>\n" +
				"<h1>Not Found</h1>\n" +
				"The requested URL was not found on this server (" + reason + ").\n" +
				"</body>\n" +
				"</html>\n");
	}

	@SuppressWarnings("HardcodedLineSeparator") // OK unix newline in html
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

	private static final class Request extends AssertionFailedHttpServletRequest
	{
		private final String pathInfo;
		private long ifModifiedSince = -1;

		Request(final String pathInfo)
		{
			this.pathInfo = pathInfo;
		}

		Request ifModifiedSince(final long ifModifiedSince)
		{
			this.ifModifiedSince = ifModifiedSince;
			return this;
		}

		@Override
		public String getRemoteAddr()
		{
			return "testRemoteAddr";
		}

		@Override
		public boolean isSecure()
		{
			return false;
		}

		@Override
		public String getMethod()
		{
			return "GET";
		}

		@Override
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

		@Override
		public long getDateHeader(final String name)
		{
			if("If-Modified-Since".equals(name))
				return ifModifiedSince;
			else
				return super.getDateHeader(name);
		}

		@Override
		public String getContextPath()
		{
			return "/testContextPath";
		}

		@Override
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
			return null;
		}
	}

	private static final class Response extends AssertionFailedHttpServletResponse
	{
		Response()
		{
			// make package private
		}

		private String location;
		private String cacheControl;
		private final ArrayList<String> headers = new ArrayList<>();

		@Override
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
			else
				super.setHeader(name, value);
		}

		@Override
		public void addHeader(final String name, final String value)
		{
			assertNotNull(name);
			assertNotNull(value);
			assertNull(out);

			headers.add(name);
			headers.add(value);
		}


		private long lastModified = Long.MIN_VALUE;

		@Override
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

			return new AssertionFailedServletOutputStream()
			{
				@Override
				public void write(final byte[] b, final int off, final int len)
				{
					myOut.write(b, off, len);
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


		void assertOk()
		{
			assertAll(
					() -> assertEquals(null,              this.location,                 "location"),
					() -> assertEquals(Long.MIN_VALUE,    this.lastModified,             "lastModified"),
					() -> assertEquals(Integer.MIN_VALUE, this.status,                   "sc"),
					() -> assertEquals(null,              this.charset,                  "charset"),
					() -> assertEquals(null,              this.contentType,              "contentType"),
					() -> assertEquals("responseBody",    this.outString(),              "content"),
					() -> assertEquals(10011,             this.contentLength,            "contentLength"),
					() -> assertEquals(null,              this.cacheControl,             "cacheControl"),
					() -> assertEquals(emptyList(),       this.headers,                  "headers"),
					() -> assertEquals(0,                 this.flushBufferCount,         "flushBuffer"));
		}

		void assertOkAndCache(final long lastModified)
		{
			assertAll(
					() -> assertEquals(null,              this.location,                 "location"),
					() -> assertEquals(lastModified,      this.lastModified,             "lastModified"),
					() -> assertEquals(Integer.MIN_VALUE, this.status,                   "sc"),
					() -> assertEquals(null,              this.charset,                  "charset"),
					() -> assertEquals(null,              this.contentType,              "contentType"),
					() -> assertEquals("responseBody",    this.outString(),              "content"),
					() -> assertEquals(10011,             this.contentLength,            "contentLength"),
					() -> assertEquals("max-age=456",     this.cacheControl,             "cacheControl"),
					() -> assertEquals(emptyList(),       this.headers,                  "headers"),
					() -> assertEquals(0,                 this.flushBufferCount,         "flushBuffer"));
		}

		void assertNotModified(final long lastModified)
		{
			assertAll(
					() -> assertEquals(null,              this.location,                 "location"),
					() -> assertEquals(lastModified,      this.lastModified,             "lastModified"),
					() -> assertEquals(SC_NOT_MODIFIED,   this.status,                   "sc"),
					() -> assertEquals(null,              this.charset,                  "charset"),
					() -> assertEquals(null,              this.contentType,              "contentType"),
					() -> assertEquals(null,              this.outString(),              "content"),
					() -> assertEquals(Integer.MIN_VALUE, this.contentLength,            "contentLength"),
					() -> assertEquals("max-age=456",     this.cacheControl,             "cacheControl"),
					() -> assertEquals(emptyList(),       this.headers,                  "headers"),
					() -> assertEquals(1,                 this.flushBufferCount,         "flushBuffer"));
		}

		void assertOkAndCacheControl(final String value)
		{
			assertAll(
					() -> assertEquals(null,              this.location,                 "location"),
					() -> assertEquals(Integer.MIN_VALUE, this.status,                   "sc"),
					() -> assertEquals(null,              this.charset,                  "charset"),
					() -> assertEquals(null,              this.contentType,              "contentType"),
					() -> assertEquals("responseBody",    this.outString(),              "content"),
					() -> assertEquals(10011,             this.contentLength,            "contentLength"),
					() -> assertEquals(value,             this.cacheControl,             "cacheControl"),
					() -> assertEquals(emptyList(),       this.headers,                  "headers"),
					() -> assertEquals(0,                 this.flushBufferCount,         "flushBuffer"));
		}

		void assertOkAndHeaders(final String... headers)
		{
			assertAll(
					() -> assertEquals(null,              this.location,                 "location"),
					() -> assertEquals(Long.MIN_VALUE,    this.lastModified,             "lastModified"),
					() -> assertEquals(Integer.MIN_VALUE, this.status,                   "sc"),
					() -> assertEquals(null,              this.charset,                  "charset"),
					() -> assertEquals(null,              this.contentType,              "contentType"),
					() -> assertEquals("responseBody",    this.outString(),              "content"),
					() -> assertEquals(10011,             this.contentLength,            "contentLength"),
					() -> assertEquals(null,              this.cacheControl,             "cacheControl"),
					() -> assertEquals(asList(headers),   this.headers,                  "headers"),
					() -> assertEquals(0,                 this.flushBufferCount,         "flushBuffer"));
		}

		void assertError(
				final int sc,
				final String charset,
				final String contentType,
				final String content)
		{
			assertAll(
					() -> assertEquals(null,             this.location,                 "location"),
					() -> assertEquals(Long.MIN_VALUE,   this.lastModified,             "lastModified"),
					() -> assertEquals(sc,               this.status,                   "sc"),
					() -> assertEquals(charset,          this.charset,                  "charset"),
					() -> assertEquals(contentType,      this.contentType,              "contentType"),
					() -> assertEquals(content,          this.outString(),              "content"),
					() -> assertEquals(content.length(), this.contentLength,            "contentLength"),
					() -> assertEquals(null,             this.cacheControl,             "cacheControl"),
					() -> assertEquals(emptyList(),      this.headers,                  "headers"),
					() -> assertEquals(0,                this.flushBufferCount,         "flushBuffer"));
		}

		void assertRedirect(final String location)
		{
			assertAll(
					() -> assertEquals(location,             this.location,                 "location"),
					() -> assertEquals(Long.MIN_VALUE,       this.lastModified,             "lastModified"),
					() -> assertEquals(SC_MOVED_PERMANENTLY, this.status,                   "sc"),
					() -> assertEquals(null,                 this.charset,                  "charset"),
					() -> assertEquals(null,                 this.contentType,              "contentType"),
					() -> assertEquals(null,                 this.outString(),              "content"),
					() -> assertEquals(Integer.MIN_VALUE,    this.contentLength,            "contentLength"),
					() -> assertEquals(null,                 this.cacheControl,             "cacheControl"),
					() -> assertEquals(emptyList(),          this.headers,                  "headers"),
					() -> assertEquals(0,                    this.flushBufferCount,         "flushBuffer"));
		}

		Response assertLastModified(final long lastModified)
		{
			assertEquals(lastModified, this.lastModified, "lastModified");
			return this;
		}
	}

	private static final class MyMediaServlet extends MediaServlet
	{
		boolean failOnException = true;

		MyMediaServlet()
		{
			// make package private
		}

		@Override
		protected Duration getMaximumAge(
				final MediaPath.Locator locator)
		{
			return
					assertConfigMethod(locator).
							getCacheControlMaximumAge();
		}

		@Override
		protected boolean isCacheControlPrivate(
				final MediaPath.Locator locator)
		{
			return
					assertConfigMethod(locator).
							getCacheControlPrivate();
		}

		@Override
		protected void filterResponse(
				final MediaPath.Locator locator,
				final MediaResponse response)
		{
			super.filterResponse(locator, response);

			final MediaPathItem item = assertConfigMethod(locator);
			for(final Iterator<String> i =
				 item.getHeaders().iterator();
				 i.hasNext(); )
			{
				response.addHeader(i.next(), i.next());
			}
			if(item.getCacheControlNoTransform())
				response.addCacheControlNoTransform();
		}

		@Override
		protected boolean isAccessControlAllowOriginWildcard(
				final MediaPath.Locator locator)
		{
			return
					assertConfigMethod(locator).
							getAccessControlAllowOriginWildcard();
		}

		@Override
		protected boolean doFlushBufferOnNotModified(
				final MediaPath.Locator locator)
		{
			assertConfigMethod(locator);
			return true;
		}

		private static MediaPathItem assertConfigMethod(
				final MediaPath.Locator locator)
		{
			final MediaPath path = locator.getFeature();
			final Item item = locator.getItem();
			assertTrue(MODEL.hasCurrentTransaction());
			assertEquals(MediaPathItem.TYPE, path.getType());
			assertNotNull(item);
			assertEquals("MediaPathItem-0", item.getCopeID());
			return (MediaPathItem)item;
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
