package com.vibhorsrv.cameraids.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ReflectionApi {
    Field[] getFields(Class<?> aClass);

    Method[] getMethods(Class<?> aClass);
}
