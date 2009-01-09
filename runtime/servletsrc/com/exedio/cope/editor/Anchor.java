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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.exedio.cope.Item;
import com.exedio.cope.StringField;

final class Anchor implements Serializable // for session persistence
{
	private static final long serialVersionUID = 1l;
	
	final String user;
	final Session session;
	final String sessionName;
	private Target target = LiveSite.INSTANCE;
	boolean borders = false;
	private final LinkedHashMap<Modification, String> modifications = new LinkedHashMap<Modification, String>();
	
	Anchor(final String user, final Session session, final String sessionName)
	{
		this.user = user;
		this.session = session;
		this.sessionName = sessionName;
		assert user!=null;
		assert session!=null;
	}
	
	Target getTarget()
	{
		return target;
	}
	
	void setTarget(final Target target)
	{
		if(target==null)
			throw new NullPointerException();
		this.target = target;
	}
	
	int getModificationsCount()
	{
		return modifications.size();
	}
	
	String getPreview(final StringField feature, final Item item)
	{
		if(modifications.isEmpty()) // shortcut
			return null;
		
		return modifications.get(new Modification(feature, item));
	}
	
	Map<Modification, String> getModifications()
	{
		return Collections.unmodifiableMap(modifications);
	}
	
	HashMap<Modification, String> getPreviewsModifiable()
	{
		return modifications;
	}
	
	void setPreview(final String content, final StringField feature, final Item item)
	{
		modifications.put(new Modification(feature, item), content);
	}
	
	void notifyPublished(final StringField feature, final Item item)
	{
		modifications.remove(new Modification(feature, item));
	}
	
	String getHistoryAuthor()
	{
		return (sessionName!=null ? sessionName : user) + " (CCE)";
	}
	
	@Override
	public String toString()
	{
		final StringBuilder bf = new StringBuilder();
		
		// must not call login#getName() here,
		// because this may require a transaction,
		// which may not be present,
		// especially when this method is called by lamdba probe.
		if(sessionName!=null)
			bf.append('"').append(sessionName).append('"');
		else
			bf.append(user);
		
		if(borders)
			bf.append(" bordered");
		
		final int previewNumber = modifications.size();
		if(previewNumber>0)
		{
			bf.append(" *");
			if(previewNumber>1)
				bf.append(previewNumber);
		}
		
		return bf.toString();
	}
}
