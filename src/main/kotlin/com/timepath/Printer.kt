package com.timepath

public class Printer private constructor(private val indent: String) {
    private val lines: MutableList<String> = linkedListOf()
    override fun toString() = lines.asSequence().map { indent + it }.join("\n")
    fun T.plus<T>() = this@Printer.lines.addAll(this@plus.toString().split('\n')) let { Unit }
    inline fun String.invoke(body: Printer.() -> Unit) = Printer(this, body)

    fun terminate(s: String): Printer {
        lines[lines.lastIndex] += s
        return this
    }

    companion object {
        inline fun invoke(indent: String = "", configure: Printer.() -> Unit) = Printer(indent) with configure
        fun invoke(body: String = "") = Printer("") { +body }
    }
}
