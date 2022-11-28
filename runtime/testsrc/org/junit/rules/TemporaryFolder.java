/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package org.junit.rules;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.tojunit.MainRule;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * This is a replacement of the respective class in JUnit 4.
 * Allows switching to JUnit 5 without extensive changes in the project.
 */
public class TemporaryFolder extends MainRule
{
	private File root;

	@Override
	protected void before()
	{
		try
		{
			root = File.createTempFile("junit-TemporaryFolder", ".tmp");
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
		assertTrue(root.delete());
		assertTrue(root.mkdir());
	}

	public final File newFile() throws IOException
	{
		assertBeforeCalled();

		return File.createTempFile("TemporaryFolder", ".file", root);
	}

	public final File newFolder() throws IOException
	{
		assertBeforeCalled();

		final File result = File.createTempFile("TemporaryFolder", ".dir", root);
		assertTrue(result.delete());
		assertTrue(result.mkdir());
		return result;
	}

	@Override
	protected void after() throws IOException
	{
		Files.walkFileTree(root.toPath(), new SimpleFileVisitor<>()
		{
			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException
			{
				Files.delete(file);
				return super.visitFile(file, attrs);
			}

			@Override
			public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException
			{
				Files.delete(dir);
				return super.postVisitDirectory(dir, exc);
			}
		});
	}
}
