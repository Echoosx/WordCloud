package org.echoosx.mirai.plugin

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info
import org.echoosx.mirai.plugin.command.SomedayWordCloud
import org.echoosx.mirai.plugin.command.TodayWordCloud


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
    }
}
