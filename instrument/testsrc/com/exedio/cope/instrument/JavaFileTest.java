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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;
import org.junit.Test;

public class JavaFileTest
{
	@Test public void fragmentsMustBeSequential()
	{
		final JavaFile javaFile=new JavaFile(new JavaRepository(), new DummyJavaFileObject("x"), "y");
		javaFile.markFragmentAsGenerated(10, 15);
		javaFile.markFragmentAsGenerated(15, 20);
		try
		{
			javaFile.markFragmentAsGenerated(5, 7);
			fail();
		}
		catch (final RuntimeException e)
		{
			assertEquals("fragments must be marked from start to end", e.getMessage());
		}
	}

	@Test public void fragmentsMustHaveValidRange()
	{
		final JavaFile javaFile=new JavaFile(new JavaRepository(), new DummyJavaFileObject("x"), "y");
		try
		{
			javaFile.markFragmentAsGenerated(10, 9);
			fail();
		}
		catch (final RuntimeException e)
		{
			// fine
		}
		try
		{
			javaFile.markFragmentAsGenerated(10, 10);
			fail();
		}
		catch (final RuntimeException e)
		{
			// fine
		}
		try
		{
			javaFile.markFragmentAsGenerated(-1, 10);
			fail();
		}
		catch (final RuntimeException e)
		{
			// fine
		}
	}

	@Test public void sourceWithoutGeneratedFragments()
	{
		final JavaFile javaFile=new JavaFile(new JavaRepository(), new DummyJavaFileObject("x").withDummyBytes(10), "y");
		javaFile.markFragmentAsGenerated(1, 2);
		assertSourcesWithoutGeneratedFragments("023456789", javaFile);

		javaFile.markFragmentAsGenerated(7, 10);
		assertSourcesWithoutGeneratedFragments("023456", javaFile);
	}

	@Test public void generatedFragmentBehindEnd()
	{
		final JavaFile javaFile=new JavaFile(new JavaRepository(), new DummyJavaFileObject("x").withDummyBytes(10), "y");
		javaFile.markFragmentAsGenerated(1, 2);
		javaFile.markFragmentAsGenerated(11, 12);
		try
		{
			javaFile.getSourceWithoutGeneratedFragments();
			fail();
		}
		catch (final RuntimeException e)
		{
			assertEquals("unconsumed GeneratedFragment at end of file", e.getMessage());
		}
	}

	@Test public void generatedFragmentIncludingEnd()
	{
		final JavaFile javaFile=new JavaFile(new JavaRepository(), new DummyJavaFileObject("x").withDummyBytes(10), "y");
		javaFile.markFragmentAsGenerated(9, 12);
		try
		{
			javaFile.getSourceWithoutGeneratedFragments();
			fail();
		}
		catch (final RuntimeException e)
		{
			assertEquals("unconsumed GeneratedFragment at end of file", e.getMessage());
		}
	}

	private static void assertSourcesWithoutGeneratedFragments(final String expected, final JavaFile javaFile)
	{
		assertEquals(expected, new String(javaFile.getSourceWithoutGeneratedFragments(), StandardCharsets.US_ASCII));
	}
}