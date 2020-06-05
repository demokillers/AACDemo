package com.demokiller.spi.writer

import com.demokiller.spi.data.Data
import com.demokiller.spi.data.parseClassName
import com.demokiller.spi.data.parsePackage
import com.demokiller.spiannotation.IServiceImplFinder
import com.demokiller.spiannotation.ServiceConst
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import java.io.IOException
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier

class ServiceWriter(private var data: Data) {

    fun writeToFile(processingEnvironment: ProcessingEnvironment) {
        data.allApiFullNames.forEach {
            val builder = TypeSpec.classBuilder(it.parseClassName() + ServiceConst.SERVICE_FINDER_SUFFIX)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(TypeName.get(IServiceImplFinder::class.java))
                    .addMethod(createGetApisMethod())
                    .addMethod(createGetInstanceMethod())
            write(it.parsePackage(), builder, processingEnvironment)
        }
    }

    private fun createGetApisMethod(): MethodSpec? {
        return MethodSpec.methodBuilder("getApis")
                .addModifiers(Modifier.PUBLIC)
                .returns(Array<Any>::class.java)
                .addStatement("return new Class[]{${allApis(data.allApiFullNames)}}")
                .build()
    }

    private fun createGetInstanceMethod(): MethodSpec? {
        return MethodSpec.methodBuilder("getInstance")
                .addModifiers(Modifier.PUBLIC)
                .returns(Any::class.java)
                .addStatement("return new ${data.fullClassName}()")
                .build()
    }

    private fun allApis(apis: List<String>): String {
        var result = ""
        apis.forEachIndexed { index, s ->
            result = "$result$s.class"
            if (index != apis.size - 1) {
                result += ", "
            }
        }
        return result
    }

    @Throws(IOException::class)
    private fun write(packageName: String, builder: TypeSpec.Builder, processingEnvironment: ProcessingEnvironment) {
        JavaFile.builder(packageName,
                builder.build())
                .build()
                .writeTo(processingEnvironment.filer)
    }

}