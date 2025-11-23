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
    iconst_3
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
    astore 4
    aload 4
    checkcast java/util/ArrayList
    dup
    iconst_0
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    bipush 10
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
    bipush 20
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
    bipush 30
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual java/util/ArrayList/set(ILjava/lang/Object;)Ljava/lang/Object;
    pop
    astore 4
    aload 4
    astore 5
    aload 5
    iconst_0
    invokevirtual java/util/ArrayList/get(I)Ljava/lang/Object;
    astore 6
    aload 5
    dup
    invokevirtual java/util/ArrayList/size()I
    iconst_1
    swap
    invokevirtual java/util/ArrayList/subList(II)Ljava/util/List;
    astore 7
    new java/util/ArrayList
    dup
    aload 7
    invokespecial java/util/ArrayList/<init>(Ljava/util/Collection;)V
    iconst_0
    invokevirtual java/util/ArrayList/get(I)Ljava/lang/Object;
    astore 8
    aload 5
    dup
    invokevirtual java/util/ArrayList/size()I
    iconst_1
    swap
    invokevirtual java/util/ArrayList/subList(II)Ljava/util/List;
    astore 9
    new java/util/ArrayList
    dup
    aload 9
    invokespecial java/util/ArrayList/<init>(Ljava/util/Collection;)V
    dup
    invokevirtual java/util/ArrayList/size()I
    iconst_1
    swap
    invokevirtual java/util/ArrayList/subList(II)Ljava/util/List;
    astore 10
    new java/util/ArrayList
    dup
    aload 10
    invokespecial java/util/ArrayList/<init>(Ljava/util/Collection;)V
    iconst_0
    invokevirtual java/util/ArrayList/get(I)Ljava/lang/Object;
    astore 11
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 6
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 8
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 11
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    aload 5
    dup
    bipush 40
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual java/util/ArrayList/add(Ljava/lang/Object;)Z
    pop
    astore 5
    aload 5
    dup
    invokevirtual java/util/ArrayList/size()I
    iconst_1
    swap
    invokevirtual java/util/ArrayList/subList(II)Ljava/util/List;
    astore 12
    new java/util/ArrayList
    dup
    aload 12
    invokespecial java/util/ArrayList/<init>(Ljava/util/Collection;)V
    dup
    invokevirtual java/util/ArrayList/size()I
    iconst_1
    swap
    invokevirtual java/util/ArrayList/subList(II)Ljava/util/List;
    astore 13
    new java/util/ArrayList
    dup
    aload 13
    invokespecial java/util/ArrayList/<init>(Ljava/util/Collection;)V
    dup
    invokevirtual java/util/ArrayList/size()I
    iconst_1
    swap
    invokevirtual java/util/ArrayList/subList(II)Ljava/util/List;
    astore 14
    new java/util/ArrayList
    dup
    aload 14
    invokespecial java/util/ArrayList/<init>(Ljava/util/Collection;)V
    iconst_0
    invokevirtual java/util/ArrayList/get(I)Ljava/lang/Object;
    astore 15
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 15
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

