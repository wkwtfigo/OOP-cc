.class public MergeSortAlgorithm
.super java/lang/Object


.method public <init>()V
    .limit stack 16
    .limit locals 1
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public merge(Ljava/util/ArrayList;Ljava/util/ArrayList;)Ljava/util/ArrayList;
    .limit stack 64
    .limit locals 64
    aload_1
    checkcast java/util/ArrayList
    invokevirtual java/util/ArrayList/size()I
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    aload_2
    checkcast java/util/ArrayList
    invokevirtual java/util/ArrayList/size()I
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iadd
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    astore 3
    aload_3
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    istore 4
    new java/util/ArrayList
    dup
    invokespecial java/util/ArrayList/<init>()V
    astore 5
    iconst_0
    istore 6
array_init_loop_0:
    iload 6
    iload 4
    if_icmpge array_init_end_1
    aload 5
    aconst_null
    invokevirtual java/util/ArrayList/add(Ljava/lang/Object;)Z
    pop
    iinc 6 1
    goto array_init_loop_0
array_init_end_1:
    aload 5
    astore 7
    iconst_0
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    astore 8
while_begin_2:
    aload 8
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    aload_3
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iconst_1
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    isub
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    if_icmplt int_lt_true_4
    iconst_0
    goto int_lt_end_5
int_lt_true_4:
    iconst_1
int_lt_end_5:
    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;
    checkcast java/lang/Boolean
    invokevirtual java/lang/Boolean/booleanValue()Z
    ifeq while_end_3
    aload 7
    checkcast java/util/ArrayList
    dup
    aload 8
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iconst_0
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    invokevirtual java/util/ArrayList/set(ILjava/lang/Object;)Ljava/lang/Object;
    pop
    astore 7
    aload 8
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iconst_1
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iadd
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    astore 8
    goto while_begin_2
while_end_3:
    iconst_0
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    astore 9
    iconst_0
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    astore 10
while_begin_6:
    aload 9
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    aload_1
    checkcast java/util/ArrayList
    invokevirtual java/util/ArrayList/size()I
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    if_icmplt int_lt_true_8
    iconst_0
    goto int_lt_end_9
int_lt_true_8:
    iconst_1
int_lt_end_9:
    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;
    checkcast java/lang/Boolean
    invokevirtual java/lang/Boolean/booleanValue()Z
    aload 10
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    aload_2
    checkcast java/util/ArrayList
    invokevirtual java/util/ArrayList/size()I
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    if_icmplt int_lt_true_10
    iconst_0
    goto int_lt_end_11
int_lt_true_10:
    iconst_1
int_lt_end_11:
    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;
    checkcast java/lang/Boolean
    invokevirtual java/lang/Boolean/booleanValue()Z
    iand
    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;
    checkcast java/lang/Boolean
    invokevirtual java/lang/Boolean/booleanValue()Z
    ifeq while_end_7
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload_1
    checkcast java/util/ArrayList
    aload 9
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    invokevirtual java/util/ArrayList/get(I)Ljava/lang/Object;
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload_2
    checkcast java/util/ArrayList
    aload 10
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    invokevirtual java/util/ArrayList/get(I)Ljava/lang/Object;
    invokevirtual java/io/PrintStream/println(Ljava/lang/Object;)V
    aload_1
    checkcast java/util/ArrayList
    aload 9
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    invokevirtual java/util/ArrayList/get(I)Ljava/lang/Object;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    aload_2
    checkcast java/util/ArrayList
    aload 10
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    invokevirtual java/util/ArrayList/get(I)Ljava/lang/Object;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    if_icmplt int_lt_true_14
    iconst_0
    goto int_lt_end_15
int_lt_true_14:
    iconst_1
int_lt_end_15:
    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;
    checkcast java/lang/Boolean
    invokevirtual java/lang/Boolean/booleanValue()Z
    ifeq else_12
    aload 7
    checkcast java/util/ArrayList
    dup
    aload 9
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    aload_1
    checkcast java/util/ArrayList
    aload 9
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    invokevirtual java/util/ArrayList/get(I)Ljava/lang/Object;
    invokevirtual java/util/ArrayList/set(ILjava/lang/Object;)Ljava/lang/Object;
    pop
    astore 7
    aload 9
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iconst_1
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iadd
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    astore 9
    goto endif_13
else_12:
    aload 7
    checkcast java/util/ArrayList
    dup
    aload 9
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    aload_2
    checkcast java/util/ArrayList
    aload 10
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    invokevirtual java/util/ArrayList/get(I)Ljava/lang/Object;
    invokevirtual java/util/ArrayList/set(ILjava/lang/Object;)Ljava/lang/Object;
    pop
    astore 7
    aload 10
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iconst_1
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iadd
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    astore 10
endif_13:
    goto while_begin_6
while_end_7:
while_begin_16:
    aload 9
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    aload_1
    checkcast java/util/ArrayList
    invokevirtual java/util/ArrayList/size()I
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    if_icmplt int_lt_true_18
    iconst_0
    goto int_lt_end_19
int_lt_true_18:
    iconst_1
