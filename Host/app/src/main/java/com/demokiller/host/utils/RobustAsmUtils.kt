package com.demokiller.host.utils

import com.demokiller.robustpatch.ChangeQuickRedirect
import org.objectweb.asm.*

object RobustAsmUtils {
    const val REDIRECTFIELD_NAME = "changeQuickRedirect"
    val REDIRECTCLASSNAME = Type.getDescriptor(ChangeQuickRedirect::class.java)
    val PROXYCLASSNAME = PatchProxy::class.java.name.replace(".", "/")
    fun addClassStaticField(cv: ClassVisitor, fieldName: String?, typeClass: Class<*>?) {
        cv.visitField(Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC, fieldName,
                Type.getDescriptor(typeClass), null, null)
    }

    fun addClassStaticField(cv: ClassVisitor, fieldName: String?, typeClass: String?) {
        cv.visitField(Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC, fieldName, typeClass, null, null)
    }

    fun setClassStaticFieldValue(mv: MethodVisitor, className: String?, fieldName: String?, typeSign: String?) {
        mv.visitFieldInsn(Opcodes.PUTSTATIC, className, fieldName, typeSign)
    }

    fun getClassStaticFieldValue(mv: MethodVisitor, className: String?, fieldName: String?, typeSign: String?) {
        mv.visitFieldInsn(Opcodes.GETSTATIC, className, fieldName, typeSign)
    }

