package me.minhael.design.job

import java.io.Serializable

interface Job : Serializable {

    fun build(): Task

    fun interface Task {
        /**
         * true: Success
         * false: Failure
         * null: Retry
         */
        fun execute(): Boolean?
    }
}