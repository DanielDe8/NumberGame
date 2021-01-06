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
import kotlinx.coroutines.*

val hra = Hra()

fun Stage.vytvorNepratele(hra: Hra): NepratelskeCislo {
    val nepratel2 =  NepratelskeCislo(
            Random.nextInt(1, 10),
            Random.nextDouble(0.9, hra.widthNum.toDouble()),
            0.0
    )
    return nepratel2
}

class Hra() : Container() {
    val widthNum  = 550
    val heightNum = 550

    var tvojeCislo = 1
    val tvojeCisloText = text("$tvojeCislo", 100.0)

    var casovacNepratele = 0
    val nepratele = arrayListOf<NepratelskeCislo>()

    init {
        fixedSizeContainer(widthNum.toDouble(), heightNum.toDouble())
    }
}

class NepratelskeCislo(val cislo: Int, posX: Double, posY: Double) : Container() {
    var cislo2 = cislo
    val rect = roundRect(50, 50, 10)
    val text = text("$cislo2" ,50.0, Colors.BLACK)

    val posX2 = posX
    val posY2 = posY

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

suspend fun main() = Korge(
        width = hra.widthNum,
        height = hra.heightNum,
        title = "NumberGame",
        bgcolor = Colors["#2b2b2b"]
    ) {
    addChild(hra)

    hra.tvojeCisloText.x = width / 2 - (hra.tvojeCisloText.textSize / 2)
    hra.tvojeCisloText.y = width - hra.tvojeCisloText.textSize
    addFixedUpdater(60.timesPerSecond) {
        hra.tvojeCisloText.text = ("${hra.tvojeCislo}")
        hra.casovacNepratele++

        if(hra.tvojeCislo < 1) {
            //gameWindow.close()
            text("Game Over!").apply {
                fontSize = 80.0
            }
        }

        for (n in hra.nepratele) {
            n.x += (hra.tvojeCisloText.x - n.x) * 0.01
            n.y += 2
            if (n.y >= hra.tvojeCisloText.y) {
                hra.tvojeCislo -= n.cislo2
                n.removeFromParent()
            }
        }
        hra.nepratele.removeAll { n -> n.y >= hra.tvojeCisloText.y }

        if (hra.casovacNepratele == 60 * 3) {
            val nepratel = vytvorNepratele(hra)
            hra.nepratele.add(nepratel)

            addChild(nepratel)

            hra.casovacNepratele = 0
        }
    }
}