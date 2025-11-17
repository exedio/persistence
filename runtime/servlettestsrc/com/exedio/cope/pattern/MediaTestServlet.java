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

import static java.nio.charset.StandardCharsets.US_ASCII;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.Serial;

public final class MediaTestServlet extends MediaServlet
{
	@Serial
	private static final long serialVersionUID = 1l;

	@Override
	protected void onException(
			final HttpServletRequest request,
			final Exception exception)
	{
		super.onException(request, exception);
		final File file = new File("MediaTestServlet.log");
		try(PrintStream out = new PrintStream(new FileOutputStream(file, true), false, US_ASCII))
		{
			out.println(exception.getClass().getName());
			out.println(exception.getMessage());
		}
		catch(final FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}
}
