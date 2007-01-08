/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
	final boolean exists;
	final int visibility;
	final String suffix;
	final boolean booleanAsIs;
	final boolean isFinal;
	
	Option(final String optionString, final boolean allowFinal)
	{
		if(optionString==null)
		{
			exists = true;
			visibility = Option.INHERITED;
			suffix = "";
			booleanAsIs = false;
			isFinal = allowFinal;
		}
		else
		{
			if(optionString.indexOf("none")>=0)
			{
				exists = false;
				visibility = -1;
				suffix = null;
			}
			else if(optionString.indexOf("internal")>=0)
			{
				exists = true;
				visibility = PRIVATE;
				suffix = "Internal";
			}
			else if(optionString.indexOf("private")>=0)
			{
				exists = true;
				visibility = PRIVATE;
				suffix = "";
			}
			else if(optionString.indexOf("protected")>=0)
			{
				exists = true;
				visibility = PROTECTED;
				suffix = "";
			}
			else if(optionString.indexOf("package")>=0)
			{
				exists = true;
				visibility = PACKAGE;
				suffix = "";
			}
			else if(optionString.indexOf("public")>=0)
			{
				exists = true;
				visibility = PUBLIC;
				suffix = "";
			}
			else
			{
				exists = true;
				visibility = INHERITED;
				suffix = "";
			}

			booleanAsIs = (optionString.indexOf("boolean-as-is")>=0);
			if(allowFinal)
				this.isFinal = (optionString.indexOf("non-final")<0);
			else
				this.isFinal = false;
		}
	}

	private static final int INHERITED = 0;
	private static final int PRIVATE = 1;
	private static final int PROTECTED = 2;
	private static final int PACKAGE = 3;
	private static final int PUBLIC = 4;
	
	final int getModifier(final int inheritedModifier)
	{
		if(!exists)
			throw new RuntimeException();
		
		final int result;
		switch(visibility)
		{
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
		if(isFinal)
			return result | Modifier.FINAL;
		else
			return result;
	}
	
}
