package `in`.costea.wiles

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import `in`.costea.wiles.commands.ProgramCommand
import `in`.costea.wiles.converters.InputToTokensConverter
import `in`.costea.wiles.converters.TokensToSyntaxTreeConverter
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.data.Token
import `in`.costea.wiles.exceptions.CompilationFailed
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.stream.Collectors

object Main {
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val input = loadFile()
        val tokens = sourceToTokens(input)
        print("Tokens: ")
        println(tokens.stream().map(Token::content).toList())
        val ast: ProgramCommand = tokensToAST(tokens)
        val mapper = JsonMapper.builder().disable(MapperFeature.AUTO_DETECT_CREATORS).disable(MapperFeature.AUTO_DETECT_FIELDS)
                .disable(MapperFeature.AUTO_DETECT_GETTERS).disable(MapperFeature.AUTO_DETECT_IS_GETTERS).build()
        print("Syntax tree: ")
        println(ast)

        //Print exceptions
        val writer = mapper.writer(DefaultPrettyPrinter())
        writer.writeValue(File("syntaxtree.json"), ast)
        if (exceptions.size > 0) throw CompilationFailed(exceptions)
    }

    private fun loadFile(): String {
        val classloader = Thread.currentThread().contextClassLoader
        val input: String
        try {
            classloader.getResourceAsStream("input.wiles").use { inputStream ->
                Objects.requireNonNull(inputStream)
                input = BufferedReader(InputStreamReader(inputStream!!))
                    .lines().collect(Collectors.joining("\n"))
                return input
            }
        } catch (ex: NullPointerException) {
            throw Error("Error loading input file!")
        } catch (ex: IOException) {
            throw Error("Error loading input file!")
        }
    }

    private fun sourceToTokens(input: String): List<Token> {
        val converter = InputToTokensConverter(input)
        val tokens = converter.convert()
        exceptions.addAll(converter.exceptions)
        return tokens
    }

    private fun tokensToAST(tokens: List<Token>): ProgramCommand {
        val converter = TokensToSyntaxTreeConverter(tokens)
        val programCommand = converter.convert()
        exceptions.addAll(converter.exceptions)
        programCommand.setCompiledSuccessfully(exceptions.size == 0)
        return programCommand
    }
}