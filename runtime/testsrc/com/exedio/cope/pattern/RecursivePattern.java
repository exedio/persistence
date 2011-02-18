/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.StringField;

class RecursivePattern extends Pattern
{
	private static final long serialVersionUID = 1l;

	final Media media = new Media().optional();
	final StringField fetch = new StringField().optional();

	RecursivePattern()
	{
		addSource(media, "media");
		addSource(fetch, "fetch");
	}

	void set(final Item item, final String value)
	{
		fetch.set(item, value);
	}

	boolean fetch(final Item item)
	{
		final String f = fetch.get(item);
		if(f==null)
			return false;
		media.set(item, new byte[]{3, 5, 7, 9}, f);
		return true;
	}

	MediaPath.Locator getLocator(final Item item)
	{
		return media.getLocator(item);
	}
}
