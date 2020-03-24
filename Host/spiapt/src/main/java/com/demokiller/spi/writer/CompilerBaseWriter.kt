package com.demokiller.spi.writer

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec

import java.io.IOException

import javax.annotation.processing.ProcessingEnvironment

abstract class CompilerBaseWriter {

    @Throws(IOException::class)
    protected fun write(packageName: String, builder: TypeSpec.Builder, processingEnvironment: ProcessingEnvironment) {
        JavaFile.builder(packageName,
                builder.build())
                .build()
                .writeTo(processingEnvironment.filer)
    }

}
