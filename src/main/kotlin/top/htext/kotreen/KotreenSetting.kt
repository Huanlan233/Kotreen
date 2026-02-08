package top.htext.kotreen

import carpet.settings.Rule

@Suppress("removal")
object KotreenSetting {
    @Rule(options = ["0", "1", "2", "3", "4"], category = ["creative"], desc = " ")
    @JvmField
    var seriesPermission: Int = 0
    @Rule(options = ["0", "1", "2", "3", "4"], category = ["creative"], desc = " ")
    @JvmField
    var seriesCreatePermission: Int = 4
    @Rule(options = ["0", "1", "2", "3", "4"], category = ["creative"], desc = " ")
    @JvmField
    var seriesRemovePermission: Int = 4
    @Rule(options = ["0", "1", "2", "3", "4"], category = ["creative"], desc = " ")
    @JvmField
    var seriesModifyPermission: Int = 4
    @Rule(options = ["0", "1", "2", "3", "4"], category = ["creative"], desc = " ")
    @JvmField
    var seriesSpawnPermission: Int = 0
    @Rule(options = ["0", "1", "2", "3", "4"], category = ["creative"], desc = " ")
    @JvmField
    var seriesKillPermission: Int = 0
    @Rule(options = ["0", "1", "2", "3", "4"], category = ["creative"], desc = " ")
    @JvmField
    var seriesActionPermission: Int = 0
    @Rule(options = ["0", "1", "2", "3", "4"], category = ["creative"], desc = " ")
    @JvmField
    var seriesStopPermission: Int = 0

    @Rule(options = ["0", "1", "2", "3", "4"], category = ["creative"], desc = " ")
    @JvmField
    var arrangementPermission: Int = 0
    @Rule(options = ["0", "1", "2", "3", "4"], category = ["creative"], desc = " ")
    @JvmField
    var arrangementCreatePermission: Int = 4
    @Rule(options = ["0", "1", "2", "3", "4"], category = ["creative"], desc = " ")
    @JvmField
    var arrangementRemovePermission: Int = 4
    @Rule(options = ["0", "1", "2", "3", "4"], category = ["creative"], desc = " ")
    @JvmField
    var arrangementModifyPermission: Int = 4
    @Rule(options = ["0", "1", "2", "3", "4"], category = ["creative"], desc = " ")
    @JvmField
    var arrangementSpawnPermission: Int = 0
    @Rule(options = ["0", "1", "2", "3", "4"], category = ["creative"], desc = " ")
    @JvmField
    var arrangementKillPermission: Int = 0
    @Rule(options = ["0", "1", "2", "3", "4"], category = ["creative"], desc = " ")
    @JvmField
    var arrangementActionPermission: Int = 0
    @Rule(options = ["0", "1", "2", "3", "4"], category = ["creative"], desc = " ")
    @JvmField
    var arrangementStopPermission: Int = 0
}