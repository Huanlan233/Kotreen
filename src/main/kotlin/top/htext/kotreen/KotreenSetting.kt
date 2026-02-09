package top.htext.kotreen

import carpet.api.settings.Rule
import carpet.api.settings.Validators

object KotreenSetting {
    private const val CREATIVE = "creative"

    @Rule(
        options = ["true", "false", "ops", "0", "1", "2", "3", "4"], 
        categories = [CREATIVE], 
        validators = [Validators.CommandLevel::class]
    )
    @JvmField
    var seriesPermission = "true"
    @Rule(
        options = ["true", "false", "ops", "0", "1", "2", "3", "4"], 
        categories = [CREATIVE], 
        validators = [Validators.CommandLevel::class]
    )
    @JvmField
    var seriesCreatePermission = "ops"
    @Rule(
        options = ["true", "false", "ops", "0", "1", "2", "3", "4"], 
        categories = [CREATIVE], 
        validators = [Validators.CommandLevel::class]
    )
    @JvmField
    var seriesRemovePermission = "ops"
    @Rule(
        options = ["true", "false", "ops", "0", "1", "2", "3", "4"], 
        categories = [CREATIVE], 
        validators = [Validators.CommandLevel::class]
    )
    @JvmField
    var seriesModifyPermission = "ops"
    @Rule(
        options = ["true", "false", "ops", "0", "1", "2", "3", "4"], 
        categories = [CREATIVE], 
        validators = [Validators.CommandLevel::class]
    )
    @JvmField
    var seriesSpawnPermission = "true"
    @Rule(
        options = ["true", "false", "ops", "0", "1", "2", "3", "4"], 
        categories = [CREATIVE], 
        validators = [Validators.CommandLevel::class]
    )
    @JvmField
    var seriesKillPermission = "true"
    @Rule(
        options = ["true", "false", "ops", "0", "1", "2", "3", "4"], 
        categories = [CREATIVE], 
        validators = [Validators.CommandLevel::class]
    )
    @JvmField
    var seriesActionPermission = "true"
    @Rule(
        options = ["true", "false", "ops", "0", "1", "2", "3", "4"], 
        categories = [CREATIVE], 
        validators = [Validators.CommandLevel::class]
    )
    @JvmField
    var seriesStopPermission = "true"

    @Rule(
        options = ["true", "false", "ops", "0", "1", "2", "3", "4"], 
        categories = [CREATIVE], 
        validators = [Validators.CommandLevel::class]
    )
    @JvmField
    var arrangementPermission = "true"
    @Rule(
        options = ["true", "false", "ops", "0", "1", "2", "3", "4"], 
        categories = [CREATIVE], 
        validators = [Validators.CommandLevel::class]
    )
    @JvmField
    var arrangementCreatePermission = "ops"
    @Rule(
        options = ["true", "false", "ops", "0", "1", "2", "3", "4"], 
        categories = [CREATIVE], 
        validators = [Validators.CommandLevel::class]
    )
    @JvmField
    var arrangementRemovePermission = "ops"
    @Rule(
        options = ["true", "false", "ops", "0", "1", "2", "3", "4"], 
        categories = [CREATIVE], 
        validators = [Validators.CommandLevel::class]
    )
    @JvmField
    var arrangementModifyPermission = "ops"
    @Rule(
        options = ["true", "false", "ops", "0", "1", "2", "3", "4"], 
        categories = [CREATIVE], 
        validators = [Validators.CommandLevel::class]
    )
    @JvmField
    var arrangementSpawnPermission = "true"
    @Rule(
        options = ["true", "false", "ops", "0", "1", "2", "3", "4"], 
        categories = [CREATIVE], 
        validators = [Validators.CommandLevel::class]
    )
    @JvmField
    var arrangementKillPermission = "true"
    @Rule(
        options = ["true", "false", "ops", "0", "1", "2", "3", "4"], 
        categories = [CREATIVE], 
        validators = [Validators.CommandLevel::class]
    )
    @JvmField
    var arrangementActionPermission = "true"
    @Rule(
        options = ["true", "false", "ops", "0", "1", "2", "3", "4"], 
        categories = [CREATIVE], 
        validators = [Validators.CommandLevel::class]
    )
    @JvmField
    var arrangementStopPermission = "true"
}