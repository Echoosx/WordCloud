package org.echoosx.mirai.plugin.util

import com.kennycason.kumo.CollisionMode
import com.kennycason.kumo.WordFrequency
import com.kennycason.kumo.bg.CircleBackground
import com.kennycason.kumo.bg.PixelBoundaryBackground
import com.kennycason.kumo.bg.RectangleBackground
import com.kennycason.kumo.font.KumoFont
import com.kennycason.kumo.font.scale.LinearFontScalar
import com.kennycason.kumo.image.AngleGenerator
import com.kennycason.kumo.nlp.FrequencyAnalyzer
import com.kennycason.kumo.palette.ColorPalette
import org.echoosx.mirai.plugin.WordCloud
import org.echoosx.mirai.plugin.WordCloud.dataFolder
import org.echoosx.mirai.plugin.WordCloudConfig.backgroundMode
import org.echoosx.mirai.plugin.WordCloudConfig.colorList
import org.echoosx.mirai.plugin.WordCloudConfig.fontPath
import org.echoosx.mirai.plugin.WordCloudConfig.height
import org.echoosx.mirai.plugin.WordCloudConfig.imageName
import org.echoosx.mirai.plugin.WordCloudConfig.maxFontSize
import org.echoosx.mirai.plugin.WordCloudConfig.minFontSize
import org.echoosx.mirai.plugin.WordCloudConfig.minWordLength
import org.echoosx.mirai.plugin.WordCloudConfig.textAngle
import org.echoosx.mirai.plugin.WordCloudConfig.width
import org.echoosx.mirai.plugin.WordCloudConfig.wordsReturn
import java.awt.Color
import java.awt.Dimension
import java.io.File
import javax.imageio.ImageIO


object Cloud {
    fun generate(result:MutableList<String>){
        val frequencyAnalyzer = FrequencyAnalyzer()
        // 引入中文解析器
        // frequencyAnalyzer.setWordTokenizer(ChineseWordTokenizer())
        // 设置分词返回数量
        frequencyAnalyzer.setWordFrequenciesToReturn(wordsReturn)
        // 生成图片大小
        val dimension = Dimension(width, height)
        var wordCloud = com.kennycason.kumo.WordCloud(dimension, CollisionMode.PIXEL_PERFECT)

        when(backgroundMode){
            "CIRCLE" -> wordCloud.setBackground(CircleBackground(width.coerceAtMost(height) / 2))
            "RECTANGLE" -> {
                wordCloud = com.kennycason.kumo.WordCloud(dimension, CollisionMode.RECTANGLE)
                wordCloud.setBackground(RectangleBackground(dimension))
            }
            "IMAGE" -> {
                val imageFile = File("${dataFolder.absolutePath}/FrameImage/${imageName.random()}")
                val image = ImageIO.read(imageFile)
                val imageDimension = Dimension(image.width,image.height)
                wordCloud = com.kennycason.kumo.WordCloud(imageDimension, CollisionMode.PIXEL_PERFECT)
                wordCloud.setBackground(PixelBoundaryBackground(imageFile))
            }
        }
        // 最小分词长度
        frequencyAnalyzer.setMinWordLength(minWordLength)

        val wordFrequencies: List<WordFrequency> = frequencyAnalyzer.load(result)
        // 稀疏程度
        wordCloud.setPadding(2)
        // 颜色模板
        wordCloud.setColorPalette(
            ColorPalette(colorList.map { it.toIntOrNull(16)?.let { color -> Color(color) } })
        )
        // 字体
        if(fontPath.isNotBlank())
            wordCloud.setKumoFont(KumoFont(File("${dataFolder.absolutePath}/Font/${fontPath}")))
        else
            wordCloud.setKumoFont(KumoFont(WordCloud.getResourceAsStream("思源黑体CN-Heavy.otf")))
        // 字体大小范围
        wordCloud.setFontScalar(LinearFontScalar(minFontSize, maxFontSize))
        // 设置背景色
        wordCloud.setBackgroundColor(Color(255,255,255))
        // 设置偏转角度
        when(textAngle){
            "HORIZON" -> wordCloud.setAngleGenerator(AngleGenerator(0))
            "VERTICAL" -> wordCloud.setAngleGenerator(AngleGenerator(90))
            "RANDOM" -> wordCloud.setAngleGenerator(AngleGenerator(-90.0,90.0,18))
        }

        wordCloud.build(wordFrequencies)
        wordCloud.writeToFile("${dataFolder.absolutePath}/cloud.png")
    }
}