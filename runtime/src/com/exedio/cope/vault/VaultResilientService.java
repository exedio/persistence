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

/**
 * Extends the contract of its super interface {@link VaultService}
 * in a number of ways:
 * <ul>
 * <li>All methods may be called for the empty hash as well.</li>
 * <li>All methods guarantee to fail with a {@link NullPointerException},
 *     if any {@code @Nonnull} parameter is null.</li>
 * <li>All methods with a parameter {@code String hash} guarantee to fail with an {@link IllegalArgumentException},
 *     if that parameter is not a lower case hex string of exactly the length mandated by
 *     {@link VaultProperties#getAlgorithmLength()}.</li>
 * <li>Method {@link #probeGenuineServiceKey(String)} guarantees to fail with an {@link IllegalArgumentException},
 *     if parameter {@code serviceKey} is illegal according to {@link com.exedio.cope.Vault#value()}.</li>
 * <li>All methods (except {@link #close()}) guarantee to fail with an {@link IllegalStateException},
 *     if {@code close()} has been called on the same instance before.</li>
 * </ul>
 */
@SuppressWarnings("MarkerInterface") // OK: extends contract
public interface VaultResilientService extends VaultService
{
}
