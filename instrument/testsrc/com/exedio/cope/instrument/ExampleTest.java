
package com.exedio.cope.instrument;

public class ExampleTest extends InjectorTest
{

	public ExampleTest(String name)
	{
		super(name, "Example.java");
	}

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	public void assertInjection()
	{
		assertText("/*\nSome initial test comment.\n*/\n\npackage// hallo\n  com.exedio.cope.instrument");
		assertPackage("com.exedio.cope.instrument");
		assertText(";\n\nimport java.util.*");
		assertImport("import java.util.*");
		assertText(";\nimport java.text.Format");
		assertImport("java.text.Format");
	}

}
