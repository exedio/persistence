/*
 * Copyright (C) 2000  Ralf Wiebicke
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

import com.exedio.cope.instrument.Lexer.CharToken;
import com.exedio.cope.instrument.Lexer.CommentToken;
import com.exedio.cope.instrument.Lexer.EndException;
import com.exedio.cope.instrument.Lexer.ParseException;
import com.exedio.cope.instrument.Lexer.StringToken;
import com.exedio.cope.instrument.Lexer.Token;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Implements a modifying java parser.
 * This means, the input stream is continuously written
 * into an output stream, and may be modified before writing.
 *
 * The parser recognizes java meta information only,
 * which is anything outside method bodies and attribute
 * initializers.
 *
 * To use the parser, provide an implementation of the
 * ParseConsumer interface to the constructor.
 * @see ParseConsumer
 *
 * @author Ralf Wiebicke
 */
final class Parser
{
	final Lexer lexer;
	private final ParseConsumer consumer;

	private CommentToken docComment = null;

	final JavaFile javaFile;

	/**
	 * Constructs a new java parser.
	 * @param consumer
	 * an implementation of ParseConsumer,
	 * listening to parsed elements of the input stream.
	 * @see ParseConsumer
	 */
	public Parser(final Lexer lexer,
								final ParseConsumer consumer, final JavaFile javaFile)
	{
		this.lexer = lexer;
		this.consumer = consumer;
		this.javaFile = javaFile;
	}

	/**
	 * Parses a class feature. May be an attribute, a method or a inner
	 * class. May even be a normal class, in this case parent==null.
	 * @param parent the class that contains the class feature
	 * if null, there is no containing class, and
	 * the feature must be a class itself.
	 */
	private JavaFeature[] parseFeature(final JavaClass parent, final StringToken bufs)
		throws IOException, EndException, ParserException
	{
		return parseFeature(parent, bufs.value);
	}

	/**
	 * The same as parseFeature(JavaClass) but the first token has
	 * already been fetched from the input stream.
	 * @param bufs the first token of the class feature.
	 * @see #parseFeature(JavaClass,Lexer.StringToken)
	 */
	private JavaFeature[] parseFeature(final JavaClass parent, String bufs)
		throws IOException, EndException, ParserException
	{
		int modifiers = 0;

		String featuretype;
		while (true)
		{
			//System.out.println("bufs >"+bufs+"<");
			if ("public".equals(bufs))
				modifiers |= Modifier.PUBLIC;
			else if ("protected".equals(bufs))
				modifiers |= Modifier.PROTECTED;
			else if ("private".equals(bufs))
				modifiers |= Modifier.PRIVATE;
			else if ("static".equals(bufs))
				modifiers |= Modifier.STATIC;
			else if ("final".equals(bufs))
				modifiers |= Modifier.FINAL;
			else if ("synchronized".equals(bufs))
				modifiers |= Modifier.SYNCHRONIZED;
			else if ("volatile".equals(bufs))
				modifiers |= Modifier.VOLATILE;
			else if ("transient".equals(bufs))
				modifiers |= Modifier.TRANSIENT;
			else if ("native".equals(bufs))
				modifiers |= Modifier.NATIVE;
			else if ("abstract".equals(bufs))
				modifiers |= Modifier.ABSTRACT;
			else if ("interface".equals(bufs))
			{
				modifiers |= Modifier.INTERFACE;
				final JavaClass[] jcarray = { parseClass(parent, modifiers)};
				return jcarray;
			}
			else if ("class".equals(bufs))
			{
				final JavaClass[] jcarray = { parseClass(parent, modifiers)};
				return jcarray;
			}
			else if ("enum".equals(bufs))
			{
				final String enumName = lexer.readToken().getString("enum name expected");

				while(!lexer.readToken().contains('{'))
				{
					// do nothing
				}

				lexer.parseBody(false, null);
				final JavaClass result = new JavaClass(javaFile, parent, modifiers, true, enumName, null);

				consumer.onClass(result);
				result.setClassEndPosition(lexer.outputLength());
				consumer.onClassEnd(result);

				lexer.discardNextFeature(false);

				if(lexer.collect_when_blocking())
					lexer.write(lexer.getCollector());
				if(lexer.do_block())
					lexer.getCollector();

				return new JavaFeature[]{result};
			}
			else
			{
				if (parent == null)
					throw lexer.newParseException("'class' or 'interface' expected.");
				featuretype = bufs;
				break;
			}

			final Token c = lexer.readToken();
			if(!(c instanceof StringToken))
			{
				if (parent == null)
					throw lexer.newParseException("'class' or 'interface' expected.");
				else
				{
					if(c.contains('{') && modifiers == Modifier.STATIC)
					{
						// this is a static initializer
						if(lexer.collect_when_blocking())
							lexer.write(lexer.getCollector());
						lexer.flushOutbuf();
						lexer.parseBody(false, null);
						lexer.scheduleBlock(true);
						docComment = null;
						return new JavaClass[0];
					}
					else
					{
						throw lexer.newParseException("modifier expected.");
					}
				}
			}
			bufs = ((StringToken)c).value;
		}
		String featurename;

		Token c = lexer.readToken();

		if(!(c instanceof StringToken))
		{
			c.expect('('); // it's a constructor !
			featurename = featuretype;
			featuretype = null;
			if (!parent.name.equals(featurename))
				System.out.println(
					"WARNING: constructor '"
						+ featurename
						+ "' must have the classes name '"
						+ parent.name
						+ '\'');
		}
		else
		{
			featurename = ((StringToken)c).value;
			c = lexer.readToken();
		}

		if(c.contains('(')) // it's a method/constructor
		{
			final JavaBehaviour jb =
				(featuretype == null)
					? (JavaBehaviour)new JavaConstructor(parent,
						modifiers,
						featurename)
					: new JavaMethod(parent, modifiers, featuretype, featurename);
			parseBehaviour(jb);
			final JavaFeature[] jbarray = { jb };
			return jbarray;
		}
		else // it's an attribute
		{
			final JavaField field =
				new JavaField(parent, modifiers, featuretype, featurename);
			return parseField(field, c);
		}
	}

