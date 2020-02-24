import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.text.Font
import javafx.stage.Stage
import kotlin.math.abs


class JavaFxGame : Application()
{
    override fun start(theStage: Stage) {
        val root = Group()
        val scene = Scene(root)
        val canvas = Canvas(1000.0, 680.0)
        val gc = canvas.graphicsContext2D
        val background = Image("background.jpg")
        val banana = Image("peeledbanana.jpg")
        var runnerPosX = 400.0
        var runnerPosY = 350.0
        var runnerLoopY = 0.0
        var bananaPosX = 1000.0
        val bananaPosY = 470.0
        val bananaSpeed = 10.0
        val runningMan = AnimatedImage(0.07)
        val startNanoTime = System.nanoTime()
        val keyboardInputs = ArrayList<String>()
        var highscore = 0.0
        val highscoreLabel = Label("Highscore: ${highscore.toInt()} m")

        runningMan.images.add(Image("runningman1.jpg"))
        runningMan.images.add(Image("runningman2.jpg"))
        runningMan.images.add(Image("runningman3.jpg"))
        runningMan.images.add(Image("runningman4.jpg"))

        theStage.scene = scene
        theStage.title = "Kotlin game"
        theStage.isResizable = false
        root.children.add(canvas)
        root.children.add(highscoreLabel)

        highscoreLabel.isVisible = true
        highscoreLabel.relocate(430.0, 0.0)
        highscoreLabel.font = Font("Arial", 20.0)

        scene.setOnKeyReleased { event ->
            if (!keyboardInputs.contains(event.code.toString()))
                keyboardInputs.add(event.code.toString())
        }

        object : AnimationTimer() {
            override fun handle(currentNanoTime: Long) {
                val t = (currentNanoTime - startNanoTime) / 1000000000.0

                if (keyboardInputs.contains("UP")) {
                    runnerPosY = 250.0 + runnerLoopY;
                    runnerLoopY += 10
                    if (runnerLoopY == 100.0) {
                        runnerLoopY = 0.0;
                        keyboardInputs.remove("UP")
                    }
                }
                else
                    runnerPosY = 350.0

                if (keyboardInputs.contains("LEFT"))
                {
                    runnerPosX = if (runnerPosX < 0.0) runnerPosX else runnerPosX - 40.0
                    keyboardInputs.remove("LEFT")
                }

                if (keyboardInputs.contains("RIGHT"))
                {
                    runnerPosX = if (runnerPosX > 1000.0) runnerPosX else runnerPosX + 40.0
                    keyboardInputs.remove("RIGHT")
                }

                bananaPosX = if( bananaPosX - bananaSpeed < 0) 1000.0 else bananaPosX - bananaSpeed

                gc.drawImage(background, 0.0, 0.0)
                gc.drawImage(runningMan.getAnimatedImage(t), runnerPosX, runnerPosY)
                gc.drawImage(banana, bananaPosX, bananaPosY)

                if( detectCollision(runnerPosX, runnerPosY, bananaPosX, bananaPosY))
                {
                    highscore = 0.0
                    println("Game over")
                }
                else
                {
                    highscore += 0.3
                    highscoreLabel.text = "Highscore: ${highscore.toInt()} m"
                }
            }
        }.start()
        theStage.show()
    }

    fun detectCollision(charPosX: Double, charPosY: Double, objPosX: Double, objPosY: Double ) : Boolean {
        var collision = false
        if (abs(objPosX - charPosX) < 30 && abs(objPosY - charPosY) < 130)
            collision = true
        return collision
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(JavaFxGame::class.java)
        }
    }
}