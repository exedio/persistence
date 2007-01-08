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

package com.exedio.copernica.test;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.CollisionItem1;
import com.exedio.cope.testmodel.CollisionItem2;
import com.exedio.cope.testmodel.EmptyItem;
import com.exedio.cope.testmodel.EmptyItem2;
import com.exedio.cope.testmodel.FinalItem;
import com.exedio.cope.testmodel.ItemWithDoubleUnique;
import com.exedio.cope.testmodel.ItemWithSingleUnique;
import com.exedio.cope.testmodel.ItemWithSingleUniqueNotNull;
import com.exedio.cope.testmodel.UniqueFinal;
import com.exedio.cope.testmodel.Main;
import com.exedio.cope.testmodel.MediaServletItem;
import com.exedio.cope.testmodel.PointerItem;
import com.exedio.cope.testmodel.PointerTargetItem;
import com.exedio.cope.testmodel.QualifiedIntegerEnumQualifier;
import com.exedio.cope.testmodel.QualifiedItem;
import com.exedio.cope.testmodel.StringItem;
import com.exedio.cope.testmodel.PlusItem;
import com.exedio.copernica.CopernicaProvider;
import com.exedio.cops.CopsServlet;


public class InitServlet extends CopsServlet
{
	private static final long serialVersionUID = 328658236589653l;
	
	final static String ENCODING = "utf-8";

	@Override
	protected void doRequest(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		request.setCharacterEncoding(ENCODING);
		response.setContentType("text/html; charset="+ENCODING);

		final boolean initialize = (request.getParameter("INIT")!=null) && "POST".equals(request.getMethod());
		if(initialize)
		{
			try
			{
				Main.model.startTransaction("initializeExampleSystem");
				initializeExampleSystem();
				Main.model.commit();
			}
			finally
			{
				Main.model.rollbackIfNotCommitted();
			}
		}

		final PrintStream out = new PrintStream(response.getOutputStream(), false, ENCODING);
		Init_Jspm.write(out, initialize);
		out.close();
	}

	private static final void initializeExampleSystem()
	{
		try
		{
			final Class thisClass = CopernicaProvider.class;
			{
				final ItemWithSingleUnique item1 = new ItemWithSingleUnique();
				item1.setUniqueString("item1");
				final ItemWithSingleUnique item2 = new ItemWithSingleUnique();
				item2.setUniqueString("item2");
			}
			
			new UniqueFinal("item1");
			new UniqueFinal("item2");
			
			new ItemWithSingleUniqueNotNull("item1");
			new ItemWithSingleUniqueNotNull("item2");
			
			new ItemWithDoubleUnique("string1", 1);
			new ItemWithDoubleUnique("string1", 2);
			new ItemWithDoubleUnique("string2", 1);
			new ItemWithDoubleUnique("string2", 2);
			
			final EmptyItem emptyItem1 = new EmptyItem();
			final EmptyItem emptyItem2 = new EmptyItem();
			new EmptyItem();
			new EmptyItem2();
			
			final AttributeItem attributeItem1 = new AttributeItem("someString1", 5, 6l, 2.2, true, emptyItem1, AttributeItem.SomeEnum.enumValue1);
			final AttributeItem attributeItem2 = new AttributeItem("someString2", 6, 7l, 2.3, true, emptyItem2, AttributeItem.SomeEnum.enumValue2);
			final AttributeItem attributeItem3 = new AttributeItem("someString3", 7, 8l, 2.4, false, emptyItem2, AttributeItem.SomeEnum.enumValue2);
			attributeItem1.setSomeData(thisClass.getResourceAsStream("dummy.txt"), "text/plain");
			attributeItem2.setSomeData(thisClass.getResourceAsStream("osorno.png"), "image/png");
			attributeItem3.setSomeData(thisClass.getResourceAsStream("tree.jpg"), "image/jpeg");
			
			final Date date = new Date(1087368238214l);
			for(int i = 0; i<102; i++)
			{
				final AttributeItem attributeItem = new AttributeItem("running"+i, 7+i, 8l+i, 2.4+i, (i%2)==0, emptyItem2, AttributeItem.SomeEnum.enumValue2);
				attributeItem.setSomeDate(date);
			}
			{
				final StringItem item1 = new StringItem("Test Provider 1");
				final StringItem item2 = new StringItem("Test Provider 2");
				final StringItem item3 = new StringItem("Test Provider 3");
				
				item1.setAny("any1");
				item1.setMin4("min4");
				item1.setMax4("max4");
				item1.setMin4Max8("min4max8");
				
				item2.setAny("any1");
				item2.setMin4("min4");
				item2.setMax4("max4");
				item2.setMin4Max8("m4x8");
				
				item3.setAny("Herzliche Gr\u00fc\u00dfe!");
			}
			
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

			new PlusItem(1, 2, 3);
			new PlusItem(4, 5, 4);
			new PlusItem(9, 2, 6);
			new PlusItem(2, 8, 1);
			new PlusItem(5, 6, 7);
			new PlusItem(3, 5, 9);
			new PlusItem(6, 4, 0);
			new PlusItem(8, 1, 2);
			new PlusItem(2, 9, 7);
			new PlusItem(5, 2, 0);
			new PlusItem(6, 7, 6);

			{
				final QualifiedItem qualifiedItem1 = new QualifiedItem();
				final QualifiedItem qualifiedItem2 = new QualifiedItem();
				qualifiedItem1.setNumber(Integer.valueOf(1));
				qualifiedItem2.setNumber(Integer.valueOf(2));
				qualifiedItem1.setQualifiedA(emptyItem1, "1A1");
				qualifiedItem1.setQualifiedA(emptyItem2, "1A2");
				qualifiedItem1.setQualifiedB(emptyItem1, "1B1");
				qualifiedItem1.setQualifiedA("key1", Integer.valueOf(1));
				qualifiedItem1.setQualifiedA("key2", Integer.valueOf(2));
				qualifiedItem1.setQualifiedA("key3", Integer.valueOf(3));
				qualifiedItem1.setQualifiedA("key4", Integer.valueOf(4));
				qualifiedItem1.setQualifiedB("key4", Integer.valueOf(14));
				qualifiedItem1.setQualifiedA(Integer.valueOf(8), QualifiedIntegerEnumQualifier.KeyEnum.key1, "1-A-8-key1");
				qualifiedItem1.setQualifiedA(Integer.valueOf(6), QualifiedIntegerEnumQualifier.KeyEnum.key1, "1-A-6-key1");
				qualifiedItem1.setQualifiedA(Integer.valueOf(8), QualifiedIntegerEnumQualifier.KeyEnum.key2, "1-A-8-key2");
				qualifiedItem1.setQualifiedB(Integer.valueOf(8), QualifiedIntegerEnumQualifier.KeyEnum.key1, "1-B-8-key1");
			}

			{
				final PointerTargetItem item2a = new PointerTargetItem("hallo");
				final PointerTargetItem item2b = new PointerTargetItem("bello");
				final PointerItem item1a = new PointerItem("bello", item2a);
				final PointerItem item1b = new PointerItem("collo", item2b);
				item1a.setSelf(item1a);
				item1b.setSelf(item1a);
			}
			
			new FinalItem("finalString1", 1);
			new FinalItem("finalString2", 2);
			
			new CollisionItem1(emptyItem1);
			new CollisionItem1(emptyItem2);
			new CollisionItem2(emptyItem1);
			new CollisionItem2(emptyItem2);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private static final void assertID(final String id, final MediaServletItem item)
	{
		if(!id.equals(item.getCopeID()))
			throw new RuntimeException(item.getCopeID());
	}
}
