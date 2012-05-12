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

import static com.exedio.cope.TypesBound.newType;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.junit.CopeAssert;

public class TextUrlFilterAnnotationTest extends CopeAssert
{
	public void testIt()
	{
		newType(AnItem.class);

		assertFalse(pasteValue(AnItem.simple).isAnnotationPresent(PreventUrlGuessing.class));
		assertFalse(AnItem.simple.getSource().isAnnotationPresent(PreventUrlGuessing.class));
		assertTrue (pasteValue(AnItem.secret).isAnnotationPresent(PreventUrlGuessing.class));
		assertTrue (AnItem.secret.getSource().isAnnotationPresent(PreventUrlGuessing.class));

		assertNull   (pasteValue(AnItem.simple).getAnnotation(PreventUrlGuessing.class));
		assertNull   (AnItem.simple.getSource().getAnnotation(PreventUrlGuessing.class));
		assertNotNull(pasteValue(AnItem.secret).getAnnotation(PreventUrlGuessing.class));
		assertNotNull(AnItem.secret.getSource().getAnnotation(PreventUrlGuessing.class));

		assertFalse(pasteValue(AnItem.simple).isAnnotationPresent(Deprecated.class));
		assertFalse(AnItem.simple.getSource().isAnnotationPresent(Deprecated.class));
		assertFalse(pasteValue(AnItem.secret).isAnnotationPresent(Deprecated.class));
		assertFalse(AnItem.secret.getSource().isAnnotationPresent(Deprecated.class));

		assertNull(pasteValue(AnItem.simple).getAnnotation(Deprecated.class));
		assertNull(AnItem.simple.getSource().getAnnotation(Deprecated.class));
		assertNull(pasteValue(AnItem.secret).getAnnotation(Deprecated.class));
		assertNull(AnItem.secret.getSource().getAnnotation(Deprecated.class));
	}

	static final class AnItem extends Item
	{
		private static final long serialVersionUID = 1l;

		private AnItem(final ActivationParameters ap)
		{
			super(ap);
		}

		static final TextUrlFilter simple = new ATextUrlFilter(new Media(), new Media());
		@PreventUrlGuessing
		static final TextUrlFilter secret = new ATextUrlFilter(new Media(), new Media());
	}

	static final class ATextUrlFilter extends TextUrlFilter
	{
		private static final long serialVersionUID = 1l;

		public ATextUrlFilter(final Media raw, final Media pasteValue)
		{
			super(raw, "text/plain", "utf-8", "<paste>", "</paste>", new StringField(), pasteValue);
		}
	}

	private static Media pasteValue(final TextUrlFilter filter)
	{
		final Type type = filter.getSourceTypes().get(0);
		assertNotNull(type);
		final Media value = (Media)type.getFeature("value");
		assertNotNull(value);
		return value;
	}
}
