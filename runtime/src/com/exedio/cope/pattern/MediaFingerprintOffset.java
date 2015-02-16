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

package com.exedio.cope.pattern;

import static com.exedio.cope.misc.Check.requireNonNegative;

public final class MediaFingerprintOffset
{
	private final int mediaOffFingerInit;
	private int mediaOffsetFingerprint;

	public MediaFingerprintOffset(final int mediaOffsetExpires)
	{
		this.mediaOffFingerInit = requireNonNegative(mediaOffsetExpires, "mediaOffsetExpires");
		this.mediaOffsetFingerprint = mediaOffFingerInit;
	}

	public int getInitially()
	{
		return mediaOffFingerInit;
	}

	public int get()
	{
		return mediaOffsetFingerprint;
	}

	public void set(final int offset)
	{
		mediaOffsetFingerprint = requireNonNegative(offset, "offset");
	}
}
