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
		assertText(
			"/*\nSome initial test comment. \n"
				+ "*/\n\npackage// hallo\n"
				+ "  com.exedio.cope.instrument");
		assertPackage("com.exedio.cope.instrument");

		assertText(";\n\nimport java.util.*");
		assertImport("java.util.*");

		assertText(";\nimport java.text.Format");
		assertImport("java.text.Format");
		assertText(";\n\n");

		assertFileDocComment(
			"/**\n	Represents an attribute or association partner of a class.\n"
				+ "	Note: type==Model.AMIGOUS means, the attribute cannot be used in OCL due to attribute ambiguities.\n"
				+ "	See OCL spec 5.4.1. for details.\n*/");
		assertText("\npublic abstract class Example");
		assertClass("Example");
		assertText(" implements Runnable\n{\n  ");

		final JavaAttribute name =
			assertAttributeHeader("name", "String", Modifier.PRIVATE);
		assertText("private String name;");
		assertAttribute("name", null, name);
		assertText("\n  ");

		final JavaAttribute type =
			assertAttributeHeader("type", "Integer", Modifier.PRIVATE);
		assertText("private Integer type=new Integer(5);");
		assertAttribute("type", null, type);
		assertText("\n  ");

		final JavaAttribute qualifiers =
			assertAttributeHeader(
				"qualifiers",
				"Integer[]",
				Modifier.PRIVATE | Modifier.VOLATILE);
		assertText("private volatile Integer[] qualifiers;");
		assertAttribute("qualifiers", null, qualifiers);
		assertText("\n  ");

		final JavaAttribute hallo = assertAttributeHeader("hallo", "String", 0);
		assertText("String hallo=\"hallo\";");
		assertAttribute("hallo", null, hallo);
		assertText("\n  \n  ");

		assertDocComment("/**TestCommentCommaSeparated123*/");
		assertText("\n  ");
		final JavaAttribute commaSeparated1 =
			assertAttributeHeader("commaSeparated1", "int", 0);
		assertText("int commaSeparated1,commaSeparated2=0,commaSeparated3;");
		assertAttribute(
			"commaSeparated1",
			"/**TestCommentCommaSeparated123*/",
			commaSeparated1);
		assertAttributeCommaSeparated(
			"commaSeparated2",
			"/**TestCommentCommaSeparated123*/");
		assertAttributeCommaSeparated(
			"commaSeparated3",
			"/**TestCommentCommaSeparated123*/");
		assertText(" \n  ");

		assertDocComment("/**TestCommentCommaSeparated456*/");
		assertText("\n  ");
		final JavaAttribute commaSeparated4 =
			assertAttributeHeader("commaSeparated4", "int", 0);
		assertText("int commaSeparated4=80,commaSeparated5,commaSeparated6=200;");
		assertAttribute(
			"commaSeparated4",
			"/**TestCommentCommaSeparated456*/",
			commaSeparated4);
		assertAttributeCommaSeparated(
			"commaSeparated5",
			"/**TestCommentCommaSeparated456*/");
		assertAttributeCommaSeparated(
			"commaSeparated6",
			"/**TestCommentCommaSeparated456*/");
		assertText(
			" \n\n  // these attributes test the ability of the parser\n"
				+ "  // to skip more complex (ugly) attribute initializers\n  ");

		final JavaAttribute uglyAttribute1 =
			assertAttributeHeader("uglyAttribute1", "String", 0);
		assertText("String   uglyAttribute1=\"some'Thing{some\\\"Thing;Else\";");
		assertAttribute("uglyAttribute1", null, uglyAttribute1);
		assertText("\n  ");

		final JavaAttribute uglyAttribute2 =
			assertAttributeHeader("uglyAttribute2", "char", 0);
		assertText("char     uglyAttribute2=';';");
		assertAttribute("uglyAttribute2", null, uglyAttribute2);
		assertText("\n  ");

		final JavaAttribute uglyAttribute3 =
			assertAttributeHeader("uglyAttribute3", "char", 0);
		assertText("char     uglyAttribute3='{';");
		assertAttribute("uglyAttribute3", null, uglyAttribute3);
		assertText("\n  ");

		final JavaAttribute uglyAttribute4 =
			assertAttributeHeader("uglyAttribute4", "char", 0);
		assertText("char     uglyAttribute4='\"';");
		assertAttribute("uglyAttribute4", null, uglyAttribute4);
		assertText("\n  ");

		final JavaAttribute uglyAttribute5 =
			assertAttributeHeader("uglyAttribute5", "char", 0);
		assertText("char     uglyAttribute5='\\\'';");
		assertAttribute("uglyAttribute5", null, uglyAttribute5);
		assertText("\n  ");

		final JavaAttribute uglyAttribute6 =
			assertAttributeHeader("uglyAttribute6", "String[]", 0);
		assertText(
			"String[] uglyAttribute6=\n"
				+ "  {\n"
				+ "	 \"some'Thing{some\\\"Thing;Else\", // ugly ; { \" ' comment\n"
				+ "	 \"some'Thing{some\\\"Thing;Else\"\n"
				+ "  };");
		assertAttribute("uglyAttribute6", null, uglyAttribute6);
		assertText("\n  ");

		final JavaAttribute uglyAttribute7 =
			assertAttributeHeader("uglyAttribute7", "char[]", 0);
		assertText("char[]   uglyAttribute7={';','{','\"','\\\''};");
		assertAttribute("uglyAttribute7", null, uglyAttribute7);
		assertText("\n  ");

		final JavaAttribute uglyAttribute8 =
			assertAttributeHeader("uglyAttribute8", "Runnable", 0);
		assertText(
			"Runnable uglyAttribute8=new Runnable()\n"
				+ "  {\n"
				+ "\t // ugly ; { \" ' comment\n"
				+ "\t String   uglyInnerAttribute1=\"some'Thing{some\\\"Thing;Else\";\n"
				+ "\t char     uglyInnerAttribute2=';';\n"
				+ "\t char     uglyInnerAttribute3='{';\n"
				+ "\t char     uglyInnerAttribute4='\"';\n"
				+ "\t char     uglyInnerAttribute5='\\\'';\n"
				+ "\t String[] uglyInnerAttribute6=\n"
				+ "\t {\n\t\t\"some'Thing{some\\\"Thing;Else\", // ugly ; { \" ' comment\n"
				+ "\t\t\"some'Thing{some\\\"Thing;Else\"\n"
				+ "\t };\n"
				+ "\t char[]   uglyInnerAttribute7={';','{','\"','\\\''};\n"
				+ "\t public void run()\n"
				+ "\t {\n"
				+ "\t\t// ugly ; { \" ' comment\n"
				+ "\t\tString   uglyVariable1=\"some'Thing{some\\\"Thing;Else\";\n"
				+ "\t\tchar     uglyVariable2=';';\n"
				+ "\t\tchar     uglyVariable3='{';\n"
				+ "\t\tchar     uglyVariable4='\"';\n"
				+ "\t\tchar     uglyVariable5='\\\'';\n"
				+ "\t\tString[] uglyVariable6=\n"
				+ "\t\t{\n"
				+ "\t\t  \"some'Thing{some\\\"Thing;Else\", // ugly ; { \" ' comment\n"
				+ "\t\t  \"some'Thing{some\\\"Thing;Else\"\n"
				+ "\t\t};\n"
				+ "\t\tchar[]   uglyAttribute7={';','{','\"','\\\''};\n"
				+ "\t }\n\t // ugly ; { \" ' comment"
				+ "\n  };");
		assertAttribute("uglyAttribute8", null, uglyAttribute8);
		assertText("\n  // end of ugly attributes\n  \n\n  ");

		final JavaClass innerClass = assertClass("Inner");
		assertText("class Inner implements Runnable\n  {\n\t ");

		final JavaClass drinnerClass = assertClass("Drinner");
		assertText("class Drinner implements Runnable\n\t {\n\t\t");

		final JavaAttribute someDrinnerBoolean =
			assertAttributeHeader("someDrinnerBoolean", "boolean", 0);
		assertText("boolean someDrinnerBoolean=true;");
		assertAttribute("someDrinnerBoolean", null, someDrinnerBoolean);
		assertText("\n    \n\t\t");

		final JavaBehaviour drinnerRunMethod =
			assertBehaviourHeader(
				"run",
				"void",
				Modifier.PUBLIC,
				"public void run()\n\t\t");
		assertText("{\n\t\t}");
		assertMethod("run", null, drinnerRunMethod);
		assertText("\n\t ");

		assertClassEnd("Drinner", drinnerClass);
		assertInnerClassAttribute("Drinner", null);
		assertText("}\n\n\t ");

		final JavaAttribute someInnerBoolean =
			assertAttributeHeader("someInnerBoolean", "boolean", 0);
		assertText("boolean someInnerBoolean=true;");
		assertAttribute("someInnerBoolean", null, someInnerBoolean);
		assertText("\n    \n\t ");

		final JavaBehaviour innerRunMethod =
			assertBehaviourHeader(
				"run",
				"void",
				Modifier.PUBLIC,
				"public void run()\n\t ");
		assertText("{\n\t }");
		assertMethod("run", null, innerRunMethod);
		assertText("\n  ");

		assertClassEnd("Inner", innerClass);
		assertInnerClassAttribute("Inner", null);
		assertText("}  \n\n  ");

		final JavaBehaviour emptyConstructor =
			assertBehaviourHeader(
				"Example",
				null,
				Modifier.PRIVATE,
				"private Example()\n  ");
		assertText("{\n\t new Integer(5);\n  }");
		assertMethod("Example", null, emptyConstructor);
		assertText("\n  \n  ");

		final JavaBehaviour secondConstructor =
			assertBehaviourHeader(
				"Example",
				null,
				Modifier.PUBLIC,
				"public Example(String name, Integer type)\n  ");
		assertText("{\n\t super();\n  }");
		assertMethod("Example", null, secondConstructor);
		assertText("\n\n  ");

		final JavaBehaviour setter =
			assertBehaviourHeader(
				"set",
				"void",
				Modifier.PUBLIC,
				"public void set(String name, Integer type,// what a cool parameter\n"
					+ "\tfinal Integer[] qualifiers)\n\t");
		assertText(
			"{\n"
				+ "\t\t// ugly comment : { {\n"
				+ "\t\tString x=\"ugly { string \\\" { literal\";\n"
				+ "\t\tchar c='{';\n"
				+ "\t\t\n"
				+ "\t\t/**\n"
				+ "\t\tugly comment *\n"
				+ "\t\t**/\n"
				+ "\t\t\n"
				+ "\t\tint a=20;// some other comment\n"
				+ "\t\tint b=10;\n"
				+ "\t\ta=a/(a+b); // ugly expression\n"
				+ "\t}");
		assertMethod("set", null, setter);
		assertText("\n\n  ");

		final JavaBehaviour abstractMethod =
			assertBehaviourHeader(
				"abstractMethod",
				"void",
				Modifier.ABSTRACT,
				"abstract void abstractMethod()");
		assertText(";");
		assertMethod("abstractMethod", null, abstractMethod);
		assertText("\n\n  ");

		final String runComment = "/**\n\t  Some example doc-comment.\n  */";
		assertDocComment(runComment);
		assertText("\n  ");
		final JavaBehaviour emptyMethod =
			assertBehaviourHeader(
				"run",
				"void",
				Modifier.PUBLIC,
				"public void run()\n  ");
		assertText("{}");
		assertMethod("run", runComment, emptyMethod);
		assertText("\n\n\t");

		final JavaBehaviour getBoolean =
			assertBehaviourHeader(
				"getBoolean",
				"boolean",
				Modifier.PUBLIC,
				"public boolean getBoolean(Interface someInterface)\n\t");
		assertText("{\n\t\treturn true;\n\t}");
		assertMethod("getBoolean", null, getBoolean);
		assertText("\n\t\n\t");

		final JavaBehaviour getIntegers =
			assertBehaviourHeader(
				"getIntegers",
				"Integer[]",
				Modifier.PUBLIC,
				"public Integer[] getIntegers()\n\t");
		assertText("{\n\t\treturn null;\n\t}");
		assertMethod("getIntegers", null, getIntegers);
		assertText("\n\t\n\t");

		final JavaBehaviour getUnqualifiedType =
			assertBehaviourHeader(
				"getUnqualifiedType",
				"Integer",
				Modifier.PUBLIC,
				"public Integer getUnqualifiedType() throws IllegalArgumentException\n\t");
		assertText("{\n\t\treturn null;\n\t}");
		assertMethod("getUnqualifiedType", null, getUnqualifiedType);
		assertText("\n\t\n\t");

		final JavaBehaviour setParent =
			assertBehaviourHeader(
				"setParent",
				"void",
				Modifier.PUBLIC,
				"public void setParent  (Object parent)\n\t\tthrows\n\t\t\tIllegalArgumentException,\n\t\t\tNullPointerException\n\t");
		assertText("{\n\t}");
		assertMethod("setParent", null, setParent);
		assertText("\n\n\t");

		final JavaBehaviour printData =
			assertBehaviourHeader(
				"printData",
				"void",
				Modifier.PUBLIC,
				"public void printData\n" + "\t\t(java.io.PrintStream o)\n\t");
		assertText("{\n\t}");
		assertMethod("printData", null, printData);
		assertText("\n  \n\t");

		final JavaBehaviour accessifierPrivate =
			assertBehaviourHeader(
				"accessifierPrivate",
				"void",
				Modifier.PRIVATE,
				"private void accessifierPrivate() ");
		assertText("{}");
		assertMethod("accessifierPrivate", null, accessifierPrivate);
		assertText("\n\t");

		final JavaBehaviour accessifierProtected =
			assertBehaviourHeader(
				"accessifierProtected",
				"void",
				Modifier.PROTECTED,
				"protected void accessifierProtected() ");
		assertText("{}");
		assertMethod("accessifierProtected", null, accessifierProtected);
		assertText("\n\t");
	}

}
