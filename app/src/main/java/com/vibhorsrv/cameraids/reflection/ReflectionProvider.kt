package com.vibhorsrv.cameraids.reflection

import com.vibhorsrv.cameraids.api.ReflectionApi
import java.lang.reflect.Field
import java.lang.reflect.Method

class ReflectionProvider : ReflectionApi {
    override fun getFields(aClass: Class<*>): Array<Field> {
        return aClass.declaredFields
    }

    override fun getMethods(aClass: Class<*>): Array<Method?> {
        return aClass.declaredMethods
    }

    fun getResultFieldName(aClass: Class<*>, prefix: String, value: Int): String {
        for (f in getFields(aClass)) if (f.name.startsWith(prefix)) {
            try {
                if (f.getInt(f) == value) return f.name.replace(prefix, "")
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        return ""
    }
}
