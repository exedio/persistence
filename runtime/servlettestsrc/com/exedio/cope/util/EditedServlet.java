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

package com.exedio.cope.util;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Migration;
import com.exedio.cope.Model;
import com.exedio.cope.pattern.InitServlet;
import com.exedio.cope.pattern.Media;

public final class EditedServlet extends HttpServlet
{
	static final Migration[] migrations(final int length)
	{
		final Migration[] result = new Migration[length];
		for(int i = 0; i<length; i++)
		{
			final int revision = length - i;
			result[i] = new Migration(revision, "comment " + revision, "sql " + revision + "/1");
		}
		return result;
	}
	
	private static final long serialVersionUID = 1l;
	
	static final String ENCODING = "utf-8";
	
	public static final Model model = new Model(migrations(64), EditedItem.TYPE);
	
	private ConnectToken connectToken = null;
	
	@Override
	public void init() throws ServletException
	{
		super.init();
		
		connectToken = ServletUtil.connect(model, getServletConfig(), "EditedServlet#init");
		model.createDatabase();
		try
		{
			model.startTransaction("create sample data");
			createItem(0, "osorno.png", "image/png");
			createItem(1, "tree.jpg", "image/jpeg");
			createItem(2, "tree.jpg", "image/jpeg");
			model.commit();
		}
		finally
		{
			model.rollbackIfNotCommitted();
		}
	}
	
	private static final EditedItem createItem(
			final int number,
			final String image, final String imageContentType)
	{
		final EditedItem item1 = new EditedItem(
				number,
				"item" + number,
				Media.toValue(InitServlet.class.getResourceAsStream(image), imageContentType));
		item1.setMap(1, "item" + number + "map1");
		item1.setMap(2, "item" + number + "map2");
		item1.setMap(3, "item" + number + "map3Block\nsecond line\r\nthird line");
		return item1;
	}

	@Override
	public void destroy()
	{
		model.dropDatabase();
		connectToken.returnIt();
		connectToken = null;
		super.destroy();
	}
	
	@Override
	protected void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
	throws IOException
	{
		request.setCharacterEncoding(ENCODING);
		response.setContentType("text/html; charset="+ENCODING);

		response.addHeader("Cache-Control", "no-cache");
		response.addHeader("Cache-Control", "no-store");
		response.addHeader("Cache-Control", "max-age=0");
		response.addHeader("Cache-Control", "must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", System.currentTimeMillis());
		
		final PrintStream out = new PrintStream(response.getOutputStream(), false, ENCODING);
		try
		{
			model.startTransaction("EditedServlet");
			EditedServlet_Jspm.write(out,
					response,
					response.encodeURL("contentEditorLogin.html"),
					EditedItem.TYPE.search(null, EditedItem.position, true));
			model.commit();
			response.setStatus(HttpServletResponse.SC_OK);
		}
		finally
		{
			model.rollbackIfNotCommitted();
			out.close();
		}
	}
	
	@Override
	protected void doPost(
			final HttpServletRequest request,
			final HttpServletResponse response) throws IOException
	{
		doGet(request, response);
	}
}
