package org.echoosx.mirai.plugin.command

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.GroupMessageEvent
import org.echoosx.mirai.plugin.WordCloud
import org.echoosx.mirai.plugin.util.generateCloudImage
import java.text.SimpleDateFormat
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
            val resource = generateCloudImage(subject as Group, SimpleDateFormat("yyyy-MM-dd").format(Date()))
            if(resource != null){
                subject?.sendImage(resource)
                withContext(Dispatchers.IO) { resource.close() }
            }else{
                sendMessage("没有词汇记录呢QAQ")
            }
        }catch (e:Throwable){
            sendMessage("词云生成失败")
            logger.error(e)
        }
    }
}