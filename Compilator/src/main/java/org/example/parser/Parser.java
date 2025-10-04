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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.example.lexer.Token;

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
    S_TOK_LIST(6),                 /* TOK_LIST  */
    S_TOK_ARRAY(7),                /* TOK_ARRAY  */
    S_TOK_END(8),                  /* TOK_END  */
    S_TOK_VAR(9),                  /* TOK_VAR  */
    S_TOK_METHOD(10),              /* TOK_METHOD  */
    S_TOK_THIS(11),                /* TOK_THIS  */
    S_TOK_WHILE(12),               /* TOK_WHILE  */
    S_TOK_LOOP(13),                /* TOK_LOOP  */
    S_TOK_IF(14),                  /* TOK_IF  */
    S_TOK_THEN(15),                /* TOK_THEN  */
    S_TOK_ELSE(16),                /* TOK_ELSE  */
    S_TOK_RETURN(17),              /* TOK_RETURN  */
    S_TOK_PRINT(18),               /* TOK_PRINT  */
    S_TOK_ID(19),                  /* TOK_ID  */
    S_TOK_TYPE_ID(20),             /* TOK_TYPE_ID  */
    S_TOK_INT_LIT(21),             /* TOK_INT_LIT  */
    S_TOK_REAL_LIT(22),            /* TOK_REAL_LIT  */
    S_TOK_BOOL_LIT(23),            /* TOK_BOOL_LIT  */
    S_TOK_ASSIGN(24),              /* TOK_ASSIGN  */
    S_TOK_ARROW(25),               /* TOK_ARROW  */
    S_TOK_DOT(26),                 /* TOK_DOT  */
    S_TOK_COLON(27),               /* TOK_COLON  */
    S_TOK_COMMA(28),               /* TOK_COMMA  */
    S_TOK_LPAR(29),                /* TOK_LPAR  */
    S_TOK_RPAR(30),                /* TOK_RPAR  */
    S_TOK_LBRACK(31),              /* TOK_LBRACK  */
    S_TOK_RBRACK(32),              /* TOK_RBRACK  */
    S_TOK_LBRACE(33),              /* TOK_LBRACE  */
    S_TOK_RBRACE(34),              /* TOK_RBRACE  */
    S_TOK_LT(35),                  /* TOK_LT  */
    S_TOK_RT(36),                  /* TOK_RT  */
    S_TOK_EOF(37),                 /* TOK_EOF  */
    S_YYACCEPT(38),                /* $accept  */
    S_program(39),                 /* program  */
    S_class_list(40),              /* class_list  */
    S_class_declaration(41),       /* class_declaration  */
    S_optional_extends(42),        /* optional_extends  */
    S_member_list(43),             /* member_list  */
    S_member_declaration(44),      /* member_declaration  */
    S_variable_declaration(45),    /* variable_declaration  */
    S_constructor_call_opt(46),    /* constructor_call_opt  */
    S_type_name(47),               /* type_name  */
    S_method_declaration(48),      /* method_declaration  */
    S_method_header(49),           /* method_header  */
    S_method_body(50),             /* method_body  */
    S_parameter_list_opt(51),      /* parameter_list_opt  */
    S_parameter_list(52),          /* parameter_list  */
    S_parameter_declaration(53),   /* parameter_declaration  */
    S_optional_return_type(54),    /* optional_return_type  */
    S_constructor_declaration(55), /* constructor_declaration  */
    S_body(56),                    /* body  */
    S_body_element_list(57),       /* body_element_list  */
    S_body_element(58),            /* body_element  */
    S_statement(59),               /* statement  */
    S_assignment(60),              /* assignment  */
    S_lvalue(61),                  /* lvalue  */
    S_while_loop(62),              /* while_loop  */
    S_if_statement(63),            /* if_statement  */
    S_optional_else(64),           /* optional_else  */
    S_return_statement(65),        /* return_statement  */
    S_return_expression_opt(66),   /* return_expression_opt  */
    S_print_statement(67),         /* print_statement  */
    S_expression(68),              /* expression  */
    S_primary(69),                 /* primary  */
    S_constructor_invocation(70),  /* constructor_invocation  */
    S_method_invocation(71),       /* method_invocation  */
    S_argument_list_opt(72),       /* argument_list_opt  */
    S_argument_list(73);           /* argument_list  */


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
      SymbolKind.S_TOK_LIST,
      SymbolKind.S_TOK_ARRAY,
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
      SymbolKind.S_constructor_call_opt,
      SymbolKind.S_type_name,
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
  "TOK_EXTENDS", "TOK_IS", "TOK_LIST", "TOK_ARRAY", "TOK_END", "TOK_VAR",
  "TOK_METHOD", "TOK_THIS", "TOK_WHILE", "TOK_LOOP", "TOK_IF", "TOK_THEN",
  "TOK_ELSE", "TOK_RETURN", "TOK_PRINT", "TOK_ID", "TOK_TYPE_ID",
  "TOK_INT_LIT", "TOK_REAL_LIT", "TOK_BOOL_LIT", "TOK_ASSIGN", "TOK_ARROW",
  "TOK_DOT", "TOK_COLON", "TOK_COMMA", "TOK_LPAR", "TOK_RPAR",
  "TOK_LBRACK", "TOK_RBRACK", "TOK_LBRACE", "TOK_RBRACE", "TOK_LT",
  "TOK_RT", "TOK_EOF", "$accept", "program", "class_list",
  "class_declaration", "optional_extends", "member_list",
  "member_declaration", "variable_declaration", "constructor_call_opt",
  "type_name", "method_declaration", "method_header", "method_body",
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
    /** Token TOK_LIST, to be returned by the scanner.  */
    static final int TOK_LIST = 261;
    /** Token TOK_ARRAY, to be returned by the scanner.  */
    static final int TOK_ARRAY = 262;
    /** Token TOK_END, to be returned by the scanner.  */
    static final int TOK_END = 263;
    /** Token TOK_VAR, to be returned by the scanner.  */
    static final int TOK_VAR = 264;
    /** Token TOK_METHOD, to be returned by the scanner.  */
    static final int TOK_METHOD = 265;
    /** Token TOK_THIS, to be returned by the scanner.  */
    static final int TOK_THIS = 266;
    /** Token TOK_WHILE, to be returned by the scanner.  */
    static final int TOK_WHILE = 267;
    /** Token TOK_LOOP, to be returned by the scanner.  */
    static final int TOK_LOOP = 268;
    /** Token TOK_IF, to be returned by the scanner.  */
    static final int TOK_IF = 269;
    /** Token TOK_THEN, to be returned by the scanner.  */
    static final int TOK_THEN = 270;
    /** Token TOK_ELSE, to be returned by the scanner.  */
    static final int TOK_ELSE = 271;
    /** Token TOK_RETURN, to be returned by the scanner.  */
    static final int TOK_RETURN = 272;
    /** Token TOK_PRINT, to be returned by the scanner.  */
    static final int TOK_PRINT = 273;
    /** Token TOK_ID, to be returned by the scanner.  */
    static final int TOK_ID = 274;
    /** Token TOK_TYPE_ID, to be returned by the scanner.  */
    static final int TOK_TYPE_ID = 275;
    /** Token TOK_INT_LIT, to be returned by the scanner.  */
    static final int TOK_INT_LIT = 276;
    /** Token TOK_REAL_LIT, to be returned by the scanner.  */
    static final int TOK_REAL_LIT = 277;
    /** Token TOK_BOOL_LIT, to be returned by the scanner.  */
    static final int TOK_BOOL_LIT = 278;
    /** Token TOK_ASSIGN, to be returned by the scanner.  */
    static final int TOK_ASSIGN = 279;
    /** Token TOK_ARROW, to be returned by the scanner.  */
    static final int TOK_ARROW = 280;
    /** Token TOK_DOT, to be returned by the scanner.  */
    static final int TOK_DOT = 281;
    /** Token TOK_COLON, to be returned by the scanner.  */
    static final int TOK_COLON = 282;
    /** Token TOK_COMMA, to be returned by the scanner.  */
    static final int TOK_COMMA = 283;
    /** Token TOK_LPAR, to be returned by the scanner.  */
    static final int TOK_LPAR = 284;
    /** Token TOK_RPAR, to be returned by the scanner.  */
    static final int TOK_RPAR = 285;
    /** Token TOK_LBRACK, to be returned by the scanner.  */
    static final int TOK_LBRACK = 286;
    /** Token TOK_RBRACK, to be returned by the scanner.  */
    static final int TOK_RBRACK = 287;
    /** Token TOK_LBRACE, to be returned by the scanner.  */
    static final int TOK_LBRACE = 288;
    /** Token TOK_RBRACE, to be returned by the scanner.  */
    static final int TOK_RBRACE = 289;
    /** Token TOK_LT, to be returned by the scanner.  */
    static final int TOK_LT = 290;
    /** Token TOK_RT, to be returned by the scanner.  */
    static final int TOK_RT = 291;
    /** Token TOK_EOF, to be returned by the scanner.  */
    static final int TOK_EOF = 292;

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


  case 14: /* variable_declaration: TOK_VAR TOK_ID TOK_COLON type_name constructor_call_opt  */
  if (yyn == 14)
    /* "parser.y":70  */
        { yyval = new VarDeclNode(((Token)yystack.valueAt (3)).getLexeme(), (ASTNode)yystack.valueAt (1), (ExpressionNode)yystack.valueAt (0), VarDeclType.COLON); };
  break;


  case 15: /* variable_declaration: TOK_VAR TOK_ID TOK_ASSIGN type_name constructor_call_opt  */
  if (yyn == 15)
    /* "parser.y":72  */
        { yyval = new VarDeclNode(((Token)yystack.valueAt (3)).getLexeme(), (ASTNode)yystack.valueAt (1), (ExpressionNode)yystack.valueAt (0), VarDeclType.ASSIGN); };
  break;


  case 16: /* variable_declaration: TOK_VAR TOK_ID TOK_IS type_name constructor_call_opt  */
  if (yyn == 16)
    /* "parser.y":74  */
        { yyval = new VarDeclNode(((Token)yystack.valueAt (3)).getLexeme(), (ASTNode)yystack.valueAt (1), (ExpressionNode)yystack.valueAt (0), VarDeclType.IS); };
  break;


  case 17: /* variable_declaration: TOK_VAR TOK_ID TOK_COLON expression  */
  if (yyn == 17)
    /* "parser.y":76  */
        { yyval = new VarDeclNode(((Token)yystack.valueAt (2)).getLexeme(), null, (ExpressionNode)yystack.valueAt (0), VarDeclType.COLON); };
  break;


  case 18: /* variable_declaration: TOK_VAR TOK_ID TOK_ASSIGN expression  */
  if (yyn == 18)
    /* "parser.y":78  */
        { yyval = new VarDeclNode(((Token)yystack.valueAt (2)).getLexeme(), null, (ExpressionNode)yystack.valueAt (0), VarDeclType.ASSIGN); };
  break;


  case 19: /* variable_declaration: TOK_VAR TOK_ID TOK_IS expression  */
  if (yyn == 19)
    /* "parser.y":80  */
        { yyval = new VarDeclNode(((Token)yystack.valueAt (2)).getLexeme(), null, (ExpressionNode)yystack.valueAt (0), VarDeclType.IS); };
  break;


  case 20: /* constructor_call_opt: %empty  */
  if (yyn == 20)
    /* "parser.y":84  */
                  { yyval = null; };
  break;


  case 21: /* constructor_call_opt: TOK_LPAR argument_list_opt TOK_RPAR  */
  if (yyn == 21)
    /* "parser.y":85  */
                                          { yyval = new ConstructorInvocationNode(null, (List<ExpressionNode>)yystack.valueAt (1)); };
  break;


  case 22: /* type_name: TOK_TYPE_ID  */
  if (yyn == 22)
    /* "parser.y":89  */
                                             { yyval = new TypeNode(((Token)yystack.valueAt (0)).getLexeme()); };
  break;


  case 23: /* type_name: TOK_LIST TOK_LBRACK type_name TOK_RBRACK  */
  if (yyn == 23)
    /* "parser.y":90  */
                                               { yyval = new GenericTypeNode("List", (TypeNode)yystack.valueAt (1)); };
  break;


  case 24: /* type_name: TOK_ARRAY TOK_LBRACK type_name TOK_RBRACK  */
  if (yyn == 24)
    /* "parser.y":91  */
                                                { yyval = new GenericTypeNode("Array", (TypeNode)yystack.valueAt (1)); };
  break;


  case 25: /* method_declaration: method_header method_body  */
  if (yyn == 25)
    /* "parser.y":96  */
        { yyval = new MethodDeclNode((MethodHeaderNode)yystack.valueAt (1), (MethodBodyNode)yystack.valueAt (0)); };
  break;


  case 26: /* method_header: TOK_METHOD TOK_ID TOK_LPAR parameter_list_opt TOK_RPAR optional_return_type  */
  if (yyn == 26)
    /* "parser.y":101  */
      { yyval = new MethodHeaderNode(((Token)yystack.valueAt (4)).getLexeme(), (List<ParamDeclNode>)yystack.valueAt (2), yystack.valueAt (0) == null ? null : ((Token)yystack.valueAt (0)).getLexeme()); };
  break;


  case 27: /* method_body: TOK_IS body TOK_END  */
  if (yyn == 27)
    /* "parser.y":105  */
                          { yyval = new MethodBodyNode((BodyNode)yystack.valueAt (1), false); };
  break;


  case 28: /* method_body: TOK_ARROW expression  */
  if (yyn == 28)
    /* "parser.y":106  */
                           { yyval = new MethodBodyNode((ExpressionNode)yystack.valueAt (0), true); };
  break;


  case 29: /* parameter_list_opt: %empty  */
  if (yyn == 29)
    /* "parser.y":110  */
                  { yyval = new ArrayList<ParamDeclNode>(); };
  break;


  case 30: /* parameter_list_opt: parameter_list  */
  if (yyn == 30)
    /* "parser.y":111  */
                     { yyval = yystack.valueAt (0); };
  break;


  case 31: /* parameter_list: parameter_declaration  */
  if (yyn == 31)
    /* "parser.y":116  */
        { yyval = new ArrayList<ParamDeclNode>(); ((List<ParamDeclNode>)yyval).add((ParamDeclNode)yystack.valueAt (0)); };
  break;


  case 32: /* parameter_list: parameter_list TOK_COMMA parameter_declaration  */
  if (yyn == 32)
    /* "parser.y":118  */
        { ((List<ParamDeclNode>)yystack.valueAt (2)).add((ParamDeclNode)yystack.valueAt (0)); yyval = yystack.valueAt (2); };
  break;


  case 33: /* parameter_declaration: TOK_ID TOK_COLON type_name  */
  if (yyn == 33)
    /* "parser.y":122  */
                                 { yyval = new ParamDeclNode(((Token)yystack.valueAt (2)).getLexeme(), (TypeNode)yystack.valueAt (0)); };
  break;


  case 34: /* optional_return_type: %empty  */
  if (yyn == 34)
    /* "parser.y":126  */
                  { yyval = null; };
  break;


  case 35: /* optional_return_type: TOK_COLON TOK_TYPE_ID  */
  if (yyn == 35)
    /* "parser.y":127  */
                            { yyval = yystack.valueAt (0); };
  break;


  case 36: /* constructor_declaration: TOK_THIS TOK_LPAR parameter_list_opt TOK_RPAR TOK_IS body TOK_END  */
  if (yyn == 36)
    /* "parser.y":132  */
        { yyval = new ConstructorDeclNode((List<ParamDeclNode>)yystack.valueAt (4), (BodyNode)yystack.valueAt (1)); };
  break;


  case 37: /* body: %empty  */
  if (yyn == 37)
    /* "parser.y":136  */
                  { yyval = new BodyNode(null); };
  break;


  case 38: /* body: body_element_list  */
  if (yyn == 38)
    /* "parser.y":137  */
                        { yyval = new BodyNode((List<BodyElementNode>)yystack.valueAt (0)); };
  break;


  case 39: /* body_element_list: body_element  */
  if (yyn == 39)
    /* "parser.y":142  */
        { yyval = new ArrayList<BodyElementNode>(); ((List<BodyElementNode>)yyval).add((BodyElementNode)yystack.valueAt (0)); };
  break;


  case 40: /* body_element_list: body_element_list body_element  */
  if (yyn == 40)
    /* "parser.y":144  */
        { ((List<BodyElementNode>)yystack.valueAt (1)).add((BodyElementNode)yystack.valueAt (0)); yyval = yystack.valueAt (1); };
  break;


  case 41: /* body_element: statement  */
  if (yyn == 41)
    /* "parser.y":148  */
                { yyval = (BodyElementNode)yystack.valueAt (0); };
  break;


  case 42: /* body_element: variable_declaration  */
  if (yyn == 42)
    /* "parser.y":149  */
                           { yyval = (BodyElementNode)yystack.valueAt (0); };
  break;


  case 43: /* statement: assignment  */
  if (yyn == 43)
    /* "parser.y":153  */
                 { yyval = (StatementNode)yystack.valueAt (0); };
  break;


  case 44: /* statement: while_loop  */
  if (yyn == 44)
    /* "parser.y":154  */
                 { yyval = (StatementNode)yystack.valueAt (0); };
  break;


  case 45: /* statement: if_statement  */
  if (yyn == 45)
    /* "parser.y":155  */
                   { yyval = (StatementNode)yystack.valueAt (0); };
  break;


  case 46: /* statement: return_statement  */
  if (yyn == 46)
    /* "parser.y":156  */
                       { yyval = (StatementNode)yystack.valueAt (0); };
  break;


  case 47: /* statement: print_statement  */
  if (yyn == 47)
    /* "parser.y":157  */
                      { yyval = (StatementNode)yystack.valueAt (0); };
  break;


  case 48: /* assignment: lvalue TOK_ASSIGN expression  */
  if (yyn == 48)
    /* "parser.y":162  */
        { yyval = new AssignmentNode((ExpressionNode)yystack.valueAt (2), (ExpressionNode)yystack.valueAt (0)); };
  break;


  case 49: /* lvalue: TOK_ID  */
  if (yyn == 49)
    /* "parser.y":166  */
             { yyval = new IdentifierNode(((Token)yystack.valueAt (0)).getLexeme()); };
  break;


  case 50: /* lvalue: lvalue TOK_DOT TOK_ID  */
  if (yyn == 50)
    /* "parser.y":167  */
                            { yyval = new MemberAccessNode((ExpressionNode)yystack.valueAt (2), new IdentifierNode(((Token)yystack.valueAt (0)).getLexeme())); };
  break;


  case 51: /* while_loop: TOK_WHILE expression TOK_LOOP body TOK_END  */
  if (yyn == 51)
    /* "parser.y":171  */
                                                 { yyval = new WhileLoopNode((ExpressionNode)yystack.valueAt (3), (BodyNode)yystack.valueAt (1)); };
  break;


  case 52: /* if_statement: TOK_IF expression TOK_THEN body optional_else TOK_END  */
  if (yyn == 52)
    /* "parser.y":176  */
        { yyval = new IfStatementNode((ExpressionNode)yystack.valueAt (4), (BodyNode)yystack.valueAt (2), (BodyNode)yystack.valueAt (1)); };
  break;


  case 53: /* optional_else: %empty  */
  if (yyn == 53)
    /* "parser.y":180  */
                  { yyval = null; };
  break;


  case 54: /* optional_else: TOK_ELSE body  */
  if (yyn == 54)
    /* "parser.y":181  */
                    { yyval = (BodyNode)yystack.valueAt (0); };
  break;


  case 55: /* return_statement: TOK_RETURN return_expression_opt  */
  if (yyn == 55)
    /* "parser.y":185  */
                                       { yyval = new ReturnNode((ExpressionNode)yystack.valueAt (0)); };
  break;


  case 56: /* return_expression_opt: %empty  */
  if (yyn == 56)
    /* "parser.y":189  */
                  { yyval = null; };
  break;


  case 57: /* return_expression_opt: expression  */
  if (yyn == 57)
    /* "parser.y":190  */
                 { yyval = yystack.valueAt (0); };
  break;


  case 58: /* print_statement: TOK_PRINT expression  */
  if (yyn == 58)
    /* "parser.y":194  */
                           { yyval = new PrintNode((ExpressionNode)yystack.valueAt (0)); };
  break;


  case 59: /* expression: primary  */
  if (yyn == 59)
    /* "parser.y":198  */
              { yyval = yystack.valueAt (0); };
  break;


  case 60: /* expression: constructor_invocation  */
  if (yyn == 60)
    /* "parser.y":199  */
                             { yyval = yystack.valueAt (0); };
  break;


  case 61: /* expression: method_invocation  */
  if (yyn == 61)
    /* "parser.y":200  */
                        { yyval = yystack.valueAt (0); };
  break;


  case 62: /* expression: expression TOK_DOT method_invocation  */
  if (yyn == 62)
    /* "parser.y":202  */
        { yyval = new MemberAccessNode((ExpressionNode)yystack.valueAt (2), (MethodInvocationNode)yystack.valueAt (0)); };
  break;


  case 63: /* expression: expression TOK_DOT TOK_ID  */
  if (yyn == 63)
    /* "parser.y":203  */
                                { yyval = new MemberAccessNode((ExpressionNode)yystack.valueAt (2), new IdentifierNode(((Token)yystack.valueAt (0)).getLexeme())); };
  break;


  case 64: /* primary: TOK_ID  */
  if (yyn == 64)
    /* "parser.y":207  */
                    { yyval = new IdentifierNode(((Token)yystack.valueAt (0)).getLexeme()); };
  break;


  case 65: /* primary: TOK_INT_LIT  */
  if (yyn == 65)
    /* "parser.y":208  */
                    { yyval = new IntLiteralNode(Integer.parseInt(((Token)yystack.valueAt (0)).getLexeme())); };
  break;


  case 66: /* primary: TOK_REAL_LIT  */
  if (yyn == 66)
    /* "parser.y":209  */
                    { yyval = new RealLiteralNode(Double.parseDouble(((Token)yystack.valueAt (0)).getLexeme())); };
  break;


  case 67: /* primary: TOK_BOOL_LIT  */
  if (yyn == 67)
    /* "parser.y":210  */
                    { yyval = new BoolLiteralNode(Boolean.parseBoolean(((Token)yystack.valueAt (0)).getLexeme())); };
  break;


  case 68: /* primary: TOK_THIS  */
  if (yyn == 68)
    /* "parser.y":211  */
                    { yyval = new ThisNode(); };
  break;


  case 69: /* constructor_invocation: TOK_TYPE_ID TOK_LPAR argument_list_opt TOK_RPAR  */
  if (yyn == 69)
    /* "parser.y":216  */
        { yyval = new ConstructorInvocationNode(((Token)yystack.valueAt (3)).getLexeme(), (List<ExpressionNode>)yystack.valueAt (1)); };
  break;


  case 70: /* method_invocation: TOK_ID TOK_LPAR argument_list_opt TOK_RPAR  */
  if (yyn == 70)
    /* "parser.y":221  */
        { yyval = new MethodInvocationNode(new IdentifierNode(((Token)yystack.valueAt (3)).getLexeme()), (List<ExpressionNode>)yystack.valueAt (1)); };
  break;


  case 71: /* argument_list_opt: %empty  */
  if (yyn == 71)
    /* "parser.y":224  */
                  { yyval = new ArrayList<ExpressionNode>(); };
  break;


  case 72: /* argument_list_opt: argument_list  */
  if (yyn == 72)
    /* "parser.y":225  */
                    { yyval = yystack.valueAt (0); };
  break;


  case 73: /* argument_list: expression  */
  if (yyn == 73)
    /* "parser.y":230  */
        { yyval = new ArrayList<ExpressionNode>(); ((List<ExpressionNode>)yyval).add((ExpressionNode)yystack.valueAt (0)); };
  break;


  case 74: /* argument_list: argument_list TOK_COMMA expression  */
  if (yyn == 74)
    /* "parser.y":232  */
        { ((List<ExpressionNode>)yystack.valueAt (2)).add((ExpressionNode)yystack.valueAt (0)); yyval = yystack.valueAt (2); };
  break;



