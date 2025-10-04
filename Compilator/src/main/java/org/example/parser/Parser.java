/* A Bison parser, made by GNU Bison 3.8.2.  */

/* Skeleton implementation for Bison LALR(1) parsers in Java

   Copyright (C) 2007-2015, 2018-2021 Free Software Foundation, Inc.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <https://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* DO NOT RELY ON FEATURES THAT ARE NOT DOCUMENTED in the manual,
   especially those whose name start with YY_ or yy_.  They are
   private implementation details that can be changed or removed.  */

package org.example.parser;

/* First part of user prologue.  */
/* "parser.y":8  */

import java.util.*;
import org.example.lexer.Token;
import org.example.parser.*;

/* "parser.java":47  */


import java.text.MessageFormat;

/**
 * A Bison parser, automatically generated from <tt>parser.y</tt>.
 *
 * @author LALR (1) parser skeleton written by Paolo Bonzini.
 */
public class Parser
{
  /** Version number for the Bison executable that generated this parser.  */
  public static final String bisonVersion = "3.8.2";

  /** Name of the skeleton that generated this parser.  */
  public static final String bisonSkeleton = "lalr1.java";



  /**
   * True if verbose error messages are enabled.
   */
  private boolean yyErrorVerbose = true;

  /**
   * Whether verbose error messages are enabled.
   */
  public final boolean getErrorVerbose() { return yyErrorVerbose; }

  /**
   * Set the verbosity of error messages.
   * @param verbose True to request verbose error messages.
   */
  public final void setErrorVerbose(boolean verbose)
  { yyErrorVerbose = verbose; }




  public enum SymbolKind
  {
    S_YYEOF(0),                    /* "end of file"  */
    S_YYerror(1),                  /* error  */
    S_YYUNDEF(2),                  /* "invalid token"  */
    S_TOK_CLASS(3),                /* TOK_CLASS  */
    S_TOK_EXTENDS(4),              /* TOK_EXTENDS  */
    S_TOK_IS(5),                   /* TOK_IS  */
    S_TOK_END(6),                  /* TOK_END  */
    S_TOK_VAR(7),                  /* TOK_VAR  */
    S_TOK_METHOD(8),               /* TOK_METHOD  */
    S_TOK_THIS(9),                 /* TOK_THIS  */
    S_TOK_WHILE(10),               /* TOK_WHILE  */
    S_TOK_LOOP(11),                /* TOK_LOOP  */
    S_TOK_IF(12),                  /* TOK_IF  */
    S_TOK_THEN(13),                /* TOK_THEN  */
    S_TOK_ELSE(14),                /* TOK_ELSE  */
    S_TOK_RETURN(15),              /* TOK_RETURN  */
    S_TOK_PRINT(16),               /* TOK_PRINT  */
    S_TOK_ID(17),                  /* TOK_ID  */
    S_TOK_TYPE_ID(18),             /* TOK_TYPE_ID  */
    S_TOK_INT_LIT(19),             /* TOK_INT_LIT  */
    S_TOK_REAL_LIT(20),            /* TOK_REAL_LIT  */
    S_TOK_BOOL_LIT(21),            /* TOK_BOOL_LIT  */
    S_TOK_ASSIGN(22),              /* TOK_ASSIGN  */
    S_TOK_ARROW(23),               /* TOK_ARROW  */
    S_TOK_DOT(24),                 /* TOK_DOT  */
    S_TOK_COLON(25),               /* TOK_COLON  */
    S_TOK_COMMA(26),               /* TOK_COMMA  */
    S_TOK_LPAR(27),                /* TOK_LPAR  */
    S_TOK_RPAR(28),                /* TOK_RPAR  */
    S_TOK_LBRACK(29),              /* TOK_LBRACK  */
    S_TOK_RBRACK(30),              /* TOK_RBRACK  */
    S_TOK_LBRACE(31),              /* TOK_LBRACE  */
    S_TOK_RBRACE(32),              /* TOK_RBRACE  */
    S_TOK_LT(33),                  /* TOK_LT  */
    S_TOK_RT(34),                  /* TOK_RT  */
    S_TOK_EOF(35),                 /* TOK_EOF  */
    S_YYACCEPT(36),                /* $accept  */
    S_program(37),                 /* program  */
    S_class_list(38),              /* class_list  */
    S_class_declaration(39),       /* class_declaration  */
    S_optional_extends(40),        /* optional_extends  */
    S_member_list(41),             /* member_list  */
    S_member_declaration(42),      /* member_declaration  */
    S_variable_declaration(43),    /* variable_declaration  */
    S_method_declaration(44),      /* method_declaration  */
    S_method_header(45),           /* method_header  */
    S_method_body(46),             /* method_body  */
    S_parameter_list_opt(47),      /* parameter_list_opt  */
    S_parameter_list(48),          /* parameter_list  */
    S_parameter_declaration(49),   /* parameter_declaration  */
    S_optional_return_type(50),    /* optional_return_type  */
    S_constructor_declaration(51), /* constructor_declaration  */
    S_body(52),                    /* body  */
    S_body_element_list(53),       /* body_element_list  */
    S_body_element(54),            /* body_element  */
    S_statement(55),               /* statement  */
    S_assignment(56),              /* assignment  */
    S_lvalue(57),                  /* lvalue  */
    S_while_loop(58),              /* while_loop  */
    S_if_statement(59),            /* if_statement  */
    S_optional_else(60),           /* optional_else  */
    S_return_statement(61),        /* return_statement  */
    S_return_expression_opt(62),   /* return_expression_opt  */
    S_print_statement(63),         /* print_statement  */
    S_expression(64),              /* expression  */
    S_primary(65),                 /* primary  */
    S_constructor_invocation(66),  /* constructor_invocation  */
    S_method_invocation(67),       /* method_invocation  */
    S_argument_list_opt(68),       /* argument_list_opt  */
    S_argument_list(69);           /* argument_list  */


    private final int yycode_;

    SymbolKind (int n) {
      this.yycode_ = n;
    }

