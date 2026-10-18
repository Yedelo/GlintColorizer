package org.polyfrost.glintcolorizer

import dev.rdh.argentum.impl.gui.VideoOptionsScreen
import net.minecraft.client.resources.I18n
import org.apache.logging.log4j.LogManager
import org.embeddedt.embeddium.impl.gui.framework.TextComponent
import org.embeddedt.embeddium.impl.gui.framework.TextComponent.Styled
import org.polyfrost.glintcolorizer.utils.OptionProperties
import org.polyfrost.oneconfig.api.config.v1.CompatSnapshots
import org.polyfrost.oneconfig.api.config.v1.Properties
import org.polyfrost.oneconfig.api.config.v1.Tree
import org.polyfrost.oneconfig.api.config.v1.Visualizer
import org.polyfrost.oneconfig.api.config.v1.dsl.noCache
import org.polyfrost.oneconfig.api.config.v1.dsl.saveFunction
import org.polyfrost.oneconfig.api.platform.v1.Platform
import org.taumc.celeritas.api.OptionGUIConstructionEvent
import org.taumc.celeritas.api.options.control.Control
import org.taumc.celeritas.api.options.control.CyclingControl
import org.taumc.celeritas.api.options.control.SliderControl
import org.taumc.celeritas.api.options.control.TickBoxControl
import org.taumc.celeritas.api.options.structure.Option
import org.taumc.celeritas.api.options.structure.OptionGroup
import org.taumc.celeritas.api.options.structure.OptionPage
import sun.security.krb5.EncryptionKey.findKey
import java.util.HashMap
import java.util.function.Consumer
import java.util.function.Supplier


object CeleritasCompat {
    private val LOGGER = LogManager.getLogger("OneConfig/CeleritasCompat")
    private const val ARGENTUM_SCREEN_CLASS = "dev.rdh.argentum.impl.gui.VideoOptionsScreen"
    private var enabled = false
    private var trees = HashMap<String, Tree>()

    @JvmStatic
    fun enable() {
        if (enabled) return
        enabled = true
        // i cannot decide how to initialize things because this is all a mess with compiling
        // i would initialize a new VideoOptionsScreen (this is why i added argentum dependency) but it has been a nightmare
        val list = ArrayList<OptionPage>()
        val event = OptionGUIConstructionEvent(list)
        OptionGUIConstructionEvent.BUS.post(event)
        handleOptionPages(list)
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
        val id = option.id?.path
        val name = option.name.getAsString()
        val description = option.tooltip.getAsString()
        val property = OptionProperties.getProperty(option, id, name, description)
        val visualizer = when (option.control) {
            is SliderControl -> Visualizer.SliderVisualizer::class.java;
            is TickBoxControl -> Visualizer.CheckboxVisualizer::class.java;
            is CyclingControl -> Visualizer.RadioVisualizer::class.java;
            else -> throw IllegalStateException("something else (yt)")
        }
        property.addMetadata("visualizer", visualizer)
        property.addMetadata("category", optionPage.name.toString())
        property.addMetadata("subcategory", optionGroup.id.toString())
        tree.put(property)
    }

    fun TextComponent.getAsString(): String {
        if (this is TextComponent.Literal) {
            return this.text()
        }
        else if (this is TextComponent.Translatable) {
            //@TODO translatable text
            return this.toString()
        }
        else if (this is Styled) {
            return this.inner.getAsString()
        }
        else {
            throw IllegalArgumentException("Unexpected component class: " + javaClass.name)
        }
    }

    private fun loadClass(name: String): Class<*>? =
        runCatching { Class.forName(name, false, CeleritasCompat::class.java.classLoader) }.getOrNull()
}