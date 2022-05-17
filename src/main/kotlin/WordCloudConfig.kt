package org.echoosx.mirai.plugin

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value


object WordCloudConfig:AutoSavePluginConfig("Setting") {
    @ValueDescription("设置背景宽度，背景模式为IMAGE时无效")
    val width:Int by value(1000)

    @ValueDescription("设置背景高度，背景模式为IMAGE时无效")
    val height:Int by value(1000)

    @ValueDescription("设置字体大小下限")
    val minFontSize:Int by value(30)

    @ValueDescription("设置字体大小上限")
    val maxFontSize:Int by value(80)

    @ValueDescription("设置词的数量")
    val wordsReturn:Int by value(300)

    @ValueDescription("设置词的最短长度")
    val minWordLength:Int by value(2)

    @ValueDescription("自动过滤匹配的内容，支持正则表达式")
    val removeRegex:List<String> by value()

    @ValueDescription("设置词云的背景模式,可选 CIRCLE,RECTANGLE,IMAGE 三种模式")
    val backgroundMode:String by value("CIRCLE")

    @ValueDescription("""
       若背景模式为IMAGE,需要此项来指定背景图片,必须是PNG格式，透明部分不填充文字
       请填写data/FrameImage目录下放置的图片名称，如:"default.png"（可以填多个，生成时则随机选用）
       生成云图的大小与指定图片相同
    """)
    val imageName:List<String> by value()

    @ValueDescription("""
       设置词云选用指定的字体，请填写data/Font/目录下放置的字体文件名，如:"思源黑体.ttf"
    """)
    val fontPath: String by value()

    @ValueDescription("设置词云文字可选的颜色,用16进制表示，越靠前的颜色对应的频率越高")
    val colorList: MutableList<String> by value(
        mutableListOf(
            "253DF3",
            "4055F1",
            "408DF1",
            "40AAF1",
            "40C5F1"
        )
    )

    @ValueDescription("文字角度，默认横向与竖向夹杂,值为 HORIZON 只有横向，值为 VERTICAL 只有竖向，值为 RANDOM 角度随机")
    val textAngle:String by value()
}