    private static final SymbolKind[] values_ = {
      SymbolKind.S_YYEOF,
      SymbolKind.S_YYerror,
      SymbolKind.S_YYUNDEF,
      SymbolKind.S_TOK_CLASS,
      SymbolKind.S_TOK_EXTENDS,
      SymbolKind.S_TOK_IS,
      SymbolKind.S_TOK_END,
      SymbolKind.S_TOK_VAR,
      SymbolKind.S_TOK_METHOD,
      SymbolKind.S_TOK_THIS,
      SymbolKind.S_TOK_WHILE,
      SymbolKind.S_TOK_LOOP,
      SymbolKind.S_TOK_IF,
      SymbolKind.S_TOK_THEN,
      SymbolKind.S_TOK_ELSE,
      SymbolKind.S_TOK_RETURN,
      SymbolKind.S_TOK_PRINT,
      SymbolKind.S_TOK_ID,
      SymbolKind.S_TOK_TYPE_ID,
      SymbolKind.S_TOK_INT_LIT,
      SymbolKind.S_TOK_REAL_LIT,
      SymbolKind.S_TOK_BOOL_LIT,
      SymbolKind.S_TOK_ASSIGN,
      SymbolKind.S_TOK_ARROW,
      SymbolKind.S_TOK_DOT,
      SymbolKind.S_TOK_COLON,
      SymbolKind.S_TOK_COMMA,
      SymbolKind.S_TOK_LPAR,
      SymbolKind.S_TOK_RPAR,
      SymbolKind.S_TOK_LBRACK,
      SymbolKind.S_TOK_RBRACK,
      SymbolKind.S_TOK_LBRACE,
      SymbolKind.S_TOK_RBRACE,
      SymbolKind.S_TOK_LT,
      SymbolKind.S_TOK_RT,
      SymbolKind.S_TOK_EOF,
      SymbolKind.S_YYACCEPT,
      SymbolKind.S_program,
      SymbolKind.S_class_list,
      SymbolKind.S_class_declaration,
      SymbolKind.S_optional_extends,
      SymbolKind.S_member_list,
      SymbolKind.S_member_declaration,
      SymbolKind.S_variable_declaration,
      SymbolKind.S_method_declaration,
      SymbolKind.S_method_header,
      SymbolKind.S_method_body,
      SymbolKind.S_parameter_list_opt,
      SymbolKind.S_parameter_list,
      SymbolKind.S_parameter_declaration,
      SymbolKind.S_optional_return_type,
      SymbolKind.S_constructor_declaration,
      SymbolKind.S_body,
      SymbolKind.S_body_element_list,
      SymbolKind.S_body_element,
      SymbolKind.S_statement,
      SymbolKind.S_assignment,
      SymbolKind.S_lvalue,
      SymbolKind.S_while_loop,
      SymbolKind.S_if_statement,
      SymbolKind.S_optional_else,
      SymbolKind.S_return_statement,
      SymbolKind.S_return_expression_opt,
      SymbolKind.S_print_statement,
      SymbolKind.S_expression,
      SymbolKind.S_primary,
      SymbolKind.S_constructor_invocation,
      SymbolKind.S_method_invocation,
      SymbolKind.S_argument_list_opt,
      SymbolKind.S_argument_list
    };

    static final SymbolKind get(int code) {
      return values_[code];
    }

    public final int getCode() {
      return this.yycode_;
    }

    /* Return YYSTR after stripping away unnecessary quotes and
       backslashes, so that it's suitable for yyerror.  The heuristic is
       that double-quoting is unnecessary unless the string contains an
       apostrophe, a comma, or backslash (other than backslash-backslash).
       YYSTR is taken from yytname.  */
    private static String yytnamerr_(String yystr)
    {
      if (yystr.charAt (0) == '"')
        {
          StringBuffer yyr = new StringBuffer();
          strip_quotes: for (int i = 1; i < yystr.length(); i++)
            switch (yystr.charAt(i))
              {
              case '\'':
              case ',':
                break strip_quotes;

              case '\\':
                if (yystr.charAt(++i) != '\\')
                  break strip_quotes;
                /* Fall through.  */
              default:
                yyr.append(yystr.charAt(i));
                break;

              case '"':
                return yyr.toString();
              }
        }
      return yystr;
    }

    /* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
       First, the terminals, then, starting at \a YYNTOKENS_, nonterminals.  */
    private static final String[] yytname_ = yytname_init();
  private static final String[] yytname_init()
  {
    return new String[]
    {
  "\"end of file\"", "error", "\"invalid token\"", "TOK_CLASS",
  "TOK_EXTENDS", "TOK_IS", "TOK_END", "TOK_VAR", "TOK_METHOD", "TOK_THIS",
  "TOK_WHILE", "TOK_LOOP", "TOK_IF", "TOK_THEN", "TOK_ELSE", "TOK_RETURN",
  "TOK_PRINT", "TOK_ID", "TOK_TYPE_ID", "TOK_INT_LIT", "TOK_REAL_LIT",
  "TOK_BOOL_LIT", "TOK_ASSIGN", "TOK_ARROW", "TOK_DOT", "TOK_COLON",
  "TOK_COMMA", "TOK_LPAR", "TOK_RPAR", "TOK_LBRACK", "TOK_RBRACK",
  "TOK_LBRACE", "TOK_RBRACE", "TOK_LT", "TOK_RT", "TOK_EOF", "$accept",
  "program", "class_list", "class_declaration", "optional_extends",
  "member_list", "member_declaration", "variable_declaration",
  "method_declaration", "method_header", "method_body",
  "parameter_list_opt", "parameter_list", "parameter_declaration",
  "optional_return_type", "constructor_declaration", "body",
  "body_element_list", "body_element", "statement", "assignment", "lvalue",
  "while_loop", "if_statement", "optional_else", "return_statement",
  "return_expression_opt", "print_statement", "expression", "primary",
  "constructor_invocation", "method_invocation", "argument_list_opt",
  "argument_list", null
    };
  }

    /* The user-facing name of this symbol.  */
    public final String getName() {
      return yytnamerr_(yytname_[yycode_]);
    }

  };


  /**
   * Communication interface between the scanner and the Bison-generated
   * parser <tt>Parser</tt>.
   */
  public interface Lexer {
    /* Token kinds.  */
    /** Token "end of file", to be returned by the scanner.  */
    static final int YYEOF = 0;
    /** Token error, to be returned by the scanner.  */
    static final int YYerror = 256;
    /** Token "invalid token", to be returned by the scanner.  */
    static final int YYUNDEF = 257;
    /** Token TOK_CLASS, to be returned by the scanner.  */
    static final int TOK_CLASS = 258;
    /** Token TOK_EXTENDS, to be returned by the scanner.  */
    static final int TOK_EXTENDS = 259;
    /** Token TOK_IS, to be returned by the scanner.  */
    static final int TOK_IS = 260;
    /** Token TOK_END, to be returned by the scanner.  */
    static final int TOK_END = 261;
    /** Token TOK_VAR, to be returned by the scanner.  */
    static final int TOK_VAR = 262;
    /** Token TOK_METHOD, to be returned by the scanner.  */
    static final int TOK_METHOD = 263;
    /** Token TOK_THIS, to be returned by the scanner.  */
    static final int TOK_THIS = 264;
    /** Token TOK_WHILE, to be returned by the scanner.  */
    static final int TOK_WHILE = 265;
    /** Token TOK_LOOP, to be returned by the scanner.  */
    static final int TOK_LOOP = 266;
    /** Token TOK_IF, to be returned by the scanner.  */
    static final int TOK_IF = 267;
    /** Token TOK_THEN, to be returned by the scanner.  */
    static final int TOK_THEN = 268;
    /** Token TOK_ELSE, to be returned by the scanner.  */
    static final int TOK_ELSE = 269;
    /** Token TOK_RETURN, to be returned by the scanner.  */
    static final int TOK_RETURN = 270;
    /** Token TOK_PRINT, to be returned by the scanner.  */
    static final int TOK_PRINT = 271;
    /** Token TOK_ID, to be returned by the scanner.  */
    static final int TOK_ID = 272;
    /** Token TOK_TYPE_ID, to be returned by the scanner.  */
    static final int TOK_TYPE_ID = 273;
    /** Token TOK_INT_LIT, to be returned by the scanner.  */
    static final int TOK_INT_LIT = 274;
    /** Token TOK_REAL_LIT, to be returned by the scanner.  */
    static final int TOK_REAL_LIT = 275;
    /** Token TOK_BOOL_LIT, to be returned by the scanner.  */
    static final int TOK_BOOL_LIT = 276;
    /** Token TOK_ASSIGN, to be returned by the scanner.  */
    static final int TOK_ASSIGN = 277;
    /** Token TOK_ARROW, to be returned by the scanner.  */
    static final int TOK_ARROW = 278;
    /** Token TOK_DOT, to be returned by the scanner.  */
    static final int TOK_DOT = 279;
    /** Token TOK_COLON, to be returned by the scanner.  */
    static final int TOK_COLON = 280;
    /** Token TOK_COMMA, to be returned by the scanner.  */
    static final int TOK_COMMA = 281;
    /** Token TOK_LPAR, to be returned by the scanner.  */
    static final int TOK_LPAR = 282;
    /** Token TOK_RPAR, to be returned by the scanner.  */
    static final int TOK_RPAR = 283;
    /** Token TOK_LBRACK, to be returned by the scanner.  */
    static final int TOK_LBRACK = 284;
    /** Token TOK_RBRACK, to be returned by the scanner.  */
    static final int TOK_RBRACK = 285;
    /** Token TOK_LBRACE, to be returned by the scanner.  */
    static final int TOK_LBRACE = 286;
    /** Token TOK_RBRACE, to be returned by the scanner.  */
    static final int TOK_RBRACE = 287;
    /** Token TOK_LT, to be returned by the scanner.  */
    static final int TOK_LT = 288;
    /** Token TOK_RT, to be returned by the scanner.  */
    static final int TOK_RT = 289;
    /** Token TOK_EOF, to be returned by the scanner.  */
    static final int TOK_EOF = 290;

