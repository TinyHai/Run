package com.mdzz.run

import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class Test {

    @Test
    fun test() {
        val clazz = this::class.java
        val declaredMethods = clazz.declaredMethods
        declaredMethods.forEach {
            println(it.name)
        }
    }

    fun haha(a: String, b: BigDecimal): Boolean {
        return true
    }
}