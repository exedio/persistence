/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.instrument;

import java.lang.reflect.Modifier;

public class ExampleTest extends InjectorTest
{

	public ExampleTest()
	{
		super("Example.java", true);
	}

	@Override
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

		final String fileDocComment =
			"/**\n	Represents an attribute or association partner of a class.\n"
				+ "	Note: type==Model.AMIGOUS means, the attribute cannot be used in OCL due to attribute ambiguities.\n"
				+ "	See OCL spec 5.4.1. for details.\n*/";
		assertFileDocComment(fileDocComment);
		assertText(fileDocComment+"\npublic abstract class Example implements Runnable\n");
		final JavaClass exampleClass = assertClass("Example", null, new String[]{"Runnable"});
		assertText("{\n  ");

		final JavaAttribute name =
			assertAttributeHeader("name", "String", Modifier.PRIVATE);
		assertText("private String name;");
		assertAttribute("name", null, name);
		assertEquals(null, name.getInitializer());
		assertText("\n  ");

		final JavaAttribute type =
			assertAttributeHeader("type", "Integer", Modifier.PRIVATE);
		assertText("private Integer type=new Integer(5);");
		assertAttribute("type", null, type);
		assertEquals("new Integer(5)", type.getInitializer());
		assertText("\n  ");

		final JavaAttribute qualifiers =
			assertAttributeHeader(
				"qualifiers",
				"Integer[]",
				Modifier.PRIVATE | Modifier.VOLATILE);
		assertText("private volatile Integer[] qualifiers;");
		assertAttribute("qualifiers", null, qualifiers);
		assertEquals(null, qualifiers.getInitializer());
		assertText("\n  ");

		final JavaAttribute hallo = assertAttributeHeader("hallo", "String", 0);
		assertText("String hallo=\"hallo\";");
		assertAttribute("hallo", null, hallo);
		assertEquals("\"hallo\"", hallo.getInitializer());
		assertText("\n  \n  ");

		assertDocComment("/**TestCommentCommaSeparated123*/");
		assertText("/**TestCommentCommaSeparated123*/\n  ");
		final JavaAttribute commaSeparated1 =
			assertAttributeHeader("commaSeparated1", "int", 0);
		assertText("int commaSeparated1,commaSeparated2=0,commaSeparated3;");
		assertAttribute(
			"commaSeparated1",
			"/**TestCommentCommaSeparated123*/",
			commaSeparated1);
		assertEquals(null, commaSeparated1.getInitializer());
		assertAttributeCommaSeparated(
			"commaSeparated2",
			"/**TestCommentCommaSeparated123*/");
		assertAttributeCommaSeparated(
			"commaSeparated3",
			"/**TestCommentCommaSeparated123*/");
		assertText(" \n  ");

		assertDocComment("/**TestCommentCommaSeparated456*/");
		assertText("/**TestCommentCommaSeparated456*/\n  ");
		final JavaAttribute commaSeparated4 =
			assertAttributeHeader("commaSeparated4", "int", 0);
		assertText("int commaSeparated4=80,commaSeparated5,commaSeparated6=200;");
		assertAttribute(
			"commaSeparated4",
			"/**TestCommentCommaSeparated456*/",
			commaSeparated4);
		assertEquals("80", commaSeparated4.getInitializer());
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
		assertEquals("\"some'Thing{some\\\"Thing;Else\"", uglyAttribute1.getInitializer());
		assertText("\n  ");

		final JavaAttribute uglyAttribute2 =
			assertAttributeHeader("uglyAttribute2", "char", 0);
		assertText("char     uglyAttribute2=';';");
		assertAttribute("uglyAttribute2", null, uglyAttribute2);
		assertEquals("';'", uglyAttribute2.getInitializer());
		assertText("\n  ");

		final JavaAttribute uglyAttribute3 =
			assertAttributeHeader("uglyAttribute3", "char", 0);
		assertText("char     uglyAttribute3='{';");
		assertAttribute("uglyAttribute3", null, uglyAttribute3);
		assertEquals("'{'", uglyAttribute3.getInitializer());
		assertText("\n  ");

		final JavaAttribute uglyAttribute4 =
			assertAttributeHeader("uglyAttribute4", "char", 0);
		assertText("char     uglyAttribute4='\"';");
		assertAttribute("uglyAttribute4", null, uglyAttribute4);
		assertEquals("'\"'", uglyAttribute4.getInitializer());
		assertText("\n  ");

