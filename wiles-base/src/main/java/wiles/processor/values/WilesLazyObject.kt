package wiles.processor.values

import wiles.processor.processors.ProcessorExpression

class WilesLazyObject(private val expression: ProcessorExpression) {
    private var obj : Any? = null
    fun getObject(): Any? {
        if(obj != null)
            return obj
        expression.process()
        obj = expression.value.getObj()
        return obj
    }

    fun hasBeenComputed() : Boolean
    {
        return obj != null
    }
}