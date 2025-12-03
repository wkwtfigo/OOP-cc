.class public Main
.super java/lang/Object

.field public arr Ljava/util/ArrayList;
.field public lst Ljava/util/ArrayList;

.method public <init>()V
    .limit stack 16
    .limit locals 1
    aload_0
    invokespecial java/lang/Object/<init>()V
    aload_0
    new java/util/ArrayList
    dup
    invokespecial java/util/ArrayList/<init>()V
    dup
    bipush 12
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual java/util/ArrayList/add(Ljava/lang/Object;)Z
    pop
    dup
    bipush 23
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual java/util/ArrayList/add(Ljava/lang/Object;)Z
    pop
    dup
    ldc2_w 0.1
    invokestatic java/lang/Double/valueOf(D)Ljava/lang/Double;
    invokevirtual java/util/ArrayList/add(Ljava/lang/Object;)Z
    pop
    putfield Main/lst Ljava/util/ArrayList;
    aload_0
    iconst_2
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    istore 1
    new java/util/ArrayList
    dup
    invokespecial java/util/ArrayList/<init>()V
    astore 2
    iconst_0
    istore 3
array_init_loop_0:
    iload 3
    iload 1
    if_icmpge array_init_end_1
    aload 2
    aconst_null
    invokevirtual java/util/ArrayList/add(Ljava/lang/Object;)Z
    pop
    iinc 3 1
    goto array_init_loop_0
array_init_end_1:
    aload 2
    putfield Main/arr Ljava/util/ArrayList;
    return
.end method

.method public f(Ljava/lang/Integer;)Ljava/lang/Integer;
    .limit stack 64
    .limit locals 64
    aconst_null
    areturn
.end method

.method public f(Ljava/lang/Double;)Ljava/lang/Double;
    .limit stack 64
    .limit locals 64
    aload_1
    dup
    instanceof java/lang/Integer
    ifne real_from_int_2
    checkcast java/lang/Double
    invokevirtual java/lang/Double/doubleValue()D
    goto real_from_end_3
real_from_int_2:
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    i2d
real_from_end_3:
    ldc2_w 0.5
    invokestatic java/lang/Double/valueOf(D)Ljava/lang/Double;
    dup
    instanceof java/lang/Integer
    ifne real_from_int_4
    checkcast java/lang/Double
    invokevirtual java/lang/Double/doubleValue()D
    goto real_from_end_5
real_from_int_4:
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    i2d
real_from_end_5:
    dadd
    invokestatic java/lang/Double/valueOf(D)Ljava/lang/Double;
    areturn
.end method

.method public f(LTest;)Ljava/lang/Boolean;
    .limit stack 64
    .limit locals 64
    aload_1
    getfield Test/l Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    ifne int_to_bool_true_6
    iconst_0
    goto int_to_bool_end_7
int_to_bool_true_6:
    iconst_1
int_to_bool_end_7:
    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;
    areturn
.end method

.method public start()V
    .limit stack 64
    .limit locals 64
    iconst_5
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    astore 1
    new Test
    dup
    invokespecial Test/<init>()V
    astore 2
    aload_1
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iconst_3
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iadd
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    astore 3
    aload_0
    ldc2_w 3.4
    invokestatic java/lang/Double/valueOf(D)Ljava/lang/Double;
    invokevirtual Main/f(Ljava/lang/Double;)Ljava/lang/Double;
    astore 4
    aload_0
    iconst_5
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual Main/f(Ljava/lang/Integer;)Ljava/lang/Integer;
    astore 5
    bipush 21
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    astore 6
    aload_0
    aload_2
    invokevirtual Main/f(LTest;)Ljava/lang/Boolean;
    astore 7
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 4
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 5
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload_3
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 7
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 6
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload_0
    getfield Main/lst Ljava/util/ArrayList;
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    return
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

