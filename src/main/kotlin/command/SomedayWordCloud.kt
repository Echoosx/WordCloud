package org.echoosx.mirai.plugin.command

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.GroupMessageEvent
import org.echoosx.mirai.plugin.WordCloud
import org.echoosx.mirai.plugin.util.generateCloudImage
import java.text.ParseException

object SomedayWordCloud:SimpleCommand(
    WordCloud,
    "词云", description = "生成某日词云"
) {
    private val logger get() = WordCloud.logger
    @Suppress("unused")
    @Handler
    suspend fun CommandSenderOnMessage<GroupMessageEvent>.handle(dateStr: String, group: Group = subject as Group){
        try {
            val resource = generateCloudImage(group, dateStr)
            if(resource != null){
                subject?.sendImage(resource)
                withContext(Dispatchers.IO) { resource.close() }
            }else{
                sendMessage("没有词汇记录呢QAQ")
            }
        }catch (e: ParseException){
            sendMessage("日期格式有误，请按照\"yyyy-MM-dd\"格式输入日期\n示例：/词云 1970-01-01")
        } catch (e:Throwable){
            sendMessage("词云生成失败")
            logger.error(e)
        }
    }
}