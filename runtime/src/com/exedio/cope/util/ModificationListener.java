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

package com.exedio.cope.util;

import com.exedio.cope.Item;
import com.exedio.cope.Transaction;
import java.util.Collection;

@FunctionalInterface
public interface ModificationListener
{
	/**
	 * @deprecated
	 * ModificationListener are not yet triggered in clustered mode
	 * if transactions are committed on remote nodes.
	 * Use {@link com.exedio.cope.ChangeListener} instead.
	 * @param transaction the committed transaction
	 */
	@Deprecated
	void onModifyingCommit(Collection<Item> modifiedItems, Transaction transaction);
}