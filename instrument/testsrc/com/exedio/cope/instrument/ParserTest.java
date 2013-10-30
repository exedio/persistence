/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.instrument.Lexer.CommentToken;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import junit.framework.AssertionFailedError;

public abstract class ParserTest extends InstrumentorTest
{
	private final String resourceName;
	final boolean assertText;
	private final String lineSeparator;

	protected ParserTest(final String resourceName, final boolean assertText)
	{
		this.resourceName = resourceName;
		this.assertText = assertText;
		this.lineSeparator = System.getProperty("line.separator");
	}

	LinkedList<ParseEvent> parseEvents;
	private TestParseConsumer testParseConsumer;

	public abstract void assertParse();

	public void testIt()
		throws IOException, ParserException
	{
		final File inputFile = new File(ParserTest.class.getResource(resourceName).getFile());

		parseEvents = new LinkedList<ParseEvent>();
		testParseConsumer = new TestParseConsumer();
		final JavaRepository repository = new JavaRepository();
		final JavaFile javaFile = new JavaFile(repository);
		final Parser parser = new Parser(new Lexer(inputFile, Charset.forName("ascii"), javaFile), testParseConsumer, javaFile);
		if(assertText)
			testParseConsumer.output = parser.javaFile.buffer;
		parser.parseFile();

		assertParse();
		parseEvents = null;
	}

	private ParseEvent fetchEvent()
	{
		return parseEvents.removeFirst();
	}

	private static String format(final String s)
	{
		return s.replace('\n', '#').replace(' ', '_').replace('\t', '~');
	}

	private String replaceLineBreaks(final String s)
	{
		if(s==null)
			return null;

		final StringBuilder result = new StringBuilder();
		int pos;
		int lastpos = -1;
		for(pos = s.indexOf('\n'); pos>=0; pos = s.indexOf('\n', pos+1))
		{
			result.append(s.substring(lastpos+1, pos));
			result.append(lineSeparator);
			lastpos = pos;
		}
		result.append(s.substring(lastpos+1));
		return result.toString();
	}

	protected void assertEqualsText(final String expectedText, final String actualText)
	{
		assertEquals("ZAPP \n>"+format(expectedText)+"<\n>"+format(actualText)+"<\n", replaceLineBreaks(expectedText), actualText);
	}

	protected void assertText(final String text)
	{
		if(!assertText)
			throw new RuntimeException("assertText is false");

		final ParseEvent event = fetchEvent();
		if(!(event instanceof TextEvent))
			throw new AssertionFailedError("expected text event >"+text+"<, but was "+event);
		final String actualText = ((TextEvent)event).text;
		assertEqualsText(text, actualText);
	}

	protected void assertPackage(final String packageName)
	{
		final ParseEvent event = fetchEvent();
		assertEquals(packageName, ((PackageEvent)event).javafile.getPackageName());
	}

	protected void assertImport(final String importText)
	{
		final ParseEvent event = fetchEvent();
		assertEquals(importText, ((ImportEvent)event).importText);
	}

	protected void assertDocComment(final String docComment)
	{
		final ParseEvent event = fetchEvent();
		if(!(event instanceof DocCommentEvent))
			throw new AssertionFailedError("expected docComment event >"+docComment+"<, but was "+event);
		assertEquals(replaceLineBreaks(docComment), ((DocCommentEvent)event).docComment);
	}

	protected void assertFileDocComment(final String docComment)
	{
		final ParseEvent event = fetchEvent();
		assertEquals(replaceLineBreaks(docComment), ((FileDocCommentEvent)event).docComment);
	}

	protected JavaClass assertClass(final String className, final String classExtends)
	{
		return assertClass(className, classExtends, null);
	}

	protected JavaClass assertClass(final String className, final String classExtends, final JavaClass parent)
	{
		final ParseEvent event = fetchEvent();
		if(!(event instanceof ClassEvent))
			throw new RuntimeException(event.toString());
		final JavaClass javaClass = ((ClassEvent)event).javaClass;
		assertEquals(className, javaClass.name);
		assertEquals(classExtends, javaClass.classExtends);
		assertSame(parent, javaClass.parent);
		return javaClass;
	}

	protected void assertClassEnd(final JavaClass expectedJavaClass)
	{
		final ParseEvent event = fetchEvent();
		final JavaClass javaClass = ((ClassEndEvent)event).javaClass;
		assertSame(expectedJavaClass, javaClass);
	}

	protected JavaBehaviour assertBehaviourHeader(final String name, final String type, final int modifier)
	{
		final ParseEvent event = fetchEvent();
		if(!(event instanceof BehaviourHeaderEvent))
			throw new AssertionFailedError("expected BehaviourHeader event >"+name+"<, but was "+event);
		final JavaBehaviour javaBehaviour = ((BehaviourHeaderEvent)event).javaBehaviour;
		assertEquals(name, javaBehaviour.name);
		assertEquals(type, javaBehaviour.type);
		assertEquals(modifier, javaBehaviour.modifier);
		return javaBehaviour;
	}

	protected JavaField assertFieldHeader(final String name, final String type, final int modifier)
	{
		final ParseEvent event = fetchEvent();
		final JavaField javaAttribute = ((FieldHeaderEvent)event).javaField;
		assertEquals(name, javaAttribute.name);
		assertEquals(type, javaAttribute.type);
		assertEquals(modifier, javaAttribute.modifier);
		return javaAttribute;
	}

	private void assertFeature(final String name, final String docComment, final JavaFeature expectedJavaFeature)
	{
		final ParseEvent event = fetchEvent();
		final JavaFeature javaFeature = ((ClassFeatureEvent)event).javaFeature;
		assertEquals(name, javaFeature.name);
		assertEquals(replaceLineBreaks(docComment), ((ClassFeatureEvent)event).docComment);
		if(expectedJavaFeature!=null)
			assertSame(expectedJavaFeature, javaFeature);
	}

