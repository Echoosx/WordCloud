package org.echoosx.mirai.plugin

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info
import org.echoosx.mirai.plugin.DailyWorldCloudConfig.dailyWordCloudHour
import org.echoosx.mirai.plugin.DailyWorldCloudConfig.dailyWordCloudMin
import org.echoosx.mirai.plugin.command.SomedayWordCloud
import org.echoosx.mirai.plugin.command.TodayWordCloud
import org.echoosx.mirai.plugin.util.Daily
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory
import java.io.File


object WordCloud : KotlinPlugin(
    JvmPluginDescription(
        id = "org.echoosx.mirai.plugin.WordCloud",
        name = "WordCloud",
        version = "1.1.2"
    ) {
        author("Echoosx")
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-hibernate-plugin", false)
    }
) {
    override fun onEnable() {
        logger.info { "WordCloud loaded" }

        WordCloudConfig.reload()
        DailyWorldCloudConfig.reload()
        TodayWordCloud.register()
        SomedayWordCloud.register()

        touchDir("${dataFolderPath}/FrameImage")
        touchDir("${dataFolderPath}/Font")
        touchDir("${dataFolderPath}/Cloud")

        val cronSchedule = "0 $dailyWordCloudMin $dailyWordCloudHour * * ?"
        val scheduler = StdSchedulerFactory.getDefaultScheduler()

        val jobDetail = JobBuilder.newJob(Daily::class.java)
            .build()

        val trigger = TriggerBuilder.newTrigger()
            .withSchedule(
                CronScheduleBuilder.cronSchedule(cronSchedule)
            )
            .startNow()
            .build()

        scheduler.scheduleJob(jobDetail, trigger)
        scheduler.start()
    }

    private fun touchDir(dirPath: String): Boolean {
        var destDirName = dirPath
        val dir = File(destDirName)
        if (dir.exists()) {
            return false
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName += File.separator
        }
        return if (dir.mkdirs()) {
            true
        } else {
            logger.error("创建目录" + destDirName + "失败！")
            false
        }
    }
}