	private void parseBehaviour(final JavaBehaviour jb)
		throws EndException, ParseException
	{
		Token c = lexer.readToken();
		int parameterListParenthesisLevel = 1;
		// parsing parameter list
		while(parameterListParenthesisLevel>0)
		{
			// TODO
			// does not work if parameter has annotation with string literal
			// containing parenthesis
			if(c.contains('('))
				parameterListParenthesisLevel++;
			else if(c.contains(')'))
				parameterListParenthesisLevel--;

			c = lexer.readToken();
		}

		// parsing throws clauses
		if(c.contains("throws"))
		{
			do
			{
				c = lexer.readToken();
				c = lexer.readToken();
			}
			while(c.contains(','));
		}

		if(c instanceof CharToken)
		{
			switch(((CharToken)c).value)
			{
				case '{' :
					if(lexer.collect_when_blocking())
					{
						lexer.write(lexer.getCollector());
						consumer.onBehaviourHeader(jb);
					}
					lexer.parseBody(false, null);
					lexer.flushOutbuf();
					break;
				case ';' :
					if(lexer.collect_when_blocking())
					{
						lexer.write(lexer.getCollector());
						consumer.onBehaviourHeader(jb);
					}
					lexer.flushOutbuf();
					break;
				default :
					throw lexer.newParseException("'{' expected.");
			}
		}
		else
			throw lexer.newParseException("'{' expected.");

		if(lexer.do_block())
			lexer.getCollector();
		else
		{
			//jb.print(System.out);
		}
	}

	private JavaField[] parseField(JavaField field, Token c)
		throws EndException, ParserException
	{
		consumer.onFieldHeader(field);

		final ArrayList<JavaField> commaSeparatedFields = new ArrayList<>();
		commaSeparatedFields.add(field);
		//if(!do_block) ja.print(System.out);

		while (true)
		{
			if(!(c instanceof CharToken))
				throw lexer.newParseException("characters expected, but was '" + c + '\'');

			switch(((CharToken)c).value)
			{
				case ';' :
					if(lexer.collect_when_blocking())
						lexer.write(lexer.getCollector());
					lexer.flushOutbuf();
					if(lexer.do_block())
						lexer.getCollector();
					final JavaField[] jaarray =
						new JavaField[commaSeparatedFields.size()];
					commaSeparatedFields.toArray(jaarray);
					return jaarray;
				case ',' :
					c = lexer.readToken();
					field = new JavaField(field, c.getString("attribute name expected."));
					commaSeparatedFields.add(field);
					//if(!do_block) ja.print(System.out);
					c = lexer.readToken();
					break;
				case '=' :
					if(lexer.collect_when_blocking())
						lexer.write(lexer.getCollector());
					c = lexer.parseBody(true, field);
					lexer.flushOutbuf();
					break;
				default :
					throw lexer.newParseException("';', '=' or ',' expected, but was '" + c + '\'');
			}
		}
	}