    /** Deprecated, use YYEOF instead.  */
    public static final int EOF = YYEOF;


    /**
     * Method to retrieve the semantic value of the last scanned token.
     * @return the semantic value of the last scanned token.
     */
    Object getLVal();

    /**
     * Entry point for the scanner.  Returns the token identifier corresponding
     * to the next token and prepares to return the semantic value
     * of the token.
     * @return the token identifier corresponding to the next token.
     */
    int yylex() throws java.io.IOException;

    /**
     * Emit an errorin a user-defined way.
     *
     *
     * @param msg The string for the error message.
     */
     void yyerror(String msg);


  }


  /**
   * The object doing lexical analysis for us.
   */
  private Lexer yylexer;


  /**
   * The root node of the parsed AST.
   */
  private ProgramNode rootNode;



  /**
   * Instantiates the Bison-generated parser.
   * @param yylexer The scanner that will supply tokens to the parser.
   */
  public Parser(Lexer yylexer)
  {

    this.yylexer = yylexer;

  }



  private int yynerrs = 0;

  /**
   * The number of syntax errors so far.
   */
  public final int getNumberOfErrors() { return yynerrs; }

    /**
   * Get the root node of the parsed AST.
   * @return the root ProgramNode, or null if parsing failed
   */
  public final ProgramNode getRootNode() { return rootNode; }

  /**
   * Print an error message via the lexer.
   *
   * @param msg The error message.
   */
  public final void yyerror(String msg) {
      yylexer.yyerror(msg);
  }



  private final class YYStack {
    private int[] stateStack = new int[16];
    private Object[] valueStack = new Object[16];

    public int size = 16;
    public int height = -1;

    public final void push(int state, Object value) {
      height++;
      if (size == height) {
        int[] newStateStack = new int[size * 2];
        System.arraycopy(stateStack, 0, newStateStack, 0, height);
        stateStack = newStateStack;

        Object[] newValueStack = new Object[size * 2];
        System.arraycopy(valueStack, 0, newValueStack, 0, height);
        valueStack = newValueStack;

        size *= 2;
      }

      stateStack[height] = state;
      valueStack[height] = value;
    }

    public final void pop() {
      pop(1);
    }

    public final void pop(int num) {
      // Avoid memory leaks... garbage collection is a white lie!
      if (0 < num) {
        java.util.Arrays.fill(valueStack, height - num + 1, height + 1, null);
      }
      height -= num;
    }

    public final int stateAt(int i) {
      return stateStack[height - i];
    }

    public final Object valueAt(int i) {
      return valueStack[height - i];
    }

    // Print the state stack on the debug stream.
    public void print(java.io.PrintStream out) {
      out.print ("Stack now");

      for (int i = 0; i <= height; i++) {
        out.print(' ');
        out.print(stateStack[i]);
      }
      out.println();
    }
  }

  /**
   * Returned by a Bison action in order to stop the parsing process and
   * return success (<tt>true</tt>).
   */
  public static final int YYACCEPT = 0;

  /**
   * Returned by a Bison action in order to stop the parsing process and
   * return failure (<tt>false</tt>).
   */
  public static final int YYABORT = 1;



  /**
   * Returned by a Bison action in order to start error recovery without
   * printing an error message.
   */
  public static final int YYERROR = 2;

  /**
   * Internal return codes that are not supported for user semantic
   * actions.
   */
  private static final int YYERRLAB = 3;
  private static final int YYNEWSTATE = 4;
  private static final int YYDEFAULT = 5;
  private static final int YYREDUCE = 6;
  private static final int YYERRLAB1 = 7;
  private static final int YYRETURN = 8;


  private int yyerrstatus_ = 0;


  /**
   * Whether error recovery is being done.  In this state, the parser
   * reads token until it reaches a known state, and then restarts normal
   * operation.
   */
  public final boolean recovering ()
  {
    return yyerrstatus_ == 0;
  }

  /** Compute post-reduction state.
   * @param yystate   the current state
   * @param yysym     the nonterminal to push on the stack
   */
  private int yyLRGotoState(int yystate, int yysym) {
    int yyr = yypgoto_[yysym - YYNTOKENS_] + yystate;
    if (0 <= yyr && yyr <= YYLAST_ && yycheck_[yyr] == yystate)
      return yytable_[yyr];
    else
      return yydefgoto_[yysym - YYNTOKENS_];
  }

