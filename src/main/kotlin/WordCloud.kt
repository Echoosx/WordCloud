package org.echoosx.mirai.plugin

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info
import org.echoosx.mirai.plugin.command.SomedayWordCloud
import org.echoosx.mirai.plugin.command.TodayWordCloud
import xyz.cssxsh.mirai.plugin.MiraiHibernateConfiguration
import java.io.File


object WordCloud : KotlinPlugin(
    JvmPluginDescription(
        id = "org.echoosx.mirai.plugin.WordCloud",
        name = "WordCloud",
        version = "1.0.0"
    ) {
        author("Echoosx")
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-hibernate-plugin", false)
    }
) {
    override fun onEnable() {
        logger.info { "WordCloud loaded" }

        WordCloudConfig.reload()
        TodayWordCloud.register()
        SomedayWordCloud.register()

        val factory = MiraiHibernateConfiguration(plugin = this).buildSessionFactory()
        factory.openSession().use { session ->
            session.doReturningWork { connection -> connection.metaData }
        }

        touchDir("${dataFolderPath}/FrameImage")
        touchDir("${dataFolderPath}/Font")
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
        //创建目录
        return if (dir.mkdirs()) {
            true
        } else {
            logger.error("创建目录" + destDirName + "失败！")
            false
        }
    }
}
