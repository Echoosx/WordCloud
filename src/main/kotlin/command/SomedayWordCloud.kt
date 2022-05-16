package org.echoosx.mirai.plugin.command

import com.huaban.analysis.jieba.JiebaSegmenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.echoosx.mirai.plugin.WordCloud
import org.echoosx.mirai.plugin.WordCloud.dataFolder
import org.echoosx.mirai.plugin.WordCloudConfig
import org.echoosx.mirai.plugin.util.Cloud
import xyz.cssxsh.mirai.plugin.MiraiHibernateRecorder
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object SomedayWordCloud:SimpleCommand(
    WordCloud,
    "词云", description = "生成某日词云"
) {
    private val logger get() = WordCloud.logger
    @Suppress("unused")
    @Handler
    suspend fun CommandSenderOnMessage<GroupMessageEvent>.handle(dateStr: String){
        try {
            val rec = MiraiHibernateRecorder[this.fromEvent.group]
            val (start, end) = date2timestamp(dateStr)
            val segment = JiebaSegmenter()

            val countList = mutableListOf<String>()
            rec.filter{ it.time in start until end }.filter { it.code.contains(""""type":"PlainText"""") }.forEach {
                var sentence = it.toMessageSource().originalMessage.toString().replace(Regex("\\[.+?]")," ")
                WordCloudConfig.removeRegex.forEach{ regex->
                    sentence = sentence.replace(Regex(regex)," ")
                }
                if(sentence.isNotBlank()){
                    val segmentList = segment.process(sentence, JiebaSegmenter.SegMode.INDEX)
                    segmentList.forEach{ result->
                        if(!result.word.matches(Regex("(\\d+|\\s+|\\p{P}+|\\p{S}+|\\p{Zs}+)"))){
                            countList.add(result.word)
                        }
                    }
                }
            }
            if(countList.isNotEmpty()){
                Cloud.generate(countList)
                val resource = File("${dataFolder.absolutePath}/cloud.png").toExternalResource()
                subject?.sendImage(resource)
                withContext(Dispatchers.IO) {
                    resource.close()
                }
            }else{
                sendMessage("没有这天的词汇记录呢QAQ")
            }
        }catch (e: ParseException){
            sendMessage("日期格式有误，请按照\"yyyy-MM-dd\"格式输入日期\n示例：/词云 1970-01-01")
        } catch (e:Throwable){
            sendMessage("词云生成失败")
        }
    }

    /*
    * 获取某日的起始与终止时间戳
    * */
    private fun date2timestamp(dateStr:String): Pair<Int,Int>{
        val sdf = SimpleDateFormat( "yyyy-MM-dd" )
        val date = sdf.parse(dateStr)

        val calendar = Calendar.getInstance()
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MILLISECOND] = 0
        calendar[Calendar.DATE] = date.date
        val todayStart = (calendar.timeInMillis / 1000).toInt()
        calendar[Calendar.DATE] += 1
        val todayEnd = (calendar.timeInMillis / 1000).toInt()
        return Pair(todayStart,todayEnd)
    }

}