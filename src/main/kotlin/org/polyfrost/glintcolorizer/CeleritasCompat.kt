package org.polyfrost.glintcolorizer

import org.apache.logging.log4j.LogManager
import org.polyfrost.oneconfig.api.config.v1.CompatSnapshots
import org.polyfrost.oneconfig.api.config.v1.Tree
import org.polyfrost.oneconfig.api.config.v1.dsl.noCache
import org.polyfrost.oneconfig.api.config.v1.dsl.saveFunction
import org.polyfrost.oneconfig.api.platform.v1.ModInfo
import org.polyfrost.oneconfig.api.platform.v1.ModInfoProvider
import org.polyfrost.oneconfig.api.platform.v1.Platform
import org.taumc.celeritas.api.OptionGUIConstructionEvent
import org.taumc.celeritas.api.options.structure.OptionPage


object CeleritasCompat {
    private val LOGGER = LogManager.getLogger("OneConfig/CeleritasCompat")
    private const val EVENT_CLASS = "org.taumc.celeritas.api.OptionGUIConstructionEvent"
    private var enabled = false

    @JvmStatic
    fun enable() {
        if (enabled) return
        enabled = true
        if (loadClass(EVENT_CLASS) == null) return
        val optionPages = ArrayList<OptionPage>()
        val event = OptionGUIConstructionEvent(optionPages)
        OptionGUIConstructionEvent.BUS.post(event)
        for (optionPage in optionPages) {
            handleOptionPage(optionPage)
        }

    }
    fun handleOptionPage(optionPage: OptionPage) {
        println("found optionPage ${optionPage.name} ${optionPage.id}")
        val id = optionPage.id.modId
        val mod = Platform.compatibility().mods.find { it.id == id }
        val tree = Tree.tree()
        tree.id = id
        tree.title = mod?.name
        tree.noCache = true
        tree.saveFunction = Runnable {
            runCatching {
                for (option in optionPage.options) {
                    option.storage.save()
                }
            }.onFailure {
                LOGGER.warn("Failed to save celeritas config", it)
            }
        }
        //@TODO add original screen opener which opens argentum VideoOptionsScreen
        mod?.extractIconFile()?.let {
            tree.addMetadata("icon_path", it)
        }
        for (optionGroup in optionPage.groups) {
            println("- found optionGroup ${optionGroup.id}")
            for (option in optionGroup.options) {
                println(" - found option ${option.id} ${option.name}")
            }
        }
        CompatSnapshots.register(tree)
    }


    private fun loadClass(name: String): Class<*>? =
        runCatching { Class.forName(name, false, CeleritasCompat::class.java.classLoader) }.getOrNull()
}