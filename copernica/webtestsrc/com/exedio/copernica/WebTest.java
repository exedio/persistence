package com.exedio.copernica;

import net.sourceforge.jwebunit.WebTestCase;

public class WebTest extends WebTestCase
{

	public WebTest(String name)
	{
		super(name);
	}

	String someString;
	String someNotNullString;
	String someInteger;
	String someNotNullInteger;
	String someLong;
	String someNotNullLong;
	String someDouble;
	String someNotNullDouble;
	String someDate;
	String someBoolean;
	boolean someNotNullBoolean;
	String someEnumeration;
	String someNotNullEnumeration;
	String someItem;
	String someNotNullItem;

	public void setUp() throws Exception
	{
		super.setUp();
		getTestContext().setBaseUrl("http://127.0.0.1:8080/copetest-oracle/");
		someString = "";
		someNotNullString = "running100";
		someInteger = "";
		someNotNullInteger = "107";
		someLong = "";
		someNotNullLong = "108";
		someDouble = "";
		someNotNullDouble = "102.4";
		someDate = "16.06.2004 08:43:58.214";
		someBoolean = "null";
		someNotNullBoolean = true;
		someEnumeration = "null";
		someNotNullEnumeration = "enumValue2";
		someItem = "";
		someNotNullItem = "EmptyItem.1";
	}
	
	private void assertItemForm()
	{
		assertFormElementEquals("someString", someString);
		assertFormElementEquals("someNotNullString", someNotNullString);
		assertFormElementEquals("someInteger", someInteger);
		assertFormElementEquals("someNotNullInteger", someNotNullInteger);
		assertFormElementEquals("someLong", someLong);
		assertFormElementEquals("someNotNullLong", someNotNullLong);
		assertFormElementEquals("someDouble", someDouble);
		assertFormElementEquals("someNotNullDouble", someNotNullDouble);
		assertFormElementEquals("someDate", someDate);
		assertFormElementEquals("someBoolean", someBoolean);
		if(someNotNullBoolean)
			assertCheckboxSelected("someNotNullBoolean");
		else
			assertCheckboxNotSelected("someNotNullBoolean");
		assertFormElementEquals("someEnumeration", someEnumeration);
		assertFormElementEquals("someNotNullEnumeration", someNotNullEnumeration);
		assertFormElementEquals("someItem", someItem);
		if(someItem.length()>0)
			assertLinkPresentWithText(someItem);
		assertFormElementEquals("someNotNullItem", someNotNullItem);
		assertLinkPresentWithText(someNotNullItem);
	}

	public void testItemForm()
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
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someNotNullString", "running100changed"); someNotNullString = "running100changed";
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someInteger", "99999"); someInteger = "99999";
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someNotNullInteger", "1077"); someNotNullInteger = "1077";
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someLong", "9999999999999"); someLong = "9999999999999";
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someNotNullLong", "-9999999999999"); someNotNullLong = "-9999999999999";
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someDouble", ""); someDouble = "";
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someNotNullDouble", "-75.9912"); someNotNullDouble = "-75.9912";
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someDate", "17.07.2005 09:44:59.215"); someDate = "17.07.2005 09:44:59.215";
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someBoolean", "on"); someBoolean = "on";
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someBoolean", "off"); someBoolean = "off";
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		uncheckCheckbox("someNotNullBoolean"); someNotNullBoolean = false;
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someEnumeration", "enumValue2"); someEnumeration = "enumValue2";
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someNotNullEnumeration", "enumValue1"); someNotNullEnumeration = "enumValue1";
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someItem", "EmptyItem.1"); someItem = "EmptyItem.1";
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someNotNullItem", "EmptyItem.2"); someNotNullItem = "EmptyItem.2";
		submit("SAVE");
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
		assertItemForm();

		setFormElement("someString", ""); someString = "";
		setFormElement("someNotNullString", "running100"); someNotNullString = "running100";
		setFormElement("someInteger", ""); someInteger = "";
		setFormElement("someNotNullInteger", "107"); someNotNullInteger = "107";
		setFormElement("someLong", ""); someLong = "";
		setFormElement("someNotNullLong", "108"); someNotNullLong = "108";
		setFormElement("someDouble", ""); someDouble = "";
		setFormElement("someNotNullDouble", "102.4"); someNotNullDouble = "102.4";
		setFormElement("someDate", "16.06.2004 08:43:58.214"); someDate = "16.06.2004 08:43:58.214";
		setFormElement("someBoolean", "null"); someBoolean = "null";
		setFormElement("someNotNullBoolean", "on"); someNotNullBoolean = true;
		setFormElement("someEnumeration", "null"); someEnumeration = "null";
		setFormElement("someNotNullEnumeration", "enumValue2"); someNotNullEnumeration = "enumValue2";
		setFormElement("someItem", ""); someItem = "";
		setFormElement("someNotNullItem", "EmptyItem.1"); someNotNullItem = "EmptyItem.1";
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();
	}
}
