.class public Test
.super java/lang/Object

.field public l Ljava/lang/Integer;

.method public <init>()V
    .limit stack 64
    .limit locals 64
    aload_0
    invokespecial java/lang/Object/<init>()V
    aload_0
    bipush 90
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    putfield Test/l Ljava/lang/Integer;
    aload_0
    getfield Test/l Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iconst_3
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    imul
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    aload_0
    swap
    putfield Test/l Ljava/lang/Integer;
    return
.end method

