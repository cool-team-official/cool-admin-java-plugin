package com.cool.core.plugin;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.cool.core.config.PluginJson;
import com.cool.core.exception.CoolPreconditions;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

@Setter
@Slf4j
public abstract class BaseCoolPlugin {

    protected PluginJson pluginJson;

    private ApplicationContext applicationContext;

    /**
     * 插件入口方法
     */
    public abstract Object invokePlugin(String... params);

    /**
     * 主应用更新配置时调用
     */
    public void setPluginJson(String pluginJson) {
        if (ObjUtil.isNotEmpty(pluginJson)) {
            this.pluginJson = JSONUtil.toBean(pluginJson, PluginJson.class);
        }
    }

    /**
     * 调用主应用的方法,如缓存
     * Object cache = invokeMain("coolCache", "get", "verify:img:4fd03504-47ac-4b57-b8e4-3c64059230d0");
     */
    protected Object invokeMain(String key, String methodName, Object... params) {
        try {
            Object beanInstance = applicationContext.getBean(key);
            Class<?>[] paramTypes = Arrays.stream(params).map(Object::getClass)
                .toArray(Class<?>[]::new);
            Method method = findMethod(beanInstance.getClass(), methodName, paramTypes);
            if (method == null) {
                throw new NoSuchMethodException(
                    "No such method: " + methodName + " with parameters " + Arrays.toString(
                        paramTypes));
            }
            log.info("调用主应用: {}, 方法: {}", key, methodName);
            return invoke(params, method, beanInstance);
        } catch (Exception e) {
            log.error("调用主应用{}.{}失败", key, methodName, e);
            CoolPreconditions.alwaysThrow("调用主应用{}.{}失败", key, methodName);
        }
        return null;
    }

    protected Object invokeOtherPlugin(String key, String methodName, Object... params) {
        try {
            // 获取其他插件实例
            Object beanInstance = invokeMain("coolPluginService", "getInstance", key);
            Class<?>[] paramTypes = Arrays.stream(params).map(Object::getClass)
                .toArray(Class<?>[]::new);
            Method method = findMethod(beanInstance.getClass(), methodName, paramTypes);
            if (method == null) {
                throw new NoSuchMethodException(
                    "No such method: " + methodName + " with parameters " + Arrays.toString(
                        paramTypes));
            }
            log.info("调用其他插件{}, 方法: {}", key, methodName);
            return invoke(params, method, beanInstance);
        } catch (Exception e) {
            log.error("调用主应用{}.{}失败", key, methodName, e);
            CoolPreconditions.alwaysThrow("调用主应用{}.{}失败", key, methodName);
        }
        return null;
    }

    private static Object invoke(Object[] params, Method method, Object beanInstance)
        throws IllegalAccessException, InvocationTargetException {
        if (method.isVarArgs()) {
            // 处理可变参数调用
            int varArgIndex = method.getParameterTypes().length - 1;
            Object[] varArgs =
                (Object[])
                    java.lang.reflect.Array.newInstance(
                        method.getParameterTypes()[varArgIndex].getComponentType(),
                        params.length - varArgIndex);
            System.arraycopy(params, varArgIndex, varArgs, 0, varArgs.length);
            Object[] methodArgs = new Object[varArgIndex + 1];
            System.arraycopy(params, 0, methodArgs, 0, varArgIndex);
            methodArgs[varArgIndex] = varArgs;
            return method.invoke(beanInstance, methodArgs);
        } else {
            // 正常调用
            return method.invoke(beanInstance, params);
        }
    }

    // 查找方法，包括处理可变参数
    private static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            // Try to find a varargs method
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName) && isAssignable(paramTypes, method.getParameterTypes(), method.isVarArgs())) {
                    return method;
                }
            }
            // If not found, try to find in superclass
            if (clazz.getSuperclass() != null) {
                return findMethod(clazz.getSuperclass(), methodName, paramTypes);
            }
        }
        return null;
    }

    private static boolean isAssignable(Class<?>[] paramTypes, Class<?>[] methodParamTypes, boolean isVarArgs) {
        if (isVarArgs) {
            if (paramTypes.length < methodParamTypes.length - 1) {
                return false;
            }
            for (int i = 0; i < methodParamTypes.length - 1; i++) {
                if (!methodParamTypes[i].isAssignableFrom(paramTypes[i])) {
                    return false;
                }
            }
            Class<?> varArgType = methodParamTypes[methodParamTypes.length - 1].getComponentType();
            for (int i = methodParamTypes.length - 1; i < paramTypes.length; i++) {
                if (!varArgType.isAssignableFrom(paramTypes[i])) {
                    return false;
                }
            }
            return true;
        } else {
            if (paramTypes.length != methodParamTypes.length) {
                return false;
            }
            for (int i = 0; i < paramTypes.length; i++) {
                if (!methodParamTypes[i].isAssignableFrom(paramTypes[i])) {
                    return false;
                }
            }
            return true;
        }
    }

}