/* "parser.java":1116  */

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

  private static final byte yypact_ninf_ = -85;
  private static final short yytable_ninf_ = -1;

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
  private static final byte[] yypact_ = yypact_init();
  private static final byte[] yypact_init()
  {
    return new byte[]
    {
      -2,     8,   -85,    24,     4,   -85,    21,   -85,   -85,   -85,
      12,    28,   -85,   -85,    65,   -85,    17,    27,    11,   -85,
     -85,   -85,    13,   -85,     3,    18,    36,    53,    31,   -85,
      38,    38,    38,    36,    39,    47,    40,   -85,    31,    31,
      31,    31,   -85,   -85,    61,    53,   -85,   -85,   -85,     5,
     -85,   -85,   -85,   -85,   -85,    49,    50,   -85,   -85,   -85,
      54,   -85,   -85,   -85,    51,    52,    50,    55,    54,    55,
      54,    55,    54,    56,    14,    76,    36,    -4,     0,   -85,
      54,    54,   -85,   -85,    31,    66,    31,    31,    68,    14,
      14,    31,   -85,   -85,   -85,    62,   -85,   -85,    53,   -85,
      53,    53,    54,   -85,    54,    58,    64,    60,    49,   -85,
      63,    67,    70,    73,   -85,    86,    88,    81,   -85,    31,
     -85,   -85,   -85,   -85,   -85,   -85,   -85,    53,    90,    54,
     -85,   -85
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
      11,    12,     0,    13,     0,     0,    29,    37,     0,    25,
       0,     0,     0,    29,     0,     0,    30,    31,     0,     0,
      56,     0,    49,    42,     0,    38,    39,    41,    43,     0,
      44,    45,    46,    47,    68,    64,     0,    65,    66,    67,
      28,    59,    60,    61,     0,     0,    22,    20,    19,    20,
      18,    20,    17,     0,     0,     0,     0,     0,     0,    55,
      57,    58,    27,    40,     0,     0,    71,    71,     0,     0,
       0,    71,    16,    15,    14,    34,    22,    33,    37,    32,
      37,    37,    48,    50,    73,     0,    72,     0,    63,    62,
       0,     0,     0,     0,    26,     0,     0,    53,    70,     0,
      69,    23,    24,    21,    35,    36,    51,    37,     0,    74,
      54,    52
    };
  }

