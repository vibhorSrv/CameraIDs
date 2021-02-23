package com.vibhorsrv.cameraids.reflection;

import com.vibhorsrv.cameraids.api.ReflectionApi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionProvider implements ReflectionApi {
    @Override
    public Field[] getFields(Class<?> aClass) {
        return aClass.getDeclaredFields();
    }

    @Override
    public Method[] getMethods(Class<?> aClass) {
        return aClass.getDeclaredMethods();
    }

    public String getResultFieldName(Class<?> aClass, String prefix, Integer value) {
        for (Field f : getFields(aClass))
            if (f.getName().startsWith(prefix)) {
                try {
                    if (f.getInt(f) == value)
                        return f.getName().replace(prefix, "");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        return "";
    }
}
