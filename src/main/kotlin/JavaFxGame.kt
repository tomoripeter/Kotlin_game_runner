import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.stage.Stage
import kotlin.math.abs
import kotlin.random.Random


class JavaFxGame : Application() {
    private val keyboardInputs = ArrayList<String>()
    private var runnerPosX = 400.0
    private var runnerPosY = 350.0
    private var runnerLoopX = 0
    private var runnerLoopY = 20
    private var gameOver = true

    override fun start(theStage: Stage) {
        val root = Group()
        val scene = Scene(root)
        val canvas = Canvas(1000.0, 680.0)
        val gc = canvas.graphicsContext2D
        val background = Image("background.jpg")
        val banana = Image("peeledbanana.jpg")
        var bananaPosX = 930.0
        val bananaPosY = 470.0
        var bananaSpeed = 0.0
        val runningMan = AnimatedImage(0.07)
        val startNanoTime = System.nanoTime()
        var highscore = 0.0
        val highscoreLabel = Label("Highscore: ${highscore.toInt()} m")
        val startgameLabel = Label("Press SPACE BAR to start game")

        // add images to animation
        runningMan.images.add(Image("runningman1.jpg"))
        runningMan.images.add(Image("runningman2.jpg"))
        runningMan.images.add(Image("runningman3.jpg"))
        runningMan.images.add(Image("runningman4.jpg"))

        theStage.scene = scene
        theStage.title = "Kotlin game"
        theStage.isResizable = false
        root.children.add(canvas)
        root.children.add(highscoreLabel)
        root.children.add(startgameLabel)

        // set highscorelabel's properties
        highscoreLabel.isVisible = true
        highscoreLabel.relocate(420.0, 0.0)
        highscoreLabel.font = Font("Arial", 20.0)

        // set startgamelabel's properties
        startgameLabel.isVisible = true
        startgameLabel.relocate(270.0, 250.0)
        startgameLabel.font = Font("Arial", 30.0)
        startgameLabel.textFill = Paint.valueOf("#FF0000")

        // add keyboard events
        scene.setOnKeyReleased { event ->
            if (!keyboardInputs.contains(event.code.toString()))
                keyboardInputs.add(event.code.toString())
        }

        object : AnimationTimer() {
            override fun handle(currentNanoTime: Long) {
                val t = (currentNanoTime - startNanoTime) / 1000000000.0

                // start/restart the game with spacebar
                if (keyboardInputs.contains(KeyCode.SPACE.toString()) && gameOver) {
                    gameOver = false
                    highscore = 0.0
                    startgameLabel.isVisible = false
                    keyboardInputs.remove(KeyCode.SPACE.toString())
                    bananaSpeed = 10.0
                }

                // calculate the objects' position
                calculateRunnerPosX()
                calculateRunnerPosY()
                bananaPosX = if (bananaPosX - bananaSpeed < 0) 1000.0 else bananaPosX - bananaSpeed
                // calculate different speed for each banana
                if (bananaPosX == 1000.0 - bananaSpeed && !gameOver)
                    bananaSpeed = 5 + Random.nextDouble(0.0, 10.0)

                // render the background, runner and banana
                gc.drawImage(background, 0.0, 0.0)
                gc.drawImage(runningMan.getAnimatedImage(t), runnerPosX, runnerPosY)
                gc.drawImage(banana, bananaPosX, bananaPosY)

                // game over detection
                if (detectCollision(runnerPosX, runnerPosY, bananaPosX, bananaPosY)) {
                    gameOver = true
                    bananaPosX = 930.0
                    bananaSpeed = 0.0
                    startgameLabel.isVisible = true
                }
                // game is not over yet, increment highscore
                else if (!gameOver) {
                    highscore += 0.3
                    highscoreLabel.text = "Highscore: ${highscore.toInt()} m"
                }
            }
        }.start()
        theStage.show()
    }

    // function that detects if a collision occurred
    fun detectCollision(charPosX: Double, charPosY: Double, objPosX: Double, objPosY: Double): Boolean {
        var collision = false
        if (abs(objPosX - charPosX) < 30 && abs(objPosY - charPosY) < 130)
            collision = true
        return collision
    }

    // function that calculates runner's Y position
    fun calculateRunnerPosY() {
        if (keyboardInputs.contains(KeyCode.UP.toString())) {
            runnerLoopY--
            runnerPosY = 350.0 - runnerLoopY * 4

            if (runnerLoopY == 0) {
                runnerLoopY = 20
                keyboardInputs.remove(KeyCode.UP.toString())
            }
        } else
            runnerPosY = 350.0
    }

    // function that calculates the runner's X position
    fun calculateRunnerPosX() {
        if (keyboardInputs.contains(KeyCode.LEFT.toString())) {
            runnerLoopX++
            runnerPosX = if (runnerPosX < 0.0) runnerPosX else runnerPosX - runnerLoopX
            if (runnerLoopX == 10) {
                keyboardInputs.remove(KeyCode.LEFT.toString())
                runnerLoopX = 0
            }
        }

        if (keyboardInputs.contains(KeyCode.RIGHT.toString())) {
            runnerLoopX++
            runnerPosX = if (runnerPosX > 1000.0) runnerPosX else runnerPosX + runnerLoopX
            if (runnerLoopX == 10) {
                keyboardInputs.remove(KeyCode.RIGHT.toString())
                runnerLoopX = 0
            }
        }
    }
}