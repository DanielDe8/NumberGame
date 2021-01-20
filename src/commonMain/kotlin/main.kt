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

fun Stage.zacniHru() {
    hra.zkoncilaHra = false
}

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

    var maxTvojeCislo = "0"
    var maxTvojeCisloVeHre = tvojeCislo

    var zkoncilaHra = false

    var casovacNepratele = 0
    val nepratele = arrayListOf<NepratelskeCislo>()
    var odstranitNepratele = false

    val sedyCtverec = solidRect(widthNum, heightNum, Colors["#000000BF"])

    init {
        fixedSizeContainer(widthNum.toDouble(), heightNum.toDouble())
        sedyCtverec.removeFromParent()
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

            if (hra.odstranitNepratele) {
                removeFromParent()
                hra.odstranitNepratele = false
            }
        }

        onClick {
            cislo2 -= 1
            if (cislo2 < 1) {
                hra.tvojeCislo += cislo
                hra.maxTvojeCisloVeHre += cislo
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

    val storage = NativeStorage(views)

    hra.tvojeCisloText.x = width / 2 - (hra.tvojeCisloText.textSize / 2)
    hra.tvojeCisloText.y = width - hra.tvojeCisloText.textSize
    addFixedUpdater(60.timesPerSecond) {
        if (hra.zkoncilaHra == false) {
            hra.tvojeCisloText.text = ("${hra.tvojeCislo}")
            hra.casovacNepratele++

            if (hra.tvojeCislo < 1) {
                //gameWindow.close()
                hra.odstranitNepratele = true

//                hra.tvojeCisloText.removeFromParent()
                hra.nepratele.clear()

                 container {
                    val pozadi = solidRect(hra.widthNum, hra.heightNum, Colors["#2b2b2b"])
                    val gameOverText = text("Game Over!").apply {
                        fontSize = 80.0
                        y = 100.0
                        centerXOn(pozadi)
                    }

                    container {
                        val rect = roundRect(300, 60, 20)
                        val text = text("Play Again").apply {
                            color = Colors["#2b2b2b"]
                            fontSize = 55.0
                        }.centerOn(rect).apply { y += 4 }

                    }.apply {
                        centerOn(gameOverText)
                        y += 150
                    }.onClick {
                        this.removeFromParent()
                        hra.tvojeCislo = 10
                        hra.tvojeCisloText.text = "${hra.tvojeCislo}"
//                        hra.tvojeCisloText.addTo(this@Korge)
                        hra.zkoncilaHra = false
                    }

                    container {
                        val rect = roundRect(200, 60, 20)
                        val text = text("Quit").apply {
                            color = Colors["#2b2b2b"]
                            fontSize = 55.0
                        }.centerOn(rect)

                    }.apply {
                        centerOn(gameOverText)
                        y += 270
                    }.onClick {
                        gameWindow.close()
                    }
                }.centerOn(hra)

                hra.zkoncilaHra = true
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
}