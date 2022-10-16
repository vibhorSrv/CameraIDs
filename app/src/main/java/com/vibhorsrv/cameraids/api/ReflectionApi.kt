package com.vibhorsrv.cameraids.api

import java.lang.reflect.Field
import java.lang.reflect.Method

interface ReflectionApi {
    fun getFields(aClass: Class<*>): Array<Field>
    fun getMethods(aClass: Class<*>): Array<Method?>
}
