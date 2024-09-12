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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class CharReplacements
{
	private final List<Replacement> replacements=new ArrayList<>();

	private int bufferSize=1024;

	/** @return this */
	CharReplacements setBufferSize(final int bufferSize)
	{
		if (bufferSize<1) throw new RuntimeException();
		this.bufferSize=bufferSize;
		return this;
	}

	void addReplacement(final int start, final int end, final String replacement) throws IllegalArgumentException
	{
		if (!replacements.isEmpty())
		{
			final Replacement last=replacements.get(replacements.size()-1);
			if (last.endExclusive>start)
				throw new IllegalArgumentException("replacements must be marked from start to end; ["+last.startInclusive+"-"+last.endExclusive+"] ["+start+"-"+end+"]");
		}
		replacements.add(new Replacement(start, end, replacement));
	}

	String applyReplacements(final String originalSource)
	{
		try (StringReader bais=new StringReader(originalSource))
		{
			return applyReplacements(bais);
		}
		catch (final IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	String applyReplacements(final Reader reader) throws IOException
	{
		final Iterator<Replacement> replacementIter=replacements.iterator();
		try (final StringWriter os = new StringWriter(InstrumentorWriteProcessor.INITIAL_BUFFER_SIZE))
		{
			final char[] buffer = new char[bufferSize];
			int indexInSource = 0;
			while (replacementIter.hasNext())
			{
				final Replacement replacement=replacementIter.next();
				while (indexInSource<replacement.startInclusive)
				{
					final int read=reader.read(buffer, 0, Math.min(buffer.length, replacement.startInclusive-indexInSource));
					if (read==-1) throw new RuntimeException("unexpected EOF");
					indexInSource+=read;
					os.write(buffer, 0, read);
				}
				final int skip=replacement.endExclusive-replacement.startInclusive;
				int reallySkipped=0;
				while (reallySkipped<skip)
				{
					final int skippedNow=reader.read(buffer, 0, Math.min(buffer.length, skip-reallySkipped));
					if (skippedNow==-1) throw new RuntimeException("unexpected EOF while skipping "+replacement);
					reallySkipped+=skippedNow;
				}
				indexInSource+=reallySkipped;
				os.write(replacement.replacementString);
			}
			// write the rest
			int read;
			while ((read=reader.read(buffer, 0, buffer.length))>=0)
			{
				os.write(buffer, 0, read);
			}
			return os.toString();
		}
	}

	int translateToPositionInOutput(final int positionInInput)
	{
		int droppedChars = 0;
		int addedChars = 0;
		for (final Replacement replacement : replacements)
		{
			if (replacement.startInclusive<=positionInInput)
			{
				if (replacement.endExclusive>positionInInput)
				{
					throw new RuntimeException("in replaced part");
				}
				droppedChars += (replacement.endExclusive-replacement.startInclusive);
				addedChars += replacement.replacementString.length();
			}
			else
			{
				break;
			}
		}

		return positionInInput+addedChars-droppedChars;
	}

	private record Replacement(int startInclusive, int endExclusive, String replacementString)
	{
		private Replacement
		{
			if (startInclusive<0 || startInclusive>endExclusive) throw new RuntimeException(startInclusive + "-" + endExclusive);
		}

		@Override
		public String toString()
		{
			return "Replacement[" + startInclusive + "-" + endExclusive + "]";
		}
	}
}
