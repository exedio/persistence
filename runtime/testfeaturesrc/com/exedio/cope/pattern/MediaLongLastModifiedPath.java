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

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.LongField;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.Wrap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.Serial;
import java.util.Date;

/**
 * This feature allows testing {@link MediaPath#getLastModified(Item)} with values,
 * that are not allowed by {@link com.exedio.cope.DateField}.
 */
public final class MediaLongLastModifiedPath extends MediaPath
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final StringField contentType = new StringField().optional();
	private final LongField lastModified = new LongField().optional();

	public MediaLongLastModifiedPath()
	{
		addSourceFeature(contentType, "contentType");
		addSourceFeature(lastModified, "lastModified");
	}

	@Wrap(order=10)
	public void setContentType(final Item item, final String contentType)
	{
		this.contentType.set(item, contentType);
	}

	@Wrap(order=20)
	public void setLastModified(final Item item, final long lastModified)
	{
		this.lastModified.set(item, lastModified);
	}


	@Override
	public boolean isMandatory()
	{
		return true;
	}

	@Override
	public String getContentType(final Item item)
	{
		return contentType.get(item);
	}

	@Override
	public Date getLastModified(final Item item)
	{
		return new Date(lastModified.get(item));
	}

	@Override
	public void doGetAndCommit(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Item item)
	{
		throw new AssertionError();
	}

	@Override
	public Condition isNull()
	{
		throw new AssertionError();
	}

	@Override
	public Condition isNotNull()
	{
		throw new AssertionError();
	}
}