/* YYPGOTO[NTERM-NUM].  */
  private static final byte[] yypgoto_ = yypgoto_init();
  private static final byte[] yypgoto_init()
  {
    return new byte[]
    {
     -85,   -85,   -85,    97,   -85,   -85,   -85,    89,   -32,   -26,
     -85,   -85,   -85,    69,   -85,    29,   -85,   -85,   -84,   -85,
      59,   -85,   -85,   -85,   -85,   -85,   -85,   -85,   -85,   -85,
     -28,   -85,   -85,    19,   -68,   -85
    };
  }

/* YYDEFGOTO[NTERM-NUM].  */
  private static final short[] yydefgoto_ = yydefgoto_init();
  private static final short[] yydefgoto_init()
  {
    return new short[]
    {
       0,     3,     4,     5,    11,    14,    19,    43,    92,    67,
      21,    22,    29,    35,    36,    37,   114,    23,    44,    45,
      46,    47,    48,    49,    50,    51,   128,    52,    79,    53,
     104,    61,    62,    63,   105,   106
    };
  }

/* YYTABLE[YYPACT[STATE-NUM]] -- What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule whose
   number is the opposite.  If YYTABLE_NINF, syntax error.  */
  private static final short[] yytable_ = yytable_init();
  private static final short[] yytable_init()
  {
    return new short[]
    {
      60,     1,    68,    70,    72,    69,    71,     1,    30,   100,
      77,    78,    80,    81,   115,   101,   116,   117,    27,   107,
      64,    65,    88,   112,     7,    10,    88,    31,     6,    84,
      32,    85,    12,    13,    96,     2,    24,    93,    28,    94,
      26,     8,    54,   130,    64,    65,    25,    33,    97,    54,
      55,    56,    57,    58,    59,    34,   102,    55,    66,    57,
      58,    59,    16,   110,   111,    38,    74,    39,    76,    82,
      40,    41,    42,    15,    16,    17,    18,    75,    86,    87,
      88,    98,    89,    90,    91,   103,    95,   108,   118,   113,
     120,   129,   119,   124,   125,   121,   126,   127,   131,   122,
     123,     9,    73,    20,    83,    99,     0,   109
    };
  }

