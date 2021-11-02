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

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.Provider.Service;
import java.security.Security;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

/**
 * Hash codes must exactly match MessageDigest codes supported by JDK.
 */
public class HashCodeTest
{
	@Test void test()
	{
		final TreeSet<String> codes = new TreeSet<>(asList(
				Dialect.HASH_MD5,
				Dialect.HASH_SHA,
				Dialect.HASH_SHA224,
				Dialect.HASH_SHA256,
				Dialect.HASH_SHA384,
				Dialect.HASH_SHA512));

		stream(Security.getProviders()).
				flatMap(provider -> provider.getServices().stream()).
				filter(service -> "MessageDigest".equals(service.getType())).
				map(Service::getAlgorithm).
				forEach(codes::remove);

		assertEquals(emptySet(), codes);
	}
}
