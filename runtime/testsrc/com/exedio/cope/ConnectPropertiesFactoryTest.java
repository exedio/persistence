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

package com.exedio.cope;

import static com.exedio.cope.ConnectProperties.factory;
import static com.exedio.cope.PrimaryKeyGenerator.memory;
import static com.exedio.cope.PrimaryKeyGenerator.sequence;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.describe;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ConnectProperties.Factory;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Source;
import org.junit.jupiter.api.Test;

public class ConnectPropertiesFactoryTest
{
	@Test void testDefault()
	{
		assertIt(false, 60, 60, false, memory, "while", "protected", "media/", f -> f);
	}

	@Test void testDisableNativeDate()
	{
		assertIt(true, 60, 60, false, memory, "while", "protected", "media/", Factory::disableNativeDate);
	}

	@Test void testLegacyNameLength()
	{
		assertIt(false, 60, 25, false, memory, "while", "protected", "media/", Factory::legacyNameLength);
	}

	@Test void testRedundantUnq()
	{
		assertIt(false, 60, 60, true, memory, "while", "protected", "media/", Factory::redundantUnq);
	}

	@Test void testPrimaryKeyGeneratorSequence()
	{
		assertIt(false, 60, 60, false, sequence, "while", "protected", "media/", Factory::primaryKeyGeneratorSequence);
	}

	@Test void testRevisionTable()
	{
		assertIt(false, 60, 60, false, memory, "revTab", "revPk", "media/", f -> f.revisionTable("revTab", "revPk"));
	}

	@Test void testMediaRootUrl()
	{
		assertIt(false, 60, 60, false, memory, "while", "protected", "/custom/", f -> f.mediaRootUrl("/custom/"));
	}

	@Test void testMediaRootUrlNull()
	{
		final Factory f = FACTORY.mediaRootUrl(null);

		assertFails(
				() -> f.create(SOURCE),
				IllegalPropertiesException.class,
				"property media.rooturl in DESC must be specified as there is no default");
	}

	private static void assertIt(
			final boolean supportDisabledForNativeDate,
			final int trimmerStandard,
			final int trimmerLegacy,
			final boolean redundantUnq,
			final PrimaryKeyGenerator primaryKeyGenerator,
			final String revisionTableName,
			final String revisionPrimaryKeyName,
			final String mediaRootUrl,
			final java.util.function.Function<Factory, Factory> modifier)
	{
		final ConnectProperties p =
				modifier.apply(FACTORY).create(SOURCE);
		assertAll(
				() -> assertEquals(supportDisabledForNativeDate, p.isSupportDisabledForNativeDate(), "supportDisabledForNativeDate"),
				() -> assertEquals(trimmerStandard, p.trimmerStandard.maxLength, "trimmerStandard"),
				() -> assertEquals(trimmerLegacy  , p.trimmerLegacy  .maxLength, "trimmerLegacy"  ),
				() -> assertEquals(redundantUnq, p.redundantUnq, "redundantUnq"),
				() -> assertEquals(primaryKeyGenerator, p.primaryKeyGenerator, "primaryKeyGenerator"),
				() -> assertEquals(revisionTableName, p.revisionTableName, "revisionTableName"),
				() -> assertEquals(revisionPrimaryKeyName, p.revisionPrimaryKeyName, "revisionPrimaryKeyName"),
				() -> assertEquals(mediaRootUrl, p.getMediaRootUrl(), "mediaRootUrl"));
	}

	private static final Factory FACTORY = factory();
	private static final Source SOURCE = describe("DESC", TestSources.minimal());
}
