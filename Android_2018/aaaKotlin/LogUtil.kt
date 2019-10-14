package com.example.limap.util

import android.util.Log
import com.example.limap.BuildConfig

class LogUtil(){

    companion object {

        private lateinit var className: String//类名
        private lateinit var methodName: String//方法名
        private var lineNumber: Int = 0//行数

        private fun isDebuggable(): Boolean {
            return BuildConfig.DEBUG
        }

        private fun createLog(log: String): String {
            val buffer = StringBuffer()
            buffer.append(methodName)
            buffer.append("(").append(className).append(":").append(lineNumber).append(")")
            buffer.append(log)
            return buffer.toString()
        }

        private fun getMethodNames(sElements: Array<StackTraceElement>) {
            className = sElements[1].fileName
            methodName = sElements[1].methodName
            lineNumber = sElements[1].lineNumber
        }


        fun e(message: String) {
            if (!isDebuggable())
                return

            // Throwable instance must be created before any methods
            getMethodNames(Throwable().stackTrace)
            Log.e(className, createLog(message))
        }


        fun i(message: String) {
            if (!isDebuggable())
                return

            getMethodNames(Throwable().stackTrace)
            Log.i(className, createLog(message))
        }

        fun d(message: String) {
            if (!isDebuggable())
                return

            getMethodNames(Throwable().stackTrace)
            Log.d(className, createLog(message))
        }

        fun v(message: String) {
            if (!isDebuggable())
                return

            getMethodNames(Throwable().stackTrace)
            Log.v(className, createLog(message))
        }

        fun w(message: String) {
            if (!isDebuggable())
                return

            getMethodNames(Throwable().stackTrace)
            Log.w(className, createLog(message))
        }

        fun wtf(message: String) {
            if (!isDebuggable())
                return
            getMethodNames(Throwable().stackTrace)
            Log.wtf(className, createLog(message))
        }

//打印长日志，超出LogCat中的Msg输出的上限
        fun longMsg(msg: String) {
            val LOG_MAXLENGTH = 2000
            if (!isDebuggable())
                return
            getMethodNames(Throwable().stackTrace)
            val strLength = msg.length
            var start = 0
            var end = LOG_MAXLENGTH
            for (i in 0..99) {
                if (strLength > end) {
                    Log.i(className, createLog(msg.substring(start, end)))
                    start = end
                    end = end + LOG_MAXLENGTH
                } else {
                    Log.i(className, createLog(msg.substring(start, strLength)))
                    break
                }
            }
        }
    }
}