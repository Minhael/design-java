package me.minhael.design

import me.minhael.design.test.CharsetsTest

internal class ICUMuxTest: CharsetsTest() {
    override val charsets = ICUMux.INSTANCE
}