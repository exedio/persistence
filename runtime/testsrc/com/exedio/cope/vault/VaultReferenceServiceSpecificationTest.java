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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.vaultmock.VaultMockService;
import com.exedio.cope.vaulttest.VaultServiceTest;
import java.util.Properties;
import org.junit.jupiter.api.Test;

public class VaultReferenceServiceSpecificationTest extends VaultServiceTest
{
	@Override
	@SuppressWarnings({"unchecked","rawtypes"}) // TODO after VaultFallbackService: drop
	protected Class<? extends VaultService> getServiceClass()
	{
		return (Class)VaultReferenceService.class;
	}

	@Override
	protected Properties getServiceProperties()
	{
		final Properties result = new Properties();
		result.setProperty("main", VaultMockService.class.getName());
		result.setProperty("main.bucketTagAction", "bucketTagAction-main");
		result.setProperty("reference", VaultMockService.class.getName());
		result.setProperty("reference.bucketTagAction", "bucketTagAction-reference");
		return result;
	}

	/**
	 * Sufficiently tested in {@link VaultReferenceBucketTagTest}
	 */
	@Override
	@Test
	protected final void probeBucketTag() throws Exception
	{
		assertEquals(
				"mock:bucketTagAction-main(my-Bucket)",
				getService().probeBucketTag("my-Bucket"));
	}
}
