package es.mundosoft.sample

import net_62v.external.MetaCore
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

object MetaServiceBridge {
    interface ServiceHookRecovery
    {
        /**
         * Destroy the installed hook scope
         */
        fun destroyServiceHook()
    }

    /**
     * Add custom hook to a system service, e.g. Context.ACTIVITY_SERVICE
     * @see android.content.Context.ACTIVITY_SERVICE
     * usage: hookService(Context.ACTIVITY_SERVICE, "method name", customServiceHook)
     * @see lu.die.mundo.APISample.samplePackageAction
     */
    fun hookService(
        serviceName : String,
        methodName : String,
        serviceHook: ZFork_ServiceHook
    ) : ServiceHookRecovery
    {
        var invocationHandlerRef : InvocationHandler? = null
        var shouldSkipThisHook = false
        invocationHandlerRef = (MetaCore.addServiceInterpreter(
            serviceName,
            methodName
        ) { p0, p1, p2 ->
            val originCall = object : (Any?, Method, Array<Any?>) -> Any?
            {
                override fun invoke(s0: Any?, s1: Method, s2: Array<Any?>): Any? {
                    val ref = invocationHandlerRef
                    if(ref != null)
                    {
                        return ref.invoke(s0, s1, s2)
                    }
                    return s1.invoke(s0, *s2)
                }
            }
            if(shouldSkipThisHook)
                originCall(p0, p1, p2)
            else serviceHook.invokeResult(p0, p1, p2, originCall)
        })
        return object : ServiceHookRecovery
        {
            override fun destroyServiceHook() {
                shouldSkipThisHook = true
                if(invocationHandlerRef != null) MetaCore.addServiceInterpreter(
                    serviceName,
                    methodName,
                    invocationHandlerRef
                )
            }
        }
    }
}