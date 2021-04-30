package com.ainzzorl.algorithms.images

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.pow
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
        val storeArtifacts = cmd.hasOption("artifacts")
        val artifactsPath = cmd.getOptionValue("artifacts")

        val original: BufferedImage = ImageIO.read(File(inputFilePath))
        val grey = EnergyMap.toGrey(original)
        val energyMap = EnergyMap.toEnergyMap(grey)

        val seam = getLowEnergyVerticalSeam(energyMap)
        if (storeArtifacts) {
            ImageIO.write(paintVerticalSeam(original, seam), "jpg", File("$artifactsPath/seam.jpg"))
        }
        val result = removeSeam(original, seam)

        // TODO: use provided width and height
        // TODO: support horizontal seams

        if (storeArtifacts) {
            ImageIO.write(grey, "jpg", File("$artifactsPath/grey.jpg"))
            ImageIO.write(energyMap, "jpg", File("$artifactsPath/energy.jpg"))
        }

        ImageIO.write(result, "jpg", File(outputFilePath))
    }

    private fun getLowEnergyVerticalSeam(energyMap: BufferedImage) : IntArray {
        // TODO: implement for real
        return (0 until energyMap.height).toList().toIntArray()
    }

    private fun paintVerticalSeam(image: BufferedImage, seam: IntArray) : BufferedImage {
        val result = ImageUtils.deepCopy(image)
        for (y in 0 until image.height) {
            result.setRGB(seam[y], y, 0xFF0000) // Red
        }
        return result
    }

    private fun removeSeam(image: BufferedImage, seam: IntArray) : BufferedImage {
        val result = BufferedImage(image.width - 1, image.height, image.type)
        for (y in 0 until image.height) {
            for (x in 0 until seam[y]) {
                result.setRGB(x, y, image.getRGB(x, y))
            }
            for (x in seam[y] until image.width - 1) {
                result.setRGB(x, y, image.getRGB(x + 1, y))
            }
        }
        return result
    }
}