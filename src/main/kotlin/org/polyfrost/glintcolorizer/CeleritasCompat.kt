package org.polyfrost.glintcolorizer

import org.apache.logging.log4j.LogManager
import org.polyfrost.oneconfig.api.config.v1.CompatSnapshots
import org.polyfrost.oneconfig.api.config.v1.Properties
import org.polyfrost.oneconfig.api.config.v1.Tree
import org.polyfrost.oneconfig.api.config.v1.Tree.tree
import org.polyfrost.oneconfig.api.config.v1.Visualizer
import org.polyfrost.oneconfig.api.config.v1.dsl.noCache
import org.polyfrost.oneconfig.api.config.v1.dsl.saveFunction
import org.polyfrost.oneconfig.api.platform.v1.ModInfo
import org.polyfrost.oneconfig.api.platform.v1.ModInfoProvider
import org.polyfrost.oneconfig.api.platform.v1.Platform
import org.taumc.celeritas.api.OptionGUIConstructionEvent
import org.taumc.celeritas.api.options.structure.Option
import org.taumc.celeritas.api.options.structure.OptionGroup
import org.taumc.celeritas.api.options.structure.OptionPage
import kotlin.collections.iterator
import kotlin.io.iterator
import kotlin.text.iterator


object CeleritasCompat {
    private val LOGGER = LogManager.getLogger("OneConfig/CeleritasCompat")
    private const val EVENT_CLASS = "org.taumc.celeritas.api.OptionGUIConstructionEvent"
    private var enabled = false
    private var trees = HashMap<String, Tree>()

    @JvmStatic
    fun enable() {
        if (enabled) return
        enabled = true
        //@TODO replace posting an event with constructing argentum's videooptionsscreen, since it's required in order for argentum to work and there's no point in it being separate
        if (loadClass(EVENT_CLASS) == null) return
        val optionPages = ArrayList<OptionPage>()
        val event = OptionGUIConstructionEvent(optionPages)
        OptionGUIConstructionEvent.BUS.post(event)
        handleOptionPages(optionPages)
    }

    fun handleOptionPages(optionPages: List<OptionPage>) {
        for (optionPage in optionPages) {
            handleOptionPage(optionPage)
        }
    }

    fun handleOptionPage(optionPage: OptionPage) {
        println("found optionPage ${optionPage.name} ${optionPage.id}")
        val id = optionPage.id.modId
        val mod = Platform.compatibility().mods.find { it.id == id }
        val tree = trees.computeIfAbsent(id) {
            val treee = Tree.tree()
            treee.id = id
            treee.title = mod?.name
            treee.noCache = true
            treee.saveFunction = Runnable {
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
                treee.addMetadata("icon_path", it)
            }
            CompatSnapshots.register(treee)
            treee
        }
        for (optionGroup in optionPage.groups) {
            handleOptionGroup(tree, optionPage, optionGroup)
        }
    }

    fun handleOptionGroup(tree: Tree, optionPage: OptionPage, optionGroup: OptionGroup) {
        println("- found optionGroup ${optionGroup.id}")
        for (option in optionGroup.options) {
            handleOption(tree, optionPage, optionGroup, option)
        }
    }
    var x = 5
    fun handleOption(tree: Tree, optionPage: OptionPage, optionGroup: OptionGroup, option: Option<*>) {
        println(" - found option ${option.id} ${option.name}")
        val property = Properties.functional({ 3 }, {}, "exampleid${x++}", "Example Name", "woah look option!")
        property.addMetadata("visualizer", Visualizer.SliderVisualizer::class.java)
        property.addMetadata("category", optionPage.name.toString())
        property.addMetadata("subcategory", optionGroup.id.toString())
        tree.put(property)
    }

    private fun loadClass(name: String): Class<*>? =
        runCatching { Class.forName(name, false, CeleritasCompat::class.java.classLoader) }.getOrNull()
}