  private int yyaction(int yyn, YYStack yystack, int yylen)
  {
    /* If YYLEN is nonzero, implement the default value of the action:
       '$$ = $1'.  Otherwise, use the top of the stack.

       Otherwise, the following line sets YYVAL to garbage.
       This behavior is undocumented and Bison
       users should not rely upon it.  */
    Object yyval = (0 < yylen) ? yystack.valueAt(yylen - 1) : yystack.valueAt(0);

    switch (yyn)
      {
          case 2: /* program: TOK_EOF  */
  if (yyn == 2)
    /* "parser.y":38  */
              { yyval = new ProgramNode(null);};
  break;


  case 3: /* program: class_list TOK_EOF  */
  if (yyn == 3)
    /* "parser.y":39  */
                         { yyval = new ProgramNode((List<ClassDeclNode>)yystack.valueAt (1)); };
  break;


  case 4: /* class_list: class_declaration  */
  if (yyn == 4)
    /* "parser.y":43  */
                        { yyval = new ArrayList<ClassDeclNode>(); ((List<ClassDeclNode>)yyval).add((ClassDeclNode)yystack.valueAt (0)); };
  break;


  case 5: /* class_list: class_list class_declaration  */
  if (yyn == 5)
    /* "parser.y":44  */
                                   { ((List<ClassDeclNode>)yystack.valueAt (1)).add((ClassDeclNode)yystack.valueAt (0)); yyval = yystack.valueAt (1); };
  break;


  case 6: /* class_declaration: TOK_CLASS TOK_TYPE_ID optional_extends TOK_IS member_list TOK_END  */
  if (yyn == 6)
    /* "parser.y":49  */
        { yyval = new ClassDeclNode(((Token)yystack.valueAt (4)).getLexeme(), yystack.valueAt (3) == null ? null : (String)yystack.valueAt (3), (List<MemberNode>)yystack.valueAt (1)); };
  break;


  case 7: /* optional_extends: %empty  */
  if (yyn == 7)
    /* "parser.y":53  */
                  { yyval = null; };
  break;


  case 8: /* optional_extends: TOK_EXTENDS TOK_TYPE_ID  */
  if (yyn == 8)
    /* "parser.y":54  */
                              { yyval = ((Token)yystack.valueAt (0)).getLexeme(); };
  break;


  case 9: /* member_list: %empty  */
  if (yyn == 9)
    /* "parser.y":58  */
                  { yyval = new ArrayList<MemberNode>(); };
  break;


  case 10: /* member_list: member_list member_declaration  */
  if (yyn == 10)
    /* "parser.y":59  */
                                     { ((List<MemberNode>)yystack.valueAt (1)).add((MemberNode)yystack.valueAt (0)); yyval = yystack.valueAt (1); };
  break;


  case 11: /* member_declaration: variable_declaration  */
  if (yyn == 11)
    /* "parser.y":63  */
                           { yyval = (MemberNode)yystack.valueAt (0); };
  break;


  case 12: /* member_declaration: method_declaration  */
  if (yyn == 12)
    /* "parser.y":64  */
                         { yyval = (MemberNode)yystack.valueAt (0); };
  break;


  case 13: /* member_declaration: constructor_declaration  */
  if (yyn == 13)
    /* "parser.y":65  */
                              { yyval = (MemberNode)yystack.valueAt (0); };
  break;


  case 14: /* variable_declaration: TOK_VAR TOK_ID TOK_COLON expression  */
  if (yyn == 14)
    /* "parser.y":69  */
                                          { yyval = new VarDeclNode(((Token)yystack.valueAt (2)).getLexeme(), null, (ExpressionNode)yystack.valueAt (0), VarDeclType.COLON); };
  break;


  case 15: /* variable_declaration: TOK_VAR TOK_ID TOK_ASSIGN expression  */
  if (yyn == 15)
    /* "parser.y":70  */
                                           { yyval = new VarDeclNode(((Token)yystack.valueAt (2)).getLexeme(), null, (ExpressionNode)yystack.valueAt (0), VarDeclType.ASSIGN); };
  break;


  case 16: /* variable_declaration: TOK_VAR TOK_ID TOK_IS expression  */
  if (yyn == 16)
    /* "parser.y":71  */
                                       { yyval = new VarDeclNode(((Token)yystack.valueAt (2)).getLexeme(), null, (ExpressionNode)yystack.valueAt (0), VarDeclType.IS); };
  break;


  case 17: /* method_declaration: method_header method_body  */
  if (yyn == 17)
    /* "parser.y":76  */
        { yyval = new MethodDeclNode((MethodHeaderNode)yystack.valueAt (1), (MethodBodyNode)yystack.valueAt (0)); };
  break;


  case 18: /* method_header: TOK_METHOD TOK_ID TOK_LPAR parameter_list_opt TOK_RPAR optional_return_type  */
  if (yyn == 18)
    /* "parser.y":81  */
      { yyval = new MethodHeaderNode(((Token)yystack.valueAt (4)).getLexeme(), (List<ParamDeclNode>)yystack.valueAt (2), yystack.valueAt (0) == null ? null : ((Token)yystack.valueAt (0)).getLexeme()); };
  break;


  case 19: /* method_body: TOK_IS body TOK_END  */
  if (yyn == 19)
    /* "parser.y":85  */
                          { yyval = new MethodBodyNode((BodyNode)yystack.valueAt (1), false); };
  break;


  case 20: /* method_body: TOK_ARROW expression  */
  if (yyn == 20)
    /* "parser.y":86  */
                           { yyval = new MethodBodyNode((ExpressionNode)yystack.valueAt (0), true); };
  break;


  case 21: /* parameter_list_opt: %empty  */
  if (yyn == 21)
    /* "parser.y":90  */
                  { yyval = new ArrayList<ParamDeclNode>(); };
  break;


  case 22: /* parameter_list_opt: parameter_list  */
  if (yyn == 22)
    /* "parser.y":91  */
                     { yyval = yystack.valueAt (0); };
  break;


  case 23: /* parameter_list: parameter_declaration  */
  if (yyn == 23)
    /* "parser.y":96  */
        { yyval = new ArrayList<ParamDeclNode>(); ((List<ParamDeclNode>)yyval).add((ParamDeclNode)yystack.valueAt (0)); };
  break;


  case 24: /* parameter_list: parameter_list TOK_COMMA parameter_declaration  */
  if (yyn == 24)
    /* "parser.y":98  */
        { ((List<ParamDeclNode>)yystack.valueAt (2)).add((ParamDeclNode)yystack.valueAt (0)); yyval = yystack.valueAt (2); };
  break;


  case 25: /* parameter_declaration: TOK_ID TOK_COLON TOK_TYPE_ID  */
  if (yyn == 25)
    /* "parser.y":102  */
                                   { yyval = new ParamDeclNode(((Token)yystack.valueAt (2)).getLexeme(), ((Token)yystack.valueAt (0)).getLexeme()); };
  break;


  case 26: /* optional_return_type: %empty  */
  if (yyn == 26)
    /* "parser.y":106  */
                  { yyval = null; };
  break;


  case 27: /* optional_return_type: TOK_COLON TOK_TYPE_ID  */
  if (yyn == 27)
    /* "parser.y":107  */
                            { yyval = yystack.valueAt (0); };
  break;


  case 28: /* constructor_declaration: TOK_THIS TOK_LPAR parameter_list_opt TOK_RPAR TOK_IS body TOK_END  */
  if (yyn == 28)
    /* "parser.y":112  */
        { yyval = new ConstructorDeclNode((List<ParamDeclNode>)yystack.valueAt (4), (BodyNode)yystack.valueAt (1)); };
  break;


  case 29: /* body: %empty  */
  if (yyn == 29)
    /* "parser.y":116  */
                  { yyval = new BodyNode(null); };
  break;


  case 30: /* body: body_element_list  */
  if (yyn == 30)
    /* "parser.y":117  */
                        { yyval = new BodyNode((List<BodyElementNode>)yystack.valueAt (0)); };
  break;


  case 31: /* body_element_list: body_element  */
  if (yyn == 31)
    /* "parser.y":122  */
        { yyval = new ArrayList<BodyElementNode>(); ((List<BodyElementNode>)yyval).add((BodyElementNode)yystack.valueAt (0)); };
  break;


  case 32: /* body_element_list: body_element_list body_element  */
  if (yyn == 32)
    /* "parser.y":124  */
        { ((List<BodyElementNode>)yystack.valueAt (1)).add((BodyElementNode)yystack.valueAt (0)); yyval = yystack.valueAt (1); };
  break;


  case 33: /* body_element: statement  */
  if (yyn == 33)
    /* "parser.y":128  */
                { yyval = (BodyElementNode)yystack.valueAt (0); };
  break;


  case 34: /* body_element: variable_declaration  */
  if (yyn == 34)
    /* "parser.y":129  */
                           { yyval = (BodyElementNode)yystack.valueAt (0); };
  break;


  case 35: /* statement: assignment  */
  if (yyn == 35)
    /* "parser.y":133  */
                 { yyval = (StatementNode)yystack.valueAt (0); };
  break;


  case 36: /* statement: while_loop  */
  if (yyn == 36)
    /* "parser.y":134  */
                 { yyval = (StatementNode)yystack.valueAt (0); };
  break;


  case 37: /* statement: if_statement  */
  if (yyn == 37)
    /* "parser.y":135  */
                   { yyval = (StatementNode)yystack.valueAt (0); };
  break;


  case 38: /* statement: return_statement  */
  if (yyn == 38)
    /* "parser.y":136  */
                       { yyval = (StatementNode)yystack.valueAt (0); };
  break;


  case 39: /* statement: print_statement  */
  if (yyn == 39)
    /* "parser.y":137  */
                      { yyval = (StatementNode)yystack.valueAt (0); };
  break;


  case 40: /* assignment: lvalue TOK_ASSIGN expression  */
  if (yyn == 40)
    /* "parser.y":142  */
        { yyval = new AssignmentNode((ExpressionNode)yystack.valueAt (2), (ExpressionNode)yystack.valueAt (0)); };
  break;


  case 41: /* lvalue: TOK_ID  */
  if (yyn == 41)
    /* "parser.y":146  */
             { yyval = new IdentifierNode(((Token)yystack.valueAt (0)).getLexeme()); };
  break;


  case 42: /* lvalue: lvalue TOK_DOT TOK_ID  */
  if (yyn == 42)
    /* "parser.y":147  */
                            { yyval = new MemberAccessNode((ExpressionNode)yystack.valueAt (2), new IdentifierNode(((Token)yystack.valueAt (0)).getLexeme())); };
  break;


  case 43: /* while_loop: TOK_WHILE expression TOK_LOOP body TOK_END  */
  if (yyn == 43)
    /* "parser.y":151  */
                                                 { yyval = new WhileLoopNode((ExpressionNode)yystack.valueAt (3), (BodyNode)yystack.valueAt (1)); };
  break;


  case 44: /* if_statement: TOK_IF expression TOK_THEN body optional_else TOK_END  */
  if (yyn == 44)
    /* "parser.y":156  */
        { yyval = new IfStatementNode((ExpressionNode)yystack.valueAt (4), (BodyNode)yystack.valueAt (2), (BodyNode)yystack.valueAt (1)); };
  break;


  case 45: /* optional_else: %empty  */
  if (yyn == 45)
    /* "parser.y":160  */
                  { yyval = null; };
  break;


  case 46: /* optional_else: TOK_ELSE body  */
  if (yyn == 46)
    /* "parser.y":161  */
                    { yyval = (BodyNode)yystack.valueAt (0); };
  break;


  case 47: /* return_statement: TOK_RETURN return_expression_opt  */
  if (yyn == 47)
    /* "parser.y":165  */
                                       { yyval = new ReturnNode((ExpressionNode)yystack.valueAt (0)); };
  break;


  case 48: /* return_expression_opt: %empty  */
  if (yyn == 48)
    /* "parser.y":169  */
                  { yyval = null; };
  break;


  case 49: /* return_expression_opt: expression  */
  if (yyn == 49)
    /* "parser.y":170  */
                 { yyval = yystack.valueAt (0); };
  break;


  case 50: /* print_statement: TOK_PRINT expression  */
  if (yyn == 50)
    /* "parser.y":174  */
                           { yyval = new PrintNode((ExpressionNode)yystack.valueAt (0)); };
  break;


  case 51: /* expression: primary  */
  if (yyn == 51)
    /* "parser.y":178  */
              { yyval = yystack.valueAt (0); };
  break;


  case 52: /* expression: constructor_invocation  */
  if (yyn == 52)
    /* "parser.y":179  */
                             { yyval = yystack.valueAt (0); };
  break;


  case 53: /* expression: method_invocation  */
  if (yyn == 53)
    /* "parser.y":180  */
                        { yyval = yystack.valueAt (0); };
  break;


  case 54: /* expression: expression TOK_DOT method_invocation  */
  if (yyn == 54)
    /* "parser.y":182  */
        { yyval = new MemberAccessNode((ExpressionNode)yystack.valueAt (2), (MethodInvocationNode)yystack.valueAt (0)); };
  break;


  case 55: /* expression: expression TOK_DOT TOK_ID  */
  if (yyn == 55)
    /* "parser.y":183  */
                                { yyval = new MemberAccessNode((ExpressionNode)yystack.valueAt (2), new IdentifierNode(((Token)yystack.valueAt (0)).getLexeme())); };
  break;


  case 56: /* primary: TOK_ID  */
  if (yyn == 56)
    /* "parser.y":187  */
                    { yyval = new IdentifierNode(((Token)yystack.valueAt (0)).getLexeme()); };
  break;


  case 57: /* primary: TOK_INT_LIT  */
  if (yyn == 57)
    /* "parser.y":188  */
                    { yyval = new IntLiteralNode(Integer.parseInt(((Token)yystack.valueAt (0)).getLexeme())); };
  break;


  case 58: /* primary: TOK_REAL_LIT  */
  if (yyn == 58)
    /* "parser.y":189  */
                    { yyval = new RealLiteralNode(Double.parseDouble(((Token)yystack.valueAt (0)).getLexeme())); };
  break;


  case 59: /* primary: TOK_BOOL_LIT  */
  if (yyn == 59)
    /* "parser.y":190  */
                    { yyval = new BoolLiteralNode(Boolean.parseBoolean(((Token)yystack.valueAt (0)).getLexeme())); };
  break;


  case 60: /* primary: TOK_THIS  */
  if (yyn == 60)
    /* "parser.y":191  */
                    { yyval = new ThisNode(); };
  break;


  case 61: /* constructor_invocation: TOK_TYPE_ID TOK_LPAR argument_list_opt TOK_RPAR  */
  if (yyn == 61)
    /* "parser.y":196  */
        { yyval = new ConstructorInvocationNode(((Token)yystack.valueAt (3)).getLexeme(), (List<ExpressionNode>)yystack.valueAt (1)); };
  break;


  case 62: /* method_invocation: TOK_ID TOK_LPAR argument_list_opt TOK_RPAR  */
  if (yyn == 62)
    /* "parser.y":201  */
        { yyval = new MethodInvocationNode(new IdentifierNode(((Token)yystack.valueAt (3)).getLexeme()), (List<ExpressionNode>)yystack.valueAt (1)); };
  break;


  case 63: /* argument_list_opt: %empty  */
  if (yyn == 63)
    /* "parser.y":204  */
                  { yyval = new ArrayList<ExpressionNode>(); };
  break;


  case 64: /* argument_list_opt: argument_list  */
  if (yyn == 64)
    /* "parser.y":205  */
                    { yyval = yystack.valueAt (0); };
  break;


  case 65: /* argument_list: expression  */
  if (yyn == 65)
    /* "parser.y":210  */
        { yyval = new ArrayList<ExpressionNode>(); ((List<ExpressionNode>)yyval).add((ExpressionNode)yystack.valueAt (0)); };
  break;


  case 66: /* argument_list: argument_list TOK_COMMA expression  */
  if (yyn == 66)
    /* "parser.y":212  */
        { ((List<ExpressionNode>)yystack.valueAt (2)).add((ExpressionNode)yystack.valueAt (0)); yyval = yystack.valueAt (2); };
  break;



/* "parser.java":1047  */

        default: break;
      }

    yystack.pop(yylen);
    yylen = 0;
    /* Shift the result of the reduction.  */
    int yystate = yyLRGotoState(yystack.stateAt(0), yyr1_[yyn]);
    yystack.push(yystate, yyval);
    return YYNEWSTATE;
  }




