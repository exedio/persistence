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

import static java.util.Objects.requireNonNull;
import static javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Specifies a http redirect (moved permanently) to
 * a {@link Media}.
 * <p>
 * Common usage is to maintain old urls after renaming a {@link Media}.
 * For instance, if there is a media <tt>picture</tt>:
 *
 * <pre>
 * static final Media picture = new Media(OPTIONAL);
 * </pre>
 * and this media is renamed to <tt>image</tt>:
 *
 * <pre>
 * static final Media image = new Media(OPTIONAL);
 * </pre>
 * then old urls created by <tt>picture</tt>
 * can be supported with an additional:
 *
 * <pre>
 * static final MediaRedirect picture = new MediaRedirect(image);
 * </pre>
 *
 * @author Ralf Wiebicke
 */
public final class MediaRedirect extends MediaPath
{
	private static final long serialVersionUID = 1l;

	private final MediaPath target;

	/**
	 * @deprecated Use {@link RedirectFrom} instead
	 */
	@Deprecated
	public MediaRedirect(final MediaPath target)
	{
		this.target = requireNonNull(target, "target");
	}

	public MediaPath getTarget()
	{
		return target;
	}

	@Override
	public boolean isMandatory()
	{
		return target.isMandatory();
	}

	@Override
	public String getContentType(final Item item)
	{
		return target.getContentType(item);
	}

	@Override
	public boolean isContentTypeWrapped()
	{
		return target.isContentTypeWrapped();
	}

	private static final String RESPONSE_LOCATION = "Location";

	@Override
	public void doGetAndCommit(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Item item)
	throws NotFound
	{
		final Locator locator = target.getLocator(item);
		commit();
		if(locator==null)
			throw notFoundIsNull();

		final StringBuilder location = new StringBuilder();
		location.
			// There is no need for absolute url anymore: https://en.wikipedia.org/wiki/HTTP_location
			append(request.getContextPath()).
			append(request.getServletPath()).
			append('/');
		locator.appendPath(location);
		//System.out.println("location="+location);

		response.setStatus(SC_MOVED_PERMANENTLY);
		response.setHeader(RESPONSE_LOCATION, location.toString());
	}

	@Override
	public Condition isNull()
	{
		return target.isNull();
	}

	@Override
	public Condition isNull(final Join join)
	{
		return target.isNull(join);
	}

	@Override
	public Condition isNotNull()
	{
		return target.isNotNull();
	}

	@Override
	public Condition isNotNull(final Join join)
	{
		return target.isNotNull(join);
	}
}
