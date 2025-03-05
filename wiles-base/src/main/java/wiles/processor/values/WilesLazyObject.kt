package wiles.processor.values

import wiles.processor.processors.ProcessorExpression

class WilesLazyObject(private val expression: ProcessorExpression) {
    private var obj : Any? = null
    fun getObject(): Any? {
        if(obj != null)
            return obj
        try{
            expression.process()
            obj = expression.value.getObj()
            return obj
        }
        catch (ex : StackOverflowError)
        {
            TODO("Stack overflow while reading lazy object!")
        }
    }

    fun hasBeenComputed() : Boolean
    {
        return obj != null
    }

    override fun toString(): String {
        return "Lazy(${obj})"
    }
}