  /**
   * Parse input from the scanner that was specified at object construction
   * time.  Return whether the end of the input was reached successfully.
   *
   * @return <tt>true</tt> if the parsing succeeds.  Note that this does not
   *          imply that there were no syntax errors.
   */
  public boolean parse() throws java.io.IOException

  {


    /* Lookahead token kind.  */
    int yychar = YYEMPTY_;
    /* Lookahead symbol kind.  */
    SymbolKind yytoken = null;

    /* State.  */
    int yyn = 0;
    int yylen = 0;
    int yystate = 0;
    YYStack yystack = new YYStack ();
    int label = YYNEWSTATE;



    /* Semantic value of the lookahead.  */
    Object yylval = null;



    yyerrstatus_ = 0;
    yynerrs = 0;

    /* Initialize the stack.  */
    yystack.push (yystate, yylval);



    for (;;)
      switch (label)
      {
        /* New state.  Unlike in the C/C++ skeletons, the state is already
           pushed when we come here.  */
      case YYNEWSTATE:

        /* Accept?  */
        if (yystate == YYFINAL_)
          {
            // Save the root node from the stack
            // Search through the stack to find ProgramNode
            if (yystack.height >= 0) {
              for (int i = 0; i <= yystack.height; i++) {
                Object value = yystack.valueAt(i);
                if (value instanceof ProgramNode) {
                  rootNode = (ProgramNode) value;
                  break;
                }
              }
            }
            return true;
          }

        /* Take a decision.  First try without lookahead.  */
        yyn = yypact_[yystate];
        if (yyPactValueIsDefault (yyn))
          {
            label = YYDEFAULT;
            break;
          }

        /* Read a lookahead token.  */
        if (yychar == YYEMPTY_)
          {

            yychar = yylexer.yylex ();
            yylval = yylexer.getLVal();

          }

        /* Convert token to internal form.  */
        yytoken = yytranslate_ (yychar);

        if (yytoken == SymbolKind.S_YYerror)
          {
            // The scanner already issued an error message, process directly
            // to error recovery.  But do not keep the error token as
            // lookahead, it is too special and may lead us to an endless
            // loop in error recovery. */
            yychar = Lexer.YYUNDEF;
            yytoken = SymbolKind.S_YYUNDEF;
            label = YYERRLAB1;
          }
        else
          {
            /* If the proper action on seeing token YYTOKEN is to reduce or to
               detect an error, take that action.  */
            yyn += yytoken.getCode();
            if (yyn < 0 || YYLAST_ < yyn || yycheck_[yyn] != yytoken.getCode()) {
              label = YYDEFAULT;
            }

            /* <= 0 means reduce or error.  */
            else if ((yyn = yytable_[yyn]) <= 0)
              {
                if (yyTableValueIsError(yyn)) {
                  label = YYERRLAB;
                } else {
                  yyn = -yyn;
                  label = YYREDUCE;
                }
              }

            else
              {
                /* Shift the lookahead token.  */
                /* Discard the token being shifted.  */
                yychar = YYEMPTY_;

                /* Count tokens shifted since error; after three, turn off error
                   status.  */
                if (yyerrstatus_ > 0)
                  --yyerrstatus_;

                yystate = yyn;
                yystack.push(yystate, yylval);
                label = YYNEWSTATE;
              }
          }
        break;

      /*-----------------------------------------------------------.
      | yydefault -- do the default action for the current state.  |
      `-----------------------------------------------------------*/
      case YYDEFAULT:
        yyn = yydefact_[yystate];
        if (yyn == 0)
          label = YYERRLAB;
        else
          label = YYREDUCE;
        break;

      /*-----------------------------.
      | yyreduce -- Do a reduction.  |
      `-----------------------------*/
      case YYREDUCE:
        yylen = yyr2_[yyn];
        label = yyaction(yyn, yystack, yylen);
        yystate = yystack.stateAt(0);
        break;

      /*------------------------------------.
      | yyerrlab -- here on detecting error |
      `------------------------------------*/
      case YYERRLAB:
        /* If not already recovering from an error, report this error.  */
        if (yyerrstatus_ == 0)
          {
            ++yynerrs;
            if (yychar == YYEMPTY_)
              yytoken = null;
            yyreportSyntaxError(new Context(this, yystack, yytoken));
          }

        if (yyerrstatus_ == 3)
          {
            /* If just tried and failed to reuse lookahead token after an
               error, discard it.  */

            if (yychar <= Lexer.YYEOF)
              {
                /* Return failure if at end of input.  */
                if (yychar == Lexer.YYEOF)
                  return false;
              }
            else
              yychar = YYEMPTY_;
          }

        /* Else will try to reuse lookahead token after shifting the error
           token.  */
        label = YYERRLAB1;
        break;

      /*-------------------------------------------------.
      | errorlab -- error raised explicitly by YYERROR.  |
      `-------------------------------------------------*/
      case YYERROR:
        /* Do not reclaim the symbols of the rule which action triggered
           this YYERROR.  */
        yystack.pop (yylen);
        yylen = 0;
        yystate = yystack.stateAt(0);
        label = YYERRLAB1;
        break;

      /*-------------------------------------------------------------.
      | yyerrlab1 -- common code for both syntax error and YYERROR.  |
      `-------------------------------------------------------------*/
      case YYERRLAB1:
        yyerrstatus_ = 3;       /* Each real token shifted decrements this.  */

        // Pop stack until we find a state that shifts the error token.
        for (;;)
          {
            yyn = yypact_[yystate];
            if (!yyPactValueIsDefault (yyn))
              {
                yyn += SymbolKind.S_YYerror.getCode();
                if (0 <= yyn && yyn <= YYLAST_
                    && yycheck_[yyn] == SymbolKind.S_YYerror.getCode())
                  {
                    yyn = yytable_[yyn];
                    if (0 < yyn)
                      break;
                  }
              }

            /* Pop the current state because it cannot handle the
             * error token.  */
            if (yystack.height == 0)
              return false;


            yystack.pop ();
            yystate = yystack.stateAt(0);
          }

        if (label == YYABORT)
          /* Leave the switch.  */
          break;



        /* Shift the error token.  */

        yystate = yyn;
        yystack.push (yyn, yylval);
        label = YYNEWSTATE;
        break;

        /* Accept.  */
      case YYACCEPT:
        return true;

        /* Abort.  */
      case YYABORT:
        return false;
      }
}




