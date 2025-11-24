.class public Main
.super java/lang/Object


.method public <init>()V
    .limit stack 16
    .limit locals 1
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public start()V
    .limit stack 64
    .limit locals 64
    new java/util/ArrayList
    dup
    invokespecial java/util/ArrayList/<init>()V
    dup
    iconst_5
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual java/util/ArrayList/add(Ljava/lang/Object;)Z
    pop
    astore 1
    aload_1
    dup
    bipush 10
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual java/util/ArrayList/add(Ljava/lang/Object;)Z
    pop
    astore_1
    aload_0
    iconst_3
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual Main/makeArray(Ljava/lang/Integer;)Ljava/util/ArrayList;
    astore 2
    aload_0
    aload_1
    aload_2
    invokevirtual Main/combine(Ljava/util/ArrayList;Ljava/util/ArrayList;)Ljava/lang/Integer;
    astore 3
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload_3
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    return
.end method

.method public makeArray(Ljava/lang/Integer;)Ljava/util/ArrayList;
    .limit stack 64
    .limit locals 64
    aload_1
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    istore 2
    new java/util/ArrayList
    dup
    invokespecial java/util/ArrayList/<init>()V
    astore 3
    iconst_0
    istore 4
array_init_loop_0:
    iload 4
    iload 2
    if_icmpge array_init_end_1
    aload 3
    aconst_null
    invokevirtual java/util/ArrayList/add(Ljava/lang/Object;)Z
    pop
    iinc 4 1
    goto array_init_loop_0
array_init_end_1:
    aload 3
    astore 5
    iconst_0
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    astore 6
while_begin_2:
    aload 6
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    aload 5
    checkcast java/util/ArrayList
    invokevirtual java/util/ArrayList/size()I
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    if_icmplt int_lt_true_4
    iconst_0
    goto int_lt_end_5
int_lt_true_4:
    iconst_1
int_lt_end_5:
    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;
    checkcast java/lang/Boolean
    invokevirtual java/lang/Boolean/booleanValue()Z
    ifeq while_end_3
    aload 6
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iconst_1
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iadd
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    astore 7
    aload 5
    checkcast java/util/ArrayList
    dup
    aload 6
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    aload 7
    invokevirtual java/util/ArrayList/set(ILjava/lang/Object;)Ljava/lang/Object;
    pop
    astore 5
    aload 6
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iconst_1
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iadd
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    astore 6
    goto while_begin_2
while_end_3:
    aload 5
    areturn
    aconst_null
    areturn
.end method

.method public combine(Ljava/util/ArrayList;Ljava/util/ArrayList;)Ljava/lang/Integer;
    .limit stack 64
    .limit locals 64
    aload_1
    iconst_0
    invokevirtual java/util/ArrayList/get(I)Ljava/lang/Object;
    astore 3
    aload_2
    checkcast java/util/ArrayList
    invokevirtual java/util/ArrayList/size()I
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    astore 4
    aload_3
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    aload 4
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iadd
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    areturn
    aconst_null
    areturn
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 4
    .limit locals 1
    new Main
    dup
    invokespecial Main/<init>()V
    invokevirtual Main/start()V
    return
.end method

