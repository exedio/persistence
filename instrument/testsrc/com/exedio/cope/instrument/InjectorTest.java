
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
		
		assertInjection();
		injectionEvents = null;
	}
	
	private InjectionEvent fetchEvent()
	{
		return (InjectionEvent)injectionEvents.removeFirst();
	}

	private String format(final String s)
	{
		return s.replace('\n', '#').replace(' ', '_').replace('\t', '~');
	}

	protected void assertText(final String text)
	{
		final InjectionEvent event = fetchEvent();
		final String actualText = ((TextEvent)event).text;
		assertEquals("ZAPP \n>"+format(text)+"<\n>"+format(actualText)+"<\n", text, actualText);
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
		assertEquals(docComment, ((DocCommentEvent)event).docComment);
	}

	protected void assertFileDocComment(final String docComment)
	{
		final InjectionEvent event = fetchEvent();
		assertEquals(docComment, ((FileDocCommentEvent)event).docComment);
	}

	protected JavaClass assertClass(final String className)
	{
		final InjectionEvent event = fetchEvent();
		final JavaClass javaClass = ((ClassEvent)event).javaClass;
		assertEquals(className, javaClass.getName());
		return javaClass;
	}
	
	protected void assertClassEnd(final String className, final JavaClass expectedJavaClass)
	{
		final InjectionEvent event = fetchEvent();
		final JavaClass javaClass = ((ClassEndEvent)event).javaClass;
		assertEquals(className, javaClass.getName());
		assertSame(expectedJavaClass, javaClass);
	}
	
	protected void assertBehaviourHeader(final String name, final String type, final int modifier)
	{
		final InjectionEvent event = fetchEvent();
		final JavaBehaviour javaBehaviour = ((BehaviourHeaderEvent)event).javaBehaviour;
		assertEquals(name, javaBehaviour.getName());
		assertEquals(type, javaBehaviour.getType());
		assertEquals(modifier, javaBehaviour.getModifiers());
	}
	
	protected void assertAttributeHeader(final String name, final String type, final int modifier)
	{
		final InjectionEvent event = fetchEvent();
		final JavaAttribute javaAttribute = ((AttributeHeaderEvent)event).javaAttribute;
		assertEquals(name, javaAttribute.getName());
		assertEquals(type, javaAttribute.getType());
		assertEquals(modifier, javaAttribute.getModifiers());
	}
	
	private void assertFeature(final String name, final String docComment)
	{
		final InjectionEvent event = fetchEvent();
		final JavaFeature javaFeature = ((ClassFeatureEvent)event).javaFeature;
		assertEquals(name, javaFeature.getName());
		assertEquals(docComment, ((ClassFeatureEvent)event).docComment);
	}
	
	protected void assertAttribute(final String name, final String docComment)
	{
		assertFeature(name, docComment);
	}
	
	protected void assertMethod(final String name, final String docComment)
	{
		assertFeature(name, docComment);
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
	}
	
	private static class AttributeHeaderEvent extends InjectionEvent
	{
		final JavaAttribute javaAttribute;

		AttributeHeaderEvent(final JavaAttribute javaAttribute)
		{
			this.javaAttribute = javaAttribute;
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
			addInjectionEvent(new ClassEvent(cc));
		}

		public void onClassEnd(final JavaClass cc)
			throws java.io.IOException, InjectorParseException
		{
			addInjectionEvent(new ClassEndEvent(cc));
		}

		public void onBehaviourHeader(final JavaBehaviour jb)
			throws java.io.IOException
		{
			addInjectionEvent(new BehaviourHeaderEvent(jb));
		}

		public void onAttributeHeader(final JavaAttribute ja)
			throws java.io.IOException
		{
			addInjectionEvent(new AttributeHeaderEvent(ja));
		}

		public void onClassFeature(final JavaFeature cf, final String doccomment)
			throws java.io.IOException, InjectorParseException
		{
			addInjectionEvent(new ClassFeatureEvent(cf, doccomment));
		}

		public boolean onDocComment(final String doccomment) throws java.io.IOException
		{
			addInjectionEvent(new DocCommentEvent(doccomment));
			return true;
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
