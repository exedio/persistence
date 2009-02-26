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

package com.exedio.cope.editor;

import com.exedio.cope.Item;
import com.exedio.cope.StringField;

final class TargetNewDraft implements Target
{
	static final TargetNewDraft INSTANCE = new TargetNewDraft();
	static final String ID = "New";
	
	private TargetNewDraft()
	{
		// prevent public instantiation
	}
	
	public String getID()
	{
		return ID;
	}
	
	public boolean exists()
	{
		return true;
	}
	
	public String getDescription()
	{
		return "New Draft";
	}
	
	public boolean isLive()
	{
		return false;
	}
	
	public String get(final StringField feature, final Item item)
	{
		return null;
	}
	
	public void save(final Anchor anchor)
	{
		final Draft draft = new Draft(anchor.user, anchor.sessionName, "new draft");
		for(final Modification m : anchor.getModifications())
			m.saveTo(draft);
		anchor.setTarget(new TargetDraft(draft));
		// TODO maintain some special draft history
	}
	
	@Override
	public int hashCode()
	{
		return getClass().hashCode();
	}
	
	@Override
	public boolean equals(final Object other)
	{
		return (other!=null) && (other instanceof TargetNewDraft);
	}
	
	private static final long serialVersionUID = 1l;
}
