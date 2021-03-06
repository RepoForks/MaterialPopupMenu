package com.github.zawadz88.materialpopupmenu

import android.view.Gravity

/**
 * Builder for creating a [MaterialPopupMenu].
 *
 * The [MaterialPopupMenu] must have at least one section.
 * All sections must also have at least one item and each item must have a non-null label set.
 *
 * @author Piotr Zawadzki
 */
@PopupMenuMarker
class MaterialPopupMenuBuilder {

    /**
     * Style of the popup menu. Default is [R.style.Widget_MPM_Menu].
     *
     * For dark themes you should use [R.style.Widget_MPM_Menu_Dark].
     *
     * You can also provide your own style, however make sure that all of the attributes
     * that are declared in [R.style.Widget_MPM_Menu] are also declared in your style.
     */
    var style: Int = R.style.Widget_MPM_Menu

    /**
     * Gravity of the dropdown list. This is commonly used to
     * set gravity to START or END for alignment with the anchor.
     */
    var dropdownGravity: Int = Gravity.NO_GRAVITY

    internal val sectionHolderList = arrayListOf<SectionHolder>()

    /**
     * Adds a new section to the popup menu.
     *
     * Sections are separated with a divider from each other and must contain at least one item.
     * Section titles are optional.
     * @param init block containing section definition
     */
    fun section(init: SectionHolder.() -> Unit) {
        val section = SectionHolder()
        section.init()
        sectionHolderList.add(section)
    }

    /**
     * Creates a [MaterialPopupMenu] with the already configured params.
     *
     * This might throw [IllegalStateException] if it wasn't configured properly
     * - see class description for validation details.
     */
    fun build(): MaterialPopupMenu {
        check(sectionHolderList.isNotEmpty(), { "Popup menu sections cannot be empty!" })

        val sections = sectionHolderList.map { it.convertToPopupMenuSection() }

        return MaterialPopupMenu(style, dropdownGravity, sections)
    }

    /**
     * Holds section info for the builder. This gets converted to [MaterialPopupMenu.PopupMenuSection].
     */
    @PopupMenuMarker
    class SectionHolder {

        /**
         * Optional section holder. *null* by default.
         * If the title is not *null* it will be displayed in the menu.
         */
        var title: String? = null

        internal val itemsHolderList = arrayListOf<ItemHolder>()

        /**
         * Adds an item to the section.
         * @param init block containing item definition
         */
        fun item(init: ItemHolder.() -> Unit) {
            val item = ItemHolder()
            item.init()
            itemsHolderList.add(item)
        }

        override fun toString(): String {
            return "SectionHolder(title=$title, itemsHolderList=$itemsHolderList)"
        }

        internal fun convertToPopupMenuSection(): MaterialPopupMenu.PopupMenuSection {
            check(itemsHolderList.isNotEmpty(), { "Section '$this' has no items!" })
            return MaterialPopupMenu.PopupMenuSection(title, itemsHolderList.map { it.convertToPopupMenuItem() })
        }

    }

    /**
     * Holds section item info for the builder. This gets converted to [MaterialPopupMenu.PopupMenuItem].
     */
    @PopupMenuMarker
    class ItemHolder {

        /**
         * Item label. This is a required field and must not be *null*.
         */
        var label: String? = null

        /**
         * Optional icon to be displayed together with the label.
         *
         * This must be a valid drawable resource ID if set.
         * *0* Means that no icon should be displayed.
         */
        var icon: Int = 0

        /**
         * Callback to be invoked once an item gets selected.
         */
        var callback: () -> Unit = {}

        override fun toString(): String {
            return "ItemHolder(label=$label, icon=$icon, callback=$callback)"
        }

        internal fun convertToPopupMenuItem(): MaterialPopupMenu.PopupMenuItem {
            return MaterialPopupMenu.PopupMenuItem(
                    checkNotNull(label, { "Item '$this' does not have a label" }),
                    icon,
                    callback)
        }

    }

}

/**
 * Function to create a [MaterialPopupMenuBuilder].
 * @param init block containing popup menu definition
 */
fun popupMenuBuilder(init: MaterialPopupMenuBuilder.() -> Unit): MaterialPopupMenuBuilder {
    val popupMenu = MaterialPopupMenuBuilder()
    popupMenu.init()
    return popupMenu
}

/**
 * Function to create a [MaterialPopupMenu].
 * @param init block containing popup menu definition
 */
fun popupMenu(init: MaterialPopupMenuBuilder.() -> Unit): MaterialPopupMenu {
    return popupMenuBuilder(init).build()
}

@DslMarker
internal annotation class PopupMenuMarker
