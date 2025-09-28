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
    S_type_expression(44),         /* type_expression  */
    S_method_declaration(45),      /* method_declaration  */
    S_method_header(46),           /* method_header  */
    S_optional_method_body(47),    /* optional_method_body  */
    S_method_body(48),             /* method_body  */
    S_parameter_list_opt(49),      /* parameter_list_opt  */
    S_parameter_list(50),          /* parameter_list  */
    S_parameter_declaration(51),   /* parameter_declaration  */
    S_optional_return_type(52),    /* optional_return_type  */
    S_constructor_declaration(53), /* constructor_declaration  */
    S_body(54),                    /* body  */
    S_body_element_list(55),       /* body_element_list  */
    S_body_element(56),            /* body_element  */
    S_statement(57),               /* statement  */
    S_assignment(58),              /* assignment  */
    S_while_loop(59),              /* while_loop  */
    S_if_statement(60),            /* if_statement  */
    S_optional_else(61),           /* optional_else  */
    S_return_statement(62),        /* return_statement  */
    S_return_expression_opt(63),   /* return_expression_opt  */
    S_print_statement(64),         /* print_statement  */
    S_expression(65),              /* expression  */
    S_primary(66),                 /* primary  */
    S_constructor_invocation(67),  /* constructor_invocation  */
    S_method_invocation(68),       /* method_invocation  */
    S_argument_list_opt(69),       /* argument_list_opt  */
    S_argument_list(70);           /* argument_list  */


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
      SymbolKind.S_type_expression,
      SymbolKind.S_method_declaration,
      SymbolKind.S_method_header,
      SymbolKind.S_optional_method_body,
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
  "type_expression", "method_declaration", "method_header",
  "optional_method_body", "method_body", "parameter_list_opt",
  "parameter_list", "parameter_declaration", "optional_return_type",
  "constructor_declaration", "body", "body_element_list", "body_element",
  "statement", "assignment", "while_loop", "if_statement", "optional_else",
  "return_statement", "return_expression_opt", "print_statement",
  "expression", "primary", "constructor_invocation", "method_invocation",
  "argument_list_opt", "argument_list", null
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
        
