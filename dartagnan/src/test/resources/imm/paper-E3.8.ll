; ModuleID = '/home/ponce/git/Dat3M/output/paper-E3.8.ll'
source_filename = "/home/ponce/git/Dat3M/benchmarks/imm/paper-E3.8.c"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

%union.pthread_attr_t = type { i64, [48 x i8] }

@x = dso_local global i32 0, align 4, !dbg !0
@y = dso_local global i32 0, align 4, !dbg !18
@a = dso_local global i32 0, align 4, !dbg !24
@b = dso_local global i32 0, align 4, !dbg !26
@c = dso_local global i32 0, align 4, !dbg !28
@d = dso_local global i32 0, align 4, !dbg !30
@.str = private unnamed_addr constant [40 x i8] c"!(a == 1 && b == 0 && c == 1 && d == 0)\00", align 1
@.str.1 = private unnamed_addr constant [50 x i8] c"/home/ponce/git/Dat3M/benchmarks/imm/paper-E3.8.c\00", align 1
@__PRETTY_FUNCTION__.main = private unnamed_addr constant [11 x i8] c"int main()\00", align 1

; Function Attrs: noinline nounwind uwtable
define dso_local i8* @thread_1(i8* noundef %0) #0 !dbg !40 {
  call void @llvm.dbg.value(metadata i8* %0, metadata !44, metadata !DIExpression()), !dbg !45
  %2 = load atomic i32, i32* @x acquire, align 4, !dbg !46
  call void @llvm.dbg.value(metadata i32 %2, metadata !47, metadata !DIExpression()), !dbg !45
  fence seq_cst, !dbg !48
  %3 = load atomic i32, i32* @y acquire, align 4, !dbg !49
  call void @llvm.dbg.value(metadata i32 %3, metadata !50, metadata !DIExpression()), !dbg !45
  fence seq_cst, !dbg !51
  store i32 %2, i32* @a, align 4, !dbg !52
  store i32 %3, i32* @b, align 4, !dbg !53
  ret i8* null, !dbg !54
}

; Function Attrs: nofree nosync nounwind readnone speculatable willreturn
declare void @llvm.dbg.declare(metadata, metadata, metadata) #1

; Function Attrs: noinline nounwind uwtable
define dso_local i8* @thread_2(i8* noundef %0) #0 !dbg !55 {
  call void @llvm.dbg.value(metadata i8* %0, metadata !56, metadata !DIExpression()), !dbg !57
  store atomic i32 1, i32* @x release, align 4, !dbg !58
  ret i8* null, !dbg !59
}

; Function Attrs: noinline nounwind uwtable
define dso_local i8* @thread_3(i8* noundef %0) #0 !dbg !60 {
  call void @llvm.dbg.value(metadata i8* %0, metadata !61, metadata !DIExpression()), !dbg !62
  store atomic i32 1, i32* @y release, align 4, !dbg !63
  ret i8* null, !dbg !64
}

; Function Attrs: noinline nounwind uwtable
define dso_local i8* @thread_4(i8* noundef %0) #0 !dbg !65 {
  call void @llvm.dbg.value(metadata i8* %0, metadata !66, metadata !DIExpression()), !dbg !67
  %2 = load atomic i32, i32* @y acquire, align 4, !dbg !68
  call void @llvm.dbg.value(metadata i32 %2, metadata !69, metadata !DIExpression()), !dbg !67
  fence seq_cst, !dbg !70
  %3 = load atomic i32, i32* @x acquire, align 4, !dbg !71
  call void @llvm.dbg.value(metadata i32 %3, metadata !72, metadata !DIExpression()), !dbg !67
  fence seq_cst, !dbg !73
  store i32 %2, i32* @c, align 4, !dbg !74
  store i32 %3, i32* @d, align 4, !dbg !75
  ret i8* null, !dbg !76
}

