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

package com.exedio.cope.util;

import java.io.File;
import java.util.Arrays;

import com.exedio.cope.junit.CopeAssert;

public class PropertiesTest extends CopeAssert
{
	static class TestProperties extends Properties
	{
		final BooleanField boolFalse = new BooleanField("boolFalse", false);
		final BooleanField boolTrue = new BooleanField("boolTrue", true);
		final IntField int10 = new IntField("int10", 10, 5);
		final StringField stringMandatory = new StringField("stringMandatory");
		final StringField stringOptional = new StringField("stringOptional", "stringOptional.defaultValue");
		final StringField stringHidden = new StringField("stringHidden", true);
		final FileField file = new FileField("file");
		final MapField map = new MapField("map");
		
		public TestProperties(final java.util.Properties properties, final String source)
		{
			super(properties, source);
		}
		
		public TestProperties(final java.util.Properties properties, final String source, final Context context)
		{
			super(properties, source, context);
		}
		
		void assertIt()
		{
			assertEqualsUnmodifiable(Arrays.asList(new Properties.Field[]{
					boolFalse,
					boolTrue,
					int10,
					stringMandatory,
					stringOptional,
					stringHidden,
					file,
					map,
			}), getFields());
			
			assertEquals("boolFalse", boolFalse.getKey());
			assertEquals("boolTrue", boolTrue.getKey());
			assertEquals("int10", int10.getKey());
			assertEquals("stringMandatory", stringMandatory.getKey());
			assertEquals("stringOptional", stringOptional.getKey());
			assertEquals("stringHidden", stringHidden.getKey());
			assertEquals("file", file.getKey());
			assertEquals("map", map.getKey());
			
			assertEquals(Boolean.FALSE, boolFalse.getDefaultValue());
			assertEquals(Boolean.TRUE, boolTrue.getDefaultValue());
			assertEquals(Integer.valueOf(10), int10.getDefaultValue());
			assertEquals(null, stringMandatory.getDefaultValue());
			assertEquals("stringOptional.defaultValue", stringOptional.getDefaultValue());
			assertEquals(null, stringHidden.getDefaultValue());
			assertEquals(null, file.getDefaultValue());
			assertEquals(null, map.getDefaultValue());

			assertEquals(false, boolFalse.hasHiddenValue());
			assertEquals(false, boolTrue.hasHiddenValue());
			assertEquals(false, int10.hasHiddenValue());
			assertEquals(false, stringMandatory.hasHiddenValue());
			assertEquals(false, stringOptional.hasHiddenValue());
			assertEquals(true, stringHidden.hasHiddenValue());
			assertEquals(false, file.hasHiddenValue());
			assertEquals(false, map.hasHiddenValue());
		}
	}
	
	File file1;
	File file2;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		file1 = File.createTempFile("exedio-cope-PropertiesTest-", ".tmp");
		file2 = File.createTempFile("exedio-cope-PropertiesTest-", ".tmp");
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		file1.delete();
		file2.delete();

