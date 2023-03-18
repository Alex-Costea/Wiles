package wiles.parser

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.google.common.base.CharMatcher
import wiles.Main.DEBUG
import wiles.Main.filename
import wiles.parser.converters.TokensToSyntaxTreeConverter
import wiles.parser.statements.CodeBlockStatement
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.InternalErrorException
import wiles.shared.Token
import wiles.shared.TokenLocation
import wiles.shared.constants.ErrorMessages.IO_ERROR
import wiles.shared.constants.Settings.SYNTAX_TREE_FILE
import java.io.*
import java.util.*
import java.util.stream.Collectors

class Parser(content : String?) {
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    private var results : CodeBlockStatement
    val input = content?:loadFile(filename)
    lateinit var json : String
    var resourceLineLength = 0

    init{
        val tokens = sourceToTokens(input)
        if(DEBUG) {
            print("Tokens: ")
            println(tokens.stream().map(Token::content).toList())
        }

        val ast = tokensToAST(tokens, lastLocation(input))
        val mapper =
            JsonMapper.builder().disable(MapperFeature.AUTO_DETECT_CREATORS).disable(MapperFeature.AUTO_DETECT_FIELDS)
                .disable(MapperFeature.AUTO_DETECT_GETTERS).disable(MapperFeature.AUTO_DETECT_IS_GETTERS).build()
        results = ast

        val writer = mapper.writer(DefaultPrettyPrinter())
        if(DEBUG)
            writer.writeValue(File(SYNTAX_TREE_FILE), ast)
        else json = writer.writeValueAsString(ast)
    }

    fun getExceptions() : CompilationExceptionsCollection
    {
        return exceptions
    }

    fun getResults() : CodeBlockStatement
    {
        return results
    }

    private fun lastLocation(input : String) : TokenLocation
    {
        val textSplit = CharMatcher.whitespace().trimTrailingFrom(input).split("\n")
        val lastIndex = textSplit.lastIndex
        val lastLineLocation = textSplit[lastIndex].length
        return TokenLocation(lastIndex+1,lastLineLocation+2)
    }

    private fun loadFile(filename: String): String {
        val input: String
        try {
            val resource : InputStream = File(filename).inputStream()
            resource.use { input = BufferedReader(InputStreamReader(it))
                    .lines().collect(Collectors.joining("\n"))
                return readStandardLibrary() + input
            }
        }
        catch (ex: IOException) {
            throw InternalErrorException(IO_ERROR)
        }
    }

    private fun readStandardLibrary(): String {
        val classloader = Thread.currentThread().contextClassLoader
        val input: String
        try {
            classloader.getResourceAsStream("StandardLibrary.wiles").use { inputStream ->
                Objects.requireNonNull(inputStream)
                input = BufferedReader(InputStreamReader(inputStream!!))
                    .lines().collect(Collectors.joining("\n"))
                resourceLineLength=input.count { it == '\n' }
                return input
            }
        } catch (ex: NullPointerException) {
            throw InternalErrorException(IO_ERROR)
        } catch (ex: IOException) {
            throw InternalErrorException(IO_ERROR)
        }
    }

    private fun sourceToTokens(input: String): List<Token> {
        val converter = wiles.parser.converters.InputToTokensConverter(input,resourceLineLength)
        val tokens = converter.convert()
        exceptions.addAll(converter.exceptions)
        return tokens
    }

    private fun tokensToAST(tokens: List<Token>, lastLocation : TokenLocation): CodeBlockStatement {
        val converter = TokensToSyntaxTreeConverter(tokens,lastLocation)
        val programStatement = converter.convert()
        exceptions.addAll(converter.exceptions)
        return programStatement
    }
}