int_lt_end_19:
    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;
    checkcast java/lang/Boolean
    invokevirtual java/lang/Boolean/booleanValue()Z
    ifeq while_end_17
    aload 7
    checkcast java/util/ArrayList
    dup
    aload 9
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    aload_1
    checkcast java/util/ArrayList
    aload 9
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    invokevirtual java/util/ArrayList/get(I)Ljava/lang/Object;
    invokevirtual java/util/ArrayList/set(ILjava/lang/Object;)Ljava/lang/Object;
    pop
    astore 7
    aload 9
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iconst_1
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iadd
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    astore 9
    goto while_begin_16
while_end_17:
while_begin_20:
    aload 10
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    aload_2
    checkcast java/util/ArrayList
    invokevirtual java/util/ArrayList/size()I
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    if_icmplt int_lt_true_22
    iconst_0
    goto int_lt_end_23
int_lt_true_22:
    iconst_1
int_lt_end_23:
    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;
    checkcast java/lang/Boolean
    invokevirtual java/lang/Boolean/booleanValue()Z
    ifeq while_end_21
    aload 7
    checkcast java/util/ArrayList
    dup
    aload 9
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    aload_2
    checkcast java/util/ArrayList
    aload 10
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    invokevirtual java/util/ArrayList/get(I)Ljava/lang/Object;
    invokevirtual java/util/ArrayList/set(ILjava/lang/Object;)Ljava/lang/Object;
    pop
    astore 7
    aload 10
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iconst_1
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iadd
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    astore 10
    goto while_begin_20
while_end_21:
    aload 7
    areturn
    aconst_null
    areturn
.end method

.method public sort(Ljava/util/ArrayList;)Ljava/util/ArrayList;
    .limit stack 64
    .limit locals 64
    aload_1
    checkcast java/util/ArrayList
    invokevirtual java/util/ArrayList/size()I
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iconst_1
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    if_icmple int_le_true_26
    iconst_0
    goto int_le_end_27
int_le_true_26:
    iconst_1
int_le_end_27:
    invokestatic java/lang/Boolean/valueOf(Z)Ljava/lang/Boolean;
    checkcast java/lang/Boolean
    invokevirtual java/lang/Boolean/booleanValue()Z
    ifeq else_24
    aload_1
    areturn
    goto endif_25
else_24:
endif_25:
    aload_1
    checkcast java/util/ArrayList
    invokevirtual java/util/ArrayList/size()I
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    iconst_2
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    idiv
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    astore 2
    aload_1
    checkcast java/util/ArrayList
    astore_3
    aload_3
    iconst_0
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    aload_2
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    invokevirtual java/util/ArrayList/subList(II)Ljava/util/List;
    astore 4
    new java/util/ArrayList
    dup
    aload 4
    invokespecial java/util/ArrayList/<init>(Ljava/util/Collection;)V
    astore 5
    aload 5
    invokevirtual java/util/ArrayList/size()I
    iconst_1
    isub
    istore 6
subarray_trim_loop_28:
    iload 6
    iflt subarray_trim_end_29
    aload 5
    dup
    iload 6
    invokevirtual java/util/ArrayList/get(I)Ljava/lang/Object;
    dup
    ifnonnull subarray_trim_keep_30
    pop
    iload 6
    invokevirtual java/util/ArrayList/remove(I)Ljava/lang/Object;
    pop
    iinc 6 -1
    goto subarray_trim_loop_28
subarray_trim_keep_30:
    pop
    pop
    goto subarray_trim_end_29
subarray_trim_end_29:
    aload 5
    astore 7
    aload_1
    checkcast java/util/ArrayList
    astore 8
    aload 8
    aload_2
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    aload_1
    checkcast java/util/ArrayList
    invokevirtual java/util/ArrayList/size()I
    invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;
    checkcast java/lang/Integer
    invokevirtual java/lang/Integer/intValue()I
    invokevirtual java/util/ArrayList/subList(II)Ljava/util/List;
    astore 9
    new java/util/ArrayList
    dup
    aload 9
    invokespecial java/util/ArrayList/<init>(Ljava/util/Collection;)V
    astore 10
    aload 10
    invokevirtual java/util/ArrayList/size()I
    iconst_1
    isub
    istore 11
subarray_trim_loop_31:
    iload 11
    iflt subarray_trim_end_32
    aload 10
    dup
    iload 11
    invokevirtual java/util/ArrayList/get(I)Ljava/lang/Object;
    dup
    ifnonnull subarray_trim_keep_33
    pop
    iload 11
    invokevirtual java/util/ArrayList/remove(I)Ljava/lang/Object;
    pop
    iinc 11 -1
    goto subarray_trim_loop_31
subarray_trim_keep_33:
    pop
    pop
    goto subarray_trim_end_32
subarray_trim_end_32:
    aload 10
    astore 12
    aload_0
    aload 7
    invokevirtual MergeSortAlgorithm/sort(Ljava/util/ArrayList;)Ljava/util/ArrayList;
    astore 7
    aload_0
    aload 12
    invokevirtual MergeSortAlgorithm/sort(Ljava/util/ArrayList;)Ljava/util/ArrayList;
    astore 12
    aload_0
    aload 7
    aload 12
    invokevirtual MergeSortAlgorithm/merge(Ljava/util/ArrayList;Ljava/util/ArrayList;)Ljava/util/ArrayList;
    areturn
    aconst_null
    areturn
.end method

