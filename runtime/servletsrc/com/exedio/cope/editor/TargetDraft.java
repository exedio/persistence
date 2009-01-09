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

import java.util.Map;

import com.exedio.cope.Item;
import com.exedio.cope.StringField;

final class TargetDraft implements Target
{
	private final Draft draft;
	
	TargetDraft(final Draft draft)
	{
		if(draft==null)
			throw new NullPointerException();
		
		this.draft = draft;
	}
	
	public String getID()
	{
		return draft.getCopeID();
	}
	
	public String getDescription()
	{
		return "Draft \"" + draft.getComment() + "\" by " + draft.getAuthor();
	}
	
	public boolean isLive()
	{
		return false;
	}
	
	public String get(final StringField feature, final Item item)
	{
		final DraftItem i = DraftItem.forParentFeatureAndItem(draft, feature, item);
		return i!=null ? i.getNewValue() : null;
	}
	
	public void save(final Map<Modification, String> modifications)
	{
		for(final Map.Entry<Modification, String> e : modifications.entrySet())
			e.getKey().saveTo(draft, e.getValue());
		// TODO maintain some special draft history
	}
	
	@Override
	public int hashCode()
	{
		return draft.hashCode() ^ 238652836;
	}
	
	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof TargetDraft))
			return false;

		return draft.equals(((TargetDraft)other).draft);
	}
	
	private static final long serialVersionUID = 1l;
}
