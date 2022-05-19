package org.echoosx.mirai.plugin

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object DailyWorldCloudConfig:AutoSavePluginConfig("Daily") {
    @ValueDescription("订阅每日词云的群号")
    val dailyWordCloudGroup:List<Long> by value()

    @ValueDescription("每日词云的发送时间(h)")
    val dailyWordCloudHour:Int by value(23)

    @ValueDescription("每日词云的发送时间(h)")
    val dailyWordCloudMin:Int by value(59)
}