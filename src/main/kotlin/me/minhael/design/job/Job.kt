package me.minhael.design.job

import java.io.Serializable

fun interface Job : Serializable {

    fun build(): Task

    interface Task {
        /**
         * true: Success
         * false: Failure
         * null: Retry
         */
        fun execute(): Boolean?
    }
}