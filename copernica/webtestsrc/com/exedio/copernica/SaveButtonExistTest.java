package com.exedio.copernica;

import net.sourceforge.jwebunit.WebTestCase;

public class SaveButtonExistTest extends WebTestCase
{

	public SaveButtonExistTest(String name)
	{
		super(name);
	}

	public void setUp() throws Exception
	{
		super.setUp();
		getTestContext().setBaseUrl("http://localhost:8080/copernicatest/");
	}
	
	public void testSaveButtonExists()
	{
		beginAt("copernica.jsp");
		assertTitleEquals("Copernica");

		clickLinkWithText("String Item");
		assertTitleEquals("String Item");
		
		final String SAVE = "SAVE";
		clickLinkWithText("StringItem.1");
		assertTitleEquals("StringItem.1");
		assertSubmitButtonPresent(SAVE);

		clickLinkWithText("Collision Item1");
		assertTitleEquals("Collision Item1");
		
		clickLinkWithText("EmptyItem.1");
		assertTitleEquals("EmptyItem.1");
		assertSubmitButtonNotPresent(SAVE);
	}
}