private static final byte[] yycheck_ = yycheck_init();
  private static final byte[] yycheck_init()
  {
    return new byte[]
    {
      28,     3,    30,    31,    32,    31,    32,     3,     5,    13,
      38,    39,    40,    41,    98,    15,   100,   101,     5,    87,
       6,     7,    26,    91,     0,     4,    26,    24,    20,    24,
      27,    26,    20,     5,    20,    37,    19,    69,    25,    71,
      29,    37,    11,   127,     6,     7,    19,    29,    74,    11,
      19,    20,    21,    22,    23,    19,    84,    19,    20,    21,
      22,    23,     9,    89,    90,    12,    27,    14,    28,     8,
      17,    18,    19,     8,     9,    10,    11,    30,    29,    29,
      26,     5,    31,    31,    29,    19,    30,    19,    30,    27,
      30,   119,    28,    20,     8,    32,     8,    16,     8,    32,
      30,     4,    33,    14,    45,    76,    -1,    88
    };
  }

/* YYSTOS[STATE-NUM] -- The symbol kind of the accessing symbol of
   state STATE-NUM.  */
  private static final byte[] yystos_ = yystos_init();
  private static final byte[] yystos_init()
  {
    return new byte[]
    {
       0,     3,    37,    39,    40,    41,    20,     0,    37,    41,
       4,    42,    20,     5,    43,     8,     9,    10,    11,    44,
      45,    48,    49,    55,    19,    19,    29,     5,    25,    50,
       5,    24,    27,    29,    19,    51,    52,    53,    12,    14,
      17,    18,    19,    45,    56,    57,    58,    59,    60,    61,
      62,    63,    65,    67,    11,    19,    20,    21,    22,    23,
      68,    69,    70,    71,     6,     7,    20,    47,    68,    47,
      68,    47,    68,    51,    27,    30,    28,    68,    68,    66,
      68,    68,     8,    58,    24,    26,    29,    29,    26,    31,
      31,    29,    46,    46,    46,    30,    20,    47,     5,    53,
      13,    15,    68,    19,    68,    72,    73,    72,    19,    71,
      47,    47,    72,    27,    54,    56,    56,    56,    30,    28,
      30,    32,    32,    30,    20,     8,     8,    16,    64,    68,
      56,     8
    };
  }

