package com.exedio.copernica;

/**
 * Tests especcially, that the forms still work
 * without multipart/form-data encoding.
 */
public class StringTest extends AbstractWebTest
{

	public StringTest(String name)
	{
		super(name);
	}

	String any;
	String min4;
	String max4;
	String min4Max8;

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
		assertFormElementEquals("any", any);
		assertFormElementEquals("min4", min4);
		assertFormElementEquals("max4", max4);
		assertFormElementEquals("min4Max8", min4Max8);
	}

	public void testItemForm()
	{
		beginAt("copernica.jsp");
		assertTitleEquals("Copernica");

		clickLinkWithText("String Item");
		assertTitleEquals("String Item");
		assertTextPresent("String Item");
		
		clickLinkWithText("StringItem.0");
		assertTitleEquals("StringItem.0");
		assertItemForm();

		setFormElement("any", "yeah"); any = "yeah";
		submit(ItemForm.SAVE_BUTTON);
		assertTitleEquals("StringItem.0");
		assertItemForm();

		// take back all changes
		setFormElement("any", "any1"); any = "any1";
		submit(ItemForm.SAVE_BUTTON);
		assertTitleEquals("StringItem.0");
		assertItemForm();
	}
}
