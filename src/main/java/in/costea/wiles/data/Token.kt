package `in`.costea.wiles.data

data class Token(val content: String, val location: TokenLocation?) {

     override fun toString(): String {
         location?:return ""
         return content+"at line: "+location.line+" index: "+location.lineIndex
     }

     override fun equals(other: Any?): Boolean {
         if (this === other) return true
         if(other !is Token)
             return false
         other.location?:return true
         if (content != other.content) return false
         if (location != other.location) return false
         return true
     }

     //TODO: Not ideal
     override fun hashCode(): Int {
         var result = content.hashCode()
         result = 31 * result + if(location==null) 1 else 0
         return result
     }

 }
