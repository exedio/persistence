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

package com.exedio.cope.pattern;

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.pattern.PasswordRecoveryItem.TYPE;
import static com.exedio.cope.pattern.PasswordRecoveryItem.password;
import static com.exedio.cope.pattern.PasswordRecoveryItem.passwordRecovery;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.misc.Computed;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class PasswordRecoveryModelTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(PasswordRecoveryModelTest.class, "MODEL");
	}

	@Test void testIt()
	{
		assertEquals(Arrays.asList(new Type<?>[]{
				TYPE,
				passwordRecovery.getTokenType(),
		}), MODEL.getTypes());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				password,
				password.getStorage(),
				passwordRecovery,
			}), TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				password,
				password.getStorage(),
				passwordRecovery,
			}), TYPE.getDeclaredFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				passwordRecovery.getTokenType().getThis(),
				passwordRecovery.getParent(PasswordRecoveryItem.class),
				passwordRecovery.getSecret(),
				passwordRecovery.getExpires(),
				passwordRecovery.getTokens(),
			}), passwordRecovery.getTokenType().getFeatures());

		assertEquals(TYPE, password.getType());
		assertEquals(passwordRecovery.getTokenType(), passwordRecovery.getSecret().getType());
		assertEquals(passwordRecovery.getTokenType(), passwordRecovery.getExpires().getType());
		assertEquals("password", password.getName());
		assertEquals("secret", passwordRecovery.getSecret().getName());
		assertEquals("expires", passwordRecovery.getExpires().getName());

		assertEquals(list(), passwordRecovery.getSourceFeatures());
		assertEquals(null, passwordRecovery.getSecret().getPattern());
		assertEquals(null, passwordRecovery.getExpires().getPattern());

		assertSame(password, HashTest.getPassword(passwordRecovery));

		assertFalse(password                       .isAnnotationPresent(Computed.class));
		assertFalse(passwordRecovery               .isAnnotationPresent(Computed.class));
		assertTrue (passwordRecovery.getTokenType().isAnnotationPresent(Computed.class));

		assertSerializedSame(passwordRecovery, 411);
	}
}