	protected void assertField(final String name, final String docComment, final JavaField expectedJavaAttribute)
	{
		if(expectedJavaAttribute==null)
			throw new NullPointerException();
		assertFeature(name, docComment, expectedJavaAttribute);
		//System.out.println("---"+name+" >"+expectedJavaAttribute.getInitializerTokens()+"<");
	}

	/**
	 * TODO: InnerClassAttribute is non-sense, and should not be reported by the parser
	 */
	protected void assertInnerClassAttribute(final String name, final String docComment)
	{
		assertFeature(name, docComment, null);
	}

	protected void assertAttributeCommaSeparated(final String name, final String docComment)
	{
		assertFeature(name, docComment, null);
	}

	protected void assertMethod(final String name, final String docComment, final JavaBehaviour jb)
	{
		if(jb==null)
			throw new NullPointerException();
		assertFeature(name, docComment, jb);
	}

	protected void assertMethodDiscarded(final String name, final String docComment)
	{
		assertFeature(name, docComment, null);
	}


	private static class ParseEvent
	{
		ParseEvent()
		{
			// make constructor non-private
		}
		// just a common super class
	}

	private static class TextEvent extends ParseEvent
	{
		final String text;

		TextEvent(final String text)
		{
			this.text = text;
			//System.out.println("new TextEvent("+text+")");
		}
	}

	private static class PackageEvent extends ParseEvent
	{
		final JavaFile javafile;

		PackageEvent(final JavaFile javafile)
		{
			this.javafile = javafile;
		}
	}

	private static class ImportEvent extends ParseEvent
	{
		final String importText;

		ImportEvent(final String importText)
		{
			this.importText = importText;
		}
	}

	private static class DocCommentEvent extends ParseEvent
	{
		final String docComment;

		DocCommentEvent(final String docComment)
		{
			this.docComment = docComment;
		}
	}

	private static class FileDocCommentEvent extends ParseEvent
	{
		final String docComment;

		FileDocCommentEvent(final String docComment)
		{
			this.docComment = docComment;
		}
	}

	private static abstract class AbstractClassEvent extends ParseEvent
	{
		final JavaClass javaClass;

		AbstractClassEvent(final JavaClass javaClass)
		{
			this.javaClass = javaClass;
		}
	}

	private static final class ClassEvent extends AbstractClassEvent
	{
		ClassEvent(final JavaClass javaClass)
		{
			super(javaClass);
		}
	}

	private static final class ClassEndEvent extends AbstractClassEvent
	{
		ClassEndEvent(final JavaClass javaClass)
		{
			super(javaClass);
		}
	}

	private static class BehaviourHeaderEvent extends ParseEvent
	{
		final JavaBehaviour javaBehaviour;

		BehaviourHeaderEvent(final JavaBehaviour javaBehaviour)
		{
			this.javaBehaviour = javaBehaviour;
		}

		@Override
		public String toString()
		{
			return "BehaviourHeaderEvent("+javaBehaviour+")";
		}
	}

	private static class FieldHeaderEvent extends ParseEvent
	{
		final JavaField javaField;

		FieldHeaderEvent(final JavaField javaField)
		{
			this.javaField = javaField;
		}

		@Override
		public String toString()
		{
			return "FieldHeaderEvent:"+javaField.toString();
		}
	}

	private static class ClassFeatureEvent extends ParseEvent
	{
		final JavaFeature javaFeature;
		final String docComment;

		ClassFeatureEvent(final JavaFeature javaFeature, final String docComment)
		{
			this.javaFeature = javaFeature;
			this.docComment = docComment;
		}

		@Override
		public String toString()
		{
			return "ClassFeatureEvent("+javaFeature+")";
		}
	}

	private class TestParseConsumer implements ParseConsumer
	{
		StringBuilder output;

		TestParseConsumer()
		{
			// make constructor non-private
		}

		public void onPackage(final JavaFile javaFile) throws ParserException
		{
			//System.out.println("PACKAGE"+javaFile.getPackageName()+"--------------"+output.getBuffer());
			addParseEvent(new PackageEvent(javaFile));
		}

		public void onImport(final String importname)
		{
			addParseEvent(new ImportEvent(importname));
		}

		public void onClass(final JavaClass cc)
		{
			addParseEvent(new ClassEvent(cc));
		}

		public void onClassEnd(final JavaClass cc)
		{
			addParseEvent(new ClassEndEvent(cc));
		}

		public void onBehaviourHeader(final JavaBehaviour jb)
		{
			addParseEvent(new BehaviourHeaderEvent(jb));
		}

		public void onFieldHeader(final JavaField ja)
		{
			addParseEvent(new FieldHeaderEvent(ja));
		}

		public void onClassFeature(final JavaFeature cf, final CommentToken doccomment)
		{
			//System.out.println("onClassFeature("+cf.name+" "+doccomment+")");
			addParseEvent(new ClassFeatureEvent(cf, doccomment!=null ? doccomment.comment : null));
		}

		public boolean onDocComment(final CommentToken doccommentToken)
		{
			final String doccomment = doccommentToken.comment;
			addParseEvent(new DocCommentEvent(doccomment));
			return doccomment.indexOf("DO_DISCARD")<0;
		}

		public void onFileDocComment(final String doccomment)
		{
			addParseEvent(new FileDocCommentEvent(doccomment));
		}

		private void addParseEvent(final ParseEvent parseEvent)
		{
			flushOutput();
			parseEvents.add(parseEvent);
		}

		private void flushOutput()
		{
			if(assertText && output.length()>0)
			{
				parseEvents.add(new TextEvent(output.toString()));
				output.setLength(0);
			}
		}
	}

}
