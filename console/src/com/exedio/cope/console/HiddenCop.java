/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;

final class HiddenCop extends ConsoleCop
{

	HiddenCop()
	{
		super("hidden");
		addParameter(TAB, TAB_HIDDEN);
	}
	
	@Override
	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request) throws IOException
	{
		Properties_Jspm.writeHidden(out, model.getHiddenFeatures());
	}
}
