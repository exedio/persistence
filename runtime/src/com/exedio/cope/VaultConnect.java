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

import static com.exedio.cope.util.JobContext.deferOrStopIfRequested;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.util.JobContext;
import com.exedio.cope.vault.VaultResilientService;

final class VaultConnect
{
	final VaultResilientService service;
	final VaultMarkPut markPut;

	VaultConnect(
			final VaultResilientService service,
			final VaultMarkPut markPut)
	{
		this.service = requireNonNull(service);
		this.markPut = requireNonNull(markPut);
	}

	void purgeSchema(final JobContext ctx)
	{
		if(ctx.supportsMessage())
			ctx.setMessage("vault " + service);
		deferOrStopIfRequested(ctx);
		service.purgeSchema(ctx);
	}
}
