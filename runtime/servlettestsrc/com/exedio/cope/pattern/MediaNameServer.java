/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.util.CharsetName.UTF8;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.StringField;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A test subclass of MediaPath for unit-testing custom extentions of MediaPath.
 * @author Ralf Wiebicke
 */
final class MediaNameServer extends MediaPath
{
	private static final long serialVersionUID = 1l;

	final StringField source;

	MediaNameServer(final StringField source)
	{
		this.source = source;
		if(source!=null)
			addSource(source, "Source");
	}

	StringField getSource()
	{
		return source;
	}

	@Override
	public String getContentType(final Item item)
	{
		return source.get(item)!=null ? "text/plain" : null;
	}

	@Override
	public void doGetAndCommit(
			final HttpServletRequest request, final HttpServletResponse response,
			final Item item)
		throws IOException, NotFound
	{
		final String content = source.get(item);

		commit();

		//System.out.println("contentType="+contentType);
		if(content==null)
			throw notFoundIsNull();

		if(content.endsWith(" error"))
			throw new RuntimeException("test error in MediaNameServer");

		MediaUtil.send("text/plain", UTF8, content, response);
	}

	@Override
	public Condition isNull()
	{
		return source.isNull();
	}

	@Override
	public Condition isNull(final Join join)
	{
		return source.bind( join ).isNull();
	}

	@Override
	public Condition isNotNull()
	{
		return source.isNotNull();
	}

	@Override
	public Condition isNotNull(final Join join)
	{
		return source.bind(join).isNotNull();
	}
}
