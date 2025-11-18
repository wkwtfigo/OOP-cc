.class public Base
.super java/lang/Object


.method public <init>()V
    .limit stack 128
    .limit locals 128
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public whoAmI()Ljava/lang/Integer;
    .limit stack 128
    .limit locals 128
    iconst_1
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    areturn
.end method