		super.tearDown();
	}
	
	public void testIt()
	{
		final java.util.Properties pminimal = new java.util.Properties();
		pminimal.setProperty("stringMandatory", "stringMandatory.minimalValue");
		pminimal.setProperty("stringHidden", "stringHidden.minimalValue");
		
		final TestProperties minimal = new TestProperties(pminimal, "minimal");
		minimal.assertIt();
		assertEquals("minimal", minimal.getSource());
		
		assertEquals(false, minimal.boolFalse.getBooleanValue());
		assertEquals(true, minimal.boolTrue.getBooleanValue());
		assertEquals(Boolean.FALSE, minimal.boolFalse.getValue());
		assertEquals(Boolean.TRUE, minimal.boolTrue.getValue());
		assertEquals(10, minimal.int10.getIntValue());
		assertEquals(Integer.valueOf(10), minimal.int10.getValue());
		assertEquals("stringMandatory.minimalValue", minimal.stringMandatory.getStringValue());
		assertEquals("stringOptional.defaultValue", minimal.stringOptional.getStringValue());
		assertEquals("stringHidden.minimalValue", minimal.stringHidden.getStringValue());
		assertEquals("stringMandatory.minimalValue", minimal.stringMandatory.getValue());
		assertEquals("stringOptional.defaultValue", minimal.stringOptional.getValue());
		assertEquals("stringHidden.minimalValue", minimal.stringHidden.getValue());
		assertEquals(null, minimal.file.getFileValue());
		assertEquals(null, minimal.file.getValue());
		assertEquals(new java.util.Properties(), minimal.map.getMapValue());
		assertEquals(new java.util.Properties(), minimal.map.getValue());
		assertEquals(null, minimal.map.getValue("explicitKey1"));
		
		assertEquals(false, minimal.boolFalse.isSpecified());
		assertEquals(false, minimal.boolTrue.isSpecified());
		assertEquals(false, minimal.int10.isSpecified());
		assertEquals(true, minimal.stringMandatory.isSpecified());
		assertEquals(false, minimal.stringOptional.isSpecified());
		assertEquals(true, minimal.stringHidden.isSpecified());
		assertEquals(false, minimal.file.isSpecified());
		assertEquals(false, minimal.map.isSpecified());
		
		minimal.ensureEquality(minimal);
		
		{
			final TestProperties minimal1 = new TestProperties(pminimal, "minimal2");
			assertEquals("minimal2", minimal1.getSource());
			minimal.ensureEquality(minimal1);
			minimal1.ensureEquality(minimal);
		}
		{
			final java.util.Properties p = copy(pminimal);
			p.setProperty("boolFalse", "true");
			p.setProperty("boolTrue", "false");
			p.setProperty("int10", "20");
			p.setProperty("stringMandatory", "stringMandatory.explicitValue");
			p.setProperty("stringOptional", "stringOptional.explicitValue");
			p.setProperty("stringHidden", "stringHidden.explicitValue");
			p.setProperty("file", file1.getPath());
			p.setProperty("map.explicitKey1", "map.explicitValue1");
			p.setProperty("map.explicitKey2", "map.explicitValue2");
			final TestProperties tp = new TestProperties(p, "maximal");
			assertEquals("maximal", tp.getSource());
			
			assertEquals(true, tp.boolFalse.getBooleanValue());
			assertEquals(false, tp.boolTrue.getBooleanValue());
			assertEquals(Boolean.TRUE, tp.boolFalse.getValue());
			assertEquals(Boolean.FALSE, tp.boolTrue.getValue());
			assertEquals(20, tp.int10.getIntValue());
			assertEquals(Integer.valueOf(20), tp.int10.getValue());
			assertEquals("stringMandatory.explicitValue", tp.stringMandatory.getStringValue());
			assertEquals("stringOptional.explicitValue", tp.stringOptional.getStringValue());
			assertEquals("stringHidden.explicitValue", tp.stringHidden.getStringValue());
			assertEquals("stringMandatory.explicitValue", tp.stringMandatory.getValue());
			assertEquals("stringOptional.explicitValue", tp.stringOptional.getValue());
			assertEquals("stringHidden.explicitValue", tp.stringHidden.getValue());
			assertEquals(file1, tp.file.getFileValue());
			assertEquals(file1, tp.file.getValue());
			java.util.Properties mapExpected = new java.util.Properties();
			mapExpected.setProperty("explicitKey1", "map.explicitValue1");
			mapExpected.setProperty("explicitKey2", "map.explicitValue2");
			assertEquals(mapExpected, tp.map.getMapValue());
			assertEquals(mapExpected, tp.map.getValue());
			assertEquals("map.explicitValue1", tp.map.getValue("explicitKey1"));
			assertEquals("map.explicitValue2", tp.map.getValue("explicitKey2"));
			assertEquals(null, tp.map.getValue("explicitKeyNone"));

			assertEquals(true, tp.boolFalse.isSpecified());
			assertEquals(true, tp.boolTrue.isSpecified());
			assertEquals(true, tp.int10.isSpecified());
			assertEquals(true, tp.stringMandatory.isSpecified());
			assertEquals(true, tp.stringOptional.isSpecified());
			assertEquals(true, tp.stringHidden.isSpecified());
			assertEquals(true, tp.file.isSpecified());
			assertEquals(false, tp.map.isSpecified()); // TODO
		}
		{
			final java.util.Properties p = copy(pminimal);
			p.setProperty("wrongKey.zack", "somethingZack");
			final TestProperties tp = new TestProperties(p, "wrongkey");
			assertEquals("wrongkey", tp.getSource());
			tp.ensureValidity("wrongKey");
			try
			{
				tp.ensureValidity();
				fail();
			}
			catch(IllegalArgumentException e)
			{
				assertEquals(
						"property wrongKey.zack in wrongkey is not allowed, but only one of [" +
						"boolFalse, " +
						"boolTrue, " +
						"int10, " +
						"stringMandatory, " +
						"stringOptional, " +
						"stringHidden, " +
						"file] " +
						"or one starting with [map.].", e.getMessage());
			}
		}
		
		// boolean
		assertWrong(pminimal,
				"wrong.bool.true",
				"boolFalse", "True",
				"property boolFalse in wrong.bool.true has invalid value," +
					" expected >true< or >false<, but got >True<.");
		assertWrong(pminimal,
				"wrong.bool.false",
				"boolFalse", "falsE",
				"property boolFalse in wrong.bool.false has invalid value," +
					" expected >true< or >false<, but got >falsE<.");
		assertInconsistent(pminimal,
				"inconsistent.bool",
				"boolFalse", "true",
				"inconsistent initialization for boolFalse between minimal and inconsistent.bool," +
					" expected false but got true.",
				"inconsistent initialization for boolFalse between inconsistent.bool and minimal," +
					" expected true but got false.");
		
		// int
		{
			// test lowest value
			final java.util.Properties p = copy(pminimal);
			p.setProperty("int10", "5");
			final TestProperties tp = new TestProperties(p, "int.border");
			assertEquals(5, tp.int10.getIntValue());
			assertEquals(Integer.valueOf(5), tp.int10.getValue());
		}
		assertWrong(pminimal,
				"wrong.int.tooSmall",
				"int10", "4",
				"property int10 in wrong.int.tooSmall has invalid value," +
				" expected an integer greater or equal 5, but got 4.");
		assertWrong(pminimal,
				"wrong.int.noNumber",
				"int10", "10x",
				"property int10 in wrong.int.noNumber has invalid value," +
				" expected an integer greater or equal 5, but got >10x<.");
		assertInconsistent(pminimal,
				"inconsistent.int",
				"int10", "88",
				"inconsistent initialization for int10 between minimal and inconsistent.int," +
					" expected 10 but got 88.",
				"inconsistent initialization for int10 between inconsistent.int and minimal," +
					" expected 88 but got 10.");

		// String
		assertWrong(pminimal,
				"wrong.stringMandatory.missing",
				"stringMandatory", null,
				"property stringMandatory in wrong.stringMandatory.missing not set and not default value specified.");
		assertWrong(pminimal,
				"wrong.stringHidden.missing",
				"stringHidden", null,
				"property stringHidden in wrong.stringHidden.missing not set and not default value specified.");
		assertInconsistent(pminimal,
				"inconsistent.stringMandatory",
				"stringMandatory", "stringMandatory.inconsistentValue",
				"inconsistent initialization for stringMandatory between minimal and inconsistent.stringMandatory," +
					" expected stringMandatory.minimalValue but got stringMandatory.inconsistentValue.",
				"inconsistent initialization for stringMandatory between inconsistent.stringMandatory and minimal," +
					" expected stringMandatory.inconsistentValue but got stringMandatory.minimalValue.");
		assertInconsistent(pminimal,
				"inconsistent.stringOptional",
				"stringOptional", "stringOptional.inconsistentValue",
				"inconsistent initialization for stringOptional between minimal and inconsistent.stringOptional," +
					" expected stringOptional.defaultValue but got stringOptional.inconsistentValue.",
				"inconsistent initialization for stringOptional between inconsistent.stringOptional and minimal," +
					" expected stringOptional.inconsistentValue but got stringOptional.defaultValue.");
		assertInconsistent(pminimal,
				"inconsistent.stringHidden",
				"stringHidden", "stringHidden.inconsistentValue",
				"inconsistent initialization for stringHidden between minimal and inconsistent.stringHidden.",
				"inconsistent initialization for stringHidden between inconsistent.stringHidden and minimal.");
		
		// File
		assertInconsistent(pminimal,
				"inconsistent.file",
				"file", file2.getPath(),
				"inconsistent initialization for file between minimal and inconsistent.file," +
					" expected null but got " + file2.getPath() + ".",
				"inconsistent initialization for file between inconsistent.file and minimal," +
					" expected " + file2.getPath() + " but got null.");
		
		final java.util.Properties fileInconsistency = copy(pminimal);
		fileInconsistency.setProperty("file", file1.getPath());
		assertInconsistent(fileInconsistency,
				"inconsistent.file",
				"file", file2.getPath(),
				"inconsistent initialization for file between minimal and inconsistent.file," +
					" expected " + file1.getPath() + " but got " + file2.getPath() + ".",
				"inconsistent initialization for file between inconsistent.file and minimal," +
					" expected " + file2.getPath() + " but got " + file1.getPath() + ".");
		
		// Map
		assertInconsistent(pminimal,
				"inconsistent.map",
				"map.inconsistentKey", "map.inconsistentValue",
				"inconsistent initialization for map between minimal and inconsistent.map," +
					" expected {} but got {inconsistentKey=map.inconsistentValue}.",
				"inconsistent initialization for map between inconsistent.map and minimal," +
					" expected {inconsistentKey=map.inconsistentValue} but got {}.");
	}
	
	private void assertWrong(
			final java.util.Properties template,
			final String source,
			final String key,
			final String value,
			final String message)
	{
		final java.util.Properties wrongProps = copy(template);
		if(value!=null)
			wrongProps.setProperty(key, value);
		else
			wrongProps.remove(key);
		
		try
		{
			new TestProperties(wrongProps, source);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(message, e.getMessage());
		}
	}
	
	private void assertInconsistent(
			final java.util.Properties template,
			final String source,
			final String key,
			final String value,
			final String message1, final String message2)
	{
		final TestProperties templateProps = new TestProperties(template, "minimal");
		templateProps.assertIt();
		
		final java.util.Properties p = copy(template);
		p.setProperty(key, value);
		final TestProperties inconsistentProps = new TestProperties(p, source);
		try
		{
			templateProps.ensureEquality(inconsistentProps);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(message1, 	e.getMessage());
		}
		try
		{
			inconsistentProps.ensureEquality(templateProps);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(message2, 	e.getMessage());
		}
	}
	
	private static final java.util.Properties copy(final java.util.Properties source)
	{
		return (java.util.Properties)source.clone();
	}
	
	public void testContext()
	{
		assertContext("y", "${x}");
		assertContext("bucket", "${eimer}");
		assertContext("bucketpostfix", "${eimer}postfix");
		assertContext("prefixbucket", "prefix${eimer}");
		assertContext("prefixbucketpostfix", "prefix${eimer}postfix");
		assertContext("bucketinfixwater", "${eimer}infix${wasser}");
		assertContext("bucketinfixwaterpostfix", "${eimer}infix${wasser}postfix");
		assertContext("prefixbucketinfixwater", "prefix${eimer}infix${wasser}");
		assertContext("prefixbucketinfixwaterpostfix", "prefix${eimer}infix${wasser}postfix");
		assertContext("x$kkk", "x$kkk");

		try
		{
			getContext("${nixus}");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("key 'nixus' not defined by context TestContext", e.getMessage());
		}
		try
		{
			getContext("x${}y");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("${} not allowed in x${}y", e.getMessage());
		}
		try
		{
			getContext("x${kkk");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("missing '}' in x${kkk", e.getMessage());
		}
	}
	
	private static final TestProperties getContext(final String raw)
	{
		final java.util.Properties pminimal = new java.util.Properties();
		pminimal.setProperty("stringMandatory", raw);
		pminimal.setProperty("stringHidden", "stringHidden.minimalValue");
		final TestProperties minimal = new TestProperties(pminimal, "minimal", new Properties.Context(){

			public String get(final String key)
			{
				if("x".equals(key))
					return "y";
				else if("eimer".equals(key))
					return "bucket";
				else if("wasser".equals(key))
					return "water";
				else if("nixus".equals(key))
					return null;
				else
					throw new RuntimeException(key);
			}
			
			@Override
			public String toString()
			{
				return "TestContext";
			}
		});
		
		return minimal;
	}
	
	private static final void assertContext(final String replaced, final String raw)
	{
		assertEquals(replaced, getContext(raw).stringMandatory.getStringValue());
	}
	
	public void testGetContext()
	{
		final java.util.Properties pcontext = new java.util.Properties();
		pcontext.setProperty("stringMandatory", "stringMandatory.minimalValue");
		pcontext.setProperty("stringHidden", "stringHidden.minimalValue");
		final TestProperties context = new TestProperties(pcontext, "context", new Properties.Context(){

			public String get(final String key)
			{
				if("a".equals(key))
					return "b";
				else if("a1".equals(key))
					return "b1";
				else if("n".equals(key))
					return null;
				else
					throw new RuntimeException(key);
			}
			
			@Override
			public String toString()
			{
				return "TestGetContext";
			}
		});
		assertEquals("b", context.getContext("a"));
		assertEquals("b1", context.getContext("a1"));
		
		try
		{
			context.getContext(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("key must not be null", e.getMessage());
		}
		try
		{
			context.getContext("n");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("no value available for key >n< in context TestGetContext", e.getMessage());
		}

		final TestProperties none = new TestProperties(pcontext, "none", null);
		try
		{
			none.getContext("c");
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("no context available", e.getMessage());
		}
	}
}
