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

	public void setUp() throws Exception
	{
		super.setUp();
		getTestContext().setBaseUrl("http://localhost:8080/copernicatest/");
		someNotNullString = "running100";
		someNotNullInteger = "107";
		someBoolean = "NULL";
	}
	
	private void assertItemForm()
	{
		assertFormElementEquals("someNotNullString", someNotNullString);
		assertFormElementEquals("someNotNullInteger", someNotNullInteger);
		assertFormElementEquals("someBoolean", someBoolean);
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

		setFormElement("someBoolean", "TRUE"); someBoolean = "TRUE";
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();

		setFormElement("someNotNullString", "running100"); someNotNullString = "running100";
		setFormElement("someNotNullInteger", "107"); someNotNullInteger = "107";
		setFormElement("someBoolean", "NULL"); someBoolean = "NULL";
		submit("SAVE");
		assertTitleEquals("AttributeItem.103");
		assertItemForm();
	}
}
