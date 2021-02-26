package me.minhael.design.state

class Redux {

    private val observers = hashMapOf<String, MutableList<(Any) -> Unit>>()

    private val state = HashMap<String, Any>()

    fun setState(reducer: (Map<String, Any>) -> Map<String, Any>): Map<String, Any> {
        return reducer(HashMap(state))
            .also { state.putAll(it) }
            .onEach { (key, value) -> observers[key]?.forEach { it.invoke(value) } }
    }

    fun dispatch(reducer: (Map<String, Any>) -> Map<String, Any>): Map<String, Any> {
        return reducer(HashMap(state))
            .filter { (key, value) -> !state.containsKey(key) || value != state[key] }
            .also { state.putAll(it) }
            .onEach { (key, value) -> observers[key]?.forEach { it.invoke(value) } }
    }

    fun clear() {
        state.clear()
    }

    fun observe(state: String, effector: (Any) -> Unit) {
        observers.getOrPut(state) { ArrayList() }.add(effector)
    }

    fun remove(state: String, effector: (Any) -> Unit): Boolean {
        return observers.getOrPut(state) { ArrayList() }.remove(effector)
    }

    fun <T> get(name: String, klass: Class<T>): T? {
        return state[name]?.let { klass.cast(it) }
    }

    inline fun <reified T> observeWith(state: String, crossinline effector: (T) -> Unit): (Any) -> Unit {
        return { it: Any -> effector(T::class.java.cast(it)) }.also { observe(state, it) }
    }

    inline fun <reified T> getWith(name: String): T? = get(name, T::class.java)
}