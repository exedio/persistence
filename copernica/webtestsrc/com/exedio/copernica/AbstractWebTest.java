package com.exedio.copernica;

import java.io.IOException;

import net.sourceforge.jwebunit.TestContext;
import net.sourceforge.jwebunit.WebTestCase;

import org.xml.sax.SAXException;

import com.meterware.httpunit.Button;
import com.meterware.httpunit.WebForm;

public class AbstractWebTest extends WebTestCase
{

	protected static final String SAVE_BUTTON = "Save";

	public AbstractWebTest(String name)
	{
		super(name);
	}

	public void setUp() throws Exception
	{
		super.setUp();
		final TestContext ctx = getTestContext();
		ctx.setBaseUrl("http://127.0.0.1:8080/copetest-hsqldb/");
		ctx.setAuthorization("admin", "nimda");
		beginAt("admin.jsp");
		submit("CREATE");
		beginAt("init.jsp");
		submit("INIT");
	}
	
	public void tearDown() throws Exception
	{
		beginAt("admin.jsp");
		submit("DROP");
		super.tearDown();
	}
	
	protected final void assertFormElementEqualsWithLabel(final String formElementLabel, final String expectedValue)
	{
		final String formElementName = getDialog().getFormElementNameForLabel(formElementLabel);
		assertNotNull("no form element with label "+formElementLabel, formElementName);
		assertFormElementEquals(formElementName, expectedValue);
	}
	
	protected final void submitWithValue(final String buttonValue) throws SAXException, IOException
	{
		final Button buttonWithValue = null;
		final WebForm[] forms = getDialog().getResponse().getForms();
		for(int i = 0; i<forms.length; i++)
		{
			final Button[] buttons = forms[i].getButtons();
			for(int j = 0; j<buttons.length; j++)
			{
				final Button button = buttons[j];
				if(button.getValue().equals(buttonValue))
				{
					if(buttonWithValue!=null)
						fail("there is more than one button with value "+buttonValue);
					else
					{
						submit(button.getName());
						return;
					}
				}
			}
		}
		fail("there is no button with value "+buttonValue);
	}
	
}