; Function Attrs: noinline nounwind uwtable
define dso_local i32 @main() #0 !dbg !77 {
  %1 = alloca i64, align 8
  %2 = alloca i64, align 8
  %3 = alloca i64, align 8
  %4 = alloca i64, align 8
  call void @llvm.dbg.value(metadata i64* %1, metadata !80, metadata !DIExpression(DW_OP_deref)), !dbg !84
  %5 = call i32 @pthread_create(i64* noundef nonnull %1, %union.pthread_attr_t* noundef null, i8* (i8*)* noundef nonnull @thread_1, i8* noundef null) #5, !dbg !85
  call void @llvm.dbg.value(metadata i64* %2, metadata !86, metadata !DIExpression(DW_OP_deref)), !dbg !84
  %6 = call i32 @pthread_create(i64* noundef nonnull %2, %union.pthread_attr_t* noundef null, i8* (i8*)* noundef nonnull @thread_2, i8* noundef null) #5, !dbg !87
  call void @llvm.dbg.value(metadata i64* %3, metadata !88, metadata !DIExpression(DW_OP_deref)), !dbg !84
  %7 = call i32 @pthread_create(i64* noundef nonnull %3, %union.pthread_attr_t* noundef null, i8* (i8*)* noundef nonnull @thread_3, i8* noundef null) #5, !dbg !89
  call void @llvm.dbg.value(metadata i64* %4, metadata !90, metadata !DIExpression(DW_OP_deref)), !dbg !84
  %8 = call i32 @pthread_create(i64* noundef nonnull %4, %union.pthread_attr_t* noundef null, i8* (i8*)* noundef nonnull @thread_4, i8* noundef null) #5, !dbg !91
  %9 = load i64, i64* %1, align 8, !dbg !92
  call void @llvm.dbg.value(metadata i64 %9, metadata !80, metadata !DIExpression()), !dbg !84
  %10 = call i32 @pthread_join(i64 noundef %9, i8** noundef null) #5, !dbg !93
  %11 = load i64, i64* %2, align 8, !dbg !94
  call void @llvm.dbg.value(metadata i64 %11, metadata !86, metadata !DIExpression()), !dbg !84
  %12 = call i32 @pthread_join(i64 noundef %11, i8** noundef null) #5, !dbg !95
  %13 = load i64, i64* %3, align 8, !dbg !96
  call void @llvm.dbg.value(metadata i64 %13, metadata !88, metadata !DIExpression()), !dbg !84
  %14 = call i32 @pthread_join(i64 noundef %13, i8** noundef null) #5, !dbg !97
  %15 = load i64, i64* %4, align 8, !dbg !98
  call void @llvm.dbg.value(metadata i64 %15, metadata !90, metadata !DIExpression()), !dbg !84
  %16 = call i32 @pthread_join(i64 noundef %15, i8** noundef null) #5, !dbg !99
  %17 = load i32, i32* @a, align 4, !dbg !100
  %18 = icmp eq i32 %17, 1, !dbg !100
  %19 = load i32, i32* @b, align 4, !dbg !100
  %20 = icmp eq i32 %19, 0, !dbg !100
  %or.cond = select i1 %18, i1 %20, i1 false, !dbg !100
  %21 = load i32, i32* @c, align 4, !dbg !100
  %22 = icmp eq i32 %21, 1, !dbg !100
  %or.cond3 = select i1 %or.cond, i1 %22, i1 false, !dbg !100
  %23 = load i32, i32* @d, align 4, !dbg !100
  %24 = icmp eq i32 %23, 0, !dbg !100
  %or.cond5 = select i1 %or.cond3, i1 %24, i1 false, !dbg !100
  br i1 %or.cond5, label %25, label %26, !dbg !100

