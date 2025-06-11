package es.mundosoft.sample

import java.lang.reflect.Method

open class ZFork_ServiceHook {
    class ServiceHookParam(
        var param : Array<Any?>,
        var method: Method,
        var thisObject : Any?
    )
    {
        var isReturnEarly = false
        private var currentRes : Any? = null
        var result : Any? get() {
            return currentRes
        } set(value) {
            isReturnEarly = true
            currentRes = value
        }
    }
    fun invokeResult(
        instance : Any,
        method : Method,
        args : Array<Any?>,
        originCall : (Any?, Method, Array<Any?>) -> Any?
    ) : Any?
    {
        val param = ServiceHookParam(args, method, instance)
        beforeHookedMethod(param)
        if(param.isReturnEarly)
        {
            return param.result
        }
        param.result = originCall(param.thisObject, param.method, param.param)
        afterHookedMethod(param)
        return param.result
    }

    /**
     * Xposed like method callback
     * This method will be called before the specific method invoked
     * @see <a href="https://github.com/rovo89/XposedBridge/wiki/Development-tutorial#using-reflection-to-find-and-hook-a-method">https://github.com/rovo89/XposedBridge/wiki/Development-tutorial#using-reflection-to-find-and-hook-a-method</a>
     */
    open fun beforeHookedMethod(param : ServiceHookParam)
    {
        // Do nothing
    }
    /**
     * Xposed like method callback
     * This method will be called after the specific method invoked
     * @see <a href="https://github.com/rovo89/XposedBridge/wiki/Development-tutorial#using-reflection-to-find-and-hook-a-method">https://github.com/rovo89/XposedBridge/wiki/Development-tutorial#using-reflection-to-find-and-hook-a-method</a>
     */
    open fun afterHookedMethod(param : ServiceHookParam)
    {
        // Do nothing
    }
}