package com.ainzzorl.algorithms.images

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import kotlin.system.exitProcess

object SeamCarving {
    @JvmStatic
    fun main(args: Array<String>) {
        val options = Options()
        val input = Option("i", "input", true, "input file path")
        input.isRequired = true
        options.addOption(input)

        val output = Option("o", "output", true, "output file")
        output.isRequired = true
        options.addOption(output)

        val outputHeight = Option("h", "output-height", true, "output height")
        outputHeight.isRequired = false
        options.addOption(outputHeight)

        val outputWidth = Option("w", "output-width", true, "output width")
        outputWidth.isRequired = false
        options.addOption(outputWidth)

        val artifacts = Option("a", "artifacts", true, "directory to store artifacts")
        artifacts.isRequired = false
        options.addOption(artifacts)

        val parser = DefaultParser()
        val formatter = HelpFormatter()
        val cmd: CommandLine

        try {
            cmd = parser.parse(options, args);
        } catch (e: ParseException) {
            println(e.message);
            formatter.printHelp("seam-carving", options);
            exitProcess(1);
        }

        val inputFilePath = cmd.getOptionValue("input")
        val outputFilePath = cmd.getOptionValue("output")

        println(inputFilePath)
        println(outputFilePath)
    }
}