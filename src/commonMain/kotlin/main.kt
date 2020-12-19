import com.soywiz.klock.*
import com.soywiz.korev.*
import com.soywiz.korge.*
import com.soywiz.korge.animate.*
import com.soywiz.korge.html.*
import com.soywiz.korge.input.*
import com.soywiz.korge.service.storage.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tween.moveTo
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import com.soywiz.korio.lang.Thread_sleep
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.*
import com.soywiz.korma.interpolation.*
import kotlin.collections.set
import kotlin.properties.Delegates
import kotlin.random.*
import kotlin.*
import kotlinx.*

val hra = Hra()

fun vytvorNepratele(hra: Hra): NepratelskeCislo {
    return NepratelskeCislo(
            Random.nextInt(1, 10),
            Random.nextDouble(0.9, hra.widthNum.toDouble()),
            0.0
    )
}

class Hra() : Container() {
    val widthNum  = 550
    val heightNum = 550

    var tvojeCislo = 10
    val tvojeCisloText = text("$tvojeCislo", 100.0)


    init {
        fixedSizeContainer(widthNum.toDouble(), heightNum.toDouble())
    }
}

class NepratelskeCislo(val cislo: Int, posX: Double, posY: Double) : Container() {
    var cislo2 = cislo
    val rect = roundRect(50, 50, 10)
    val text = text("$cislo2" ,50.0, Colors.BLACK)

    init {
        x = posX
        y = posY

        addFixedUpdater(60.timesPerSecond) {
            text.centerOn(rect)
            text.text = ("$cislo2")
//            println(cislo)
        }

        onClick {
            cislo2 -= 1
            if (cislo2 < 1) {
                hra.tvojeCislo += cislo
                removeFromParent()
            }
        }
    }
}

suspend fun main() = Korge(width = hra.widthNum, height = hra.heightNum, title = "NumberGame", bgcolor = Colors["#2b2b2b"]) {
    val nepratel = vytvorNepratele(hra)
    addChild(hra)
    addChild(nepratel)


    hra.apply {
        tvojeCisloText.x = width / 2 - (tvojeCisloText.textSize / 2)
        tvojeCisloText.y = width - tvojeCisloText.textSize
        addFixedUpdater(60.timesPerSecond) {
            tvojeCisloText.text = ("$tvojeCislo")
        }
    }

    nepratel.moveTo(hra.tvojeCisloText.x - 50, hra.tvojeCisloText.y - 50, 5.seconds, Easing.LINEAR)

    for (i in 1..nepratel.cislo2) {
        delay(0.5.seconds)
        hra.tvojeCislo--
        nepratel.cislo2--
    }
    nepratel.removeFromParent()
}