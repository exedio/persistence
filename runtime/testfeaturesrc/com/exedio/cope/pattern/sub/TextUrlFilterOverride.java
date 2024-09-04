/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern.sub;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.TextUrlFilter;
import java.io.Serial;
import java.nio.charset.Charset;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public final class TextUrlFilterOverride extends TextUrlFilter
{
	@Serial
	private static final long serialVersionUID = 1l;

	public TextUrlFilterOverride(
			final Media raw,
			final String supportedContentType,
			final Charset charset,
			final StringField pasteKey,
			final Media pasteValue)
	{
		super(raw, supportedContentType, charset, "<paste>", "</paste>", pasteKey, pasteValue);
	}

	@Override
	public Set<String> check( final Item item ) throws NotFound
	{
		final Set<String> check = super.check( item );
		check.remove( EXTRA );
		return check;
	}

	@Override
	protected void appendKey(
			final StringBuilder bf,
			final Item item,
			final String key,
			final HttpServletRequest request)
	{
		assertNotNull(item);
		assertNotNull(request);
		if(EXTRA.equals(key))
			bf.append("<extra/>");
		else
			super.appendKey(bf, item, key, request);
	}

	private static final String EXTRA = "EXTRA";

	@Override
	protected void appendURL(
			final StringBuilder bf,
			final Paste paste,
			final HttpServletRequest request)
	{
		assertNotNull(paste.getLocator());
		assertNotNull(paste.getURL());
		bf.append("<override>");
		super.appendURL(bf, paste, request);
		bf.append("</override>");
	}
}
