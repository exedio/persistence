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

import static com.exedio.cope.AbstractRuntimeTest.assertTestAnnotation;
import static com.exedio.cope.AbstractRuntimeTest.assertTestAnnotationNull;
import static com.exedio.cope.pattern.RecursiveItem.TYPE;
import static com.exedio.cope.pattern.RecursiveItem.testPattern;
import static com.exedio.cope.pattern.RecursiveItem.testPattern2;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import org.junit.jupiter.api.Test;

public class RecursiveTest extends TestWithEnvironment
{
	public static final Model MODEL = new Model(TYPE);

	public RecursiveTest()
	{
		super(MODEL);
	}

	@Test void testIt()
	{
		// type
		assertEqualsUnmodifiable(
				list(
					TYPE.getThis(),
					testPattern,
					testPattern.media,
					testPattern.media.getBody(),
					testPattern.media.getContentType(),
					testPattern.media.getLastModified(),
					testPattern.media.getUnison(),
					testPattern.fetch,
					testPattern2,
					testPattern2.media,
					testPattern2.media.getBody(),
					testPattern2.media.getContentType(),
					testPattern2.media.getLastModified(),
					testPattern2.media.getUnison(),
					testPattern2.fetch
				),
				TYPE.getFeatures());

		assertSame(testPattern, TYPE.getFeature("testPattern"));
		assertSame(testPattern2, TYPE.getFeature("testPattern2"));

		assertEqualsUnmodifiable(list(
					testPattern.media,
					testPattern.fetch
				), testPattern.getSourceFeatures());
		assertEqualsUnmodifiable(list(
					testPattern2.media,
					testPattern2.fetch
				), testPattern2.getSourceFeatures());

		assertEqualsUnmodifiable(list(
					testPattern.media.getBody(),
					testPattern.media.getContentType(),
					testPattern.media.getLastModified(),
					testPattern.media.getUnison()
				), testPattern.media.getSourceFeatures());
		assertEqualsUnmodifiable(list(
					testPattern2.media.getBody(),
					testPattern2.media.getContentType(),
					testPattern2.media.getLastModified(),
					testPattern2.media.getUnison()
			), testPattern2.media.getSourceFeatures());

		assertSame(testPattern,  testPattern .media.getPattern());
		assertSame(testPattern,  testPattern .fetch.getPattern());
		assertSame(testPattern2, testPattern2.media.getPattern());
		assertSame(testPattern2, testPattern2.fetch.getPattern());
		assertSame(testPattern .media, testPattern .media.getBody        ().getPattern());
		assertSame(testPattern .media, testPattern .media.getContentType ().getPattern());
		assertSame(testPattern .media, testPattern .media.getLastModified().getPattern());
		assertSame(testPattern2.media, testPattern2.media.getBody        ().getPattern());
		assertSame(testPattern2.media, testPattern2.media.getContentType ().getPattern());
		assertSame(testPattern2.media, testPattern2.media.getLastModified().getPattern());

		assertSame(TYPE, testPattern .media.getType());
		assertSame(TYPE, testPattern .fetch.getType());
		assertSame(TYPE, testPattern2.media.getType());
		assertSame(TYPE, testPattern2.fetch.getType());
		assertSame(TYPE, testPattern .media.getBody        ().getType());
		assertSame(TYPE, testPattern .media.getContentType ().getType());
		assertSame(TYPE, testPattern .media.getLastModified().getType());
		assertSame(TYPE, testPattern2.media.getBody        ().getType());
		assertSame(TYPE, testPattern2.media.getContentType ().getType());
		assertSame(TYPE, testPattern2.media.getLastModified().getType());

		assertSame("testPattern-media" , testPattern .media.getName());
		assertSame("testPattern-fetch" , testPattern .fetch.getName());
		assertSame("testPattern2-media", testPattern2.media.getName());
		assertSame("testPattern2-fetch", testPattern2.fetch.getName());
		assertSame("testPattern-media-body"         , testPattern .media.getBody        ().getName());
		assertSame("testPattern-media-contentType"  , testPattern .media.getContentType ().getName());
		assertSame("testPattern-media-lastModified" , testPattern .media.getLastModified().getName());
		assertSame("testPattern2-media-body"        , testPattern2.media.getBody        ().getName());
		assertSame("testPattern2-media-contentType" , testPattern2.media.getContentType ().getName());
		assertSame("testPattern2-media-lastModified", testPattern2.media.getLastModified().getName());

		assertSame("RecursiveItem.testPattern-media" , testPattern .media.getID());
		assertSame("RecursiveItem.testPattern-fetch" , testPattern .fetch.getID());
		assertSame("RecursiveItem.testPattern2-media", testPattern2.media.getID());
		assertSame("RecursiveItem.testPattern2-fetch", testPattern2.fetch.getID());
		assertSame("RecursiveItem.testPattern-media-body"         , testPattern .media.getBody        ().getID());
		assertSame("RecursiveItem.testPattern-media-contentType"  , testPattern .media.getContentType ().getID());
		assertSame("RecursiveItem.testPattern-media-lastModified" , testPattern .media.getLastModified().getID());
		assertSame("RecursiveItem.testPattern2-media-body"        , testPattern2.media.getBody        ().getID());
		assertSame("RecursiveItem.testPattern2-media-contentType" , testPattern2.media.getContentType ().getID());
		assertSame("RecursiveItem.testPattern2-media-lastModified", testPattern2.media.getLastModified().getID());

		assertTestAnnotationNull(PatternTestItem.testPattern.ownString);
		assertTestAnnotationNull(PatternTestItem.testPattern2.ownString);
		assertTestAnnotation("ownIntAnn",  PatternTestItem.testPattern.ownInt);
		assertTestAnnotation("ownItemAnn", PatternTestItem.testPattern.getOwnItem());
		assertTestAnnotation("ownIntAnn",  PatternTestItem.testPattern2.ownInt);
		assertTestAnnotation("ownItemAnn", PatternTestItem.testPattern2.getOwnItem());

		final RecursiveItem item = new RecursiveItem();
		assertEquals(null, testPattern.getLocator(item));

		assertFalse(item.fetch());
		assertEquals(null, testPattern.getLocator(item));

		item.setTestPattern("image/png");
		assertEquals(null, testPattern.getLocator(item));

		assertTrue(item.fetch());
		assertEquals("RecursiveItem/testPattern-media/" + item + ".png", testPattern.getLocator(item).toString());
	}
}
