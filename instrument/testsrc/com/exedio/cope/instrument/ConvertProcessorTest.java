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
import org.junit.jupiter.api.Test;

@SuppressWarnings({"synthetic-access", "HardcodedLineSeparator"})
public class ConvertProcessorTest
{
	private String lineSeparator=System.lineSeparator();

	@Test public void convertDocumentedWithUnixLineSeparator() throws URISyntaxException
	{
		lineSeparator="\n";
		assertDocumented();
	}

	@Test public void convertDocumentedWithWindowsLineSeparator() throws URISyntaxException
	{
		lineSeparator="\r\n";
		assertDocumented();
	}

	@Test public void convertUndocumentedWithUnixLineSeparator() throws URISyntaxException
	{
		lineSeparator="\n";
		assertUndocumented();
	}

	@Test public void convertUndocumentedWithWindowsLineSeparator() throws URISyntaxException
	{
		lineSeparator="\r\n";
		assertUndocumented();
	}

	private void assertDocumented() throws URISyntaxException
	{
		assertConversion(
			lines(
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
				"",
				"import com.exedio.cope.instrument.WrapperIgnore;",
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

	@Test public void singleLineComment() throws URISyntaxException
	{
		assertConversion(
			lines(
				"class A",
				"{",
				"	/** @cope.ignore */",
				"	int i;",
				"}"
			),
			lines(
				"",
				"import com.exedio.cope.instrument.WrapperIgnore;",
				"class A",
				"{",
				"	@WrapperIgnore",
				"	int i;",
				"}"
			)
		);
	}

	@Test public void endNoWhiteSpace() throws URISyntaxException
	{
		assertConversion(
			lines(
				"class A",
				"{",
				"	/** @cope.ignore*/",
				"	int i;",
				"}"
			),
			lines(
				"",
				"import com.exedio.cope.instrument.WrapperIgnore;",
				"class A",
				"{",
				"	@WrapperIgnore",
				"	int i;",
				"}"
			)
		);
	}

	@Test public void convertClass() throws URISyntaxException
	{
		assertConversion(
			lines(
				"package x;",
				"",
				"import java.util.Collection;",
				"",
				"/** @cope.constructor public */",
				"class A",
				"{",
				"}"
			),
			lines(
				"package x;",
				"",
				"import static com.exedio.cope.instrument.Visibility.PUBLIC;",
				"",
				"import com.exedio.cope.instrument.WrapperType;",
				"import java.util.Collection;",
				"",
				"@WrapperType(constructor=PUBLIC)",
				"class A",
				"{",
				"}"
			)
		);
	}

	@Test public void convertClassWithEmptyComment() throws URISyntaxException
	{
		assertConversion(
			lines(
				"package x;",
				"/**",
				" */",
				"class A",
				"{",
				"}"
			),
			null // nothing to convert
		);
	}

	@Test public void classWithoutPackage() throws URISyntaxException
	{
		assertConversion(
			lines(
				"/** @cope.type private */ class A {}"
			),
			lines(
				"",
				"import static com.exedio.cope.instrument.Visibility.PRIVATE;",
				"",
				"import com.exedio.cope.instrument.WrapperType;",
				"@WrapperType(type=PRIVATE) class A {}"
			)
		);
	}

	@Test public void convertClassWithSeveralRules() throws URISyntaxException
	{
		assertConversion(
			lines(
				"package x;",
				"",
				"/**",
				" * @cope.type private",
				" * @cope.constructor private",
				" * @cope.generic.constructor public",
				" * @cope.activation.constructor package",
				" * @cope.indent 2",
				" */",
				"class A {}"
			),
			lines(
				"package x;",
				"",
				"import static com.exedio.cope.instrument.Visibility.PACKAGE;",
				"import static com.exedio.cope.instrument.Visibility.PRIVATE;",
				"import static com.exedio.cope.instrument.Visibility.PUBLIC;",
				"",
				"import com.exedio.cope.instrument.WrapperType;",
				"",
				"@WrapperType(activationConstructor=PACKAGE, constructor=PRIVATE, genericConstructor=PUBLIC, indent=2, type=PRIVATE)",
				"class A {}"
			)
		);
	}

	@Test public void multiAssignment() throws URISyntaxException
	{
		assertConversion(
			lines(
				"package x;",
				"",
				"class A {",
				"	/** @cope.initial */",
				"	int i=1, j=2;",
				"}"
			),
			lines(
				"package x;",
				"",
				"import com.exedio.cope.instrument.WrapperInitial;",
				"",
				"class A {",
				"	@WrapperInitial",
				"	int i=1, j=2;",
				"}"
			)
		);
	}

	@Test public void tolerateDocContentEndBeingZero() throws URISyntaxException
	{
		assertConversion(
			lines(
				"/*",
				" * Copyright ...",
				" */",
				"package com.exedio.copedemo.facet;",
				"/**",
				" * Facet type, e.g. Clock rate",
				" *",
				" * @author Dirk Forchel &lt;dirk.forchel@exedio.com&gt;",
				" *",
				" */",
				"abstract class A implements java.io.Serializable",
				"{}"
			),
			null
		);
	}

	private void assertUndocumented() throws URISyntaxException
	{
		assertConversion(
			lines(
				"class A",
				"{",
				"	/** @cope.xyz public */ int j;",
				"}"
			),
			lines(
				"",
				"import static com.exedio.cope.instrument.Visibility.PUBLIC;",
				"",
				"import com.exedio.cope.instrument.Wrapper;",
				"class A",
				"{",
				"	@Wrapper(wrap=\"xyz\", visibility=PUBLIC) int j;",
				"}"
			)
		);
	}

	private void assertConversion(final String originalText, final String expectedText) throws URISyntaxException
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

	private String lines(final String... lines)
	{
		final StringBuilder result=new StringBuilder();
		for (final String line: lines)
		{
			result.append(line).append(lineSeparator);
		}
		return result.toString();
	}

	private static final class StringJavaFileObject extends SimpleJavaFileObject
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
			final String actual=outputStream==null?null:new String(outputStream.toByteArray(), StandardCharsets.US_ASCII);
			assertEquals(showWhitespace(expectedNewContent), showWhitespace(actual));
			assertEquals(expectedNewContent, actual);
		}
	}

	private static String showWhitespace(final String s)
	{
		if (s==null)
		{
			return null;
		}
		else
		{
			return s
				.replaceAll(" ", ".")
				.replaceAll("\\t", "+")
				.replaceAll("\\n", "\\\\n\n")
				.replaceAll("\\r", "\\\\r");
		}
	}
}
