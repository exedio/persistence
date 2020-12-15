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

import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.MysqlExtendedVarchar;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.misc.Computed;
import java.lang.annotation.Annotation;
import org.junit.jupiter.api.Test;

public class UniqueHashedMediaHashAnnotationTest
{
	@Test void testComputed()
	{
		assertPresent(false, AnItem.shorter,            Computed.class);
		assertPresent(false, AnItem.longer,             Computed.class);
		assertPresent(true,  AnItem.shorter.getMedia(), Computed.class);
		assertPresent(true,  AnItem.longer .getMedia(), Computed.class);
		assertPresent(true,  AnItem.shorter.getHash(),  Computed.class);
		assertPresent(true,  AnItem.longer .getHash(),  Computed.class);
	}

	@Test void testLength()
	{
		assertEquals(32, AnItem.shorter.getHash().getMaximumLength());
		assertEquals(40, AnItem.longer .getHash().getMaximumLength());
	}

	@Test void testMysqlExtendedVarchar()
	{
		assertPresent(false, AnItem.shorter,            MysqlExtendedVarchar.class);
		assertPresent(false, AnItem.longer,             MysqlExtendedVarchar.class);
		assertPresent(false, AnItem.shorter.getMedia(), MysqlExtendedVarchar.class);
		assertPresent(false, AnItem.longer .getMedia(), MysqlExtendedVarchar.class);
		assertPresent(false, AnItem.shorter.getHash(),  MysqlExtendedVarchar.class);
		assertPresent(true,  AnItem.longer .getHash(),  MysqlExtendedVarchar.class);
	}

	private static void assertPresent(
			final boolean expected,
			final Feature feature,
			final Class<? extends Annotation> annotationClass)
	{
		assertEquals(expected, feature.isAnnotationPresent(annotationClass));
		final Annotation ann = feature.getAnnotation(annotationClass);
		if(expected)
			assertNotNull(ann);
		else
			assertNull(ann);
	}


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@WrapperIgnore static final UniqueHashedMedia shorter = new UniqueHashedMedia(new Media(), "MD5");
		@WrapperIgnore static final UniqueHashedMedia longer  = new UniqueHashedMedia(new Media(), "SHA");

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
