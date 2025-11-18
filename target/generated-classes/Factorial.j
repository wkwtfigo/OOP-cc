.class public Factorial
.super java/lang/Object


.method public <init>()V
    .limit stack 16
    .limit locals 1
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public fact(Ljava/lang/Integer;)Ljava/lang/Integer;
    .limit stack 64
    .limit locals 64
    aload_1
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iconst_1
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    if_icmple int_le_true_2
    iconst_0
    goto int_le_end_3
int_le_true_2:
    iconst_1
int_le_end_3:
    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;
    checkcast java/lang/Boolean
    invokevirtual java/lang/Boolean/booleanValue()Z
    ifeq else_0
    iconst_1
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    areturn
    goto endif_1
else_0:
    aload_1
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    aload_0
    aload_1
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iconst_1
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    isub
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual Factorial/fact(Ljava/lang/Integer;)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    imul
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    areturn
endif_1:
    aconst_null
    areturn
.end method

