/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Model;
import com.exedio.cope.util.ConnectToken;
import com.exedio.cope.util.ServletUtil;

public class InitServlet extends HttpServlet
{
	private static final long serialVersionUID = 1l;
	
	public static final Model model = new Model(MediaServletItem.TYPE);
	
	private ConnectToken connectToken = null;
	
	@Override
	public void init() throws ServletException
	{
		super.init();
		
		final Class thisClass = InitServlet.class;
		try
		{
			connectToken = ServletUtil.connect(model, getServletContext(), thisClass.getName());
			model.createDatabase();
			try
			{
				model.startTransaction(thisClass.getName());
				
				final MediaServletItem text = new MediaServletItem();
				assertID("MediaServletItem.0", text);
				text.setContent(thisClass.getResourceAsStream("dummy.txt"), "text/plain");
					
				final MediaServletItem empty = new MediaServletItem();
				assertID("MediaServletItem.1", empty);

				final MediaServletItem png = new MediaServletItem();
				assertID("MediaServletItem.2", png);
				png.setContent(thisClass.getResourceAsStream("osorno.png"), "image/png");

				final MediaServletItem jpeg = new MediaServletItem();
				assertID("MediaServletItem.3", jpeg);
				jpeg.setContent(thisClass.getResourceAsStream("tree.jpg"), "image/jpeg");

				final MediaServletItem unknown = new MediaServletItem();
				assertID("MediaServletItem.4", unknown);
				unknown.setContent(thisClass.getResourceAsStream("dummy.txt"), "unknownma/unknownmi");
				
				final MediaServletItem nameOk = new MediaServletItem("media item 1");
				assertID("MediaServletItem.5", nameOk);
				final MediaServletItem nameNull = new MediaServletItem(null);
				assertID("MediaServletItem.6", nameNull);
				final MediaServletItem nameError = new MediaServletItem("media item 3 error");
				assertID("MediaServletItem.7", nameError);
				
				final MediaServletItem gif = new MediaServletItem();
				assertID("MediaServletItem.8", gif);
				gif.setContent(thisClass.getResourceAsStream("gif.gif"), "image/gif");

				final MediaServletItem small = new MediaServletItem();
				assertID("MediaServletItem.9", small);
				small.setContent(thisClass.getResourceAsStream("small.jpg"), "image/jpeg");

				final MediaServletItem antialias = new MediaServletItem();
				assertID("MediaServletItem.10", antialias);
				antialias.setContent(thisClass.getResourceAsStream("antialias.png"), "image/png");

				final MediaServletItem antialiasJpeg = new MediaServletItem();
				assertID("MediaServletItem.11", antialiasJpeg);
				antialiasJpeg.setContent(thisClass.getResourceAsStream("antialias.jpg"), "image/jpeg");

				final MediaServletItem transparency = new MediaServletItem();
				assertID("MediaServletItem.12", transparency);
				transparency.setContent(thisClass.getResourceAsStream("transparency.png"), "image/png");
				
				model.commit();
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
		}
		catch(RuntimeException e)
		{
			// tomcat does not print stack trace or exception message, so we do
			System.err.println("RuntimeException in ConsoleServlet.init");
			e.printStackTrace();
			throw e;
		}
		catch(IOException e)
		{
			// tomcat does not print stack trace or exception message, so we do
			System.err.println("IOException in ConsoleServlet.init");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		catch(Error e)
		{
			// tomcat does not print stack trace or exception message, so we do
			System.err.println("Error in ConsoleServlet.init");
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void destroy()
	{
		connectToken.returnIt();
		connectToken = null;
		super.destroy();
	}
	
	private static final void assertID(final String id, final MediaServletItem item)
	{
		if(!id.equals(item.getCopeID()))
			throw new RuntimeException(item.getCopeID());
	}
	
	@Override
	protected final void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
	{
		response.setContentType("text/plain");
	}
}