/* YYR1[RULE-NUM] -- Symbol kind of the left-hand side of rule RULE-NUM.  */
  private static final byte[] yyr1_ = yyr1_init();
  private static final byte[] yyr1_init()
  {
    return new byte[]
    {
       0,    38,    39,    39,    40,    40,    41,    42,    42,    43,
      43,    44,    44,    44,    45,    45,    45,    45,    45,    45,
      46,    46,    47,    47,    47,    48,    49,    50,    50,    51,
      51,    52,    52,    53,    54,    54,    55,    56,    56,    57,
      57,    58,    58,    59,    59,    59,    59,    59,    60,    61,
      61,    62,    63,    64,    64,    65,    66,    66,    67,    68,
      68,    68,    68,    68,    69,    69,    69,    69,    69,    70,
      71,    72,    72,    73,    73
    };
  }

/* YYR2[RULE-NUM] -- Number of symbols on the right-hand side of rule RULE-NUM.  */
  private static final byte[] yyr2_ = yyr2_init();
  private static final byte[] yyr2_init()
  {
    return new byte[]
    {
       0,     2,     1,     2,     1,     2,     6,     0,     2,     0,
       2,     1,     1,     1,     5,     5,     5,     4,     4,     4,
       0,     3,     1,     4,     4,     2,     6,     3,     2,     0,
       1,     1,     3,     3,     0,     2,     7,     0,     1,     1,
       2,     1,     1,     1,     1,     1,     1,     1,     3,     1,
       3,     5,     6,     0,     2,     2,     0,     1,     2,     1,
       1,     1,     3,     3,     1,     1,     1,     1,     1,     4,
       4,     0,     1,     1,     3
    };
  }




  /* YYTRANSLATE_(TOKEN-NUM) -- Symbol number corresponding to TOKEN-NUM
     as returned by yylex, with out-of-bounds checking.  */
  private static final SymbolKind yytranslate_(int t)
  {
    // Last valid token kind.
    int code_max = 292;
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
      35,    36,    37
    };
  }


  private static final int YYLAST_ = 107;
  private static final int YYEMPTY_ = -2;
  private static final int YYFINAL_ = 7;
  private static final int YYNTOKENS_ = 38;


}
/* "parser.y":235  */

