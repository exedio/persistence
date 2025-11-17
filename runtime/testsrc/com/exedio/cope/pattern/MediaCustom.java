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

package com.exedio.cope.pattern;

import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapInterim;
import java.io.Serial;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A test subclass of MediaPath for unit-testing custom extensions of MediaPath.
 * @author Ralf Wiebicke
 */
final class MediaCustom extends MediaPath
{
	@Serial
	private static final long serialVersionUID = 1l;

	@WrapInterim
	private final StringField source;

	@WrapInterim
	MediaCustom(final StringField source)
	{
		this.source = source;
	}

	StringField getSource()
	{
		return source;
	}

	@Override
	@WrapInterim
	public boolean isMandatory()
	{
		return false;
	}

	@Override
	public String getContentType(final Item item)
	{
		return source.get(item)!=null ? "text/plain" : null;
	}

	@Override
	public void doGetAndCommit(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Item item)
	{
		throw new RuntimeException();
	}
}
