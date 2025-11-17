.class public Main
.super java/lang/Object


.method public <init>()V
    .limit stack 128
    .limit locals 128
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public start()V
    .limit stack 128
    .limit locals 128
    new ConstructorExample
    dup
    invokespecial ConstructorExample/<init>()V
    astore_1
    new ConstructorExample
    dup
    bipush 10
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokespecial ConstructorExample/<init>(Ljava/lang/Integer;)V
    astore_2
    new ConstructorExample
    dup
    iconst_5
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    bipush 7
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokespecial ConstructorExample/<init>(Ljava/lang/Integer;Ljava/lang/Integer;)V
    astore_3
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload_1
    getfield ConstructorExample/a Ljava/lang/Integer;
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload_1
    getfield ConstructorExample/b Ljava/lang/Integer;
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload_2
    getfield ConstructorExample/a Ljava/lang/Integer;
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload_2
    getfield ConstructorExample/b Ljava/lang/Integer;
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload_3
    getfield ConstructorExample/a Ljava/lang/Integer;
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload_3
    getfield ConstructorExample/b Ljava/lang/Integer;
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

