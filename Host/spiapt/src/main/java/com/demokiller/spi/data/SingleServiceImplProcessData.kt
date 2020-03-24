package com.demokiller.spi.data

data class SingleServiceImplProcessData(
        val fullClassName: String,
        val allApiFullNames: List<String>,
        var firstApi: String
)
