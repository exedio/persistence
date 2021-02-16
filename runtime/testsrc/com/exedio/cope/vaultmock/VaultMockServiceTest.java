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

package com.exedio.cope.vaultmock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.vault.VaultService;
import com.exedio.cope.vaulttest.VaultServiceTest;
import java.util.Properties;
import org.junit.jupiter.api.Test;

public class VaultMockServiceTest extends VaultServiceTest
{
	@Override
	protected Class<? extends VaultService> getServiceClass()
	{
		return VaultMockService.class;
	}

	@Override
	protected Properties getServiceProperties()
	{
		final Properties result = new Properties();
		result.setProperty("example", "exampleValue");
		return result;
	}

	@Test void serviceProperties()
	{
		final VaultMockService service = (VaultMockService)getService();
		assertSame(getProperties(), service.vaultProperties);
		assertEquals("exampleValue", service.serviceProperties.example);
		assertEquals("default", service.serviceKey);
		assertEquals(true, service.writable);
	}
}
