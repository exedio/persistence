
package com.exedio.cope.instrument;

import java.lang.reflect.Modifier;

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

		assertAttributeHeader("name", "String", Modifier.PRIVATE);
		assertText("private String name;");
		assertAttribute("name", null);
		assertText("\n  ");

		assertAttributeHeader("type", "Integer", Modifier.PRIVATE);
		assertText("private Integer type=new Integer(5);");
		assertAttribute("type", null);
		assertText("\n  ");

		assertAttributeHeader("qualifiers", "Integer[]", Modifier.PRIVATE|Modifier.VOLATILE);
		assertText("private volatile Integer[] qualifiers;");
		assertAttribute("qualifiers", null);
		assertText("\n  ");

		assertAttributeHeader("hallo", "String", 0);
		assertText("String hallo=\"hallo\";");
		assertAttribute("hallo", null);
		assertText("\n  \n  ");

		assertDocComment("/**TestCommentCommaSeparated123*/");
		assertText("\n  ");
		assertAttributeHeader("commaSeparated1", "int", 0);
		//assertText("int commaSeparated1,commaSeparated2=0,commaSeparated3;"); TODO: where is the text of these attributes?
		assertAttribute("commaSeparated1", "/**TestCommentCommaSeparated123*/");
		assertAttribute("commaSeparated2", "/**TestCommentCommaSeparated123*/");
		assertAttribute("commaSeparated3", "/**TestCommentCommaSeparated123*/");
		assertText(" \n  ");

		assertDocComment("/**TestCommentCommaSeparated456*/");
		assertText("\n  ");
		assertAttributeHeader("commaSeparated4", "int", 0);
		assertAttribute("commaSeparated4", "/**TestCommentCommaSeparated456*/");
		assertAttribute("commaSeparated5", "/**TestCommentCommaSeparated456*/");
		assertAttribute("commaSeparated6", "/**TestCommentCommaSeparated456*/");
		assertText(" \n\n  // these attributes test the ability of the parser\n  // to skip more complex (ugly) attribute initializers\n  ");

		assertAttributeHeader("uglyAttribute1", "String", 0);
		assertText("String   uglyAttribute1=\"some'Thing{some\\\"Thing;Else\";");
		assertAttribute("uglyAttribute1", null);
		assertText("\n  ");

		assertAttributeHeader("uglyAttribute2", "char", 0);
		assertText("char     uglyAttribute2=';';");
		assertAttribute("uglyAttribute2", null);
		assertText("\n  ");

		assertAttributeHeader("uglyAttribute3", "char", 0);
		assertText("char     uglyAttribute3='{';");
		assertAttribute("uglyAttribute3", null);
		assertText("\n  ");

		assertAttributeHeader("uglyAttribute4", "char", 0);
		assertText("char     uglyAttribute4='\"';");
		assertAttribute("uglyAttribute4", null);
		assertText("\n  ");

		assertAttributeHeader("uglyAttribute5", "char", 0);
		assertText("char     uglyAttribute5='\\\'';");
		assertAttribute("uglyAttribute5", null);
		assertText("\n  ");

		assertAttributeHeader("uglyAttribute6", "String[]", 0);
		assertText("String[] uglyAttribute6=\n  {\n	 \"some'Thing{some\\\"Thing;Else\", // ugly ; { \" ' comment\n	 \"some'Thing{some\\\"Thing;Else\"\n  };");
		assertAttribute("uglyAttribute6", null);
		assertText("\n  ");

		assertAttributeHeader("uglyAttribute7", "char[]", 0);
		assertText("char[]   uglyAttribute7={';','{','\"','\\\''};");
		assertAttribute("uglyAttribute7", null);
		assertText("\n  ");

		assertAttributeHeader("uglyAttribute8", "Runnable", 0);
		assertText(
"Runnable uglyAttribute8=new Runnable()\n"+
"  {\n\t // ugly ; { \" ' comment\n\t String   uglyInnerAttribute1=\"some'Thing{some\\\"Thing;Else\";\n\t char     uglyInnerAttribute2=';';\n\t char     uglyInnerAttribute3='{';\n\t char     uglyInnerAttribute4='\"';\n\t char     uglyInnerAttribute5='\\\'';\n\t String[] uglyInnerAttribute6=\n\t {\n\t\t\"some'Thing{some\\\"Thing;Else\", // ugly ; { \" ' comment\n\t\t\"some'Thing{some\\\"Thing;Else\"\n\t };\n\t char[]   uglyInnerAttribute7={';','{','\"','\\\''};\n\t public void run()\n\t {\n\t\t// ugly ; { \" ' comment\n\t\tString   uglyVariable1=\"some'Thing{some\\\"Thing;Else\";\n\t\tchar     uglyVariable2=';';\n\t\tchar     uglyVariable3='{';\n\t\tchar     uglyVariable4='\"';\n\t\tchar     uglyVariable5='\\\'';\n\t\tString[] uglyVariable6=\n\t\t{\n\t\t  \"some'Thing{some\\\"Thing;Else\", // ugly ; { \" ' comment\n\t\t  \"some'Thing{some\\\"Thing;Else\"\n\t\t};\n\t\tchar[]   uglyAttribute7={';','{','\"','\\\''};\n\t }\n\t // ugly ; { \" ' comment\n  };");
		assertAttribute("uglyAttribute8", null);
		//assertText("\n  ");
	}

}
