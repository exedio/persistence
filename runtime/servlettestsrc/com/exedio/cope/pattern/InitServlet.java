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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.exedio.cope.Model;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.misc.ConnectToken;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.text.ParseException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION") // OK: closed in destroy
public class InitServlet extends HttpServlet
{
	private static final long serialVersionUID = 1l;

	public static final Model model = new Model(MediaServletItem.TYPE, MediaPatternItem.TYPE);

	@SuppressFBWarnings({"SE_BAD_FIELD", "MSF_MUTABLE_SERVLET_FIELD", "MTIA_SUSPECT_SERVLET_INSTANCE_FIELD"})
	private ConnectToken connectToken = null;

	@Override
	public void init() throws ServletException
	{
		super.init();

		@SuppressWarnings("HardcodedLineSeparator")
		final byte[] textValue = "This is an example file\nfor testing media data.\n".getBytes(UTF_8);
		final Class<?> thisClass = InitServlet.class;
		//noinspection resource OK: closed in destroy
		connectToken = ConnectToken.issue(model, thisClass.getName()).returnOnFailureOf(t ->
		{
			model.createSchema();
			try(TransactionTry tx = model.startTransactionTry(thisClass.getName()))
			{
				final MediaServletItem text = new MediaServletItem();
				assertID("MediaServletItem-0", text);
				text.setContent(textValue, "text/plain", 0);
				text.setContentLarge(textValue, "text/plain", 0);

				final MediaServletItem empty = new MediaServletItem();
				assertID("MediaServletItem-1", empty);

				final MediaServletItem png = new MediaServletItem();
				assertID("MediaServletItem-2", png);
				png.setContent(thisClass.getResourceAsStream("osorno.png"), "image/png", 2);
				assertPath("MediaServletItem/finger/.fjeCiepS/MediaServletItem-2.jpg", png.getFingerLocator());

				final MediaServletItem jpeg = new MediaServletItem();
				assertID("MediaServletItem-3", jpeg);
				jpeg.setContent(thisClass.getResourceAsStream("tree.jpg"), "image/jpeg", 3);
				assertPath("MediaServletItem/finger/.fjYxvepS/MediaServletItem-3.jpg", jpeg.getFingerLocator());

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
				pattern.addSourceItem(textValue, "text/plain", 20);
				pattern.addSourceItem(textValue, "text/plain", 21);

				final MediaServletItem catchPhrase = new MediaServletItem();
				assertID("MediaServletItem-14", catchPhrase);
				catchPhrase.setCatchphrase("zick");
				catchPhrase.setContent(textValue, "text/plain", 14);

				final MediaServletItem nameErrorLastModified = new MediaServletItem("media item 4 error");
				assertID("MediaServletItem-15", nameErrorLastModified);
				nameErrorLastModified.setNameServerLastModified(15);

				tx.commit();
			}
			catch(final IOException | ParseException e)
			{
				throw new RuntimeException(e);
			}
		});
		// DO NOT WRITE ANYTHING HERE,
		// OTHERWISE ConnectTokens MAY BE LOST
	}

	@Override
	public void destroy()
	{
		connectToken.returnStrictly();
		connectToken = null;
		super.destroy();
	}

	private static void assertID(final String id, final MediaServletItem item)
	{
		if(!id.equals(item.getCopeID()))
			throw new RuntimeException(item.getCopeID());
	}

	private static void assertPath(final String expected, final MediaPath.Locator locator)
	{
		final String path = locator.getPath();
		if(!expected.equals(path))
			throw new RuntimeException("expected '" + expected + "', but was '" + path + '\'');
	}

	@Override
	protected final void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
	{
		response.setContentType("text/plain");
	}
}
