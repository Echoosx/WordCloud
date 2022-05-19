package org.echoosx.mirai.plugin.util

import com.huaban.analysis.jieba.JiebaSegmenter
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.ExternalResource
import org.echoosx.mirai.plugin.WordCloudConfig.removeRegex
import xyz.cssxsh.mirai.plugin.MiraiHibernateRecorder
import java.text.SimpleDateFormat
import java.util.*


internal fun generateCloudImage(group: Group,dateStr: String): ExternalResource? {
    val rec = MiraiHibernateRecorder[group]
    val (start, end) = date2timestamp(dateStr)
    val segment = JiebaSegmenter()
    val countList = mutableListOf<String>()

    rec.filter { it.time in start until end }.filter { it.code.contains("PlainText") }.forEach {
        println("test")
        var sentence = it.toMessageChain().content.replace(Regex("\\[.+?]"), " ")
        removeRegex.forEach { regex ->
            sentence = sentence.replace(Regex(regex), " ")
        }
        if (sentence.isNotBlank()) {
            val segmentList = segment.process(sentence, JiebaSegmenter.SegMode.INDEX)
            segmentList.forEach { result ->
                if (!result.word.matches(Regex("(\\d+|\\s+|\\p{P}+|\\p{S}+|\\p{Zs}+)"))) {
                    countList.add(result.word)
                }
            }
        }
    }
    return if (countList.isNotEmpty()) {
        Cloud.generate(countList)
    } else {
        null
    }
}

/*
* 获取某日的起始与终止时间戳
* */
internal fun date2timestamp(dateStr:String): Pair<Int,Int>{
    val sdf = SimpleDateFormat( "yyyy-MM-dd" )
    val date = sdf.parse(dateStr)

    val calendar = Calendar.getInstance()
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MILLISECOND] = 0
    calendar[Calendar.DATE] = date.date
    val start = (calendar.timeInMillis / 1000).toInt()
    calendar[Calendar.DATE] += 1
    val end = (calendar.timeInMillis / 1000).toInt()
    return Pair(start,end)
}