
package com.exedio.cope.instrument;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.LinkedList;

import junit.framework.TestCase;

/**
 * @author rw7
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InjectorTest extends TestCase
{
	final String resourceName;

	public InjectorTest(String name, final String resourceName)
	{
		super(name);
		this.resourceName = resourceName;
	}

	Reader input = null;
	StringWriter output = null;
	File outputfile;
	private LinkedList injectionEvents;
	TestInjectionConsumer testInjectionConsumer;

	protected void setUp() throws Exception
	{
		super.setUp();

		String inputfile = InjectorTest.class.getResource(resourceName).getFile();
		input = new InputStreamReader(new FileInputStream(inputfile));
		output = new StringWriter();

		injectionEvents = new LinkedList();
		testInjectionConsumer = new TestInjectionConsumer();
		(new Injector(input, output, testInjectionConsumer)).parseFile();
	}

	protected void tearDown() throws Exception
	{
		input.close();
		input = null;
		output.close();
		output = null;
		injectionEvents = null;
		super.tearDown();
	}
	

	protected void assertText(final String text)
	{
		final StringBuffer buf = new StringBuffer();
	
		final InjectionEvent firstEvent = (InjectionEvent)injectionEvents.getFirst();
		if(firstEvent instanceof TextEvent)
		{
			buf.append(((TextEvent)firstEvent).text);
			injectionEvents.removeFirst();
		}
		final StringBuffer outputBuffer = output.getBuffer();
		if(outputBuffer.length()>0)
		{
			buf.append(outputBuffer);
			outputBuffer.setLength(0);
		}
		assertEquals(text, buf.toString());
	}
	
	private InjectionEvent getEvent()
	{
		final StringBuffer outputBuffer = output.getBuffer();
		if(outputBuffer.length()>0)
		{
			injectionEvents.add(new TextEvent(outputBuffer.toString()));
			outputBuffer.setLength(0);
		}

		return (InjectionEvent)injectionEvents.removeFirst();
	}

	protected void assertPackage(final String packageName)
	{
		final InjectionEvent event = getEvent();
		assertEquals(packageName, ((PackageEvent)event).javafile.getPackageName());
	}

	static class InjectionEvent
	{
	}

	static class TextEvent extends InjectionEvent
	{
		final String text;

		TextEvent(final String text)
		{
			this.text = text;
		}
	}
	
	static class PackageEvent extends InjectionEvent
	{
		final JavaFile javafile;

		PackageEvent(final JavaFile javafile)
		{
			this.javafile = javafile;
		}
	}
	
	private void addInjectionEvent(final InjectionEvent injectionEvent)
	{
		injectionEvents.add(injectionEvent);
	}

	public class TestInjectionConsumer implements InjectionConsumer
	{

		public void onPackage(final JavaFile javaFile) throws InjectorParseException
		{
			System.out.println("PACKAGE");
			addInjectionEvent(new PackageEvent(javaFile));
		}

		public void onImport(final String importname)
		{
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
		}

		public void onFileEnd()
		{
		}

	}
}