  /**
   * Information needed to get the list of expected tokens and to forge
   * a syntax error diagnostic.
   */
  public static final class Context {
    Context(Parser parser, YYStack stack, SymbolKind token) {
      yyparser = parser;
      yystack = stack;
      yytoken = token;
    }

    private Parser yyparser;
    private YYStack yystack;


    /**
     * The symbol kind of the lookahead token.
     */
    public final SymbolKind getToken() {
      return yytoken;
    }

    private SymbolKind yytoken;
    static final int NTOKENS = Parser.YYNTOKENS_;

    /**
     * Put in YYARG at most YYARGN of the expected tokens given the
     * current YYCTX, and return the number of tokens stored in YYARG.  If
     * YYARG is null, return the number of expected tokens (guaranteed to
     * be less than YYNTOKENS).
     */
    int getExpectedTokens(SymbolKind yyarg[], int yyargn) {
      return getExpectedTokens (yyarg, 0, yyargn);
    }

    int getExpectedTokens(SymbolKind yyarg[], int yyoffset, int yyargn) {
      int yycount = yyoffset;
      int yyn = yypact_[this.yystack.stateAt(0)];
      if (!yyPactValueIsDefault(yyn))
        {
          /* Start YYX at -YYN if negative to avoid negative
             indexes in YYCHECK.  In other words, skip the first
             -YYN actions for this state because they are default
             actions.  */
          int yyxbegin = yyn < 0 ? -yyn : 0;
          /* Stay within bounds of both yycheck and yytname.  */
          int yychecklim = YYLAST_ - yyn + 1;
          int yyxend = yychecklim < NTOKENS ? yychecklim : NTOKENS;
          for (int yyx = yyxbegin; yyx < yyxend; ++yyx)
            if (yycheck_[yyx + yyn] == yyx && yyx != SymbolKind.S_YYerror.getCode()
                && !yyTableValueIsError(yytable_[yyx + yyn]))
              {
                if (yyarg == null)
                  yycount += 1;
                else if (yycount == yyargn)
                  return 0; // FIXME: this is incorrect.
                else
                  yyarg[yycount++] = SymbolKind.get(yyx);
              }
        }
      if (yyarg != null && yycount == yyoffset && yyoffset < yyargn)
        yyarg[yycount] = null;
      return yycount - yyoffset;
    }
  }




