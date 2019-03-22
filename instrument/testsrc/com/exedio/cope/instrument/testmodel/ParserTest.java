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

package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.instrument.Parameter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This file is just for testing whether the parser can read this file without exception.
 */
@SuppressWarnings({"AssignmentReplaceableWithOperatorAssignment", "UnusedAssignment", "unused"}) // OK: just for testing instrumentor
@SuppressFBWarnings({"DLS_DEAD_LOCAL_STORE","UC_USELESS_VOID_METHOD"})
class ParserTest
{
	void methodParamNone()
	{
		// empty
	}

	void methodParamSimple(int tx)
	{
		tx = tx + 1;
	}

	void methodParamAnnotated(@Parameter int tx)
	{
		tx = tx + 1;
	}

	void methodParamAnnotatedParenthesisEmpty(@Parameter int tx)
	{
		tx = tx + 1;
	}

	void methodParamAnnotatedParenthesisNonEmpty(@Parameter("string literal") int tx)
	{
		tx = tx + 1;
	}
}
