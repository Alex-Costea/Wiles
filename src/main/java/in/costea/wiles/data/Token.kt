package `in`.costea.wiles.data

data class Token(val content: String, val location: TokenLocation?) {

     override fun toString(): String {
         location?:return ""
         return content+"at line: "+location.line+" index: "+location.lineIndex
     }

     override fun equals(other: Any?): Boolean {
         if(other !is Token) return false
         if (content != other.content) return false
         location?: return true
         other.location?: return true
         return location == other.location
     }

    override fun hashCode(): Int {
        return content.hashCode()
    }

 }
