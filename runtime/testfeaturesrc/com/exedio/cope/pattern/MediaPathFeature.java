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

import com.exedio.cope.Condition;
import com.exedio.cope.DateField;
import com.exedio.cope.EnumField;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.Wrap;
import java.io.IOException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class MediaPathFeature extends MediaPath
{
	private static final long serialVersionUID = 1l;

	enum Result
	{
		notFoundIsNull, notFoundNotComputable, IOException, RuntimeException;
	}

	private boolean isFinal = false;

	void reset()
	{
		isFinal = false;
	}

	private final StringField contentType = new StringField().optional();
	private final DateField lastModified = new DateField().optional();
	private final EnumField<Result> result = EnumField.create(Result.class).optional();

	public MediaPathFeature()
	{
		addSource(result, "result");
		addSource(contentType, "contentType");
		addSource(lastModified, "lastModified");
	}

	public void setFinal(final boolean isFinal)
	{
		this.isFinal = isFinal;
	}

	@Wrap(order=10)
	public void setContentType(final Item item, final String contentType)
	{
		this.contentType.set(item, contentType);
	}

	@Wrap(order=20)
	public void setLastModified(final Item item, final Date lastModified)
	{
		this.lastModified.set(item, lastModified);
	}

	@Wrap(order=30)
	public void setResult(final Item item, final Result bodyFailure)
	{
		this.result.set(item, bodyFailure);
	}


	@Override
	public boolean isFinal()
	{
		return isFinal;
	}

	@Override
	public String getContentType(final Item item)
	{
		return contentType.get(item);
	}

	@Override
	public Date getLastModified(final Item item)
	{
		return lastModified.get(item);
	}

	@Override
	public void doGetAndCommit(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Item item)
		throws IOException, NotFound
	{
		final Result failure = result.get(item);
		if(failure!=null)
		{
			switch(failure)
			{
				case notFoundIsNull:        throw notFoundIsNull();
				case notFoundNotComputable: throw notFoundNotComputable();
				case IOException:           throw new IOException("test IOException");
				case RuntimeException:      throw new RuntimeException("test RuntimeException");
				default:
					throw new AssertionError();
			}
		}
		commit();
		response.setContentLength(10011);
	}

	@Override
	public Condition isNull()
	{
		throw new AssertionError();
	}

	@Override
	public Condition isNull(final Join join)
	{
		throw new AssertionError();
	}

	@Override
	public Condition isNotNull()
	{
		throw new AssertionError();
	}

	@Override
	public Condition isNotNull(final Join join)
	{
		throw new AssertionError();
	}
}
