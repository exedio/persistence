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

package com.exedio.cope.instrument;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

final class Params
{
	boolean verify = false;
	Charset charset = StandardCharsets.US_ASCII;
	boolean longJavadoc = true;
	boolean annotateGenerated = true;
	boolean finalArgs = true;
	boolean suppressUnusedWarningOnPrivateActivationConstructor = false;
	boolean serialVersionUID = true;
	boolean genericSetValueArray = true;
	boolean directSetValueMap = false;
	String hidingWarningSuppressor = null;
	boolean parenthesesOnEmptyMemberAnnotations = true;
	boolean verbose = false;
	File timestampFile = null;
	boolean nullabilityAnnotations = true;
}
