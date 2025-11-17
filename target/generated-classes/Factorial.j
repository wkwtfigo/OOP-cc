.class public Factorial
.super java/lang/Object


.method public <init>()V
    .limit stack 128
    .limit locals 128
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public fact(Ljava/lang/Integer;)Ljava/lang/Integer;
    .limit stack 128
    .limit locals 128
    aload_1
    checkcast java/lang/Number
    invokevirtual java/lang/Number/intValue()I
    iconst_1
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Number
    invokevirtual java/lang/Number/intValue()I
    if_icmple L3
    iconst_0
    goto L4
L3:
    iconst_1
L4:
    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;
    checkcast java/lang/Boolean
    invokevirtual java/lang/Boolean/booleanValue()Z
    ifne L0
    goto L1
L0:
    iconst_1
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    areturn
    goto L2
L1:
    aload_1
    checkcast java/lang/Number
    invokevirtual java/lang/Number/intValue()I
    aload_0
    aload_1
    checkcast java/lang/Number
    invokevirtual java/lang/Number/intValue()I
    iconst_1
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Number
    invokevirtual java/lang/Number/intValue()I
isub
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual Factorial/fact(Ljava/lang/Integer;)Ljava/lang/Integer;
    checkcast java/lang/Number
    invokevirtual java/lang/Number/intValue()I
imul
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    areturn
L2:
    aconst_null
    areturn
.end method

