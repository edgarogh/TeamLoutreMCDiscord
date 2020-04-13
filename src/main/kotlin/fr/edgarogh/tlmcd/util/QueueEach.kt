package fr.edgarogh.tlmcd.util

import java.util.*

fun <T> Queue<T>.each(action: (T) -> Unit) {
    var el: T?
    while (poll().also { el = it } != null) {
        action(el!!)
    }
}