package me.minhael.design.data

interface Policy {
    fun <T : Any> transfer(key: Data.Element<T>, from: Data, to: MutableData)

    class Fill : Policy {
        override fun <T : Any> transfer(key: Data.Element<T>, from: Data, to: MutableData) {
            if (!to.has(key))
                to.transfer(key, from)
        }
    }

    class Fetch : Policy {
        override fun <T : Any> transfer(key: Data.Element<T>, from: Data, to: MutableData) {
            to.transfer(key, from)
        }
    }

    companion object {

        @JvmField val FILL = Fill()
        @JvmField val FETCH = Fetch()

        private fun <T : Any> MutableData.transfer(key: Data.Element<T>, from: Data) {
            //  TODO    Deep clone map and list
            from[key]?.also { set(key, it) }
        }
    }
}
