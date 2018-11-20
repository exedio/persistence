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
import static com.exedio.cope.pattern.PasswordLimiterItem.TYPE;
import static com.exedio.cope.pattern.PasswordLimiterItem.password;
import static com.exedio.cope.pattern.PasswordLimiterItem.passwordLimited;
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

public class PasswordLimiterModelTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(PasswordLimiterModelTest.class, "MODEL");
	}

	@Test void testIt()
	{
		assertEquals(Arrays.asList(new Type<?>[]{
				TYPE,
				passwordLimited.getRefusalType(),
		}), MODEL.getTypes());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				password,
				password.getStorage(),
				passwordLimited,
			}), TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				password,
				password.getStorage(),
				passwordLimited,
			}), TYPE.getDeclaredFeatures());

		assertEquals(TYPE, password.getType());
		assertEquals(passwordLimited.getRefusalType(), passwordLimited.getDate().getType());
		assertEquals("password", password.getName());
		assertEquals("date", passwordLimited.getDate().getName());

		assertEquals(list(), passwordLimited.getSourceFeatures());
		assertEquals(passwordLimited.getRefusals(), passwordLimited.getDate().getPattern());

		assertSame(password, passwordLimited.getPassword());
		assertEquals(60*1000, passwordLimited.getPeriod());
		assertEquals(2, passwordLimited.getLimit());

		assertFalse(password                        .isAnnotationPresent(Computed.class));
		assertFalse(passwordLimited                 .isAnnotationPresent(Computed.class));
		assertTrue (passwordLimited.getRefusalType().isAnnotationPresent(Computed.class));

		assertSerializedSame(passwordLimited, 408);
	}
}
