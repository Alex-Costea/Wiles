package wiles

import org.apache.commons.cli.*
import wiles.parser.Parser
import wiles.processor.Processor
import wiles.shared.*
import wiles.shared.constants.ErrorMessages.COMPILATION_FAILED_ERROR
import wiles.shared.constants.ErrorMessages.IO_ERROR
import wiles.shared.constants.ErrorMessages.LINE_SYMBOL
import wiles.shared.constants.Utils.convertStatementToSyntaxTree
import java.io.*
import java.util.*
import java.util.stream.Collectors
import kotlin.system.exitProcess


object WilesCompiler {

    private fun getErrorsDisplay(exceptions: WilesExceptionsCollection, input: String, debug : Boolean) : String
    {
        if (exceptions.size > 0)
            return run {
                val optional =
                    exceptions.sortedWith(nullsLast(compareBy<WilesException> { it.tokenLocation.line }
                        .thenBy { it.tokenLocation.lineIndex }))
                        .map {
                            LINE_SYMBOL + "Line ${it.tokenLocation.line}: " + it.message +
                                    it.tokenLocation.displayLocation(
                                        input) + (if (debug) "\n" + it.stackTraceToString() else "")
                        }
                        .fold("") { a, b -> a + b }
                if (optional.isEmpty())
                    throw InternalErrorException()
                COMPILATION_FAILED_ERROR + optional
            }
        else return ""
    }

    private fun loadFile(filename: String): String {
        val input: String
        try {
            val resource : InputStream = File(filename).inputStream()
            resource.use { input = BufferedReader(InputStreamReader(it))
                .lines().collect(Collectors.joining("\n"))
                return input
            }
        }
        catch (ex: IOException) {
            throw InternalErrorException(IO_ERROR + ex.message)
        }
    }

    data class ProgramArgs(
        val isDebug: Boolean,
        val code: String,
        val input: String?
    )

    private fun getCommandLine(args: Array<String>): ProgramArgs {
        val help = Option.builder("help").longOpt("help")
            .desc("Print this message").build()

        val debug = Option.builder("debug").longOpt("debug")
            .desc("Run in debug mode, which displays additional logs.").build()

        val file = Option.builder("file").longOpt("file").hasArg()
            .desc("File that contains the code to be executed").type(String.Companion::class.java).build()

        val input = Option.builder("input").longOpt("input").hasArg()
            .desc("Input to be used by IO").type(String.Companion::class.java).build()

        val options = Options().addOption(debug).addOption(file).addOption(input).addOption(help)
        val parser = DefaultParser()
        val formatter = HelpFormatter()
        try{
            val line = parser.parse(options, args)
            val helpValue = line.hasOption(help)
            if(helpValue)
            {
                formatter.printHelp("java -jar Wiles.jar",options)
                exitProcess(0)
            }
            val debugValue = line.hasOption(debug)
            val fileValue = line.getParsedOptionValue<String>(file)
            val inputValue = line.getParsedOptionValue<String?>(input)
            val code = loadFile(fileValue)
            return ProgramArgs(isDebug = debugValue, code = code, input = inputValue)
        }
        catch (ex : ParseException)
        {
            println("Argument parsing error.")
            formatter.printHelp("java -jar Wiles.jar",options)
            exitProcess(0)
        }
    }

    @JvmStatic
    fun getOutput(args: Array<String>) : OutputData
    {
        //args
        val exceptions = WilesExceptionsCollection()
        val exceptionsString = StringBuilder()
        val clArgs = getCommandLine(args)

        //get input
        val scanner = if(clArgs.input == null)
            Scanner(System.`in`)
        else Scanner(ByteArrayInputStream(clArgs.input.toByteArray(Charsets.UTF_8)))

        val parser = Parser(clArgs.code, clArgs.isDebug)
        exceptions.addAll(parser.getExceptions())
        val result = parser.getResults()

        if (clArgs.isDebug) {
            println("Syntax tree: ")
            println(result)
        }

        if (exceptions.isNotEmpty()) {
            exceptionsString.append(getErrorsDisplay(exceptions, clArgs.code, clArgs.isDebug))
            return OutputData(output = "",
                exceptionsString = exceptionsString.toString(),
                exceptions = exceptions)
        }

        val processor = Processor(scanner, convertStatementToSyntaxTree(result), clArgs.isDebug)
        processor.process()
        val output = processor.getOutput()
        exceptions.addAll(processor.getExceptions())
        if (exceptions.isNotEmpty()) {
            exceptionsString.append(getErrorsDisplay(exceptions, clArgs.code, clArgs.isDebug))
        }
        return OutputData(
            output = output,
            exceptionsString = exceptionsString.toString(),
            exceptions = exceptions)
    }

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val result = getOutput(args)
        System.err.print(result.exceptionsString)
        print(result.output)
    }
}