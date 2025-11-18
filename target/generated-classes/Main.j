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
    new Factorial
    dup
    invokespecial Factorial/<init>()V
    astore 1
    aload_0
    iconst_5
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual Main/fact(Ljava/lang/Object;)Ljava/lang/Object;
    astore 2
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload_2
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

