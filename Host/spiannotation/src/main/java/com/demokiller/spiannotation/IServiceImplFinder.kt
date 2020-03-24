package com.demokiller.spiannotation

interface IServiceImplFinder {
    fun getInstance(): Any

    fun getFirstApiClass(): Class<out Any>

    fun getAllApis(): Array<Any>
}