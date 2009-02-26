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

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.editor.Editor;

public final class EditorFilter extends Editor
{
	public EditorFilter()
	{
		super(EditedServlet.model);
	}
	
	private static class Session implements com.exedio.cope.editor.Session, Serializable
	{
		private static final long serialVersionUID = 1l;
		
		private final String user;
		private final boolean nameIsNull;
		
		Session(final String user)
		{
			this.user = user;
			this.nameIsNull = "noname".equals(user);
		}
		
		public String getName()
		{
			return nameIsNull ? null : "getName(" + user + ')';
		}
		
		@Override
		public String toString()
		{
			return "toString(" + user + ')';
		}
	};
	
	@Override
	protected Session login(final String user, final String password)
	{
		if(password.equals(user+"1234"))
			return new Session(user);
		
		return null;
	}
	
	@Override
	protected String getBorderButtonURL(final HttpServletRequest request, final HttpServletResponse response, final boolean bordersEnabled)
	{
		return request.getContextPath() + "/border-" + (bordersEnabled ? "dis" : "en") + "able.png";
		//return null;
	}
	
	@Override
	protected String getHideButtonURL(final HttpServletRequest request, final HttpServletResponse response)
	{
		return request.getContextPath() + "/hide.png";
	}
	
	@Override
	protected String getCloseButtonURL(final HttpServletRequest request, final HttpServletResponse response)
	{
		return request.getContextPath() + "/close.png";
		//return null;
	}
	
	@Override
	protected String getPreviousPositionButtonURL(final HttpServletRequest request, final HttpServletResponse response)
	{
		return request.getContextPath() + "/previous.png";
		//return null;
	}
}
