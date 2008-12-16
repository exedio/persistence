/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.editor;

import java.io.Serializable;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;

final class Preview implements Serializable // for session persistence
{
	private static final long serialVersionUID = 1l;
	
	private final String feature;
	private final Item item;
	
	Preview(final StringField feature, final Item item)
	{
		this.feature = feature.getID(); // id is serializable
		this.item = item;
		
		assert feature!=null;
		assert item!=null;
	}
	
	String getOldValue(final Model model)
	{
		return ((StringField)model.getFeature(feature)).get(item);
	}
	
	void save(final Model model, final String value)
	{
		((StringField)model.getFeature(feature)).set(item, value);
	}
	
	@Override
	public int hashCode()
	{
		return feature.hashCode() ^ item.hashCode();
	}
	
	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof Preview))
			return false;
		
		final Preview o = (Preview)other;
		return feature.equals(o.feature) && item.equals(o.item);
	}
}
