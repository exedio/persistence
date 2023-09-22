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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables data vault for a {@link DataField}.
 * <p>
 * This annotation is essentially applicable to {@link Feature features} (@Target(FIELD))
 * that are {@link DataField data fields} only.
 * When applying it to other features, there is no effect at all.
 * When applying it to types (@Target(TYPE),
 * it is automatically applied to all features of that type.
 *
 * @see DataField#getVaultInfo()
 */
@Target({FIELD,TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Vault
{
	/**
	 * Specifying a value for the {@code @Vault} annotation allows
	 * to store data into different vault buckets.
	 * <p>
	 * With a few {@code @Vault} annotations in your code
	 * and some more {@code @Vault("other")} annotations
	 * your {@link ConnectProperties connect properties} may look like this:
	 *
	 * <pre>
	 * vault=true
	 * vault.buckets=default other
	 * vault.default.service=com.exedio.cope.vault.VaultFileService
	 * vault.default.service.root=/var/lib/cope-vault
	 * vault.other.service=com.exedio.cope.vault.VaultFileService
	 * vault.other.service.root=/var/lib/other-cope-vault
	 * </pre>
	 * <p>
	 * Must not be empty, and must contain latin letters (A-Z,a-z), digits (0-9),
	 * and dashes (-) only.
	 */
	String value() default DEFAULT;
	String DEFAULT = "default";
}
