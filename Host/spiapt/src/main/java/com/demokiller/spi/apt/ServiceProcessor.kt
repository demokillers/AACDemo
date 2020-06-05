package com.demokiller.spi.apt
import com.demokiller.spi.data.CompilerContext
import com.demokiller.spi.data.ServiceProcessData
import com.demokiller.spiannotation.Service
import com.demokiller.spiannotation.ServiceImpl
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

class ServiceProcessor : AbstractProcessor() {
    private lateinit var compilerContext: CompilerContext

    override fun init(p0: ProcessingEnvironment) {
        super.init(p0)
        compilerContext = CompilerContext(p0)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        val set = mutableSetOf<String>()
        set.add(Service::class.java.canonicalName)
        set.add(ServiceImpl::class.java.canonicalName)
        return set
    }

    override fun process(typeElement: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?): Boolean {
        if (roundEnvironment == null) {
            return true
        }
        val serviceSet = roundEnvironment.getElementsAnnotatedWith(ServiceImpl::class.java)
        val allData = ServiceProcessData(compilerContext, serviceSet)
        allData.write(processingEnv)
        return true
    }

}