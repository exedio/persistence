
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
		assertText(";\n\n");
		assertFileDocComment("/**\n	Represents an attribute or association partner of a class.\n	Note: type==Model.AMIGOUS means, the attribute cannot be used in OCL due to attribute ambiguities.\n	See OCL spec 5.4.1. for details.\n*/");
		assertText("\n");
		assertClass("Example");
		assertText("public abstract class Example implements Runnable\n{\n  ");
		assertAttributeHeader("name");
		assertText("private String name");
		assertAttribute("name", null);
	}

}
