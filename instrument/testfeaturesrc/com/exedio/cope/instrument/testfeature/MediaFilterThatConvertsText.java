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

package com.exedio.cope.instrument.testfeature;

import static com.exedio.cope.pattern.MediaType.JPEG;

import com.exedio.cope.Item;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaFilter;
import java.io.Serial;
import java.util.Collections;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MediaFilterThatConvertsText extends MediaFilter
{
	@Serial
	private static final long serialVersionUID=1L;

	private static final String TEXT_PLAIN = "text/plain";

	public MediaFilterThatConvertsText(final Media source)
	{
		super(source);
	}

	@Override
	public Set<String> getSupportedSourceContentTypes()
	{
		return Collections.singleton(TEXT_PLAIN);
	}

	@Override
	public String getContentType(final Item item)
	{
		return TEXT_PLAIN.equals(getSource().getContentType(item)) ? JPEG : null;
	}

	@Override
	public void doGetAndCommit(final HttpServletRequest request, final HttpServletResponse response, final Item item)
	{
		throw new RuntimeException("not implemented");
	}
}
