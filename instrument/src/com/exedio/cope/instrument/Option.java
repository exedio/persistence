/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.lang.reflect.Modifier;

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
	
	final boolean exists;
	final Visibility visibility;
	final String suffix;
	final boolean booleanAsIs;
	final boolean isFinal;
	
	Option(final String optionString, final boolean allowFinal)
	{
		if(optionString==null)
		{
			exists = true;
			visibility = null;
			suffix = "";
			booleanAsIs = false;
			isFinal = allowFinal;
		}
		else
		{
			if(optionString.indexOf(TEXT_NONE)>=0)
			{
				exists = false;
				visibility = null;
				suffix = null;
			}
			else if(optionString.indexOf(TEXT_INTERNAL)>=0)
			{
				exists = true;
				visibility = Visibility.PRIVATE;
				suffix = "Internal";
			}
			else if(optionString.indexOf(TEXT_VISIBILITY_PRIVATE)>=0)
			{
				exists = true;
				visibility = Visibility.PRIVATE;
				suffix = "";
			}
			else if(optionString.indexOf(TEXT_VISIBILITY_PROTECTED)>=0)
			{
				exists = true;
				visibility = Visibility.PROTECTED;
				suffix = "";
			}
			else if(optionString.indexOf(TEXT_VISIBILITY_PACKAGE)>=0)
			{
				exists = true;
				visibility = Visibility.PACKAGE;
				suffix = "";
			}
			else if(optionString.indexOf(TEXT_VISIBILITY_PUBLIC)>=0)
			{
				exists = true;
				visibility = Visibility.PUBLIC;
				suffix = "";
			}
			else
			{
				exists = true;
				visibility = null;
				suffix = "";
			}

			booleanAsIs = (optionString.indexOf(TEXT_BOOLEAN_AS_IS)>=0);
			if(allowFinal)
				this.isFinal = (optionString.indexOf(TEXT_NON_FINAL)<0);
			else
				this.isFinal = false;
		}
	}

	final int getModifier(final int inheritedModifier)
	{
		if(!exists)
			throw new RuntimeException();
		
		final int visibilityModifier =
			visibility!=null
			? visibility.modifier
			: inheritedModifier & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE);
		
		if(isFinal)
			return visibilityModifier | Modifier.FINAL;
		else
			return visibilityModifier;
	}
}
