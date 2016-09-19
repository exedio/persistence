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

import static com.exedio.cope.instrument.CopeType.TAG_ACTIVATION_CONSTRUCTOR;
import static com.exedio.cope.instrument.CopeType.TAG_GENERIC_CONSTRUCTOR;
import static com.exedio.cope.instrument.CopeType.TAG_INDENT;
import static com.exedio.cope.instrument.CopeType.TAG_INITIAL_CONSTRUCTOR;
import static com.exedio.cope.instrument.CopeType.TAG_TYPE;
import static com.exedio.cope.instrument.Tags.getLine;
import static java.lang.Integer.parseInt;

import java.lang.annotation.Annotation;

final class Option
{
	static final String TEXT_NONE = "none";
	static final String TEXT_INTERNAL = "internal";
	static final String TEXT_VISIBILITY_PRIVATE = "private";
	static final String TEXT_VISIBILITY_PROTECTED = "protected";
	static final String TEXT_VISIBILITY_PACKAGE = "package";
	static final String TEXT_VISIBILITY_PUBLIC = "public";
	static final String TEXT_BOOLEAN_AS_IS = "boolean-as-is";
	static final String TEXT_NON_FINAL = "non-final";
	private static final String TEXT_OVERRIDE = "override";

	private static Visibility getVisibility(final String line)
	{
		if(line==null)
		{
			return null;
		}
		else if(line.contains(TEXT_NONE))
		{
			return Visibility.NONE;
		}
		else if(line.contains(TEXT_VISIBILITY_PRIVATE))
		{
			return Visibility.PRIVATE;
		}
		else if(line.contains(TEXT_VISIBILITY_PROTECTED))
		{
			return Visibility.PROTECTED;
		}
		else if(line.contains(TEXT_VISIBILITY_PACKAGE))
		{
			return Visibility.PACKAGE;
		}
		else if(line.contains(TEXT_VISIBILITY_PUBLIC))
		{
			return Visibility.PUBLIC;
		}
		else
		{
			return Visibility.DEFAULT;
		}
	}

	static WrapperType forType(final String docComment)
	{
		final Visibility type                  = getVisibility(getLine(docComment, TAG_TYPE));
		final Visibility constructor           = getVisibility(getLine(docComment, TAG_INITIAL_CONSTRUCTOR));
		final Visibility genericConstructor    = getVisibility(getLine(docComment, TAG_GENERIC_CONSTRUCTOR));
		final Visibility activationConstructor = getVisibility(getLine(docComment, TAG_ACTIVATION_CONSTRUCTOR));
		final String indentLine = getLine(docComment, TAG_INDENT);
		if(
			type==null &&
			constructor==null &&
			genericConstructor==null &&
			activationConstructor==null &&
			indentLine==null)
		{
			return null;
		}

		final int indent = indentLine!=null ? parseInt(indentLine) : 1;
		return new WrapperType()
		{
			@Override public Class<? extends Annotation> annotationType() { return WrapperType.class; }
			@Override public Visibility type() { return nullToDefault(type); }
			@Override public Visibility constructor() { return nullToDefault(constructor); }
			@Override public Visibility genericConstructor() { return nullToDefault(genericConstructor); }
			@Override public Visibility activationConstructor() { return nullToDefault(activationConstructor);}
			@Override public int indent() { return indent; }
			@Override public boolean comments() { return true; }

			private Visibility nullToDefault(final Visibility visibility)
			{
				return visibility==null ? Visibility.DEFAULT : visibility;
			}
		};
	}

	static Wrapper forFeature(final String docComment, final String modifierTag)
	{
		final String line = getLine(docComment, CopeFeature.TAG_PREFIX + modifierTag);
		if(line==null)
			return null;
		else
			return forFeatureLine(modifierTag, line);
	}

	static Wrapper forFeatureLine(final String modifierTag, final String line)
	{
		final Visibility visibility = getVisibility(line);
		final boolean internal = line.contains(TEXT_INTERNAL);
		final boolean booleanAsIs = line.contains(TEXT_BOOLEAN_AS_IS);
		final boolean asFinal = !line.contains(TEXT_NON_FINAL);
		final boolean override = line.contains(TEXT_OVERRIDE);

		return new Wrapper()
		{
			@Override public Class<? extends Annotation> annotationType() { return Wrapper.class; }
			@Override public String wrap() { return modifierTag; }
			@Override public Visibility visibility() { return visibility; }
			@Override public boolean internal() { return internal; }
			@Override public boolean booleanAsIs() { return booleanAsIs; }
			@Override public boolean asFinal() { return asFinal; }
			@Override public boolean override() { return override; }
		};
	}

	private static final WrapperIgnore WRAPPER_IGNORE_INSTANCE = new WrapperIgnore()
	{
		@Override public Class<? extends Annotation> annotationType() { return WrapperIgnore.class; }
	};

	static WrapperIgnore forIgnore(final String docComment)
	{
		if(Tags.has(docComment, CopeFeature.TAG_PREFIX + "ignore"))
			return WRAPPER_IGNORE_INSTANCE;
		else
			return null;
	}

	private static final WrapperInitial WRAPPER_INITIAL_INSTANCE = new WrapperInitial()
	{
		@Override public Class<? extends Annotation> annotationType() { return WrapperInitial.class; }
		@Override public boolean value() { return true; }
	};

	static WrapperInitial forInitial(final String docComment)
	{
		if(Tags.has(docComment, CopeFeature.TAG_INITIAL))
			return WRAPPER_INITIAL_INSTANCE;
		else
			return null;
	}

	private Option()
	{
		// prevent instantiation
	}
}
