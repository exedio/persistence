
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
		assertText("/*\nSome initial test comment. \n*/\n\npackage// hallo\n  com.exedio.cope.instrument");
		assertPackage("com.exedio.cope.instrument");

		assertText(";\n\nimport java.util.*");
		assertImport("java.util.*");

		assertText(";\nimport java.text.Format");
		assertImport("java.text.Format");
		assertText(";\n\n");

		assertFileDocComment("/**\n	Represents an attribute or association partner of a class.\n	Note: type==Model.AMIGOUS means, the attribute cannot be used in OCL due to attribute ambiguities.\n	See OCL spec 5.4.1. for details.\n*/");
		assertText("\npublic abstract class Example");
		assertClass("Example");
		assertText(" implements Runnable\n{\n  ");

		assertAttributeHeader("name");
		assertText("private String name;");
		assertAttribute("name", null);
		assertText("\n  ");

		assertAttributeHeader("type");
		assertText("private Integer type=new Integer(5);");
		assertAttribute("type", null);
		assertText("\n  ");

		assertAttributeHeader("qualifiers");
		assertText("private volatile Integer[] qualifiers;");
		assertAttribute("qualifiers", null);
		assertText("\n  ");

		assertAttributeHeader("hallo");
		assertText("String hallo=\"hallo\";");
		assertAttribute("hallo", null);
		assertText("\n  \n  ");

		assertDocComment("/**TestCommentCommaSeparated123*/");
		assertText("\n  ");
		assertAttributeHeader("commaSeparated1");
		//assertText("int commaSeparated1,commaSeparated2=0,commaSeparated3;"); TODO: where is the text of these attributes?
		assertAttribute("commaSeparated1", "/**TestCommentCommaSeparated123*/");
		assertAttribute("commaSeparated2", "/**TestCommentCommaSeparated123*/");
		assertAttribute("commaSeparated3", "/**TestCommentCommaSeparated123*/");
		assertText(" \n  ");
	}

}