  private int yysyntaxErrorArguments(Context yyctx, SymbolKind[] yyarg, int yyargn) {
    /* There are many possibilities here to consider:
       - If this state is a consistent state with a default action,
         then the only way this function was invoked is if the
         default action is an error action.  In that case, don't
         check for expected tokens because there are none.
       - The only way there can be no lookahead present (in tok) is
         if this state is a consistent state with a default action.
         Thus, detecting the absence of a lookahead is sufficient to
         determine that there is no unexpected or expected token to
         report.  In that case, just report a simple "syntax error".
       - Don't assume there isn't a lookahead just because this
         state is a consistent state with a default action.  There
         might have been a previous inconsistent state, consistent
         state with a non-default action, or user semantic action
         that manipulated yychar.  (However, yychar is currently out
         of scope during semantic actions.)
       - Of course, the expected token list depends on states to
         have correct lookahead information, and it depends on the
         parser not to perform extra reductions after fetching a
         lookahead from the scanner and before detecting a syntax
         error.  Thus, state merging (from LALR or IELR) and default
         reductions corrupt the expected token list.  However, the
         list is correct for canonical LR with one exception: it
         will still contain any token that will not be accepted due
         to an error action in a later state.
    */
    int yycount = 0;
    if (yyctx.getToken() != null)
      {
        if (yyarg != null)
          yyarg[yycount] = yyctx.getToken();
        yycount += 1;
        yycount += yyctx.getExpectedTokens(yyarg, 1, yyargn);
      }
    return yycount;
  }


  /**
   * Build and emit a "syntax error" message in a user-defined way.
   *
   * @param ctx  The context of the error.
   */
  private void yyreportSyntaxError(Context yyctx) {
      if (yyErrorVerbose) {
          final int argmax = 5;
          SymbolKind[] yyarg = new SymbolKind[argmax];
          int yycount = yysyntaxErrorArguments(yyctx, yyarg, argmax);
          String[] yystr = new String[yycount];
          for (int yyi = 0; yyi < yycount; ++yyi) {
              yystr[yyi] = yyarg[yyi].getName();
          }
          String yyformat;
          switch (yycount) {
              default:
              case 0: yyformat = "syntax error"; break;
              case 1: yyformat = "syntax error, unexpected {0}"; break;
              case 2: yyformat = "syntax error, unexpected {0}, expecting {1}"; break;
              case 3: yyformat = "syntax error, unexpected {0}, expecting {1} or {2}"; break;
              case 4: yyformat = "syntax error, unexpected {0}, expecting {1} or {2} or {3}"; break;
              case 5: yyformat = "syntax error, unexpected {0}, expecting {1} or {2} or {3} or {4}"; break;
          }
          yyerror(new MessageFormat(yyformat).format(yystr));
      } else {
          yyerror("syntax error");
      }
  }

  /**
   * Whether the given <code>yypact_</code> value indicates a defaulted state.
   * @param yyvalue   the value to check
   */
  private static boolean yyPactValueIsDefault(int yyvalue) {
    return yyvalue == yypact_ninf_;
  }

  /**
   * Whether the given <code>yytable_</code>
   * value indicates a syntax error.
   * @param yyvalue the value to check
   */
  private static boolean yyTableValueIsError(int yyvalue) {
    return yyvalue == yytable_ninf_;
  }

