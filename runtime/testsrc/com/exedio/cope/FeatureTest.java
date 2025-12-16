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

package com.exedio.cope;

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class FeatureTest
{
	@Test void testType()
	{
		final StringField f = new StringField().lengthRange(5, 8);

		assertEqualsUnmodifiable(asList(), f.getSuspicions());
		assertFails(
				f::getType,
				IllegalStateException.class,
				"feature not mounted");
		assertFails(
				f::getName,
				IllegalStateException.class,
				"feature not mounted");
		assertFails(
				f::getID,
				IllegalStateException.class,
				"feature not mounted");
		assertFails(
				() -> f.getAnnotation(Override.class),
				IllegalStateException.class,
				"feature not mounted");
		assertTrue(f.toString().startsWith("com.exedio.cope.StringField@"));
		assertTrue(toString(f, null).startsWith("com.exedio.cope.StringField@"));
		assertEquals(5, f.getMinimumLength());
		assertEquals(8, f.getMaximumLength());

		final Features features = new Features();
		features.put("featureName", f);
		final Type<AnItem> t = new Type<>(AnItem.class, AnItem::new, AnItem.class, false, "typeId", null, null, null, features);

		assertSame(t, f.getType());
		assertEquals("featureName", f.getName());
		assertEquals("typeId.featureName", f.getID());
		assertEquals(null, f.getAnnotation(Override.class));
		assertEquals("typeId.featureName", f.toString());
		assertEquals("typeId.featureName", toString(f, null));
		assertEquals("featureName", toString(f, t));
		assertEquals(5, f.getMinimumLength());
		assertEquals(8, f.getMaximumLength());

		assertFails(
				() -> new Type<>(AnItem.class, AnItem::new, AnItem.class, false, "typeId", null, null, null, features),
				IllegalStateException.class,
				"feature already mounted: typeId.featureName");
	}

	private static String toString(final Feature f, final Type<?> defaultType)
	{
		final StringBuilder sb = new StringBuilder();
		f.toString(sb, defaultType);
		return sb.toString();
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
