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

package com.exedio.cope.vault;

import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;

import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Factory;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.vaultmock.VaultMockService;
import org.junit.jupiter.api.Test;

public class VaultReferenceServiceNestedWrongTest
{
	@Test void test()
	{
		final Factory<VaultProperties> factory = VaultProperties.factory();

		final Source source =
				describe("DESC", cascade(
						single("service", VaultReferenceService.class),
						single("service.main", VaultMockService.class),
						single("service.reference", VaultReferenceService.class),
						single("service.reference.main", VaultMockService.class),
						single("service.reference.reference", VaultMockService.class)
				));

		assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property service.reference in DESC must not nest another VaultReferenceService, " +
				"nest into main instead");
	}
}