  private static final byte yypact_ninf_ = -80;
  private static final byte yytable_ninf_ = -1;

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
  private static final byte[] yypact_ = yypact_init();
  private static final byte[] yypact_init()
  {
    return new byte[]
    {
      -2,     1,   -80,    22,     2,   -80,    24,   -80,   -80,   -80,
      20,    25,   -80,   -80,    45,   -80,    23,    26,    15,   -80,
     -80,   -80,    12,   -80,     9,    21,    30,    29,     6,   -80,
       6,     6,     6,    30,    31,    27,    32,   -80,     6,     6,
       6,     6,   -80,   -80,    43,    29,   -80,   -80,   -80,    -6,
     -80,   -80,   -80,   -80,   -80,    33,    34,   -80,   -80,   -80,
      35,   -80,   -80,   -80,    35,    35,    35,    36,    39,    57,
      30,    -4,     8,   -80,    35,    35,   -80,   -80,     6,    46,
       6,     6,    48,    41,   -80,    29,   -80,    29,    29,    35,
     -80,    35,    40,    44,    49,    33,   -80,    51,   -80,    61,
      65,    58,   -80,     6,   -80,   -80,   -80,   -80,    29,    67,
      35,   -80,   -80
    };
  }

/* YYDEFACT[STATE-NUM] -- Default reduction number in state STATE-NUM.
   Performed when YYTABLE does not specify something else to do.  Zero
   means the default is an error.  */
  private static final byte[] yydefact_ = yydefact_init();
  private static final byte[] yydefact_init()
  {
    return new byte[]
    {
       0,     0,     2,     0,     0,     4,     7,     1,     3,     5,
       0,     0,     8,     9,     0,     6,     0,     0,     0,    10,
      11,    12,     0,    13,     0,     0,    21,    29,     0,    17,
       0,     0,     0,    21,     0,     0,    22,    23,     0,     0,
      48,     0,    41,    34,     0,    30,    31,    33,    35,     0,
      36,    37,    38,    39,    60,    56,     0,    57,    58,    59,
      20,    51,    52,    53,    16,    15,    14,     0,     0,     0,
       0,     0,     0,    47,    49,    50,    19,    32,     0,     0,
      63,    63,     0,    26,    25,    29,    24,    29,    29,    40,
      42,    65,     0,    64,     0,    55,    54,     0,    18,     0,
       0,    45,    62,     0,    61,    27,    28,    43,    29,     0,
      66,    46,    44
    };
  }

/* YYPGOTO[NTERM-NUM].  */
  private static final byte[] yypgoto_ = yypgoto_init();
  private static final byte[] yypgoto_init()
  {
    return new byte[]
    {
     -80,   -80,   -80,    70,   -80,   -80,   -80,    62,   -80,   -80,
     -80,    47,   -80,    11,   -80,   -80,   -79,   -80,    37,   -80,
     -80,   -80,   -80,   -80,   -80,   -80,   -80,   -80,   -28,   -80,
     -80,    -3,     3,   -80
    };
  }

/* YYDEFGOTO[NTERM-NUM].  */
  private static final byte[] yydefgoto_ = yydefgoto_init();
  private static final byte[] yydefgoto_init()
  {
    return new byte[]
    {
       0,     3,     4,     5,    11,    14,    19,    43,    21,    22,
      29,    35,    36,    37,    98,    23,    44,    45,    46,    47,
      48,    49,    50,    51,   109,    52,    73,    53,    91,    61,
      62,    63,    92,    93
    };
  }

/* YYTABLE[YYPACT[STATE-NUM]] -- What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule whose
   number is the opposite.  If YYTABLE_NINF, syntax error.  */
  private static final byte[] yytable_ = yytable_init();
  private static final byte[] yytable_init()
  {
    return new byte[]
    {
      60,     1,    64,    65,    66,     1,    99,    87,   100,   101,
      71,    72,    74,    75,    30,    54,    78,    27,    79,     6,
      82,    88,     7,    55,    56,    57,    58,    59,    10,   111,
      13,    31,    82,     2,    32,    28,    16,     8,    12,    38,
      24,    39,    26,    25,    40,    41,    42,    34,    33,    76,
      89,    15,    16,    17,    18,    69,    68,    84,    70,    82,
      80,    81,    85,    90,    83,    95,    97,   106,   102,   105,
     103,   107,   108,   112,     9,   110,    20,   104,     0,    96,
      67,    86,    77,     0,    94
    };
  }

private static final byte[] yycheck_ = yycheck_init();
  private static final byte[] yycheck_init()
  {
    return new byte[]
    {
      28,     3,    30,    31,    32,     3,    85,    11,    87,    88,
      38,    39,    40,    41,     5,     9,    22,     5,    24,    18,
      24,    13,     0,    17,    18,    19,    20,    21,     4,   108,
       5,    22,    24,    35,    25,    23,     7,    35,    18,    10,
      17,    12,    27,    17,    15,    16,    17,    17,    27,     6,
      78,     6,     7,     8,     9,    28,    25,    18,    26,    24,
      27,    27,     5,    17,    28,    17,    25,     6,    28,    18,
      26,     6,    14,     6,     4,   103,    14,    28,    -1,    82,
      33,    70,    45,    -1,    81
    };
  }

/* YYSTOS[STATE-NUM] -- The symbol kind of the accessing symbol of
   state STATE-NUM.  */
  private static final byte[] yystos_ = yystos_init();
  private static final byte[] yystos_init()
  {
    return new byte[]
    {
       0,     3,    35,    37,    38,    39,    18,     0,    35,    39,
       4,    40,    18,     5,    41,     6,     7,     8,     9,    42,
      43,    44,    45,    51,    17,    17,    27,     5,    23,    46,
       5,    22,    25,    27,    17,    47,    48,    49,    10,    12,
      15,    16,    17,    43,    52,    53,    54,    55,    56,    57,
      58,    59,    61,    63,     9,    17,    18,    19,    20,    21,
      64,    65,    66,    67,    64,    64,    64,    47,    25,    28,
      26,    64,    64,    62,    64,    64,     6,    54,    22,    24,
      27,    27,    24,    28,    18,     5,    49,    11,    13,    64,
      17,    64,    68,    69,    68,    17,    67,    25,    50,    52,
      52,    52,    28,    26,    28,    18,     6,     6,    14,    60,
      64,    52,     6
    };
  }

/* YYR1[RULE-NUM] -- Symbol kind of the left-hand side of rule RULE-NUM.  */
  private static final byte[] yyr1_ = yyr1_init();
  private static final byte[] yyr1_init()
  {
    return new byte[]
    {
       0,    36,    37,    37,    38,    38,    39,    40,    40,    41,
      41,    42,    42,    42,    43,    43,    43,    44,    45,    46,
      46,    47,    47,    48,    48,    49,    50,    50,    51,    52,
      52,    53,    53,    54,    54,    55,    55,    55,    55,    55,
      56,    57,    57,    58,    59,    60,    60,    61,    62,    62,
      63,    64,    64,    64,    64,    64,    65,    65,    65,    65,
      65,    66,    67,    68,    68,    69,    69
    };
  }

/* YYR2[RULE-NUM] -- Number of symbols on the right-hand side of rule RULE-NUM.  */
  private static final byte[] yyr2_ = yyr2_init();
  private static final byte[] yyr2_init()
  {
    return new byte[]
    {
       0,     2,     1,     2,     1,     2,     6,     0,     2,     0,
       2,     1,     1,     1,     4,     4,     4,     2,     6,     3,
       2,     0,     1,     1,     3,     3,     0,     2,     7,     0,
       1,     1,     2,     1,     1,     1,     1,     1,     1,     1,
       3,     1,     3,     5,     6,     0,     2,     2,     0,     1,
       2,     1,     1,     1,     3,     3,     1,     1,     1,     1,
       1,     4,     4,     0,     1,     1,     3
    };
  }




  /* YYTRANSLATE_(TOKEN-NUM) -- Symbol number corresponding to TOKEN-NUM
     as returned by yylex, with out-of-bounds checking.  */
  private static final SymbolKind yytranslate_(int t)
  {
    // Last valid token kind.
    int code_max = 290;
    if (t <= 0)
      return SymbolKind.S_YYEOF;
    else if (t <= code_max)
      return SymbolKind.get(yytranslate_table_[t]);
    else
      return SymbolKind.S_YYUNDEF;
  }
  private static final byte[] yytranslate_table_ = yytranslate_table_init();
  private static final byte[] yytranslate_table_init()
  {
    return new byte[]
    {
       0,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     1,     2,     3,     4,
       5,     6,     7,     8,     9,    10,    11,    12,    13,    14,
      15,    16,    17,    18,    19,    20,    21,    22,    23,    24,
      25,    26,    27,    28,    29,    30,    31,    32,    33,    34,
      35
    };
  }


  private static final int YYLAST_ = 84;
  private static final int YYEMPTY_ = -2;
  private static final int YYFINAL_ = 7;
  private static final int YYNTOKENS_ = 36;


}
/* "parser.y":215  */

