package org.echoosx.mirai.plugin.command

import com.huaban.analysis.jieba.JiebaSegmenter
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.echoosx.mirai.plugin.WordCloud
import org.echoosx.mirai.plugin.WordCloud.dataFolder
import org.echoosx.mirai.plugin.WordCloudConfig.removeRegex
import org.echoosx.mirai.plugin.util.Cloud
import xyz.cssxsh.mirai.plugin.MiraiHibernateRecorder
import java.io.File
import java.util.*


object TodayWordCloud:SimpleCommand(
    WordCloud,
    "今日词云","本日词云", description = "生成本日词云"
) {
    @OptIn(ConsoleExperimentalApi::class, ExperimentalCommandDescriptors::class)
    override val prefixOptional: Boolean = true

    private val logger get() = WordCloud.logger
    @Suppress("unused")
    @Handler
    suspend fun CommandSenderOnMessage<GroupMessageEvent>.handle(){
        try {
            val rec = MiraiHibernateRecorder[this.fromEvent.group]
            val (start,_) = today2timestamp()
            val segment = JiebaSegmenter()

            val countList = mutableListOf<String>()
            rec.filter{ it.time >= start }.filter { it.code.contains("PlainText") }.forEach {
                var sentence = it.toMessageChain().content.replace(Regex("\\[.+?]")," ")
                removeRegex.forEach{ regex->
                    sentence = sentence.replace(Regex(regex)," ")
                }
                if(sentence.isNotBlank()){
                    val segmentList = segment.process(sentence, SegMode.INDEX)
                    segmentList.forEach{ result->
                        if(!result.word.matches(Regex("(\\d+|\\s+|\\p{P}+|\\p{S}+|\\p{Zs}+)"))){
                            countList.add(result.word)
                        }
                    }
                }
            }
            Cloud.generate(countList)
            val resource = File("${dataFolder.absolutePath}/cloud.png").toExternalResource()
            subject?.sendImage(resource)
            withContext(Dispatchers.IO) {
                resource.close()
            }
        }catch (e:Throwable){
            sendMessage("词云生成失败")
        }
    }

    /*
    * 获取本日的起始与终止时间戳
    * */
    private fun today2timestamp(): Pair<Int,Int>{
        val calendar = Calendar.getInstance()
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MILLISECOND] = 0
        val todayStart = (calendar.timeInMillis / 1000).toInt()
        calendar[Calendar.DATE] += 1
        val todayEnd = (calendar.timeInMillis / 1000).toInt()
        return Pair(todayStart,todayEnd)
    }
}