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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParamsToolProviderTest
{
	@Test void testWithMinor()
	{
		assertJavaVersion(false,  "1.8.2");
		assertJavaVersion(false,  "8.2");
		assertJavaVersion(false,  "9.2");
		assertJavaVersion(false, "10.2");
		assertJavaVersion(true,  "11.2");
		assertJavaVersion(true,  "12.2");
		assertJavaVersion(true,  "13.2");
		assertJavaVersion(true,  "14.2");
		assertJavaVersion(true,  "15.2");
		assertJavaVersion(true,  "16.2");
		assertJavaVersion(true,  "17.2");
		assertJavaVersion(true,  "18.2");
		assertJavaVersion(true,  "19.2");
		assertJavaVersion(false, "20.2");
	}

	@Test void testJustDot()
	{
		assertJavaVersion(false,  "1.8.");
		assertJavaVersion(false,  "8.");
		assertJavaVersion(false,  "9.");
		assertJavaVersion(false, "10.");
		assertJavaVersion(true,  "11.");
		assertJavaVersion(true,  "12.");
		assertJavaVersion(true,  "13.");
		assertJavaVersion(true,  "14.");
		assertJavaVersion(true,  "15.");
		assertJavaVersion(true,  "16.");
		assertJavaVersion(true,  "17.");
		assertJavaVersion(true,  "18.");
		assertJavaVersion(true,  "19.");
		assertJavaVersion(false, "20.");
	}

	@Test void testWithoutMinor()
	{
		assertJavaVersion(false,  "1.8");
		assertJavaVersion(false,  "8");
		assertJavaVersion(false,  "9");
		assertJavaVersion(false, "10");
		assertJavaVersion(true,  "11");
		assertJavaVersion(true,  "12");
		assertJavaVersion(true,  "13");
		assertJavaVersion(true,  "14");
		assertJavaVersion(true,  "15");
		assertJavaVersion(true,  "16");
		assertJavaVersion(true,  "17");
		assertJavaVersion(true,  "18");
		assertJavaVersion(true,  "19");
		assertJavaVersion(false, "20");
	}

	private void assertJavaVersion(final boolean expectedToolProvider, final String actualJavaVersion)
	{
		setJavaVersion(actualJavaVersion);
		assertEquals(expectedToolProvider, new Params().toolProvider, actualJavaVersion);
	}


	@BeforeEach void beforeEach()
	{
		assertNull(javaVersionBackup);
		javaVersionBackup = System.getProperty("java.version");
	}

	@AfterEach void afterEach()
	{
		System.setProperty("java.version", javaVersionBackup);
		javaVersionBackup = null;
	}

	private void setJavaVersion(final String value)
	{
		assertNotNull(value);
		assertNotNull(javaVersionBackup);
		System.setProperty("java.version", value);
	}

	private String javaVersionBackup;
}
