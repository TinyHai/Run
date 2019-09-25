package com.mdzz.run

import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Test {

    @Test
    fun test() {
        println(10 * (2 shr 20))
    }
}