/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.console;

import java.io.PrintStream;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;

final class ClusterCop extends ConsoleCop
{
	static final String PING = "ping";
	
	ClusterCop(final Args args)
	{
		super(TAB_CLUSTER, "cluster", args);
	}

	@Override
	protected ClusterCop newArgs(final Args args)
	{
		return new ClusterCop(args);
	}
	
	private boolean donePing = false;
	
	@Override
	void initialize(final HttpServletRequest request, final Model model)
	{
		super.initialize(request, model);
		if(isPost(request))
		{
			if(request.getParameter(PING)!=null)
			{
				model.pingClusterNetwork();
				donePing = true;
			}
		}
	}
	
	@Override
	final void writeBody(
			final PrintStream out,
			final Model model,
			final HttpServletRequest request,
			final History history)
	{
		Cluster_Jspm.writeBody(this, out, donePing);
	}
}
