package com.ainzzorl.algorithms.images

import java.awt.image.BufferedImage
import kotlin.math.abs

object ImageUtils {
    fun deepCopy(bi: BufferedImage): BufferedImage {
        val cm = bi.colorModel
        val isAlphaPremultiplied = cm.isAlphaPremultiplied
        val raster = bi.copyData(null)
        return BufferedImage(cm, raster, isAlphaPremultiplied, null)
    }

    fun transpose(image: BufferedImage): BufferedImage {
        val result = BufferedImage(image.height, image.width, image.type)
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                result.setRGB(y, x, image.getRGB(x, y))
            }
        }
        return result
    }
}