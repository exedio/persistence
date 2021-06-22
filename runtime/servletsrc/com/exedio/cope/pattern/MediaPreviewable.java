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

import java.io.IOException;
import java.nio.file.Path;

/**
 * An interface to be implemented by subclasses of {@link MediaFilter}.
 */
@SuppressWarnings({
		"InterfaceMayBeAnnotatedFunctional", // OK: is to be implemented by Features only
		"InterfaceNeverImplemented", "RedundantThrows", "unused"}) // TODO there is no implementation of MediaPreviewable in cope, just in copeim4java
public interface MediaPreviewable
{
	/**
	 * Filters source content without setting this content at the
	 * {@link MediaFilter#getSource() source media}.
	 * Useful for testing filters beforehand.
	 * @param sourceBody the source content to be filtered
	 * @param sourceContentType the content type of {@code sourceBody}
	 * @param target the file the filter result is written to
	 * @return the content type of the filter result
	 * @throws IOException when writing to {@code target} throws such an exception
	 * @throws IllegalArgumentException if {@code sourceContentType} is not supported
	 */
	String preview(Path sourceBody, String sourceContentType, Path target) throws IOException;
}
