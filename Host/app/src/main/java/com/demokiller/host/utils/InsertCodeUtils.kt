package com.demokiller.host.utils

import org.objectweb.asm.*
import java.io.*
import java.util.*

object InsertCodeUtils {
    fun operateClassByteCode(classByteAry: ByteArray?): ByteArray { // 使用全限定名，创建一个ClassReader对象
        val classReader = ClassReader(classByteAry)
        // 构建一个ClassWriter对象，并设置让系统自动计算栈和本地变量大小
        val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
        val classAdapter: ClassVisitor = IClassAdapter(classWriter, classReader.className)
        classReader.accept(classAdapter, ClassReader.SKIP_DEBUG)
        return classWriter.toByteArray()
    }

    fun operateClassByteCode(classFile: File?, newClassFile: File?): Boolean {
        var fos: FileOutputStream? = null
        var fis: FileInputStream? = null
        var bos: ByteArrayOutputStream? = null
        try {
            fis = FileInputStream(classFile)
            fos = FileOutputStream(newClassFile)
            bos = ByteArrayOutputStream()
            var len = -1
            val buffer = ByteArray(1024)
            while (fis.read(buffer).also { len = it } != -1) {
                bos.write(buffer, 0, len)
            }
            var newAry: ByteArray? = operateClassByteCode(bos.toByteArray()) ?: return false
            fos.write(newAry)
            newAry = null
            return true
        } catch (e: IOException) {
            println("add code to classfile error:$e")
        } finally {
            try {
                fos!!.close()
                bos!!.close()
                fis!!.close()
            } catch (e: Exception) {
            }
        }
        return false
    }

    internal class IClassAdapter(cv: ClassVisitor, className: String) : ClassVisitor(Opcodes.ASM7, cv) {
        private val className: String
        override fun visitMethod(access: Int, name: String, desc: String,
                                 signature: String, exceptions: Array<String>): MethodVisitor {
            val mv = cv.visitMethod(access, name, desc, signature, exceptions)
            // 当是sayName方法是做对应的修改
            return if (name != "<clinit>" && name != "<init>") {
                var isStatic = false
                if (Opcodes.ACC_STATIC == access and Opcodes.ACC_STATIC) {
                    isStatic = true
                }
                IMethodAdapter(mv, className, desc, isStatic)
            } else {
                mv
            }
        }

        // 定义一个自己的方法访问类
        internal inner class IMethodAdapter(mv: MethodVisitor?, className: String, desc: String?, isStatic: Boolean) : MethodVisitor(Opcodes.ASM7, mv) {
            private val argsType: Array<Type>
            private val returnType: Type
            private val isStatic: Boolean
            private val className: String
            // 在源方法前去修改方法内容,这部分的修改将加载源方法的字节码之前
            override fun visitCode() {
                val argsList: MutableList<String> = ArrayList(argsType.size)
                for (type in argsType) {
                    argsList.add(type.descriptor)
                }
                RobustAsmUtils.createInsertCode(mv, className.replace(".", "/"), argsList,
                        returnType.descriptor, isStatic)
            }

            init {
                argsType = Type.getArgumentTypes(desc)
                returnType = Type.getReturnType(desc)
                this.isStatic = isStatic
                this.className = className
            }
        }

        init {
            RobustAsmUtils.addClassStaticField(cv, RobustAsmUtils.REDIRECTFIELD_NAME,
                    RobustAsmUtils.REDIRECTCLASSNAME)
            this.className = className
        }
    }
}