
package com.exedio.cope.instrument;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.LinkedList;

import junit.framework.TestCase;

public abstract class InjectorTest extends TestCase
{
	private final String resourceName;

	protected InjectorTest(String name, final String resourceName)
	{
		super(name);
		this.resourceName = resourceName;
	}

	private LinkedList injectionEvents;
	private TestInjectionConsumer testInjectionConsumer;

	public abstract void assertInjection();

	public void testInjection()
		throws IOException, InjectorParseException
	{	
		Reader input = null;
		StringWriter output = null;
		String inputfile = InjectorTest.class.getResource(resourceName).getFile();
		input = new InputStreamReader(new FileInputStream(inputfile));
		output = new StringWriter();

		injectionEvents = new LinkedList();
		testInjectionConsumer = new TestInjectionConsumer(output);
		(new Injector(input, output, testInjectionConsumer)).parseFile();
		input.close();
		input = null;
		output.close();
		output = null;
		injectionEvents = null;
	}
	
	private InjectionEvent fetchEvent()
	{
		return (InjectionEvent)injectionEvents.removeFirst();
	}

	protected void assertText(final String text)
	{
		final InjectionEvent event = fetchEvent();
		assertEquals(text, ((TextEvent)event).text);
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

	protected void assertFileDocComment(final String docComment)
	{
		final InjectionEvent event = fetchEvent();
		assertEquals(docComment, ((FileDocCommentEvent)event).docComment);
	}

	private static class InjectionEvent
	{
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
	
	private static class FileDocCommentEvent extends InjectionEvent
	{
		final String docComment;

		FileDocCommentEvent(final String docComment)
		{
			this.docComment = docComment;
		}
	}
	
	private class TestInjectionConsumer implements InjectionConsumer
	{
		final StringWriter output;
		
		TestInjectionConsumer(final StringWriter output)
		{
			this.output = output;
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
		}

		public void onClassEnd(final JavaClass cc)
			throws java.io.IOException, InjectorParseException
		{
		}

		public void onBehaviourHeader(final JavaBehaviour jb)
			throws java.io.IOException
		{
		}

		public void onAttributeHeader(final JavaAttribute ja)
			throws java.io.IOException
		{
		}

		public void onClassFeature(final JavaFeature cf, final String doccomment)
			throws java.io.IOException, InjectorParseException
		{
		}

		public boolean onDocComment(final String doccomment) throws java.io.IOException
		{
			return false;
		}

		public void onFileDocComment(final String doccomment)
			throws java.io.IOException
		{
			addInjectionEvent(new FileDocCommentEvent(doccomment));
		}

		public void onFileEnd()
		{
		}

		private void addInjectionEvent(final InjectionEvent injectionEvent)
		{
			flushOutput();
			injectionEvents.add(injectionEvent);
		}

		private void flushOutput()
		{
			final StringBuffer outputBuffer = output.getBuffer();
			if(outputBuffer.length()>0)
			{
				injectionEvents.add(new TextEvent(outputBuffer.toString()));
				outputBuffer.setLength(0);
			}
		}
	}
}