	@SuppressFBWarnings("DB_DUPLICATE_SWITCH_CLAUSES") // is a bug in findbugs
	private JavaClass parseClass(final JavaClass parent, final int modifiers)
		throws IOException, EndException, ParserException
	{
		final String classname = lexer.readToken().getString("class name expected.");
		//System.out.println("class ("+Modifier.toString(modifiers)+") >"+classname+"<");

		Token imc;
		char extendsOrImplements = '-';
		String classExtends = null;
		while(!(imc=lexer.readToken()).contains('{'))
		{
			if(imc instanceof StringToken)
			{
				final String s = ((StringToken)imc).value;

				if("extends".equals(s))
					extendsOrImplements = 'e';
				else if("implements".equals(s))
					extendsOrImplements = 'i';
				else
				{
					switch(extendsOrImplements)
					{
						case '-':
							throw lexer.newParseException("expected extends or implements");
						case 'e':
							if(!Modifier.isInterface(modifiers))
							{
								if(classExtends!=null)
									throw lexer.newParseException("more than one type in extends clause");
								classExtends = s;
							}
							break;
						case 'i':
							break;
						default:
							throw new RuntimeException(String.valueOf(extendsOrImplements));
					}
				}

				//System.out.println("---------------"+s+"---"+extendsOrImplements+"---------------"+classExtends+"--------"+classImplements);
			}
		}

		final JavaClass jc = new JavaClass(javaFile, parent, modifiers, false, classname, classExtends);
		//cc.print(System.out);

		consumer.onClass(jc);
		lexer.discardNextFeature(false);

		if(lexer.collect_when_blocking())
			lexer.write(lexer.getCollector());
		if(lexer.do_block())
			lexer.getCollector();

		lexer.scheduleBlock(true);
		ml : while (true)
		{
			final Token token = lexer.readToken();
			if(token instanceof CommentToken)
			{
				final CommentToken comment = (CommentToken)token;
				if(comment.isDocumentation())
				{
					docComment = comment;
					//System.out.println("docComment: "+docComment);
					final boolean onDocCommentResult = consumer.onDocComment(docComment);
					lexer.discardNextFeature(!onDocCommentResult);
					if(onDocCommentResult)
						lexer.write(comment.comment);
					lexer.scheduleBlock(onDocCommentResult);
				}
				else
				{
					//System.out.println("comment: "+comment);
					lexer.write(comment.comment);
					lexer.scheduleBlock(true);
				}
			}
			else if(token instanceof StringToken)
			{
				final JavaFeature[] jfarray = parseFeature(jc, (StringToken)token);
				for(final JavaFeature jf : jfarray)
					consumer.onClassFeature(jf, docComment);
				lexer.discardNextFeature(false);
				docComment = null;
				lexer.scheduleBlock(true);
			}
			else
			{
				switch(((CharToken)token).value)
				{
					case '}' :
						lexer.getCollector();
						break ml;
					case ';' :
						// javac (but not jikes) accepts semicolons on class level,
						// so do we.
						lexer.getCollector();
						break;
					case '{' :
						// this is an object initializer as defined
						// in Java Language Specification D.1.3
						if(lexer.collect_when_blocking())
							lexer.write(lexer.getCollector());
						lexer.flushOutbuf();
						lexer.parseBody(false, null);
						lexer.scheduleBlock(true);
						break;
					case '@':
						parseAnnotation();
						break;
					default :
						throw lexer.newParseException("class member expected.");
				}
			}
		}

		jc.setClassEndPosition(lexer.outputLength());
		consumer.onClassEnd(jc);
		return jc;
	}

	public void parseFile() throws IOException, ParserException
	{
		try
		{
			Token c;
			while (true)
			{
				lexer.scheduleBlock(true);
				try
				{
					c = lexer.readToken();
				}
				catch (final EndException e)
				{
					return;
				}

				if(lexer.collect_when_blocking())
					lexer.write(lexer.getCollector());
				if(lexer.do_block())
					lexer.getCollector();

				if(c instanceof StringToken)
				{
					final String bufs = ((StringToken)c).value;
					if ("package".equals(bufs))
					{
						c = lexer.readToken();
						javaFile.setPackage(c.getString("package name expected."));
						consumer.onPackage(javaFile);
						//System.out.println("package >"+((StringToken)c).value+"<");
						c = lexer.readToken();
						c.expect(';');
					}
					else if ("import".equals(bufs))
					{
						c = lexer.readToken();
						if(!(c instanceof StringToken))
							throw lexer.newParseException("class name expected.");
						if("static".equals(((StringToken)c).value))
						{
							c = lexer.readToken();
							if(!(c instanceof StringToken))
								throw lexer.newParseException("static import expected.");
						}
						else
						{
							final String importstring = ((StringToken)c).value;
							//System.out.println("import >"+importstring+"<");
							javaFile.addImport(importstring);
							consumer.onImport(importstring);
						}
						c = lexer.readToken();
						c.expect(';');
					}
					else
						parseFeature(null, bufs);
					// null says, its a top-level class
				}
				else if(c instanceof CommentToken)
				{
					final CommentToken comment = (CommentToken)c;
					if(comment.isDocumentation())
					{
						docComment = comment;
						//System.out.println ("file level docComment: "+docComment);
						consumer.onFileDocComment(comment.comment);
						lexer.write(comment.comment);
						docComment = null; // Mark docComment as handled...
					}
					else
					{
						//System.out.println("comment: "+comment);
						lexer.write(comment.comment);
					}
				}
				else
				{
					switch(((CharToken)c).value)
					{
						case '@':
							parseAnnotation();
							break;

						default :
							//System.out.println("bufc >" + c + "<");
							break;
					}
				}
			}
		}
		catch (final EndException e)
		{
			throw lexer.newParseException("Unexpected End-of-File.");
		}
	}

	private void parseAnnotation() throws EndException
	{
		lexer.readToken().getString("expected name of annotation");
		//System.out.println("---------name of annotation-------"+buf);

		final Token bracketToken = lexer.readToken();
		if(!bracketToken.contains('('))
			return; // TODO this is a bug, should push back the token

		while(!lexer.readToken().contains(')'))
		{
			// do nothing
		}
	}
}
