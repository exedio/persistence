/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;

import com.sun.tools.javac.api.JavacTool;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import org.junit.Test;

@SuppressWarnings("synthetic-access")
public class ConvertProcessorTest
{
	@Test public void convertDocumentedWithUnixLineSeparator() throws URISyntaxException
	{
		assertDocumented("\n");
	}

	@Test public void convertDocumentedWithWindowsLineSeparator() throws URISyntaxException
	{
		assertDocumented("\r\n");
	}

	@Test public void convertUndocumentedWithUnixLineSeparator() throws URISyntaxException
	{
		assertUndocumented("\n");
	}

	@Test public void convertUndocumentedWithWindowsLineSeparator() throws URISyntaxException
	{
		assertUndocumented("\r\n");
	}

	private static void assertDocumented(final String lineSeparator) throws URISyntaxException
	{
		assertWithLineSeparator(
			lineSeparator,
			lines(
				lineSeparator,
				"class A",
				"{",
				"	/**",
				"	 * text",
				"	 * more text",
				"	 *",
				"	 * @cope.ignore",
				"	 */",
				"	int i;",
				"}"
			),
			lines(
				lineSeparator,
				"class A",
				"{",
				"	/**",
				"	 * text",
				"	 * more text",
				"	 *",
				"	 */",
				"	@WrapperIgnore",
				"	int i;",
				"}"
			)
		);
	}

	private static void assertUndocumented(final String lineSeparator) throws URISyntaxException
	{
		assertWithLineSeparator(
			lineSeparator,
			lines(
				lineSeparator,
				"class A",
				"{",
				"	/** @cope.xyz public */ int j;",
				"}"
			),
			lines(
				lineSeparator,
				"class A",
				"{",
				"	@Wrapper(wrap=\"xyz\", visibility=Visibility.PUBLIC) int j;",
				"}"
			)
		);
	}

	private static void assertWithLineSeparator(final String lineSeparator, final String originalText, final String expectedText) throws URISyntaxException
	{
		final JavaCompiler compiler=JavacTool.create();
		final StringJavaFileObject javaFile=new StringJavaFileObject(
			"A.java",
			originalText,
			expectedText
		);
		final Iterable<? extends JavaFileObject> sources=singleton(javaFile);
		final JavaCompiler.CompilationTask task = compiler.getTask(null, null, null, options(), null, sources);
		final ConvertTagsToAnnotations.ConvertProcessor convertProcessor=new ConvertTagsToAnnotations.ConvertProcessor(lineSeparator);
		task.setProcessors(singleton(convertProcessor));
		task.call();
		javaFile.validateNewContent();
	}

	private static Iterable<String> options()
	{
		return singleton("-proc:only");
	}

	private static String lines(final String lineSeparator, final String... lines)
	{
		final StringBuilder result=new StringBuilder();
		for (final String line: lines)
		{
			result.append(line).append(lineSeparator);
		}
		return result.toString();
	}

	private static class StringJavaFileObject extends SimpleJavaFileObject
	{
		private final String name;
		private final String originalContent;
		private final String expectedNewContent;

		private ByteArrayOutputStream outputStream=null;

		private StringJavaFileObject(final String name, final String originalContent, final String expectedNewContent) throws URISyntaxException
		{
			super(new URI("mem://"+name), Kind.SOURCE);
			this.name=name;
			this.originalContent=originalContent;
			this.expectedNewContent=expectedNewContent;
		}

		@Override
		public String getName()
		{
			return name;
		}

		@Override
		public InputStream openInputStream()
		{
			return new ByteArrayInputStream(originalContent.getBytes(StandardCharsets.US_ASCII));
		}

		@Override
		public CharSequence getCharContent(final boolean ignoreEncodingErrors)
		{
			return originalContent;
		}

		@Override
		public OutputStream openOutputStream()
		{
			if (outputStream!=null) throw new RuntimeException();
			outputStream=new ByteArrayOutputStream();
			return outputStream;
		}

		private void validateNewContent()
		{
			final String actual=new String(outputStream.toByteArray(), StandardCharsets.US_ASCII);
			assertEquals(showWhitespace(expectedNewContent), showWhitespace(actual));
			assertEquals(expectedNewContent, actual);
		}
	}

	private static String showWhitespace(final String s)
	{
		return s
			.replaceAll(" ", ".")
			.replaceAll("\\t", "+")
			.replaceAll("\\n", "\\\\n\n")
			.replaceAll("\\r", "\\\\r");
	}
}