25:                                               ; preds = %0
  call void @__assert_fail(i8* noundef getelementptr inbounds ([40 x i8], [40 x i8]* @.str, i64 0, i64 0), i8* noundef getelementptr inbounds ([50 x i8], [50 x i8]* @.str.1, i64 0, i64 0), i32 noundef 58, i8* noundef getelementptr inbounds ([11 x i8], [11 x i8]* @__PRETTY_FUNCTION__.main, i64 0, i64 0)) #6, !dbg !100
  unreachable, !dbg !100

26:                                               ; preds = %0
  ret i32 0, !dbg !103
}

; Function Attrs: nounwind
declare i32 @pthread_create(i64* noundef, %union.pthread_attr_t* noundef, i8* (i8*)* noundef, i8* noundef) #2

declare i32 @pthread_join(i64 noundef, i8** noundef) #3

; Function Attrs: noreturn nounwind
declare void @__assert_fail(i8* noundef, i8* noundef, i32 noundef, i8* noundef) #4

; Function Attrs: nofree nosync nounwind readnone speculatable willreturn
declare void @llvm.dbg.value(metadata, metadata, metadata) #1

attributes #0 = { noinline nounwind uwtable "frame-pointer"="all" "min-legal-vector-width"="0" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }
attributes #1 = { nofree nosync nounwind readnone speculatable willreturn }
attributes #2 = { nounwind "frame-pointer"="all" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }
attributes #3 = { "frame-pointer"="all" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }
attributes #4 = { noreturn nounwind "frame-pointer"="all" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }
attributes #5 = { nounwind }
attributes #6 = { noreturn nounwind }

!llvm.dbg.cu = !{!2}
!llvm.module.flags = !{!32, !33, !34, !35, !36, !37, !38}
!llvm.ident = !{!39}

