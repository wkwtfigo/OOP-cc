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
    new Factorial
    dup
    invokespecial Factorial/<init>()V
    astore_1
    aload_1
    iconst_5
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual Factorial/fact(Ljava/lang/Integer;)Ljava/lang/Integer;
    astore_2
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

