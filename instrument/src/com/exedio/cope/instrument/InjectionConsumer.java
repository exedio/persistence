
package com.exedio.cope.instrument;

import java.io.Writer;

/**
 * Implementors of this interface get the results of the
 * {@link Injector java parser}.
 * <p>
 * An implementation may write to the ouput stream of the
 * java parser. Therefore, the interface of each method
 * specifies the position of the output stream, when
 * the method is called.
 */
public interface InjectionConsumer
{
	/**
	 * Encountered a package statement.
	 * This method is guaranteed to be called at most once.
	 * @see JavaFile#getPackageName()
	 */
	public void onPackage(JavaFile javafile) throws InjectorParseException;
	
	/**
	 * Encountered an import statement.
	 * Imports are also saved in JavaFile.imports.
	 * This information may be used for mapping type names to types.
	 * @see JavaFile#findType(String)
	 */
	public void onImport(String importname);
	
	/**
	 * Encountered a class header.
	 * Is also called for inner classes.
	 */
	public void onClass(JavaClass cc);
	
	/**
	 * Encountered the end of a class.
	 * @param cc
	 * the same object as in the corresponding call to onClass
	 * @see #onClass(JavaClass)
	 */
	public void onClassEnd(JavaClass cc, Writer output)
	throws java.io.IOException, InjectorParseException;
	
	/**
	 * Encountered the header of a java method.
	 * Is called additionally to
	 * {@link #onClassFeature(JavaFeature, String)}.
	 *
	 * @param jb
	 * contains all parsed information about the method
	 */
	public void onBehaviourHeader(JavaBehaviour jb)
	throws java.io.IOException;
	
	/**
	 * Encountered the header of a java attribute.
	 * Is called additionally to
	 * {@link #onClassFeature(JavaFeature, String)}.
	 *
	 * @param ja
	 * contains all parsed information about the attribute
	 */
	public void onAttributeHeader(JavaAttribute ja)
	throws java.io.IOException;
	
	/**
	 * Called for attributes and methods.
	 * Is called additionally to
	 * {@link #onBehaviourHeader(JavaBehaviour)}.
	 *
	 * @param doccomment
	 * the doccomment associated to this feature.
	 * Is null, if there was none.
	 */
	public void onClassFeature(JavaFeature cf, String doccomment)
	throws java.io.IOException, InjectorParseException;
	
	/**
	 * Encountered a java documentation comment.
	 * Is called for comments on class level only,
	 * i.e. inside a class, but outside of methods and attributes.
	 *
	 * @return
	 * if false is returned, the next class feature is ignored.
	 */
	public boolean onDocComment(String doccomment, Writer output)
	throws java.io.IOException;
	
	/**
	 * Encountered a java documentation comment.
	 * Is called for comments on file level only,
	 * i.e. outside of any classes.
	 */
	public void onFileDocComment(String doccomment, Writer output)
	throws java.io.IOException;
	
}
