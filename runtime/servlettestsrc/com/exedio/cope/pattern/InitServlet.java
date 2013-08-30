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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Model;
import com.exedio.cope.misc.ConnectToken;

public class InitServlet extends HttpServlet
{
	private static final long serialVersionUID = 1l;

	public static final Model model = new Model(MediaServletItem.TYPE, MediaPatternItem.TYPE);

	private ConnectToken connectToken = null;

	@Override
	public void init() throws ServletException
	{
		super.init();

		final byte[] textValue;
		try
		{
			textValue = "This is an example file\nfor testing media data.\n".getBytes(UTF8);
		}
		catch(final UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}

		final Class<?> thisClass = InitServlet.class;
		connectToken = ConnectToken.issue(model, thisClass.getName());
		model.createSchema();
		try
		{
			model.startTransaction(thisClass.getName());

			final MediaServletItem text = new MediaServletItem();
			assertID("MediaServletItem-0", text);
			text.setContent(textValue, "text/plain", 0);

			final MediaServletItem empty = new MediaServletItem();
			assertID("MediaServletItem-1", empty);

			final MediaServletItem png = new MediaServletItem();
			assertID("MediaServletItem-2", png);
			png.setContent(thisClass.getResourceAsStream("osorno.png"), "image/png", 2);

			final MediaServletItem jpeg = new MediaServletItem();
			assertID("MediaServletItem-3", jpeg);
			jpeg.setContent(thisClass.getResourceAsStream("tree.jpg"), "image/jpeg", 3);

			final MediaServletItem unknown = new MediaServletItem();
			assertID("MediaServletItem-4", unknown);
			unknown.setContent(textValue, "unknownma/unknownmi", 4);

			final MediaServletItem nameOk = new MediaServletItem("media item 1");
			assertID("MediaServletItem-5", nameOk);
			final MediaServletItem nameNull = new MediaServletItem(null);
			assertID("MediaServletItem-6", nameNull);
			final MediaServletItem nameError = new MediaServletItem("media item 3 error");
			assertID("MediaServletItem-7", nameError);

			final MediaServletItem gif = new MediaServletItem();
			assertID("MediaServletItem-8", gif);
			gif.setContent(thisClass.getResourceAsStream("gif.gif"), "image/gif", 8);

			final MediaServletItem small = new MediaServletItem();
			assertID("MediaServletItem-9", small);
			small.setContent(thisClass.getResourceAsStream("small.jpg"), "image/jpeg", 9);

			final MediaServletItem antialias = new MediaServletItem();
			assertID("MediaServletItem-10", antialias);
			antialias.setContent(thisClass.getResourceAsStream("antialias.png"), "image/png", 10);

			final MediaServletItem antialiasJpeg = new MediaServletItem();
			assertID("MediaServletItem-11", antialiasJpeg);
			antialiasJpeg.setContent(thisClass.getResourceAsStream("antialias.jpg"), "image/jpeg", 11);

			final MediaServletItem transparency = new MediaServletItem();
			assertID("MediaServletItem-12", transparency);
			transparency.setContent(thisClass.getResourceAsStream("transparency.png"), "image/png", 12);

			final MediaServletItem html = new MediaServletItem();
			assertID("MediaServletItem-13", html);
			html.setContent(thisClass.getResourceAsStream("filter.html"), "text/html", 13);
			html.addHtmlPaste("small", Media.toValue(thisClass.getResourceAsStream("small.jpg"), "image/jpeg"));
			html.addHtmlPaste("tree",  Media.toValue(thisClass.getResourceAsStream("tree.jpg"),  "image/jpeg"));
			html.modifyHtmlPaste( "tree", Media.toValue( thisClass.getResourceAsStream( "small.jpg" ), "image/jpeg" ) );
			html.modifyHtmlPaste( "tree", Media.toValue( thisClass.getResourceAsStream( "tree.jpg" ), "image/jpeg" ) );

			final MediaPatternItem pattern = new MediaPatternItem();
			pattern.setSourceFeature(textValue, "text/plain", 10);
			pattern.addSourceItem(textValue, "text/plain");
			pattern.addSourceItem(textValue, "text/plain");

			final MediaServletItem catchPhrase = new MediaServletItem();
			assertID("MediaServletItem-14", catchPhrase);
			catchPhrase.setCatchPhrase("zick");
			catchPhrase.setContent(textValue, "text/plain", 14);

			model.commit();
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
		catch(final ParseException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			model.rollbackIfNotCommitted();
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
