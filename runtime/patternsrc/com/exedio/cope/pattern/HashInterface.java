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

import com.exedio.cope.Item;
import com.exedio.cope.Settable;
import java.security.SecureRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused") // OK: Methods are tested on implementation classes, but never used as a member of this interface.
public interface HashInterface extends Settable<String>
{
	boolean check(@Nonnull Item item, @Nullable String actualPlainText);
	boolean isNull(Item item);
	String getHash(Item item);
	void set(Item item, String plainText);

	void blind(String actualPlainText);

	/**
	 * @deprecated
	 * This method is needed to support the recently deprecated {@link PasswordRecovery#redeem(Item, long)} only.
	 * Therefore it is deprecated as well.
	 */
	@Deprecated
	String newRandomPassword(SecureRandom random);
}
