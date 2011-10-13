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

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.StringField;

public final class TextUrlFilterOverride extends TextUrlFilter
{
	private static final long serialVersionUID = 1l;

	public TextUrlFilterOverride(
			final Media raw,
			final String supportedContentType,
			final String encoding,
			final String pasteStart,
			final String pasteStop,
			final StringField pasteKey,
			final Media pasteValue)
	{
		super(raw, supportedContentType, encoding, pasteStart, pasteStop, pasteKey, pasteValue);
	}

	@Override
	void appendURL(
			final StringBuilder sb,
			final Paste paste,
			final HttpServletRequest request)
	{
		sb.append("<override>");
		super.appendURL(sb, paste, request);
		sb.append("</override>");
	}
}