		final JavaAttribute uglyAttribute5 =
			assertAttributeHeader("uglyAttribute5", "char", 0);
		assertText("char     uglyAttribute5='\\\'';");
		assertAttribute("uglyAttribute5", null, uglyAttribute5);
		assertEquals("'\\\''", uglyAttribute5.getInitializer());
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
		assertEquals("'\\\''", uglyAttribute5.getInitializer());
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
				+ "\t\t\n"
				+ "\t\tSystem.out.println(uglyVariable1+uglyVariable2+uglyVariable3+uglyVariable4+uglyVariable5+uglyVariable6+uglyAttribute7[0]);\n"
				+ "\t }\n\t // ugly ; { \" ' comment"
				+ "\n  };");
		assertAttribute("uglyAttribute8", null, uglyAttribute8);
		assertText("\n  // end of ugly attributes\n  \n\n  ");

		final JavaClass innerClass = assertClass("Inner", null, new String[]{"Runnable"}, exampleClass);
		assertText("class Inner implements Runnable\n  {\n\t ");

		final JavaClass drinnerClass = assertClass("Drinner", null, new String[]{"Runnable"}, innerClass);
		assertText("class Drinner implements Runnable\n\t {\n\t\t");

		final JavaAttribute someDrinnerBoolean =
			assertAttributeHeader("someDrinnerBoolean", "boolean", 0);
		assertText("boolean someDrinnerBoolean=true;");
		assertAttribute("someDrinnerBoolean", null, someDrinnerBoolean);
		assertText("\n    \n\t\t"+"public void run()\n\t\t");

		final JavaBehaviour drinnerRunMethod =
			assertBehaviourHeader(
				"run",
				"void",
				Modifier.PUBLIC);
		assertText("{\n\t\t}");
		assertMethod("run", null, drinnerRunMethod);
		assertText("\n\t ");

		assertClassEnd(drinnerClass);
		assertInnerClassAttribute("Drinner", null);
		assertText("}\n\n\t ");

		final JavaAttribute someInnerBoolean =
			assertAttributeHeader("someInnerBoolean", "boolean", 0);
		assertText("boolean someInnerBoolean=true;");
		assertAttribute("someInnerBoolean", null, someInnerBoolean);
		assertText("\n    \n\t "+"public void run()\n\t ");

		final JavaBehaviour innerRunMethod =
			assertBehaviourHeader(
				"run",
				"void",
				Modifier.PUBLIC);
		assertText("{\n\t }");
		assertMethod("run", null, innerRunMethod);
		assertText("\n  ");

		assertClassEnd(innerClass);
		assertInnerClassAttribute("Inner", null);

		assertText("}  \n\n  ");
		final JavaClass subClass = assertClass("InnerSub", "ExampleTest", new String[]{"java.io.Serializable", "Cloneable"}, exampleClass);
		assertText("static class InnerSub extends ExampleTest implements java.io.Serializable, Cloneable\n  {\n  ");
		assertClassEnd(subClass);
		assertInnerClassAttribute("InnerSub", null);
		assertText("}\n\n  public Example()\n  ");

		final JavaBehaviour emptyConstructor =
			assertBehaviourHeader(
				"Example",
				null,
				Modifier.PUBLIC);
		assertText("{\n\t new Integer(5);\n  }");
		assertMethod("Example", null, emptyConstructor);
		assertText("\n  \n  "+"private Example(String name, Integer type)\n  ");

		final JavaBehaviour secondConstructor =
			assertBehaviourHeader(
				"Example",
				null,
				Modifier.PRIVATE);
		assertText("{\n\t super();\n  }");
		assertMethod("Example", null, secondConstructor);
		assertText("\n\n  "
			+ "public void set(String name, Integer type,// what a cool parameter\n"
			+ "\tfinal Integer[] qualifiers)\n\t");

		final JavaBehaviour setter =
			assertBehaviourHeader(
				"set",
				"void",
				Modifier.PUBLIC);
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
				+ "\t\t\n"
				+ "\t\tSystem.out.println(x+c);\n"
				+ "\t}");
		assertMethod("set", null, setter);
		assertText("\n\n  "+"abstract void abstractMethod()");

		final JavaBehaviour abstractMethod =
			assertBehaviourHeader(
				"abstractMethod",
				"void",
				Modifier.ABSTRACT);
		assertText(";");
		assertMethod("abstractMethod", null, abstractMethod);
		assertText("\n\n  ");

		final String runComment = "/**\n\t  Some example doc-comment.\n  */";
		assertDocComment(runComment);
		assertText("/**\n\t  Some example doc-comment.\n  */\n  "+"public void run()\n  ");
		final JavaBehaviour emptyMethod =
			assertBehaviourHeader(
				"run",
				"void",
				Modifier.PUBLIC);
		assertText("{}");
		assertMethod("run", runComment, emptyMethod);
		assertText("\n\n\t"+"public boolean getBoolean(int someInterface)\n\t");

		final JavaBehaviour getBoolean =
			assertBehaviourHeader(
				"getBoolean",
				"boolean",
				Modifier.PUBLIC);
		assertText("{\n\t\treturn true;\n\t}");
		assertMethod("getBoolean", null, getBoolean);
		assertText("\n\t\n\t"+"public Integer[] getIntegers()\n\t");

		final JavaBehaviour getIntegers =
			assertBehaviourHeader(
				"getIntegers",
				"Integer[]",
				Modifier.PUBLIC);
		assertText("{\n\t\treturn null;\n\t}");
		assertMethod("getIntegers", null, getIntegers);
		assertText("\n\t\n\t");

		assertDocComment("/** DO_DISCARD */");
		final JavaAttribute discardAttribute =
			assertAttributeHeader("discardAttribute", "int", 0);
		assertAttribute("discardAttribute", "/** DO_DISCARD */", discardAttribute);
		assertText("\n\t\n\t");

		assertDocComment("/** DO_DISCARD */");
		assertMethodDiscarded("discardMethod", "/** DO_DISCARD */");

		assertText("\n\t\n\t"+"public Integer getUnqualifiedType() throws IllegalArgumentException\n\t");

		final JavaBehaviour getUnqualifiedType =
			assertBehaviourHeader(
				"getUnqualifiedType",
				"Integer",
				Modifier.PUBLIC);
		assertText("{\n\t\treturn null;\n\t}");
		assertMethod("getUnqualifiedType", null, getUnqualifiedType);
		assertText("\n\t\n\t"+"public void setParent  (Object parent)\n\t\tthrows\n\t\t\tIllegalArgumentException,\n\t\t\tNullPointerException\n\t");

		final JavaBehaviour setParent =
			assertBehaviourHeader(
				"setParent",
				"void",
				Modifier.PUBLIC);
		assertText("{\n\t}");
		assertMethod("setParent", null, setParent);
		assertText("\n\n\t"+"public void printData\n" + "\t\t(java.io.PrintStream o)\n\t");

		final JavaBehaviour printData =
			assertBehaviourHeader(
				"printData",
				"void",
				Modifier.PUBLIC);
		assertText("{\n\t}");
		assertMethod("printData", null, printData);
		assertText("\n  \n\t"+"private void accessifierPrivate() ");

		final JavaBehaviour accessifierPrivate =
			assertBehaviourHeader(
				"accessifierPrivate",
				"void",
				Modifier.PRIVATE);
		assertText("{}");
		assertMethod("accessifierPrivate", null, accessifierPrivate);
		assertText("\n\t"+"protected void accessifierProtected() ");

		final JavaBehaviour accessifierProtected =
			assertBehaviourHeader(
				"accessifierProtected",
				"void",
				Modifier.PROTECTED);
		assertText("{}");
		assertMethod("accessifierProtected", null, accessifierProtected);
		assertText("\n\t"+"void accessifierPackage() ");

		final JavaBehaviour accessifierPackage =
			assertBehaviourHeader(
				"accessifierPackage",
				"void",
				0);
		assertText("{}");
		assertMethod("accessifierPackage", null, accessifierPackage);
		assertText("\n\t"+"public void accessifierPublic() ");

		final JavaBehaviour accessifierPublic =
			assertBehaviourHeader(
				"accessifierPublic",
				"void",
				Modifier.PUBLIC);
		assertText("{}");
		assertMethod("accessifierPublic", null, accessifierPublic);
		assertText("\n  \n\t"+"static public void main(String[] args)\n\t");

		final JavaBehaviour main =
			assertBehaviourHeader(
				"main",
				"void",
				Modifier.PUBLIC|Modifier.STATIC);
		assertText(
				"{\n" +
				"\t\t// use imports\n" +
				"\t\tList l;\n" +
				"\t\tFormat f;\n" +
				"\t\tl=null;\n" +
				"\t\tf=null;\n" +
			   "\t\tSystem.out.println(l.toString()+f.toString());\n" +
				"\t}");
		assertMethod("main", null, main);
		assertText("\n\n");
		
		assertClassEnd(exampleClass);
		assertText("}\n\nclass SecondExample extends Example");
		final JavaClass secondExampleClass = assertClass("SecondExample", "Example", null);
		assertText("{"+"void abstractMethod()");
		
		final JavaBehaviour abstractMethod2 =
			assertBehaviourHeader(
				"abstractMethod",
				"void",
				0);
		assertText("{}");
		assertMethod("abstractMethod", null, abstractMethod2);

		assertClassEnd(secondExampleClass);
		//assertText("}\n\n");
	}

}
