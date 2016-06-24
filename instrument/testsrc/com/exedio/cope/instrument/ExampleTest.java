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

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Modifier;
import java.util.Collections;

public class ExampleTest extends ParserTest
{

	public ExampleTest()
	{
		super("Example.java", true);
	}

	@Override
	void assertParse(JavaFile file)
	{
		assertEquals("com.exedio.cope.instrument", file.getPackageName());

		final JavaClass javaClass=findClass(file, "Example");
		assertEquals(
			"	Represents an attribute or association partner of a class.\n"
				+ "	Note: type==Model.AMIGOUS means, the attribute cannot be used in OCL due to attribute ambiguities.\n"
				+ "	See OCL spec 5.4.1. for details.\n",
			javaClass.getDocComment()
		);
		assertEquals("Example", javaClass.name);
		assertEquals("com.exedio.cope.instrument.Example", javaClass.getFullName());
		assertEquals(Modifier.ABSTRACT|Modifier.PUBLIC, javaClass.modifier);

		assertEquals(Collections.emptyList(), javaClass.getFields());
	}

	private JavaClass findClass(JavaFile file, String className)
	{
		for (JavaClass javaClass: file.getClasses())
		{
			if (className.equals(javaClass.name))
			{
				return javaClass;
			}
		}
		throw new RuntimeException(file.getClasses().toString());
	}

}
