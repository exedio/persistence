/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import junit.framework.AssertionFailedError;

public abstract class InjectorTest extends InstrumentorTest
{
	private final String resourceName;
	final boolean assertText;
	private final String lineSeparator;

	protected InjectorTest(final String resourceName, final boolean assertText)
	{
		this.resourceName = resourceName;
		this.assertText = assertText;
		this.lineSeparator = System.getProperty("line.separator");
	}

	LinkedList<InjectionEvent> injectionEvents;
	private TestInjectionConsumer testInjectionConsumer;

	public abstract void assertInjection();

	public void testInjection()
		throws IOException, InjectorParseException
	{
		final File inputFile = new File(InjectorTest.class.getResource(resourceName).getFile());

		injectionEvents = new LinkedList<InjectionEvent>();
		testInjectionConsumer = new TestInjectionConsumer();
		final JavaRepository repository = new JavaRepository();
		final Injector injector = new Injector(inputFile, testInjectionConsumer, repository);
		if(assertText)
			testInjectionConsumer.output = injector.javaFile.buffer;
		injector.parseFile();
		
		assertInjection();
		injectionEvents = null;
	}
	
	private InjectionEvent fetchEvent()
	{
		return injectionEvents.removeFirst();
	}

	private String format(final String s)
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
		
		final InjectionEvent event = fetchEvent();
		if(!(event instanceof TextEvent))
			throw new AssertionFailedError("expected text event >"+text+"<, but was "+event);
		final String actualText = ((TextEvent)event).text;
		assertEqualsText(text, actualText);
	}

	protected void assertPackage(final String packageName)
	{
		final InjectionEvent event = fetchEvent();
		assertEquals(packageName, ((PackageEvent)event).javafile.getPackageName());
	}

	protected void assertImport(final String importText)
	{
		final InjectionEvent event = fetchEvent();
		assertEquals(importText, ((ImportEvent)event).importText);
	}

	protected void assertDocComment(final String docComment)
	{
		final InjectionEvent event = fetchEvent();
		if(!(event instanceof DocCommentEvent))
			throw new AssertionFailedError("expected docComment event >"+docComment+"<, but was "+event);
		assertEquals(replaceLineBreaks(docComment), ((DocCommentEvent)event).docComment);
	}

	protected void assertFileDocComment(final String docComment)
	{
		final InjectionEvent event = fetchEvent();
		assertEquals(replaceLineBreaks(docComment), ((FileDocCommentEvent)event).docComment);
	}

	protected JavaClass assertClass(final String className, final String classExtends, final String[] classImplements)
	{
		return assertClass(className, classExtends, classImplements, null);
	}
	
	protected JavaClass assertClass(final String className, final String classExtends, final String[] classImplements, final JavaClass parent)
	{
		final InjectionEvent event = fetchEvent();
		if(!(event instanceof ClassEvent))
			throw new RuntimeException(event.toString());
		final JavaClass javaClass = ((ClassEvent)event).javaClass;
		assertEquals(className, javaClass.name);
		assertEquals(classExtends, javaClass.classExtends);
		assertEquals(classImplements==null ? Collections.EMPTY_LIST : Arrays.asList(classImplements), javaClass.classImplements);
		assertSame(parent, javaClass.parent);
		return javaClass;
	}
	
	protected void assertClassEnd(final JavaClass expectedJavaClass)
	{
		final InjectionEvent event = fetchEvent();
		final JavaClass javaClass = ((ClassEndEvent)event).javaClass;
		assertSame(expectedJavaClass, javaClass);
	}
	
	protected JavaBehaviour assertBehaviourHeader(final String name, final String type, final int modifier)
	{
		final InjectionEvent event = fetchEvent();
		if(!(event instanceof BehaviourHeaderEvent))
			throw new AssertionFailedError("expected BehaviourHeader event >"+name+"<, but was "+event);
		final JavaBehaviour javaBehaviour = ((BehaviourHeaderEvent)event).javaBehaviour;
		assertEquals(name, javaBehaviour.name);
		assertEquals(type, javaBehaviour.type);
		assertEquals(modifier, javaBehaviour.modifier);
		return javaBehaviour;
	}
	
	protected JavaAttribute assertAttributeHeader(final String name, final String type, final int modifier)
	{
		final InjectionEvent event = fetchEvent();
		final JavaAttribute javaAttribute = ((AttributeHeaderEvent)event).javaAttribute;
		assertEquals(name, javaAttribute.name);
		assertEquals(type, javaAttribute.type);
		assertEquals(modifier, javaAttribute.modifier);
		return javaAttribute;
	}
	
	private void assertFeature(final String name, final String docComment, final JavaFeature expectedJavaFeature)
	{
		final InjectionEvent event = fetchEvent();
		final JavaFeature javaFeature = ((ClassFeatureEvent)event).javaFeature;
		assertEquals(name, javaFeature.name);
		assertEquals(replaceLineBreaks(docComment), ((ClassFeatureEvent)event).docComment);
		if(expectedJavaFeature!=null)
			assertSame(expectedJavaFeature, javaFeature);
	}
	
	protected void assertAttribute(final String name, final String docComment, final JavaAttribute expectedJavaAttribute)
	{
		if(expectedJavaAttribute==null)
			throw new NullPointerException();
		assertFeature(name, docComment, expectedJavaAttribute);
		//System.out.println("---"+name+" >"+expectedJavaAttribute.getInitializerTokens()+"<");
	}
	
	/**
	 * TODO: InnerClassAttribute is non-sense, and should not be reported by the injector
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


	private static class InjectionEvent
	{
		InjectionEvent()
		{
			// make constructor non-private
		}
		// just a common super class
	}

	private static class TextEvent extends InjectionEvent
	{
		final String text;

		TextEvent(final String text)
		{
			this.text = text;
			//System.out.println("new TextEvent("+text+")");
		}
	}
	
	private static class PackageEvent extends InjectionEvent
	{
		final JavaFile javafile;

		PackageEvent(final JavaFile javafile)
		{
			this.javafile = javafile;
		}
	}
	
	private static class ImportEvent extends InjectionEvent
	{
		final String importText;

		ImportEvent(final String importText)
		{
			this.importText = importText;
		}
	}
	
	private static class DocCommentEvent extends InjectionEvent
	{
		final String docComment;

		DocCommentEvent(final String docComment)
		{
			this.docComment = docComment;
		}
	}
	
	private static class FileDocCommentEvent extends InjectionEvent
	{
		final String docComment;

		FileDocCommentEvent(final String docComment)
		{
			this.docComment = docComment;
		}
	}
	
	private static abstract class AbstractClassEvent extends InjectionEvent
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
	
	private static class BehaviourHeaderEvent extends InjectionEvent
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
	
	private static class AttributeHeaderEvent extends InjectionEvent
	{
		final JavaAttribute javaAttribute;

		AttributeHeaderEvent(final JavaAttribute javaAttribute)
		{
			this.javaAttribute = javaAttribute;
		}
		
		@Override
		public String toString()
		{
			return "AttributeHeaderEvent:"+javaAttribute.toString();
		}
	}
	
	private static class ClassFeatureEvent extends InjectionEvent
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
	
	private class TestInjectionConsumer implements InjectionConsumer
	{
		StringBuilder output;
		
		TestInjectionConsumer()
		{
			// make constructor non-private
		}
		
		public void onPackage(final JavaFile javaFile) throws InjectorParseException
		{
			//System.out.println("PACKAGE"+javaFile.getPackageName()+"--------------"+output.getBuffer());
			addInjectionEvent(new PackageEvent(javaFile));
		}

		public void onImport(final String importname)
		{
			addInjectionEvent(new ImportEvent(importname));
		}

		public void onClass(final JavaClass cc)
		{
			addInjectionEvent(new ClassEvent(cc));
		}

		public void onClassEnd(final JavaClass cc)
		{
			addInjectionEvent(new ClassEndEvent(cc));
		}

		public void onBehaviourHeader(final JavaBehaviour jb)
		{
			addInjectionEvent(new BehaviourHeaderEvent(jb));
		}

		public void onAttributeHeader(final JavaAttribute ja)
		{
			addInjectionEvent(new AttributeHeaderEvent(ja));
		}

		public void onClassFeature(final JavaFeature cf, final String doccomment)
		{
			//System.out.println("onClassFeature("+cf.name+" "+doccomment+")");
			addInjectionEvent(new ClassFeatureEvent(cf, doccomment));
		}

		public boolean onDocComment(final String doccomment)
		{
			addInjectionEvent(new DocCommentEvent(doccomment));
			return doccomment.indexOf("DO_DISCARD")<0;
		}

		public void onFileDocComment(final String doccomment)
		{
			addInjectionEvent(new FileDocCommentEvent(doccomment));
		}

		private void addInjectionEvent(final InjectionEvent injectionEvent)
		{
			flushOutput();
			injectionEvents.add(injectionEvent);
		}

		private void flushOutput()
		{
			if(assertText && output.length()>0)
			{
				injectionEvents.add(new TextEvent(output.toString()));
				output.setLength(0);
			}
		}
	}

}
