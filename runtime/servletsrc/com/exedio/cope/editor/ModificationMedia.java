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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import com.exedio.cope.Item;
import com.exedio.cope.pattern.Media;

final class ModificationMedia extends Modification
{
	private static final long serialVersionUID = 1l;
	
	final FileItem value;
	
	ModificationMedia(final Media feature, final Item item)
	{
		this(feature, item, null);
	}
	
	ModificationMedia(final Media feature, final Item item, final FileItem value)
	{
		super(feature, item);
		this.value = value;
	}
	
	@Override
	Media getFeature()
	{
		return (Media)super.getFeature();
	}
	
	String getURL(final HttpServletRequest request, final HttpServletResponse response)
	{
		return
			request.getContextPath() +
			request.getServletPath() +
			response.encodeURL(
					Editor.LOGIN_PATH_INFO +
					'?' + Editor.MEDIA_FEATURE + '=' + getFeature().getID() +
					'&' + Editor.MEDIA_ITEM + '=' + item.getCopeID());
	}
	
	@Override
	void publish()
	{
		try
		{
			getFeature().set(item, Media.toValue(value.getInputStream(), value.getContentType()));
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@Override
	void saveTo(final Draft draft)
	{
		System.out.println("TODO saving media to drafts is not yet implemented"); // TODO
	}
}
