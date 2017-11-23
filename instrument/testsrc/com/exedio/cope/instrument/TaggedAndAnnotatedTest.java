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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

public class TaggedAndAnnotatedTest
{
	@Test
	public void taggedAndAnnotatedAreConsistent()
	{
		final String taggedSource=readFile("instrument/testsrc/com/exedio/cope/instrument/testmodel/TaggedItem.java");
		final String annotatedSource=readFile("instrument/testsrc/com/exedio/cope/instrument/testmodel/AnnotatedItem.java");
		final String taggedNormalised=normalise(taggedSource, "TaggedItem", "taggedItem");
		final String annotatedNormalised=normalise(annotatedSource, "AnnotatedItem", "annotatedItem");
		assertEquals(taggedNormalised, annotatedNormalised);
	}

	private static String readFile(final String path)
	{
		try
		{
			final File file=new File(path);
			assertEquals(true, file.isFile(), file.getAbsolutePath());
			final byte[] bytes=Files.readAllBytes(file.toPath());
			return new String(bytes, StandardCharsets.US_ASCII);
		}
		catch (final IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static String normalise(final String completeSource, final String className, final String typeInfoString)
	{
		final int startGenerated=completeSource.indexOf("// marker for end of hand-written code");
		assertTrue(startGenerated>=0);
		final String generatedPart=completeSource.substring(startGenerated);
		return generatedPart.replaceAll(className, "[[classname]]").replaceAll(typeInfoString, "[[typeInfo]]");
	}
}
