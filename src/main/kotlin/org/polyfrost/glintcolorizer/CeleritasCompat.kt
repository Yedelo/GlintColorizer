package org.polyfrost.glintcolorizer

import org.polyfrost.oneconfig.api.platform.v1.ModInfoProvider
import org.taumc.celeritas.api.OptionGUIConstructionEvent
import org.taumc.celeritas.api.options.structure.OptionPage


object CeleritasCompat {
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
            println("found optionPage ${optionPage.name} ${optionPage.id}")
            for (optionGroup in optionPage.groups) {
                println("- found optionGroup ${optionGroup.id}")
                for (option in optionGroup.options) {
                    println(" - found option ${option.id} ${option.name}")
                }
            }
        }
    }


    private fun loadClass(name: String): Class<*>? =
        runCatching { Class.forName(name, false, CeleritasCompat::class.java.classLoader) }.getOrNull()
}