package com.timepath

public class Printer(private val indent: String, @Suppress("UNUSED_PARAMETER") dummy: Unit) {
    private val lines: MutableList<String> = linkedListOf()
    override fun toString() = lines.asSequence().map { indent + it }.joinToString("\n")
    operator fun <T> T.unaryPlus() = this@Printer.lines.addAll(this@unaryPlus.toString().split('\n')).let { Unit }
    inline operator fun String.invoke(body: Printer.() -> Unit) = Printer(this, body)

    fun terminate(s: String): Printer {
        lines[lines.lastIndex] += s
        return this
    }

    companion object {
        inline operator fun invoke(indent: String = "", configure: Printer.() -> Unit) = Printer(indent, Unit).apply(configure)
        operator fun invoke(body: String = "") = Printer("") { +body }
    }
}
