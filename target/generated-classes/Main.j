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
    bipush 6
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
array_init_loop_34:
    iload 3
    iload 1
    if_icmpge array_init_end_35
    aload 2
    aconst_null
    invokevirtual java/util/ArrayList/add(Ljava/lang/Object;)Z
    pop
    iinc 3 1
    goto array_init_loop_34
array_init_end_35:
    aload 2
    astore 4
    aload 4
    checkcast java/util/ArrayList
    dup
    iconst_0
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    bipush 12
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual java/util/ArrayList/set(ILjava/lang/Object;)Ljava/lang/Object;
    pop
    astore 4
    aload 4
    checkcast java/util/ArrayList
    dup
    iconst_1
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    bipush 11
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual java/util/ArrayList/set(ILjava/lang/Object;)Ljava/lang/Object;
    pop
    astore 4
    aload 4
    checkcast java/util/ArrayList
    dup
    iconst_2
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    bipush 13
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual java/util/ArrayList/set(ILjava/lang/Object;)Ljava/lang/Object;
    pop
    astore 4
    aload 4
    checkcast java/util/ArrayList
    dup
    iconst_3
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iconst_5
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual java/util/ArrayList/set(ILjava/lang/Object;)Ljava/lang/Object;
    pop
    astore 4
    aload 4
    checkcast java/util/ArrayList
    dup
    iconst_4
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    bipush 7
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual java/util/ArrayList/set(ILjava/lang/Object;)Ljava/lang/Object;
    pop
    astore 4
    aload 4
    checkcast java/util/ArrayList
    dup
    iconst_5
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    bipush 6
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual java/util/ArrayList/set(ILjava/lang/Object;)Ljava/lang/Object;
    pop
    astore 4
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 4
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    new MergeSortAlgorithm
    dup
    invokespecial MergeSortAlgorithm/<init>()V
    astore 5
    aload 5
    aload 4
    invokevirtual MergeSortAlgorithm/sort(Ljava/util/ArrayList;)Ljava/util/ArrayList;
    astore 6
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 6
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