/* "parser.java":569  */

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
          return true;

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





  /**
   * Build and emit a "syntax error" message in a user-defined way.
   *
   * @param ctx  The context of the error.
   */
  private void yyreportSyntaxError(Context yyctx) {
      yyerror("syntax error");
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

  private static final byte yypact_ninf_ = -81;
  private static final byte yytable_ninf_ = -1;

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
  private static final byte[] yypact_ = yypact_init();
  private static final byte[] yypact_init()
  {
    return new byte[]
    {
     -81,     8,    -2,   -81,    -4,   -81,   -81,    13,     2,    17,
     -81,   -81,    34,   -81,    14,    18,     3,   -81,   -81,   -81,
       0,   -81,    11,    27,    27,    22,     7,   -81,   -81,    29,
      20,    23,    24,   -81,    21,     7,     7,     7,     7,    30,
     -81,    45,    22,   -81,   -81,   -81,   -81,   -81,   -81,   -81,
     -81,    26,    28,   -81,   -81,   -81,    32,   -81,   -81,   -81,
      31,   -81,    36,    39,   -81,    27,    54,    -5,    -6,   -81,
      32,    32,     7,   -81,   -81,     7,     7,    43,     7,   -81,
     -81,   -81,    22,    22,    22,    32,    32,    33,    37,    38,
     -81,    40,    56,    58,    51,   -81,     7,   -81,   -81,   -81,
     -81,    22,    61,    32,   -81,   -81
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
       3,     0,     0,     1,     0,     2,     4,     6,     0,     0,
       7,     8,     0,     5,     0,     0,     0,     9,    10,    11,
      18,    12,     0,    22,    22,    30,     0,    16,    19,     0,
       0,    27,    23,    24,     0,     0,     0,    47,     0,     0,
      35,     0,    31,    32,    34,    36,    37,    38,    39,    40,
      57,     0,     0,    54,    55,    56,    21,    50,    51,    52,
      14,    13,     0,     0,    17,     0,     0,     0,     0,    46,
      48,    49,     0,    20,    33,    60,    60,     0,    60,    26,
      28,    25,    30,    30,    30,    41,    62,     0,    61,     0,
      53,     0,     0,     0,    44,    59,     0,    58,    15,    29,
      42,    30,     0,    63,    45,    43
    };
  }

/* YYPGOTO[NTERM-NUM].  */
  private static final byte[] yypgoto_ = yypgoto_init();
  private static final byte[] yypgoto_init()
  {
    return new byte[]
    {
     -81,   -81,   -81,   -81,   -81,   -81,   -81,    57,   -81,   -81,
     -81,   -81,   -81,    47,   -81,     9,   -81,   -81,   -80,   -81,
      35,   -81,   -81,   -81,   -81,   -81,   -81,   -81,   -81,   -26,
     -81,   -81,    -1,   -63,   -81
    };
  }

/* YYDEFGOTO[NTERM-NUM].  */
  private static final byte[] yydefgoto_ = yydefgoto_init();
  private static final byte[] yydefgoto_init()
  {
    return new byte[]
    {
       0,     1,     2,     6,     9,    12,    17,    40,    61,    19,
      20,    27,    28,    31,    32,    33,    64,    21,    41,    42,
      43,    44,    45,    46,    47,   102,    48,    69,    49,    86,
      57,    58,    59,    87,    88
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
      56,     4,    92,    93,    94,    25,    83,    84,     3,    67,
      68,    70,    71,    89,     7,    91,    50,     8,    77,    77,
      10,   104,    11,    26,    51,    52,    53,    54,    55,    14,
      24,    22,    35,     5,    36,    23,    29,    37,    38,    39,
      13,    14,    15,    16,    30,    62,    85,    60,    63,    66,
      65,    73,    72,    75,    79,    76,    77,    80,    78,    82,
      51,    95,    99,    96,   100,   101,    97,   105,    98,    18,
     103,    34,     0,     0,    81,     0,    90,    74
    };
  }

private static final byte[] yycheck_ = yycheck_init();
  private static final byte[] yycheck_init()
  {
    return new byte[]
    {
      26,     3,    82,    83,    84,     5,    11,    13,     0,    35,
      36,    37,    38,    76,    18,    78,     9,     4,    24,    24,
      18,   101,     5,    23,    17,    18,    19,    20,    21,     7,
      27,    17,    10,    35,    12,    17,    25,    15,    16,    17,
       6,     7,     8,     9,    17,    25,    72,    18,    25,    28,
      26,     6,    22,    27,    18,    27,    24,    18,    27,     5,
      17,    28,     6,    26,     6,    14,    28,     6,    28,    12,
      96,    24,    -1,    -1,    65,    -1,    77,    42
    };
  }

/* YYSTOS[STATE-NUM] -- The symbol kind of the accessing symbol of
   state STATE-NUM.  */
  private static final byte[] yystos_ = yystos_init();
  private static final byte[] yystos_init()
  {
    return new byte[]
    {
       0,    37,    38,     0,     3,    35,    39,    18,     4,    40,
      18,     5,    41,     6,     7,     8,     9,    42,    43,    45,
      46,    53,    17,    17,    27,     5,    23,    47,    48,    25,
      17,    49,    50,    51,    49,    10,    12,    15,    16,    17,
      43,    54,    55,    56,    57,    58,    59,    60,    62,    64,
       9,    17,    18,    19,    20,    21,    65,    66,    67,    68,
      18,    44,    25,    25,    52,    26,    28,    65,    65,    63,
      65,    65,    22,     6,    56,    27,    27,    24,    27,    18,
      18,    51,     5,    11,    13,    65,    65,    69,    70,    69,
      68,    69,    54,    54,    54,    28,    26,    28,    28,     6,
       6,    14,    61,    65,    54,     6
    };
  }

/* YYR1[RULE-NUM] -- Symbol kind of the left-hand side of rule RULE-NUM.  */
  private static final byte[] yyr1_ = yyr1_init();
  private static final byte[] yyr1_init()
  {
    return new byte[]
    {
       0,    36,    37,    38,    38,    39,    40,    40,    41,    41,
      42,    42,    42,    43,    44,    44,    45,    46,    47,    47,
      48,    48,    49,    49,    50,    50,    51,    52,    52,    53,
      54,    54,    55,    55,    56,    56,    57,    57,    57,    57,
      57,    58,    59,    60,    61,    61,    62,    63,    63,    64,
      65,    65,    65,    65,    66,    66,    66,    66,    67,    68,
      69,    69,    70,    70
    };
  }

/* YYR2[RULE-NUM] -- Number of symbols on the right-hand side of rule RULE-NUM.  */
  private static final byte[] yyr2_ = yyr2_init();
  private static final byte[] yyr2_init()
  {
    return new byte[]
    {
       0,     2,     2,     0,     2,     6,     0,     2,     0,     2,
       1,     1,     1,     4,     1,     4,     2,     4,     0,     1,
       3,     2,     0,     1,     1,     3,     3,     0,     2,     7,
       0,     1,     1,     2,     1,     1,     1,     1,     1,     1,
       1,     3,     5,     6,     0,     2,     2,     0,     1,     2,
       1,     1,     1,     3,     1,     1,     1,     1,     4,     4,
       0,     1,     1,     3
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


  private static final int YYLAST_ = 77;
  private static final int YYEMPTY_ = -2;
  private static final int YYFINAL_ = 3;
  private static final int YYNTOKENS_ = 36;


}
/* "parser.y":192  */

