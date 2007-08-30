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

package com.exedio.copernica;

import java.util.Arrays;

public class WebTest extends AbstractWebTest
{
	String someString;
	String someNotNullString;
	String section;
	String someInteger;
	String someNotNullInteger;
	String someLong;
	String someNotNullLong;
	String someDouble;
	String someNotNullDouble;
	String someDate;
	String someBoolean;
	boolean someNotNullBoolean;
	String someEnum;
	String someNotNullEnum;
	String someItem;
	String someNotNullItem;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		someString = "";
		someNotNullString = "running100";
		section = SECTION_NUMBERS;
		someInteger = "";
		someNotNullInteger = "107";
		someLong = "";
		someNotNullLong = "108";
		someDouble = "";
		someNotNullDouble = "102.4";
		someDate = "16.06.2004 08:43:58.214";
		someBoolean = "null";
		someNotNullBoolean = true;
		someEnum = "null";
		someNotNullEnum = "enumValue2";
		someItem = "";
		someNotNullItem = "EmptyItem.1";
	}
	
	private static final String SECTION_NUMBERS = "numbers";
	private static final String SECTION_DATA = "data";
	private static final String SECTION_OTHER = "other";
	
	private void assertItemForm()
	{
		assertTrue(section, Arrays.asList(new String[]{null, SECTION_NUMBERS, SECTION_DATA, SECTION_OTHER}).contains(section));

		assertFormElementEquals("someString", someString);
		assertFormElementEquals("someNotNullString", someNotNullString);

		assertFormElementEquals("someInteger", someInteger);
		assertFormElementEquals("someNotNullInteger", someNotNullInteger);
		assertFormElementEquals("someLong", someLong);
		assertFormElementEquals("someNotNullLong", someNotNullLong);
		assertFormElementEquals("someDouble", someDouble);
		assertFormElementEquals("someNotNullDouble", someNotNullDouble);

		assertFormElementEquals("someEnum", someEnum);
		assertFormElementEquals("someNotNullEnum", someNotNullEnum);
	
		assertFormElementEquals("someDate", someDate); // TODO fails with wrong time zone
		assertFormElementEquals("someBoolean", someBoolean);
		if(section.equals(SECTION_OTHER))
		{
			if(someNotNullBoolean)
				assertCheckboxSelected("someNotNullBoolean");
			else
				assertCheckboxNotSelected("someNotNullBoolean");
		}
		else
		{
			if(someNotNullBoolean)
				assertFormElementEquals("someNotNullBoolean", "on");
			else
				assertFormElementNotPresent("someNotNullBoolean");
		}
		assertFormElementEquals("someItem", someItem);
		if(someItem.length()>0)
		{
			if(section.equals(SECTION_OTHER))
				assertLinkPresentWithText(someItem);
			else
				assertLinkNotPresentWithText(someItem);
		}
		assertFormElementEquals("someNotNullItem", someNotNullItem);
		if(section.equals(SECTION_OTHER))
			assertLinkPresentWithText(someNotNullItem);
		else
			assertLinkNotPresentWithText(someNotNullItem);
	}
	
	private void section(final String section)
	{
		this.section = section;
		submit(section);
	}

	public void testItemForm() throws Exception
	{
		beginAt("copernica.jsp");
		assertTitleEquals("Copernica");
		assertLinkPresentWithText("de");

		clickLinkWithText("Attribute Item");
		assertTitleEquals("Attribute Item");
		assertTextPresent("Attribute Item");
		
		assertLinkPresentWithText("50");
		clickLinkWithText("50");
		assertTitleEquals("Attribute Item");
		assertLinkPresentWithText("50"); // TODO check if its inactive

		assertLinkPresentWithText(">");
		clickLinkWithText(">");
		assertTitleEquals("Attribute Item");
		assertLinkPresentWithText(">");
		clickLinkWithText(">");
		assertTitleEquals("Attribute Item");
		assertLinkPresentWithText(">"); // TODO check if its inactive

		clickLinkWithText("AttributeItem.103");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someString", "yeah"); someString = "yeah";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someNotNullString", "running100changed"); someNotNullString = "running100changed";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		section(SECTION_NUMBERS);
		assertItemForm();
		setFormElement("someInteger", "99999"); someInteger = "99999";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		section(SECTION_NUMBERS);
		assertItemForm();
		setFormElement("someNotNullInteger", "1077"); someNotNullInteger = "1077";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		section(SECTION_NUMBERS);
		assertItemForm();
		setFormElement("someLong", "9999999999999"); someLong = "9999999999999";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		section(SECTION_NUMBERS);
		assertItemForm();
		setFormElement("someNotNullLong", "-9999999999999"); someNotNullLong = "-9999999999999";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		section(SECTION_NUMBERS);
		assertItemForm();
		setFormElement("someDouble", ""); someDouble = "";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		section(SECTION_NUMBERS);
		assertItemForm();
		setFormElement("someNotNullDouble", "-75.9912"); someNotNullDouble = "-75.9912";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		section(SECTION_OTHER);
		assertItemForm();
		setFormElement("someDate", "17.07.2005 09:44:59.215"); someDate = "17.07.2005 09:44:59.215";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		section(SECTION_OTHER);
		assertItemForm();
		setFormElement("someBoolean", "on"); someBoolean = "on";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		section(SECTION_OTHER);
		assertItemForm();
		setFormElement("someBoolean", "off"); someBoolean = "off";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		section(SECTION_OTHER);
		assertItemForm();
		uncheckCheckbox("someNotNullBoolean"); someNotNullBoolean = false;
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		section(SECTION_DATA);
		assertItemForm();
		setFormElement("someEnum", "enumValue2"); someEnum = "enumValue2";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		section(SECTION_DATA);
		assertItemForm();
		setFormElement("someNotNullEnum", "enumValue1"); someNotNullEnum = "enumValue1";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		section(SECTION_OTHER);
		assertItemForm();
		setFormElement("someItem", "EmptyItem.1"); someItem = "EmptyItem.1";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		section(SECTION_OTHER);
		assertItemForm();
		setFormElement("someNotNullItem", "EmptyItem.2"); someNotNullItem = "EmptyItem.2";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		// refetch item page, thus check,
		// whether all values hav been saved properly
		clickLinkWithText("Attribute Item");
		assertTitleEquals("Attribute Item");
		assertTextPresent("Attribute Item");

		assertLinkPresentWithText("50");
		clickLinkWithText("50");
		assertTitleEquals("Attribute Item");
		assertLinkPresentWithText("50"); // TODO check if its inactive

		assertLinkPresentWithText(">");
		clickLinkWithText(">");
		assertTitleEquals("Attribute Item");
		assertLinkPresentWithText(">");
		clickLinkWithText(">");
		assertTitleEquals("Attribute Item");
		assertLinkPresentWithText(">"); // TODO check if its inactive

		clickLinkWithText("AttributeItem.103");
		assertTitleEquals("AttributeItem.103");

		section=SECTION_NUMBERS;
		assertItemForm();
		section(SECTION_DATA);
		assertItemForm();
		section(SECTION_OTHER);
		assertItemForm();

		section(SECTION_NUMBERS);
		setFormElement("someString", ""); someString = "";
		setFormElement("someNotNullString", "running100"); someNotNullString = "running100";
		setFormElement("someInteger", ""); someInteger = "";
		setFormElement("someNotNullInteger", "107"); someNotNullInteger = "107";
		setFormElement("someLong", ""); someLong = "";
		setFormElement("someNotNullLong", "108"); someNotNullLong = "108";
		setFormElement("someDouble", ""); someDouble = "";
		setFormElement("someNotNullDouble", "102.4"); someNotNullDouble = "102.4";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		section(SECTION_DATA);
		setFormElement("someEnum", "null"); someEnum = "null";
		setFormElement("someNotNullEnum", "enumValue2"); someNotNullEnum = "enumValue2";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		section(SECTION_OTHER);
		setFormElement("someDate", "16.06.2004 08:43:58.214"); someDate = "16.06.2004 08:43:58.214";
		setFormElement("someBoolean", "null"); someBoolean = "null";
		checkCheckbox("someNotNullBoolean"); someNotNullBoolean = true;
		setFormElement("someItem", ""); someItem = "";
		setFormElement("someNotNullItem", "EmptyItem.1"); someNotNullItem = "EmptyItem.1";
		submitWithValue(SAVE_BUTTON);
		assertTitleEquals("AttributeItem.103");
		assertItemForm();
	}
}
