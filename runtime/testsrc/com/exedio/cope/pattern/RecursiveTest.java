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

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Model;

public class RecursiveTest extends AbstractRuntimeTest
{
	public static final Model MODEL = new Model(RecursiveItem.TYPE);

	public RecursiveTest()
	{
		super(MODEL);
	}

	public void testIt()
	{
		// type
		assertEqualsUnmodifiable(
				list(
					RecursiveItem.TYPE.getThis(),
					RecursiveItem.testPattern,
					RecursiveItem.testPattern.media,
					RecursiveItem.testPattern.media.getBody(),
					RecursiveItem.testPattern.media.getContentType(),
					RecursiveItem.testPattern.media.getLastModified(),
					RecursiveItem.testPattern.media.getUnison(),
					RecursiveItem.testPattern.fetch,
					RecursiveItem.testPattern2,
					RecursiveItem.testPattern2.media,
					RecursiveItem.testPattern2.media.getBody(),
					RecursiveItem.testPattern2.media.getContentType(),
					RecursiveItem.testPattern2.media.getLastModified(),
					RecursiveItem.testPattern2.media.getUnison(),
					RecursiveItem.testPattern2.fetch
				),
				RecursiveItem.TYPE.getFeatures());

		assertSame(RecursiveItem.testPattern, RecursiveItem.TYPE.getFeature("testPattern"));
		assertSame(RecursiveItem.testPattern2, RecursiveItem.TYPE.getFeature("testPattern2"));

		assertEqualsUnmodifiable(list(
					RecursiveItem.testPattern.media,
					RecursiveItem.testPattern.fetch
				), RecursiveItem.testPattern.getSourceFeatures());
		assertEqualsUnmodifiable(list(
					RecursiveItem.testPattern2.media,
					RecursiveItem.testPattern2.fetch
				), RecursiveItem.testPattern2.getSourceFeatures());

		assertSame(RecursiveItem.testPattern,  RecursiveItem.testPattern .media.getPattern());
		assertSame(RecursiveItem.testPattern,  RecursiveItem.testPattern .fetch.getPattern());
		assertSame(RecursiveItem.testPattern2, RecursiveItem.testPattern2.media.getPattern());
		assertSame(RecursiveItem.testPattern2, RecursiveItem.testPattern2.fetch.getPattern());
		assertSame(RecursiveItem.testPattern .media, RecursiveItem.testPattern .media.getBody        ().getPattern());
		assertSame(RecursiveItem.testPattern .media, RecursiveItem.testPattern .media.getContentType ().getPattern());
		assertSame(RecursiveItem.testPattern .media, RecursiveItem.testPattern .media.getLastModified().getPattern());
		assertSame(RecursiveItem.testPattern2.media, RecursiveItem.testPattern2.media.getBody        ().getPattern());
		assertSame(RecursiveItem.testPattern2.media, RecursiveItem.testPattern2.media.getContentType ().getPattern());
		assertSame(RecursiveItem.testPattern2.media, RecursiveItem.testPattern2.media.getLastModified().getPattern());

		assertSame(RecursiveItem.TYPE, RecursiveItem.testPattern .media.getType());
		assertSame(RecursiveItem.TYPE, RecursiveItem.testPattern .fetch.getType());
		assertSame(RecursiveItem.TYPE, RecursiveItem.testPattern2.media.getType());
		assertSame(RecursiveItem.TYPE, RecursiveItem.testPattern2.fetch.getType());
		assertSame(RecursiveItem.TYPE, RecursiveItem.testPattern .media.getBody        ().getType());
		assertSame(RecursiveItem.TYPE, RecursiveItem.testPattern .media.getContentType ().getType());
		assertSame(RecursiveItem.TYPE, RecursiveItem.testPattern .media.getLastModified().getType());
		assertSame(RecursiveItem.TYPE, RecursiveItem.testPattern2.media.getBody        ().getType());
		assertSame(RecursiveItem.TYPE, RecursiveItem.testPattern2.media.getContentType ().getType());
		assertSame(RecursiveItem.TYPE, RecursiveItem.testPattern2.media.getLastModified().getType());

		assertSame("testPattern-media" , RecursiveItem.testPattern .media.getName());
		assertSame("testPattern-fetch" , RecursiveItem.testPattern .fetch.getName());
		assertSame("testPattern2-media", RecursiveItem.testPattern2.media.getName());
		assertSame("testPattern2-fetch", RecursiveItem.testPattern2.fetch.getName());
		assertSame("testPattern-media-body"         , RecursiveItem.testPattern .media.getBody        ().getName());
		assertSame("testPattern-media-contentType"  , RecursiveItem.testPattern .media.getContentType ().getName());
		assertSame("testPattern-media-lastModified" , RecursiveItem.testPattern .media.getLastModified().getName());
		assertSame("testPattern2-media-body"        , RecursiveItem.testPattern2.media.getBody        ().getName());
		assertSame("testPattern2-media-contentType" , RecursiveItem.testPattern2.media.getContentType ().getName());
		assertSame("testPattern2-media-lastModified", RecursiveItem.testPattern2.media.getLastModified().getName());

		assertSame("RecursiveItem.testPattern-media" , RecursiveItem.testPattern .media.getID());
		assertSame("RecursiveItem.testPattern-fetch" , RecursiveItem.testPattern .fetch.getID());
		assertSame("RecursiveItem.testPattern2-media", RecursiveItem.testPattern2.media.getID());
		assertSame("RecursiveItem.testPattern2-fetch", RecursiveItem.testPattern2.fetch.getID());
		assertSame("RecursiveItem.testPattern-media-body"         , RecursiveItem.testPattern .media.getBody        ().getID());
		assertSame("RecursiveItem.testPattern-media-contentType"  , RecursiveItem.testPattern .media.getContentType ().getID());
		assertSame("RecursiveItem.testPattern-media-lastModified" , RecursiveItem.testPattern .media.getLastModified().getID());
		assertSame("RecursiveItem.testPattern2-media-body"        , RecursiveItem.testPattern2.media.getBody        ().getID());
		assertSame("RecursiveItem.testPattern2-media-contentType" , RecursiveItem.testPattern2.media.getContentType ().getID());
		assertSame("RecursiveItem.testPattern2-media-lastModified", RecursiveItem.testPattern2.media.getLastModified().getID());

		assertTestAnnotationNull(PatternTestItem.testPattern.ownString);
		assertTestAnnotationNull(PatternTestItem.testPattern2.ownString);
		assertTestAnnotation("ownIntAnn",  PatternTestItem.testPattern.ownInt);
		assertTestAnnotation("ownItemAnn", PatternTestItem.testPattern.getOwnItem());
		assertTestAnnotation("ownIntAnn",  PatternTestItem.testPattern2.ownInt);
		assertTestAnnotation("ownItemAnn", PatternTestItem.testPattern2.getOwnItem());

		final RecursiveItem item = deleteOnTearDown(new RecursiveItem());
		assertEquals(null, item.testPattern.getLocator(item));

		assertFalse(item.fetch());
		assertEquals(null, item.testPattern.getLocator(item));

		item.setTestPattern("image/png");
		assertEquals(null, item.testPattern.getLocator(item));

		assertTrue(item.fetch());
		assertEquals("RecursiveItem/testPattern-media/" + item + ".png", item.testPattern.getLocator(item).toString());
	}

	@Deprecated
	public void testDeprecated()
	{
		assertEqualsUnmodifiable(list(
					RecursiveItem.testPattern.fetch
				), RecursiveItem.testPattern.getSourceFields());
		assertEqualsUnmodifiable(list(
					RecursiveItem.testPattern2.fetch
				), RecursiveItem.testPattern2.getSourceFields());
	}
}
