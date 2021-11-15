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

import java.lang.annotation.Annotation;

final class AnnotationHelper
{
	private static final WrapperType OPTION_DEFAULT = new WrapperType()
	{
		@Override public Class<? extends Annotation> annotationType() { throw new RuntimeException(); }
		@Override public Visibility wildcardClass() { return Visibility.DEFAULT; }
		@Override public Visibility type() { return Visibility.DEFAULT; }
		@Override public Visibility constructor() { return Visibility.DEFAULT; }
		@Override public String[]   constructorSuppressWarnings() { return new String[0]; }
		@Override public Visibility genericConstructor() { return Visibility.DEFAULT; }
		@Override public Visibility activationConstructor() { return Visibility.DEFAULT; }
		@Override public int indent() { return 1; }
		@Override public boolean comments() { return true; }
	};

	private AnnotationHelper()
	{
	}

	static WrapperType getOrDefault(final WrapperType typeOption)
	{
		return typeOption!=null ? typeOption : OPTION_DEFAULT;
	}
}
