/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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


class Option
{
	final int visibility;
	final boolean booleanAsIs;
	final boolean allowFinal;
	
	Option(final String optionString, final boolean allowFinal)
	{
		if(optionString==null)
		{
			visibility = Option.INHERITED;
			booleanAsIs = false;
		}
		else
		{
			if(optionString.indexOf("none")>=0)
				visibility = NONE;
			else if(optionString.indexOf("private")>=0)
				visibility = PRIVATE;
			else if(optionString.indexOf("protected")>=0)
				visibility = PROTECTED;
			else if(optionString.indexOf("package")>=0)
				visibility = PACKAGE;
			else if(optionString.indexOf("public")>=0)
				visibility = PUBLIC;
			else
				visibility = INHERITED;

			booleanAsIs = (optionString.indexOf("boolean-as-is")>=0);
		}
		this.allowFinal = allowFinal;
	}

	private static final int NONE = 0;
	private static final int INHERITED = 1;
	private static final int PRIVATE = 2;
	private static final int PROTECTED = 3;
	private static final int PACKAGE = 4;
	private static final int PUBLIC = 5;
	
	final boolean isVisible()
	{
		return visibility!=NONE;
	}
	
	final int getModifier(final int inheritedModifier)
	{
		final int result;
		switch(visibility)
		{
			case Option.NONE:
				throw new RuntimeException();
			case Option.INHERITED:
				result = inheritedModifier & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE);
				break;
			case Option.PRIVATE:
				result = Modifier.PRIVATE;
				break;
			case Option.PROTECTED:
				result = Modifier.PROTECTED;
				break;
			case Option.PACKAGE:
				result = 0;
				break;
			case Option.PUBLIC:
				result = Modifier.PUBLIC;
				break;
			default:
				throw new RuntimeException(String.valueOf(visibility));
		}
		if(allowFinal)
			return result | Modifier.FINAL;
		else
			return result;
	}
	
}
