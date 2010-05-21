/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.console.example;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.misc.ServletUtil;

public final class InitServlet extends HttpServlet
{
	private static final long serialVersionUID = 1l;
	
	private ConnectToken connectToken = null;
	
	@Override
	public void init() throws ServletException
	{
		super.init();
		
		final Class thisClass = InitServlet.class;
		connectToken = ServletUtil.connect(Main.model, getServletConfig(), thisClass.getName());
		Main.model.createSchema();
		try
		{
			Main.model.startTransaction(thisClass.getName());
			new AnItem("aField1");
			new AnItem("aField2");
			new ASubItem("aField1s", "aSubField1s");
			new ASubItem("aField2s", "aSubField2s");
			new ASubItem("aField3s", "aSubField3s");
			new AMediaItem();
			new AMediaItem().setContent(thisClass.getResourceAsStream("test.png"), "image/png");
			new AMediaItem().setContent(thisClass.getResourceAsStream("test.png"), "unknownma/unknownmi");
			new AMediaItem().setContent(thisClass.getResourceAsStream("test.png"), "image/jpeg"); // wrong content type by intention
			Main.model.commit();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			Main.model.rollbackIfNotCommitted();
		}
		Revisions.revisions(Main.model);
	}

	@Override
	public void destroy()
	{
		connectToken.returnIt();
		connectToken = null;
		super.destroy();
	}
	
	@Override
	protected final void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
	{
		response.setContentType("text/plain");
	}
}
