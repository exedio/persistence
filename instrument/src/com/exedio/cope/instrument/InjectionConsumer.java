/*
 * Copyright (C) 2000  Ralf Wiebicke
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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


/**
 * Implementors of this interface get the results of the
 * {@link Injector java parser}.
 * <p>
 * An implementation may write to the ouput stream of the
 * java parser. Therefore, the interface of each method
 * specifies the position of the output stream, when
 * the method is called.
 *
 * @author Ralf Wiebicke
 */
interface InjectionConsumer
{
	/**
	 * Encountered a package statement.
	 * This method is guaranteed to be called at most once.
	 * @see JavaFile#getPackageName()
	 */
	void onPackage(JavaFile javafile) throws InjectorParseException;
	
	/**
	 * Encountered an import statement.
	 * Imports are also saved in JavaFile.imports.
	 * This information may be used for mapping type names to types.
	 * @see JavaFile#findTypeExternally(String)
	 */
	void onImport(String importname);
	
	/**
	 * Encountered a class header.
	 * Is also called for inner classes.
	 */
	void onClass(JavaClass cc) throws InjectorParseException;
	
	/**
	 * Encountered the end of a class.
	 * @param cc
	 * the same object as in the corresponding call to onClass
	 * @see #onClass(JavaClass)
	 */
	void onClassEnd(JavaClass cc) throws InjectorParseException;
	
	/**
	 * Encountered the header of a java method.
	 * Is called additionally to
	 * {@link #onClassFeature(JavaFeature, String)}.
	 *
	 * @param jb
	 * contains all parsed information about the method
	 */
	void onBehaviourHeader(JavaBehaviour jb);
	
	/**
	 * Encountered the header of a java attribute.
	 * Is called additionally to
	 * {@link #onClassFeature(JavaFeature, String)}.
	 *
	 * @param ja
	 * contains all parsed information about the attribute
	 */
	void onAttributeHeader(JavaAttribute ja);
	
	/**
	 * Called for attributes and methods.
	 * Is called additionally to
	 * {@link #onBehaviourHeader(JavaBehaviour)}.
	 *
	 * @param doccomment
	 * the doccomment associated to this feature.
	 * Is null, if there was none.
	 */
	void onClassFeature(JavaFeature cf, String doccomment) throws InjectorParseException;
	
	/**
	 * Encountered a java documentation comment.
	 * Is called for comments on class level only,
	 * i.e. inside a class, but outside of methods and attributes.
	 *
	 * @return
	 * if false is returned, the next class feature is ignored,
	 * and the doccomment itself should not appear in the output.
	 */
	boolean onDocComment(String doccomment);
	
	/**
	 * Encountered a java documentation comment.
	 * Is called for comments on file level only,
	 * i.e. outside of any classes.
	 */
	void onFileDocComment(String doccomment) throws InjectorParseException;
	
}