!0 = !DIGlobalVariableExpression(var: !1, expr: !DIExpression())
!1 = distinct !DIGlobalVariable(name: "x", scope: !2, file: !20, line: 6, type: !21, isLocal: false, isDefinition: true)
!2 = distinct !DICompileUnit(language: DW_LANG_C99, file: !3, producer: "Ubuntu clang version 14.0.6", isOptimized: false, runtimeVersion: 0, emissionKind: FullDebug, enums: !4, retainedTypes: !15, globals: !17, splitDebugInlining: false, nameTableKind: None)
!3 = !DIFile(filename: "/home/ponce/git/Dat3M/benchmarks/imm/paper-E3.8.c", directory: "/home/ponce/git/Dat3M", checksumkind: CSK_MD5, checksum: "a0b5cca6bfdee395cf83324abb333d2a")
!4 = !{!5}
!5 = !DICompositeType(tag: DW_TAG_enumeration_type, name: "memory_order", file: !6, line: 56, baseType: !7, size: 32, elements: !8)
!6 = !DIFile(filename: "/usr/lib/llvm-14/lib/clang/14.0.6/include/stdatomic.h", directory: "", checksumkind: CSK_MD5, checksum: "de5d66a1ef2f5448cc1919ff39db92bc")
!7 = !DIBasicType(name: "unsigned int", size: 32, encoding: DW_ATE_unsigned)
!8 = !{!9, !10, !11, !12, !13, !14}
!9 = !DIEnumerator(name: "memory_order_relaxed", value: 0)
!10 = !DIEnumerator(name: "memory_order_consume", value: 1)
!11 = !DIEnumerator(name: "memory_order_acquire", value: 2)
!12 = !DIEnumerator(name: "memory_order_release", value: 3)
!13 = !DIEnumerator(name: "memory_order_acq_rel", value: 4)
!14 = !DIEnumerator(name: "memory_order_seq_cst", value: 5)
!15 = !{!16}
!16 = !DIDerivedType(tag: DW_TAG_pointer_type, baseType: null, size: 64)
!17 = !{!0, !18, !24, !26, !28, !30}
!18 = !DIGlobalVariableExpression(var: !19, expr: !DIExpression())
!19 = distinct !DIGlobalVariable(name: "y", scope: !2, file: !20, line: 6, type: !21, isLocal: false, isDefinition: true)
!20 = !DIFile(filename: "benchmarks/imm/paper-E3.8.c", directory: "/home/ponce/git/Dat3M", checksumkind: CSK_MD5, checksum: "a0b5cca6bfdee395cf83324abb333d2a")
!21 = !DIDerivedType(tag: DW_TAG_typedef, name: "atomic_int", file: !6, line: 92, baseType: !22)
!22 = !DIDerivedType(tag: DW_TAG_atomic_type, baseType: !23)
!23 = !DIBasicType(name: "int", size: 32, encoding: DW_ATE_signed)
!24 = !DIGlobalVariableExpression(var: !25, expr: !DIExpression())
!25 = distinct !DIGlobalVariable(name: "a", scope: !2, file: !20, line: 8, type: !23, isLocal: false, isDefinition: true)
!26 = !DIGlobalVariableExpression(var: !27, expr: !DIExpression())
!27 = distinct !DIGlobalVariable(name: "b", scope: !2, file: !20, line: 8, type: !23, isLocal: false, isDefinition: true)
!28 = !DIGlobalVariableExpression(var: !29, expr: !DIExpression())
!29 = distinct !DIGlobalVariable(name: "c", scope: !2, file: !20, line: 8, type: !23, isLocal: false, isDefinition: true)
!30 = !DIGlobalVariableExpression(var: !31, expr: !DIExpression())
!31 = distinct !DIGlobalVariable(name: "d", scope: !2, file: !20, line: 8, type: !23, isLocal: false, isDefinition: true)
!32 = !{i32 7, !"Dwarf Version", i32 5}
!33 = !{i32 2, !"Debug Info Version", i32 3}
!34 = !{i32 1, !"wchar_size", i32 4}
!35 = !{i32 7, !"PIC Level", i32 2}
!36 = !{i32 7, !"PIE Level", i32 2}
!37 = !{i32 7, !"uwtable", i32 1}
!38 = !{i32 7, !"frame-pointer", i32 2}
!39 = !{!"Ubuntu clang version 14.0.6"}
!40 = distinct !DISubprogram(name: "thread_1", scope: !20, file: !20, line: 10, type: !41, scopeLine: 11, flags: DIFlagPrototyped, spFlags: DISPFlagDefinition, unit: !2, retainedNodes: !43)
!41 = !DISubroutineType(types: !42)
!42 = !{!16, !16}
!43 = !{}
!44 = !DILocalVariable(name: "unused", arg: 1, scope: !40, file: !20, line: 10, type: !16)
!45 = !DILocation(line: 0, scope: !40)
!46 = !DILocation(line: 12, column: 11, scope: !40)
!47 = !DILocalVariable(name: "r0", scope: !40, file: !20, line: 12, type: !23)
!48 = !DILocation(line: 13, column: 2, scope: !40)
!49 = !DILocation(line: 14, column: 11, scope: !40)
!50 = !DILocalVariable(name: "r1", scope: !40, file: !20, line: 14, type: !23)
!51 = !DILocation(line: 15, column: 2, scope: !40)
!52 = !DILocation(line: 16, column: 4, scope: !40)
!53 = !DILocation(line: 17, column: 4, scope: !40)
!54 = !DILocation(line: 18, column: 2, scope: !40)
!55 = distinct !DISubprogram(name: "thread_2", scope: !20, file: !20, line: 21, type: !41, scopeLine: 22, flags: DIFlagPrototyped, spFlags: DISPFlagDefinition, unit: !2, retainedNodes: !43)
!56 = !DILocalVariable(name: "unused", arg: 1, scope: !55, file: !20, line: 21, type: !16)
!57 = !DILocation(line: 0, scope: !55)
!58 = !DILocation(line: 23, column: 2, scope: !55)
!59 = !DILocation(line: 24, column: 2, scope: !55)
!60 = distinct !DISubprogram(name: "thread_3", scope: !20, file: !20, line: 27, type: !41, scopeLine: 28, flags: DIFlagPrototyped, spFlags: DISPFlagDefinition, unit: !2, retainedNodes: !43)
!61 = !DILocalVariable(name: "unused", arg: 1, scope: !60, file: !20, line: 27, type: !16)
!62 = !DILocation(line: 0, scope: !60)
!63 = !DILocation(line: 29, column: 2, scope: !60)
!64 = !DILocation(line: 30, column: 2, scope: !60)
!65 = distinct !DISubprogram(name: "thread_4", scope: !20, file: !20, line: 33, type: !41, scopeLine: 34, flags: DIFlagPrototyped, spFlags: DISPFlagDefinition, unit: !2, retainedNodes: !43)
!66 = !DILocalVariable(name: "unused", arg: 1, scope: !65, file: !20, line: 33, type: !16)
!67 = !DILocation(line: 0, scope: !65)
!68 = !DILocation(line: 35, column: 11, scope: !65)
!69 = !DILocalVariable(name: "r0", scope: !65, file: !20, line: 35, type: !23)
!70 = !DILocation(line: 36, column: 2, scope: !65)
!71 = !DILocation(line: 37, column: 11, scope: !65)
!72 = !DILocalVariable(name: "r1", scope: !65, file: !20, line: 37, type: !23)
!73 = !DILocation(line: 38, column: 2, scope: !65)
!74 = !DILocation(line: 39, column: 4, scope: !65)
!75 = !DILocation(line: 40, column: 4, scope: !65)
!76 = !DILocation(line: 41, column: 2, scope: !65)
!77 = distinct !DISubprogram(name: "main", scope: !20, file: !20, line: 44, type: !78, scopeLine: 45, spFlags: DISPFlagDefinition, unit: !2, retainedNodes: !43)
!78 = !DISubroutineType(types: !79)
!79 = !{!23}
!80 = !DILocalVariable(name: "t1", scope: !77, file: !20, line: 46, type: !81)
!81 = !DIDerivedType(tag: DW_TAG_typedef, name: "pthread_t", file: !82, line: 27, baseType: !83)
!82 = !DIFile(filename: "/usr/include/x86_64-linux-gnu/bits/pthreadtypes.h", directory: "", checksumkind: CSK_MD5, checksum: "2d764266ce95ab26d4a4767c2ec78176")
!83 = !DIBasicType(name: "unsigned long", size: 64, encoding: DW_ATE_unsigned)
!84 = !DILocation(line: 0, scope: !77)
!85 = !DILocation(line: 48, column: 2, scope: !77)
!86 = !DILocalVariable(name: "t2", scope: !77, file: !20, line: 46, type: !81)
!87 = !DILocation(line: 49, column: 2, scope: !77)
!88 = !DILocalVariable(name: "t3", scope: !77, file: !20, line: 46, type: !81)
!89 = !DILocation(line: 50, column: 2, scope: !77)
!90 = !DILocalVariable(name: "t4", scope: !77, file: !20, line: 46, type: !81)
!91 = !DILocation(line: 51, column: 2, scope: !77)
!92 = !DILocation(line: 53, column: 15, scope: !77)
!93 = !DILocation(line: 53, column: 2, scope: !77)
!94 = !DILocation(line: 54, column: 15, scope: !77)
!95 = !DILocation(line: 54, column: 2, scope: !77)
!96 = !DILocation(line: 55, column: 15, scope: !77)
!97 = !DILocation(line: 55, column: 2, scope: !77)
!98 = !DILocation(line: 56, column: 15, scope: !77)
!99 = !DILocation(line: 56, column: 2, scope: !77)
!100 = !DILocation(line: 58, column: 2, scope: !101)
!101 = distinct !DILexicalBlock(scope: !102, file: !20, line: 58, column: 2)
!102 = distinct !DILexicalBlock(scope: !77, file: !20, line: 58, column: 2)
!103 = !DILocation(line: 60, column: 2, scope: !77)
