package `in`.costea.wiles.services

import `in`.costea.wiles.commands.AbstractCommand

class OrderOfOperationsProcessor(private val components: List<AbstractCommand>) {
    fun process(): List<AbstractCommand> {
        //TODO: implement
        return components.toMutableList()
    }
}