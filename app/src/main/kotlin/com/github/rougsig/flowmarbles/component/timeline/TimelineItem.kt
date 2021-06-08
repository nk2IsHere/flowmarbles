package com.github.rougsig.flowmarbles.component.timeline

import com.github.rougsig.flowmarbles.component.timeline.Timeline.Companion.MAX_TIME
import com.github.rougsig.flowmarbles.core.Component
import org.w3c.dom.DOMRect
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document

class TimelineItem<T : Any>(model: Marble.Model<T>, posY: Long = 1) : Component {
  var dragListener: ((Long) -> Unit)? = null
    set(value) {
      if (value != null) rootNode.setAttribute("style", "cursor: ew-resize;")
      else rootNode.setAttribute("style", "cursor: default;")
      field = value
    }
  private val marble = Marble(model, posY)
  override val rootNode = marble.rootNode

  init {

    rootNode.addEventListener("mousedown", { down ->
      val rect = down.asDynamic().currentTarget.parentElement.getBoundingClientRect() as DOMRect
      val width = rect.width
      val left = rect.left
      val ratio = MAX_TIME / width
      val percent = { x: Double ->
        val pos = ((x - left) * ratio).toLong()
        when {
          pos > MAX_TIME -> MAX_TIME
          pos < 0 -> 0L
          else -> pos
        }
      }

      val moveListener = { move: Event ->
        val mouseMoveEvent = move as MouseEvent
        val posX = percent(mouseMoveEvent.pageX)
        dragListener?.invoke(posX)
        Unit
      }
      document.addEventListener("mousemove", moveListener)

      document.addEventListener("mouseup", {
        document.removeEventListener("mousemove", moveListener)
      })
    })
  }
}
