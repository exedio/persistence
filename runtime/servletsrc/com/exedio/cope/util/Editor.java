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

package com.exedio.cope.util;

import java.io.PrintStream;

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.editor.Session;
import com.exedio.cope.pattern.MapField;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaFilter;

/**
 * @deprecated Use {@link com.exedio.cope.editor.Editor} instead
 */
@Deprecated
public final class Editor
{
	private Editor()
	{
		// prevent instantiation
	}
	
	public static final boolean isLoggedIn()
	{
		return com.exedio.cope.editor.Editor.isLoggedIn();
	}
	
	public static final Session getSession()
	{
		return com.exedio.cope.editor.Editor.getSession();
	}
	
	public static final <K> String edit(final String content, final MapField<K, String> feature, final Item item, final K key)
	{
		return com.exedio.cope.editor.Editor.edit(content, feature, item, key);
	}
	
	public static final String edit(final String content, final StringField feature, final Item item)
	{
		return com.exedio.cope.editor.Editor.edit(content, feature, item);
	}
	
	public static final String edit(final Media feature, final Item item)
	{
		return com.exedio.cope.editor.Editor.edit(feature, item);
	}
	
	public static final String edit(final MediaFilter feature, final Item item)
	{
		return com.exedio.cope.editor.Editor.edit(feature, item);
	}
	
	public static final String edit(final IntegerField feature, final Item item)
	{
		return com.exedio.cope.editor.Editor.edit(feature, item);
	}
	
	public static final void writeBar(final PrintStream out)
	{
		com.exedio.cope.editor.Editor.writeBar(out);
	}
	
	public static final void writeBar(final StringBuilder out)
	{
		com.exedio.cope.editor.Editor.writeBar(out);
	}
	
	// ------------------- deprecated stuff -------------------
	
	/**
	 * @deprecated Use {@link #isLoggedIn()} instead
	 */
	@Deprecated
	public static final boolean isActive()
	{
		return isLoggedIn();
	}
	
	/**
	 * @deprecated use {@link #edit(String, MapField, Item, Object)} instead.
	 */
	@Deprecated
	public static final <K> String editBlock(final String content, final MapField<K, String> feature, final Item item, final K key)
	{
		return edit(content, feature, item, key);
	}
	
	/**
	 * @deprecated Use {@link #getSession()} instead
	 */
	@Deprecated
	public static final Session getLogin()
	{
		return getSession();
	}
}
