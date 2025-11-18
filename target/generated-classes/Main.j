.class public Main
.super Base

.field public a Ljava/lang/Integer;

.method public <init>()V
    .limit stack 128
    .limit locals 128
    aload_0
    invokespecial Base/<init>()V
    aload_0
    iconst_0
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    putfield Main/a Ljava/lang/Integer;
    iconst_0
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    aload_0
    swap
    putfield Main/a Ljava/lang/Integer;
    return
.end method

.method public <init>(Ljava/lang/Integer;)V
    .limit stack 128
    .limit locals 128
    aload_0
    invokespecial Base/<init>()V
    aload_0
    iconst_0
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    putfield Main/a Ljava/lang/Integer;
    aload_1
    aload_0
    swap
    putfield Main/a Ljava/lang/Integer;
    return
.end method

.method public start()V
    .limit stack 128
    .limit locals 128
    new Base
    dup
    invokespecial Base/<init>()V
    astore_1
    bipush 45
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    astore_2
    new Main
    dup
    aload_2
    invokespecial Main/<init>(Ljava/lang/Integer;)V
    astore_3
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload_3
    invokevirtual Main/whoAmI()Ljava/lang/Integer;
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    aload_1
    invokevirtual Base/whoAmI()Ljava/lang/Integer;
    astore 4
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

