package com.demokiller.spi.data

import javax.annotation.processing.ProcessingEnvironment
import javax.tools.Diagnostic

class CompilerContext(var processingEnvironment: ProcessingEnvironment) {

    fun logW(msg: String) {
        processingEnvironment.messager.printMessage(Diagnostic.Kind.WARNING, msg)
    }
}