    /**
     * 插入代码
     *
     * @param mv
     * @param className
     * @param paramsTypeClass
     * @param returnTypeStr
     * @param isStatic
     */
    fun createInsertCode(mv: MethodVisitor, className: String?, paramsTypeClass: List<String>, returnTypeStr: String, isStatic: Boolean) { //获取changeQuickRedirect静态变量
        mv.visitFieldInsn(Opcodes.GETSTATIC,
                className,
                REDIRECTFIELD_NAME,
                REDIRECTCLASSNAME)
        val l1 = Label()
        mv.visitJumpInsn(Opcodes.IFNULL, l1)
        /**
         * 调用isSupport方法
         */
//第一个参数：new Object[]{...};,如果方法没有参数直接传入new Object[0]
        if (paramsTypeClass.size == 0) {
            mv.visitInsn(Opcodes.ICONST_0)
            mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object")
        } else {
            createObjectArray(mv, paramsTypeClass, isStatic)
        }
        //第二个参数：this,如果方法是static的话就直接传入null
        if (isStatic) {
            mv.visitInsn(Opcodes.ACONST_NULL)
        } else {
            mv.visitVarInsn(Opcodes.ALOAD, 0)
        }
        //第三个参数：changeQuickRedirect
        mv.visitFieldInsn(Opcodes.GETSTATIC,
                className,
                REDIRECTFIELD_NAME,
                REDIRECTCLASSNAME)
        //第四个参数：false,标志是否为static
        mv.visitInsn(if (isStatic) Opcodes.ICONST_1 else Opcodes.ICONST_0)
        //开始调用
        mv.visitMethodInsn(Opcodes.GETSTATIC,
                PROXYCLASSNAME,
                "isSupport",
                "([Ljava/lang/Object;Ljava/lang/Object;" + REDIRECTCLASSNAME + "Z)Z")
        mv.visitJumpInsn(Opcodes.IFEQ, l1)
        /**
         * 调用accessDispatch方法
         */
//第一个参数：new Object[]{...};,如果方法没有参数直接传入new Object[0]
        if (paramsTypeClass.size == 0) {
            mv.visitInsn(Opcodes.ICONST_0)
            mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object")
        } else {
            createObjectArray(mv, paramsTypeClass, isStatic)
        }
        //第二个参数：this,如果方法是static的话就直接传入null
        if (isStatic) {
            mv.visitInsn(Opcodes.ACONST_NULL)
        } else {
            mv.visitVarInsn(Opcodes.ALOAD, 0)
        }
        //第三个参数:changeQuickRedirect
        mv.visitFieldInsn(Opcodes.GETSTATIC,
                className,
                REDIRECTFIELD_NAME,
                REDIRECTCLASSNAME)
        //第四个参数：false,标志是否为static
        mv.visitInsn(if (isStatic) Opcodes.ICONST_1 else Opcodes.ICONST_0)
        //开始调用
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                PROXYCLASSNAME,
                "accessDispatch",
                "([Ljava/lang/Object;Ljava/lang/Object;" + REDIRECTCLASSNAME + "Z)Ljava/lang/Object;")
        //判断是否有返回值，代码不同
        if ("V" == returnTypeStr) {
            mv.visitInsn(Opcodes.POP)
            mv.visitInsn(Opcodes.RETURN)
        } else { //强制转化类型
            if (!castPrimateToObj(mv, returnTypeStr)) { //这里需要注意，如果是数组类型的直接使用即可，如果非数组类型，就得去除前缀了,还有最终是没有结束符;
//比如：Ljava/lang/String; ==》 java/lang/String
                var newTypeStr: String? = null
                val len = returnTypeStr.length
                newTypeStr = if (returnTypeStr.startsWith("[")) {
                    returnTypeStr.substring(0, len)
                } else {
                    returnTypeStr.substring(1, len - 1)
                }
                mv.visitTypeInsn(Opcodes.CHECKCAST, newTypeStr)
            }
            //这里还需要做返回类型不同返回指令也不同
            mv.visitInsn(getReturnTypeCode(returnTypeStr))
        }
        mv.visitLabel(l1)
    }

    /**
     * 创建局部参数代码
     *
     * @param mv
     * @param paramsTypeClass
     * @param isStatic
     */
    private fun createObjectArray(mv: MethodVisitor, paramsTypeClass: List<String>, isStatic: Boolean) { //Opcodes.ICONST_0 ~ Opcodes.ICONST_5 这个指令范围
        val argsCount = paramsTypeClass.size
        //声明 Object[argsCount];
        if (argsCount >= 6) {
            mv.visitIntInsn(Opcodes.BIPUSH, argsCount)
        } else {
            mv.visitInsn(Opcodes.ICONST_0 + argsCount)
        }
        mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object")
        //如果是static方法，没有this隐含参数
        var loadIndex = if (isStatic) 0 else 1
        //填充数组数据
        for (i in 0 until argsCount) {
            mv.visitInsn(Opcodes.DUP)
            if (i <= 5) {
                mv.visitInsn(Opcodes.ICONST_0 + i)
            } else {
                mv.visitIntInsn(Opcodes.BIPUSH, i)
            }
            //这里又要做特殊处理，在实践过程中发现个问题：public void xxx(long a, boolean b, double c,int d)
//当一个参数的前面一个参数是long或者是double类型的话，后面参数在使用LOAD指令，加载数据索引值要+1
//个人猜想是和long，double是8个字节的问题有关系。这里做了处理
//比如这里的参数：[a=LLOAD 1] [b=ILOAD 3] [c=DLOAD 4] [d=ILOAD 6];
            if (i >= 1) { //这里需要判断当前参数的前面一个参数的类型是什么
                if ("J" == paramsTypeClass[i - 1] || "D" == paramsTypeClass[i - 1]) { //如果前面一个参数是long，double类型，load指令索引就要增加1
                    loadIndex++
                }
            }
            if (!createPrimateTypeObj(mv, loadIndex, paramsTypeClass[i])) {
                mv.visitVarInsn(Opcodes.ALOAD, loadIndex)
                mv.visitInsn(Opcodes.AASTORE)
            }
            loadIndex++
        }
    }

    private fun createBooleanObj(mv: MethodVisitor, argsPostion: Int) {
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/Byte")
        mv.visitInsn(Opcodes.DUP)
        mv.visitVarInsn(Opcodes.ILOAD, argsPostion)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Byte", "<init>", "(B)V")
        mv.visitInsn(Opcodes.AASTORE)
    }

    private fun createShortObj(mv: MethodVisitor, argsPostion: Int) {
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/Short")
        mv.visitInsn(Opcodes.DUP)
        mv.visitVarInsn(Opcodes.ILOAD, argsPostion)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Short", "<init>", "(S)V")
        mv.visitInsn(Opcodes.AASTORE)
    }

    private fun createCharObj(mv: MethodVisitor, argsPostion: Int) {
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/Character")
        mv.visitInsn(Opcodes.DUP)
        mv.visitVarInsn(Opcodes.ILOAD, argsPostion)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Character", "<init>", "(C)V")
        mv.visitInsn(Opcodes.AASTORE)
    }

    private fun createIntegerObj(mv: MethodVisitor, argsPostion: Int) {
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/Integer")
        mv.visitInsn(Opcodes.DUP)
        mv.visitVarInsn(Opcodes.ILOAD, argsPostion)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Integer", "<init>", "(I)V")
        mv.visitInsn(Opcodes.AASTORE)
    }

    private fun createFloatObj(mv: MethodVisitor, argsPostion: Int) {
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/Float")
        mv.visitInsn(Opcodes.DUP)
        mv.visitVarInsn(Opcodes.FLOAD, argsPostion)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Float", "<init>", "(F)V")
        mv.visitInsn(Opcodes.AASTORE)
    }

    private fun createDoubleObj(mv: MethodVisitor, argsPostion: Int) {
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/Double")
        mv.visitInsn(Opcodes.DUP)
        mv.visitVarInsn(Opcodes.DLOAD, argsPostion)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Double", "<init>", "(D)V")
        mv.visitInsn(Opcodes.AASTORE)
    }

    private fun createLongObj(mv: MethodVisitor, argsPostion: Int) {
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/Long")
        mv.visitInsn(Opcodes.DUP)
        mv.visitVarInsn(Opcodes.LLOAD, argsPostion)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Long", "<init>", "(J)V")
        mv.visitInsn(Opcodes.AASTORE)
    }

    /**
     * 创建基本类型对应的对象
     *
     * @param mv
     * @param argsPostion
     * @param typeS
     * @return
     */
    private fun createPrimateTypeObj(mv: MethodVisitor, argsPostion: Int, typeS: String): Boolean {
        if ("Z" == typeS) {
            createBooleanObj(mv, argsPostion)
            return true
        }
        if ("B" == typeS) {
            createBooleanObj(mv, argsPostion)
            return true
        }
        if ("C" == typeS) {
            createCharObj(mv, argsPostion)
            return true
        }
        if ("S" == typeS) {
            createShortObj(mv, argsPostion)
            return true
        }
        if ("I" == typeS) {
            createIntegerObj(mv, argsPostion)
            return true
        }
        if ("F" == typeS) {
            createFloatObj(mv, argsPostion)
            return true
        }
        if ("D" == typeS) {
            createDoubleObj(mv, argsPostion)
            return true
        }
        if ("J" == typeS) {
            createLongObj(mv, argsPostion)
            return true
        }
        return false
    }

    /**
     * 基本类型需要做对象类型分装
     *
     * @param mv
     * @param typeS
     * @return
     */
    private fun castPrimateToObj(mv: MethodVisitor, typeS: String): Boolean {
        if ("Z" == typeS) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean") //强制转化类型
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z")
            return true
        }
        if ("B" == typeS) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Byte") //强制转化类型
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B")
            return true
        }
        if ("C" == typeS) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Character") //强制转化类型
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "intValue", "()C")
            return true
        }
        if ("S" == typeS) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Short") //强制转化类型
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S")
            return true
        }
        if ("I" == typeS) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer") //强制转化类型
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I")
            return true
        }
        if ("F" == typeS) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float") //强制转化类型
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F")
            return true
        }
        if ("D" == typeS) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double") //强制转化类型
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D")
            return true
        }
        if ("J" == typeS) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Long") //强制转化类型
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J")
            return true
        }
        return false
    }

    /**
     * 针对不同类型返回指令不一样
     *
     * @param typeS
     * @return
     */
    private fun getReturnTypeCode(typeS: String): Int {
        if ("Z" == typeS) {
            return Opcodes.IRETURN
        }
        if ("B" == typeS) {
            return Opcodes.IRETURN
        }
        if ("C" == typeS) {
            return Opcodes.IRETURN
        }
        if ("S" == typeS) {
            return Opcodes.IRETURN
        }
        if ("I" == typeS) {
            return Opcodes.IRETURN
        }
        if ("F" == typeS) {
            return Opcodes.FRETURN
        }
        if ("D" == typeS) {
            return Opcodes.DRETURN
        }
        return if ("J" == typeS) {
            Opcodes.LRETURN
        } else Opcodes.ARETURN
    }
}