package com.demokiller.spi.data

import com.demokiller.spi.writer.ServiceWriter
import com.demokiller.spiannotation.Service
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

class ServiceProcessData(private val mContext: CompilerContext, private val mServiceImplElement: Set<Element>) {

    val mImplDatas = mutableListOf<Data>()

    init {
        generateData()
    }

    private fun generateData() {
        mServiceImplElement.forEach {
            it as TypeElement
            //全类名
            val fullClassName = it.qualifiedName.toString()
            val interfaces = it.interfaces

            val allApiFullNames = mutableListOf<String>()
            val allApiNames = mutableListOf<String>()
            var firstApi = ""
            val services = interfaces.filter { typeMirror ->
                val element = mContext.processingEnvironment.typeUtils.asElement(typeMirror)
                val annotation = element.getAnnotation(Service::class.java)
                var hasServiceAnnotation = true
                if (annotation == null) {
                    hasServiceAnnotation = false
                }
                hasServiceAnnotation
            }
            mContext.logW("$services")
            services.forEachIndexed { i, service ->
                val apiFullName = service.toString()
                allApiFullNames.add(apiFullName)
                allApiNames.add(apiFullName.parseClassName())
                if (i == 0) {
                    firstApi = apiFullName
                }
            }

            mImplDatas.add(Data(fullClassName, allApiFullNames, firstApi))

        }
    }

    fun write(processingEnvironment: ProcessingEnvironment) {
        mImplDatas.forEach {
            ServiceWriter(it).writeToFile(processingEnvironment)
        }
    }
}


fun String.parseClassName(): String {
    return substring(lastIndexOf(".") + 1, length)
}

fun String.parsePackage(): String {
    return substring(0, lastIndexOf("."))
}

