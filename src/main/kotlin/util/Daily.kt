package org.echoosx.mirai.plugin.util

import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import org.echoosx.mirai.plugin.DailyWorldCloudConfig.dailyWordCloudGroup
import org.echoosx.mirai.plugin.WordCloud
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import java.text.SimpleDateFormat
import java.util.*

internal class Daily : Job {
    private val logger get() = WordCloud.logger

    @Throws(JobExecutionException::class)
    override fun execute(jobExecutionContext: JobExecutionContext?) {
        Bot.instances.filter { it.isOnline }.forEach{ bot->
            bot.dailyWordCloud()
        }
    }
}

internal fun Bot.dailyWordCloud() = WordCloud.launch {
    dailyWordCloudGroup.forEach{ id->
        val group = bot.getGroupOrFail(id)
        val resource = generateCloudImage(group, SimpleDateFormat("yyyy-MM-dd").format(Date()))

        if(resource != null){
            group.sendMessage("今日词云:")
            group.sendImage(resource)
            resource.close()
        }
    }
}