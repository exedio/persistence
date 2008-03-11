/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

package com.exedio.copernica;

/**
 * Tests especcially, that the forms still work
 * without multipart/form-data encoding.
 *
 * @author Ralf Wiebicke
 */
public class StringTest extends AbstractWebTest
{

	String any;
	String min4;
	String max4;
	String min4Max8;
	
	private static final String ANY = "Any";
	private static final String MIN4 = "Min4";
	private static final String MAX4 = "Max4";
	private static final String MIN4MAX8 = "Min4 Max8";

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		any="any1";
		min4="min4";
		max4="max4";
		min4Max8="min4max8";
	}
	
	private void assertItemForm()
	{
		assertFormElementEqualsWithLabel(ANY, any);
		assertFormElementEqualsWithLabel(MIN4, min4);
		assertFormElementEqualsWithLabel(MAX4, max4);
		assertFormElementEqualsWithLabel(MIN4MAX8, min4Max8);
	}

	public void testItemForm() throws Exception
	{
		beginAt("copernica.jsp");
		assertTitleEquals("Copernica");

		clickLinkWithText("String Item");
		assertTitleEquals("String Item");
		assertTextPresent("String Item");
		
		clickLinkWithText("StringItem.0");
		assertTitleEquals("StringItem.0");
		assertItemForm();

		setFormElementWithLabel(ANY, "yeah"); any = "yeah";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("StringItem.0");
		assertItemForm();

		// take back all changes
		setFormElementWithLabel(ANY, "any1"); any = "any1";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("StringItem.0");
		assertItemForm();
	}
}
