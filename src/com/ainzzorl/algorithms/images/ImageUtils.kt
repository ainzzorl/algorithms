package com.ainzzorl.algorithms.images

import java.awt.image.BufferedImage

object ImageUtils {
    fun deepCopy(bi: BufferedImage): BufferedImage {
        val cm = bi.colorModel
        val isAlphaPremultiplied = cm.isAlphaPremultiplied
        val raster = bi.copyData(null)
        return BufferedImage(cm, raster, isAlphaPremultiplied, null)
    }
}