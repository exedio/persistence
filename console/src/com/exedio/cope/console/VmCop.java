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

package com.exedio.cope.console;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;


final class VmCop extends ConsoleCop
{
	private static final String DETAILED = "dt";
	private static final String ALL_PACKAGES = "ap";
	
	final boolean detailed;
	final boolean allPackages;

	VmCop(final Args args, final boolean detailed, final boolean allPackages)
	{
		super(TAB_VM, "vm", args);
		this.detailed = detailed;
		this.allPackages = allPackages;
		
		addParameter(DETAILED, detailed);
		addParameter(ALL_PACKAGES, allPackages);
	}
	
	static final VmCop getVmCop(final Args args, final HttpServletRequest request)
	{
		return new VmCop(args, getBooleanParameter(request, DETAILED), getBooleanParameter(request, ALL_PACKAGES));
	}

	@Override
	protected VmCop newArgs(final Args args)
	{
		return new VmCop(args, detailed, allPackages);
	}
	
	VmCop toToggleDetailed()
	{
		return new VmCop(args, !detailed, allPackages);
	}
	
	VmCop toToggleAllPackages()
	{
		return new VmCop(args, detailed, !allPackages);
	}
	
	private static final Comparator<Package> COMPARATOR = new Comparator<Package>()
	{
		public int compare(final Package p1, final Package p2)
		{
			return p1.getName().compareTo(p2.getName());
		}
	};
	
	@Override
	final void writeBody(
			final PrintStream out,
			final Model model,
			final HttpServletRequest request,
			final History history)
	{
		final HashMap<String, TreeSet<Package>> jarMap = new HashMap<String, TreeSet<Package>>();
		
		for(final Package pack : Package.getPackages())
		{
			if(!allPackages && !pack.getName().startsWith("com.exedio."))
				continue;
			
			if(pack.getSpecificationTitle()==null &&
				pack.getSpecificationVersion()==null &&
				pack.getSpecificationVendor()==null &&
				pack.getImplementationTitle()==null &&
				pack.getImplementationVersion()==null &&
				pack.getImplementationVendor()==null)
				continue;
				
			final String key =
				pack.getSpecificationTitle() + '|' +
				pack.getSpecificationVersion() + '|' +
				pack.getSpecificationVendor() + '|' +
				pack.getImplementationTitle() + '|' +
				pack.getImplementationVersion() + '|' +
				pack.getImplementationVendor();
			
			TreeSet<Package> jar = jarMap.get(key);
			
			if(jar==null)
			{
				jar = new TreeSet<Package>(COMPARATOR);
				jarMap.put(key, jar);
			}
			jar.add(pack);
		}
		
		final ArrayList<TreeSet<Package>> jars = new ArrayList<TreeSet<Package>>();
		jars.addAll(jarMap.values());
		
		Vm_Jspm.writeBody(out, this, jars);
	}
}
