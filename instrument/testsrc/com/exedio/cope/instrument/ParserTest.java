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

import static java.lang.System.lineSeparator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;

public abstract class ParserTest
{
	private final String resourceName;
	final boolean assertText;

	protected ParserTest(final String resourceName, final boolean assertText)
	{
		this.resourceName = resourceName;
		this.assertText = assertText;
	}

	abstract void assertParse(JavaFile file);

	@Test public void testIt()
		throws IOException
	{
		final File inputFile = new File(ParserTest.class.getResource(resourceName).getFile());
		final JavaRepository repository = new JavaRepository();
		new Main().runJavac(Arrays.asList(inputFile), repository);
		repository.stage=JavaRepository.Stage.GENERATE;
		final JavaFile file=singleElement(repository.getFiles());
		assertParse(file);
	}

	<T> T singleElement(Iterable<T> iterable)
	{
		final Iterator<T> iter=iterable.iterator();
		if (!iter.hasNext())
		{
			throw new RuntimeException(iterable.toString());
		}
		final T result=iter.next();
		if (iter.hasNext())
		{
			throw new RuntimeException(iterable.toString());
		}
		return result;
	}

	private static String format(final String s)
	{
		return s.replace('\n', '#').replace(' ', '_').replace('\t', '~');
	}

	private static String replaceLineBreaks(final String s)
	{
		if(s==null)
			return null;

		final StringBuilder result = new StringBuilder();
		int pos;
		int lastpos = -1;
		for(pos = s.indexOf('\n'); pos>=0; pos = s.indexOf('\n', pos+1))
		{
			result.append(s.substring(lastpos+1, pos));
			result.append(lineSeparator());
			lastpos = pos;
		}
		result.append(s.substring(lastpos+1));
		return result.toString();
	}
}
