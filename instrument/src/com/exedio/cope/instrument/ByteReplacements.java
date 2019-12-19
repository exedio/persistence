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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class ByteReplacements
{
	private final List<Replacement> replacements=new ArrayList<>();

	private int bufferSize=1024;

	/** @return this */
	ByteReplacements setBufferSize(final int bufferSize)
	{
		if (bufferSize<1) throw new RuntimeException();
		this.bufferSize=bufferSize;
		return this;
	}

	void addReplacement(final int start, final int end, final String replacementAscii)
	{
		addReplacement(start, end, replacementAscii.getBytes(StandardCharsets.US_ASCII));
	}

	void addReplacement(final int start, final int end, final byte[] replacement) throws IllegalArgumentException
	{
		if (!replacements.isEmpty())
		{
			final Replacement last=replacements.get(replacements.size()-1);
			if (last.endExclusive>start)
				throw new IllegalArgumentException("replacements must be marked from start to end; ["+last.startInclusive+"-"+last.endExclusive+"] ["+start+"-"+end+"]");
		}
		replacements.add(new Replacement(start, end, replacement));
	}

	byte[] applyReplacements(final byte[] originalSource)
	{
		try (ByteArrayInputStream bais=new ByteArrayInputStream(originalSource))
		{
			return applyReplacements(bais);
		}
		catch (final IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	byte[] applyReplacements(final InputStream inputStream) throws IOException
	{
		final Iterator<Replacement> replacementIter=replacements.iterator();
		try (final ByteArrayOutputStream os = new ByteArrayOutputStream(InstrumentorWriteProcessor.INITIAL_BUFFER_SIZE))
		{
			final byte[] buffer = new byte[bufferSize];
			int indexInSource = 0;
			while (replacementIter.hasNext())
			{
				final Replacement replacement=replacementIter.next();
				while (indexInSource<replacement.startInclusive)
				{
					final int read=inputStream.read(buffer, 0, Math.min(buffer.length, replacement.startInclusive-indexInSource));
					if (read==-1) throw new RuntimeException("unexpected EOF");
					indexInSource+=read;
					os.write(buffer, 0, read);
				}
				final int skip=replacement.endExclusive-replacement.startInclusive;
				int reallySkipped=0;
				while (reallySkipped<skip)
				{
					final int skippedNow=inputStream.read(buffer, 0, Math.min(buffer.length, skip-reallySkipped));
					if (skippedNow==-1) throw new RuntimeException("unexpected EOF while skipping "+replacement);
					reallySkipped+=skippedNow;
				}
				indexInSource+=reallySkipped;
				os.write(replacement.replacementBytes);
			}
			// write the rest
			int read;
			while ((read=inputStream.read(buffer, 0, buffer.length))>=0)
			{
				os.write(buffer, 0, read);
			}
			return os.toByteArray();
		}
	}

	int translateToPositionInOutput(final int positionInInput)
	{
		int droppedBytes = 0;
		int addedBytes = 0;
		for (final Replacement replacement : replacements)
		{
			if (replacement.startInclusive<=positionInInput)
			{
				if (replacement.endExclusive>positionInInput)
				{
					throw new RuntimeException("in replaced part");
				}
				droppedBytes += (replacement.endExclusive-replacement.startInclusive);
				addedBytes += replacement.replacementBytes.length;
			}
			else
			{
				break;
			}
		}

		return positionInInput+addedBytes-droppedBytes;
	}

	private static class Replacement
	{
		final int startInclusive;
		final int endExclusive;
		final byte[] replacementBytes;

		Replacement(final int startInclusive, final int endExclusive, final byte[] replacementBytes)
		{
			if (startInclusive<0 || startInclusive>endExclusive) throw new RuntimeException("" + startInclusive + '-' + endExclusive);
			this.startInclusive=startInclusive;
			this.endExclusive=endExclusive;
			this.replacementBytes=replacementBytes;
		}

		@Override
		public String toString()
		{
			return "Replacement[" + startInclusive + "-" + endExclusive + "]";
		}
	}
}
