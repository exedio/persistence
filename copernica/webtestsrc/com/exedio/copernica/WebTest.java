package com.exedio.copernica;

import net.sourceforge.jwebunit.WebTestCase;

public class WebTest extends WebTestCase
{

	public WebTest(String name)
	{
		super(name);
	}

	String someNotNullString;
	String someNotNullInteger;
	String someBoolean;
	boolean someNotNullBoolean;
	String someEnumeration;
	String someNotNullEnumeration;
	String someItem;
	String someNotNullItem;

	public void setUp() throws Exception
	{
		super.setUp();
		getTestContext().setBaseUrl("http://localhost:8080/copernicatest/");
		someNotNullString = "running100";
		someNotNullInteger = "107";
		someBoolean = "null";
		someNotNullBoolean = true;
		someEnumeration = "null";
		someNotNullEnumeration = "enumValue2";
		someItem = "";
		someNotNullItem = "EmptyItem.1";
	}
	
	private void assertItemForm()
	{
		assertFormElementEquals("someNotNullString", someNotNullString);
		assertFormElementEquals("someNotNullInteger", someNotNullInteger);
		assertFormElementEquals("someBoolean", someBoolean);
		if(someNotNullBoolean)
			assertCheckboxSelected("someNotNullBoolean");
		else
			assertCheckboxNotSelected("someNotNullBoolean");
		assertFormElementEquals("someEnumeration", someEnumeration);
		assertFormElementEquals("someNotNullEnumeration", someNotNullEnumeration);
		assertFormElementEquals("someItem", someItem);
		assertFormElementEquals("someNotNullItem", someNotNullItem);
	}

	public void testSearch()
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
		assertLinkNotPresentWithText("50");

		clickLinkWithText("[X]");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someNotNullString", "running100changed"); someNotNullString = "running100changed";
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someNotNullInteger", "1077"); someNotNullInteger = "1077";
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

		setFormElement("someNotNullString", "running100"); someNotNullString = "running100";
		setFormElement("someNotNullInteger", "107"); someNotNullInteger = "107";
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
