/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.misc;

import com.exedio.cope.util.Interrupter;

abstract class InterrupterTaskContext
{
	static final int run(final Interrupter interrupter, final InterrupterTaskContext body)
	{
		final Adapter ctx = new Adapter(interrupter);
		body.run(ctx);
		return ctx.finish();
	}

	abstract void run(ExperimentalTaskContext ctx);

	private static final class Adapter implements ExperimentalTaskContext
	{
		private final Interrupter interrupter;
		private int progress = 0;
		private boolean finished = false;

		Adapter(final Interrupter interrupter)
		{
			this.interrupter = interrupter;
		}

		public boolean requestsStop()
		{
			if(finished)
				throw new IllegalStateException("finished");

			return interrupter!=null && interrupter.isRequested();
		}

		public void notifyProgress()
		{
			if(finished)
				throw new IllegalStateException("finished");

			progress++;
		}

		int finish()
		{
			if(finished)
				throw new IllegalStateException("finished");

			finished = true;
			return progress;
		}
	}
}
