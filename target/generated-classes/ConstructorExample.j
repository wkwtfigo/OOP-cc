.class public ConstructorExample
.super java/lang/Object

.field public a Ljava/lang/Integer;
.field public b Ljava/lang/Integer;

.method public <init>()V
    .limit stack 128
    .limit locals 128
    aload_0
    invokespecial java/lang/Object/<init>()V
    aload_0
    iconst_0
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Number
    invokevirtual java/lang/Number/intValue()I
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    putfield ConstructorExample/a Ljava/lang/Integer;
    aload_0
    iconst_0
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Number
    invokevirtual java/lang/Number/intValue()I
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    putfield ConstructorExample/b Ljava/lang/Integer;
    iconst_2
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    aload_0
    swap
    putfield ConstructorExample/b Ljava/lang/Integer;
    return
.end method

.method public <init>(Ljava/lang/Integer;)V
    .limit stack 128
    .limit locals 128
    aload_0
    invokespecial java/lang/Object/<init>()V
    aload_0
    iconst_0
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Number
    invokevirtual java/lang/Number/intValue()I
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    putfield ConstructorExample/a Ljava/lang/Integer;
    aload_0
    iconst_0
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Number
    invokevirtual java/lang/Number/intValue()I
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    putfield ConstructorExample/b Ljava/lang/Integer;
    aload_1
    aload_0
    swap
    putfield ConstructorExample/a Ljava/lang/Integer;
    iconst_1
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    aload_0
    swap
    putfield ConstructorExample/b Ljava/lang/Integer;
    return
.end method

.method public <init>(Ljava/lang/Integer;Ljava/lang/Integer;)V
    .limit stack 128
    .limit locals 128
    aload_0
    invokespecial java/lang/Object/<init>()V
    aload_0
    iconst_0
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Number
    invokevirtual java/lang/Number/intValue()I
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    putfield ConstructorExample/a Ljava/lang/Integer;
    aload_0
    iconst_0
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Number
    invokevirtual java/lang/Number/intValue()I
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    putfield ConstructorExample/b Ljava/lang/Integer;
    aload_1
    aload_0
    swap
    putfield ConstructorExample/a Ljava/lang/Integer;
    aload_2
    aload_0
    swap
    putfield ConstructorExample/b Ljava/lang/Integer;
    return
.end method

